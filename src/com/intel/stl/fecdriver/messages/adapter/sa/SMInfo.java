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

import com.intel.stl.api.subnet.SMInfoBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 * SMInfo
 *
 * Attribute Modifier as: 0 (not used)
 *
 * typedef struct {
 * 	uint64  PortGUID;   			// RO This SM's perception of the GUID
 * 								// of the master SM
 * 	uint64  SM_Key; 			// RO Key of this SM. This is shown as 0 unless
 * 								// the requesting SM is proven to be the
 * 								// master, or the requester is otherwise
 * 								// authenticated
 * 	uint32  ActCount;   		// RO Counter that increments each time the SM
 * 								// issues a SMP or performs other management
 * 								// activities. Used as a 'heartbeat' indicator
 * 								// by standby SMs
 * 	uint32  ElapsedTime;   		// RO Time (in seconds): time Master SM has been
 * 								// Master, or time since Standby SM was last
 * 								// updated by Master
 * 	union {
 * 		uint16  AsReg16;
 * 		struct { IB_BITFIELD4( uint16,
 * 			Priority:			4, 	// RO Administratively assigned priority for this
 * 									// SM. Can be reset by master SM
 * 			ElevatedPriority:	4,	// RO This SM's elevated priority
 * 			InitialPriority:	4,	// RO This SM's initial priority
 * 			SMStateCurrent:		4 )	// RO This SM's current state (see SM_STATE)
 * 		} s;
 * 	} u;
 *
 * } PACK_SUFFIX STL_SM_INFO;
 * </pre>
 *
 */
public class SMInfo extends SimpleDatagram<SMInfoBean> {
    public SMInfo() {
        super(26);
    }

    public void setGuid(long guid) {
        buffer.putLong(0, guid);
    }

    public void setSMKey(long key) {
        buffer.putLong(8, key);
    }

    public void setActCount(int count) {
        buffer.putInt(16, count);
    }

    public void setElapsedTime(int seconds) {
        buffer.putInt(20, seconds);
    }

    public void setPriorities(short priorities) {
        buffer.putShort(24, priorities);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public SMInfoBean toObject() {
        buffer.clear();
        SMInfoBean bean = new SMInfoBean();
        bean.setPortGuid(buffer.getLong());
        bean.setSmKey(buffer.getLong());
        bean.setActCount(buffer.getInt());
        bean.setElapsedTime(buffer.getInt());
        short shortVal = buffer.getShort();
        bean.setPriority((byte) ((shortVal >>> 12) & 0x0f));
        bean.setElevatedPriority((byte) ((shortVal >>> 8) & 0x0f));
        bean.setInitialPriority((byte) ((shortVal >>> 4) & 0x0f));
        bean.setSmStateCurrent((byte) (shortVal & 0x0f));
        return bean;
    }

}
