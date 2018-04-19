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
import java.util.EnumMap;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

/**
 */
public enum StateLongTypeViz {
    CRITICAL(NoticeSeverity.CRITICAL, STLConstants.K0029_CRITICAL.getValue(),
            UIConstants.INTEL_DARK_RED, UIImages.CRITICAL_ICON),
    ERROR(NoticeSeverity.ERROR, STLConstants.K0030_ERROR.getValue(),
            UIConstants.INTEL_DARK_ORANGE, UIImages.ERROR_ICON),
    WARNING(NoticeSeverity.WARNING, STLConstants.K0031_WARNING.getValue(),
            UIConstants.INTEL_YELLOW, UIImages.WARNING_ICON),
    NORMAL(NoticeSeverity.INFO, STLConstants.K0022_NORMAL.getValue(),
            UIConstants.INTEL_GREEN, UIImages.NORMAL_ICON);

    public final static String[] names =
            new String[StateLongTypeViz.values().length];
    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = StateLongTypeViz.values()[i].name;
        }
    };

    public final static Color[] colors =
            new Color[StateLongTypeViz.values().length];
    static {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = StateLongTypeViz.values()[i].color;
        }
    };

    private final NoticeSeverity severity;

    private final String name;

    private final Color color;

    private final UIImages icon;

    private StateLongTypeViz(NoticeSeverity severity, String name, Color color,
            UIImages icon) {
        this.severity = severity;
        this.name = name;
        this.color = color;
        this.icon = icon;
    }

    /**
     * @return the severity
     */
    public NoticeSeverity getSeverity() {
        return severity;
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

    /**
     * @return the icon
     */
    public UIImages getIcon() {
        return icon;
    }

    public static int[] getDistributionValues(
            EnumMap<NoticeSeverity, Integer> counts, int total) {
        if (counts == null) {
            return null;
        }

        StateLongTypeViz[] all = StateLongTypeViz.values();
        int[] res = new int[all.length];
        int sum = 0;
        for (int i = 0; i < all.length; i++) {
            Integer val = counts.get(all[i].severity);
            res[i] = val == null ? 0 : val;
            sum += res[i];
        }
        res[StateLongTypeViz.NORMAL.ordinal()] += total - sum;
        return res;
    }

}
