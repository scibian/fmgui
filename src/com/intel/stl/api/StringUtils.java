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
package com.intel.stl.api;

/**
 */
public class StringUtils {

    public static String byteHexString(byte value) {
        return String.format("0x%02x", Byte.valueOf(value));
    }

    public static String shortHexString(short value) {
        return String.format("0x%04x", Short.valueOf(value));
    }

    public static String intHexString(int value) {
        return String.format("0x%08x", Integer.valueOf(value));
    }

    public static String longHexString(long value) {
        return String.format("0x%016x", Long.valueOf(value));
    }

    public static String getErrorMessage(Throwable error) {
        if (error == null) {
            return null;
        }

        while (error.getCause() != null) {
            error = error.getCause();
        }

        String msg = error.getLocalizedMessage();
        if (msg == null || msg.isEmpty()) {
            msg = error.getClass().getSimpleName();
        }
        return msg;
    }

    public static String getIpV4Addr(byte[] ipBytes) {
        if (ipBytes == null) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < ipBytes.length; i++) {
            int value = ipBytes[i] & 0xff;
            if (i == 0) {
                sb.append(value);
            } else {
                sb.append("." + value);
            }
        }
        return sb.toString();
    }

    // See IETF RFC 5952 for the canonical format used here to render an IPv6
    // address
    public static String getIpV6Addr(byte[] ipBytes) {
        if (ipBytes == null) {
            return null;
        }
        // Find max sequence of 16-bit zeros
        int compressSize = 0;
        int compressStart = -1;
        for (int i = 0; i < ipBytes.length; i = i + 2) {
            int numZeroGroups = 0;
            int j = i;
            while (j < ipBytes.length && ipBytes[j] == 0 && ipBytes[j + 1] == 0) {
                j = j + 2;
                numZeroGroups++;
            }
            if (numZeroGroups > compressSize) {
                compressSize = numZeroGroups;
                compressStart = i;
            }
        }
        StringBuffer buff = new StringBuffer(40);
        if (compressSize < 2) {
            bytesToHex(buff, ipBytes, 0, ipBytes.length);
        } else {
            bytesToHex(buff, ipBytes, 0, compressStart);
            buff.append("::");
            bytesToHex(buff, ipBytes, (compressStart + (compressSize * 2)),
                    ipBytes.length);
        }
        return buff.toString();
    }

    private static void bytesToHex(StringBuffer buff, byte[] src, int start,
            int end) {
        String groupSeparator = "";
        for (int i = start; i < end; i = i + 2) {
            buff.append(groupSeparator);
            int group = ((src[i] << 8) & 0xff00) | ((src[i + 1]) & 0xff);
            buff.append(Integer.toHexString(group));
            groupSeparator = ":";
        }
    }
}
