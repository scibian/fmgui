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

import com.intel.stl.ui.model.GraphNode;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel.mxAtomicGraphModelChange;

public class CollapseChange extends mxAtomicGraphModelChange {
    protected GraphNode node;
    protected boolean collapsed, previous = true;
    
    public CollapseChange() {
        this(null, null, false);
    }
    
    /**
     * Description: 
     *
     * @param node
     * @param collapsed
     * @param previous 
     */
    public CollapseChange(mxGraphModel model, GraphNode node, boolean collapsed) {
        super(model);
        this.node = node;
        this.collapsed = collapsed;
        this.previous = collapsed;
    }

    /**
     * @return the node
     */
    public GraphNode getNode() {
        return node;
    }

    /**
     * @param node the node to set
     */
    public void setNode(GraphNode node) {
        this.node = node;
    }

    /**
     * @return the collapsed
     */
    public boolean isCollapsed() {
        return collapsed;
    }

    /**
     * @param collapsed the collapsed to set
     */
    public void setCollapsed(boolean collapsed) {
        this.collapsed = collapsed;
    }

    /**
     * @return the previous
     */
    public boolean isPrevious() {
        return previous;
    }

    /**
     * @param previous the previous to set
     */
    public void setPrevious(boolean previous) {
        this.previous = previous;
    }

    /* (non-Javadoc)
     * @see com.mxgraph.model.mxIGraphModel.mxAtomicGraphModelChange#execute()
     */
    @Override
    public void execute() {
        collapsed = previous;
        previous = node.isCollapsed();
        node.setCollapsed(collapsed);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "mxCollapseChange [node=" + node + ", collapsed=" + collapsed
                + ", previous=" + previous + "]";
    }

}
