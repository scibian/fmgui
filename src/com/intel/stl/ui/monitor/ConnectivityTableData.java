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

package com.intel.stl.ui.monitor;

import java.io.Serializable;
import java.util.concurrent.atomic.AtomicReference;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.STLConstants;

/**
 * Data for the Connectivity table
 */
public class ConnectivityTableData implements Serializable {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -3222455776096537707L;

    private final long nodeGuidValue;

    private final int nodeLidValue;

    private final NodeType nodeType;

    private final short portNumValue;

    private boolean slowLinkState;

    private final boolean isNeighbor;

    private String nodeName;

    private final String nodeGUID;

    private String portNumber;

    private String cableInfo;

    private String linkState; // TODO Don't know where to get this

    private String physicalLinkState; // TODO Don't know where to get this

    private String activeLinkWidth;

    private String enabledLinkWidth;

    private String supportedLinkWidth;

    private String activeLinkWidthDnGrdTx;

    private String activeLinkWidthDnGrdRx;

    private String enabledLinkWidthDnGrd;

    private String supportedLinkWidthDnGrd;

    private String activeLinkSpeed;

    private String enabledLinkSpeed;

    private String supportedLinkSpeed;

    private final AtomicReference<PerformanceData> performance;

    private int linkQuality;

    /**
     * Description:
     *
     */
    public ConnectivityTableData(int nodeLid, long nodeGuidValue,
            NodeType nodeType, short portNumValue, boolean isNeighbor) {
        super();
        this.nodeLidValue = nodeLid;
        this.nodeType = nodeType;
        this.nodeGuidValue = nodeGuidValue;
        this.portNumValue = portNumValue;
        this.isNeighbor = isNeighbor;
        performance = new AtomicReference<PerformanceData>();

        nodeGUID = StringUtils.longHexString(nodeGuidValue);
        if (isNeighbor) {
            portNumber = Integer.toString(portNumValue) + " ("
                    + STLConstants.K0525_NEIGHBOR.getValue() + ")";
        } else {
            portNumber = Integer.toString(portNumValue);
        }
    }

    /**
     * @return the isNeighbor
     */
    public boolean isNeighbor() {
        return isNeighbor;
    }

    /**
     * @return slowLinkState - true if slow link, false if not
     */
    public boolean isSlowLinkState() {
        return slowLinkState;
    }

    public void setSlowLinkState(boolean state) {
        slowLinkState = state;
    }

    /**
     * @return the deviceName
     */
    public String getNodeName() {
        return nodeName;
    }

    /**
     * @param nodeName
     *            the deviceName to set
     */
    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    /**
     * @return the nodeLidValue
     */
    public int getNodeLidValue() {
        return nodeLidValue;
    }

    /**
     * @return the nodeType
     */
    public NodeType getNodeType() {
        return nodeType;
    }

    /**
     * @return the nodeGUID
     */
    public String getNodeGUID() {
        return nodeGUID;
    }

    /**
     * @return the portNumber
     */
    public String getPortNumber() {
        return portNumber;
    }

    /**
     * @return the portNumValue
     */
    public short getPortNumValue() {
        return portNumValue;
    }

    /**
     *
     * @return the cableInfo
     */
    public String getCableInfo() {
        return cableInfo;
    }

    /**
     *
     * @param cableInfo
     *            - the cableInfo to set
     */
    public void setCableInfo(String cableInfo) {
        this.cableInfo = cableInfo;
    }

    /**
     * @return the linkState
     */
    public String getLinkState() {
        return linkState;
    }

    /**
     * @param linkState
     *            the linkState to set
     */
    public void setLinkState(String linkState) {
        this.linkState = linkState;
    }

    /**
     * @return the physicalLinkState
     */
    public String getPhysicalLinkState() {
        return physicalLinkState;
    }

    /**
     * @param physicalLinkState
     *            the physicalLinkState to set
     */
    public void setPhysicalLinkState(String physicalLinkState) {
        this.physicalLinkState = physicalLinkState;
    }

    /**
     * @return the activeLinkWidth
     */
    public String getActiveLinkWidth() {
        return activeLinkWidth;
    }

    /**
     * @param activeLinkWidth
     *            the activeLinkWidth to set
     */
    public void setActiveLinkWidth(String activeLinkWidth) {
        this.activeLinkWidth = activeLinkWidth;
    }

    /**
     * @return the enabledLinkWidth
     */
    public String getEnabledLinkWidth() {
        return enabledLinkWidth;
    }

    /**
     * @param enabledLinkWidth
     *            the enabledLinkWidth to set
     */
    public void setEnabledLinkWidth(String enabledLinkWidth) {
        this.enabledLinkWidth = enabledLinkWidth;
    }

    /**
     * @return the supportedLinkWidth
     */
    public String getSupportedLinkWidth() {
        return supportedLinkWidth;
    }

    /**
     * @param supportedLinkWidth
     *            the supportedLinkWidth to set
     */
    public void setSupportedLinkWidth(String supportedLinkWidth) {
        this.supportedLinkWidth = supportedLinkWidth;
    }

    /**
     * @return the activeLinkWidthDnGrdTx
     */
    public String getActiveLinkWidthDnGrdTx() {
        return activeLinkWidthDnGrdTx;
    }

    /**
     * @param activeLinkWidthDnGrdTx
     *            the activeLinkWidthDnGrdTx to set
     */
    public void setActiveLinkWidthDnGrdTx(String activeLinkWidthDnGrdTx) {
        this.activeLinkWidthDnGrdTx = activeLinkWidthDnGrdTx;
    }

    /**
     * @return the activeLinkWidthDnGrdRx
     */
    public String getActiveLinkWidthDnGrdRx() {
        return activeLinkWidthDnGrdRx;
    }

    /**
     * @param activeLinkWidthDnGrdRx
     *            the activeLinkWidthDnGrdRx to set
     */
    public void setActiveLinkWidthDnGrdRx(String activeLinkWidthDnGrdRx) {
        this.activeLinkWidthDnGrdRx = activeLinkWidthDnGrdRx;
    }

    /**
     * @return the enabledLinkWidthDnGrd
     */
    public String getEnabledLinkWidthDnGrd() {
        return enabledLinkWidthDnGrd;
    }

    /**
     * @param enabledLinkWidthDnGrd
     *            the enabledLinkWidthDnGrd to set
     */
    public void setEnabledLinkWidthDnGrd(String enabledLinkWidthDnGrd) {
        this.enabledLinkWidthDnGrd = enabledLinkWidthDnGrd;
    }

    /**
     * @return the supportedLinkWidthDnGrd
     */
    public String getSupportedLinkWidthDnGrd() {
        return supportedLinkWidthDnGrd;
    }

    /**
     * @param supportedLinkWidthDnGrd
     *            the supportedLinkWidthDnGrd to set
     */
    public void setSupportedLinkWidthDnGrd(String supportedLinkWidthDnGrd) {
        this.supportedLinkWidthDnGrd = supportedLinkWidthDnGrd;
    }

    /**
     * @return the activeLinkSpeed
     */
    public String getActiveLinkSpeed() {
        return activeLinkSpeed;
    }

    /**
     * @param activeLinkSpeed
     *            the activeLinkSpeed to set
     */
    public void setActiveLinkSpeed(String activeLinkSpeed) {
        this.activeLinkSpeed = activeLinkSpeed;
    }

    /**
     * @return the enabledLinkSpeed
     */
    public String getEnabledLinkSpeed() {
        return enabledLinkSpeed;
    }

    /**
     * @param enabledLinkSpeed
     *            the enabledLinkSpeed to set
     */
    public void setEnabledLinkSpeed(String enabledLinkSpeed) {
        this.enabledLinkSpeed = enabledLinkSpeed;
    }

    /**
     * @return the supportedLinkSpeed
     */
    public String getSupportedLinkSpeed() {
        return supportedLinkSpeed;
    }

    /**
     * @param supportedLinkSpeed
     *            the supportedLinkSpeed to set
     */
    public void setSupportedLinkSpeed(String supportedLinkSpeed) {
        this.supportedLinkSpeed = supportedLinkSpeed;
    }

    public PerformanceData getPerformanceData() {
        return performance.get();
    }

    public void setPerformanceData(PerformanceData perfData) {
        performance.set(perfData);
    }

    public void setLinkQualityData(int linkQuality) {
        this.linkQuality = linkQuality;
    }

    public int getLinkQualityData() {
        return linkQuality;
    }

    public void clear() {
        String na = STLConstants.K0383_NA.getValue();
        slowLinkState = false;
        nodeName = na;
        linkState = na;
        physicalLinkState = na;
        activeLinkWidth = na;
        enabledLinkWidth = na;
        supportedLinkWidth = na;
        activeLinkWidthDnGrdTx = na;
        activeLinkWidthDnGrdRx = na;
        enabledLinkWidthDnGrd = na;
        supportedLinkWidthDnGrd = na;
        activeLinkSpeed = na;
        enabledLinkSpeed = na;
        supportedLinkSpeed = na;
        linkQuality = 0;
        PerformanceData perfData = getPerformanceData();
        if (perfData != null) {
            perfData.clear();
        }
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
        result = prime * result
                + (int) (nodeGuidValue ^ (nodeGuidValue >>> 32));
        result = prime * result + portNumValue;
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
        ConnectivityTableData other = (ConnectivityTableData) obj;
        if (nodeGuidValue != other.nodeGuidValue) {
            return false;
        }
        if (portNumValue != other.portNumValue) {
            return false;
        }
        return true;
    }

    public static class PerformanceData {
        private Long tx32BitWords; // TODO Don't know where to get this

        private Long rx32BitWords; // TODO Don't know where to get this

        private Long txPackets;

        private Long rxPackets;

        private Long portXmitData;

        private Long portRcvData;

        private Long numSymbolErrors; // TODO Don't know where to get this

        private Long numLinkRecoveries; // TODO Don't know where to get this

        private Long numLinkDown;

        private Byte numLanesDown;

        private Long rxErrors;

        private Long rxRemotePhysicalErrors;

        private Long txDiscards;

        private Long localLinkIntegrityErrors;

        private Long excessiveBufferOverruns;

        private Long switchRelayErrors;

        private Long txConstraints;

        private Long rxConstraints;

        private Long vl15Dropped; // TODO Don't know where to get this

        private Long fmConfigErrors;

        private Long portMulticastRcvPkts;

        private Long portRcvFECN;

        private Long portRcvBECN;

        private Long portRcvBubble;

        private Long portMulticastXmitPkts;

        private Long portXmitWait;

        private Long portXmitTimeCong;

        private Long portXmitWastedBW;

        private Long portXmitWaitData;

        private Long portMarkFECN;

        private Short uncorrectableErrors; // unsigned byte

        private Long swPortCongestion;

        /**
         * @return the tx32BitWords
         */
        public Long getTx32BitWords() {
            return tx32BitWords;
        }

        /**
         * @param tx32BitWords
         *            the tx32BitWords to set
         */
        public void setTx32BitWords(Long tx32BitWords) {
            this.tx32BitWords = tx32BitWords;
        }

        /**
         * @return the rx32BitWords
         */
        public Long getRx32BitWords() {
            return rx32BitWords;
        }

        /**
         * @param rx32BitWords
         *            the rx32BitWords to set
         */
        public void setRx32BitWords(Long rx32BitWords) {
            this.rx32BitWords = rx32BitWords;
        }

        /**
         * @return the txPackets
         */
        public Long getTxPackets() {
            return txPackets;
        }

        /**
         * @param txPackets
         *            the txPackets to set
         */
        public void setTxPackets(Long txPackets) {
            this.txPackets = txPackets;
        }

        /**
         * @return the rxPackets
         */
        public Long getRxPackets() {
            return rxPackets;
        }

        /**
         * @param rxPackets
         *            the rxPackets to set
         */
        public void setRxPackets(Long rxPackets) {
            this.rxPackets = rxPackets;
        }

        /**
         * @return the numSymbolErrors
         */
        public Long getNumSymbolErrors() {
            return numSymbolErrors;
        }

        /**
         * @param numSymbolErrors
         *            the numSymbolErrors to set
         */
        public void setNumSymbolErrors(Long numSymbolErrors) {
            this.numSymbolErrors = numSymbolErrors;
        }

        /**
         * @return the numLinkRecoveries
         */
        public Long getNumLinkRecoveries() {
            return numLinkRecoveries;
        }

        /**
         * @param numLinkRecoveries
         *            the numLinkRecoveries to set
         */
        public void setNumLinkRecoveries(Long numLinkRecoveries) {
            this.numLinkRecoveries = numLinkRecoveries;
        }

        /**
         * @return the numLinkDown
         */
        public Long getNumLinkDown() {
            return numLinkDown;
        }

        /**
         * @param numLinkDown
         *            the numLinkDown to set
         */
        public void setNumLinkDown(Long numLinkDown) {
            this.numLinkDown = numLinkDown;
        }

        /**
         * @return the numLanesDown
         */
        public Byte getNumLanesDown() {
            return numLanesDown;
        }

        /**
         * @param numLanesDown
         *            the numLanesDown to set
         */
        public void setNumLanesDown(Byte numLanesDown) {
            this.numLanesDown = numLanesDown;
        }

        /**
         * @return the rxErrors
         */
        public Long getRxErrors() {
            return rxErrors;
        }

        /**
         * @param rxErrors
         *            the rxErrors to set
         */
        public void setRxErrors(Long rxErrors) {
            this.rxErrors = rxErrors;
        }

        /**
         * @return the rxRemotePhysicalErrors
         */
        public Long getRxRemotePhysicalErrors() {
            return rxRemotePhysicalErrors;
        }

        /**
         * @param rxRemotePhysicalErrors
         *            the rxRemotePhysicalErrors to set
         */
        public void setRxRemotePhysicalErrors(Long rxRemotePhysicalErrors) {
            this.rxRemotePhysicalErrors = rxRemotePhysicalErrors;
        }

        /**
         * @return the txDiscards
         */
        public Long getTxDiscards() {
            return txDiscards;
        }

        /**
         * @param txDiscards
         *            the txDiscards to set
         */
        public void setTxDiscards(Long txDiscards) {
            this.txDiscards = txDiscards;
        }

        /**
         * @return the localLinkIntegrityErrors
         */
        public Long getLocalLinkIntegrityErrors() {
            return localLinkIntegrityErrors;
        }

        /**
         * @param localLinkIntegrityErrors
         *            the localLinkIntegrityErrors to set
         */
        public void setLocalLinkIntegrityErrors(Long localLinkIntegrityErrors) {
            this.localLinkIntegrityErrors = localLinkIntegrityErrors;
        }

        /**
         * @return the excessiveBufferOverruns
         */
        public Long getExcessiveBufferOverruns() {
            return excessiveBufferOverruns;
        }

        /**
         * @param excessiveBufferOverruns
         *            the excessiveBufferOverruns to set
         */
        public void setExcessiveBufferOverruns(Long excessiveBufferOverruns) {
            this.excessiveBufferOverruns = excessiveBufferOverruns;
        }

        /**
         * @return the switchRelayErrors
         */
        public Long getSwitchRelayErrors() {
            return switchRelayErrors;
        }

        /**
         * @param switchRelayErrors
         *            the switchRelayErrors to set
         */
        public void setSwitchRelayErrors(Long switchRelayErrors) {
            this.switchRelayErrors = switchRelayErrors;
        }

        /**
         * @return the txConstraints
         */
        public Long getTxConstraints() {
            return txConstraints;
        }

        /**
         * @param txConstraints
         *            the txConstraints to set
         */
        public void setTxConstraints(Long txConstraints) {
            this.txConstraints = txConstraints;
        }

        /**
         * @return the rxConstraints
         */
        public Long getRxConstraints() {
            return rxConstraints;
        }

        /**
         * @param rxConstraints
         *            the rxConstraints to set
         */
        public void setRxConstraints(Long rxConstraints) {
            this.rxConstraints = rxConstraints;
        }

        /**
         * @return the vl15Dropped
         */
        public Long getVl15Dropped() {
            return vl15Dropped;
        }

        /**
         * @param vl15Dropped
         *            the vl15Dropped to set
         */
        public void setVl15Dropped(Long vl15Dropped) {
            this.vl15Dropped = vl15Dropped;
        }

        /**
         * @return the portXmitData
         */
        public Long getPortXmitData() {
            return portXmitData;
        }

        /**
         * @return the portRcvData
         */
        public Long getPortRcvData() {
            return portRcvData;
        }

        /**
         * @return the fmConfigErrors
         */
        public Long getFmConfigErrors() {
            return fmConfigErrors;
        }

        /**
         * @return the portMulticastRcvPkts
         */
        public Long getPortMulticastRcvPkts() {
            return portMulticastRcvPkts;
        }

        /**
         * @return the portRcvFECN
         */
        public Long getPortRcvFECN() {
            return portRcvFECN;
        }

        /**
         * @return the portRcvBECN
         */
        public Long getPortRcvBECN() {
            return portRcvBECN;
        }

        /**
         * @return the portRcvBubble
         */
        public Long getPortRcvBubble() {
            return portRcvBubble;
        }

        /**
         * @return the portMulticastXmitPkts
         */
        public Long getPortMulticastXmitPkts() {
            return portMulticastXmitPkts;
        }

        /**
         * @return the portXmitWait
         */
        public Long getPortXmitWait() {
            return portXmitWait;
        }

        /**
         * @return the portXmitTimeCong
         */
        public Long getPortXmitTimeCong() {
            return portXmitTimeCong;
        }

        /**
         * @return the portXmitWastedBW
         */
        public Long getPortXmitWastedBW() {
            return portXmitWastedBW;
        }

        /**
         * @return the portXmitWaitData
         */
        public Long getPortXmitWaitData() {
            return portXmitWaitData;
        }

        /**
         * @return the portMarkFECN
         */
        public Long getPortMarkFECN() {
            return portMarkFECN;
        }

        /**
         * @return the uncorrectableErrors
         */
        public Short getUncorrectableErrors() {
            return uncorrectableErrors;
        }

        /**
         * @return the swPortCongestion
         */
        public Long getSwPortCongestion() {
            return swPortCongestion;
        }

        /**
         * @param portXmitData
         *            the portXmitData to set
         */
        public void setPortXmitData(Long portXmitData) {
            this.portXmitData = portXmitData;
        }

        /**
         * @param portRcvData
         *            the portRcvData to set
         */
        public void setPortRcvData(Long portRcvData) {
            this.portRcvData = portRcvData;
        }

        /**
         * @param fmConfigErrors
         *            the fmConfigErrors to set
         */
        public void setFmConfigErrors(Long fmConfigErrors) {
            this.fmConfigErrors = fmConfigErrors;
        }

        /**
         * @param portMulticastRcvPkts
         *            the portMulticastRcvPkts to set
         */
        public void setPortMulticastRcvPkts(Long portMulticastRcvPkts) {
            this.portMulticastRcvPkts = portMulticastRcvPkts;
        }

        /**
         * @param portRcvFECN
         *            the portRcvFECN to set
         */
        public void setPortRcvFECN(Long portRcvFECN) {
            this.portRcvFECN = portRcvFECN;
        }

        /**
         * @param portRcvBECN
         *            the portRcvBECN to set
         */
        public void setPortRcvBECN(Long portRcvBECN) {
            this.portRcvBECN = portRcvBECN;
        }

        /**
         * @param portRcvBubble
         *            the portRcvBubble to set
         */
        public void setPortRcvBubble(Long portRcvBubble) {
            this.portRcvBubble = portRcvBubble;
        }

        /**
         * @param portMulticastXmitPkts
         *            the portMulticastXmitPkts to set
         */
        public void setPortMulticastXmitPkts(Long portMulticastXmitPkts) {
            this.portMulticastXmitPkts = portMulticastXmitPkts;
        }

        /**
         * @param portXmitWait
         *            the portXmitWait to set
         */
        public void setPortXmitWait(Long portXmitWait) {
            this.portXmitWait = portXmitWait;
        }

        /**
         * @param portXmitTimeCong
         *            the portXmitTimeCong to set
         */
        public void setPortXmitTimeCong(Long portXmitTimeCong) {
            this.portXmitTimeCong = portXmitTimeCong;
        }

        /**
         * @param portXmitWastedBW
         *            the portXmitWastedBW to set
         */
        public void setPortXmitWastedBW(Long portXmitWastedBW) {
            this.portXmitWastedBW = portXmitWastedBW;
        }

        /**
         * @param portXmitWaitData
         *            the portXmitWaitData to set
         */
        public void setPortXmitWaitData(Long portXmitWaitData) {
            this.portXmitWaitData = portXmitWaitData;
        }

        /**
         * @param portMarkFECN
         *            the portMarkFECN to set
         */
        public void setPortMarkFECN(Long portMarkFECN) {
            this.portMarkFECN = portMarkFECN;
        }

        /**
         * @param uncorrectableErrors
         *            the uncorrectableErrors to set
         */
        public void setUncorrectableErrors(Short uncorrectableErrors) {
            this.uncorrectableErrors = uncorrectableErrors;
        }

        /**
         * @param swPortCongestion
         *            the swPortCongestion to set
         */
        public void setSwPortCongestion(Long swPortCongestion) {
            this.swPortCongestion = swPortCongestion;
        }

        public void clear() {
            tx32BitWords = null;
            rx32BitWords = null;
            txPackets = null;
            rxPackets = null;
            numSymbolErrors = null;
            numLinkRecoveries = null;
            numLinkDown = null;
            rxErrors = null;
            rxRemotePhysicalErrors = null;
            txDiscards = null;
            localLinkIntegrityErrors = null;
            excessiveBufferOverruns = null;
            switchRelayErrors = null;
            txConstraints = null;
            rxConstraints = null;
            vl15Dropped = null;
            fmConfigErrors = null;
            portMulticastRcvPkts = null;
            portRcvFECN = null;
            portRcvBECN = null;
            portRcvBubble = null;
            portMulticastXmitPkts = null;
            portXmitWait = null;
            portXmitTimeCong = null;
            portXmitWastedBW = null;
            portXmitWaitData = null;
            portMarkFECN = null;
            uncorrectableErrors = null;
            swPortCongestion = null;
        }
    }

}
