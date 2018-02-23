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

import java.util.List;

import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.Dataset;

import com.intel.stl.api.performance.FocusPortsRspBean;
import com.intel.stl.api.performance.VFFocusPortsRspBean;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.FocusStatusViz;
import com.intel.stl.ui.model.PortEntry;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.observer.TopNDataObserver;
import com.intel.stl.ui.performance.observer.VFTopNDataObserver;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.performance.provider.FocusPortProvider;
import com.intel.stl.ui.performance.provider.VFFocusPortProvider;

public abstract class TopNItem extends AbstractPerformanceItem<GroupSource> {
    protected DefaultCategoryDataset dataset;

    private Selection selection;

    private int range;

    private final Object copyCritical = new Object();

    public TopNItem(String shortName, String fullName, Selection selection,
            int range) {
        this(shortName, fullName, DEFAULT_DATA_POINTS, selection, range);
    }

    /**
     * Description:
     *
     * @param name
     * @param maxDataPoints
     * @param selection
     * @param range
     */
    public TopNItem(String shortName, String fullName, int maxDataPoints,
            Selection selection, int range) {
        super(UILabels.STL10210_TOPN.getDescription(range), shortName, fullName,
                maxDataPoints);
        this.selection = selection;
        this.range = range;
        initDataProvider();
        initDataset();
    }

    public TopNItem(TopNItem item) {
        super(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#copyPreparation
     * (com.intel.stl.ui.performance.item.AbstractPerformanceItem)
     */
    @Override
    protected void copyPreparation(AbstractPerformanceItem<GroupSource> item) {
        TopNItem topNItem = (TopNItem) item;
        selection = topNItem.selection;
        range = topNItem.range;
        super.copyPreparation(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#copyDataset
     * (com.intel.stl.ui.performance.item.AbstractPerformanceItem)
     */
    @Override
    protected void copyDataset(AbstractPerformanceItem<GroupSource> item) {
        try {
            TopNItem ti = (TopNItem) item;
            synchronized (ti.copyCritical) {
                dataset = (DefaultCategoryDataset) ti.dataset.clone();
            }
        } catch (CloneNotSupportedException e) {
            // shouldn't happen
            e.printStackTrace();
        }
    }

    /**
     * @return the selection
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * @return the range
     */
    public int getRange() {
        return range;
    }

    @Override
    protected void initDataProvider() {
        FocusPortProvider provider =
                new FocusPortProvider(getSelection(), getRange());
        TopNDataObserver observer = new TopNDataObserver(this);
        registerDataProvider(DataProviderName.PORT_GROUP, provider, observer);

        VFFocusPortProvider vfProvider =
                new VFFocusPortProvider(getSelection(), getRange());
        VFTopNDataObserver vfObserver = new VFTopNDataObserver(this);
        registerDataProvider(DataProviderName.VIRTUAL_FABRIC, vfProvider,
                vfObserver);
    }

    protected void initDataset() {
        dataset = createTopNDataset();
    }

    protected DefaultCategoryDataset createTopNDataset() {
        return new DefaultCategoryDataset();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.performance.IPerformanceItem#getDataset()
     */
    @Override
    public Dataset getDataset() {
        return dataset;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#isJumpable()
     */
    @Override
    protected boolean isJumpable() {
        return true;
    }

    public void updateTopN(final List<FocusPortsRspBean> portList) {
        if (dataset == null || portList == null || portList.isEmpty()) {
            return;
        }

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                synchronized (copyCritical) {
                    dataset.setNotify(false);
                    dataset.clear();
                    for (FocusPortsRspBean port : portList) {
                        PortEntry pe = new PortEntry(port.getNodeDesc(),
                                port.getNodeLid(), port.getPortNumber());
                        pe.setObject(FocusStatusViz
                                .getFocusStatusViz(port.getLocalStatus()));
                        dataset.addValue(getValue(port),
                                STLConstants.K0113_PORT_VALUE.getValue(), pe);
                        dataset.addValue(getNeighborValue(port),
                                STLConstants.K0114_NBR_VALUE.getValue(), pe);
                    }
                    dataset.setNotify(true);
                }
            }

        });
    }

    protected Number getValue(FocusPortsRspBean port) {
        return port.getValue();
    }

    protected Number getNeighborValue(FocusPortsRspBean port) {
        return port.getNeighborValue();
    }

    public void updateVFTopN(final List<VFFocusPortsRspBean> portList) {
        if (dataset == null || portList == null || portList.isEmpty()) {
            return;
        }

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                synchronized (copyCritical) {
                    dataset.setNotify(false);
                    dataset.clear();
                    for (VFFocusPortsRspBean port : portList) {
                        PortEntry pe = new PortEntry(port.getNodeDesc(),
                                port.getNodeLid(), port.getPortNumber());
                        pe.setObject(FocusStatusViz
                                .getFocusStatusViz(port.getLocalStatus()));
                        dataset.addValue(port.getValue(),
                                STLConstants.K0113_PORT_VALUE.getValue(), pe);
                        dataset.addValue(port.getNeighborValue(),
                                STLConstants.K0114_NBR_VALUE.getValue(), pe);
                    }
                    dataset.setNotify(true);
                }
            }

        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.performance.IPerformanceItem#clear()
     */
    @Override
    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                synchronized (copyCritical) {
                    if (dataset != null) {
                        dataset.clear();
                    }
                }
            }
        });
    }

}
