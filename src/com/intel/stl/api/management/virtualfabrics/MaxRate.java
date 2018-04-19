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

import com.intel.stl.api.configuration.StaticRate;
import com.intel.stl.api.management.WrapperNode;
import com.intel.stl.api.management.XMLConstants;

/**
 * Maximum static rate for SM to return in any PathRecord or Multicast group for
 * the VirtualFabric. Similar behaviors to MaxMTU. The value can also be stated
 * as Unlimited.
 * If not specified the default MaxMTU will be Unlimited.
 */
public class MaxRate extends WrapperNode<StaticRate> {
    private static final long serialVersionUID = 4236692336423150546L;

    public MaxRate() {
        this(StaticRate.UNLIMITED);
    }

    /**
     * Description:
     *
     * @param value
     */
    public MaxRate(StaticRate value) {
        super(XMLConstants.MAX_RATE, value);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.WrapperNode#valueOf(java.lang.String)
     */
    @Override
    protected StaticRate valueOf(String str) {
    	return StaticRate.getStaticRate(str);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.WrapperNode#valueString(java.lang.Object)
     */
    @Override
    protected String valueString(StaticRate value) {
    	return value.getName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IAttribute#copy()
     */
    @Override
    public MaxRate copy() {
        return new MaxRate(value);
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "MaxRate [type=" + type + ", value=" + value + "]";
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
        vf.setMaxRate(this);
    }

    public static MaxRate[] values() {
        StaticRate[] rates = StaticRate.values();
        MaxRate[] res = new MaxRate[rates.length];
        for (int i = 0; i < res.length; i++) {
            res[i] = new MaxRate(rates[i]);
        }
        return res;
    }
}
