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

package com.intel.stl.fecdriver.adapter;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.ISecurityHandler;
import com.intel.stl.api.failure.IFailureEvaluator;
import com.intel.stl.api.failure.IFailureManagement;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.configuration.AsyncTask;
import com.intel.stl.configuration.ResultHandler;
import com.intel.stl.fecdriver.IFailoverManager;
import com.intel.stl.fecdriver.dispatcher.IConnectionEventListener;
import com.intel.stl.fecdriver.session.ISession;

public interface IAdapter {

    /**
     * 
     * <i>Description:</i> registers a certificate assistant with this adapter
     * 
     * @param assistant
     *            the certificate assistant
     */
    void registerCertsAssistant(ICertsAssistant assistant);

    /**
     * 
     * <i>Description:</i> registers a security assistant with this adapter
     * 
     * @param securityHandler
     */
    void registerSecurityHandler(ISecurityHandler securityHandler);

    /**
     * 
     * <i>Description:</i> returns a SSLEngine for a remote host
     * 
     * @param hostInfo
     *            host information for the remote host
     * @return a SSLEngine
     * @throws Exception
     */
    SSLEngine getSSLEngine(HostInfo hostInfo) throws Exception;

    SSLEngine getSSLEngine(HostInfo hostInfo, CertsDescription certs)
            throws Exception;

    /**
     * 
     * <i>Description:</i> creates a SocketChannel
     * 
     * @return
     */
    SocketChannel createChannel();

    /**
     * 
     * <i>Description:</i> creates a session with a subnet
     * 
     * @param subnet
     *            the target subnet for the session
     * @return a session
     * @throws IOException
     */
    ISession createSession(SubnetDescription subnet) throws IOException;

    /**
     * 
     * <i>Description:</i> creates a session with a subnet, specifying a Subnet
     * Manager event listener
     * 
     * @param subnet
     *            the target subnet for the session
     * @param listener
     *            the Subnet Manager event listener
     * @return a session
     * @throws IOException
     */
    ISession createSession(SubnetDescription subnet, ISMEventListener listener)
            throws IOException;

    /**
     * 
     * <i>Description:</i> creates a temporary session with a remote host. A
     * temporary session can be used to send fabric commands but it has no error
     * handling or failover processing, the invoker must implement it thru the
     * connection event listener provided.
     * 
     * @param host
     *            the target host
     * @param listener
     *            an connection event listener
     * @return a session
     * @throws IOException
     */
    ISession createTemporarySession(HostInfo host,
            IConnectionEventListener listener) throws IOException;

    /**
     * 
     * <i>Description:</i> returns a failover manager
     * 
     * @return a failover manager
     */
    IFailoverManager getFailoverManager();

    /**
     * 
     * <i>Description:</i> returns a failure manager to be invoked at request
     * errors
     * 
     * @return a failure mananger
     */
    IFailureManagement getFailureManager();

    /**
     * 
     * <i>Description:</i> returns a failure evaluator
     * 
     * @return
     */
    IFailureEvaluator getFailureEvaluator();

    /**
     * 
     * <i>Description:</i> submits an asynchronous task for processing in the
     * background
     * 
     * @param asyncTask
     * @param resultHandler
     */
    <R> void submitTask(AsyncTask<R> asyncTask, ResultHandler<R> resultHandler);

    /**
     * 
     * <i>Description:</i> shuts down the specified subnet. No requests are
     * further processed for the subnet
     * 
     * @param subnet
     */
    void shutdownSubnet(SubnetDescription subnet);

    /**
     * <i>Description:</i>
     * 
     * @param subnet
     */
    void refreshSubnetDescription(SubnetDescription subnet);

    /**
     * <i>Description:</i>
     * 
     * @param subnetName
     */
    void startSimulatedFailover(String subnetName);

}
