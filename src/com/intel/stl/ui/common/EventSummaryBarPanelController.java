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

package com.intel.stl.ui.common;

import static com.intel.stl.api.configuration.UserSettings.PROPERTY_TIMING_WINDOW;
import static com.intel.stl.ui.common.PageWeight.LOW;

import java.util.EnumMap;
import java.util.Properties;

import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.view.EventSummaryBarPanel;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.StateSummary;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.IStateChangeListener;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.EventSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;

/**
 * A controller for the severity count summary panel (EventSummaryBarPanel)
 * that goes to pin board.
 */
public class EventSummaryBarPanelController implements IContextAware,
        IStateChangeListener {

    private static final String NAME = "EventSummaryBar";

    private final EventSummaryBarPanel view;

    private Context context;

    private IEventSummaryBarListener iEventSummaryBarListener;

    private EventSubscriber eventSubscriber;

    private ICallback<StateSummary> stateSummaryCallback;

    private Task<StateSummary> stateSummaryTask;
    
    private String timingWindow;

    public EventSummaryBarPanelController(
            EventSummaryBarPanel eventSummaryBarPanel) {
        this.view = eventSummaryBarPanel;
    }

    public void setEventSummaryBarListener(
            IEventSummaryBarListener iEventSummaryBarListener) {
        this.iEventSummaryBarListener = iEventSummaryBarListener;
        view.setEventSummaryBarListener(this.iEventSummaryBarListener);
    }

    /**
     * @param context
     *            the context to set
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        clear();

        this.context = context;
        UserSettings userSettings = this.context.getUserSettings();
        if(userSettings != null){
        	Properties userPreference = userSettings.getUserPreference();
        	if(userPreference != null){
        		this.timingWindow = userPreference.getProperty(PROPERTY_TIMING_WINDOW);
        		view.setTimingWindow(this.timingWindow);
        	}
        }
        
        this.context.getEvtCal().addListener(this);

        TaskScheduler scheduler = this.context.getTaskScheduler();
        eventSubscriber =
                (EventSubscriber) scheduler.getSubscriber(SubscriberType.EVENT);
        stateSummaryCallback = new CallbackAdapter<StateSummary>() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(StateSummary result) {
                if (result != null) {
                    onStateChange(result);
                }
            }
        };
        stateSummaryTask =
                eventSubscriber.registerStateSummary(stateSummaryCallback);
    }

    @Override
    public String getName() {
        return NAME;
    }

    protected void processStateSummary(StateSummary stateSummary) {
        // Update Event Summary into the EventSummaryBarPanel
        final EnumMap<NoticeSeverity, Integer> states =
                stateSummary.getStates(null);
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                view.updateEventSeverity(states);
            }
        });
              
        UserSettings userSettings = this.context.getUserSettings();
        if(userSettings != null){
        	Properties userPreference = userSettings.getUserPreference();
        	if(userPreference != null){
        		String timingWindow = userPreference.getProperty(PROPERTY_TIMING_WINDOW);
        		if(!this.timingWindow.equals(timingWindow)){
                	this.timingWindow = timingWindow;
                	view.setTimingWindow(this.timingWindow);
                };
        	}
        }  
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return LOW;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return LOW;
    }

    @Override
    public void onStateChange(StateSummary summary) {
        if (summary != null) {
            // System.out.println("EventSummary.onStateChange called.");
            processStateSummary(summary);
        }
    }

    protected void clear() {
        if (context != null && context.getEvtCal() != null) {
            context.getEvtCal().removeListener(this);
        }

        if (eventSubscriber != null && stateSummaryTask != null) {
            eventSubscriber.deregisterStateSummary(stateSummaryTask,
                    stateSummaryCallback);
        }
    }

    @Override
    public String toString() {
        return "EventSummaryBarPanelController";
    }
}
