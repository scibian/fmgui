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
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.engio.mbassy.bus.MBassador;

import org.jfree.util.Log;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UndoableJumpEvent;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.NodesSelectedEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.model.ConnectivityTableColumns;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.monitor.CableInfoPopupController;
import com.intel.stl.ui.monitor.IPortSelectionListener;
import com.intel.stl.ui.monitor.view.CableInfoPopupView;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;
import com.intel.stl.ui.network.view.ResourceLinkSubpageView;
import com.intel.stl.ui.network.view.ResourceLinkView;

/**
 * Controller for the JCardView to display tabbed pages when links are selected
 * on the topology graph
 */
public class ResourceLinkSection
        extends ResourceSection<ResourceLinkSubpageView>
        implements IPortSelectionListener, IPageListener {

    /**
     * Subpages for the Topology page
     */
    private final Map<GraphEdge, ResourceLinkPage> subpages =
            new LinkedHashMap<GraphEdge, ResourceLinkPage>();

    private Context context;

    private IProgressObserver observer;

    private List<GraphEdge> currentLinks;

    private Map<GraphEdge, List<GraphEdge>> currentTraces;

    private String previousSubpageName;

    private String currentSubpageName;

    private UndoHandler undoHandler;

    private final String origin = TopologyPage.NAME;

    /**
     * Description:
     *
     * @param view
     */
    public ResourceLinkSection(ResourceLinkSubpageView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        this.view = view;
        view.setPageListener(this);
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        this.context = context;
        if (context != null && context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }
        observer.onFinish();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.network.ResourceSection#setCurrentSubpage(java.lang.
     * String)
     */
    @Override
    public void setCurrentSubpage(String subpageName) {
        previousSubpageName = currentSubpageName;
        currentSubpageName = subpageName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ResourceSection#getPreviousSubpage()
     */
    @Override
    public String getPreviousSubpage() {
        return previousSubpageName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.network.ResourceSection#getCurrentSubpage()
     */
    @Override
    public String getCurrentSubpage() {
        return currentSubpageName;
    }

    protected void showLinks(List<GraphEdge> links, String vfName) {
        links = toPortLinks(links);
        if (links != null && links.equals(currentLinks)) {
            for (GraphEdge link : links) {
                ResourceLinkPage page = subpages.get(link);
                if (page != null) {
                    page.showLink(link, vfName);
                } else {
                    Log.warn("Cannot find page for " + link);
                }
            }
            return;
        }

        // Set the card title to the name of the node
        view.setTitle(STLConstants.K0013_LINKS.getValue());

        // Clear out the subpage list and page map
        clearSubpages();
        if (links == null || links.isEmpty()) {
            currentLinks = null;
            currentTraces = null;
            return;
        }

        // For each selected link, create a new link page and add
        // it to the subpage list
        for (GraphEdge link : links) {
            ConnectivityTableModel linkTableModel =
                    new ConnectivityTableModel();
            ConnectivitySubpageView linkTableView =
                    createLinkView(linkTableModel);
            CableInfoPopupView cableInfoPopupView =
                    new CableInfoPopupView(linkTableView);
            linkTableView.setCableInfoPopupView(cableInfoPopupView);
            CableInfoPopupController cableInfoPopupController =
                    new CableInfoPopupController(cableInfoPopupView);

            cableInfoPopupController.setContext(context, null);
            cableInfoPopupView.setCableInfoListener(cableInfoPopupController);
            linkTableView.setPortSelectionListener(this);

            ResourceLinkView linkView = new ResourceLinkView();
            ResourceLinkPage linkPage = new ResourceLinkPage(linkTableModel,
                    linkTableView, linkView);
            linkPage.setContext(context, null);
            linkPage.showLink(link, vfName);

            // Add subpage to list of subpages
            subpages.put(link, linkPage);
        }

        previousSubpageName = currentSubpageName;
        view.setTabs(subpages.values().toArray(new ResourceLinkPage[0]),
                currentSubpageName);
        currentSubpageName = view.getCurrentSubpage();
        currentLinks = links;
        currentTraces = null;

        setHelpID(HelpAction.getInstance().getLinks());
    } // showLinks

    protected List<GraphEdge> toPortLinks(List<GraphEdge> edges) {
        List<GraphEdge> res = new ArrayList<GraphEdge>();
        for (GraphEdge edge : edges) {
            Map<Integer, Integer> links = edge.getLinks();
            if (links.size() == 1) {
                res.add(edge);
            } else if (links.size() > 1) {
                for (Entry<Integer, Integer> link : links.entrySet()) {
                    res.add(new GraphEdge(edge.getFromLid(), edge.getFromType(),
                            edge.getToLid(), edge.getToType(),
                            Collections.singletonMap(link.getKey(),
                                    link.getValue())));
                }
            }
        }
        return res;
    }

    protected void showPath(final Map<GraphEdge, List<GraphEdge>> traceMap,
            String vfName) {
        if (traceMap != null && traceMap.equals(currentTraces)) {
            for (GraphEdge link : traceMap.keySet()) {
                subpages.get(link).showPath(link, traceMap.get(link), vfName);
            }
            return;
        }

        // Set the card title to the name of the node
        view.setTitle(STLConstants.K1028_ROUTE_RESOURCE.getValue());

        // Clear the subpage list
        clearSubpages();
        if (traceMap == null || traceMap.isEmpty()) {
            currentTraces = traceMap;
            currentLinks = null;
            return;
        }

        // For each path, create one new path subpage and add it to the list
        Iterator<Entry<GraphEdge, List<GraphEdge>>> it =
                traceMap.entrySet().iterator();
        while (it.hasNext()) {
            Entry<GraphEdge, List<GraphEdge>> entry = it.next();

            ConnectivityTableModel pathTableModel =
                    new ConnectivityTableModel();
            ConnectivitySubpageView pathTableView =
                    createPathView(pathTableModel);
            CableInfoPopupView cableInfoPopupView =
                    new CableInfoPopupView(pathTableView);
            pathTableView.setCableInfoPopupView(cableInfoPopupView);
            CableInfoPopupController cableInfoPopupController =
                    new CableInfoPopupController(cableInfoPopupView);

            cableInfoPopupController.setContext(context, null);
            cableInfoPopupView.setCableInfoListener(cableInfoPopupController);
            pathTableView.setPortSelectionListener(this);

            ResourceLinkView pathView = new ResourceLinkView();
            ResourceLinkPage pathPage = new ResourceLinkPage(pathTableModel,
                    pathTableView, pathView);

            // Page needs context before table update
            pathPage.setContext(context, null);

            // Update the table with the new page info
            pathPage.showPath(entry.getKey(), entry.getValue(), vfName);

            // Add this pathPage to the list of subpages
            subpages.put(entry.getKey(), pathPage);
        } // while

        previousSubpageName = currentSubpageName;
        // Show all the pages on the tabbed pane
        view.setTabs(subpages.values().toArray(new ResourceLinkPage[0]),
                currentSubpageName);
        currentSubpageName = view.getCurrentSubpage();

        currentTraces = traceMap;
        currentLinks = null;

        setHelpID(HelpAction.getInstance().getRoutes());
    } // showPath

    protected GraphNode findNode(int lid, GraphNode node) {
        boolean found = false;
        GraphNode targetNode = null;

        Iterator<GraphNode> it = node.getMiddleNeighbor().iterator();
        while ((!found) && (it.hasNext())) {
            GraphNode neighbor = it.next();
            if (neighbor.getLid() == lid) {
                found = true;
                targetNode = neighbor;
            }
        }

        return targetNode;
    }

    @Override
    public ISectionListener getSectionListener() {
        return this;
    }

    @Override
    public boolean canPageChange(String oldPage, String newPage) {
        return true;
    }

    @Override
    public synchronized void onPageChanged(String oldPageId, String newPageId) {
        if (undoHandler != null && !undoHandler.isInProgress()) {
            UndoableLinkSubpageSelection undoSel =
                    new UndoableLinkSubpageSelection(view, oldPageId,
                            newPageId);
            undoHandler.addUndoAction(undoSel);
        }
        previousSubpageName = oldPageId;
        currentSubpageName = newPageId;
    }

    protected ConnectivitySubpageView createLinkView(
            ConnectivityTableModel linkTableModel) {

        ConnectivitySubpageView linkTableView =
                new ConnectivitySubpageView(linkTableModel) {

                    private static final long serialVersionUID =
                            5930204470646720711L;

                    @Override
                    protected void filterColumns() {
                        ConnectivityTableColumns[] toShow =
                                new ConnectivityTableColumns[] {

                                        // Show these columns
                                        ConnectivityTableColumns.NODE_NAME,
                                        ConnectivityTableColumns.PORT_NUMBER,
                                        ConnectivityTableColumns.CABLE_INFO,
                                        ConnectivityTableColumns.LINK_STATE,
                                        ConnectivityTableColumns.PHYSICAL_LINK_STATE,
                                        ConnectivityTableColumns.LINK_QUALITY,
                                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH,
                                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH_DG_TX,
                                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH_DG_RX,
                                        ConnectivityTableColumns.ACTIVE_LINK_SPEED,
                                        ConnectivityTableColumns.RX_DATA,
                                        ConnectivityTableColumns.TX_DATA,
                                        ConnectivityTableColumns.LINK_DOWNED, };

                        ConnectivityTableColumns[] all =
                                ConnectivityTableColumns.values();
                        boolean[] vis = new boolean[all.length];
                        for (ConnectivityTableColumns col : toShow) {
                            vis[col.getId()] = true;
                        }
                        for (int i = 0; i < vis.length; i++) {
                            mTable.getColumnExt(all[i].getTitle())
                                    .setVisible(vis[i]);
                        }
                    }
                };
        return linkTableView;
    }

    protected ConnectivitySubpageView createPathView(
            ConnectivityTableModel pathTableModel) {

        ConnectivitySubpageView pathTableView =
                new ConnectivitySubpageView(pathTableModel) {

                    private static final long serialVersionUID =
                            5930204470646720711L;

                    @Override
                    protected void filterColumns() {
                        ConnectivityTableColumns[] toHide =
                                new ConnectivityTableColumns[] {

                                        // Show these columns
                                        // ConnectivityTableColumns.DEVICE_NAME,
                                        // ConnectivityTableColumns.PORT_NUMBER,
                                        // ConnectivityTableColumns.LINK_STATE,
                                        // ConnectivityTableColumns.ACTIVE_LINK_SPEED,
                                        // ConnectivityTableColumns.SUPPORTED_LINK_SPEED,

                                        // Hide these columns
                                        ConnectivityTableColumns.LINK_ERROR_RECOVERIES,
                                        ConnectivityTableColumns.LINK_DOWNED,
                                        ConnectivityTableColumns.NUM_LANES_DOWN,
                                        ConnectivityTableColumns.NODE_GUID,
                                        ConnectivityTableColumns.PHYSICAL_LINK_STATE,
                                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH,
                                        ConnectivityTableColumns.ENABLED_LINK_WIDTH,
                                        ConnectivityTableColumns.SUPPORTED_LINK_WIDTH,
                                        ConnectivityTableColumns.ENABLED_LINK_SPEED,
                                        ConnectivityTableColumns.TX_PACKETS,
                                        ConnectivityTableColumns.RX_PACKETS,
                                        ConnectivityTableColumns.RX_ERRORS,
                                        ConnectivityTableColumns.RX_REMOTE_PHYSICAL_ERRRORS,
                                        ConnectivityTableColumns.TX_DISCARDS,
                                        ConnectivityTableColumns.RX_SWITCH_RELAY_ERRRORS,
                                        ConnectivityTableColumns.TX_CONSTRAINT,
                                        ConnectivityTableColumns.RX_CONSTRAINT,
                                        ConnectivityTableColumns.LOCAL_LINK_INTEGRITY,
                                        ConnectivityTableColumns.EXCESSIVE_BUFFER_OVERRUNS,
                                        ConnectivityTableColumns.RX_MC_PACKETS,
                                        ConnectivityTableColumns.RX_ERRORS,
                                        ConnectivityTableColumns.RX_CONSTRAINT,
                                        ConnectivityTableColumns.RX_FECN,
                                        ConnectivityTableColumns.RX_BECN,
                                        ConnectivityTableColumns.RX_BUBBLE,
                                        ConnectivityTableColumns.TX_MC_PACKETS,
                                        ConnectivityTableColumns.TX_WAIT,
                                        ConnectivityTableColumns.TX_TIME_CONG,
                                        ConnectivityTableColumns.TX_WASTED_BW,
                                        ConnectivityTableColumns.TX_WAIT_DATA,
                                        ConnectivityTableColumns.LOCAL_LINK_INTEGRITY,
                                        ConnectivityTableColumns.MARK_FECN,
                                        ConnectivityTableColumns.LINK_ERROR_RECOVERIES,
                                        ConnectivityTableColumns.UNCORRECTABLE_ERRORS,
                                        ConnectivityTableColumns.SW_PORT_CONGESTION };

                        for (ConnectivityTableColumns col : toHide) {
                            mTable.getColumnExt(col.getTitle())
                                    .setVisible(false);
                        }
                    }
                };
        return pathTableView;
    }

    public void clearSubpages() {
        for (ResourceLinkPage subpage : subpages.values()) {
            subpage.clear();
            subpage.cleanup();
        }
        // Clear the subpages
        subpages.clear();
        currentLinks = null;
        currentTraces = null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.IPortSelectionListener#onPortSelection(int)
     */
    @Override
    public void onPortSelection(int rowIndex) {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.monitor.IPortSelectionListener#onJumpToPort(int,
     * short, java.lang.String)
     */
    @Override
    public void onJumpToPort(int lid, short portNum, String destination) {
        if (eventBus != null) {
            PortsSelectedEvent pse =
                    new PortsSelectedEvent(lid, portNum, this, destination);

            if (undoHandler != null && !undoHandler.isInProgress()) {
                UndoableJumpEvent undoSel = new UndoableJumpEvent(eventBus,
                        getOldSelectionEvent(), pse);
                undoHandler.addUndoAction(undoSel);
            }

            eventBus.publish(pse);
        }
    }

    protected JumpToEvent getOldSelectionEvent() {
        if (currentLinks != null) {
            PortsSelectedEvent event = new PortsSelectedEvent(this, origin);
            for (GraphEdge link : currentLinks) {
                event.addPort(link.getFromLid(), link.getLinks().keySet()
                        .iterator().next().shortValue());
            }
            return event;
        }

        if (currentTraces != null) {
            NodesSelectedEvent event = new NodesSelectedEvent(this, origin);
            Map<Integer, Byte> nodes = new HashMap<Integer, Byte>();
            for (GraphEdge source : currentTraces.keySet()) {
                if (!nodes.containsKey(source.getFromLid())) {
                    nodes.put(source.getFromLid(), source.getFromType());
                }
                if (!nodes.containsKey(source.getToLid())) {
                    nodes.put(source.getToLid(), source.getToType());
                }
            }
            for (Entry<Integer, Byte> node : nodes.entrySet()) {
                event.addNode(node.getKey(),
                        NodeType.getNodeType(node.getValue()));
            }
            return event;
        }

        // shouldn't happen
        throw new RuntimeException(
                "Couldn't create JumpToEvent because no links or traces");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.ISectionController#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return null;
    }

}
