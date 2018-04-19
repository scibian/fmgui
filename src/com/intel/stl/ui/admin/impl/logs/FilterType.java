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

package com.intel.stl.ui.admin.impl.logs;

import java.awt.Color;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;

public enum FilterType {

    SM((byte) 0, STLConstants.K2142_SM.getValue(), UIConstants.INTEL_PALE_BLUE),
    PM((byte) 1, STLConstants.K2143_PM.getValue(),
            UIConstants.INTEL_MEDIUM_BLUE),
    FE((byte) 2, STLConstants.K2144_FE.getValue(), UIConstants.INTEL_LIGHT_BLUE),
    WARNINGS((byte) 3, STLConstants.K2145_WARN.getValue(),
            UIConstants.INTEL_YELLOW),
    ERRORS((byte) 4, STLConstants.K2146_ERROR.getValue(),
            UIConstants.INTEL_MEDIUM_RED);

    public final static String[] names = new String[FilterType.values().length];
    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = FilterType.values()[i].name;
        }
    };

    private final byte id;

    private final String name;

    private final Color color;

    private FilterType(byte id, String name, Color color) {
        this.id = id;
        this.name = name;
        this.color = color;
    }

    public byte getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static String[] getNames() {
        return names;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    public static FilterType getFilter(String name) {
        int i = 0;
        boolean found = false;
        FilterType[] filters = FilterType.values();
        while (!found && i < filters.length) {
            found = (filters[i].getName().equals(name));
            i = (found) ? i : i + 1;
        }

        return (found) ? filters[i] : null;
    }
}
