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

package com.intel.stl.ui.performance;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.IntervalXYDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IChartCreator;

public class PortCounterChartsCreator implements IChartCreator {
    private static final PortCounterChartsCreator instance =
            new PortCounterChartsCreator();

    private PortCounterChartsCreator() {
    }

    public static PortCounterChartsCreator instance() {
        return instance;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IChartCreator#createChart(java.lang.String,
     * org.jfree.data.general.Dataset)
     */
    @Override
    public JFreeChart createChart(String name, Dataset dataset) {
        if (STLConstants.K0881_PC_PACKET.getValue().equals(name)) {
            return getPacketsChart(dataset);
        } else if (STLConstants.K0880_PC_DATA.getValue().equals(name)) {
            return getDataChart(dataset);
        }
        return getErrorChart(dataset);
    }

    protected JFreeChart getPacketsChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createXYTrendChart(
                STLConstants.K0035_TIME.getValue(),
                STLConstants.K3311_PACKETS.getValue(),
                (IntervalXYDataset) dataset, false);

        NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(false);
        return chart;
    }

    protected JFreeChart getDataChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createXYTrendChart(
                STLConstants.K0035_TIME.getValue(),
                STLConstants.K0748_FLITS.getValue(),
                (IntervalXYDataset) dataset, false);

        NumberAxis rangeAxis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        rangeAxis.setAutoRangeIncludesZero(false);
        return chart;
    }

    protected JFreeChart getErrorChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createXYTrendChart(
                STLConstants.K0035_TIME.getValue(),
                STLConstants.K0882_COUNT.getValue(),
                (IntervalXYDataset) dataset, true);
        return chart;
    }
}
