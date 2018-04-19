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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.135
 * STL PORT TYPE values imply cable info availabilty and format 
 * #define STL_PORT_TYPE_UNKNOWN       0
 * #define STL_PORT_TYPE_DISCONNECTED      1   // the port is not currently usable - CableInfo not available
 * #define STL_PORT_TYPE_FIXED             2   // A fixed backplane port in a director class switch - All STL ASICS
 * #define STL_PORT_TYPE_VARIABLE          3   // A backplane port in a blade system - possibly mixed configuration
 * #define STL_PORT_TYPE_STANDARD          4   // implies a SFF-8636 defined format for CableInfo (QSFP)
 * #define STL_PORT_TYPE_SI_PHOTONICS      5   // A silicon photonics module - 
 *                                             //implies TBD defined format for CableInfo as defined by Intel SFO group
 * // 6 - 15 Reserved
 * 
 * </pre>
 */
public enum PortType {
    UNKNOWN((byte) 0),
    DISCONNECTED((byte) 1), /*
                             * the port is not currently usable - CableInfo not
                             * available
                             */
    FIXED((byte) 2), /*
                      * A fixed backplane port in a director class switch - All
                      * STL ASICS
                      */
    VARIABLE((byte) 3), /*
                         * A backplane port in a blade system - possibly mixed
                         * configuration
                         */
    STANDARD((byte) 4), /*
                         * implies a SFF-8636 defined format for CableInfo
                         * (QSFP)
                         */
    SI_PHOTONICS((byte) 5); /*
                             * A silicon photonics module - implies TBD defined
                             * format for CableInfo as defined by Intel SFO
                             * group
                             */
    /* 6 - 15 Reserved */

    private final byte value;

    private PortType(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static PortType getPortType(byte value) {
        for (PortType portType : PortType.values()) {
            if (portType.value == value) {
                return portType;
            }
        }
        throw new IllegalArgumentException("Unsupported PortType "
                + StringUtils.byteHexString(value));
    }
}
