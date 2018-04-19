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

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

/**
 */
public enum NodeTypeViz {
    HFI(NodeType.HFI, STLConstants.K0110_HFI.getValue(),
            STLConstants.K0111_HFIS.getValue(), UIConstants.INTEL_BLUE,
            UIImages.HFI_ICON),
    SWITCH(NodeType.SWITCH, STLConstants.K0017_SWITCH.getValue(),
            STLConstants.K0048_SWITCHES.getValue(), UIConstants.INTEL_GREEN,
            UIImages.SW_ICON),
    ROUTER(NodeType.ROUTER, STLConstants.K0019_ROUTER.getValue(),
            STLConstants.K0050_ROUTERS.getValue(), UIConstants.INTEL_ORANGE,
            UIImages.ROUTER_ICON),
    OTHER(NodeType.OTHER, STLConstants.K0109_OTHERS.getValue(),
            STLConstants.K0109_OTHERS.getValue(), UIConstants.INTEL_GRAY, null);

    public final static String[] names =
            new String[NodeTypeViz.values().length];
    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = NodeTypeViz.values()[i].name;
        }
    };

    public final static Color[] colors = new Color[NodeTypeViz.values().length];
    static {
        for (int i = 0; i < colors.length; i++) {
            colors[i] = NodeTypeViz.values()[i].color;
        }
    };

    private final NodeType type;

    private final String name;

    private final String pluralName;

    private final Color color;

    private final UIImages icon;

    private NodeTypeViz(NodeType type, String name, String pluralName,
            Color color, UIImages icon) {
        this.type = type;
        this.name = name;
        this.pluralName = pluralName;
        this.color = color;
        this.icon = icon;
    }

    /**
     * @return the type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the pluralName
     */
    public String getPluralName() {
        return pluralName;
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

    public static NodeTypeViz getNodeTypeViz(byte id) {
        NodeType type = NodeType.getNodeType(id);
        return getNodeTypeViz(type);
    }

    public static NodeTypeViz getNodeTypeViz(NodeType type) {
        for (NodeTypeViz ntv : NodeTypeViz.values()) {
            if (ntv.type == type) {
                return ntv;
            }
        }
        return null;
    }

    public static long[] getDistributionValues2(
            EnumMap<NodeType, Integer> counts) {
        NodeTypeViz[] all = NodeTypeViz.values();
        long[] res = new long[all.length];
        for (int i = 0; i < res.length; i++) {
            Integer val = counts.get(all[i].type);
            res[i] = val == null ? 0 : val;
        }
        return res;
    }

    public static long[] getDistributionValues(EnumMap<NodeType, Long> counts) {
        NodeTypeViz[] all = NodeTypeViz.values();
        long[] res = new long[all.length];
        for (int i = 0; i < res.length; i++) {
            Long val = counts.get(all[i].type);
            res[i] = val == null ? 0 : val;
        }
        return res;
    }
}
