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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.model.GraphNode;
import com.mxgraph.layout.mxGraphLayout;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxRectangle;

public class TreeLayout extends mxGraphLayout {
    private static final Logger log = LoggerFactory.getLogger(TreeLayout.class);

    public static final double DEFAULT_INTRA_SPACE = 30.0;

    public static final double DEFAULT_INTER_SPACE = 30.0;

    public static final double CIRCLE_ANGLE = Math.PI / 3;

    public enum Style {
        CIRCLE,
        SLASH,
        LINE
    };

    private final TopGraph topGraph;

    private final TopologyTreeModel model;

    /**
     * The minimum buffer between cells on the same rank
     */
    protected double intraCellSpacing = DEFAULT_INTRA_SPACE;

    /**
     * The minimum distance between cells on adjacent ranks
     */
    protected double interRankCellSpacing = DEFAULT_INTER_SPACE;

    /**
     * the minimum width for the graph space
     */
    protected double minWidth = 100.0;

    /**
     * the minimum height for the graph space
     */
    protected double minHeight = 100.0;

    /**
     * Specifies if all edge points of traversed edges should be removed.
     * Default is true.
     */
    protected boolean resetEdges = true;

    protected Style style;

    protected double r;

    protected mxRectangle prefRec;

    private int[] numOpenNodes;

    public TreeLayout(TopGraph graph, TopologyTreeModel model) {
        this(graph, model, Style.CIRCLE);
    }

    /**
     * Description:
     *
     * @param graph
     */
    public TreeLayout(TopGraph graph, TopologyTreeModel model, Style style) {
        super(graph);
        this.model = model;
        this.style = style;
        setUseBoundingBox(false);
        topGraph = graph;
        graph.setVertexScaleThreshold(GraphBuilder.SWITCH_SIZE
                / (GraphBuilder.SWITCH_SIZE + intraCellSpacing));
    }

    /**
     * @return the intraCellSpacing
     */
    public double getIntraCellSpacing() {
        return intraCellSpacing;
    }

    /**
     * @param intraCellSpacing
     *            the intraCellSpacing to set
     */
    public void setIntraCellSpacing(double intraCellSpacing) {
        this.intraCellSpacing = intraCellSpacing;
    }

    /**
     * @return the interRankCellSpacing
     */
    public double getInterRankCellSpacing() {
        return interRankCellSpacing;
    }

    /**
     * @param interRankCellSpacing
     *            the interRankCellSpacing to set
     */
    public void setInterRankCellSpacing(double interRankCellSpacing) {
        this.interRankCellSpacing = interRankCellSpacing;
    }

    /**
     * @return the minWidth
     */
    public double getMinWidth() {
        return minWidth;
    }

    /**
     * @param minWidth
     *            the minWidth to set
     */
    public void setMinWidth(double minWidth) {
        this.minWidth = minWidth;
    }

    /**
     * @return the minHeight
     */
    public double getMinHeight() {
        return minHeight;
    }

    /**
     * @param minHeight
     *            the minHeight to set
     */
    public void setMinHeight(double minHeight) {
        this.minHeight = minHeight;
    }

    /**
     * @return the resetEdges
     */
    public boolean isResetEdges() {
        return resetEdges;
    }

    /**
     * @param resetEdges
     *            the resetEdges to set
     */
    public void setResetEdges(boolean resetEdges) {
        this.resetEdges = resetEdges;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mxgraph.layout.mxGraphLayout#execute(java.lang.Object)
     */
    @Override
    public void execute(Object parent) {
        execute(parent, null);
    }

    public void execute(Object parent, ICancelIndicator cancelIndicator) {
        log.info("layout start @ " + topGraph + " " + Thread.currentThread());
        super.execute(parent);
        mxIGraphModel gModel = graph.getModel();
        r = (intraCellSpacing + GraphBuilder.SWITCH_SIZE);
        prefRec = calculateSize(cancelIndicator);
        if (prefRec == null || isCancelled(cancelIndicator)) {
            log.info("layout execute cancelled " + topGraph + " "
                    + Thread.currentThread());
            return;
        }

        gModel.beginUpdate();
        try {
            placeMiddleNodes(cancelIndicator);
            log.info("layout finished " + topGraph + " "
                    + Thread.currentThread());
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            gModel.endUpdate();
        }
    }

    protected boolean isCancelled(ICancelIndicator cancelIndicator) {
        return cancelIndicator == null ? false : cancelIndicator.isCancelled();
    }

    protected mxRectangle getMaxSize(mxIGraphModel gModel) {
        double maxWidth = 0;
        double maxHeight = 0;
        int childCount = gModel.getChildCount(parent);

        for (int i = 0; i < childCount; i++) {
            Object cell = gModel.getChildAt(parent, i);
            if (!isVertexIgnored(cell)) {
                mxRectangle bounds = getVertexBounds(cell);
                maxWidth = Math.max(maxWidth, bounds.getWidth());
                maxHeight = Math.max(maxHeight, bounds.getHeight());
            } else if (!isEdgeIgnored(cell) && isResetEdges()) {
                graph.resetEdge(cell);
            }
        }

        return new mxRectangle(0, 0, maxWidth, maxHeight);
    }

    protected mxRectangle calculateSize(ICancelIndicator cancelIndicator) {

        if (model == null) {
            return null;
        }

        int numColumns = model.getMaxWidth();
        double width =
                numColumns * (GraphBuilder.SWITCH_SIZE + intraCellSpacing)
                        + intraCellSpacing * 2;
        double height = model.getNumRanks()
                * (GraphBuilder.SWITCH_SIZE + interRankCellSpacing)
                + interRankCellSpacing * 2;
        List<List<Integer>> ranks = model.getRanks();
        numOpenNodes = new int[ranks.size()];
        if (style == Style.CIRCLE) {
            for (int i = 0; i < ranks.size(); i++) {
                if (isCancelled(cancelIndicator)) {
                    log.info("calculateSize cancelled");
                    return null;
                }
                List<Integer> nodeLids = ranks.get(i);
                double w = nodeLids.size()
                        * (GraphBuilder.SWITCH_SIZE + intraCellSpacing)
                        + intraCellSpacing * 2;
                numOpenNodes[i] = 0;
                for (Integer nodeLid : nodeLids) {
                    GraphNode node =
                            (GraphNode) topGraph.getVertex(nodeLid).getValue();
                    if (!node.isCollapsed()) {
                        numOpenNodes[i] += 1;
                    }
                }
                w += 2 * r * numOpenNodes[i];
                width = Math.max(width, w);
            }
        }
        height = Math.max(height, width / 2);
        mxRectangle rec = new mxRectangle(GraphBuilder.SWITCH_SIZE,
                GraphBuilder.SWITCH_SIZE, Math.max(width, minWidth),
                Math.max(height, minHeight));
        return rec;
    }

    protected void placeMiddleNodes(ICancelIndicator cancelIndicator) {
        List<List<Integer>> rwen = model.getRanks();
        Set<GraphNode> sharedEndNodes = new HashSet<GraphNode>();
        Set<Object> collapsedVertices = new HashSet<Object>();
        Set<Object> expandedVertices = new HashSet<Object>();
        double x0 = prefRec.getX();
        double y0 = prefRec.getY();
        double stepX = 0;
        double stepY = prefRec.getHeight() / (rwen.size() - 1);
        double x = x0, y = y0;
        // circle layout for switches
        boolean circleLayout;
        double startAngle = (Math.PI - CIRCLE_ANGLE) / 2;
        double startCircleHeight = Math.sin(startAngle);
        double angleStep = 0;
        double circleR = 0;
        for (int i = 0; i < rwen.size(); i++) {
            List<Integer> rank = rwen.get(i);
            stepX = (prefRec.getWidth() - numOpenNodes[i] * 2 * r)
                    / rank.size();
            x = x0 + stepX / 2;
            circleLayout = hasInternalConnection(rank);
            if (circleLayout) {
                angleStep = CIRCLE_ANGLE / (rank.size() - 1);
                circleR =
                        stepX * (rank.size() - 1) / (2 * Math.cos(startAngle));
            }
            Map<Object, List<GraphNode>> endNodes =
                    new LinkedHashMap<Object, List<GraphNode>>();
            for (int j = 0; j < rank.size(); j++) {
                if (isCancelled(cancelIndicator)) {
                    log.info("placeMiddleNodes cancelled");
                    return;
                }

                mxCell cell = topGraph.getVertex(rank.get(j));
                GraphNode node = null;
                if (cell == null) {
                    System.out
                            .println("Couldn't find vertex lid=" + rank.get(j));
                    continue;
                } else {
                    node = (GraphNode) cell.getValue();
                }
                Object vertex = null;
                if (node != null) {
                    if (style == Style.CIRCLE && !node.isCollapsed()) {
                        x += r;
                    }
                    vertex = topGraph.getVertex(node.getLid());
                } else {
                    continue;
                }

                if (isVertexMovable(vertex)) {
                    if (circleLayout) {
                        double circleY =
                                y - (Math.sin(startAngle + angleStep * j)
                                        - startCircleHeight) * circleR;
                        setVertexLocation(vertex, x, (int) (circleY + 0.5));
                    } else {
                        setVertexLocation(vertex, x, y);
                    }
                }
                if (style == Style.CIRCLE && node != null
                        && !node.isCollapsed()) {
                    x += r;
                }
                x += stepX;

                if (node != null && (!node.hasEndNodes() || node.isEndNode())) {
                    continue;
                }

                mxGraphModel gModel = (mxGraphModel) graph.getModel();
                // check node type for the special b2b case
                if (node != null && !node.isEndNode()) {
                    if (node.isCollapsed()) {
                        collapsedVertices.add(vertex);
                    } else {
                        expandedVertices.add(vertex);
                    }
                }

                boolean hasChange = false;
                List<GraphNode> toPlace = new ArrayList<GraphNode>();
                if (node != null) {
                    for (GraphNode endNode : node.getEndNeighbor()) {
                        Object enVertex = topGraph.getVertex(endNode.getLid());
                        if (endNode.getMiddleNeighbor().size() > 1) {
                            sharedEndNodes.add(endNode);
                            if (!gModel.isVisible(enVertex)) {
                                gModel.setVisible(enVertex, true);
                                hasChange = true;
                            }
                        } else if (!node.isCollapsed()) {
                            toPlace.add(endNode);
                            if (!gModel.isVisible(enVertex)) {
                                gModel.setVisible(enVertex, true);
                                hasChange = true;
                            }
                        } else if (gModel.isVisible(enVertex)) {
                            gModel.setVisible(enVertex, false);
                            hasChange = true;
                        }
                    }
                }
                // hack to include node collapse into currentEdit
                // that will be used by undoManager
                if (hasChange) {
                    CollapseChange collapseChange = new CollapseChange(gModel,
                            node, node.isCollapsed());
                    gModel.execute(collapseChange);
                    collapseChange.setPrevious(!node.isCollapsed());
                }

                if (!toPlace.isEmpty()) {
                    endNodes.put(vertex, toPlace);
                }
            }
            double expand = placeEndNodes(endNodes);
            y += Math.max(stepY, expand + interRankCellSpacing);
        }
        for (GraphNode node : sharedEndNodes) {
            if (isCancelled(cancelIndicator)) {
                log.info("placeMiddleNodes cancelled");
                return;
            }
            placeSharedNode(node);
        }
        graph.setCellStyles(mxConstants.STYLE_IMAGE,
                UIImages.SWITCH_COLLAPSED_IMG.getFileName(),
                collapsedVertices.toArray());
        graph.setCellStyles(mxConstants.STYLE_IMAGE,
                UIImages.SWITCH_EXPANDED_IMG.getFileName(),
                expandedVertices.toArray());
    }

    protected boolean hasInternalConnection(List<Integer> rank) {
        for (Integer id : rank) {
            mxCell cell = topGraph.getVertex(id);
            GraphNode node = (GraphNode) cell.getValue();
            for (GraphNode peer : node.getMiddleNeighbor()) {
                if (rank.contains(peer.getLid())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Description:
     *
     * @param endNodes
     * @return
     */
    protected double placeEndNodes(Map<Object, List<GraphNode>> endNodes) {
        double expand = 0;
        for (Object vertex : endNodes.keySet()) {
            List<GraphNode> toPlace = endNodes.get(vertex);
            mxRectangle rec = getVertexBounds(vertex);
            double x0 = rec.getCenterX();
            double y0 = rec.getCenterY() + interRankCellSpacing * 2
                    + GraphBuilder.HFI_SIZE;
            if (style == Style.CIRCLE) {
                circleNodes(toPlace, x0, y0, r - intraCellSpacing);
                if (expand == 0) {
                    expand = r;
                }
            } else if (style == Style.SLASH) {
                double height = 0;
                if (toPlace.size() * GraphBuilder.HFI_SIZE > r) {
                    height = toPlace.size() * GraphBuilder.HFI_SIZE;
                }
                slashNodes(toPlace, x0, y0, r - intraCellSpacing, height);
                expand = Math.max(expand, height);
            } else if (style == Style.LINE) {
                expand += intraCellSpacing + GraphBuilder.HFI_SIZE;
                lineNodes(toPlace, x0, y0, expand);
            }
        }
        return expand;
    }

    protected void circleNodes(List<GraphNode> nodes, double x0, double y0,
            double r) {
        int size = nodes.size();
        double step = Math.PI / (size + 1);
        double alpha = Math.PI;
        double x = x0;
        double y = y0;
        double weight = 1;
        for (int i = 0; i < size; i++) {
            Object v = topGraph.getVertex(nodes.get(i).getLid());
            alpha += step;
            weight = i % 2 == 0 ? 0.8 : 1.2;
            if (isVertexMovable(v)) {
                setVertexLocation(v, x + r * Math.cos(alpha) * weight,
                        y - r * Math.sin(alpha) * weight);
            }
        }
    }

    protected void slashNodes(List<GraphNode> nodes, double x0, double y0,
            double w, double h) {
        int size = nodes.size();
        double xStep = size < 1 ? w : w / (size - 1);
        double yStep = size < 1 ? 0 : h / (size - 1);
        double x = x0 - w / 2;
        double y = y0;
        for (int i = 0; i < size; i++) {
            Object v = topGraph.getVertex(nodes.get(i).getLid());
            if (isVertexMovable(v)) {
                setVertexLocation(v, x, y);
            }
            x += xStep;
            y += yStep;
        }
    }

    protected void lineNodes(List<GraphNode> nodes, double x0, double y0,
            double h) {
        int size = nodes.size();
        double xStep = GraphBuilder.HFI_SIZE + intraCellSpacing;
        double x = x0 - xStep * (nodes.size() - 1) / 2;
        if (x < intraCellSpacing) {
            x = intraCellSpacing;
        }
        double y = y0 + h;
        for (int i = 0; i < size; i++) {
            Object v = topGraph.getVertex(nodes.get(i).getLid());
            if (isVertexMovable(v)) {
                setVertexLocation(v, x, y);
            }
            x += xStep;
        }
    }

    protected void placeSharedNode(GraphNode node) {
        Object vertex = topGraph.getVertex(node.getLid());
        double x = 0, y = 0;
        Set<GraphNode> neighbor = node.getMiddleNeighbor();
        for (GraphNode gn : neighbor) {
            Object v = topGraph.getVertex(gn.getLid());
            mxRectangle rec = getVertexBounds(v);
            x += rec.getX();
            y += rec.getY();
        }
        setVertexLocation(vertex, x / neighbor.size(), y / neighbor.size());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mxgraph.layout.mxGraphLayout#setVertexLocation(java.lang.Object,
     * double, double)
     */
    @Override
    public mxRectangle setVertexLocation(Object vertex, double x, double y) {
        try {
            mxRectangle rec = getVertexBounds(vertex);
            return super.setVertexLocation(vertex, x - rec.getWidth() / 2,
                    y - rec.getHeight() / 2);
        } catch (Exception e) {
            log.warn(((mxCell) vertex).getValue() + " " + e.getMessage());
            System.out.println(((mxCell) vertex).getValue());
            e.printStackTrace();
        }
        return null;
    }

}
