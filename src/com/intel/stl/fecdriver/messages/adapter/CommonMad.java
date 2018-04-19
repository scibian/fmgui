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

import com.intel.stl.api.IdGenerator;
import com.intel.stl.common.Constants;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_mad.h
 * 
 * <pre>
 * typedef struct _MAD_COMMON {
 * 	uint8	BaseVersion;			// Version of management datagram base  
 * 									// format.Value is set to 1 
 * 	uint8	MgmtClass;				// Class of operation.  
 * 	uint8	ClassVersion;			// Version of MAD class-specific format. 
 * 									// Value is set to 1 except  
 * 									// for the Vendor class 
 * 	union {
 * 		uint8	AsReg8;
 * 		struct { IB_BITFIELD2(uint8,
 * 			R		:1,		// Request/response field,  
 * 							// conformant to base class definition 
 * 			Method	:7)		// Method to perform based on  
 * 							// the management class 
 * 		} PACK_SUFFIX s;
 * 	} mr;
 * 
 * 	union {
 * 		// All MADs use this structure 
 * 		struct {
 * 			MAD_STATUS	Status;		// Code indicating status of method 
 * 			uint16		Reserved1;	// Reserved. Shall be set to 0 
 * 		} NS;						// Normal MAD 
 * 
 * 		// This structure is used only for Directed Route SMP's 
 * 		struct {
 * 			struct { IB_BITFIELD2(uint16,
 * 				D		:1,	// Direction bit to determine  
 * 							// direction of packet 
 * 				Status	:15)/* Code indicating status of method 
 * 			} PACK_SUFFIX s;						
 * 			uint8	HopPointer;		// used to indicate the current byte  
 * 									// of the Initial/Return Path field. 
 * 			uint8	HopCount;		// used to contain the number of valid  
 * 									// bytes in the Initial/Return Path 
 * 		} DR;				// Directed Route only 
 * 	} u;
 * 	uint64	TransactionID;			// Transaction specific identifier 
 * 	uint16	AttributeID;			// Defines objects being operated  
 * 									// on by a management class 
 * 	uint16	Reserved2;				// Reserved. Shall be set to 0 
 * 	uint32	AttributeModifier;		// Provides further scope to  
 * 									// the Attributes, usage  
 * 									// determined by the management class 
 * } PACK_SUFFIX MAD_COMMON;
 * </pre>
 * 
 */
public class CommonMad extends SimpleDatagram<Void> {
    public CommonMad() {
        super(24);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.resource.stl.data.SimpleDatagram#initData()
     */
    @Override
    protected void initData() {
        super.initData();
        setTransactionID(IdGenerator.id());
        setNSStatus(Constants.MAD_STATUS_SUCCESS);
    }

    public void setBaseVersion(byte version) {
        buffer.put(0, version);
    }

    public void setMgmtClass(byte mgmtClass) {
        buffer.put(1, mgmtClass);
    }

    public void setClassVersion(byte version) {
        buffer.put(2, version);
    }

    public void setMethod(byte mr) {
        buffer.put(3, mr);
    }

    // for normal mad
    public void setNSStatus(short status) {
        buffer.putShort(4, status);
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public short getNSStatus() {
        return buffer.getShort(4);
    }

    // for directed route
    public void setDRStatus(short status, byte hopPointer, byte hopCount) {
        buffer.putShort(4, status);
        buffer.put(6, hopPointer);
        buffer.put(7, hopCount);
    }

    public void setTransactionID(long id) {
        buffer.putLong(8, id);
    }

    public long getTransactionId() {
        return buffer.getLong(8);
    }

    public void setAttributeID(short id) {
        buffer.putShort(16, id);
    }

    public short getAttributeID() {
        return buffer.getShort(16);
    }

    public void setAttributeModifer(int modifer) {
        buffer.putInt(20, modifer);
    }

}
