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
package com.intel.stl.ui.model;

import java.io.Serializable;

import javax.swing.ImageIcon;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;

/**
 */
public class NodeScore extends TimedScore implements Comparable<NodeScore>,
        Serializable {
    private static final long serialVersionUID = -375009123447030706L;

    private final String name;

    private final NodeType type;

    private final int lid;

    private final EventType eventType;

    public NodeScore(String name, NodeType type, int lid, EventType eventType,
            long time, double score) {
        super(time, score);
        this.name = name;
        this.type = type;
        this.lid = lid;
        this.eventType = eventType;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the type
     */
    public NodeType getType() {
        return type;
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @return the isActive
     */
    public boolean isActive() {
        return eventType != EventType.PORT_INACTIVE;
    }

    /**
     * @return the eventType
     */
    public EventType getEventType() {
        return eventType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(NodeScore o) {
        double s1 = getScore();
        double s2 = o.getScore();
        return Double.compare(s1, s2);
    }

    public ImageIcon getIcon() {
        NodeTypeViz viz = NodeTypeViz.getNodeTypeViz(type.getId());
        if (viz != null) {
            return viz.getIcon().getImageIcon();
        } else {
            throw new IllegalArgumentException("Couldn't find NodeTypeViz for "
                    + type);
        }
    }

    public String getDescription() {
        double score = getScore();
        return UILabels.STL10211_WORST_NODE.getDescription(name,
                eventType.name(), UIConstants.DECIMAL.format(score));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeScore [getTime()=" + getTime() + ", getScore()="
                + getScore() + ", lid=" + lid + ", name=" + name + ", type="
                + type + "]";
    }

}
