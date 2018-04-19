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

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Future;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.ISource;
import com.intel.stl.ui.performance.observer.IDataObserver;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

public abstract class CombinedDataProvider<E, S extends ISource> extends
        AbstractDataProvider<E[], S> {
    protected S[] sourceNames;

    protected Object sourceCritical = new Object();

    protected List<Task<E>> tasks;

    protected Future<Void> historyTask;

    private ICallback<E[]> callback;

    /**
     * Description:
     *
     * @param sourceNames
     */
    public CombinedDataProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.AbstractDataProvider#getSourceNames
     * ()
     */
    @Override
    protected S[] getSourceNames() {
        return sourceNames;
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
        Set<S> newSet = new HashSet<S>();
        if (names != null && names.length > 0) {
            newSet.addAll(Arrays.asList(names));
        }

        synchronized (sourceCritical) {
            Set<S> oldSet = new HashSet<S>();
            if (sourceNames != null) {
                oldSet.addAll(Arrays.asList(sourceNames));
            }
            return newSet.equals(oldSet);
        }
    }

    @Override
    protected void setSources(S[] names) {
        synchronized (sourceCritical) {
            sourceNames = names;
            if (names != null && names.length > 0) {
                for (IDataObserver<E[]> observer : observers) {
                    observer.reset();
                }

                if (historyType != null && historyType != HistoryType.CURRENT) {
                    historyTask = initHistory(names, getCallback());
                }
                tasks = registerTasks(names, getCallback());
                onRefresh(null);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.IDataProvider#onRefresh(com.intel
     * .stl.ui.common.IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        if (scheduler == null) {
            return;
        }
        scheduler.submitToBackground(new Runnable() {
            @Override
            public void run() {
                S[] sources = getSourceNames();
                if (sources.length > 0) {
                    E[] result = refresh(sources);
                    if (sameSources(sources)) {
                        getCallback().onDone(result);
                    }
                }
            }
        });
    }

    protected ICallback<E[]> getCallback() {
        if (callback == null) {
            callback = new CallbackAdapter<E[]>() {
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
                        fireNewData(result);
                    }
                }
            };
        }
        return callback;
    }

    protected abstract E[] refresh(S[] sourceNames);

    protected abstract List<Task<E>> registerTasks(S[] sourceNames,
            ICallback<E[]> callback);

    protected abstract void deregisterTasks(List<Task<E>> task,
            ICallback<E[]> callback);

    @Override
    public void clearSources() {
        if (scheduler != null) {
            if (tasks != null) {
                deregisterTasks(tasks, callback);
            }

            if (historyTask != null) {
                historyTask.cancel(true);
            }
        }

        synchronized (sourceCritical) {
            sourceNames = null;
        }
    }

    protected abstract Future<Void> initHistory(S[] names,
            ICallback<E[]> callback);

    /**
     *
     * Description:TrendItem set HistoryType to CombinedDataProvider to
     * calculate the maxDataPoints.
     *
     * @param type
     */
    @Override
    public void setHistoryType(HistoryType type, boolean forcedUpdate) {
        super.setHistoryType(type, forcedUpdate);

        for (IDataObserver<E[]> observer : observers) {
            observer.reset();
        }

        if (historyTask != null && !historyTask.isDone()) {
            historyTask.cancel(true);
        }

        S[] sources = getSourceNames();
        if (sources != null && scheduler != null
                && historyType != HistoryType.CURRENT) {
            historyTask = initHistory(sources, getCallback());
        }
    }
}
