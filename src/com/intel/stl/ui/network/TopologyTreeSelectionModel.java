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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.DefaultTreeSelectionModel;
import javax.swing.tree.TreePath;

import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

/**
 * Special tree selection model that only allows
 * 1) selections on the same level
 * 2) forbid selection on inactive port
 * 3) ignore selection on switch zero ports if they are combined with other
 * selections
 */
public class TopologyTreeSelectionModel extends DefaultTreeSelectionModel {
    private static final long serialVersionUID = 8288776306386190352L;

    private final Set<TreePath> switchZeroPorts = new HashSet<TreePath>();

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.tree.DefaultTreeSelectionModel#setSelectionPaths(javax.swing
     * .tree.TreePath[])
     */
    @Override
    public void setSelectionPaths(TreePath[] pPaths) {
        if (pPaths.length <= 0) {
            return;
        }

        if (pPaths[0] != null) {
            int len = pPaths[0].getPathCount();
            super.setSelectionPaths(filterPaths(pPaths, len, false));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.tree.DefaultTreeSelectionModel#addSelectionPaths(javax.swing
     * .tree.TreePath[])
     */
    @Override
    public void addSelectionPaths(TreePath[] paths) {
        if (selection != null && selection.length > 0) {
            int len = selection[0].getPathCount();
            super.addSelectionPaths(filterPaths(paths, len, true));
        } else {
            super.addSelectionPaths(paths);
        }
    }

    protected TreePath[] filterPaths(TreePath[] paths, int len, boolean addMode) {
        List<TreePath> validPaths = new ArrayList<TreePath>();
        List<Integer> currentSwitchZeroPorts = new ArrayList<Integer>();
        // filter out inactive ports
        for (TreePath path : paths) {
            if (path == null) {
                continue;
            }
            FVResourceNode node = (FVResourceNode) path.getLastPathComponent();
            if (path.getPathCount() == len
                    && node.getType() != TreeNodeType.INACTIVE_PORT) {
                validPaths.add(path);
                if (isSwitchZeroPort(node)) {
                    currentSwitchZeroPorts.add(validPaths.size() - 1);
                }
            }
        }
        // filter out switch zero ports if they are combined with other
        // selections
        int totalSelections = validPaths.size();
        int totalSwitchZeroPorts = currentSwitchZeroPorts.size();
        if (addMode) {
            totalSelections += (selection == null ? 0 : selection.length);
            totalSwitchZeroPorts += switchZeroPorts.size();
        }
        if (totalSwitchZeroPorts != totalSelections) {
            for (int i = currentSwitchZeroPorts.size() - 1; i >= 0; i--) {
                int index = currentSwitchZeroPorts.get(i);
                validPaths.remove(index);
            }
            if (addMode) {
                removeSelectionPaths(switchZeroPorts.toArray(new TreePath[0]));
            }
            switchZeroPorts.clear();
        } else if (!currentSwitchZeroPorts.isEmpty()) {
            if (!addMode) {
                switchZeroPorts.clear();
            }
            for (int index : currentSwitchZeroPorts) {
                switchZeroPorts.add(validPaths.get(index));
            }
        }
        return validPaths.toArray(new TreePath[0]);
    }

    protected boolean isSwitchZeroPort(FVResourceNode node) {
        return node.getId() == 0 && node.getParent() != null
                && node.getParent().getType() == TreeNodeType.SWITCH;
    }
}
