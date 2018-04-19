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

import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.VLArbTableBean;
import com.intel.stl.api.subnet.VLArbTableRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 * VLArbitrationRecord
 *
 * STL Differences:
 * 	Switch LID extended.
 * 	Blocknum now defined as 0 - 3 as per the VL Arbitration Table MAD.
 * 	Length of Low, High tables extended to 128 bytes.
 * 	Preempt table added.
 *
 * typedef struct {
 * 	struct {
 * 		uint32	LID;
 * 		uint8	OutputPortNum;  // for switch or HFI: port numnber
 * 		uint8	BlockNum;
 * 	} PACK_SUFFIX RID;
 *
 * 	uint16		Reserved;
 *
 * 	STL_VLARB_TABLE VLArbTable;
 *
 * } PACK_SUFFIX STL_VLARBTABLE_RECORD;
 *
 * typedef struct {
 * 	struct { IB_BITFIELD2( uint8,
 * 		Reserved:		3,
 * 		VL:				5 )		// RW
 * 	} s;
 *
 * 	uint8   Weight;				// RW
 *
 * } PACK_SUFFIX STL_VLARB_TABLE_ELEMENT;
 *
 * #define VLARB_TABLE_LENGTH 128
 * typedef union {
 * 	STL_VLARB_TABLE_ELEMENT  Elements[VLARB_TABLE_LENGTH]; // RW
 * 	uint32                   Matrix[STL_MAX_VLS];	// RW
 * 													// POD: 0
 *
 * } PACK_SUFFIX STL_VLARB_TABLE;
 *
 * #define STL_MAX_VLS			32
 * </pre>
 *
 */
public class VLArbTableRecord extends SimpleDatagram<VLArbTableRecordBean> {

    public VLArbTableRecord() {
        super(264);
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setOutputPortNum(byte num) {
        buffer.put(4, num);
    }

    public void setBlockNum(byte num) {
        buffer.put(5, num);
    }

    public void setElements(byte[] vls, byte[] weights) {
        if (vls.length != SAConstants.VLARB_TABLE_LENGTH) {
            throw new IllegalArgumentException("Invalid VLs length. Expected "
                    + SAConstants.VLARB_TABLE_LENGTH + ", got " + vls.length);
        }
        if (weights.length != SAConstants.VLARB_TABLE_LENGTH) {
            throw new IllegalArgumentException(
                    "Invalid Weights length. Expected "
                            + SAConstants.VLARB_TABLE_LENGTH + ", got "
                            + weights.length);
        }

        buffer.position(8);
        for (int i = 0; i < SAConstants.VLARB_TABLE_LENGTH; i++) {
            buffer.put((byte) (vls[i] & 0x1f));
            buffer.put(weights[i]);
        }
    }

    public void setMatrix(int[] matrix) {
        if (matrix.length != SAConstants.STL_MAX_VLS) {
            throw new IllegalArgumentException(
                    "Invalid matrix length. Expected " + SAConstants.STL_MAX_VLS
                            + ", got " + matrix.length);
        }

        buffer.position(8);
        for (int i = 0; i < SAConstants.STL_MAX_VLS; i++) {
            buffer.putInt(matrix[i]);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public VLArbTableRecordBean toObject() {
        buffer.clear();
        VLArbTableRecordBean bean = new VLArbTableRecordBean(buffer.getInt(),
                buffer.get(), buffer.get());

        // TODO: when to interpret it as Matrix?
        buffer.position(8);
        VLArbTableBean[] data =
                new VLArbTableBean[SAConstants.VLARB_TABLE_LENGTH];
        for (int i = 0; i < SAConstants.VLARB_TABLE_LENGTH; i++) {
            data[i] = new VLArbTableBean((byte) (buffer.get() & 0x1f),
                    buffer.get());
        }
        bean.setVlArbTableElement(data);

        buffer.position(8);
        int[] matrix = new int[SAConstants.STL_MAX_VLS];
        for (int i = 0; i < SAConstants.STL_MAX_VLS; i++) {
            matrix[i] = buffer.getInt();
        }
        bean.setMatrix(matrix);

        return bean;
    }

}
