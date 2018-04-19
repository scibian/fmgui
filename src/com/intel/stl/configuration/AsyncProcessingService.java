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

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class AsyncProcessingService extends BaseProcessingService implements
        SerialProcessingService {

    private static final int DEFAULT_POOL_SIZE = 2;

    private static final long SERIAL_SHUTDOWN_TIMEOUT = 10000; // 10 secs

    // This value is expressly set to 1 to serialize tasks. DO NOT CHANGE to any
    // value other than 1
    private static final int SERIAL_POOL_SIZE = 1;

    private final ExecutorService serialService;

    public AsyncProcessingService() {
        super(Executors.newFixedThreadPool(DEFAULT_POOL_SIZE,
                new ProcessingServiceThreadFactory()));

        ThreadFactory pssThreadFactory = new SerialServiceThreadFactory();
        serialService =
                Executors
                        .newFixedThreadPool(SERIAL_POOL_SIZE, pssThreadFactory);
    }

    @Override
    public <T> void submitSerial(AsyncTask<T> task, ResultHandler<T> handler) {
        if (serialService.isShutdown()) {
            return;
        }
        executeTask(task, handler, serialService);
    }

    @Override
    public void shutdown() {
        shutdownService(serialService, SERIAL_SHUTDOWN_TIMEOUT);
        super.shutdown();
    }

    @Override
    public void shutdown(long timeout) {
        shutdownService(serialService, timeout);
        super.shutdown(timeout);
    }
}
