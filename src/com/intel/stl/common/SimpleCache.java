/**
 * Copyright (c) 2015, Intel Corporation
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 *     * Redistributions of source code must retain the above copyright notice,
 *       this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of Intel Corporation nor the names of its contributors
 *       may be used to endorse or promote products derived from this software
 *       without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.intel.stl.common;

import java.lang.ref.Reference;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A simple LFU (Least-Frequently Used) based cache with life time control
 */
public class SimpleCache<K, V> {
    private static final String NAME = "cache_cleanup_thread-";

    private static int THREAD_COUNT = 0;

    private static final int DEFAULT_CAPACITY = 1000;

    private static final long DEFAULT_LEFTTME = 5000; // 5 sec

    /**
     * Max number of items to cache
     */
    private int capacity;

    /**
     * cached item's life time in MS
     */
    private final long lifeTimeMs;

    /**
     * core storage on items
     */
    private final Map<K, Reference<ValueItem>> cache =
            new HashMap<K, Reference<ValueItem>>();

    private final LinkedList<KeyItem> keyItems = new LinkedList<KeyItem>();

    private long lastCheckTime;

    private long checkInterval;

    private CleanupTask cleanupTask;

    public SimpleCache() {
        this(DEFAULT_CAPACITY, DEFAULT_LEFTTME);
    }

    public SimpleCache(int capacity) {
        this(capacity, DEFAULT_LEFTTME);
    }

    public SimpleCache(int capacity, long lifeTimeMs) {
        super();
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        this.lifeTimeMs = lifeTimeMs;
        checkInterval = lifeTimeMs / 4 + 100;
        cleanupTask = new CleanupTask();
    }

    public synchronized void setCapacity(int capacity) {
        if (capacity < 0) {
            throw new IllegalArgumentException("Capacity must be positive");
        }

        this.capacity = capacity;
        trim();
    }

    public synchronized void push(K key, V value) {
        Reference<ValueItem> ref = cache.get(key);
        ValueItem vi = ref == null ? null : ref.get();
        if (vi == null) {
            KeyItem ki = new KeyItem(key);
            vi = new ValueItem(ki, value);
            ref = createReference(vi);
            cache.put(key, ref);
            // replace if already exist
            keyItems.remove(ki);
            keyItems.add(ki);
        } else {
            vi.setValue(value);
            vi.getKeyItem().accessed();
        }

        trim();
        scheduleCleanup();
    }

    /**
     * 
     * <i>Description:</i> can override to create strong, soft or weak reference
     * 
     * @param vi
     * @return
     */
    protected Reference<ValueItem> createReference(ValueItem vi) {
        return new SoftReference<ValueItem>(vi);
    }

    public synchronized V get(K key) {
        V res = null;
        Reference<ValueItem> ref = cache.get(key);
        if (ref != null) {
            ValueItem vi = ref.get();
            if (vi == null) {
                remove(key);
            } else {
                vi.getKeyItem().accessed();
                res = vi.getValue();
            }
        }
        scheduleCleanup();
        return res;
    }

    public synchronized int size() {
        return cache.size();
    }

    public synchronized void clear() {
        cache.clear();
        keyItems.clear();
    }

    /**
     * 
     * <i>Description:</i> for test purpose
     * 
     * @param key
     */
    public synchronized void remove(K key) {
        Reference<ValueItem> ref = cache.remove(key);
        ValueItem vi = ref == null ? null : ref.get();
        if (vi != null) {
            keyItems.remove(vi.getKeyItem());
        } else {
            keyItems.remove(new KeyItem(key));
        }
    }

    protected void trim() {
        if (keyItems.size() > capacity) {
            cleanup();

            if (keyItems.size() > capacity) {
                Collections.sort(keyItems, keyCountComp);
                while (keyItems.size() > capacity) {
                    KeyItem item = keyItems.remove();
                    cache.remove(item.getKey());
                }
            }
        }
    }

    protected void scheduleCleanup() {
        lastCheckTime = System.currentTimeMillis();
        if (!cleanupTask.isRunning()) {
            // set isRunning on main thread to avoid potential sync issue
            // between main and cleanup thread
            cleanupTask.isRunning = true;
            new Thread(cleanupTask, NAME + (++THREAD_COUNT)).start();
        }
    }

    /**
     * 
     * <i>Description:</i> remove items that are either too old or the value is
     * released already
     * 
     */
    protected synchronized void cleanup() {
        long time = System.currentTimeMillis();
        for (int i = keyItems.size() - 1; i >= 0; i--) {
            KeyItem item = keyItems.get(i);
            K key = item.getKey();
            Reference<ValueItem> ref = cache.get(key);
            if (!item.alive(time, lifeTimeMs) || ref == null
                    || ref.get() == null) {
                cache.remove(key);
                keyItems.remove(i);
            }
        }
    }

    protected Comparator<KeyItem> keyCountComp = new Comparator<KeyItem>() {

        @Override
        public int compare(KeyItem o1, KeyItem o2) {
            int c1 = o1.getCount();
            int c2 = o2.getCount();
            return c1 > c2 ? 1 : (c1 < c2 ? -1 : 0);
        }

    };

    protected class KeyItem {
        private int count;

        private long lastAccess;

        private final K key;

        public KeyItem(K key) {
            super();
            this.key = key;
            accessed();
        }

        public void accessed() {
            count += 1;
            lastAccess = System.currentTimeMillis();
        }

        /**
         * @return the count
         */
        public int getCount() {
            return count;
        }

        /**
         * @return the key
         */
        public K getKey() {
            return key;
        }

        /**
         * @return the lastAccess
         */
        public long getLastAccess() {
            return lastAccess;
        }

        public boolean alive(long lifeTime) {
            return alive(System.currentTimeMillis(), lifeTime);
        }

        public boolean alive(long currentTime, long lifeTime) {
            return currentTime - lastAccess < lifeTime;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + ((key == null) ? 0 : key.hashCode());
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            KeyItem other = (KeyItem) obj;
            if (!getOuterType().equals(other.getOuterType())) {
                return false;
            }
            if (key == null) {
                if (other.key != null) {
                    return false;
                }
            } else if (!key.equals(other.key)) {
                return false;
            }
            return true;
        }

        @SuppressWarnings("rawtypes")
        private SimpleCache getOuterType() {
            return SimpleCache.this;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "KeyItem [key=" + key + ", count=" + count + ", lastAccess="
                    + lastAccess + "]";
        }
    }

    protected class ValueItem {
        private final KeyItem keyItem;

        private V value;

        public ValueItem(KeyItem keyItem, V value) {
            super();
            this.keyItem = keyItem;
            this.value = value;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(V value) {
            this.value = value;
        }

        /**
         * @return the key
         */
        public KeyItem getKeyItem() {
            return keyItem;
        }

        /**
         * @return the value
         */
        public V getValue() {
            return value;
        }

    }

    protected class CleanupTask implements Runnable {
        private boolean isRunning;

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            try {
                while (System.currentTimeMillis() - lastCheckTime < lifeTimeMs) {
                    Thread.sleep(checkInterval);
                }
                cleanup();
            } catch (InterruptedException e) {
            } finally {
                isRunning = false;
            }
        }

        /**
         * @return the isRunning
         */
        public boolean isRunning() {
            return isRunning;
        }

    }
}
