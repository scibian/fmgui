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

import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * LinkRecord
 *
 * STL Differences:
 * 		LIDs lengthened
 * 		Reserved field added to preserve alignment.
 *
 * typedef struct _STL_LINK_RECORD {
 * 	struct {
 * 		uint32	FromLID;
 * 		uint8	FromPort; // for switch or HFI: port numnber
 * 	} PACK_SUFFIX RID;
 *
 * 	uint8		ToPort;   // for switch or HFI: port numnber
 *
 * 	uint16		Reserved;
 *
 * 	uint32		ToLID;
 *
 * } PACK_SUFFIX STL_LINK_RECORD;
 * </pre>
 *
 */
public class LinkRecord extends SimpleDatagram<LinkRecordBean> {
    public LinkRecord() {
        super(12);
    }

    public void setFromLID(int lid) {
        buffer.putInt(0, lid);
    }

    public int getFromLID() {
        return buffer.getInt(0);
    }

    public void setFromPort(byte port) {
        buffer.put(4, port);
    }

    public void setToPort(byte port) {
        buffer.put(5, port);
    }

    public void setToLid(int lid) {
        buffer.putInt(8, lid);
    }

    @Override
    public LinkRecordBean toObject() {
        buffer.clear();
        int fromLID = buffer.getInt();
        byte fromPortIndex = buffer.get();
        byte toPortIndex = buffer.get();
        buffer.position(8);
        int toLID = buffer.getInt();
        LinkRecordBean bean =
                new LinkRecordBean(fromLID, fromPortIndex, toLID, toPortIndex);
        return bean;
    }
}
