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

package com.intel.stl.ui.publisher.subscriber;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.DefaultDeviceGroup;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.StateSummary;
import com.intel.stl.ui.publisher.EventCalculator;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

public class EventSubscriber extends Subscriber<StateSummary> {
    private static Logger log = LoggerFactory.getLogger(EventSubscriber.class);

    // reserved KEY. should be different from all the PA reserved keys that in
    // the range of (0x00-0xff).
    private static int KEY = 0x10000000;

    private final EventCalculator evtCal;

    /**
     * Description:
     *
     * @param taskScheduler
     * @param perfApi
     * @param evtCal
     */
    public EventSubscriber(IRegisterTask taskScheduler,
            EventCalculator evtCal) {
        super(taskScheduler, null);
        this.evtCal = evtCal;
    }

    public synchronized Task<StateSummary> registerStateSummary(
            ICallback<StateSummary> callback) {
        Task<StateSummary> task =
                new Task<StateSummary>(KEY, DefaultDeviceGroup.ALL.name(),
                        UILabels.STL40012_DEVICE_STATES.getDescription());
        Callable<StateSummary> caller = new Callable<StateSummary>() {
            @Override
            public StateSummary call() throws Exception {
                StateSummary res = evtCal.getSummary();
                return res;
            }
        };
        try {
            Task<StateSummary> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);
            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public synchronized void deregisterStateSummary(Task<StateSummary> task,
            ICallback<StateSummary> callback) {
        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
