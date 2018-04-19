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

public interface IFailureManagement {
    /**
     * 
     * <i>Description:</i> submit a task failure to the failure manager when we
     * see an exception. The failure item ({@link ITaskFailure}) will provide a
     * task that can be used to retry to see if we can recover. When we do this,
     * the failure manager should consider two possible situations:
     * <ol>
     * <li>The task itself will throw exceptions. The manager should capture
     * these exceptions and call the provided failure item's
     * {@link ITaskFailure#onFatal} when it sees it as unrecoverable.
     * <li>The task will indirectly cause another call to this
     * {@link #submit(ITaskFailure, Throwable)} method, for example a task that
     * sends a command to a remote server.
     * </ol>
     * 
     * @param failure
     *            the failure item. see {@link ITaskFailure}
     * @param error
     *            the reason we submit a failure item. The value can be
     *            <code>null</code> and it's depend on the implementation to
     *            interpret it. For example, one implementation may treat
     *            <code>null</code> as task being executed successfully and uses
     *            this information to clear or reset its memory about this task.
     *            Another implementation may just ignore it and clear or reset
     *            memory about a task purely based on time, such as any failures
     *            no update for 3 minutes are treated as successfully recovered.
     *            One of the advantages of this time based approach is that the
     *            caller needn't to call this method every time no matter
     *            whether it has an exception or not.
     */
    void submit(ITaskFailure<Void> failure, Throwable error);

    /**
     * 
     * <i>Description:</i> Blocked version
     * {@link #submit(ITaskFailure, Throwable)}. This method will wait until the
     * failure is recovered or it is identified as unrecoverable and
     * {@link ITaskFailure#onFatal()} is executed.
     * 
     * @param failure
     * @param error
     * @return task result
     */
    <E> E evaluate(ITaskFailure<E> failure, Throwable error);

    /**
     * 
     * Description: cleanup this failure manager, such as release resources.
     * 
     * @throws InterruptedException
     * 
     */
    void cleanup() throws InterruptedException;
}
