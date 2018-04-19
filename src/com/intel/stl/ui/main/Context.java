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

package com.intel.stl.ui.main;

import java.awt.Component;
import java.beans.PropertyChangeListener;
import java.security.UnrecoverableKeyException;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.FMException;
import com.intel.stl.api.FMKeyStoreException;
import com.intel.stl.api.FMTrustStoreException;
import com.intel.stl.api.IConnectionAssistant;
import com.intel.stl.api.ISubnetEventListener;
import com.intel.stl.api.SSLStoreCredentialsDeniedException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.SubnetEvent;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.logs.ILogApi;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.notice.INoticeApi;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.SubnetDescription.Status;
import com.intel.stl.ui.alert.NoticeEventListener;
import com.intel.stl.ui.alert.NoticeNotifier;
import com.intel.stl.ui.alert.NotifierFactory;
import com.intel.stl.ui.alert.NotifierType;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.UserPreference;
import com.intel.stl.ui.publisher.EventCalculator;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.UserSettingsProcessor;

/**
 */
public class Context implements ISubnetEventListener, IConnectionAssistant {
    private static final Logger log = LoggerFactory.getLogger(Context.class);

    public static final long TIME_OUT = 30000; // 30 sec

    private TaskScheduler scheduler;

    private EventCalculator evtCal;

    private UserSettingsProcessor userSettingsProcessor;

    private List<Throwable> errors;

    private final SubnetContext subnetContext;

    private final IFabricController controller;

    private final String userName;

    private UserPreference userPreference;

    private final Component owner;

    private final NoticeEventListener noticeListener;

    private NoticeNotifier emailNotifier;

    public Context(SubnetContext subnetContext, IFabricController controller,
            String userName) {
        this.subnetContext = subnetContext;
        this.userName = userName;
        this.controller = controller;
        this.owner = controller.getViewFrame();
        this.noticeListener = new NoticeEventListener(controller.getEventBus());
        this.subnetContext.addSubnetEventListener(this);
        this.subnetContext.getSubnetDescription().setConnectionAssistant(this);
    }

    /**
     * @return the owner
     */
    public Component getOwner() {
        return owner;
    }

    /**
     * @return the controller
     */
    public IFabricController getController() {
        return controller;
    }

    public EventCalculator getEvtCal() {
        return evtCal;
    }

    public void initialize() throws SubnetConnectionException {
        subnetContext.initialize();

        getNoticeApi().addEventListener(noticeListener);
        refreshUserSettings();
        UserSettings userSettings = getUserSettings();
        userPreference = new UserPreference(userSettings);

        EnumMap<NodeType, Integer> nodes = null;
        try {
            nodes = subnetContext.getSubnetApi().getNodesTypeDist(false, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        evtCal = new EventCalculator(nodes, userPreference);
        this.subnetContext.getNoticeApi().addEventListener(evtCal);
        userSettingsProcessor = new UserSettingsProcessor(userSettings, this);

        PMConfigBean pmConf = getPerformanceApi().getPMConfig();
        int refreshRate = userPreference.getRefreshRateInSeconds();
        if (pmConf != null) {
            int sweepInterval = pmConf.getSweepInterval();
            if (refreshRate > sweepInterval) {
                this.scheduler = new TaskScheduler(this, refreshRate);
            } else {
                this.scheduler = new TaskScheduler(this, sweepInterval);
            }
        } else {
            this.scheduler = new TaskScheduler(this, refreshRate);
        }

        this.emailNotifier =
                NotifierFactory.createNotifier(NotifierType.MAIL, this);
        // AppInfo appInfo = this.getConfigurationApi().getAppInfo();
        this.subnetContext.getNoticeApi().addEventListener(emailNotifier);
    }

    public NoticeNotifier getEmailNotifier() {
        return emailNotifier;
    }

    public IConfigurationApi getConfigurationApi() {
        return subnetContext.getConfigurationApi();
    }

    public ISubnetApi getSubnetApi() {
        return subnetContext.getSubnetApi();
    }

    public IPerformanceApi getPerformanceApi() {
        return subnetContext.getPerformanceApi();
    }

    public INoticeApi getNoticeApi() {
        return subnetContext.getNoticeApi();
    }

    public ILogApi getLogApi() {
        return subnetContext.getLogApi();
    }

    public IManagementApi getManagementApi() {
        return subnetContext.getManagementApi();
    }

    public UserSettings getUserSettings() {
        UserSettings userSettings = null;
        try {
            userSettings = subnetContext.getUserSettings(userName);
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
        return userSettings;
    }

    public void refreshUserSettings() {
        try {
            subnetContext.refreshUserSettings(userName);

            if (userSettingsProcessor != null) {
                UserSettings userSettings = getUserSettings();
                userSettingsProcessor.process(userSettings);
            }
        } catch (UserNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void setRandom(boolean random) {
        subnetContext.setRandom(random);
    }

    public void addSubnetEventListener(ISubnetEventListener listener) {
        subnetContext.addSubnetEventListener(listener);
    }

    public void removeSubnetEventListener(ISubnetEventListener listener) {
        subnetContext.removeSubnetEventListener(listener);
    }

    public void addFailoverProgressListener(PropertyChangeListener listener) {
        subnetContext.addFailoverProgressListener(listener);
    }

    public void removeFailoverProgressListener(
            PropertyChangeListener listener) {
        subnetContext.removeFailoverProgressListener(listener);
    }

    public void setDeleted(boolean deleted) {
        subnetContext.setDeleted(deleted);
    }

    /**
     * @return the apiBroker
     */
    public TaskScheduler getTaskScheduler() {
        return scheduler;
    }

    public SubnetDescription getSubnetDescription() {
        return subnetContext.getSubnetDescription();
    }

    public void close() {

        final Status status =
                subnetContext.isValid() ? Status.VALID : Status.INVALID;
        final IConfigurationApi confApi = subnetContext.getConfigurationApi();
        SubnetDescription subnet = subnetContext.getSubnetDescription();
        final long subnetId = subnet.getSubnetId();

        // Saving the subnet description to the database is done on the EDT
        // to prevent collision with deletion of the subnet while it is running
        // in a window. The EDT will serialize these two operations so there is
        // no resource contention.
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                try {
                    SubnetDescription subnetDesc = confApi.getSubnet(subnetId);
                    subnetDesc.setLastStatus(status);

                    SubnetDescription currentSubnet =
                            subnetContext.getSubnetDescription();
                    subnetDesc.getCurrentFE().setSshUserName(
                            currentSubnet.getCurrentFE().getSshUserName());
                    subnetDesc.getCurrentFE().setSshPortNum(
                            currentSubnet.getCurrentFE().getSshPortNum());
                    confApi.updateSubnet(subnetDesc);
                } catch (SubnetDataNotFoundException e) {
                    log.error(e.getMessage(), e);
                } catch (ConfigurationException e) {
                }
            }
        });
        cleanup();
    }

    public void cleanup() {
        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
            if (noticeListener != null) {
                noticeListener.shutdown();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            subnetContext.cleanup();
        }
    }

    public boolean isValid() {
        return subnetContext.isValid();
    }

    @Override
    public void onFailoverCompleted(SubnetEvent event) {
        // This method is intentionally left empty.
        // FabricController.onFailoverCompleted will handle the following
        // 1) reschedule of tasks after fail-over.
        // 2) reset ManagementApi so we will reload opafm.xml
        // 3) refresh UI to update to the latest FM state
    }

    @Override
    public void onFailoverFailed(SubnetEvent event) {
        log.debug("Context is cleaning up after failover failed");
        cleanup();
    }

    @Override
    public void onSubnetManagerConnectionLost(SubnetEvent event) {
        log.debug("Stopping the task scheduler");
        // Stop the TaskScheduler when a connection is lost to avoid more
        // requests until failover is completed
        try {
            scheduler.suspendServiceDuringFailover();
        } catch (Exception e) {
        }
    }

    public void cancelFailover() {
        subnetContext.cancelFailover();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.ISubnetEventListener#onSubnetManagerConnected(com.intel
     * .stl.api.SubnetEvent)
     */
    @Override
    public void onSubnetManagerConnected(SubnetEvent arg0) {
        // TODO Auto-generated method stub

    }

    @Override
    public CertsDescription getSSLStoreCredentials(HostInfo hostInfo)
            throws SSLStoreCredentialsDeniedException {

        CertsDescription certs = null;
        CertsLoginController loginCtr = controller.getCertsLoginController();

        if (errors != null) {
            // Parse if there is a certificate password error
            for (Throwable error : errors) {
                System.out.println("error=" + error);
                if (error instanceof FMKeyStoreException) {
                    Throwable cause = getPasswordException(error);
                    if (cause != null) {
                        loginCtr.setKeyStorePwdError(
                                StringUtils.getErrorMessage(cause));
                    } else {
                        loginCtr.setKeyStoreLocError(
                                StringUtils.getErrorMessage(error));
                    }
                } else if (error instanceof FMTrustStoreException) {
                    Throwable cause = getPasswordException(error);
                    if (cause != null) {
                        loginCtr.setTrustStorePwdError(
                                StringUtils.getErrorMessage(cause));
                    } else {
                        loginCtr.setTrustStoreLocError(
                                StringUtils.getErrorMessage(error));
                    }
                } else {
                    log.warn("Unsupported error", error);
                }
            }
        } else {
            errors = new ArrayList<Throwable>();
        }

        certs = loginCtr.getSSLCredentials(hostInfo);

        // Cleanup errors from this iteration
        if (errors != null) {
            errors.clear();
        }

        return certs;
    }

    @Override
    public void onSSLStoreError(FMException fmException) {
        errors.add(fmException);
    }

    public void reset() {
        subnetContext.reset();
    }

    private Throwable getPasswordException(Throwable e) {
        while (e.getCause() != null) {
            e = e.getCause();
        }
        if ((e instanceof UnrecoverableKeyException)
                && e.getMessage().contains("Password")) {
            return e;
        }
        return null;
    }
}
