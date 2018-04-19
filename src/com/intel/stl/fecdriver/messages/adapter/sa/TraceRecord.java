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

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.TraceRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sa.h v1.92
 *
 * <pre>
 *  TraceRecord
 *
 *  STL Differences
 *
 *  	GIDPrefix deleted.
 *  	EntryPort, ExitPort moved for alignment.
 *  		Reserved2 added to word/qword-align NodeID.
 *
 *  typedef struct {
 * [2] 	uint16		IDGeneration;
 * [3] 	uint8		Reserved;
 * [4] 	uint8		NodeType;
 * [5] 	uint8		EntryPort;
 * [6] 	uint8		ExitPort;
 * [8] 	uint16		Reserved2;
 *
 * [16] 	uint64		NodeID;
 * [24] 	uint64		ChassisID;
 * [32] 	uint64		EntryPortID;
 * [40] 	uint64		ExitPortID;
 *
 *  } PACK_SUFFIX STL_TRACE_RECORD;
 * </pre>
 *
 */
public class TraceRecord extends SimpleDatagram<TraceRecordBean> {
    public static final long STL_TRACE_RECORD_COMP_ENCRYPT_MASK = 0x55555555;

    public TraceRecord() {
        super(40);
    }

    public void setIDGeneration(short iDGeneration) {
        buffer.putShort(0, iDGeneration);
    }

    public void setNodeType(NodeType type) {
        buffer.put(3, (byte) type.ordinal());
    }

    public void setEntryPort(byte port) {
        buffer.put(4, port);
    }

    public void setExitPort(byte port) {
        buffer.put(5, port);
    }

    public void setNodeId(long id) {
        buffer.putLong(8, id);
    }

    public void setChassisId(long id) {
        buffer.putLong(16, id);
    }

    public void setEntryPortId(long id) {
        buffer.putLong(24, id);
    }

    public void setExitPortId(long id) {
        buffer.putLong(32, id);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public TraceRecordBean toObject() {
        buffer.clear();
        short idGeneration = buffer.getShort();
        buffer.position(3);
        byte nodeType = buffer.get();
        byte entryPort = buffer.get();
        byte exitPort = buffer.get();
        buffer.position(8);
        long nodeId = buffer.getLong() ^ STL_TRACE_RECORD_COMP_ENCRYPT_MASK;
        long chassisId = buffer.getLong() ^ STL_TRACE_RECORD_COMP_ENCRYPT_MASK;
        long entryPortId =
                buffer.getLong() ^ STL_TRACE_RECORD_COMP_ENCRYPT_MASK;
        long exitPortId = buffer.getLong() ^ STL_TRACE_RECORD_COMP_ENCRYPT_MASK;

        TraceRecordBean bean =
                new TraceRecordBean(idGeneration, nodeType, entryPort, exitPort,
                        nodeId, chassisId, entryPortId, exitPortId);
        return bean;
    }

}
