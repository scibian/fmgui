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

package com.intel.stl.ui.event;

import java.util.Arrays;

public class LinkUpdateEvent extends PortUpdateEvent {
    protected final int[] toNodeLids;

    protected final short[] toPortNums;

    /**
     * Description:
     * 
     * @param nodeLid
     * @param portNum
     */
    public LinkUpdateEvent(int[] fromNodeLids, short[] fromPortNums,
            int[] toNodeLids, short[] toPortNums, Object source) {
        super(fromNodeLids, fromPortNums, source);
        this.toNodeLids = toNodeLids;
        this.toPortNums = toPortNums;
    }

    /**
     * @return the toNodeLid
     */
    public int[] getToNodeLids() {
        return toNodeLids;
    }

    /**
     * @return the toPortNum
     */
    public short[] getToPortNums() {
        return toPortNums;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(toNodeLids);
        result = prime * result + Arrays.hashCode(toPortNums);
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
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        LinkUpdateEvent other = (LinkUpdateEvent) obj;
        if (!Arrays.equals(toNodeLids, other.toNodeLids)) {
            return false;
        }
        if (!Arrays.equals(toPortNums, other.toPortNums)) {
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
        return "LinkUpdateEvent [toNodeLids=" + Arrays.toString(toNodeLids)
                + ", toPortNums=" + Arrays.toString(toPortNums) + ", portNums="
                + Arrays.toString(portNums) + ", nodeLids="
                + Arrays.toString(nodeLids) + "]";
    }

}
