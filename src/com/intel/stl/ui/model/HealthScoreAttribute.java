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

import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMHFILINKS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMHFIS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMISLS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMNONDEGHFILINKS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMNONDEGISLS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMPORTS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_WEIGHT_NUMSWITCHES;
import static com.intel.stl.ui.common.UILabels.STL10300_NUM_SWITCHES;
import static com.intel.stl.ui.common.UILabels.STL10301_NUM_HFIS;
import static com.intel.stl.ui.common.UILabels.STL10302_NUM_ISLINKS;
import static com.intel.stl.ui.common.UILabels.STL10303_NUM_HFILINKS;
import static com.intel.stl.ui.common.UILabels.STL10304_NUM_PORTS;
import static com.intel.stl.ui.common.UILabels.STL10305_NUM_NONDEGRADISLS;
import static com.intel.stl.ui.common.UILabels.STL10306_NUM_NONDEGRADHFILINKS;

import com.intel.stl.ui.common.UILabels;

public enum HealthScoreAttribute {

    /**
     * For specific attributes, -1 means that the EventCalculator will do a
     * special calculation to determine the attribute's weight. It must support
     * it.
     */
    NUM_SWITCHES(STL10300_NUM_SWITCHES, PROPERTY_WEIGHT_NUMSWITCHES, -1),
    NUM_HFIS(STL10301_NUM_HFIS, PROPERTY_WEIGHT_NUMHFIS, -1),
    NUM_ISLINKS(STL10302_NUM_ISLINKS, PROPERTY_WEIGHT_NUMISLS, 3),
    NUM_HFILINKS(STL10303_NUM_HFILINKS, PROPERTY_WEIGHT_NUMHFILINKS, 2),
    NUM_PORTS(STL10304_NUM_PORTS, PROPERTY_WEIGHT_NUMPORTS, 1),
    NUM_NONDEGRADED_ISLINKS(STL10305_NUM_NONDEGRADISLS,
            PROPERTY_WEIGHT_NUMNONDEGISLS, 3),
    NUM_NONDEGRADED_HFILINKS(STL10306_NUM_NONDEGRADHFILINKS,
            PROPERTY_WEIGHT_NUMNONDEGHFILINKS, 2);

    private final String description;

    private final String propertiesKey;

    private final int defaultWeight;

    private HealthScoreAttribute(UILabels label, String propertiesKey,
            int defaultWeight) {
        this.description = label.getDescription();
        this.propertiesKey = propertiesKey;
        this.defaultWeight = defaultWeight;
    }

    public String getPropertiesKey() {
        return propertiesKey;
    }

    public int getDefaultWeight() {
        return defaultWeight;
    }

    public String getDescription() {
        return description;
    }

}
