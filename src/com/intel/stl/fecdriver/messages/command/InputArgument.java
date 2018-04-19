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
package com.intel.stl.fecdriver.messages.command;

import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.fecdriver.messages.adapter.sa.GID;

/**
 */
public class InputArgument {
    public enum InputType {
        InputTypeNoInput, /* No input. returns all records */
        InputTypeNodeType, /* NodeType */
        InputTypeSystemImageGuid, /* Guid - a system Image guid */
        InputTypeNodeGuid, /* Guid - a node guid */
        InputTypePortGuid, /* Guid - a port guid */
        InputTypePortGid, /* Gid - a gid associated with a port */
        InputTypeMcGid, /* Gid - a multicast gid */
        InputTypePortGuidPair, /* GuidPair - a pair of port guids */
        InputTypeGidPair, /* GidPair - a pair of gids */
        InputTypePathRecord, /* PathRecord */
        // #ifdef IB_STACK_OPENIB
        // InputTypePathRecordNetworkOrder,/* PathRecord in network byte order*/
        // #endif
        InputTypeLid, /* Lid - a lid in the local subnet */
        InputTypePKey, /* PKey - a pkey */
        InputTypeSL, /* SL - a service level */
        InputTypeIndex, /* Index - an index associated with a VF */
        InputTypeServiceId, /* ServiceId */
        InputTypeNodeDesc, /* NodeDesc - a node description/name */
        InputTypeServiceRecord, /*
                                 * ServiceRecordValue - complete SA
                                 * SERVICE_RECORD and component mask
                                 */
        InputTypeMcMemberRecord, /*
                                  * McMemberRecordValue - complete SA
                                  * MCMEMBER_RECORD and component mask
                                  */
        InputTypePortGuidList, /* GuidList - a list of port guids */
        InputTypeGidList, /* GidList - a list of gids */
        InputTypeMultiPathRecord, /* MultiPathRecord */

        InputTypeImageId, /*
                           * ImageId - may be used with groupInfo, groupConfig,
                           * portCounters (delta)
                           */
        InputTypeGroupName, /* GroupName - group name for groupInfo query */
        InputTypeVFName, /* VFName - vFabric name for vfInfo query */
        InputTypeLidPortNumber, /*
                                 * lid of node for portCounters query, port
                                 * number for portCounters query
                                 */
        InputTypeVFNamePort,
        InputTypeFocus, /*
                         * Argument for getting sorted list of ports using
                         * utilization or error values (from group buckets)
                         */
        InputTypeVFNameFocus
    }

    /**
     * @return the type
     */
    public InputType getType() {
        return InputType.InputTypeNoInput;
    }

    public int getLid() {
        throw new UnsupportedOperationException();
    }

    public String getNodeDesc() {
        throw new UnsupportedOperationException();
    }

    public long getNodeGuid() {
        throw new UnsupportedOperationException();
    }

    public NodeType getNodeType() {
        throw new UnsupportedOperationException();
    }

    public long getPortGuid() {
        throw new UnsupportedOperationException();
    }

    public long getSystemImageGuid() {
        throw new UnsupportedOperationException();
    }

    public short getPKey() {
        throw new UnsupportedOperationException();
    }

    public byte getSL() {
        throw new UnsupportedOperationException();
    }

    public long getServiceId() {
        throw new UnsupportedOperationException();
    }

    public long getSourcePortGuid() {
        throw new UnsupportedOperationException();
    }

    public long getDestPortGuid() {
        throw new UnsupportedOperationException();
    }

    public GID<?> getSourceGid() {
        throw new UnsupportedOperationException();
    }

    public GID<?> getDestGid() {
        throw new UnsupportedOperationException();
    }

    public GID<?> getPortGid() {
        throw new UnsupportedOperationException();
    }

    public String getGroupName() {
        throw new UnsupportedOperationException();
    }

    public String getVfName() {
        throw new UnsupportedOperationException();
    }

    public ImageIdBean getImageId() {
        throw new UnsupportedOperationException();
    }

    public byte getPortNumber() {
        throw new UnsupportedOperationException();
    }

    public boolean isDelta() {
        throw new UnsupportedOperationException();
    }

    public int getSelect() {
        throw new UnsupportedOperationException();
    }

    public int getStart() {
        throw new UnsupportedOperationException();
    }

    public int getRange() {
        throw new UnsupportedOperationException();
    }
}
