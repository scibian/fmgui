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

package com.intel.stl.api.notice;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.intel.stl.api.subnet.NodeRecordBean;

public class NoticeWrapper {
    private final NoticeBean notice;

    // the node described in this notice
    private NodeRecordBean node;

    private final TrapType trapType;

    // the LIDs of other related nodes, such as the node link to the current
    // node
    private final Set<Integer> relatedNodes = new HashSet<Integer>();

    /**
     * Description:
     * 
     * @param notice
     */
    public NoticeWrapper(NoticeBean notice, TrapType trapType) {
        super();
        this.notice = notice;
        this.trapType = trapType;
    }

    /**
     * @return the notice
     */
    public NoticeBean getNotice() {
        return notice;
    }

    public void addRelatedNode(Integer lid) {
        relatedNodes.add(lid);
    }

    public void addRelatedNodes(Collection<Integer> lids) {
        relatedNodes.addAll(lids);
    }

    /**
     * @return the relatedNodes
     */
    public Set<Integer> getRelatedNodes() {
        return relatedNodes;
    }

    /**
     * @return the node
     */
    public NodeRecordBean getNode() {
        return node;
    }

    /**
     * @param node
     *            the node to set
     */
    public void setNode(NodeRecordBean node) {
        this.node = node;
    }

    public TrapType getTrapType() {
        return trapType;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "NoticeWrapper [notice=" + notice + ", relatedNodes="
                + relatedNodes + "]";
    }

}
