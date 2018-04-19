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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.NameSorter;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.monitor.TreeNodeType;

public class DeviceTypesTreeSynchronizer
        extends TreeSynchronizer<TreeNodeType> {
    private final ISubnetApi subnetApi;

    private Map<Long, NodeRecordBean> nodeMap;

    private Map<TreeNodeType, List<Long>> typeMap;

    private final CreationBasedNodesSynchronizer nodesUpadter;

    public DeviceTypesTreeSynchronizer(ISubnetApi subnetApi) {
        this(subnetApi, true);
    }

    /**
     * Description:
     *
     * @param nodeComparator
     * @param nodeMap
     */
    public DeviceTypesTreeSynchronizer(ISubnetApi subnetApi,
            boolean removeEmptyGroup) {
        super(removeEmptyGroup);
        this.subnetApi = subnetApi;
        initData();
        nodesUpadter = new CreationBasedNodesSynchronizer(subnetApi, nodeMap);
    }

    protected void initData() {
        // Retrieve a list of nodes from the Subnet API
        List<NodeRecordBean> allNodeBeans;
        try {
            allNodeBeans = subnetApi.getNodes(false);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(UILabels.STL90005_UPDATE_TREE_FAILED
                    .getDescription(STLConstants.K0407_DEVICE_TYPES.getValue(),
                            StringUtils.getErrorMessage(e)));
        }

        nodeMap = new HashMap<Long, NodeRecordBean>();
        Map<NodeType, List<Long>> nodeTypeMap =
                new HashMap<NodeType, List<Long>>();
        for (NodeRecordBean node : allNodeBeans) {
            long guid = node.getNodeInfo().getNodeGUID();
            nodeMap.put(guid, node);
            NodeType type = node.getNodeType();
            List<Long> members = nodeTypeMap.get(type);
            if (members == null) {
                members = new LinkedList<Long>();
                nodeTypeMap.put(type, members);
            }
            members.add(guid);
        }

        typeMap = new LinkedHashMap<TreeNodeType, List<Long>>();
        Comparator<Long> comparator = new Comparator<Long>() {

            @Override
            public int compare(Long o1, Long o2) {
                NodeRecordBean node1 = nodeMap.get(o1);
                NodeRecordBean node2 = nodeMap.get(o2);
                String name1 = node1 == null ? null : node1.getNodeDesc();
                String name2 = node2 == null ? null : node2.getNodeDesc();
                return NameSorter.instance().compare(name1, name2);
            }

        };
        List<Long> members = nodeTypeMap.get(NodeType.HFI);
        if (members != null) {
            Collections.sort(members, comparator);
            typeMap.put(TreeNodeType.HCA_GROUP, members);
        }
        members = nodeTypeMap.get(NodeType.SWITCH);
        if (members != null) {
            Collections.sort(members, comparator);
            typeMap.put(TreeNodeType.SWITCH_GROUP, members);
        }
        members = nodeTypeMap.get(NodeType.ROUTER);
        if (members != null) {
            Collections.sort(members, comparator);
            typeMap.put(TreeNodeType.ROUTER_GROUP, members);
        }
    }

    public void updateTree(FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        List<TreeNodeType> types = new ArrayList<TreeNodeType>();
        for (TreeNodeType type : typeMap.keySet()) {
            if (!typeMap.get(type).isEmpty()) {
                types.add(type);
            }
        }
        updateTree(parent, types.toArray(new TreeNodeType[0]), monitors,
                observer);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.tree.FastTreeUpater#compare(com.intel.stl.ui
     * .monitor.FVResourceNode, java.lang.Object)
     */
    @Override
    protected int compare(FVResourceNode node, TreeNodeType element) {
        return TreeNodeFactory.comapreTreeNodeType(node.getType(), element);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#createNode(int)
     */
    @Override
    protected FVResourceNode createNode(TreeNodeType type) {
        return TreeNodeFactory.createTypeNode(type);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.tree.TreeUpater#addNode(int,
     * com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel)
     */
    @Override
    protected FVResourceNode addNode(int index, TreeNodeType key,
            FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        FVResourceNode node =
                super.addNode(index, key, parent, monitors, observer);
        // we call updateNode to fill children for a device type node
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
        TreeNodeType type = node.getType();
        List<Long> elements = typeMap.get(type);
        if (elements != null) {
            nodesUpadter.updateTree(node, elements.toArray(new Long[0]),
                    monitors, observer);
        }
    }

}
