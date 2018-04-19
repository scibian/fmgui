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
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;

/**
 */
public class VFPortCountersBean implements ITimestamped, Serializable {
    private static final long serialVersionUID = 1L;

    private int nodeLid;

    private short portNumber; // promote to handle unsigned byte

    private int flags;

    private String vfName;

    private ImageIdBean imageId;

    private long portVFXmitData;

    private long portVFRcvData;

    private long portVFXmitPkts;

    private long portVFRcvPkts;

    private long portVFXmitDiscards;

    private long swPortVFCongestion;

    private long portVFXmitWait;

    private long portVFRcvFECN;

    private long portVFRcvBECN;

    private long portVFXmitTimeCong;

    private long portVFXmitWastedBW;

    private long portVFXmitWaitData;

    private long portVFRcvBubble;

    private long portVFMarkFECN;

    private long timestamp;

    private int imageInterval;

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
     * @return the flags
     */
    public int getFlags() {
        return flags;
    }

    /**
     * @param flags
     *            the flags to set
     */
    public void setFlags(int flags) {
        this.flags = flags;
    }

    public boolean isDelta() {
        return (flags & 0x01) == 0x01;
    }

    public boolean hasUnexpectedClear() {
        return (flags & 0x02) == 0x02;
    }

    /**
     * @return the vfName
     */
    public String getVfName() {
        return vfName;
    }

    /**
     * @param vfName
     *            the vfName to set
     */
    public void setVfName(String vfName) {
        this.vfName = vfName;
    }

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
     * @return the portVFXmitData
     */
    public long getPortVFXmitData() {
        return portVFXmitData;
    }

    /**
     * @param portVFXmitData
     *            the portVFXmitData to set
     */
    public void setPortVFXmitData(long portVFXmitData) {
        this.portVFXmitData = portVFXmitData;
    }

    /**
     * @return the portVFRcvData
     */
    public long getPortVFRcvData() {
        return portVFRcvData;
    }

    /**
     * @param portVFRcvData
     *            the portVFRcvData to set
     */
    public void setPortVFRcvData(long portVFRcvData) {
        this.portVFRcvData = portVFRcvData;
    }

    /**
     * @return the portVFXmitPkts
     */
    public long getPortVFXmitPkts() {
        return portVFXmitPkts;
    }

    /**
     * @param portVFXmitPkts
     *            the portVFXmitPkts to set
     */
    public void setPortVFXmitPkts(long portVFXmitPkts) {
        this.portVFXmitPkts = portVFXmitPkts;
    }

    /**
     * @return the portVFRcvPkts
     */
    public long getPortVFRcvPkts() {
        return portVFRcvPkts;
    }

    /**
     * @param portVFRcvPkts
     *            the portVFRcvPkts to set
     */
    public void setPortVFRcvPkts(long portVFRcvPkts) {
        this.portVFRcvPkts = portVFRcvPkts;
    }

    /**
     * @return the portVFXmitDiscards
     */
    public long getPortVFXmitDiscards() {
        return portVFXmitDiscards;
    }

    /**
     * @param portVFXmitDiscards
     *            the portVFXmitDiscards to set
     */
    public void setPortVFXmitDiscards(long portVFXmitDiscards) {
        this.portVFXmitDiscards = portVFXmitDiscards;
    }

    /**
     * @return the swPortVFCongestion
     */
    public long getSwPortVFCongestion() {
        return swPortVFCongestion;
    }

    /**
     * @param swPortVFCongestion
     *            the swPortVFCongestion to set
     */
    public void setSwPortVFCongestion(long swPortVFCongestion) {
        this.swPortVFCongestion = swPortVFCongestion;
    }

    /**
     * @return the portVFXmitWait
     */
    public long getPortVFXmitWait() {
        return portVFXmitWait;
    }

    /**
     * @param portVFXmitWait
     *            the portVFXmitWait to set
     */
    public void setPortVFXmitWait(long portVFXmitWait) {
        this.portVFXmitWait = portVFXmitWait;
    }

    /**
     * @return the portVFRcvFECN
     */
    public long getPortVFRcvFECN() {
        return portVFRcvFECN;
    }

    /**
     * @param portVFRcvFECN
     *            the portVFRcvFECN to set
     */
    public void setPortVFRcvFECN(long portVFRcvFECN) {
        this.portVFRcvFECN = portVFRcvFECN;
    }

    /**
     * @return the portVFRcvBECN
     */
    public long getPortVFRcvBECN() {
        return portVFRcvBECN;
    }

    /*
     * @param portVFRcvBECN the portVFRcvBECN to set
     */
    public void setPortVFRcvBECN(long portVFRcvBECN) {
        this.portVFRcvBECN = portVFRcvBECN;
    }

    /**
     * @return the portVFXmitTimeCong
     */
    public long getPortVFXmitTimeCong() {
        return portVFXmitTimeCong;
    }

    /**
     * @param portVFXmitTimeCong
     *            the portVFXmitTimeCong to set
     */
    public void setPortVFXmitTimeCong(long portVFXmitTimeCong) {
        this.portVFXmitTimeCong = portVFXmitTimeCong;
    }

    /**
     * @return the portVFXmitWastedBW
     */
    public long getPortVFXmitWastedBW() {
        return portVFXmitWastedBW;
    }

    /**
     * @param portVFXmitWastedBW
     *            the portVFXmitWastedBW to set
     */
    public void setPortVFXmitWastedBW(long portVFXmitWastedBW) {
        this.portVFXmitWastedBW = portVFXmitWastedBW;
    }

    /**
     * @return the portVFXmitWaitData
     */
    public long getPortVFXmitWaitData() {
        return portVFXmitWaitData;
    }

    /**
     * @param portVFXmitWaitData
     *            the portVFXmitWaitData to set
     */
    public void setPortVFXmitWaitData(long portVFXmitWaitData) {
        this.portVFXmitWaitData = portVFXmitWaitData;
    }

    /**
     * @return the portVFRcvBubble
     */
    public long getPortVFRcvBubble() {
        return portVFRcvBubble;
    }

    /**
     * @param portVFRcvBubble
     *            the portVFRcvBubble to set
     */
    public void setPortVFRcvBubble(long portVFRcvBubble) {
        this.portVFRcvBubble = portVFRcvBubble;
    }

    /**
     * @return the portVFMarkFECN
     */
    public long getPortVFMarkFECN() {
        return portVFMarkFECN;
    }

    /**
     * @param portVFMarkFECN
     *            the portVFMarkFECN to set
     */
    public void setPortVFMarkFECN(long portVFMarkFECN) {
        this.portVFMarkFECN = portVFMarkFECN;
    }

    /**
     * Note that sweepTimestamp is Unix time (seconds since Jan 1st, 1970)
     *
     * @return the sweepTimestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * This field is set at the API level when VFPortCounters is retrieved from
     * FE. At that time, the ImageInfo is also retrieved from buffers or from
     * the FE and sweepTimestamp is initialized to sweepStart. Note that
     * sweepStart is Unix time (seconds since Jan 1st, 1970)
     *
     * @param sweepTimestamp
     *            the sweepTimestamp to set
     */
    @Override
    public void setTimestamp(long sweepTimestamp) {
        this.timestamp = sweepTimestamp;
    }

    /**
     *
     * <i>Description:</i> returns sweepTimestamp as Date
     *
     * @return sweepStart converted to Date
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
        return "VFPortCountersBean [nodeLid="
                + StringUtils.intHexString(nodeLid) + ", portNumber="
                + portNumber + ", flags=" + StringUtils.intHexString(flags)
                + ", vfName=" + vfName + ", imageId=" + imageId
                + ", portVFXmitData=" + portVFXmitData + ", portVFRcvData="
                + portVFRcvData + ", portVFXmitPkts=" + portVFXmitPkts
                + ", portVFRcvPkts=" + portVFRcvPkts + ", portVFXmitDiscards="
                + portVFXmitDiscards + ", swPortVFCongestion="
                + swPortVFCongestion + ", portVFXmitWait=" + portVFXmitWait
                + ", portVFRecvFECN=" + portVFRcvFECN + ", portVFRecvBECN="
                + portVFRcvBECN + ", portVFXmitTimeCong=" + portVFXmitTimeCong
                + ", portVFXmitWastedBW=" + portVFXmitWastedBW
                + ", portVFXmitWaitData=" + portVFXmitWaitData
                + ", portVFRcvBubble=" + portVFRcvBubble + ", portVFMarkFECN="
                + portVFMarkFECN + "]";
    }

}
