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

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.StringUtils;

public enum PortErrorAction {

    EXCESSIVE_BUFFER_OVERRUN(0x80000000),
    FM_CONFIG_ERROR_EXCEEDMULTICASTLIMIT(0x00800000),
    FM_CONFIG_ERROR_BADCONTROLFLIT(0x00400000),
    FM_CONFIG_ERROR_BADPREEMPT(0x00200000),
    FM_CONFIG_ERROR_UNSUPPORTEDVLMARKER(0x00100000),
    FM_CONFIG_ERROR_BADCRDTACK(0x00080000),
    FM_CONFIG_ERROR_BADCTRLDIST(0x00040000),
    FM_CONFIG_ERROR_BADTAILDIST(0x00020000),
    FM_CONFIG_ERROR_BADHEADDIST(0x00010000),
    PORT_RCV_ERROR_BADVLMARKER(0x00002000),
    PORT_RCV_ERROR_PREEMPTVL15(0x00001000),
    PORT_RCV_ERROR_PREEMPTERROR(0x00000800),
    PORT_RCV_ERROR_BADMIDTAIL(0x00000200),
    PORT_RCV_ERROR_RESERVED(0x00000100),
    PORT_RCV_ERROR_BADSC(0x00000080),
    PORT_RCV_ERROR_BADL2(0x00000040),
    PORT_RCV_ERROR_BADDLID(0x00000020),
    PORT_RCV_ERROR_BADSLID(0x00000010),
    PORT_RCV_ERROR_PKTLENTOOSHORT(0x00000008),
    PORT_RCV_ERROR_PKTLENTOOLONG(0x00000004),
    PORT_RCV_ERROR_BADPKTLEN(0x00000002);

    private final int mask;

    private PortErrorAction(int mask) {
        this.mask = mask;
    }

    public int getMask() {
        return mask;
    }

    public static PortErrorAction getPortErrorAction(int value) {
        for (PortErrorAction pea : PortErrorAction.values()) {
            if (pea.mask == value) {
                return pea;
            }
        }
        throw new IllegalArgumentException("Unsupported PortErrorAction "
                + StringUtils.intHexString(value));
    }

    public static List<PortErrorAction> getPortErroActions(int value) {
        List<PortErrorAction> errorActions = new ArrayList<PortErrorAction>();
        for (PortErrorAction pea : PortErrorAction.values()) {
            if ((value & pea.mask) == pea.mask) {
                errorActions.add(pea);
            }
        }
        return errorActions;
    }
}
