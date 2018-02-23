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

import java.io.Serializable;

public class TrapCapabilityBean implements Serializable {
    private static final long serialVersionUID = 93229705407669540L;

    private int lid;

    private int capabilityMask;

    private short capabilityMask3;

    private boolean linkSpeedEnabledChange;

    private boolean linkWidthEnabledChange;

    private boolean NodeDescriptionChange;

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
     * @return the capabilityMask
     */
    public int getCapabilityMask() {
        return capabilityMask;
    }

    /**
     * @param capabilityMask
     *            the capabilityMask to set
     */
    public void setCapabilityMask(int capabilityMask) {
        this.capabilityMask = capabilityMask;
    }

    /**
     * @return the capabilityMask3
     */
    public short getCapabilityMask3() {
        return capabilityMask3;
    }

    /**
     * @param capabilityMask3
     *            the capabilityMask3 to set
     */
    public void setCapabilityMask3(short capabilityMask3) {
        this.capabilityMask3 = capabilityMask3;
    }

    /**
     * @return the linkSpeedEnabledChange
     */
    public boolean isLinkSpeedEnabledChange() {
        return linkSpeedEnabledChange;
    }

    /**
     * @param linkSpeedEnabledChange
     *            the linkSpeedEnabledChange to set
     */
    public void setLinkSpeedEnabledChange(boolean linkSpeedEnabledChange) {
        this.linkSpeedEnabledChange = linkSpeedEnabledChange;
    }

    /**
     * @return the linkWidthEnabledChange
     */
    public boolean isLinkWidthEnabledChange() {
        return linkWidthEnabledChange;
    }

    /**
     * @param linkWidthEnabledChange
     *            the linkWidthEnabledChange to set
     */
    public void setLinkWidthEnabledChange(boolean linkWidthEnabledChange) {
        this.linkWidthEnabledChange = linkWidthEnabledChange;
    }

    /**
     * @return the nodeDescriptionChange
     */
    public boolean isNodeDescriptionChange() {
        return NodeDescriptionChange;
    }

    /**
     * @param nodeDescriptionChange
     *            the nodeDescriptionChange to set
     */
    public void setNodeDescriptionChange(boolean nodeDescriptionChange) {
        NodeDescriptionChange = nodeDescriptionChange;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TrapCapabilityBean [lid=" + lid + ", capabilityMask="
                + capabilityMask + ", capabilityMask3=" + capabilityMask3
                + ", linkSpeedEnabledChange=" + linkSpeedEnabledChange
                + ", linkWidthEnabledChange=" + linkWidthEnabledChange
                + ", NodeDescriptionChange=" + NodeDescriptionChange + "]";
    }

}
