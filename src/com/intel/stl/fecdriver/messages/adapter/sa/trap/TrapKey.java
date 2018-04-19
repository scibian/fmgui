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

import com.intel.stl.api.notice.TrapKeyBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;
import com.intel.stl.fecdriver.messages.adapter.sa.GID;

/**
 * <pre>
 * ref:/ALL_EMB/IbAcess/Common/Inc/stl_sm.h v1.115
 * 
 * typedef struct {
 *     uint32      Lid1;
 *     uint32      Lid2;
 *     //  8 bytes
 *     uint32      Key;    // pkey or qkey
 *     STL_FIELDUNION2(u,8,
 *                 SL:5,
 *                 Reserved:3);
 *     uint8       Reserved[3];
 *     //  16 bytes 
 *     IB_GID      Gid1;
 *     //  32 bytes 
 *     IB_GID      Gid2;
 *     //  48 bytes 
 *     STL_FIELDUNION2(qp1,32,
 *                 Reserved:8,
 *                 qp:24);
 *     //  52 bytes 
 *     STL_FIELDUNION2(qp2,32,
 *                 Reserved:8,
 *                 qp:24);
 *     //  56 bytes 
 * } PACK_SUFFIX STL_TRAP_BAD_KEY_DATA;
 *     
 * #define STL_TRAP_BAD_P_KEY_DATA STL_TRAP_BAD_KEY_DATA
 * #define STL_TRAP_BAD_Q_KEY_DATA STL_TRAP_BAD_KEY_DATA
 * </pre>
 */
public class TrapKey extends SimpleDatagram<TrapKeyBean> {

    public TrapKey() {
        super(56);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.fecdriver.messages.adapter.SimpleDatagram#toObject()
     */
    @Override
    public TrapKeyBean toObject() {
        buffer.clear();
        TrapKeyBean bean = new TrapKeyBean();
        bean.setLid1(buffer.getInt());
        bean.setLid2(buffer.getInt());
        bean.setKey(buffer.getInt());
        byte byteVal = buffer.get();
        bean.setSl((byte) ((byteVal >>> 3) & 0x1f));
        GID.Global gid1 = new GID.Global();
        gid1.wrap(buffer.array(), buffer.arrayOffset() + 16);
        bean.setGid1(gid1.toObject());
        GID.Global gid2 = new GID.Global();
        gid2.wrap(buffer.array(), buffer.arrayOffset() + 32);
        bean.setGid1(gid2.toObject());
        buffer.position(48);
        int intVal = buffer.getInt();
        bean.setQp1(intVal & 0xfff);
        intVal = buffer.getInt();
        bean.setQp2(intVal & 0xfff);
        return bean;
    }

}
