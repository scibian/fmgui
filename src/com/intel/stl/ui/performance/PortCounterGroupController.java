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

package com.intel.stl.ui.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.jfree.data.time.TimePeriodValuesCollection;

import com.intel.stl.ui.common.ChartsCard;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.IChartCreator;
import com.intel.stl.ui.common.view.OptionChartsView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.view.IDataTypeListener;
import com.intel.stl.ui.model.DatasetDescription;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.monitor.ChartScaleGroupManager;
import com.intel.stl.ui.monitor.DataChartScaleGroupManager;
import com.intel.stl.ui.monitor.PacketChartScaleGroupManager;
import com.intel.stl.ui.performance.item.IPerformanceItem;
import com.intel.stl.ui.performance.item.PCDataItem;
import com.intel.stl.ui.performance.item.PCErrItem;
import com.intel.stl.ui.performance.item.PCPacketItem;
import com.intel.stl.ui.performance.item.PortCounterFieldItem;

import net.engio.mbassy.bus.MBassador;

public class PortCounterGroupController
        extends AbstractGroupController<PortCounterSourceName> {
    private ChartsCard portCounterChartCard;

    private final PacketChartScaleGroupManager packetScaleManager;

    private final DataChartScaleGroupManager dataScaleManager;

    private OptionChartsView optionChartsView;

    private PortCounterFieldItem oldSelectedItem;

    PCErrItem errItem;

    PCDataItem dataItem;

    PCPacketItem packetItem;

    @SuppressWarnings("unchecked")
    public PortCounterGroupController(MBassador<IAppEvent> eventBus,
            String name, PCErrItem errItem, PCDataItem dataItem,
            PCPacketItem packetItem, HistoryType[] historyTypes) {
        super(eventBus, name,
                new IPerformanceItem[] { errItem, dataItem, packetItem });

        this.errItem = errItem;
        this.dataItem = dataItem;
        this.packetItem = packetItem;

        installTimeScopes(historyTypes);

        packetScaleManager = PacketChartScaleGroupManager.getInstance();
        if (portCounterChartCard != null) {
            packetScaleManager.addChart(
                    portCounterChartCard.getView()
                            .getChart(packetItem.getName()),
                    (TimePeriodValuesCollection) packetItem.getDataset());
        }
        packetItem.setScaleManager(packetScaleManager);

        dataScaleManager = DataChartScaleGroupManager.getInstance();
        if (portCounterChartCard != null) {
            dataScaleManager.addChart(
                    portCounterChartCard.getView().getChart(dataItem.getName()),
                    (TimePeriodValuesCollection) dataItem.getDataset());
        }
        dataItem.setScaleManager(dataScaleManager);
    }

    protected void installTimeScopes(HistoryType... types) {
        if (optionChartsView != null) {
            optionChartsView.setHistoryTypes(types);
        }
    }

    @Override
    protected List<ChartsCard> initCards(Map<String, DatasetDescription> map) {
        List<ChartsCard> res = new ArrayList<ChartsCard>();
        portCounterChartCard = createCard(allItems, map);
        res.add(portCounterChartCard);
        return res;
    }

    @SuppressWarnings("serial")
    @Override
    protected ChartsCard createCard(
            final IPerformanceItem<PortCounterSourceName>[] items,
            Map<String, DatasetDescription> map) {
        optionChartsView = new OptionChartsView("", getChartCreator()) {
            /**
             * To avoid adding chart list, leave this empty. charts(hashmap)
             * will still be created for 3 different items.
             */
            @Override
            protected void setChartNames(List<DatasetDescription> datasets) {

            }
        };
        String[] names = new String[items.length];
        for (int i = 0; i < items.length; i++) {
            if (items[i] != null) {
                names[i] = getItemName(items[i]);
            }
        }

        ChartsCard chartsCard = createChartsCard(optionChartsView, map, names);
        optionChartsView
                .setHistoryTypeListener(new IDataTypeListener<HistoryType>() {
                    @Override
                    public void onDataTypeChange(HistoryType oldType,
                            HistoryType newType) {
                        optionChartsView.setHistoryType(newType);

                        for (int i = 0; i < items.length; i++) {
                            items[i].setHistoryType(newType, false);
                        }
                        UndoHandler undoHandler = getUndoHandler();
                        if (undoHandler != null
                                && !undoHandler.isInProgress()) {
                            UndoableChartHistorySelection sel =
                                    new UndoableChartHistorySelection(items,
                                            optionChartsView, oldType, newType);
                            undoHandler.addUndoAction(sel);
                        }
                    }
                });

        return chartsCard;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#clearPin(com.intel
     * .stl.ui.common.ChartsCard,
     * com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected void clearPin(ChartsCard pinCard,
            IPerformanceItem<PortCounterSourceName> pinItem) {
        super.clearPin(pinCard, pinItem);
        // remove from scale manager
        ChartScaleGroupManager<TimePeriodValuesCollection> scaleMgr =
                ((PortCounterFieldItem) pinItem).getScaleManager();
        if (scaleMgr != null) {
            scaleMgr.removeChart(pinCard.getView().getChart(pinItem.getName()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.AbstractGroupController#getPinCard(com.intel
     * .stl.ui.performance.ChartArgument,
     * com.intel.stl.ui.performance.item.IPerformanceItem)
     */
    @Override
    protected ChartsCard getPinCard(ChartArgument<PortCounterSourceName> arg,
            IPerformanceItem<PortCounterSourceName> source) {
        ChartsCard res = super.getPinCard(arg, source);
        // set scale manager
        PortCounterFieldItem pinItem = (PortCounterFieldItem) pinItems.get(arg);
        if (pinItem != null) {
            ChartScaleGroupManager<TimePeriodValuesCollection> scaleMgr =
                    pinItem.getScaleManager();
            if (scaleMgr == null) {
                if (source.getName().equals(allItems[2].getName())) {
                    scaleMgr = packetScaleManager;
                } else if (source.getName().equals(allItems[1].getName())) {
                    scaleMgr = dataScaleManager;
                }
                if (scaleMgr != null) {
                    scaleMgr.addChart(res.getView().getChart(arg.getName()),
                            (TimePeriodValuesCollection) pinItem.getDataset());
                    pinItem.setScaleManager(scaleMgr);
                    scaleMgr.updateChartsRange();
                }
            }
        }
        return res;
    }

    @Override
    protected IChartCreator getChartCreator() {
        return PortCounterChartsCreator.instance();
    }

    public void onFieldNameChange(String label) {

        // Select chart only based on item names. (err, bw, pr).
        // Get the name of the item to select chart because chartCard only
        // knows those three charts based on items.

        if (STLConstants.K0728_RX_CUMULATIVE_PACKETS.getValue().equals(label)
                || STLConstants.K0734_TX_CUMULATIVE_PACKETS.getValue()
                        .equals(label)
                || STLConstants.K0834_RX_MULTICAST_PACKETS.getValue()
                        .equals(label)
                || STLConstants.K0833_TX_MULTICAST_PACKETS.getValue()
                        .equals(label)) {
            packetItem.setFieldName(label);
            // Note: text is name, not short or full name for the item.
            // Following method eventually use the name to select chart in
            // the ChartsView.

            // If it's different field but same item selected, just clear
            // current chart and don't call chartCard#onSelectChart.
            if (!(oldSelectedItem instanceof PCPacketItem)) {
                portCounterChartCard.onSelectChart(packetItem.getName());
            } else {
                packetItem.clear();
            }

            oldSelectedItem = packetItem;
        } else if (STLConstants.K0730_RX_CUMULATIVE_DATA.getValue()
                .equals(label)
                || STLConstants.K0732_TX_CUMULATIVE_DATA.getValue()
                        .equals(label)) {
            dataItem.setFieldName(label);
            if (!(oldSelectedItem instanceof PCDataItem)) {
                portCounterChartCard.onSelectChart(dataItem.getName());
            } else {
                dataItem.clear();
            }

            oldSelectedItem = dataItem;
        } else {
            errItem.setFieldName(label);
            if (!(oldSelectedItem instanceof PCErrItem)) {
                portCounterChartCard.onSelectChart(errItem.getName());
            } else {
                errItem.clear();
            }

            oldSelectedItem = errItem;
        }
        portCounterChartCard.getView().setTitle(label);
        resetViews();
    }

    @Override
    protected ChartArgument<PortCounterSourceName> getChartArgument(
            IPerformanceItem<PortCounterSourceName> item) {
        PortCounterChartArgument arg = new PortCounterChartArgument();
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
        PortCounterSourceName[] sourceNames = item.getSources();
        if (sourceNames != null && sourceNames.length == 1) {
            arg.setSources(item.getSources());
        } else {
            // shouldn't happen
            throw new RuntimeException("Strange performance item with sources: "
                    + Arrays.toString(sourceNames));
        }
        return arg;
    }

    @Override
    protected ChartsView getItemView(
            IPerformanceItem<PortCounterSourceName> item) {
        // Should be same view.
        if (portCounterChartCard != null) {
            return portCounterChartCard.getView();
        }
        return null;
    }

    public ChartsCard getPortCounterChartCard() {
        return portCounterChartCard;
    }

}
