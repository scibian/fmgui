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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.model.GraphNode;
import com.mxgraph.model.mxCell;
import com.mxgraph.view.mxGraph;

public class GraphBuilder {
    private static final Logger log =
            LoggerFactory.getLogger(GraphBuilder.class);

    public static final int SWITCH_SIZE = 64;

    public static final int HFI_SIZE = 32;

    private static boolean DEBUG = false;

    public GraphBuilder() {
    }

    public TopologyTreeModel build(TopGraph graph, List<NodeRecordBean> nodes,
            List<LinkRecordBean> links) {
        long t = System.currentTimeMillis();
        log.info("Create graph with " + nodes.size() + " nodes, " + links.size()
                + " links");
        Map<Integer, GraphNode> nodesMap = new HashMap<Integer, GraphNode>();
        Set<GraphNode> endNodes = new HashSet<GraphNode>();
        for (NodeRecordBean node : nodes) {
            GraphNode gn = new GraphNode(node.getLid());
            gn.setName(node.getNodeDesc());
            gn.setType(node.getNodeInfo().getNodeType());
            gn.setNumPorts(node.getNodeInfo().getNumPorts());
            if (gn.isEndNode()) {
                gn.setDepth(0);
                endNodes.add(gn);
            }
            nodesMap.put(node.getLid(), gn);
        }
        fillLinks(nodesMap, links);
        List<GraphNode> roots = endNodes.isEmpty() ? getRoots(nodesMap.values())
                : getRoots(nodesMap.values(), endNodes);
        if (DEBUG) {
            for (GraphNode node : nodesMap.values()) {
                node.dump(System.out);
            }
        }
        log.info("Found " + roots.size() + " root nodes");
        if (DEBUG) {
            System.out.println("Roots: " + roots);
        }
        graph.clear();
        TopologyTreeModel model = fillGraph(graph, roots);
        log.info("Created graph " + graph + " in "
                + (System.currentTimeMillis() - t) + " ms "
                + Thread.currentThread());
        return model;
    }

    /**
     * <i>Description:</i> exclude switches that are unlikely to be leaf
     * switches
     *
     * @param endNodes
     */
    protected Set<GraphNode> screenEndNodes(Set<GraphNode> endNodes) {
        Set<GraphNode> leafSwitches = new HashSet<GraphNode>();
        // get all leaf switch candidate
        for (GraphNode node : endNodes) {
            Set<GraphNode> nbr = node.getMiddleNeighbor();
            for (GraphNode parent : nbr) {
                if (!leafSwitches.contains(parent)) {
                    leafSwitches.add(parent);
                }
            }
        }

        // special case - only two candidate
        if (leafSwitches.size() <= 2) {
            return endNodes;
        }

        // remove switches connect to another candidate and have less number of
        // end nodes
        Set<GraphNode> toRemove = new HashSet<GraphNode>();
        for (GraphNode node : leafSwitches) {
            Set<GraphNode> nbrs = node.getMiddleNeighbor();
            int numHosts = node.getEndNeighbor().size();
            for (GraphNode peer : nbrs) {
                if (!leafSwitches.contains(peer) || toRemove.contains(peer)) {
                    continue;
                }

                if (peer.getEndNeighbor().size() > numHosts) {
                    toRemove.add(node);
                    break;
                } else {
                    toRemove.add(peer);
                }
            }
        }
        leafSwitches.removeAll(toRemove);
        Set<GraphNode> res = new HashSet<GraphNode>();
        for (GraphNode node : leafSwitches) {
            res.addAll(node.getEndNeighbor());
        }
        if (res.isEmpty()) {
            return endNodes;
        } else {
            return res;
        }
    }

    protected void fillLinks(Map<Integer, GraphNode> map,
            List<LinkRecordBean> links) {
        for (LinkRecordBean link : links) {
            int fromLid = link.getFromLID();
            GraphNode node = map.get(fromLid);
            int toLid = link.getToLID();
            GraphNode toNode = map.get(toLid);
            if (node != null && toNode != null) {
                node.addLink(toNode, link.getFromPortIndex(),
                        link.getToPortIndex());
            } else {
                if (node == null) {
                    // this shouldn't happen
                    log.warn("Node " + fromLid + " are not in node list");
                }

                if (toNode == null) {
                    // this shouldn't happen
                    log.warn("Node " + toLid + " are not in node list");
                }
            }

        }
    }

    protected List<GraphNode> getRoots(Collection<GraphNode> nodes,
            Set<GraphNode> endNodes) {
        Set<GraphNode> workingNodes = new HashSet<GraphNode>(nodes);
        workingNodes.removeAll(endNodes);
        Set<GraphNode> nextRef =
                new HashSet<GraphNode>(screenEndNodes(endNodes));

        boolean hasChange = true;
        while (!workingNodes.isEmpty() && hasChange) {
            Set<GraphNode> refNodes = nextRef;
            nextRef = new HashSet<GraphNode>();
            hasChange = false;
            for (GraphNode ref : refNodes) {
                for (GraphNode gn : ref.getMiddleNeighbor()) {
                    boolean success = workingNodes.remove(gn);
                    if (success) {
                        nextRef.add(gn);
                        gn.setDepth(ref.getDepth() + 1);
                    }
                    if (!hasChange) {
                        hasChange = true;
                    }
                }
            }
        }
        List<GraphNode> res = new ArrayList<GraphNode>(nextRef);
        if (!workingNodes.isEmpty()) {
            res.addAll(workingNodes);
            log.warn("Found isolated nodes " + workingNodes.size());
        }
        Collections.sort(res);
        return res;
    }

    /**
     *
     * Description: if a subnet has no end nodes, we try switches one by one to
     * figure out the root(s)
     *
     * @param nodes
     * @return
     */
    protected List<GraphNode> getRoots(Collection<GraphNode> nodes) {
        // simple heuristic: using the switches with least links
        // TODO: test and revisit the approach
        Set<GraphNode> refNodes = new HashSet<GraphNode>();
        int minNumLinks = Integer.MAX_VALUE;
        for (GraphNode node : nodes) {
            int links = node.getMiddleNeighbor().size();
            if (links < minNumLinks) {
                refNodes.clear();
                refNodes.add(node);
            } else if (links == minNumLinks) {
                refNodes.add(node);
            }
        }
        return getRoots(nodes, refNodes);
    }

    protected TopologyTreeModel fillGraph(TopGraph graph,
            List<GraphNode> roots) {
        List<List<Integer>> ranks = new ArrayList<List<Integer>>();
        int maxRankSize = 0;
        int numNodes = 0;
        Set<GraphNode> processed = new HashSet<GraphNode>();

        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        try {
            for (GraphNode node : roots) {
                insertVertex(graph, parent, node);
            }

            List<GraphNode> nextNodes = new ArrayList<GraphNode>(roots);
            while (!nextNodes.isEmpty()) {
                List<Integer> rank = new ArrayList<Integer>();
                for (GraphNode node : nextNodes) {
                    rank.add(node.getLid());
                }
                ranks.add(Collections.unmodifiableList(rank));
                numNodes += rank.size();
                if (nextNodes.size() > maxRankSize) {
                    maxRankSize = nextNodes.size();
                }
                List<GraphNode> workingNodes = nextNodes;
                nextNodes = new ArrayList<GraphNode>();
                for (GraphNode node : workingNodes) {
                    mxCell vertex = graph.getVertex(node.getLid());
                    Set<GraphNode> neighbor = node.getMiddleNeighbor();
                    for (GraphNode nbr : neighbor) {
                        if (processed.contains(nbr)) {
                            continue;
                        }
                        mxCell nbrVertex = graph.getVertex(nbr.getLid());
                        if (nbrVertex == null) {
                            nbrVertex = insertVertex(graph, parent, nbr);
                        }
                        String edgeId = TopGraph.getEdgeId(vertex, nbrVertex);
                        graph.insertEdge(parent, edgeId, null, vertex,
                                nbrVertex);
                        if (!nextNodes.contains(nbr)
                                && !workingNodes.contains(nbr)) {
                            nextNodes.add(nbr);
                        }
                    }
                    neighbor = node.getEndNeighbor();
                    numNodes += neighbor.size();
                    for (GraphNode nbr : neighbor) {
                        if (processed.contains(nbr)) {
                            continue;
                        }
                        mxCell nbrVertex = graph.getVertex(nbr.getLid());
                        if (nbrVertex == null) {
                            nbrVertex = insertVertex(graph, parent, nbr);
                        }
                        String edgeId = TopGraph.getEdgeId(vertex, nbrVertex);
                        graph.insertEdge(parent, edgeId, null, vertex,
                                nbrVertex);
                    }
                }
                processed.addAll(workingNodes);
            }
            TopologyTreeModel model = new TopologyTreeModel(ranks, maxRankSize,
                    new ArrayList<Integer>(), numNodes);
            return model;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            graph.getModel().endUpdate();
        }

        return null;
    }

    protected mxCell insertVertex(mxGraph graph, Object parent, GraphNode nbr) {
        NodeType type = NodeType.getNodeType(nbr.getType());
        int w = type == NodeType.HFI ? HFI_SIZE : SWITCH_SIZE;
        int h = type == NodeType.HFI ? HFI_SIZE : SWITCH_SIZE;
        String style = type == NodeType.HFI
                ? "shape=image;image=" + UIImages.HFI_IMG.getFileName()
                : "shape=image;image="
                        + UIImages.SWITCH_EXPANDED_IMG.getFileName();
        Object vertex = graph.insertVertex(parent,
                TopGraph.getVertexId(nbr.getLid()), nbr, 0, 0, w, h, style);
        return (mxCell) vertex;
    }

}
