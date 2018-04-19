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

package com.intel.stl.api.management.virtualfabrics;

import java.text.NumberFormat;

import com.intel.stl.api.management.WrapperNode;
import com.intel.stl.api.management.XMLConstants;

/**
 * 1-100% This is the minimum percentage of bandwidth which should be given to
 * this Virtual Fabric relative to other low priority Virtual Fabrics. When
 * there is no contention, this Virtual Fabric could get more than this amount.
 * If unspecified, the SM evenly distributes remaining among all the Virtual
 * Fabrics with unspecified Bandwidth. Total Bandwidth for all enabled Virtual
 * Fabrics with QOS enabled must not exceed 100%. If HighPriority is specified,
 * this field is ignored.
 * 
 */
public class Bandwidth extends WrapperNode<Double> {
    private static final long serialVersionUID = 3254223451844225287L;

    private static final NumberFormat format = NumberFormat
            .getPercentInstance();

    public Bandwidth() {
        this(null);
    }

    /**
     * Description:
     * 
     * @param value
     */
    public Bandwidth(Double value) {
        super(XMLConstants.BANDWIDTH, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.WrapperNode#valueOf(java.lang.String)
     */
    @Override
    protected Double valueOf(String str) {
        Double res = null;
        if (str.charAt(str.length() - 1) == '%') {
            res = Double.parseDouble(str.substring(0, str.length() - 1));
            if (res >= 1 && res <= 100) {
                return res / 100f;
            } else {
                throw new IllegalArgumentException("Invalid value range '"
                        + str + "'. Expect value in 1-100%");
            }
        }
        throw new IllegalArgumentException("Invalid format '" + str
                + "'. Expect value in 1-100%");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.WrapperNode#valueString(java.lang.Object)
     */
    @Override
    protected String valueString(Double value) {
        return format.format(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.IAttribute#copy()
     */
    @Override
    public Bandwidth copy() {
        return new Bandwidth(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Bandwidth [type=" + type + ", value=" + value + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.WrapperNode#installVirtualFabric(com.intel
     * .stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void installVirtualFabric(VirtualFabric vf) {
        vf.setBandwidth(this);
    }

}
