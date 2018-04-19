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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.126
 * Link Init Reason, indicated as follows: 
 * #define STL_LINKINIT_REASON_NOP          0  // None on Get/no change on Set
 * #define STL_LINKINIT_REASON_LINKUP       1  // link just came up 
 * //values from 2-7 will not be altered by transistions from Polling to Linkup/Init 
 * //these can represent persistent reasons why the SM is ignoring a link 
 * #define STL_LINKINIT_REASON_FLAPPING     2  // FM ignoring flapping port 
 * //reserved 3-7
 * //values from 8-15 will be altered by transistions from Polling to LinkUp/Init 
 * //these can represent transient reasons why the SM is ignoring a link 
 * #define STL_LINKINIT_OUTSIDE_POLICY      8  // FM ignoring, width or speed outside FM configured policy
 * #define STL_LINKINIT_QUARANTINED         9  // FM ignoring, quarantined for security
 * #define STL_LINKINIT_INSUFIC_CAPABILITY  10 // FM ignoring, link has insufficient capabilities
 *                                             // for FM configuration (eg. MTU too small etc)
 * //reserved 11-15
 * 
 * </pre>
 */
public enum LinkInitReason {
    NONE((byte) 0),
    LINKUP((byte) 1),
    FLAPPING((byte) 2),
    OUTSIDE_POLICY((byte) 8),
    QUARANTINED((byte) 9),
    INSUFIC_CAPABILITY((byte) 10);

    private static final Map<Byte, LinkInitReason> linkInitReasonMap =
            new HashMap<Byte, LinkInitReason>() {
                private static final long serialVersionUID = 1L;
                {
                    for (LinkInitReason ldr : LinkInitReason.values()) {
                        put(ldr.code, ldr);
                    }
                }
            };

    private final byte code;

    private LinkInitReason(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }

    public static LinkInitReason getLinkInitReason(byte code) {
        LinkInitReason res = linkInitReasonMap.get(code);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Unsupported LinkInitReason "
                    + StringUtils.byteHexString(code));
        }
    }
}
