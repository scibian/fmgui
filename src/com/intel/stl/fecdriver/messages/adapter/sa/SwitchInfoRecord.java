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

import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.fecdriver.messages.adapter.ComposedDatagram;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h v1.92
 * 
 * <pre>
 * SwitchInfoRecord
 * 
 * STL Differences
 * 	Old LID/Reserved RID replaced with 32 bit LID.
 * 	Reserved added to align SwitchInfoData.
 * 
 * typedef struct {
 * 	struct {
 * 		uint32 	LID;
 * 	} PACK_SUFFIX RID;
 * 
 * 	uint32		Reserved;		
 * 
 * 	STL_SWITCH_INFO	SwitchInfoData; 	
 * } PACK_SUFFIX STL_SWITCHINFO_RECORD;
 * </pre>
 * 
 */
public class SwitchInfoRecord extends ComposedDatagram<SwitchRecordBean> {
    private SimpleDatagram<Void> header = null;

    private SwitchInfo switchInfo = null;

    public SwitchInfoRecord() {
        header = new SimpleDatagram<Void>(8);
        addDatagram(header);

        switchInfo = new SwitchInfo();
        addDatagram(switchInfo);
    }

    public void setLid(int lid) {
        header.getByteBuffer().putInt(0, lid);
    }

    public int getLid() {
        return header.getByteBuffer().getInt(0);
    }

    /**
     * @return the switchInfo
     */
    public SwitchInfo getSwitchInfo() {
        return switchInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public SwitchRecordBean toObject() {
        SwitchRecordBean bean =
                new SwitchRecordBean(getLid(), switchInfo.toObject());
        return bean;
    }

}
