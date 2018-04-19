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

import java.util.List;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.performance.FocusPortsRspBean;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.ui.publisher.FocusPortsTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Subscriber class to schedule tasks for collecting focus port counter beans
 */
public class FocusPortCounterSubscriber
        extends Subscriber<List<FocusPortsRspBean>> {

    private static Logger log =
            LoggerFactory.getLogger(FocusPortCounterSubscriber.class);

    public FocusPortCounterSubscriber(IRegisterTask taskScheduler,
            IPerformanceApi perfApi) {
        super(taskScheduler, perfApi);
    }

    public synchronized Task<List<FocusPortsRspBean>> registerFocusPorts(
            final String group, final Selection selection, final int range,
            ICallback<List<FocusPortsRspBean>> callback) {
        Task<List<FocusPortsRspBean>> task =
                new FocusPortsTask<List<FocusPortsRspBean>>(group, selection,
                        range);
        Callable<List<FocusPortsRspBean>> caller =
                new Callable<List<FocusPortsRspBean>>() {
                    @Override
                    public List<FocusPortsRspBean> call() throws Exception {
                        List<FocusPortsRspBean> ports =
                                perfApi.getFocusPorts(group, selection, range);
                        return ports;
                    }
                };
        try {
            Task<List<FocusPortsRspBean>> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);

            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public synchronized void deregisterFocusPorts(
            Task<List<FocusPortsRspBean>> task,
            ICallback<List<FocusPortsRspBean>> callback) {
        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
