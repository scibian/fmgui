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

package com.intel.stl.ui.main;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.Timer;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.performance.SMInfoDataBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.BaseCardController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.StatisticsView;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.model.NodeTypeViz;

import net.engio.mbassy.bus.MBassador;

/**
 */
public class StatisticsCard
        extends BaseCardController<ICardListener, StatisticsView> {
    private final StaDetailsController nodesController;

    private final StaDetailsController portsController;

    private Timer viewClearTimer;

    public StatisticsCard(StatisticsView view, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        nodesController = new StaDetailsController(
                STLConstants.K0014_ACTIVE_NODES.getValue(),
                view.getNodesPanel());
        portsController = new StaDetailsController(
                STLConstants.K0024_ACTIVE_PORTS.getValue(),
                view.getPortsPanel()) {

            /*
             * (non-Javadoc)
             *
             * @see
             * com.intel.stl.ui.main.StaDetailsController#getTypeString(int,
             * com.intel.stl.ui.model.NodeTypeViz)
             */
            @Override
            protected String getTypeString(long count, NodeTypeViz nodeType) {
                if (count == 1) {
                    if (nodeType == NodeTypeViz.SWITCH) {
                        return STLConstants.K0135_SW_PORT.getValue();
                    } else if (nodeType == NodeTypeViz.HFI) {
                        return STLConstants.K0137_HFI_PORT.getValue();
                    }
                } else {
                    if (nodeType == NodeTypeViz.SWITCH) {
                        return STLConstants.K0136_SW_PORTS.getValue();
                    } else if (nodeType == NodeTypeViz.HFI) {
                        return STLConstants.K0138_HFI_PORTS.getValue();
                    }
                }
                return super.getTypeString(count, nodeType);
            }

        };
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.ICardController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getSubnetStatisticsName();
    }

    /**
     * @return the nodesController
     */
    public StaDetailsController getNodesController() {
        return nodesController;
    }

    /**
     * @return the portsController
     */
    public StaDetailsController getPortsController() {
        return portsController;
    }

    public void updateStatistics(final GroupStatistics sta) {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }

        int totalNodes = sta.getNumNodes();
        nodesController.setStates(totalNodes, sta.getNumNoRespNodes(),
                sta.getNumSkippedNodes());
        nodesController.setTypes(totalNodes, sta.getNodeTypesDist());

        long totalPorts = sta.getNumActivePorts();
        portsController.setStates(totalPorts, sta.getNumNoRespPorts(),
                sta.getNumSkippedPorts());
        portsController.setTypes(totalPorts, sta.getPortTypesDist());

        // only need to do it once
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                view.setTitle(getTitle(sta));
                view.setDuration(sta.getMsmUptimeInSeconds(), TimeUnit.SECONDS);
                long numLinks = sta.getNumLinks();
                long numHostLinks = sta.getNodeTypesDist().get(NodeType.HFI);
                long numSwitchLinks = 0;
                // for special case b2b, numSwitchLinks should be zero
                Integer switches = sta.getNodeTypesDist().get(NodeType.SWITCH);
                if (switches != null && switches.intValue() > 0) {
                    numSwitchLinks = numLinks - numHostLinks;
                }
                view.setNumSwitchLinks(
                        UIConstants.INTEGER.format(numSwitchLinks));
                view.setNumHostLinks(UIConstants.INTEGER.format(numHostLinks));
                view.setOtherPorts(
                        UIConstants.INTEGER.format(sta.getOtherPorts()));
                List<SMInfoDataBean> sms = sta.getSMInfo();
                if (sta.getNumSMs() > 0) {
                    view.setMsmName(sms.get(0).getSmNodeDesc(),
                            getSMDescription(sms.get(0)));
                    String[] names = null;
                    String[] descriptions = null;
                    if (sta.getNumSMs() > 1) {
                        names = new String[sms.size() - 1];
                        descriptions = new String[sms.size() - 1];
                        for (int i = 1; i < sms.size(); i++) {
                            names[i - 1] = sms.get(i).getSmNodeDesc();
                            descriptions[i - 1] = getSMDescription(sms.get(i));
                        }
                    }
                    view.setStandbySMNames(names, descriptions);
                } else {
                    view.setMsmName(null, null);
                }
                view.repaint();
            }
        });

    }

    protected String getSMDescription(SMInfoDataBean sm) {
        return STLConstants.K0026_LID.getValue() + ": "
                + StringUtils.intHexString(sm.getLid()) + " "
                + STLConstants.K0027_PORT_GUID.getValue() + ": "
                + StringUtils.longHexString(sm.getSmPortGuid());
    }

    public String getTitle(GroupStatistics sta) {
        return STLConstants.K0007_SUBNET.getValue() + ": " + sta.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseCardController#getCardListener()
     */
    @Override
    public ICardListener getCardListener() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseCardController#clear()
     */
    @Override
    public void clear() {
        nodesController.clear();
        portsController.clear();
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
