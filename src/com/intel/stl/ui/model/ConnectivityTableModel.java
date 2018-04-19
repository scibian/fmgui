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

package com.intel.stl.ui.model;

import com.intel.stl.ui.common.FVTableModel;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.monitor.ConnectivityTableData;
import com.intel.stl.ui.monitor.ConnectivityTableData.PerformanceData;

/**
 * Model for the Connectivity Table
 */
public class ConnectivityTableModel
        extends FVTableModel<ConnectivityTableData> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 8267890546744729698L;

    /**
     *
     * Description: Constructor for the ConnectivityTableModel class
     *
     * @param N
     *            /a
     *
     * @return ConnectivityTableModel
     *
     */
    public ConnectivityTableModel() {
        String[] columnNames =
                new String[ConnectivityTableColumns.values().length];
        for (int i = 0; i < ConnectivityTableColumns.values().length; i++) {
            columnNames[i] = ConnectivityTableColumns.values()[i].getTitle();
        }
        setColumnNames(columnNames);
    }

    /**
     * Description: Override getColumnName to set the column headings
     *
     * @param column
     *            - integer indicating the column number
     *
     * @return result - name of the column heading
     *
     */
    @Override
    public String getColumnName(int column) {
        return mColumnNames[column];
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int pRow, int pCol) {

        Object value = STLConstants.K0383_NA.getValue();

        ConnectivityTableData nodeData = null;
        synchronized (critical) {
            nodeData = this.mEntryList.get(pRow);
        }
        if (nodeData == null) {
            return value;
        }
        PerformanceData perfData = nodeData.getPerformanceData();

        try {
            ConnectivityTableColumns col =
                    ConnectivityTableColumns.values()[pCol];
            switch (col) {
                case NODE_NAME:
                    value = nodeData.getNodeName();
                    break;

                case NODE_LID:
                    value = nodeData.getNodeLidValue();
                    break;

                case NODE_TYPE:
                    value = nodeData.getNodeType();
                    break;

                case NODE_GUID:
                    value = nodeData.getNodeGUID();
                    break;

                case PORT_NUMBER:
                    value = nodeData.getPortNumber();
                    break;

                case CABLE_INFO:
                    value = nodeData.getCableInfo();
                    break;

                case LINK_STATE:
                    value = (nodeData.getLinkState() != null)
                            ? nodeData.getLinkState().toString() : "";
                    break;

                case PHYSICAL_LINK_STATE:
                    value = (nodeData.getPhysicalLinkState() != null)
                            ? nodeData.getPhysicalLinkState().toString() : "";
                    break;

                case LINK_QUALITY:
                    value = nodeData.getLinkQualityData();
                    break;

                case ACTIVE_LINK_WIDTH:
                    value = nodeData.getActiveLinkWidth();
                    break;

                case ENABLED_LINK_WIDTH:
                    value = nodeData.getEnabledLinkWidth();
                    break;

                case SUPPORTED_LINK_WIDTH:
                    value = nodeData.getSupportedLinkWidth();
                    break;

                case ACTIVE_LINK_WIDTH_DG_TX:
                    value = nodeData.getActiveLinkWidthDnGrdTx();
                    break;

                case ACTIVE_LINK_WIDTH_DG_RX:
                    value = nodeData.getActiveLinkWidthDnGrdRx();
                    break;

                case ENABLED_LINK_WIDTH_DG:
                    value = nodeData.getEnabledLinkWidthDnGrd();
                    break;

                case SUPPORTED_LINK_WIDTH_DG:
                    value = nodeData.getSupportedLinkWidthDnGrd();
                    break;

                case ACTIVE_LINK_SPEED:
                    value = nodeData.getActiveLinkSpeed();
                    break;

                case ENABLED_LINK_SPEED:
                    value = nodeData.getEnabledLinkSpeed();
                    break;

                case SUPPORTED_LINK_SPEED:
                    value = nodeData.getSupportedLinkSpeed();
                    break;

                default:
                    // NOP
                    if (perfData != null) {
                        value = getPerformanceValueAt(perfData, col);
                    }
                    break;
            } // switch
        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return value;
    } // getValueAt

    protected Object getPerformanceValueAt(PerformanceData perfData,
            ConnectivityTableColumns col) {
        Object value = null;
        switch (col) {
            case TX_PACKETS:
                value = perfData.getTxPackets();
                break;

            case RX_PACKETS:
                value = perfData.getRxPackets();
                break;

            case LINK_ERROR_RECOVERIES:
                value = perfData.getNumLinkRecoveries();
                break;

            case LINK_DOWNED:
                value = perfData.getNumLinkDown();
                break;

            case NUM_LANES_DOWN:
                value = perfData.getNumLanesDown();
                break;

            case RX_ERRORS:
                value = perfData.getRxErrors();
                break;

            case RX_REMOTE_PHYSICAL_ERRRORS:
                value = perfData.getRxRemotePhysicalErrors();
                break;

            case TX_DISCARDS:
                value = perfData.getTxDiscards();
                break;

            case LOCAL_LINK_INTEGRITY:
                value = perfData.getLocalLinkIntegrityErrors();
                break;

            case EXCESSIVE_BUFFER_OVERRUNS:
                value = perfData.getExcessiveBufferOverruns();
                break;

            case RX_SWITCH_RELAY_ERRRORS:
                value = perfData.getSwitchRelayErrors();
                break;

            case TX_CONSTRAINT:
                value = perfData.getTxConstraints();
                break;

            case RX_CONSTRAINT:
                value = perfData.getRxConstraints();
                break;

            case RX_DATA:
                value = perfData.getPortRcvData();
                break;

            case TX_DATA:
                value = perfData.getPortXmitData();
                break;

            case FM_CONFIG_ERRORS:
                value = perfData.getFmConfigErrors();
                break;

            case RX_MC_PACKETS:
                value = perfData.getPortMulticastRcvPkts();
                break;

            case RX_FECN:
                value = perfData.getPortRcvFECN();
                break;

            case RX_BECN:
                value = perfData.getPortRcvBECN();
                break;

            case RX_BUBBLE:
                value = perfData.getPortRcvBubble();
                break;

            case TX_MC_PACKETS:
                value = perfData.getPortMulticastXmitPkts();
                break;

            case TX_WAIT:
                value = perfData.getPortXmitWait();
                break;

            case TX_TIME_CONG:
                value = perfData.getPortXmitTimeCong();
                break;

            case TX_WASTED_BW:
                value = perfData.getPortXmitWastedBW();
                break;

            case TX_WAIT_DATA:
                value = perfData.getPortXmitWaitData();
                break;

            case MARK_FECN:
                value = perfData.getPortMarkFECN();
                break;

            case UNCORRECTABLE_ERRORS:
                value = perfData.getUncorrectableErrors();
                break;

            case SW_PORT_CONGESTION:
                value = perfData.getSwPortCongestion();
                break;

            default:
                break;
        }
        if (value == null) {
            value = STLConstants.K0383_NA.getValue();
        }
        return value;
    }

    /**
     * Description:
     *
     */
    public void clear() {
        mEntryList.clear();
    }

    public boolean isEmpty() {
        return mEntryList.isEmpty();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.FVTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

}
