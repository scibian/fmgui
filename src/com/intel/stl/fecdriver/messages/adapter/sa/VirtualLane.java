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

import com.intel.stl.api.subnet.VirtualLaneBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm.h v1.115
 * 
 * <pre>
 * 
 * 	struct {
 *      uint8    PreemptCap;
 * 
 * 		struct { IB_BITFIELD2( uint8,
 * 			Reserved:		3,
 * 			Cap:			5 )		// RO/HS-E Virtual Lanes supported on this port 
 * 		} s2;
 * 
 * 		uint16  HighLimit;			// RW/HS-E Limit of high priority component of 
 * 									//  VL Arbitration table 
 * 									// POD: 0 
 * 		uint16  PreemptingLimit;	// RW/HS-E Limit of preempt component of 
 * 									//  VL Arbitration table 
 * 									// POD: 0 
 * 		uint8   ArbitrationHighCap; // RO/HS-E 
 * 		uint8   ArbitrationLowCap;	// RO/HS-E 
 * 	} VL;
 * </pre>
 * 
 */
public class VirtualLane extends SimpleDatagram<VirtualLaneBean> {

    /**
     * @param length
     */
    public VirtualLane() {
        super(8);
    }

    public void setPreemptCap(byte cap) {
        buffer.put(0, cap);
    }

    public void setCap(byte cap) {
        buffer.put(1, cap);
    }

    public void setHighLimit(short limit) {
        buffer.putShort(2, limit);
    }

    public void setPreemptingLimit(short limit) {
        buffer.putShort(4, limit);
    }

    public void setArbitrationHighCap(byte cap) {
        buffer.putShort(6, cap);
    }

    public void setArbitrationLowCap(byte cap) {
        buffer.putShort(7, cap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public VirtualLaneBean toObject() {
        buffer.clear();
        VirtualLaneBean bean =
                new VirtualLaneBean(buffer.get(), buffer.get(),
                        buffer.getShort(), buffer.getShort(), buffer.get(),
                        buffer.get());
        return bean;
    }

}
