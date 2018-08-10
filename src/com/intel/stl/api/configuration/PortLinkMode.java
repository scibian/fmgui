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

import java.util.Arrays;

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit 8d05ba37b98661fa539132e27cbd3bd15eea81aa
 * date 2017-11-09 11:11:00
 * STL Port link mode, indicated as follows:
 * values are additive for Supported and Enabled fields
 *
 * #define STL_PORT_LINK_MODE_NOP  0       // No change
 * // reserved 1
 * // reserved 2
 * #define STL_PORT_LINK_MODE_STL  4       // Port mode is STL
 *
 * </pre>
 */
public enum PortLinkMode {
    NOP((byte) 0x00),
    STL((byte) 0x04);

    private final byte mode;

    private PortLinkMode(byte mode) {
        this.mode = mode;
    }

    public byte getMode() {
        return mode;
    }

    public static PortLinkMode getPortLinkMode(byte mode) {
        for (PortLinkMode plm : PortLinkMode.values()) {
            if (plm.getMode() == mode) {
                return plm;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported PortLinkMode " + StringUtils.byteHexString(mode));
    }

    public static PortLinkMode[] getPortLinkModes(byte mode) {
        if (isNoneSupported(mode)) {
            return new PortLinkMode[] { NOP };
        }
        PortLinkMode[] portLinkModes = PortLinkMode.values();
        PortLinkMode[] modes = new PortLinkMode[portLinkModes.length];
        int numModes = 0;
        for (PortLinkMode plm : portLinkModes) {
            if (plm != NOP && ((plm.mode & mode) == plm.mode)) {
                modes[numModes] = plm;
                numModes++;
            }
        }
        return Arrays.copyOf(modes, numModes);
    }

    public static boolean isNoneSupported(byte mode) {
        return mode == NOP.mode;
    }

}
