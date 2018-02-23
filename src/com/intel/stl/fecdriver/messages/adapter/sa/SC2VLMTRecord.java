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
import com.intel.stl.api.subnet.SC2VLMTRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_types.h
 * commit b48928560903beafd3a69be0137f081c68b73fb7
 * date 2017-02-21 15:29:45
 *
 *
 *  SC2VL Mapping Table Records
 *
 *  There are three possible SC to VL mapping tables: NT, T and R. SC2VL_R
 *  will not be implemented in STL Gen 1. While they are all three separate
 *  SA MAD attributes, they all have identical structure.
 *
 *  typedef struct {
 *      struct {
 *          uint32  LID;
 *          uint8   Port;
 *      } PACK_SUFFIX RID;
 *
 *      uint8       Reserved[3];
 *
 *      STL_VL      SCVLMap[STL_MAX_SCS];
 *
 *  } PACK_SUFFIX STL_SC2VL_R_MAPPING_TABLE_RECORD;
 *
 *
 *  typedef struct { IB_BITFIELD2( uint8,
 *     Reserved:   3,
 *     VL:         5 )
 * } STL_VL;
 *
 * #define STL_MAX_SCS          32          // Max number of SCs
 * </pre>
 *
 */
public class SC2VLMTRecord extends SimpleDatagram<SC2VLMTRecordBean> {

    public SC2VLMTRecord() {
        super(40);// 4+1+3+1*32
    }

    public void setLID(int lid) {
        buffer.putInt(0, lid);
    }

    public void setPort(byte port) {
        buffer.put(4, port);
    }

    public void setSC2VLMTData(byte[] data) {
        if (data.length != SAConstants.STL_MAX_SCS) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.STL_MAX_SCS + ", got " + data.length);
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
    public SC2VLMTRecordBean toObject() {
        buffer.clear();
        int lid = buffer.getInt();
        byte port = buffer.get();
        buffer.position(8);
        byte[] data = new byte[SAConstants.STL_MAX_SCS];
        buffer.get(data);
        SC2VLMTRecordBean bean = new SC2VLMTRecordBean(lid, port, data);
        return bean;
    }

}
