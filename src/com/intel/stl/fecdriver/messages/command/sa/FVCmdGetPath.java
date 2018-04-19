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

import com.intel.stl.api.subnet.PathRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.common.Constants;
import com.intel.stl.fecdriver.MultipleResponseCommand;
import com.intel.stl.fecdriver.messages.adapter.CommonMad;
import com.intel.stl.fecdriver.messages.adapter.sa.PathRecord;
import com.intel.stl.fecdriver.messages.adapter.sa.SAHeader;
import com.intel.stl.fecdriver.messages.command.InputArgument;
import com.intel.stl.fecdriver.messages.response.sa.FVRspGetPath;

/**
 */
public class FVCmdGetPath extends SACommand<PathRecord, PathRecordBean>
        implements MultipleResponseCommand<PathRecordBean, FVRspGetPath> {

    /**
     * @param command
     */
    public FVCmdGetPath() {
        setResponse(new FVRspGetPath());
        setInput(new InputArgument());
    }

    public FVCmdGetPath(InputArgument input) {
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
        comm.setBaseVersion(Constants.IB_BASE_VERSION);
        comm.setClassVersion(SAConstants.IB_SUBN_ADM_CLASS_VERSION);
        comm.setAttributeID(SAConstants.STL_SA_ATTR_PATH_RECORD);
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
        // case InputTypeNoInput:
        // mask = SAConstants.IB_PATH_RECORD_COMP_SGID |
        // SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE |
        // SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
        // header.setComponentMask(mask);
        // record.setReversible(true);
        // record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
        // break;
            case InputTypePKey:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_PKEY
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getSourceGid());
                record.setPKey(input.getPKey());
                break;
            case InputTypeSL:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_SL
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getSourceGid());
                record.setSL(input.getSL());
                break;
            case InputTypeServiceId:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_SERVICEID
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getSourceGid());
                record.setServiceId(input.getServiceId());
                break;
            // case InputTypePortGuidPair:
            // mask = SAConstants.IB_PATH_RECORD_COMP_SGID |
            // SAConstants.IB_PATH_RECORD_COMP_DGID |
            // SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE |
            // SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
            // header.setComponentMask(mask);
            // record.setReversible(true);
            // record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
            // GID.Global sgid = new GID.Global(input.getSourcePortGuid());
            // record.setSGID(sgid);
            // GID.Global dgid = new GID.Global(input.getDestPortGuid());
            // record.setDGID(dgid);
            // break;
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
            // case InputTypePortGuid:
            // mask = SAConstants.IB_PATH_RECORD_COMP_SGID |
            // SAConstants.IB_PATH_RECORD_COMP_DGID |
            // SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE |
            // SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
            // header.setComponentMask(mask);
            // record.setReversible(true);
            // record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
            // dgid = new GID.Global(input.getPortGuid());
            // record.setDGID(dgid);
            // break;
            case InputTypePortGid:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getPortGid());
                break;
            case InputTypeLid:
                mask =
                        SAConstants.IB_PATH_RECORD_COMP_SGID
                                | SAConstants.IB_PATH_RECORD_COMP_DLID
                                | SAConstants.IB_PATH_RECORD_COMP_REVERSIBLE
                                | SAConstants.IB_PATH_RECORD_COMP_NUMBPATH;
                header.setComponentMask(mask);
                record.setReversible(true);
                record.setNumPath(SAConstants.PATHRECORD_NUMBPATH);
                record.setSGID(input.getSourceGid());
                record.setDLidLow((short) (input.getLid() & 0xffff));
                break;
            default:
                throw new IllegalArgumentException("Unsupported input type "
                        + input.getType());
        }
    }
}
