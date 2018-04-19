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

/**
 * Title:        PortRecordBean
 * Description:  Port Record from SA populated by the connect manager.
 * 
 * @version 0.0
 */
import static com.intel.stl.api.configuration.PortState.ACTIVE;

import java.io.Serializable;
import java.util.Arrays;

import com.intel.stl.api.Utils;

public class PortRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int NaN = -1;

    // Header
    private int endPortLID = NaN;

    private short portNum = NaN; // promote to handle unsigned byte

    private PortInfoBean portInfo = null;

    private PortDownReasonBean[] linkDownReasons;

    public PortRecordBean() {
        super();
    }

    public PortRecordBean(int endPortLID, byte portNum, PortInfoBean portInfo) {
        super();
        this.endPortLID = endPortLID;
        this.portNum = Utils.unsignedByte(portNum);
        this.portInfo = portInfo;
    }

    /**
     * @return hash code
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + endPortLID;
        result = prime * result + portNum;
        return result;
    }

    /**
     * @returns true if equals, false if not
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
        PortRecordBean other = (PortRecordBean) obj;
        if (endPortLID != other.endPortLID) {
            return false;
        }
        if (portNum != other.portNum) {
            return false;
        }
        return true;
    }

    /**
     * @return the endPortLID
     */
    public int getEndPortLID() {
        return endPortLID;
    }

    /**
     * @param endPortLID
     *            the endPortLID to set
     */
    public void setEndPortLID(int endPortLID) {
        this.endPortLID = endPortLID;
    }

    /**
     * @return the portNum
     */
    public short getPortNum() {
        return portNum;
    }

    /**
     * @param portNum
     *            the portNum to set
     */
    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }

    /**
     * @param portNum
     *            the portNum to set
     */
    public void setPortNum(byte portNum) {
        this.portNum = Utils.unsignedByte(portNum);
    }

    /**
     * @return the portInfo
     */
    public PortInfoBean getPortInfo() {
        return portInfo;
    }

    /**
     * @param portInfo
     *            the portInfo to set
     */
    public void setPortInfo(PortInfoBean portInfo) {
        this.portInfo = portInfo;
    }

    /**
     * @return the linkDownReasons
     */
    public PortDownReasonBean[] getLinkDownReasons() {
        return linkDownReasons;
    }

    /**
     * @param linkDownReasons
     *            the linkDownReasons to set
     */
    public void setLinkDownReasons(PortDownReasonBean[] linkDownReasons) {
        this.linkDownReasons = linkDownReasons;
    }

    /**
     * @return the state
     */
    public boolean isActive() {
        if (portInfo == null || portInfo.getPortStates() == null
                || portInfo.getPortStates().getPortState() == null) {
            return false;
        }
        return portInfo.getPortStates().getPortState() == ACTIVE;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortRecordBean [endPortLID=" + endPortLID + ", portNum="
                + portNum + ", portInfo=" + portInfo + ", linkDownReasons="
                + Arrays.toString(linkDownReasons) + "]";
    }

}
