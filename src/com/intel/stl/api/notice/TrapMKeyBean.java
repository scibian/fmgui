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
import java.util.Arrays;

public class TrapMKeyBean implements Serializable {
    private static final long serialVersionUID = 3639303016600181495L;
    
    private int lid;
    private int drSLid;
    private byte method;
    private boolean drNotice;
    private boolean drPathTruncated;
    private byte drHopCount;
    private short attributeID;
    private int attributeModifier;
    private long mKey;
    private byte[] drReturnPath;
    
    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @param lid the lid to set
     */
    public void setLid(int lid) {
        this.lid = lid;
    }

    /**
     * @return the drSLid
     */
    public int getDrSLid() {
        return drSLid;
    }

    /**
     * @param drSLid the drSLid to set
     */
    public void setDrSLid(int drSLid) {
        this.drSLid = drSLid;
    }

    /**
     * @return the method
     */
    public byte getMethod() {
        return method;
    }

    /**
     * @param method the method to set
     */
    public void setMethod(byte method) {
        this.method = method;
    }

    /**
     * @return the drNotice
     */
    public boolean isDrNotice() {
        return drNotice;
    }

    /**
     * @param drNotice the drNotice to set
     */
    public void setDrNotice(boolean drNotice) {
        this.drNotice = drNotice;
    }

    /**
     * @return the drPathTruncated
     */
    public boolean isDrPathTruncated() {
        return drPathTruncated;
    }

    /**
     * @param drPathTruncated the drPathTruncated to set
     */
    public void setDrPathTruncated(boolean drPathTruncated) {
        this.drPathTruncated = drPathTruncated;
    }

    /**
     * @return the drHopCount
     */
    public byte getDrHopCount() {
        return drHopCount;
    }

    /**
     * @param drHopCount the drHopCount to set
     */
    public void setDrHopCount(byte drHopCount) {
        this.drHopCount = drHopCount;
    }

    /**
     * @return the attributeID
     */
    public short getAttributeID() {
        return attributeID;
    }

    /**
     * @param attributeID the attributeID to set
     */
    public void setAttributeID(short attributeID) {
        this.attributeID = attributeID;
    }

    /**
     * @return the attributeModifier
     */
    public int getAttributeModifier() {
        return attributeModifier;
    }

    /**
     * @param attributeModifier the attributeModifier to set
     */
    public void setAttributeModifier(int attributeModifier) {
        this.attributeModifier = attributeModifier;
    }

    /**
     * @return the mKey
     */
    public long getMKey() {
        return mKey;
    }

    /**
     * @param mKey the mKey to set
     */
    public void setMKey(long mKey) {
        this.mKey = mKey;
    }

    /**
     * @return the drReturnPath
     */
    public byte[] getDrReturnPath() {
        return drReturnPath;
    }

    /**
     * @param drReturnPath the drReturnPath to set
     */
    public void setDrReturnPath(byte[] drReturnPath) {
        this.drReturnPath = drReturnPath;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TrapMKeyBean [lid=" + lid + ", drSLid=" + drSLid + ", method="
                + method + ", drNotice=" + drNotice + ", drPathTruncated="
                + drPathTruncated + ", drHopCount=" + drHopCount
                + ", attributeID=" + attributeID + ", attributeModifier="
                + attributeModifier + ", mKey=" + mKey + ", drReturnPath="
                + Arrays.toString(drReturnPath) + "]";
    }
    
}
