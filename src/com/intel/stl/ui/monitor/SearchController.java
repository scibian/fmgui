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

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.EnumMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;

import javax.swing.JTree;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeSelectionModel;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.TimeDrivenProgressObserver;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeManager;
import com.intel.stl.ui.monitor.tree.FVTreeManager.TreeManagementModel;
import com.intel.stl.ui.monitor.tree.FVTreeModel;
import com.intel.stl.ui.monitor.tree.SearchResult;
import com.intel.stl.ui.monitor.tree.SearchResultNode;
import com.intel.stl.ui.monitor.view.SearchView;
import com.intel.stl.ui.publisher.CancellableCall;

public class SearchController implements TreeSelectionListener,
        ISearchListener, PropertyChangeListener {

    private static final Logger log = LoggerFactory
            .getLogger(SearchController.class);

    private final SearchView searchView;

    private final FVTreeManager treeSearcher;

    private TreeSearchType searchType;

    private String searchValue;

    private final TreeController<?> parentTreeController;

    private EnumMap<TreeTypeEnum, SearchResult> resultTrees;

    private TreeTypeEnum treeType;

    private int treeNodeCount;

    private int totalNodeCount;

    private FVTreeModel treeModel;

    private final EnumMap<TreeTypeEnum, SearchResult> searchResultsMap;

    private SwingWorker<EnumMap<TreeTypeEnum, SearchResult>, SearchResult> worker;

    private PropertyChangeSupport support;

    private TimeDrivenProgressObserver observer;

    public static final String PROGRESS_AMOUNT_PROPERTY = "ProgressAmount";

    public static final String PROGRESS_NOTE_PROPERTY = "ProgressNote";

    private double workDone;

    private ICancelIndicator cancelIndicator;

    public SearchController(SearchView pTreeView,
            MBassador<IAppEvent> eventBus, FVTreeManager treeBuilder,
            TreeController<?> parentTreeController) {
        treeSearcher = treeBuilder;
        searchView = pTreeView;
        searchView.addSearchListener(this);
        searchView.addTreeSelectionListener(this);
        searchView.setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        this.parentTreeController = parentTreeController;

        searchResultsMap =
                new EnumMap<TreeTypeEnum, SearchResult>(TreeTypeEnum.class);
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        JTree tree = (JTree) e.getSource();
        if (tree.getSelectionCount() == 1) {
            Object node = tree.getLastSelectedPathComponent();
            if (node != null && (node instanceof SearchResultNode)) {
                SearchResultNode resultNode = (SearchResultNode) node;
                if (resultNode.isNode()) {

                    TreeTypeEnum treeType = findTreeType(resultNode);
                    FVResourceNode matchingNode = null;
                    if (resultTrees != null && resultNode != null) {
                        SearchResult searchResult = resultTrees.get(treeType);
                        if (searchResult != null) {
                            matchingNode =
                                    searchResult.getNodeMap().get(resultNode);
                        }
                    }
                    if (matchingNode != null) {
                        parentTreeController.showNode(treeType, matchingNode);
                    }
                }
            }
        }
    }

    private TreeTypeEnum findTreeType(FVResourceNode node) {
        TreeTypeEnum res = null;
        while (node != null && (res = getTreeType(node)) == null) {
            node = node.getParent();
        }
        return res;
    }

    private TreeTypeEnum getTreeType(FVResourceNode node) {
        switch (node.getType()) {
            case DEVICE_GROUP:
                return TreeTypeEnum.DEVICE_GROUPS_TREE;
            case VIRTUAL_FABRIC:
                return TreeTypeEnum.VIRTUAL_FABRICS_TREE;
            case HCA_GROUP:
            case SWITCH_GROUP:
            case ROUTER_GROUP:
                return TreeTypeEnum.DEVICE_TYPES_TREE;
            default:
                break;
        }
        return null;
    }

    @Override
    public void setSearchType(TreeSearchType type) {
        searchType = type;

    }

    @Override
    public void setSearchValue(String value) {
        searchValue = value;

    }

    /**
     * 
     */
    @Override
    public void searchTree() {
        resultTrees = null;
        workDone = 0.0;
        totalNodeCount = 0;

        synchronized (SearchController.this) {
            if (worker != null && !worker.isDone()) {
                worker.removePropertyChangeListener(this);
                worker.cancel(true);
            }
        }

        final CancellableCall<SearchResult> caller =
                new CancellableCall<SearchResult>() {
                    @Override
                    public SearchResult call(ICancelIndicator cancelIndicator)
                            throws Exception {
                        SearchResult result =
                                treeSearcher.searchTreeNode(treeType,
                                        searchType, searchValue, observer,
                                        cancelIndicator);
                        return result;
                    }
                };

        worker =
                new SwingWorker<EnumMap<TreeTypeEnum, SearchResult>, SearchResult>() {

                    @Override
                    protected EnumMap<TreeTypeEnum, SearchResult> doInBackground()
                            throws Exception {

                        if (isCancelled()) {
                            log.info("Cancelled search tree ");
                            return null;
                        }

                        for (TreeManagementModel model : treeSearcher
                                .getMgrModels().values()) {
                            if (model.getTree() != null) {
                                totalNodeCount += getNodeCount(model.getTree());
                            }
                        }

                        if (totalNodeCount > 0) {
                            observer =
                                    new TimeDrivenProgressObserver(
                                            this.getPropertyChangeSupport(),
                                            totalNodeCount);

                            searchResultsMap.clear();
                            for (TreeTypeEnum type : TreeTypeEnum.values()) {
                                treeType = type;
                                SearchResult result = caller.call();
                                if (result != null) {
                                    searchResultsMap.put(treeType, result);
                                    publish(result);
                                }
                            }
                        }
                        return searchResultsMap;
                    }

                    @Override
                    protected void done() {
                        try {
                            resultTrees = get();
                            if (resultTrees == null) {
                                if (caller.getCancelIndicator().isCancelled()) {
                                    searchView
                                            .showMessage(UILabels.STL50203_SEARCH_CANCELLED
                                                    .getDescription());
                                } else {
                                    searchView
                                            .showMessage(UILabels.STL50204_SEARCH_NULL
                                                    .getDescription());
                                }
                            } else if (resultTrees.isEmpty()) {
                                searchView
                                        .showMessage(UILabels.STL50205_SEARCH_EMPTY
                                                .getDescription());
                            } else {
                                buildTree(resultTrees);
                                searchView.showTree(true);
                            }
                        } catch (InterruptedException e) {
                        } catch (CancellationException e) {
                            searchView
                                    .showMessage(UILabels.STL50203_SEARCH_CANCELLED
                                            .getDescription());
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } finally {
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                            }
                            searchView.enableSearch();
                        }
                    }

                    @Override
                    protected void process(List<SearchResult> results) {
                        for (SearchResult result : results) {
                            observer.publishNote(result.getTreeType().getName()
                                    + " " + result.getResultTree().getNumHits());
                        }
                    }
                };
        cancelIndicator = caller.getCancelIndicator();
        caller.setCancelIndicator(new ICancelIndicator() {
            @Override
            public boolean isCancelled() {
                return worker.isCancelled()
                        || (cancelIndicator != null && cancelIndicator
                                .isCancelled());
            }
        });
        worker.addPropertyChangeListener(this);
        this.support = worker.getPropertyChangeSupport();

        searchView.setRunning(true);
        worker.execute();
    }

    public int getNodeCount(FVResourceNode root) {

        if (root.isNode()) {
            treeNodeCount = 1;
        } else {
            treeNodeCount = 0;
            for (FVResourceNode child : root.getChildren()) {
                treeNodeCount += getNodeCount(child);
            }
        }

        return treeNodeCount;
    }

    protected void buildTree(EnumMap<TreeTypeEnum, SearchResult> resultTrees) {
        for (Entry<TreeTypeEnum, SearchResult> entry : resultTrees.entrySet()) {
            SearchResultNode root = entry.getValue().getResultTree();
            if (root != null) {
                treeModel = new FVTreeModel(root);
                searchView.setTreeModel(entry.getKey(), treeModel,
                        root.getNumHits());
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROGRESS_AMOUNT_PROPERTY == evt.getPropertyName()) {
            double progress = (Double) evt.getNewValue();
            workDone = workDone + progress;
            double percentProgress = (workDone / totalNodeCount) * 100;
            if (percentProgress > 100) {
                percentProgress = 100.00;
            }

            if (searchView != null) {
                searchView.setProgress((int) percentProgress);
            }
        } else if (PROGRESS_NOTE_PROPERTY == evt.getPropertyName()) {
            String note = (String) evt.getNewValue();
            if (searchView != null) {
                searchView.setProgressNote(note);
            }
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.ISearchListener#cancel()
     */
    @Override
    public void cancel() {
        worker.cancel(true);
    }
}
