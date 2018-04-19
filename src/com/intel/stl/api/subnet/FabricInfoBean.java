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

public class FabricInfoBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private long numHFIs;

    private long numSwitches;

    private long numInternalHFILinks;

    private long numExternalHFILinks;

    private long numInternalISLs;

    private long numExternalISLs;

    private long numDegradedHFILinks;

    private long numDegradedISLs;

    private long numOmittedHFILinks;

    private long numOmittedISLs;

    public long getNumHFIs() {
        return numHFIs;
    }

    public void setNumHFIs(long numHFIs) {
        this.numHFIs = numHFIs;
    }

    public long getNumSwitches() {
        return numSwitches;
    }

    public void setNumSwitches(long numSwitches) {
        this.numSwitches = numSwitches;
    }

    public long getNumInternalHFILinks() {
        return numInternalHFILinks;
    }

    public void setNumInternalHFILinks(long numInternalHFILinks) {
        this.numInternalHFILinks = numInternalHFILinks;
    }

    public long getNumExternalHFILinks() {
        return numExternalHFILinks;
    }

    public void setNumExternalHFILinks(long numExternalHFILinks) {
        this.numExternalHFILinks = numExternalHFILinks;
    }

    public long getNumInternalISLs() {
        return numInternalISLs;
    }

    public void setNumInternalISLs(long numInternalISLs) {
        this.numInternalISLs = numInternalISLs;
    }

    public long getNumExternalISLs() {
        return numExternalISLs;
    }

    public void setNumExternalISLs(long numExternalISLs) {
        this.numExternalISLs = numExternalISLs;
    }

    public long getNumDegradedHFILinks() {
        return numDegradedHFILinks;
    }

    public void setNumDegradedHFILinks(long numDegradedHFILinks) {
        this.numDegradedHFILinks = numDegradedHFILinks;
    }

    public long getNumDegradedISLs() {
        return numDegradedISLs;
    }

    public void setNumDegradedISLs(long numDegradedISLs) {
        this.numDegradedISLs = numDegradedISLs;
    }

    public long getNumOmittedHFILinks() {
        return numOmittedHFILinks;
    }

    public void setNumOmittedHFILinks(long numOmittedHFILinks) {
        this.numOmittedHFILinks = numOmittedHFILinks;
    }

    public long getNumOmittedISLs() {
        return numOmittedISLs;
    }

    public void setNumOmittedISLs(long numOmittedISLs) {
        this.numOmittedISLs = numOmittedISLs;
    }

    @Override
    public String toString() {
        return "FabricInfoBean [numHFIs=" + numHFIs + ", numSwitches="
                + numSwitches + ", numInternalHFILinks=" + numInternalHFILinks
                + ", numExternalHFILinks=" + numExternalHFILinks
                + ", numInternalISLs=" + numInternalISLs + ", numExternalISLs="
                + numExternalISLs + ", numDegradedHFILinks="
                + numDegradedHFILinks + ", numDegradedISLs=" + numDegradedISLs
                + ", numOmittedHFILinks=" + numOmittedHFILinks
                + ", numOmittedISLs=" + numOmittedISLs + "]";
    }

}
