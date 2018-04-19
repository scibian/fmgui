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
package com.intel.stl.api.subnet;

/**
 * Title:        NodeRecordBean
 * Description:  Node Record from SA populated by the connect manager.
 * 
 * @version 0.0
 */
import java.io.Serializable;

public class NodeRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private NodeInfoBean nodeInfo;

    private int lid;

    private String nodeDesc;

    // this should be set by DB
    private transient boolean active = true;

    public NodeRecordBean() {
        super();
    }

    public NodeRecordBean(NodeInfoBean nodeInfo, int lid, String nodeDesc) {
        super();
        this.nodeInfo = nodeInfo;
        this.lid = lid;
        this.nodeDesc = nodeDesc;
    }

    public NodeType getNodeType() {
        if (nodeInfo == null) {
            return null;
        }
        return nodeInfo.getNodeTypeEnum();
    }

    public void setNodeType(NodeType nodeType) {
        if (nodeInfo != null) {
            nodeInfo.setNodeTypeEnum(nodeType);
        }
    }

    /**
     * @return the nodeInfo
     */
    public NodeInfoBean getNodeInfo() {
        return nodeInfo;
    }

    /**
     * @param nodeInfo
     *            the nodeInfo to set
     */
    public void setNodeInfo(NodeInfoBean nodeInfo) {
        this.nodeInfo = nodeInfo;
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
     * @return the nodeDesc
     */
    public String getNodeDesc() {
        return nodeDesc;
    }

    /**
     * @param nodeDesc
     *            the nodeDesc to set
     */
    public void setNodeDesc(String nodeDesc) {
        this.nodeDesc = nodeDesc;
    }

    /**
     * @return the state
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param state
     *            the state to set
     */
    public void setActive(boolean state) {
        this.active = state;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NodeRecordBean [nodeInfo=" + nodeInfo + ", lid=" + lid
                + ", nodeDesc=" + nodeDesc + ", active=" + active + "]";
    }
}
