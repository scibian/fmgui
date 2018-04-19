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

import static com.intel.stl.ui.common.STLConstants.K0387_UNKNOWN;
import static com.intel.stl.ui.model.DeviceProperty.FLOW_CONTROL_DISABLE_MASK;
import static com.intel.stl.ui.model.DeviceProperty.HOQLIFE_LABEL;
import static com.intel.stl.ui.model.DeviceProperty.OPERATIONAL_VLS;
import static com.intel.stl.ui.model.DeviceProperty.VL_ARBITR_HIGH_CAP;
import static com.intel.stl.ui.model.DeviceProperty.VL_ARBITR_LOW_CAP;
import static com.intel.stl.ui.model.DeviceProperty.VL_CAP;
import static com.intel.stl.ui.model.DeviceProperty.VL_HIGH_LIMIT;
import static com.intel.stl.ui.model.DeviceProperty.VL_PREEMPTING_LIMIT;
import static com.intel.stl.ui.model.DeviceProperty.VL_PREEMPT_CAP;
import static com.intel.stl.ui.model.DeviceProperty.VL_STALL_COUNT;

import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.VirtualLaneBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class VirtualLaneProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        NodeInfoBean nodeInfo = context.getNodeInfo();
        PortInfoBean portInfo = context.getPortInfo();

        if (!(nodeInfo != null && portInfo != null)) {
            getEmptyVirtualLane(category);
            return;
        }
        String unknown = K0387_UNKNOWN.getValue();
        String na = STLConstants.K0383_NA.getValue();
        VirtualLaneBean vlBean = portInfo.getVl();
        String value = unknown;
        byte cap = portInfo.getOperationalVL();
        value = hex(cap);
        addProperty(category, OPERATIONAL_VLS, value);
        cap = vlBean.getCap();
        value = hex(cap);
        addProperty(category, VL_CAP, value);
        addProperty(category, VL_HIGH_LIMIT, dec(vlBean.getHighLimit()));
        addProperty(category, VL_PREEMPT_CAP, dec(vlBean.getPreemptCap()));
        addProperty(category, VL_PREEMPTING_LIMIT,
                dec(vlBean.getPreemptingLimit()));
        if (nodeInfo.getNodeTypeEnum() == NodeType.SWITCH
                && context.getPort().getPortNum() > 0) {
            addProperty(category, FLOW_CONTROL_DISABLE_MASK,
                    hex(portInfo.getFlowControlMask()));
        } else {
            addProperty(category, FLOW_CONTROL_DISABLE_MASK, na);
        }
        addProperty(category, VL_ARBITR_HIGH_CAP,
                Short.toString(vlBean.getArbitrationHighCap()));
        addProperty(category, VL_ARBITR_LOW_CAP,
                Short.toString(vlBean.getArbitrationLowCap()));
    }

    private void getEmptyVirtualLane(DevicePropertyCategory category) {
        addProperty(category, OPERATIONAL_VLS, "");
        addProperty(category, VL_CAP, "");
        addProperty(category, VL_HIGH_LIMIT, "");
        addProperty(category, VL_PREEMPT_CAP, "");
        addProperty(category, VL_PREEMPTING_LIMIT, "");
        addProperty(category, FLOW_CONTROL_DISABLE_MASK, "");
        addProperty(category, VL_ARBITR_HIGH_CAP, "");
        addProperty(category, VL_ARBITR_LOW_CAP, "");
        addProperty(category, VL_STALL_COUNT, "");
        addProperty(category, HOQLIFE_LABEL, "");
    }
}
