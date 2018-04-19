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

package com.intel.stl.api.configuration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.125
 * STL Link width, continued from IB_LINK_WIDTH and indicated as follows:
 * values are additive for Supported and Enabled fields
 * 
 * #define     STL_LINK_WIDTH_NOP                   0  // LinkWidth.Enabled: no changeon set (nop)
 *                                                     // LinkWidth.Active: link is LinkDown
 *                                                     // LinkWidthDowngrade.Supported: unsupported
 *                                                     // LinkWidthDowngrade.Enable: disabled
 *                                                     // LinkWidthDowngrade.TxActive: link is LinkDown
 *                                                     // LinkWidthDowngrade.RxActive: link is LinkDown
 * #define STL_LINK_WIDTH_1X 0x0001
 * #define STL_LINK_WIDTH_2X 0x0002
 * #define STL_LINK_WIDTH_3X 0x0004
 * #define STL_LINK_WIDTH_4X 0x0008
 * </pre>
 */
public enum LinkWidthMask {
    STL_LINK_WIDTH_NOP((short) 0x0000, -1),
    STL_LINK_WIDTH_1X((short) 0x0001, 1),
    STL_LINK_WIDTH_2X((short) 0x0002, 2),
    STL_LINK_WIDTH_3X((short) 0x0004, 3),
    STL_LINK_WIDTH_4X((short) 0x0008, 4);

    private final short val;

    private final int width;

    private LinkWidthMask(short inval, int width) {
        val = inval;
        this.width = width;
    }

    public short getWidthMask() {
        return val;
    }

    /**
     * @return the width
     */
    public int getWidth() {
        return width;
    }

    public static LinkWidthMask getLinkWidthMask(short inval) {
        for (LinkWidthMask spd : LinkWidthMask.values()) {
            if (spd.getWidthMask() == inval) {
                return spd;
            }
        }
        throw new IllegalArgumentException("Unsupported Link Width "
                + StringUtils.shortHexString(inval));
    }

    public static List<LinkWidthMask> getWidthMasks(short inval) {
        // special case for IB_LINK_SPEED_NOP
        if (inval == STL_LINK_WIDTH_NOP.getWidthMask()) {
            return Collections.singletonList(STL_LINK_WIDTH_NOP);
        }

        List<LinkWidthMask> res = new ArrayList<LinkWidthMask>();
        for (LinkWidthMask mask : LinkWidthMask.values()) {
            if (mask != STL_LINK_WIDTH_NOP
                    && (inval & mask.getWidthMask()) == mask.getWidthMask()) {
                res.add(mask);
            }
        }
        return res;
    }

}
