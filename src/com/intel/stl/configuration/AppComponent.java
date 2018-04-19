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
package com.intel.stl.configuration;

import com.intel.stl.api.StartupProgressObserver;

/**
 */
public interface AppComponent {

    /**
     *
     * <i>Description:</i>initializes the application component
     *
     * @param settings
     *            the settings for the application
     * @param observer
     *            an observer interested in application startup/shutdown
     *            progress
     * @throws AppConfigurationException
     */
    void initialize(AppSettings settings, StartupProgressObserver observer)
            throws AppConfigurationException;

    /**
     *
     * <i>Description:</i>returns the component's description
     *
     * @return a description
     */
    String getComponentDescription();

    /**
     *
     * <i>Description:</i>returns the expected weight this component has in the
     * overall application initialization process. This weight, relative to
     * other component's weight, determines the progress achieved after the
     * component is initialized
     *
     * @return this component's initialization weight
     */
    int getInitializationWeight();

    /**
     *
     * <i>Description:</i>shuts down this application component
     *
     * @param observer
     *            an observer interested in application startup/shutdown
     *            progress
     */
    void shutdown(StartupProgressObserver observer);

}
