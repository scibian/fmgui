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
package com.intel.stl.fecdriver.messages.adapter.pa;

import com.intel.stl.api.performance.FocusPortsRspBean;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa.h version 1.55
 *
 * <pre>
 *   // TBD is value2 always guid?
 *  typedef struct _STL_FOCUS_PORTS_RSP {
 * [16]     STL_PA_IMAGE_ID_DATA    imageId;
 * [20]     uint32                  nodeLid;
 * [21]     uint8                   portNumber;
 * [22]     uint8                   rate;   // IB_STATIC_RATE - 5 bit value
 * [23]     uint8                   mtu;    // enum IB_MTU - 4 bit value
 * [24]     IB_BITFIELD2(uint8,     localFlags : 4,
 *                                  neighborFlags : 4)
 * [32]     uint64                  value;      // list sorting factor
 * [40]     uint64                  nodeGUID;
 * [104]    char                    nodeDesc[64]; // can be 64 char w/o \0
 * [108]    uint32                  neighborLid;
 * [109]    uint8                   neighborPortNumber;
 * [112]    uint8                   reserved3[3];
 * [120]    uint64                  neighborValue;
 * [128]    uint64                  neighborGuid;
 * [192]    char                    neighborNodeDesc[64]; // can be 64 char w/o \0
 *  } PACK_SUFFIX STL_FOCUS_PORTS_RSP;
 *
 *   typedef struct _STL_PA_Image_ID_Data {
 *       uint64                  imageNumber;
 *       int32                   imageOffset;
 *       uint32                  reserved;
 *   } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 * </pre>
 *
 */
public class FocusPortsRsp extends SimpleDatagram<FocusPortsRspBean> {
    public FocusPortsRsp() {
        super(192);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public FocusPortsRspBean toObject() {
        buffer.clear();
        FocusPortsRspBean bean = new FocusPortsRspBean();
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt()));
        buffer.position(16);
        bean.setNodeLid(buffer.getInt());
        bean.setPortNumber(buffer.get());
        bean.setRate(buffer.get());
        bean.setMtu(buffer.get());
        byte val = buffer.get();
        bean.setLocalFlags((byte) ((val & 0xf0) >> 4));
        bean.setNeighborFlags((byte) (val & 0x0f));
        bean.setValue(buffer.getLong());
        bean.setNodeGUID(buffer.getLong());
        bean.setNodeDesc(StringUtils.toString(buffer.array(),
                buffer.arrayOffset() + 40, SAConstants.NODE_DESC_LENGTH));
        buffer.position(104);
        bean.setNeighborLid(buffer.getInt());
        bean.setNeighborPortNumber(buffer.get());
        buffer.position(112);
        bean.setNeighborValue(buffer.getLong());
        bean.setNeighborGuid(buffer.getLong());
        bean.setNeighborNodeDesc(StringUtils.toString(buffer.array(),
                buffer.arrayOffset() + 128, SAConstants.NODE_DESC_LENGTH));
        return bean;
    }

}
