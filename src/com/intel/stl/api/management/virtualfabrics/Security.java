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

import com.intel.stl.api.management.BooleanNode;
import com.intel.stl.api.management.XMLConstants;

/**
 * When On (1) the FM will provide security within this VF and between other
 * VFs. When On, LimitedMembers cannot talk to each other in the VF. When Off
 * (0) the FM is free to manage Routes and PKeys as it chooses and there are no
 * guarantees. When Off (0) LimitedMembers can talk to each other in the VF.
 */
public class Security extends BooleanNode {
    private static final long serialVersionUID = 2656503575588555136L;

    /**
     * Description:
     * 
     * @param type
     */
    public Security() {
        this(false);
    }

    /**
     * Description:
     * 
     * @param type
     * @param isSelected
     */
    public Security(boolean isSelected) {
        super(XMLConstants.SECURITY, isSelected);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.BooleanNode#copy()
     */
    @Override
    public Security copy() {
        return new Security(isSelected());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.BooleanNode#installVirtualFabric(com.intel
     * .stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void installVirtualFabric(VirtualFabric vf) {
        vf.setSecurity(this);
    }

}
