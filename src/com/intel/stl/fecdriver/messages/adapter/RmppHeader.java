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
package com.intel.stl.fecdriver.messages.adapter;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_generalServices.h
 * <pre>
 * typedef struct _RMPP_HEADER {
 * 	uint8		RmppVersion;		// version of RMPP implemented 
 * 									// must be 0 if RMPP_FLAG.Active=0 
 * 	uint8		RmppType;			// type of RMPP packet 
 * 	RMPP_FLAG	RmppFlags;
 * 	uint8		RmppStatus;
 * 	union {
 * 		uint32		AsReg32;
 * 		uint32		SegmentNum;	// DATA and ACK 
 * 		uint32		Reserved1;	// ABORT and STOP 
 * 	} u1;
 * 	union {
 * 		uint32		AsReg32;
 * 		uint32		PayloadLen;		// first and last DATA 
 * 		uint32		NewWindowLast;	// ACK 
 * 		uint32		Reserved2;		// ABORT, STOP and middle DATA 
 * 	} u2;
 * } PACK_SUFFIX RMPP_HEADER, *PRMPP_HEADER;
 * </pre> 
 */
public class RmppHeader extends SimpleDatagram<Void> {
	
	public RmppHeader() {
		super(12);
	}
	
	public void setRmppVersion(byte version) {
		buffer.put(0, version);
	}
	
	public void setRmppType(byte type) {
		buffer.put(1, type);
	}
	
	public void setRmppFlags(byte flags) {
		buffer.put(2, flags);
	}
	
	public void setRmppStatus(byte status) {
		buffer.put(3, status);
	}
	
	public void setSegmentNum(int num) {
		buffer.putInt(4, num);
	}
	
	public void setReserved1(int value) {
		buffer.putInt(4, value);
	}
	
	public void setPayloadLen(int payloadLen) {
		buffer.putInt(8, payloadLen);
	}
	
	public void setNewWindowLast(int newWindowLast) {
		buffer.putInt(8, newWindowLast);
	}
	
	public void Reserved2(int value) {
		buffer.putInt(8, value);
	}
}
