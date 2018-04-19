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

import static com.intel.stl.ui.common.STLConstants.K0029_CRITICAL;
import static com.intel.stl.ui.common.STLConstants.K0030_ERROR;
import static com.intel.stl.ui.common.STLConstants.K0031_WARNING;
import static com.intel.stl.ui.common.STLConstants.K0032_INFORMATIONAL;
import static com.intel.stl.ui.common.UIConstants.INTEL_GREEN;
import static com.intel.stl.ui.common.UIConstants.INTEL_ORANGE;
import static com.intel.stl.ui.common.UIConstants.INTEL_RED;
import static com.intel.stl.ui.common.UIConstants.INTEL_YELLOW;

import java.awt.Color;
import java.util.EnumMap;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.UIImages;

/**
 */
public enum NoticeSeverityViz {
    CRITICAL(
            NoticeSeverity.CRITICAL,
            K0029_CRITICAL.getValue(),
            INTEL_RED,
            UIImages.CRITICAL_ICON),
    ERROR(NoticeSeverity.ERROR, 
            K0030_ERROR.getValue(), 
            INTEL_ORANGE, 
            UIImages.ERROR_ICON),
    WARNING(
            NoticeSeverity.WARNING,
            K0031_WARNING.getValue(),
            INTEL_YELLOW,
            UIImages.WARNING_ICON),
    INFO(NoticeSeverity.INFO, 
            K0032_INFORMATIONAL.getValue(), 
            INTEL_GREEN, 
            UIImages.INFORMATION_ICON);

    public final static String[] names = new String[NoticeSeverityViz.values().length];
    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = NoticeSeverityViz.values()[i].name;
        }
    };

    public final static Color[] colors = new Color[NoticeSeverityViz.values().length];
    static {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = NoticeSeverityViz.values()[i].color;
        }
    };

    private final NoticeSeverity severity;

    private final String name;

    private final Color color;

    private final UIImages icon;

    private NoticeSeverityViz(NoticeSeverity severity, String name,
            Color color, UIImages icon) {
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
            EnumMap<NoticeSeverity, Integer> counts) {
        NoticeSeverityViz[] all = NoticeSeverityViz.values();
        int[] res = new int[all.length];
        for (int i = 0; i < all.length; i++) {
            Integer val = counts.get(all[i].severity);
            res[i] = val == null ? 0 : val;
        }
        return res;
    }

    public static NoticeSeverity getNoticeSeverityFor(String name) {
        NoticeSeverityViz[] all = NoticeSeverityViz.values();
        for (int i = 0; i < all.length; i++) {
            if (all[i].getName().equals(name)) {
                return all[i].getSeverity();
            }
        }
        return null;
    }

    public static NoticeSeverityViz getNoticeSeverityVizFor(NoticeSeverity type) {
        NoticeSeverityViz[] values = NoticeSeverityViz.values();
        for (int i = 0; i < values.length; i++) {
            if (type == values[i].getSeverity()) {
                return values[i];
            }
        }
        return null;
    }
}
