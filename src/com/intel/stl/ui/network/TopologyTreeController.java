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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.event.NodeUpdateEvent;
import com.intel.stl.ui.event.PortUpdateEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoableSelection;
import com.intel.stl.ui.model.GraphCells;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.monitor.SearchController;
import com.intel.stl.ui.monitor.TreeController;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.TreeSelection;
import com.intel.stl.ui.monitor.TreeSubpageSelection;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeManager;
import com.intel.stl.ui.monitor.tree.FVTreeModel;
import com.intel.stl.ui.network.view.TopologyView;
import com.intel.stl.ui.publisher.CallbackAdapter;

import net.engio.mbassy.bus.MBassador;

public class TopologyTreeController extends TreeController<TopologyView> {
    private static final Logger log =
            LoggerFactory.getLogger(TopologyTreeController.class);

    private static final boolean DEBUG = true;

    private FVResourceNode[] lastTreeSelection;

    private final TopologyGraphController graphSelectionController;

    private String previousSubpageName;

    private String currentSubpageName;

    /**
     * Description:
     *
     * @param pTreeView
     */
    public TopologyTreeController(TopologyView pTreeView,
            MBassador<IAppEvent> eventBus, FVTreeManager treeBuilder) {
        super(pTreeView, eventBus, treeBuilder);
        graphSelectionController = new TopologyGraphController(this, eventBus);

        new SearchController(view.getSearchView(), eventBus, treeBuilder, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.TreeController#setContext(com.intel.stl.ui.main
     * .Context)
     */
    @Override
    public synchronized void setContext(final Context pContext,
            IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(2);
        graphSelectionController.setContext(pContext, subObservers[0]);
        subObservers[0].onFinish();
        if (observer.isCancelled()) {
            return;
        }
        super.setContext(pContext, subObservers[1]);
        subObservers[1].onFinish();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.TreeController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(final IProgressObserver observer) {
        isSystemUpdate = true;
        // we do refresh by recovering last tree selections which is done when
        // we refresh our hierarchy trees. Setting lastTreeSelection to null
        // will allow us respond to these selections rather ignore them.
        lastTreeSelection = null;
        final IProgressObserver[] subObservers = observer.createSubObservers(2);
        graphSelectionController.onRefresh(subObservers[0],
                new CallbackAdapter<Void>() {
                    @Override
                    public void onDone(Void result) {
                        if (!observer.isCancelled()) {
                            // refresh tree only after graph model was updated
                            // properly, so we can update tree selection on our
                            // graph correctly
                            refreshTreeOnBackground(subObservers[1]);
                        }
                    }
                });
    }

    protected void refreshTreeOnBackground(final IProgressObserver observer) {
        mContext.getTaskScheduler().submitToBackground(new Runnable() {
            @Override
            public void run() {
                TopologyTreeController.super.onRefresh(observer);
            }
        });
    }

    @Override
    public synchronized void onNodeUpdate(final NodeUpdateEvent evt) {
        // ignore portUpdateEvent
        if (evt instanceof PortUpdateEvent || mContext == null) {
            return;
        }

        isSystemUpdate = true;
        lastTreeSelection = null;
        graphSelectionController.onRefresh(null, new CallbackAdapter<Void>() {
            @Override
            public void onDone(Void result) {
                // update tree only after graph model was updated
                // properly, so we can update tree selection on our
                // graph correctly
                updateTreeOnBackground(evt);
            }
        });
    }

    protected void updateTreeOnBackground(final NodeUpdateEvent evt) {
        mContext.getTaskScheduler().submitToBackground(new Runnable() {
            @Override
            public void run() {
                TopologyTreeController.super.onNodeUpdate(evt);
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.TreeController#showNode(com.intel.stl.ui.monitor
     * .FVResourceNode)
     */
    @Override
    protected void showNode(FVResourceNode node) {
        if (node != null) {
            showNodes(new FVResourceNode[] { node });
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.TreeController#showNodes(com.intel.stl.ui.
     * monitor .FVResourceNode[])
     */
    @Override
    protected synchronized void showNodes(FVResourceNode[] nodes) {
        if (DEBUG) {
            System.out.println("Current TreeNodes " + Arrays.toString(nodes));
            System.out.println(
                    "Last TreeNodes " + Arrays.toString(lastTreeSelection));
        }

        if (nodes == null || nodes.length == 0
                || areSameNodes(lastTreeSelection, nodes)) {
            return;
        }

        previousSubpageName = graphSelectionController.getCurrentSubpage();
        // always set subpage name to null for a new set of nodes. If they come
        // from undo, we shall keep currentSubpageName because it's set during
        // undo
        if (!undoHandler.isInProgress()) {
            currentSubpageName = null;
        }

        graphSelectionController.setCurrentSubpage(currentSubpageName);
        collapseTreeSelections(lastTreeSelection, nodes);
        lastTreeSelection = nodes;
        switch (nodes[0].getType()) {
            case SWITCH:
            case HFI:
                graphSelectionController.processTreeNodes(nodes);
                break;

            case ACTIVE_PORT:
            case INACTIVE_PORT:
                graphSelectionController.processTreePorts(nodes);
                break;

            case HCA_GROUP:
            case SWITCH_GROUP:
            case DEVICE_GROUP:
            case VIRTUAL_FABRIC:
                graphSelectionController.processTreeGroups(nodes);
                break;

            default:
                graphSelectionController.onSelectionChange(new GraphCells(),
                        graphSelectionController, nodes);
                break;
        } // switch
    }

    /**
     * <i>Description:</i> to return <code>true</code>, the nodes in
     * <code>nodes1</code> and <code>nodes2</code> shall have the same path and
     * the root needs to be the same instance
     *
     * @param nodes1
     * @param nodes2
     * @return
     */
    protected boolean areSameNodes(FVResourceNode[] nodes1,
            FVResourceNode[] nodes2) {
        if (!Arrays.equals(nodes1, nodes2)) {
            return false;
        }

        for (int i = 0; i < nodes1.length; i++) {
            if (!nodes1[i].hasSamePath(nodes2[i])) {
                return false;
            } else if (nodes1[i].getRoot() != nodes2[i].getRoot()) {
                return false;
            }
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.TreeController#getUndoableSelection(com.intel
     * .stl.ui.monitor.TreeSelection, com.intel.stl.ui.monitor.TreeSelection)
     */
    @Override
    protected UndoableSelection<?> getUndoableSelection(
            TreeSelection oldSelection, TreeSelection newSelection) {
        // In theory, the currentSubpageName shall be the real current subpage
        // name. However, it will take time to know the subpage name because
        // the subpage is set after background tasks finished. This will require
        // some sync and the control logic can be complex. To make things
        // simple, we always set desired subpage name to null, and rely on the
        // same logic to figure out the real subpage, i.e. always pick up the
        // first subpage. In this way, we needn't to wait for the real subpage
        // and can safely generate undo action by using null for
        // currentSubpageName.
        TreeSubpageSelection oldTSSelection =
                new TreeSubpageSelection(oldSelection, previousSubpageName);
        TreeSubpageSelection newTSSelection =
                new TreeSubpageSelection(newSelection, currentSubpageName);
        return new UndoableTopTreeSelection(this, oldTSSelection,
                newTSSelection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.TreeController#getCurrentNode()
     */
    @Override
    protected FVResourceNode getCurrentNode() {
        if (lastTreeSelection == null || lastTreeSelection.length == 0) {
            return null;
        } else {
            return lastTreeSelection[lastTreeSelection.length - 1];
        }
    }

    protected void collapseTreeSelections(FVResourceNode[] previous,
            FVResourceNode[] current) {
        if (previous == null || previous.length == 0) {
            return;
        }

        Set<TreePath> toCollapse = new HashSet<TreePath>();
        for (FVResourceNode node : previous) {
            TreePath path = null;
            switch (node.getType()) {
                case SWITCH:
                case HFI:
                    if (node.getParent() != null) {
                        path = node.getParent().getPath();
                    }
                    break;

                case ACTIVE_PORT:
                case INACTIVE_PORT:
                    if (node.getParent() != null) {
                        path = node.getParent().getPath();
                    }
                    break;

                default:
                    break;
            }
            if (path != null) {
                toCollapse.add(path);
            }
        }
        for (FVResourceNode node : current) {
            switch (node.getType()) {
                case SWITCH:
                case HFI:
                    if (node.getParent() != null) {
                        toCollapse.remove(node.getParent().getPath());
                    }
                    break;

                case ACTIVE_PORT:
                case INACTIVE_PORT:
                    FVResourceNode parent = node.getParent();
                    if (parent != null) {
                        toCollapse.remove(parent.getPath());
                        parent = parent.getParent();
                        if (parent != null) {
                            toCollapse.remove(parent.getPath());
                        }
                    }
                    break;

                default:
                    break;
            }
        }
        for (TreePath path : toCollapse) {
            view.collapseTreePath(getCurrentTreeModel(), path);
        }
        if (!toCollapse.isEmpty()) {
            view.ensureSelectionVisible(getCurrentTreeModel());
        }
    }

    protected void selectTreeSelections(FVResourceNode[] nodes) {
        if (nodes == null || nodes.length == 0) {
            return;
        }

        TreePath[] paths = new TreePath[nodes.length];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = nodes[i].getPath();
        }
        view.setTreeSelection(getCurrentTreeModel(), paths);
    }

    /**
     * Description:
     *
     */
    public void clearTreeSelection() {
        view.clearTreeSelection(getCurrentTreeModel());
    }

    protected FVResourceNode[] selectTreeNodes(List<GraphNode> nodes,
            ICancelIndicator indicator) {
        FVTreeModel model = getCurrentTreeModel();
        List<TreePath> paths = new ArrayList<TreePath>();
        List<FVResourceNode> treeNodes = new ArrayList<FVResourceNode>();
        for (GraphNode node : nodes) {
            if (indicator.isCancelled()) {
                return null;
            }
            int lid = node.getLid();
            TreePath path = null;
            path = model.getTreePath(lid,
                    node.isEndNode() ? TreeNodeType.HFI : TreeNodeType.SWITCH,
                    getSearchHint());
            if (path == null) {
                log.warn("Couldn't find tree node for node Lid=" + lid);
            } else {
                paths.add(path);
                treeNodes.add((FVResourceNode) path.getLastPathComponent());
            }
        }
        if (!indicator.isCancelled() && !paths.isEmpty()) {
            lastTreeSelection = treeNodes.toArray(new FVResourceNode[0]);
            view.setTreeSelection(model, paths.toArray(new TreePath[0]));
        }
        return lastTreeSelection;
    }

    protected FVResourceNode getSearchHint() {
        if (lastTreeSelection == null || lastTreeSelection.length == 0) {
            return null;
        }

        FVResourceNode hint = lastTreeSelection[0];
        switch (hint.getType()) {
            case SWITCH:
            case HFI:
                hint = hint.getParent();
                break;

            case ACTIVE_PORT:
            case INACTIVE_PORT:
                hint = hint.getParent().getParent();
                break;

            default:
                break;
        }
        return hint;
    }

    protected FVResourceNode[] selectTreePorts(List<GraphEdge> edges,
            ICancelIndicator indicator) {
        FVTreeModel model = getCurrentTreeModel();
        List<TreePath> paths = new ArrayList<TreePath>();
        for (GraphEdge edge : edges) {
            if (indicator.isCancelled()) {
                return null;
            }
            int lid = edge.getFromLid();
            Collection<Integer> ports = edge.getLinks().keySet();
            populatePaths(lid, ports, paths);

            lid = edge.getToLid();
            ports = edge.getLinks().values();
            populatePaths(lid, ports, paths);
        }

        if (indicator.isCancelled()) {
            return null;
        }
        // by default we will scroll tree to ensure the first path visible.
        // to avoid escaping from the current selected tree node, we put the
        // first matched selection on the top of tree path array. The following
        // code is inefficient. But given we have no a lot selections, it
        // should be fine. Otherwise we need to consider use hash
        int firstPath = -1;
        if (lastTreeSelection != null && paths.size() > 1) {
            for (FVResourceNode node : lastTreeSelection) {
                for (int i = 0; i < paths.size(); i++) {
                    if (paths.get(i).getLastPathComponent() == node) {
                        firstPath = i;
                        break;
                    }
                }
            }
        }
        if (firstPath >= 0) {
            TreePath path = paths.remove(firstPath);
            paths.add(0, path);
        }

        if (!paths.isEmpty()) {
            List<FVResourceNode> treeNodes = new ArrayList<FVResourceNode>();
            TreePath[] pathArray = new TreePath[paths.size()];
            for (int i = 0; i < paths.size(); i++) {
                pathArray[i] = paths.get(i);
                treeNodes.add(
                        (FVResourceNode) pathArray[i].getLastPathComponent());
            }
            lastTreeSelection = treeNodes.toArray(new FVResourceNode[0]);
            view.setTreeSelection(model, pathArray);
        }
        return lastTreeSelection;
    }

    protected void populatePaths(int lid, Collection<Integer> ports,
            List<TreePath> paths) {
        FVTreeModel model = getCurrentTreeModel();
        TreePath path =
                model.getTreePath(lid, TreeNodeType.NODE, getSearchHint());
        if (path == null) {
            log.warn("Couldn't find tree node for node Lid=" + lid);
        } else {
            FVResourceNode node = (FVResourceNode) path.getLastPathComponent();
            if (node.getType() == TreeNodeType.HFI) {
                paths.add(path.pathByAddingChild(node.getChildAt(0)));
            } else {
                for (int port : ports) {
                    paths.add(path.pathByAddingChild(node.getChildAt(port)));
                }
            }
        }
    }

    @Override
    public String getName() {
        return JumpDestination.TOPOLOGY.getName();
    }

    public void cleanup() {
        graphSelectionController.cleanup();
    }

    /**
     * <i>Description:</i>
     *
     * @param treeModel
     * @param paths
     * @param expanded
     * @param subpageName
     */
    public void showNode(FVTreeModel treeModel, TreePath[] paths,
            boolean[] expanded, String subpageName) {
        // isSystemUpdate = true;
        currentSubpageName = subpageName;
        showNode(treeModel, paths, expanded);
    }
}
