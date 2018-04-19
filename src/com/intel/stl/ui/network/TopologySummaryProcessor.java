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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.LinkSpeedMask;
import com.intel.stl.api.configuration.LinkWidthMask;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.model.GraphNode;
import com.intel.stl.ui.model.NodeTypeViz;
import com.intel.stl.ui.model.PropertyItem;
import com.intel.stl.ui.model.PropertySet;
import com.intel.stl.ui.model.SimplePropertyCategory;
import com.intel.stl.ui.model.SimplePropertyGroup;
import com.intel.stl.ui.model.SimplePropertyKey;
import com.intel.stl.ui.network.TopologyTier.Quality;
import com.mxgraph.model.mxCell;
import com.mxgraph.model.mxGraphModel;

public class TopologySummaryProcessor {
    private final Logger log =
            LoggerFactory.getLogger(TopologySummaryProcessor.class);

    private final String subnetSumName;

    private final String topSumName;

    private final ISubnetApi subnetApi;

    private final TopologyTreeModel topArch;

    /**
     * The full graph holds the whole subnet, so it's possible for us to figure
     * out external ports
     */
    private final TopGraph refGraph;

    /**
     * current graph
     */
    private final TopGraph graph;

    private final ICancelIndicator cancelIndicator;

    /**
     * Description:
     *
     * @param name
     * @param topArch
     * @param cancelIndicator
     */
    public TopologySummaryProcessor(String subnetSumName, String topSumName,
            TopologyTreeModel topArch, TopGraph graph, TopGraph fullGraph,
            ISubnetApi subnetApi, ICancelIndicator cancelIndicator) {
        super();
        this.subnetSumName = subnetSumName;
        this.topSumName = topSumName;
        this.topArch = topArch;
        this.subnetApi = subnetApi;
        this.graph = graph;
        this.refGraph = fullGraph;
        this.cancelIndicator = cancelIndicator;
    }

    public PropertySet<SimplePropertyGroup> populate() {
        PropertySet<SimplePropertyGroup> model =
                new PropertySet<SimplePropertyGroup>();

        TopologyTier[] tiers = createTiers();
        SimplePropertyGroup topGroup = new SimplePropertyGroup(topSumName);
        for (TopologyTier tier : tiers) {
            if (!cancelIndicator.isCancelled()) {
                SimplePropertyCategory category =
                        new SimplePropertyCategory(tier.getName(), null);
                SimplePropertyKey key = new SimplePropertyKey(tier.getName());
                PropertyItem<SimplePropertyKey> item =
                        new PropertyItem<SimplePropertyKey>(key, tier);
                category.addItem(item);
                topGroup.addPropertyCategory(category);
            }
        }

        int numSwitches = 0;
        int numHFIs = 0;
        int numActiveSwitchPorts = 0;
        int numOtherPorts = 0;
        Object[] allVertices = graph.getVertices();
        for (Object vertex : allVertices) {
            String id = ((mxCell) vertex).getId();
            mxCell refCell =
                    (mxCell) ((mxGraphModel) refGraph.getModel()).getCell(id);
            GraphNode refNode = (GraphNode) refCell.getValue();
            if (refNode.getType() == NodeType.SWITCH.getId()) {
                numSwitches += 1;
                int activePorts = refNode.getActivePorts();
                // count switch zero port
                numActiveSwitchPorts += activePorts + 1;
                numOtherPorts += refNode.getNumPorts() - activePorts;
            } else if (refNode.getType() == NodeType.HFI.getId()) {
                numHFIs += 1;
            }
        }

        SimplePropertyGroup subnetGroup =
                new SimplePropertyGroup(subnetSumName);
        SimplePropertyCategory category = populateNodes(numSwitches, numHFIs);
        subnetGroup.addPropertyCategory(category);
        category = populateActivePorts(numActiveSwitchPorts, numHFIs);
        subnetGroup.addPropertyCategory(category);
        category = populateOtherPorts(numOtherPorts);
        subnetGroup.addPropertyCategory(category);

        model.addPropertyGroup(subnetGroup);

        if (numSwitches > 0) {
            model.addPropertyGroup(topGroup);
        }
        return model;
    }

    protected TopologyTier[] createTiers() {
        Map<Point, PortRecordBean> portMap = getPortMap();
        List<List<Integer>> ranks = topArch.getRanks();
        TopologyTier[] tiers = new TopologyTier[ranks.size()];
        for (int i = 0; i < tiers.length; i++) {
            tiers[i] = new TopologyTier(i);
            List<Integer> lids = ranks.get(i);
            tiers[i].setNumSwitches(lids.size());
            int numHFIs = 0;
            int totalPorts = 0;
            Quality upQuality = new Quality();
            Quality downQuality = new Quality();
            for (int lid : lids) {
                if (!cancelIndicator.isCancelled()) {

                    GraphNode node =
                            (GraphNode) refGraph.getVertex(lid).getValue();
                    totalPorts += node.getNumPorts();
                    Set<GraphNode> neighbors = node.getMiddleNeighbor();
                    for (GraphNode nbr : neighbors) {
                        Set<Integer> ports = node.getLinkPorts(nbr).keySet();

                        Quality quality = null;
                        if (node.getDepth() > nbr.getDepth()) {
                            quality = downQuality;
                        } else if (node.getDepth() < nbr.getDepth()) {
                            quality = upQuality;
                        } else {
                            // happens on irregular tree
                            log.warn("Mismatched nodes depth " + node + " "
                                    + nbr);
                            continue;
                        }
                        updateQuality(quality, node.getLid(), ports, portMap);
                    }
                    neighbors = node.getEndNeighbor();
                    for (GraphNode nbr : neighbors) {
                        Set<Integer> ports = node.getLinkPorts(nbr).keySet();
                        updateQuality(downQuality, node.getLid(), ports,
                                portMap);
                    }
                    numHFIs += neighbors.size();
                }
                tiers[i].setNumHFIs(numHFIs);
                tiers[i].setUpQuality(upQuality);
                tiers[i].setDownQuality(downQuality);
                tiers[i].setNumOtherPorts(totalPorts - upQuality.getTotalPorts()
                        - downQuality.getTotalPorts());
                // System.out.println(tiers[i]);
            }
        }
        return tiers;
    }

    protected void updateQuality(Quality quality, int lid, Set<Integer> ports,
            Map<Point, PortRecordBean> portMap) {
        quality.increaseTotalPorts(ports.size());
        for (Integer portNum : ports) {
            PortRecordBean port = portMap.get(new Point(lid, portNum));
            if (port != null) {
                if (!Utils.isExpectedSpeed(port.getPortInfo(),
                        LinkSpeedMask.STL_LINK_SPEED_25G)) {
                    quality.addSlowPort(lid, portNum);
                }
                if (!Utils.isExpectedWidthDowngrade(port.getPortInfo(),
                        LinkWidthMask.STL_LINK_WIDTH_4X)) {
                    quality.addDegPort(lid, portNum);
                }
            } else {
                // this shouldn't happen
                log.error("Couldn't find port lid=" + lid + " portNim="
                        + portNum);
            }
        }
    }

    protected Map<Point, PortRecordBean> getPortMap() {
        Map<Point, PortRecordBean> map = new HashMap<Point, PortRecordBean>();
        try {
            List<PortRecordBean> ports = subnetApi.getPorts();
            for (PortRecordBean port : ports) {
                map.put(new Point(port.getEndPortLID(), port.getPortNum()),
                        port);
            }
        } catch (SubnetDataNotFoundException e) {
            log.error("Couldn't create port map", e);
        }
        return map;
    }

    protected SimplePropertyCategory populateNodes(int numSwitches,
            int numHFIs) {
        SimplePropertyCategory category = new SimplePropertyCategory(
                STLConstants.K0014_ACTIVE_NODES.getValue(), null);
        category.setShowHeader(true);
        NodeTypeViz type = NodeTypeViz.SWITCH;
        PropertyItem<SimplePropertyKey> item =
                populateCountItem(type, numSwitches);
        category.addItem(item);

        type = NodeTypeViz.HFI;
        item = populateCountItem(type, numHFIs);
        category.addItem(item);
        return category;
    }

    protected SimplePropertyCategory populateActivePorts(int numSwitches,
            int numHFIs) {
        SimplePropertyCategory category = new SimplePropertyCategory(
                STLConstants.K0024_ACTIVE_PORTS.getValue(), null);
        category.setShowHeader(true);
        NodeTypeViz type = NodeTypeViz.SWITCH;
        PropertyItem<SimplePropertyKey> item =
                populateCountItem(type, numSwitches);
        category.addItem(item);

        type = NodeTypeViz.HFI;
        item = populateCountItem(type, numHFIs);
        category.addItem(item);
        return category;
    }

    protected SimplePropertyCategory populateOtherPorts(int others) {
        String countString = UIConstants.INTEGER.format(others);
        SimplePropertyCategory category = new SimplePropertyCategory(
                STLConstants.K2071_OTHER_PORTS.getValue(), countString);
        category.setShowHeader(true);
        return category;
    }

    protected PropertyItem<SimplePropertyKey> populateCountItem(
            NodeTypeViz type, Integer count) {
        SimplePropertyKey key = new SimplePropertyKey(type.getPluralName());
        String countString =
                count == null ? STLConstants.K0039_NOT_AVAILABLE.getValue()
                        : UIConstants.INTEGER.format(count);
        return new PropertyItem<SimplePropertyKey>(key, countString);
    }
}
