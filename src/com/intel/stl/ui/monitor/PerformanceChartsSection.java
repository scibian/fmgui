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

import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.common.BaseSectionController;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.view.IDataTypeListener;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.monitor.view.PerformanceChartsSectionView;
import com.intel.stl.ui.performance.PortGroupController;
import com.intel.stl.ui.performance.PortSourceName;
import com.intel.stl.ui.performance.item.RxDataRateItem;
import com.intel.stl.ui.performance.item.RxPktRateItem;
import com.intel.stl.ui.performance.item.TxDataRateItem;
import com.intel.stl.ui.performance.item.TxPktRateItem;
import com.intel.stl.ui.performance.observer.CounterRateObserver;
import com.intel.stl.ui.performance.observer.VFCounterRateObserver;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.performance.provider.PortCountersProvider;
import com.intel.stl.ui.performance.provider.VFPortCountersProvider;

import net.engio.mbassy.bus.MBassador;

/**
 * This is the "Charts" section controller for the Performance "Node" view which
 * holds the Tx/Rx packets cards
 */
public class PerformanceChartsSection extends
        BaseSectionController<ISectionListener, PerformanceChartsSectionView> {
    private final PortGroupController groupController;

    private UndoHandler undoHandler;

    private RxPktRateItem rxPkt;

    private RxDataRateItem rxData;

    private TxPktRateItem txPkt;

    private TxDataRateItem txData;

    /**
     * Description:
     *
     * @param view
     * @param eventBus
     */
    public PerformanceChartsSection(PerformanceChartsSectionView view,
            boolean isNode, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);

        createItems();

        groupController = new PortGroupController(eventBus,
                STLConstants.K0200_PERFORMANCE.getValue(), rxPkt, rxData, txPkt,
                txData, HistoryType.values());
        // no need to set origin because we have no group selection

        if (groupController.getRxCard() != null
                && groupController.getTxCard() != null) {
            view.installCardViews(groupController.getRxCard().getView(),
                    groupController.getTxCard().getView());
        }
        groupController.setSleepMode(false);

        HelpAction helpAction = HelpAction.getInstance();
        if (isNode) {
            setHelpID(helpAction.getNodePerf());
            groupController.setHelpIDs(helpAction.getNodeRcvPkts(),
                    helpAction.getNodeTranPkts());
        } else {
            setHelpID(helpAction.getPortPerf());
            groupController.setHelpIDs(helpAction.getPortRcvPkts(),
                    helpAction.getPortTranPkts());
        }
    }

    private void createItems() {
        PortCountersProvider provider = new PortCountersProvider();
        VFPortCountersProvider vfProvider = new VFPortCountersProvider();

        // receive packets
        rxPkt = new RxPktRateItem();

        CounterRateObserver rxPktObserver = new CounterRateObserver(rxPkt) {
            @Override
            protected long getValue(PortCountersBean bean) {
                return bean.getPortRcvPkts();
            }
        };
        rxPkt.registerDataProvider(DataProviderName.PORT, provider,
                rxPktObserver);

        VFCounterRateObserver rxPktVfObserver =
                new VFCounterRateObserver(rxPkt) {
                    @Override
                    protected long getValue(VFPortCountersBean bean) {
                        return bean.getPortVFRcvPkts();
                    }
                };
        rxPkt.registerDataProvider(DataProviderName.VF_PORT, vfProvider,
                rxPktVfObserver);

        // receive data
        rxData = new RxDataRateItem();
        CounterRateObserver rxDataObserver = new CounterRateObserver(rxData) {
            @Override
            protected long getValue(PortCountersBean bean) {
                return bean.getPortRcvData();
            }
        };
        rxData.registerDataProvider(DataProviderName.PORT, provider,
                rxDataObserver);

        VFCounterRateObserver rxDataVfObserver =
                new VFCounterRateObserver(rxData) {
                    @Override
                    protected long getValue(VFPortCountersBean bean) {
                        return bean.getPortVFRcvData();
                    }
                };
        rxData.registerDataProvider(DataProviderName.VF_PORT, vfProvider,
                rxDataVfObserver);

        // transmitted packets
        txPkt = new TxPktRateItem();
        CounterRateObserver txPktObserver = new CounterRateObserver(txPkt) {
            @Override
            protected long getValue(PortCountersBean bean) {
                return bean.getPortXmitPkts();
            }
        };
        txPkt.registerDataProvider(DataProviderName.PORT, provider,
                txPktObserver);

        VFCounterRateObserver txPktVfObserver =
                new VFCounterRateObserver(txPkt) {
                    @Override
                    protected long getValue(VFPortCountersBean bean) {
                        return bean.getPortVFXmitPkts();
                    }
                };
        txPkt.registerDataProvider(DataProviderName.VF_PORT, vfProvider,
                txPktVfObserver);

        // transmitted data
        txData = new TxDataRateItem();
        CounterRateObserver txDataObserver = new CounterRateObserver(txData) {
            @Override
            protected long getValue(PortCountersBean bean) {
                return bean.getPortXmitData();
            }
        };
        txData.registerDataProvider(DataProviderName.PORT, provider,
                txDataObserver);

        VFCounterRateObserver txDataVfObserver =
                new VFCounterRateObserver(txData) {
                    @Override
                    protected long getValue(VFPortCountersBean bean) {
                        return bean.getPortVFXmitData();
                    }
                };
        txData.registerDataProvider(DataProviderName.VF_PORT, vfProvider,
                txDataVfObserver);

    }

    public void setPinID(PinID pinID) {
        groupController.setPinID(pinID);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.ISectionController#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return groupController.getCards().toArray(new ICardController[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseSectionController#getSectionListener()
     */
    @Override
    protected ISectionListener getSectionListener() {
        return this;
    }

    public void setContext(Context context, IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }
        groupController.setContext(context, observer);

        if (context != null && context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }

        view.setHistoryTypeListener(new IDataTypeListener<HistoryType>() {
            @Override
            // Each time different port is selected, each chart will be
            // defaulted to show current. Only when different HistoryScope is
            // selected by user, chart will show different history range.
            public void onDataTypeChange(HistoryType oldType,
                    HistoryType newType) {
                setHistoryType(newType);

                if (undoHandler != null && !undoHandler.isInProgress()) {
                    UndoableSectionHistorySelection sel =
                            new UndoableSectionHistorySelection(
                                    PerformanceChartsSection.this, oldType,
                                    newType);
                    undoHandler.addUndoAction(sel);
                }
            }
        });
        observer.onFinish();
    }

    public void onRefresh(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }
        groupController.onRefresh(observer);
        observer.onFinish();
    }

    public void setSource(PortSourceName source) {
        if (source.getVfName() != null) {
            groupController.setDataProvider(DataProviderName.VF_PORT);
        } else {
            groupController.setDataProvider(DataProviderName.PORT);
        }
        groupController.setDataSources(new PortSourceName[] { source });
    }

    public void setHistoryType(HistoryType type) {
        groupController.setHistoryType(type);
        view.setHistoryType(type);
    }

    public void updateLinkQualityIcon(byte linkQuality) {
        view.setLinkQualityValue(linkQuality);
    }

}
