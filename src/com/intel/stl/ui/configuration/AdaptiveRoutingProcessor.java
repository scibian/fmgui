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
import static com.intel.stl.ui.common.STLConstants.K0444_NOT_SUPPORTED;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_ALGORITHM;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_ENABLE;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_FREQ;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_LOST;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_PAUSE;
import static com.intel.stl.ui.model.DeviceProperty.ADAPTIVE_ROUTING_THRESHOLD;

import com.intel.stl.api.subnet.SwitchInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class AdaptiveRoutingProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        SwitchInfoBean switchInfo = context.getSwitchInfo();

        if (switchInfo == null) {
            getEmptySwitchAdaptiveRouting(category);
            return;
        }
        if (switchInfo.isAdaptiveRoutingSupported()) {
            String trueStr = K0385_TRUE.getValue();
            String falseStr = K0386_FALSE.getValue();
            String value = falseStr;
            if (switchInfo.isAdaptiveRoutingEnable()) {
                value = trueStr;
            }
            addProperty(category, ADAPTIVE_ROUTING_ENABLE, value);
            value = falseStr;
            if (switchInfo.isAdaptiveRoutingPause()) {
                value = trueStr;
            }
            addProperty(category, ADAPTIVE_ROUTING_PAUSE, value);
            value = falseStr;
            if (switchInfo.isAdaptiveRoutingLostRoutesOnly()) {
                value = trueStr;
            }
            addProperty(category, ADAPTIVE_ROUTING_LOST, value);
            addProperty(category, ADAPTIVE_ROUTING_FREQ,
                    dec(switchInfo.getAdaptiveRoutingFrequency()));
            addProperty(category, ADAPTIVE_ROUTING_ALGORITHM,
                    dec(switchInfo.getAdaptiveRoutingAlgorithm()));
            addProperty(category, ADAPTIVE_ROUTING_THRESHOLD,
                    dec(switchInfo.getAdaptiveRoutingThreshold()));
        } else {
            addProperty(category, ADAPTIVE_ROUTING,
                    K0444_NOT_SUPPORTED.getValue());
        }
    }

    private void getEmptySwitchAdaptiveRouting(DevicePropertyCategory category) {
        addProperty(category, ADAPTIVE_ROUTING, K0383_NA.getValue());
    }
}
