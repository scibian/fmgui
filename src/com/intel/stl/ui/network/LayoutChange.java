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

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.model.LayoutType;
import com.intel.stl.ui.network.TreeLayout.Style;
import com.mxgraph.layout.mxFastOrganicLayout;
import com.mxgraph.layout.mxIGraphLayout;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;

public class LayoutChange implements IModelChange {
    private final TopologyTreeModel topTreeModel;

    private final LayoutType type;

    /**
     * Description:
     * 
     * @param topTreeModel
     * @param type
     */
    public LayoutChange(LayoutType type, TopologyTreeModel topTreeModel) {
        super();
        this.type = type;
        this.topTreeModel = topTreeModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.network.IModelChange#execute(com.intel.stl.ui.network
     * .TopGraph, com.intel.stl.ui.common.ICancelIndicator)
     */
    @Override
    public boolean execute(TopGraph graph, ICancelIndicator indicator) {
        mxIGraphLayout layout = null;
        switch (type) {
            case FORCE_DIRECTED:
                graph.expandAll();
                layout = new mxFastOrganicLayout(graph);
                break;
            case HIERARCHICAL:
                graph.expandAll();
                layout = new mxHierarchicalLayout(graph);
                break;
            case TREE_CIRCLE:
                layout = new TreeLayout(graph, topTreeModel, Style.CIRCLE);
                break;
            case TREE_SLASH:
                layout = new TreeLayout(graph, topTreeModel, Style.SLASH);
                break;
            case TREE_LINE:
                layout = new TreeLayout(graph, topTreeModel, Style.LINE);
                break;
            default:
                throw new IllegalArgumentException("Unknown type " + type);
        }

        if (layout instanceof TreeLayout) {
            ((TreeLayout) layout).execute(graph.getDefaultParent(), indicator);
        } else {
            layout.execute(graph.getDefaultParent());
        }
        return true;
    }

}
