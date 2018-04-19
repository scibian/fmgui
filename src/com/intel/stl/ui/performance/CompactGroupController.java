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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.OptionChartsView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.item.AbstractPerformanceItem;
import com.intel.stl.ui.performance.item.IPerformanceItem;
import com.intel.stl.ui.performance.item.TopNItem;
import com.intel.stl.ui.performance.item.TrendItem;

public class CompactGroupController extends
        AbstractGroupController<GroupSource> {
    private ChartsCard trendCard, auxCard;

    /**
     * Description:
     * 
     * @param eventBus
     * @param trendItem
     * @param histogramItem
     * @param topNItem
     * @param sourceNames
     */
    @SuppressWarnings("unchecked")
    public CompactGroupController(MBassador<IAppEvent> eventBus, String name,
            TrendItem<GroupSource> trendItem,
            AbstractPerformanceItem<GroupSource> histogramItem,
            TopNItem topNItem, HistoryType[] historyTypes) {
        super(eventBus, name, new IPerformanceItem[] { trendItem,
                histogramItem, topNItem });
        installTimeScopes(historyTypes);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected List<ChartsCard> initCards(Map<String, DatasetDescription> map) {
        List<ChartsCard> res = new ArrayList<ChartsCard>();

        if (allItems[0] != null) {
            trendCard = createTrendCard(allItems[0], map);
            res.add(trendCard);
        }

        if (allItems[1] != null || allItems[2] != null) {
            auxCard =
                    createAuxCard(new IPerformanceItem[] { allItems[2],
                            allItems[1] }, map);
            res.add(auxCard);
        }

        return res;
    }

    public void setHelpIDs(String trendHelpID, String auxHelpID) {
        if (trendCard != null) {
            trendCard.setHelpID(trendHelpID);
        }
        if (auxCard != null) {
            auxCard.setHelpID(auxHelpID);
        }
    }

    protected ChartsCard createTrendCard(IPerformanceItem<GroupSource> item,
            Map<String, DatasetDescription> map) {
        return createOptionCard(item, map, true, false);
    }

    protected ChartsCard createAuxCard(IPerformanceItem<GroupSource>[] items,
            Map<String, DatasetDescription> map) {
        return createCard(items, map);
    }

    protected void installTimeScopes(HistoryType... types) {
        if (group != null) {
            ChartsView view = group.getChartView();
            if (view instanceof OptionChartsView) {
                ((OptionChartsView) view).setHistoryTypes(types);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getItemView(com.
     * intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartsView getItemView(IPerformanceItem<GroupSource> item) {
        if (allItems[0] == item) {
            return trendCard.getView();
        } else if (allItems[1] == item || allItems[2] == item) {
            return auxCard.getView();
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getChartArgument
     * (com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartArgument<GroupSource> getChartArgument(
            IPerformanceItem<GroupSource> item) {
        GroupChartArgument arg = new GroupChartArgument();
        String name = item.getName();
        arg.setName(name);
        arg.setFullName(item.getFullName());
        arg.setProvider(item.getCurrentProviderName().name());
        if (item.getType() != null) {
            arg.setDataType(item.getType());
        }
        if (item.getHistoryType() != null) {
            arg.setHistoryType(item.getHistoryType());
        }
        arg.setSources(item.getSources());
        return arg;
    }

}
