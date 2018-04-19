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

package com.intel.stl.fecdriver.network.ssh.impl;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.STLMessages;
import com.intel.stl.common.SshSession;
import com.intel.stl.fecdriver.network.ssh.IJSchSession;
import com.intel.stl.fecdriver.network.ssh.JSchChannelType;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

/**
 * The JSchSession class is a wrapper for the JSch session type. It hides the
 * details of session creation and provides access methods to obtain Exec, Sftp,
 * or Shell channels from it; see also JSchSessionFactory.
 */
public class JSchSession implements IJSchSession {

    @SuppressWarnings("unused")
    private final static Logger log =
            LoggerFactory.getLogger(JSchSession.class);

    private final boolean DEBUG_SESSION = false;

    private final String CONNECTION_THREAD_NAME =
            "JSchSession:connectionThread";

    private boolean connected;

    private Session session;

    private final SubnetDescription subnet;

    private final boolean strictHostKey;

    private final String sshKey;

    /**
     * Threads
     */
    private Thread connectionThread;

    // Connection Thread for maintaining session connection
    private boolean connectionThreadRunning = false;

    /**
     *
     * Description: Basic JSchSession constructor
     *
     * @param subnet
     *            - subnet description
     *
     * @param strictHostKey
     *            - whether to use strict host key checking or not
     *
     * @throws JSchException
     *
     * @throws Exception
     */
    public JSchSession(SubnetDescription subnet, boolean strictHostKey,
            char[] password, String sshKey) throws JSchException {
        super();
        this.subnet = subnet;
        this.strictHostKey = strictHostKey;
        this.sshKey = sshKey;
        createSession(password);
    }

    public boolean isSessionConnected() {
        return connected;
    }

    /**
     * @return the SshSession
     */
    public SshSession getSshSession() {
        if (session != null) {
            return new SshSession(session);
        } else {
            return null;
        }
    }

    /**
     *
     * <i>Description: Create the JSch session</i>
     *
     * @throws JSchException
     * @throws Exception
     */
    protected void createSession(char[] password) throws JSchException {
        HostInfo hostInfo = subnet.getCurrentFE();
        String host = hostInfo.getHost();
        String userName = hostInfo.getSshUserName();
        int port = hostInfo.getSshPortNum();

        // Create the session and connect
        JSch jsch = Utils.createJSch();
        session = jsch.getSession(userName, host, port);
        session.setPassword(new String(password));
        Properties config = new java.util.Properties();
        config.put("StrictHostKeyChecking", (strictHostKey) ? "yes" : "no");
        session.setConfig(config);
        session.connect();
        connected = session.isConnected();

        if (DEBUG_SESSION) {
            if (connected) {
                startConnectionThread();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.network.IJSchSession#getShellChannel()
     */
    @Override
    public ChannelShell getShellChannel() throws JSchException {

        ChannelShell channel = null;

        if (session.isConnected()) {
            channel = (ChannelShell) session
                    .openChannel(JSchChannelType.SHELL_CHANNEL.getValue());
        } else {
            throw new JSchException(STLMessages.STL61019_SSH_CONNECTION_FAILURE
                    .getDescription());
        }

        return channel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.network.IJSchSession#getExecChannel()
     */
    @Override
    public ChannelExec getExecChannel() throws JSchException {

        ChannelExec channel = null;

        if (session.isConnected()) {
            channel = (ChannelExec) session
                    .openChannel(JSchChannelType.EXEC_CHANNEL.getValue());
        } else {
            throw new JSchException(STLMessages.STL61019_SSH_CONNECTION_FAILURE
                    .getDescription());
        }

        return channel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.network.IJSchSession#getSFtpChannel()
     */
    @Override
    public ChannelSftp getSFtpChannel() throws JSchException {

        ChannelSftp channel = null;

        if (session.isConnected()) {
            channel = (ChannelSftp) session
                    .openChannel(JSchChannelType.SFTP_CHANNEL.getValue());
        } else {
            throw new JSchException(STLMessages.STL61019_SSH_CONNECTION_FAILURE
                    .getDescription());
        }

        return channel;
    }

    // TODO Remove this!
    public void disconnect() {
        session.disconnect();
    }

    public String getSshKeyType() {
        return sshKey;
    }

    /**************************************************************************
     * Threads: connectionThread
     **************************************************************************/
    /**
     *
     * <i>Description: Stop the connection thread</i>
     *
     */
    public synchronized void stopConnectionThread() {
        connectionThreadRunning = false;
    }

    /**
     *
     * <i>Description: Start the connection thread to send a periodic keep alive
     * message to the server. If it fails restart the session. </i>
     *
     */
    public synchronized void startConnectionThread() {
        connectionThread = new Thread(new Runnable() {

            @Override
            public void run() {

                while (connectionThreadRunning) {
                    try {
                        // Try to send a keep alive message
                        session.sendKeepAliveMsg();
                        System.out.println("JSchSession: connected="
                                + session.isConnected());
                    } catch (Exception e) {
                        // TODO If the keep alive fails, restart the session
                        // notify the UI and stop the connection thread
                    }

                    // Delay before trying again
                    try {
                        Thread.sleep(session.getServerAliveInterval() / 2);
                    } catch (InterruptedException e) {
                    }
                }
            }
        });
        connectionThread.setName(CONNECTION_THREAD_NAME);
        connectionThreadRunning = true;
        connectionThread.start();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.network.IJSchSession#isConnected()
     */
    @Override
    public boolean isConnected() {
        return session.isConnected();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.network.IJSchSession#shutdown()
     */
    @Override
    public void shutdown() {
        session.disconnect();
        stopConnectionThread();
    }
}
