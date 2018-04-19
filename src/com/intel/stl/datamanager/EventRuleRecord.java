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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.subnet.SubnetDescription;

@Entity
@Table(name = "EVENT_RULES")
public class EventRuleRecord extends DatabaseRecord implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column(length = 32)
    private String eventName;

    private EventRule eventRule;

    @ManyToMany
    @JoinTable(name = "EVENT_RULES_SUBNETS", joinColumns = { @JoinColumn(
            name = "eventName", referencedColumnName = "eventName") },
            inverseJoinColumns = { @JoinColumn(name = "subnetId",
                    referencedColumnName = "subnetId") })
    private Set<SubnetRecord> eventSubnets;

    @ManyToMany
    @JoinTable(name = "EVENT_RULES_ACTIONS", joinColumns = { @JoinColumn(
            name = "eventName", referencedColumnName = "eventName") },
            inverseJoinColumns = { @JoinColumn(name = "eventAction",
                    referencedColumnName = "id") })
    private Set<EventActionRecord> eventActions;

    public EventRuleRecord() {
    }

    public EventRuleRecord(EventRule eventRule) {
        this.eventRule = eventRule;
        this.eventName = eventRule.getEventName();
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public EventRule getEventRule() {
        eventRule.setEventName(eventName);
        Set<SubnetRecord> subnets = getEventSubnets();
        Set<SubnetDescription> eventSubnets = new HashSet<SubnetDescription>();
        if (subnets != null) {
            for (SubnetRecord subnet : subnets) {
                eventSubnets.add(subnet.getSubnetDescription());
            }
        }
        eventRule.setEventSubnets(eventSubnets);
        Set<EventActionRecord> actions = getEventActions();
        List<EventRuleAction> eventActions = new ArrayList<EventRuleAction>();
        if (actions != null) {
            for (EventActionRecord action : actions) {
                eventActions.add(action.getAction());
            }
        }
        eventRule.setEventActions(eventActions);
        return eventRule;
    }

    public void setEventRule(EventRule eventRule) {
        this.eventRule = eventRule;
    }

    public Set<SubnetRecord> getEventSubnets() {
        return eventSubnets;
    }

    public void setEventSubnets(Set<SubnetRecord> eventSubnets) {
        this.eventSubnets = eventSubnets;
    }

    public Set<EventActionRecord> getEventActions() {
        return eventActions;
    }

    public void setEventActions(Set<EventActionRecord> eventActions) {
        this.eventActions = eventActions;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime
                        * result
                        + ((eventName == null) ? 0 : eventName.toLowerCase()
                                .hashCode());
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
        EventRuleRecord other = (EventRuleRecord) obj;
        if (eventName == null) {
            if (other.eventName != null)
                return false;
        } else if (!eventName.equalsIgnoreCase(other.eventName))
            return false;
        return true;
    }
}
