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

package com.intel.stl.api.notice.impl;

import java.io.Serializable;
import java.util.List;

import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.TrapType;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.PortRecordBean;

/**
 * Notice wrapper class.
 */
public class NoticeProcess implements Serializable {

    private static final long serialVersionUID = 1L;

    private int lid;

    private TrapType trapType;

    private NodeRecordBean node;

    private List<PortRecordBean> ports;

    private List<LinkRecordBean> links;

    private final NoticeBean notice;

    public NoticeProcess(NoticeBean notice) {
        this.notice = notice;
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @return the node
     */
    public NodeRecordBean getNode() {
        return node;
    }

    /**
     * @return the ports
     */
    public List<PortRecordBean> getPorts() {
        return ports;
    }

    /**
     * @return the links
     */
    public List<LinkRecordBean> getLinks() {
        return links;
    }

    /**
     * @return the notice
     */
    public NoticeBean getNotice() {
        return notice;
    }

    /**
     * @param lid
     *            the lid to set
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * @return the trap type
     */
    public TrapType getTrapType() {
        return trapType;
    }

    /**
     * @param trapType
     *            the trap type to set
     */
    public void setTrapType(TrapType trapType) {
        this.trapType = trapType;
    }

    /**
     * @param node
     *            the node to set
     */
    public void setNode(NodeRecordBean node) {
        this.node = node;
    }

    /**
     * @param ports
     *            the ports to set
     */
    public void setPorts(List<PortRecordBean> ports) {
        this.ports = ports;
    }

    /**
     * @param links
     *            the links to set
     */
    public void setLinks(List<LinkRecordBean> links) {
        this.links = links;
    }

}
