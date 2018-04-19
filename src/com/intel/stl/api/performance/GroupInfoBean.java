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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.Utils;

/**
 */
public class GroupInfoBean implements ITimestamped, Serializable {
    private static final long serialVersionUID = 1L;

    private String groupName;

    private ImageIdBean imageId;

    private long timestamp;

    private int imageInterval;

    private long numInternalPorts; // unsigned int

    private long numExternalPorts; // unsigned int

    private UtilStatsBean internalUtilStats;

    private UtilStatsBean sendUtilStats;

    private UtilStatsBean recvUtilStats;

    private ErrSummaryBean internalErrorMaximums;

    private List<ErrBucketBean> internalErrorPorts;

    private ErrSummaryBean externalErrorMaximums;

    private List<ErrBucketBean> externalErrorPorts;

    private byte maxInternalRate;

    private byte minInternalRate;

    private byte maxExternalRate;

    private byte minExternalRate;

    private int maxInternalMBps;

    private int maxExternalMBps;

    /**
     * @return the groupName
     */
    public String getGroupName() {
        return groupName;
    }

    /**
     * @param groupName
     *            the groupName to set
     */
    public void setGroupName(String groupName) {
        if (groupName.length() > PAConstants.STL_PM_GROUPNAMELEN) {
            throw new IllegalArgumentException(
                    "Invalid string length: " + groupName.length() + " > "
                            + PAConstants.STL_PM_GROUPNAMELEN);
        }

        this.groupName = groupName;
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
     * Note that sweepTimestamp is Unix time (seconds since Jan 1st, 1970)
     *
     * @return the sweepTimestamp
     */
    @Override
    public long getTimestamp() {
        return timestamp;
    }

    /**
     * This field is set at the API level when GroupInfo is retrieved from FE.
     * At that time, the ImageInfo is also retrieved from buffers or from the FE
     * and sweepTimestamp is initialized to sweepStart. Note that sweepStart is
     * Unix time (seconds since Jan 1st, 1970)
     *
     * @param sweepTimestamp
     *            the sweepTimestamp to set
     */
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
    public void setImageInterval(int imageInterval) {
        this.imageInterval = imageInterval;
    }

    /**
     * @return the numInternalPorts
     */
    public long getNumInternalPorts() {
        return numInternalPorts;
    }

    /**
     * @param numInternalPorts
     *            the numInternalPorts to set
     */
    public void setNumInternalPorts(long numInternalPorts) {
        this.numInternalPorts = numInternalPorts;
    }

    /**
     * @param numInternalPorts
     *            the numInternalPorts to set
     */
    public void setNumInternalPorts(int numInternalPorts) {
        this.numInternalPorts = Utils.unsignedInt(numInternalPorts);
    }

    /**
     * @return the numExternalPorts
     */
    public long getNumExternalPorts() {
        return numExternalPorts;
    }

    /**
     * @param numExternalPorts
     *            the numExternalPorts to set
     */
    public void setNumExternalPorts(long numExternalPorts) {
        this.numExternalPorts = numExternalPorts;
    }

    /**
     * @param numExternalPorts
     *            the numExternalPorts to set
     */
    public void setNumExternalPorts(int numExternalPorts) {
        this.numExternalPorts = Utils.unsignedInt(numExternalPorts);
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
     * @return the sendUtilStats
     */
    public UtilStatsBean getSendUtilStats() {
        return sendUtilStats;
    }

    /**
     * @param sendUtilStats
     *            the sendUtilStats to set
     */
    public void setSendUtilStats(UtilStatsBean sendUtilStats) {
        this.sendUtilStats = sendUtilStats;
    }

    /**
     * @return the recvUtilStats
     */
    public UtilStatsBean getRecvUtilStats() {
        return recvUtilStats;
    }

    /**
     * @param recvUtilStats
     *            the recvUtilStats to set
     */
    public void setRecvUtilStats(UtilStatsBean recvUtilStats) {
        this.recvUtilStats = recvUtilStats;
    }

    /**
     * @return the internalErrors
     */
    public ErrStatBean getInternalErrors() {
        ErrStatBean internalErrors = new ErrStatBean();
        internalErrors.setErrorMaximums(internalErrorMaximums);

        internalErrors.setPorts(getInternalErrorPortsAsArray());
        return internalErrors;
    }

    /**
     * @param internalErrors
     *            the internalErrors to set
     */
    public void setInternalErrors(ErrStatBean internalErrors) {
        this.internalErrorMaximums = internalErrors.getErrorMaximums();
        setInternalErrorPorts(internalErrors.getPorts());
    }

    public ErrSummaryBean getInternalErrorMaximums() {
        return internalErrorMaximums;
    }

    public void setInternalErrorMaximums(ErrSummaryBean internalErrorMaximums) {
        this.internalErrorMaximums = internalErrorMaximums;
    }

    public ErrBucketBean[] getInternalErrorPortsAsArray() {
        if (internalErrorPorts == null) {
            internalErrorPorts = new ArrayList<ErrBucketBean>();
        }
        ErrBucketBean[] ebArray = new ErrBucketBean[internalErrorPorts.size()];
        return internalErrorPorts.toArray(ebArray);
    }

    public void setInternalErrorPorts(ErrBucketBean[] internalErrorPorts) {
        List<ErrBucketBean> ebList =
                new ArrayList<ErrBucketBean>(Arrays.asList(internalErrorPorts));
        this.internalErrorPorts = ebList;
    }

    public List<ErrBucketBean> getInternalErrorPorts() {
        return internalErrorPorts;
    }

    public void setInternalErrorPorts(List<ErrBucketBean> internalErrorPorts) {
        this.internalErrorPorts = internalErrorPorts;
    }

    /**
     * @return the externalErrors
     */
    public ErrStatBean getExternalErrors() {
        ErrStatBean externalErrors = new ErrStatBean();
        externalErrors.setErrorMaximums(externalErrorMaximums);
        externalErrors.setPorts(getExternalErrorPortsAsArray());
        return externalErrors;
    }

    /**
     * @param externalErrors
     *            the externalErrors to set
     */
    public void setExternalErrors(ErrStatBean externalErrors) {
        this.externalErrorMaximums = externalErrors.getErrorMaximums();
        setExternalErrorPorts(externalErrors.getPorts());
    }

    public ErrSummaryBean getExternalErrorMaximums() {
        return externalErrorMaximums;
    }

    public void setExternalErrorMaximums(ErrSummaryBean externalErrorMaximums) {
        this.externalErrorMaximums = externalErrorMaximums;
    }

    public ErrBucketBean[] getExternalErrorPortsAsArray() {
        if (externalErrorPorts == null) {
            externalErrorPorts = new ArrayList<ErrBucketBean>();
        }
        ErrBucketBean[] ebArray = new ErrBucketBean[externalErrorPorts.size()];
        return externalErrorPorts.toArray(ebArray);
    }

    public void setExternalErrorPorts(ErrBucketBean[] externalErrorPorts) {
        List<ErrBucketBean> ebList =
                new ArrayList<ErrBucketBean>(Arrays.asList(externalErrorPorts));
        this.externalErrorPorts = ebList;
    }

    public List<ErrBucketBean> getExternalErrorPorts() {
        return externalErrorPorts;
    }

    public void setExternalErrorPorts(List<ErrBucketBean> externalErrorPorts) {
        this.externalErrorPorts = externalErrorPorts;
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
     * @return the maxExternalRate
     */
    public byte getMaxExternalRate() {
        return maxExternalRate;
    }

    /**
     * @param maxExternalRate
     *            the maxExternalRate to set
     */
    public void setMaxExternalRate(byte maxExternalRate) {
        this.maxExternalRate = maxExternalRate;
    }

    /**
     * @return the minExternalRate
     */
    public byte getMinExternalRate() {
        return minExternalRate;
    }

    /**
     * @param minExternalRate
     *            the minExternalRate to set
     */
    public void setMinExternalRate(byte minExternalRate) {
        this.minExternalRate = minExternalRate;
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
     * @return the maxExternalMBps
     */
    public int getMaxExternalMBps() {
        return maxExternalMBps;
    }

    /**
     * @param maxExternalMBps
     *            the maxExternalMBps to set
     */
    public void setMaxExternalMBps(int maxExternalMBps) {
        this.maxExternalMBps = maxExternalMBps;
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
        result = prime * result
                + ((groupName == null) ? 0 : groupName.hashCode());
        result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
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
        GroupInfoBean other = (GroupInfoBean) obj;
        if (groupName == null) {
            if (other.groupName != null) {
                return false;
            }
        } else if (!groupName.equals(other.groupName)) {
            return false;
        }
        if (imageId == null) {
            if (other.imageId != null) {
                return false;
            }
        } else if (!imageId.equals(other.imageId)) {
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
        return "GroupInfoBean [groupName=" + groupName + ", imageId=" + imageId
                + ", numInternalPorts=" + numInternalPorts
                + ", numExternalPorts=" + numExternalPorts
                + ", internalUtilStats=" + internalUtilStats
                + ", sendUtilStats=" + sendUtilStats + ", recvUtilStats="
                + recvUtilStats + ", internalErrors=" + getInternalErrors()
                + ", externalErrors=" + getExternalErrors()
                + ", maxInternalRate=" + maxInternalRate + ", minInternalRate="
                + minInternalRate + ", maxExternalRate=" + maxExternalRate
                + ", minExternalRate=" + minExternalRate + ", maxInternalMBps="
                + maxInternalMBps + ", maxExternalMBps=" + maxExternalMBps
                + "]";
    }

}
