/**
 * Copyright (c) 2017, Intel Corporation
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

import com.intel.stl.api.configuration.VLSchedulingMode;
import com.intel.stl.ui.common.STLConstants;

public enum VLSchedulingModeViz {

    VL_ARB(VLSchedulingMode.VL_ARB, STLConstants.K0181_VLARB.getValue()),
    BW_METER(VLSchedulingMode.BW_METER, STLConstants.K0182_BW_METER.getValue()),
    AUTOMATIC(VLSchedulingMode.AUTOMATIC,
            STLConstants.K0183_AUTOMATIC.getValue());

    private final VLSchedulingMode mode;

    private final String name;

    private VLSchedulingModeViz(VLSchedulingMode mode, String name) {
        this.mode = mode;
        this.name = name;
    }

    /**
     * @return the mode
     */
    public VLSchedulingMode getMode() {
        return mode;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public static VLSchedulingModeViz getVLSchedulingModeVizFor(
            VLSchedulingMode mode) throws Exception {
        for (VLSchedulingModeViz value : values()) {
            if (mode == value.getMode()) {
                return value;
            }
        }

        throw new IllegalArgumentException(
                "Undefined VLSchedulingModeViz : VLSchedulingMode ='" + mode
                        + "'");
    }

}
