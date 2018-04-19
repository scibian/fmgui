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

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class UserSettings implements Serializable {

    private static final long serialVersionUID = 1L;

    // Define here any sections you define in Preferences
    public static final String SECTION_USERSTATE = "UserState";

    public static final String SECTION_PREFERENCE = "Preference";

    public static final String SECTION_PIN_BOARD = "PinBoard";

    // Define here any preference name defined in a section
    public static final String PROPERTY_LASTSUBNETACCESSED =
            "LastSubnetAccessed";

    public static final String PROPERTY_REFRESH_RATE = "RefreshRate";

    public static final String PROPERTY_REFRESH_RATE_UNITS = "RefreshRateUnits";

    public static final String PROPERTY_TIMING_WINDOW = "TimingWindow";

    public static final String PROPERTY_NUM_WORST_NODES = "NumWorstNodes";

    public static final String PROPERTY_MAIL_RECIPIENTS = "mailRecipients";

    public static final String PROPERTY_WEIGHT_NUMSWITCHES =
            "WeightNumSwitches";

    public static final String PROPERTY_WEIGHT_NUMHFIS = "WeightNumHfis";

    public static final String PROPERTY_WEIGHT_NUMISLS = "WeightNumIsls";

    public static final String PROPERTY_WEIGHT_NUMHFILINKS =
            "WeightNumHfiLinks";

    public static final String PROPERTY_WEIGHT_NUMPORTS = "WeightNumPorts";

    public static final String PROPERTY_WEIGHT_NUMNONDEGISLS =
            "WeightNumNonDegIsls";

    public static final String PROPERTY_WEIGHT_NUMNONDEGHFILINKS =
            "WeightNumNonDegHfiLinks";

    private String userName;

    private String userDescription;

    private Map<String, Properties> preferences;

    private List<EventRule> eventRules;

    private Map<ResourceType, List<PropertyGroup>> propertiesDisplayOptions;

    public UserSettings() {

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserDescription() {
        return userDescription;
    }

    public void setUserDescription(String userDescription) {
        this.userDescription = userDescription;
    }

    public Map<String, Properties> getPreferences() {
        return preferences;
    }

    public void setPreferences(Map<String, Properties> preferences) {
        this.preferences = preferences;
    }

    public Properties getUserState() {
        return getProperties(SECTION_USERSTATE);
    }

    public Properties getUserPreference() {
        return getProperties(SECTION_PREFERENCE);
    }

    protected Properties getProperties(String name) {
        Properties res = preferences.get(name);
        if (res != null) {
            return res;
        } else {
            throw new RuntimeException("Couldn't find Properties for '" + name
                    + "'");
        }
    }

    public List<EventRule> getEventRules() {
        return eventRules;
    }

    public void setEventRules(List<EventRule> eventRules) {
        this.eventRules = eventRules;
    }

    public Map<ResourceType, List<PropertyGroup>> getPropertiesDisplayOptions() {
        return propertiesDisplayOptions;
    }

    public void setPropertiesDisplayOptions(
            Map<ResourceType, List<PropertyGroup>> propertiesDisplayOptions) {
        this.propertiesDisplayOptions = propertiesDisplayOptions;
    }

}
