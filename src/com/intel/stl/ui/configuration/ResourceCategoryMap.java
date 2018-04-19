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

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.STLConstants.K0007_SUBNET;
import static com.intel.stl.ui.common.STLConstants.K0026_LID;
import static com.intel.stl.ui.common.STLConstants.K0058_PORT_MASK;
import static com.intel.stl.ui.common.STLConstants.K0084_LINK_WIDTH_DOWNGRADE;
import static com.intel.stl.ui.common.STLConstants.K0302_PORT_PART_ENFORCE_TITLE;
import static com.intel.stl.ui.common.STLConstants.K0307_DEV_INFO_TITLE;
import static com.intel.stl.ui.common.STLConstants.K0319_PORT_LINK_WIDTH;
import static com.intel.stl.ui.common.STLConstants.K0323_PORT_LINK_SPEED;
import static com.intel.stl.ui.common.STLConstants.K0324_PORT_LINK_CONN_TO;
import static com.intel.stl.ui.common.STLConstants.K0327_PORT_INDEX;
import static com.intel.stl.ui.common.STLConstants.K0392_PORT_PROPERTIES;
import static com.intel.stl.ui.common.STLConstants.K0401_TIME;
import static com.intel.stl.ui.common.STLConstants.K0435_FORWARDING_INFO;
import static com.intel.stl.ui.common.STLConstants.K0437_SWITCH_PROPERTIES;
import static com.intel.stl.ui.common.STLConstants.K0439_ROUTING_MODE;
import static com.intel.stl.ui.common.STLConstants.K0440_IP_ADDR;
import static com.intel.stl.ui.common.STLConstants.K0443_ADAPTIVE_ROUTING;
import static com.intel.stl.ui.common.STLConstants.K0455_PORT_LINK_MODE;
import static com.intel.stl.ui.common.STLConstants.K0456_PORT_LTP_CRC_MODE;
import static com.intel.stl.ui.common.STLConstants.K0490_LINK_DOWN_REASON;
import static com.intel.stl.ui.common.STLConstants.K0497_NEIGHBOR_MODE;
import static com.intel.stl.ui.common.STLConstants.K0763_INTERLEAVE;
import static com.intel.stl.ui.common.STLConstants.K0764_PREEMPTION;
import static com.intel.stl.ui.common.STLConstants.K0815_PORT_MODE;
import static com.intel.stl.ui.common.STLConstants.K0817_BUFFER_UNITS;
import static com.intel.stl.ui.common.STLConstants.K0827_PACKET_FORMAT;
import static com.intel.stl.ui.common.STLConstants.K1300_NEIGHBOR_LINKDOWN_REASON;

import java.util.EnumMap;

import com.intel.stl.api.configuration.ResourceCategory;

public enum ResourceCategoryMap {

    NODE_INFO(ResourceCategory.NODE_INFO, K0307_DEV_INFO_TITLE.getValue(),
            null, NodeInfoProcessor.class),
    DEVICE_GROUPS(ResourceCategory.DEVICE_GROUPS, null, null,
            DeviceGroupsProcessor.class),
    LINK_WIDTH(ResourceCategory.LINK_WIDTH, K0319_PORT_LINK_WIDTH.getValue(),
            null, LinkWidthProcessor.class),
    LINK_WIDTH_DOWNGRADE(ResourceCategory.LINK_WIDTH_DOWNGRADE,
            K0084_LINK_WIDTH_DOWNGRADE.getValue(), null,
            LinkWidthDowngradeProcessor.class),
    LINK_SPEED(ResourceCategory.LINK_SPEED, K0323_PORT_LINK_SPEED.getValue(),
            null, LinkSpeedProcessor.class),
    LINK_CONNECTED_TO(ResourceCategory.LINK_CONNECTED_TO,
            K0324_PORT_LINK_CONN_TO.getValue(), null,
            LinkConnectedToProcessor.class),
    NEIGHBOR_MODE(ResourceCategory.NEIGHBOR_MODE, K0497_NEIGHBOR_MODE
            .getValue(), null, NeighborModeProcessor.class),
    NODE_PORT_INFO(ResourceCategory.NODE_PORT_INFO, K0392_PORT_PROPERTIES
            .getValue(), null, NodePortInfoProcessor.class),
    PORT_INFO(ResourceCategory.PORT_INFO, null, null, PortInfoProcessor.class),
    PORT_LINK_MODE(ResourceCategory.PORT_LINK_MODE, K0455_PORT_LINK_MODE
            .getValue(), null, LinkModeProcessor.class),
    PORT_LTP_CRC_MODE(ResourceCategory.PORT_LTP_CRC_MODE,
            K0456_PORT_LTP_CRC_MODE.getValue(), null, LtpCrcModeProcessor.class),
    PORT_MODE(ResourceCategory.PORT_MODE, K0815_PORT_MODE.getValue(), null,
            PortModeProcessor.class),
    PORT_PACKET_FORMAT(ResourceCategory.PORT_PACKET_FORMAT, K0827_PACKET_FORMAT
            .getValue(), null, PacketFormatProcessor.class),
    PORT_ERROR_ACTIONS(ResourceCategory.PORT_ERROR_ACTIONS, null, null,
            PortErrorActionsProcessor.class),
    PORT_BUFFER_UNITS(ResourceCategory.PORT_BUFFER_UNITS, K0817_BUFFER_UNITS
            .getValue(), null, BufferUnitsProcessor.class),
    PORT_IPADDR(ResourceCategory.PORT_IPADDR, K0440_IP_ADDR.getValue(), null,
            PortIPAddressProcessor.class),
    PORT_SUBNET(ResourceCategory.PORT_SUBNET, K0007_SUBNET.getValue(), null,
            PortSubnetProcessor.class),
    PORT_CAPABILITIES(ResourceCategory.PORT_CAPABILITIES, null, null,
            PortCapabilitiesProcessor.class),
    PORT_DIAGNOSTICS(ResourceCategory.PORT_DIAGNOSTICS, null, null,
            DiagnosticsProcessor.class),
    PORT_MANAGEMENT(ResourceCategory.PORT_MANAGEMENT, null, null,
            PortManagementProcessor.class),
    PORT_PARTITION_ENFORCEMENT(ResourceCategory.PORT_PARTITION_ENFORCEMENT,
            null, null, PortPartitionEnforcementProcessor.class),
    FLIT_CTRL_INTERLEAVE(ResourceCategory.FLIT_CTRL_INTERLEAVE,
            K0763_INTERLEAVE.getValue(), null,
            FlitControlInterleaveProcessor.class),
    FLIT_CTRL_PREEMPTION(ResourceCategory.FLIT_CTRL_PREEMPTION,
            K0764_PREEMPTION.getValue(), null,
            FlitControlPreemptionProcessor.class),
    VIRTUAL_LANE(ResourceCategory.VIRTUAL_LANE, null, null,
            VirtualLaneProcessor.class),
    SWITCH_INFO(ResourceCategory.SWITCH_INFO, K0437_SWITCH_PROPERTIES
            .getValue(), null, SwitchInfoProcessor.class),
    SWITCH_FORWARDING(ResourceCategory.SWITCH_FORWARDING, K0435_FORWARDING_INFO
            .getValue(), null, ForwardingInfoProcessor.class),
    SWITCH_ROUTING(ResourceCategory.SWITCH_ROUTING, K0439_ROUTING_MODE
            .getValue(), null, SwitchRoutingProcessor.class),
    SWITCH_IPADDR(ResourceCategory.SWITCH_IPADDR, K0440_IP_ADDR.getValue(),
            null, SwitchIPAddressProcessor.class),
    SWITCH_PARTITION_ENFORCEMENT(ResourceCategory.SWITCH_PARTITION_ENFORCEMENT,
            K0302_PORT_PART_ENFORCE_TITLE.getValue(), null,
            SwitchPartitionEnforcementProcessor.class),
    SWITCH_ADAPTIVE_ROUTING(ResourceCategory.SWITCH_ADAPTIVE_ROUTING,
            K0443_ADAPTIVE_ROUTING.getValue(), null,
            AdaptiveRoutingProcessor.class),
    MFT_TABLE(ResourceCategory.MFT_TABLE, K0026_LID.getValue(), K0058_PORT_MASK
            .getValue(), MFTTableProcessor.class),
    LFT_HISTOGRAM(ResourceCategory.LFT_HISTOGRAM, null, null,
            LFTSeriesProcessor.class),
    LFT_TABLE(ResourceCategory.LFT_TABLE, K0026_LID.getValue(),
            K0327_PORT_INDEX.getValue(), LFTTableProcessor.class),
    MTU_CHART(ResourceCategory.MTU_CHART, null, null, MTUByVLProcessor.class),
    HOQLIFE_CHART(ResourceCategory.HOQLIFE_CHART, null, null,
            HoQLifeProcessor.class),
    VL_STALL_CHART(ResourceCategory.VL_STALL_CHART, null, null,
            VLStallCountByVLProcessor.class),
    CABLE_INFO(ResourceCategory.CABLE_INFO, null, null,
            CableInfoProcessor.class),
    SC2SLMT_CHART(ResourceCategory.SC2SLMT_CHART, null, null,
            SC2SLMTProcessor.class),
    SC2VLTMT_CHART(ResourceCategory.SC2VLTMT_CHART, null, null,
            SC2VLTMTProcessor.class),
    SC2VLNTMT_CHART(ResourceCategory.SC2VLNTMT_CHART, null, null,
            SC2VLNTMTProcessor.class),
    LINK_DOWN_ERROR_LOG(ResourceCategory.LINK_DOWN_ERROR_LOG, K0401_TIME
            .getValue(), K0490_LINK_DOWN_REASON.getValue(),
            LinkDownErrorLogProcessor.class),
    NEIGHBOR_LINK_DOWN_ERROR_LOG(ResourceCategory.NEIGHBOR_LINK_DOWN_ERROR_LOG,
            K0401_TIME.getValue(), K1300_NEIGHBOR_LINKDOWN_REASON.getValue(),
            NeighborLinkDownErrorLogProcessor.class);

    private final static EnumMap<ResourceCategory, ResourceCategoryMap> resourceCategoryMap =
            new EnumMap<ResourceCategory, ResourceCategoryMap>(
                    ResourceCategory.class);
    static {
        for (ResourceCategoryMap rcm : ResourceCategoryMap.values()) {
            resourceCategoryMap.put(rcm.resourceCategory, rcm);
        }
    };

    private final ResourceCategory resourceCategory;

    private final String defaultKeyHeader;

    private final String defaultValueHeader;

    private final Class<? extends ResourceCategoryProcessor> processor;

    private ResourceCategoryProcessor processorInstance;

    private ResourceCategoryMap(ResourceCategory category,
            String defaultKeyHeader, String defaultValueHeader,
            Class<? extends ResourceCategoryProcessor> processor) {
        this.resourceCategory = category;
        this.defaultKeyHeader = defaultKeyHeader;
        this.defaultValueHeader = defaultValueHeader;
        this.processor = processor;
    }

    public ResourceCategory getResourceCategory() {
        return resourceCategory;
    }

    public String getDefaultKeyHeader() {
        return defaultKeyHeader;
    }

    public String getDefaultValueHeader() {
        return defaultValueHeader;
    }

    public ResourceCategoryProcessor getProcessor() {
        if (processorInstance == null) {
            try {
                processorInstance = processor.newInstance();
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return processorInstance;
    }

    public static ResourceCategoryMap getResourceCategoryMapFor(
            ResourceCategory resourceCategory) {
        return resourceCategoryMap.get(resourceCategory);
    }
}
