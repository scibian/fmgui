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

import static com.intel.stl.ui.common.STLConstants.K0016_UNKNOWN;
import static com.intel.stl.ui.common.STLConstants.K0056_NONE;
import static com.intel.stl.ui.common.STLConstants.K0460_FM_BOUNCE;
import static com.intel.stl.ui.common.STLConstants.K0461_RECEIVE_ERROR;
import static com.intel.stl.ui.common.STLConstants.K0462_BAD_PACKET_LENGTH;
import static com.intel.stl.ui.common.STLConstants.K0463_PACKET_TOO_LONG;
import static com.intel.stl.ui.common.STLConstants.K0464_PACKET_TOO_SHORT;
import static com.intel.stl.ui.common.STLConstants.K0465_BAD_SOURCE_LID;
import static com.intel.stl.ui.common.STLConstants.K0466_BAD_DESTINATION_LID;
import static com.intel.stl.ui.common.STLConstants.K0469_BAD_MID_TAIL;
import static com.intel.stl.ui.common.STLConstants.K0470_BAD_VL_MARKER;
import static com.intel.stl.ui.common.STLConstants.K0471_BAD_HEAD_DIST;
import static com.intel.stl.ui.common.STLConstants.K0472_BAD_TAIL_DIST;
import static com.intel.stl.ui.common.STLConstants.K0473_BAD_CTRL_DIST;
import static com.intel.stl.ui.common.STLConstants.K0474_BAD_CREDIT_ACK;
import static com.intel.stl.ui.common.STLConstants.K0475_BAD_PREEMPT;
import static com.intel.stl.ui.common.STLConstants.K0476_BAD_CTRL_FLIT;
import static com.intel.stl.ui.common.STLConstants.K0477_PREMPT_ERROR;
import static com.intel.stl.ui.common.STLConstants.K0478_PREMPT_VL15;
import static com.intel.stl.ui.common.STLConstants.K0479_UNSUPPORTED_VL_MARKER;
import static com.intel.stl.ui.common.STLConstants.K0480_EXCEEDED_MULTICAST_LIMIT;
import static com.intel.stl.ui.common.STLConstants.K0481_EXCESSIVE_BUFFER_OVERRUN;
import static com.intel.stl.ui.common.STLConstants.K0483_NEIGHBOR_UNKNOWN;
import static com.intel.stl.ui.common.STLConstants.K1340_LINKDOWN_SPEED_POLICY;
import static com.intel.stl.ui.common.STLConstants.K1341_LINKDOWN_WIDTH_POLICY;
import static com.intel.stl.ui.common.STLConstants.K1349_LINKDOWN_DISCONNECTED;
import static com.intel.stl.ui.common.STLConstants.K1350_LINKDOWN_LOCAL_MEDIA_NOT_INSTALLED;
import static com.intel.stl.ui.common.STLConstants.K1351_LINKDOWN_NOT_INSTALLED;
import static com.intel.stl.ui.common.STLConstants.K1352_LINKDOWN_CHASSIS_CONFIG;
import static com.intel.stl.ui.common.STLConstants.K1354_LINKDOWN_END_TO_END_NOT_INSTALLED;
import static com.intel.stl.ui.common.STLConstants.K1356_LINKDOWN_POWER_POLICY;
import static com.intel.stl.ui.common.STLConstants.K1357_LINKDOWN_LINKSPEED_POLICY;
import static com.intel.stl.ui.common.STLConstants.K1358_LINKDOWN_LINKWIDTH_POLICY;
import static com.intel.stl.ui.common.STLConstants.K1360_LINKDOWN_SWITCH_MGMT;
import static com.intel.stl.ui.common.STLConstants.K1361_LINKDOWN_SMA_DISABLED;
import static com.intel.stl.ui.common.STLConstants.K1363_LINKDOWN_TRANSIENT;

import java.util.HashMap;
import java.util.Map;

import com.intel.stl.api.configuration.LinkDownReason;
import com.intel.stl.ui.common.STLConstants;

public enum LinkDownReasonViz {

    NONE(LinkDownReason.NONE, K0056_NONE.getValue()),
    RCV_ERROR_0(LinkDownReason.RCV_ERROR_0, K0461_RECEIVE_ERROR.getValue()
            + " 0"),
    BAD_PKT_LEN(LinkDownReason.BAD_PKT_LEN, K0462_BAD_PACKET_LENGTH.getValue()),
    PKT_TOO_LONG(LinkDownReason.PKT_TOO_LONG, K0463_PACKET_TOO_LONG.getValue()),
    PKT_TOO_SHORT(LinkDownReason.PKT_TOO_SHORT, K0464_PACKET_TOO_SHORT
            .getValue()),
    BAD_SLID(LinkDownReason.BAD_SLID, K0465_BAD_SOURCE_LID.getValue()),
    BAD_DLID(LinkDownReason.BAD_DLID, K0466_BAD_DESTINATION_LID.getValue()),
    BAD_L2(LinkDownReason.BAD_L2, STLConstants.K0467_BAD_L2.getValue()),
    BAD_SC(LinkDownReason.BAD_SC, STLConstants.K0468_BAD_SC.getValue()),
    RCV_ERROR_8(LinkDownReason.RCV_ERROR_8, K0461_RECEIVE_ERROR.getValue()
            + " 8"),
    BAD_MID_TAIL(LinkDownReason.BAD_MID_TAIL, K0469_BAD_MID_TAIL.getValue()),
    RCV_ERROR_10(LinkDownReason.RCV_ERROR_10, K0461_RECEIVE_ERROR.getValue()
            + " 10"),
    PREEMPT_ERROR(LinkDownReason.PREEMPT_ERROR, K0477_PREMPT_ERROR.getValue()),
    PREEMPT_VL15(LinkDownReason.PREEMPT_VL15, K0478_PREMPT_VL15.getValue()),
    BAD_VL_MARKER(LinkDownReason.BAD_VL_MARKER, K0470_BAD_VL_MARKER.getValue()),
    RCV_ERROR_14(LinkDownReason.RCV_ERROR_14, K0461_RECEIVE_ERROR.getValue()
            + " 14"),
    RCV_ERROR_15(LinkDownReason.RCV_ERROR_15, K0461_RECEIVE_ERROR.getValue()
            + " 15"),
    BAD_HEAD_DIST(LinkDownReason.BAD_HEAD_DIST, K0471_BAD_HEAD_DIST.getValue()),
    BAD_TAIL_DIST(LinkDownReason.BAD_TAIL_DIST, K0472_BAD_TAIL_DIST.getValue()),
    BAD_CTRL_DIST(LinkDownReason.BAD_CTRL_DIST, K0473_BAD_CTRL_DIST.getValue()),
    BAD_CREDIT_ACK(LinkDownReason.BAD_CREDIT_ACK, K0474_BAD_CREDIT_ACK
            .getValue()),
    UNSUPPORTED_VL_MARKER(LinkDownReason.UNSUPPORTED_VL_MARKER,
            K0479_UNSUPPORTED_VL_MARKER.getValue()),
    BAD_PREEMPT(LinkDownReason.BAD_PREEMPT, K0475_BAD_PREEMPT.getValue()),
    BAD_CONTROL_FLIT(LinkDownReason.BAD_CONTROL_FLIT, K0476_BAD_CTRL_FLIT
            .getValue()),
    EXCEED_MULTICAST_LIMIT(LinkDownReason.EXCEED_MULTICAST_LIMIT,
            K0480_EXCEEDED_MULTICAST_LIMIT.getValue()),
    RCV_ERROR_24(LinkDownReason.RCV_ERROR_24, K0461_RECEIVE_ERROR.getValue()
            + " 24"),
    RCV_ERROR_25(LinkDownReason.RCV_ERROR_25, K0461_RECEIVE_ERROR.getValue()
            + " 25"),
    RCV_ERROR_26(LinkDownReason.RCV_ERROR_26, K0461_RECEIVE_ERROR.getValue()
            + " 26"),
    RCV_ERROR_27(LinkDownReason.RCV_ERROR_27, K0461_RECEIVE_ERROR.getValue()
            + " 27"),
    RCV_ERROR_28(LinkDownReason.RCV_ERROR_28, K0461_RECEIVE_ERROR.getValue()
            + " 28"),
    RCV_ERROR_29(LinkDownReason.RCV_ERROR_29, K0461_RECEIVE_ERROR.getValue()
            + " 29"),
    RCV_ERROR_30(LinkDownReason.RCV_ERROR_30, K0461_RECEIVE_ERROR.getValue()
            + " 30"),
    EXCESSIVE_BUFFER_OVERRUN(LinkDownReason.EXCESSIVE_BUFFER_OVERRUN,
            K0481_EXCESSIVE_BUFFER_OVERRUN.getValue()),
    UNKNOWN(LinkDownReason.UNKNOWN, K0016_UNKNOWN.getValue()),
    REBOOT(LinkDownReason.REBOOT, STLConstants.K0482_REBOOT.getValue()),
    NEIGHBOR_UNKNOWN(LinkDownReason.NEIGHBOR_UNKNOWN, K0483_NEIGHBOR_UNKNOWN
            .getValue()),
    FM_BOUNCE(LinkDownReason.FM_BOUNCE, K0460_FM_BOUNCE.getValue()),
    SPEED_POLICY(LinkDownReason.SPEED_POLICY, K1340_LINKDOWN_SPEED_POLICY
            .getValue()),
    WIDTH_POLICY(LinkDownReason.WIDTH_POLICY, K1341_LINKDOWN_WIDTH_POLICY
            .getValue()),
    DISCONNECTED(LinkDownReason.DISCONNECTED, K1349_LINKDOWN_DISCONNECTED
            .getValue()),
    LOCAL_MEDIA_NOT_INSTALLED(LinkDownReason.LOCAL_MEDIA_NOT_INSTALLED,
            K1350_LINKDOWN_LOCAL_MEDIA_NOT_INSTALLED.getValue()),
    NOT_INSTALLED(LinkDownReason.NOT_INSTALLED, K1351_LINKDOWN_NOT_INSTALLED
            .getValue()),
    CHASSIS_CONFIG(LinkDownReason.CHASSIS_CONFIG, K1352_LINKDOWN_CHASSIS_CONFIG
            .getValue()),
    END_TO_END_NOT_INSTALLED(LinkDownReason.END_TO_END_NOT_INSTALLED,
            K1354_LINKDOWN_END_TO_END_NOT_INSTALLED.getValue()),
    POWER_POLICY(LinkDownReason.POWER_POLICY, K1356_LINKDOWN_POWER_POLICY
            .getValue()),
    LINKSPEED_POLICY(LinkDownReason.LINKSPEED_POLICY,
            K1357_LINKDOWN_LINKSPEED_POLICY.getValue()),
    LINKWIDTH_POLICY(LinkDownReason.LINKWIDTH_POLICY,
            K1358_LINKDOWN_LINKWIDTH_POLICY.getValue()),
    SWITCH_MGMT(LinkDownReason.SWITCH_MGMT, K1360_LINKDOWN_SWITCH_MGMT
            .getValue()),
    SMA_DISABLED(LinkDownReason.SMA_DISABLED, K1361_LINKDOWN_SMA_DISABLED
            .getValue()),
    TRANSIENT(LinkDownReason.TRANSIENT, K1363_LINKDOWN_TRANSIENT.getValue());

    private static final Map<Byte, String> linkDownReasonMap =
            new HashMap<Byte, String>();
    static {
        for (LinkDownReasonViz ldrv : LinkDownReasonViz.values()) {
            linkDownReasonMap.put(ldrv.reason.getCode(), ldrv.value);
        }
    };

    private final LinkDownReason reason;

    private final String value;

    private LinkDownReasonViz(LinkDownReason reason, String value) {
        this.reason = reason;
        this.value = value;
    }

    public LinkDownReason getReason() {
        return reason;
    }

    public String getValue() {
        return value;
    }

    public static String getLinkDownReasonStr(byte reason) {
        return linkDownReasonMap.get(reason);
    }
}
