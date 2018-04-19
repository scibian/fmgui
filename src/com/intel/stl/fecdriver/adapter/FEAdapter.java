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

import static com.intel.stl.common.STLMessages.STL10025_STARTING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10026_STOPPING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10027_FE_ADAPTER;
import static com.intel.stl.configuration.AppSettings.APP_FAILOVER_TIMEOUT;
import static com.intel.stl.configuration.AppSettings.APP_NET_DEBUG;

import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLHandshakeException;
import javax.net.ssl.TrustManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.ISecurityHandler;
import com.intel.stl.api.StartupProgressObserver;
import com.intel.stl.api.Utils;
import com.intel.stl.api.failure.BaseFailureEvaluator;
import com.intel.stl.api.failure.FailureManager;
import com.intel.stl.api.failure.IFailureEvaluator;
import com.intel.stl.api.failure.IFailureManagement;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.configuration.AppComponent;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.configuration.AppSettings;
import com.intel.stl.configuration.AsyncTask;
import com.intel.stl.configuration.ResultHandler;
import com.intel.stl.configuration.SerialProcessingService;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.IFailoverHelper;
import com.intel.stl.fecdriver.IFailoverManager;
import com.intel.stl.fecdriver.dispatcher.IConnectionEventListener;
import com.intel.stl.fecdriver.dispatcher.IRequestDispatcher;
import com.intel.stl.fecdriver.dispatcher.ITempRequestDispatcher;
import com.intel.stl.fecdriver.dispatcher.SMFailoverManager;
import com.intel.stl.fecdriver.dispatcher.SubnetRequestDispatcher;
import com.intel.stl.fecdriver.dispatcher.TempRequestDispatcher;
import com.intel.stl.fecdriver.session.ISession;

public class FEAdapter implements AppComponent, IAdapter, IFailoverHelper {
    private static Logger log = LoggerFactory.getLogger(FEAdapter.class);

    private static final String ADAPTER_COMPONENT =
            STL10027_FE_ADAPTER.getDescription();

    private static final String PROGRESS_MESSAGE =
            STL10025_STARTING_COMPONENT.getDescription(ADAPTER_COMPONENT);

    private static final String SHUTDOWN_MESSAGE =
            STL10026_STOPPING_COMPONENT.getDescription(ADAPTER_COMPONENT);

    private final ConcurrentHashMap<SubnetDescription, IRequestDispatcher> cachedDispatchers =
            new ConcurrentHashMap<SubnetDescription, IRequestDispatcher>();

    // Default failover timeout
    public static String SM_FAILOVER_TIMEOUT = "120"; // 120 seconds

    private long failoverTimeout = 120000;

    private final Map<String, KeyManagerFactory> cachedKeyFactories =
            new HashMap<String, KeyManagerFactory>();

    private final Map<String, TrustManagerFactory> cachedTrustFactories =
            new HashMap<String, TrustManagerFactory>();

    private final AtomicReference<ITempRequestDispatcher> tempreqDispatcher =
            new AtomicReference<ITempRequestDispatcher>();

    private final IFailureManagement failureMgr;

    private final BaseFailureEvaluator failureEvaluator;

    private final DatabaseManager dbMgr;

    private final SerialProcessingService procService;

    public FEAdapter(DatabaseManager dbMgr,
            SerialProcessingService procService) {
        this.dbMgr = dbMgr;
        this.procService = procService;
        failureEvaluator = new BaseFailureEvaluator();
        failureEvaluator.setRecoverableErrors(IOException.class,
                RuntimeException.class);
        failureEvaluator.setUnrecoverableErrors(ClosedChannelException.class,
                SSLHandshakeException.class);
        failureMgr = FailureManager.getManager();
    }

    @Override
    public void initialize(AppSettings settings,
            StartupProgressObserver observer) throws AppConfigurationException {
        if (observer != null) {
            observer.setProgress(PROGRESS_MESSAGE);
        }
        String foTimeoutStr = settings.getConfigOption(APP_FAILOVER_TIMEOUT,
                SM_FAILOVER_TIMEOUT);
        try {
            this.failoverTimeout = (Integer.parseInt(foTimeoutStr)) * 1000;
        } catch (NumberFormatException e) {
            log.error(
                    "Failover timeout value '{}' is invalid; defaulting to: {} seconds",
                    foTimeoutStr, SM_FAILOVER_TIMEOUT);
            this.failoverTimeout = Integer.parseInt(SM_FAILOVER_TIMEOUT) * 1000;
        }
        String debug = settings.getConfigOption(APP_NET_DEBUG, null);
        if (debug != null) {
            System.setProperty("javax.net.debug", debug);
            log.info("FEAdapter network debug set to '" + debug + "'");
        }
        log.info("FE Adapter initialization finished");
    }

    @Override
    public void registerCertsAssistant(ICertsAssistant assistant) {
    }

    @Override
    public void registerSecurityHandler(ISecurityHandler securityHandler) {
    }

    @Override
    public SSLEngine getSSLEngine(HostInfo hostInfo) throws Exception {
        String host = hostInfo.getHost();
        int port = hostInfo.getPort();
        CertsDescription certs = hostInfo.getCertsDescription();
        String keyFile = certs.getKeyStoreFile();
        String trustFile = certs.getTrustStoreFile();
        KeyManagerFactory kmf = cachedKeyFactories.get(keyFile);
        TrustManagerFactory tmf = cachedTrustFactories.get(trustFile);
        if (kmf != null && tmf != null) {
            return Utils.createSSLEngine(host, port, kmf, tmf);
        }
        return null;
    }

    @Override
    public SSLEngine getSSLEngine(HostInfo hostInfo, CertsDescription certs)
            throws Exception {
        String host = hostInfo.getHost();
        int port = hostInfo.getPort();
        String keyFile = certs.getKeyStoreFile();
        KeyManagerFactory kmf =
                Utils.createKeyManagerFactory(keyFile, certs.getKeyStorePwd());
        cachedKeyFactories.put(keyFile, kmf);
        String trustFile = certs.getTrustStoreFile();
        TrustManagerFactory tmf = Utils.createTrustManagerFactory(trustFile,
                certs.getTrustStorePwd());
        cachedTrustFactories.put(trustFile, tmf);
        return Utils.createSSLEngine(host, port, kmf, tmf);
    }

    @Override
    public ISession createSession(SubnetDescription subnet) throws IOException {
        return createSession(subnet, null);
    }

    @Override
    public ISession createSession(SubnetDescription subnet,
            ISMEventListener listener) throws IOException {
        IRequestDispatcher dispatcher = cachedDispatchers.get(subnet);
        if (dispatcher == null) {
            SubnetRequestDispatcher tmpDispatcher =
                    new SubnetRequestDispatcher(subnet, this, dbMgr);
            dispatcher = cachedDispatchers.putIfAbsent(subnet, tmpDispatcher);
            if (dispatcher == null) {
                dispatcher = tmpDispatcher;
                log.info("Starting SubnetRequestDispatcher for subnet {}...",
                        subnet.getName());
                dispatcher.start();
            }
        }
        return dispatcher.createSession(listener);
    }

    @Override
    public ISession createTemporarySession(HostInfo host,
            IConnectionEventListener listener) throws IOException {
        ITempRequestDispatcher tmpDispatcher = tempreqDispatcher.get();
        if (tmpDispatcher == null) {
            tmpDispatcher = new TempRequestDispatcher(this);
            if (tempreqDispatcher.compareAndSet(null, tmpDispatcher)) {
                log.info("Starting TemporaryRequestDispatcher...");
                tmpDispatcher.start();
            } else {
                tmpDispatcher = tempreqDispatcher.get();
            }
        }
        return tmpDispatcher.createTemporarySession(host, listener);
    }

    @Override
    public int getNumInitialConnections() {
        return SubnetRequestDispatcher.MIN_CONN_POOL_SIZE;
    }

    @Override
    public long getFailoverTimeout() {
        return failoverTimeout;
    }

    @Override
    public <R> void submitTask(AsyncTask<R> task, ResultHandler<R> handler) {
        procService.submit(task, handler);
    }

    @Override
    public void shutdownSubnet(SubnetDescription subnet) {
        IRequestDispatcher cachedDispatcher = cachedDispatchers.get(subnet);
        if (cachedDispatcher != null) {
            log.info("Shutting down SubnetRequestDispatcher for subnet {}.",
                    subnet.getName());
            cachedDispatchers.remove(subnet);
            cachedDispatcher.shutdown();
        }
    }

    @Override
    public void shutdown(StartupProgressObserver observer) {
        if (observer != null) {
            observer.setProgress(SHUTDOWN_MESSAGE);
        }
        try {
            ITempRequestDispatcher tmpDispatcher = tempreqDispatcher.get();
            if (tmpDispatcher != null) {
                tmpDispatcher.shutdown();
            }
        } catch (Exception e) {
            log.warn("Exception shutting temporary dispatcher.", e);
        } finally {
            for (SubnetDescription subnet : cachedDispatchers.keySet()) {
                try {
                    IRequestDispatcher dispatcher =
                            cachedDispatchers.get(subnet);
                    dispatcher.shutdown();
                } catch (Exception e) {
                    log.warn("Exception shutting down subnet {}",
                            subnet.getName(), e);
                }
            }
        }
    }

    @Override
    public SocketChannel createChannel() {
        try {
            return SocketChannel.open();
        } catch (IOException e) {
            log.error("Error creating channel", e);
            return null;
        }
    }

    @Override
    public IFailoverManager getFailoverManager() {
        return new SMFailoverManager(this);
    }

    @Override
    public IFailureManagement getFailureManager() {
        return failureMgr;
    }

    @Override
    public IFailureEvaluator getFailureEvaluator() {
        return failureEvaluator;
    }

    @Override
    public String getComponentDescription() {
        return ADAPTER_COMPONENT;
    }

    @Override
    public int getInitializationWeight() {
        return 10;
    }

    @Override
    public void refreshSubnetDescription(SubnetDescription subnet) {
        IRequestDispatcher cachedDispatcher = cachedDispatchers.get(subnet);
        if (cachedDispatcher != null) {
            cachedDispatcher.refreshSubnetDescription(subnet);
        }
    }

    @Override
    public void startSimulatedFailover(String subnetName) {
        // TODO Auto-generated method stub

    }

}
