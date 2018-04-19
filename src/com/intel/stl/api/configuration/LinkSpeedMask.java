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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.121
 * 
 * STL Link speed, continued from IB_LINK_SPEED and indicated as follows:
 * values are additive for Supported and Enabled fields
 * 
 * #define STL_LINK_SPEED_NOP       0      // no change, valid only for enabled 
 * #define STL_LINK_SPEED_12_5G     0x0001 // 12.5 Gbps 
 * #define STL_LINK_SPEED_25G       0x0002     // 25.78125? Gbps (EDR)
 * 
 * </pre>
 */
public enum LinkSpeedMask {
    STL_LINK_SPEED_NOP((short) 0x0, 0f),
    STL_LINK_SPEED_12_5G((short) 0x0001, 12.5f),
    STL_LINK_SPEED_25G((short) 0x002, 25.78125f); // special case

    private final short val;

    private final float speedInGb;

    private LinkSpeedMask(short inval, float speedInGb) {
        val = inval;
        this.speedInGb = speedInGb;
    }

    public short getSpeedMask() {
        return val;
    }

    public float getSpeedInGb() {
        return speedInGb;
    }

    public static LinkSpeedMask getLinkSpeedMask(short inval) {
        for (LinkSpeedMask spd : LinkSpeedMask.values()) {
            if (spd.getSpeedMask() == inval) {
                return spd;
            }
        }
        throw new IllegalArgumentException("Unsupported Link Speed "
                + StringUtils.shortHexString(inval));
    }

    public static List<LinkSpeedMask> getSpeedMasks(short inval) {
        if (inval == STL_LINK_SPEED_NOP.val) {
            return Collections.singletonList(STL_LINK_SPEED_NOP);
        }

        List<LinkSpeedMask> res = new ArrayList<LinkSpeedMask>();
        for (LinkSpeedMask mask : LinkSpeedMask.values()) {
            if (mask != STL_LINK_SPEED_NOP
                    && (inval & mask.getSpeedMask()) == mask.getSpeedMask()) {
                res.add(mask);
            }
        }
        return res;
    }

}
