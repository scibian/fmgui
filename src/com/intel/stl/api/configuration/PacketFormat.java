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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.115
 * STL Port link formats, indicated as follows:
 * values are additive for Supported and Enabled fields
 * 
 * #define STL_PORT_PACKET_FORMAT_NOP  0       // No change 
 * #define STL_PORT_PACKET_FORMAT_8B   1       // Format 8B 
 * #define STL_PORT_PACKET_FORMAT_9B   2       // Format 9B 
 * #define STL_PORT_PACKET_FORMAT_10B  4       // Format 10B
 * #define STL_PORT_PACKET_FORMAT_16B  8       // Format 16B
 * 
 * </pre>
 */
public enum PacketFormat {
    NOP((byte) 0),
    FORMAT_8B((byte) 1),
    FORMAT_9B((byte) 2),
    FORMAT_10B((byte) 4),
    FORMAT_16B((byte) 8);

    private final byte value;

    /**
     * Description:
     * 
     * @param value
     */
    private PacketFormat(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static PacketFormat getPacketFormat(short value) {
        byte bValue = (byte) (value & 0xff);
        for (PacketFormat format : PacketFormat.values()) {
            if (format.value == bValue) {
                return format;
            }
        }
        throw new IllegalArgumentException("Unsupported Packet Format "
                + StringUtils.shortHexString(value));
    }

    public static PacketFormat[] getPacketFormats(short value) {
        byte bValue = (byte) (value & 0xff);
        if (isNoneSupported(bValue)) {
            return new PacketFormat[] { NOP };
        }

        PacketFormat[] allPacketFormats = PacketFormat.values();
        PacketFormat[] formats = new PacketFormat[allPacketFormats.length];
        int numFormats = 0;
        for (PacketFormat pfv : allPacketFormats) {
            if (pfv != NOP && ((pfv.value & bValue) == pfv.value)) {
                formats[numFormats] = pfv;
                numFormats++;
            }
        }
        return Arrays.copyOf(formats, numFormats);
    }

    public static boolean isNoneSupported(byte value) {
        return value == NOP.value;
    }
}
