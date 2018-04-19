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

import java.io.Serializable;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;

/**
 */
public class TraceRecordBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private short idGeneration;

    private byte nodeType;

    private short entryPort; // promote to handle unsigned byte

    private short exitPort; // promote to handle unsigned byte

    private long nodeId;

    private long chassisId;

    private long entryPortId;

    private long exitPortId;

    public TraceRecordBean() {
        super();
    }

    public TraceRecordBean(short idGeneration, byte nodeType, byte entryPort,
            byte exitPort, long nodeId, long chassisId, long entryPortId,
            long exirPortId) {
        super();
        this.idGeneration = idGeneration;
        this.nodeType = nodeType;
        this.entryPort = Utils.unsignedByte(entryPort);
        this.exitPort = Utils.unsignedByte(exitPort);
        this.nodeId = nodeId;
        this.chassisId = chassisId;
        this.entryPortId = entryPortId;
        this.exitPortId = exirPortId;
    }

    /**
     * @return the idGeneration
     */
    public short getIdGeneration() {
        return idGeneration;
    }

    /**
     * @param idGeneration
     *            the idGeneration to set
     */
    public void setIdGeneration(short idGeneration) {
        this.idGeneration = idGeneration;
    }

    /**
     * @return the nodeType
     */
    public byte getNodeType() {
        return nodeType;
    }

    /**
     * @param nodeType
     *            the nodeType to set
     */
    public void setNodeType(byte nodeType) {
        this.nodeType = nodeType;
    }

    /**
     * @return the entryPort
     */
    public short getEntryPort() {
        return entryPort;
    }

    /**
     * @param entryPort
     *            the entryPort to set
     */
    public void setEntryPort(short entryPort) {
        this.entryPort = entryPort;
    }

    /**
     * @param entryPort
     *            the entryPort to set
     */
    public void setEntryPort(byte entryPort) {
        this.entryPort = Utils.unsignedByte(entryPort);
    }

    /**
     * @return the exitPort
     */
    public short getExitPort() {
        return exitPort;
    }

    /**
     * @param exitPort
     *            the exitPort to set
     */
    public void setExitPort(short exitPort) {
        this.exitPort = exitPort;
    }

    /**
     * @param exitPort
     *            the exitPort to set
     */
    public void setExitPort(byte exitPort) {
        this.exitPort = Utils.unsignedByte(exitPort);
    }

    /**
     * @return the nodeId
     */
    public long getNodeId() {
        return nodeId;
    }

    /**
     * @param nodeId
     *            the nodeId to set
     */
    public void setNodeId(long nodeId) {
        this.nodeId = nodeId;
    }

    /**
     * @return the chassisId
     */
    public long getChassisId() {
        return chassisId;
    }

    /**
     * @param chassisId
     *            the chassisId to set
     */
    public void setChassisId(long chassisId) {
        this.chassisId = chassisId;
    }

    /**
     * @return the entryPortId
     */
    public long getEntryPortId() {
        return entryPortId;
    }

    /**
     * @param entryPortId
     *            the entryPortId to set
     */
    public void setEntryPortId(long entryPortId) {
        this.entryPortId = entryPortId;
    }

    /**
     * @return the exirPortId
     */
    public long getExitPortId() {
        return exitPortId;
    }

    /**
     * @param exirPortId
     *            the exirPortId to set
     */
    public void setExitPortId(long exirPortId) {
        this.exitPortId = exirPortId;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TraceRecordBean [idGeneration="
                + StringUtils.longHexString(idGeneration) + ", nodeType="
                + nodeType + ", entryPort=" + entryPort + ", exitPort="
                + exitPort + ", nodeId=" + StringUtils.longHexString(nodeId)
                + ", chassisId=" + StringUtils.longHexString(chassisId)
                + ", entryPortId=" + StringUtils.longHexString(entryPortId)
                + ", exitPortId=" + StringUtils.longHexString(exitPortId) + "]";
    }

}
