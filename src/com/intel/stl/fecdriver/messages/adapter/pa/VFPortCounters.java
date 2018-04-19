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

import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa.h v1.52
 * 
 * <pre>
 *  typedef struct _STL_PA_VF_PORT_COUNTERS_DATA {
 * [4]     uint32              nodeLid;
 * [5]     uint8               portNumber;
 * [8]     uint8               reserved[3];
 * [12]    uint32              flags;
 * [16]    uint32              reserved1;
 * [24]    uint64              reserved3;
 * [88]    char                vfName[STL_PM_VFNAMELEN];
 * [96]    uint64              reserved2;
 * [112]   STL_PA_IMAGE_ID_DATA imageId;
 * [120]   uint64              portVFXmitData;
 * [128]   uint64              portVFRcvData;
 * [136]   uint64              portVFXmitPkts;
 * [144]   uint64              portVFRcvPkts;
 * [152]   uint64              portVFXmitDiscards;
 * [160]   uint64              swPortVFCongestion;
 * [168]   uint64              portVFXmitWait;
 * [176]   uint64              portVFRcvFECN;
 * [184]   uint64              portVFRcvBECN;
 * [192]   uint64              portVFXmitTimeCong;
 * [200]   uint64              portVFXmitWastedBW;
 * [208]   uint64              portVFXmitWaitData;
 * [216]   uint64              portVFRcvBubble;
 * [224]   uint64              portVFMarkFECN;
 *  } PACK_SUFFIX STL_PA_VF_PORT_COUNTERS_DATA;
 *  
 *  typedef struct _STL_PA_Image_ID_Data {
 * [8] 	uint64					imageNumber;
 * [12] 	int32					imageOffset;
 * [16] 	uint32					reserved;
 *  } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 * </pre>
 * 
 */
public class VFPortCounters extends SimpleDatagram<VFPortCountersBean> {

    private static final int VFNAME_OFFSET = 24;

    private static final int IMAGEID_OFFSET = 96;

    private static final int IMAGEOFFSET_OFFSET = IMAGEID_OFFSET + 8;

    private static final int COUNTERS_OFFSET = 112;

    public VFPortCounters() {
        super(224);
    }

    public void setNodeLid(int lid) {
        buffer.putInt(0, lid);
    }

    public void setPortNumber(byte num) {
        buffer.put(4, num);
    }

    public void setFlags(int flag) {
        buffer.putInt(8, flag);
    }

    public void setVfName(String name) {
        StringUtils.setString(name, buffer, VFNAME_OFFSET,
                PAConstants.STL_PM_GROUPNAMELEN);
    }

    public void setImageNumber(long imageNumber) {
        buffer.putLong(IMAGEID_OFFSET, imageNumber);
    }

    public void setImageOffset(int imageOffset) {
        buffer.putInt(IMAGEOFFSET_OFFSET, imageOffset);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public VFPortCountersBean toObject() {
        buffer.clear();
        VFPortCountersBean bean = new VFPortCountersBean();
        bean.setNodeLid(buffer.getInt());
        bean.setPortNumber(buffer.get());
        buffer.position(8);
        bean.setFlags(buffer.getInt());
        bean.setVfName(StringUtils.toString(buffer.array(),
                buffer.arrayOffset() + VFNAME_OFFSET,
                PAConstants.STL_PM_VFNAMELEN));
        buffer.position(IMAGEID_OFFSET);
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt()));
        buffer.position(COUNTERS_OFFSET);
        bean.setPortVFXmitData(buffer.getLong());
        bean.setPortVFRcvData(buffer.getLong());
        bean.setPortVFXmitPkts(buffer.getLong());
        bean.setPortVFRcvPkts(buffer.getLong());
        bean.setPortVFXmitDiscards(buffer.getLong());
        bean.setSwPortVFCongestion(buffer.getLong());
        bean.setPortVFXmitWait(buffer.getLong());
        bean.setPortVFRcvFECN(buffer.getLong());
        bean.setPortVFRcvBECN(buffer.getLong());
        bean.setPortVFXmitTimeCong(buffer.getLong());
        bean.setPortVFXmitWastedBW(buffer.getLong());
        bean.setPortVFXmitWaitData(buffer.getLong());
        bean.setPortVFRcvBubble(buffer.getLong());
        bean.setPortVFMarkFECN(buffer.getLong());
        return bean;
    }

}
