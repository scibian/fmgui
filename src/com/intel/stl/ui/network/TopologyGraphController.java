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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.GraphCells;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.model.LayoutType;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.TreeLayout.Style;
import com.intel.stl.ui.network.task.LayoutTask;
import com.intel.stl.ui.network.task.RefreshGraphTask;
import com.intel.stl.ui.network.task.ShowAllTask;
import com.intel.stl.ui.network.task.ShowEdgesTask;
import com.intel.stl.ui.network.task.ShowGroupTask;
import com.intel.stl.ui.network.task.ShowNodeTask;
import com.intel.stl.ui.network.task.ShowRoutesTask;
import com.intel.stl.ui.network.view.TopologyGraphView;
import com.intel.stl.ui.network.view.TopologyGuideView;
import com.intel.stl.ui.network.view.TopologyView;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.CancellableCall;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.SingleTaskManager;

import net.engio.mbassy.bus.MBassador;

public class TopologyGraphController implements ITopologyListener {
    private static final Logger log =
            LoggerFactory.getLogger(TopologyGraphController.class);

    private static final boolean DEBUG = true;

    private final TopologyTreeController parent;

    private final TopologyGraphView graphView;

    private final TopologyGuideView guideView;

    private ISubnetApi subnetApi;

    private final SingleTaskManager taskMgr;

    private TopologyTreeModel fullTopTreeModel;

    private TopologyTreeModel topTreeModel;

    private TopologyUpdateController updateCtrl;

    private final ResourceController resourceController;

    private final LayoutType[] availableLayouts;

    private final LayoutType defaultLayout = LayoutType.TREE_SLASH;

    private LayoutType currentLayout = defaultLayout;

    private GraphCells lastGraphSelection;

    private FVResourceNode[] lastResourceSelection;

    /**
     * Description:
     *
     * @param parent
     */
    public TopologyGraphController(TopologyTreeController parent,
            MBassador<IAppEvent> eventBus) {
        super();
        this.parent = parent;
        TopologyView topView = parent.getView();
        graphView = topView.getGraphView();
        graphView.setTopologyListener(this);
        availableLayouts = LayoutType.values();
        graphView.setAvailableLayouts(availableLayouts);

        guideView = topView.getGuideView();

        resourceController =
                new ResourceController(topView.getResourceView(), eventBus);
        taskMgr = new SingleTaskManager();
    }

    public void setContext(final Context pContext, IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(3);

        if (updateCtrl != null) {
            updateCtrl.cancel();
        }

        resourceController.setContext(pContext, subObservers[0]);
        subObservers[0].onFinish();

        subnetApi = pContext.getSubnetApi();
        List<NodeRecordBean> nodes = null;
        List<LinkRecordBean> links = null;
        try {
            nodes = subnetApi.getNodes(false);
            links = subnetApi.getLinks(false);
        } catch (Exception e) {
            e.printStackTrace();
            RuntimeException rte = new RuntimeException(
                    "Could not retrieve nodes and links.", e);
            throw rte;
        }
        if (nodes == null || links == null) {
            observer.onFinish();
            return;
        }

        subObservers[1].onFinish();
        if (observer.isCancelled()) {
            return;
        }

        final TopGraph fullGraph = TopGraph.createGraph();
        if (observer.isCancelled()) {
            return;
        }

        GraphBuilder builder = new GraphBuilder();
        fullTopTreeModel =
                topTreeModel = builder.build(fullGraph, nodes, links);
        if (topTreeModel == null || observer.isCancelled()) {
            return;
        }
        fullGraph.expandAll();
        TreeLayout layout =
                new TreeLayout(fullGraph, fullTopTreeModel, Style.SLASH);
        layout.execute(fullGraph.getDefaultParent());
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                TopGraph outlineGraph = TopGraph.createGraph();
                outlineGraph.setModel(fullGraph.getModel());
                guideView.setGraph(outlineGraph);
                graphView.setGraph(fullGraph);
            }
        });

        updateCtrl = new TopologyUpdateController(fullGraph, graphView);
        subObservers[2].onFinish();
        if (observer.isCancelled()) {
            return;
        }

    }

    public void onRefresh(IProgressObserver observer,
            final ICallback<Void> callback) {
        if (updateCtrl == null) {
            // this happens when #setContext exited because of null nodes or
            // links
            TopGraph fullGraph = TopGraph.createGraph();
            TopGraph outlineGraph = TopGraph.createGraph();
            guideView.setGraph(outlineGraph);

            graphView.setGraph(fullGraph);
            updateCtrl = new TopologyUpdateController(fullGraph, graphView);
        }
        RefreshGraphTask task = new RefreshGraphTask(this, null, null,
                defaultLayout, observer) {
            @Override
            public void onSuccess(ICancelIndicator indicator, TopGraph graph) {
                updateCtrl.setGraph(graph);
                super.onSuccess(indicator, graph);
                if (callback != null) {
                    callback.onDone(null);
                } else {
                    onSelectionChange(lastGraphSelection, null,
                            lastResourceSelection);
                }
            }
        };
        updateCtrl.update(task);
    }

    /**
     * @return the subnetApi
     */
    public ISubnetApi getSubnetApi() {
        return subnetApi;
    }

    /**
     * @return the graphView
     */
    public TopologyGraphView getGraphView() {
        return graphView;
    }

    /**
     * @return the guideView
     */
    public TopologyGuideView getGuideView() {
        return guideView;
    }

    /**
     * @return the resourceController
     */
    public ResourceController getResourceController() {
        return resourceController;
    }

    /**
     * @return the fullTopTreeModel
     */
    public TopologyTreeModel getFullTopTreeModel() {
        return fullTopTreeModel;
    }

    /**
     * @param fullTopTreeModel
     *            the fullTopTreeModel to set
     */
    public void setFullTopTreeModel(TopologyTreeModel fullTopTreeModel) {
        this.fullTopTreeModel = fullTopTreeModel;
    }

    /**
     * @return the topTreeModel
     */
    public TopologyTreeModel getTopTreeModel() {
        return topTreeModel;
    }

    /**
     * @param topTreeModel
     *            the topTreeModel to set
     */
    public void setTopTreeModel(TopologyTreeModel topTreeModel) {
        this.topTreeModel = topTreeModel;
    }

    /**
     * @return the currentLayout
     */
    public LayoutType getCurrentLayout() {
        return currentLayout;
    }

    /**
     * @param currentLayout
     *            the currentLayout to set
     */
    public void setCurrentLayout(LayoutType currentLayout) {
        this.currentLayout = currentLayout;
    }

    protected void processTreeGroups(final FVResourceNode[] groups) {
        CancellableCall<GraphCells> caller = new CancellableCall<GraphCells>() {
            @Override
            public GraphCells call(ICancelIndicator indicator)
                    throws Exception {
                GraphCells current = new GraphCells();
                for (FVResourceNode group : groups) {
                    for (FVResourceNode node : group.getChildren()) {
                        if (indicator.isCancelled()) {
                            return null;
                        }

                        GraphNode gNode = updateCtrl.getGraphNode(node.getId());
                        current.addNode(gNode);
                    }
                }
                return current;
            }
        };

        ICallback<GraphCells> callback = new CallbackAdapter<GraphCells>() {
            @Override
            public void onDone(GraphCells result) {
                onSelectionChange(result, TopologyGraphController.this, groups);
            }
        };

        taskMgr.submit(caller, callback);
    }

    protected void processTreeNodes(final FVResourceNode[] nodes) {
        CancellableCall<GraphCells> caller = new CancellableCall<GraphCells>() {
            @Override
            public GraphCells call(ICancelIndicator indicator)
                    throws Exception {
                GraphCells current = new GraphCells();
                for (FVResourceNode node : nodes) {
                    if (indicator.isCancelled()) {
                        return null;
                    }

                    GraphNode gNode = updateCtrl.getGraphNode(node.getId());
                    current.addNode(gNode);
                }
                return current;
            }
        };

        ICallback<GraphCells> callback = new CallbackAdapter<GraphCells>() {
            @Override
            public void onDone(GraphCells result) {
                onSelectionChange(result, TopologyGraphController.this, nodes);
            }
        };

        taskMgr.submit(caller, callback);
    }

    protected void processTreePorts(final FVResourceNode[] nodes) {
        CancellableCall<GraphCells> caller = new CancellableCall<GraphCells>() {
            @Override
            public GraphCells call(ICancelIndicator indicator)
                    throws Exception {
                GraphCells current = new GraphCells();

                // in a graph we only have one edge between two vertex, so we
                // use the following map to help us only add one edge
                Map<Point, GraphEdge> edges = new HashMap<Point, GraphEdge>();
                for (FVResourceNode node : nodes) {
                    if (indicator.isCancelled()) {
                        return null;
                    }

                    FVResourceNode parent = node.getParent();
                    int lid = parent.getId();
                    TreeNodeType type = parent.getType();
                    int portNum = node.getId();
                    GraphNode gNode = updateCtrl.getGraphNode(lid);
                    if (gNode != null) {
                        GraphNode toNode = gNode.getNeighbor(portNum);
                        if (toNode != null) {
                            Integer toPort =
                                    gNode.getLinkPorts(toNode).get(portNum);
                            TreeMap<Integer, Integer> linkPorts =
                                    new TreeMap<Integer, Integer>();
                            if (toPort != null) {
                                linkPorts.put(portNum, toPort);
                            }
                            GraphEdge tmp = new GraphEdge(lid,
                                    TreeNodeType.getNodeTypeCode(type),
                                    toNode.getLid(), toNode.getType(),
                                    linkPorts);
                            GraphEdge edge =
                                    edges.get(new Point(lid, toNode.getLid()));
                            if (edge == null) {
                                edge = edges
                                        .get(new Point(toNode.getLid(), lid));
                                if (edge != null) {
                                    // ensure its links are reversed as well
                                    tmp = tmp.normalize();
                                }
                            }
                            if (edge == null) {
                                current.addEdge(tmp);
                                edges.put(new Point(lid, toNode.getLid()), tmp);
                            } else {
                                edge.getLinks().putAll(tmp.getLinks());
                            }
                        } else {
                            log.warn("Couldn't find connection for Lid=" + lid
                                    + " PortNum=" + portNum);
                        }
                    } else {
                        log.warn("Couldn't find node with Lid=" + lid);
                    }
                }
                return current;
            }
        };

        ICallback<GraphCells> callback = new CallbackAdapter<GraphCells>() {
            @Override
            public void onDone(GraphCells result) {
                onSelectionChange(result, TopologyGraphController.this, nodes);
            }
        };

        taskMgr.submit(caller, callback);
    }

    protected void setLayout(final LayoutType type,
            final IModelChange preChange) {
        LayoutTask updateTask = new LayoutTask(this, type, preChange);
        updateCtrl.update(updateTask);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onUndo()
     */
    @Override
    public void onUndo() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                graphView.enableUndo(updateCtrl.undo());
                graphView.enableRedo(true);
                graphView.revalidate();
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onRedo()
     */
    @Override
    public void onRedo() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                graphView.enableUndo(true);
                graphView.enableRedo(updateCtrl.redo());
                graphView.revalidate();
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onReset()
     */
    @Override
    public void onReset() {
        // onCollapseAll();
        setLayout(currentLayout, null);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onLayoutTypeChange(int)
     */
    @Override
    public void onLayoutTypeChange(int typeIndex) {
        LayoutType layout = availableLayouts[typeIndex];
        if (currentLayout != layout) {
            setLayout(layout, null);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onExpandAll()
     */
    @Override
    public void onExpandAll() {
        if (currentLayout != null) {
            IModelChange expandAllchange = new IModelChange() {
                @Override
                public boolean execute(TopGraph graph,
                        ICancelIndicator indicator) {
                    graph.expandAll();
                    return true;
                }
            };
            setLayout(currentLayout, expandAllchange);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ITopologyListener#onCollapseAll()
     */
    @Override
    public void onCollapseAll() {
        if (currentLayout != null) {
            IModelChange collapseAllchange = new IModelChange() {
                @Override
                public boolean execute(TopGraph graph,
                        ICancelIndicator indicator) {
                    graph.collapseAll();
                    return true;
                }
            };
            setLayout(currentLayout, collapseAllchange);
        }
    }

    @Override
    public void onSelectionChange(final GraphCells current, final Object source,
            FVResourceNode[] selecedResources) {
        if (DEBUG) {
            System.out.println("Current " + Arrays.toString(selecedResources)
                    + " " + current + " " + Thread.currentThread());
            System.out.println("Last " + lastGraphSelection);
        }

        // we have no valid layouted model yet, reject everything
        if (currentLayout == null) {
            return;
        }
        lastGraphSelection = current;
        lastResourceSelection = selecedResources;
        // special case: selection from graph
        if (selecedResources == null) {
            List<FVResourceNode> resources = new ArrayList<FVResourceNode>();
            if (current.hasNodes()) {
                for (GraphNode node : current.getNodes()) {
                    FVResourceNode resource = new FVResourceNode(
                            Integer.toString(node.getLid()), node.isEndNode()
                                    ? TreeNodeType.HFI : TreeNodeType.SWITCH,
                            node.getLid());
                    resources.add(resource);
                }
            } else if (current.hasEdges()) {
                for (GraphEdge edge : current.getEdges()) {
                    Map<Integer, Integer> links = edge.getLinks();
                    int portNum = links.keySet().iterator().next();
                    FVResourceNode resource = new FVResourceNode(
                            edge.getFromLid() + ":" + portNum,
                            TreeNodeType.ACTIVE_PORT, portNum);
                    resources.add(resource);
                }
            }
            selecedResources = resources.toArray(new FVResourceNode[0]);
        }

        final FVResourceNode firstResource =
                (selecedResources == null || selecedResources.length == 0)
                        ? null : selecedResources[0];
        final FVResourceNode[] resourceSelection = selecedResources;

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                // the following #showXXX methods should use SwingWorker when it
                // involves connecting to backend to collect data
                List<GraphNode> tmpNodes = current.getNodes();
                List<GraphNode> nodes = new ArrayList<GraphNode>();
                if (tmpNodes != null) {
                    for (GraphNode tmpNode : tmpNodes) {
                        if (tmpNode != null) {
                            nodes.add(
                                    updateCtrl.getGraphNode(tmpNode.getLid()));
                        }
                    }
                }
                if (firstResource == null
                        || firstResource.getType() == TreeNodeType.ALL) {
                    onEmptySelection(source, resourceSelection);
                } else if (firstResource.isNode()) {
                    if (resourceSelection.length == 1) {
                        onSingleNode(nodes.get(0), source, resourceSelection);
                    } else {
                        onMultipleNodes(nodes, source, resourceSelection);
                    }
                } else if (firstResource.isPort()) {
                    if (firstResource.getId() > 0) {
                        List<GraphEdge> edges = current.getEdges();
                        onEdges(edges, source, resourceSelection);
                    } else {
                        // special cause: multiple switch zero ports are
                        // selected
                        FVResourceNode[] nodeSelection =
                                new FVResourceNode[resourceSelection.length];
                        for (int i = 0; i < nodeSelection.length; i++) {
                            nodeSelection[i] = resourceSelection[i].getParent();
                        }
                        if (resourceSelection.length == 1) {
                            onSingleNode(nodes.get(0), source, nodeSelection);
                        } else {
                            onMultipleNodes(nodes, source, nodeSelection);
                        }
                    }
                } else {
                    onNodeSet(nodes, source, resourceSelection);
                }
            }
        });
    }

    // no selection, try to show the whole graph
    protected void onEmptySelection(final Object source,
            final FVResourceNode[] selectedResources) {
        ShowAllTask updateTask =
                new ShowAllTask(this, source, selectedResources);
        updateCtrl.update(updateTask);
    }

    // a group of nodes that are treated as a set. it's different from
    // #onMultipleNodes because it doesn't show routing information among the
    // nodes
    protected void onNodeSet(final List<GraphNode> nodes, final Object source,
            final FVResourceNode[] selectedResources) {
        ShowGroupTask updateTask =
                new ShowGroupTask(this, source, selectedResources, nodes);
        updateCtrl.update(updateTask);
    }

    // single node
    protected void onSingleNode(GraphNode node, Object source,
            FVResourceNode[] selectedResources) {
        ShowNodeTask updateTask =
                new ShowNodeTask(this, source, selectedResources, node);
        updateCtrl.update(updateTask);
    }

    // multiple nodes, try to find routes among the nodes
    protected void onMultipleNodes(List<GraphNode> nodes, Object source,
            FVResourceNode[] selectedResources) {
        ShowRoutesTask updateTask =
                new ShowRoutesTask(this, source, selectedResources, nodes);
        updateCtrl.update(updateTask);
    }

    // multiple edges
    protected void onEdges(final List<GraphEdge> edges, final Object source,
            final FVResourceNode[] selectedResources) {
        ShowEdgesTask updateTask =
                new ShowEdgesTask(this, source, selectedResources, edges);
        updateCtrl.update(updateTask);
    }

    // protected NodesVisibilityChange getNodeFlipChange(List<GraphNode> nodes)
    // {
    // int[] toInspect = new int[nodes.size()];
    // for (int i = 0; i < toInspect.length; i++) {
    // toInspect[i] = nodes.get(i).getLid();
    // }
    // return new NodesVisibilityChange(currentLayout, topTreeModel, toInspect);
    // }

    public void clearTreeSelection() {
        parent.clearTreeSelection();
    }

    public FVResourceNode[] selectTreeNodes(List<GraphNode> nodes,
            ICancelIndicator indicator) {
        return parent.selectTreeNodes(nodes, indicator);
    }

    public FVResourceNode[] selectTreePorts(List<GraphEdge> edges,
            ICancelIndicator indicator) {
        return parent.selectTreePorts(edges, indicator);
    }

    /**
     * <i>Description:</i>
     *
     * @param subpageName
     */
    public void setCurrentSubpage(String subpageName) {
        resourceController.setCurrentSubpage(subpageName);
    }

    public String getPreviousSubpage() {
        return resourceController.getPreviousSubpage();
    }

    public String getCurrentSubpage() {
        return resourceController.getCurrentSubpage();
    }

    public void cleanup() {
        try {
            if (updateCtrl != null) {
                updateCtrl.cancel();
            }
            graphView.getUpdateService().shutdown();
            log.info("GraphView update service shutdown");
        } finally {
            guideView.getUpdateService().shutdown();
            log.info("GuideView update service shutdown");
        }
    }

}
