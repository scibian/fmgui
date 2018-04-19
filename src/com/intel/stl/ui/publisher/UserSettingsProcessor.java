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

package com.intel.stl.ui.publisher;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.UserPreference;

/**
 * Depending on what's how changed, we want to calculate events differently.
 */
public class UserSettingsProcessor {

    private UserPreference userPreference;

    private final Context context;

    private final Map<EventType, NoticeSeverity> eventSeverityMap =
            new HashMap<EventType, NoticeSeverity>();

    public UserSettingsProcessor(UserSettings userSettings, Context context) {
        this.context = context;
        List<EventRule> eventRules = null;
        this.userPreference = new UserPreference(userSettings);
        if (userSettings != null) {
            eventRules = userSettings.getEventRules();
        }
        if (eventRules != null) {
            for (EventRule er : eventRules) {
                eventSeverityMap.put(er.getEventType(), er.getEventSeverity());
            }
        }
    }

    public void process(UserSettings newSettings) {
        List<EventRule> newList = null;
        if (newSettings != null) {
            newList = newSettings.getEventRules();
        }
        boolean severityChanged = false;
        if (newList != null) {
            for (EventRule er : newList) {
                EventType eventType = er.getEventType();
                NoticeSeverity oldSeverity = eventSeverityMap.get(eventType);
                NoticeSeverity newSeverity = er.getEventSeverity();
                if (oldSeverity != newSeverity) {
                    eventSeverityMap.put(eventType, newSeverity);
                    severityChanged = true;
                }
            }
        }

        EventCalculator evtCal = context.getEvtCal();
        if (severityChanged) {
            evtCal.clear();
        }

        UserPreference newPreference = new UserPreference(newSettings);
        boolean userPrefChanged =
                evtCal.updateUserPreference(userPreference, newPreference);

        boolean refreshRateChanged =
                newPreference.getRefreshRateInSeconds() != userPreference
                        .getRefreshRateInSeconds();
        if (refreshRateChanged) {
            context.getTaskScheduler()
                    .updateRefreshRate(newPreference.getRefreshRateInSeconds());
        }

        if (severityChanged || userPrefChanged || refreshRateChanged) {
            userPreference = newPreference;
        }

    }
}
