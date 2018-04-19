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

import com.intel.stl.api.subnet.P_KeyTableBean;
import com.intel.stl.api.subnet.P_KeyTableRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h v1.92<br>
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 * P_KeyTableRecord
 *
 * STL Differences:
 * 	LID extended to 32 bits.
 * 	Reserved shortened to restore alignment.
 *
 * typedef struct {
 * 	struct {
 * 		uint32	LID;
 * 		uint16	Blocknum;
 * 		uint8	PortNum;  // for switch or HFI: port numnber
 * 	} PACK_SUFFIX RID;
 *
 * 	uint8		Reserved;
 *
 * 	STL_PARTITION_TABLE	PKeyTblData;
 *
 * } PACK_SUFFIX STL_P_KEY_TABLE_RECORD;
 *
 * typedef struct {
 *
 * 	STL_PKEY_ELEMENT PartitionTableBlock[NUM_PKEY_ELEMENTS_BLOCK];	// RW List of P_Key Block elements
 *
 * } PACK_SUFFIX STL_PARTITION_TABLE;
 *
 * typedef union {
 * 	uint16  AsReg16;
 * 	struct { IB_BITFIELD2( uint16,
 * 		MembershipType:		1,				// 0=Limited, 1=Full
 * 		P_KeyBase:			15 )			// Base value of the P_Key that
 * 											//  the endnode will use to check
 * 											//  against incoming packets
 * 	} s;
 *
 * } PACK_SUFFIX STL_PKEY_ELEMENT;
 *
 * #define NUM_PKEY_ELEMENTS_BLOCK		(PARTITION_TABLE_BLOCK_SIZE)
 * #define PARTITION_TABLE_BLOCK_SIZE 32
 *
 * </pre>
 *
 */
public class PKeyTableRecord extends SimpleDatagram<P_KeyTableRecordBean> {

    public PKeyTableRecord() {
        super(72);
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setBlockNum(short num) {
        buffer.putShort(4, num);
    }

    public void setPortNum(byte num) {
        buffer.put(6, num);
    }

    public void setPKeyTableData(short[] data) {
        if (data.length != SAConstants.NUM_PKEY_ELEMENTS_BLOCK) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.NUM_PKEY_ELEMENTS_BLOCK + ",  got "
                    + data.length + ".");
        }

        buffer.position(8);
        for (int i = 0; i < SAConstants.NUM_PKEY_ELEMENTS_BLOCK; i++) {
            buffer.putShort(data[i]);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public P_KeyTableRecordBean toObject() {
        buffer.clear();
        int lid = buffer.getInt();
        short blockNum = buffer.getShort();
        byte portNum = buffer.get();

        buffer.position(8);
        P_KeyTableBean[] pKeyTableData =
                new P_KeyTableBean[SAConstants.NUM_PKEY_ELEMENTS_BLOCK];
        for (int i = 0; i < SAConstants.NUM_PKEY_ELEMENTS_BLOCK; i++) {
            short val = buffer.getShort();
            pKeyTableData[i] = new P_KeyTableBean((val & 0x8000) == 0x8000,
                    (short) (val & 0x7fff));
        }
        P_KeyTableRecordBean bean =
                new P_KeyTableRecordBean(lid, blockNum, portNum, pKeyTableData);
        return bean;
    }

}
