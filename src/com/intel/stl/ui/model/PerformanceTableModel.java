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
import com.intel.stl.ui.monitor.PerformanceTableData;

/**
 * Model for the Performance table
 */
public class PerformanceTableModel extends FVTableModel<PerformanceTableData> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6716106545100111380L;

    /**
     *
     * Description: Constructor for the PerformanceTableModel class
     *
     * @return PerformanceTableModel
     *
     */
    public PerformanceTableModel() {

        String[] columnNames =
                new String[PerformanceTableColumns.values().length];
        for (int i = 0; i < PerformanceTableColumns.values().length; i++) {
            columnNames[i] = PerformanceTableColumns.values()[i].getTitle();
        }
        setColumnNames(columnNames);
    } // PerformanceTableModel

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
     * TODO: This would be useful if we decide to set renderers based on object
     * type placed in each cell.
     *
     *
     * public Class getColumnClass(int column){ Class clazz = Object.class;
     *
     * try { clazz = getValueAt(0, column).getClass(); } catch (Exception e){ //
     * nothing to do, proceed. }
     *
     * return clazz; }
     */

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int pRow, int pCol) {

        Object value = null;

        long num = -1;

        short uncorrectableErr = -1;

        PerformanceTableData portData = null;
        synchronized (critical) {
            portData = this.mEntryList.get(pRow);
        }

        try {
            if (portData != null) {
                switch (PerformanceTableColumns.values()[pCol]) {
                    case PORT_NUM:
                        value = portData.getPortNumber();
                        break;

                    case LINK_QUALITY:
                        value = portData.getLinkQualityValue();
                        break;

                    case RX_REMOTE_PHY_ERRORS:
                        num = portData.getPortRxRemotePhysicalErrors();
                        value = num == -1 ? null : num;
                        break;

                    case RX_PKTS_RATE:
                        value = portData.getPortRxPktsRate();
                        break;

                    case RX_DATA_RATE:
                        value = portData.getPortRxDataRate();
                        break;

                    case RX_PACKETS:
                        value = portData.getPortRxCumulativePkts();
                        break;

                    case RX_DATA:
                        value = portData.getPortRxCumulativeData();
                        break;

                    case RX_SWITCH_ERRORS:
                        num = portData.getPortRxSwitchRelayErrors();
                        value = num == -1 ? null : num;
                        break;

                    case TX_DISCARDS:
                        value = portData.getPortTxDiscards();
                        break;

                    case TX_PKTS_RATE:
                        value = portData.getPortTxPktsRate();
                        break;

                    case TX_DATA_RATE:
                        value = portData.getPortTxDataRate();
                        break;

                    case TX_PACKETS:
                        value = portData.getPortTxCumulativePkts();
                        break;

                    case TX_DATA:
                        value = portData.getPortTxCumulativeData();
                        break;

                    case EXCESSIVE_BUFFER_OVERRUNS:
                        num = portData.getExcessiveBufferOverruns();
                        value = num == -1 ? null : num;
                        break;

                    case FM_CONFIG_ERRORS:
                        num = portData.getFmConfigErrors();
                        value = num == -1 ? null : num;
                        break;

                    case RX_MC_PACKETS:
                        num = portData.getPortMulticastRcvPkts();
                        value = num == -1 ? null : num;
                        break;

                    case RX_ERRORS:
                        num = portData.getPortRcvErrors();
                        value = num == -1 ? null : num;
                        break;

                    case RX_CONSTRAINT:
                        num = portData.getPortRcvConstraintErrors();
                        value = num == -1 ? null : num;
                        break;

                    case RX_FECN:
                        value = portData.getPortRcvFECN();
                        break;

                    case RX_BECN:
                        value = portData.getPortRcvBECN();
                        break;

                    case RX_BUBBLE:
                        value = portData.getPortRcvBubble();
                        break;

                    case TX_MC_PACKETS:
                        num = portData.getPortMulticastXmitPkts();
                        value = num == -1 ? null : num;
                        break;

                    case TX_CONSTRAINT:
                        num = portData.getPortXmitConstraintErrors();
                        value = num == -1 ? null : num;
                        break;

                    case TX_WAIT:
                        value = portData.getPortXmitWait();
                        break;

                    case TX_TIME_CONG:
                        value = portData.getPortXmitTimeCong();
                        break;

                    case TX_WASTED_BW:
                        value = portData.getPortXmitWastedBW();
                        break;

                    case TX_WAIT_DATA:
                        value = portData.getPortXmitWaitData();
                        break;

                    case LOCAL_LINK_INTEGRITY:
                        num = portData.getLocalLinkIntegrityErrors();
                        value = num == -1 ? null : num;
                        break;

                    case MARK_FECN:
                        value = portData.getPortMarkFECN();
                        break;

                    case LINK_ERROR_RECOVERIES:
                        num = portData.getLinkErrorRecovery();
                        value = num == -1 ? null : num;
                        break;

                    case LINK_DOWNED:
                        num = portData.getLinkDowned();
                        value = num == -1 ? null : num;
                        break;

                    case NUM_LANES_DOWN:
                        num = portData.getNumLanesDown();
                        value = num == -1 ? null : num;
                        break;

                    case UNCORRECTABLE_ERRORS:
                        num = portData.getUncorrectableErrors();
                        value = num == -1 ? null : num;
                        break;

                    case SW_PORT_CONGESTION:
                        value = portData.getSwPortCongestion();
                        break;

                    default:
                        // NOP
                        break;
                } // switch
            }

        } catch (IndexOutOfBoundsException e) {
            e.printStackTrace();
        }

        return value;
    } // getValueAt

    /**
     * Description:
     *
     */
    public void clear() {
        mEntryList.clear();
    }

    public int getEntrySize() {
        return mEntryList.size();
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

} // class PerformanceTableModel
