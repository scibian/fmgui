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
import com.intel.stl.api.subnet.SC2SLMTRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h.1.91<br>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_types.h.1.20
 * 
 * <pre>
 * 
 * 
 * SC2SLMappingTableRecord
 * 
 * STL Differences:
 *      New for STL.
 * 
 * typedef struct {
 *     struct {
 *         uint32  LID;    
 *         uint16  Reserved;               
 *     } PACK_SUFFIX RID;
 *    
 *     uint16      Reserved2;
 *    
 *     STL_SL      SCSLMap[STL_MAX_SLS];   
 *    
 * } PACK_SUFFIX STL_SC2SL_MAPPING_TABLE_RECORD;
 * 
 * 
 * typedef struct { IB_BITFIELD2( uint8,
 *     Reserved:   3,
 *     SL:         5 )
 * } STL_SL;
 * </pre>
 * 
 */
public class SC2SLMTRecord extends SimpleDatagram<SC2SLMTRecordBean> {

    public SC2SLMTRecord() {
        super(40);// 4+2+2+1*32
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setSC2SLMTData(byte[] data) {
        if (data.length != SAConstants.STL_MAX_SLS) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.STL_MAX_SLS + ", got " + data.length);
        }

        buffer.position(8);
        for (byte val : data) {
            buffer.put(val);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public SC2SLMTRecordBean toObject() {
        buffer.clear();
        int lid = buffer.getInt();

        buffer.position(8);
        byte[] data = new byte[SAConstants.STL_MAX_SLS];
        buffer.get(data);
        SC2SLMTRecordBean bean = new SC2SLMTRecordBean(lid, data);
        return bean;
    }

}
