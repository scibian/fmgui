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

import com.intel.stl.ui.common.IProgressObserver;

public class CopyBasedNodesSynchronizer extends TreeSynchronizer<Long> {
    private static final Logger log =
            LoggerFactory.getLogger(CopyBasedNodesSynchronizer.class);

    private final Map<Long, FVResourceNode> nodeMap;

    /**
     * Description:
     *
     * @param perfApi
     * @param nodeMap
     */
    public CopyBasedNodesSynchronizer(Map<Long, FVResourceNode> nodeMap) {
        super(false);
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
            FVResourceNode node2 = nodeMap.get(element);
            String name2 = node2 == null ? null : node2.getName();
            res = TreeNodeFactory.comapreNodeName(name1, name2);
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#createNode(java.lang.Object)
     */
    @Override
    protected FVResourceNode createNode(Long key) {
        FVResourceNode node = nodeMap.get(key);
        return node == null ? new FVResourceNode("null", null, -1)
                : node.copy();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.tree.TreeUpater#updateNode(com.intel.stl.ui.
     * monitor.FVResourceNode, com.intel.stl.ui.monitor.FVResourceNode,
     * com.intel.stl.ui.monitor.FVTreeModel,
     * com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    protected void updateNode(FVResourceNode node, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        FVResourceNode refNode = nodeMap.get(node.getGuid());
        if (refNode == null) {
            throw new IllegalArgumentException("Couldn't find FVResourceNode "
                    + node.getId() + ":" + node.getGuid());
        }

        Map<Integer, FVResourceNode> updated =
                new HashMap<Integer, FVResourceNode>();
        boolean hasStructureChange = false;
        int toUpdate = Math.min(refNode.getChildCount(), node.getChildCount());
        for (int i = 0; i < toUpdate; i++) {
            // update ports
            FVResourceNode port = node.getChildAt(i);
            FVResourceNode refPort = refNode.getChildAt(i);
            if (port.getType() != refPort.getType()) {
                boolean hasChanged = port.getType() != refPort.getType();
                if (hasChanged) {
                    port.setType(refPort.getType());
                    updated.put(i, port);
                }
            }
        }
        if (toUpdate < node.getChildCount()) {
            // remove ports
            for (int i = toUpdate; i < node.getChildCount(); i++) {
                node.removeChild(toUpdate);
                if (!hasStructureChange) {
                    hasStructureChange = true;
                }
            }
        } else if (toUpdate < refNode.getChildCount()) {
            // add ports
            for (int i = toUpdate; i < refNode.getChildCount(); i++) {
                FVResourceNode port = refNode.getChildAt(i).copy();
                node.addChild(port);
                if (!hasStructureChange) {
                    hasStructureChange = true;
                }
            }
        }

        if (node.getChildCount() == 0) {
            log.error("Empty Node: refNodeSize={} toUpdate={}",
                    node.getChildCount(), toUpdate);
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

}
