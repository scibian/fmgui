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

package com.intel.stl.fecdriver.dispatcher;

import java.util.Map;
import java.util.concurrent.Callable;

import org.slf4j.MDC;

/**
 * The Reactor architectural pattern.
 * 
 * In this design pattern, the handler is responsible to process an event. They
 * react to the occurrence of specific events
 * 
 */
public abstract class Handler<R> implements Callable<R> {

    private Map<String, String> loggingContextMap;

    /**
     * 
     * <i>Description:</i> dispatches this handler's processing in the same
     * thread as the reactor
     * 
     * @throws Exception
     */
    public void dispatch() throws Exception {
        initLoggingContext();
        handle();
    }

    /**
     * This method is invoked as part of a FutureTask execution. It is meant to
     * be used in conjunction with a ExecutorService
     */
    @Override
    public R call() throws Exception {
        initLoggingContext();
        R result = handle();
        return result;
    }

    protected abstract R handle() throws Exception;

    protected void setLoggingContextMap(Map<String, String> loggingContextMap) {
        this.loggingContextMap = loggingContextMap;
    }

    private void initLoggingContext() {
        if (loggingContextMap != null) {
            MDC.setContextMap(loggingContextMap);
        }
    }

}
