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
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.ImageIcon;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.monitor.ConnectivityTableController;
import com.intel.stl.ui.monitor.view.ConnectivitySubpageView;
import com.intel.stl.ui.network.view.ResourceLinkView;

/**
 * Controller for the Link subpage on the Topology page
 */
public class ResourceLinkPage implements IPageController {
    private final static Logger log =
            LoggerFactory.getLogger(ResourceLinkPage.class);

    private final static byte NUM_TAB_CHARS = 12;

    private final ResourceLinkView view;

    private final ConnectivityTableController tableController;

    private ISubnetApi subnetApi;

    private String pageName = STLConstants.K0013_LINKS.getValue();

    private String pageDescription =
            new String(STLConstants.K1023_LINK_RESOURCE_DESCRIPTION.getValue());

    public ResourceLinkPage(ConnectivityTableModel tableModel,
            ConnectivitySubpageView tableView, ResourceLinkView view) {
        this.view = view;
        this.view.addTableView(tableView);
        tableController = new ConnectivityTableController(tableModel,
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
        subnetApi = context.getSubnetApi();
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
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return pageName;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return pageDescription;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return view;
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

    private String createNodeName(String name) {
        return Util.truncateString(name, 1, NUM_TAB_CHARS);
    }

    private String createToolTip(String fromName, int fromLid, String toName,
            int toLid) {
        String toolTip = new String("");

        NodeInfoBean fromNode;
        try {
            fromNode = subnetApi.getNode(fromLid).getNodeInfo();
            NodeInfoBean toNode = subnetApi.getNode(toLid).getNodeInfo();

            String fromGuid = String.format("%#020x", fromNode.getNodeGUID());
            String toGuid = String.format("%#020x", toNode.getNodeGUID());
            toolTip = "<html>" + fromName + " GUID=" + fromGuid + "  LID="
                    + fromLid + "<br>" + toName + " GUID=" + toGuid + "  LID="
                    + toLid + "</html>";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toolTip;
    }

    private String createToolTip(String fromName, int fromLid, int fromPort,
            String toName, int toLid, int toPort) {
        String toolTip = new String("");

        NodeInfoBean fromNode;
        try {
            fromNode = subnetApi.getNode(fromLid).getNodeInfo();
            NodeInfoBean toNode = subnetApi.getNode(toLid).getNodeInfo();

            String fromGuid = String.format("%#020x", fromNode.getNodeGUID());
            String toGuid = String.format("%#020x", toNode.getNodeGUID());
            toolTip = "<html>" + fromName + " GUID=" + fromGuid + "  LID="
                    + fromLid + " PORT=" + fromPort + "<br>" + toName + " GUID="
                    + toGuid + "  LID=" + toLid + " PORT=" + toPort + "</html>";
        } catch (Exception e) {
            e.printStackTrace();
        }

        return toolTip;
    }

    protected String getNodeName(int lid) {
        String nodeName = new String("");

        try {
            nodeName = subnetApi.getNode(lid).getNodeDesc();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return nodeName;
    }

    public void showLink(GraphEdge edge, String vfName) {
        if (edge.getLinks().size() == 1) {

            NodeRecordBean fromNodeBean = null;
            NodeRecordBean toNodeBean = null;
            try {
                fromNodeBean = subnetApi.getNode(edge.getFromLid());
                toNodeBean = subnetApi.getNode(edge.getToLid());
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (fromNodeBean == null || toNodeBean == null) {
                log.warn("Couldn't fond nodes for link " + edge);
                tableController.clear();
                return;
            }

            // Create abbreviations for the path end nodes to put on the page
            // tab
            Entry<Integer, Integer> link =
                    edge.getLinks().entrySet().iterator().next();
            String fromName = createNodeName(fromNodeBean.getNodeDesc()) + " : "
                    + link.getKey();
            String toName = createNodeName(toNodeBean.getNodeDesc()) + " : "
                    + link.getValue();
            setName(fromName + "," + toName);

            // Create the tool-tip description for the path end nodes with node
            // name, GUID, and LID
            String description = createToolTip(fromName, edge.getFromLid(),
                    link.getKey(), toName, edge.getToLid(), link.getValue());
            setDescription(description);

            // Create the port list
            List<Short> portList = new ArrayList<Short>();
            Map<Integer, Integer> links = edge.getLinks();
            Iterator<Entry<Integer, Integer>> it = links.entrySet().iterator();
            while (it.hasNext()) {
                Integer portNum = it.next().getKey();
                portList.add(portNum.shortValue());
            }

            // Convert list of Short to array of short
            Short[] pShorts = portList.toArray(new Short[portList.size()]);
            short[] ports = new short[pShorts.length];
            for (int i = 0; i < pShorts.length; i++) {
                ports[i] = pShorts[i].shortValue();
            }

            // Show the data
            tableController.showConnectivity(edge.getFromLid(), vfName, null,
                    ports);
        } else {
            throw new IllegalArgumentException(
                    "Link has more than one paire of ports!");
        }
    } // showLink

    public void showPath(GraphEdge trace, List<GraphEdge> links,
            String vfName) {
        LinkedHashMap<GraphEdge, Short> portMap =
                new LinkedHashMap<GraphEdge, Short>();

        // Populate the map with the lids/ports for this path
        for (GraphEdge link : links) {
            Iterator<Entry<Integer, Integer>> it =
                    link.getLinks().entrySet().iterator();
            while (it.hasNext()) {
                Entry<Integer, Integer> entry = it.next();
                short fromPortNum = entry.getKey().shortValue();
                // connectivity table requires local port number for HFIs
                try {
                    NodeRecordBean nrb = subnetApi.getNode(link.getFromLid());
                    if (nrb.getNodeType() == NodeType.HFI) {
                        fromPortNum = nrb.getNodeInfo().getLocalPortNum();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                portMap.put(link, fromPortNum);
            } // while
        } // for

        // Create abbreviations for the path end nodes to put on the page tab
        String fromName = createNodeName(getNodeName(trace.getFromLid()));
        String toName = createNodeName(getNodeName(trace.getToLid()));
        setName(new String(fromName + "," + toName));

        // Create the tool-tip description for the path end nodes with node
        // name, GUID, and LID
        String description = createToolTip(fromName, trace.getFromLid(), toName,
                trace.getToLid());
        setDescription(description);

        // Update the table
        tableController.showPathConnectivity(portMap, vfName, null);
    }

    public void setName(String name) {
        pageName = name;
    }

    public void setDescription(String description) {
        pageDescription = description;
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
