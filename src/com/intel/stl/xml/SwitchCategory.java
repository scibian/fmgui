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
 * Java class for SwitchCategory.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * <p>
 * 
 * <pre>
 * &lt;simpleType name="SwitchCategory">
 *   &lt;restriction base="{http://www.w3.org/2001/XMLSchema}string">
 *     &lt;enumeration value="NODE_INFO"/>
 *     &lt;enumeration value="DEVICE_GROUPS"/>
 *     &lt;enumeration value="NODE_PORT_INFO"/>
 *     &lt;enumeration value="SWITCH_INFO"/>
 *     &lt;enumeration value="SWITCH_FORWARDING"/>
 *     &lt;enumeration value="SWITCH_ROUTING"/>
 *     &lt;enumeration value="SWITCH_IPADDR"/>
 *     &lt;enumeration value="SWITCH_PARTITION_ENFORCEMENT"/>
 *     &lt;enumeration value="SWITCH_ADAPTIVE_ROUTING"/>
 *     &lt;enumeration value="MFT_TABLE"/>
 *     &lt;enumeration value="LFT_HISTOGRAM"/>
 *     &lt;enumeration value="LFT_TABLE"/>
 *     &lt;enumeration value="SC2SLMT_CHART"/>
 *   &lt;/restriction>
 * &lt;/simpleType>
 * </pre>
 * 
 */
@XmlType(name = "SwitchCategory")
@XmlEnum
public enum SwitchCategory {

    NODE_INFO,
    DEVICE_GROUPS,
    NODE_PORT_INFO,
    SWITCH_INFO,
    SWITCH_FORWARDING,
    SWITCH_ROUTING,
    SWITCH_IPADDR,
    SWITCH_PARTITION_ENFORCEMENT,
    SWITCH_ADAPTIVE_ROUTING,
    MFT_TABLE,
    LFT_HISTOGRAM,
    LFT_TABLE,
    SC2SLMT_CHART;

    public String value() {
        return name();
    }

    public static SwitchCategory fromValue(String v) {
        return valueOf(v);
    }

}
