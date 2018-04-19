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

package com.intel.stl.ui.main.view;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.util.Date;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.labels.StandardXYToolTipGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYDataset;

import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.monitor.UnitDescription;

public abstract class AbstractDataChartRangeUpdater
        implements IChartRangeUpdater {

    protected String unitStr = "";

    protected String unitDes = "";

    protected double tickUnitSize = 1L;

    protected static final long KB = 1000;

    protected static final long MB = 1000000;

    protected static final long GB = 1000000000;

    @Override
    public void updateChartRange(JFreeChart chart, long lower, long upper) {
        if (lower > upper) {
            return;
        }

        XYPlot xyplot = (XYPlot) chart.getPlot();
        NumberAxis range = (NumberAxis) xyplot.getRangeAxis();
        range.setRangeWithMargins(lower, upper);
        range.setLowerBound(0);

        // If upper is less than 1000, don't do anything to convert the y-axis
        // label and convert the unit tick.
        TickUnitSource unitSrc = createTickUnits(upper);
        if (unitSrc != null) {
            // Change tick values only if upper is above 1000.
            range.setStandardTickUnits(unitSrc);

            xyplot.getRenderer()
                    .setBaseToolTipGenerator(new DataToolTipGenerator(
                            "<html><b>{0}</b><br> Time: {1}<br> Data: {2}</html>",
                            Util.getHHMMSS(),
                            new DecimalFormat("#,##0.00" + " " + unitDes)));
        } else {
            range.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        }
        // y-axis label
        setChartRangeLabel(range);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IChartRange#setChartRangeLabel(org.jfree.chart
     * .axis.NumberAxis)
     */
    @Override
    public void setChartRangeLabel(NumberAxis range) {
        range.setLabel(unitStr);
    }

    @Override
    public UnitDescription getUnitDescription() {
        return new UnitDescription(unitStr, tickUnitSize);
    }

    @Override
    abstract public TickUnitSource createTickUnits(long upper);

    class DataToolTipGenerator extends StandardXYToolTipGenerator {
        private static final long serialVersionUID = 4825888117284967486L;

        /**
         * Description:
         *
         * @param string
         * @param hhmmss
         * @param decimalFormat
         */
        public DataToolTipGenerator(String string, DateFormat hhmmss,
                DecimalFormat decimalFormat) {
            super(string, hhmmss, decimalFormat);
        }

        @Override
        protected Object[] createItemArray(XYDataset dataset, int series,
                int item) {

            String nullXString = "null";
            String nullYString = "null";

            Object[] result = new Object[3];
            result[0] = dataset.getSeriesKey(series).toString();

            double x = dataset.getXValue(series, item);
            if (Double.isNaN(x) && dataset.getX(series, item) == null) {
                result[1] = nullXString;
            } else {
                DateFormat xDateFormat = this.getXDateFormat();
                if (xDateFormat != null) {
                    result[1] = xDateFormat.format(new Date((long) x));
                } else {

                    result[1] = this.getXFormat().format(x);
                }
            }

            double y = dataset.getYValue(series, item);
            if (Double.isNaN(y) && dataset.getY(series, item) == null) {
                result[2] = nullYString;
            } else {
                DateFormat yDateFormat = this.getYDateFormat();
                if (yDateFormat != null) {
                    result[2] = yDateFormat.format(new Date((long) y));
                } else {
                    result[2] = this.getYFormat().format(y / tickUnitSize);
                }
            }
            return result;
        }

    }
}
