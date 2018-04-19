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

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.ui.monitor.TreeNodeType;

public class DeviceTypesTreeUpdater implements ITreeUpdater {
    private final static Logger log =
            LoggerFactory.getLogger(DeviceTypesTreeUpdater.class);

    private final ISubnetApi subnetApi;

    protected final Comparator<FVResourceNode> typeNodeComparator;

    protected final Comparator<FVResourceNode> nodeComparator;

    /**
     * Description:
     *
     * @param subnetApi
     */
    public DeviceTypesTreeUpdater(ISubnetApi subnetApi) {
        super();
        this.subnetApi = subnetApi;

        typeNodeComparator = TreeNodeFactory.getTypeNodeComparator();
        nodeComparator = TreeNodeFactory.getNodeComparator();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.IPartialTreeUpdater#addNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    public void addNode(int lid, FVResourceNode tree,
            List<ITreeMonitor> monitors) {
        updateNode(lid, tree, true, monitors);
    }

    @Override
    public void updateNode(int lid, FVResourceNode tree,
            List<ITreeMonitor> monitors) {
        updateNode(lid, tree, true, monitors);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.IPartialTreeUpdater#removeNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    public void removeNode(int lid, FVResourceNode tree,
            boolean removeEmptyParents, List<ITreeMonitor> monitors) {
        updateNode(lid, tree, true, monitors);
    }

    public void updateNode(int lid, FVResourceNode tree,
            boolean removeEmptyParents, List<ITreeMonitor> monitors) {
        NodeRecordBean bean = null;
        try {
            bean = subnetApi.getNode(lid);
        } catch (SubnetDataNotFoundException e) {
            // This node is not found from fabric.
        }

        if (bean == null || !bean.isActive()) {
            removeDeviceTypesNode(lid, tree, true, monitors);
            return;
        }

        long guid = bean.getNodeInfo().getNodeGUID();
        Map<Long, NodeRecordBean> nodeMap = new HashMap<Long, NodeRecordBean>();
        nodeMap.put(guid, bean);
        CreationBasedNodesSynchronizer nodeUpdater =
                new CreationBasedNodesSynchronizer(subnetApi, nodeMap);

        FVResourceNode typeNode = getTypeNode(bean, tree);
        FVResourceNode node = TreeNodeFactory.createNode(bean);

        Vector<FVResourceNode> children = typeNode.getChildren();
        int index = Collections.binarySearch(children, node, nodeComparator);
        if (index < 0) {
            index = -index - 1;
            nodeUpdater.addNode(index, guid, typeNode, monitors, null);
        } else {
            FVResourceNode updateNode = typeNode.getChildAt(index);
            nodeUpdater.updateNode(updateNode, typeNode, monitors, null);
        }
    }

    protected FVResourceNode getTypeNode(NodeRecordBean bean,
            FVResourceNode parent) {
        TreeNodeType treeNodeType =
                TreeNodeFactory.getTreeNodeType(bean.getNodeType());
        FVResourceNode node = TreeNodeFactory.createTypeNode(treeNodeType);
        Vector<FVResourceNode> children = parent.getChildren();
        int index =
                Collections.binarySearch(children, node, typeNodeComparator);
        if (index < 0) {
            int pos = -index - 1;
            parent.addChild(pos, node);
        } else {
            node = parent.getChildAt(index);
        }

        return node;
    }

    public void removeDeviceTypesNode(int lid, FVResourceNode tree,
            boolean removeEmptyParents, List<ITreeMonitor> monitors) {
        for (int i = 0; i < tree.getChildCount(); i++) {
            FVResourceNode typeNode = tree.getChildAt(i);
            for (int j = 0; j < typeNode.getChildCount(); j++) {
                FVResourceNode node = typeNode.getChildAt(j);
                if (node.getId() == lid) {
                    int index = j;
                    FVResourceNode parent = typeNode;
                    if (removeEmptyParents && typeNode.getChildCount() == 1) {
                        index = tree.getIndex(typeNode);
                        parent = tree;
                    }
                    parent.removeChild(index);
                    if (monitors != null) {
                        for (ITreeMonitor monitor : monitors) {
                            monitor.fireTreeNodesRemoved(this,
                                    parent.getPath().getPath(),
                                    new int[] { index },
                                    new FVResourceNode[] { node });
                        }
                    }
                    return;
                }
            }
        }
        log.warn("Node lid=" + lid + " doesn't exist!");
        // throw new IllegalArgumentException("Node lid=" + lid
        // + " doesn't exist!");
    }
}
