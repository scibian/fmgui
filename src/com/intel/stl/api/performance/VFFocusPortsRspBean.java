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

package com.intel.stl.api.performance;

import java.io.Serializable;
import java.util.Date;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.Utils;

/**
 */
public class VFFocusPortsRspBean implements ITimestamped, Serializable {
    private static final long serialVersionUID = 1L;

    private ImageIdBean imageId;

    private int nodeLid;

    private short portNumber; // unsigned byte

    private byte rate;

    private byte mtu;

    private byte localFlags;

    private byte neighborFlags;

    private long value; // list sorting factor

    private long nodeGUID;

    private String nodeDesc;

    private int neighborLid;

    private short neighborPortNumber; // unsigned byte

    private long neighborValue;

    private long neighborGuid;

    private String neighborNodeDesc;

    private long timestamp;

    private int imageInterval;

    /**
     * @return the imageId
     */
    public ImageIdBean getImageId() {
        return imageId;
    }

    /**
     * @param imageId
     *            the imageId to set
     */
    public void setImageId(ImageIdBean imageId) {
        this.imageId = imageId;
    }

    /**
     * @return the nodeLid
     */
    public int getNodeLid() {
        return nodeLid;
    }

    /**
     * @param nodeLid
     *            the nodeLid to set
     */
    public void setNodeLid(int nodeLid) {
        this.nodeLid = nodeLid;
    }

    /**
     * @return the portNumber
     */
    public short getPortNumber() {
        return portNumber;
    }

    /**
     * @param portNumber
     *            the portNumber to set
     */
    public void setPortNumber(short portNumber) {
        this.portNumber = portNumber;
    }

    /**
     * @param portNumber
     *            the portNumber to set
     */
    public void setPortNumber(byte portNumber) {
        this.portNumber = Utils.unsignedByte(portNumber);
    }

    /**
     * @return the rate
     */
    public byte getRate() {
        return rate;
    }

    /**
     * @param rate
     *            the rate to set
     */
    public void setRate(byte rate) {
        this.rate = rate;
    }

    /**
     * @return the mtu
     */
    public byte getMtu() {
        return mtu;
    }

    /**
     * @param mtu
     *            the mtu to set
     */
    public void setMtu(byte mtu) {
        this.mtu = mtu;
    }

    /**
     * @return the localFlags
     */
    public byte getLocalFlags() {
        return localFlags;
    }

    /**
     * @param localFlags
     *            the localFlags to set
     */
    public void setLocalFlags(byte localFlags) {
        this.localFlags = localFlags;
    }

    /**
     * @return the neighborFlags
     */
    public byte getNeighborFlags() {
        return neighborFlags;
    }

    /**
     * @param neighborFlags
     *            the neighborFlags to set
     */
    public void setNeighborFlags(byte neighborFlags) {
        this.neighborFlags = neighborFlags;
    }

    /**
     * @return the value
     */
    public long getValue() {
        return value;
    }

    /**
     * @param value
     *            the value to set
     */
    public void setValue(long value) {
        this.value = value;
    }

    /**
     * @return the value2
     */
    public long getNodeGUID() {
        return nodeGUID;
    }

    /**
     * @param value2
     *            the value2 to set
     */
    public void setNodeGUID(long guid) {
        this.nodeGUID = guid;
    }

    /**
     * @return the nodeDesc
     */
    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * @param nodeDesc
     *            the nodeDesc to set
     */
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    /**
     * @return the neighborLid
     */
    public int getNeighborLid() {
        return neighborLid;
    }

    /**
     * @param neighborLid
     *            the neighborLid to set
     */
    public void setNeighborLid(int neighborLid) {
        this.neighborLid = neighborLid;
    }

    /**
     * @return the neighborPortNumber
     */
    public short getNeighborPortNumber() {
        return neighborPortNumber;
    }

    /**
     * @param neighborPortNumber
     *            the neighborPortNumber to set
     */
    public void setNeighborPortNumber(short neighborPortNumber) {
        this.neighborPortNumber = neighborPortNumber;
    }

    /**
     * @param neighborPortNumber
     *            the neighborPortNumber to set
     */
    public void setNeighborPortNumber(byte neighborPortNumber) {
        this.neighborPortNumber = neighborPortNumber;
    }

    /**
     * @return the neighborValue
     */
    public long getNeighborValue() {
        return neighborValue;
    }

    /**
     * @param neighborValue
     *            the neighborValue to set
     */
    public void setNeighborValue(long neighborValue) {
        this.neighborValue = neighborValue;
    }

    /**
     * @return the neighborGuid
     */
    public long getNeighborGuid() {
        return neighborGuid;
    }

    /**
     * @param neighborGuid
     *            the neighborGuid to set
     */
    public void setNeighborGuid(long neighborGuid) {
        this.neighborGuid = neighborGuid;
    }

    /**
     * @return the neighborNodeDesc
     */
    public String getNeighborNodeDesc() {
        return neighborNodeDesc;
    }

    /**
     * @param neighborNodeDesc
     *            the neighborNodeDesc to set
     */
    public void setNeighborNodeDesc(String neighborNodeDesc) {
        this.neighborNodeDesc = neighborNodeDesc;
    }

    /**
     * @return the timestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * @param timestamp
     *            the timestamp to set
     */
    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.ITimestamped#getTimestampDate()
     */
    @Override
    public Date getTimestampDate() {
        return Utils.convertFromUnixTime(timestamp);
    }

    /**
     * @return the imageInterval
     */
    @Override
    public int getImageInterval() {
        return imageInterval;
    }

    /**
     * @param imageInterval
     *            the imageInterval to set
     */
    @Override
    public void setImageInterval(int imageInterval) {
        this.imageInterval = imageInterval;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VFFocusPortRspBean [nodeLid=" + nodeLid + ", portNumber="
                + portNumber + ", rate=" + rate + ", mtu=" + mtu
                + ", localFlags=" + localFlags + ", neighborFlags="
                + neighborFlags + ", value=0x" + Long.toHexString(value)
                + ", nodeGUID=0x" + Long.toHexString(nodeGUID) + ", nodeDesc="
                + nodeDesc + ", neighborLid=" + neighborLid
                + ", neighborPortNumber=" + neighborPortNumber
                + ", neighborValue=0x" + Long.toHexString(neighborValue)
                + ", neighborGuid=0x" + Long.toHexString(neighborGuid)
                + ", neighborNodeDesc=" + neighborNodeDesc + "]";
    }
}
