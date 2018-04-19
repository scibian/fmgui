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

import java.util.List;

/**
 * Partially update a tree
 */
public interface ITreeUpdater {
    /**
     * 
     * <i>Description:</i> add a node (described by lid) to the given tree
     * 
     * @param lid
     *            the node to be added
     * @param tree
     *            the tree (or tree branch) to update
     * @param monitors
     *            the monitors used to first tree change event, so the
     *            listeners, such as a JTree, can update the visualization
     */
    public void addNode(int lid, FVResourceNode tree,
            List<ITreeMonitor> monitors);

    /**
     * 
     * <i>Description:</i> remove a node from the given tree
     * 
     * @param lid
     *            the node to be removed
     * @param tree
     *            same as {@link #addNode(int, FVResourceNode, List)}
     * @param removeEmptyParents
     *            indicates whether remove its parents when they are empty after
     *            we removed the node
     * @param monitors
     *            same as {@link #addNode(int, FVResourceNode, List)}
     */
    public void removeNode(int lid, FVResourceNode tree,
            boolean removeEmptyParents, List<ITreeMonitor> monitors);

    /**
     * 
     * <i>Description:</i>update a node in the given tree
     * 
     * @param lid
     *            the node to be updated
     * @param tree
     *            same as {@link #addNode(int, FVResourceNode, List)}
     * @param monitors
     *            same as {@link #addNode(int, FVResourceNode, List)}
     */
    public void updateNode(int lid, FVResourceNode tree,
            List<ITreeMonitor> monitors);
}
