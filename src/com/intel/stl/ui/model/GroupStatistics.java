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

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.SMInfoDataBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDescription;

/**
 */
public class GroupStatistics {
    private final SubnetDescription subnet;

    private long numLinks;

    private EnumMap<NodeType, Integer> nodeTypesDist;

    private EnumMap<NodeType, Long> portTypesDist;

    private int numNoRespNodes;

    private long numNoRespPorts;

    private int numSkippedNodes;

    private long numSkippedPorts;

    private int numSMs;

    private List<SMInfoDataBean> smInfo;

    private long msmUptimeInSeconds;

    public GroupStatistics(SubnetDescription subnet, ImageInfoBean imageInfo,
            List<SMInfoDataBean> sms) {
        this.subnet = subnet;
        if (imageInfo == null) {
            throw new IllegalArgumentException("No imageInfo");
        }

        setImageInfo(imageInfo, sms);
    }

    public void setImageInfo(ImageInfoBean imageInfo,
            List<SMInfoDataBean> sms) {
        numLinks = imageInfo.getNumLinks();
        numNoRespNodes = imageInfo.getNumNoRespNodes();
        numNoRespPorts = imageInfo.getNumNoRespPorts();
        numSkippedNodes = imageInfo.getNumSkippedNodes();
        numSkippedPorts = imageInfo.getNumSkippedPorts();
        numSMs = imageInfo.getNumSMs();
        smInfo = new ArrayList<SMInfoDataBean>();
        SMInfoDataBean[] imgSmInfo = imageInfo.getSMInfo();
        Set<Integer> imgSMs = new HashSet<Integer>();
        for (int i = 0; i < imgSmInfo.length; i++) {
            smInfo.add(imgSmInfo[i]);
            imgSMs.add(imgSmInfo[i].getLid());
        }
        if (sms != null) {
            for (SMInfoDataBean sm : sms) {
                if (!imgSMs.contains(sm.getLid())) {
                    smInfo.add(sm);
                }
            }
        }

        nodeTypesDist = new EnumMap<NodeType, Integer>(NodeType.class);
        nodeTypesDist.put(NodeType.SWITCH, imageInfo.getNumSwitchNodes());
        nodeTypesDist.put(NodeType.HFI, imageInfo.getNumHFIPorts());

        portTypesDist = new EnumMap<NodeType, Long>(NodeType.class);
        portTypesDist.put(NodeType.SWITCH, imageInfo.getNumSwitchPorts());
        portTypesDist.put(NodeType.HFI, (long) imageInfo.getNumHFIPorts());
    }

    /**
     * @return the msmUptimeInSeconds
     */
    public long getMsmUptimeInSeconds() {
        return msmUptimeInSeconds;
    }

    /**
     * @param msmUptimeInSeconds
     *            the msmUptimeInSeconds to set
     */
    public void setMsmUptimeInSeconds(long msmUptimeInSeconds) {
        this.msmUptimeInSeconds = msmUptimeInSeconds;
    }

    /**
     * @return the numLinks
     */
    public long getNumLinks() {
        return numLinks;
    }

    public int getNumNodes() {
        int sum = 0;
        if (nodeTypesDist != null) {
            for (Integer count : nodeTypesDist.values()) {
                sum += count;
            }
        }
        return sum;
    }

    /**
     * @return the nodesTypeDist
     */
    public EnumMap<NodeType, Integer> getNodeTypesDist() {
        return nodeTypesDist;
    }

    /**
     * @param nodeTypesDist
     *            the nodesTypeDist to set
     */
    public void setNodeTypesDist(EnumMap<NodeType, Integer> nodeTypesDist) {
        this.nodeTypesDist = nodeTypesDist;
    }

    public long getNumActivePorts() {
        long sum = 0;
        if (portTypesDist != null) {
            for (NodeType type : portTypesDist.keySet()) {
                Long count = portTypesDist.get(type);
                if (type != NodeType.OTHER && count != null) {
                    sum += count;
                }
            }
        }
        return sum;
    }

    /**
     * @return the portsTypeDist
     */
    public EnumMap<NodeType, Long> getPortTypesDist() {
        return portTypesDist;
    }

    /**
     * @param portTypesDist
     *            the portsTypeDist to set
     */
    public void setPortTypesDist(EnumMap<NodeType, Long> portTypesDist) {
        this.portTypesDist = portTypesDist;
    }

    /**
     * <i>Description:</i>
     *
     * @return
     */
    public long getOtherPorts() {
        Long count = portTypesDist.get(NodeType.OTHER);
        return count == null ? 0 : count;
    }

    /**
     * @return the name
     */
    public String getName() {
        return subnet.getName();
    }

    /**
     * @return the numFailedNodes
     */
    public int getNumNoRespNodes() {
        return numNoRespNodes;
    }

    /**
     * @return the numFailedPorts
     */
    public long getNumNoRespPorts() {
        return numNoRespPorts;
    }

    /**
     * @return the numSkippedNodes
     */
    public int getNumSkippedNodes() {
        return numSkippedNodes;
    }

    /**
     * @return the numSkippedPorts
     */
    public long getNumSkippedPorts() {
        return numSkippedPorts;
    }

    /**
     * @return the numSMs
     */
    public int getNumSMs() {
        return numSMs;
    }

    /**
     * @return the sMInfo
     */
    public List<SMInfoDataBean> getSMInfo() {
        return smInfo;
    }

    public SMInfoDataBean getMasterSM() {
        if (smInfo != null && !smInfo.isEmpty()) {
            return smInfo.get(0);
        } else {
            return null;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "GroupStatistics [name=" + subnet.getName() + ", numLinks="
                + numLinks + ", nodeTypesDist=" + nodeTypesDist
                + ", portTypesDist=" + portTypesDist + ", numNoRespNodes="
                + numNoRespNodes + ", numNoRespPorts=" + numNoRespPorts
                + ", numSkippedNodes=" + numSkippedNodes + ", numSkippedPorts="
                + numSkippedPorts + ", numSMs=" + numSMs + ", SMInfo=" + smInfo
                + ", msmUptimeInSeconds=" + msmUptimeInSeconds + "]";
    }

}
