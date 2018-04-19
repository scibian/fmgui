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

package com.intel.stl.api;

import java.util.BitSet;

import com.intel.stl.api.subnet.NodeType;

public class NodeState {
    private final NodeType type;

    private final BitSet ports;

    private final int numPorts;

    /**
     * 
     * Description:
     * 
     * @param type
     * @param lid
     * @param numPorts
     *            not include InternalMgrPort
     */
    public NodeState(NodeType type, int numPorts) {
        super();
        this.type = type;
        ports = new BitSet();
        this.numPorts = numPorts;
    }

    /**
     * @return the type
     */
    public NodeType getType() {
        return type;
    }

    public void setActivePort(short portNum) {
        ports.set(portNum);
    }

    public boolean isActivePort(short portNum) {
        if (portNum == 0 && type == NodeType.SWITCH) {
            return true;
        } else {
            return ports.get(portNum);
        }
    }

    public int numActicePorts(boolean countInternalMgrPort) {
        if (countInternalMgrPort && type == NodeType.SWITCH) {
            return ports.cardinality() + 1;
        } else {
            return ports.cardinality();
        }
    }

    /**
     * @return the numPorts
     */
    public int getNumPorts(boolean countInternalMgrPort) {
        if (countInternalMgrPort && type == NodeType.SWITCH) {
            return numPorts + 1;
        } else {
            return numPorts;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeState [type=" + type + ", numPorts=" + numPorts
                + ", ports=" + ports + "]";
    }

}
