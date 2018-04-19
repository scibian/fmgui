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

package com.intel.stl.ui.console;

import java.awt.Dimension;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;

import com.intel.stl.api.Utils;
import com.jcraft.jsch.ChannelShell;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.wittams.gritty.Questioner;

/**
 * This class implements the ITty interface and replaces the Gritty JSchTty
 * class to provide more control over the connection
 */
public class IntelTty implements ITty {
    private InputStream in = null;

    private OutputStream out = null;

    private Session session;

    private ChannelShell channel;

    private int port = 22;

    private String user = null;

    private String host = null;

    private char[] password = null;

    private Dimension pendingTermSize;

    private Dimension pendingPixelSize;

    private final IConsoleMsgListener messageListener;

    private boolean enableMsgListener = false;

    // Added this comment to correct PR 126675 comment above
    public IntelTty(LoginBean loginBean, IConsoleMsgListener messageListener)
            throws NumberFormatException {
        this.host = loginBean.getHostName();
        this.user = loginBean.getUserName();
        this.password = loginBean.getPassword();
        this.messageListener = messageListener;

        try {
            this.port = Integer.parseInt(loginBean.getPortNum());
        } catch (NumberFormatException e) {
            throw e;
        }
    }

    @Override
    public void resize(Dimension termSize, Dimension pixelSize) {
        pendingTermSize = termSize;
        pendingPixelSize = pixelSize;
        if (channel != null) {
            resizeImmediately();
        }
    }

    private void resizeImmediately() {
        if (pendingTermSize != null && pendingPixelSize != null) {
            channel.setPtySize(pendingTermSize.width, pendingTermSize.height,
                    pendingPixelSize.width, pendingPixelSize.height);
            pendingTermSize = null;
            pendingPixelSize = null;
        }
    }

    @Override
    public void close() {
        if (session != null) {
            session.disconnect();
            session = null;

            if (channel != null) {
                channel.disconnect();
                channel = null;
            }

            try {
                in.close();
                in = null;
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    out.close();
                    out = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#closeChannel()
     */
    @Override
    public void closeChannel() {
        if (channel != null) {
            channel.disconnect();
            channel = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#initialize()
     */
    @Override
    public boolean initialize() throws Exception {

        if ((session == null) || (!session.isConnected())) {
            session = connectSession();
        }

        channel = (ChannelShell) session.openChannel("shell");

        in = channel.getInputStream();
        out = channel.getOutputStream();
        channel.connect();
        resizeImmediately();

        return true;
    }

    private Session connectSession() throws JSchException {
        JSch.setLogger(new ConsoleLogger());

        JSch jsch = Utils.createJSch();
        Session session = jsch.getSession(user, host, port);

        final IntelUserInfo ui = new IntelUserInfo();
        if (password != null) {
            session.setPassword(new String(password));
            ui.setPassword(password);
        }
        session.setUserInfo(ui);

        final java.util.Properties config = new java.util.Properties();
        config.put("compression.s2c", "zlib,none");
        config.put("compression.c2s", "zlib,none");
        configureSession(session, config);
        session.setTimeout(5000);
        session.connect();
        session.setTimeout(0);

        return session;
    }

    protected void configureSession(Session session,
            final java.util.Properties config) {
        session.setConfig(config);
    }

    @Override
    public String getName() {
        return "ConnectRunnable";
    }

    @Override
    public int read(byte[] buf, int offset, int length) throws IOException {
        int res = in.read(buf, offset, length);

        // If enabled, pass the resulting data to the message listener
        if (enableMsgListener) {
            messageListener.storeCmdResult(in.available(), res, buf);
        }

        if (res < 0) {
            // the stream is closed, throw the InterruptedIOException to tell
            // the channel the terminal is exited.
            throw new InterruptedIOException();
        }
        return res;
    }

    @Override
    public void write(byte[] bytes) throws IOException {

        if (out != null) {
            out.write(bytes);
            out.flush();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#isConnected()
     */
    @Override
    public boolean isConnected() {

        boolean isConnected = false;

        if (session != null) {
            isConnected = session.isConnected();
        }

        return isConnected;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.wittams.gritty.Tty#init(com.wittams.gritty.Questioner)
     */
    @Override
    public boolean init(Questioner q) {
        // TODO Auto-generated method stub
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#getSession()
     */
    @Override
    public Session getSession() {
        return session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#setSession(com.jcraft.jsch.Session)
     */
    @Override
    public void setSession(Session session) {

        this.session = session;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#isEnableMsgListener()
     */
    @Override
    public boolean isEnableMsgListener() {
        return enableMsgListener;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITty#enableMsgListener(boolean)
     */
    @Override
    public void enableMsgListener(boolean enableMsgListener) {
        this.enableMsgListener = enableMsgListener;

    }
}
