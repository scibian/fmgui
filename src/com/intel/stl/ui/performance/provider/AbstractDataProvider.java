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
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.ISource;
import com.intel.stl.ui.performance.observer.IDataObserver;
import com.intel.stl.ui.publisher.TaskScheduler;

public abstract class AbstractDataProvider<E, S extends ISource> implements
        IDataProvider<E, S> {
    private final static boolean DEBUG = false;

    protected TaskScheduler scheduler;

    protected List<IDataObserver<E>> observers =
            new CopyOnWriteArrayList<IDataObserver<E>>();

    protected List<ISourceObserver<S>> sourceObservers =
            new CopyOnWriteArrayList<ISourceObserver<S>>();

    protected HistoryType historyType;

    protected boolean forcedUpdate;

    @Override
    public void setContext(Context context, IProgressObserver progressObserver,
            S[] sourceNames) {
        if (DEBUG) {
            System.out.println("setContext " + context + " with sources "
                    + Arrays.toString(sourceNames));
        }

        if (context == null) {
            clear();
            return;
        }

        boolean sameSources = sameSources(sourceNames);
        if (!hasScheduler(context)) {
            clear();
            scheduler = context.getTaskScheduler();
        } else if (sameSources) {
            onRefresh(null);
            return;
        } else {
            clear();
        }

        fireSourcesToAdd(sourceNames);
        try {
            setSources(sourceNames);
        } finally {
            fireSourcesAdded(sourceNames);
        }
    }

    protected abstract S[] getSourceNames();

    protected abstract boolean sameSources(S[] names);

    protected abstract void setSources(S[] names);

    protected boolean hasScheduler(Context context) {
        return scheduler != null && context.getTaskScheduler() == scheduler;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.IDataProvider#addObserver(com.intel
     * .stl.ui.common.performance.IDataObserver)
     */
    @Override
    public void addObserver(IDataObserver<E> observer) {
        observers.add(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.IDataProvider#removeObserver(com.
     * intel.stl.ui.common.performance.IDataObserver)
     */
    @Override
    public void removeObserver(IDataObserver<E> observer) {
        observers.remove(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.provider.IDataProvider#addSourceObserver
     * (com.intel.stl.ui.performance.provider.ISourceObserver)
     */
    @Override
    public void addSourceObserver(ISourceObserver<S> observer) {
        sourceObservers.add(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.provider.IDataProvider#removeSourceObserver
     * (com.intel.stl.ui.performance.provider.ISourceObserver)
     */
    @Override
    public void removeSourceObserver(ISourceObserver<S> observer) {
        sourceObservers.remove(observer);
    }

    protected void fireNewData(E data) {
        for (IDataObserver<E> observer : observers) {
            observer.processData(data);
        }
    }

    protected void fireSourcesToRemove(S[] names) {
        for (ISourceObserver<S> observer : sourceObservers) {
            observer.sourcesToRemove(names);
        }
    }

    protected void fireSourcesToAdd(S[] names) {
        for (ISourceObserver<S> observer : sourceObservers) {
            observer.sourcesToAdd(names);
        }
    }

    protected void fireSourcesRemoved(S[] names) {
        for (ISourceObserver<S> observer : sourceObservers) {
            observer.sourcesRemoved(names);
        }
    }

    protected void fireSourcesAdded(S[] names) {
        for (ISourceObserver<S> observer : sourceObservers) {
            observer.sourcesAdded(names);
        }
    }

    @Override
    public void setHistoryType(HistoryType type, boolean forcedUpdate) {
        this.historyType = type;
        this.forcedUpdate = forcedUpdate;
    }

    /**
     * @return the historyType
     */
    @Override
    public HistoryType getHistoryType() {
        return historyType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.performance.provider.IDataProvider#clear()
     */
    @Override
    public void clear() {
        S[] oldSources = getSourceNames();
        if (oldSources != null && oldSources.length > 0) {
            fireSourcesToRemove(oldSources);
        }
        try {
            clearSources();
        } finally {
            if (oldSources != null && oldSources.length > 0) {
                fireSourcesRemoved(oldSources);
            }
        }
    }

    protected abstract void clearSources();

}
