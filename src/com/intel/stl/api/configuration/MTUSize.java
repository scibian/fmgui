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

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_types.h v1.64
 * MTU of neighbor endnode connected to this port:
 * this enum is used for all SM and CM messages
 * it is also used in other datatypes as noted below
 *
 * typedef enum _IB_MTU {
 *     IB_MTU_256      = 1,
 *     IB_MTU_512      = 2,
 *     IB_MTU_1024     = 3,
 *     IB_MTU_2048     = 4,
 *     IB_MTU_4096     = 5,
 *     IB_MTU_MAX      = 5
 *     /* 0, 6 - 15 (or 63 in some packets): reserved
 * } IB_MTU;
 *
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_types.h v1.27
 * STL MTU values continue from IB_MTU
 * #define STL_MTU_0           0
 * #define STL_MTU_8192        6
 * #define STL_MTU_10240       7
 * #define STL_MTU_MAX         STL_MTU_10240
 * </pre>
 */
public enum MTUSize {
    INVALID((byte) 0, "0"),
    // remove the following as they are no longer supported by the SM
    // IB_MTU_256((byte) 1, "256"),
    // IB_MTU_512((byte) 2, "512"),
    // IB_MTU_1024((byte) 3, "1024"),
    IB_MTU_2048((byte) 4, "2048"),
    IB_MTU_4096((byte) 5, "4096"),
    STL_MTU_8192((byte) 6, "8192"),
    STL_MTU_10240((byte) 7, "10240");

    private final byte sz;

    private final String name;

    MTUSize(byte val, String name) {
        sz = val;
        this.name = name;
    }

    public byte getSize() {
        return sz;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public static MTUSize getMTUSize(byte inval) {
        for (MTUSize mtu : MTUSize.values()) {
            if (mtu.getSize() == inval) {
                return mtu;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported MTUSize " + StringUtils.byteHexString(inval));
    }

    public static MTUSize getMTUSize(String name) {
        for (MTUSize mtu : MTUSize.values()) {
            if (mtu.getName().equalsIgnoreCase(name)) {
                return mtu;
            }
        }
        throw new IllegalArgumentException(
                "Unsupported MTUSize '" + name + "'");
    }
}
