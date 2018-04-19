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

package com.intel.stl.ui.network;

import java.util.EnumMap;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.model.PropertyItem;
import com.intel.stl.ui.model.SimplePropertyCategory;
import com.intel.stl.ui.model.SimplePropertyGroup;
import com.intel.stl.ui.model.SimplePropertyKey;

public class SubnetSummaryProcessor {
    private final String name;

    private final ISubnetApi subnetApi;

    private final ICancelIndicator cancelIndicator;

    /**
     * Description:
     * 
     * @param subnetApi
     * @param cancelIndicator
     */
    public SubnetSummaryProcessor(String name, ISubnetApi subnetApi,
            ICancelIndicator cancelIndicator) {
        super();
        this.subnetApi = subnetApi;
        this.cancelIndicator = cancelIndicator;
        this.name = name;
    }

    public SimplePropertyGroup populate() {
        SimplePropertyGroup group = new SimplePropertyGroup(name);
        if (!cancelIndicator.isCancelled()) {
            group.addPropertyCategory(populateNodes());
        }
        if (!cancelIndicator.isCancelled()) {
            group.addPropertyCategory(populatePorts());
        }
        return group;
    }

    protected SimplePropertyCategory populateNodes() {
        SimplePropertyCategory category =
                new SimplePropertyCategory(
                        STLConstants.K0014_ACTIVE_NODES.getValue(), null);
        category.setShowHeader(true);
        try {
            EnumMap<NodeType, Integer> dist =
                    subnetApi.getNodesTypeDist(false, false);

            NodeTypeViz type = NodeTypeViz.SWITCH;
            Integer count = dist.get(type.getType());
            PropertyItem<SimplePropertyKey> item =
                    populateCountItem(type, (long) count);
            category.addItem(item);

            type = NodeTypeViz.HFI;
            count = dist.get(type.getType());
            item = populateCountItem(type, (long) count);
            category.addItem(item);

        } catch (SubnetDataNotFoundException e) {
            e.printStackTrace();
        }
        return category;
    }

    protected SimplePropertyCategory populatePorts() {
        SimplePropertyCategory category =
                new SimplePropertyCategory(
                        STLConstants.K0024_ACTIVE_PORTS.getValue(), null);
        category.setShowHeader(true);
        try {
            EnumMap<NodeType, Long> dist =
                    subnetApi.getPortsTypeDist(true, false);

            NodeTypeViz type = NodeTypeViz.SWITCH;
            Long count = dist.get(type.getType());
            PropertyItem<SimplePropertyKey> item =
                    populateCountItem(type, count);
            category.addItem(item);

            type = NodeTypeViz.HFI;
            count = dist.get(type.getType());
            item = populateCountItem(type, count);
            category.addItem(item);

            type = NodeTypeViz.OTHER;
            count = dist.get(type.getType());
            item = populateCountItem(type, count);
            category.addItem(item);
        } catch (SubnetDataNotFoundException e) {
            e.printStackTrace();
        }
        return category;
    }

    protected PropertyItem<SimplePropertyKey> populateCountItem(
            NodeTypeViz type, Long count) {
        SimplePropertyKey key = new SimplePropertyKey(type.getPluralName());
        String countString =
                count == null ? STLConstants.K0039_NOT_AVAILABLE.getValue()
                        : UIConstants.INTEGER.format(count);
        return new PropertyItem<SimplePropertyKey>(key, countString);
    }
}
