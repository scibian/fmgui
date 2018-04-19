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

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.ui.main.Context;

public abstract class NoticeNotifier implements
        IEventListener<EventDescription> {
    protected final Context context;

    /**
     * A list of notify rules. Rule evaluation stops when it meet the first
     * matched rule
     */
    private final List<INotifyRule> rules = new ArrayList<INotifyRule>();


	/**
     * Description:  Constructor for generic notifier.
     * 
     * @param context
     * @param rules
     */
    public NoticeNotifier(Context context, List<INotifyRule> rules) {
        super();
        this.context = context;
        setRules(rules);
    }

    public synchronized void addRule(INotifyRule rule) {
        if (rule != null) {
            rules.add(rule);
        }
    }

    public synchronized void removeRule(INotifyRule rule) {
        if (rule != null) {
            rules.remove(rule);
        }
    }

    public synchronized void setRules(List<INotifyRule> rules) {
        if (rules != null) {
            this.rules.clear();
            this.rules.addAll(rules);
        }
    }

    public synchronized List<INotifyRule> getRules() {
		return rules;
	}
    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.notice.IEventListener#onNewEvent(java.lang.Object[])
     */
    @Override
    public void onNewEvent(EventDescription[] data) {
        List<EventDescription> toSend = new ArrayList<EventDescription>();
        for (EventDescription event : data) {
            if (shouldNotify(event)) {
                toSend.add(event);
            }
        }
        if (!toSend.isEmpty()) {
            notify(toSend);
        }
    };

    protected synchronized boolean shouldNotify(EventDescription event) {
        for (INotifyRule rule : rules) {
            if (rule.match(event)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 
     * <i>Description:</i> notify a list of events
     * 
     * @param toSend
     */
    protected abstract void notify(List<EventDescription> toSend);

}
