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

package com.intel.stl.ui.publisher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.NodeSource;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.subnet.NodeType;

public class NodeEvents implements Serializable, Comparable<NodeEvents> {
    private static final long serialVersionUID = 8632566237911275093L;

    private int lid;

    private String name;

    private NodeType nodeType;

    private List<EventItem> events = new ArrayList<EventItem>();

    private NoticeSeverity overallSeverity;

    public NodeEvents() {
    }

    public NodeEvents(NodeSource source) {
        lid = source.getLid();
        nodeType = source.getNodeType();
        name = source.getNodeName();
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the nodeType
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    public synchronized long getEarlistTime() {
        if (!events.isEmpty()) {
            return events.get(0).getTime();
        } else {
            return -1;
        }
    }

    public synchronized int getSize() {
        return events.size();
    }

    /**
     * @return the overallSeverity
     */
    public synchronized NoticeSeverity getOverallSeverity() {
        return overallSeverity;
    }

    public synchronized NoticeSeverity clear(long earliestTime) {
        boolean recalculateSeverity = false;
        while (!events.isEmpty() && events.get(0).getTime() < earliestTime) {
            NoticeSeverity severity = events.remove(0).getSeverity();
            if (!recalculateSeverity && severity == overallSeverity) {
                recalculateSeverity = true;
            }
        }
        if (recalculateSeverity) {
            overallSeverity = calculateSeverity();
        }
        return overallSeverity;
    }

    public synchronized NoticeSeverity addEvent(long time, EventType type,
            NoticeSeverity severity) {
        EventItem item = new EventItem(time, type, severity);
        events.add(item);
        if (overallSeverity == null
                || severity.ordinal() > overallSeverity.ordinal()) {
            overallSeverity = severity;
        }
        return overallSeverity;
    }

    protected NoticeSeverity calculateSeverity() {
        NoticeSeverity res = null;
        for (EventItem item : events) {
            NoticeSeverity severity = item.getSeverity();
            if (res == null || severity.ordinal() > res.ordinal()) {
                res = severity;
            }
        }
        return res;
    }

    /**
     * 
     * Description: score in [0, 100]
     * 
     * @return
     */
    public double getHealthScore() {
        NoticeSeverity severity = getOverallSeverity();
        if (severity == null) {
            return 0;
        }
        return EventCalculator.HEALTH_WEIGHTS.get(severity) * 100;
    }

    public synchronized EventItem getLatestEvent() {
        if (!events.isEmpty()) {
            return events.get(events.size() - 1);
        } else {
            return null;
        }
    }

    /**
     * 
     * Description: deep copy of the objet
     * 
     * @return
     */
    public NodeEvents copy() {
        NodeEvents res = new NodeEvents();
        res.lid = this.lid;
        res.name = new String(this.name);
        res.nodeType = this.nodeType;
        // shallow copy
        res.events = new ArrayList<EventItem>(this.events);
        res.overallSeverity = this.overallSeverity;
        return res;
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
        result = prime * result + lid;
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
        NodeEvents other = (NodeEvents) obj;
        if (lid != other.lid) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(NodeEvents o) {
        long t1 = getEarlistTime();
        long t2 = o.getEarlistTime();
        return t1 > t2 ? 1 : (t1 < t2 ? -1 : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeEvents [lid=" + lid + ", name=" + name + ", nodeType="
                + nodeType + ", events=" + events + ", overallSeverity="
                + overallSeverity + "]";
    }

    public static class EventItem implements Serializable {
        private final long time; // in ms

        private final EventType type;

        private final NoticeSeverity severity;

        /**
         * Description:
         * 
         * @param time
         * @param type
         * @param severity
         */
        public EventItem(long time, EventType type, NoticeSeverity severity) {
            super();
            this.time = time;
            this.type = type;
            this.severity = severity;
        }

        /**
         * @return the time
         */
        public long getTime() {
            return time;
        }

        /**
         * @return the type
         */
        public EventType getType() {
            return type;
        }

        /**
         * @return the severity
         */
        public NoticeSeverity getSeverity() {
            return severity;
        }

        public double getHealthScore() {
            return EventCalculator.HEALTH_WEIGHTS.get(severity) * 100;
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
            result =
                    prime * result
                            + ((severity == null) ? 0 : severity.hashCode());
            result = prime * result + (int) (time ^ (time >>> 32));
            result = prime * result + ((type == null) ? 0 : type.hashCode());
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
            EventItem other = (EventItem) obj;
            if (severity != other.severity) {
                return false;
            }
            if (time != other.time) {
                return false;
            }
            if (type != other.type) {
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
            return "Item [time=" + time + ", type=" + type + ", severity="
                    + severity + "]";
        }
    }
}
