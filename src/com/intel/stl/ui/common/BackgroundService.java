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

package com.intel.stl.ui.common;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public class BackgroundService implements IBackgroundService {

    private static final long DEFAULT_SHUTDOWN_TIMEOUT = 1000;

    private final ExecutorService service;

    private final AtomicBoolean shutdown = new AtomicBoolean(false);

    private final long timeout;

    public BackgroundService(ExecutorService service) {
        this.service = service;
        this.timeout = DEFAULT_SHUTDOWN_TIMEOUT;
    }

    @Override
    public Future<Void> submit(Runnable runnable) {
        Exception e = new Exception("Caller's stack trace");
        BackgroundTask<Void> task =
                new BackgroundTask<Void>(runnable, shutdown, e);
        try {
            service.submit(task);
        } catch (RejectedExecutionException ree) {
            if (!shutdown.get()) {
                throw ree;
            }
        }
        return task;
    }

    @Override
    public <V> Future<V> submit(Callable<V> callable) {
        Exception e = new Exception("Caller's stack trace");
        BackgroundTask<V> task = new BackgroundTask<V>(callable, shutdown, e);
        service.submit(task);
        return task;
    }

    @Override
    public void shutdown() {
        shutdown.set(true);
        service.shutdown();
        try {
            service.awaitTermination(timeout, TimeUnit.MILLISECONDS);
            if (!service.isTerminated()) {
                service.shutdownNow();
            }

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isShutdown() {
        return shutdown.get();
    }

    private class BackgroundTask<V> extends FutureTask<V> {

        private final AtomicBoolean shutdown;

        private final Exception clientTrace;

        public BackgroundTask(Callable<V> callable, AtomicBoolean shutdown,
                Exception clientTrace) {
            super(callable);
            this.shutdown = shutdown;
            this.clientTrace = clientTrace;
        }

        public BackgroundTask(Runnable runnable, AtomicBoolean shutdown,
                Exception clientTrace) {
            super(runnable, null);
            this.shutdown = shutdown;
            this.clientTrace = clientTrace;
        }

        @Override
        protected void setException(Throwable t) {
            if (shutdown.get()) {
                System.out.println("Exception ignored after shutdown: " + t);
            } else {
                if (clientTrace != null) {
                    t.addSuppressed(clientTrace);
                }
                super.setException(t);
            }

        }

    }
}
