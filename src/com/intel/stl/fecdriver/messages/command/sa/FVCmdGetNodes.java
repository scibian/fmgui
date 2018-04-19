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

import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.fecdriver.MultipleResponseCommand;
import com.intel.stl.fecdriver.messages.adapter.CommonMad;
import com.intel.stl.fecdriver.messages.adapter.sa.NodeRecord;
import com.intel.stl.fecdriver.messages.adapter.sa.SAHeader;
import com.intel.stl.fecdriver.messages.command.InputArgument;
import com.intel.stl.fecdriver.messages.response.sa.FVRspGetNode;

public class FVCmdGetNodes extends SACommand<NodeRecord, NodeRecordBean>
        implements MultipleResponseCommand<NodeRecordBean, FVRspGetNode> {
    /**
     * FVCmdGetNode Constructor. Creates and instance of the Command GetNode and
     * set's its Fabric Response.
     */
    public FVCmdGetNodes() {
        setResponse(new FVRspGetNode());
        setInput(new InputArgument());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vieo.fv.message.command.sa.SACommand#fillCommonMad(com.vieo.fv.resource
     * .stl.data.CommonMad)
     */
    @Override
    protected void fillCommonMad(CommonMad comm) {
        super.fillCommonMad(comm);
        comm.setAttributeID(SAConstants.STL_SA_ATTR_NODE_RECORD);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.message.command.sa.SACommand#buildRecord()
     */
    @Override
    protected NodeRecord buildRecord() {
        NodeRecord rec = new NodeRecord();
        rec.build(true);
        return rec;
    }

    @Override
    protected void fillInput(SAHeader header, NodeRecord record) {
        InputArgument input = getInput();

        switch (input.getType()) {
            case InputTypeLid:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_LID);
                record.setLid(input.getLid());
                break;
            case InputTypeNodeDesc:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_NODEDESC);
                record.getNodeDescription().setDescription(input.getNodeDesc());
                break;
            case InputTypeNodeGuid:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_NODEGUID);
                record.getNodeInfo().setNodeGUID(input.getNodeGuid());
                break;
            case InputTypeNodeType:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_NODETYPE);
                record.getNodeInfo().setNodeType(
                        (byte) input.getNodeType().ordinal());
                break;
            case InputTypeNoInput:
                header.setComponentMask(0);
                break;
            case InputTypePortGuid:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_PORTGUID);
                record.getNodeInfo().setPortGUID(input.getPortGuid());
                break;
            case InputTypeSystemImageGuid:
                header.setComponentMask(SAConstants.STL_NODE_RECORD_COMP_SYSIMAGEGUID);
                record.getNodeInfo().setSystemImageGUID(
                        input.getSystemImageGuid());
                break;
            default:
                throw new IllegalArgumentException("Unsupported input type "
                        + input.getType());
        }
    }

}
