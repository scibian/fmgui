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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.CancellationException;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.GraphNode;
import com.mxgraph.io.mxCodecRegistry;
import com.mxgraph.io.mxObjectCodec;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;
import com.mxgraph.model.mxIGraphModel;
import com.mxgraph.util.mxConstants;
import com.mxgraph.util.mxEvent;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.view.mxCellState;
import com.mxgraph.view.mxGraph;
import com.mxgraph.view.mxGraphSelectionModel;
import com.mxgraph.view.mxGraphView;
import com.mxgraph.view.mxStylesheet;

public class TopGraph extends mxGraph {
    private static final String TOP = "N";

    private static final Logger log = LoggerFactory.getLogger(TopGraph.class);
    static {
        mxCodecRegistry.addPackage(GraphNode.class.getPackage().getName());
        mxCodecRegistry.register(new mxObjectCodec(new GraphNode()));
    }

    private AtomicReference<mxIGraphModel> modelRef;

    private Map<String, Map<String, Object>> highlightedEdges;

    private Map<Map<String, Object>, Map<String, Object>> cachedHighlighStyle;

    private Set<String> markedEdges;

    private double vertexScaleThreshold;

    /**
     * Description:
     * 
     */
    public TopGraph() {
        this(null, null);
    }

    /**
     * Description:
     * 
     * @param model
     */
    public TopGraph(mxIGraphModel model) {
        this(model, null);
    }

    /**
     * Description:
     * 
     * @param stylesheet
     */
    public TopGraph(mxStylesheet stylesheet) {
        this(null, stylesheet);
    }

    /**
     * Description:
     * 
     * @param model
     * @param stylesheet
     */
    public TopGraph(mxIGraphModel model, mxStylesheet stylesheet) {
        super(model, stylesheet);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#createSelectionModel()
     */
    @Override
    protected mxGraphSelectionModel createSelectionModel() {
        return new TopSelectionModel(this);
    }

    /**
     * @param vertexScaleThreshold
     *            the vertexScaleThreshold to set
     */
    public void setVertexScaleThreshold(double vertexScaleThreshold) {
        this.vertexScaleThreshold = vertexScaleThreshold;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#getToolTipForCell(java.lang.Object)
     */
    @Override
    public String getToolTipForCell(Object cell) {
        if (model.isVertex(cell)) {
            GraphNode node = (GraphNode) ((mxCell) cell).getValue();
            if (node.hasEndNodes() && node.isCollapsed()) {
                // StringBuilder sb = new StringBuilder("<html>");
                // sb.append("<p>"+node.getName()+" with <strong>"+node.getEndNeighbor().size()+"</strong> end nodes</p>");
                // // for (GraphNode en : node.getEndNeighbor()) {
                // // sb.append("&nbsp;&nbsp;"+en.getName()+"<br>");
                // // }
                // sb.append("</html>");
                // return sb.toString();
                return UILabels.STL70001_COLLAPSABLE_NODE_TOOLTIP
                        .getDescription(node.getName(),
                                Integer.toString(node.getEndNeighbor().size()));
            }
        }
        return super.getToolTipForCell(cell);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#createGraphView()
     */
    @Override
    protected mxGraphView createGraphView() {
        highlightedEdges = new HashMap<String, Map<String, Object>>();
        cachedHighlighStyle =
                new HashMap<Map<String, Object>, Map<String, Object>>();
        markedEdges = new HashSet<String>();

        return new GraphView(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#getModel()
     */
    @Override
    public mxIGraphModel getModel() {
        if (modelRef == null) {
            throw new RuntimeException("modelRef get is null in getModel.");
        }
        return modelRef.get();
    }

    @Override
    public void setModel(mxIGraphModel model) {
        super.setModel(model);
        restoreSelection(model, null);
        if (modelRef == null) {
            modelRef = new AtomicReference<mxIGraphModel>();
        }
        modelRef.set(model);
        if (highlightedEdges != null || markedEdges != null) {
            refresh();
        }
    }

    public void setModel(mxIGraphModel model, ICancelIndicator indicator) {
        super.setModel(model);
        restoreSelection(model, indicator);
        modelRef.set(model);
        if (highlightedEdges != null || markedEdges != null) {
            refresh();
        }
    }

    protected void restoreSelection(mxIGraphModel model,
            ICancelIndicator indicator) {
        Object[] curSelections = getSelectionCells();
        if (curSelections.length == 0) {
            return;
        }

        Map<String, Object> selections = new LinkedHashMap<String, Object>();
        for (Object obj : curSelections) {
            selections.put(((mxCell) obj).getId(), null);
        }

        Object[] children =
                mxGraphModel.getChildCells(model, getDefaultParent(), false,
                        false);
        for (Object child : children) {
            if (indicator != null && indicator.isCancelled()) {
                log.info("setModel " + ((mxCell) model.getRoot()).getId()
                        + " cancelled.");
                return;
            }
            mxCell cell = (mxCell) child;
            if (selections.containsKey(cell.getId())) {
                selections.put(cell.getId(), cell);
            }
        }

        synchronized (this) {
            selectionModel.setCells(selections.values().toArray());
        }

        if (indicator != null && indicator.isCancelled()) {
            log.info("setModel " + ((mxCell) model.getRoot()).getId()
                    + " cancelled.");
            return;
        }
    }

    public mxCell getVertex(int lid) {
        String cellId = getVertexId(lid);
        return (mxCell) ((mxGraphModel) getModel()).getCell(cellId);
    }

    public mxCell getEdge(int fromId, int toId) {
        String cellId = getEdgeId(fromId, toId);
        mxCell res = (mxCell) ((mxGraphModel) getModel()).getCell(cellId);
        if (res == null) {
            cellId = getEdgeId(toId, fromId);
            res = (mxCell) ((mxGraphModel) getModel()).getCell(cellId);
        }
        return res;
    }

    public Object[] getVertices() {
        return mxGraphModel.getChildCells(model, getDefaultParent(), true,
                false);
    }

    public void collapseAll() {
        setCollapseState(true);
    }

    public void expandAll() {
        setCollapseState(false);
    }

    protected void setCollapseState(boolean b) {
        Object[] children =
                mxGraphModel.getChildCells(model, getDefaultParent(), true,
                        false);
        for (Object child : children) {
            GraphNode node = (GraphNode) ((mxCell) child).getValue();
            if (node.hasEndNodes()) {
                node.setCollapsed(b);
            }
        }
    }

    private void highlight(mxCell cell) {
        Map<String, Object> style = highlightedEdges.get(cell.getId());
        if (style == null) {
            Map<String, Object> orgStyle = getCellStyle(cell);
            style = cachedHighlighStyle.get(orgStyle);
            if (style == null) {
                style = new HashMap<String, Object>(orgStyle);
                style.put(mxConstants.STYLE_STROKECOLOR,
                        UIConstants.EDGE_HIGHLIGHT_COLOR_STR);
                style.put(mxConstants.STYLE_STROKEWIDTH,
                        UIConstants.EDGE_HIGHLIGHT_STROKE_STR);
                style.put(mxConstants.STYLE_OPACITY, "100");
                cachedHighlighStyle.put(orgStyle, style);
            }
            highlightedEdges.put(cell.getId(), style);
        }
    }

    private void dehighlight(mxCell cell) {
        highlightedEdges.remove(cell.getId());
    }

    public synchronized void highlightConnections(mxCell cell, boolean highlight) {
        if (cell == null) {
            return;
        }

        int numEdges = cell.getEdgeCount();
        for (int i = 0; i < numEdges; i++) {
            mxCell edge = (mxCell) cell.getEdgeAt(i);
            if (highlight) {
                highlight(edge);
            } else {
                dehighlight(edge);
            }
        }
        refresh();
    }

    public synchronized void clearHighlightedEdges() {
        highlightedEdges.clear();
        refresh();
    }

    private void mark(mxCell cell) {
        markedEdges.add(cell.getId());
    }

    private void demark(mxCell cell) {
        markedEdges.remove(cell.getId());
    }

    public synchronized void markConnections(Collection<mxCell> cells,
            boolean selected, ICancelIndicator indicator) {
        if (cells == null || cells.isEmpty()) {
            return;
        }

        for (mxCell cell : cells) {
            int numEdges = cell.getEdgeCount();
            for (int i = 0; i < numEdges; i++) {
                if (indicator != null && indicator.isCancelled()) {
                    return;
                }

                mxCell edge = (mxCell) cell.getEdgeAt(i);
                if (selected) {
                    mark(edge);
                } else {
                    demark(edge);
                }
            }
        }

        fireMarkEvent();
        refresh();
    }

    public synchronized void markEdges(Collection<mxCell> cells,
            boolean selected) {
        if (cells == null || cells.isEmpty()) {
            return;
        }

        for (mxCell cell : cells) {
            if (selected) {
                mark(cell);
            } else {
                demark(cell);
            }
        }

        fireMarkEvent();
        refresh();
    }

    public synchronized void clearMarkedEdges() {
        markedEdges.clear();

        fireMarkEvent();
        refresh();
    }

    protected void fireMarkEvent() {
        fireEvent(new mxEventObject(mxEvent.MARK));
    }

    public synchronized boolean isMarked(mxCell cell) {
        String cellId = cell.getId();
        return markedEdges.contains(cellId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#setSelectionCells(java.lang.Object[])
     */
    @Override
    public void setSelectionCells(Object[] cells) {
        // because we are using double model, we should use Id rather than
        // reference to identify a cell
        mxGraphModel model = (mxGraphModel) getModel();
        Object[] realCells = null;
        if (cells != null) {
            realCells = new Object[cells.length];
            for (int i = 0; i < cells.length; i++) {
                String id = ((mxCell) cells[i]).getId();
                realCells[i] = model.getCell(id);
            }
        }
        super.setSelectionCells(realCells);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.view.mxGraph#clearSelection()
     */
    @Override
    public synchronized void clearSelection() {
        super.clearSelection();
    }

    public void clear() {
        mxCell root = (mxCell) getDefaultParent();
        while (root.getChildCount() > 0) {
            root.remove(0);
        }

        mxGraphModel model = (mxGraphModel) getModel();
        if (model != null) {
            model.getCells().clear();
        }
    }

    public TopGraph getGraphCopy(int modelId, ICancelIndicator indicator) {
        mxIGraphModel org = getModel();
        Object[] rootCopy =
                org.cloneCells(new Object[] { org.getRoot() }, true);
        mxCell rootCell = (mxCell) rootCopy[0];
        rootCell.setId(getModelName(modelId));
        int childCount = rootCell.getChildCount();
        for (int i = 0; i < childCount; i++) {
            if (indicator != null && indicator.isCancelled()) {
                throw new CancellationException();
            }
            mxCell child = (mxCell) rootCell.getChildAt(i);
            resetId(child);
        }
        mxIGraphModel newModel = new mxGraphModel(rootCopy[0]);

        TopGraph newGraph = createGraph();
        newGraph.setModel(newModel);
        cloneGraphNodes(newGraph, indicator);

        return newGraph;
    }

    protected void resetId(mxCell cell) {
        cell.setId(getPreferedId(cell));
        int childCount = cell.getChildCount();
        for (int i = 0; i < childCount; i++) {
            mxCell child = (mxCell) cell.getChildAt(i);
            resetId(child);
        }
    }

    public TopGraph filterBy(int modelId, Collection<Integer> members,
            boolean includeNeighbors, ICancelIndicator indicator) {
        if (members == null) {
            return getGraphCopy(modelId, indicator);
        } else if (members.isEmpty()) {
            return createGraph();
        }

        mxGraphModel org = (mxGraphModel) getModel();

        mxCell rootCell = new mxCell();
        rootCell.setId(getModelName(modelId));
        mxCell baseCell = new mxCell();
        rootCell.insert(baseCell);

        try {
            Map<String, mxCell> newCells = new HashMap<String, mxCell>();
            // copy vertices
            for (int lid : members) {
                if (indicator != null && indicator.isCancelled()) {
                    throw new CancellationException();
                }
                String cellId = getVertexId(lid);
                mxCell cell = (mxCell) org.getCell(cellId);
                mxCell copy = (mxCell) cell.clone();
                copy.setId(cellId);
                baseCell.insert(copy);
                newCells.put(cellId, copy);
            }

            // copy edges
            for (int lid : members) {
                if (indicator != null && indicator.isCancelled()) {
                    throw new CancellationException();
                }
                mxCell cell = (mxCell) org.getCell(getVertexId(lid));
                int numEdges = cell.getEdgeCount();
                if (numEdges > 0) {
                    for (int i = 0; i < numEdges; i++) {
                        mxCell edge = (mxCell) cell.getEdgeAt(i);
                        mxCell cloneEdge = (mxCell) edge.clone();
                        cloneEdge.setId(edge.getId());

                        mxCell source = (mxCell) edge.getTerminal(true);
                        mxCell cloneSource = newCells.get(source.getId());
                        if (cloneSource == null && includeNeighbors) {
                            cloneSource = (mxCell) source.clone();
                            cloneSource.setId(source.getId());
                            baseCell.insert(cloneSource);
                            newCells.put(source.getId(), cloneSource);
                        }

                        mxCell target = (mxCell) edge.getTerminal(false);
                        mxCell cloneTarget = newCells.get(target.getId());
                        if (cloneTarget == null && includeNeighbors) {
                            cloneTarget = (mxCell) target.clone();
                            cloneTarget.setId(target.getId());
                            baseCell.insert(cloneTarget);
                            newCells.put(target.getId(), cloneTarget);
                        }
                        if (cloneSource != null && cloneTarget != null) {
                            baseCell.insert(cloneEdge);
                            cloneSource.insertEdge(cloneEdge, true);
                            cloneTarget.insertEdge(cloneEdge, false);
                        }
                    }
                }
            }
        } catch (CloneNotSupportedException e) {
            // shoudn't happen
            e.printStackTrace();
        }

        mxIGraphModel newModel = new mxGraphModel(rootCell);
        TopGraph newGraph = createGraph();
        newGraph.setModel(newModel);
        cloneGraphNodes(newGraph, indicator);
        // dump("", rootCell);

        return newGraph;
    }

    protected void dump(String prefix, mxCell cell) {
        System.out.println(prefix + cell.getId() + " edges="
                + cell.getEdgeCount() + " " + cell.getSource() + "->"
                + cell.getTarget());
        Object val = cell.getValue();
        if (val != null) {
            ((GraphNode) val).dump(System.out);
        }
        int count = cell.getChildCount();
        for (int i = 0; i < count; i++) {
            dump(prefix + "  ", (mxCell) cell.getChildAt(i));
        }
    }

    protected String getModelName(int modelId) {
        return "Model " + modelId;
    }

    protected void cloneGraphNodes(TopGraph graph, ICancelIndicator indicator) {
        Object[] children =
                mxGraphModel.getChildCells(graph.getModel(),
                        graph.getDefaultParent(), true, false);
        // clone graph node
        for (Object child : children) {
            if (indicator != null && indicator.isCancelled()) {
                throw new CancellationException();
            }
            mxCell cell = (mxCell) child;
            GraphNode gNode = (GraphNode) (cell.getValue());
            cell.setValue(gNode.copy());
        }

        // restore graph node neighbors
        for (Object child : children) {
            if (indicator != null && indicator.isCancelled()) {
                throw new CancellationException();
            }
            mxCell cell = (mxCell) child;
            GraphNode gNode = (GraphNode) (cell.getValue());
            TreeMap<GraphNode, TreeMap<Integer, Integer>> middleNodes =
                    gNode.getMiddleNodes();
            if (middleNodes != null) {
                TreeMap<GraphNode, TreeMap<Integer, Integer>> newMiddleNodes =
                        updateNodeMap(middleNodes, graph);
                gNode.setMiddleNodes(newMiddleNodes);
            }

            TreeMap<GraphNode, TreeMap<Integer, Integer>> endNodes =
                    gNode.getEndNodes();
            if (endNodes != null) {
                TreeMap<GraphNode, TreeMap<Integer, Integer>> newEndNodes =
                        updateNodeMap(endNodes, graph);
                gNode.setEndNodes(newEndNodes);
            }
        }
    }

    protected TreeMap<GraphNode, TreeMap<Integer, Integer>> updateNodeMap(
            TreeMap<GraphNode, TreeMap<Integer, Integer>> target, TopGraph graph) {
        TreeMap<GraphNode, TreeMap<Integer, Integer>> res =
                new TreeMap<GraphNode, TreeMap<Integer, Integer>>();
        for (GraphNode node : target.keySet()) {
            mxCell cell = graph.getVertex(node.getLid());
            if (cell != null) {
                GraphNode newNode = (GraphNode) cell.getValue();
                res.put(newNode, target.get(node));
            }
        }
        return res;
    }

    class GraphView extends mxGraphView {

        /**
         * Description:
         * 
         * @param graph
         */
        public GraphView(mxGraph graph) {
            super(graph);
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.mxgraph.view.mxGraphView#createState(java.lang.Object)
         */
        @Override
        public mxCellState createState(Object cell) {
            Map<String, Object> style = null;
            synchronized (this) {
                String cellId = ((mxCell) cell).getId();
                style = highlightedEdges.get(cellId);
                // if (style == null) {
                // style = markedEdges.get(cellId);
                // }
            }
            if (style != null) {
                return new mxCellState(this, cell, style);
            }
            return super.createState(cell);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.mxgraph.view.mxGraphView#updateVertexLabelOffset(com.mxgraph.
         * view.mxCellState)
         */
        @Override
        public void updateVertexLabelOffset(mxCellState state) {
            // if (vertexScaleThreshold>0 && scale>vertexScaleThreshold) {
            // double w = state.getWidth();
            // double h = state.getHeight();
            // double newScale = scale; //Math.sqrt(scale);
            // state.setWidth(w/newScale);
            // state.setHeight(h/newScale);
            // }
            super.updateVertexLabelOffset(state);
        }

    }

    /**
     * Special selection model that only allows the following selections: 1)
     * single vertex ==> Node selection 2) multiple vertices ==> Route selection
     * 3) multiple edges ==> Link selection It forbids combination of vertices
     * and links. When vertices and edges appear together, all edges are
     * ignored.
     */
    class TopSelectionModel extends mxGraphSelectionModel {
        private boolean isVertexSelection;

        /**
         * Description:
         * 
         * @param graph
         */
        public TopSelectionModel(mxGraph graph) {
            super(graph);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.mxgraph.view.mxGraphSelectionModel#setCells(java.lang.Object[])
         */
        @Override
        public void setCells(Object[] cells) {
            if (cells != null) {
                Object[][] splittedCells = split(cells);
                Object[] vertices = splittedCells[0];
                Object[] edges = splittedCells[1];
                if (vertices.length > 0) {
                    isVertexSelection = true;
                    super.setCells(vertices);
                } else {
                    isVertexSelection = false;
                    super.setCells(edges);
                }
            } else {
                clear();
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.mxgraph.view.mxGraphSelectionModel#addCells(java.lang.Object[])
         */
        @Override
        public void addCells(Object[] cells) {
            if (cells == null) {
                return;
            }

            Collection<Object> remove = null;
            List<Object> tmp = new ArrayList<Object>(cells.length);

            if (singleSelection) {
                remove = this.cells;
                cells = new Object[] { getFirstSelectableCell(cells) };
            } else {
                Object[][] splittedCells = split(cells);
                Object[] vertices = splittedCells[0];
                Object[] edges = splittedCells[1];
                if (vertices.length > 0) {
                    if (!isVertexSelection) {
                        remove = this.cells;
                        isVertexSelection = true;
                    }
                    cells = vertices;
                } else {
                    if (isVertexSelection) {
                        remove = this.cells;
                        isVertexSelection = false;
                    }
                    cells = edges;
                }
            }

            for (int i = 0; i < cells.length; i++) {
                if (!isSelected(cells[i]) && graph.isCellSelectable(cells[i])) {
                    tmp.add(cells[i]);
                }
            }
            changeSelection(tmp, remove);
        }

        /**
         * 
         * Description: Split cells into vertices and edges
         * 
         * @param cells
         * @return an array of [vertices array, edges array]
         */
        protected Object[][] split(Object[] cells) {
            List<Object> vertices = new ArrayList<Object>();
            List<Object> edges = new ArrayList<Object>();
            mxIGraphModel model = graph.getModel();
            for (Object cell : cells) {
                if (model.isVertex(cell)) {
                    vertices.add(cell);
                } else {
                    edges.add(cell);
                }
            }
            return new Object[][] { vertices.toArray(), edges.toArray() };
        }

    }

    public static TopGraph createGraph() {
        TopGraph graph = new TopGraph();
        graph.setAllowDanglingEdges(false);
        graph.setCellsResizable(false);
        graph.setCellsEditable(false);
        graph.setCellsCloneable(false);
        graph.setEdgeLabelsMovable(false);
        graph.setCellsDisconnectable(false);
        graph.setSplitEnabled(false);
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_NOLABEL, "1");
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_MOVABLE, "0");
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_ENDARROW, "0");
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_STARTARROW, "0");
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_STROKECOLOR, "gray"/* "#00AA00" */);
        graph.getStylesheet().getDefaultEdgeStyle()
                .put(mxConstants.STYLE_OPACITY, "25");
        graph.getStylesheet().getDefaultVertexStyle()
                .put(mxConstants.STYLE_NOLABEL, "1");
        return graph;
    }

    public static String getPreferedId(mxCell cell) {
        if (cell.isVertex()) {
            int lid = ((GraphNode) cell.getValue()).getLid();
            return getVertexId(lid);
        } else if (cell.isEdge()) {
            mxCell source = (mxCell) cell.getSource();
            mxCell target = (mxCell) cell.getTarget();
            return getEdgeId(source, target);
        } else {
            return cell.getId();
        }
    }

    public static String getVertexId(int lid) {
        return TOP + "_" + Integer.toString(lid);
    }

    public static String getEdgeId(mxCell source, mxCell target) {
        int sourceLid = ((GraphNode) source.getValue()).getLid();
        int targetLid = ((GraphNode) target.getValue()).getLid();
        return getEdgeId(sourceLid, targetLid);
    }

    public static String getEdgeId(int sourceLid, int targetLid) {
        return getVertexId(sourceLid) + ":" + getVertexId(targetLid);
    }
}
