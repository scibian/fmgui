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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.model.GraphNode;
import com.mxgraph.model.mxCell;

/**
 * model stores tree structure information to help us do tree layout.
 */
public class TopologyTreeModel {
    private final static Logger log = LoggerFactory
            .getLogger(TopologyTreeModel.class);

    private final List<List<Integer>> ranks;

    /**
     * The max width across all ranks
     */
    private final int maxWidth;

    private final int numTotalNodes;

    private final List<Integer> unclassifiedNodes;

    public TopologyTreeModel(List<List<Integer>> ranks, int maxRankSize,
            List<Integer> unclassifiedNodes, int numTotalNodes) {
        this.ranks = Collections.unmodifiableList(ranks);
        this.maxWidth = maxRankSize;
        this.unclassifiedNodes = unclassifiedNodes;
        this.numTotalNodes = numTotalNodes;
    }

    /**
     * @return the nodeLevels
     */
    public List<List<Integer>> getRanks() {
        return ranks;
    }

    /**
     * @return the maxWidth
     */
    public int getMaxWidth() {
        return maxWidth;
    }

    public int getNumRanks() {
        return ranks.size();
    }

    /**
     * @return the numNodes
     */
    public int getNumTotalNodes() {
        return numTotalNodes;
    }

    /**
     * @return the isolatedNodes
     */
    public List<Integer> getUnclassifiedNodes() {
        return unclassifiedNodes;
    }

    public TopologyTreeModel filterBy(TopGraph graph) {
        Object[] allNodes = graph.getVertices();
        Set<Integer> allLids = new HashSet<Integer>();
        for (Object node : allNodes) {
            allLids.add(((GraphNode) ((mxCell) node).getValue()).getLid());
        }
        List<List<Integer>> newRanks = new ArrayList<List<Integer>>();
        int newMaxWidth = 0;
        for (List<Integer> rank : ranks) {
            List<Integer> newRank = new ArrayList<Integer>();
            for (Integer nodeId : rank) {
                mxCell cell = graph.getVertex(nodeId);
                if (cell != null) {
                    newRank.add(nodeId);
                    allLids.remove(nodeId);
                    Set<GraphNode> neighbors =
                            ((GraphNode) cell.getValue()).getEndNeighbor();
                    for (GraphNode nbr : neighbors) {
                        allLids.remove(nbr.getLid());
                    }
                }
            }
            newRanks.add(newRank);
            if (newRank.size() > newMaxWidth) {
                newMaxWidth = newRank.size();
            }
        }
        return new TopologyTreeModel(newRanks, newMaxWidth,
                new ArrayList<Integer>(allLids), allNodes.length);
    }

    public void dump(PrintStream out) {
        out.println(ranks.size() + " tiers");
        for (int i = 0; i < ranks.size(); i++) {
            out.println(i + " " + ranks.get(i));
        }
        out.println(unclassifiedNodes.size() + " unclassifiedNodes "
                + unclassifiedNodes);
    }
}
