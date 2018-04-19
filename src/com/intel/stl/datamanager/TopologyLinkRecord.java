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

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.intel.stl.api.subnet.LinkRecordBean;

@Entity
@Table(name = "TOPOLOGIES_LINKS")
public class TopologyLinkRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TopologyLinkId id = new TopologyLinkId();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "topologyId", insertable = false, updatable = false)
    private TopologyRecord topology;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "fromNodeGUID", insertable = false, updatable = false)
    private NodeRecord fromNode;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "toNodeGUID", insertable = false, updatable = false)
    private NodeRecord toNode;

    private boolean active = true;

    private boolean fromPortActive = true;

    public TopologyLinkRecord() {
    }

    public TopologyLinkRecord(LinkRecordBean link) {
        id.setSourcePort(link.getFromPortIndex());
        id.setTargetPort(link.getToPortIndex());
    }

    public TopologyLinkId getId() {
        return id;
    }

    public void setId(TopologyLinkId id) {
        this.id = id;
    }

    public TopologyRecord getTopology() {
        return topology;
    }

    public void setTopology(TopologyRecord topology) {
        this.topology = topology;
        this.id.setLinkTopology(topology.getId());
    }

    public NodeRecord getFromNode() {
        return fromNode;
    }

    public void setFromNode(NodeRecord fromNode) {
        this.fromNode = fromNode;
        this.id.setSourceNode(fromNode.getNodeGUID());
    }

    public short getFromPort() {
        return this.id.getSourcePort();
    }

    public void setFromPort(short fromPort) {
        this.id.setSourcePort(fromPort);
    }

    public NodeRecord getToNode() {
        return toNode;
    }

    public void setToNode(NodeRecord toNode) {
        this.toNode = toNode;
        this.id.setTargetNode(toNode.getNodeGUID());
    }

    public short getToPort() {
        return this.id.getTargetPort();
    }

    public void setToPort(short toPort) {
        this.id.setTargetPort(toPort);
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * @return the fromPortActive
     */
    public boolean isFromPortActive() {
        return fromPortActive;
    }

    /**
     * @param fromPortActive
     *            the fromPortActive to set
     */
    public void setFromPortActive(boolean fromPortActive) {
        this.fromPortActive = fromPortActive;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (active ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
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
        TopologyLinkRecord other = (TopologyLinkRecord) obj;
        if (active != other.active) {
            return false;
        }
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TopologyLinkRecord [id=" + id + ", active=" + active
                + ", fromPortActive=" + fromPortActive + ", fromNode="
                + fromNode + ", toNode=" + toNode + "]";
    }

}
