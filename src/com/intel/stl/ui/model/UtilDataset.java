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

package com.intel.stl.ui.model;

import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;

import com.intel.stl.ui.performance.PmaNoRespGroupSource;
import com.intel.stl.ui.performance.TopoNoRespGroupSource;

public class UtilDataset extends TimePeriodValuesCollection {
    private static final long serialVersionUID = 7676929820872719510L;

    private final TimePeriodValuesCollection utilDataset =
            new TimePeriodValuesCollection();

    private final TimePeriodValuesCollection pmaDataset =
            new TimePeriodValuesCollection();

    private final TimePeriodValuesCollection topoDataset =
            new TimePeriodValuesCollection();

    public UtilDataset() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jfree.data.time.TimeSeriesCollection#addSeries(org.jfree.data.time.
     * TimeSeries)
     */
    @Override
    public void addSeries(TimePeriodValues series) {
        super.addSeries(series);
        String name = (String) series.getKey();
        if (PmaNoRespGroupSource.isPmaNoResp(name)) {
            pmaDataset.addSeries(series);
        } else if (TopoNoRespGroupSource.isTopoNoResp(name)) {
            topoDataset.addSeries(series);
        } else {
            utilDataset.addSeries(series);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jfree.data.time.TimeSeriesCollection#removeSeries(org.jfree.data.time
     * .TimeSeries)
     */
    @Override
    public void removeSeries(TimePeriodValues series) {
        super.removeSeries(series);
        pmaDataset.removeSeries(series);
        topoDataset.removeSeries(series);
        utilDataset.removeSeries(series);
    }

    /**
     * @return the utilDataset
     */
    public TimePeriodValuesCollection getUtilDataset() {
        return utilDataset;
    }

    /**
     * @return the pmaDataset
     */
    public TimePeriodValuesCollection getPmaDataset() {
        return pmaDataset;
    }

    /**
     * @return the topoDataset
     */
    public TimePeriodValuesCollection getTopoDataset() {
        return topoDataset;
    }

}
