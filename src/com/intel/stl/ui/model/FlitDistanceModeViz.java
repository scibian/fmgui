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

import com.intel.stl.api.configuration.FlitDistanceMode;

public enum FlitDistanceModeViz {
    NOP(FlitDistanceMode.NOP, "NOP"),
    MODE_1(FlitDistanceMode.MODE_1, "MODE 1"),
    MODE_2(FlitDistanceMode.MODE_2, "MODE 2");

    private final static EnumMap<FlitDistanceMode, String> flitDistanceModeMap =
            new EnumMap<FlitDistanceMode, String>(FlitDistanceMode.class);
    static {
        for (FlitDistanceModeViz fdmv : FlitDistanceModeViz.values()) {
            flitDistanceModeMap.put(fdmv.flitDistanceMode, fdmv.value);
        }
    };

    private final FlitDistanceMode flitDistanceMode;

    private final String value;

    private FlitDistanceModeViz(FlitDistanceMode portLinkMode, String value) {
        this.flitDistanceMode = portLinkMode;
        this.value = value;
    }

    public static String getFlitDistanceModeStr(FlitDistanceMode mode) {
        return flitDistanceModeMap.get(mode);
    }

    public static String getFlitDistanceModeStr(byte mode) {
        FlitDistanceMode[] modes = FlitDistanceMode.getFlitDistanceModes(mode);
        String comma = "";
        StringBuffer flitDistanceModeStr = new StringBuffer();
        for (int i = 0; i < modes.length; i++) {
            flitDistanceModeStr.append(comma);
            flitDistanceModeStr.append(flitDistanceModeMap.get(modes[i]));
            comma = ", ";
        }
        return flitDistanceModeStr.toString();
    }
}
