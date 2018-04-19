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

import static com.intel.stl.ui.common.STLConstants.K0080_ON;
import static com.intel.stl.ui.common.STLConstants.K0383_NA;
import static com.intel.stl.ui.common.STLConstants.K0699_OFF;
import static com.intel.stl.ui.model.DeviceProperty.PARTITION_ENFORCE_IN;
import static com.intel.stl.ui.model.DeviceProperty.PARTITION_ENFORCE_OUT;

import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class PortPartitionEnforcementProcessor extends BaseCategoryProcessor {

	@Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        NodeInfoBean nodeInfo = context.getNodeInfo();
        PortInfoBean portInfo = context.getPortInfo();
        PortRecordBean portBean = context.getPort();

        if (nodeInfo == null || portInfo == null || portBean == null) {
        	setPartitionEnforcement(category, "", "");
            return;
        }
        String trueStr = K0080_ON.getValue();
        String falseStr = K0699_OFF.getValue();
        String inboundValue = K0383_NA.getValue();
        String outboundValue = K0383_NA.getValue();
         
        if (nodeInfo.getNodeTypeEnum() == NodeType.SWITCH && 
        		portBean.getPortNum() > 0) {
        	inboundValue = portInfo.isPartitionEnforcementInbound() ? trueStr : falseStr;
        	outboundValue = portInfo.isPartitionEnforcementOutbound() ? trueStr : falseStr;
        }
        setPartitionEnforcement(category, inboundValue, outboundValue);
    }

    private void setPartitionEnforcement(DevicePropertyCategory category, 
    		String inbound, String outbound){
    	addProperty(category, PARTITION_ENFORCE_IN, inbound);
        addProperty(category, PARTITION_ENFORCE_OUT, outbound);
    }
}
