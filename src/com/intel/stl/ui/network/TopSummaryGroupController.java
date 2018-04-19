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

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.UndoableJumpEvent;
import com.intel.stl.ui.event.GroupsSelectedEvent;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.SimplePropertyCategory;
import com.intel.stl.ui.model.SimplePropertyGroup;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.TopologyTier.Quality;
import com.intel.stl.ui.network.view.TopSummaryGroupPanel;

public class TopSummaryGroupController {
    private final TopSummaryGroupPanel view;

    private final MBassador<IAppEvent> eventBus;

    private final UndoHandler undoHandler;

    /**
     * Description:
     * 
     * @param view
     */
    public TopSummaryGroupController(TopSummaryGroupPanel view,
            MBassador<IAppEvent> eventBus, UndoHandler undoHandler) {
        super();
        this.view = view;
        this.eventBus = eventBus;
        this.undoHandler = undoHandler;

        view.enableHelp(true);
        HelpAction helpAction = HelpAction.getInstance();
        helpAction.getHelpBroker().enableHelpOnButton(view.getHelpButton(),
                helpAction.getTopologySummary(), helpAction.getHelpSet());
    }

    public void setModel(FVResourceNode[] selResources,
            SimplePropertyGroup model) {
        JumpToEvent origin = getOrigin(selResources);
        view.init(model.size());
        List<SimplePropertyCategory> categories = model.getList();
        int maxPortsSegment = getMaxPortsSegment(categories);
        for (int i = 0; i < categories.size(); i++) {
            TopologyTier tier =
                    (TopologyTier) categories.get(i).getItems().iterator()
                            .next().getObject();
            updateTierView(i, tier, maxPortsSegment, origin);
        }
    }

    protected JumpToEvent getOrigin(FVResourceNode[] selResources) {
        GroupsSelectedEvent event =
                new GroupsSelectedEvent(this, TopologyPage.NAME);
        for (FVResourceNode node : selResources) {
            event.addGroup(node.getTitle(), node.getType());
        }
        return event;
    }

    protected void updateTierView(int index, TopologyTier tier,
            double maxPortsSegment, JumpToEvent origin) {
        view.setTierName(index, tier.getName());

        String numSwitches = UIConstants.INTEGER.format(tier.getNumSwitches());
        String numHFIs = UIConstants.INTEGER.format(tier.getNumHFIs());
        String numPorts = UIConstants.INTEGER.format(tier.getTotalPorts());
        view.setSummary(index, numSwitches, numHFIs, numPorts);

        Quality up = tier.getUpQuality();
        Quality down = tier.getDownQuality();
        // ports
        double[] normalizedVals = new double[3];
        normalizedVals[0] = up.getTotalPorts() / maxPortsSegment;
        normalizedVals[1] = down.getTotalPorts() / maxPortsSegment;
        normalizedVals[2] = tier.getNumOtherPorts() / maxPortsSegment;
        String[] values = new String[3];
        values[0] = UIConstants.INTEGER.format(up.getTotalPorts());
        values[1] = UIConstants.INTEGER.format(down.getTotalPorts());
        values[2] = UIConstants.INTEGER.format(tier.getNumOtherPorts());
        String[] labels = new String[3];
        labels[0] = STLConstants.K2069_UP_PORTS.getValue();
        labels[1] = STLConstants.K2070_DOWN_PORTS.getValue();
        labels[2] = STLConstants.K2071_OTHER_PORTS.getValue();
        Color[] colors =
                new Color[] { UIConstants.INTEL_DARK_BLUE,
                        UIConstants.INTEL_DARK_GREEN,
                        UIConstants.INTEL_DARK_ORANGE };
        view.setPortsDist(index, normalizedVals, values, colors, labels, null);

        // slow ports
        normalizedVals = new double[2];
        normalizedVals[0] = up.getNumSlowPorts() / maxPortsSegment;
        normalizedVals[1] = down.getNumSlowPorts() / maxPortsSegment;
        values = new String[2];
        values[0] = UIConstants.INTEGER.format(up.getNumSlowPorts());
        values[1] = UIConstants.INTEGER.format(down.getNumSlowPorts());
        labels = new String[2];
        labels[0] = STLConstants.K2072_SLOW_UP_PORTS.getValue();
        labels[1] = STLConstants.K2073_SLOW_DOWN_PORTS.getValue();
        colors =
                new Color[] { UIConstants.INTEL_DARK_BLUE,
                        UIConstants.INTEL_DARK_GREEN };
        if (up.getNumSlowPorts() > 0) {
            colors[0] = UIConstants.INTEL_RED;
        }
        if (down.getNumSlowPorts() > 0) {
            colors[1] = UIConstants.INTEL_RED;
        }
        String[] tooltips = new String[2];
        tooltips[0] =
                tooltips[1] = UILabels.STL70002_SLOW_PORTS.getDescription();
        ActionListener[] actions = new ActionListener[2];
        actions[0] = createAction(up.getSlowPorts(), origin);
        actions[1] = createAction(down.getSlowPorts(), origin);
        view.setSlowPortsDist(index, normalizedVals, values, colors, labels,
                tooltips, actions);

        // degraded ports
        normalizedVals = new double[2];
        normalizedVals[0] = up.getNumDegPorts() / maxPortsSegment;
        normalizedVals[1] = down.getNumDegPorts() / maxPortsSegment;
        values = new String[2];
        values[0] = UIConstants.INTEGER.format(up.getNumDegPorts());
        values[1] = UIConstants.INTEGER.format(down.getNumDegPorts());
        colors =
                new Color[] { UIConstants.INTEL_DARK_BLUE,
                        UIConstants.INTEL_DARK_GREEN };
        if (up.getNumDegPorts() > 0) {
            colors[0] = UIConstants.INTEL_RED;
        }
        if (down.getNumDegPorts() > 0) {
            colors[1] = UIConstants.INTEL_RED;
        }
        labels = new String[2];
        labels[0] = STLConstants.K2074_DEG_UP_PORTS.getValue();
        labels[1] = STLConstants.K2075_DEG_DOWN_PORTS.getValue();
        tooltips = new String[2];
        tooltips[0] =
                tooltips[1] = UILabels.STL70003_DEG_PORTS.getDescription();
        actions = new ActionListener[2];
        actions[0] = createAction(up.getDegPorts(), origin);
        actions[1] = createAction(down.getDegPorts(), origin);
        view.setDegPortsDist(index, normalizedVals, values, colors, labels,
                tooltips, actions);
    }

    protected ActionListener createAction(List<Point> ports,
            final JumpToEvent origin) {
        if (ports == null || ports.isEmpty()) {
            return null;
        }

        final PortsSelectedEvent event =
                new PortsSelectedEvent(this, TopologyPage.NAME);
        for (Point port : ports) {
            event.addPort(port.x, (short) port.y);
        }

        ActionListener res = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                eventBus.publish(event);

                if (undoHandler != null && !undoHandler.isInProgress()) {
                    UndoableJumpEvent undoSel =
                            new UndoableJumpEvent(eventBus, origin, event);
                    undoHandler.addUndoAction(undoSel);
                }
            }

        };
        return res;
    }

    protected int getMaxPortsSegment(List<SimplePropertyCategory> categories) {
        int max = 0;
        for (int i = 0; i < categories.size(); i++) {
            TopologyTier tier =
                    (TopologyTier) categories.get(i).getItems().iterator()
                            .next().getObject();
            Quality up = tier.getUpQuality();
            Quality down = tier.getDownQuality();
            int tmp =
                    maxNumber(up.getTotalPorts(), down.getTotalPorts(),
                            tier.getNumOtherPorts());
            if (tmp > max) {
                max = tmp;
            }
        }
        return max;
    }

    private int maxNumber(int... vals) {
        int max = 0;
        for (int val : vals) {
            if (val > max) {
                max = val;
            }
        }
        return max;
    }

}
