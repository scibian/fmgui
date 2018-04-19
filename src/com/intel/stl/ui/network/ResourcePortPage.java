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

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.Vector;

import javax.swing.ImageIcon;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.monitor.ConnectivityTableController;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;
import com.intel.stl.ui.network.view.ResourcePortView;

/**
 * Controller for the Port subpage on the Topology page
 */
public class ResourcePortPage implements IResourceNodeSubpageController {

    private final ResourcePortView pageView;

    private ConnectivityTableController tableController;

    public ResourcePortPage(ResourcePortView view) {
        this.pageView = view;
    }

    public ResourcePortPage(ConnectivityTableModel tableModel,
            ConnectivitySubpageView tableView, ResourcePortView pageView) {

        this.pageView = pageView;
        this.pageView.addTableView(tableView);
        tableController =
                new ConnectivityTableController(tableModel,
                        tableView.getTable());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#setContext(com.intel.stl.ui.main
     * .Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        tableController.setContext(context, null);
        if (observer != null) {
            observer.onFinish();
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
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return STLConstants.K0415_CONNECTIVITY.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return STLConstants.K0416_CONNECTIVITY_DESCRIPTION.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return pageView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // TODO Auto-generated method stub

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
     * @see
     * com.intel.stl.ui.network.IResourceSubpageController#showNode(com.intel
     * .stl.ui.model.GraphNode)
     */
    @Override
    public void showNode(FVResourceNode source, GraphNode node) {
        String vfName = null;
        FVResourceNode group = source.getParent();
        if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
            vfName = group.getTitle();
        }
        // processNode(node, NodeType.getNodeType(node.getType()), vfName);
        processNode(source, NodeType.getNodeType(node.getType()), vfName);
    }

    protected void processNode(FVResourceNode node, NodeType nodeType,
            String vfName) {
        Vector<FVResourceNode> children = node.getChildren();
        if (children.size() >= 1) {
            short[] ports = null;
            if (nodeType == NodeType.SWITCH) {
                ports = new short[children.size() - 1];
                for (int i = 1; i < children.size(); i++) {
                    ports[i - 1] = (short) children.get(i).getId();
                }
            } else {
                ports = new short[children.size()];
                for (int i = 0; i < children.size(); i++) {
                    ports[i] = (short) children.get(i).getId();
                }
            }
            tableController.showConnectivity(node.getId(), vfName, null, ports);
        }
    }

    protected void processNode(GraphNode node, NodeType nodeType, String vfName) {
        // node.dump(System.out);
        TreeMap<GraphNode, TreeMap<Integer, Integer>> middleNodes =
                node.getMiddleNodes();
        List<Short> portList = new ArrayList<Short>();

        if (middleNodes != null && middleNodes.keySet().size() > 0) {
            // For each middle node, get the port numbers
            for (GraphNode gNode : node.getMiddleNodes().keySet()) {
                TreeMap<Integer, Integer> neighbor =
                        node.getMiddleNodes().get(gNode);

                for (Integer portNum : neighbor.keySet()) {
                    portList.add(portNum.shortValue());
                }
            }
        }

        // For each end node, get the port numbers if applicable
        if (node.getEndNodes() != null) {
            for (GraphNode gNode : node.getEndNodes().keySet()) {
                TreeMap<Integer, Integer> neighbor =
                        node.getEndNodes().get(gNode);

                for (Integer portNum : neighbor.keySet()) {
                    portList.add(portNum.shortValue());
                }
            }
        }

        // Convert list of Short to array of short
        Short[] pShorts = portList.toArray(new Short[portList.size()]);
        short[] ports = new short[pShorts.length];
        for (int i = 0; i < pShorts.length; i++) {
            ports[i] = pShorts[i].shortValue();

            // Increment the port number for HFIs
            if (nodeType == NodeType.HFI) {
                ports[i]++;
            }
        }

        tableController.showConnectivity(node.getLid(), vfName, null, ports);
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
