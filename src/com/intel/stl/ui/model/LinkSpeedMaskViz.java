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

import com.intel.stl.api.configuration.LinkSpeedMask;
import com.intel.stl.ui.common.STLConstants;

public enum LinkSpeedMaskViz {
    STL_LINK_SPEED_NONE(LinkSpeedMask.STL_LINK_SPEED_NOP,
            STLConstants.K0117_NONE.getValue()),
    STL_LINK_SPEED_12_5G(LinkSpeedMask.STL_LINK_SPEED_12_5G, "12.5 Gbps"),
    STL_LINK_SPEED_25G(LinkSpeedMask.STL_LINK_SPEED_25G, "25 Gbps");

    private final static EnumMap<LinkSpeedMask, String> linkSpeedMap =
            new EnumMap<LinkSpeedMask, String>(LinkSpeedMask.class);
    static {
        for (LinkSpeedMaskViz lsmv : LinkSpeedMaskViz.values()) {
            linkSpeedMap.put(lsmv.linkSpeedMask, lsmv.value);
        }
    };

    private final LinkSpeedMask linkSpeedMask;

    private final String value;

    private LinkSpeedMaskViz(LinkSpeedMask linkSpeedMask, String value) {
        this.linkSpeedMask = linkSpeedMask;
        this.value = value;
    }

    public LinkSpeedMask getLinkSpeedMask() {
        return linkSpeedMask;
    }

    public String getValue() {
        return value;
    }

    public static String getLinkSpeedMaskStr(LinkSpeedMask mask) {
        return linkSpeedMap.get(mask);
    }
}
