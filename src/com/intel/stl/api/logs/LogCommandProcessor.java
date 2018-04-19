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

package com.intel.stl.api.logs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.common.STLMessages;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSession;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;

/**
 * Single thread task to send commands to the remote SSH server, wait for the
 * responses, and send them to the listeners
 */
public class LogCommandProcessor {
    private static Logger log =
            LoggerFactory.getLogger(LogCommandProcessor.class);

    private final boolean DEBUG = false;

    private final boolean DEBUG_COMMANDS = false;

    private final ExecutorService service;

    private Future<LogResponse> future;

    private final JSchSession jschSession;

    private boolean initialized;

    private ChannelExec execChannel;

    private BufferedReader inputBuffer = null;

    private IResponseListener responseListener;

    private LogMessageType msgType;

    private long timeoutInMs;

    private final String identifier;

    private final LogCommandProcessor cmdProc = this;

    private boolean processingDone = false;

    private boolean shutdown = false;

    public LogCommandProcessor(JSchSession jschSession, int timeoutInMs,
            String id) {
        super();
        identifier = id + ":commandProcessingTask";
        service = Executors.newSingleThreadExecutor(new LogThreadFactory(id));
        this.jschSession = jschSession;
        this.timeoutInMs = timeoutInMs;
    }

    public void setResponseListener(IResponseListener listener) {
        responseListener = listener;
    }

    public void setTimeoutInMs(long timeoutMs) {
        this.timeoutInMs = timeoutMs;
    }

    public void stop() {
        shutdown = true;
        shutdown();
    }

    protected boolean initializeChannel() throws JSchException, IOException {

        boolean initialized = false;

        execChannel = jschSession.getExecChannel();
        inputBuffer = new BufferedReader(
                new InputStreamReader(execChannel.getInputStream()));
        execChannel.setErrStream(System.err);

        // Wait for the channel to be initialized
        long currentTime = System.currentTimeMillis();
        long expiredTime = currentTime + timeoutInMs;
        while (execChannel.isClosed() && !execChannel.isConnected()
                && (currentTime < expiredTime)) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            currentTime = System.currentTimeMillis();
        }
        initialized = !execChannel.isClosed();

        return initialized;
    }

    protected String getLine() {

        String inputLine = null;

        try {
            inputLine = inputBuffer.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return inputLine;
    }

    protected boolean inputReady() {

        boolean ready = false;

        try {
            long currentTime = System.currentTimeMillis();
            long expireTime = currentTime + timeoutInMs;

            // Wait for either the end of the data, data to be available on the
            // input stream, or the timeout to expire
            while (!(ready = inputBuffer.ready())
                    && (currentTime < expireTime)) {
                Thread.sleep(200);
                currentTime = System.currentTimeMillis();
            }
        } catch (InterruptedException e) {
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        if (DEBUG) {
            if (!ready) {
                debug("!Received:", "Response Timeout!");
            }
        }

        return ready;
    }

    public void executeCommand(LogMessageType msgType, String cmd) {
        try {
            future = service
                    .submit(new LogCommandTask(new LogCommand(msgType, cmd)));

            // Process the result
            if ((future != null) && !future.isCancelled()) {

                LogResponse response = future.get();
                if (response.getEntries().size() > 0) {

                    // If the 'ls' command failed when checking for the log
                    // file, then we must be connected directly to an
                    // ESM - which should never be done!
                    if (response.getEntries().get(0)
                            .equals(STLMessages.STL50014_ESM_COMMAND_NOT_FOUND
                                    .getDescription())) {
                        responseListener.onResponseError(
                                LogErrorType.SYSLOG_ACCESS_ERROR,
                                response.getMsgType());
                    } else {
                        responseListener.onResponseReceived(response);
                        if (DEBUG_COMMANDS) {
                            debug("Receive:", response.getEntries().get((0)));
                        }
                    }
                } else {
                    if (DEBUG_COMMANDS) {
                        debug("Receive:", "Response Timeout!");
                    }

                    LogErrorType errorCode;
                    switch (msgType) {
                        case CHECK_FOR_FILE:
                            errorCode = LogErrorType.LOG_FILE_NOT_FOUND;
                            break;

                        case LAST_LINES:
                            errorCode = LogErrorType.EMPTY_LOG_FILE;
                            break;

                        default:
                            errorCode = LogErrorType.RESPONSE_TIMEOUT;
                            break;
                    }
                    responseListener.onResponseError(errorCode,
                            response.getMsgType());
                }
            }
        } catch (InterruptedException | ExecutionException e) {
            log.error(e.getCause().getMessage(), e);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void shutdown() {
        if (future != null) {
            future.cancel(true);
        }

        service.shutdown(); // Disable new tasks from being submitted
        try {
            // Wait a while for existing tasks to terminate
            if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                service.shutdownNow(); // Cancel currently executing tasks
                // Wait a while for tasks to respond to being cancelled
                if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.warn("ExecutorService did not terminate");
                }
            }
        } catch (InterruptedException ie) {
            // (Re-)Cancel if current thread also interrupted
            service.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }

        try {
            execChannel.getInputStream().close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void debug(String... msgs) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.println(formatter.format(new Date()) + ": " + identifier
                + ": " + msgs[0] + " " + msgs[1]);
    }

    protected class LogCommandTask implements Callable<LogResponse> {

        private final LogCommand logMsg;

        public LogCommandTask(LogCommand logMsg) {
            this.logMsg = logMsg;
        }

        /*
         * (non-Javadoc)
         *
         * @see java.util.concurrent.Callable#call()
         */
        @Override
        public LogResponse call() throws Exception {

            // Get a command from the queue
            cmdProc.msgType = logMsg.getMsgType();
            LogResponse response = new LogResponse(msgType);

            try {
                // Process command or exit
                if (!logMsg.getMsgType().equals(LogMessageType.EXIT)) {

                    // Set up the channel if needed
                    if ((execChannel == null) || (execChannel.isClosed())) {
                        initialized = initializeChannel();
                    }

                    if (DEBUG) {
                        debug("Sending:", logMsg.getMsgType().toString());
                    }

                    // Send the command
                    if (initialized) {
                        execChannel.setCommand(logMsg.getCommand());
                        execChannel.connect();
                        execChannel.getOutputStream().flush();

                        if (DEBUG_COMMANDS) {
                            debug("Sending:", logMsg.getCommand());
                        }
                    }

                    processingDone = false;
                    String entry = new String("");

                    // Process lines until the EOF is encountered or timeout
                    while (!shutdown && !Thread.currentThread().isInterrupted()
                            && (!processingDone && inputReady())) {

                        entry = getLine();
                        if (entry != null) {
                            processingDone =
                                    entry.equals(LogCommander.RESPONSE_EOM);
                        }

                        if (!processingDone) {
                            response.addEntry(entry);
                        }

                        if (DEBUG) {
                            debug("Receive:", entry);
                        }
                    }
                }
            } catch (JSchException e) {
                log.error(e.getMessage(), e);
                shutdown();
            }

            return response;
        }
    } // class LogCommandTask
}
