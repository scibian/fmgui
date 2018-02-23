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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import com.intel.stl.ui.common.FinishObserver;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.configuration.DevicePropertiesController;
import com.intel.stl.ui.configuration.view.DevicePropertiesPanel;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoableSelection;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.DeviceProperties;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeManager;
import com.intel.stl.ui.monitor.tree.FVTreeModel;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;
import com.intel.stl.ui.monitor.view.PerformanceSubpageView;
import com.intel.stl.ui.monitor.view.PerformanceTreeView;
import com.intel.stl.ui.monitor.view.SummarySubpageView;

import net.engio.mbassy.bus.MBassador;

public class PerformanceTreeController
        extends TreeController<PerformanceTreeView> implements IPageListener {

    /**
     * Subpages for the performance page
     */
    private List<IPerfSubpageController> mSubpages;

    private String previousSubpageName;

    /**
     * name of the currently selected subpage
     */
    private String currentSubpageName;

    private IPerfSubpageController currentSubpage;

    /**
     * Map of subpages
     */
    private EnumMap<TreeNodeType, List<IPerfSubpageController>> pageMap;

    private FVResourceNode lastNode;

    public PerformanceTreeController(PerformanceTreeView pTreeView,
            MBassador<IAppEvent> eventBus, FVTreeManager treeBuilder) {
        super(pTreeView, eventBus, treeBuilder);
        view.setPageListener(this);
        view.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        initSubpages();

        new SearchController(view.getSearchView(), eventBus, treeBuilder, this);
    }

    @Override
    protected synchronized void showNode(FVResourceNode node) {
        if (node == null) {
            return;
        }

        view.setNodeName(node);
        setRunning(true);

        if (lastNode != null) {
            if (lastNode.hasSamePath(node)
                    && lastNode.getRoot() == node.getRoot()) {
                // refresh the subpage
                currentSubpage.showNode(node, new FinishObserver() {
                    @Override
                    public void onFinish() {
                        setRunning(false);
                    }
                });
                return;
            }
            // collapse last node
            collapseTreeSelection(lastNode, node);
        }

        // Deregister tasks on all subpages
        for (IPerfSubpageController page : mSubpages) {
            page.clear();
        }

        List<IPerfSubpageController> subpages =
                getSubpagesByType(node.getType());
        if (subpages == null) {
            view.clearPage(node.getType());
            setRunning(false);
            lastNode = null;
        } else {
            previousSubpageName = currentSubpageName;
            int curIndex = -1;
            IPerfSubpageController subpage = null;
            if (currentSubpageName != null) {
                for (int i = 0; i < subpages.size(); i++) {
                    subpage = subpages.get(i);
                    if (subpage.getName().equals(currentSubpageName)) {
                        curIndex = i;
                        break;
                    }
                }
            }
            if (curIndex == -1) {
                curIndex = 0;
                subpage = subpages.get(0);
                currentSubpageName = subpage.getName();
            }
            subpage.showNode(node, new FinishObserver() {

                @Override
                public void onFinish() {
                    setRunning(false);
                }

            });
            lastNode = node;
            currentSubpage = subpage;
            view.setTabs(subpages, curIndex);
        }
    }

    protected void collapseTreeSelection(FVResourceNode node,
            FVResourceNode current) {
        TreePath toCollapse = null;
        switch (node.getType()) {
            case SWITCH:
            case HFI:
            case ACTIVE_PORT:
            case INACTIVE_PORT:
                if (node.getParent() != null) {
                    toCollapse = node.getParent().getPath();
                }
                break;

            default:
                break;
        }
        FVResourceNode parent = current.getParent();
        switch (current.getType()) {
            case SWITCH:
            case HFI:
                if (parent != null && parent.getPath().equals(toCollapse)) {
                    toCollapse = null;
                }
                break;

            case ACTIVE_PORT:
            case INACTIVE_PORT:
                if (parent != null) {
                    if (parent.getPath().equals(toCollapse)) {
                        toCollapse = null;
                    }
                    parent = parent.getParent();
                    if (parent != null && parent.getPath().equals(toCollapse)) {
                        toCollapse = null;
                    }
                }
                break;

            default:
                break;
        }
        if (toCollapse != null) {
            view.collapseTreePath(getCurrentTreeModel(), toCollapse);
            view.ensureSelectionVisible(getCurrentTreeModel());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.TreeController#showNodes(com.intel.stl.ui.
     * monitor .FVResourceNode[])
     */
    @Override
    protected void showNodes(FVResourceNode[] node) {
        throw new UnsupportedOperationException();
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
        // the new currentSubpageName can be unknown because the real update can
        // happen after some long time task running on background. Subpage name
        // is only useful for undo. The redo is fine without subpage name. So
        // instead of waiting for update to be finished, we immediately return
        // undoableSelection by using null as the new currentSubpageName
        TreeSubpageSelection oldTSSelection =
                new TreeSubpageSelection(oldSelection, previousSubpageName);
        TreeSubpageSelection newTSSelection =
                new TreeSubpageSelection(newSelection, null);
        return new UndoablePerfTreeSelection(this, oldTSSelection,
                newTSSelection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.TreeController#getCurrentNode()
     */
    @Override
    protected FVResourceNode getCurrentNode() {
        return lastNode;
    }

    protected void initSubpages() {
        // Create thew views
        PerformanceSubpageView perfSubpageView = new PerformanceSubpageView();
        SummarySubpageView summarySubpageView = new SummarySubpageView();
        ConnectivityTableModel connectTableModel = new ConnectivityTableModel();
        ConnectivitySubpageView connectSubpageView =
                new ConnectivitySubpageView(connectTableModel);
        DevicePropertiesPanel propSubpageView = new DevicePropertiesPanel();

        // Create subpages
        IPerfSubpageController summarySP =
                new PSSubpageController(summarySubpageView, eventBus);
        summarySP.setParentController(this);
        IPerfSubpageController performanceSP =
                new PerformanceSubpageController(perfSubpageView, eventBus);
        performanceSP.setParentController(this);
        IPerfSubpageController connectSP = new ConnectivitySubpageController(
                connectTableModel, connectSubpageView, eventBus);
        connectSP.setParentController(this);
        DeviceProperties props = new DeviceProperties();
        IPerfSubpageController propertySP = new DevicePropertiesController(
                props, propSubpageView, eventBus);
        propertySP.setParentController(this);
        mSubpages =
                Arrays.asList(summarySP, performanceSP, connectSP, propertySP);

        // Init TreeNodeType and associated sub-pages
        pageMap = new EnumMap<TreeNodeType, List<IPerfSubpageController>>(
                TreeNodeType.class);
        pageMap.put(TreeNodeType.ALL, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.INACTIVE_PORT, null);
        pageMap.put(TreeNodeType.DEVICE_GROUP, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.HCA_GROUP, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.ROUTER_GROUP, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.SWITCH_GROUP, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.VIRTUAL_FABRIC, Arrays.asList(summarySP));
        pageMap.put(TreeNodeType.HFI,
                Arrays.asList(performanceSP, connectSP, propertySP));
        pageMap.put(TreeNodeType.SWITCH,
                Arrays.asList(performanceSP, connectSP, propertySP));
        pageMap.put(TreeNodeType.ACTIVE_PORT,
                Arrays.asList(performanceSP, connectSP, propertySP));

        // for demo purpose
        // pageMap.put(TreeNodeType.HCA, Arrays.asList(performanceSP,
        // propertySP));
        // pageMap.put(TreeNodeType.SWITCH, Arrays.asList(performanceSP,
        // propertySP));
        // pageMap.put(TreeNodeType.PORT, Arrays.asList(performanceSP,
        // propertySP));
    }

    @Override
    public void setContext(Context pContext, IProgressObserver observer) {
        IProgressObserver[] subObservers =
                observer.createSubObservers(mSubpages.size() + 1);
        for (int i = 0; i < mSubpages.size(); i++) {
            // subObservers[i].setNote(mSubpages.get(i).getName());
            mSubpages.get(i).setContext(pContext, subObservers[i]);
            subObservers[i].onFinish();
            // observer.setProgress((i+1.0)/(mSubpages.size()+1));
            if (observer.isCancelled()) {
                for (int j = 0; j <= i; j++) {
                    mSubpages.get(j).clear();
                }
                return;
            }
        }
        // subObservers[mSubpages.size()].setNote("TREE");
        super.setContext(pContext, subObservers[mSubpages.size()]);
        // clean subpages if we cancel after they are initialized
        if (observer.isCancelled()) {
            for (int i = 0; i < mSubpages.size(); i++) {
                for (int j = 0; j <= i; j++) {
                    mSubpages.get(j).clear();
                }
            }
        }
    }

    public void setRunning(boolean isRunning) {
        view.setRunning(isRunning);
    }

    protected List<IPerfSubpageController> getSubpagesByType(
            TreeNodeType type) {
        return pageMap.get(type);
    }

    @Override
    public boolean canPageChange(String oldPage, String newPage) {
        return true;
    }

    @Override
    public synchronized void onPageChanged(String oldPageId, String newPageId) {
        List<IPerfSubpageController> subpages =
                getSubpagesByType(lastNode.getType());
        if (subpages == null) {
            // shouldn't happen
            throw new RuntimeException(
                    "No pages found for last node " + lastNode);
        }

        IPerfSubpageController oldPage = null;
        for (IPerfSubpageController page : subpages) {
            if (page.getName().equals(oldPageId)) {
                oldPage = page;
                oldPage.onExit();
                break;
            }
        }

        IPerfSubpageController newPage = null;
        for (IPerfSubpageController page : subpages) {
            if (page.getName().equals(newPageId)) {
                newPage = page;
                break;
            }
        }

        if (newPage != null && !currentSubpageName.equals(newPage.getName())) {
            setRunning(true);
            newPage.onEnter();
            newPage.showNode(lastNode, new FinishObserver() {
                @Override
                public void onFinish() {
                    setRunning(false);
                }
            });
            currentSubpageName = newPage.getName();
            currentSubpage = newPage;
        }

        if (undoHandler != null && !undoHandler.isInProgress()) {
            UndoableSubpageSelection undoSel =
                    new UndoableSubpageSelection(view, oldPageId, newPageId);
            undoHandler.addUndoAction(undoSel);
        }
    }

    @Override
    public String getName() {
        return JumpDestination.PERFORMANCE.getName();
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
        currentSubpageName = subpageName;
        showNode(treeModel, paths, expanded);
    }

}
