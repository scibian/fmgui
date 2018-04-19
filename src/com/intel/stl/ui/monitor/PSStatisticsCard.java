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

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.BaseCardController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.DevicesStatistics;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.monitor.view.PSStatisticsCardView;

/**
 * Controller for the statistics card on the Performance Summary subpage
 */
public class PSStatisticsCard extends
        BaseCardController<ICardListener, PSStatisticsCardView> {
    private final PSNodesDetailsController nodeController;

    private final PSPortsDetailsController portController;

    public PSStatisticsCard(PSStatisticsCardView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);

        nodeController =
                new PSNodesDetailsController(
                        STLConstants.K0014_ACTIVE_NODES.getValue(),
                        view.getNodesPanel());

        portController =
                new PSPortsDetailsController(
                        STLConstants.K0024_ACTIVE_PORTS.getValue(),
                        view.getPortsPanel());
        HelpAction helpAction = HelpAction.getInstance();
        helpAction.getHelpBroker().enableHelpOnButton(view.getHelpButton(),
                helpAction.getPerformanceStatistics(), helpAction.getHelpSet());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ICardController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getPerformanceStatistics();
    }

    /**
     * @return the nodesController
     */
    public PSNodesDetailsController getNodesController() {
        return nodeController;
    }

    /**
     * @return the portsController
     */
    public PSPortsDetailsController getPortsController() {
        return portController;
    }

    public void updateStatistics(DevicesStatistics dgStats) {

        long totalPorts = dgStats.getNumAtivePorts();
        portController.setDeviceTypes(totalPorts, dgStats.getPortTypesDist());
        portController.setFlowType(dgStats.getInternalPorts(),
                dgStats.getExternalPorts());

        int totalNodes = dgStats.getNumNodes();
        nodeController.setTypes(totalNodes, dgStats.getOtherPorts(),
                dgStats.getNodeTypesDist());
    }

    public String getTitle(GroupStatistics stats) {
        return STLConstants.K0007_SUBNET.getValue() + ": " + stats.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ICardController#clear()
     */
    @Override
    public void clear() {
        nodeController.clear();
        portController.clear();
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

}
