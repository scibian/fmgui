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
import static com.intel.stl.ui.model.DeviceProperty.AUTO_MIGR_SUPPORTED;
import static com.intel.stl.ui.model.DeviceProperty.CONN_LABEL_SUPPORTED;
import static com.intel.stl.ui.model.DeviceProperty.DEVICE_MGMT_SUPPORTED;
import static com.intel.stl.ui.model.DeviceProperty.NOTICE_SUPPORTED;
import static com.intel.stl.ui.model.DeviceProperty.PORT_ADRR_RANGE_CONFIG;
import static com.intel.stl.ui.model.DeviceProperty.PORT_ASYNC_SC2VL;
import static com.intel.stl.ui.model.DeviceProperty.PORT_PASSTHRU;
import static com.intel.stl.ui.model.DeviceProperty.PORT_SHARED_SPACE;
import static com.intel.stl.ui.model.DeviceProperty.PORT_SNOOP;
import static com.intel.stl.ui.model.DeviceProperty.PORT_VL15_MULTICAST;
import static com.intel.stl.ui.model.DeviceProperty.PORT_VLR;
import static com.intel.stl.ui.model.DeviceProperty.PORT_VL_MARKER;
import static com.intel.stl.ui.model.DeviceProperty.SUBNET_MANAGER;
import static com.intel.stl.ui.model.DeviceProperty.VENDOR_CLASS_SUPPORTED;

import com.intel.stl.api.configuration.CapabilityMask;
import com.intel.stl.api.configuration.CapabilityMask3;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class PortCapabilitiesProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        if (portInfo != null) {
            String trueStr = STLConstants.K0385_TRUE.getValue();
            String falseStr = STLConstants.K0386_FALSE.getValue();
            int val = portInfo.getCapabilityMask();
            String value = falseStr;
            if (CapabilityMask.HAS_SM.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, SUBNET_MANAGER, value);
            value = falseStr;
            if (CapabilityMask.HAS_NOTICE.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, NOTICE_SUPPORTED, value);
            value = falseStr;
            if (CapabilityMask.HAS_AUTOMIGRATION.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, AUTO_MIGR_SUPPORTED, value);
            value = falseStr;
            if (CapabilityMask.HAS_CONNECTIONMANAGEMENT.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, CONN_LABEL_SUPPORTED, value);
            value = falseStr;
            if (CapabilityMask.HAS_DEVICEMANAGEMENT.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, DEVICE_MGMT_SUPPORTED, value);
            value = falseStr;
            if (CapabilityMask.HAS_VENDORCLASS.hasThisMask(val)) {
                value = trueStr;
            }
            addProperty(category, VENDOR_CLASS_SUPPORTED, value);

            short val3 = portInfo.getCapabilityMask3();
            value = falseStr;
            if (CapabilityMask3.SNOOP_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_SNOOP, value);
            value = falseStr;
            if (CapabilityMask3.ASYNCSC2VL_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_ASYNC_SC2VL, value);
            value = falseStr;
            if (CapabilityMask3.ADDRRANGECONFIG_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_ADRR_RANGE_CONFIG, value);
            value = falseStr;
            if (CapabilityMask3.PASSTHROUGH_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_PASSTHRU, value);
            value = falseStr;
            if (CapabilityMask3.SHAREDSPACE_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_SHARED_SPACE, value);
            value = falseStr;
            if (CapabilityMask3.VLMARKER_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_VL_MARKER, value);
            value = falseStr;
            if (CapabilityMask3.VLR_SUPPORTED.hasThisMask(val3)) {
                value = trueStr;
            }
            addProperty(category, PORT_VLR, value);
        } else {
            String na = K0383_NA.getValue();
            addProperty(category, SUBNET_MANAGER, na);
            addProperty(category, NOTICE_SUPPORTED, na);
            addProperty(category, AUTO_MIGR_SUPPORTED, na);
            addProperty(category, CONN_LABEL_SUPPORTED, na);
            addProperty(category, DEVICE_MGMT_SUPPORTED, na);
            addProperty(category, VENDOR_CLASS_SUPPORTED, na);
            addProperty(category, PORT_SNOOP, na);
            addProperty(category, PORT_ASYNC_SC2VL, na);
            addProperty(category, PORT_ADRR_RANGE_CONFIG, na);
            addProperty(category, PORT_PASSTHRU, na);
            addProperty(category, PORT_SHARED_SPACE, na);
            addProperty(category, PORT_VL15_MULTICAST, na);
            addProperty(category, PORT_VL_MARKER, na);
            addProperty(category, PORT_VLR, na);
        }
    }
}
