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

import com.intel.stl.api.configuration.EventClass;
import com.intel.stl.ui.common.STLConstants;

public enum EventClassViz {

    SUBNET_EVENTS(EventClass.SUBNET_EVENTS, STLConstants.K0684_SUBNET_EVENTS
            .getValue()),
    MISC_EVENTS(EventClass.MISCELLANEOUS_EVENTS,
            STLConstants.K0685_MISC_EVENTS.getValue());

    private final EventClass eventClass;

    private final String name;

    private EventClassViz(EventClass eventClass, String name) {
        this.eventClass = eventClass;
        this.name = name;
    }

    public EventClass getEventClass() {
        return eventClass;
    }

    public String getName() {
        return name;
    }

    public static EventClassViz getEventTypeClassFor(EventClass evtClass) {
        EventClassViz[] values = EventClassViz.values();
        for (int i = 0; i < values.length; i++) {
            if (evtClass == values[i].getEventClass()) {
                return values[i];
            }
        }
        return null;
    }

}
