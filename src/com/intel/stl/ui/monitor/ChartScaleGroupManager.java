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

package com.intel.stl.ui.monitor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jfree.chart.JFreeChart;
import org.jfree.data.general.Dataset;

import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.view.IChartRangeUpdater;

public abstract class ChartScaleGroupManager<E extends Dataset> {
    protected Map<JFreeChart, E> chartDataMap =
            new ConcurrentHashMap<JFreeChart, E>();

    protected long lower;

    protected long upper;

    protected IChartRangeUpdater rangeUpdater;

    public ChartScaleGroupManager() {
        rangeUpdater = getChartRangeUpdater();
    }

    public void addChart(JFreeChart chart, E dataset) {
        chartDataMap.put(chart, dataset);
    }

    public void removeChart(JFreeChart chart) {
        chartDataMap.remove(chart);
        updateChartsRange();
    }

    /**
     *
     * Description: Update relevant JFreeCharts using the IChartRangeUpdater.
     * The access to JFreeCharts is defined in actual implementation for now.
     *
     */
    public void updateChartsRange() {
        calculateRangeBounds();
        // This manager should have all Charts update in a list.
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                for (JFreeChart chart : chartDataMap.keySet()) {
                    rangeUpdater.updateChartRange(chart, lower, upper);
                }
            }
        });
    }

    /**
     *
     * Description: Calculates min/max for all datasets registered.
     *
     */
    protected void calculateRangeBounds() {
        long lower = Long.MAX_VALUE;
        long upper = Long.MIN_VALUE;

        for (E dataset : chartDataMap.values()) {
            long[] minMax = getMinMax(dataset);
            lower = Math.min(lower, minMax[0]);
            upper = Math.max(upper, minMax[1]);
        }

        this.lower = lower;
        this.upper = upper;
    }

    abstract IChartRangeUpdater getChartRangeUpdater();

    abstract long[] getMinMax(E dataset);
}
