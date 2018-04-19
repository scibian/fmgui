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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.model.LayoutType;

/**
 * For a given list of node LIDs, make any necessary change on the model to
 * ensure all given nodes are visible on our graph. For the last node, if it's
 * collapsable, we flip it. 
 */
public class NodesVisibilityChange extends LayoutChange {
    private static final Logger log = LoggerFactory.getLogger(NodesVisibilityChange.class);
    
    private int[] toInspect;
    
    /**
     * Description: 
     *
     * @param type
     * @param toInspect 
     */
    public NodesVisibilityChange(LayoutType type, TopologyTreeModel topTreeModel, int[] toInspect) {
        super(type, topTreeModel);
        this.toInspect = toInspect;
    }

    /* (non-Javadoc)
     * @see com.intel.stl.ui.network.IModelChange#execute(com.intel.stl.ui.network.TopGraph, com.intel.stl.ui.common.ICancelIndicator)
     */
    @Override
    public boolean execute(TopGraph graph, ICancelIndicator indicator) {
        boolean hasChange = false;
        for (int lid : toInspect) {
            GraphNode node = (GraphNode)graph.getVertex(lid).getValue();
            // expand its "parent" when necessary to ensure this node is visible
            if (node.isEndNode()) {
                for (GraphNode nbr : node.getMiddleNeighbor()) {
                    if (nbr.isCollapsed()) {
                        nbr.setCollapsed(false);
                        if (!hasChange) {
                            hasChange = true;
                        }
                    }
                }
            }
        }
        int lid = toInspect[toInspect.length-1];
        GraphNode node = (GraphNode)graph.getVertex(lid).getValue();
        if (node.hasEndNodes()) {
            node.setCollapsed(!node.isCollapsed());
            if (!hasChange) {
                hasChange = true;
            }
        }
        
        if (hasChange) {
            super.execute(graph, indicator);
        }
        
        return hasChange;
    }

}
