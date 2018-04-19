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

package com.intel.stl.ui.performance.provider;

import java.util.concurrent.Future;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.ISource;
import com.intel.stl.ui.performance.observer.IDataObserver;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

public abstract class SimpleDataProvider<E, S extends ISource> extends
        AbstractDataProvider<E, S> {

    protected S sourceName;

    protected Task<E> task;

    protected Future<Void> historyTask;

    private ICallback<E> callback;

    private ICallback<E[]> batchedCallback;

    /**
     * Description:
     *
     * @param sourceName
     */
    public SimpleDataProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.AbstractDataProvider#getSourceNames
     * ()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected S[] getSourceNames() {
        return sourceName == null ? null : (S[]) new ISource[] { sourceName };
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.AbstractDataProvider#sameSources
     * (java.lang.String[])
     */
    @Override
    protected boolean sameSources(S[] names) {
        S name = names == null || names.length == 0 ? null : names[0];
        if (name != null) {
            return name.equals(sourceName);
        } else if (sourceName != null) {
            return sourceName.equals(name);
        } else {
            return true;
        }
    }

    @Override
    protected void setSources(S[] names) {
        sourceName = names == null || names.length == 0 ? null : names[0];
        if (sourceName != null) {
            for (IDataObserver<E> observer : observers) {
                observer.reset();
            }

            if (historyType != null && historyType != HistoryType.CURRENT) {
                historyTask = initHistory(sourceName, getBatchedCallback());
            }
            task = registerTask(sourceName, getCallback());
            onRefresh(null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.IDataProvider#onRefresh(com.intel
     * .stl.ui.common.IProgressObserver, java.lang.String[])
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        if (scheduler == null) {
            return;
        }
        scheduler.submitToBackground(new Runnable() {
            @Override
            public void run() {
                S source = sourceName;
                if (source != null) {
                    E result = refresh(source);
                    if (source.equals(sourceName)) {
                        getCallback().onDone(result);
                    }
                }
            }
        });
    }

    protected ICallback<E> getCallback() {
        if (callback == null) {
            callback = new CallbackAdapter<E>() {
                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java
                 * .lang .Object)
                 */
                @Override
                public synchronized void onDone(E result) {
                    if (result != null) {
                        fireNewData(result);
                    }
                }
            };
        }
        return callback;
    }

    protected abstract E refresh(S sourceName);

    protected abstract Task<E> registerTask(S sourceName, ICallback<E> callback);

    protected abstract void deregisterTask(Task<E> task, ICallback<E> callback);

    @Override
    public void clearSources() {
        if (scheduler != null) {
            if (task != null) {
                deregisterTask(task, callback);
            }
            if (historyTask != null) {
                historyTask.cancel(true);
            }
        }
        sourceName = null;
    }

    protected ICallback<E[]> getBatchedCallback() {
        if (batchedCallback == null) {
            batchedCallback = new CallbackAdapter<E[]>() {
                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java
                 * .lang .Object)
                 */
                @Override
                public synchronized void onDone(E[] result) {
                    if (result != null) {
                        for (E element : result) {
                            fireNewData(element);
                        }
                    }
                }
            };
        }
        return batchedCallback;
    }

    protected abstract Future<Void> initHistory(S sourceName,
            ICallback<E[]> callback);

    @Override
    public void setHistoryType(HistoryType type, boolean forcedUpdate) {
        // For PortCountersProvider, we don't want to call this again for
        // different items. Also, if current selected historyType is same as
        // previous one, we don't need to do anything.
        if (this.historyType == type && !forcedUpdate) {
            return;
        }

        super.setHistoryType(type, forcedUpdate);
        for (IDataObserver<E> observer : observers) {
            observer.reset();
        }

        if (historyTask != null && !historyTask.isDone()) {
            historyTask.cancel(true);
        }

        if (sourceName != null && scheduler != null
                && historyType != HistoryType.CURRENT) {
            historyTask = initHistory(sourceName, getBatchedCallback());
        }
    }

}
