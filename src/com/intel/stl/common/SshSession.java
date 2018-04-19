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

package com.intel.stl.common;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;

/**
 * Utility class to submit a command thru a SSH session and receive its output
 * as an array of strings (one string per console output line) or a future, if
 * user wants to handle the wait.
 */
public class SshSession {
    private static Logger log = LoggerFactory.getLogger(SshSession.class);

    private String host;

    private int port;

    private String userName;

    private Session session = null;

    private String charsetName = "ASCII";

    private int exitStatus;

    private final ExecutorService readingThread;

    private JSch jsch;

    public SshSession() throws JSchException {
        this.jsch = createJSch();
        this.readingThread = Executors
                .newSingleThreadExecutor(new SshSessionThreadFactory());
    }

    public void connect(String host, int port, String user, char[] password)
            throws JSchException {
        disconnect();
        this.host = host;
        this.port = port;
        this.userName = user;
        this.session = jsch.getSession(user, host, port);
        this.session.setUserInfo(new MyUserInfo(password));
        session.connect();
    }

    public SshSession(Session session) {
        this.session = session;
        this.readingThread = Executors
                .newSingleThreadExecutor(new SshSessionThreadFactory());
    }

    public void setCharsetName(String charsetName) {
        this.charsetName = charsetName;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public String getUserName() {
        return userName;
    }

    public List<String> exec(String command) throws Exception {
        return executeCommand(command, null);
    }

    public List<String> exec(String command, long timeout) throws Exception {
        return executeCommand(command, new Long(timeout));
    }

    public Future<List<String>> submit(String command)
            throws IOException, JSchException {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setPty(true);
        SshOutputLineReader reader = new SshOutputLineReader(channel);
        Future<List<String>> future = readingThread.submit(reader);
        channel.connect();
        return future;
    }

    public int getLastExitStatus() {
        return exitStatus;
    }

    public void disconnect() {
        if (session != null) {
            session.disconnect();
        }
    }

    protected List<String> executeCommand(String command, Long timeout)
            throws Exception {
        ChannelExec channel = (ChannelExec) session.openChannel("exec");
        channel.setCommand(command);
        channel.setPty(true);
        SshOutputLineReader reader = new SshOutputLineReader(channel);
        Future<List<String>> future = readingThread.submit(reader);
        channel.connect();

        List<String> response;
        try {
            if (timeout == null) {
                response = future.get();
            } else {
                response = future.get(timeout, TimeUnit.MILLISECONDS);
            }
        } catch (TimeoutException e) {
            future.cancel(true);
            throw e;
        } catch (InterruptedException e) {
            log.warn(
                    "SSH session command execution interrupted after reading {} lines.",
                    reader.getLinesRead());
            future.cancel(true);
            response = reader.getPartialResults();
        } catch (ExecutionException e) {
            Exception cause = (Exception) e.getCause();
            log.error("SSH session command execution had an error: {}",
                    cause.getMessage(), cause);
            throw cause;
        } finally {
            exitStatus = channel.getExitStatus();
            reader.close();
            channel.disconnect();
        }
        return response;
    }

    private class SshOutputLineReader implements Callable<List<String>> {

        private int lineCt;

        private final PipedInputStream snk;

        private final PipedOutputStream src;

        private final BufferedReader br;

        private final InputStream in;

        private final List<String> read;

        private final ChannelExec channel;

        public SshOutputLineReader(ChannelExec channel) throws IOException {
            this.channel = channel;
            this.in = channel.getInputStream();
            this.read = new ArrayList<String>();
            snk = new PipedInputStream();
            src = new PipedOutputStream(snk);
            channel.setOutputStream(src);
            channel.setErrStream(src);
            InputStreamReader reader = new InputStreamReader(snk, charsetName);
            br = new BufferedReader(reader);
        }

        @Override
        public List<String> call() throws Exception {
            while ((in.available() > 0) || !channel.isClosed()) {
                String line = br.readLine();
                if (line != null) {
                    read.add(line);
                    lineCt++;
                }
            }
            // Make sure the buffer is completely read
            while (br.ready()) {
                String line = br.readLine();
                read.add(line);
                lineCt++;
            }
            return read;
        }

        public int getLinesRead() {
            return lineCt;
        }

        public List<String> getPartialResults() {
            return read;
        }

        public void close() {
            try {
                try {
                    src.close();
                } finally {
                    this.br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class SshSessionThreadFactory implements ThreadFactory {

        private static final String SSHSESSION_THREAD_PREFIX =
                "sshread-thread-";

        private final AtomicInteger threadCount = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            String threadName =
                    SSHSESSION_THREAD_PREFIX + threadCount.getAndIncrement();
            return new Thread(r, threadName);
        }

    }

    private class MyUserInfo implements UserInfo {

        private final String password;

        public MyUserInfo(char[] password) {
            this.password = new String(password);
        }

        @Override
        public String getPassphrase() {
            return null;
        }

        @Override
        public String getPassword() {
            return password;
        }

        @Override
        public boolean promptPassphrase(String message) {
            return true;
        }

        @Override
        public boolean promptPassword(String message) {
            return true;
        }

        @Override
        public boolean promptYesNo(String str) {
            System.out.println(str);
            return true;
        }

        @Override
        public void showMessage(String message) {
            System.out.println("showMessage: " + message);
        }

    }

    // For testing
    protected JSch createJSch() throws JSchException {
        return Utils.createJSch();
    }

}
