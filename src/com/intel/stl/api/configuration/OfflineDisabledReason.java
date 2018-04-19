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

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.115
 * Offline Disabled Reason, indicated as follows: 
 * 
 * #define STL_OFFDIS_REASON_NONE                  0   // Nop/No specified reason
 * #define STL_OFFDIS_REASON_DISCONNECTED          1   // not connected in design
 * #define STL_OFFDIS_REASON_LOCAL_MEDIA_NOT_INSTALLED 2   // Module not installed
 *                                                         // in connector (QSFP,
 *                                                         // SiPh_x16, etc)
 * #define STL_OFFDIS_REASON_NOT_INSTALLED         3   // internal link not
 *                                                     // installed, neighbor FRU
 *                                                     // absent
 * #define STL_OFFDIS_REASON_CHASSIS_CONFIG        4   // Chassis mgmt forced
 *                                                     // offline due to incompat
 *                                                     // or absent neighbor FRU
 * // reserved 5
 * #define STL_OFFDIS_REASON_END_TO_END_NOT_INSTALLED  6  // local module present
 *                                                        // but unable to detect
 *                                                        // end to optical link
 * // reserved 7
 * #define STL_OFFDIS_REASON_POWER_POLICY          8   // enabling port would
 *                                                     // exceed power policy 
 * #define STL_OFFDIS_REASON_LINKSPEED_POLICY      9   // enabled speed unable to
 *                                                     // be met due to persistent
 *                                                     // cause 
 * #define STL_OFFDIS_REASON_LINKWIDTH_POLICY      10  // enabled width unable to
 *                                                     // be met due to persistent
 *                                                     // cause 
 * // reserved 11
 * #define STL_OFFDIS_REASON_SWITCH_MGMT           12  // user disabled via switch
 *                                                     // mangement interface
 *                                                     
 * #define STL_OFFDIS_REASON_SMA_DISABLED          13  // user disabled via SMA
 *                                                     // Set to phys port state
 *                                                     // disabled
 * // reserved 14
 * #define STL_OFFDIS_REASON_TRANSIENT             15  // Transient offline as part
 *                                                     // of sync with neighbor
 *                                                     // phys port state machine
 * </pre>
 */
public enum OfflineDisabledReason {
    NONE((byte) 0),
    DISCONNECTED((byte) 1),
    LOCAL_MEDIA_NOT_INSTALLED((byte) 2),
    NOT_INSTALLED((byte) 3),
    CHASSIS_CONFIG((byte) 4),
    END_TO_END_NOT_INSTALLED((byte) 6),
    POWER_POLICY((byte) 8),
    LINKSPEED_POLICY((byte) 9),
    LINKWIDTH_POLICY((byte) 10),
    SWITCH_MGMT((byte) 12),
    SMA_DISABLED((byte) 13),
    TRANSIENT((byte) 15);

    private final byte code;

    private OfflineDisabledReason(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static OfflineDisabledReason getOfflineDisabledReason(byte code) {
        for (OfflineDisabledReason reason : OfflineDisabledReason.values()) {
            if (reason.code == code) {
                return reason;
            }
        }
        throw new IllegalArgumentException("Unsupported OfflineDisabledReason "
                + StringUtils.byteHexString(code));
    }
}
