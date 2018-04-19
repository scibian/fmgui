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

import com.intel.stl.api.subnet.LFTRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * LFTRecord
 *
 * Blocks are still defined as 64 bytes long to be consistent with IB.
 *
 * STL Differences:
 *
 * 	LID extended to 32 bits.
 * BlockNum extended to 18 bits.
 *
 * typedef struct {
 * 	struct {
 * 		uint32	LID;
 * 		IB_BITFIELD2(uint32,
 * 				Reserved:14,
 * 				BlockNum:18);
 * 	} PACK_SUFFIX RID;
 *
 * 	// 8 bytes
 *
 * 	uint8 		LinearFdbData[64];
 *
 * 	// 72 bytes
 * } PACK_SUFFIX STL_LINEAR_FORWARDING_TABLE_RECORD;
 * </pre>
 *
 */
public class LFTRecord extends SimpleDatagram<LFTRecordBean> {

    public LFTRecord() {
        super(72);
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setBlockNum(int num) {
        buffer.putInt(4, num & 0x03ffff);
    }

    public void setLinearFdbData(byte[] data) {
        if (data.length != SAConstants.FDB_DATA_LENGTH) {
            throw new IllegalArgumentException("Invalid array length. Expect "
                    + SAConstants.FDB_DATA_LENGTH + ", got" + data.length);
        }

        buffer.position(8);
        buffer.put(data);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public LFTRecordBean toObject() {
        buffer.clear();
        int lid = buffer.getInt();
        int blockNum = buffer.getInt() & 0x03ffff;
        byte[] data = new byte[SAConstants.FDB_DATA_LENGTH];
        buffer.get(data);
        LFTRecordBean bean = new LFTRecordBean(lid, blockNum, data);
        return bean;
    }

}
