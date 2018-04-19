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

import java.util.HashMap;
import java.util.Map;

import com.intel.stl.ui.common.STLConstants;

public enum PropertyGroupViz {
    GENERAL(STLConstants.K0397_GENERAL),
    SWITCH_INFO(STLConstants.K0436_SWITCH_INFO),
    ROUTING_INFO(STLConstants.K0438_ROUTING_INFO),
    DEVICE_GROUP(STLConstants.K0367_DEVICE_GRP),
    MFT(STLConstants.K0399_MULTI_FWD_TABLE),
    LFT(STLConstants.K0398_LINEAR_FWD_TABLE),
    DEVICE_INFO(STLConstants.K0307_DEV_INFO_TITLE),
    PORT_LINK(STLConstants.K0318_PORT_LINK_TITLE),
    LINK_CONNECTION(STLConstants.K0457_LINK_CONNECTION),
    PORT_CAPABILITY(STLConstants.K0328_PORT_CAPABILITY_TITLE),
    VIRTUAL_LANE(STLConstants.K0342_PORT_VL_TITLE),
    PORT_DIAGNOSTICS(STLConstants.K0350_PORT_DIAG_TITLE),
    PORT_PARTITION_ENFORCEMENT(STLConstants.K0302_PORT_PART_ENFORCE_TITLE),
    PORT_MANAGEMENT(STLConstants.K0358_PORT_MANAGEMENT_TITLE),
    FLIT_CONTROL(STLConstants.K0775_FLIT_CTRL),
    PORT_ERROR_ACTIONS(STLConstants.K1041_PORT_ERROR_ACTIONS),
    MISCELLANEOUS(STLConstants.K0822_MISCELLANEOUS),
    MTU(STLConstants.K1066_MTU_SERIES),
    HOQLIFE(STLConstants.K1069_HOQLIFE_BY_VL),
    VL_STALL_COUNT(STLConstants.K1070_VLSTALL_SERIES),
    CABLE_INFO(STLConstants.K1071_QSFP_CABLE_INFO),
    SC2SLMT(STLConstants.K1104_SC2SL_MAPPING_TABLE),
    SC2VLTMT(STLConstants.K1107_SC2VLT_MAPPING_TABLE),
    SC2VLNTMT(STLConstants.K1108_SC2VLNT_MAPPING_TABLE),
    LINK_DOWN_ERROR_REASON(STLConstants.K1111_LINK_DOWN_ERROR_LOG);

    private final static Map<String, PropertyGroupViz> groupNameMap =
            new HashMap<String, PropertyGroupViz>();
    static {
        for (PropertyGroupViz pgv : PropertyGroupViz.values()) {
            groupNameMap.put(pgv.name(), pgv);
        }
    };

    private final static Map<String, PropertyGroupViz> groupTitleMap =
            new HashMap<String, PropertyGroupViz>();
    static {
        for (PropertyGroupViz pgv : PropertyGroupViz.values()) {
            groupTitleMap.put(pgv.getTitle(), pgv);
        }
    };

    private final STLConstants title;

    private PropertyGroupViz(STLConstants title) {
        this.title = title;
    }

    public String getTitle() {
        return title.getValue();
    }

    public static PropertyGroupViz getPropertyGroupViz(String name) {
        PropertyGroupViz res = groupNameMap.get(name);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Couldn't find PropertyGroup '"
                    + name + "'");
        }
    }

    public static PropertyGroupViz getPropertyGroupVizByTitle(String title) {
        PropertyGroupViz res = groupTitleMap.get(title);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find PropertyGroup with title '" + groupTitleMap
                            + "'");
        }
    }
}
