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

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.STLConstants.K0383_NA;
import static com.intel.stl.ui.common.STLConstants.K0385_TRUE;
import static com.intel.stl.ui.common.STLConstants.K0386_FALSE;
import static com.intel.stl.ui.common.STLConstants.K0387_UNKNOWN;
import static com.intel.stl.ui.model.DeviceProperty.BASE_LID;
import static com.intel.stl.ui.model.DeviceProperty.GID_PREFIX;
import static com.intel.stl.ui.model.DeviceProperty.LINK_DOWN_REASON;
import static com.intel.stl.ui.model.DeviceProperty.LINK_INIT_REASON;
import static com.intel.stl.ui.model.DeviceProperty.LMC;
import static com.intel.stl.ui.model.DeviceProperty.MAX_MTU_SIZE;
import static com.intel.stl.ui.model.DeviceProperty.MAX_NEIGHBOR_MTU_SIZE;
import static com.intel.stl.ui.model.DeviceProperty.NEIGHBOR_LINK_DOWN_REASON;
import static com.intel.stl.ui.model.DeviceProperty.NODE_GUID;
import static com.intel.stl.ui.model.DeviceProperty.OFFLINE_DISABLED_REASON;
import static com.intel.stl.ui.model.DeviceProperty.PORT_LED_ENABLED;
import static com.intel.stl.ui.model.DeviceProperty.PORT_LID;
import static com.intel.stl.ui.model.DeviceProperty.PORT_LOCAL_NUM;
import static com.intel.stl.ui.model.DeviceProperty.PORT_NEIGHBOR_NORMAL;
import static com.intel.stl.ui.model.DeviceProperty.PORT_NUMBER;
import static com.intel.stl.ui.model.DeviceProperty.PORT_PHYSICAL_STATE;
import static com.intel.stl.ui.model.DeviceProperty.PORT_SM_CONFIG_START;
import static com.intel.stl.ui.model.DeviceProperty.PORT_STATE;
import static com.intel.stl.ui.model.DeviceProperty.PORT_TYPE;
import static com.intel.stl.ui.model.DeviceProperty.UNI_DIAG_CODE;
import static com.intel.stl.ui.model.DeviceProperty.VENDOR_DIAG_CODE;

import com.intel.stl.api.configuration.MTUSize;
import com.intel.stl.api.configuration.PhysicalState;
import com.intel.stl.api.configuration.PortState;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.PortStatesBean;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.LinkDownReasonViz;
import com.intel.stl.ui.model.LinkInitReasonViz;
import com.intel.stl.ui.model.MTUSizeViz;
import com.intel.stl.ui.model.OfflineDisabledReasonViz;
import com.intel.stl.ui.model.PhysicalStateViz;
import com.intel.stl.ui.model.PortStateViz;
import com.intel.stl.ui.model.PortTypeViz;

public class PortInfoProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        NodeInfoBean nodeInfo = context.getNodeInfo();
        PortRecordBean portBean = context.getPort();
        PortInfoBean portInfo = context.getPortInfo();
        boolean isHFI = context.isHFI();
        boolean isExternalSWPort = context.isExternalSWPort();
        if (!(nodeInfo != null && portInfo != null)) {
            getEmptyPortDeviceInfo(category);
            return;
        }
        String trueStr = K0385_TRUE.getValue();
        String falseStr = K0386_FALSE.getValue();
        String unknownStr = K0387_UNKNOWN.getValue();
        String value = falseStr;
        addProperty(category, PORT_LID, dec(portBean.getEndPortLID()));
        addProperty(category, PORT_NUMBER, dec(portBean.getPortNum()));
        addProperty(category, PORT_LOCAL_NUM, dec(portInfo.getLocalPortNum()));
        PortStatesBean psBean = portInfo.getPortStates();
        if (psBean != null) {
            PortState portState = psBean.getPortState();
            value = unknownStr;
            if (portState != null) {
                value = PortStateViz.getPortStateStr(portState);
            }
            addProperty(category, PORT_STATE, value);
            addProperty(category, LINK_INIT_REASON, LinkInitReasonViz
                    .getLinkInitReasonStr(portInfo.getLinkInitReason()));
            PhysicalState physState = psBean.getPortPhysicalState();
            value = unknownStr;
            if (physState != null) {
                value = PhysicalStateViz.getPhysicalStateStr(physState);
            }
            addProperty(category, PORT_PHYSICAL_STATE, value);
            value = OfflineDisabledReasonViz
                    .getOfflineDisabledReasonStr(psBean.getOfflineReason());
            if (value == null) {
                value = unknownStr;
            }
            addProperty(category, OFFLINE_DISABLED_REASON, value);
            value = falseStr;
            if (isExternalSWPort || isHFI) {
                if (psBean.isLedEnabled()) {
                    value = trueStr;
                }
                addProperty(category, PORT_LED_ENABLED, value);
            } else {
                addProperty(category, PORT_LED_ENABLED, K0383_NA.getValue());
            }
            value = falseStr;
            if (psBean.isSMConfigurationStarted()) {
                value = trueStr;
            }
            addProperty(category, PORT_SM_CONFIG_START, value);
            value = falseStr;
            if (isExternalSWPort || isHFI) {
                if (psBean.isNeighborNormal()) {
                    value = trueStr;
                }
                addProperty(category, PORT_NEIGHBOR_NORMAL, value);
            } else {
                addProperty(category, PORT_NEIGHBOR_NORMAL,
                        K0383_NA.getValue());
            }
        } else {
            addProperty(category, PORT_STATE, "");
            addProperty(category, PORT_PHYSICAL_STATE, "");
            addProperty(category, OFFLINE_DISABLED_REASON, "");
            addProperty(category, PORT_SM_CONFIG_START, "");
            addProperty(category, PORT_NEIGHBOR_NORMAL, "");
        }

        addProperty(category, BASE_LID, hex(portInfo.getLid()));
        addProperty(category, LMC, dec(portInfo.getLmc()));

        addProperty(category, PORT_TYPE,
                PortTypeViz.getPortTypeStr(portInfo.getPortType()));
        addProperty(category, NODE_GUID, hex(nodeInfo.getNodeGUID()));
        addProperty(category, LINK_DOWN_REASON, LinkDownReasonViz
                .getLinkDownReasonStr(portInfo.getLinkDownReason()));
        addProperty(category, NEIGHBOR_LINK_DOWN_REASON, LinkDownReasonViz
                .getLinkDownReasonStr(portInfo.getNeighborLinkDownReason()));
        if (isExternalSWPort) {
            addProperty(category, GID_PREFIX, K0383_NA.getValue());
        } else {
            addProperty(category, GID_PREFIX, hex(portInfo.getSubnetPrefix()));
        }
        addProperty(category, UNI_DIAG_CODE,
                dec(portInfo.getUniversalDiagCode()));
        addProperty(category, VENDOR_DIAG_CODE,
                dec(portInfo.getVendorDiagCode()));

        MTUSize mtuCap = portInfo.getMtuCap();

        if (mtuCap != null) {
            addProperty(category, MAX_MTU_SIZE,
                    MTUSizeViz.getMTUSizeStr(mtuCap));
        } else {
            addProperty(category, MAX_MTU_SIZE, K0387_UNKNOWN.getValue());
        }
    }

    private void getEmptyPortDeviceInfo(DevicePropertyCategory category) {
        addProperty(category, PORT_LID, "");
        addProperty(category, PORT_NUMBER, "");
        addProperty(category, PORT_STATE, "");
        addProperty(category, PORT_PHYSICAL_STATE, "");
        addProperty(category, OFFLINE_DISABLED_REASON, "");
        addProperty(category, PORT_SM_CONFIG_START, "");
        addProperty(category, PORT_NEIGHBOR_NORMAL, "");
        addProperty(category, BASE_LID, "");
        addProperty(category, LMC, "");
        addProperty(category, PORT_TYPE, "");
        addProperty(category, NODE_GUID, "");
        addProperty(category, LINK_DOWN_REASON, "");
        addProperty(category, GID_PREFIX, "");
        addProperty(category, UNI_DIAG_CODE, "");
        addProperty(category, VENDOR_DIAG_CODE, "");
        addProperty(category, MAX_NEIGHBOR_MTU_SIZE, "");
        addProperty(category, MAX_MTU_SIZE, "");
    }
}
