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
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.SMInfoDataBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 *  typedef struct _STL_SMINFO_DATA {
 * [4] 	STL_LID_32				lid;
 * [5] 	IB_BITFIELD2(uint8,
 *  		priority : 4,
 *  		state : 4)
 * [6] 	uint8					portNumber;
 * [8] 	uint16					reserved;
 * [16] 	uint64					smPortGuid;
 * [80] 	char					smNodeDesc[64]; // can be 64 char w/o \0
 *  } PACK_SUFFIX STL_SMINFO_DATA;
 *
 *  typedef struct _STL_PA_IMAGE_INFO_DATA {
 * [16] 	STL_PA_IMAGE_ID_DATA	imageId;
 * [24] 	uint64					sweepStart;
 * [28] 	uint32					sweepDuration;
 * [30] 	uint16					numHFIPorts;
 * [32] 	uint16					reserved3;
 * [34]	    uint16					reserved;
 * [36] 	uint16					numSwitchNodes;
 * [40] 	uint32					numSwitchPorts;
 * [44] 	uint32					numLinks;
 * [48] 	uint32					numSMs;
 * [52] 	uint32					numNoRespNodes;
 * [56] 	uint32					numNoRespPorts;
 * [60] 	uint32					numSkippedNodes;
 * [64] 	uint32					numSkippedPorts;
 * [68] 	uint32					numUnexpectedClearPorts;
 * [72] 	uint32					imageInterval;
 * [232] 	STL_SMINFO_DATA			SMInfo[2];
 *  } PACK_SUFFIX STL_PA_IMAGE_INFO_DATA;
 *
 *  typedef struct _STL_PA_Image_ID_Data {
 *  	uint64					imageNumber;
 *  	int32					imageOffset;
 *      union {
 *          uint32              absoluteTime;
 *          int32               timeOffset;
 *      }
 *  } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 * </pre>
 *
 */
public class ImageInfo extends SimpleDatagram<ImageInfoBean> {
    public ImageInfo() {
        super(232);
    }

    public void setImageNumber(long imageNumber) {
        buffer.putLong(0, imageNumber);
    }

    public void setImageOffset(int imageOffset) {
        buffer.putInt(8, imageOffset);
    }

    private SMInfoDataBean getSMInfoBean(int position) {
        buffer.position(position);
        SMInfoDataBean bean = new SMInfoDataBean();
        bean.setLid(buffer.getInt());
        byte byteVal = buffer.get();
        bean.setPriority((byte) ((byteVal >>> 4) & 0x0f));
        bean.setState((byte) (byteVal & 0x0f));
        bean.setPortNumber(buffer.get());
        bean.setSmPortGuid(buffer.getLong(position + 8));
        byte[] raw = new byte[SAConstants.NODE_DESC_LENGTH];
        buffer.position(position + 16);
        buffer.get(raw);
        String name =
                StringUtils.toString(raw, 0, SAConstants.NODE_DESC_LENGTH);
        bean.setSmNodeDesc(name);
        return bean;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public ImageInfoBean toObject() {
        buffer.clear();
        ImageInfoBean bean = new ImageInfoBean();
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt(),
                buffer.getInt()));
        buffer.position(16);
        bean.setSweepStart(buffer.getLong());
        bean.setSweepDuration(buffer.getInt());
        bean.setNumHFIPorts(buffer.getShort());
        buffer.getShort(); // reserved
        buffer.getShort(); // reserved
        bean.setNumSwitchNodes(buffer.getShort());
        bean.setNumSwitchPorts(buffer.getInt());
        bean.setNumLinks(buffer.getInt());
        bean.setNumSMs(buffer.getInt());
        bean.setNumNoRespNodes(buffer.getInt());
        bean.setNumNoRespPorts(buffer.getInt());
        bean.setNumSkippedNodes(buffer.getInt());
        bean.setNumSkippedPorts(buffer.getInt());
        bean.setNumUnexpectedClearPorts(buffer.getInt());
        bean.setImageInterval(buffer.getInt());
        SMInfoDataBean[] smInfo =
                new SMInfoDataBean[] { getSMInfoBean(72), getSMInfoBean(152) };
        bean.setSMInfo(smInfo);
        return bean;
    }

}
