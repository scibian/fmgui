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
import static com.intel.stl.ui.model.DeviceProperty.MASTER_SMSL;
import static com.intel.stl.ui.model.DeviceProperty.M_KEY_VIOLATIONS;
import static com.intel.stl.ui.model.DeviceProperty.PORT_GANGED_DETAILS;
import static com.intel.stl.ui.model.DeviceProperty.PORT_OVERALL_BUFF_SPACE;
import static com.intel.stl.ui.model.DeviceProperty.P_KEY_VIOLATIONS;
import static com.intel.stl.ui.model.DeviceProperty.Q_KEY_VIOLATIONS;
import static com.intel.stl.ui.model.DeviceProperty.REPLAY_DEPTH_BUFFER;
import static com.intel.stl.ui.model.DeviceProperty.REPLAY_DEPTH_WIRE;
import static com.intel.stl.ui.model.DeviceProperty.RESPONSE_TIME;
import static com.intel.stl.ui.model.DeviceProperty.SUBNET_TIMEOUT;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class DiagnosticsProcessor extends BaseCategoryProcessor {

    private static final int FORMAT_MULT_THRESHOLD_FRACTION = 1000;

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        boolean isEndPort = context.isEndPort();
        boolean isHFI = context.isHFI();
        boolean isExternalSWPort = context.isExternalSWPort();
        if (portInfo != null) {
            addProperty(category, PORT_OVERALL_BUFF_SPACE,
                    hex(portInfo.getOverallBufferSpace()));
            if (isExternalSWPort || isHFI) {
                addProperty(category, REPLAY_DEPTH_BUFFER,
                        hex(portInfo.getBufferDepth()));
                addProperty(category, REPLAY_DEPTH_WIRE,
                        hex(portInfo.getWireDepth()));
            } else {
                addProperty(category, REPLAY_DEPTH_BUFFER, K0383_NA.getValue());
                addProperty(category, REPLAY_DEPTH_WIRE, K0383_NA.getValue());
            }
            String na = K0383_NA.getValue();
            String value = na;
            if (isEndPort) {
                value = Byte.toString(portInfo.getMasterSMSL());
            }
            addProperty(category, MASTER_SMSL, value);
            value = Byte.toString(portInfo.getMasterSMSL());
            if (isExternalSWPort) {
                addProperty(category, M_KEY_VIOLATIONS, na);
                addProperty(category, P_KEY_VIOLATIONS, na);
                addProperty(category, Q_KEY_VIOLATIONS, na);
            } else {
                addProperty(category, M_KEY_VIOLATIONS, value);
                value = Short.toString(portInfo.getPKeyViolation());
                addProperty(category, P_KEY_VIOLATIONS, value);
                value = Short.toString(portInfo.getQKeyViolation());
                addProperty(category, Q_KEY_VIOLATIONS, value);
            }
            value = formatTimeout(portInfo.getRespTimeValue());
            addProperty(category, RESPONSE_TIME, value);
            if (isExternalSWPort) {
                addProperty(category, SUBNET_TIMEOUT, na);
            } else {
                value = formatTimeout(portInfo.getSubnetTimeout());
                addProperty(category, SUBNET_TIMEOUT, value);
            }
        } else {
            addProperty(category, PORT_GANGED_DETAILS, "");
            addProperty(category, PORT_OVERALL_BUFF_SPACE, "");
            addProperty(category, REPLAY_DEPTH_BUFFER, "");
            addProperty(category, REPLAY_DEPTH_WIRE, "");
            addProperty(category, MASTER_SMSL, "");
            addProperty(category, M_KEY_VIOLATIONS, "");
            addProperty(category, P_KEY_VIOLATIONS, "");
            addProperty(category, Q_KEY_VIOLATIONS, "");
            addProperty(category, RESPONSE_TIME, "");
            addProperty(category, SUBNET_TIMEOUT, "");
        }
    }

    private String formatTimeout(byte timeout) {
        double exp = Math.pow(2.0, timeout);
        long val = 4096 * (long) exp; // value in ns
        if (val < FORMAT_MULT_THRESHOLD_FRACTION) {
            return String.format("%d ns", val);
        } else {
            val = val / 1000;
            if (val < FORMAT_MULT_THRESHOLD_FRACTION) {
                return String.format("%d µs", val);
            } else {
                val = val / 1000;
                if (val < FORMAT_MULT_THRESHOLD_FRACTION) {
                    return String.format("%d ms", val);
                } else {
                    val = val / 1000;
                    return String.format("%d sec", val);
                }
            }
        }

    }
}
