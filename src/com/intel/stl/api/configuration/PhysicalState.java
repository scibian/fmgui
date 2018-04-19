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

import java.util.HashMap;
import java.util.Map;

import com.intel.stl.api.StringUtils;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.115
 * 
 * STL_PORT_PHYS_STATE values continue from IB_PORT_PHYS_STATE
 * // reserved 7-8 
 * #define STL_PORT_PHYS_OFFLINE       9       // offline
 * // reserved 10
 * #define STL_PORT_PHYS_TEST          11      // test
 * 
 * </pre>
 */
public enum PhysicalState {
    NO_ST_CHANGE((byte) 0),
    SLEEP((byte) 1),
    POLLING((byte) 2),
    DISABLED((byte) 3),
    PORT_CONFIG_TRAINING((byte) 4),
    LINKUP((byte) 5),
    LINK_ERROR_RECOVERY((byte) 6),
    OFFLINE((byte) 9),
    TEST((byte) 11);

    private static final Map<Byte, PhysicalState> _map =
            new HashMap<Byte, PhysicalState>() {
                private static final long serialVersionUID = 1L;
                {
                    for (PhysicalState type : PhysicalState.values()) {
                        put(type.id, type);
                    }
                }
            };

    private final byte id;

    private PhysicalState(byte id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public byte getId() {
        return id;
    }

    public static PhysicalState getPhysicalState(byte id) {
        PhysicalState res = _map.get(id);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Unsupported PhysicalState "
                    + StringUtils.byteHexString(id));
        }
    }

}
