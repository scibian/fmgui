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

import com.intel.stl.api.notice.TrapMKeyBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 * typedef struct {
 *     uint32      Lid;
 *     uint32      DRSLid;
 *     //  8 bytes
 *     uint8       Method;
 *     STL_FIELDUNION3(u,8,
 *                 DRNotice:1,
 *                 DRPathTruncated:1,
 *                 DRHopCount:6);
 *     uint16      AttributeID;
 *     //  12 bytes
 *     uint32      AttributeModifier;
 *     //  16 bytes
 *     uint64      MKey;
 *     //  24 bytes
 *     uint8       DRReturnPath[30]; // We can make this longer....
 *     //  54 bytes
 * } PACK_SUFFIX STL_TRAP_BAD_M_KEY_DATA;
 * </pre>
 */
public class TrapMKey extends SimpleDatagram<TrapMKeyBean> {
    public TrapMKey() {
        super(54);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.messages.adapter.SimpleDatagram#toObject()
     */
    @Override
    public TrapMKeyBean toObject() {
        buffer.clear();
        TrapMKeyBean bean = new TrapMKeyBean();
        bean.setLid(buffer.getInt());
        bean.setDrSLid(buffer.getInt());
        bean.setMethod(buffer.get());
        byte byteVal = buffer.get();
        bean.setDrNotice((byteVal & 0x80) == 0x80);
        bean.setDrPathTruncated((byteVal & 0x40) == 0x40);
        bean.setDrHopCount((byte) (byteVal & 0x3f));
        bean.setAttributeID(buffer.getShort());
        bean.setAttributeModifier(buffer.getInt());
        bean.setMKey(buffer.getLong());
        byte[] byteArray = new byte[30];
        buffer.get(byteArray);
        bean.setDrReturnPath(byteArray);
        return bean;
    }

}
