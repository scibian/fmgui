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

/**
 */
public class ErrBucketBean implements Serializable {
    private static final long serialVersionUID = 1L;

    private int integrityErrors;

    private int congestionErrors;

    private int smaCongestionErrors;

    private int bubbleErrors;

    private int securityErrors;

    private int routingErrors;

    public ErrBucketBean() {
        super();
    }

    public ErrBucketBean(int integrityErrors, int congestionErrors,
            int smaCongestionErrors, int bubbleErrors, int securityErrors,
            int routingErrors) {
        super();
        this.integrityErrors = integrityErrors;
        this.congestionErrors = congestionErrors;
        this.smaCongestionErrors = smaCongestionErrors;
        this.bubbleErrors = bubbleErrors;
        this.securityErrors = securityErrors;
        this.routingErrors = routingErrors;
    }

    /**
     * @return the integrityErrors
     */
    public int getIntegrityErrors() {
        return integrityErrors;
    }

    /**
     * @param integrityErrors
     *            the integrityErrors to set
     */
    public void setIntegrityErrors(int integrityErrors) {
        this.integrityErrors = integrityErrors;
    }

    /**
     * @return the congestionErrors
     */
    public int getCongestionErrors() {
        return congestionErrors;
    }

    /**
     * @param congestionErrors
     *            the congestionErrors to set
     */
    public void setCongestionErrors(int congestionErrors) {
        this.congestionErrors = congestionErrors;
    }

    /**
     * @return the smaCongestionErrors
     */
    public int getSmaCongestionErrors() {
        return smaCongestionErrors;
    }

    /**
     * @param smaCongestionErrors
     *            the smaCongestionErrors to set
     */
    public void setSmaCongestionErrors(int smaCongestionErrors) {
        this.smaCongestionErrors = smaCongestionErrors;
    }

    /**
     * @return the bubbleErrors
     */
    public int getBubbleErrors() {
        return bubbleErrors;
    }

    /**
     * @param bubbleErrors
     *            the bubbleErrors to set
     */
    public void setBubbleErrors(int bubbleErrors) {
        this.bubbleErrors = bubbleErrors;
    }

    /**
     * @return the securityErrors
     */
    public int getSecurityErrors() {
        return securityErrors;
    }

    /**
     * @param securityErrors
     *            the securityErrors to set
     */
    public void setSecurityErrors(int securityErrors) {
        this.securityErrors = securityErrors;
    }

    /**
     * @return the routingErrors
     */
    public int getRoutingErrors() {
        return routingErrors;
    }

    /**
     * @param routingErrors
     *            the routingErrors to set
     */
    public void setRoutingErrors(int routingErrors) {
        this.routingErrors = routingErrors;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ErrBucketBean [integrityErrors=" + integrityErrors
                + ", congestionErrors=" + congestionErrors
                + ", smaCongestionErrors=" + smaCongestionErrors
                + ", bubbleErrors=" + bubbleErrors + ", securityErrors="
                + securityErrors + ", routingErrors=" + routingErrors + "]";
    }

}
