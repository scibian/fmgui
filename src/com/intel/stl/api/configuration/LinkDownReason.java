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

package com.intel.stl.api.configuration;

import java.util.HashMap;
import java.util.Map;

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.129
 * these correspond to locally initiated link bounce due to PortErrorAction 
 * See Section 9.8.3, Error Counters, Table 9-23 
 * #define STL_LINKDOWN_REASON_NONE                0   // No specified reason 
 * #define STL_LINKDOWN_REASON_RCV_ERROR_0         1
 * #define STL_LINKDOWN_REASON_BAD_PKT_LEN         2
 * #define STL_LINKDOWN_REASON_PKT_TOO_LONG        3
 * #define STL_LINKDOWN_REASON_PKT_TOO_SHORT       4
 * #define STL_LINKDOWN_REASON_BAD_SLID            5
 * #define STL_LINKDOWN_REASON_BAD_DLID            6
 * #define STL_LINKDOWN_REASON_BAD_L2              7
 * #define STL_LINKDOWN_REASON_BAD_SC              8
 * #define STL_LINKDOWN_REASON_RCV_ERROR_8         9
 * #define STL_LINKDOWN_REASON_BAD_MID_TAIL        10
 * #define STL_LINKDOWN_REASON_RCV_ERROR_10        11
 * #define STL_LINKDOWN_REASON_PREEMPT_ERROR       12
 * #define STL_LINKDOWN_REASON_PREEMPT_VL15        13
 * #define STL_LINKDOWN_REASON_BAD_VL_MARKER       14
 * #define STL_LINKDOWN_REASON_RCV_ERROR_14        15
 * #define STL_LINKDOWN_REASON_RCV_ERROR_15        16
 * #define STL_LINKDOWN_REASON_BAD_HEAD_DIST       17
 * #define STL_LINKDOWN_REASON_BAD_TAIL_DIST       18
 * #define STL_LINKDOWN_REASON_BAD_CTRL_DIST       19
 * #define STL_LINKDOWN_REASON_BAD_CREDIT_ACK      20
 * #define STL_LINKDOWN_REASON_UNSUPPORTED_VL_MARKER 21
 * #define STL_LINKDOWN_REASON_BAD_PREEMPT         22
 * #define STL_LINKDOWN_REASON_BAD_CONTROL_FLIT    23
 * #define STL_LINKDOWN_REASON_EXCEED_MULTICAST_LIMIT  24
 * #define STL_LINKDOWN_REASON_RCV_ERROR_24        25
 * #define STL_LINKDOWN_REASON_RCV_ERROR_25        26
 * #define STL_LINKDOWN_REASON_RCV_ERROR_26        27
 * #define STL_LINKDOWN_REASON_RCV_ERROR_27        28
 * #define STL_LINKDOWN_REASON_RCV_ERROR_28        29
 * #define STL_LINKDOWN_REASON_RCV_ERROR_29        30
 * #define STL_LINKDOWN_REASON_RCV_ERROR_30        31
 * #define STL_LINKDOWN_REASON_EXCESSIVE_BUFFER_OVERRUN 32
 * the next two correspond to locally initiated intentional link down 
 * #define STL_LINKDOWN_REASON_UNKNOWN             33
 *             // code 33 is used for locally initiated link downs which don't
 *             // match any of these reason code 
 * // reserved 34
 * #define STL_LINKDOWN_REASON_REBOOT              35 // reboot or device reset 
 * #define STL_LINKDOWN_REASON_NEIGHBOR_UNKNOWN    36
 *             // This indicates the link down was not locally initiated 
 *             // but no LinkGoingDown idle flit was received 
 *             // See Section 6.3.11.1.2, "PlannedDownInform Substate" 
 * // reserved 37 - 38 
 * //These correspond to locally initiated intentional link down 
 * #define STL_LINKDOWN_REASON_FM_BOUNCE           39  // FM initiated bounce 
 *                                                     // by transitioning from linkup to Polling 
 * #define STL_LINKDOWN_REASON_SPEED_POLICY        40 // link outside speed policy 
 * #define STL_LINKDOWN_REASON_WIDTH_POLICY        41 // link downgrade outside 
 *                                                    // LinkWidthDowngrade.Enabled policy 
 * // reserved 42-47 
 * // these correspond to locally initiated link down via transition to Offline or Disabled 
 * // See Section 6.6.2, Offline/Disabled Reasons, Table 6-38
 * // All values in that section are provided for here, although in practice a few 
 * // such as 49 (Disconnected) represent links which can never reach LinkUp and hence 
 * // could not have a transition to LinkDown 
 * // reserved 48 
 * #define STL_LINKDOWN_REASON_DISCONNECTED        49
 * #define STL_LINKDOWN_REASON_LOCAL_MEDIA_NOT_INSTALLED 50
 * #define STL_LINKDOWN_REASON_NOT_INSTALLED       51
 * #define STL_LINKDOWN_REASON_CHASSIS_CONFIG      52
 * // reserved 53 
 * #define STL_LINKDOWN_REASON_END_TO_END_NOT_INSTALLED 54
 * // reserved 55 
 * #define STL_LINKDOWN_REASON_POWER_POLICY        56
 * #define STL_LINKDOWN_REASON_LINKSPEED_POLICY    57
 * #define STL_LINKDOWN_REASON_LINKWIDTH_POLICY    58
 * // reserved 59 
 * #define STL_LINKDOWN_REASON_SWITCH_MGMT         60
 * #define STL_LINKDOWN_REASON_SMA_DISABLED        61
 * // reserved 62 
 * #define STL_LINKDOWN_REASON_TRANSIENT           63
 * // reserved 64-255
 * 
 * </pre>
 */
public enum LinkDownReason {
    NONE((byte) 0),
    RCV_ERROR_0((byte) 1),
    BAD_PKT_LEN((byte) 2),
    PKT_TOO_LONG((byte) 3),
    PKT_TOO_SHORT((byte) 4),
    BAD_SLID((byte) 5),
    BAD_DLID((byte) 6),
    BAD_L2((byte) 7),
    BAD_SC((byte) 8),
    RCV_ERROR_8((byte) 9),
    BAD_MID_TAIL((byte) 10),
    RCV_ERROR_10((byte) 11),
    PREEMPT_ERROR((byte) 12),
    PREEMPT_VL15((byte) 13),
    BAD_VL_MARKER((byte) 14),
    RCV_ERROR_14((byte) 15),
    RCV_ERROR_15((byte) 16),
    BAD_HEAD_DIST((byte) 17),
    BAD_TAIL_DIST((byte) 18),
    BAD_CTRL_DIST((byte) 19),
    BAD_CREDIT_ACK((byte) 20),
    UNSUPPORTED_VL_MARKER((byte) 21),
    BAD_PREEMPT((byte) 22),
    BAD_CONTROL_FLIT((byte) 23),
    EXCEED_MULTICAST_LIMIT((byte) 24),
    RCV_ERROR_24((byte) 25),
    RCV_ERROR_25((byte) 26),
    RCV_ERROR_26((byte) 27),
    RCV_ERROR_27((byte) 28),
    RCV_ERROR_28((byte) 29),
    RCV_ERROR_29((byte) 30),
    RCV_ERROR_30((byte) 31),
    EXCESSIVE_BUFFER_OVERRUN((byte) 32),
    UNKNOWN((byte) 33),
    REBOOT((byte) 35),
    NEIGHBOR_UNKNOWN((byte) 36),
    FM_BOUNCE((byte) 39),
    SPEED_POLICY((byte) 40),
    WIDTH_POLICY((byte) 41),
    DISCONNECTED((byte) 49),
    LOCAL_MEDIA_NOT_INSTALLED((byte) 50),
    NOT_INSTALLED((byte) 51),
    CHASSIS_CONFIG((byte) 52),
    END_TO_END_NOT_INSTALLED((byte) 54),
    POWER_POLICY((byte) 56),
    LINKSPEED_POLICY((byte) 57),
    LINKWIDTH_POLICY((byte) 58),
    SWITCH_MGMT((byte) 60),
    SMA_DISABLED((byte) 61),
    TRANSIENT((byte) 63);

    private static final Map<Byte, LinkDownReason> linkDownReasonMap =
            new HashMap<Byte, LinkDownReason>() {
                private static final long serialVersionUID = 1L;
                {
                    for (LinkDownReason ldr : LinkDownReason.values()) {
                        put(ldr.code, ldr);
                    }
                }
            };

    private final byte code;

    private LinkDownReason(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static LinkDownReason getLinkDownReason(byte code) {
        LinkDownReason res = linkDownReasonMap.get(code);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Unsupported LinkDownReason "
                    + StringUtils.byteHexString(code));
        }
    }
}
