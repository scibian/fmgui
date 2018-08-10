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
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit 8d05ba37b98661fa539132e27cbd3bd15eea81aa
 * date 2017-11-09 11:11:00
 *
 * Capability Mask 3 - a bit set to 1 for affirmation of supported capability
 *  by a given port
 *
 *  typedef STL_FIELDUNION15(STL_CAPABILITY_MASK3, 16,
 *       CmReserved0:                    1,
 *       CmReserved1:                    1,
 *       CmReserved2:                    1,
 *       IsMAXLIDSupported:          1,      // RO/H--- Does the HFI support the MAX
 *                                           // LID being configured
 *       CmReserved3:                1,
 *       CmReserved4:                1,
 *       VLSchedulingConfig:         2,      // RO/H-PE VL Arbitration
 *                                           // see STL_VL_SCHEDULING_MODE
 *                                           // Port 0 indicates whole switch
 *       IsSnoopSupported:           1,      // RO/--PE Packet snoop
 *                                           // Port 0 indicates whole switch
 *       IsAsyncSC2VLSupported:      1,      // RO/H-PE Port 0 indicates whole switch
 *       IsAddrRangeConfigSupported: 1,      // RO/H-PE Can addr range for Multicast
 *                                           // and Collectives be configured
 *                                           // Port 0 indicates whole switch
 *       IsPassThroughSupported:     1,      // RO/--PE Packet pass through
 *                                           // Port 0 indicates whole switch
 *       IsSharedSpaceSupported:     1,      // RO/H-PE Shared Space
 *                                           // Port 0 indicates whole switch
 *       IsSharedGroupSpaceSupported:1,      // RO/H-PE Shared Space
 *                                           // Port 0 indicates whole switch
 *       IsVLMarkerSupported:        1,      // RO/H-PE VL Marker
 *                                           // Port 0 indicates whole switch
 *       IsVLrSupported:             1 );    // RO/H-PE SC->VL_r table
 *
 * </pre>
 */
public enum CapabilityMask3 {
    MAXLID_SUPPORTED((short) 0x1000),
    SNOOP_SUPPORTED((short) 0x0080),
    ASYNCSC2VL_SUPPORTED((short) 0x0040),
    ADDRRANGECONFIG_SUPPORTED((short) 0x0020),
    PASSTHROUGH_SUPPORTED((short) 0x0010),
    SHAREDSPACE_SUPPORTED((short) 0x0008),
    SHAREDGROUPSPACE_SUPPORTED((short) 0x0004),
    VLMARKER_SUPPORTED((short) 0x0002),
    VLR_SUPPORTED((short) 0x0001);

    private final short mask;

    private CapabilityMask3(short mask) {
        this.mask = mask;
    }

    public short getCapabilityMask() {
        return mask;
    }

    public boolean hasThisMask(short val) {
        return ((this.mask & val) == mask);
    }
}
