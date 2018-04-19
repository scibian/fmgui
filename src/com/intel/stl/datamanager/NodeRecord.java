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

package com.intel.stl.datamanager;

import static javax.persistence.FetchType.LAZY;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;

@Entity
@Table(name = "NODES", indexes = { @Index(name = "IDX_NODE_PORTGUID",
        columnList = "portGUID") })
public class NodeRecord extends DatabaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long nodeGUID;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "nodeType")
    private NodeTypeRecord type = new NodeTypeRecord();

    private NodeRecordBean node;

    @OneToMany(fetch = LAZY, mappedBy = "node")
    private Set<TopologyNodeRecord> topologies;

    @OneToMany(fetch = LAZY, mappedBy = "fromNode")
    private Set<TopologyLinkRecord> outboundLinks;

    @OneToMany(fetch = LAZY, mappedBy = "toNode")
    private Set<TopologyLinkRecord> inboundLinks;

    public NodeRecord() {
    }

    public NodeRecord(NodeRecordBean node) {
        setNodeFields(node);
        this.node = node;
    }

    public long getNodeGUID() {
        return nodeGUID;
    }

    public void setNodeGUID(long nodeGUID) {
        this.nodeGUID = nodeGUID;
    }

    public NodeTypeRecord getType() {
        return type;
    }

    public void setType(NodeTypeRecord type) {
        this.type = type;
    }

    public NodeRecordBean getNode() {
        NodeInfoBean info = node.getNodeInfo();
        info.setNodeGUID(nodeGUID);
        info.setNodeTypeEnum(type.getNodeType());
        return node;
    }

    public void setNode(NodeRecordBean node) {
        setNodeFields(node);
        this.node = node;
    }

    public Set<TopologyNodeRecord> getTopologies() {
        return topologies;
    }

    public void setTopologies(Set<TopologyNodeRecord> topologies) {
        this.topologies = topologies;
    }

    public Set<TopologyLinkRecord> getOutboundLinks() {
        return outboundLinks;
    }

    public void setOutboundLinks(Set<TopologyLinkRecord> outboundLinks) {
        this.outboundLinks = outboundLinks;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (nodeGUID ^ (nodeGUID >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NodeRecord other = (NodeRecord) obj;
        if (nodeGUID != other.nodeGUID) {
            return false;
        }
        return true;
    }

    private void setNodeFields(NodeRecordBean node) {
        NodeInfoBean info = node.getNodeInfo();
        if (info == null) {
            // TODO Create message for this
            throw new IllegalArgumentException(
                    "No NodeInfoBean attached to NodeRecordBean");
        }
        this.nodeGUID = info.getNodeGUID();
        this.type.setId(info.getNodeType());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeRecord [nodeGUID=" + nodeGUID + ", node=" + getNode() + "]";
    }

}
