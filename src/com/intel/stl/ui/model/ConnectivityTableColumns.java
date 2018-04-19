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

import com.intel.stl.ui.common.STLConstants;

public enum ConnectivityTableColumns {

    // Keep these ids in column order!
    NODE_NAME(STLConstants.K0500_NODE_NAME.getValue(),
            STLConstants.K0500_NODE_NAME.getValue()),

    NODE_LID(STLConstants.K0501_NODE_LID.getValue(),
            STLConstants.K0501_NODE_LID.getValue()),

    NODE_TYPE(STLConstants.K0083_NODE_TYPE.getValue(),
            STLConstants.K0083_NODE_TYPE.getValue()),

    NODE_GUID(STLConstants.K0363_NODE_GUID.getValue(),
            STLConstants.K0363_NODE_GUID.getValue()),

    PORT_NUMBER(STLConstants.K0404_PORT_NUMBER.getValue(),
            STLConstants.K0404_PORT_NUMBER.getValue()),

    CABLE_INFO(STLConstants.K3049_CABLE_INFO.getValue(),
            STLConstants.K3049_CABLE_INFO.getValue()),

    LINK_STATE(STLConstants.K0505_LINK_STATE.getValue(),
            STLConstants.K0505_LINK_STATE.getValue()),

    PHYSICAL_LINK_STATE(STLConstants.K0506_PHYSICAL_LINK_STATE.getValue(),
            STLConstants.K0506_PHYSICAL_LINK_STATE.getValue()),

    LINK_QUALITY(STLConstants.K2068_LINK_QUALITY.getValue(),
            STLConstants.K3204_LINK_QUALITY_DESCRIPTION.getValue()),

    ACTIVE_LINK_WIDTH(STLConstants.K0319_PORT_LINK_WIDTH.getValue(),
            STLConstants.K0319_PORT_LINK_WIDTH.getValue()),

    ENABLED_LINK_WIDTH(STLConstants.K0507_LINK_WIDTH_ENABLED.getValue(),
            STLConstants.K0507_LINK_WIDTH_ENABLED.getValue()),

    SUPPORTED_LINK_WIDTH(STLConstants.K0508_LINK_WIDTH_SUPPORTED.getValue(),
            STLConstants.K0508_LINK_WIDTH_SUPPORTED.getValue()),

    ACTIVE_LINK_WIDTH_DG_TX(STLConstants.K0526_TX_LINK_WIDTH_DG.getValue(),
            STLConstants.K0526_TX_LINK_WIDTH_DG.getValue()),

    ACTIVE_LINK_WIDTH_DG_RX(STLConstants.K0527_RX_LINK_WIDTH_DG.getValue(),
            STLConstants.K0527_RX_LINK_WIDTH_DG.getValue()),

    ENABLED_LINK_WIDTH_DG(STLConstants.K0528_LINK_WIDTH_DG_ENABLED.getValue(),
            STLConstants.K0507_LINK_WIDTH_ENABLED.getValue()),

    SUPPORTED_LINK_WIDTH_DG(
            STLConstants.K0529_LINK_WIDTH_DG_SUPPORTED.getValue(),
            STLConstants.K0508_LINK_WIDTH_SUPPORTED.getValue()),

    ACTIVE_LINK_SPEED(STLConstants.K0509_ACTIVE_LINK_SPEED.getValue(),
            STLConstants.K0509_ACTIVE_LINK_SPEED.getValue()),

    ENABLED_LINK_SPEED(STLConstants.K0510_LINK_SPEED_ENABLED.getValue(),
            STLConstants.K0510_LINK_SPEED_ENABLED.getValue()),

    SUPPORTED_LINK_SPEED(STLConstants.K0511_LINK_SPEED_SUPPORTED.getValue(),
            STLConstants.K0511_LINK_SPEED_SUPPORTED.getValue()),

    RX_DATA(STLConstants.K0729_RX_CUMULATIVE_DATA_MB.getValue(),
            STLConstants.K3207_RX_CUMULATIVE_DATA_DESCRIPTION.getValue()),

    RX_PACKETS(STLConstants.K0728_RX_CUMULATIVE_PACKETS.getValue(),
            STLConstants.K3209_RX_CUMULATIVE_PACKETS_DESCRIPTION.getValue()),

    RX_MC_PACKETS(STLConstants.K0834_RX_MULTICAST_PACKETS.getValue(),
            STLConstants.K3216_RX_MULTICAST_PACKETS.getValue()),

    RX_ERRORS(STLConstants.K0519_RX_ERRORS.getValue(),
            STLConstants.K3208_RX_ERRORS_DESCRIPTION.getValue()),

    RX_CONSTRAINT(STLConstants.K0522_RX_PORT_CONSTRAINT.getValue(),
            STLConstants.K3206_RX_PORT_CONSTRAINT_DESCRIPTION.getValue()),

    RX_SWITCH_RELAY_ERRRORS(STLConstants.K0717_REC_SW_REL_ERR.getValue(),
            STLConstants.K3211_REC_SW_REL_ERR_DESCRIPTION.getValue()),

    RX_REMOTE_PHYSICAL_ERRRORS(
            STLConstants.K0520_RX_REMOTE_PHY_ERRORS.getValue(),
            STLConstants.K3210_RX_REMOTE_PHY_ERRORS_DESCRIPTION.getValue()),

    RX_FECN(STLConstants.K0837_RX_FECN.getValue(),
            STLConstants.K3223_RX_FECN_DESCRIPTION.getValue()),

    RX_BECN(STLConstants.K0838_RX_BECN.getValue(),
            STLConstants.K3224_RX_BECN_DESCRIPTION.getValue()),

    RX_BUBBLE(STLConstants.K0842_RX_BUBBLE.getValue(),
            STLConstants.K3225_RX_BUBBLE_DESCRIPTION.getValue()),

    TX_DATA(STLConstants.K0735_TX_CUMULATIVE_DATA_MB.getValue(),
            STLConstants.K3213_TX_CUMULATIVE_DATA_DESCRIPTION.getValue()),

    TX_PACKETS(STLConstants.K0734_TX_CUMULATIVE_PACKETS.getValue(),
            STLConstants.K3215_TX_CUMULATIVE_PACKETS_DESCRIPTION.getValue()),

    TX_MC_PACKETS(STLConstants.K0833_TX_MULTICAST_PACKETS.getValue(),
            STLConstants.K3217_TX_MULTICAST_PACKETS.getValue()),

    TX_DISCARDS(STLConstants.K0731_TX_DISCARDS.getValue(),
            STLConstants.K3214_TX_DISCARDS_DESCRIPTION.getValue()),

    TX_CONSTRAINT(STLConstants.K0521_TX_PORT_CONSTRAINT.getValue(),
            STLConstants.K3212_TX_PORT_CONSTRAINT_DESCRIPTION.getValue()),

    TX_WAIT(STLConstants.K0836_TX_WAIT.getValue(),
            STLConstants.K3226_TX_WAIT_DESCRIPTION.getValue()),

    TX_TIME_CONG(STLConstants.K0839_TX_TIME_CONG.getValue(),
            STLConstants.K3227_TX_TIME_CONG_DESCRIPTION.getValue()),

    TX_WASTED_BW(STLConstants.K0840_TX_WASTED_BW.getValue(),
            STLConstants.K3228_TX_WASTED_BW_DESCRIPTION.getValue()),

    TX_WAIT_DATA(STLConstants.K0841_TX_WAIT_DATA.getValue(),
            STLConstants.K3229_TX_WAIT_DATA_DESCRIPTION.getValue()),

    LOCAL_LINK_INTEGRITY(STLConstants.K0718_LOCAL_LINK_INTEG_ERR.getValue(),
            STLConstants.K3205_LOCAL_LINK_INTEG_ERR_DESCRIPTION.getValue()),

    FM_CONFIG_ERRORS(STLConstants.K0737_FM_CONFIG_ERRRORS.getValue(),
            STLConstants.K3201_FM_CONFIG_ERR_DESCRIPTION.getValue()),

    EXCESSIVE_BUFFER_OVERRUNS(
            STLConstants.K0719_EXCESS_BUFF_OVERRUNS.getValue(),
            STLConstants.K3200_EXCESS_BUFF_OVERRUNS_DESCRIPTION.getValue()),

    SW_PORT_CONGESTION(STLConstants.K0835_SW_PORT_CONG.getValue(),
            STLConstants.K3230_SW_PORT_CONG_DESCRIPTION.getValue()),

    MARK_FECN(STLConstants.K0843_MARK_FECN.getValue(),
            STLConstants.K3231_MARK_FECN_DESCRIPTION.getValue()),

    LINK_ERROR_RECOVERIES(STLConstants.K0517_LINK_RECOVERIES.getValue(),
            STLConstants.K3203_LINK_RECOVERIES_DESCRIPTION.getValue()),

    LINK_DOWNED(STLConstants.K0518_LINK_DOWN.getValue(),
            STLConstants.K3202_LINK_DOWN_DESCRIPTION.getValue()),

    NUM_LANES_DOWN(STLConstants.K1655_NUM_LANES_DOWN.getValue(),
            STLConstants.K3234_NUM_LANES_DOWN_DESCRIPTION.getValue()),

    UNCORRECTABLE_ERRORS(STLConstants.K0716_UNCORR_ERR.getValue(),
            STLConstants.K3232_UNCORR_ERR_DESCRIPTION.getValue());

    private int id = -1;

    private final String title;

    private final String toolTip;

    private ConnectivityTableColumns(String title, String toolTip) {
        this.title = title;
        this.toolTip = toolTip;
    }

    public int getId() {
        if (id == -1) {
            ConnectivityTableColumns[] vals = values();
            for (int i = 0; i < vals.length; i++) {
                if (vals[i] == this) {
                    id = i;
                    break;
                }
            }
        }
        return id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getToolTip() {
        return this.toolTip;
    }

}
