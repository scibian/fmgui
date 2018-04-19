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
 * Title:        PortStatesBean
 * Description:  A substructure in Port Info from SA populated by the connect manager.
 * 
 * @version 0.0
 */
import java.io.Serializable;

import com.intel.stl.api.configuration.PhysicalState;
import com.intel.stl.api.configuration.PortState;

public class PortStatesBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private boolean ledEnabled;

    private boolean isSMConfigurationStarted;

    private boolean neighborNormal;

    private byte offlineDisabledReason;

    private PhysicalState portPhysicalState;

    private PortState portState;

    public PortStatesBean() {
        super();
    }

    public PortStatesBean(boolean ledEnabled, boolean isSMConfigurationStarted,
            boolean neighborNormal, byte offlineDisableReason,
            byte portPhysicalState, byte portState) {
        super();
        this.ledEnabled = ledEnabled;
        this.isSMConfigurationStarted = isSMConfigurationStarted;
        this.neighborNormal = neighborNormal;
        this.offlineDisabledReason = offlineDisableReason;
        this.portPhysicalState =
                PhysicalState.getPhysicalState(portPhysicalState);
        this.portState = PortState.getPortState(portState);
    }

    /**
     * @return the ledEnabled
     */
    public boolean isLedEnabled() {
        return ledEnabled;
    }

    /**
     * @return the isSMConfigurationStarted
     */
    public boolean isSMConfigurationStarted() {
        return isSMConfigurationStarted;
    }

    /**
     * @param isSMConfigurationStarted
     *            the isSMConfigurationStarted to set
     */
    public void setSMConfigurationStarted(boolean isSMConfigurationStarted) {
        this.isSMConfigurationStarted = isSMConfigurationStarted;
    }

    /**
     * @return the neighborNormal
     */
    public boolean isNeighborNormal() {
        return neighborNormal;
    }

    /**
     * @param neighborNormal
     *            the neighborNormal to set
     */
    public void setNeighborNormal(boolean neighborNormal) {
        this.neighborNormal = neighborNormal;
    }

    /**
     * @return the offlineReason
     */
    public byte getOfflineReason() {
        return offlineDisabledReason;
    }

    /**
     * @param offlineReason
     *            the offlineReason to set
     */
    public void setOfflineReason(byte offlineReason) {
        this.offlineDisabledReason = offlineReason;
    }

    /**
     * @return the portPhysicalState
     */
    public PhysicalState getPortPhysicalState() {
        return portPhysicalState;
    }

    /**
     * @param portPhysicalState
     *            the portPhysicalState to set
     */
    public void setPortPhysicalState(byte portPhysicalState) {
        this.portPhysicalState =
                PhysicalState.getPhysicalState(portPhysicalState);
    }

    public void setPortPhysicalState(PhysicalState portPhysicalState) {
        this.portPhysicalState = portPhysicalState;
    }

    /**
     * @return the portState
     */
    public PortState getPortState() {
        return portState;
    }

    /**
     * @param portState
     *            the portState to set
     */
    public void setPortState(byte portState) {
        this.portState = PortState.getPortState(portState);
    }

    public void setPortState(PortState portState) {
        this.portState = portState;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortStatesBean [ledEnabled=" + ledEnabled
                + ", isSMConfigurationStarted=" + isSMConfigurationStarted
                + ", neighborNormal=" + neighborNormal
                + ", offlineDisabledReason=" + offlineDisabledReason
                + ", portPhysicalState=" + portPhysicalState + ", portState="
                + portState + "]";
    }

}
