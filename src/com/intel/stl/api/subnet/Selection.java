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

package com.intel.stl.api.subnet;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa.h v1.28
 * 
 * <pre>
 * #define STL_PA_SELECT_UTIL_HIGH         0x00020001          // highest first, descending
 * #define STL_PA_SELECT_UTIL_MC_HIGH      0x00020081
 * #define STL_PA_SELECT_UTIL_PKTS_HIGH    0x00020082
 * #define STL_PA_SELECT_UTIL_LOW          0x00020101          // lowest first, ascending
 * #define STL_PA_SELECT_UTIL_MC_LOW       0x00020102
 * #define STL_PA_SELECT_ERR_INTEG         0x00030001          // hightest first, descending
 * #define STL_PA_SELECT_ERR_CONG          0x00030002
 * #define STL_PA_SELECT_ERR_SMA_CONG      0x00030003
 * #define STL_PA_SELECT_ERR_BUBBLE        0x00030004
 * #define STL_PA_SELECT_ERR_SEC           0x00030005
 * #define STL_PA_SELECT_ERR_ROUT          0x00030006
 * </pre>
 */
public enum Selection {
    UTILIZATION_HIGH(0x00020001),
    PACKET_RATE_HIGH(0x00020082),
    UTILIZATION_LOW(0x00020101),
    INTEGRITY_ERRORS_HIGH(0x00030001),
    CONGESTION_ERRORS_HIGH(0x00030002),
    SMA_CONGESTION_ERRORS_HIGH(0x00030003),
    BUBBLE_ERRORS_HIGH(0x00030004),
    SECURITY_ERRORS_HIGH(0x00030005),
    ROUTING_ERRORS_HIGH(0x00030006);

    private final int select;

    private Selection(int select) {
        this.select = select;
    }

    /**
     * @return the select
     */
    public int getSelect() {
        return select;
    }

}
