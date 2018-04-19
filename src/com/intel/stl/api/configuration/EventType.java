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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.notice.TrapType;

/**
 * See STL spec table 20-2 for the default severity level
 */
public enum EventType {
    SM_TOPO_CHANGE(0, EventClass.SUBNET_EVENTS, NoticeSeverity.ERROR),
    PORT_ACTIVE(1, EventClass.SUBNET_EVENTS, NoticeSeverity.INFO),
    PORT_INACTIVE(2, EventClass.SUBNET_EVENTS, NoticeSeverity.WARNING),
    FE_CONNECTION_LOST(3, EventClass.MISCELLANEOUS_EVENTS,
            NoticeSeverity.CRITICAL),
    FE_CONNECTION_ESTABLISH(4, EventClass.MISCELLANEOUS_EVENTS,
            NoticeSeverity.INFO),
    SM_CONNECTION_LOST(5, EventClass.MISCELLANEOUS_EVENTS,
            NoticeSeverity.CRITICAL),
    SM_CONNECTION_ESTABLISH(6, EventClass.MISCELLANEOUS_EVENTS,
            NoticeSeverity.INFO);

    private final static Map<String, EventType> eventTypeMap =
            new HashMap<String, EventType>();
    static {
        for (EventType evtType : EventType.values()) {
            eventTypeMap.put(evtType.name(), evtType);
        }
    };

    private static Logger log = LoggerFactory.getLogger(EventType.class);

    private int id;

    private EventClass eventClass;

    private NoticeSeverity defaultSeverity;

    private EventType(int id, EventClass eventClass, NoticeSeverity severity) {
        this.id = id;
        this.eventClass = eventClass;
        this.defaultSeverity = severity;
    }

    public static EventType getEventType(TrapType type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case GID_NOW_IN_SERVICE:
                return EventType.PORT_ACTIVE;
            case GID_OUT_OF_SERVICE:
                return EventType.PORT_INACTIVE;
            case LINK_PORT_CHANGE_STATE:
                return EventType.SM_TOPO_CHANGE;
            case SM_CONNECTION_LOST:
                return EventType.SM_CONNECTION_LOST;
            case SM_CONNECTION_ESTABLISH:
                return EventType.SM_CONNECTION_ESTABLISH;
            case FE_CONNECTION_LOST:
                return EventType.FE_CONNECTION_LOST;
            case FE_CONNECTION_ESTABLISH:
                return EventType.FE_CONNECTION_ESTABLISH;
            default:
                throw new IllegalArgumentException("Unsupported TrapType "
                        + type);
        }
    }

    public static EventType getEventType(short type) {
        return getEventType(TrapType.getTrapType(type));
    }

    public int getId() {
        return id;
    }

    public EventClass getEventClass() {
        return eventClass;
    }

    /**
     * @return the defaultSeverity
     */
    public NoticeSeverity getDefaultSeverity() {
        return defaultSeverity;
    }

    public static EventType getEventType(String eventTypeName) {
        return eventTypeMap.get(eventTypeName);
    }
}
