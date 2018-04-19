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

import com.intel.stl.api.subnet.PortDownReasonBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.fecdriver.messages.adapter.ComposedDatagram;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h v1.100
 * 
 * <pre>
 * typedef struct {
 * 	struct {
 * 		uint32	EndPortLID;				
 * 		uint8	PortNum;
 * 		uint8  Reserved;	
 * 	} PACK_SUFFIX RID;
 * 	
 * 	uint16		Reserved;	
 * 
 * 	STL_PORT_INFO PortInfo;
 * 	STL_LINKDOWN_REASON LinkDownReasons[STL_NUM_LINKDOWN_REASONS];
 * } PACK_SUFFIX STL_PORTINFO_RECORD;
 * 
 * #define STL_NUM_LINKDOWN_REASONS 8
 * typedef struct {
 *     uint8 Reserved[6];
 *     uint8 NeighborLinkDownReason;
 *     uint8 LinkDownReason;
 *     uint64 Timestamp;
 * } PACK_SUFFIX STL_LINKDOWN_REASON;
 * </pre>
 * 
 */
public class PortInfoRecord extends ComposedDatagram<PortRecordBean> {
    private PortInfoHeader header = null;

    private SimpleDatagram<Void> reserved = null;

    private PortInfo portInfo = null;

    private PortDownReason[] linkDownReasons = null;

    public PortInfoRecord() {
        header = new PortInfoHeader();
        addDatagram(header);
        reserved = new SimpleDatagram<Void>(2);
        addDatagram(reserved);
        portInfo = new PortInfo();
        addDatagram(portInfo);
        linkDownReasons =
                new PortDownReason[SAConstants.STL_NUM_LINKDOWN_REASONS];
        for (int i = 0; i < linkDownReasons.length; i++) {
            linkDownReasons[i] = new PortDownReason();
            addDatagram(linkDownReasons[i]);
        }
    }

    /**
     * @return the header
     */
    public PortInfoHeader getHeader() {
        return header;
    }

    /**
     * @return the portInfo
     */
    public PortInfo getPortInfo() {
        return portInfo;
    }

    public PortDownReasonBean[] getLinkDownReasons() {
        PortDownReasonBean[] res =
                new PortDownReasonBean[linkDownReasons.length];
        for (int i = 0; i < linkDownReasons.length; i++) {
            res[i] = linkDownReasons[i].toObject();
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public PortRecordBean toObject() {
        PortRecordBean bean =
                new PortRecordBean(header.getEndPortLid(), header.getPortNum(),
                        portInfo.toObject());
        bean.setLinkDownReasons(getLinkDownReasons());
        return bean;
    }

}
