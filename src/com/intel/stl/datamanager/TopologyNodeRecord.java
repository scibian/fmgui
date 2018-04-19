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
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "TOPOLOGIES_NODES", indexes = { @Index(name = "IDX_NODE_LID",
        columnList = "lid") })
public class TopologyNodeRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private TopologyNodeId id = new TopologyNodeId();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "topologyId", insertable = false, updatable = false)
    private TopologyRecord topology;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "nodeGUID", insertable = false, updatable = false)
    private NodeRecord node;

    @Transient
    private boolean lidChanged = false;

    private int lid;

    private boolean active = true;

    public TopologyNodeId getId() {
        return id;
    }

    public void setId(TopologyNodeId id) {
        this.id = id;
    }

    public TopologyRecord getTopology() {
        return topology;
    }

    public void setTopology(TopologyRecord topology) {
        this.topology = topology;
        id.setTopologyId(topology.getId());
    }

    public NodeRecord getNode() {
        node.getNode().setActive(active);
        // we join NodeRecord by guid. A node record can have a different lid.
        // So we set its current lid here
        node.getNode().setLid(lid);
        return node;
    }

    public void setNode(NodeRecord node) {
        this.node = node;
        this.id.setTopologyNode(node.getNodeGUID());
    }

    public boolean isLidChanged() {
        return lidChanged;
    }

    public void setLidChanged(boolean lidChanged) {
        this.lidChanged = lidChanged;
    }

    public int getLid() {
        return this.lid;
    }

    public void setLid(int lid) {
        this.lid = lid;
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
        TopologyNodeRecord other = (TopologyNodeRecord) obj;
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
        return "TopologyNodeRecord [id=" + id + ", lidChanged=" + lidChanged
                + ", lid=" + lid + ", active=" + active + ", node=" + node
                + "]";
    }

}
