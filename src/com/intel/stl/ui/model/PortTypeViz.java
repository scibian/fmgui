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
import static com.intel.stl.ui.common.STLConstants.K0491_DISCONNECTED;
import static com.intel.stl.ui.common.STLConstants.K0492_STANDARD;
import static com.intel.stl.ui.common.STLConstants.K0493_FIXED;
import static com.intel.stl.ui.common.STLConstants.K0494_VARIABLE;
import static com.intel.stl.ui.common.STLConstants.K0495_SILICON_PHOTONICS;

import com.intel.stl.api.configuration.PortType;

public enum PortTypeViz {

    DISCONNECTED(PortType.DISCONNECTED, K0491_DISCONNECTED.getValue()),
    STANDARD(PortType.STANDARD, K0492_STANDARD.getValue()),
    FIXED(PortType.FIXED, K0493_FIXED.getValue()),
    VARIABLE(PortType.VARIABLE, K0494_VARIABLE.getValue()),
    SI_PHOTONICS(PortType.SI_PHOTONICS, K0495_SILICON_PHOTONICS.getValue());

    private final PortType portType;

    private final String value;

    private PortTypeViz(PortType portType, String value) {
        this.portType = portType;
        this.value = value;
    }

    public PortType getPortType() {
        return portType;
    }

    public String getValue() {
        return value;
    }

    public static String getPortTypeStr(byte portType) {
        for (PortTypeViz portTypeViz : PortTypeViz.values()) {
            if (portTypeViz.portType.getValue() == portType) {
                return portTypeViz.value;
            }
        }
        return K0016_UNKNOWN.getValue();
    }
}
