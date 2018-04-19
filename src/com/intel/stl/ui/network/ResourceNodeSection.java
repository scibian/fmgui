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

import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.UndoableJumpEvent;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.configuration.view.DevicePropertiesPanel;
import com.intel.stl.ui.event.NodesSelectedEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.model.ConnectivityTableColumns;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.DeviceProperties;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.monitor.CableInfoPopupController;
import com.intel.stl.ui.monitor.IPortSelectionListener;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.CableInfoPopupView;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;
import com.intel.stl.ui.network.view.ResourcePortView;
import com.intel.stl.ui.network.view.ResourceSubpageView;

import net.engio.mbassy.bus.MBassador;

/**
 * Controller for the subpages card on the Topology page
 */
public class ResourceNodeSection extends ResourceSection<ResourceSubpageView>
        implements IPortSelectionListener, IPageListener {

    /**
     * Subpages for the Topology page
     */
    private List<IResourceNodeSubpageController> mSubpages;

    private GraphNode lastNode;

    /**
     * Map of subpages
     */
    private EnumMap<TreeNodeType, List<IResourceNodeSubpageController>> pageMap;

    private final DeviceProperties model;

    private CableInfoPopupController cableInfoPopupController;

    private String previousSubpageName;

    private String currentSubpageName;

    private UndoHandler undoHandler;

    private final String origin = TopologyPage.NAME;

    /**
     * Description:
     *
     * @param view
     */
    public ResourceNodeSection(ResourceSubpageView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        this.view = view;
        view.setPageListener(this);
        model = new DeviceProperties();
        initSubpages();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseSectionController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getTopologyNode();
    }

    @Override
    public void setContext(Context pContext, IProgressObserver observer) {
        cableInfoPopupController.setContext(pContext, observer);
        if (pContext != null && pContext.getController() != null) {
            undoHandler = pContext.getController().getUndoHandler();
        }

        IProgressObserver[] subObservers =
                observer.createSubObservers(mSubpages.size());
        for (int i = 0; i < mSubpages.size(); i++) {
            mSubpages.get(i).setContext(pContext, subObservers[i]);
            subObservers[i].onFinish();
            if (observer.isCancelled()) {
                for (int j = 0; j <= i; j++) {
                    mSubpages.get(j).clear();
                }
                return;
            }
        }
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

    protected void showNode(FVResourceNode source, GraphNode node) {
        if (source == null) {
            return;
        }

        // Set the card title to the name of the node
        view.setTitle(source.getName(), source.getType().getIcon());

        if (lastNode != null && lastNode.equals(node)) {
            IResourceNodeSubpageController currentSubpage =
                    getCurrentSubpage(source.getType());
            if (currentSubpage != null) {
                currentSubpage.showNode(source, node);
            }
            return;
        }

        lastNode = node;
        // connectivitySubpageController.setLastNode(source);

        // Deregister tasks on all subpages
        for (IResourceNodeSubpageController page : mSubpages) {
            page.clear();
        }

        List<IResourceNodeSubpageController> subpages =
                getSubpagesByType(source.getType());
        if (subpages != null) {
            if (currentSubpageName == null) {
                previousSubpageName = null;
                currentSubpageName = view.getCurrentSubpage();
            }
            int curIndex = -1;
            for (int i = 0; i < subpages.size(); i++) {
                IResourceNodeSubpageController subpage = subpages.get(i);
                if (subpage.getName().equals(currentSubpageName)) {
                    curIndex = i;
                }
                subpage.showNode(source, node);
            }
            view.setTabs(subpages, curIndex);
        }
    }

    protected IResourceNodeSubpageController getCurrentSubpage(
            TreeNodeType type) {
        List<IResourceNodeSubpageController> subpages = getSubpagesByType(type);
        if (subpages != null) {
            String current = view.getCurrentSubpage();
            if (current != null) {
                for (int i = 0; i < subpages.size(); i++) {
                    IResourceNodeSubpageController subpage = subpages.get(i);
                    if (current.equals(subpage.getName())) {
                        return subpage;
                    }
                }
            } else {
                return subpages.get(0);
            }
        }
        return null;
    }

    protected void initSubpages() {

        // Table Initialization
        ConnectivityTableModel portTableModel = new ConnectivityTableModel();
        ConnectivitySubpageView portTableView = createPortView(portTableModel);
        CableInfoPopupView cableInfoPopupView =
                new CableInfoPopupView(portTableView);
        portTableView.setCableInfoPopupView(cableInfoPopupView);
        cableInfoPopupController =
                new CableInfoPopupController(cableInfoPopupView);
        cableInfoPopupView.setCableInfoListener(cableInfoPopupController);
        // connectivitySubpageController =
        // new ConnectivitySubpageController(portTableModel,
        // portTableView, eventBus);
        // portTableView.setPortSelectionListener(connectivitySubpageController);
        portTableView.setPortSelectionListener(this);

        // Create the views
        DevicePropertiesPanel switchNodeView = new DevicePropertiesPanel();
        DevicePropertiesPanel hfiNodeView = new DevicePropertiesPanel();
        ResourcePortView portView = new ResourcePortView();

        // Create the subpages
        IResourceNodeSubpageController switchNodePage =
                new ResourceNodePage(model, switchNodeView, eventBus);
        IResourceNodeSubpageController hfiNodePage =
                new ResourceNodePage(model, hfiNodeView, eventBus);
        IResourceNodeSubpageController portPage =
                new ResourcePortPage(portTableModel, portTableView, portView);
        mSubpages = Arrays.asList(hfiNodePage, switchNodePage, portPage);

        // Init TopologyComponentType and associated sub-pages
        pageMap =
                new EnumMap<TreeNodeType, List<IResourceNodeSubpageController>>(
                        TreeNodeType.class);
        pageMap.put(TreeNodeType.SWITCH,
                Arrays.asList(switchNodePage, portPage));
        pageMap.put(TreeNodeType.HFI, Arrays.asList(hfiNodePage, portPage));
    }

    protected List<IResourceNodeSubpageController> getSubpagesByType(
            TreeNodeType type) {
        return pageMap.get(type);
    }

    @Override
    public boolean canPageChange(String oldPage, String newPage) {
        return true;
    }

    @Override
    public synchronized void onPageChanged(String oldPage, String newPage) {
        if (undoHandler != null && !undoHandler.isInProgress()) {
            UndoableNodeSubpageSelection undoSel =
                    new UndoableNodeSubpageSelection(view, oldPage, newPage);
            undoHandler.addUndoAction(undoSel);
        }
        previousSubpageName = oldPage;
        currentSubpageName = newPage;
    }

    protected ConnectivitySubpageView createPortView(
            ConnectivityTableModel portTableModel) {

        ConnectivitySubpageView portTableView =
                new ConnectivitySubpageView(portTableModel) {

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
        return portTableView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.BaseSectionController#getSectionListener()
     */
    @Override
    protected ISectionListener getSectionListener() {
        return this;
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
            eventBus.publish(pse);
            if (undoHandler != null && !undoHandler.isInProgress()) {
                NodeType type = NodeType.getNodeType(lastNode.getType());
                UndoableJumpEvent undoSel =
                        new UndoableJumpEvent(eventBus, new NodesSelectedEvent(
                                lastNode.getLid(), type, this, origin), pse);
                undoHandler.addUndoAction(undoSel);
            }
        }
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
