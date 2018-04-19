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

package com.intel.stl.ui.admin.impl.logs;

import java.util.HashMap;
import java.util.Map;

import com.intel.stl.ui.common.STLConstants;

public enum TextEventType {

    COPY((byte) 0, STLConstants.K2159_COPY.getValue()),
    PASTE((byte) 1, STLConstants.K2160_PASTE.getValue()),
    HIGHLIGHT((byte) 2, STLConstants.K2161_HIGHLIGHT.getValue());

    private static final Map<Byte, TextEventType> typeMap =
            new HashMap<Byte, TextEventType>() {
                private static final long serialVersionUID = 1L;
                {
                    for (TextEventType type : TextEventType.values()) {
                        put(type.id, type);
                    }
                }
            };

    private final byte id;

    private final String value;

    private TextEventType(byte id, String value) {
        this.id = id;
        this.value = value;
    }

    public byte getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static TextEventType getType(byte id) {
        return typeMap.get(id);
    }
}
