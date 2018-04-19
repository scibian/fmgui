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
 * Title:        VirtualLaneBean
 * Description:  A substructure in Port Info for the Fabric View API
 * 
 * @version 0.0
 */
import java.io.Serializable;

import com.intel.stl.api.Utils;

public class VirtualLaneBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private short preemptCap; // promote to handler unsigned byte

    private byte cap;

    private int highLimit; // promote to handler unsigned short

    private int preemptingLimit; // promote to handler unsigned short

    private short arbitrationHighCap; // promote to handler unsigned byte

    private short arbitrationLowCap; // promote to handler unsigned byte

    public VirtualLaneBean() {
        super();
    }

    public VirtualLaneBean(byte preemptCap, byte cap, short highLimit,
            short preemptingLimit, byte arbitrationHighCap,
            byte arbitrationLowCap) {
        super();
        this.preemptCap = Utils.unsignedByte(preemptCap);
        this.cap = cap;
        this.highLimit = Utils.unsignedShort(highLimit);
        this.preemptingLimit = Utils.unsignedShort(preemptingLimit);
        this.arbitrationHighCap = Utils.unsignedByte(arbitrationHighCap);
        this.arbitrationLowCap = Utils.unsignedByte(arbitrationLowCap);
    }

    /**
     * @return the preemptCap
     */
    public short getPreemptCap() {
        return preemptCap;
    }

    /**
     * @param preemptCap
     *            the preemptCap to set
     */
    public void setPreemptCap(short preemptCap) {
        this.preemptCap = preemptCap;
    }

    /**
     * @param initType
     *            the initType to set
     */
    public void setPreemptCap(byte preemptCap) {
        this.preemptCap = Utils.unsignedByte(preemptCap);
    }

    /**
     * @return the cap
     */
    public byte getCap() {
        return cap;
    }

    /**
     * @param cap
     *            the cap to set
     */
    public void setCap(byte cap) {
        this.cap = cap;
    }

    /**
     * @return the highLimit
     */
    public int getHighLimit() {
        return highLimit;
    }

    /**
     * @param highLimit
     *            the highLimit to set
     */
    public void setHighLimit(int highLimit) {
        this.highLimit = highLimit;
    }

    /**
     * @param highLimit
     *            the highLimit to set
     */
    public void setHighLimit(short highLimit) {
        this.highLimit = Utils.unsignedShort(highLimit);
    }

    /**
     * @return the preemptingLimit
     */
    public int getPreemptingLimit() {
        return preemptingLimit;
    }

    /**
     * @param preemptingLimit
     *            the preemptingLimit to set
     */
    public void setPreemptingLimit(int preemptingLimit) {
        this.preemptingLimit = preemptingLimit;
    }

    /**
     * @param preemptingLimit
     *            the preemptingLimit to set
     */
    public void setPreemptingLimit(short preemptingLimit) {
        this.preemptingLimit = Utils.unsignedShort(preemptingLimit);
    }

    /**
     * @return the arbitrationHighCap
     */
    public short getArbitrationHighCap() {
        return arbitrationHighCap;
    }

    /**
     * @param arbitrationHighCap
     *            the arbitrationHighCap to set
     */
    public void setArbitrationHighCap(short arbitrationHighCap) {
        this.arbitrationHighCap = arbitrationHighCap;
    }

    /**
     * @param arbitrationHighCap
     *            the arbitrationHighCap to set
     */
    public void setArbitrationHighCap(byte arbitrationHighCap) {
        this.arbitrationHighCap = Utils.unsignedByte(arbitrationHighCap);
    }

    /**
     * @return the arbitrationLowCap
     */
    public short getArbitrationLowCap() {
        return arbitrationLowCap;
    }

    /**
     * @param arbitrationLowCap
     *            the arbitrationLowCap to set
     */
    public void setArbitrationLowCap(short arbitrationLowCap) {
        this.arbitrationLowCap = arbitrationLowCap;
    }

    /**
     * @param arbitrationLowCap
     *            the arbitrationLowCap to set
     */
    public void setArbitrationLowCap(byte arbitrationLowCap) {
        this.arbitrationLowCap = Utils.unsignedByte(arbitrationLowCap);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VirtualLaneBean [preemptCap=" + preemptCap + ", cap=" + cap
                + ", highLimit=" + highLimit + ", preemptingLimit="
                + preemptingLimit + ", arbitrationHighCap="
                + arbitrationHighCap + ", arbitrationLowCap="
                + arbitrationLowCap + "]";
    }

}
