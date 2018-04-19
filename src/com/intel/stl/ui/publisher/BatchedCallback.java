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

package com.intel.stl.ui.publisher;

import java.lang.reflect.Array;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BatchedCallback<E> {
    private static final Logger log = LoggerFactory
            .getLogger(BatchedCallback.class);

    private final Class<E> klass;

    private final int size;

    private final ICallback<E[]> finalCallback;

    private final ICallback<E>[] callbacks;

    private E[] temp;

    private int count;

    private E[] results;

    private final int[] ids;

    private int targetId;

    private int maxId;

    private final Set<Throwable> processedErrors;

    @SuppressWarnings("unchecked")
    public BatchedCallback(int size, ICallback<E[]> callback, Class<E> klass) {
        this.size = size;
        this.finalCallback = callback;
        this.klass = klass;
        callbacks = new ICallback[size];
        for (int i = 0; i < size; i++) {
            callbacks[i] = new IndexedCallback(i);
        }
        processedErrors = new HashSet<Throwable>();
        temp = (E[]) Array.newInstance(klass, size);
        ids = new int[size];
        targetId = maxId = 1;
    }

    public ICallback<E> getCallback(int index) {
        return callbacks[index];
    }

    @SuppressWarnings("unchecked")
    protected synchronized void addResult(int index, E result) {
        ids[index] += 1;
        if (ids[index] > targetId) {
            if (maxId < ids[index]) {
                maxId = ids[index];
            }
            log.warn(klass.getSimpleName() + " Result " + index + " (id="
                    + ids[index] + " targetId=" + targetId + " maxId=" + maxId
                    + ") already exist! You may have too small refresh rate.");
        } else if (ids[index] == targetId) {
            temp[index] = result;
            count += 1;
            finalCallback.onProgress((double) count / size);
            if (count == size) {
                // log.info("Release id="+targetId+" maxId="+maxId);
                results = (E[]) Array.newInstance(klass, size);
                System.arraycopy(temp, 0, results, 0, size);
                temp = (E[]) Array.newInstance(klass, size);
                count = 0;
                for (int i = 0; i < size; i++) {
                    ids[i] = ids[i] - maxId;
                }
                maxId = 1;
                targetId = 1;
                processedErrors.clear();
                // log.info("Init next release "+Arrays.toString(ids));
                finalCallback.onDone(results);
            }
        }
    }

    protected void clear() {
        processedErrors.clear();
        targetId = maxId = 1;
        for (int i = 0; i < size; i++) {
            temp[i] = null;
            ids[i] = 0;
        }
    }

    protected class IndexedCallback extends CallbackAdapter<E> {
        private final int index;

        public IndexedCallback(int index) {
            super();
            this.index = index;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.intel.stl.ui.publisher.CallBackAdapter#onDone(java.lang.Object)
         */
        @Override
        public void onDone(E result) {
            addResult(index, result);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.intel.stl.ui.publisher.CallBackAdapter#onError(java.lang.Throwable
         * [])
         */
        @Override
        public void onError(Throwable... errors) {
            Set<Throwable> newErrors = new HashSet<Throwable>();
            for (Throwable error : errors) {
                if (!processedErrors.contains(error)) {
                    newErrors.add(error);
                } else {
                    processedErrors.add(error);
                }
            }
            finalCallback.onError(newErrors.toArray(new Throwable[0]));
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.stl.ui.publisher.CallbackAdapter#reset()
         */
        @Override
        public void reset() {
            clear();
        }

        public boolean belongToCallback(ICallback<E[]> callback) {
            return finalCallback == callback;
        }
    }
}
