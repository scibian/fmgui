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
package com.intel.stl.fecdriver.messages.command.sa;

import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.TraceRecordBean;
import com.intel.stl.fecdriver.MultipleResponseCommand;
import com.intel.stl.fecdriver.messages.adapter.CommonMad;
import com.intel.stl.fecdriver.messages.adapter.sa.PathRecord;
import com.intel.stl.fecdriver.messages.adapter.sa.SAHeader;
import com.intel.stl.fecdriver.messages.command.InputArgument;
import com.intel.stl.fecdriver.messages.response.sa.FVRspGetTrace;

/**
 */
public class FVCmdGetTrace extends SACommand<PathRecord, TraceRecordBean>
        implements MultipleResponseCommand<TraceRecordBean, FVRspGetTrace> {

    /**
     * @param command
     */
    public FVCmdGetTrace() {
        setResponse(new FVRspGetTrace());
        setInput(new InputArgument());
    }

    public FVCmdGetTrace(InputArgument input) {
        this();
        setInput(input);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.hpc.stl.message.command.sa.SACommand#fillCommonMad(com.intel
     * .hpc.stl.resourceadapter.data.CommonMad)
     */
    @Override
    protected void fillCommonMad(CommonMad comm) {
        super.fillCommonMad(comm);
        comm.setAttributeID(SAConstants.STL_SA_ATTR_TRACE_RECORD);
        comm.setMethod(SAConstants.SUBN_ADM_GETTRACETABLE);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.message.command.sa.SACommand#buildRecord()
     */
    @Override
    protected PathRecord buildRecord() {
        PathRecord rec = new PathRecord();
        rec.build(true);
        return rec;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.hpc.stl.message.command.sa.SACommand#fillInput(com.intel.hpc
     * .stl.resourceadapter.data.sa.SAHeader,
     * com.intel.hpc.stl.resourceadapter.data.IDatagram)
     */
    @Override
    protected void fillInput(SAHeader header, PathRecord record) {
        InputArgument input = getInput();

        long mask = 0;
        switch (input.getType()) {
            case InputTypeGidPair:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_DGID
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getSourceGid());
                record.setDGID(input.getDestGid());
                break;
            default:
                throw new IllegalArgumentException("Unsupported input type "
                        + input.getType());
        }
    }

}
