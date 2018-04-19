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

package com.intel.stl.ui.wizards.model.preferences;

import static com.intel.stl.api.configuration.UserSettings.PROPERTY_MAIL_RECIPIENTS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_NUM_WORST_NODES;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_REFRESH_RATE;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_REFRESH_RATE_UNITS;
import static com.intel.stl.api.configuration.UserSettings.PROPERTY_TIMING_WINDOW;
import static com.intel.stl.api.configuration.UserSettings.SECTION_PREFERENCE;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import com.intel.stl.ui.common.STLConstants;

/**
 * Model to store the preferences wizard settings
 */
public class PreferencesModel {

    private Map<String, Properties> preferencesMap =
            new HashMap<String, Properties>();

    public PreferencesModel() {
        initialize();
    }

    public void initialize() {
        Properties preferences = new Properties();
        preferences.put(PROPERTY_REFRESH_RATE, "10");
        preferences.put(PROPERTY_REFRESH_RATE_UNITS,
                STLConstants.K0012_SECONDS.getValue());
        preferences.put(PROPERTY_TIMING_WINDOW, "60");
        preferences.put(PROPERTY_NUM_WORST_NODES, "10");
        preferences.put(PROPERTY_MAIL_RECIPIENTS, "");
        preferencesMap.put(SECTION_PREFERENCE, preferences);
    }

    public String getMailRecipients() {
        return preferencesMap.get(SECTION_PREFERENCE).getProperty(
                PROPERTY_MAIL_RECIPIENTS);
    }

    public void setMailRecipients(String recipients) {
        preferencesMap.get(SECTION_PREFERENCE).setProperty(
                PROPERTY_MAIL_RECIPIENTS, recipients);
    }

    public String getRefreshRate() {
        return preferencesMap.get(SECTION_PREFERENCE).getProperty(
                PROPERTY_REFRESH_RATE);
    }

    public void setRefreshRate(String refreshRateInSeconds) {
        preferencesMap.get(SECTION_PREFERENCE).setProperty(
                PROPERTY_REFRESH_RATE, refreshRateInSeconds);
    }

    public String getRefreshRateUnits() {

        return preferencesMap.get(SECTION_PREFERENCE).getProperty(
                PROPERTY_REFRESH_RATE_UNITS);
    }

    public void setRefreshRateUnits(String refreshRateUnits) {

        preferencesMap.get(SECTION_PREFERENCE).setProperty(
                PROPERTY_REFRESH_RATE_UNITS, refreshRateUnits);
    }

    public String getTimingWindowInSeconds() {
        return preferencesMap.get(SECTION_PREFERENCE).getProperty(
                PROPERTY_TIMING_WINDOW);
    }

    public void setTimingWindowInSeconds(String timingWindow) {
        preferencesMap.get(SECTION_PREFERENCE).setProperty(
                PROPERTY_TIMING_WINDOW, timingWindow);
    }

    public String getNumWorstNodes() {
        return preferencesMap.get(SECTION_PREFERENCE).getProperty(
                PROPERTY_NUM_WORST_NODES);
    }

    public void setNumWorstNodes(String numWorstNodes) {
        preferencesMap.get(SECTION_PREFERENCE).setProperty(
                PROPERTY_NUM_WORST_NODES, numWorstNodes);
    }

    public void clear() {
        if (preferencesMap != null) {
            initialize();
        }
    }

    public Map<String, Properties> getPreferencesMap() {
        return preferencesMap;
    }

    public void setPreferencesMap(Map<String, Properties> preferencesMap) {
        this.preferencesMap = preferencesMap;
    }

    public Properties getPreferences() {
        return preferencesMap.get(SECTION_PREFERENCE);
    }
}
