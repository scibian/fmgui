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

package com.intel.stl.api.management.applications;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;
import com.intel.stl.api.management.XMLConstants;

public class MGIDRange extends MGID {
    private static final long serialVersionUID = -5917419318702635265L;

    private long maxLower;

    private long maxUpper;

    public MGIDRange() {
    }

    /**
     * Description:
     * 
     * @param lower
     * @param upper
     * @param maxLower
     * @param maxUpper
     */
    public MGIDRange(long lower, long upper, long maxLower, long maxUpper) {
        super(XMLConstants.MGID_RANGE, lower, upper);
        this.maxLower = maxLower;
        this.maxUpper = maxUpper;
    }

    /**
     * @return the maxLower
     */
    public long getMaxLower() {
        return maxLower;
    }

    /**
     * @return the maxUpper
     */
    public long getMaxUpper() {
        return maxUpper;
    }

    /**
     * @return the maxLower
     */
    public long getMinLower() {
        return lower;
    }

    /**
     * @return the maxUpper
     */
    public long getMinUpper() {
        return upper;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.MGID#setVal(java.lang.String)
     */
    @Override
    public void setVal(String val) {
        String[] segs = val.split("-");
        super.setVal(segs[0]);
        if (segs.length > 0) {
            segs = segs[1].split(":");
            maxLower = Utils.toLong(segs[0]);
            if (segs.length > 0) {
                maxUpper = Utils.toLong(segs[1]);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (int) (maxLower ^ (maxLower >>> 32));
        result = prime * result + (int) (maxUpper ^ (maxUpper >>> 32));
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        MGIDRange other = (MGIDRange) obj;
        if (maxLower != other.maxLower) {
            return false;
        }
        if (maxUpper != other.maxUpper) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.application.MGID#copy()
     */
    @Override
    public MGIDRange copy() {
        return new MGIDRange(lower, upper, maxLower, maxUpper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return super.toString() + "-" + StringUtils.longHexString(maxLower)
                + ":" + StringUtils.longHexString(maxUpper);
    }
}
