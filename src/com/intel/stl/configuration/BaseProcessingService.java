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

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import org.slf4j.MDC;

public class BaseProcessingService implements ProcessingService {

    private static final int DEFAULT_POOL_SIZE = 2;

    private static final long DEFAULT_SHUTDOWN_TIMEOUT = 1000;

    private final ExecutorService processingService;

    public BaseProcessingService() {
        this(Executors.newFixedThreadPool(DEFAULT_POOL_SIZE));
    }

    public BaseProcessingService(ExecutorService service) {
        this.processingService = service;
    }

    @Override
    public <T> void submit(AsyncTask<T> task, ResultHandler<T> handler) {
        if (processingService.isShutdown()) {
            return;
        }
        executeTask(task, handler, processingService);
    }

    @Override
    public void shutdown() {
        shutdownService(processingService, DEFAULT_SHUTDOWN_TIMEOUT);
    }

    @Override
    public void shutdown(long timeout) {
        shutdownService(processingService, timeout);
    }

    // We don't want the same task to be submitted twice (to either executor
    // service). As long as the submitter creates a new task every time it
    // submits a task, it is guaranteed that the task will execute. If multiple
    // threads try to submit the same task instance, neither can know which
    // handler will be invoked.
    protected synchronized <T> void executeTask(AsyncTask<T> task,
            ResultHandler<T> handler, ExecutorService executor) {
        FutureTask<T> currFuture = task.getFuture();
        if (currFuture == null) {
            task.setLoggingContextMap(MDC.getCopyOfContextMap());
            AsyncFutureTask<T> future = new AsyncFutureTask<T>(task, handler);
            task.setFuture(future);
            future.setClientTrace(new Exception("Caller's stack trace"));
            executor.execute(future);
        }
    }

    protected void shutdownService(ExecutorService executor, long millis) {
        executor.shutdown();
        try {
            executor.awaitTermination(millis, TimeUnit.MILLISECONDS);
            if (!executor.isTerminated()) {
                executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    protected class AsyncFutureTask<V> extends FutureTask<V> {

        private Exception clientTrace;

        private final ResultHandler<V> handler;

        public AsyncFutureTask(Callable<V> callable, ResultHandler<V> handler) {
            super(callable);
            this.handler = handler;
        }

        @Override
        protected void setException(Throwable t) {
            if (clientTrace != null) {
                t.addSuppressed(clientTrace);
            }
            super.setException(t);
        }

        @Override
        protected void done() {
            if (handler != null) {
                handler.onTaskCompleted(this);
            }
        }

        protected void setClientTrace(Exception clientTrace) {
            this.clientTrace = clientTrace;
        }

        protected Exception getClientTrace() {
            return clientTrace;
        }
    }
}
