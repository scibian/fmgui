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

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.awt.Component;
import java.util.Vector;

import javax.swing.ImageIcon;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UndoableJumpEvent;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.NodesSelectedEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.CableInfoPopupView;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;

import net.engio.mbassy.bus.MBassador;

/**
 * Controller for the Connectivity subpage
 */
public class ConnectivitySubpageController
        implements IPerfSubpageController, IPortSelectionListener {
    private UndoHandler undoHandler;

    private final String origin = PerformancePage.NAME;

    private final ConnectivityTableController tableController;

    private final ConnectivitySubpageView view;

    private final MBassador<IAppEvent> eventBus;

    private PerformanceTreeController parentController;

    private final CableInfoPopupController cableInfoPopupController;

    private final CableInfoPopupView cableInfoPopupView;

    public ConnectivitySubpageController(
            ConnectivityTableModel connectTableModel,
            ConnectivitySubpageView pSubpageView,
            MBassador<IAppEvent> eventBus) {

        tableController = new ConnectivityTableController(connectTableModel,
                pSubpageView.getTable());
        cableInfoPopupView = new CableInfoPopupView(pSubpageView);
        pSubpageView.setCableInfoPopupView(cableInfoPopupView);
        cableInfoPopupController =
                new CableInfoPopupController(cableInfoPopupView);
        cableInfoPopupView.setCableInfoListener(cableInfoPopupController);
        view = pSubpageView;
        view.setPortSelectionListener(this);
        this.eventBus = eventBus;
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        if (context != null) {
            tableController.setContext(context, observer);
            cableInfoPopupController.setContext(context, observer);
            if (context.getController() != null) {
                undoHandler = context.getController().getUndoHandler();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        tableController.refreshConnectivity(observer);
    }

    @Override
    public String getName() {
        return STLConstants.K0415_CONNECTIVITY.getValue();
    }

    @Override
    public String getDescription() {
        return STLConstants.K0416_CONNECTIVITY_DESCRIPTION.getValue();
    }

    @Override
    public Component getView() {
        return view;
    }

    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void cleanup() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showNode(FVResourceNode node, IProgressObserver observer) {
        switch (node.getType()) {
            case SWITCH:
                processSwitch(node, observer);
                break;

            case HFI:
                processHFI(node, observer);
                break;
            case ACTIVE_PORT:
                FVResourceNode parent = node.getParent();
                String vfName = null;
                FVResourceNode group = parent.getParent();
                if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
                    vfName = group.getTitle();
                }
                tableController.showConnectivity(node.getParent().getId(),
                        vfName, observer, (short) node.getId());
                break;

            case INACTIVE_PORT:
                break;
            default:
                break;
        } // switch
    }

    @Override
    public void setParentController(
            PerformanceTreeController parentController) {
        this.parentController = parentController;
    }

    protected void processSwitch(FVResourceNode node,
            IProgressObserver observer) {
        Vector<FVResourceNode> children = node.getChildren();
        if (children.size() > 1) {
            String vfName = null;
            FVResourceNode group = node.getParent();
            if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
                vfName = group.getTitle();
            }
            short[] ports = new short[children.size() - 1];
            for (int i = 1; i < children.size(); i++) {
                ports[i - 1] = (short) children.get(i).getId();
            }
            tableController.showConnectivity(node.getId(), vfName, observer,
                    ports);
        } else {
            observer.onFinish();
        }
    }

    protected void processHFI(FVResourceNode node, IProgressObserver observer) {
        Vector<FVResourceNode> children = node.getChildren();
        if (children.size() > 0) {
            String vfName = null;
            FVResourceNode group = node.getParent();
            if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
                vfName = group.getTitle();
            }
            short[] ports = new short[children.size()];
            for (int i = 0; i < children.size(); i++) {
                // set local port number
                ports[i] = (short) children.get(i).getId();
            }
            tableController.showConnectivity(node.getId(), vfName, observer,
                    ports);
        } else {
            observer.onFinish();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        tableController.clear();
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
     * java.lang.String)
     */
    @Override
    public void onJumpToPort(int lid, short portNum, String destination) {
        if (eventBus != null) {
            PortsSelectedEvent pse =
                    new PortsSelectedEvent(lid, portNum, this, destination);

            if (undoHandler != null && !undoHandler.isInProgress()) {
                JumpToEvent oldSel = null;
                FVResourceNode node = parentController.getCurrentNode();
                if (node.isNode()) {
                    NodeType type = TreeNodeType.getNodeType(node.getType());
                    oldSel = new NodesSelectedEvent(node.getId(), type, this,
                            origin);
                } else if (node.isPort()) {
                    oldSel = new PortsSelectedEvent(node.getParent().getId(),
                            (short) node.getId(), this, origin);
                } else {
                    // shouldn't happen
                    throw new RuntimeException("Unsupported node " + node);
                }
                UndoableJumpEvent undoSel =
                        new UndoableJumpEvent(eventBus, oldSel, pse);
                undoHandler.addUndoAction(undoSel);
            }

            eventBus.publish(pse);
        }
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }
}
