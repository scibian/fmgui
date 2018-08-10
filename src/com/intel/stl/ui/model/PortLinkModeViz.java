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

import java.util.EnumMap;

import com.intel.stl.api.configuration.PortLinkMode;
import com.intel.stl.ui.common.STLConstants;

public enum PortLinkModeViz {
    NOP(PortLinkMode.NOP, STLConstants.K0118_NOOP.getValue()),
    STL(PortLinkMode.STL, STLConstants.K0120_STL.getValue());

    private final static EnumMap<PortLinkMode, String> portLinkModeMap =
            new EnumMap<PortLinkMode, String>(PortLinkMode.class);

    static {
        for (PortLinkModeViz plmv : PortLinkModeViz.values()) {
            portLinkModeMap.put(plmv.portLinkMode, plmv.value);
        }
    };

    private final PortLinkMode portLinkMode;

    private final String value;

    private PortLinkModeViz(PortLinkMode portLinkMode, String value) {
        this.portLinkMode = portLinkMode;
        this.value = value;
    }

    public static String getPortLinkModeStr(PortLinkMode mode) {
        return portLinkModeMap.get(mode);
    }

    public static String getPortLinkModeStr(byte mode) {
        PortLinkMode[] modes = PortLinkMode.getPortLinkModes(mode);
        String comma = "";
        StringBuffer linkModeStr = new StringBuffer();
        for (int i = 0; i < modes.length; i++) {
            linkModeStr.append(comma);
            linkModeStr.append(portLinkModeMap.get(modes[i]));
            comma = ", ";
        }
        return linkModeStr.toString();
    }
}
