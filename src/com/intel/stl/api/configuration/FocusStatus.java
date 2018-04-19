/**
 * Copyright (c) 2016, Intel Corporation
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
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pm.h v1.55
 * #define STL_PA_FOCUS_FLAG_OK           0
 * #define STL_PA_FOCUS_FLAG_PMA_IGNORE   1
 * #define STL_PA_FOCUS_FLAG_PMA_FAILURE  2
 * #define STL_PA_FOCUS_FLAG_TOPO_FAILURE 3
 *
 * </pre>
 */
public enum FocusStatus {
    OK((byte) 0),
    PMA_IGNORE((byte) 1),
    PMA_NORESP((byte) 2),
    TOPO_NORESP((byte) 3);

    private static final Map<Byte, FocusStatus> focusStatusMap =
            new HashMap<Byte, FocusStatus>() {
                private static final long serialVersionUID = 1L;

                {
                    for (FocusStatus ff : FocusStatus.values()) {
                        put(ff.code, ff);
                    }
                }
            };

    private final byte code;

    private FocusStatus(byte code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public byte getCode() {
        return code;
    }

    public static FocusStatus getFocusStatus(byte code) {
        FocusStatus res = focusStatusMap.get(code);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException(
                    "Unsupported FocusFlag " + StringUtils.byteHexString(code));
        }
    }
}
