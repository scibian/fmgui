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

import javax.swing.Icon;

import com.intel.stl.api.configuration.LinkQuality;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;

public enum LinkQualityViz {
    // Unknown is an error condition
    UNKNOWN(LinkQuality.UNKNOWN, STLConstants.K1609_QUALITY_UNKNOWN.getValue(),
            STLConstants.K1622_QUALITY_UNKNOWN_DESC.getValue(),
            UIImages.LINK_QUALITY_UNKNOWN.getImageIcon()),
    RESERVED(LinkQuality.RESERVED, STLConstants.K1610_QUALITY_EXCELLENT
            .getValue(), STLConstants.K1616_QUALITY_EXCELLENT_DESC.getValue(),
            UIImages.LINK_QUALITY_EXCELLENT.getImageIcon()),
    EXCELLENT(LinkQuality.EXCELLENT, STLConstants.K1610_QUALITY_EXCELLENT
            .getValue(), STLConstants.K1616_QUALITY_EXCELLENT_DESC.getValue(),
            UIImages.LINK_QUALITY_EXCELLENT.getImageIcon()),
    VERY_GOOD(LinkQuality.VERY_GOOD, STLConstants.K1611_QUALITY_VERY_GOOD
            .getValue(), STLConstants.K1617_QUALITY_VERY_GOOD_DESC.getValue(),
            UIImages.LINK_QUALITY_VERY_GOOD.getImageIcon()),
    GOOD(LinkQuality.GOOD, STLConstants.K1612_QUALITY_GOOD.getValue(),
            STLConstants.K1618_QUALITY_GOOD_DESC.getValue(),
            UIImages.LINK_QUALITY_GOOD.getImageIcon()),
    POOR(LinkQuality.POOR, STLConstants.K1613_QUALITY_POOR.getValue(),
            STLConstants.K1619_QUALITY_POOR_DESC.getValue(),
            UIImages.LINK_QUALITY_POOR.getImageIcon()),
    BAD(LinkQuality.BAD, STLConstants.K1614_QUALITY_BAD.getValue(),
            STLConstants.K1620_QUALITY_BAD_DESC.getValue(),
            UIImages.LINK_QUALITY_BAD.getImageIcon()),
    NONE(LinkQuality.NONE, STLConstants.K1615_QUALITY_NONE.getValue(),
            STLConstants.K1621_QUALITY_NONE_DESC.getValue(),
            UIImages.LINK_QUALITY_NONE.getImageIcon());

    private final static EnumMap<LinkQuality, LinkQualityViz> linkQualityMap =
            new EnumMap<LinkQuality, LinkQualityViz>(LinkQuality.class);
    static {
        for (LinkQualityViz lqz : LinkQualityViz.values()) {
            linkQualityMap.put(lqz.linkQuality, lqz);
        }
    };

    private final LinkQuality linkQuality;

    private final String value;

    private final String description;

    private final Icon icon;

    private LinkQualityViz(LinkQuality portLinkMode, String value,
            String description, Icon icon) {
        this.linkQuality = portLinkMode;
        this.value = value;
        this.description = description;
        this.icon = icon;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * 
     * @return the icon
     */
    public Icon getIcon() {
        return icon;
    }

    public static String getLinkQualityStr(LinkQuality quality) {
        return getLinkQualityViz(quality).getValue();
    }

    public static String getLinkQualityStr(byte value) {
        LinkQuality quality = LinkQuality.getLinkQuality(value);
        return getLinkQualityViz(quality).getValue();
    }

    public static String getLinkQualityDescription(LinkQuality quality) {
        return getLinkQualityViz(quality).getDescription();
    }

    public static String getLinkQualityDescription(byte value) {
        LinkQuality quality = LinkQuality.getLinkQuality(value);
        return getLinkQualityViz(quality).getDescription();
    }

    public static Icon getLinkQualityIcon(byte value) {
        LinkQuality quality = LinkQuality.getLinkQuality(value);
        return getLinkQualityViz(quality).getIcon();
    }

    public static LinkQualityViz getLinkQualityViz(LinkQuality quality) {
        LinkQualityViz res = linkQualityMap.get(quality);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find LinkQualityViz for " + quality);
        }
    }
}
