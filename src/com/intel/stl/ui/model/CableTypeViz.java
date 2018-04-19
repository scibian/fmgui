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

import com.intel.stl.api.subnet.CableType;
import com.intel.stl.ui.common.STLConstants;

public enum CableTypeViz {
    AOC_VCSEL_850NM(CableType.AOC_VCSEL_850NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1122_CABLE_850NM_VCSEL.getValue()),
    OT_VCSEL_850NM(CableType.OT_VCSEL_850NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1122_CABLE_850NM_VCSEL.getValue()),
    AOC_VCSEL_1310NM(CableType.AOC_VCSEL_1310NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1123_CABLE_1310NM_VCSEL.getValue()),
    OT_VCSEL_1310NM(CableType.OT_VCSEL_1310NM,
            STLConstants.K1121_CABLE_OPT_TRAN.getValue()
                    + STLConstants.K1123_CABLE_1310NM_VCSEL),
    AOC_VCSEL_1550NM(CableType.AOC_VCSEL_1550NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1124_CABLE_1550NM_VCSEL.getValue()),
    OT_VCSEL_1550NM(CableType.OT_VCSEL_1550NM,
            STLConstants.K1121_CABLE_OPT_TRAN.getValue()
                    + STLConstants.K1124_CABLE_1550NM_VCSEL.getValue()),
    AOC_FP_1310NM(CableType.AOC_FP_1310NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1125_CABLE_1310NM_FP.getValue()),
    OT_FP_1310NM(CableType.OT_FP_1310NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1125_CABLE_1310NM_FP.getValue()),
    AOC_DFB_1310NM(CableType.AOC_DFB_1310NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1126_CABLE_1310NM_DFB.getValue()),
    OT_DFB_1310NM(CableType.OT_DFB_1310NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1126_CABLE_1310NM_DFB.getValue()),
    AOC_DFB_1550NM(CableType.AOC_DFB_1550NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1127_CABLE_1550NM_DFB.getValue()),
    OT_DFB_1550NM(CableType.OT_DFB_1550NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1127_CABLE_1550NM_DFB.getValue()),
    AOC_EML_1310NM(CableType.AOC_EML_1310NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1128_CABLE_1310NM_EML.getValue()),
    OT_EML_1310NM(CableType.OT_EML_1310NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1128_CABLE_1310NM_EML.getValue()),
    AOC_EML_1550NM(CableType.AOC_EML_1550NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1129_CABLE_1550NM_EML.getValue()),
    OT_EML_1550NM(CableType.OT_EML_1550NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1129_CABLE_1550NM_EML.getValue()),
    OTHER_UNDEFINED(CableType.OTHER_UNDEFINED,
            STLConstants.K1130_CABLE_OTH_UNDEFINED.getValue()),
    AOC_DFB_1490NM(CableType.AOC_DFB_1490NM, STLConstants.K1120_CABLE_AOC
            .getValue() + STLConstants.K1131_CABLE_1490NM_DFB.getValue()),
    OT_DFB_1490NM(CableType.OT_DFB_1490NM, STLConstants.K1121_CABLE_OPT_TRAN
            .getValue() + STLConstants.K1131_CABLE_1490NM_DFB.getValue()),
    PASSIVE_COPPER_CABL(CableType.PASSIVE_COPPER_CABL,
            STLConstants.K1132_CABLE_PASS_COPPER.getValue()),
    EQUALIZED_PASSIVE_COPPER_CABL(CableType.EQUALIZED_PASSIVE_COPPER_CABL,
            STLConstants.K1133_CABLE_EQU_PASS_COPPER.getValue()),
    ACTIVE_COPPER_CABL_TX_RX(CableType.ACTIVE_COPPER_CABL_TX_RX,
            STLConstants.K1134_CABLE_ACT_COPPER_TX_RX.getValue()),
    ACTIVE_COPPER_CABL_RX(CableType.ACTIVE_COPPER_CABL_RX,
            STLConstants.K1135_CABLE_ACT_COPPER_RX.getValue()),
    ACTIVE_COPPER_CABL_TX(CableType.ACTIVE_COPPER_CABL_TX,
            STLConstants.K1136_CABLE_ACT_COPPER_TX.getValue()),
    LINEAR_ACTIVE_COPPER_CABL(CableType.LINEAR_ACTIVE_COPPER_CABL,
            STLConstants.K1137_CABLE_LINEAR_ACT_COPPER.getValue()),
    UNDEFINED(CableType.UNDEFINED, STLConstants.K1138_CABLE_UNDEFINED
            .getValue());

    private final CableType type;

    private final String name;

    private CableTypeViz(CableType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the type
     */
    public CableType getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public static CableTypeViz getCableTypeVizFor(CableType type)
            throws Exception {
        CableTypeViz[] values = CableTypeViz.values();
        for (int i = 0; i < values.length; i++) {
            if (type == values[i].getType()) {
                return values[i];
            }
        }

        throw new IllegalArgumentException(
                "Undefined CableTypeViz : CableType ='" + type + "'");
    }

}
