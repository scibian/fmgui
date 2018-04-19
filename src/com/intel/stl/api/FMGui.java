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

package com.intel.stl.api;

import java.util.List;

public interface FMGui extends StartupProgressObserver {

    /**
     *
     * <i>Description:</i>initializes a FM graphical user interface (front end
     * code). Implementors should initialize their UI components in preparation
     * for the front end code to be invoked after the back end code is
     * initialized (see invokeMain)
     *
     * @param appContext
     *            the application context to be used in UI operations
     */
    void init(AppContext appContext);

    /**
     *
     * <i>Description:</i>passes control to the front end code after the back
     * end code has been initialized.
     *
     * @param firstRun
     *            a boolean indicating whether the application is being run for
     *            the first time. The meaning of this flag is implementation
     *            dependent.
     */
    void invokeMain(boolean firstRun);

    /**
     *
     * <i>Description:</i>requests a shutdown for the front end code.
     *
     */
    void shutdown();

    /**
     *
     * <i>Description:</i>return the application context associated with the
     * front end code.
     *
     * @return
     */
    AppContext getAppContext();

    /**
     *
     * <i>Description:</i>requests the front end code to display the list of
     * errors that occurred during back end processing
     *
     * @param errors
     *            a list of errors in the back end code
     */
    void showErrors(List<Throwable> errors);
}
