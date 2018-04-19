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
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.monitor.view.PSNodesDetailsPanel;

/**
 * per feedback we got, we do not show Router info here. we intentionally
 * change it on UI side rather than backend because we may need to support it
 * again in the future
 *
 * Controller for the statistics detail view on the Performance Summary subpage
 */
public class PSNodesDetailsController {
    private final String name;

    private final DefaultPieDataset typeDataset;

    private final PSNodesDetailsPanel view;

    /**
     * To avoid a "blink" on screen, we only clear our view when the update will
     * take a time period longer than
     * {@link com.intel.stl.ui.common.UIConstants.UPDATE_TIME}
     */
    private Timer viewClearTimer;

    public PSNodesDetailsController(String name, PSNodesDetailsPanel view) {
        this.view = view;

        this.name = name;
        view.setName(name);

        typeDataset = new DefaultPieDataset();
        typeDataset.setValue(NodeType.SWITCH, 0);
        typeDataset.setValue(NodeType.HFI, 0);
        // typeDataset.setValue(NodeType.ROUTER, 0);
        Color[] colors =
                new Color[] { NodeTypeViz.SWITCH.getColor(),
                        NodeTypeViz.HFI.getColor(),
                /* NodeTypeViz.ROUTER.getColor() */};
        view.setTypeDataset(typeDataset, colors);
    }

    /**
     * @return the view
     */
    public PSNodesDetailsPanel getView() {
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
        return typeDataset;
    }

    public void setTypes(int total, long other, EnumMap<NodeType, Integer> types) {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }

        final long[] counts = NodeTypeViz.getDistributionValues2(types);

        final String totalNumber = UIConstants.INTEGER.format(total);

        final String caNumber = UIConstants.INTEGER.format(counts[0]);
        final String caLabel =
                counts[0] == 1 ? STLConstants.K0051_HOST.getValue()
                        : STLConstants.K0052_HOSTS.getValue();

        final String swNumber = UIConstants.INTEGER.format(counts[1]);
        final String swLabel =
                counts[1] == 1 ? STLConstants.K0017_SWITCH.getValue()
                        : STLConstants.K0048_SWITCHES.getValue();

        // final String rtNumber = Integer.toString(counts[2]);
        // final String rtLabel = counts[2] == 1 ? STLConstants.K0019_ROUTER
        // .getValue() : STLConstants.K0050_ROUTERS.getValue();

        final String otherPorts = UIConstants.INTEGER.format(other);

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                typeDataset.setValue(NodeType.HFI, counts[0]);
                typeDataset.setValue(NodeType.SWITCH, counts[1]);
                // typeDataset.setValue(NodeType.ROUTER, counts[2]);

                view.setTotalNumber(totalNumber);
                view.setSwitches(swNumber, swLabel);
                view.setFabricInterfaes(caNumber, caLabel);
                // view.setRouters(rtNumber, rtLabel);
                view.setOtherPorts(otherPorts);
            }
        });
    }

    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                typeDataset.clear();
            }
        });
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
