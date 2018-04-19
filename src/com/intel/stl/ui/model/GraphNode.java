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

package com.intel.stl.ui.model;

import java.io.PrintStream;
import java.util.Collections;
import java.util.Set;
import java.util.TreeMap;

import com.intel.stl.api.subnet.NodeType;

public class GraphNode extends GraphCell implements Comparable<GraphNode> {
    private static final long serialVersionUID = 1980137982718321748L;

    private int lid;

    private byte type;

    private int numPorts;

    private TreeMap<GraphNode, TreeMap<Integer, Integer>> middleNodes;

    private TreeMap<GraphNode, TreeMap<Integer, Integer>> endNodes;

    private boolean isCollapsed = true;

    private transient int depth = -1;

    /**
     * Description:
     * 
     */
    public GraphNode() {
        super();
    }

    /**
     * Description:
     * 
     * @param lid
     */
    public GraphNode(int lid) {
        super();
        this.lid = lid;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.GraphCell#isVertex()
     */
    @Override
    public boolean isVertex() {
        return true;
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @param lid
     *            the lid to set
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * @return the type
     */
    public byte getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(byte type) {
        this.type = type;
    }

    /**
     * @return the numPorts
     */
    public int getNumPorts() {
        return numPorts;
    }

    /**
     * @param numPorts
     *            the numPorts to set
     */
    public void setNumPorts(int numPorts) {
        this.numPorts = numPorts;
    }

    public int getActivePorts() {
        int sum = 0;
        if (middleNodes != null) {
            for (GraphNode node : middleNodes.keySet()) {
                sum += middleNodes.get(node).size();
            }
        }
        if (endNodes != null) {
            for (GraphNode node : endNodes.keySet()) {
                sum += endNodes.get(node).size();
            }
        }
        return sum;
    }

    public boolean isEndNode() {
        return type == NodeType.HFI.getId();
    }

    /**
     * @return the middleNodes
     */
    public TreeMap<GraphNode, TreeMap<Integer, Integer>> getMiddleNodes() {
        return middleNodes;
    }

    /**
     * @param middleNodes
     *            the middleNodes to set
     */
    public void setMiddleNodes(
            TreeMap<GraphNode, TreeMap<Integer, Integer>> middleNodes) {
        this.middleNodes = middleNodes;
    }

    /**
     * @return the endNodes
     */
    public TreeMap<GraphNode, TreeMap<Integer, Integer>> getEndNodes() {
        return endNodes;
    }

    /**
     * @param endNodes
     *            the endNodes to set
     */
    public void setEndNodes(
            TreeMap<GraphNode, TreeMap<Integer, Integer>> endNodes) {
        this.endNodes = endNodes;
    }

    public void addLink(GraphNode toNode, int fromPortNum, int toPortNum) {
        TreeMap<GraphNode, TreeMap<Integer, Integer>> neighbors;
        if (toNode.getType() == NodeType.HFI.getId()) {
            if (endNodes == null) {
                endNodes = new TreeMap<GraphNode, TreeMap<Integer, Integer>>();
            }
            neighbors = endNodes;
        } else {
            if (middleNodes == null) {
                middleNodes =
                        new TreeMap<GraphNode, TreeMap<Integer, Integer>>();
            }
            neighbors = middleNodes;
        }
        TreeMap<Integer, Integer> ports = neighbors.get(toNode);
        if (ports == null) {
            ports = new TreeMap<Integer, Integer>();
            neighbors.put(toNode, ports);
        }
        ports.put(fromPortNum, toPortNum);
    }

    public TreeMap<Integer, Integer> getLinkPorts(GraphNode toNode) {
        TreeMap<Integer, Integer> res = null;
        if (toNode.getType() == NodeType.HFI.getId() && endNodes != null) {
            res = endNodes.get(toNode);
        } else if (middleNodes != null) {
            res = middleNodes.get(toNode);
        }

        if (res == null) {
            res = new TreeMap<Integer, Integer>();
        }
        return res;
    }

    public GraphNode getNeighbor(int portNum) {
        if (middleNodes != null) {
            for (GraphNode node : middleNodes.keySet()) {
                TreeMap<Integer, Integer> portsMap = middleNodes.get(node);
                if (portsMap.containsKey(portNum)) {
                    return node;
                }
            }
        }

        if (endNodes != null) {
            for (GraphNode node : endNodes.keySet()) {
                TreeMap<Integer, Integer> portsMap = endNodes.get(node);
                if (portsMap.containsKey(portNum)) {
                    return node;
                }
            }
        }

        return null;
    }

    public Set<GraphNode> getMiddleNeighbor() {
        return middleNodes == null ? Collections.<GraphNode> emptySet()
                : middleNodes.keySet();
    }

    public Set<GraphNode> getEndNeighbor() {
        return endNodes == null ? Collections.<GraphNode> emptySet() : endNodes
                .keySet();
    }

    public boolean hasEndNodes() {
        return endNodes != null && !endNodes.isEmpty();
    }

    /**
     * @return the isCollapsed
     */
    public boolean isCollapsed() {
        return isCollapsed;
    }

    /**
     * @param isCollapsed
     *            the isCollapsed to set
     */
    public void setCollapsed(boolean isCollapsed) {
        if (hasEndNodes()) {
            this.isCollapsed = isCollapsed;
        }
    }

    /**
     * @return the depth
     */
    public int getDepth() {
        return depth;
    }

    /**
     * @param depth
     *            the depth to set
     */
    public void setDepth(int depth) {
        if (depth > this.depth) {
            this.depth = depth;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(GraphNode o) {
        if (o == null) {
            return 1;
        }

        return lid > o.lid ? 1 : (lid < o.lid ? -1 : 0);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + lid;
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GraphNode other = (GraphNode) obj;
        if (lid != other.lid) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return name;
    }

    public GraphNode copy() {
        GraphNode copy = new GraphNode(lid);
        copy.name = name;
        copy.type = type;
        copy.numPorts = numPorts;
        copy.isCollapsed = isCollapsed;
        copy.depth = depth;
        copy.endNodes = endNodes;
        copy.middleNodes = middleNodes;
        return copy;
    }

    public void dump(PrintStream out) {
        out.println("GraphNode " + lid + " " + name + " "
                + NodeType.getNodeType(type) + " depth:" + depth);
        if (middleNodes != null) {
            out.println("  MiddleNode Neighbor");
            for (GraphNode node : middleNodes.keySet()) {
                out.println("    " + node.lid + " " + node.name + " "
                        + node.getType() + " " + middleNodes.get(node));
            }
        }
        if (endNodes != null) {
            out.println("  EndNode Neighbor");
            for (GraphNode node : endNodes.keySet()) {
                out.println("    " + node.lid + " " + node.name + " "
                        + node.getType() + " " + endNodes.get(node));
            }
        }
    }
}
