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

import com.intel.stl.api.subnet.PowerClassType;
import com.intel.stl.ui.common.STLConstants;

public enum PowerClassTypeViz {
    CLASS1(PowerClassType.CLASS1, STLConstants.K1145_CABLE_CLASS1.getValue()),
    CLASS2(PowerClassType.CLASS2, STLConstants.K1146_CABLE_CLASS2.getValue()),
    CLASS3(PowerClassType.CLASS3, STLConstants.K1147_CABLE_CLASS3.getValue()),
    CLASS4(PowerClassType.CLASS4, STLConstants.K1148_CABLE_CLASS4.getValue()),
    CLASS5(PowerClassType.CLASS5, STLConstants.K1149_CABLE_CLASS5.getValue()),
    CLASS6(PowerClassType.CLASS6, STLConstants.K1150_CABLE_CLASS6.getValue()),
    CLASS7(PowerClassType.CLASS7, STLConstants.K1151_CABLE_CLASS7.getValue()),
    UNDEFINED(PowerClassType.UNDEFINED, STLConstants.K1138_CABLE_UNDEFINED
            .getValue());

    private final PowerClassType type;

    private final String name;

    private PowerClassTypeViz(PowerClassType type, String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the type
     */
    public PowerClassType getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public static PowerClassTypeViz getPowerClassTypeVizFor(PowerClassType type)
            throws Exception {
        PowerClassTypeViz[] values = PowerClassTypeViz.values();
        for (int i = 0; i < values.length; i++) {
            if (type == values[i].getType()) {
                return values[i];
            }
        }
        throw new IllegalArgumentException(
                "Undefined PowerClassTypeViz : PowerClassType ='" + type + "'");
    }

}
