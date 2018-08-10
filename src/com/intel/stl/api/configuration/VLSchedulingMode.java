/**
 * Copyright (c) 2017, Intel Corporation
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

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h commit:
 * ebe854e038f332c15aa4598bf6af7773d85405c5
 *
 * typedef enum {
 *       VL_SCHED_MODE_VLARB             = 0,    // VL Arbitration Tables
 *       VL_SCHED_MODE_BW_METER          = 1,    // BW Metering Tables
 *       VL_SCHED_MODE_AUTOMATIC         = 2,    // harcoded, not configurabl
 *       // reserved 3
 * } STL_VL_SCHEDULING_MODE;
 *
 * </pre>
 */

public enum VLSchedulingMode {
    VL_ARB((short) 0),
    BW_METER((short) 1),
    AUTOMATIC((short) 2);

    private final short code;

    private VLSchedulingMode(short code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public short getCode() {
        return code;
    }

    public static VLSchedulingMode getVLSchedulingMode(short vlCode) {
        for (VLSchedulingMode vlMode : values()) {
            if (vlCode == vlMode.getCode()) {
                return vlMode;
            }
        }
        throw new IllegalArgumentException(
                "Unknown VL Scheduling Mode " + vlCode);

    }
}
