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

import org.jfree.util.Log;

import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SwitchInfoBean;
import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class CategoryProcessorContext implements ICategoryProcessorContext {

    private FVResourceNode resourceNode;

    private final Context context;

    private NodeRecordBean node;

    private NodeInfoBean nodeInfo;

    private SwitchRecordBean switchBean;

    private SwitchInfoBean switchInfo;

    private PortRecordBean portBean;

    private PortInfoBean portInfo;

    private LinkRecordBean linkBean;

    private NodeRecordBean neighbor;

    private boolean endPort;

    public CategoryProcessorContext(FVResourceNode node, Context context) {
        this.resourceNode = node;
        this.context = context;
        try {
            switch (node.getType()) {
                case SWITCH:
                    setupForSwitch();
                    break;
                case HFI:
                    setupForNode();
                    break;
                case PORT:
                    setupForPort();
                    break;
                case ACTIVE_PORT:
                    setupForPort();
                    break;
                case INACTIVE_PORT:
                    break;
                default:
                    break;
            }
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    // Constructor for a Node
    public CategoryProcessorContext(int lid, Context context) {
        this.context = context;
        try {
            setupForNode(lid);
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    // Constructor for a port
    public CategoryProcessorContext(int lid, short port, Context context) {
        this.context = context;
        try {
            setupForPort(lid, port);
        } catch (SubnetDataNotFoundException e) {
            RuntimeException re = new RuntimeException(e.getMessage());
            re.initCause(e);
            throw re;
        }
    }

    @Override
    public FVResourceNode getResourceNode() {
        return resourceNode;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public ISubnetApi getSubnetApi() {
        return context.getSubnetApi();
    }

    @Override
    public IConfigurationApi getConfigurationApi() {
        return context.getConfigurationApi();
    }

    @Override
    public IPerformanceApi getPerformanceApi() {
        return context.getPerformanceApi();
    }

    @Override
    public NodeRecordBean getNode() {
        return node;
    }

    @Override
    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    @Override
    public SwitchRecordBean getSwitch() {
        return switchBean;
    }

    @Override
    public SwitchInfoBean getSwitchInfo() {
        return switchInfo;
    }

    @Override
    public PortRecordBean getPort() {
        return portBean;
    }

    @Override
    public PortInfoBean getPortInfo() {
        return portInfo;
    }

    @Override
    public LinkRecordBean getLink() {
        return linkBean;
    }

    @Override
    public NodeRecordBean getNeighbor() {
        return neighbor;
    }

    @Override
    public boolean isEndPort() {
        return endPort;
    }

    private void setupForNode() throws SubnetDataNotFoundException {
        int lid = resourceNode.getId();
        node = getNode(lid);
        nodeInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
    }

    protected void setupForNode(int lid) throws SubnetDataNotFoundException {
        node = getNode(lid);
        nodeInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
    }

    private void setupForSwitch() throws SubnetDataNotFoundException {
        int lid = resourceNode.getId();
        node = getNode(lid);
        switchBean = getSubnetApi().getSwitch(lid);
        nodeInfo = null;
        switchInfo = null;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
        }
        if (switchBean != null) {
            switchInfo = switchBean.getSwitchInfo();
        }
    }

    private void setupForPort() throws SubnetDataNotFoundException {
        FVResourceNode parent = resourceNode.getParent();
        TreeNodeType type = parent.getType();
        int lid = parent.getId();
        int portNum = resourceNode.getId();
        node = getNode(lid);
        portBean = type == TreeNodeType.SWITCH
                ? getSubnetApi().getPortByPortNum(lid, (short) portNum)
                : getSubnetApi().getPortByLocalPortNum(lid, (short) portNum);
        nodeInfo = null;
        portInfo = null;
        linkBean = null;
        neighbor = null;
        endPort = false;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
            NodeType parentType = nodeInfo.getNodeTypeEnum();
            // According to the IB spec
            // Endport: A Port which can be a destination of LID-routed
            // communication within the same Subnet as the sender. All Channel
            // Adapter ports on the subnet are endports of that subnet, as is
            // Port 0 of each Switch in the subnet. Switch ports other than Port
            // 0 may not be endports. When port is used without qualification,
            // it may be assumed to mean endport whenever the context indicates
            // that it is a destination of communication.
            if ((parentType != NodeType.SWITCH)
                    || (parentType == NodeType.SWITCH && portNum == 0)) {
                endPort = true;
            }
        }
        if (portBean != null) {
            portInfo = portBean.getPortInfo();
        }
        if (portBean != null) {
            linkBean = null;
            if (type != TreeNodeType.SWITCH) {
                linkBean = getSubnetApi().getLinkBySource(lid, (short) 1);
            } else if (portNum != 0) {
                linkBean = getSubnetApi().getLinkBySource(lid, (short) portNum);
            }
            if (linkBean != null) {
                neighbor = getNode(linkBean.getToLID());
            }
        }
    }

    protected void setupForPort(int lid, short portNum)
            throws SubnetDataNotFoundException {

        node = getNode(lid);
        portBean = getPort(lid, portNum);
        nodeInfo = null;
        portInfo = null;
        linkBean = null;
        neighbor = null;
        endPort = false;
        if (node != null) {
            nodeInfo = node.getNodeInfo();
            NodeType parentType = nodeInfo.getNodeTypeEnum();
            // According to the IB spec
            // Endport: A Port which can be a destination of LID-routed
            // communication within the same Subnet as the sender. All Channel
            // Adapter ports on the subnet are endports of that subnet, as is
            // Port 0 of each Switch in the subnet. Switch ports other than Port
            // 0 may not be endports. When port is used without qualification,
            // it may be assumed to mean endport whenever the context indicates
            // that it is a destination of communication.
            if ((parentType != NodeType.SWITCH)
                    || (parentType == NodeType.SWITCH && portNum == 0)) {
                endPort = true;
            }
        }
        if (portBean != null) {
            portInfo = portBean.getPortInfo();
        }
        if (portBean != null) {
            linkBean = null;
            if (portNum != 0) {
                linkBean = getSubnetApi().getLinkBySource(lid, portNum);
            }
            if (linkBean != null) {
                neighbor = getNode(linkBean.getToLID());
            }
        }
    }

    private NodeRecordBean getNode(int lid) throws SubnetDataNotFoundException {
        NodeRecordBean node = getSubnetApi().getNode(lid);
        return node;
    }

    protected PortRecordBean getPort(int lid, short portNum) {

        PortRecordBean portBean = null;
        try {
            portBean = getSubnetApi().getPortByPortNum(lid, portNum);
        } catch (SubnetDataNotFoundException e) {
            Log.error(e.getMessage());
        }

        return portBean;
    }

    @Override
    public boolean isHFI() {
        // check if the port category is HFI ('H')
        FVResourceNode node = getResourceNode();
        if (node.getParent().getType() == TreeNodeType.HFI) {
            return true;
        }
        return false;
    }

    @Override
    public boolean isExternalSWPort() {
        // check if the port category is External Switch Port ('S')
        FVResourceNode node = getResourceNode();
        PortRecordBean portBean = getPort();
        if (portBean != null) {
            if (portBean.getPortNum() != 0
                    && node.getParent().getType() == TreeNodeType.SWITCH) {
                return true;
            }
            return false;
        }
        return false;
    }

    @Override
    public boolean isBaseSWPort0() {
        // check if the port category is Base Switch Port 0 ('P')
        FVResourceNode node = getResourceNode();
        PortRecordBean portBean = getPort();
        if (portBean.getPortNum() == 0
                && node.getParent().getType() == TreeNodeType.SWITCH) {
            return true;
        }
        return false;

    }

    @Override
    public boolean isEnhSWPort0() {
        // check if the port category is Enhanced Switch Port 0 ('E')
        SwitchInfoBean switchinfo = getSwitchInfo();
        FVResourceNode node = getResourceNode();
        PortRecordBean portBean = getPort();
        if (switchinfo.isEnhancedPort0() && portBean.getPortNum() == 0
                && node.getParent().getType() == TreeNodeType.SWITCH) {
            return true;
        }
        return false;
    }

}
