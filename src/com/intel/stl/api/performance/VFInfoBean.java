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
import java.util.Date;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;

/**
 */
public class VFInfoBean implements ITimestamped, Serializable {
    private static final long serialVersionUID = 1L;

    private String vfName;

    private ImageIdBean imageId;

    private long numPorts; // unsigned int

    private UtilStatsBean internalUtilStats;

    private CategoryStatBean internalCategoryStats;

    private byte maxInternalRate;

    private byte minInternalRate;

    private int maxInternalMBps;

    private long timestamp;

    private int imageInterval;

    /**
     * @return the groupName
     */
    public String getVfName() {
        return vfName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setVfName(String vfName) {
        if (vfName.length() > PAConstants.STL_PM_VFNAMELEN) {
            throw new IllegalArgumentException("Invalid string length: "
                    + vfName.length() + " > " + PAConstants.STL_PM_VFNAMELEN);
        }

        this.vfName = vfName;
    }

    /**
     * @return the imageId
     */
    public ImageIdBean getImageId() {
        return imageId;
    }

    /**
     * @param imageId
     *            the imageId to set
     */
    public void setImageId(ImageIdBean imageId) {
        this.imageId = imageId;
    }

    /**
     * @return the numPorts
     */
    public long getNumPorts() {
        return numPorts;
    }

    /**
     * @param numPorts
     *            the numPorts to set
     */
    public void setNumPorts(long numPorts) {
        this.numPorts = numPorts;
    }

    /**
     * @param numPorts
     *            the numPorts to set
     */
    public void setNumPorts(int numPorts) {
        this.numPorts = Utils.unsignedInt(numPorts);
    }

    /**
     * @return the internalUtilStats
     */
    public UtilStatsBean getInternalUtilStats() {
        return internalUtilStats;
    }

    /**
     * @param internalUtilStats
     *            the internalUtilStats to set
     */
    public void setInternalUtilStats(UtilStatsBean internalUtilStats) {
        this.internalUtilStats = internalUtilStats;
    }

    /**
     * @return the internalCategoryStats
     */
    public CategoryStatBean getInternalCategoryStats() {
        return internalCategoryStats;
    }

    /**
     * @param internalCategoryStats
     *            the internalCategoryStats to set
     */
    public void setInternalCategoryStats(
            CategoryStatBean internalCategoryStats) {
        this.internalCategoryStats = internalCategoryStats;
    }

    /**
     * @return the maxInternalRate
     */
    public byte getMaxInternalRate() {
        return maxInternalRate;
    }

    /**
     * @param maxInternalRate
     *            the maxInternalRate to set
     */
    public void setMaxInternalRate(byte maxInternalRate) {
        this.maxInternalRate = maxInternalRate;
    }

    /**
     * @return the minInternalRate
     */
    public byte getMinInternalRate() {
        return minInternalRate;
    }

    /**
     * @param minInternalRate
     *            the minInternalRate to set
     */
    public void setMinInternalRate(byte minInternalRate) {
        this.minInternalRate = minInternalRate;
    }

    /**
     * @return the maxInternalMBps
     */
    public int getMaxInternalMBps() {
        return maxInternalMBps;
    }

    /**
     * @param maxInternalMBps
     *            the maxInternalMBps to set
     */
    public void setMaxInternalMBps(int maxInternalMBps) {
        this.maxInternalMBps = maxInternalMBps;
    }

    /**
     * Note that sweepTimestamp is Unix time (seconds since Jan 1st, 1970)
     *
     * @return the sweepTimestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * This field is set at the API level when VFInfo is retrieved from FE. At
     * that time, the ImageInfo is also retrieved from buffers or from the FE
     * and sweepTimestamp is initialized to sweepStart. Note that sweepStart is
     * Unix time (seconds since Jan 1st, 1970)
     *
     * @param sweepTimestamp
     *            the sweepTimestamp to set
     */
    @Override
    public void setTimestamp(long sweepTimestamp) {
        this.timestamp = sweepTimestamp;
    }

    /**
     *
     * <i>Description:</i> returns sweepTimestamp as Date
     *
     * @return sweepStart converted to Date
     */
    @Override
    public Date getTimestampDate() {
        return Utils.convertFromUnixTime(timestamp);
    }

    /**
     * @return the imageInterval
     */
    @Override
    public int getImageInterval() {
        return imageInterval;
    }

    /**
     * @param imageInterval
     *            the imageInterval to set
     */
    @Override
    public void setImageInterval(int imageInterval) {
        this.imageInterval = imageInterval;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
        result = prime * result + ((vfName == null) ? 0 : vfName.hashCode());
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
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        VFInfoBean other = (VFInfoBean) obj;
        if (imageId == null) {
            if (other.imageId != null) {
                return false;
            }
        } else if (!imageId.equals(other.imageId)) {
            return false;
        }
        if (vfName == null) {
            if (other.vfName != null) {
                return false;
            }
        } else if (!vfName.equals(other.vfName)) {
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VFInfoBean [vfName=" + vfName + ", imageId=" + imageId
                + ", numPorts=" + numPorts + ", internalUtilStats="
                + internalUtilStats + ", internalCategoryStats="
                + internalCategoryStats + ", maxInternalRate="
                + StringUtils.byteHexString(maxInternalRate)
                + ", minInternalRate="
                + StringUtils.byteHexString(minInternalRate)
                + ", maxInternalMBps=" + maxInternalMBps + "]";
    }
}
