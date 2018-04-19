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

import com.intel.stl.api.subnet.GIDBean;

public class TrapSwitchPKeyBean implements Serializable {
    private static final long serialVersionUID = -3080083106024241483L;
    
    private boolean hasLid1;
    private boolean hasLid2;
    private boolean hasPKey;
    private boolean hasSL;
    private boolean hasQP1;
    private boolean hasQP2;
    private boolean hasGid1;
    private boolean hasGid2;
    private short pKey;
    private int lid1;
    private int lid2;
    private byte sl;
    private GIDBean gid1;
    private GIDBean gid2;
    private int qp1;
    private int qp2;
    
    /**
     * @return the hasLid1
     */
    public boolean isHasLid1() {
        return hasLid1;
    }

    /**
     * @param hasLid1 the hasLid1 to set
     */
    public void setHasLid1(boolean hasLid1) {
        this.hasLid1 = hasLid1;
    }

    /**
     * @return the hasLid2
     */
    public boolean isHasLid2() {
        return hasLid2;
    }

    /**
     * @param hasLid2 the hasLid2 to set
     */
    public void setHasLid2(boolean hasLid2) {
        this.hasLid2 = hasLid2;
    }

    /**
     * @return the hasPKey
     */
    public boolean isHasPKey() {
        return hasPKey;
    }

    /**
     * @param hasPKey the hasPKey to set
     */
    public void setHasPKey(boolean hasPKey) {
        this.hasPKey = hasPKey;
    }

    /**
     * @return the hasSL
     */
    public boolean isHasSL() {
        return hasSL;
    }

    /**
     * @param hasSL the hasSL to set
     */
    public void setHasSL(boolean hasSL) {
        this.hasSL = hasSL;
    }

    /**
     * @return the hasQP1
     */
    public boolean isHasQP1() {
        return hasQP1;
    }

    /**
     * @param hasQP1 the hasQP1 to set
     */
    public void setHasQP1(boolean hasQP1) {
        this.hasQP1 = hasQP1;
    }

    /**
     * @return the hasQP2
     */
    public boolean isHasQP2() {
        return hasQP2;
    }

    /**
     * @param hasQP2 the hasQP2 to set
     */
    public void setHasQP2(boolean hasQP2) {
        this.hasQP2 = hasQP2;
    }

    /**
     * @return the hasGid1
     */
    public boolean isHasGid1() {
        return hasGid1;
    }

    /**
     * @param hasGid1 the hasGid1 to set
     */
    public void setHasGid1(boolean hasGid1) {
        this.hasGid1 = hasGid1;
    }

    /**
     * @return the hasGid2
     */
    public boolean isHasGid2() {
        return hasGid2;
    }

    /**
     * @param hasGid2 the hasGid2 to set
     */
    public void setHasGid2(boolean hasGid2) {
        this.hasGid2 = hasGid2;
    }

    /**
     * @return the pKey
     */
    public short getpKey() {
        return pKey;
    }

    /**
     * @param pKey the pKey to set
     */
    public void setpKey(short pKey) {
        this.pKey = pKey;
    }

    /**
     * @return the lid1
     */
    public int getLid1() {
        return lid1;
    }

    /**
     * @param lid1 the lid1 to set
     */
    public void setLid1(int lid1) {
        this.lid1 = lid1;
    }

    /**
     * @return the lid2
     */
    public int getLid2() {
        return lid2;
    }

    /**
     * @param lid2 the lid2 to set
     */
    public void setLid2(int lid2) {
        this.lid2 = lid2;
    }

    /**
     * @return the sl
     */
    public byte getSl() {
        return sl;
    }

    /**
     * @param sl the sl to set
     */
    public void setSl(byte sl) {
        this.sl = sl;
    }

    /**
     * @return the gid1
     */
    public GIDBean getGid1() {
        return gid1;
    }

    /**
     * @param gid1 the gid1 to set
     */
    public void setGid1(GIDBean gid1) {
        this.gid1 = gid1;
    }

    /**
     * @return the gid2
     */
    public GIDBean getGid2() {
        return gid2;
    }

    /**
     * @param gid2 the gid2 to set
     */
    public void setGid2(GIDBean gid2) {
        this.gid2 = gid2;
    }

    /**
     * @return the qp1
     */
    public int getQp1() {
        return qp1;
    }

    /**
     * @param qp1 the qp1 to set
     */
    public void setQp1(int qp1) {
        this.qp1 = qp1;
    }

    /**
     * @return the qp2
     */
    public int getQp2() {
        return qp2;
    }

    /**
     * @param qp2 the qp2 to set
     */
    public void setQp2(int qp2) {
        this.qp2 = qp2;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TrapSwitchPKey [hasLid1=" + hasLid1 + ", hasLid2=" + hasLid2
                + ", hasPKey=" + hasPKey + ", hasSL=" + hasSL + ", hasQP1="
                + hasQP1 + ", hasQP2=" + hasQP2 + ", hasGid1=" + hasGid1
                + ", hasGid2=" + hasGid2 + ", pKey=" + pKey + ", lid1=" + lid1
                + ", lid2=" + lid2 + ", sl=" + sl + ", gid1=" + gid1
                + ", gid2=" + gid2 + ", qp1=" + qp1 + ", qp2=" + qp2 + "]";
    }
    
}
