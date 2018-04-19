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

import static com.intel.stl.ui.common.STLConstants.K0039_NOT_AVAILABLE;
import static com.intel.stl.ui.common.STLConstants.K0776_EXCESSIVE_BUFF_OVERRUN;
import static com.intel.stl.ui.common.STLConstants.K0777_FM_CONFIG_ERROR;
import static com.intel.stl.ui.common.STLConstants.K0778_PORT_RCV_ERROR;
import static com.intel.stl.ui.common.STLConstants.K0779_EXCEEDMULTICASTLIMIT;
import static com.intel.stl.ui.common.STLConstants.K0780_BADCONTROLFLIT;
import static com.intel.stl.ui.common.STLConstants.K0781_BADPREEMPT;
import static com.intel.stl.ui.common.STLConstants.K0782_UNSUPPORTEDVLMARKER;
import static com.intel.stl.ui.common.STLConstants.K0784_BADCTRLDIST;
import static com.intel.stl.ui.common.STLConstants.K0785_BADTAILDIST;
import static com.intel.stl.ui.common.STLConstants.K0786_BADHEADDIST;
import static com.intel.stl.ui.common.STLConstants.K0787_BADVLMARKER;
import static com.intel.stl.ui.common.STLConstants.K0788_PREEMPTVL15;
import static com.intel.stl.ui.common.STLConstants.K0789_PREEMPTERROR;
import static com.intel.stl.ui.common.STLConstants.K0790_BADMIDTAIL;
import static com.intel.stl.ui.common.STLConstants.K0791_RESERVED;
import static com.intel.stl.ui.common.STLConstants.K0792_BADSC;
import static com.intel.stl.ui.common.STLConstants.K0793_BADL2;
import static com.intel.stl.ui.common.STLConstants.K0794_BADDLID;
import static com.intel.stl.ui.common.STLConstants.K0795_BADSLID;
import static com.intel.stl.ui.common.STLConstants.K0796_PKTLENTOOSHORT;
import static com.intel.stl.ui.common.STLConstants.K0797_PKTLENTOOLONG;
import static com.intel.stl.ui.common.STLConstants.K0798_BADPKTLEN;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.configuration.PortErrorAction;

/**
 * see SPEC table 9-23 for action definition and explanation
 */
public enum PortErrorActionViz {

    EXCESSIVE_BUFFER_OVERRUN(PortErrorAction.EXCESSIVE_BUFFER_OVERRUN,
            K0776_EXCESSIVE_BUFF_OVERRUN.getValue(),
            K0039_NOT_AVAILABLE.getValue()),
    FM_CONFIG_ERROR_EXCEEDMULTICASTLIMIT(
            PortErrorAction.FM_CONFIG_ERROR_EXCEEDMULTICASTLIMIT,
            K0777_FM_CONFIG_ERROR.getValue(),
            K0779_EXCEEDMULTICASTLIMIT.getValue()),
    FM_CONFIG_ERROR_BADCONTROLFLIT(
            PortErrorAction.FM_CONFIG_ERROR_BADCONTROLFLIT,
            K0777_FM_CONFIG_ERROR.getValue(), K0780_BADCONTROLFLIT.getValue()),
    FM_CONFIG_ERROR_BADPREEMPT(PortErrorAction.FM_CONFIG_ERROR_BADPREEMPT,
            K0777_FM_CONFIG_ERROR.getValue(), K0781_BADPREEMPT.getValue()),
    FM_CONFIG_ERROR_UNSUPPORTEDVLMARKER(
            PortErrorAction.FM_CONFIG_ERROR_UNSUPPORTEDVLMARKER,
            K0777_FM_CONFIG_ERROR.getValue(),
            K0782_UNSUPPORTEDVLMARKER.getValue()),
    FM_CONFIG_ERROR_BADCRDTACK(PortErrorAction.FM_CONFIG_ERROR_BADCRDTACK,
            K0777_FM_CONFIG_ERROR.getValue(),
            K0782_UNSUPPORTEDVLMARKER.getValue()),
    FM_CONFIG_ERROR_BADCTRLDIST(PortErrorAction.FM_CONFIG_ERROR_BADCTRLDIST,
            K0777_FM_CONFIG_ERROR.getValue(), K0784_BADCTRLDIST.getValue()),
    FM_CONFIG_ERROR_BADTAILDIST(PortErrorAction.FM_CONFIG_ERROR_BADTAILDIST,
            K0777_FM_CONFIG_ERROR.getValue(), K0785_BADTAILDIST.getValue()),
    FM_CONFIG_ERROR_BADHEADDIST(PortErrorAction.FM_CONFIG_ERROR_BADHEADDIST,
            K0777_FM_CONFIG_ERROR.getValue(), K0786_BADHEADDIST.getValue()),
    PORT_RCV_ERROR_BADVLMARKER(PortErrorAction.PORT_RCV_ERROR_BADVLMARKER,
            K0778_PORT_RCV_ERROR.getValue(), K0787_BADVLMARKER.getValue()),
    PORT_RCV_ERROR_PREEMPTVL15(PortErrorAction.PORT_RCV_ERROR_PREEMPTVL15,
            K0778_PORT_RCV_ERROR.getValue(), K0788_PREEMPTVL15.getValue()),
    PORT_RCV_ERROR_PREEMPTERROR(PortErrorAction.PORT_RCV_ERROR_PREEMPTERROR,
            K0778_PORT_RCV_ERROR.getValue(), K0789_PREEMPTERROR.getValue()),
    PORT_RCV_ERROR_BADMIDTAIL(PortErrorAction.PORT_RCV_ERROR_BADMIDTAIL,
            K0778_PORT_RCV_ERROR.getValue(), K0790_BADMIDTAIL.getValue()),
    PORT_RCV_ERROR_RESERVED(PortErrorAction.PORT_RCV_ERROR_RESERVED,
            K0778_PORT_RCV_ERROR.getValue(), K0791_RESERVED.getValue()),
    PORT_RCV_ERROR_BADSC(PortErrorAction.PORT_RCV_ERROR_BADSC,
            K0778_PORT_RCV_ERROR.getValue(), K0792_BADSC.getValue()),
    PORT_RCV_ERROR_BADL2(PortErrorAction.PORT_RCV_ERROR_BADL2,
            K0778_PORT_RCV_ERROR.getValue(), K0793_BADL2.getValue()),
    PORT_RCV_ERROR_BADDLID(PortErrorAction.PORT_RCV_ERROR_BADDLID,
            K0778_PORT_RCV_ERROR.getValue(), K0794_BADDLID.getValue()),
    PORT_RCV_ERROR_BADSLID(PortErrorAction.PORT_RCV_ERROR_BADSLID,
            K0778_PORT_RCV_ERROR.getValue(), K0795_BADSLID.getValue()),
    PORT_RCV_ERROR_PKTLENTOOSHORT(PortErrorAction.PORT_RCV_ERROR_PKTLENTOOSHORT,
            K0778_PORT_RCV_ERROR.getValue(), K0796_PKTLENTOOSHORT.getValue()),
    PORT_RCV_ERROR_PKTLENTOOLONG(PortErrorAction.PORT_RCV_ERROR_PKTLENTOOLONG,
            K0778_PORT_RCV_ERROR.getValue(), K0797_PKTLENTOOLONG.getValue()),
    PORT_RCV_ERROR_BADPKTLEN(PortErrorAction.PORT_RCV_ERROR_BADPKTLEN,
            K0778_PORT_RCV_ERROR.getValue(), K0798_BADPKTLEN.getValue());

    private final PortErrorAction errorAction;

    private final String generalReason;

    private final String specificReason;

    private PortErrorActionViz(PortErrorAction errorAction,
            String generalReason, String specificReason) {
        this.errorAction = errorAction;
        this.generalReason = generalReason;
        this.specificReason = specificReason;
    }

    public PortErrorAction getPortErrorAction() {
        return errorAction;
    }

    public String getGeneralReason() {
        return generalReason;
    }

    public String getSpecificReason() {
        return specificReason;
    }

    public String getReasonDescription() {
        if (specificReason == null || specificReason.length() == 0) {
            return generalReason;
        }
        return generalReason + ": " + specificReason;
    }

    public static PortErrorActionViz getPortErrorActionViz(int value) {
        for (PortErrorActionViz peav : PortErrorActionViz.values()) {
            if (peav.errorAction.getMask() == value) {
                return peav;
            }
        }
        return null;
    }

    public static List<PortErrorActionViz> getPortErroActions(int value) {
        List<PortErrorActionViz> errorActions =
                new ArrayList<PortErrorActionViz>();
        for (PortErrorActionViz peav : PortErrorActionViz.values()) {
            int mask = peav.errorAction.getMask();
            if ((value & mask) == mask) {
                errorActions.add(peav);
            }
        }
        return errorActions;
    }

}
