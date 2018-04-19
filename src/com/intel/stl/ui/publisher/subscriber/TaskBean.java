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

import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Description: TaskBean class to hold the task information
 * 
 * @param <E>
 * 
 * @param taskList
 *            list of tasks
 * 
 * @param tasks
 *            task in the list
 * 
 * @param callback
 *            method to call when scheduled time has arrived
 * 
 * @param caller
 *            object that registered the task
 */
class TaskBean<E> {

    private final Task<E> task;

    private ICallback<E> callback = null;

    private ICallback<E[]> callbacks = null;

    private final Callable<E> caller;

    public TaskBean(Task<E> task, ICallback<E> callback, Callable<E> caller) {

        this.task = task;
        this.callback = callback;
        this.caller = caller;
    }

    public TaskBean(List<Task<?>> taskList, Task<E> task,
            ICallback<E[]> callbacks, Callable<E> caller) {

        this.task = task;
        this.callbacks = callbacks;
        this.caller = caller;
    }

    public Task<E> getTask() {
        return task;
    }

    public ICallback<E> getCallback() {
        return callback;
    }

    public ICallback<E[]> getCallbacks() {
        return callbacks;
    }

    public Callable<E> getCaller() {
        return caller;
    }

    @Override
    public String toString() {

        return "Task [task=" + task.toString() + ", callback=" + callback
                + ", caller=" + caller + "]";

    }
} // class TaskBean

