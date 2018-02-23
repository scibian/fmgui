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
import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.IntervalXYDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IChartCreator;
import com.intel.stl.ui.model.UtilDataset;

public class PerformanceChartsCreator implements IChartCreator {
    private static final PerformanceChartsCreator instance =
            new PerformanceChartsCreator();

    private PerformanceChartsCreator() {
    }

    public static PerformanceChartsCreator instance() {
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
        if (STLConstants.K0871_BANDWIDTH_TREND.getValue().equals(name)) {
            return getBwTrendChart(dataset);
        } else if (Util.matchPattern(
                UILabels.STL10200_TOPN_BANDWIDTH.getDescription(), name)) {
            return getBwTopNChart(dataset);
        } else if (STLConstants.K0045_BANDWIDTH_HISTOGRAM.getValue()
                .equals(name)) {
            return getBwHistogramChart(dataset);
        } else if (STLConstants.K0872_PACKECT_RATE_TREND.getValue()
                .equals(name)) {
            return getPrTrendChart(dataset);
        } else if (Util.matchPattern(
                UILabels.STL10205_TOPN_PACKET_RATE.getDescription(), name)) {
            return getPrTopNChart(dataset);
        } else if (STLConstants.K0874_CONGESTION_TREND.getValue().equals(name)
                || STLConstants.K0873_INTEGRITY_TREND.getValue().equals(name)
                || STLConstants.K0875_SMA_CONGESTION_TREND.getValue()
                        .equals(name)
                || STLConstants.K0876_BUBBLE_TREND.getValue().equals(name)
                || STLConstants.K0877_SECURITY_TREND.getValue().equals(name)
                || STLConstants.K0878_ROUTING_TREND.getValue().equals(name)) {
            return getErrorTrendChart(dataset);
        } else if (Util.matchPattern(
                UILabels.STL10201_TOPN_CONGESTION.getDescription(), name)
                || Util.matchPattern(UILabels.STL10206_TOPN_SIGNAL_INTEGRITY
                        .getDescription(), name)
                || Util.matchPattern(
                        UILabels.STL10207_TOPN_SMA_CONGESTION.getDescription(),
                        name)
                || Util.matchPattern(
                        UILabels.STL10213_TOPN_BUBBLE.getDescription(), name)
                || Util.matchPattern(
                        UILabels.STL10208_TOPN_SECURITY.getDescription(), name)
                || Util.matchPattern(
                        UILabels.STL10209_TOPN_ROUTING.getDescription(),
                        name)) {
            return getErrorTopNChart(dataset);
        } else if (STLConstants.K0046_CONGESTION_HISTOGRAM.getValue()
                .equals(name)
                || STLConstants.K0068_INTEGRITY_HISTOGRAM.getValue()
                        .equals(name)
                || STLConstants.K0071_SMA_CONGESTION_HISTOGRAM.getValue()
                        .equals(name)
                || STLConstants.K0488_BUBBLE_HISTOGRAM.getValue().equals(name)
                || STLConstants.K0073_SECURITY_HISTOGRAM.getValue().equals(name)
                || STLConstants.K0075_ROUTING_HISTOGRAM.getValue()
                        .equals(name)) {
            return getErrorHistogramChart(dataset);
        }
        return null;
    }

    protected JFreeChart getBwTrendChart(Dataset dataset) {
        if (dataset instanceof UtilDataset) {
            UtilDataset ud = (UtilDataset) dataset;
            JFreeChart chart = ComponentFactory.createUtilXYTrendChart(
                    STLConstants.K0035_TIME.getValue(),
                    STLConstants.K0040_MBPS.getValue(), ud.getUtilDataset(),
                    ud.getPmaDataset(), ud.getTopoDataset(), true);
            return chart;
        } else {
            throw new IllegalArgumentException(
                    "Unsupported dataset type " + dataset.getClass());
        }
    }

    protected JFreeChart getBwTopNChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createTopNBarChart(
                STLConstants.K0053_CAPABILITY.getValue() + " (%)",
                (CategoryDataset) dataset);
        return chart;
    }

    protected JFreeChart getBwHistogramChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createXYBarChart(
                STLConstants.K0053_CAPABILITY.getValue(),
                STLConstants.K0044_NUM_PORTS.getValue(),
                (IntervalXYDataset) dataset, (XYItemLabelGenerator) null);
        NumberAxis axis = (NumberAxis) chart.getXYPlot().getDomainAxis();
        axis.setTickUnit(new NumberTickUnit(0.2, UIConstants.PERCENTAGE));

        axis = (NumberAxis) chart.getXYPlot().getRangeAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

    protected JFreeChart getPrTrendChart(Dataset dataset) {
        if (dataset instanceof UtilDataset) {
            UtilDataset ud = (UtilDataset) dataset;
            JFreeChart chart = ComponentFactory.createUtilXYTrendChart(
                    STLConstants.K0035_TIME.getValue(),
                    STLConstants.K0066_KPPS.getValue(), ud.getUtilDataset(),
                    ud.getPmaDataset(), ud.getTopoDataset(), true);
            return chart;
        } else {
            throw new IllegalArgumentException(
                    "Unsupported dataset type " + dataset.getClass());
        }
    }

    protected JFreeChart getPrTopNChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createTopNBarChart(
                STLConstants.K0066_KPPS.getValue(), (CategoryDataset) dataset);
        return chart;
    }

    protected JFreeChart getErrorTrendChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createXYTrendChart(
                STLConstants.K0035_TIME.getValue(),
                STLConstants.K0125_NUM_EVENTS.getValue(),
                (IntervalXYDataset) dataset, true);
        return chart;
    }

    protected JFreeChart getErrorTopNChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createTopNBarChart(
                STLConstants.K0126_EVENT_RATE.getValue(),
                (CategoryDataset) dataset);
        return chart;
    }

    protected JFreeChart getErrorHistogramChart(Dataset dataset) {
        JFreeChart chart = ComponentFactory.createBarChart(
                STLConstants.K0127_PERCENT_OF_THRESHOLD.getValue(),
                STLConstants.K0044_NUM_PORTS.getValue(),
                (CategoryDataset) dataset);
        NumberAxis axis = (NumberAxis) chart.getCategoryPlot().getRangeAxis();
        axis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        return chart;
    }

}
