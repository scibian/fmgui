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

package com.intel.stl.fecdriver.messages.adapter.sa;

import com.intel.stl.api.subnet.FabricInfoBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 *
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * typedef struct {
 *     uint32  NumHFIs;             // HFI Nodes
 *     uint32  NumSwitches;         // Switch Nodes (ASICs)
 *          // Internal = in same SystemImageGuid
 *          // HFI = HFI to switch and HFI to HFI links
 *          // ISL = switch to switch links
 *          // links which are Omitted will not be considered for Degraded checks
 *          // switch port 0 is not counted as a link
 *     uint32  NumInternalHFILinks; // HFI to switch (or HFI) links
 *     uint32  NumExternalHFILinks; // HFI to switch (or HFI) links
 *     uint32  NumInternalISLs;     // switch to switch links
 *     uint32  NumExternalISLs;     // switch to switch links
 *     uint32  NumDegradedHFILinks; // links with one or both sides below best enabled
 *     uint32  NumDegradedISLs;     // links with one or both sides below best enabled
 *     uint32  NumOmittedHFILinks;  // inks quarantined or left in Init
 *     uint32  NumOmittedISLs;      // links quarantined or left in Init
 *     uint32  rsvd5[92];
 * } PACK_SUFFIX STL_FABRICINFO_RECORD;
 *
 * </pre>
 */
public class FabricInfo extends SimpleDatagram<FabricInfoBean> {

    public FabricInfo() {
        super(408);
    }

    @Override
    public FabricInfoBean toObject() {
        FabricInfoBean fabricInfo = new FabricInfoBean();
        buffer.clear();
        fabricInfo.setNumHFIs(uint2long(buffer.getInt()));
        fabricInfo.setNumSwitches(uint2long(buffer.getInt()));
        fabricInfo.setNumInternalHFILinks(uint2long(buffer.getInt()));
        fabricInfo.setNumExternalHFILinks(uint2long(buffer.getInt()));
        fabricInfo.setNumInternalISLs(uint2long(buffer.getInt()));
        fabricInfo.setNumExternalISLs(uint2long(buffer.getInt()));
        fabricInfo.setNumDegradedHFILinks(uint2long(buffer.getInt()));
        fabricInfo.setNumDegradedISLs(uint2long(buffer.getInt()));
        fabricInfo.setNumOmittedHFILinks(uint2long(buffer.getInt()));
        fabricInfo.setNumOmittedISLs(uint2long(buffer.getInt()));
        return fabricInfo;
    }

    private static long uint2long(int val) {
        return val & 0x00000000ffffffffL;
    }

}
