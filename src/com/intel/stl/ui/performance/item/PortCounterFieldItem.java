/**
 * Copyright (c) 2016, Intel Corporation
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.TrendSeries;
import com.intel.stl.ui.performance.PortCounterSourceName;

public abstract class PortCounterFieldItem
        extends TrendItem<PortCounterSourceName> {
    private static final Logger log =
            LoggerFactory.getLogger(PortCounterFieldItem.class);

    public PortCounterFieldItem(String shortName, String fullName,
            int maxDataPoints) {
        super(shortName, fullName, maxDataPoints);
    }

    public PortCounterFieldItem(String name, String shortName, String fullName,
            int maxDataPoints) {
        super(name, shortName, fullName, maxDataPoints);
    }

    public PortCounterFieldItem(PortCounterFieldItem item) {
        super(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#getShortName()
     */
    @Override
    public String getShortName() {
        PortCounterSourceName[] sources = getSources();
        if (sources == null || sources.length == 0) {
            return super.getShortName();
        } else {
            return sources[0].getFieldName();
        }
    }

    @Override
    protected void initDataProvider() {
        // Super class TrendItem constructor call this but ignore it because a
        // common provider is initialized in a section controller.
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.TrendItem#createTrendSeries(com.intel
     * .stl.ui.performance.ISource[])
     */
    @Override
    protected List<TrendSeries> createTrendSeries(
            PortCounterSourceName[] series) {
        List<TrendSeries> res = new ArrayList<TrendSeries>();
        if (series != null) {
            for (PortCounterSourceName serie : series) {
                TrendSeries ts = new TrendSeries(serie.getPrettyName());
                res.add(ts);
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.TrendItem#updateTrend(double,
     * java.util.Date, com.intel.stl.ui.performance.ISource)
     */
    @Override
    public void updateTrend(final double value, final Date date,
            final int interval, final PortCounterSourceName name) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                TrendSeries series = getTimeSeries(name);
                if (series != null) {
                    PortCounterFieldItem.super.updateTrend(value, date,
                            interval, name);
                } else {
                    // the shared observer is working on a field name that this
                    // item doesn't support. So we just ignore it.
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.TrendItem#getTimeSeries(java.lang.
     * String )
     */
    @Override
    protected synchronized TrendSeries getTimeSeries(
            PortCounterSourceName name) {
        for (PortCounterSourceName sn : sourceNames) {
            if (sn.equals(name)) {
                for (int i = 0; i < dataset.getSeriesCount(); i++) {
                    if (sn.getPrettyName()
                            .equals(dataset.getSeries(i).getKey())) {
                        return (TrendSeries) dataset.getSeries(i);
                    }
                }
            }
        }
        log.warn(this + " " + name.sourceName() + " is not registered source!");
        return null;
    }
}
