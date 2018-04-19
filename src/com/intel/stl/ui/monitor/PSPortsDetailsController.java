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

package com.intel.stl.ui.monitor;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumMap;

import javax.swing.Timer;

import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.FlowTypeViz;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.monitor.view.PSPortsDetailsPanel;

/**
 * per feedback we got, we do not show Router info here. we intentionally
 * change it on UI side rather than backend because we may need to support it
 * again in the future
 *
 * Controller for the statistics detail view on the Performance Summary subpage
 */
public class PSPortsDetailsController {
    private final String name;

    private final DefaultPieDataset deviceTypeDataset;

    private final DefaultPieDataset flowTypeDataset;

    private final PSPortsDetailsPanel view;

    private final NodeTypeViz[] nodeTypes;

    private final FlowTypeViz[] flowTypes;

    /**
     * To avoid a "blink" on screen, we only clear our view when the update will
     * take a time period longer than
     * {@link com.intel.stl.ui.common.UIConstants.UPDATE_TIME}
     */
    private Timer viewClearTimer;

    public PSPortsDetailsController(String name, PSPortsDetailsPanel view) {
        this.view = view;

        this.name = name;
        view.setName(name);

        nodeTypes = view.getNodeTypes();
        deviceTypeDataset = new DefaultPieDataset();
        Color[] colors = new Color[nodeTypes.length];
        for (int i = 0; i < nodeTypes.length; i++) {
            NodeTypeViz type = nodeTypes[i];
            deviceTypeDataset.setValue(type, 0);
            colors[i] = type.getColor();
        }
        view.setDeviceTypeDataset(deviceTypeDataset, colors);

        flowTypes = view.getFlowTypes();
        flowTypeDataset = new DefaultPieDataset();
        colors = new Color[flowTypes.length];
        for (int i = 0; i < flowTypes.length; i++) {
            FlowTypeViz type = flowTypes[i];
            flowTypeDataset.setValue(type, 0);
            colors[i] = type.getColor();
        }
        view.setFlowTypeDataset(flowTypeDataset, colors);
    }

    /**
     * @return the view
     */
    public PSPortsDetailsPanel getView() {
        return view;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the typeDataset
     */
    public DefaultPieDataset getTypeDataset() {
        return deviceTypeDataset;
    }

    public void setDeviceTypes(long total, EnumMap<NodeType, Long> types) {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }

        final long[] counts = NodeTypeViz.getDistributionValues(types);

        final String totalNumber = UIConstants.INTEGER.format(total);
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                view.setTotalNumber(totalNumber);

                for (NodeTypeViz type : nodeTypes) {
                    long count = counts[type.ordinal()];
                    deviceTypeDataset.setValue(type, counts[type.ordinal()]);
                    String number = UIConstants.INTEGER.format(count);
                    String label =
                            count == 1 ? type.getName() : type.getPluralName();
                    view.setTypeInfo(type, number, label);
                }
            }
        });
    }

    public void setFlowType(final long internalPorts, final long externalPorts) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                flowTypeDataset.setValue(FlowTypeViz.INTERNAL, internalPorts);
                flowTypeDataset.setValue(FlowTypeViz.EXTERNAL, externalPorts);

                String text = UIConstants.INTEGER.format(internalPorts);
                view.setFlowInfo(FlowTypeViz.INTERNAL, text);
                text = UIConstants.INTEGER.format(externalPorts);
                view.setFlowInfo(FlowTypeViz.EXTERNAL, text);
            }
        });
    }

    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                deviceTypeDataset.clear();
                flowTypeDataset.clear();
            }
        });
        view.clear();
        if (viewClearTimer == null) {
            viewClearTimer =
                    new Timer(UIConstants.UPDATE_TIME, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (viewClearTimer != null) {
                                view.clear();
                            }
                        }
                    });
            viewClearTimer.setRepeats(false);
        }
        viewClearTimer.restart();
    }
}
