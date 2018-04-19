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

package com.intel.stl.api.configuration.impl;

import static com.intel.stl.common.STLMessages.STL10020_APPCONTEXT_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10025_STARTING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10026_STOPPING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL30022_SUBNET_NOT_FOUND;
import static com.intel.stl.common.STLMessages.STL50008_SUBNET_CONNECTION_ERROR;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.AppContext;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.ISecurityHandler;
import com.intel.stl.api.StartupProgressObserver;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.SubnetException;
import com.intel.stl.configuration.AppComponent;
import com.intel.stl.configuration.AppComponentRegistry;
import com.intel.stl.configuration.AppConfig;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.configuration.AppSettings;
import com.intel.stl.configuration.AsyncProcessingService;
import com.intel.stl.configuration.SerialProcessingService;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.adapter.IAdapter;
import com.intel.stl.fecdriver.adapter.ISMEventListener;
import com.intel.stl.fecdriver.session.ISession;

public class AppContextImpl implements AppComponent, AppContext {
    private static Logger log = LoggerFactory.getLogger(AppContextImpl.class);

    private static final String APPCONTEXT_COMPONENT =
            STL10020_APPCONTEXT_COMPONENT.getDescription();

    private static final String PROGRESS_MESSAGE =
            STL10025_STARTING_COMPONENT.getDescription(APPCONTEXT_COMPONENT);

    private static final String SHUTDOWN_MESSAGE =
            STL10026_STOPPING_COMPONENT.getDescription(APPCONTEXT_COMPONENT);

    private final IAdapter adapter;

    private final DatabaseManager dbMgr;

    private final MailManager mailMgr;

    private final SerialProcessingService processingService;

    private final Map<String, SubnetDescription> subnetsBySubnetName =
            new HashMap<String, SubnetDescription>();

    private final Map<SubnetDescription, SubnetContext> subnetContexts =
            new HashMap<SubnetDescription, SubnetContext>();

    private IConfigurationApi confApi;

    private AppSettings settings;

    private final AtomicInteger threadCount = new AtomicInteger(1);

    public AppContextImpl(IAdapter adapter, DatabaseManager dbMgr,
            MailManager mailMgr, AsyncProcessingService processingService) {
        this.adapter = adapter;
        this.dbMgr = dbMgr;
        this.mailMgr = mailMgr;
        this.processingService = processingService;
    }

    public MailManager getMailMgr() {
        return mailMgr;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.AppContext#regsiterCertsAssistant(com.intel.stl.api
     * .ICertsAssistant)
     */
    @Override
    public void registerCertsAssistant(ICertsAssistant assistant) {
        adapter.registerCertsAssistant(assistant);
    }

    @Override
    public void registerSecurityHandler(ISecurityHandler securityHandler) {
        adapter.registerSecurityHandler(securityHandler);
    }

    @Override
    public void initialize(AppSettings settings,
            StartupProgressObserver observer) throws AppConfigurationException {
        if (observer != null) {
            observer.setProgress(PROGRESS_MESSAGE);
        }
        this.settings = settings;
        this.confApi = new ConfigurationApi(adapter, dbMgr, mailMgr, settings);
    }

    @Override
    public String getComponentDescription() {
        return APPCONTEXT_COMPONENT;
    }

    @Override
    public int getInitializationWeight() {
        return 15;
    }

    @Override
    public List<SubnetDescription> getSubnets() {
        return dbMgr.getSubnets();
    }

    @Override
    public SubnetContext getSubnetContextFor(SubnetDescription subnet) {
        return getSubnetContextFor(subnet, false);
    }

    @Override
    public SubnetContext getSubnetContextFor(SubnetDescription subnet,
            boolean startBackgroundTasks) {
        // We use a different SubnetDescription object than the UI just to make
        // sure it's not changed while being used by the backend
        SubnetDescription dbSubnet = getSubnet(subnet.getName());
        dbSubnet.setContent(subnet);
        boolean subnetContextCreated = isSubnetContextCreated(dbSubnet);
        if (!subnetContextCreated) {
            addSubnetContext(dbSubnet, startBackgroundTasks);
        }
        SubnetContext subnetContext = subnetContexts.get(dbSubnet);
        return subnetContext;
    }

    @Override
    public String getAppSetting(String settingName, String defaultValue) {
        if (settings != null) {
            return settings.getConfigOption(settingName, defaultValue);
        }
        return defaultValue;
    }

    private boolean isSubnetContextCreated(SubnetDescription subnet) {
        SubnetContext context = subnetContexts.get(subnet);
        return (context != null) && (context.isValid() && !context.isClosed());
    }

    private void addSubnetContext(SubnetDescription subnet,
            boolean startBackgroundTasks) {
        SubnetContext context = subnetContexts.get(subnet);
        if (context == null || !context.isValid() || context.isClosed()) {
            log.info("Creating SubnetContext for subnet " + subnet.getName()
                    + " with startBackgroundTasks=" + startBackgroundTasks);
            createSubnetContext(subnet, startBackgroundTasks);
        }
    }

    protected synchronized void createSubnetContext(SubnetDescription subnet,
            boolean startBackgroundTasks) {
        SubnetContext context = subnetContexts.get(subnet);
        if (context == null || !context.isValid() || context.isClosed()) {
            context = new SubnetContextImpl(subnet, this);
            SubnetContext oldContext = subnetContexts.put(subnet, context);
            if (oldContext != null) {
                oldContext.cleanup();
            }
        }
    }

    @Override
    public IConfigurationApi getConfigurationApi() {
        return confApi;
    }

    @Override
    public void shutdown(StartupProgressObserver observer) {
        if (observer != null) {
            observer.setProgress(SHUTDOWN_MESSAGE);
        }
        processingService.shutdown();
        for (SubnetContext subnetCtx : subnetContexts.values()) {
            try {
                subnetCtx.cleanup();
            } catch (Exception e) {
                log.error("Error shutting down SubnetContext", e);
            }
        }
    }

    @Override
    public void shutdown() {
        log.info("Starting application shutdown");
        AppComponentRegistry registry = AppConfig.getAppComponentRegistry();
        try {
            registry.shutdown();
        } finally {
            log.info("Application component shutdown complete");
            System.gc();
            System.exit(0);
        }
    }

    public DatabaseManager getDatabaseManager() {
        return dbMgr;
    }

    public SerialProcessingService getProcessingService() {
        return processingService;
    }

    public int getNoticeManagerThreadCount() {
        return threadCount.getAndIncrement();
    }

    protected SubnetDescription getSubnet(String subnetName)
            throws SubnetException {
        SubnetDescription subnet = subnetsBySubnetName.get(subnetName);
        if (subnet == null) {
            subnet = insertNewSubnet(subnetName);
        }
        return subnet;
    }

    protected synchronized SubnetDescription insertNewSubnet(
            String subnetName) {
        SubnetDescription subnet = dbMgr.getSubnet(subnetName);
        if (subnet == null) {
            ConfigurationException ce = new ConfigurationException(
                    STL30022_SUBNET_NOT_FOUND, subnetName);
            throw ce;
        }
        long subnetId = subnet.getSubnetId();
        Iterator<SubnetDescription> it =
                subnetsBySubnetName.values().iterator();
        while (it.hasNext()) {
            if (it.next().getSubnetId() == subnetId) {
                // Remove any old subnetDescription
                it.remove();
            }
        }
        subnetsBySubnetName.put(subnetName, subnet);
        return subnet;
    }

    protected ISession createSession(SubnetDescription subnet,
            ISMEventListener listener) {
        try {
            ISession session = adapter.createSession(subnet, listener);
            if (session != null) {
                session.getSubnetDescription().setName(subnet.getName());
            }
            return session;
        } catch (Exception e) {
            ConfigurationException ce = new ConfigurationException(
                    STL50008_SUBNET_CONNECTION_ERROR, e, subnet.getName(),
                    StringUtils.getErrorMessage(e));
            log.error(StringUtils.getErrorMessage(e), e);
            throw ce;
        }
    }

    // For testing

    /**
     * @return the subnetContexts
     */
    public Map<SubnetDescription, SubnetContext> getSubnetContexts() {
        return subnetContexts;
    }

}
