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

package com.intel.stl.ui.monitor.tree;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.monitor.TreeNodeType;

public class CreationBasedNodesSynchronizer extends TreeSynchronizer<Long> {
    private static final Logger log =
            LoggerFactory.getLogger(CreationBasedNodesSynchronizer.class);

    private final ISubnetApi subnetApi;

    private final Map<Long, NodeRecordBean> nodeMap;

    /**
     * Description:
     *
     * @param nodeComparator
     * @param nodeMap
     */
    public CreationBasedNodesSynchronizer(ISubnetApi subnetApi,
            Map<Long, NodeRecordBean> nodeMap) {
        super(false);
        this.subnetApi = subnetApi;
        this.nodeMap = nodeMap;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.tree.FastTreeUpater#compare(com.intel.stl.ui
     * .monitor.FVResourceNode, java.lang.Object)
     */
    @Override
    protected int compare(FVResourceNode node, Long element) {
        int res = Long.compare(node.getGuid(), element);
        if (res != 0) {
            String name1 = node.getName();
            NodeRecordBean node2 = nodeMap.get(element);
            String name2 = node2 == null ? null : node2.getNodeDesc();
            res = TreeNodeFactory.comapreNodeName(name1, name2);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#createNode(int)
     */
    @Override
    protected FVResourceNode createNode(Long guid) {
        NodeRecordBean bean = nodeMap.get(guid);
        return bean == null ? new FVResourceNode("null", null, -1)
                : TreeNodeFactory.createNode(bean);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#addNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected FVResourceNode addNode(int index, Long id, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        FVResourceNode node =
                super.addNode(index, id, parent, monitors, observer);
        // we call updateNode to fill ports for a device node
        updateNode(node, parent, monitors, observer);
        return node;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#updateNode(com.intel.stl.ui.
     * monitor.FVResourceNode, com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected void updateNode(FVResourceNode node, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        NodeRecordBean bean = nodeMap.get(node.getGuid());
        if (bean == null) {
            log.warn("Couldn't update tree because no node " + node.getId()
                    + ":" + node.getGuid() + " found");
            return;
        }

        // a node can have LID change, e.g we change Lmc in opafm.xml. So we
        // need to update it. Same as node description.
        node.setId(bean.getLid());
        node.setTitle(bean.getNodeDesc());
        Map<Integer, FVResourceNode> updated =
                new HashMap<Integer, FVResourceNode>();
        boolean hasStructureChange = false;
        int numPorts = bean.getNodeInfo().getNumPorts();
        if (node.getType() == TreeNodeType.SWITCH) {
            numPorts += 1; // count in internal port
        }
        int toUpdate = Math.min(numPorts, node.getChildCount());
        for (int i = 0; i < toUpdate; i++) {
            // update ports
            FVResourceNode port = node.getChildAt(i);
            boolean statusChanged = setPortStatus(node, port);
            if (statusChanged) {
                updated.put(i, port);
            }
        }
        if (toUpdate < node.getChildCount()) {
            // remove ports
            while (node.getChildCount() > toUpdate) {
                node.removeChild(toUpdate);
                if (!hasStructureChange) {
                    hasStructureChange = true;
                }
            }
        } else if (toUpdate < numPorts) {
            // add ports
            if (node.getType() == TreeNodeType.SWITCH) {
                numPorts -= 1;
                toUpdate -= 1;
            }
            for (int i = toUpdate + 1; i <= numPorts; i++) {
                FVResourceNode port = new FVResourceNode(Integer.toString(i),
                        TreeNodeType.ACTIVE_PORT, i);

                // If the port is in the hash set, make it active
                // otherwise make it inactive
                setPortStatus(node, port);
                node.addChild(port);
                if (!hasStructureChange) {
                    hasStructureChange = true;
                }
            }
        }

        if (node.getChildCount() == 0) {
            log.error(
                    "Empty Node: numPorts=" + numPorts + " toUpdate=" + toUpdate
                            + " NodeRecordBean=" + bean,
                    new Exception("Empty Node"));
        }

        if (monitors == null) {
            return;
        }

        if (hasStructureChange) {
            fireStructureChanged(monitors, node);
        } else if (!updated.isEmpty()) {
            for (Integer childIndex : updated.keySet()) {
                fireNodesUpdated(monitors, node, childIndex,
                        updated.get(childIndex));
            }
        }
    }

    private boolean setPortStatus(FVResourceNode parentNode,
            FVResourceNode portNode) {
        int lid = parentNode.getId();
        int portNum = portNode.getId();
        boolean isActive = false;
        if (parentNode.getType() == TreeNodeType.SWITCH) {
            isActive = subnetApi.hasPort(lid, (short) portNum);
        } else {
            assert portNum > 0 : "HFI(" + parentNode
                    + ") has invalid local port number " + portNum;
            isActive = subnetApi.hasLocalPort(lid, (short) portNum);
        }

        if (isActive) {
            if (portNode.getType() != TreeNodeType.ACTIVE_PORT) {
                portNode.setType(TreeNodeType.ACTIVE_PORT);
                return true;
            }
        } else {
            if (portNode.getType() != TreeNodeType.INACTIVE_PORT) {
                portNode.setType(TreeNodeType.INACTIVE_PORT);
                return true;
            }
        }
        return false;
    }

}
