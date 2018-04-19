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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

/**
 * The Reactor architectural pattern.
 * 
 * This design pattern inverts the flow of control within the application. The
 * reactor waits for event and synchronously demultiplexes them to event
 * handlers. Handlers are responsible for processing the event and then
 * notifying clients about outcomes (Don't call us, we'll call you). In summary,
 * the reactor dispatches event handlers that react to events.
 */
public abstract class Reactor<H extends Handler<R>, R> extends Thread {
    protected static Logger log = LoggerFactory.getLogger(Reactor.class);

    private static final long SHUTDOWN_TIMEOUT = 1000;

    private boolean shutdown = false;

    private final ExecutorService threadPool;

    public Reactor(ExecutorService service) {
        this.threadPool = service;
    }

    /**
     * 
     * <i>Description:</i> dispatches a handler The current implementation uses
     * a thread pool but we could dispatch the handler in the same thread as the
     * reactor.
     * 
     * @param handler
     *            a handler
     */
    protected void dispatch(H handler) {
        if (shutdown) {
            return;
        }
        handler.setLoggingContextMap(MDC.getCopyOfContextMap());
        HandlerTask task = createHandlerTask(handler);
        threadPool.execute(task);
    }

    /**
     * 
     * <i>Description:</i> creates a FutureTask to dispatch a handler
     * 
     * @param handler
     *            the handler
     * @return a FutureTask
     */
    protected HandlerTask createHandlerTask(H handler) {
        return new HandlerTask(handler);
    }

    /**
     * 
     * <i>Description:</i> shut downs the reactor
     * 
     */
    protected void shutdown() {
        shutdown = true;
        threadPool.shutdown();
        try {
            threadPool
                    .awaitTermination(SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS);
            if (!threadPool.isTerminated()) {
                threadPool.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 
     * <i>Description:</i> processes the outcome of an event after being handled
     * by the handler
     * 
     * @param future
     *            a result of an asynchronous handler processing
     */
    protected abstract void onHandlerDone(HandlerTask future);

    protected class HandlerTask extends FutureTask<R> {

        private final H callable;

        protected HandlerTask(H callable) {
            super(callable);
            this.callable = callable;
        }

        @Override
        protected void setException(Throwable t) {
            super.setException(t);
            log.error("Exception occurred in handler {}", callable, t);
        }

        @Override
        protected void done() {
            onHandlerDone(this);
        }

        protected H getCallable() {
            return callable;
        }
    }

}
