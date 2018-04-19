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

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;

/**
 * Synchronize a tree to given elements
 */
public abstract class TreeSynchronizer<E> {
    protected final boolean removeEmptyGroup;

    /**
     * Description:
     *
     * @param nodeComparator
     */
    public TreeSynchronizer(boolean removeEmptyGroup) {
        super();
        this.removeEmptyGroup = removeEmptyGroup;
    }

    /**
     *
     * <i>Description:</i> update a tree branch to ensure its members
     * synchronize with the given elements that are described by keys. The
     * algorithm used here is very similar to merged sort.
     *
     * @param parent
     *            the tree branch to update
     * @param elements
     *            the desired elements in the given tree branch
     * @param monitors
     *            the monitors used to fire tree change events when necessary
     */
    public void updateTree(FVResourceNode parent, E[] elements,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        if (elements == null || elements.length == 0) {
            return;
        }

        if (observer == null) {
            observer = new ObserverAdapter();
        }

        int oldIndex = 0;
        int newIndex = 0;
        FVResourceNode oldNode = oldIndex >= parent.getChildCount() ? null
                : parent.getChildAt(oldIndex);
        E element = elements[newIndex];
        while (oldNode != null || element != null) {
            int comp = oldNode == null ? 1
                    : (element == null ? -1 : compare(oldNode, element));
            if (comp == 0) {
                updateNode(oldNode, parent, monitors, observer);
                oldIndex += 1;
                oldNode = oldIndex >= parent.getChildCount() ? null
                        : parent.getChildAt(oldIndex);
                newIndex += 1;
                element =
                        newIndex >= elements.length ? null : elements[newIndex];
            } else if (comp > 0) {
                addNode(oldIndex, element, parent, monitors, observer);
                oldIndex += 1;
                newIndex += 1;
                element =
                        newIndex >= elements.length ? null : elements[newIndex];
            } else {
                removeNode(oldIndex, oldNode, parent, monitors, observer);
                oldNode = oldIndex >= parent.getChildCount() ? null
                        : parent.getChildAt(oldIndex);
            }
        }

        if (removeEmptyGroup) {
            for (int i = 0; i < parent.getChildCount(); i++) {
                FVResourceNode node = parent.getChildAt(i);
                if (node.getChildCount() == 0) {
                    parent.removeChild(i);
                    if (monitors != null) {
                        fireNodesRemoved(monitors, parent, i, node);
                    }
                    i -= 1;
                }
            }
        }
    }

    protected void fireNodesRemoved(final List<ITreeMonitor> monitors,
            final FVResourceNode parent, final int childIndex,
            final FVResourceNode child) {
        for (ITreeMonitor monitor : monitors) {
            monitor.fireTreeNodesRemoved(this, parent.getPath().getPath(),
                    new int[] { childIndex }, new FVResourceNode[] { child });
        }
    }

    protected abstract int compare(FVResourceNode node, E element);

    protected FVResourceNode addNode(int index, E key, FVResourceNode parent,
            List<ITreeMonitor> monitors, IProgressObserver observer) {
        FVResourceNode node = createNode(key);
        assert node != null : "Node shoudn't be null";
        parent.addChild(index, node);
        if (monitors != null) {
            fireNodesInserted(monitors, parent, index, node);
        }
        return node;
    }

    protected void fireNodesInserted(final List<ITreeMonitor> monitors,
            final FVResourceNode parent, final int childIndex,
            final FVResourceNode child) {
        for (ITreeMonitor monitor : monitors) {
            monitor.fireTreeNodesInserted(this, parent.getPath().getPath(),
                    new int[] { childIndex }, new FVResourceNode[] { child });
        }
    }

    protected abstract FVResourceNode createNode(E key);

    /**
     *
     * <i>Description:</i> Remove a device node from the given tree branch
     *
     * @param bean
     *            the node record to be removed
     * @param parent
     *            the tree branch to update
     * @param monitors
     *            the monitors used to fire tree change event when necessary
     */
    protected void removeNode(int index, FVResourceNode node,
            FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer) {
        parent.removeChild(index);
        if (monitors != null) {
            fireNodesRemoved(monitors, parent, index, node);
        }
    }

    /**
     *
     * <i>Description:</i> update a node in a given tree branch
     *
     * @param bean
     *            the node record to be updated on a tree branch
     * @param parent
     *            the tree branch to update
     * @param monitors
     *            the monitors used to fire tree change events when necessary
     */
    protected abstract void updateNode(FVResourceNode node,
            FVResourceNode parent, List<ITreeMonitor> monitors,
            IProgressObserver observer);

    protected void fireNodesUpdated(final List<ITreeMonitor> monitors,
            final FVResourceNode parent, final int childIndex,
            final FVResourceNode child) {
        for (ITreeMonitor monitor : monitors) {
            monitor.fireTreeNodesChanged(this, parent.getPath().getPath(),
                    new int[] { childIndex }, new FVResourceNode[] { child });
        }
    }

    protected void fireStructureChanged(final List<ITreeMonitor> monitors,
            final FVResourceNode node) {
        for (ITreeMonitor monitor : monitors) {
            monitor.fireTreeStructureChanged(this, node.getPath());
        }
    }
}
