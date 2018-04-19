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

package com.intel.stl.api.configuration;

import static com.intel.stl.api.configuration.ResourceType.HFI;
import static com.intel.stl.api.configuration.ResourceType.PORT;
import static com.intel.stl.api.configuration.ResourceType.SWITCH;

import java.util.HashMap;
import java.util.Map;

public enum ResourceCategory {

    NODE_INFO(HFI, SWITCH),
    DEVICE_GROUPS(HFI, SWITCH),
    LINK_WIDTH(PORT),
    LINK_WIDTH_DOWNGRADE(PORT),
    LINK_SPEED(PORT),
    LINK_CONNECTED_TO(PORT),
    NEIGHBOR_MODE(PORT),
    NODE_PORT_INFO(HFI, SWITCH),
    PORT_INFO(PORT),
    PORT_LINK_MODE(PORT),
    PORT_LTP_CRC_MODE(PORT),
    PORT_MODE(PORT),
    PORT_PACKET_FORMAT(PORT),
    PORT_ERROR_ACTIONS(PORT),
    PORT_BUFFER_UNITS(PORT),
    PORT_IPADDR(PORT),
    PORT_SUBNET(PORT),
    PORT_CAPABILITIES(PORT),
    PORT_DIAGNOSTICS(PORT),
    PORT_MANAGEMENT(PORT),
    PORT_PARTITION_ENFORCEMENT(PORT),
    FLIT_CTRL_INTERLEAVE(PORT),
    FLIT_CTRL_PREEMPTION(PORT),
    HOQLIFE_CHART(PORT),
    VIRTUAL_LANE(PORT),
    VL_STALL_CHART(PORT),
    MTU_CHART(PORT),
    // CABLE_INFO is PORT ResourceType but not necessarily from PortInfo data
    // structure.
    CABLE_INFO(PORT),
    SC2SLMT_CHART(HFI, SWITCH),
    SC2VLTMT_CHART(PORT),
    SC2VLNTMT_CHART(PORT),
    LINK_DOWN_ERROR_LOG(PORT),
    NEIGHBOR_LINK_DOWN_ERROR_LOG(PORT),

    SWITCH_INFO(SWITCH),
    SWITCH_FORWARDING(SWITCH),
    SWITCH_ROUTING(SWITCH),
    SWITCH_IPADDR(SWITCH),
    SWITCH_PARTITION_ENFORCEMENT(SWITCH),
    SWITCH_ADAPTIVE_ROUTING(SWITCH),
    MFT_TABLE(SWITCH),
    LFT_HISTOGRAM(SWITCH),
    LFT_TABLE(SWITCH);

    private final static Map<String, ResourceCategory> categoryMap =
            new HashMap<String, ResourceCategory>();
    static {
        for (ResourceCategory rc : ResourceCategory.values()) {
            categoryMap.put(rc.name(), rc);
        }
    };

    private final ResourceType[] resourceTypes;

    private ResourceCategory(ResourceType... resourceTypes) {
        this.resourceTypes = resourceTypes;
    }

    public boolean isApplicableTo(ResourceType resourceType) {
        for (int i = 0; i < resourceTypes.length; i++) {
            if (resourceTypes[i] == resourceType) {
                return true;
            }
        }
        return false;
    }

    public static ResourceCategory getResourceCategoryFor(String categoryName) {
        ResourceCategory res = categoryMap.get(categoryName);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Unsupported ResourceCategory '"
                    + categoryName + "'");
        }
    }
}
