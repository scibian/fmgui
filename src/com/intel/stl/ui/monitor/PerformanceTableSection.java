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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.LinkQuality;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.common.BaseSectionController;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.PerformanceTableModel;
import com.intel.stl.ui.monitor.view.PerformanceXTableView;

import net.engio.mbassy.bus.MBassador;

/**
 * This is the "Table" section controller for the Performance "Node" view which
 * holds the performance table
 */
public class PerformanceTableSection extends
        BaseSectionController<ISectionListener, JSectionView<ISectionListener>> {

    private final static Logger log =
            LoggerFactory.getLogger(PerformanceTableSection.class);

    /**
     * Performance Table Model
     */
    private final PerformanceTableModel tableModel;

    /**
     * Performance Table View
     */
    private final PerformanceXTableView tableView;

    /**
     * Port Data Accumulator
     */
    private PortDataAccumulator portDataAcc;

    /**
     * Map between a port number and its Port Data Accumulator
     */
    private final Map<Short, PortDataAccumulator> portDataAccMap =
            new HashMap<Short, PortDataAccumulator>();

    /**
     * Map between a port number and its latest data timestamp
     */
    private final Map<Short, Long> lastAccessMap = new HashMap<Short, Long>();

    private List<PerformanceTableData> currentDataList =
            new ArrayList<PerformanceTableData>();

    /**
     * Description:
     *
     * @param view
     */
    public PerformanceTableSection(PerformanceTableModel tableModel,
            PerformanceXTableView tableView,
            JSectionView<ISectionListener> tableSectionView,
            MBassador<IAppEvent> eventBus) {
        super(tableSectionView, eventBus);
        this.tableModel = tableModel;
        this.tableView = tableView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseSectionController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getPerfNodePortsTable();
    }

    /**
     *
     * Description: updates the table
     *
     * @param event
     *            - event message
     */
    public void updateTable(PortCountersBean[] beanList,
            final int previewPortIndex) {
        final List<PerformanceTableData> dataList =
                new ArrayList<PerformanceTableData>();
        for (PortCountersBean bean : beanList) {
            PerformanceTableData portData = createPortEntry(bean);
            dataList.add(portData);
        } // for

        currentDataList = dataList;
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                int oldSize = tableModel.getEntrySize();
                tableModel.setEntries(dataList);
                if (oldSize == dataList.size()) {
                    tableModel.fireTableRowsUpdated(0, oldSize - 1);
                } else {
                    log.warn("Table changed from " + oldSize + " rows to "
                            + dataList.size() + " rows!!");
                    tableModel.fireTableDataChanged();
                    if ((previewPortIndex >= 0) && (dataList.size() > 0)) {
                        tableView.setSelectedPort(previewPortIndex);
                    }
                }
            }
        });
    }

    public void updateTable(VFPortCountersBean[] beanList,
            final int previewPortIndex) {
        final List<PerformanceTableData> data =
                new ArrayList<PerformanceTableData>();
        for (VFPortCountersBean bean : beanList) {
            PerformanceTableData portData = createPortEntry(bean);
            data.add(portData);
        } // for
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                int oldSize = tableModel.getEntrySize();
                tableModel.setEntries(data);
                if (oldSize == data.size()) {
                    tableModel.fireTableRowsUpdated(0, oldSize - 1);
                } else {
                    log.warn("Table changed from " + oldSize + " rows to "
                            + data.size() + " rows!!");
                    tableModel.fireTableDataChanged();
                    if ((previewPortIndex >= 0) && (data.size() > 0)) {
                        tableView.setSelectedPort(previewPortIndex);
                    }
                }
            }
        });
    }

    /**
     *
     * Description: Creates an data entry object for the performance table
     *
     * @param bean
     *            - port counters bean associated with a given port
     *
     * @return performance table data
     */
    public PerformanceTableData createPortEntry(PortCountersBean bean) {
        long rxDataRate = 0;
        long txDataRate = 0;
        PerformanceTableData portData = null;

        if (bean != null) {
            portData = new PerformanceTableData(bean.getNodeLid());
        } else {
            log.error(STLConstants.K3047_PORT_BEAN_COUNTERS_NULL.getValue());
            portData = new PerformanceTableData(-1);
            portData.setLinkQuality(LinkQuality.UNKNOWN.getValue());
            return portData;
        }

        // If there is no entry in the accumulator map, create a new entry
        portDataAcc = portDataAccMap.get(bean.getPortNumber());
        if (portDataAcc == null) {
            portDataAcc = new PortDataAccumulator();
            portDataAccMap.put(bean.getPortNumber(), portDataAcc);
        }

        // If there is no entry in the first access map, create one
        if (lastAccessMap.get(bean.getPortNumber()) == null) {
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // If this bean has been unexpected cleared, reset this ports first
        // access status
        if (bean.hasUnexpectedClear()) {
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // Initialize port values
        portData.setPortNumber(bean.getPortNumber());
        portData.setPortRxRemotePhysicalErrors(
                bean.getPortRcvRemotePhysicalErrors());
        portData.setPortRxDataRate(
                createTableDataDescription(rxDataRate, true));
        portData.setPortTxDataRate(
                createTableDataDescription(txDataRate, true));
        portData.setPortRxSwitchRelayErrors(bean.getPortRcvSwitchRelayErrors());
        portData.setPortTxDiscards(bean.getPortXmitDiscards());
        portData.setExcessiveBufferOverruns(bean.getExcessiveBufferOverruns());
        portData.setFmConfigErrors(bean.getFmConfigErrors());
        portData.setLinkQuality(bean.getLinkQualityIndicator());

        portData.setPortMulticastRcvPkts(bean.getPortMulticastRcvPkts());
        portData.setPortRcvErrors(bean.getPortRcvErrors());
        portData.setPortRcvConstraintErrors(bean.getPortRcvConstraintErrors());
        portData.setPortRcvFECN(bean.getPortRcvFECN());
        portData.setPortRcvBECN(bean.getPortRcvBECN());
        portData.setPortRcvBubble(bean.getPortRcvBubble());

        portData.setPortMulticastXmitPkts(bean.getPortMulticastXmitPkts());
        portData.setPortXmitConstraintErrors(
                bean.getPortXmitConstraintErrors());
        portData.setPortXmitWait(bean.getPortXmitWait());
        portData.setPortXmitTimeCong(bean.getPortXmitTimeCong());
        portData.setPortXmitWastedBW(bean.getPortXmitWastedBW());
        portData.setPortXmitWaitData(bean.getPortXmitWaitData());

        portData.setLocalLinkIntegrityErrors(
                bean.getLocalLinkIntegrityErrors());

        portData.setPortMarkFECN(bean.getPortMarkFECN());
        portData.setLinkErrorRecovery(bean.getLinkErrorRecovery());
        portData.setLinkDowned(bean.getLinkDowned());
        portData.setNumLanesDown(bean.getNumLanesDown());
        portData.setUncorrectableErrors(bean.getUncorrectableErrors());
        portData.setSwPortCongestion(bean.getSwPortCongestion());

        if (bean.isDelta()) {
            // it will be complicate to handle delta style data. To get correct
            // cumulative data, we need the initial data when we clear counters.
            // without a database that keeps tracking data from the very
            // beginning, this is almost impossible. Plus we also need
            // to clear it when a user click clear counter button. So it's
            // better to let FM to handle it and we force ourselves to use
            // no delta style port counter data
            throw new IllegalArgumentException(
                    "We do not support delta style PortCounters");
        } else {
            long rxPackets = bean.getPortRcvPkts();
            long rxData = bean.getPortRcvData();
            long txPackets = bean.getPortXmitPkts();
            long txData = bean.getPortXmitData();

            // Clean calculation for each port entry.
            // Calculate the delta packets and data
            if (bean.getTimestamp() > lastAccessMap.get(bean.getPortNumber())) {
                long deltaTime = bean.getTimestamp()
                        - lastAccessMap.get(bean.getPortNumber());
                portData.setPortRxPktsRate(
                        (rxPackets - portDataAcc.getRxCumulativePacket())
                                / deltaTime);
                portData.setPortRxDataRate(createTableDataDescription(
                        (rxData - portDataAcc.getRxCumulativeData())
                                / deltaTime,
                        true));
                portData.setPortTxPktsRate(
                        (txPackets - portDataAcc.getTxCumulativePacket())
                                / deltaTime);
                portData.setPortTxDataRate(createTableDataDescription(
                        (txData - portDataAcc.getTxCumulativeData())
                                / deltaTime,
                        true));
            }

            // Store the cumulative packets and data
            portData.setPortRxCumulativePkts(rxPackets);
            portData.setPortRxCumulativeData(
                    createTableDataDescription(rxData, false));
            portData.setPortTxCumulativePkts(txPackets);
            portData.setPortTxCumulativeData(
                    createTableDataDescription(txData, false));

            // Collect the most recent cumulative values
            portDataAcc.setRxCumulativePacket(rxPackets);
            portDataAcc.setRxCumulativeData(rxData);
            portDataAcc.setTxCumulativePacket(txPackets);
            portDataAcc.setTxCumulativeData(txData);
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // Update the map with the latest accumulators
        portDataAccMap.put(bean.getPortNumber(), portDataAcc);

        return portData;
    } // updatePerformanceTable

    public PerformanceTableData createPortEntry(VFPortCountersBean bean) {
        long rxDataRate = 0;
        long txDataRate = 0;
        PerformanceTableData portData = null;

        if (bean != null) {
            portData = new PerformanceTableData(bean.getNodeLid());
        } else {
            log.error(STLConstants.K3047_PORT_BEAN_COUNTERS_NULL.getValue());
            portData = new PerformanceTableData(-1);
            portData.setLinkQuality(LinkQuality.UNKNOWN.getValue());
            return portData;
        }

        // If there is no entry in the accumulator map, create a new entry
        portDataAcc = portDataAccMap.get(bean.getPortNumber());
        if (portDataAcc == null) {
            portDataAcc = new PortDataAccumulator();
            portDataAccMap.put(bean.getPortNumber(), portDataAcc);
        }

        // If there is no entry in the first access map, create one
        if (lastAccessMap.get(bean.getPortNumber()) == null) {
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // If this bean has been unexpected cleared, reset this ports first
        // access status
        if (bean.hasUnexpectedClear()) {
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // Initialize port values
        portData.setPortNumber(bean.getPortNumber());
        portData.setPortRxRemotePhysicalErrors(-1);
        portData.setPortRxDataRate(
                createTableDataDescription(rxDataRate, true));
        portData.setPortTxDataRate(
                createTableDataDescription(txDataRate, true));
        portData.setPortRxSwitchRelayErrors(-1);
        portData.setPortTxDiscards(bean.getPortVFXmitDiscards());
        portData.setExcessiveBufferOverruns(-1);
        portData.setFmConfigErrors(-1);

        portData.setPortMulticastRcvPkts(-1);
        portData.setPortRcvErrors(-1);
        portData.setPortRcvConstraintErrors(-1);
        portData.setPortRcvFECN(bean.getPortVFRcvFECN());
        portData.setPortRcvBECN(bean.getPortVFRcvBECN());
        portData.setPortRcvBubble(bean.getPortVFRcvBubble());

        portData.setPortMulticastXmitPkts(-1);
        portData.setPortXmitConstraintErrors(-1);
        portData.setPortXmitWait(bean.getPortVFXmitWait());
        portData.setPortXmitTimeCong(bean.getPortVFXmitTimeCong());
        portData.setPortXmitWastedBW(bean.getPortVFXmitWastedBW());
        portData.setPortXmitWaitData(bean.getPortVFXmitWaitData());

        portData.setLocalLinkIntegrityErrors(-1);

        portData.setPortMarkFECN(bean.getPortVFMarkFECN());
        portData.setLinkErrorRecovery(-1);
        portData.setLinkDowned(-1);
        portData.setNumLanesDown((byte) -1);
        portData.setUncorrectableErrors((short) -1);
        portData.setSwPortCongestion(bean.getSwPortVFCongestion());

        if (bean.isDelta()) {
            // it will be complicate to handle delta style data. To get correct
            // cumulative data, we need the initial data when we clear counters.
            // without a database that keeps tracking data from the very
            // beginning, this is almost impossible. Plus we also need
            // to clear it when a user click clear counter button. So it's
            // better to let FM to handle it and we force ourselves to use
            // no delta style port counter data
            throw new IllegalArgumentException(
                    "We do not support delta style PortCounters");
        } else {
            long rxPackets = bean.getPortVFRcvPkts();
            long rxData = bean.getPortVFRcvData();
            long txPackets = bean.getPortVFXmitPkts();
            long txData = bean.getPortVFXmitData();

            // Clean calculation for each port entry.
            // Calculate the delta packets and data
            if (bean.getTimestamp() > lastAccessMap.get(bean.getPortNumber())) {
                long deltaTime = bean.getTimestamp()
                        - lastAccessMap.get(bean.getPortNumber());
                portData.setPortRxPktsRate(
                        (rxPackets - portDataAcc.getRxCumulativePacket())
                                / deltaTime);
                portData.setPortRxDataRate(createTableDataDescription(
                        (rxData - portDataAcc.getRxCumulativeData())
                                / deltaTime,
                        true));
                portData.setPortTxPktsRate(
                        (txPackets - portDataAcc.getTxCumulativePacket())
                                / deltaTime);
                portData.setPortTxDataRate(createTableDataDescription(
                        (txData - portDataAcc.getTxCumulativeData())
                                / deltaTime,
                        true));
            }

            // Store the cumulative packets and data
            portData.setPortRxCumulativePkts(rxPackets);
            portData.setPortRxCumulativeData(
                    createTableDataDescription(rxData, false));
            portData.setPortTxCumulativePkts(txPackets);
            portData.setPortTxCumulativeData(
                    createTableDataDescription(txData, false));

            // Collect the most recent cumulative values
            portDataAcc.setRxCumulativePacket(rxPackets);
            portDataAcc.setRxCumulativeData(rxData);
            portDataAcc.setTxCumulativePacket(txPackets);
            portDataAcc.setTxCumulativeData(txData);
            lastAccessMap.put(bean.getPortNumber(), bean.getTimestamp());
        }

        // Update the map with the latest accumulators
        portDataAccMap.put(bean.getPortNumber(), portDataAcc);

        return portData;
    } // updatePerformanceTable

    /**
     *
     * <i>Description:</i>Data rate converted from flits to bytes.
     *
     * @param data
     * @return
     */
    private TableDataDescription createTableDataDescription(double data,
            boolean isRate) {
        double dataBytes = data * UIConstants.BYTE_PER_FLIT;

        String dataFlits = null;
        if (isRate) {
            dataFlits = Double.toString(data) + " "
                    + STLConstants.K3222_FPS.getValue();
        } else {
            dataFlits = Long.toString((long) data) + " "
                    + STLConstants.K0748_FLITS.getValue();
        }
        return new TableDataDescription(dataBytes, dataFlits);
    }

    public List<PerformanceTableData> getCurrentDataList() {
        return currentDataList;
    }

    /**
     * Description: clear the table
     */
    @Override
    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                portDataAccMap.clear();
                lastAccessMap.clear();
                // needn't clear tableModel since we will replace it
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.ISectionController#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return null;
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

    /**
     * @return the tableView
     */
    public PerformanceXTableView getTableView() {
        return tableView;
    }

}
