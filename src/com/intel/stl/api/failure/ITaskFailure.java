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

package com.intel.stl.api.failure;

import java.util.concurrent.Callable;

/**
 * Description of a TASK failure that allows us identify the task that caused
 * the failure, and apply proper behaviors, such as retry or treat it as fatal
 * failure that is unrecoverable.
 */
public interface ITaskFailure<E> {
    /**
     * 
     * <i>Description:</i> ID that identifies the task that caused the failure
     * 
     * @return the id
     */
    Object getTaskId();

    /**
     * 
     * <i>Description:</i> The task can be used to retry to recover the failure
     * 
     * @return the task
     */
    Callable<E> getTask();

    /**
     * 
     * <i>Description:</i> transfer an <code>error</code> exception to a
     * FailureType so we a Failure Manager can handle the failure properly
     * 
     * @param error
     *            the exception that cause the failure
     * @return the {@link FailureType}
     */
    FailureType getFailureType(Throwable error);

    /**
     * 
     * <i>Description:</i> called when the failure is identified as
     * unrecoverable
     * 
     */
    void onFatal();
}
