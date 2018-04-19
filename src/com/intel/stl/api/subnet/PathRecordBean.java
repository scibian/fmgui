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

import com.intel.stl.api.Utils;

/**
 */
public class PathRecordBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private long serviceId;

    private GIDBean dGid;

    private GIDBean sGid;

    private short dLid;

    private short sLid;

    private boolean rawTraffic;

    private int flowLabel;

    private short hopLimit; // promote to handle unsigned byte

    private byte tClass;

    private byte numbPath;

    private boolean reversible;

    private short pKey;

    private byte qosType;

    private short qosPriority; // promote to handle unsigned byte

    private byte sl;

    private byte mtuSelector;

    private byte mtu;

    private byte rateSelector;

    private byte rate;

    private byte pktLifeTimeSelector;

    private byte pktLifeTime;

    private byte preference;

    /**
     * @return the serviceId
     */
    public long getServiceId() {
        return serviceId;
    }

    /**
     * @param serviceId
     *            the serviceId to set
     */
    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
    }

    /**
     * @return the dGid
     */
    public GIDBean getDGid() {
        return dGid;
    }

    /**
     * @param dGid
     *            the dGid to set
     */
    public void setDGid(GIDBean dGid) {
        this.dGid = dGid;
    }

    /**
     * @return the sGid
     */
    public GIDBean getSGid() {
        return sGid;
    }

    /**
     * @param sGid
     *            the sGid to set
     */
    public void setSGid(GIDBean sGid) {
        this.sGid = sGid;
    }

    /**
     * @return the dLid
     */
    public short getDLid() {
        return dLid;
    }

    /**
     * @param dLid
     *            the dLid to set
     */
    public void setDLid(short dLid) {
        this.dLid = dLid;
    }

    /**
     * @return the sLid
     */
    public short getSLid() {
        return sLid;
    }

    /**
     * @param sLid
     *            the sLid to set
     */
    public void setSLid(short sLid) {
        this.sLid = sLid;
    }

    /**
     * @return the rawTraffic
     */
    public boolean getRawTraffic() {
        return rawTraffic;
    }

    /**
     * @param rawTraffic
     *            the rawTraffic to set
     */
    public void setRawTraffic(boolean rawTraffic) {
        this.rawTraffic = rawTraffic;
    }

    /**
     * @return the flowLabel
     */
    public int getFlowLabel() {
        return flowLabel;
    }

    /**
     * @param flowLabel
     *            the flowLabel to set
     */
    public void setFlowLabel(int flowLabel) {
        this.flowLabel = flowLabel;
    }

    /**
     * @return the hopLimit
     */
    public short getHopLimit() {
        return hopLimit;
    }

    /**
     * @param hopLimit
     *            the hopLimit to set
     */
    public void setHopLimit(short hopLimit) {
        this.hopLimit = hopLimit;
    }

    /**
     * @param hopLimit
     *            the hopLimit to set
     */
    public void setHopLimit(byte hopLimit) {
        this.hopLimit = (short) (hopLimit & 0xff);
    }

    /**
     * @return the tClass
     */
    public byte getTClass() {
        return tClass;
    }

    /**
     * @param tClass
     *            the tClass to set
     */
    public void setTClass(byte tClass) {
        this.tClass = tClass;
    }

    /**
     * @return the numbPath
     */
    public byte getNumbPath() {
        return numbPath;
    }

    /**
     * @param numbPath
     *            the numbPath to set
     */
    public void setNumbPath(byte numbPath) {
        this.numbPath = numbPath;
    }

    /**
     * @return the reversible
     */
    public boolean isReversible() {
        return reversible;
    }

    /**
     * @param reversible
     *            the reversible to set
     */
    public void setReversible(boolean reversible) {
        this.reversible = reversible;
    }

    /**
     * @return the pKey
     */
    public short getPKey() {
        return pKey;
    }

    /**
     * @param pKey
     *            the pKey to set
     */
    public void setPKey(short pKey) {
        this.pKey = pKey;
    }

    /**
     * @return the qosType
     */
    public byte getQosType() {
        return qosType;
    }

    /**
     * @param qosType
     *            the qosType to set
     */
    public void setQosType(byte qosType) {
        this.qosType = qosType;
    }

    /**
     * @return the qosPriority
     */
    public short getQosPriority() {
        return qosPriority;
    }

    /**
     * @param qosPriority
     *            the qosPriority to set
     */
    public void setQosPriority(byte qosPriority) {
        this.qosPriority = Utils.unsignedByte(qosPriority);
    }

    /**
     * @return the sl
     */
    public byte getSL() {
        return sl;
    }

    /**
     * @param sl
     *            the sl to set
     */
    public void setSL(byte sl) {
        this.sl = sl;
    }

    /**
     * @return the mtuSelector
     */
    public byte getMtuSelector() {
        return mtuSelector;
    }

    /**
     * @param mtuSelector
     *            the mtuSelector to set
     */
    public void setMtuSelector(byte mtuSelector) {
        this.mtuSelector = mtuSelector;
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
     * @return the rateSelector
     */
    public byte getRateSelector() {
        return rateSelector;
    }

    /**
     * @param rateSelector
     *            the rateSelector to set
     */
    public void setRateSelector(byte rateSelector) {
        this.rateSelector = rateSelector;
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
     * @return the pktLifeTimeSelector
     */
    public byte getPktLifeTimeSelector() {
        return pktLifeTimeSelector;
    }

    /**
     * @param pktLifeTimeSelector
     *            the pktLifeTimeSelector to set
     */
    public void setPktLifeTimeSelector(byte pktLifeTimeSelector) {
        this.pktLifeTimeSelector = pktLifeTimeSelector;
    }

    /**
     * @return the pktLifeTime
     */
    public byte getPktLifeTime() {
        return pktLifeTime;
    }

    /**
     * @param pktLifeTime
     *            the pktLifeTime to set
     */
    public void setPktLifeTime(byte pktLifeTime) {
        this.pktLifeTime = pktLifeTime;
    }

    /**
     * @return the preference
     */
    public byte getPreference() {
        return preference;
    }

    /**
     * @param preference
     *            the preference to set
     */
    public void setPreference(byte preference) {
        this.preference = preference;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PathRecordBean [serviceId=" + serviceId + ", dGid=" + dGid
                + ", sGid=" + sGid + ", dLid=" + dLid + ", sLid=" + sLid
                + ", rawTraffic=" + rawTraffic + ", flowLabel=" + flowLabel
                + ", hopLimit=" + hopLimit + ", tClass=" + tClass
                + ", numbPath=" + numbPath + ", reversible=" + reversible
                + ", pKey=" + pKey + ", qosType=" + qosType + ", qosPriority="
                + qosPriority + ", sl=" + sl + ", mtuSelector=" + mtuSelector
                + ", mtu=" + mtu + ", rateSelector=" + rateSelector + ", rate="
                + rate + ", pktLifeTimeSelector=" + pktLifeTimeSelector
                + ", pktLifeTime=" + pktLifeTime + ", preference=" + preference
                + "]";
    }

}
