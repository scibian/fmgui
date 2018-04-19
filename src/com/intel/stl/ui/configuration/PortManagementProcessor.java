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
import static com.intel.stl.ui.common.STLConstants.K0387_UNKNOWN;
import static com.intel.stl.ui.model.DeviceProperty.MASTER_SM_LID;
import static com.intel.stl.ui.model.DeviceProperty.M_KEY;
import static com.intel.stl.ui.model.DeviceProperty.M_KEY_LEASE_PERIOD;
import static com.intel.stl.ui.model.DeviceProperty.M_KEY_PROTECT;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.MKeyProtectViz;

public class PortManagementProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        boolean isEndPort = context.isEndPort();
        boolean isExternalSWPort = context.isExternalSWPort();
        if (portInfo == null) {
            getEmptyManagement(category);
            return;
        }
        String na = K0383_NA.getValue();
        String unknown = K0387_UNKNOWN.getValue();
        portInfo.getRespTimeValue();
        String value = na;
        if (isEndPort) {
            value = hex(portInfo.getMKey());
        }
        addProperty(category, M_KEY, value);
        value = na;
        if (isEndPort) {
            value = hex(portInfo.getMasterSMLID());
        }
        addProperty(category, MASTER_SM_LID, value);
        value = na;
        if (isEndPort) {
            value = Integer.toString(portInfo.getMKeyLeasePeriod());
        }
        addProperty(category, M_KEY_LEASE_PERIOD, value);
        if (isExternalSWPort) {
            addProperty(category, M_KEY_PROTECT, na);
        } else {
            value = MKeyProtectViz
                    .getMKeyProtectStr(portInfo.getMKeyProtectBits());
            if (value == null) {
                value = unknown;
            }
            addProperty(category, M_KEY_PROTECT, value);
        }
    }

    private void getEmptyManagement(DevicePropertyCategory category) {
        addProperty(category, M_KEY, "");
        addProperty(category, MASTER_SM_LID, "");
        addProperty(category, M_KEY_LEASE_PERIOD, "");
    }
}
