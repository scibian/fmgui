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
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "TOPOLOGIES")
public class TopologyRecord extends DatabaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private long id;

    private long numNodes;

    private long numCAs;

    private long numSwitches;

    private int numRouters;

    private int numUnknown;

    @OneToMany(fetch = LAZY, mappedBy = "topology")
    private Set<TopologyNodeRecord> nodes;

    @OneToMany(fetch = LAZY, mappedBy = "topology")
    private Set<TopologyLinkRecord> links;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getNumNodes() {
        return numNodes;
    }

    public void setNumNodes(long numNodes) {
        this.numNodes = numNodes;
    }

    public long getNumCAs() {
        return numCAs;
    }

    public void setNumCAs(long numCAs) {
        this.numCAs = numCAs;
    }

    public long getNumSwitches() {
        return numSwitches;
    }

    public void setNumSwitches(long numSwitches) {
        this.numSwitches = numSwitches;
    }

    public int getNumRouters() {
        return numRouters;
    }

    public void setNumRouters(int numRouters) {
        this.numRouters = numRouters;
    }

    public int getNumUnknown() {
        return numUnknown;
    }

    public void setNumUnknown(int numUnknown) {
        this.numUnknown = numUnknown;
    }

    public Set<TopologyNodeRecord> getNodes() {
        return nodes;
    }

    public void setNodes(Set<TopologyNodeRecord> nodes) {
        this.nodes = nodes;
    }

    public Set<TopologyLinkRecord> getLinks() {
        return links;
    }

    public void setLinks(Set<TopologyLinkRecord> links) {
        this.links = links;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        TopologyRecord other = (TopologyRecord) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
