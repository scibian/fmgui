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

import com.intel.stl.api.Utils;

/**
 * A substructure in Port Info for the Fabric View API
 */
public class FlitControlBean implements Serializable {

    private static final long serialVersionUID = 1L;

    // Interleave
    // private short ilAsReg16;
    private byte distanceSupported;

    private byte distanceEnabled;

    private byte maxNestLevelTxEnabled;

    private byte maxNestLevelRxSupported;

    // Preemption
    private int minInitial; // promote to handle unsigned short

    private int minTail; // promote to handle unsigned short

    private short largePktLimit; // promote to handle unsigned byte

    private short smallPktLimit; // promote to handle unsigned byte

    private short maxSmallPktLimit; // promote to handle unsigned byte

    private short preemptionLimit; // promote to handle unsigned byte

    public FlitControlBean() {
        super();
    }

    public FlitControlBean(byte distanceSupported, byte distanceEnabled,
            byte maxNestLevelTxEnabled, byte maxNestLevelRxSupported,
            short minInitial, short minTail, byte largePktLimit,
            byte smallPktLimit, byte maxSmallPktLimit, byte preemptionLimit) {
        super();
        this.distanceSupported = distanceSupported;
        this.distanceEnabled = distanceEnabled;
        this.maxNestLevelTxEnabled = maxNestLevelTxEnabled;
        this.maxNestLevelRxSupported = maxNestLevelRxSupported;
        this.minInitial = Utils.unsignedShort(minInitial);
        this.minTail = Utils.unsignedShort(minTail);
        this.largePktLimit = Utils.unsignedByte(largePktLimit);
        this.smallPktLimit = Utils.unsignedByte(smallPktLimit);
        this.maxSmallPktLimit = Utils.unsignedByte(maxSmallPktLimit);
        this.preemptionLimit = Utils.unsignedByte(preemptionLimit);
    }

    /**
     * @return the distanceSupported
     */
    public byte getDistanceSupported() {
        return distanceSupported;
    }

    /**
     * @param distanceSupported
     *            the distanceSupported to set
     */
    public void setDistanceSupported(byte distanceSupported) {
        this.distanceSupported = distanceSupported;
    }

    /**
     * @return the distanceEnabled
     */
    public byte getDistanceEnabled() {
        return distanceEnabled;
    }

    /**
     * @param distanceEnabled
     *            the distanceEnabled to set
     */
    public void setDistanceEnabled(byte distanceEnabled) {
        this.distanceEnabled = distanceEnabled;
    }

    /**
     * @return the maxNestLevelTxEnabled
     */
    public byte getMaxNestLevelTxEnabled() {
        return maxNestLevelTxEnabled;
    }

    /**
     * @param maxNestLevelTxEnabled
     *            the maxNestLevelTxEnabled to set
     */
    public void setMaxNestLevelTxEnabled(byte maxNestLevelTxEnabled) {
        this.maxNestLevelTxEnabled = maxNestLevelTxEnabled;
    }

    /**
     * @return the maxNestLevelRxSupported
     */
    public byte getMaxNestLevelRxSupported() {
        return maxNestLevelRxSupported;
    }

    /**
     * @param maxNestLevelRxSupported
     *            the maxNestLevelRxSupported to set
     */
    public void setMaxNestLevelRxSupported(byte maxNestLevelRxSupported) {
        this.maxNestLevelRxSupported = maxNestLevelRxSupported;
    }

    /**
     * @return the minInitial
     */
    public int getMinInitial() {
        return minInitial;
    }

    /**
     * @param minInitial
     *            the minInitial to set
     */
    public void setMinInitial(int minInitial) {
        this.minInitial = minInitial;
    }

    /**
     * @param minInitial
     *            the minInitial to set
     */
    public void setMinInitial(short minInitial) {
        this.minInitial = Utils.unsignedShort(minInitial);
    }

    /**
     * @return the minTail
     */
    public int getMinTail() {
        return minTail;
    }

    /**
     * @param minTail
     *            the minTail to set
     */
    public void setMinTail(int minTail) {
        this.minTail = minTail;
    }

    /**
     * @param minTail
     *            the minTail to set
     */
    public void setMinTail(short minTail) {
        this.minTail = Utils.unsignedShort(minTail);
    }

    /**
     * @return the largePktLimit
     */
    public short getLargePktLimit() {
        return largePktLimit;
    }

    /**
     * @param largePktLimit
     *            the largePktLimit to set
     */
    public void setLargePktLimit(short largePktLimit) {
        this.largePktLimit = largePktLimit;
    }

    /**
     * @param largePktLimit
     *            the largePktLimit to set
     */
    public void setLargePktLimit(byte largePktLimit) {
        this.largePktLimit = Utils.unsignedByte(largePktLimit);
    }

    /**
     * @return the smallPktLimit
     */
    public short getSmallPktLimit() {
        return smallPktLimit;
    }

    /**
     * @param smallPktLimit
     *            the smallPktLimit to set
     */
    public void setSmallPktLimit(short smallPktLimit) {
        this.smallPktLimit = smallPktLimit;
    }

    /**
     * @param smallPktLimit
     *            the smallPktLimit to set
     */
    public void setSmallPktLimit(byte smallPktLimit) {
        this.smallPktLimit = Utils.unsignedByte(smallPktLimit);
    }

    /**
     * @return the maxSmallPktLimit
     */
    public short getMaxSmallPktLimit() {
        return maxSmallPktLimit;
    }

    /**
     * @param maxSmallPktLimit
     *            the maxSmallPktLimit to set
     */
    public void setMaxSmallPktLimit(short maxSmallPktLimit) {
        this.maxSmallPktLimit = maxSmallPktLimit;
    }

    /**
     * @param maxSmallPktLimit
     *            the maxSmallPktLimit to set
     */
    public void setMaxSmallPktLimit(byte maxSmallPktLimit) {
        this.maxSmallPktLimit = Utils.unsignedByte(maxSmallPktLimit);
    }

    /**
     * @return the preemptionLimit
     */
    public short getPreemptionLimit() {
        return preemptionLimit;
    }

    /**
     * @param preemptionLimit
     *            the preemptionLimit to set
     */
    public void setPreemptionLimit(short preemptionLimit) {
        this.preemptionLimit = preemptionLimit;
    }

    /**
     * @param preemptionLimit
     *            the preemptionLimit to set
     */
    public void setPreemptionLimit(byte preemptionLimit) {
        this.preemptionLimit = Utils.unsignedByte(preemptionLimit);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "FlitControlBean [distanceSupported=" + distanceSupported
                + ", distanceEnabled=" + distanceEnabled
                + ", maxNestLevelTxEnabled=" + maxNestLevelTxEnabled
                + ", maxNestLevelRxSupported=" + maxNestLevelRxSupported
                + ", minInitial=" + minInitial + ", minTail=" + minTail
                + ", largePktLimit=" + largePktLimit + ", smallPktLimit="
                + smallPktLimit + ", maxSmallPktLimit=" + maxSmallPktLimit
                + ", preemptionLimit=" + preemptionLimit + "]";
    }

}
