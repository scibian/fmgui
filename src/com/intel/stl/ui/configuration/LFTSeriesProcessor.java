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

import static com.intel.stl.ui.model.DeviceProperty.LFT_SERIES;
import static com.intel.stl.ui.model.DeviceProperty.NUM_PORTS;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LFTRecordBean;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.ui.model.DevicePropertyItem;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class LFTSeriesProcessor extends BaseCategoryProcessor {

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        FVResourceNode node = context.getResourceNode();
        NodeInfoBean nodeInfo = context.getNodeInfo();

        if (nodeInfo == null) {
            double[] series = new double[1];
            DevicePropertyItem property = new DevicePropertyItem(LFT_SERIES, series);
            category.addPropertyItem(property);
            DevicePropertyItem numPorts = new DevicePropertyItem(NUM_PORTS, new Integer(0));
            category.addPropertyItem(numPorts);
            return;
        }
        ISubnetApi subnetApi = context.getContext().getSubnetApi();
        int lid = node.getId();
        List<LFTRecordBean> lftRecs = subnetApi.getLFT(lid);

        if (lftRecs == null)
            return;
        List<Double> lftSeries = new ArrayList<Double>();
        for (LFTRecordBean lft : lftRecs) {
            lid = lft.getBlockNum() * SAConstants.FDB_DATA_LENGTH;
            byte[] retPts = lft.getLinearFdbData();
            for (int i = 0; i < retPts.length; i++, lid++) {
                if (retPts[i] != -1) {
                    String doubleValue =
                            dec((short) (retPts[i] & 0xff)) + "." + lid;
                    lftSeries.add(Double.parseDouble(doubleValue));
                }
            }
        }
        double[] series = new double[lftSeries.size()];
        for (int i = 0; i < lftSeries.size(); i++) {
            series[i] = lftSeries.get(i).doubleValue();
        }
        DevicePropertyItem property = new DevicePropertyItem(LFT_SERIES, series);
        category.addPropertyItem(property);
        DevicePropertyItem numPorts =
                new DevicePropertyItem(NUM_PORTS, new Integer(nodeInfo.getNumPorts()));
        category.addPropertyItem(numPorts);
    }
}
