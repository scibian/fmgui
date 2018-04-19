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

package com.intel.stl.ui.monitor;

import java.util.ArrayList;
import java.util.List;

import javax.swing.tree.TreePath;

import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeModel;

public class TreeSelection {
    private final FVTreeModel treeModel;

    private final List<FVResourceNode> nodes = new ArrayList<FVResourceNode>();

    private final List<Boolean> isExpanded = new ArrayList<Boolean>();

    /**
     * Description:
     * 
     * @param treeModel
     */
    public TreeSelection(FVTreeModel treeModel) {
        super();
        this.treeModel = treeModel;
    }

    public void addNode(FVResourceNode node, boolean isExpanded) {
        nodes.add(node);
        this.isExpanded.add(isExpanded);
    }

    /**
     * @return the treeModel
     */
    public FVTreeModel getTreeModel() {
        return treeModel;
    }

    /**
     * @return the node
     */
    public List<FVResourceNode> getNodes() {
        return nodes;
    }

    public TreePath[] getPaths() {
        TreePath[] paths = new TreePath[nodes.size()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = nodes.get(i).getPath();
        }
        return paths;
    }

    /**
     * @return the isExpanded
     */
    public boolean[] isExpanded() {
        boolean[] res = new boolean[isExpanded.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = isExpanded.get(i);
        }
        return res;
    }

    public boolean isValid() {
        if (treeModel == null || nodes.isEmpty()) {
            return false;
        }

        for (FVResourceNode node : nodes) {
            if (node != null) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result =
                prime * result
                        + ((isExpanded == null) ? 0 : isExpanded.hashCode());
        result = prime * result + ((nodes == null) ? 0 : nodes.hashCode());
        result =
                prime * result
                        + ((treeModel == null) ? 0 : treeModel.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        TreeSelection other = (TreeSelection) obj;
        if (isExpanded == null) {
            if (other.isExpanded != null) {
                return false;
            }
        } else if (!isExpanded.equals(other.isExpanded)) {
            return false;
        }
        if (nodes == null) {
            if (other.nodes != null) {
                return false;
            }
        } else if (!nodes.equals(other.nodes)) {
            return false;
        }
        if (treeModel == null) {
            if (other.treeModel != null) {
                return false;
            }
        } else if (!treeModel.equals(other.treeModel)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TreeSelection [treeModel=" + treeModel + ", nodes=" + nodes
                + ", isExpanded=" + isExpanded + "]";
    }

}
