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

package com.intel.stl.ui.model;

import java.util.EnumMap;

import com.intel.stl.api.configuration.MTUSize;
import com.intel.stl.ui.common.STLConstants;

public enum MTUSizeViz {
    INVALID(MTUSize.INVALID, STLConstants.K0039_NOT_AVAILABLE.getValue()),
    // remove the following as they are no longer supported by the SM
    // IB_MTU_256(MTUSize.IB_MTU_256, "256"),
    // IB_MTU_512(MTUSize.IB_MTU_512, "512"),
    // IB_MTU_1024(MTUSize.IB_MTU_1024, "1024"),
    IB_MTU_2048(MTUSize.IB_MTU_2048, "2048"),
    IB_MTU_4096(MTUSize.IB_MTU_4096, "4096"),
    STL_MTU_8192(MTUSize.STL_MTU_8192, "8192"),
    STL_MTU_10240(MTUSize.STL_MTU_10240, "10240");

    private final static EnumMap<MTUSize, String> mtuSizeMap =
            new EnumMap<MTUSize, String>(MTUSize.class);

    static {
        for (MTUSizeViz msv : MTUSizeViz.values()) {
            mtuSizeMap.put(msv.mtuSize, msv.value);
        }
    };

    private final MTUSize mtuSize;

    private final String value;

    private MTUSizeViz(MTUSize mtuSize, String value) {
        this.mtuSize = mtuSize;
        this.value = value;
    }

    public MTUSize getMTUSize() {
        return mtuSize;
    }

    public String getValue() {
        return value;
    }

    public static MTUSizeViz getMTUSizeViz(byte size) {
        for (MTUSizeViz msv : MTUSizeViz.values()) {
            if (msv.mtuSize.getSize() == size) {
                return msv;
            }
        }
        return null;
    }

    public static String getMTUSizeStr(MTUSize mtuSize) {
        return mtuSizeMap.get(mtuSize);
    }
}
