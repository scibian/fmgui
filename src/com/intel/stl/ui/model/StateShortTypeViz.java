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

import java.awt.Color;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;

/**
 */
public enum StateShortTypeViz {
    NORESP(STLConstants.K0140_NO_RESP.getValue(), UIConstants.INTEL_RED),
    SKIPPED(STLConstants.K0021_SKIPPED.getValue(), UIConstants.INTEL_ORANGE),
    NORMAL(STLConstants.K0022_NORMAL.getValue(), UIConstants.INTEL_GRAY);

    public final static String[] names =
            new String[StateShortTypeViz.values().length];

    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = StateShortTypeViz.values()[i].name;
        }
    };

    public final static Color[] colors =
            new Color[StateShortTypeViz.values().length];

    static {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = StateShortTypeViz.values()[i].color;
        }
    };

    private final String name;

    private final Color color;

    private StateShortTypeViz(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    public static long[] getDistributionValues(long failed, long skipped,
            long total) {
        return new long[] { failed, skipped, total - failed - skipped };
    }

}
