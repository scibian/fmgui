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

package com.intel.stl.ui.performance.item;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.jfree.data.general.Dataset;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.ISource;
import com.intel.stl.ui.performance.observer.IDataObserver;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.performance.provider.IDataProvider;
import com.intel.stl.ui.performance.provider.ISourceObserver;

public abstract class AbstractPerformanceItem<S extends ISource>
        implements IPerformanceItem<S>, ISourceObserver<S> {
    public final static int DEFAULT_DATA_POINTS = 100;

    private final static boolean DEBUG = false;

    // Make it volatile so that the value set by other thread can be used.
    // Updating chart time series is done in EDT thread so this variable has to
    // be a volatile.
    protected volatile int maxDataPoints;

    protected final String name;

    protected String shortName;

    protected String description;

    protected S[] sourceNames;

    protected DatasetDescription datasetDescription;

    private final Map<DataProviderName, IDataProvider<?, S>> providers =
            new HashMap<DataProviderName, IDataProvider<?, S>>();

    protected final Map<DataProviderName, IDataObserver<?>> observers =
            new HashMap<DataProviderName, IDataObserver<?>>();

    protected DataProviderName currentProviderName;

    private Context currentContext;

    private boolean isActive;

    private DataType type;

    private HistoryType historyType;

    public AbstractPerformanceItem(String name, String shortName,
            String fullName) {
        this(name, shortName, fullName, DEFAULT_DATA_POINTS);
    }

    /**
     * Description:
     *
     * @param name
     * @param observer
     */
    public AbstractPerformanceItem(String name, String shortName,
            String fullName, int maxDataPoints) {
        super();
        this.name = name;
        this.shortName = shortName;
        this.description = fullName;
        this.maxDataPoints = maxDataPoints;
    }

    // make a copy
    public AbstractPerformanceItem(AbstractPerformanceItem<S> item) {
        this(item.name, item.shortName, item.description, item.maxDataPoints);
        copyPreparation(item);
        copyDataProvider(item);
        copyDataObserver(item);
        copyDataset(item);
        copyState(item);
    }

    protected void copyPreparation(AbstractPerformanceItem<S> item) {
        initDataProvider();
    }

    protected void copyDataProvider(AbstractPerformanceItem<S> item) {
        IDataProvider<?, S> curProvider = item.getCurrentProvider();
        if (curProvider != null) {
            HistoryType type = curProvider.getHistoryType();
            for (IDataProvider<?, S> provider : providers.values()) {
                provider.setHistoryType(type, false);
            }
        }
    }

    protected void copyDataObserver(AbstractPerformanceItem<S> item) {
        IDataObserver<?> curObserver = item.getCurrentObserver();
        if (curObserver != null) {
            DataType type = curObserver.getType();
            for (IDataObserver<?> observer : observers.values()) {
                observer.setType(type);
            }
        }
    }

    protected abstract void copyDataset(AbstractPerformanceItem<S> item);

    @SuppressWarnings("unchecked")
    protected void copyState(AbstractPerformanceItem<S> item) {
        currentProviderName = item.currentProviderName;
        if (item.sourceNames != null && item.sourceNames.length > 0) {
            sourceNames =
                    (S[]) Array.newInstance(item.sourceNames[0].getClass(),
                            item.sourceNames.length);
            for (int i = 0; i < sourceNames.length; i++) {
                sourceNames[i] = (S) item.sourceNames[i].copy();
            }
        }
        isActive = item.isActive;
        currentContext = item.currentContext;
        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null) {
            provider.removeSourceObserver(this); // do not clear data
            if (provider != null && isActive) {
                provider.setContext(currentContext, null, sourceNames);
            }
            provider.addSourceObserver(this);
        }
    }

    /**
     * @return the name
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * @return the shortName
     */
    @Override
    public String getShortName() {
        return shortName;
    }

    /**
     * @return the description
     */
    @Override
    public String getFullName() {
        return description;
    }

    /**
     * @return the maxDataPoints
     */
    public int getMaxDataPoints() {
        return maxDataPoints;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.IPerformanceItem#getDatasetDescription
     * ()
     */
    @Override
    public DatasetDescription getDatasetDescription() {
        if (datasetDescription == null && getDataset() != null) {
            datasetDescription = new DatasetDescription(name, description,
                    getDataset(), isJumpable());
        }
        return datasetDescription;
    }

    protected abstract void initDataProvider();

    protected abstract Dataset getDataset();

    protected abstract boolean isJumpable();

    @Override
    public <E> void registerDataProvider(DataProviderName name,
            IDataProvider<E, S> provider, IDataObserver<E> observer) {
        providers.put(name, provider);
        observers.put(name, observer);
        provider.addObserver(observer);
        provider.addSourceObserver(this);
        if (currentProviderName == null) {
            currentProviderName = name;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.IPerformanceItem#setDataProvider(java
     * .lang.String)
     */
    @Override
    public void setDataProvider(DataProviderName name) {
        if (currentProviderName != null && currentProviderName.equals(name)) {
            return;
        }

        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null) {
            provider.clear();
        }

        currentProviderName = name;
        if (sourceNames != null) {
            clear();
        }
        sourceNames = null;
        // if (sourceNames!=null) {
        // setSources(sourceNames);
        // }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#getDataProvider()
     */
    @Override
    public DataProviderName getCurrentProviderName() {
        return currentProviderName;
    }

    protected IDataProvider<?, S> getCurrentProvider() {
        if (currentProviderName == null) {
            return null;
        } else {
            return providers.get(currentProviderName);
        }
    }

    protected IDataObserver<?> getCurrentObserver() {
        if (currentProviderName == null) {
            return null;
        } else {
            return observers.get(currentProviderName);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IPerformanceItem#setSources(java.
     * lang.String[])
     */
    @Override
    public void setSources(S[] sourceNames) {
        if (DEBUG) {
            System.out.println("[" + currentProviderName + ":" + getName() + " "
                    + getFullName() + "] setSources "
                    + Arrays.toString(sourceNames));
        }

        if (sameSources(sourceNames)) {
            onRefresh(null);
            return;
        }

        clear();

        this.sourceNames = sourceNames;
        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null && isActive) {
            provider.setContext(currentContext, null, sourceNames);
        }
    }

    protected boolean sameSources(S[] sources) {
        if (sourceNames == null) {
            return sources == null;
        } else if (sources == null) {
            return true;
        }

        Set<S> set1 = new HashSet<S>(Arrays.asList(sourceNames));
        Set<S> set2 = new HashSet<S>(Arrays.asList(sources));
        return set1.equals(set2);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#getSources()
     */
    @Override
    public S[] getSources() {
        return sourceNames;
    }

    protected S getPrimarySource() {
        if (sourceNames == null || sourceNames.length == 0) {
            return null;
        } else {
            return sourceNames[0];
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.ISourceObserver#sourcesToRemove
     * (java.lang.String[])
     */
    @Override
    public void sourcesToRemove(S[] names) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.ISourceObserver#sourcesToAdd(java
     * .lang.String[])
     */
    @Override
    public void sourcesToAdd(S[] names) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.ISourceObserver#sourcesRemoved(
     * java.lang.String[])
     */
    @Override
    public void sourcesRemoved(S[] names) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.ISourceObserver#sourcesAdded(java
     * .lang.String[])
     */
    @Override
    public void sourcesAdded(S[] names) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IPerformanceItem#setContext(com.intel
     * .stl.ui.main.Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        if (DEBUG) {
            System.out.println("[" + currentProviderName + ":" + getName() + " "
                    + getFullName() + "] setContext " + context + " isActive = "
                    + isActive);
        }

        clear();
        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null && isActive) {
            provider.setContext(context, observer, sourceNames);
        }

        if (observer != null) {
            observer.onFinish();
        }
        if (currentContext != context) {
            currentContext = context;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.IPerformanceItem#onRefresh(com.intel
     * .stl.ui.common.IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        if (DEBUG) {
            System.out.println("[" + currentProviderName + ":" + getName() + " "
                    + getFullName() + "] refresh");
        }
        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null && isActive) {
            provider.onRefresh(observer);
        }
        if (observer != null) {
            observer.onFinish();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IPerformanceItem#setType(com.intel
     * .stl.ui.common.performance.IDataObserver.Type)
     */
    @Override
    public void setType(DataType type) {
        if (this.type == type) {
            return;
        }

        if (this.type != null) {
            clear();
        }
        this.type = type;
        for (IDataObserver<?> observer : observers.values()) {
            observer.setType(type);
        }
    }

    @Override
    public DataType getType() {
        return type;
    }

    /**
     *
     * Description: Set HistoryType.
     *
     * @param type
     */
    @Override
    public void setHistoryType(HistoryType type, boolean forcedUpdate) {
        if (this.historyType == type && !forcedUpdate) {
            return;
        }

        if (historyType != null) {
            clear();
        }
        this.historyType = type;

        if (currentContext != null && type != HistoryType.CURRENT) {
            int refreshRate =
                    currentContext.getTaskScheduler().getRefreshRate();
            maxDataPoints = type.getMaxDataPoints(refreshRate);
        } else {
            maxDataPoints = DEFAULT_DATA_POINTS;
        }

        for (IDataProvider<?, S> provider : providers.values()) {
            provider.setHistoryType(type, forcedUpdate);
        }

    }

    @Override
    public HistoryType getHistoryType() {
        return historyType;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.IPerformanceItem#setActive(boolean)
     */
    @Override
    public void setActive(boolean b) {
        if (DEBUG) {
            System.out.println("  <" + currentProviderName + ":" + getName()
                    + " " + getFullName() + "> active = " + b);
        }

        if (isActive == b) {
            return;
        }

        isActive = b;
        IDataProvider<?, S> provider = getCurrentProvider();
        if (provider != null) {
            if (b && currentContext != null) {
                if (DEBUG) {
                    System.out.println("    <" + currentProviderName + ":"
                            + getName() + " " + getFullName()
                            + "> to set active for " + provider + " with "
                            + currentContext);
                }
                provider.setContext(currentContext, null, sourceNames);
            } else {
                provider.clear();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#isActive()
     */
    @Override
    public boolean isActive() {
        return isActive;
    }

}
