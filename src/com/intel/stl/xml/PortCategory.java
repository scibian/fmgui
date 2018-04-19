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

package com.intel.stl.xml;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for PortCategory.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="PortCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="LINK_WIDTH"/>
 *     &lt;enumeration value="LINK_WIDTH_DOWNGRADE"/>
 *     &lt;enumeration value="LINK_SPEED"/>
 *     &lt;enumeration value="LINK_CONNECTED_TO"/>
 *     &lt;enumeration value="NEIGHBOR_MODE"/>
 *     &lt;enumeration value="PORT_INFO"/>
 *     &lt;enumeration value="PORT_LINK_MODE"/>
 *     &lt;enumeration value="PORT_LTP_CRC_MODE"/>
 *     &lt;enumeration value="PORT_ERROR_ACTIONS"/>
 *     &lt;enumeration value="PORT_MODE"/>
 *     &lt;enumeration value="PORT_PACKET_FORMAT"/>
 *     &lt;enumeration value="PORT_BUFFER_UNITS"/>
 *     &lt;enumeration value="PORT_IPADDR"/>
 *     &lt;enumeration value="PORT_SUBNET"/>
 *     &lt;enumeration value="PORT_CAPABILITIES"/>
 *     &lt;enumeration value="PORT_DIAGNOSTICS"/>
 *     &lt;enumeration value="PORT_MANAGEMENT"/>
 *     &lt;enumeration value="PORT_PARTITION_ENFORCEMENT"/>
 *     &lt;enumeration value="FLIT_CTRL_INTERLEAVE"/>
 *     &lt;enumeration value="FLIT_CTRL_PREEMPTION"/>
 *     &lt;enumeration value="VIRTUAL_LANE"/>
 *     &lt;enumeration value="MTU_CHART"/>
 *     &lt;enumeration value="HOQLIFE_CHART"/>
 *     &lt;enumeration value="VL_STALL_CHART"/>
 *     &lt;enumeration value="CABLE_INFO"/>
 *     &lt;enumeration value="SC2VLTMT_CHART"/>
 *     &lt;enumeration value="SC2VLNTMT_CHART"/>
 *     &lt;enumeration value="LINK_DOWN_ERROR_LOG"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "PortCategory")
@XmlEnum
public enum PortCategory {

    LINK_WIDTH,
    LINK_WIDTH_DOWNGRADE,
    LINK_SPEED,
    LINK_CONNECTED_TO,
    NEIGHBOR_MODE,
    PORT_INFO,
    PORT_LINK_MODE,
    PORT_LTP_CRC_MODE,
    PORT_ERROR_ACTIONS,
    PORT_MODE,
    PORT_PACKET_FORMAT,
    PORT_BUFFER_UNITS,
    PORT_IPADDR,
    PORT_SUBNET,
    PORT_CAPABILITIES,
    PORT_DIAGNOSTICS,
    PORT_MANAGEMENT,
    PORT_PARTITION_ENFORCEMENT,
    FLIT_CTRL_INTERLEAVE,
    FLIT_CTRL_PREEMPTION,
    VIRTUAL_LANE,
    MTU_CHART,
    HOQLIFE_CHART,
    VL_STALL_CHART,
    CABLE_INFO,
    SC2VLTMT_CHART,
    SC2VLNTMT_CHART,
    LINK_DOWN_ERROR_LOG,
    NEIGHBOR_LINK_DOWN_ERROR_LOG;

    public String value() {
        return name();
    }

    public static PortCategory fromValue(String v) {
        return valueOf(v);
    }

}
