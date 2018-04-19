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

import static com.intel.stl.ui.common.STLConstants.K0368_NO_STATE_CHANGE;
import static com.intel.stl.ui.common.STLConstants.K0374_SLEEP;
import static com.intel.stl.ui.common.STLConstants.K0375_POLLING;
import static com.intel.stl.ui.common.STLConstants.K0376_DISABLED;
import static com.intel.stl.ui.common.STLConstants.K0377_CONFIG_TRAIN;
import static com.intel.stl.ui.common.STLConstants.K0378_LINKUP;
import static com.intel.stl.ui.common.STLConstants.K0379_LINK_ERR_RECOV;
import static com.intel.stl.ui.common.STLConstants.K0381_OFFLINE;
import static com.intel.stl.ui.common.STLConstants.K0382_TEST;

import java.util.EnumMap;

import com.intel.stl.api.configuration.PhysicalState;

public enum PhysicalStateViz {

    NO_ST_CHANGE(PhysicalState.NO_ST_CHANGE, K0368_NO_STATE_CHANGE.getValue()),
    SLEEP(PhysicalState.SLEEP, K0374_SLEEP.getValue()),
    POLLING(PhysicalState.POLLING, K0375_POLLING.getValue()),
    DISABLED(PhysicalState.DISABLED, K0376_DISABLED.getValue()),
    PORT_CONFIG_TRAINING(PhysicalState.PORT_CONFIG_TRAINING, K0377_CONFIG_TRAIN
            .getValue()),
    LINKUP(PhysicalState.LINKUP, K0378_LINKUP.getValue()),
    LINK_ERROR_RECOVERY(PhysicalState.LINK_ERROR_RECOVERY, K0379_LINK_ERR_RECOV
            .getValue()),
    OFFLINE(PhysicalState.OFFLINE, K0381_OFFLINE.getValue()),
    TEST(PhysicalState.TEST, K0382_TEST.getValue());

    private final static EnumMap<PhysicalState, String> physStateMap =
            new EnumMap<PhysicalState, String>(PhysicalState.class);
    static {
        for (PhysicalStateViz psv : PhysicalStateViz.values()) {
            physStateMap.put(psv.state, psv.value);
        }
    };

    private final PhysicalState state;

    private final String value;

    private PhysicalStateViz(PhysicalState state, String value) {
        this.state = state;
        this.value = value;
    }

    public PhysicalState getPhysicalState() {
        return state;
    }

    public String getValue() {
        return value;
    }

    public static PhysicalStateViz getPhysicalStateViz(byte state) {
        for (PhysicalStateViz psv : PhysicalStateViz.values()) {
            if (psv.state.getId() == state) {
                return psv;
            }
        }
        return null;
    }

    public static String getPhysicalStateStr(PhysicalState state) {
        return physStateMap.get(state);
    }
}
