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

package com.intel.stl.ui.network.task;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.TraceRecordBean;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.model.LayoutType;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.IModelChange;
import com.intel.stl.ui.network.LayoutChange;
import com.intel.stl.ui.network.TopGraph;
import com.intel.stl.ui.network.TopologyGraphController;
import com.intel.stl.ui.network.TopologyTreeModel;
import com.mxgraph.model.mxCell;

public class ShowRoutesTask extends TopologyUpdateTask {
    private static final Logger log = LoggerFactory
            .getLogger(ShowRoutesTask.class);

    private static final boolean DEBUG = true;

    private final List<GraphNode> nodes;

    private final String vfName;

    private TopologyTreeModel tmpTreeMode;

    private Map<GraphEdge, List<GraphEdge>> traceMap;

    private List<mxCell> edges;

    private final LayoutType defaultLayout = LayoutType.TREE_SLASH;

    /**
     * Description:
     * 
     * @param controller
     * @param source
     * @param selectedResources
     * @param nodes
     */
    public ShowRoutesTask(TopologyGraphController controller, Object source,
            FVResourceNode[] selectedResources, List<GraphNode> nodes) {
        super(controller, source, selectedResources);
        this.nodes = nodes;
        this.vfName = getVFName(selectedResources);
        setIncludeNeighbors(false);
    }

    protected String getVFName(FVResourceNode[] selectedResources) {
        String res = null;
        FVResourceNode refGroup = null;
        String vfName = null;
        for (int i = 0; i < selectedResources.length; i++) {
            vfName = null;
            FVResourceNode parent = selectedResources[i].getParent();
            FVResourceNode group = parent.getParent();
            if (refGroup != null && group != refGroup) {
                throw new IllegalArgumentException(
                        "Nodes have no the same parent!");
            }
            if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
                vfName = group.getTitle();
            }
            if (i == 0) {
                res = vfName;
                refGroup = group;
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.network.TopologyUpdateTask#getNodes(com.intel
     * .stl.ui.common.ICancelIndicator)
     */
    @Override
    protected Collection<Integer> getInvolvedNodes(ICancelIndicator indicator,
            TopGraph oldGraph) {
        traceMap = getTraceMap(nodes, indicator);
        if (indicator != null && indicator.isCancelled()) {
            throw new CancellationException();
        }
        resourceController.showPath(traceMap, vfName);
        if (traceMap == null) {
            return Collections.emptyList();
        }

        Map<Integer, Set<Integer>> links = getUniqueLinks(traceMap);
        edges = new ArrayList<mxCell>();
        Set<Integer> involvedNodes = new HashSet<Integer>();
        for (int fromLid : links.keySet()) {
            Set<Integer> nbr = links.get(fromLid);
            for (int toLid : nbr) {
                if (indicator != null && indicator.isCancelled()) {
                    throw new CancellationException();
                }
                mxCell cell = oldGraph.getEdge(fromLid, toLid);
                edges.add(cell);
            }
            involvedNodes.add(fromLid);
            involvedNodes.addAll(nbr);
        }
        return involvedNodes;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.network.TopologyUpdateTask#applyChanges(com.
     * intel.stl.ui.common.ICancelIndicator, com.intel.stl.ui.network.TopGraph)
     */
    @Override
    public boolean applyChanges(ICancelIndicator indicator, TopGraph newGraph) {
        tmpTreeMode = controller.getFullTopTreeModel().filterBy(newGraph);
        IModelChange change = new LayoutChange(defaultLayout, tmpTreeMode);
        change.execute(newGraph, indicator);
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.network.TopologyUpdateTask#onDone(com.intel.
     * stl.ui.common.ICancelIndicator, com.intel.stl.ui.network.TopGraph)
     */
    @Override
    public void onSuccess(final ICancelIndicator indicator, TopGraph newGraph) {
        super.onSuccess(indicator, newGraph);

        controller.setTopTreeModel(tmpTreeMode);

        FVResourceNode[] selectedNodes = selectedResources;
        if (!indicator.isCancelled() && source != controller) {
            selectedNodes = controller.selectTreeNodes(nodes, indicator);
        }
        final mxCell[] cells = new mxCell[nodes.size()];
        for (int i = 0; i < cells.length; i++) {
            cells[i] = newGraph.getVertex(nodes.get(i).getLid());
        }

        submitGraphTask(new Runnable() {
            @Override
            public void run() {
                graphView.clearEdges();

                graphView.selectEdges(edges, true);

                if (!indicator.isCancelled()) {
                    graphView.selectNodes(cells);
                }

                graphView.updateGraph();
            }
        });

        final FVResourceNode[] nodes = selectedNodes;
        submitOutlineTask(new Runnable() {
            @Override
            public void run() {
                guideView.clearEdges();

                guideView.selectEdges(edges, true);

                if (!indicator.isCancelled()) {
                    guideView.selectNodes(cells);
                }

                guideView.updateGraph();
            }
        });

        guideView.setSelectedResources(nodes);
        setLayout(defaultLayout);
    }

    protected Map<GraphEdge, List<GraphEdge>> getTraceMap(
            List<GraphNode> nodes, ICancelIndicator indicator) {
        ISubnetApi subnetApi = controller.getSubnetApi();
        Map<GraphEdge, List<GraphEdge>> traceMap =
                new LinkedHashMap<GraphEdge, List<GraphEdge>>();

        for (int i = 0; i < nodes.size() - 1; i++) {
            GraphNode source = nodes.get(i);
            for (int j = i + 1; j < nodes.size(); j++) {
                if (indicator.isCancelled()) {
                    log.info("getTraceMap cancelled " + nodes);
                    return null;
                }
                GraphNode target = nodes.get(j);
                List<TraceRecordBean> trace =
                        subnetApi.getTrace(source.getLid(), target.getLid());
                if (trace == null) {
                    log.warn("No trace found on link Lid "
                            + StringUtils.intHexString(source.getLid())
                            + " - Lid "
                            + StringUtils.intHexString(target.getLid()));
                    continue;
                }

                if (DEBUG) {
                    System.out.println(source + " -> " + target);
                }
                GraphEdge edge =
                        new GraphEdge(source.getLid(), source.getType(),
                                target.getLid(), target.getType(),
                                source.getLinkPorts(target));

                List<GraphEdge> edges =
                        new ArrayList<GraphEdge>(trace.size() - 1);
                TraceRecordBean start = null;
                NodeRecordBean startNode = null;
                for (TraceRecordBean bean : trace) {
                    if (indicator.isCancelled()) {
                        return null;
                    }
                    NodeRecordBean node = null;
                    try {
                        node = subnetApi.getNode(bean.getNodeId());
                    } catch (Exception e) {
                        e.printStackTrace();
                        continue;
                    }
                    if (start == null) {
                        start = bean;
                        startNode = node;
                    } else {
                        GraphEdge nextEdge =
                                new GraphEdge(startNode.getLid(), startNode
                                        .getNodeType().getId(), node.getLid(),
                                        node.getNodeType().getId(),
                                        Collections.singletonMap(Integer
                                                .valueOf(start.getExitPort()),
                                                Integer.valueOf(bean
                                                        .getEntryPort())));
                        edges.add(nextEdge);
                        if (DEBUG) {
                            System.out
                                    .println("  " + startNode.getNodeDesc()
                                            + "-" + node.getNodeDesc() + " "
                                            + nextEdge);
                        }
                        start = bean;
                        startNode = node;
                    }
                }

                traceMap.put(edge, edges);
            }
        }
        return traceMap;
    }

    protected Map<Integer, Set<Integer>> getUniqueLinks(
            Map<GraphEdge, List<GraphEdge>> traceMap) {
        Map<Integer, Set<Integer>> res = new HashMap<Integer, Set<Integer>>();
        for (List<GraphEdge> trace : traceMap.values()) {
            for (GraphEdge edge : trace) {
                GraphEdge tmp = edge.normalize();
                Set<Integer> nbr = res.get(tmp.getFromLid());
                if (nbr == null) {
                    nbr = new HashSet<Integer>();
                    res.put(tmp.getFromLid(), nbr);
                }
                nbr.add(tmp.getToLid());
            }
        }
        return res;
    }
}
