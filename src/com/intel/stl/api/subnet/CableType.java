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

package com.intel.stl.api.subnet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum CableType {

    AOC_VCSEL_850NM(1, 0x00),
    OT_VCSEL_850NM(2, 0x00),
    AOC_VCSEL_1310NM(1, 0x01),
    OT_VCSEL_1310NM(2, 0x01),
    AOC_VCSEL_1550NM(1, 0x02),
    OT_VCSEL_1550NM(2, 0x02),
    AOC_FP_1310NM(1, 0x03),
    OT_FP_1310NM(2, 0x03),
    AOC_DFB_1310NM(1, 0x04),
    OT_DFB_1310NM(2, 0x04),
    AOC_DFB_1550NM(1, 0x05),
    OT_DFB_1550NM(2, 0x05),
    AOC_EML_1310NM(1, 0x06),
    OT_EML_1310NM(2, 0x06),
    AOC_EML_1550NM(1, 0x07),
    OT_EML_1550NM(2, 0x07),
    OTHER_UNDEFINED(0, 0x08),
    AOC_DFB_1490NM(1, 0x09),
    OT_DFB_1490NM(2, 0x09),
    PASSIVE_COPPER_CABL(0, 0x0A),
    EQUALIZED_PASSIVE_COPPER_CABL(0, 0x0B),
    ACTIVE_COPPER_CABL_TX_RX(0, 0x0C),
    ACTIVE_COPPER_CABL_RX(0, 0x0D),
    ACTIVE_COPPER_CABL_TX(0, 0x0E),
    LINEAR_ACTIVE_COPPER_CABL(0, 0x0F),
    UNDEFINED(0, -1);

    private final int codeXmit;

    private final int connectorId;

    private static Logger log = LoggerFactory.getLogger(CableType.class);

    private CableType(int connectorId, int codeXmit) {
        this.connectorId = connectorId;
        this.codeXmit = codeXmit;
    }

    /**
     * @return the codeXmit
     */
    public int getCodeXmit() {
        return codeXmit;
    }

    /**
     * @return the connectorId
     */
    public int getConnectorId() {
        return connectorId;
    }

    public static CableType getCableType(int connectorId, int codeXmit) {
        for (CableType ct : CableType.values()) {
            if (ct.getCodeXmit() == codeXmit
                    && ct.getConnectorId() == connectorId) {
                return ct;
            }
        }

        log.error("Undefined CableType : connector ID ='" + connectorId + "'"
                + ", code Xmit ='" + codeXmit + "'");
        return UNDEFINED;
    }
}
