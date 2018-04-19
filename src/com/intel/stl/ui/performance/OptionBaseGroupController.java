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

import java.util.Map;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.OptionChartsView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.model.ChartGroup;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.item.AbstractPerformanceItem;
import com.intel.stl.ui.performance.item.IPerformanceItem;
import com.intel.stl.ui.performance.item.TopNItem;
import com.intel.stl.ui.performance.item.TrendItem;

public class OptionBaseGroupController extends BaseGroupController {

    /**
     * Description:
     * 
     * @param eventBus
     * @param trendItem
     * @param histogramItem
     * @param topNItem
     * @param types
     */
    public OptionBaseGroupController(MBassador<IAppEvent> eventBus,
            String name, TrendItem<GroupSource> trendItem,
            AbstractPerformanceItem<GroupSource> histogramItem,
            TopNItem topNItem, DataType[] types, HistoryType[] historyTypes) {
        super(eventBus, name, trendItem, histogramItem, topNItem);
        if (group != null) {
            installTypes(types);
            installTimeScopes(historyTypes);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.BaseGroupController#createTrendCard(com.
     * intel.stl.ui.performance.item.IPerformanceItem, java.util.Map)
     */
    @Override
    protected ChartsCard createTrendCard(IPerformanceItem<GroupSource> item,
            Map<String, DatasetDescription> map) {
        return createOptionCard(item, map, true, true);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.BaseGroupController#createHistogramCard(
     * com.intel.stl.ui.performance.item.IPerformanceItem, java.util.Map)
     */
    @Override
    protected ChartsCard createHistogramCard(
            IPerformanceItem<GroupSource> item,
            Map<String, DatasetDescription> map) {
        return createOptionCard(item, map, false, true);
    }

    protected void installTypes(DataType... types) {
        ChartsView view = group.getChartView();
        if (view instanceof OptionChartsView) {
            ((OptionChartsView) view).setTypes(types);
        }

        for (ChartGroup member : group.getMembers()) {
            ChartsView mView = member.getChartView();
            if (mView != view && mView instanceof OptionChartsView) {
                ((OptionChartsView) mView).setTypes(types);
            }
        }
    }

    /**
     * 
     * Description:Only install time scopes for trend chart not history nor top
     * N charts.
     * 
     * @param types
     */
    protected void installTimeScopes(HistoryType... types) {
        ChartsView view = group.getChartView();
        if (view instanceof OptionChartsView) {
            ((OptionChartsView) view).setHistoryTypes(types);
        }
    }
}
