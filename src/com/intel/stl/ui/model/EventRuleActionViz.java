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

import static com.intel.stl.ui.common.STLConstants.K0682_DISPLAY_MESSAGE;
import static com.intel.stl.ui.common.STLConstants.K0683_EMAIL_MESSAGE;

import javax.swing.ImageIcon;

import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.WidgetName;

public enum EventRuleActionViz {

    SEND_EMAIL(EventRuleAction.SEND_EMAIL, K0683_EMAIL_MESSAGE.getValue(),
            UIImages.EMAIL_ICON.getImageIcon(), WidgetName.SW_E_SENT_EMAIL),
    DISPLAY_MESSAGE(EventRuleAction.DISPLAY_MESSAGE,
            K0682_DISPLAY_MESSAGE.getValue(),
            UIImages.LOG_MENU_ICON.getImageIcon(), WidgetName.SW_E_DISPLAT_MSG);

    public final static String[] names =
            new String[EventRuleActionViz.values().length];

    static {
        for (int i = 0; i < names.length; i++) {
            names[i] = EventRuleActionViz.values()[i].name;
        }
    };

    private final EventRuleAction action;

    private final String name;

    private final ImageIcon icon;

    private final WidgetName widgetName;

    private EventRuleActionViz(EventRuleAction action, String name,
            ImageIcon icon, WidgetName widgetName) {
        this.action = action;
        this.name = name;
        this.icon = icon;
        this.widgetName = widgetName;
    }

    public EventRuleAction getEventRuleAction() {
        return action;
    }

    public String getName() {
        return name;
    }

    public ImageIcon getImageIcon() {
        return icon;
    }

    /**
     * @return the widgetName
     */
    public WidgetName getWidgetName() {
        return widgetName;
    }

    public static EventRuleActionViz getEventRuleActionVizFor(
            EventRuleAction action) {
        EventRuleActionViz[] values = EventRuleActionViz.values();
        for (int i = 0; i < values.length; i++) {
            if (action == values[i].getEventRuleAction()) {
                return values[i];
            }
        }
        return null;
    }

    public static EventRuleActionViz getEventRuleActionVizFor(String name) {
        EventRuleActionViz[] values = EventRuleActionViz.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].name.equals(name)) {
                return values[i];
            }
        }
        return null;
    }
}
