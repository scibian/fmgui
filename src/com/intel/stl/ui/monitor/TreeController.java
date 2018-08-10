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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.event.GroupsSelectedEvent;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.NodeUpdateEvent;
import com.intel.stl.ui.event.NodesSelectedEvent;
import com.intel.stl.ui.event.PortUpdateEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.UndoableSelection;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeManager;
import com.intel.stl.ui.monitor.tree.FVTreeModel;
import com.intel.stl.ui.monitor.tree.InactivePortVizIndicator;
import com.intel.stl.ui.monitor.view.TreeViewInterface;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.listener.Handler;

/**
 */
public abstract class TreeController<E extends TreeViewInterface>
        implements TreeControllerInterface, TreeSelectionListener {

    /**
     * Tree View
     */
    protected E view;

    /**
     * Tree models
     */
    private final Map<TreeTypeEnum, FVTreeModel> treeModels;

    protected FVTreeModel currentTreeModel;

    /**
     * API Context
     */
    protected Context mContext;

    /**
     * Tree builder creates hierarchical trees of various types
     */
    protected FVTreeManager mTreeBuilder;

    protected final MBassador<IAppEvent> eventBus;

    protected UndoHandler undoHandler;

    /**
     * System update, such as initialization, refresh, notice response etc.,
     * will trigger tree selection changes. This attribute tracks when system is
     * updating, so we know when we should ignore tree selection on undo track
     */
    protected boolean isSystemUpdate;

    private TreeSelection currentSelection;

    private final InactivePortVizIndicator treeIndicator =
            new InactivePortVizIndicator();

    public TreeController(E pTreeView, MBassador<IAppEvent> eventBus,
            FVTreeManager treeBuilder) {
        view = pTreeView;
        view.addTreeSelectionListener(this);
        mTreeBuilder = treeBuilder;
        this.eventBus = eventBus;
        treeModels = new HashMap<TreeTypeEnum, FVTreeModel>();
        eventBus.subscribe(this);
    } // TreeController

    /**
     * @return the view
     */
    public E getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.trees.TreeControllerInterface#setContext(com.intel
     * .hpc.stl.ui.Context)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        mContext = context;
        treeIndicator.setContext(context);
        isSystemUpdate = true;
        buildTrees(observer);
        view.clear();

        if (context != null && context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }
    } // setContext

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.TreeControllerInterface#onRefresh(com.intel.
     * stl.ui.common.IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        isSystemUpdate = true;
        updateTrees(observer);
        // By reselecting current selection, all subpage updates for current
        // selected node will be done.
        // Note: Running icon is running so no observer needed.
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                TreeTypeEnum currentTreeType =
                        getTreeType(getCurrentTreeModel());
                view.setTreeSelection(currentTreeType);
            }
        });
    }

    @Override
    public void onNodeUpdate(NodeUpdateEvent evt) {
        // ignore portUpdateEvent
        if (evt instanceof PortUpdateEvent) {
            return;
        }

        isSystemUpdate = true;
        // Not in SwingWorker, let main thread handle this.
        int[] lids = evt.getNodeLids();
        for (int lid : lids) {
            updateTreeNode(lid);
        }

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                TreeTypeEnum currentTreeType =
                        getTreeType(getCurrentTreeModel());
                view.setTreeSelection(currentTreeType);
            }
        });
    }

    public void buildTrees(IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(4);
        buildTree(TreeTypeEnum.DEVICE_TYPES_TREE, subObservers[0]);
        subObservers[0].onFinish();
        buildTree(TreeTypeEnum.DEVICE_GROUPS_TREE, subObservers[1]);
        subObservers[1].onFinish();
        buildTree(TreeTypeEnum.VIRTUAL_FABRICS_TREE, subObservers[2]);
        subObservers[2].onFinish();
        // buildTree(TreeTypeEnum.TOP_10_CONGESTED_TREE, subObservers[3]);
        subObservers[3].onFinish();
    }

    protected void buildTree(TreeTypeEnum type, IProgressObserver observer) {
        // Build the trees and set the corresponding models in the tree view
        FVTreeModel oldModel = treeModels.get(type);
        if (oldModel != null) {
            mTreeBuilder.removeMonitor(type, oldModel);
        }
        FVResourceNode tree = mTreeBuilder.buildTree(type, observer);
        if (observer.isCancelled()) {
            return;
        }
        FVTreeModel treeModel = new FVTreeModel(tree);
        treeModel.filter(treeIndicator);
        view.setTreeModel(type, treeModel);
        treeModels.put(type, treeModel);
        mTreeBuilder.addMonitor(type, treeModel);
    }

    public void updateTrees(IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(4);
        updateTree(TreeTypeEnum.DEVICE_TYPES_TREE, subObservers[0]);
        updateTree(TreeTypeEnum.DEVICE_GROUPS_TREE, subObservers[1]);
        updateTree(TreeTypeEnum.VIRTUAL_FABRICS_TREE, subObservers[2]);
        // mTreeBuilder.updateTree(TreeTypeEnum.TOP_10_CONGESTED_TREE,
        // subObservers[3]);
        subObservers[3].onFinish();
    }

    protected void updateTree(TreeTypeEnum type, IProgressObserver observer) {
        FVTreeModel treeModel = treeModels.get(type);
        if (treeModel != null) {
            mTreeBuilder.updateTree(type, observer);
            treeModel.filter(treeIndicator);
            view.setTreeModel(type, treeModel);
        }
    }

    public void updateTreeNode(int lid) {
        updateTreeNode(lid, TreeTypeEnum.DEVICE_TYPES_TREE);
        updateTreeNode(lid, TreeTypeEnum.DEVICE_GROUPS_TREE);
        updateTreeNode(lid, TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        // updateTreeNode(lid, TreeTypeEnum.TOP_10_CONGESTED_TREE);
    }

    protected void updateTreeNode(int lid, TreeTypeEnum type) {
        FVTreeModel treeModel = treeModels.get(type);
        if (treeModel != null) {
            mTreeBuilder.updateTreeNode(lid, type);
            treeModel.filter(treeIndicator);
            view.setTreeModel(type, treeModel);
        }
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        if (tree.getSelectionCount() == 0) {
            return;
        }

        TreeSelection oldSelection = currentSelection;
        currentTreeModel = (FVTreeModel) tree.getModel();

        if (tree.getSelectionCount() == 1) {
            Object node = tree.getLastSelectedPathComponent();
            if (node != null && (node instanceof FVResourceNode)) {
                currentSelection = new TreeSelection(currentTreeModel);
                currentSelection.addNode((FVResourceNode) node,
                        tree.isExpanded(tree.getSelectionPath()));
                showNode((FVResourceNode) node);
            }
        } else {
            TreePath[] paths = tree.getSelectionPaths();
            FVResourceNode[] nodes = new FVResourceNode[paths.length];
            currentSelection = new TreeSelection(currentTreeModel);
            for (int i = 0; i < nodes.length; i++) {
                Object node = paths[i].getLastPathComponent();
                if (node != null && (node instanceof FVResourceNode)) {
                    nodes[i] = (FVResourceNode) node;
                    currentSelection.addNode(nodes[i],
                            tree.isExpanded(paths[i]));
                }
            }
            showNodes(nodes);
        }

        // when we refresh or respond to a notice, StackPanel will remove
        // selections first and then add them back. This will trigger two
        // valueChanged calls. Checking whether currentSelection is null or not
        // allows us ignore the case of removing all selections, i.e.
        // currentSelection is null
        if (!isSystemUpdate && undoHandler != null
                && !undoHandler.isInProgress()) {
            UndoableSelection<?> undoSel =
                    getUndoableSelection(oldSelection, currentSelection);
            undoHandler.addUndoAction(undoSel);
        }
        if (isSystemUpdate) {
            isSystemUpdate = false;
        }
    }

    protected abstract UndoableSelection<?> getUndoableSelection(
            TreeSelection oldSelection, TreeSelection newSelection);

    public void showNode(TreeTypeEnum type, FVResourceNode node) {
        showNode(treeModels.get(type), node, false);
    }

    public synchronized void showNode(FVTreeModel treeModel,
            FVResourceNode node, boolean isExpanded) {
        showNode(treeModel, new TreePath[] { node.getPath() },
                new boolean[] { isExpanded });
    }

    public synchronized void showNode(FVTreeModel treeModel, TreePath[] paths,
            boolean[] isExpanded) {
        currentTreeModel = treeModel;
        view.expandAndSelectTreePath(currentTreeModel, paths, isExpanded);
    }

    /**
     * @return the currentTreeModel
     */
    public FVTreeModel getCurrentTreeModel() {
        if (currentTreeModel == null) {
            // this shouldn't happen
            currentTreeModel = treeModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        }
        return currentTreeModel;
    }

    public FVTreeModel getTreeModel(TreeTypeEnum type) {
        return treeModels.get(type);
    }

    public TreeTypeEnum getTreeType(FVTreeModel model) {
        if (model == null) {
            return TreeTypeEnum.DEVICE_TYPES_TREE;
        }

        for (TreeTypeEnum type : treeModels.keySet()) {
            if (treeModels.get(type) == model) {
                return type;
            }
        }
        throw new RuntimeException("Unknown FVTreeModel " + model);
    }

    /**
     *
     * Description: Derived class will show specified node info
     *
     * @param node
     *            - node to display
     */
    protected abstract void showNode(FVResourceNode node);

    protected abstract void showNodes(FVResourceNode[] nodes);

    protected abstract FVResourceNode getCurrentNode();

    @Handler
    protected void onGroupSelected(GroupsSelectedEvent event) {
        if (!acceptEvent(event)) {
            return;
        }

        FVTreeModel deviceTypesTreeModel = getCurrentTreeModel();
        if (deviceTypesTreeModel == null) {
            return;
        }

        TreePath[] paths = new TreePath[event.getNumGroups()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = deviceTypesTreeModel.getTreePath(event.getName(i),
                    event.getType(i));
        }
        isSystemUpdate = true;
        view.expandAndSelectTreePath(deviceTypesTreeModel, paths,
                new boolean[paths.length]);
    }

    @Handler
    protected void onNodesSelected(NodesSelectedEvent event) {
        if (!acceptEvent(event)) {
            return;
        }

        FVTreeModel deviceTypesTreeModel = getCurrentTreeModel();
        if (deviceTypesTreeModel == null) {
            return;
        }

        List<TreePath> paths = new ArrayList<TreePath>();
        for (int i = 0; i < event.numberOfNodes(); i++) {
            NodeType nodeType = event.getType(i);
            TreeNodeType treeNodeType = null;
            boolean nodeTypeValid = true;
            switch (nodeType) {
                case HFI: {
                    treeNodeType = TreeNodeType.HFI;
                    break;
                }
                case SWITCH: {
                    treeNodeType = TreeNodeType.SWITCH;
                    break;
                }
                case ROUTER: {
                    treeNodeType = TreeNodeType.ROUTER;
                    break;
                }
                default:
                    nodeTypeValid = false;
                    break;
            }
            if (nodeTypeValid) {
                TreePath path = deviceTypesTreeModel.getTreePath(
                        event.getLid(i), treeNodeType, getCurrentNode());
                paths.add(path);
            }
        }
        if (!paths.isEmpty()) {
            isSystemUpdate = true;
            view.expandAndSelectTreePath(deviceTypesTreeModel,
                    paths.toArray(new TreePath[0]), new boolean[paths.size()]);
        }
    }

    @Handler
    protected void onPortsSelected(PortsSelectedEvent event) {
        if (!acceptEvent(event)) {
            return;
        }

        FVTreeModel deviceTypesTreeModel = getCurrentTreeModel();
        if (deviceTypesTreeModel == null) {
            return;
        }

        TreePath[] paths = new TreePath[event.numberOfPorts()];
        for (int i = 0; i < paths.length; i++) {
            paths[i] = deviceTypesTreeModel.getTreePathForPort(event.getLid(i),
                    event.getPortNum(i), getCurrentNode());
        }

        isSystemUpdate = true;
        view.expandAndSelectTreePath(deviceTypesTreeModel, paths,
                new boolean[paths.length]);
    }

    public abstract String getName();

    protected boolean acceptEvent(JumpToEvent event) {
        return event.getDestination().equals(getName());
    }

}
