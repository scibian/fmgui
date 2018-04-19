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

import com.intel.stl.api.configuration.MTUSize;
import com.intel.stl.api.management.WrapperNode;
import com.intel.stl.api.management.XMLConstants;

/**
 * Maximum MTU for SM to return in any PathRecord or Multicast group for the
 * VirtualFabric. Actual values may be further reduced by Hardware capabilities
 * or if the PathRecord or Multicast group is requested to have a smaller MTU.
 * However, SM will consider it an error to create a Multicast group with MTU
 * larger than that of the VirtualFabric. The value can also be stated as
 * Unlimited.
 * If not specified the default MaxMTU will be Unlimited.
 */
public class MaxMtu extends WrapperNode<MTUSize> {
    private static final long serialVersionUID = 4236692336423150546L;

    public MaxMtu() {
        this(MTUSize.INVALID);
    }

    /**
     * Description:
     * 
     * @param value
     */
    public MaxMtu(MTUSize value) {
        super(XMLConstants.MAX_MTU, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.WrapperNode#valueOf(java.lang.String)
     */
    @Override
    protected MTUSize valueOf(String str) {
        if (str.equalsIgnoreCase(XMLConstants.UNLIMITED)) {
            return MTUSize.INVALID;
        } else {
            return MTUSize.getMTUSize(str);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.WrapperNode#valueString(java.lang.Object)
     */
    @Override
    protected String valueString(MTUSize value) {
        if (value == MTUSize.INVALID) {
            return XMLConstants.UNLIMITED;
        } else {
            return value.getName();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.IAttribute#copy()
     */
    @Override
    public MaxMtu copy() {
        return new MaxMtu(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MaxMtu [type=" + type + ", value=" + value + "]";
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
        vf.setMaxMtu(this);
    }

    public static MaxMtu[] values() {
        MTUSize[] mtus = MTUSize.values();
        MaxMtu[] res = new MaxMtu[mtus.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = new MaxMtu(mtus[i]);
        }
        return res;
    }
}
