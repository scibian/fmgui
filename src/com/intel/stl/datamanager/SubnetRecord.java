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

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.TableGenerator;

import com.intel.stl.api.subnet.SubnetDescription;

@Entity
@Table(name = "SUBNETS", indexes = { @Index(name = "IDX_SUBNET_NAME",
        columnList = "uniqueName", unique = true) })
@Cacheable(true)
public class SubnetRecord extends DatabaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @TableGenerator(name = "TABLE_GEN", pkColumnValue = "SUBNET_SEQ",
            allocationSize = 1, initialValue = 1)
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "TABLE_GEN")
    @Column(name = "subnetId")
    private long id;

    @Column(name = "uniqueName", length = 80)
    private String uniqueName;

    private SubnetDescription subnetDescription;

    @ManyToMany(mappedBy = "eventSubnets")
    private Set<EventRuleRecord> eventRules;

    @OneToOne(fetch = LAZY)
    private TopologyRecord topology;

    @OneToMany(fetch = LAZY, mappedBy = "subnet")
    private Set<GroupConfigRecord> groups;

    @OneToMany(fetch = LAZY, mappedBy = "subnet")
    private Set<ImageInfoRecord> images;

    public SubnetRecord() {
    }

    public SubnetRecord(SubnetDescription subnetDescription) {
        this.id = subnetDescription.getSubnetId();
        this.subnetDescription = subnetDescription;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueName() {
        return uniqueName;
    }

    /**
     * 
     * <i>Description:</i> sets the value of the unique name. Column uniqueName
     * is used for a unique index in the database. It's built from the
     * Subnet.name but it adds a character at the beginning to indicate whether
     * the subnet is in use ("1") or logically deleted ("0").
     * 
     * @param uniqueName
     *            the unique name according to the rules described above.
     */
    public void setUniqueName(String uniqueName) {
        this.uniqueName = uniqueName;
    }

    public SubnetDescription getSubnetDescription() {
        subnetDescription.setSubnetId(id);
        return subnetDescription;
    }

    public void setSubnetDescription(SubnetDescription subnetDescription) {
        this.subnetDescription = subnetDescription;
    }

    public Set<EventRuleRecord> getEventRules() {
        return eventRules;
    }

    public void setEventRules(Set<EventRuleRecord> eventRules) {
        this.eventRules = eventRules;
    }

    public TopologyRecord getTopology() {
        return topology;
    }

    public void setTopology(TopologyRecord topology) {
        this.topology = topology;
    }

    public Set<GroupConfigRecord> getGroups() {
        return groups;
    }

    public void setGroups(Set<GroupConfigRecord> groups) {
        this.groups = groups;
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
        SubnetRecord other = (SubnetRecord) obj;
        if (id != other.id)
            return false;
        return true;
    }
}
