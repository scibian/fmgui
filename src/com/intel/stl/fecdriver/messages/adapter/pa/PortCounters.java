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
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * typedef struct _STL_PORT_COUNTERS_DATA {
 * 	uint32				nodeLid;
 * 	uint8				portNumber;
 * 	uint8				reserved[3];
 * 	uint32				flags;
 * 	uint32				reserved1;
 *  uint64              reserved3;
 *  STL_PA_IMAGE_ID_DATA imageId;
 * 	uint64				portXmitData;
 * 	uint64				portRcvData;
 * 	uint64				portXmitPkts;
 * 	uint64				portRcvPkts;
 * 	uint64				portMulticastXmitPkts;
 * 	uint64				portMulticastRcvPkts;
 * 	uint64				localLinkIntegrityErrors;
 * 	uint64				fmConfigErrors;
 * 	uint64				portRcvErrors;
 * 	uint64				excessiveBufferOverruns;
 * 	uint64				portRcvConstraintErrors;
 * 	uint64				portRcvSwitchRelayErrors;
 * 	uint64				portXmitDiscards;
 * 	uint64				portXmitConstraintErrors;
 * 	uint64				portRcvRemotePhysicalErrors;
 * 	uint64				swPortCongestion;
 * 	uint64				portXmitWait;
 * 	uint64				portRcvFECN;
 * 	uint64				portRcvBECN;
 * 	uint64				portXmitTimeCong;
 * 	uint64				portXmitWastedBW;
 * 	uint64				portXmitWaitData;
 * 	uint64				portRcvBubble;
 * 	uint64				portMarkFECN;
 * 	uint32				linkErrorRecovery;
 * 	uint32				linkDowned;
 * 	uint8				uncorrectableErrors;
 * 	union {
 * 		uint8			AsReg8;
 * 		struct {		IB_BITFIELD2(uint8,
 *                        numLanesDown : 4,
 * 						  reserved : 1,
 * 						  linkQualityIndicator : 3)
 * 		} PACK_SUFFIX s;
 * 	} lq;
 *  uint8               reserved2[6];
 * } PACK_SUFFIX STL_PORT_COUNTERS_DATA;
 *
 * typedef struct _STL_PA_Image_ID_Data {
 * 	uint64					imageNumber;
 * 	int32					imageOffset;
 *  union {
 *      uint32              absoluteTime;
 *      int32               timeOffset;
 *  }
 * } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 * </pre>
 *
 */
public class PortCounters extends SimpleDatagram<PortCountersBean> {

    private static final int IMAGEID_OFFSET = 24;

    private static final int IMAGEOFFSET_OFFSET = IMAGEID_OFFSET + 8;

    public PortCounters() {
        super(248);
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
    public PortCountersBean toObject() {
        buffer.clear();
        PortCountersBean bean = new PortCountersBean();
        bean.setNodeLid(buffer.getInt());
        bean.setPortNumber(buffer.get());
        buffer.position(8);
        bean.setFlags(buffer.getInt());
        buffer.position(IMAGEID_OFFSET);
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt(),
                buffer.getInt()));
        bean.setPortXmitData(buffer.getLong());
        bean.setPortRcvData(buffer.getLong());
        bean.setPortXmitPkts(buffer.getLong());
        bean.setPortRcvPkts(buffer.getLong());
        bean.setPortMulticastXmitPkts(buffer.getLong());
        bean.setPortMulticastRcvPkts(buffer.getLong());
        bean.setLocalLinkIntegrityErrors(buffer.getLong());
        bean.setFmConfigErrors(buffer.getLong());
        bean.setPortRcvErrors(buffer.getLong());
        bean.setExcessiveBufferOverruns(buffer.getLong());
        bean.setPortRcvConstraintErrors(buffer.getLong());
        bean.setPortRcvSwitchRelayErrors(buffer.getLong());
        bean.setPortXmitDiscards(buffer.getLong());
        bean.setPortXmitConstraintErrors(buffer.getLong());
        bean.setPortRcvRemotePhysicalErrors(buffer.getLong());
        bean.setSwPortCongestion(buffer.getLong());
        bean.setPortXmitWait(buffer.getLong());
        bean.setPortRcvFECN(buffer.getLong());
        bean.setPortRcvBECN(buffer.getLong());
        bean.setPortXmitTimeCong(buffer.getLong());
        bean.setPortXmitWastedBW(buffer.getLong());
        bean.setPortXmitWaitData(buffer.getLong());
        bean.setPortRcvBubble(buffer.getLong());
        bean.setPortMarkFECN(buffer.getLong());
        bean.setLinkErrorRecovery(buffer.getInt());
        bean.setLinkDowned(buffer.getInt());
        bean.setUncorrectableErrors(buffer.get());
        byte byteVal = buffer.get();
        bean.setNumLanesDown((byte) (byteVal & 0xf0));
        bean.setLinkQualityIndicator((byte) (byteVal & 0x07));
        return bean;
    }

}
