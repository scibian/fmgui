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

import java.util.concurrent.TimeUnit;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.performance.SMInfoDataBean;
import com.intel.stl.ui.common.BaseCardController;
import com.intel.stl.ui.common.DistributionBarController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.StatisticsViewOld;
import com.intel.stl.ui.model.GroupStatistics;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.model.StateShortTypeViz;

/**
 */
public class StatisticsCardOld extends
        BaseCardController<ICardListener, StatisticsViewOld> {
    private final DistributionBarController nodeTypesController;

    private final DistributionBarController nodeStatesController;

    private final DistributionBarController portTypesController;

    private final DistributionBarController portStatesController;

    public StatisticsCardOld(StatisticsViewOld view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        nodeTypesController =
                new DistributionBarController(view.getNodeTypesBar(),
                        NodeTypeViz.names, null, NodeTypeViz.colors);
        nodeStatesController =
                new DistributionBarController(view.getNodeStatesBar(),
                        StateShortTypeViz.names, null, StateShortTypeViz.colors);
        portTypesController =
                new DistributionBarController(view.getPortTypesBar(),
                        NodeTypeViz.names, null, NodeTypeViz.colors);
        portStatesController =
                new DistributionBarController(view.getPortStatesBar(),
                        StateShortTypeViz.names, null, StateShortTypeViz.colors);
    }

    /**
     * @return the nodeTypesController
     */
    public DistributionBarController getNodeTypesController() {
        return nodeTypesController;
    }

    /**
     * @return the nodeStatesController
     */
    public DistributionBarController getNodeStatesController() {
        return nodeStatesController;
    }

    /**
     * @return the portTypesController
     */
    public DistributionBarController getPortTypesController() {
        return portTypesController;
    }

    /**
     * @return the portStatesController
     */
    public DistributionBarController getPortStatesController() {
        return portStatesController;
    }

    public void updateStatistics(final GroupStatistics sta) {
        final int totalNodes = sta.getNumNodes();
        nodeTypesController.setDistribution(NodeTypeViz
                .getDistributionValues2(sta.getNodeTypesDist()));
        nodeStatesController.setDistribution(StateShortTypeViz
                .getDistributionValues(sta.getNumNoRespNodes(),
                        sta.getNumSkippedNodes(), totalNodes));

        final long totalPorts = sta.getNumActivePorts();
        portTypesController.setDistribution(NodeTypeViz
                .getDistributionValues(sta.getPortTypesDist()));
        portStatesController.setDistribution(StateShortTypeViz
                .getDistributionValues(sta.getNumNoRespPorts(),
                        sta.getNumSkippedPorts(), totalPorts));

        // only need to do it once
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                view.setTitle(getTitle(sta));
                view.setDuration(sta.getMsmUptimeInSeconds(), TimeUnit.SECONDS);
                view.setLinks(UIConstants.INTEGER.format(sta.getNumLinks()));
                view.setNodes(UIConstants.INTEGER.format(totalNodes));
                view.setPorts(UIConstants.INTEGER.format(totalPorts));
                SMInfoDataBean msm = sta.getMasterSM();
                if (msm != null) {
                    String name = msm.getSmNodeDesc();
                    String description =
                            STLConstants.K0026_LID.getValue()
                                    + ": "
                                    + StringUtils.intHexString(msm.getLid())
                                    + " "
                                    + STLConstants.K0027_PORT_GUID.getValue()
                                    + ": "
                                    + StringUtils.longHexString(msm
                                            .getSmPortGuid());
                    view.setMsmName(name, description);
                }
            }
        });

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

}
