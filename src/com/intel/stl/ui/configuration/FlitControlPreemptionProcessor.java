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

import static com.intel.stl.ui.model.DeviceProperty.LARGE_PKT_LIMIT;
import static com.intel.stl.ui.model.DeviceProperty.MAX_SMALL_PKT_LIMIT;
import static com.intel.stl.ui.model.DeviceProperty.MIN_INITIAL;
import static com.intel.stl.ui.model.DeviceProperty.MIN_TAIL;
import static com.intel.stl.ui.model.DeviceProperty.PREEMPTION_LIMIT;
import static com.intel.stl.ui.model.DeviceProperty.SMALL_PKT_LIMIT;

import com.intel.stl.api.subnet.FlitControlBean;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.ui.model.DevicePropertyCategory;

public class FlitControlPreemptionProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();

        if (portInfo == null) {
            getEmptyFlitControlPreemption(category);
            return;
        }
        FlitControlBean flitInfo = portInfo.getFlitControl();
        if (flitInfo == null) {
            getEmptyFlitControlPreemption(category);
            return;
        }
        addProperty(category, MIN_INITIAL, hex(flitInfo.getMinInitial()));
        addProperty(category, MIN_TAIL, hex(flitInfo.getMinTail()));
        addProperty(category, LARGE_PKT_LIMIT, hex(flitInfo.getLargePktLimit()));
        addProperty(category, SMALL_PKT_LIMIT, hex(flitInfo.getSmallPktLimit()));
        addProperty(category, MAX_SMALL_PKT_LIMIT,
                hex(flitInfo.getMaxSmallPktLimit()));
        addProperty(category, PREEMPTION_LIMIT,
                hex(flitInfo.getPreemptionLimit()));
    }

    private void getEmptyFlitControlPreemption(DevicePropertyCategory category) {
        addProperty(category, MIN_INITIAL, "");
        addProperty(category, MIN_TAIL, "");
        addProperty(category, LARGE_PKT_LIMIT, "");
        addProperty(category, SMALL_PKT_LIMIT, "");
        addProperty(category, MAX_SMALL_PKT_LIMIT, "");
        addProperty(category, PREEMPTION_LIMIT, "");
    }
}
