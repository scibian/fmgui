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

package com.intel.stl.api.performance;

import java.io.Serializable;

import com.intel.stl.api.Utils;

/**
 */
public class CategorySummaryBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private long integrityErrors; // unsigned int

    private long congestion; // unsigned int

    private long smaCongestion; // unsigned int

    private long bubble; // unsigned int

    private long securityErrors; // unsigned int

    private long routingErrors; // unsigned int

    private int discardsPct10; // promote to handle unsigned short

    private int utilizationPct10; // promote to handle unsigned short

    /**
     * @return the integrityErrors
     */
    public long getIntegrityErrors() {
        return integrityErrors;
    }

    /**
     * @param integrityErrors
     *            the integrityErrors to set
     */
    public void setIntegrityErrors(long integrityErrors) {
        this.integrityErrors = integrityErrors;
    }

    /**
     * @param integrityErrors
     *            the integrityErrors to set
     */
    public void setIntegrityErrors(int integrityErrors) {
        this.integrityErrors = Utils.unsignedInt(integrityErrors);
    }

    /**
     * @return the congestion
     */
    public long getCongestion() {
        return congestion;
    }

    /**
     * @param congestion
     *            the congestion to set
     */
    public void setCongestion(long congestion) {
        this.congestion = congestion;
    }

    /**
     * @param congestion
     *            the congestion to set
     */
    public void setCongestion(int congestion) {
        this.congestion = Utils.unsignedInt(congestion);
    }

    /**
     * @return the smaCongestion
     */
    public long getSmaCongestion() {
        return smaCongestion;
    }

    /**
     * @param smaCongestion
     *            the smaCongestion to set
     */
    public void setSmaCongestion(long smaCongestion) {
        this.smaCongestion = smaCongestion;
    }

    /**
     * @param smaCongestion
     *            the smaCongestion to set
     */
    public void setSmaCongestion(int smaCongestion) {
        this.smaCongestion = Utils.unsignedInt(smaCongestion);
    }

    /**
     * @return the bubble
     */
    public long getBubble() {
        return bubble;
    }

    /**
     * @param bubble
     *            the bubble to set
     */
    public void setBubble(long bubble) {
        this.bubble = bubble;
    }

    /**
     * @param bubble
     *            the bubble to set
     */
    public void setBubble(int bubble) {
        this.bubble = Utils.unsignedInt(bubble);
    }

    /**
     * @return the securityErrors
     */
    public long getSecurityErrors() {
        return securityErrors;
    }

    /**
     * @param securityErrors
     *            the securityErrors to set
     */
    public void setSecurityErrors(long securityErrors) {
        this.securityErrors = securityErrors;
    }

    /**
     * @param securityErrors
     *            the securityErrors to set
     */
    public void setSecurityErrors(int securityErrors) {
        this.securityErrors = Utils.unsignedInt(securityErrors);
    }

    /**
     * @return the routingErrors
     */
    public long getRoutingErrors() {
        return routingErrors;
    }

    /**
     * @param routingErrors
     *            the routingErrors to set
     */
    public void setRoutingErrors(long routingErrors) {
        this.routingErrors = routingErrors;
    }

    /**
     * @param routingErrors
     *            the routingErrors to set
     */
    public void setRoutingErrors(int routingErrors) {
        this.routingErrors = Utils.unsignedInt(routingErrors);
    }

    /**
     * @return the discarddPct10
     */
    public int getDiscardsPct10() {
        return discardsPct10;
    }

    /**
     * @param discardPct10
     *            the discardPct10 to set
     */
    public void setDiscardsPct10(int discardsPct10) {
        this.discardsPct10 = discardsPct10;
    }

    /**
     * @return the utilizationPct10
     */
    public int getUtilizationPct10() {
        return utilizationPct10;
    }

    /**
     * @param utilizationPct10
     *            the utilizationPct10 to set
     */
    public void setUtilizationPct10(int utilizationPct10) {
        this.utilizationPct10 = utilizationPct10;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CategorySummaryBean [integrityErrors=" + integrityErrors
                + ", congestion=" + congestion + ", smaCongestion="
                + smaCongestion + ", bubble=" + bubble + ", securityErrors="
                + securityErrors + ", routingErrors=" + routingErrors
                + ", discardsPct10=" + discardsPct10 + ", utilizationPct10="
                + utilizationPct10 + "]";
    }

}
