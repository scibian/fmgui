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

import java.util.List;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.item.BBHistogramItem;
import com.intel.stl.ui.performance.item.BBTopNItem;
import com.intel.stl.ui.performance.item.BBTrendItem;
import com.intel.stl.ui.performance.item.BWHistogramItem;
import com.intel.stl.ui.performance.item.BWTopNItem;
import com.intel.stl.ui.performance.item.BWTrendItem;
import com.intel.stl.ui.performance.item.CGHistogramItem;
import com.intel.stl.ui.performance.item.CGTopNItem;
import com.intel.stl.ui.performance.item.CGTrendItem;
import com.intel.stl.ui.performance.item.IntegrityHistogramItem;
import com.intel.stl.ui.performance.item.IntegrityTopNItem;
import com.intel.stl.ui.performance.item.IntegrityTrendItem;
import com.intel.stl.ui.performance.item.PRTopNItem;
import com.intel.stl.ui.performance.item.PRTrendItem;
import com.intel.stl.ui.performance.item.REHistogramItem;
import com.intel.stl.ui.performance.item.RETopNItem;
import com.intel.stl.ui.performance.item.RETrendItem;
import com.intel.stl.ui.performance.item.SCHistogramItem;
import com.intel.stl.ui.performance.item.SCTopNItem;
import com.intel.stl.ui.performance.item.SCTrendItem;
import com.intel.stl.ui.performance.item.SEHistogramItem;
import com.intel.stl.ui.performance.item.SETopNItem;
import com.intel.stl.ui.performance.item.SETrendItem;

import net.engio.mbassy.bus.MBassador;

public class CompactGroupFactory {
    private static final String TOP_N_CHART = "Top";

    public static CompactGroupController createBandwidthGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0041_BANDWIDTH.getValue(), new BWTrendItem(),
                new BWHistogramItem(), new BWTopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_BW);
        setComponentNames(res, STLConstants.K0890_BANDWIDTH.getValue());
        setUnitGroupHelp(res);
        return res;
    }

    public static CompactGroupController createPacketRateGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0065_PACKET_RATE.getValue(), new PRTrendItem(),
                null, new PRTopNItem(topN), HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_PR);
        setComponentNames(res, STLConstants.K0891_PKT_RATE.getValue());
        setUnitGroupHelp(res);
        return res;
    }

    private static void setUnitGroupHelp(CompactGroupController group) {
        final HelpAction helpAction = HelpAction.getInstance();
        group.setHelpIDs(helpAction.getUnitGroup(), helpAction.getUnitGroup());
    }

    public static CompactGroupController createCongestionGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0043_CONGESTION_ERROR.getValue(),
                new CGTrendItem(), new CGHistogramItem(), new CGTopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_CG);
        setComponentNames(res, STLConstants.K0892_CONGESTION.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    public static CompactGroupController createSignalIntegrityGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0067_INTEGRITY_ERROR.getValue(),
                new IntegrityTrendItem(), new IntegrityHistogramItem(),
                new IntegrityTopNItem(topN), HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_SI);
        setComponentNames(res, STLConstants.K0894_INTEGRITY.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    public static CompactGroupController createSmaCongestionGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0070_SMA_CONGESTION_ERROR.getValue(),
                new SCTrendItem(), new SCHistogramItem(), new SCTopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_SC);
        setComponentNames(res, STLConstants.K0893_SMA_CONGESTION.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    public static CompactGroupController createBubbleGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0487_BUBBLE_ERROR.getValue(), new BBTrendItem(),
                new BBHistogramItem(), new BBTopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_BB);
        setComponentNames(res, STLConstants.K0895_BUBBLE.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    public static CompactGroupController createSecurityGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0072_SECURITY.getValue(), new SETrendItem(),
                new SEHistogramItem(), new SETopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_SE);
        setComponentNames(res, STLConstants.K0896_SECURITY.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    public static CompactGroupController createRoutingGroup(
            MBassador<IAppEvent> eventBus, int topN, DataType type,
            HistoryType historyType, GroupSource... sourceNames) {
        final CompactGroupController res = new CompactGroupController(eventBus,
                STLConstants.K0074_ROUTING_ERROR.getValue(), new RETrendItem(),
                new REHistogramItem(), new RETopNItem(topN),
                HistoryType.values());
        res.setDataSources(sourceNames);
        res.setType(type);
        res.setHistoryType(historyType);
        res.setPinID(PinID.SUBNET_RT);
        setComponentNames(res, STLConstants.K0897_ROUTING.getValue());
        setErrorGroupHelp(res);
        return res;
    }

    /**
     *
     * @param res
     * @param sectionName
     */
    private static void setComponentNames(CompactGroupController res,
            String sectionName) {
        final List<ChartsCard> cards = res.getCards();
        for (final ChartsCard card : cards) {
            final String chartName = card.getCurrentChart();
            final ChartsView view = card.getView();
            String helpBtnName = "";
            String pinBtnName = "";
            String panelName = "";
            if (chartName.equals(STLConstants.K0078_TREND.getValue())) {
                helpBtnName = WidgetName.HP_PERF_TREND_HELP_.name();
                pinBtnName = WidgetName.HP_PERF_TREND_PIN_.name();
                panelName = WidgetName.HP_PERF_TREND_PANEL_.name();
            } else if (chartName.startsWith(TOP_N_CHART) || chartName
                    .equals(STLConstants.K0079_HISTOGRAM.getValue())) {
                helpBtnName = WidgetName.HP_PERF_TOPN_HISTOGRAM_HELP_.name();
                pinBtnName = WidgetName.HP_PERF_TOPN_HISTOGRAM_PIN_.name();
                panelName = WidgetName.HP_PERF_TOPN_HISTOGRAM_PANEL_.name();
            }
            view.setHelpButtonName(helpBtnName + sectionName);
            view.setPinButtonName(pinBtnName + sectionName);
            view.setMainPanelName(panelName + sectionName);
        }
    }

    // when we have help for each counter type, we will do this in each
    // createXXXGroup method
    private static void setErrorGroupHelp(CompactGroupController group) {
        final HelpAction helpAction = HelpAction.getInstance();
        group.setHelpIDs(helpAction.getErrorGroup(),
                helpAction.getErrorGroup());
    }
}
