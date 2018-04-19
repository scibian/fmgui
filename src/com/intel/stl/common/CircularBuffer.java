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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A CircularBuffer with life span control. Old items or items not fit in the
 * buffer's capacity will be removed.
 */
public class CircularBuffer<K, V> {

    /**
     * Introduce lifeSpan to ensure we always have the latest imageinfo for an
     * image id. the worst case can be that on the client side we always happen
     * use the same id, then we need to figure out when the imageinfo is
     * updated. In theory the safe life span for an image should be (TotalImages
     * - 1) * SweepInterval. Consider UI client's clock speed may be different
     * from SM node's clock speed. We can adjust life span to be: (TotalImages -
     * 2) * SweepInterval. When TotalImages is less than 2, it should be zero
     * that means we do not cache images.
     */
    private final long lifeSpan; // ms

    private final ArrayBlockingQueue<BufferItem<K, V>> buffer;

    private final ConcurrentLinkedQueue<V> saveQueue;

    public CircularBuffer(int lifeSpanInSeconds) {
        this(lifeSpanInSeconds, 10);
    }

    public CircularBuffer(long lifeSpanInSeconds, int size) {
        this.lifeSpan = lifeSpanInSeconds * 1000; // to ms
        buffer = new ArrayBlockingQueue<BufferItem<K, V>>(size);
        saveQueue = new ConcurrentLinkedQueue<V>();
    }

    public void put(K key, V value) {
        BufferItem<K, V> item = new BufferItem<K, V>(key, value);
        if (get(key) != null) {
            return;
        }
        while (!buffer.offer(item)) {
            saveQueue.add(buffer.poll().getValue());
        }
    }

    public List<V> purgeSaveQueue() {
        List<V> result = new ArrayList<V>(saveQueue.size());
        while (!saveQueue.isEmpty()) {
            V value = saveQueue.poll();
            if (value != null) {
                result.add(value);
            }
        }
        return result;
    }

    public int getSaveQueueSize() {
        return saveQueue.size();
    }

    public V get(K key) {
        Iterator<BufferItem<K, V>> it = buffer.iterator();
        while (it.hasNext()) {
            BufferItem<K, V> item = it.next();
            if (item.getKey().equals(key)) {
                if (item.isValid()) {
                    return item.getValue();
                } else {
                    it.remove();
                }
            }
        }
        return null;
    }

    private class BufferItem<I, L> {
        private final long timeStamp;

        private final I key;

        private final L value;

        public BufferItem(I key, L value) {
            timeStamp = System.currentTimeMillis();
            this.key = key;
            this.value = value;
        }

        public I getKey() {
            return key;
        }

        public L getValue() {
            return value;
        }

        public boolean isValid() {
            return System.currentTimeMillis() - timeStamp < lifeSpan;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof BufferItem)) {
                return false;
            }
            @SuppressWarnings("unchecked")
            BufferItem<I, L> other = (BufferItem<I, L>) obj;
            return (key.equals(other.key));
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

    }

}
