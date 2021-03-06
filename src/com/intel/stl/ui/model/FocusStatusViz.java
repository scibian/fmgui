/**
 * Copyright (c) 2016, Intel Corporation
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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import com.intel.stl.api.configuration.FocusStatus;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;

public enum FocusStatusViz {
    OK(FocusStatus.OK, STLConstants.K0645_OK.getValue(),
            UIConstants.INTEL_DARK_GRAY),
    PMA_IGNORE(FocusStatus.PMA_IGNORE, STLConstants.K0212_PMA_IGNORE.getValue(),
            UIConstants.INTEL_BLUE),
    PMA_NORESP(FocusStatus.PMA_NORESP, STLConstants.K0213_PMA_NORESP.getValue(),
            UIConstants.INTEL_DARK_YELLOW),
    TOPO_NORESP(FocusStatus.TOPO_NORESP,
            STLConstants.K0214_TOPO_NORESP.getValue(),
            UIConstants.INTEL_DARK_RED);

    private static final Map<Byte, FocusStatusViz> focusStatusMap =
            new HashMap<Byte, FocusStatusViz>();

    static {
        for (FocusStatusViz ffv : FocusStatusViz.values()) {
            focusStatusMap.put(ffv.status.getCode(), ffv);
        }
    };

    private final FocusStatus status;

    private final String value;

    private final Color color;

    private FocusStatusViz(FocusStatus status, String value, Color color) {
        this.status = status;
        this.value = value;
        this.color = color;
    }

    /**
     * @return the status
     */
    public FocusStatus getStatus() {
        return status;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }

    /**
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    public static FocusStatusViz getFocusStatusViz(byte status) {
        FocusStatusViz res = focusStatusMap.get(status);
        if (res == null) {
            throw new IllegalArgumentException(
                    "Unknow status '" + status + "'!");
        }
        return res;
    }
}
