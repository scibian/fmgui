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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.148
 * STL Port LTP CRC mode, indicated as follows:
 * values are additive for Supported and Enabled fields
 * 
 * #define STL_PORT_LTP_CRC_MODE_NONE  0   // No change
 * #define STL_PORT_LTP_CRC_MODE_14    1   // 14-bit LTP CRC mode (optional) 
 * #define STL_PORT_LTP_CRC_MODE_16    2   // 16-bit LTP CRC mode 
 * #define STL_PORT_LTP_CRC_MODE_48    4   // 48-bit LTP CRC mode (optional) 
 * #define STL_PORT_LTP_CRC_MODE_12_16_PER_LANE 8  // 12/16-bit per lane LTP CRC mode
 * 
 * </pre>
 */
public enum PortLtpCrcMode {
    STL_PORT_LTP_CRC_MODE_NONE((byte) 0x00), /* No change */
    STL_PORT_LTP_CRC_MODE_14((byte) 0x01), /* 14-bit LTP CRC mode (optional) */
    STL_PORT_LTP_CRC_MODE_16((byte) 0x02), /* 16-bit LTP CRC mode */
    STL_PORT_LTP_CRC_MODE_48((byte) 0x04), /* 48-bit LTP CRC mode (optional) */
    STL_PORT_LTP_CRC_MODE_12_16_PER_LANE((byte) 0x08); /*
                                                        * 12/16-bit per lane LTP
                                                        * CRC mode
                                                        */
    private final byte mode;

    private PortLtpCrcMode(byte mode) {
        this.mode = mode;
    }

    public byte getMode() {
        return mode;
    }

    public static PortLtpCrcMode getPortLtpCrcMode(byte mode) {
        for (PortLtpCrcMode plcm : PortLtpCrcMode.values()) {
            if (plcm.getMode() == mode) {
                return plcm;
            }
        }
        throw new IllegalArgumentException("Unsupported PortLtpCrcMode "
                + StringUtils.byteHexString(mode));
    }

    public static PortLtpCrcMode[] getPortLtpCrcModes(byte mode) {
        if (isNoneSupported(mode)) {
            return new PortLtpCrcMode[] { STL_PORT_LTP_CRC_MODE_NONE };
        }
        PortLtpCrcMode[] portLtpCrcModes = PortLtpCrcMode.values();
        PortLtpCrcMode[] modes = new PortLtpCrcMode[portLtpCrcModes.length];
        int numModes = 0;
        for (PortLtpCrcMode plcm : portLtpCrcModes) {
            if (plcm != STL_PORT_LTP_CRC_MODE_NONE
                    && ((plcm.mode & mode) == plcm.mode)) {
                modes[numModes] = plcm;
                numModes++;
            }
        }
        return Arrays.copyOf(modes, numModes);
    }

    public static boolean isNoneSupported(byte mode) {
        return mode == STL_PORT_LTP_CRC_MODE_NONE.mode;
    }

}
