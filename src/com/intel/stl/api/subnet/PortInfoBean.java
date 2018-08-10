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
 * Title:        PortInfoBean
 * Description:  Port Info from SA populated by the connect manager.
 *
 * @version 0.0
 */
import java.io.Serializable;
import java.util.Arrays;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.MTUSize;

public class PortInfoBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private int lid;

    private int flowControlMask;

    private VirtualLaneBean vl;

    private PortStatesBean portStates;

    // PortPhyConfig
    private byte portType;

    // MultiCollectMask
    private byte collectiveMask;

    private byte multicastMask;

    // s1
    private byte mKeyProtectBits;

    private byte lmc;

    // s2
    private byte masterSMSL;

    // s3
    private byte linkInitReason;

    private boolean partitionEnforcementInbound;

    private boolean partitionEnforcementOutbound;

    // s4
    private byte operationalVL;

    // P_keys
    private short pKey8B;

    private short pKey10B;

    // Violations
    private short mKeyViolation;

    private short pKeyViolation;

    private short qKeyViolation;

    // SM_TrapQP
    private int smTrapQueuePair;

    // SA_QP
    private int saQueuePair;

    private short neighborPortNum; // promote to handle unsigned byte

    private byte linkDownReason;

    private byte neighborLinkDownReason;

    // Reserved22
    // Subnet
    private boolean clientReregister;

    private byte mulPKeyTrapSuppressionEnabled;

    private byte subnetTimeout;

    // Linkspeed
    private short linkSpeedSupported;

    private short linkSpeedEnabled;

    private short linkSpeedActive;

    // Linkwidth
    private short linkWidthSupported;

    private short linkWidthEnabled;

    private short linkWidthActive;

    // LinkwidthDowngrade
    private short linkWidthDownSupported;

    private short linkWidthDownEnabled;

    private short linkWidthDownTxActive;

    private short linkWidthDownRxActive;

    // PortLinkMode
    private byte plmSupported;

    private byte plmEnabled;

    private byte plmActive;

    // PortLTPCRMode;
    private byte pLTPCRCModeSupported;

    private byte pLTPCRCModeEnabled;

    private byte pLTPCRCModeActive;

    // PortMode
    private boolean isActiveOptimizeEnabled;

    private boolean isPassThroughEnabled;

    private boolean isVLMarkerEnabled;

    private boolean is16BTrapQueryEnabled;

    // PortPacketFormats
    private short ppfSupported;

    private short ppfEnabled;

    // FlitControl
    private FlitControlBean flitControl;

    // we don't show it in properties page because opasaquery doesn't display it
    private long maxLid; // promoted to support unsigned int

    // PortErrorAction
    private int portErrorAction;

    // private boolean excessiveBufferOverrun;
    // private boolean fmConfigErrorExceedMulticastLimit;
    // private boolean fmConfigErrorBadControlFlit;
    // private boolean fmConfigErrorBadPreempt;
    // private boolean fmConfigErrorUnsupportedVLMarker;
    // private boolean fmConfigErrorBadCrdtAck;
    // private boolean fmConfigErrorBadCtrlDist;
    // private boolean fmConfigErrorBadTailDist;
    // private boolean fmConfigErrorBadHeadDist;
    // private boolean portRcvErrorBadVLMarker;
    // private boolean portRcvErrorPreemptVL15;
    // private boolean portRcvErrorPreemptError;
    // private boolean portRcvErrorBadMidTail;
    // private boolean portRcvErrorReserved;
    // private boolean portRcvErrorBadSC;
    // private boolean portRcvErrorBadL2;
    // private boolean portRcvErrorBadDLID;
    // private boolean portRcvErrorBadSLID;
    // private boolean portRcvErrorPktLenTooShort;
    // private boolean portRcvErrorPktLenTooLong;
    // private boolean portRcvErrorBadPktLen;
    // PassThroughControl
    private short egressPort; // promote to handle unsigned short

    private boolean drControl;

    private int mKeyLeasePeriod; // promote to handle unsigned short

    // BufferUnits
    private short vl15Init;

    private byte vl15CreditRate;

    private byte creditAck;

    private byte bufferAlloc;

    // Reserved14
    private int masterSMLID;

    private long mKey;

    private long subnetPrefix;

    // STL_VL_TO_MTU NeighborMTU[STL_MAX_VLS / 2];
    // max size 16.
    // Probably should be saved as BLOB to database.
    // typedef union {
    // uint8 AsReg8;
    // struct { IB_BITFIELD2( uint8, /* RW/HS-E Neighbor MTU values per VL */
    // /* LUD: 2048 MTU for STL, 256 for IB link mode */
    // VL0_to_MTU: 4,
    // VL1_to_MTU: 4 )
    // } s;
    // } STL_VL_TO_MTU;
    private byte[] neighborVL0MTU;

    private byte[] neighborVL1MTU;

    // XmitQ, max size 32.
    private byte[] vlStallCount;

    private byte[] hoqLife;

    // STL_IPV6_IP_ADDR;
    // max size 16
    // /* STL IPV6 IP Address (128 bits) */
    // typedef struct {
    // uint8 addr[16];
    // } PACK_SUFFIX STL_IPV6_IP_ADDR;
    private byte[] ipAddrIPV6;

    private byte[] ipAddrIPV4;

    private long neighborNodeGUID;

    // We don't want to save 30 different boolean in database.
    // We would rather save as a 32 bit integer and decode it when we use it.
    private int capabilityMask;

    // Reserved20
    private short capabilityMask3;

    // Reserved23
    private int overallBufferSpace; // promote to handle unsigned short

    // we don't show it in properties page because opasaquery doesn't display it
    // ReplayDepthH
    private short bufferDepthH; // promote to handle unsigned byte

    private short wireDepthH; // promote to handle unsigned byte

    // DiagCode
    private byte universalDiagCode;

    private short vendorDiagCode;

    private boolean chain;

    // ReplayDepth
    private short bufferDepth; // promote to handle unsigned byte

    private short wireDepth; // promote to handle unsigned byte

    // PortNeighborMode
    private boolean mgmtAllowed;

    private boolean neighborFWAuthenBypass;

    private byte neighborNodeType;

    // MTU
    private MTUSize mtuCap;

    // Resp
    private byte respTimeValue;

    private short localPortNum; // promote to handle unsigned byte

    // Reserved25
    // Reserved24

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @param lid
     *            the lid to set
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * @return the flowControlMask
     */
    public int getFlowControlMask() {
        return flowControlMask;
    }

    /**
     * @param flowControlMask
     *            the flowControlMask to set
     */
    public void setFlowControlMask(int flowControlMask) {
        this.flowControlMask = flowControlMask;
    }

    /**
     * @return the vl
     */
    public VirtualLaneBean getVl() {
        return vl;
    }

    /**
     * @param vl
     *            the vl to set
     */
    public void setVl(VirtualLaneBean vl) {
        this.vl = vl;
    }

    /**
     * @return the portStates
     */
    public PortStatesBean getPortStates() {
        return portStates;
    }

    /**
     * @param portStates
     *            the portStates to set
     */
    public void setPortStates(PortStatesBean portStates) {
        this.portStates = portStates;
    }

    /**
     * @return the portType
     */
    public byte getPortType() {
        return portType;
    }

    /**
     * @param portType
     *            the portType to set
     */
    public void setPortType(byte portType) {
        this.portType = portType;
    }

    /**
     * @return the collectiveMask
     */
    public byte getCollectiveMask() {
        return collectiveMask;
    }

    /**
     * @param collectiveMask
     *            the collectiveMask to set
     */
    public void setCollectiveMask(byte collectiveMask) {
        this.collectiveMask = collectiveMask;
    }

    /**
     * @return the multicastMask
     */
    public byte getMulticastMask() {
        return multicastMask;
    }

    /**
     * @param multicastMask
     *            the multicastMask to set
     */
    public void setMulticastMask(byte multicastMask) {
        this.multicastMask = multicastMask;
    }

    /**
     * @return the mKeyProtectBits
     */
    public byte getMKeyProtectBits() {
        return mKeyProtectBits;
    }

    /**
     * @param mKeyProtectBits
     *            the mKeyProtectBits to set
     */
    public void setMKeyProtectBits(byte mKeyProtectBits) {
        this.mKeyProtectBits = mKeyProtectBits;
    }

    /**
     * @return the lmc
     */
    public byte getLmc() {
        return lmc;
    }

    /**
     * @param lmc
     *            the lmc to set
     */
    public void setLmc(byte lmc) {
        this.lmc = lmc;
    }

    /**
     * @return the masterSMSL
     */
    public byte getMasterSMSL() {
        return masterSMSL;
    }

    /**
     * @param masterSMSL
     *            the masterSMSL to set
     */
    public void setMasterSMSL(byte masterSMSL) {
        this.masterSMSL = masterSMSL;
    }

    /**
     * @return the linkInitReason
     */
    public byte getLinkInitReason() {
        return linkInitReason;
    }

    /**
     * @param linkInitReason
     *            the linkInitReason to set
     */
    public void setLinkInitReason(byte linkInitReason) {
        this.linkInitReason = linkInitReason;
    }

    /**
     * @return the partitionEnforcementInbound
     */
    public boolean isPartitionEnforcementInbound() {
        return partitionEnforcementInbound;
    }

    /**
     * @param partitionEnforcementInbound
     *            the partitionEnforcementInbound to set
     */
    public void setPartitionEnforcementInbound(
            boolean partitionEnforcementInbound) {
        this.partitionEnforcementInbound = partitionEnforcementInbound;
    }

    /**
     * @return the partitionEnforcementOutbound
     */
    public boolean isPartitionEnforcementOutbound() {
        return partitionEnforcementOutbound;
    }

    /**
     * @param partitionEnforcementOutbound
     *            the partitionEnforcementOutbound to set
     */
    public void setPartitionEnforcementOutbound(
            boolean partitionEnforcementOutbound) {
        this.partitionEnforcementOutbound = partitionEnforcementOutbound;
    }

    /**
     * @return the operationalVL
     */
    public byte getOperationalVL() {
        return operationalVL;
    }

    /**
     * @param operationalVL
     *            the operationalVL to set
     */
    public void setOperationalVL(byte operationalVL) {
        this.operationalVL = operationalVL;
    }

    /**
     * @return the pKey8B
     */
    public short getPKey8B() {
        return pKey8B;
    }

    /**
     * @param pKey8B
     *            the pKey8B to set
     */
    public void setPKey8B(short pKey8B) {
        this.pKey8B = pKey8B;
    }

    /**
     * @return the pKey10B
     */
    public short getPKey10B() {
        return pKey10B;
    }

    /**
     * @param pKey10B
     *            the pKey10B to set
     */
    public void setPKey10B(short pKey10B) {
        this.pKey10B = pKey10B;
    }

    /**
     * @return the mKeyViolation
     */
    public short getMKeyViolation() {
        return mKeyViolation;
    }

    /**
     * @param mKeyViolation
     *            the mKeyViolation to set
     */
    public void setMKeyViolation(short mKeyViolation) {
        this.mKeyViolation = mKeyViolation;
    }

    /**
     * @return the pKeyViolation
     */
    public short getPKeyViolation() {
        return pKeyViolation;
    }

    /**
     * @param pKeyViolation
     *            the pKeyViolation to set
     */
    public void setPKeyViolation(short pKeyViolation) {
        this.pKeyViolation = pKeyViolation;
    }

    /**
     * @return the qKeyViolation
     */
    public short getQKeyViolation() {
        return qKeyViolation;
    }

    /**
     * @param qKeyViolation
     *            the qKeyViolation to set
     */
    public void setQKeyViolation(short qKeyViolation) {
        this.qKeyViolation = qKeyViolation;
    }

    /**
     * @return the smTrapQueuePair
     */
    public int getSmTrapQueuePair() {
        return smTrapQueuePair;
    }

    /**
     * @param smTrapQueuePair
     *            the smTrapQueuePair to set
     */
    public void setSmTrapQueuePair(int smTrapQueuePair) {
        this.smTrapQueuePair = smTrapQueuePair;
    }

    /**
     * @return the saQueuePair
     */
    public int getSaQueuePair() {
        return saQueuePair;
    }

    /**
     * @param saQueuePair
     *            the saQueuePair to set
     */
    public void setSaQueuePair(int saQueuePair) {
        this.saQueuePair = saQueuePair;
    }

    /**
     * @return the neighborPortNum
     */
    public short getNeighborPortNum() {
        return neighborPortNum;
    }

    /**
     * @param neighborPortNum
     *            the neighborPortNum to set
     */
    public void setNeighborPortNum(short neighborPortNum) {
        this.neighborPortNum = neighborPortNum;
    }

    /**
     * @param neighborPortNum
     *            the neighborPortNum to set
     */
    public void setNeighborPortNum(byte neighborPortNum) {
        this.neighborPortNum = Utils.unsignedByte(neighborPortNum);
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
     * @return the clientReregister
     */
    public boolean isClientReregister() {
        return clientReregister;
    }

    /**
     * @param clientReregister
     *            the clientReregister to set
     */
    public void setClientReregister(boolean clientReregister) {
        this.clientReregister = clientReregister;
    }

    /**
     * @return the mulPKeyTrapSuppressionEnabled
     */
    public byte getMulPKeyTrapSuppressionEnabled() {
        return mulPKeyTrapSuppressionEnabled;
    }

    /**
     * @param mulPKeyTrapSuppressionEnabled
     *            the mulPKeyTrapSuppressionEnabled to set
     */
    public void setMulPKeyTrapSuppressionEnabled(
            byte mulPKeyTrapSuppressionEnabled) {
        this.mulPKeyTrapSuppressionEnabled = mulPKeyTrapSuppressionEnabled;
    }

    /**
     * @return the timeout
     */
    public byte getSubnetTimeout() {
        return subnetTimeout;
    }

    /**
     * @param timeout
     *            the timeout to set
     */
    public void setSubnetTimeout(byte subnetTimeout) {
        this.subnetTimeout = subnetTimeout;
    }

    /**
     * @return the linkSpeedSupported
     */
    public short getLinkSpeedSupported() {
        return linkSpeedSupported;
    }

    /**
     * @param linkSpeedSupported
     *            the linkSpeedSupported to set
     */
    public void setLinkSpeedSupported(short linkSpeedSupported) {
        this.linkSpeedSupported = linkSpeedSupported;
    }

    /**
     * @return the linkSpeedEnabled
     */
    public short getLinkSpeedEnabled() {
        return linkSpeedEnabled;
    }

    /**
     * @param linkSpeedEnabled
     *            the linkSpeedEnabled to set
     */
    public void setLinkSpeedEnabled(short linkSpeedEnabled) {
        this.linkSpeedEnabled = linkSpeedEnabled;
    }

    /**
     * @return the linkSpeedActive
     */
    public short getLinkSpeedActive() {
        return linkSpeedActive;
    }

    /**
     * @param linkSpeedActive
     *            the linkSpeedActive to set
     */
    public void setLinkSpeedActive(short linkSpeedActive) {
        this.linkSpeedActive = linkSpeedActive;
    }

    /**
     * @return the linkWidthSupported
     */
    public short getLinkWidthSupported() {
        return linkWidthSupported;
    }

    /**
     * @param linkWidthSupported
     *            the linkWidthSupported to set
     */
    public void setLinkWidthSupported(short linkWidthSupported) {
        this.linkWidthSupported = linkWidthSupported;
    }

    /**
     * @return the linkWidthEnabled
     */
    public short getLinkWidthEnabled() {
        return linkWidthEnabled;
    }

    /**
     * @param linkWidthEnabled
     *            the linkWidthEnabled to set
     */
    public void setLinkWidthEnabled(short linkWidthEnabled) {
        this.linkWidthEnabled = linkWidthEnabled;
    }

    /**
     * @return the linkWidthActive
     */
    public short getLinkWidthActive() {
        return linkWidthActive;
    }

    /**
     * @param linkWidthActive
     *            the linkWidthActive to set
     */
    public void setLinkWidthActive(short linkWidthActive) {
        this.linkWidthActive = linkWidthActive;
    }

    /**
     * @return the linkWidthDownSupported
     */
    public short getLinkWidthDownSupported() {
        return linkWidthDownSupported;
    }

    /**
     * @param linkWidthDownSupported
     *            the linkWidthDownSupported to set
     */
    public void setLinkWidthDownSupported(short linkWidthDownSupported) {
        this.linkWidthDownSupported = linkWidthDownSupported;
    }

    /**
     * @return the linkWidthDownEnabled
     */
    public short getLinkWidthDownEnabled() {
        return linkWidthDownEnabled;
    }

    /**
     * @param linkWidthDownEnabled
     *            the linkWidthDownEnabled to set
     */
    public void setLinkWidthDownEnabled(short linkWidthDownEnabled) {
        this.linkWidthDownEnabled = linkWidthDownEnabled;
    }

    /**
     * @return the linkWidthDownTxActive
     */
    public short getLinkWidthDownTxActive() {
        return linkWidthDownTxActive;
    }

    /**
     * @param linkWidthDownTxActive
     *            the linkWidthDownTxActive to set
     */
    public void setLinkWidthDownTxActive(short linkWidthDownTxActive) {
        this.linkWidthDownTxActive = linkWidthDownTxActive;
    }

    /**
     * @return the linkWidthDownRxActive
     */
    public short getLinkWidthDownRxActive() {
        return linkWidthDownRxActive;
    }

    /**
     * @param linkWidthDownRxActive
     *            the linkWidthDownRxActive to set
     */
    public void setLinkWidthDownRxActive(short linkWidthDownRxActive) {
        this.linkWidthDownRxActive = linkWidthDownRxActive;
    }

    /**
     * @return the plmSupported
     */
    public byte getPlmSupported() {
        return plmSupported;
    }

    /**
     * @param plmSupported
     *            the plmSupported to set
     */
    public void setPlmSupported(byte plmSupported) {
        this.plmSupported = plmSupported;
    }

    /**
     * @return the plmEnabled
     */
    public byte getPlmEnabled() {
        return plmEnabled;
    }

    /**
     * @param plmEnabled
     *            the plmEnabled to set
     */
    public void setPlmEnabled(byte plmEnabled) {
        this.plmEnabled = plmEnabled;
    }

    /**
     * @return the plmActive
     */
    public byte getPlmActive() {
        return plmActive;
    }

    /**
     * @param plmActive
     *            the plmActive to set
     */
    public void setPlmActive(byte plmActive) {
        this.plmActive = plmActive;
    }

    /**
     * @return the pLTPCRCModeSupported
     */
    public byte getPLTPCRCModeSupported() {
        return pLTPCRCModeSupported;
    }

    /**
     * @param pLTPCRCModeSupported
     *            the pLTPCRCModeSupported to set
     */
    public void setPLTPCRCModeSupported(byte pLTPCRCModeSupported) {
        this.pLTPCRCModeSupported = pLTPCRCModeSupported;
    }

    /**
     * @return the pLTPCRCModeEnabled
     */
    public byte getPLTPCRCModeEnabled() {
        return pLTPCRCModeEnabled;
    }

    /**
     * @param pLTPCRCModeEnabled
     *            the pLTPCRCModeEnabled to set
     */
    public void setPLTPCRCModeEnabled(byte pLTPCRCModeEnabled) {
        this.pLTPCRCModeEnabled = pLTPCRCModeEnabled;
    }

    /**
     * @return the pLTPCRCModeActive
     */
    public byte getPLTPCRCModeActive() {
        return pLTPCRCModeActive;
    }

    /**
     * @param pLTPCRCModeActive
     *            the pLTPCRCModeActive to set
     */
    public void setPLTPCRCModeActive(byte pLTPCRCModeActive) {
        this.pLTPCRCModeActive = pLTPCRCModeActive;
    }

    /**
     * @return the isActiveOptimizeEnabled
     */
    public boolean isActiveOptimizeEnabled() {
        return isActiveOptimizeEnabled;
    }

    /**
     * @param isActiveOptimizeEnabled
     *            the isActiveOptimizeEnabled to set
     */
    public void setActiveOptimizeEnabled(boolean isActiveOptimizeEnabled) {
        this.isActiveOptimizeEnabled = isActiveOptimizeEnabled;
    }

    /**
     * @return the isPassThroughEnabled
     */
    public boolean isPassThroughEnabled() {
        return isPassThroughEnabled;
    }

    /**
     * @param isPassThroughEnabled
     *            the isPassThroughEnabled to set
     */
    public void setPassThroughEnabled(boolean isPassThroughEnabled) {
        this.isPassThroughEnabled = isPassThroughEnabled;
    }

    /**
     * @return the isVLMarkerEnabled
     */
    public boolean isVLMarkerEnabled() {
        return isVLMarkerEnabled;
    }

    /**
     * @param isVLMarkerEnabled
     *            the isVLMarkerEnabled to set
     */
    public void setVLMarkerEnabled(boolean isVLMarkerEnabled) {
        this.isVLMarkerEnabled = isVLMarkerEnabled;
    }

    /**
     * @return the is16BTrapQueryEnabled
     */
    public boolean is16BTrapQueryEnabled() {
        return is16BTrapQueryEnabled;
    }

    /**
     * @param is16bTrapQueryEnabled
     *            the is16BTrapQueryEnabled to set
     */
    public void set16BTrapQueryEnabled(boolean is16bTrapQueryEnabled) {
        is16BTrapQueryEnabled = is16bTrapQueryEnabled;
    }

    /**
     * @return the ppfSupported
     */
    public short getPpfSupported() {
        return ppfSupported;
    }

    /**
     * @param ppfSupported
     *            the ppfSupported to set
     */
    public void setPpfSupported(short ppfSupported) {
        this.ppfSupported = ppfSupported;
    }

    /**
     * @return the ppfEnabled
     */
    public short getPpfEnabled() {
        return ppfEnabled;
    }

    /**
     * @param ppfEnabled
     *            the ppfEnabled to set
     */
    public void setPpfEnabled(short ppfEnabled) {
        this.ppfEnabled = ppfEnabled;
    }

    /**
     * @return the flitControl
     */
    public FlitControlBean getFlitControl() {
        return flitControl;
    }

    /**
     * @param flitControl
     *            the flitControl to set
     */
    public void setFlitControl(FlitControlBean flitControl) {
        this.flitControl = flitControl;
    }

    /**
     * @return the maxLid
     */
    public long getMaxLid() {
        return maxLid;
    }

    public void setMaxLid(int maxLid) {
        this.maxLid = Utils.unsignedInt(maxLid);
    }

    /**
     * @param maxLid
     *            the maxLid to set
     */
    public void setMaxLid(long maxLid) {
        this.maxLid = maxLid;
    }

    /**
     * @return the portErrorAction
     */
    public int getPortErrorAction() {
        return portErrorAction;
    }

    /**
     * @param portErrorAction
     *            the portErrorAction to set
     */
    public void setPortErrorAction(int portErrorAction) {
        this.portErrorAction = portErrorAction;
    }

    /**
     * @return the egressPort
     */
    public short getEgressPort() {
        return egressPort;
    }

    /**
     * @param egressPort
     *            the egressPort to set
     */
    public void setEgressPort(short egressPort) {
        this.egressPort = egressPort;
    }

    /**
     * @param egressPort
     *            the egressPort to set
     */
    public void setEgressPort(byte egressPort) {
        this.egressPort = Utils.unsignedByte(egressPort);
    }

    /**
     * @return the drControl
     */
    public boolean isDrControl() {
        return drControl;
    }

    /**
     * @param drControl
     *            the drControl to set
     */
    public void setDrControl(boolean drControl) {
        this.drControl = drControl;
    }

    /**
     * @return the mKeyLeasePeriod
     */
    public int getMKeyLeasePeriod() {
        return mKeyLeasePeriod;
    }

    /**
     * @return the mKeyLeasePeriod
     */
    public int getmKeyLeasePeriod() {
        return mKeyLeasePeriod;
    }

    /**
     * @param mKeyLeasePeriod
     *            the mKeyLeasePeriod to set
     */
    public void setmKeyLeasePeriod(int mKeyLeasePeriod) {
        this.mKeyLeasePeriod = mKeyLeasePeriod;
    }

    /**
     * @param mKeyLeasePeriod
     *            the mKeyLeasePeriod to set
     */
    public void setMKeyLeasePeriod(short mKeyLeasePeriod) {
        this.mKeyLeasePeriod = Utils.unsignedShort(mKeyLeasePeriod);
    }

    /**
     * @return the vl15Init
     */
    public short getVl15Init() {
        return vl15Init;
    }

    /**
     * @param vl15Init
     *            the vl15Init to set
     */
    public void setVl15Init(short vl15Init) {
        this.vl15Init = vl15Init;
    }

    /**
     * @return the vl15CreditRate
     */
    public byte getVl15CreditRate() {
        return vl15CreditRate;
    }

    /**
     * @param vl15CreditRate
     *            the vl15CreditRate to set
     */
    public void setVl15CreditRate(byte vl15CreditRate) {
        this.vl15CreditRate = vl15CreditRate;
    }

    /**
     * @return the creditAck
     */
    public byte getCreditAck() {
        return creditAck;
    }

    /**
     * @param creditAck
     *            the creditAck to set
     */
    public void setCreditAck(byte creditAck) {
        this.creditAck = creditAck;
    }

    /**
     * @return the bufferAlloc
     */
    public byte getBufferAlloc() {
        return bufferAlloc;
    }

    /**
     * @param bufferAlloc
     *            the bufferAlloc to set
     */
    public void setBufferAlloc(byte bufferAlloc) {
        this.bufferAlloc = bufferAlloc;
    }

    /**
     * @return the masterSMLID
     */
    public int getMasterSMLID() {
        return masterSMLID;
    }

    /**
     * @param masterSMLID
     *            the masterSMLID to set
     */
    public void setMasterSMLID(int masterSMLID) {
        this.masterSMLID = masterSMLID;
    }

    /**
     * @return the mKey
     */
    public long getMKey() {
        return mKey;
    }

    /**
     * @param mKey
     *            the mKey to set
     */
    public void setMKey(long mKey) {
        this.mKey = mKey;
    }

    /**
     * @return the subnetPrefix
     */
    public long getSubnetPrefix() {
        return subnetPrefix;
    }

    /**
     * @param subnetPrefix
     *            the subnetPrefix to set
     */
    public void setSubnetPrefix(long subnetPrefix) {
        this.subnetPrefix = subnetPrefix;
    }

    /**
     * @return the vlStallCount
     */
    public byte[] getVlStallCount() {
        return vlStallCount;
    }

    /**
     * @param vlStallCount
     *            the vlStallCount to set
     */
    public void setVlStallCount(byte[] vlStallCount) {
        this.vlStallCount = vlStallCount;
    }

    /**
     * @return the neighborVL0MTU
     */
    public byte[] getNeighborVL0MTU() {
        return neighborVL0MTU;
    }

    /**
     * @param neighborVL0MTU
     *            the neighborVL0MTU to set
     */
    public void setNeighborVL0MTU(byte[] neighborVL0MTU) {
        this.neighborVL0MTU = neighborVL0MTU;
    }

    /**
     * @return the neighborVL1MTU
     */
    public byte[] getNeighborVL1MTU() {
        return neighborVL1MTU;
    }

    /**
     * @param neighborVL1MTU
     *            the neighborVL1MTU to set
     */
    public void setNeighborVL1MTU(byte[] neighborVL1MTU) {
        this.neighborVL1MTU = neighborVL1MTU;
    }

    /**
     * @return the hoqLife
     */
    public byte[] getHoqLife() {
        return hoqLife;
    }

    /**
     * @param hoqLife
     *            the hoqLife to set
     */
    public void setHoqLife(byte[] hoqLife) {
        this.hoqLife = hoqLife;
    }

    /**
     * @return the ipAddrIPV6
     */
    public byte[] getIpAddrIPV6() {
        return ipAddrIPV6;
    }

    /**
     * @param ipAddrIPV6
     *            the ipAddrIPV6 to set
     */
    public void setIpAddrIPV6(byte[] ipAddrIPV6) {
        this.ipAddrIPV6 = ipAddrIPV6;
    }

    /**
     * @return the ipAddrIPV4
     */
    public byte[] getIpAddrIPV4() {
        return ipAddrIPV4;
    }

    /**
     * @param ipAddrSecondary
     *            the ipAddrSecondary to set
     */
    public void setIpAddrIPV4(byte[] ipAddrIPV4) {
        this.ipAddrIPV4 = ipAddrIPV4;
    }

    /**
     * @return the neighborNodeGUID
     */
    public long getNeighborNodeGUID() {
        return neighborNodeGUID;
    }

    /**
     * @param neighborNodeGUID
     *            the neighborNodeGUID to set
     */
    public void setNeighborNodeGUID(long neighborNodeGUID) {
        this.neighborNodeGUID = neighborNodeGUID;
    }

    /**
     * @return the capabilityMask
     */
    public int getCapabilityMask() {
        return capabilityMask;
    }

    /**
     * @param capabilityMask
     *            the capabilityMask to set
     */
    public void setCapabilityMask(int capabilityMask) {
        this.capabilityMask = capabilityMask;
    }

    /**
     * @return the capabilityMask3
     */
    public short getCapabilityMask3() {
        return capabilityMask3;
    }

    /**
     * @param capabilityMask3
     *            the capabilityMask3 to set
     */
    public void setCapabilityMask3(short capabilityMask3) {
        this.capabilityMask3 = capabilityMask3;
    }

    /**
     * @return the overallBufferSpace
     */
    public int getOverallBufferSpace() {
        return overallBufferSpace;
    }

    /**
     * @param overallBufferSpace
     *            the overallBufferSpace to set
     */
    public void setOverallBufferSpace(int overallBufferSpace) {
        this.overallBufferSpace = overallBufferSpace;
    }

    /**
     * @param overallBufferSpace
     *            the overallBufferSpace to set
     */
    public void setOverallBufferSpace(short overallBufferSpace) {
        this.overallBufferSpace = Utils.unsignedShort(overallBufferSpace);
    }

    /**
     * @return the bufferDepth
     */
    public short getBufferDepthH() {
        return bufferDepthH;
    }

    /**
     * @param bufferDepth
     *            the bufferDepth to set
     */
    public void setBufferDepthH(short bufferDepthH) {
        this.bufferDepthH = bufferDepthH;
    }

    /**
     * @param bufferDepth
     *            the bufferDepth to set
     */
    public void setBufferDepthH(byte bufferDepthH) {
        this.bufferDepthH = Utils.unsignedByte(bufferDepthH);
    }

    /**
     * @return the wireDepth
     */
    public short getWireDepthH() {
        return wireDepthH;
    }

    /**
     * @param wireDepth
     *            the wireDepth to set
     */
    public void setWireDepthH(short wireDepthH) {
        this.wireDepthH = wireDepthH;
    }

    /**
     * @param wireDepth
     *            the wireDepth to set
     */
    public void setWireDepthH(byte wireDepthH) {
        this.wireDepthH = Utils.unsignedByte(wireDepthH);
    }

    /**
     * @return the universalDiagCode
     */
    public byte getUniversalDiagCode() {
        return universalDiagCode;
    }

    /**
     * @param universalDiagCode
     *            the universalDiagCode to set
     */
    public void setUniversalDiagCode(byte universalDiagCode) {
        this.universalDiagCode = universalDiagCode;
    }

    /**
     * @return the vendorDiagCode
     */
    public short getVendorDiagCode() {
        return vendorDiagCode;
    }

    /**
     * @param vendorDiagCode
     *            the vendorDiagCode to set
     */
    public void setVendorDiagCode(short vendorDiagCode) {
        this.vendorDiagCode = vendorDiagCode;
    }

    /**
     * @return the chain
     */
    public boolean isChain() {
        return chain;
    }

    /**
     * @param chain
     *            the chain to set
     */
    public void setChain(boolean chain) {
        this.chain = chain;
    }

    /**
     * @return the bufferDepth
     */
    public short getBufferDepth() {
        return bufferDepth;
    }

    /**
     * @param bufferDepth
     *            the bufferDepth to set
     */
    public void setBufferDepth(short bufferDepth) {
        this.bufferDepth = bufferDepth;
    }

    /**
     * @param bufferDepth
     *            the bufferDepth to set
     */
    public void setBufferDepth(byte bufferDepth) {
        this.bufferDepth = Utils.unsignedByte(bufferDepth);
    }

    /**
     * @return the wireDepth
     */
    public short getWireDepth() {
        return wireDepth;
    }

    /**
     * @param wireDepth
     *            the wireDepth to set
     */
    public void setWireDepth(short wireDepth) {
        this.wireDepth = wireDepth;
    }

    /**
     * @param wireDepth
     *            the wireDepth to set
     */
    public void setWireDepth(byte wireDepth) {
        this.wireDepth = Utils.unsignedByte(wireDepth);
    }

    /**
     * @return the mgmtAllowed
     */
    public boolean isMgmtAllowed() {
        return mgmtAllowed;
    }

    /**
     * @param mgmtAllowed
     *            the mgmtAllowed to set
     */
    public void setMgmtAllowed(boolean mgmtAllowed) {
        this.mgmtAllowed = mgmtAllowed;
    }

    /**
     * @return the neighborFWAuthenBypass
     */
    public boolean isNeighborFWAuthenBypass() {
        return neighborFWAuthenBypass;
    }

    /**
     * @param neighborFWAuthenBypass
     *            the neighborFWAuthenBypass to set
     */
    public void setNeighborFWAuthenBypass(boolean neighborFWAuthenBypass) {
        this.neighborFWAuthenBypass = neighborFWAuthenBypass;
    }

    /**
     * @return the neighborNodeType
     */
    public byte getNeighborNodeType() {
        return neighborNodeType;
    }

    /**
     * @param neighborNodeType
     *            the neighborNodeType to set
     */
    public void setNeighborNodeType(byte neighborNodeType) {
        this.neighborNodeType = neighborNodeType;
    }

    /**
     * @return the cap
     */
    public MTUSize getMtuCap() {
        return mtuCap;
    }

    /**
     * @param cap
     *            the cap to set
     */
    public void setMtuCap(byte mtuCap) {
        this.mtuCap = MTUSize.getMTUSize(mtuCap);
    }

    public void setMtuCap(MTUSize mtuCap) {
        this.mtuCap = mtuCap;
    }

    /**
     * @return the timeValue
     */
    public byte getRespTimeValue() {
        return respTimeValue;
    }

    /**
     * @param timeValue
     *            the timeValue to set
     */
    public void setRespTimeValue(byte respTimeValue) {
        this.respTimeValue = respTimeValue;
    }

    /**
     * @return the localPortNum
     */
    public short getLocalPortNum() {
        return localPortNum;
    }

    /**
     * @param localPortNum
     *            the localPortNum to set
     */
    public void setLocalPortNum(short localPortNum) {
        this.localPortNum = localPortNum;
    }

    /**
     * @param localPortNum
     *            the localPortNum to set
     */
    public void setLocalPortNum(byte localPortNum) {
        this.localPortNum = Utils.unsignedByte(localPortNum);
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
        result = prime * result + lid;
        result = prime * result + localPortNum;
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
        PortInfoBean other = (PortInfoBean) obj;
        if (lid != other.lid) {
            return false;
        }
        if (localPortNum != other.localPortNum) {
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
        return "PortInfoBean [lid=" + lid + ", flowControlMask=0x"
                + Integer.toHexString(flowControlMask) + ", vl=" + vl
                + ", portStates=" + portStates + ", portType=" + portType
                + ", collectiveMask=" + collectiveMask + ", multicastMask="
                + multicastMask + ", mKeyProtectBits=" + mKeyProtectBits
                + ", lmc=" + lmc + ", masterSMSL=" + masterSMSL
                + ", linkInitReason="
                + StringUtils.byteHexString(linkInitReason)
                + ", partitionEnforcementInbound=" + partitionEnforcementInbound
                + ", partitionEnforcementOutbound="
                + partitionEnforcementOutbound + ", operationalVL="
                + operationalVL + ", pKey8B=" + pKey8B + ", pKey10B=" + pKey10B
                + ", mKeyViolation=" + mKeyViolation + ", pKeyViolation="
                + pKeyViolation + ", qKeyViolation=" + qKeyViolation
                + ", smTrapQueuePair=" + smTrapQueuePair + ", saQueuePair="
                + saQueuePair + ", neighborPortNum=" + neighborPortNum
                + ", linkDownReason=" + linkDownReason
                + ", neighborLinkDownReason=" + neighborLinkDownReason
                + ", clientReregister=" + clientReregister
                + ", mulPKeyTrapSuppressionEnabled="
                + mulPKeyTrapSuppressionEnabled + ", subnetTimeout="
                + subnetTimeout + ", linkSpeedSupported="
                + StringUtils.shortHexString(linkSpeedSupported)
                + ", linkSpeedEnabled="
                + StringUtils.shortHexString(linkSpeedEnabled)
                + ", linkSpeedActive="
                + StringUtils.shortHexString(linkSpeedActive)
                + ", linkWidthSupported="
                + StringUtils.shortHexString(linkWidthSupported)
                + ", linkWidthEnabled="
                + StringUtils.shortHexString(linkWidthEnabled)
                + ", linkWidthActive="
                + StringUtils.shortHexString(linkWidthActive)
                + ", linkWidthDownSupported="
                + StringUtils.shortHexString(linkWidthDownSupported)
                + ", linkWidthDownEnabled="
                + StringUtils.shortHexString(linkWidthDownEnabled)
                + ", linkWidthDownTxActive="
                + StringUtils.shortHexString(linkWidthDownTxActive)
                + ", linkWidthDownRxActive="
                + StringUtils.shortHexString(linkWidthDownRxActive)
                + ", plmSupported=" + plmSupported + ", plmEnabled="
                + plmEnabled + ", plmActive=" + plmActive
                + ", pLTPCRCModeSupported=" + pLTPCRCModeSupported
                + ", pLTPCRCModeEnabled=" + pLTPCRCModeEnabled
                + ", pLTPCRCModeActive=" + pLTPCRCModeActive
                + ", isActiveOptimizeEnabled=" + isActiveOptimizeEnabled
                + ", isPassThroughEnabled=" + isPassThroughEnabled
                + ", isVLMarkerEnabled=" + isVLMarkerEnabled
                + ", is16BTrapQueryEnabled=" + is16BTrapQueryEnabled
                + ", ppfSupported=" + ppfSupported + ", ppfEnabled="
                + ppfEnabled + ", flitControl=" + flitControl + ", maxLid="
                + maxLid + ", portErrorAction=" + portErrorAction
                + ", egressPort=" + egressPort + ", drControl=" + drControl
                + ", mKeyLeasePeriod=" + mKeyLeasePeriod + ", vl15Init="
                + StringUtils.intHexString(vl15Init) + ", vl15CreditRate="
                + StringUtils.byteHexString(vl15CreditRate) + ", creditAck="
                + StringUtils.byteHexString(creditAck) + ", bufferAlloc="
                + StringUtils.byteHexString(bufferAlloc) + ", masterSMLID="
                + masterSMLID + ", mKey=" + Long.toHexString(mKey)
                + ", subnetPrefix=" + Long.toHexString(subnetPrefix)
                + ", neighborVL0MTU=" + Arrays.toString(neighborVL0MTU)
                + ", neighborVL1MTU=" + Arrays.toString(neighborVL1MTU)
                + ", vlStallCount=" + Arrays.toString(vlStallCount)
                + ", hoqLife=" + Arrays.toString(hoqLife) + ", ipAddrIPV6="
                + Arrays.toString(ipAddrIPV6) + ", ipAddrIPV4="
                + Arrays.toString(ipAddrIPV4) + ", neighborNodeGUID="
                + Long.toHexString(neighborNodeGUID) + ", capabilityMask="
                + StringUtils.intHexString(capabilityMask)
                + ", capabilityMask3="
                + StringUtils.shortHexString(capabilityMask3)
                + ", overallBufferSpace=" + overallBufferSpace
                + ", bufferDepthH=" + bufferDepthH + ", wireDepthH="
                + wireDepthH + ", universalDiagCode="
                + StringUtils.shortHexString(universalDiagCode)
                + ", vendorDiagCode="
                + StringUtils.shortHexString(vendorDiagCode) + ", chain="
                + chain + ", bufferDepth=" + bufferDepth + ", wireDepth="
                + wireDepth + ", mgmtAllowed=" + mgmtAllowed
                + ", neighborFWAuthenBypass=" + neighborFWAuthenBypass
                + ", neighborNodeType=" + neighborNodeType + ", mtuCap="
                + mtuCap + ", respTimeValue=" + respTimeValue
                + ", localPortNum=" + localPortNum + "]";
    }

}
