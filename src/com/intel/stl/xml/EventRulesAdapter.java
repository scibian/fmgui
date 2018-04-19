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

package com.intel.stl.xml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.NoticeSeverity;

public class EventRulesAdapter extends XmlAdapter<EventRules, List<EventRule>> {

    @Override
    public List<EventRule> unmarshal(EventRules rules) throws Exception {

        // New list of event rules to return
        List<EventRule> eventRulesList = new ArrayList<EventRule>();

        // Traverse JaxB event rules and convert to Java event rules
        for (EventRuleType rule : rules.getEventRuleTypes()) {

            // Create a list of event rule actions
            List<EventRuleAction> eventActions =
                    new ArrayList<EventRuleAction>();
            for (ActionType action : rule.getActions()) {
                eventActions.add(EventRuleAction.getEventAction(action
                        .getName().name()));
            }

            // Create a list of event rules
            eventRulesList.add(new EventRule(EventType.getEventType(rule
                    .getType().name()), NoticeSeverity.getNoticeSeverity(rule
                    .getSeverity().name()), eventActions));
        }

        return eventRulesList;
    }

    @Override
    public EventRules marshal(List<EventRule> eventRulesList) throws Exception {

        // Get the list from the event rules so it can be populated
        EventRules eventRules = new EventRules();
        List<EventRuleType> eventTypeList = eventRules.getEventRuleTypes();

        // Traverse the event rules list provided, extract the values, and
        // put them in the event type list
        if (eventRulesList != null) {
            for (EventRule rule : eventRulesList) {
                EventRuleType eventType = new EventRuleType();
                eventType.setType(RuleType
                        .fromValue(rule.getEventType().name()));
                eventType.setSeverity(RuleSeverity.fromValue(rule
                        .getEventSeverity().name()));

                // Convert the list of actions to ActionType
                List<ActionType> actionTypes = new ArrayList<ActionType>();
                for (EventRuleAction action : rule.getEventActions()) {
                    ActionType actionType = new ActionType();
                    actionType.setName(ActionName.fromValue(action.name()));
                    actionTypes.add(actionType);
                }
                eventType.setActions(actionTypes);

                // Add the event type to the event type list
                eventTypeList.add(eventType);
            } // for
        } // if

        return eventRules;
    }
}
