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

package com.intel.stl.ui.model;

import com.intel.stl.api.configuration.ResourceType;
import com.intel.stl.ui.common.STLConstants;

public enum ResourceTypeViz {

    HFI(ResourceType.HFI, STLConstants.K0005_HOST_FABRIC_INTERFACE.getValue()),
    PORT(ResourceType.PORT, STLConstants.K1035_CONFIGURATION_PORT.getValue()),
    SWITCH(ResourceType.SWITCH, STLConstants.K0004_SWITCH.getValue());

    private final ResourceType resourceType;

    private final String value;

    private ResourceTypeViz(ResourceType resourceType, String value) {
        this.resourceType = resourceType;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public static ResourceTypeViz getResourceTypeVizFor(
            ResourceType resourceType) {
        for (ResourceTypeViz rtv : ResourceTypeViz.values()) {
            if (rtv.resourceType == resourceType) {
                return rtv;
            }
        }
        return null;
    }

}
