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

public class TrapKeyBean implements Serializable {
    private static final long serialVersionUID = -7106995880090277274L;
    
    private int lid1;
    private int lid2;
    private int key;
    private byte sl;
    private GIDBean gid1;
    private GIDBean gid2;
    private int qp1;
    private int qp2;
    
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
     * @return the key
     */
    public int getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(int key) {
        this.key = key;
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
     * @return the pq1
     */
    public int getQp1() {
        return qp1;
    }

    /**
     * @param pq1 the pq1 to set
     */
    public void setQp1(int pq1) {
        this.qp1 = pq1;
    }

    /**
     * @return the pq2
     */
    public int getQp2() {
        return qp2;
    }

    /**
     * @param pq2 the pq2 to set
     */
    public void setQp2(int pq2) {
        this.qp2 = pq2;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TrapKeyBean [lid1=" + lid1 + ", lid2=" + lid2 + ", key=" + key
                + ", sl=" + sl + ", gid1=" + gid1 + ", gid2=" + gid2 + ", pq1="
                + qp1 + ", pq2=" + qp2 + "]";
    }
    
}
