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

import java.util.Comparator;
import java.util.Map;

import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.NameSorter;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.monitor.TreeNodeType;

public class TreeNodeFactory {
    public static FVResourceNode createTypeNode(TreeNodeType type) {
        if (type == TreeNodeType.HCA_GROUP) {
            return new FVResourceNode(NodeTypeViz.HFI.getPluralName(),
                    TreeNodeType.HCA_GROUP, TreeNodeType.HCA_GROUP.ordinal());
        } else if (type == TreeNodeType.SWITCH_GROUP) {
            return new FVResourceNode(NodeTypeViz.SWITCH.getPluralName(),
                    TreeNodeType.SWITCH_GROUP,
                    TreeNodeType.SWITCH_GROUP.ordinal());
        } else if (type == TreeNodeType.ROUTER_GROUP) {
            return new FVResourceNode(NodeTypeViz.ROUTER.getName(),
                    TreeNodeType.ROUTER_GROUP,
                    TreeNodeType.ROUTER_GROUP.ordinal());
        } else {
            throw new IllegalArgumentException("Unsupported Device Type "
                    + type);
        }
    }

    public static FVResourceNode createGroupNode(String groupName, int id) {
        return new FVResourceNode(groupName, TreeNodeType.DEVICE_GROUP, id);
    }

    public static FVResourceNode createVfNode(String groupName, int id) {
        return new FVResourceNode(groupName, TreeNodeType.VIRTUAL_FABRIC, id);
    }

    public static FVResourceNode createNode(NodeRecordBean bean) {
        NodeType type = bean.getNodeType();

        // Create the appropriate node depending on the type
        if (type == NodeType.HFI) {
            return new FVResourceNode(bean.getNodeDesc(), TreeNodeType.HFI,
                    bean.getLid(), bean.getNodeInfo().getNodeGUID());
        } else if (type == NodeType.SWITCH) {
            return new FVResourceNode(bean.getNodeDesc(), TreeNodeType.SWITCH,
                    bean.getLid(), bean.getNodeInfo().getNodeGUID());
        } else if (type == NodeType.ROUTER) {
            return new FVResourceNode(bean.getNodeDesc(), TreeNodeType.ROUTER,
                    bean.getLid(), bean.getNodeInfo().getNodeGUID());
        } else {
            throw new RuntimeException("Unknown node type " + type);
        }
    }

    public static Comparator<FVResourceNode> getTypeNodeComparator() {
        return new Comparator<FVResourceNode>() {
            @Override
            public int compare(FVResourceNode node1, FVResourceNode node2) {
                // sorting by TreeNodeType enum. We should have a order of
                // HCA_GROUP, SWITCH_GROUP, ROUTER_GROUP
                TreeNodeType type1 = node1.getType();
                TreeNodeType type2 = node2.getType();
                return comapreTreeNodeType(type1, type2);
            }
        };
    }

    public static int comapreTreeNodeType(TreeNodeType type1, TreeNodeType type2) {
        int o1 = type1.ordinal();
        int o2 = type2.ordinal();
        return o1 > o2 ? 1 : (o1 < o2 ? -1 : 0);
    }

    public static Comparator<FVResourceNode> getGroupNodeComparator(
            final Map<String, Integer> groupIndices) {
        return new Comparator<FVResourceNode>() {
            @Override
            public int compare(FVResourceNode node1, FVResourceNode node2) {
                return compareNameByIndex(node1.getName(), node2.getName(),
                        groupIndices);
            }
        };
    }

    public static Comparator<FVResourceNode> getVfNodeComparator(
            final Map<String, Integer> vfIndices) {
        return new Comparator<FVResourceNode>() {

            @Override
            public int compare(FVResourceNode node1, FVResourceNode node2) {
                return compareNameByIndex(node1.getName(), node2.getName(),
                        vfIndices);
            }

        };
    }

    public static int compareNameByIndex(String name1, String name2,
            Map<String, Integer> nameIndices) {
        Integer o1 = nameIndices.get(name1);
        Integer o2 = nameIndices.get(name2);
        if (o1 == null) {
            return o2 == null ? 0 : -1;
        } else if (o2 == null) {
            return 1;
        }

        return o1 > o2 ? 1 : (o1 < o2 ? -1 : 0);
    }

    public static Comparator<FVResourceNode> getNodeComparator() {
        return new Comparator<FVResourceNode>() {
            @Override
            public int compare(FVResourceNode o1, FVResourceNode o2) {
                if (o1 == null) {
                    return o2 == null ? 0 : -1;
                } else if (o2 == null) {
                    return 1;
                } else {
                    return comapreNodeName(o1.getTitle(), o2.getTitle());
                }
            }
        };
    }

    public static int comapreNodeName(String name1, String name2) {
        return NameSorter.instance().compare(name1, name2);
    }

    public static TreeNodeType getTreeNodeType(NodeType type) {
        if (type == NodeType.HFI) {
            return TreeNodeType.HCA_GROUP;
        } else if (type == NodeType.SWITCH) {
            return TreeNodeType.SWITCH_GROUP;
        } else if (type == NodeType.ROUTER) {
            return TreeNodeType.ROUTER_GROUP;
        } else {
            throw new IllegalArgumentException("Unsupported NodeType " + type);
        }
    }
}
