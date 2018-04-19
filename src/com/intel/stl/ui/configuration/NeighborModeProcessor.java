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

import static com.intel.stl.ui.common.STLConstants.K0004_SWITCH;
import static com.intel.stl.ui.common.STLConstants.K0018_HFI;
import static com.intel.stl.ui.common.STLConstants.K0080_ON;
import static com.intel.stl.ui.common.STLConstants.K0081_YES;
import static com.intel.stl.ui.common.STLConstants.K0082_NO;
import static com.intel.stl.ui.common.STLConstants.K0383_NA;
import static com.intel.stl.ui.common.STLConstants.K0699_OFF;
import static com.intel.stl.ui.model.DeviceProperty.NEIGHBOR_FW_AUTH_BYPASS;
import static com.intel.stl.ui.model.DeviceProperty.NEIGHBOR_MGMT_ALLOWED;
import static com.intel.stl.ui.model.DeviceProperty.NEIGHBOR_NODE_TYPE;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class NeighborModeProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        boolean isHFI = context.isHFI();
        boolean isExternalSWPort = context.isExternalSWPort();
        if (portInfo == null) {
            getEmptyPortNeighborMode(category);
            return;
        }
        String value = K0082_NO.getValue();
        if (portInfo.isMgmtAllowed()) {
            value = K0081_YES.getValue();
        }
        addProperty(category, NEIGHBOR_MGMT_ALLOWED, value);

        if (isExternalSWPort || isHFI) {
            value = K0699_OFF.getValue();
            if (portInfo.isNeighborFWAuthenBypass()) {
                value = K0080_ON.getValue();
            }
            addProperty(category, NEIGHBOR_FW_AUTH_BYPASS, value);

            if (portInfo.getNeighborNodeType() != 0) {
                value = K0004_SWITCH.getValue();
            } else {
                value = K0018_HFI.getValue();
            }
            addProperty(category, NEIGHBOR_NODE_TYPE, value);

        } else {
            addProperty(category, NEIGHBOR_FW_AUTH_BYPASS, K0383_NA.getValue());
            addProperty(category, NEIGHBOR_NODE_TYPE, K0383_NA.getValue());
        }
    }

    private void getEmptyPortNeighborMode(DevicePropertyCategory category) {
        addProperty(category, NEIGHBOR_MGMT_ALLOWED, "");
        addProperty(category, NEIGHBOR_FW_AUTH_BYPASS, "");
        addProperty(category, NEIGHBOR_NODE_TYPE, "");
    }
}
