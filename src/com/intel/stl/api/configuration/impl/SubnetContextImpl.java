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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.ISubnetEventListener;
import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.SubnetEvent;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.logs.ILogApi;
import com.intel.stl.api.logs.impl.LogApi;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.management.impl.ManagementApi;
import com.intel.stl.api.notice.INoticeApi;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.NoticeWrapper;
import com.intel.stl.api.notice.impl.NoticeApi;
import com.intel.stl.api.notice.impl.NoticeProcessTask;
import com.intel.stl.api.notice.impl.NoticeSimulator;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.impl.PerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.SMRecordBean;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.impl.SubnetApi;
import com.intel.stl.configuration.BaseCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.CacheManagerImpl;
import com.intel.stl.configuration.ResultHandler;
import com.intel.stl.configuration.SerialProcessingService;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.ApplicationEvent;
import com.intel.stl.fecdriver.adapter.ISMEventListener;
import com.intel.stl.fecdriver.session.ISession;

public class SubnetContextImpl implements SubnetContext, ISMEventListener {
    private static Logger log =
            LoggerFactory.getLogger(SubnetContextImpl.class);

    public static final String PROGRESS_AMOUNT_PROPERTY = "ProgressAmount";

    public static final String PROGRESS_NOTE_PROPERTY = "ProgressNote";

    private ISubnetApi subnetApi;

    private IPerformanceApi perfApi;

    private IManagementApi managementApi;

    private ILogApi logApi;

    private boolean initialized = false;

    private boolean valid = true;

    private boolean closed = false;

    private boolean deleted = false;

    private Throwable lastError;

    private UserSettings userSettings;

    private ISession session;

    private final List<ISubnetEventListener> subnetEventListeners =
            new CopyOnWriteArrayList<ISubnetEventListener>();

    private NoticeSimulator simulator;

    private Long randomSeed;

    private final AtomicBoolean topologyUpdateTaskStarted =
            new AtomicBoolean(false);

    private INoticeApi noticeApi;

    private final CacheManagerImpl cacheMgr;

    private final PropertyChangeSupport failoverProgress;

    private final AppContextImpl appContext;

    private final SubnetDescription subnet;

    public SubnetContextImpl(SubnetDescription subnet,
            AppContextImpl appContext) {
        this.subnet = subnet;
        this.appContext = appContext;
        this.failoverProgress = new PropertyChangeSupport(this);
        this.cacheMgr = new CacheManagerImpl(this);
    }

    @Override
    public IConfigurationApi getConfigurationApi() {
        return appContext.getConfigurationApi();
    }

    @Override
    public ISubnetApi getSubnetApi() {
        return subnetApi;
    }

    @Override
    public IPerformanceApi getPerformanceApi() {
        return perfApi;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#getNoticeApi()
     */
    @Override
    public INoticeApi getNoticeApi() {
        return noticeApi;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#getLoggerApi()
     */
    @Override
    public ILogApi getLogApi() {
        return logApi;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#getManagementApi()
     */
    @Override
    public IManagementApi getManagementApi() {
        return managementApi;
    }

    public SerialProcessingService getProcessingService() {
        return appContext.getProcessingService();
    }

    public DatabaseManager getDatabaseManager() {
        return appContext.getDatabaseManager();
    }

    public CacheManager getCacheManager() {
        return cacheMgr;
    }

    public ISession getSession() {
        return session;
    }

    @Override
    public String getAppSetting(String settingName, String defaultValue) {
        return appContext.getAppSetting(settingName, defaultValue);
    }

    /**
     * Refresh only if the user setting is null.
     */
    @Override
    public UserSettings getUserSettings(String userName)
            throws UserNotFoundException {
        if (userSettings == null) {
            refreshUserSettings(userName);
        }
        return userSettings;
    }

    @Override
    public void refreshUserSettings(String userName)
            throws UserNotFoundException {
        userSettings = getConfigurationApi().getUserSettings(subnet.getName(),
                userName);
        noticeApi.setUserSettings(userSettings);
    }

    @Override
    public void setRandom(boolean random) {
        if (random) {
            if (simulator == null) {
                simulator = new NoticeSimulator(cacheMgr);
                if (randomSeed != null) {
                    simulator.setSeed(randomSeed);
                }
            }
            simulator.addEventListener(this);
            simulator.run();
        } else {
            if (simulator != null) {
                simulator.removeEventListener(this);
                simulator.stop();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#getSubnetDescription()
     */
    @Override
    public SubnetDescription getSubnetDescription() {
        return subnet;
    }

    @Override
    public void addSubnetEventListener(ISubnetEventListener listener) {
        subnetEventListeners.add(listener);
    }

    @Override
    public void removeSubnetEventListener(ISubnetEventListener listener) {
        subnetEventListeners.remove(listener);
    }

    @Override
    public void addFailoverProgressListener(PropertyChangeListener listener) {
        failoverProgress.addPropertyChangeListener(listener);
    }

    @Override
    public void removeFailoverProgressListener(
            PropertyChangeListener listener) {
        failoverProgress.removePropertyChangeListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#cancelFailover()
     */
    @Override
    public void cancelFailover() {
        session.cancelFailover();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#cleanup()
     */
    @Override
    public void cleanup() {
        try {
            PropertyChangeListener[] listeners =
                    failoverProgress.getPropertyChangeListeners();
            for (PropertyChangeListener listener : listeners) {
                failoverProgress.removePropertyChangeListener(listener);
            }
            subnetEventListeners.clear();
            if (userSettings != null && !deleted) {
                try {
                    getConfigurationApi().saveUserSettings(subnet.getName(),
                            userSettings);
                } catch (Exception e) {
                    // Ignore any errors
                }
            }
            try {
                getConfigurationApi().cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                subnetApi.cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                perfApi.cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                logApi.cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                managementApi.cleanup();
            }

        } finally {
            try {
                if (simulator != null) {
                    simulator.stop();
                }
                noticeApi.cleanup();
            } finally {
                if (!closed) {
                    session.close();
                }
                this.closed = true;
                try {
                    cacheMgr.cleanup();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public synchronized void initialize() throws SubnetConnectionException {
        if (initialized) {
            return;
        }
        // Keep in mind that this is layered initialization, from bottom up:
        // - First the FE Adapter
        this.session = appContext.createSession(subnet, this);
        // - Then the caches (they need the session)
        cacheMgr.initialize();
        submitTopologyUpdateTaskIfNeeded();
        // - Now the APIs (they need the caches)
        this.subnetApi = new SubnetApi(this);
        this.perfApi = new PerformanceApi(this);
        this.noticeApi = new NoticeApi(this);
        this.managementApi = new ManagementApi(subnet);
        this.logApi = new LogApi(this);

        List<SMRecordBean> smList = subnetApi.getSMs();
        subnet.setSMList(smList);
        fireSubnetManagerConnectedEvent();
        initialized = true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#isValid()
     */
    @Override
    public boolean isValid() {
        return valid;
    }

    @Override
    public boolean isClosed() {
        return closed;
    }

    @Override
    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    @Override
    public boolean isDeleted() {
        return deleted;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.SubnetContext#reset()
     */
    @Override
    public void reset() {
        // clear extra cached data in subnetApi
        subnetApi.reset();
        // clear extra cached data in perfApi
        perfApi.reset();

        // clear all caches
        cacheMgr.reset();
        // re-init DB data
        cacheMgr.startTopologyUpdateTask();
    }

    @Override
    public void onNewEvent(NoticeBean[] notices) {
        for (NoticeBean notice : notices) {
            log.info("Get " + notice);
        }

        processNotices(notices);
    }

    @Override
    public void onFailoverStart(ApplicationEvent event) {
        SubnetEvent subnetEvent = new SubnetEvent(this);
        for (ISubnetEventListener listener : subnetEventListeners) {
            try {
                listener.onSubnetManagerConnectionLost(subnetEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onFailoverEnd(ApplicationEvent event) {
        Throwable cause = event.getReason();
        SubnetEvent subnetEvent = new SubnetEvent(event.getSource(), cause);
        if (cause == null) {
            for (ISubnetEventListener listener : subnetEventListeners) {
                try {
                    listener.onFailoverCompleted(subnetEvent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            fireSubnetManagerConnectedEvent();
        } else {
            valid = false;
            for (ISubnetEventListener listener : subnetEventListeners) {
                listener.onFailoverFailed(subnetEvent);
            }
        }
    }

    @Override
    public void onFailoverProgress(ApplicationEvent event) {
        Object obj = event.getSource();
        if (obj instanceof String) {
            String note = (String) obj;
            failoverProgress.firePropertyChange(PROGRESS_NOTE_PROPERTY, null,
                    note);
        } else if (obj instanceof Double) {
            Double progress = (Double) obj;
            failoverProgress.firePropertyChange(PROGRESS_AMOUNT_PROPERTY, null,
                    progress);
        }
    }

    /**
     * @return the lastConnectionError
     */
    public Throwable getLastConnectionError() {
        return lastError;
    }

    private void fireSubnetManagerConnectedEvent() {
        SubnetEvent subnetEvent = new SubnetEvent(subnet);
        for (ISubnetEventListener listener : subnetEventListeners) {
            try {
                listener.onSubnetManagerConnected(subnetEvent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void processNotices(final NoticeBean[] notices) {
        submitTopologyUpdateTaskIfNeeded();
        // Once the notices are saved, create a NoticeProcessTask
        // and submit it.
        // It's an asynchronous task so even though there is
        // processing speed difference between
        // NoticeSaveTask and NoticeProcessTask, we don't care.
        final NoticeProcessTask noticeProcessTask = new NoticeProcessTask(
                subnet.getName(), appContext.getDatabaseManager(), cacheMgr);

        // Note that the NoticeProcessTask runs in the serial
        // thread, which means that only one task is processed at
        // once. This is done for two reasons: first, if an outburst
        // of notices comes our way, NoticeProcessTask will pick up
        // whatever number has been enqueued and process them in one
        // task; secondly, since there is a potential to trigger a
        // copy of the whole topology if new nodes and links are
        // added, this process would need to be unique (like the
        // SaveTopologyTask, which uses the same thread) so that two
        // tasks do not step on each other.
        getProcessingService().submitSerial(noticeProcessTask,
                new ResultHandler<Future<Boolean>>() {
                    @Override
                    public void onTaskCompleted(
                            Future<Future<Boolean>> processResult) {
                        try {
                            Future<Boolean> dbFuture = processResult.get();
                            if (dbFuture != null) {
                                // NoticeProcessTask may return a null instead
                                // of a Future if there are no notices to
                                // process

                                Boolean topologyChanged = dbFuture.get();
                                if (topologyChanged) {
                                    // Special case for DBNodeCahce that the
                                    // Node distribution depends on nodes in DB.
                                    // So we must update after the DB
                                    // updatetopologyChanged.
                                    ((BaseCache) cacheMgr.acquireNodeCache())
                                            .setCacheReady(false);

                                    log.info(
                                            "Topology changed as a result of processing notices");
                                }

                                // notify after DB is ready
                                List<NoticeWrapper> noticeWrappers =
                                        noticeProcessTask.getNoticeWrappers();
                                noticeApi.addNewEventDescriptions(noticeWrappers
                                        .toArray(new NoticeWrapper[0]));

                            }
                        } catch (InterruptedException e) {
                            log.error("notice process task was interrupted", e);
                        } catch (ExecutionException e) {
                            Exception executionException =
                                    (Exception) e.getCause();
                            // TODO, we should inform the UI of the
                            // error (perhaps a
                            // newEventDescription?)
                            log.error(
                                    "Exception caught during notice process task",
                                    executionException);
                        } catch (Exception e) {
                            log.error(
                                    "Exception caught during notice process task",
                                    e);
                        }
                    }
                });
    }

    private void submitTopologyUpdateTaskIfNeeded() {
        if (!topologyUpdateTaskStarted.get()) {
            if (topologyUpdateTaskStarted.compareAndSet(false, true)) {
                cacheMgr.startTopologyUpdateTask();
            }
        }
    }

}
