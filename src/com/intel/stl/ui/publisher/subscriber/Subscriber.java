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

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.ui.publisher.HistoryQueryTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

public abstract class Subscriber<E> {

    protected List<Task<E>> taskList = new ArrayList<Task<E>>();

    protected final IRegisterTask taskScheduler;

    protected final IPerformanceApi perfApi;

    /**
     * Description:
     *
     * @param taskScheduler
     * @param perfApi
     */
    public Subscriber(IRegisterTask taskScheduler, IPerformanceApi perfApi) {
        super();
        this.taskScheduler = taskScheduler;
        this.perfApi = perfApi;
    }

    public synchronized void cancelTask(Task<E> task) {
        if (task != null) {
            task.getFuture().cancel(true);
        }
    }

    public synchronized void cancelTasks() {
        for (Task<E> task : taskList) {
            if (task != null) {
                task.getFuture().cancel(true);
            }
        }

        taskList.clear();
    }

    public synchronized void rescheduleTasks() {
        List<Task<E>> newTaskList = new ArrayList<Task<E>>();
        for (Task<E> task : taskList) {
            if (task != null) {
                // remove callbacks first, so changes on this task have no
                // impact on UI
                List<ICallback<E>> callbacks = task.clearCallbacks();
                // it's safe to cancel now since callbacks are detached from the
                // task
                task.getFuture().cancel(true);
                // schedule tasks on taskScheduler's ExecutorService that should
                // be a new created one
                for (ICallback<E> callback : callbacks) {
                    callback.reset();
                    taskScheduler.scheduleTask(newTaskList, task, callback,
                            task.getCaller());
                }
            }
        }
        taskList = newTaskList;
    }

    protected <T extends ITimestamped> Future<Void> submitHistoryQueryTask(
            HistoryQueryTask<T> task) {
        Future<Void> future = taskScheduler.submitToBackground(task);
        task.setFuture(future);
        return future;
    }

}
