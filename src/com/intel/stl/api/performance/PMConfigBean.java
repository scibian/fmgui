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

import com.intel.stl.api.Utils;

public class PMConfigBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int sweepInterval;

    private int maxClients;

    private int sizeHistory;

    private int sizeFreeze;

    private int lease;

    private int pmFlags;

    // STL_CONGESTION_WEIGHTS_T, promoted to short to support unsigned byte
    private short portXmitWait;

    private short swPortCongestion;

    private short portRcvFECN;

    private short portRcvBECN;

    private short portXmitTimeCong;

    private short portMarkFECN;

    // STL_PM_ERR_THRESHOLDS
    private int integrityErrors;

    private int congestionErrors;

    private int smaCongestionErrors;

    private int bubbleErrors;

    private int securityErrors;

    private int routingErrors;

    // STL_INTEGRITY_WEIGHTS_T, promoted to short to support unsigned byte
    private short localLinkIntegrityErrors;

    private short portRcvErrors;

    private short excessiveBufferOverruns;

    private short linkErrorRecovery;

    private short linkDowned;

    private short uncorrectableErrors;

    private short fmConfigErrors;

    private byte linkQualityIndicator;

    private byte linkWidthDowngrade;

    //
    private long memoryFootprint;

    private int maxAttempts;

    private int responseTimeout;

    private int minResponseTimeout;

    private int maxParallelNodes;

    private int pmaBatchSize;

    private short errorClear; // unsigned byte

    public int getSweepInterval() {
        return sweepInterval;
    }

    public void setSweepInterval(int sweepInterval) {
        this.sweepInterval = sweepInterval;
    }

    public int getMaxClients() {
        return maxClients;
    }

    public void setMaxClients(int maxClients) {
        this.maxClients = maxClients;
    }

    public int getSizeHistory() {
        return sizeHistory;
    }

    public void setSizeHistory(int sizeHistory) {
        this.sizeHistory = sizeHistory;
    }

    public int getSizeFreeze() {
        return sizeFreeze;
    }

    public void setSizeFreeze(int sizeFreeze) {
        this.sizeFreeze = sizeFreeze;
    }

    public int getLease() {
        return lease;
    }

    public void setLease(int lease) {
        this.lease = lease;
    }

    public int getPmFlags() {
        return pmFlags;
    }

    public void setPmFlags(int pmFlags) {
        this.pmFlags = pmFlags;
    }

    /**
     * @return the portXmitWait
     */
    public short getPortXmitWait() {
        return portXmitWait;
    }

    /**
     * @param portXmitWait
     *            the portXmitWait to set
     */
    public void setPortXmitWait(byte portXmitWait) {
        this.portXmitWait = (short) (portXmitWait & 0xff);
    }

    /**
     * @return the swPortCongestion
     */
    public short getSwPortCongestion() {
        return swPortCongestion;
    }

    /**
     * @param swPortCongestion
     *            the swPortCongestion to set
     */
    public void setSwPortCongestion(byte swPortCongestion) {
        this.swPortCongestion = (short) (swPortCongestion & 0xff);
    }

    /**
     * @return the portRcvBECN
     */
    public short getPortRcvBECN() {
        return portRcvBECN;
    }

    /**
     * @param portRcvBECN
     *            the portRcvBECN to set
     */
    public void setPortRcvBECN(byte portRcvBECN) {
        this.portRcvBECN = (short) (portRcvBECN & 0xff);
    }

    /**
     * @return the portXmitTimeCong
     */
    public short getPortXmitTimeCong() {
        return portXmitTimeCong;
    }

    /**
     * @param portXmitTimeCong
     *            the portXmitTimeCong to set
     */
    public void setPortXmitTimeCong(byte portXmitTimeCong) {
        this.portXmitTimeCong = (short) (portXmitTimeCong & 0xff);
    }

    /**
     * @return the portMarkFECN
     */
    public short getPortMarkFECN() {
        return portMarkFECN;
    }

    /**
     * @param portMarkFECN
     *            the portMarkFECN to set
     */
    public void setPortMarkFECN(byte portMarkFECN) {
        this.portMarkFECN = (short) (portMarkFECN & 0xff);
    }

    /**
     * @return the portRcvFECN
     */
    public short getPortRcvFECN() {
        return portRcvFECN;
    }

    /**
     * @param portRcvFECN
     *            the portRcvFECN to set
     */
    public void setPortRcvFECN(byte portRcvFECN) {
        this.portRcvFECN = (short) (portRcvFECN & 0xff);
    }

    /**
     * @return the localLinkIntegrityErrors
     */
    public short getLocalLinkIntegrityErrors() {
        return localLinkIntegrityErrors;
    }

    /**
     * @param localLinkIntegrityErrors
     *            the localLinkIntegrityErrors to set
     */
    public void setLocalLinkIntegrityErrors(byte localLinkIntegrityErrors) {
        this.localLinkIntegrityErrors =
                (short) (localLinkIntegrityErrors & 0xff);
    }

    /**
     * @return the portRcvErrors
     */
    public short getPortRcvErrors() {
        return portRcvErrors;
    }

    /**
     * @param portRcvErrors
     *            the portRcvErrors to set
     */
    public void setPortRcvErrors(byte portRcvErrors) {
        this.portRcvErrors = (short) (portRcvErrors & 0xff);
    }

    /**
     * @return the excessiveBufferOverruns
     */
    public short getExcessiveBufferOverruns() {
        return excessiveBufferOverruns;
    }

    /**
     * @param excessiveBufferOverruns
     *            the excessiveBufferOverruns to set
     */
    public void setExcessiveBufferOverruns(byte excessiveBufferOverruns) {
        this.excessiveBufferOverruns = (short) (excessiveBufferOverruns & 0xff);
    }

    /**
     * @return the linkErrorRecovery
     */
    public short getLinkErrorRecovery() {
        return linkErrorRecovery;
    }

    /**
     * @param linkErrorRecovery
     *            the linkErrorRecovery to set
     */
    public void setLinkErrorRecovery(byte linkErrorRecovery) {
        this.linkErrorRecovery = (short) (linkErrorRecovery & 0xff);
    }

    /**
     * @return the linkDowned
     */
    public short getLinkDowned() {
        return linkDowned;
    }

    /**
     * @param linkDowned
     *            the linkDowned to set
     */
    public void setLinkDowned(byte linkDowned) {
        this.linkDowned = (short) (linkDowned & 0xff);
    }

    /**
     * @return the uncorrectableErrors
     */
    public short getUncorrectableErrors() {
        return uncorrectableErrors;
    }

    /**
     * @param uncorrectableErrors
     *            the uncorrectableErrors to set
     */
    public void setUncorrectableErrors(byte uncorrectableErrors) {
        this.uncorrectableErrors = (short) (uncorrectableErrors & 0xff);
    }

    /**
     * @return the fmConfigErrors
     */
    public short getFmConfigErrors() {
        return fmConfigErrors;
    }

    /**
     * @param fmConfigErrors
     *            the fmConfigErrors to set
     */
    public void setFmConfigErrors(byte fmConfigErrors) {
        this.fmConfigErrors = (short) (fmConfigErrors & 0xff);
    }

    /**
     * @return the linkQualityIndicator
     */
    public byte getLinkQualityIndicator() {
        return linkQualityIndicator;
    }

    /**
     * @param linkQualityIndicator
     *            the linkQualityIndicator to set
     */
    public void setLinkQualityIndicator(byte linkQualityIndicator) {
        this.linkQualityIndicator = linkQualityIndicator;
    }

    /**
     * @return the linkWidthDowngrade
     */
    public byte getLinkWidthDowngrade() {
        return linkWidthDowngrade;
    }

    /**
     * @param linkWidthDowngrade
     *            the linkWidthDowngrade to set
     */
    public void setLinkWidthDowngrade(byte linkWidthDowngrade) {
        this.linkWidthDowngrade = linkWidthDowngrade;
    }

    public int getIntegrityErrors() {
        return integrityErrors;
    }

    public void setIntegrityErrors(int integrityErrors) {
        this.integrityErrors = integrityErrors;
    }

    public int getCongestionErrors() {
        return congestionErrors;
    }

    public void setCongestionErrors(int congestionErrors) {
        this.congestionErrors = congestionErrors;
    }

    public int getSmaCongestionErrors() {
        return smaCongestionErrors;
    }

    public void setSmaCongestionErrors(int smaCongestionErrors) {
        this.smaCongestionErrors = smaCongestionErrors;
    }

    /**
     * @return the bubbleErrors
     */
    public int getBubbleErrors() {
        return bubbleErrors;
    }

    /**
     * @param bubbleErrors
     *            the bubbleErrors to set
     */
    public void setBubbleErrors(int bubbleErrors) {
        this.bubbleErrors = bubbleErrors;
    }

    public int getSecurityErrors() {
        return securityErrors;
    }

    public void setSecurityErrors(int securityErrors) {
        this.securityErrors = securityErrors;
    }

    public int getRoutingErrors() {
        return routingErrors;
    }

    public void setRoutingErrors(int routingErrors) {
        this.routingErrors = routingErrors;
    }

    public long getMemoryFootprint() {
        return memoryFootprint;
    }

    public void setMemoryFootprint(long memoryFootprint) {
        this.memoryFootprint = memoryFootprint;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public void setMaxAttempts(int maxAttempts) {
        this.maxAttempts = maxAttempts;
    }

    public int getResponseTimeout() {
        return responseTimeout;
    }

    public void setResponseTimeout(int responseTimeout) {
        this.responseTimeout = responseTimeout;
    }

    public int getMinResponseTimeout() {
        return minResponseTimeout;
    }

    public void setMinResponseTimeout(int minResponseTimeout) {
        this.minResponseTimeout = minResponseTimeout;
    }

    public int getMaxParallelNodes() {
        return maxParallelNodes;
    }

    public void setMaxParallelNodes(int maxParallelNodes) {
        this.maxParallelNodes = maxParallelNodes;
    }

    public int getPmaBatchSize() {
        return pmaBatchSize;
    }

    public void setPmaBatchSize(int pmaBatchSize) {
        this.pmaBatchSize = pmaBatchSize;
    }

    public short getErrorClear() {
        return errorClear;
    }

    /**
     * @param errorClear
     *            the errorClear to set
     */
    public void setErrorClear(short errorClear) {
        this.errorClear = errorClear;
    }

    public void setErrorClear(byte errorClear) {
        this.errorClear = Utils.unsignedByte(errorClear);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PMConfigBean [sweepInterval=" + sweepInterval + ", maxClients="
                + maxClients + ", sizeHistory=" + sizeHistory + ", sizeFreeze="
                + sizeFreeze + ", lease=" + lease + ", pmFlags=" + pmFlags
                + ", portXmitWait=" + portXmitWait + ", swPortCongestion="
                + swPortCongestion + ", portRcvFECN=" + portRcvFECN
                + ", portRcvBECN=" + portRcvBECN + ", portXmitTimeCong="
                + portXmitTimeCong + ", portMarkFECN=" + portMarkFECN
                + ", integrityErrors=" + integrityErrors
                + ", congestionErrors=" + congestionErrors
                + ", smaCongestionErrors=" + smaCongestionErrors
                + ", bubbleErrors=" + bubbleErrors + ", securityErrors="
                + securityErrors + ", routingErrors=" + routingErrors
                + ", localLinkIntegrityErrors=" + localLinkIntegrityErrors
                + ", portRcvErrors=" + portRcvErrors
                + ", excessiveBufferOverruns=" + excessiveBufferOverruns
                + ", linkErrorRecovery=" + linkErrorRecovery + ", linkDowned="
                + linkDowned + ", uncorrectableErrors=" + uncorrectableErrors
                + ", fmConfigErrors=" + fmConfigErrors + ", memoryFootprint="
                + memoryFootprint + ", maxAttempts=" + maxAttempts
                + ", responseTimeout=" + responseTimeout
                + ", minResponseTimeout=" + minResponseTimeout
                + ", maxParallelNodes=" + maxParallelNodes + ", pmaBatchSize="
                + pmaBatchSize + ", errorClear=" + errorClear + "]";
    }

}
