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

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_sm.h v1.115
 * typedef STL_FIELDUNION16(STL_CAPABILITY_MASK, 32,
 *         CmReserved6:                        1,      // shall be zero 
 *         CmReserved24:                       2,      // shall be zero 
 *         CmReserved5:                        2,      // shall be zero 
 *         CmReserved23:                       4,      // shall be zero 
 *         IsCapabilityMaskNoticeSupported:    1,      
 *         CmReserved22:                       1,      // shall be zero 
 *         IsVendorClassSupported:             1,      
 *         IsDeviceManagementSupported:        1,      
 *         CmReserved21:                       2,      // shall be zero 
 *         IsConnectionManagementSupported:    1,      
 *         CmReserved25:                      10,      // shall be zero 
 *         IsAutomaticMigrationSupported:      1,      
 *         CmReserved2:                        1,      // shall be zero 
 *         CmReserved20:                       2,      
 *         IsSM:                               1,      
 *         CmReserved1:                        1 );    // shall be zero
 * </pre>
 */
public enum CapabilityMask {
    HAS_NOTICE(0x00400000),
    HAS_VENDORCLASS(0x00100000),
    HAS_DEVICEMANAGEMENT(0x00080000),
    HAS_CONNECTIONMANAGEMENT(0x00010000),
    HAS_AUTOMIGRATION(0x00000020),
    HAS_SM(0x00000002);

    private final int val;

    private CapabilityMask(int inval) {
        val = inval;
    }

    public int getCapabilityMask() {
        return val;
    }

    public boolean hasThisMask(int inval) {
        return ((val & inval) == val);
    }
}
