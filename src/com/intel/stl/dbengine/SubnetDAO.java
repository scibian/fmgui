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

package com.intel.stl.dbengine;

import java.util.List;
import java.util.Set;

import com.intel.stl.api.FMException;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.TopologyLinkRecord;
import com.intel.stl.datamanager.TopologyNodeRecord;
import com.intel.stl.datamanager.TopologyRecord;

public interface SubnetDAO {

    List<SubnetDescription> getSubnets();

    SubnetRecord getSubnet(String name);

    SubnetRecord getSubnet(long subnetId);

    SubnetDescription defineSubnet(SubnetDescription subnet);

    void updateSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException;

    void removeSubnet(long subnetId) throws SubnetDataNotFoundException;

    TopologyRecord saveTopology(String subnetName, List<NodeRecordBean> nodes,
            List<LinkRecordBean> links) throws SubnetDataNotFoundException;

    TopologyRecord copyTopology(SubnetRecord subnet,
            Set<TopologyNodeRecord> newNodes, Set<TopologyNodeRecord> updNodes,
            Set<TopologyLinkRecord> newLinks, Set<TopologyLinkRecord> updLinks);

    TopologyRecord getTopology(String subnetName)
            throws SubnetDataNotFoundException;

    List<NodeRecordBean> getNodes(String subnetName)
            throws SubnetDataNotFoundException;

    NodeRecordBean getNode(long guid) throws SubnetDataNotFoundException;

    NodeRecordBean getNode(String subnetName, long guid)
            throws SubnetDataNotFoundException;

    NodeRecordBean getNode(String subnetName, int lid)
            throws SubnetDataNotFoundException;

    NodeRecordBean getNodeByPortGUID(String subnetName, long portGuid)
            throws SubnetDataNotFoundException;

    LinkRecordBean getLinkBySource(String subnetName, int lid, short portNum)
            throws FMException;

    LinkRecordBean getLinkByDestination(String subnetName, int lid,
            short portNum) throws FMException;

    void insertNode(NodeRecordBean node);

    void updateNode(NodeRecordBean node) throws SubnetDataNotFoundException;

    List<LinkRecordBean> getLinks(String subnetName)
            throws SubnetDataNotFoundException;

    List<LinkRecordBean> getLinks(String subnetName, int lid)
            throws SubnetDataNotFoundException;

    TopologyNodeRecord getTopologyNodeRecord(long topologyId, int lid);

    List<TopologyLinkRecord> getTopologyLinkRecords(long topologyId,
            long nodeGuid);

}
