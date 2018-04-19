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

import org.jfree.data.general.Dataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.performance.GroupSource;

public abstract class HistogramItem extends
        AbstractPerformanceItem<GroupSource> {
    protected XYSeriesCollection dataset;

    private final Object copyCritical = new Object();

    public HistogramItem(String shortName, String fullName) {
        this(shortName, fullName, DEFAULT_DATA_POINTS);
    }

    /**
     * Description:
     * 
     * @param sourceName
     * @param maxDataPoints
     */
    public HistogramItem(String shortName, String fullName, int maxDataPoints) {
        super(STLConstants.K0079_HISTOGRAM.getValue(), shortName, fullName,
                maxDataPoints);
        initDataProvider();
        initDataset();
    }

    public HistogramItem(HistogramItem item) {
        super(item);
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
            HistogramItem hi = (HistogramItem) item;
            synchronized (hi.copyCritical) {
                dataset = (XYSeriesCollection) hi.dataset.clone();
            }
        } catch (CloneNotSupportedException e) {
            // shouldn't happen
            e.printStackTrace();
        }
    }

    protected void initDataset() {
        dataset = createHistogramDataset();
    }

    protected XYSeriesCollection createHistogramDataset() {
        return new XYSeriesCollection();
    }

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
        return false;
    }

    public void updateHistogram(final int[] values, final double range) {
        if (dataset == null || values == null || values.length <= 1) {
            return;
        }

        final XYSeries xyseries = new XYSeries(name);
        double x = 0;
        double step = range / values.length;
        for (int i = 0; i < values.length; i++) {
            xyseries.add(x, values[i]);
            x += step;
        }

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                synchronized (copyCritical) {
                    dataset.setNotify(false);
                    dataset.removeAllSeries();
                    dataset.addSeries(xyseries);
                    dataset.setIntervalPositionFactor(0);
                    dataset.setIntervalWidth(range / values.length);
                    dataset.setNotify(true);
                }
            }
        });
    }

    @Override
    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                synchronized (copyCritical) {
                    if (dataset != null) {
                        dataset.removeAllSeries();
                    }
                }
            }
        });
    }
}
