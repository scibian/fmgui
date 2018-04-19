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
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OrderColumn;
import javax.persistence.Table;

import com.intel.stl.api.performance.GroupInfoBean;

@Entity
@Table(name = "GROUP_INFOS")
public class GroupInfoRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @EmbeddedId
    private GroupInfoId id = new GroupInfoId();

    @ManyToOne(fetch = LAZY)
    @JoinColumns({
            @JoinColumn(name = "subnetName", insertable = false,
                    updatable = false),
            @JoinColumn(name = "groupName", insertable = false,
                    updatable = false) })
    private GroupConfigRecord groupConfig;

    private GroupInfoBean groupInfo;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @CollectionTable(name = "GROUP_INFOS_INTERNAL_BWBUCKETS", joinColumns = {
            @JoinColumn(name = "subnetId"), @JoinColumn(name = "groupName"),
            @JoinColumn(name = "sweepTimestamp") })
    private List<Integer> internalBwBuckets;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @CollectionTable(name = "GROUP_INFOS_SEND_BWBUCKETS", joinColumns = {
            @JoinColumn(name = "subnetId"), @JoinColumn(name = "groupName"),
            @JoinColumn(name = "sweepTimestamp") })
    private List<Integer> sendBwBuckets;

    @ElementCollection(fetch = FetchType.EAGER)
    @OrderColumn
    @CollectionTable(name = "GROUP_INFOS_RECEIVE_BWBUCKETS", joinColumns = {
            @JoinColumn(name = "subnetId"), @JoinColumn(name = "groupName"),
            @JoinColumn(name = "sweepTimestamp") })
    private List<Integer> receiveBwBuckets;

    public GroupInfoRecord() {
    }

    public GroupInfoRecord(long subnetId, GroupInfoBean groupInfo) {
        GroupConfigId groupId = id.getGroupID();
        if (groupId == null) {
            groupId = new GroupConfigId();
            id.setGroupID(groupId);
        }
        groupId.setFabricId(subnetId);
        groupId.setSubnetGroup(groupInfo.getGroupName());
        this.id.setSweepTimestamp(groupInfo.getTimestamp());
        setGroupInfo(groupInfo);
    }

    public GroupInfoId getId() {
        return id;
    }

    public void setId(GroupInfoId id) {
        this.id = id;
    }

    public GroupConfigRecord getGroupConfig() {
        return groupConfig;
    }

    public void setGroupConfig(GroupConfigRecord groupConfig) {
        this.groupConfig = groupConfig;
    }

    public GroupInfoBean getGroupInfo() {
        if (groupInfo.getInternalUtilStats() != null) {
            groupInfo.getInternalUtilStats().setBwBuckets(internalBwBuckets);
        }
        if (groupInfo.getSendUtilStats() != null) {
            groupInfo.getSendUtilStats().setBwBuckets(sendBwBuckets);
        }
        if (groupInfo.getRecvUtilStats() != null) {
            groupInfo.getRecvUtilStats().setBwBuckets(receiveBwBuckets);
        }
        return groupInfo;
    }

    public void setGroupInfo(GroupInfoBean groupInfo) {
        if (groupInfo != null) {
            if (groupInfo.getInternalUtilStats() != null) {
                this.internalBwBuckets =
                        groupInfo.getInternalUtilStats().getBwBuckets();
            } else {
                this.internalBwBuckets = new ArrayList<Integer>();
            }
            if (groupInfo.getSendUtilStats() != null) {
                this.sendBwBuckets =
                        groupInfo.getSendUtilStats().getBwBuckets();
            } else {
                this.sendBwBuckets = new ArrayList<Integer>();
            }
            if (groupInfo.getRecvUtilStats() != null) {
                this.receiveBwBuckets =
                        groupInfo.getRecvUtilStats().getBwBuckets();
            } else {
                this.receiveBwBuckets = new ArrayList<Integer>();
            }
        } else {
            this.internalBwBuckets = new ArrayList<Integer>();
            this.sendBwBuckets = new ArrayList<Integer>();
            this.receiveBwBuckets = new ArrayList<Integer>();
        }
        this.groupInfo = groupInfo;
    }

    public List<Integer> getInternalBwBuckets() {
        return internalBwBuckets;
    }

    public void setInternalBwBuckets(List<Integer> internalBwBuckets) {
        this.internalBwBuckets = internalBwBuckets;
    }

    public List<Integer> getSendBwBuckets() {
        return sendBwBuckets;
    }

    public void setSendBwBuckets(List<Integer> sendBwBuckets) {
        this.sendBwBuckets = sendBwBuckets;
    }

    public List<Integer> getReceiveBwBuckets() {
        return receiveBwBuckets;
    }

    public void setReceiveBwBuckets(List<Integer> receiveBwBuckets) {
        this.receiveBwBuckets = receiveBwBuckets;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        GroupInfoRecord other = (GroupInfoRecord) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }
}
