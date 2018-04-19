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

import com.intel.stl.api.configuration.OfflineDisabledReason;
import com.intel.stl.ui.common.STLConstants;

public enum OfflineDisabledReasonViz {

    NONE(OfflineDisabledReason.NONE,
            STLConstants.K1750_NO_SPECIFIED_REASON.getValue()),
    DISCONNECTED(OfflineDisabledReason.DISCONNECTED,
            STLConstants.K1751_OFFDIS_DISCONNECTED.getValue()),
    LOCAL_MEDIA_NOT_INSTALLED(OfflineDisabledReason.LOCAL_MEDIA_NOT_INSTALLED,
            STLConstants.K1752_OFFDIS_LOCAL_MEDIA_NOT_INSTALLED.getValue()),
    NOT_INSTALLED(OfflineDisabledReason.NOT_INSTALLED,
            STLConstants.K1753_OFFDIS_NOT_INSTALLED.getValue()),
    CHASSIS_CONFIG(OfflineDisabledReason.CHASSIS_CONFIG,
            STLConstants.K1754_OFFDIS_CHASSIS_CONFIG.getValue()),
    END_TO_END_NOT_INSTALLED(OfflineDisabledReason.END_TO_END_NOT_INSTALLED,
            STLConstants.K1755_OFFDIS_END_TO_END_NOT_INSTALLED.getValue()),
    POWER_POLICY(OfflineDisabledReason.POWER_POLICY,
            STLConstants.K1756_OFFDIS_POWER_POLICY.getValue()),
    LINKSPEED_POLICY(OfflineDisabledReason.LINKSPEED_POLICY,
            STLConstants.K1757_OFFDIS_LINKSPEED_POLICY.getValue()),
    LINKWIDTH_POLICY(OfflineDisabledReason.LINKWIDTH_POLICY,
            STLConstants.K1758_OFFDIS_LINKWIDTH_POLICY.getValue()),
    SWITCH_MGMT(OfflineDisabledReason.SWITCH_MGMT,
            STLConstants.K1759_OFFDIS_SWITCH_MGMT.getValue()),
    SMA_DISABLED(OfflineDisabledReason.SMA_DISABLED,
            STLConstants.K1760_OFFDIS_SMA_DISABLED.getValue()),
    TRANSIENT(OfflineDisabledReason.TRANSIENT,
            STLConstants.K1761_OFFDIS_TRANSIENT.getValue());

    private final OfflineDisabledReason reason;

    private final String value;

    private OfflineDisabledReasonViz(OfflineDisabledReason reason, String value) {
        this.reason = reason;
        this.value = value;
    }

    public OfflineDisabledReason getReason() {
        return reason;
    }

    public String getValue() {
        return value;
    }

    public static String getOfflineDisabledReasonStr(byte code) {
        for (OfflineDisabledReasonViz odrv : OfflineDisabledReasonViz.values()) {
            if (odrv.reason.getCode() == code) {
                return odrv.getValue();
            }
        }
        return null;
    }
}
