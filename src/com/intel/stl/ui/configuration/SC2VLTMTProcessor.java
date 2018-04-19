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

import static com.intel.stl.ui.model.DeviceProperty.SC;
import static com.intel.stl.ui.model.DeviceProperty.VLT;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.SC2VLMTRecordBean;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyItem;

public class SC2VLTMTProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortRecordBean portBean = context.getPort();
        ISubnetApi subnetApi = context.getContext().getSubnetApi();

        // For node type Switch and port 0, don't process and display
        // N/A screen.
        if (portBean == null || portBean.getEndPortLID() == 0) {
            return;
        }

        SC2VLMTRecordBean sc2vltmtRec =
                subnetApi.getSC2VLTMT(portBean.getEndPortLID(),
                        portBean.getPortNum());
        if (sc2vltmtRec != null) {
            getSC2VLTMT(category, sc2vltmtRec.getData());
        } else {
            return;
        }
    }

    private void getSC2VLTMT(DevicePropertyCategory category, byte[] sc2vlt) {
        List<Double> sc2vltSeries = new ArrayList<Double>();

        for (int i = 0; i < sc2vlt.length; i++) {
            String doubleValue = dec((short) (sc2vlt[i] & 0xff));
            sc2vltSeries.add(Double.parseDouble(doubleValue));
        }

        double[] series = new double[sc2vltSeries.size()];
        for (int i = 0; i < sc2vltSeries.size(); i++) {
            series[i] = sc2vltSeries.get(i);
        }

        DevicePropertyItem property = new DevicePropertyItem(VLT, series);
        category.addPropertyItem(property);

        DevicePropertyItem sc =
                new DevicePropertyItem(SC, new Integer(SAConstants.STL_MAX_VLS));
        category.addPropertyItem(sc);
    }

}
