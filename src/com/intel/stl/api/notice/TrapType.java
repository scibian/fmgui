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

package com.intel.stl.api.notice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h
 * 
 * #define STL_TRAP_GID_NOW_IN_SERVICE     0x40
 * #define STL_TRAP_GID_OUT_OF_SERVICE     0x41
 * #define STL_TRAP_ADD_MULTICAST_GROUP    0x42
 * #define STL_TRAP_DEL_MULTICAST_GROUP    0x43
 * #define STL_TRAP_LINK_PORT_CHANGE_STATE 0x80
 * #define STL_TRAP_LINK_INTEGRITY         0x81
 * #define STL_TRAP_BUFFER_OVERRUN         0x82
 * #define STL_TRAP_FLOW_WATCHDOG          0x83
 * #define STL_TRAP_CHANGE_CAPABILITY      0x90
 * #define STL_TRAP_CHANGE_SYSGUID         0x91
 * #define STL_TRAP_BAD_M_KEY              0x100
 * #define STL_TRAP_BAD_P_KEY              0x101
 * #define STL_TRAP_BAD_Q_KEY              0x102
 * #define STL_TRAP_SWITCH_BAD_PKEY        0x103
 * #define STL_SMA_TRAP_LINK_WIDTH         0x800
 * </pre>
 */
public enum TrapType {
    // traps created by SM
    GID_NOW_IN_SERVICE((short) 0x40),
    GID_OUT_OF_SERVICE((short) 0x41),
    ADD_MULTICAST_GROUP((short) 0x42),
    DEL_MULTICAST_GROUP((short) 0x43),
    LINK_PORT_CHANGE_STATE((short) 0x80),
    LINK_INTEGRITY((short) 0x81),
    BUFFER_OVERRUN((short) 0x82),
    FLOW_WATCHDOG((short) 0x83),
    CHANGE_CAPABILITY((short) 0x90),
    CHANGE_SYSGUID((short) 0x91),
    BAD_M_KEY((short) 0x100),
    BAD_P_KEY((short) 0x101),
    BAD_Q_KEY((short) 0x102),
    SWITCH_BAD_PKEY((short) 0x103),
    SMA_TRAP_LINK_WIDTH((short) 0x800),
    // traps created by FE
    SM_CONNECTION_LOST((short) 0x8003),
    SM_CONNECTION_ESTABLISH((short) 0x8004),
    // traps created by FEC client
    FE_CONNECTION_LOST((short) 0x9001),
    FE_CONNECTION_ESTABLISH((short) 0x9002),
    // Traps created by notifiers
    SMTP_SETTINGS_INVALID((short) 0x9100);

    private static Logger log = LoggerFactory.getLogger(TrapType.class);

    private final short id;

    private TrapType(short id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public short getId() {
        return id;
    }

    public static TrapType getTrapType(short id) {
        for (TrapType type : TrapType.values()) {
            if (type.getId() == id) {
                return type;
            }
        }
        log.warn("Unknown TrapType id " + id);
        return null;
    }
}
