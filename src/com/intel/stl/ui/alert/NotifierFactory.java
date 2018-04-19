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

package com.intel.stl.ui.alert;

import static com.intel.stl.api.configuration.UserSettings.PROPERTY_MAIL_RECIPIENTS;
import static com.intel.stl.api.configuration.UserSettings.SECTION_PREFERENCE;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.main.Context;

public class NotifierFactory {

    private static Logger log = LoggerFactory.getLogger(NotifierFactory.class);

    public static NoticeNotifier createNotifier(NotifierType type,
            Context context) throws IllegalArgumentException {
        UserSettings settings = context.getUserSettings();
        if (settings == null) {
            // shouldn't happen
            throw new RuntimeException("No user settings found");
        }

        if (type == NotifierType.MAIL) {
            log.debug("NotifierFactory: Mail notifier created.");
            List<EventRule> eventRules = new ArrayList<EventRule>();
            List<EventRule> savedEventRules = settings.getEventRules();
            if (savedEventRules != null) {
                eventRules.addAll(savedEventRules);
            }
            List<INotifyRule> notifyRules = new ArrayList<INotifyRule>();

            // From the user settings for this subnet, we process the list
            // of event rules looking for entries that have a SEND_EMAIL
            // action.
            for (EventRule eventRule : eventRules) {
                List<EventRuleAction> actions = eventRule.getEventActions();
                for (EventRuleAction action : actions) {
                    if (action.equals(EventRuleAction.SEND_EMAIL)) {
                        // We create a MailNotifyRule corresponding to the
                        // event rule/action.
                        notifyRules.add(new MailNotifyRule(eventRule));
                        break;
                    }
                }
            }

            String mailRecepientsStr =
                    settings.getPreferences().get(SECTION_PREFERENCE)
                            .getProperty(PROPERTY_MAIL_RECIPIENTS);

            List<String> mailRecepients =
                    Utils.concatenatedStringToList(mailRecepientsStr,
                            UIConstants.MAIL_LIST_DELIMITER);
            return new MailNotifier(context, notifyRules, mailRecepients);

        } else {
            IllegalArgumentException e =
                    new IllegalArgumentException("Unsupported Notifier Type "
                            + type);
            log.error("NoticeNotifier: createNotifier exception: "
                    + e.getMessage());
            throw e;
        }
    }
}
