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
import static com.intel.stl.ui.model.DeviceProperty.BUFFER_ALLOC;
import static com.intel.stl.ui.model.DeviceProperty.CREDIT_ACK;
import static com.intel.stl.ui.model.DeviceProperty.VL15_CREDIT_RATE;
import static com.intel.stl.ui.model.DeviceProperty.VL15_INIT;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class BufferUnitsProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        boolean isHFI = context.isHFI();
        boolean isExternalSWPort = context.isExternalSWPort();
        if (portInfo == null) {
            getEmptyPortBufferUnits(category);
            return;
        }
        addProperty(category, VL15_INIT, hex(portInfo.getVl15Init()));
        if (isExternalSWPort || isHFI) {
            addProperty(category, VL15_CREDIT_RATE,
                    hex(portInfo.getVl15CreditRate()));
            addProperty(category, CREDIT_ACK, hex(portInfo.getCreditAck()));
        } else {
            addProperty(category, VL15_CREDIT_RATE, K0383_NA.getValue());
            addProperty(category, CREDIT_ACK, K0383_NA.getValue());
        }
        addProperty(category, BUFFER_ALLOC, hex(portInfo.getBufferAlloc()));
    }

    private void getEmptyPortBufferUnits(DevicePropertyCategory category) {
        addProperty(category, VL15_INIT, "");
        addProperty(category, VL15_CREDIT_RATE, "");
        addProperty(category, CREDIT_ACK, "");
        addProperty(category, BUFFER_ALLOC, "");
    }

}
