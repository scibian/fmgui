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

package com.intel.stl.ui.model;

import static com.intel.stl.ui.common.STLConstants.K0388_OR;

import java.util.List;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.CapabilityMask;
import com.intel.stl.api.configuration.LinkSpeedMask;
import com.intel.stl.api.configuration.LinkWidthMask;
import com.intel.stl.api.configuration.MTUSize;
import com.intel.stl.api.configuration.PhysicalState;
import com.intel.stl.api.configuration.PortState;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.PortStatesBean;
import com.intel.stl.ui.common.STLConstants;

/**
 * @deprecated use {@link com.intel.stl.ui.model.DevicePropertyCategory}
 */
@Deprecated
public class PortProperties extends NodeProperties {

    private PortRecordBean portRec = null;

    private PortInfoBean portInfo = null;

    private NodeRecordBean nodeRec = null;

    private LinkRecordBean linkRec = null;

    private NodeType nodeType = null;

    private String neighborNodeDesc = null;

    boolean hasData = false;

    boolean isEndPort = false;

    public PortProperties() {

    }

    /**
     * constructor
     * 
     * @param ptRec
     * @param nodeRec
     */
    public PortProperties(PortRecordBean ptRec, NodeRecordBean ndRec,
            LinkRecordBean lnkRec) {
        super(ndRec, null);

        if ((ptRec != null) && (ndRec != null)) {
            portRec = ptRec;
            nodeRec = ndRec;
            linkRec = lnkRec;

            if (portRec.getPortInfo() != null) {
                portInfo = portRec.getPortInfo();

                nodeType =
                        NodeType.getNodeType(nodeRec.getNodeInfo()
                                .getNodeType());
                int portIdx = nodeRec.getNodeInfo().getLocalPortNum();

                if ((nodeType != NodeType.SWITCH) || (portIdx == 0)) {
                    this.isEndPort = true;
                }
            }
        }
        hasData = portInfo != null;
    }

    /********** Port Device ***********/
    /**
     * 
     * @return Local ID of the port in String
     * 
     */
    public String getLID() {
        String retVal = "";

        if (hasData) {
            if (isEndPort) {
                retVal = StringUtils.intHexString(portInfo.getLid());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;

    }

    /**
     * 
     * @return State of the port in String
     */
    public String getState() {
        String retVal = "";

        if (hasData) {
            if (portInfo == null) {
                throw new RuntimeException("portInfo is null in getState.");
            }
            PortStatesBean portState = portInfo.getPortStates();

            if (portState != null) {
                PortState ptState = portState.getPortState();
                if (ptState != null) {
                    retVal = PortStateViz.getPortStateStr(ptState);
                } else {
                    retVal = STLConstants.K0387_UNKNOWN.getValue();
                }
            }
        }

        return retVal;
    }

    /**
     * 
     * @return Physical state of the port in String
     */
    public String getPhysicalState() {
        String retVal = "";

        if (hasData) {
            PortStatesBean PortState = portInfo.getPortStates();

            if (PortState != null) {
                PhysicalState phyState = PortState.getPortPhysicalState();
                if (phyState != null) {
                    retVal = PhysicalStateViz.getPhysicalStateStr(phyState);
                } else {
                    retVal = STLConstants.K0387_UNKNOWN.getValue();
                }

            }
        }

        return retVal;
    }

    // /**
    // *
    // * @return
    // */
    // public String getSuperNodeGUID() {
    // String retVal = "";
    //
    // if (HasData) {
    // retVal = this.getNodeGUID();
    // }
    //
    // return retVal;
    // }

    public String getSubnetPrefix() {
        String retVal = "";

        if (hasData) {
            if (isEndPort) {
                retVal = StringUtils.longHexString(portInfo.getSubnetPrefix());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getUniversalDiagCode() {

        String retVal = "";

        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getUniversalDiagCode();
                // TODO: STL hasn't define it yet, so just display its numerical
                // value for now
                retVal = Integer.toString(val);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getVendorDiagCode() {

        String retVal = "";

        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getVendorDiagCode();
                // TODO: STL hasn't define it yet, so just display its numerical
                // value for now
                retVal = Integer.toString(val);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getNeighborMTUSize() {
        String retVal = "";

        if (hasData) {
            byte val = portInfo.getNeighborVL0MTU()[0]; // this is how it is
                                                        // done
                                                        // in fillPortInfo()

            MTUSize mtusize = MTUSize.getMTUSize(val);
            if (mtusize != null) {
                retVal = MTUSizeViz.getMTUSizeStr(mtusize);
            } else {
                retVal = STLConstants.K0387_UNKNOWN.getValue();
            }
        }

        return retVal;
    }

    public String getMTUCapability() {
        String retVal = "";

        if (hasData) {
            MTUSize mtusize = portInfo.getMtuCap();

            if (mtusize != null) {
                retVal = MTUSizeViz.getMTUSizeStr(mtusize);
            } else {
                retVal = STLConstants.K0387_UNKNOWN.getValue();
            }
        }

        return retVal;
    }

    // public String getGUIDCap() {
    // if (HasData) {
    // if (this.IsEndPort) {
    // return Byte.toString(PortInfo.getGuidCap());
    // }
    // else {
    // return STLConstants.K0383_NA.getValue();
    // }
    // }
    // else
    // return "";
    //
    // }

    /*** Port Link ***/
    public String getLinkWidthEnabled() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthEnabled();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthSupported() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthSupported();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthActive() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthActive();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthDnGrdEnabled() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthDownEnabled();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthDnGrdSupported() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthDownSupported();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthDnGrdTx() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthDownTxActive();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    public String getLinkWidthDnGrdRx() {
        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkWidthDownRxActive();

            retVal = getLinkWidthString(val);
        }

        return retVal;
    }

    private String getLinkWidthString(short val) {
        StringBuilder lwStr = new StringBuilder();
        String join = "";
        String or = " " + K0388_OR.getValue() + " ";
        List<LinkWidthMask> masks = LinkWidthMask.getWidthMasks(val);
        for (LinkWidthMask mask : masks) {
            lwStr.append(join);
            lwStr.append(LinkWidthMaskViz.getLinkWidthMaskStr(mask));
            join = or;
        }
        return lwStr.toString();
    }

    public String getLinkSpeedEnabled() {

        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkSpeedEnabled();

            retVal = getLinkSpeedString(val);
        }

        return retVal;
    }

    public String getLinkSpeedSupported() {

        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkSpeedSupported();

            retVal = getLinkSpeedString(val);
        }

        return retVal;

    }

    public String getLinkSpeedActive() {

        String retVal = "";

        if (hasData) {
            short val = portInfo.getLinkSpeedActive();
            retVal = getLinkSpeedString(val);
        }

        return retVal;
    }

    private String getLinkSpeedString(short val) {
        StringBuffer ret = new StringBuffer();
        String or = " " + K0388_OR.getValue() + " ";
        String orConn = "";
        List<LinkSpeedMask> masks = LinkSpeedMask.getSpeedMasks(val);
        for (LinkSpeedMask mask : masks) {
            ret.append(orConn);
            ret.append(LinkSpeedMaskViz.getLinkSpeedMaskStr(mask));
            orConn = or;
        }

        return ret.toString();
    }

    /**
     * @param neighborNodeDesc
     *            the neighborNodeDesc to set
     */
    public void setNeighborNodeDesc(String neighborNodeDesc) {
        this.neighborNodeDesc = neighborNodeDesc;
    }

    /**
     * Description:
     * 
     * @return
     */
    public String getLinkToNodeDesc() {
        String retVal = "";

        if (hasData) {
            retVal =
                    linkRec == null ? STLConstants.K0383_NA.getValue()
                            : neighborNodeDesc;
        }

        return retVal;
    }

    /**
     * Description:
     * 
     * @return
     */
    public String getLinkToGUID() {
        String retVal = "";

        if (hasData) {
            retVal =
                    linkRec == null ? STLConstants.K0383_NA.getValue()
                            : StringUtils.longHexString(portInfo
                                    .getNeighborNodeGUID());
        }

        return retVal;
    }

    public String getLinkToPortIndex() {
        String retVal = "";

        if (hasData) {
            retVal =
                    linkRec == null ? STLConstants.K0383_NA.getValue()
                            : Integer.toString(linkRec.getToPortIndex());
        }

        return retVal;
    }

    /***** Port Capability *****/
    public String getSMCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_SM.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getNoticeCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_NOTICE.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getVendorCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_VENDORCLASS.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;

    }

    public String getDeviceManCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_DEVICEMANAGEMENT.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getConnManCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_CONNECTIONMANAGEMENT.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getAutoMigCapability() {
        String retVal = "";
        if (hasData) {
            if (isEndPort) {
                int val = portInfo.getCapabilityMask();
                if (CapabilityMask.HAS_AUTOMIGRATION.hasThisMask(val)) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    /***** Port Virtual Lane *****/
    public String getVLCap() {

        String retVal = "";

        if (hasData) {
            byte cap = portInfo.getVl().getCap();
            retVal = Byte.toString(cap);
        }

        return retVal;

    }

    public String getHighLimit() {
        if (hasData) {
            return Integer.toString(portInfo.getVl().getHighLimit());
        } else {
            return "";
        }
    }

    public String getHiArbitrationCap() {
        if (hasData) {
            return Short.toString(portInfo.getVl().getArbitrationHighCap());
        } else {
            return "";
        }
    }

    public String getLowArbitrationCap() {
        if (hasData) {
            return Short.toString(portInfo.getVl().getArbitrationLowCap());
        } else {
            return "";
        }
    }

    public String getVLStallCount() {
        String retVal = "";
        if (hasData) {
            if (nodeType == NodeType.SWITCH) {
                retVal = Byte.toString(portInfo.getVlStallCount()[0]);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getHOQlifeLabel() {
        String retVal = "";

        if (hasData) {
            if (nodeType != NodeType.HFI) {
                retVal = Byte.toString(portInfo.getHoqLife()[0]);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getOperationalVLs() {
        String retVal = "";

        if (hasData) {
            byte cap = portInfo.getOperationalVL();
            retVal = Byte.toString(cap);
        }

        return retVal;

    }

    /***** Port Diagnostic *****/
    public String getMastersSMSL() {
        String retVal = "";
        if (hasData) {
            if (this.isEndPort) {
                retVal = Byte.toString(portInfo.getMasterSMSL());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getMKeyViolations() {
        String retVal = "";
        if (hasData) {
            if (this.isEndPort) {
                retVal = Short.toString(portInfo.getMKeyViolation());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    public String getPKeyViolations() {
        String retVal = "";
        if (hasData) {
            if (this.isEndPort) {
                retVal = Short.toString(portInfo.getPKeyViolation());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    public String getQKeyViolations() {
        String retVal = "";
        if (hasData) {
            if (this.isEndPort) {
                retVal = Short.toString(portInfo.getQKeyViolation());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    public String getSubnetTimeout() {
        String retVal = "";

        if (hasData) {
            if (this.isEndPort) {
                double exp = Math.pow(2.0, portInfo.getSubnetTimeout());
                double val = 4.096 * exp;
                retVal = Double.toString(val);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
            ;
        }
        return retVal;
    }

    public String getRespTime() {
        String retVal = "";

        if (hasData) {
            if (this.isEndPort) {
                double exp = Math.pow(2.0, portInfo.getRespTimeValue());
                double val = 4.096 * exp;
                retVal = Double.toString(val);
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    // public String getPhysicalError() {
    // String retVal = "";
    // if (HasData) {
    // retVal = Byte.toString(PortInfo.getLocalPhysErrors());
    // }
    //
    // return retVal;
    // }

    // public String getOverrunError() {
    //
    // String retVal = "";
    // if (HasData) {
    // retVal = Byte.toString(PortInfo.getOverrunErrors());
    // }
    //
    // return retVal;
    // }

    /***** partition enforcement *****/
    public String getPartEnforceIn() {

        String retVal = "";

        if (hasData) {
            if (nodeType == NodeType.SWITCH) {
                if (portInfo.isPartitionEnforcementInbound()) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    public String getPartEnforceOut() {

        String retVal = "";

        if (hasData) {
            if (nodeType == NodeType.SWITCH) {
                if (portInfo.isPartitionEnforcementOutbound()) {
                    retVal = STLConstants.K0385_TRUE.getValue();
                } else {
                    retVal = STLConstants.K0386_FALSE.getValue();
                }
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }
        return retVal;
    }

    // public String getFilterRawPktIn() {
    //
    // String retVal = "";
    //
    // if (HasData) {
    // if (nodeType == Constants.NodeType.SWITCH) {
    // if (PortInfo.isFilterRawInbound())
    // retVal = STLConstants.K0385_TRUE.getValue();
    // else
    // retVal = STLConstants.K0386_FALSE.getValue();
    // }
    // else
    // retVal = STLConstants.K0383_NA.getValue();
    // }
    // return retVal;
    //
    // }

    // public String getFilterRawPktOut() {
    //
    // String retVal = "";
    //
    // if (HasData) {
    // if (nodeType == Constants.NodeType.SWITCH) {
    // if (PortInfo.isFilterRawOutbound())
    // retVal = STLConstants.K0385_TRUE.getValue();
    // else
    // retVal = STLConstants.K0386_FALSE.getValue();
    // }
    // else
    // retVal = STLConstants.K0383_NA.getValue();
    // }
    // return retVal;
    //
    // }

    /*** management information ***/
    public String getMKey() {
        String retVal = "";

        if (hasData) {
            if (this.isEndPort) {
                retVal = Long.toString(portInfo.getMKey());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getMasterLID() {
        String retVal = "";

        if (hasData) {
            if (this.isEndPort) {
                retVal = StringUtils.intHexString(portInfo.getMasterSMLID());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

    public String getMKeyLeasePeriod() {
        String retVal = "";

        if (hasData) {
            if (this.isEndPort) {
                retVal = Integer.toString(portInfo.getMKeyLeasePeriod());
            } else {
                retVal = STLConstants.K0383_NA.getValue();
            }
        }

        return retVal;
    }

}
