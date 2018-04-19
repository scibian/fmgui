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

package com.intel.stl.api.subnet;

import java.io.Serializable;
import java.util.Date;

import com.intel.stl.api.Utils;

public class PortDownReasonBean implements Serializable {
    private static final long serialVersionUID = -4990045846635321180L;

    private byte neighborLinkDownReason;

    private byte linkDownReason;

    private long timestamp;

    /**
     * Description:
     * 
     */
    public PortDownReasonBean() {
        super();
    }

    /**
     * Description:
     * 
     * @param linkDownReason
     * @param timestamp
     */
    public PortDownReasonBean(byte neighborLinkDownReason, byte linkDownReason,
            long timestamp) {
        super();
        this.linkDownReason = linkDownReason;
        this.neighborLinkDownReason = neighborLinkDownReason;
        this.timestamp = timestamp;
    }

    /**
     * @return the linkDownReason
     */
    public byte getLinkDownReason() {
        return linkDownReason;
    }

    /**
     * @param linkDownReason
     *            the linkDownReason to set
     */
    public void setLinkDownReason(byte linkDownReason) {
        this.linkDownReason = linkDownReason;
    }

    /**
     * @return the neighborLinkDownReason
     */
    public byte getNeighborLinkDownReason() {
        return neighborLinkDownReason;
    }

    /**
     * @param neighborLinkDownReason
     *            the neighborLinkDownReason to set
     */
    public void setNeighborLinkDownReason(byte neighborLinkDownReason) {
        this.neighborLinkDownReason = neighborLinkDownReason;
    }

    /**
     * @return the timestamp
     */
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * 
     * <i>Description:</i> returns timestamp as Date
     * 
     * @return timestamp converted to Date
     */
    public Date getTimestampDate() {
        return Utils.convertFromUnixTime(timestamp);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortDownReasonBean [linkDownReason=" + linkDownReason
                + ", neighborLinkDownReason=" + neighborLinkDownReason
                + ", timestamp=" + timestamp + "]";
    }

}
