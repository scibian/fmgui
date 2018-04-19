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
package com.intel.stl.dbengine.impl;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.datamanager.DatabaseCall;
import com.intel.stl.dbengine.DatabaseServer;
import com.intel.stl.dbengine.Scheduler;

public class SchedulerImpl implements Scheduler {
    private static Logger log = LoggerFactory.getLogger(SchedulerImpl.class);

    private final ExecutorService pool;

    private final DatabaseServer server;

    public SchedulerImpl(DatabaseServer server, int poolSize) {
        this.server = server;
        ThreadFactory threadFactory = new DatabaseThreadFactory(server);
        pool = Executors.newFixedThreadPool(poolSize, threadFactory);
    }

    @Override
    public synchronized <T> Future<T> enqueue(DatabaseCall<T> workItem) {
        workItem.setDatabaseServer(server);
        workItem.setClientTrace(new Exception("Caller's stack trace"));
        Future<T> future = pool.submit(workItem);
        workItem.setFuture(future);
        return future;
    }

    @Override
    public void shutdown() {
        log.info("Scheduler shutdown in progress");
        pool.shutdown();
        try {
            pool.awaitTermination(2L, TimeUnit.SECONDS);
            if (!pool.isTerminated()) {
                pool.shutdownNow();
            }
            log.info("Scheduler shutdown complete.");
        } catch (InterruptedException e) {
            log.info("Scheduler shutdown interrupted.", e);
        }
    }

}
