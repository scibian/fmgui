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

package com.intel.stl.fecdriver.messages.adapter.sa.trap;

import com.intel.stl.api.notice.TrapCapabilityBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 *  ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h v1.115
 *  
 *   typedef struct {
 * 4     uint32              Lid;
 * 8     uint32              CapabilityMask;
 * 10    uint16              CapabilityMask2;
 * 12    uint16              CapabilityMask3;
 * 14    STL_FIELDUNION4(u,16,
 *                          Reserved:13,
 *                          LinkSpeedEnabledChange:1,
 *                          LinkWidthEnabledChange:1,
 *                          NodeDescriptionChange:1);
 *  } PACK_SUFFIX STL_TRAP_CHANGE_CAPABILITY_DATA;
 * </pre>
 * 
 */
public class TrapCapability extends SimpleDatagram<TrapCapabilityBean> {
    public TrapCapability() {
        super(14);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.fecdriver.messages.adapter.SimpleDatagram#toObject()
     */
    @Override
    public TrapCapabilityBean toObject() {
        buffer.clear();
        TrapCapabilityBean bean = new TrapCapabilityBean();
        bean.setLid(buffer.getInt());
        bean.setCapabilityMask(buffer.getInt());
        bean.setCapabilityMask2(buffer.getShort());
        bean.setCapabilityMask3(buffer.getShort());
        short val = buffer.getShort();
        bean.setLinkSpeedEnabledChange((val & 0x04) == 0x04);
        bean.setLinkWidthEnabledChange((val & 0x02) == 0x02);
        bean.setNodeDescriptionChange((val & 0x01) == 0x01);
        return bean;
    }

}
