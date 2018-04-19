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

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.STLMessages;
import com.jcraft.jsch.JSchException;

/**
 * The JSchSessionFactory maintains a map of JSchSession objects based on a
 * subnet key to provide a single connection to the remote SSH server for any
 * object that needs a channel to it.
 */
public class JSchSessionFactory {

    @SuppressWarnings("unused")
    private final static Logger log = LoggerFactory
            .getLogger(JSchSessionFactory.class);

    private final static Map<String, JSchSession> sessionMap =
            new HashMap<String, JSchSession>();

    public synchronized static JSchSession getSession(SubnetDescription subnet,
            boolean strictHostKey, char[] password, String sshKey)
            throws JSchException {

        // Check if there is a session in the map
        JSchSession jschSession = sessionMap.get(sshKey);

        // If there is but it's disconnected, remove it from the map
        if ((jschSession != null) && (!jschSession.isConnected())) {
            closeSession(sshKey);
            sessionMap.remove(sshKey);
            jschSession = null;
        }

        // If there is no session in the map, attempt to create one and put
        // it in the map if it connects
        if (jschSession == null) {
            jschSession =
                    createSession(subnet, strictHostKey, password, sshKey);
        }

        return jschSession;
    }

    public static JSchSession getSessionFromMap(String sshKey) {
        return sessionMap.get(sshKey);
    }

    protected static JSchSession createSession(SubnetDescription subnet,
            boolean strictHostKey, char[] password, String sshKey)
            throws JSchException {

        JSchSession jschSession = null;

        try {
            jschSession =
                    new JSchSession(subnet, strictHostKey, password, sshKey);

            if ((jschSession != null) && jschSession.isConnected()) {
                sessionMap.put(sshKey, jschSession);
            } else {
                throw new JSchException(
                        STLMessages.STL50015_SESSION_CONNECTION_FAILURE
                                .getDescription());
            }
        } catch (JSchException e) {
            if (jschSession != null) {
                jschSession.shutdown();
            }

            throw e;
        }

        return jschSession;
    }

    public static void closeSession(String sshKey) {
        JSchSession jschSession = sessionMap.get(sshKey);

        if (jschSession != null) {
            try {
                jschSession.shutdown();
            } finally {
                sessionMap.remove(sshKey);
            }
        }
    }

    public static void cleanup() {
        for (JSchSession session : sessionMap.values()) {
            session.shutdown();
        }
    }
}
