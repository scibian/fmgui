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
public class SMInfoBean implements Serializable {
    private static final long serialVersionUID = 6396097702305645183L;

    private long portGuid;

    private long smKey;

    private long actCount; // promote to handle unsigned int

    private long elapsedTime; // promote to handle unsigned int

    private byte priority;

    private byte elevatedPriority;

    private byte initialPriority;

    private byte smStateCurrent;

    /**
     * @return the guid
     */
    public long getPortGuid() {
        return portGuid;
    }

    /**
     * @param guid
     *            the guid to set
     */
    public void setPortGuid(long guid) {
        this.portGuid = guid;
    }

    /**
     * @return the smKey
     */
    public long getSmKey() {
        return smKey;
    }

    /**
     * @param smKey
     *            the smKey to set
     */
    public void setSmKey(long smKey) {
        this.smKey = smKey;
    }

    /**
     * @return the actCount
     */
    public long getActCount() {
        return actCount;
    }

    /**
     * @param actCount
     *            the actCount to set
     */
    public void setActCount(long actCount) {
        this.actCount = actCount;
    }

    /**
     * @param actCount
     *            the actCount to set
     */
    public void setActCount(int actCount) {
        this.actCount = Utils.unsignedInt(actCount);
    }

    /**
     * @return the elapsedTime
     */
    public long getElapsedTime() {
        return elapsedTime;
    }

    /**
     * @param elapsedTime
     *            the elapsedTime to set
     */
    public void setElapsedTime(long elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    /**
     * @param elapsedTime
     *            the elapsedTime to set
     */
    public void setElapsedTime(int elapsedTime) {
        this.elapsedTime = Utils.unsignedInt(elapsedTime);
    }

    /**
     * @return the priority
     */
    public byte getPriority() {
        return priority;
    }

    /**
     * @param priority
     *            the priority to set
     */
    public void setPriority(byte priority) {
        this.priority = priority;
    }

    /**
     * @return the elevatedPriority
     */
    public byte getElevatedPriority() {
        return elevatedPriority;
    }

    /**
     * @param elevatedPriority
     *            the elevatedPriority to set
     */
    public void setElevatedPriority(byte elevatedPriority) {
        this.elevatedPriority = elevatedPriority;
    }

    /**
     * @return the initialPriority
     */
    public byte getInitialPriority() {
        return initialPriority;
    }

    /**
     * @param initialPriority
     *            the initialPriority to set
     */
    public void setInitialPriority(byte initialPriority) {
        this.initialPriority = initialPriority;
    }

    /**
     * @return the smStateCurrent
     */
    public byte getSmStateCurrent() {
        return smStateCurrent;
    }

    /**
     * @param smStateCurrent
     *            the smStateCurrent to set
     */
    public void setSmStateCurrent(byte smStateCurrent) {
        this.smStateCurrent = smStateCurrent;
    }

    /**
     * @return the serialversionuid
     */
    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SMInfoBean [guid=" + StringUtils.longHexString(portGuid)
                + ", smKey=" + StringUtils.longHexString(smKey) + ", actCount="
                + StringUtils.longHexString(actCount) + ", elapsedTime="
                + elapsedTime + ", priority=" + priority
                + ", elevatedPriority=" + elevatedPriority
                + ", initialPriority=" + initialPriority + ", smStateCurrent="
                + smStateCurrent + "]";
    }

}
