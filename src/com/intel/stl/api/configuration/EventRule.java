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

package com.intel.stl.api.configuration;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.subnet.SubnetDescription;

/**
 * Java bean for an Event Rule
 */
public class EventRule implements Serializable {

    // For serialization purposes (pre-populated rules file);
    static final long serialVersionUID = 1L;

    /**
     * String eventName
     */
    private String eventName = null;

    /**
     * Source of the event.
     */
    private List<String> eventSourceList = null;

    /**
     * String eventType
     */
    private EventType eventType = null;

    /**
     * String eventSeverity
     */
    private NoticeSeverity eventSeverity = null;

    /**
     * String eventAction
     */
    private List<EventRuleAction> eventActions = null;

    /**
     * boolean eventEnabled
     */
    private boolean eventEnabled = false;

    /**
     * Vector of Subnets, events enabled
     */
    private Set<SubnetDescription> eventSubnets = null;

    /**
     * Lid of the port the event was fired from.
     */
    private int lid = 0;

    /**
     * Port number of the port the event was fired from.
     */
    private short portNum = 0;

    /**
     * Command id associated with this Rule.
     */
    private int commandId = 0;

    /**
     * Message associated with this rule.
     */
    private String message = null;

    /**
     * EventRule constructor.
     */
    public EventRule() {
        eventSourceList = new Vector<String>();
    }

    public EventRule(EventType evtType, NoticeSeverity evtSeverity,
            List<EventRuleAction> evtActionList) {

        this.eventType = evtType;
        this.eventSeverity = evtSeverity;
        this.eventActions = evtActionList;
    }

    /**
     * 
     * Description: Copy constructor for the EventRule class.
     * 
     * @param eventRule
     */
    public EventRule(EventRule eventRule) {
        this(eventRule.getEventType(), eventRule.getEventSeverity(), eventRule
                .getEventActions());
    }

    public void setEventSourceVector(List<String> eventsource) {
        this.eventSourceList = eventsource;
    }

    /**
     * Sets the eventType for this EventRule.
     * 
     * @param eventType
     *            the eventType to set for this EventRule.
     */
    public void setEventType(EventType eventType, int command) {
        this.eventType = eventType;

        setCommandId(eventType, command);
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    /**
     * Gets the eventType for this EventRule.
     * 
     * @return the eventType for this EventRule.
     */
    public EventType getEventType() {
        return eventType;
    }

    /**
     * Sets the eventSeverity for this EventRule.
     * 
     * @param eventSeverity
     *            the eventSeverity to set for this EventRule.
     */
    public void setEventSeverity(NoticeSeverity eventSeverity) {
        this.eventSeverity = eventSeverity;
    }

    /**
     * Get the eventSeverity for this EventRule.
     * 
     * @return the eventSeverity for this EventRule.
     */
    public NoticeSeverity getEventSeverity() {
        return eventSeverity;
    }

    /**
     * Sets the eventActions for this EventRule.
     * 
     * @param eventAction
     *            the eventActions to set for this EventRule.
     */
    public void setEventActions(List<EventRuleAction> eventActions) {
        this.eventActions = eventActions;
    }

    /**
     * Gets the eventActions for this EventRule.
     * 
     * @return a vector of eventActions for this EventRule.
     */
    public List<EventRuleAction> getEventActions() {
        return eventActions;
    }

    /**
     * Sets the eventName for the EventRule
     * 
     * @param eventName
     *            the eventName to set for this EventRule.
     */
    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    /**
     * Gets the eventName for this EventRule.
     * 
     * @return the eventName for this EventRule.
     */
    public String getEventName() {
        return eventName;
    }

    /**
     * Sets the eventEnabled attribute for this eventRule.
     * 
     * @param enabled
     *            true if eventEnabled is to be enabled.
     */
    public void setEventEnabled(boolean enabled) {
        this.eventEnabled = enabled;
    }

    /**
     * Sets the eventEnabled attribute for this eventRule.
     * 
     * @param enabled
     *            true if eventEnabled is to be enabled.
     */
    // public void addSubnetEventsEnabled(String subnetName, boolean enabled) {
    // if (subnetEventsEnabled.containsKey(subnetName)) {
    // subnetEventsEnabled.remove(subnetName);
    // }
    // subnetEventsEnabled.put(subnetName, new Boolean(enabled));
    // }

    public void setEventSubnets(Set<SubnetDescription> eventSubnets) {
        this.eventSubnets = eventSubnets;
        // if (subnetEventsEnabled.containsKey(subnetName)) {
        // subnetEventsEnabled.remove(subnetName);
        // }
        // subnetEventsEnabled.put(subnetName, new Boolean(enabled));
    }

    public Set<SubnetDescription> getEventSubnets() {
        return eventSubnets;
    }

    /**
     * Returns if this EventRule is enabled
     * 
     * @return true if the EventRule is enabled.
     */
    public boolean isEventEnabled() {
        return eventEnabled;
    }

    /**
     * Returns if this EventRule is enabled
     * 
     * @return true if the EventRule is enabled.
     */
    // public boolean isSubnetEventEnabled(String subnetName) {
    // Boolean bool =(Boolean)subnetEventsEnabled.get(subnetName);
    // return bool.booleanValue();
    // }

    /**
     * Sets the commandID associated with this EventRule.
     * 
     * @param eventType
     *            the eventType to associate with this commandID.
     */
    private void setCommandId(EventType eventType, int command) {

        commandId = command;
        this.eventType = eventType;
    }

    public void setCommandId(int command) {
        this.commandId = command;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Gets the commandId associated with this EventRule.
     * 
     * @return commandId the commandID associated with this EventRule.
     */
    public int getCommandId() {
        return commandId;
    }

    /**
     * Sets the port LID associated with this EventRule.
     * 
     * @param lid
     *            the port LID to associate with this EventRule.
     */
    public void setLID(int lid) {
        this.lid = lid;

    }

    /**
     * Gets the port LID associated with this EventRule.
     * 
     * @return lid the port LID associated with this EventRule.
     */
    public int getLID() {
        return lid;
    }

    /**
     * Sets the port number associated with this EventRule.
     * 
     * @param portNum
     *            the port number to associate with this EventRule.
     */
    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }

    /**
     * Gets the port number associated with this EventRule.
     * 
     * @return portNum the port number associated with this EventRule.
     */
    public short getPortNum() {
        return portNum;
    }

    // /**
    // * Creates a message based on the eventType for this EventRule.
    // */
    // public void createMessage() {
    // log.debug(UILabels.STL50021_CREATE_MESSAGE_EVENT_TYPE.getDescription(eventType));
    // if (eventType.equals(SM_TOPO_CHANGE)) {
    // message = SM_TOPO_CHANGE;
    // } else if (eventType.equals(SM_CONNECTION_ESTABLISH)) {
    // message = SM_CONNECTION_ESTABLISH;
    // } else if (eventType.equals(SM_CONNECTION_LOST)) {
    // message = SM_CONNECTION_LOST;
    // } else if (eventType.equals(FE_CONNECTION_LOST)) {
    // message = FE_CONNECTION_LOST;
    // } else if (eventType.equals(PORT_ACTIVE)) {
    // message = UILabels.STL50022_PORT_ACTIVE.getDescription(getPortNum(),
    // Integer.toHexString(getLID()));
    // } else if (eventType.equals(PORT_INACTIVE)) {
    // message = UILabels.STL50023_PORT_INACTIVE.getDescription(getPortNum(),
    // Integer.toHexString(getLID()));
    // }
    // log.debug(UILabels.STL50024_CREATE_MESSAGE.getDescription(message));
    // }

    /**
     * Gets the message associated with this EventRule.
     * 
     * @return message the message associated with this EventRule.
     */
    public String getMessage() {
        return message;
    }

    /**
     * Gets the eventSource associated with this EventRule.
     * 
     * @return eventSource the eventSource associated with this EventRule.
     */
    public List<String> getEventSourceVector() {
        return eventSourceList;
    }

    public void addEventSource(String eventSource) {
        this.eventSourceList.add(eventSource);
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
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof EventRule)) {
            return false;
        }
        final EventRule other = (EventRule) obj;
        if (eventName == null) {
            if (other.eventName != null) {
                return false;
            }
        } else if (!eventName.equalsIgnoreCase(other.eventName)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {

        String actions = new String();
        Iterator<EventRuleAction> it = eventActions.iterator();

        while (it.hasNext()) {
            actions += it.next().name();

            if (it.hasNext()) {
                actions += ",";
            }
        }

        return "EventRule [type=" + eventType.name() + ", class="
                + eventType.getEventClass().name() + ", severity="
                + eventSeverity.name() + ", actions=" + actions + "]";
    }
}
