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

import java.io.Serializable;

import javax.xml.bind.annotation.XmlValue;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.Utils;
import com.intel.stl.api.management.IAttribute;
import com.intel.stl.api.management.XMLConstants;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;

public class MGID implements IAttribute, Serializable {
    private static final long serialVersionUID = 6558030997560055165L;

    protected String type;

    protected long lower;

    protected long upper;

    public MGID() {
        this(0, 0);
    }

    /**
     * Description:
     * 
     * @param lower
     * @param upper
     */
    public MGID(long lower, long upper) {
        this(XMLConstants.MGID, lower, upper);
    }

    public MGID(String type, long lower, long upper) {
        this.type = type;
        this.lower = lower;
        this.upper = upper;
    }

    /**
     * @return the lower
     */
    public long getLower() {
        return lower;
    }

    /**
     * @return the upper
     */
    public long getUpper() {
        return upper;
    }

    /**
     * @param val
     *            the xml string to set
     */
    @XmlValue
    public void setVal(String val) {
        String[] segs = val.split(":");
        lower = Utils.toLong(segs[0]);
        if (segs.length > 0) {
            upper = Utils.toLong(segs[1]);
        }
    }

    public String getVal() {
        return toString();
    }

    /**
     * @return the type
     */
    @Override
    public String getType() {
        return type;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.IAttribute#installApplication(com.intel.
     * stl.api.management.applications.Application)
     */
    @Override
    public void installApplication(Application app) {
        app.addMGID(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.IAttribute#installDevieGroup(com.intel.stl
     * .api.management.devicegroups.DeviceGroup)
     */
    @Override
    public void installDevieGroup(DeviceGroup group) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.IAttribute#installVirtualFabric(com.intel
     * .stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void installVirtualFabric(VirtualFabric vf) {
        throw new UnsupportedOperationException();
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
        result = prime * result + (int) (lower ^ (lower >>> 32));
        result = prime * result + (int) (upper ^ (upper >>> 32));
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
        MGID other = (MGID) obj;
        if (lower != other.lower) {
            return false;
        }
        if (upper != other.upper) {
            return false;
        }
        return true;
    }

    @Override
    public MGID copy() {
        return new MGID(lower, upper);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return StringUtils.longHexString(lower) + ":"
                + StringUtils.longHexString(upper);
    }

}
