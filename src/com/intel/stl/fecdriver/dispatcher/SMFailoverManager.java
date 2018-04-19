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

package com.intel.stl.fecdriver.dispatcher;

import static com.intel.stl.common.STLMessages.STL64000_SM_FAILOVER_UNSUCCESSFUL;
import static com.intel.stl.common.STLMessages.STL64003_SM_FAILOVER_CONNECTION_FAILED;
import static com.intel.stl.common.STLMessages.STL64004_SM_FAILOVER_CONNECTION_RETRY;
import static com.intel.stl.common.STLMessages.STL64005_SM_FAILOVER_ATTEMPTING_CONNECTION;
import static com.intel.stl.common.STLMessages.STL64006_SM_FAILOVER_GET_SM_ERROR;
import static com.intel.stl.common.STLMessages.STL64007_SM_FAILOVER_GET_PM_ERROR;
import static com.intel.stl.common.STLMessages.STL64008_SM_FAILOVER_GET_SM_RETRY;
import static com.intel.stl.common.STLMessages.STL64009_SM_FAILOVER_GET_PM_RETRY;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.CONNECTED;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.NOT_CONNECTED;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.PM_CHECKED;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.SM_LIST_CHECKED;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.SUCCESSFUL;
import static com.intel.stl.fecdriver.dispatcher.SMFailoverManager.STATE.UNSUCCESSFUL;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.performance.impl.PAHelper;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SMRecordBean;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.impl.SAHelper;
import com.intel.stl.fecdriver.ApplicationEvent;
import com.intel.stl.fecdriver.IFailoverHelper;
import com.intel.stl.fecdriver.IFailoverManager;
import com.intel.stl.fecdriver.IFailoverProgressListener;
import com.intel.stl.fecdriver.IStatement;
import com.intel.stl.fecdriver.session.ISession;

public class SMFailoverManager
        implements IFailoverManager, IConnectionEventListener {
    private final static Logger log =
            LoggerFactory.getLogger(SMFailoverManager.class);

    protected static enum STATE {
        NOT_CONNECTED,
        CONNECTED,
        SM_LIST_CHECKED,
        PM_CHECKED,
        SUCCESSFUL,
        UNSUCCESSFUL;
    }

    private static final String FO_TIMER_THREAD_PREFIX = "failover-";

    // The amount of time the failover thread will wait for a connection event
    // before checking if a failover timeout happened (in milliseconds)
    protected static final int CONNECTION_WAIT = 500;

    // The following percentages correspond to the four steps in checking a
    // connection; they sum up to 100%
    private static final int SM_CONN_PERCENTAGE = 34;

    private static final int SM_SMCHECK_PERCENTAGE = 25;

    private static final int SM_PMCHECK_PERCENTAGE = 33;

    private static final int SM_MINNUMCONNCHECK_PERCENTAGE = 8;

    // The delay before another retry attempt
    protected static int FO_CONN_RETRY_DELAY = 5000; // milliseconds

    protected static int FO_CHECK_SM_DELAY = 50; // milliseconds

    protected static int FO_CHECK_PM_DELAY = 50; // milliseconds

    protected static int FO_CHECK_MINNUMCONN_DELAY = 50; // milliseconds

    private final AtomicInteger threadCount = new AtomicInteger(1);

    private BlockingQueue<ConnectionResult> queue;

    private Map<HostInfo, HostStatus> sessions;

    private final IFailoverHelper helper;

    private long failoverTimeout;

    private boolean doNotClearSessions = false;

    /**
     * Description:
     *
     * @param helper
     */
    public SMFailoverManager(IFailoverHelper helper) {
        this.helper = helper;
        log.debug("SMFailoverManager constructor called.");
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.fecdriver.IFailoverManager#getFailoverTimeout()
     */
    @Override
    public long getFailoverTimeout() {
        return helper.getFailoverTimeout();
    }

    @Override
    public void stopFailover() {

    }

    @Override
    public SubnetDescription connectionLost(SubnetDescription subnet,
            IFailoverProgressListener listener) {
        long ellapsedTimeout = getFailoverTimeout();
        log.debug(
                "SMFailoverManager connectionLost called. Timeout set to {} ms.",
                ellapsedTimeout);
        int numConn = helper.getNumInitialConnections();
        if (numConn <= 0) {
            numConn = 1;
        }
        List<HostInfo> feList = subnet.getFEList();
        int queueSize = feList.size();
        this.queue = new ArrayBlockingQueue<ConnectionResult>(queueSize);
        this.sessions = new HashMap<HostInfo, HostStatus>();
        for (int i = 0; i < queueSize; i++) {
            HostInfo host = feList.get(i);
            try {
                String msg = STL64005_SM_FAILOVER_ATTEMPTING_CONNECTION
                        .getDescription(host.getHost());
                notifyListener(listener, msg);
                log.info("Starting session with host {}", host);
                HostStatus status = new HostStatus(i, host, subnet, numConn,
                        ellapsedTimeout);
                sessions.put(host, status);
                log.debug("Failover session created for host: " + host);
            } catch (IOException e) {
                log.error("IOException creating session for host {}", host, e);
            }
        }
        failoverTimeout = System.currentTimeMillis() + ellapsedTimeout;
        while (true) {
            try {
                ConnectionResult result =
                        queue.poll(CONNECTION_WAIT, TimeUnit.MILLISECONDS);
                if (result == null) {
                    checkTimeout(listener);
                    continue;
                }
                HostInfo hostInfo = result.getHostInfo();
                Throwable t = result.getError();
                HostStatus status = sessions.get(hostInfo);
                if (status != null) {
                    if (t == null) {
                        // Host checking continues
                        checkStatus(status, listener);
                        if (status.getState() == SUCCESSFUL) {
                            subnet.setCurrentFEIndex(status.getIndex());
                            cleanup(listener);
                            log.info("Failover successful for host: " + hostInfo
                                    + " index: " + status.getIndex());
                            return subnet;
                        } else {
                            checkTimeout(listener);
                        }
                    } else {
                        // This host received an error
                        status.setLastError(t);
                        checkError(status, listener);
                        if (status.getState() == UNSUCCESSFUL) {
                            removeHost(status);
                            if (sessions.size() == 0) {
                                cleanup(listener);
                                SMFailoverException foe =
                                        new SMFailoverException(
                                                STL64000_SM_FAILOVER_UNSUCCESSFUL);
                                log.info(
                                        "Failover failed - no remaining hosts to process.");
                                throw foe;
                            }
                        }
                        checkTimeout(listener);
                    }
                }
            } catch (InterruptedException e) {
                log.error("InterruptedException in FailoverManager.");
                cleanup(listener);
                SMFailoverException foe = new SMFailoverException(
                        STL64000_SM_FAILOVER_UNSUCCESSFUL);
                throw foe;
            }
        }
    }

    @Override
    public void onConnectionComplete(Connection conn) {
        HostInfo hostInfo = conn.getHostInfo();
        log.debug("Connection completed for host {}", hostInfo);
        ConnectionResult result = new ConnectionResult(hostInfo, null);
        putResult(result);
    }

    @Override
    public void onConnectionError(Connection conn, Throwable t) {
        HostInfo hostInfo = conn.getHostInfo();
        log.debug("Connection error in host {}: {}", hostInfo,
                StringUtils.getErrorMessage(t));
        ConnectionResult result = new ConnectionResult(hostInfo, t);
        putResult(result);
    }

    private ISession createSession(HostInfo hostInfo) throws IOException {
        return helper.createTemporarySession(hostInfo, this);
    }

    private void putResult(ConnectionResult result) {
        try {
            // Switch to the FailoverManager thread
            queue.put(result);
        } catch (InterruptedException e) {
            log.warn("Interrupted exception while putting connection result",
                    e);
        }
    }

    private void checkTimeout(IFailoverProgressListener listener) {
        long now = System.currentTimeMillis();
        if (now > failoverTimeout) {
            log.info("Failover unsuccessful due to timeout: {} ms over",
                    (now - failoverTimeout));
            cleanup(listener);
            SMFailoverException foe =
                    new SMFailoverException(STL64000_SM_FAILOVER_UNSUCCESSFUL);
            throw foe;
        }
    }

    private void checkStatus(HostStatus status,
            IFailoverProgressListener listener) {
        TimerTask task;
        double increment;
        int percent;
        int retries;
        int maxRetries;
        log.debug("SMFailoverManager checkStatus called for host: "
                + status.getHostInfo() + " state: " + status.getState());
        // Since we go back to the NOT_CONNECTED state after an error in each
        // subsequent state, we now count each successful step as a retry
        switch (status.getState()) {
            case NOT_CONNECTED:
                status.setState(CONNECTED);
                maxRetries = status.getMaxConnRetries();
                retries = status.getConnRetries();
                status.setConnRetries(retries + 1);
                percent = SM_CONN_PERCENTAGE / (maxRetries + 1);
                increment = status.setProgress(percent);
                notifyListener(listener, increment);
                task = new CheckSMAvailabilityTask(status);
                status.getTimer().schedule(task, FO_CHECK_SM_DELAY);
                break;
            case CONNECTED:
                status.setState(SM_LIST_CHECKED);
                maxRetries = status.getMaxSMCheckRetries();
                retries = status.getSMCheckRetries();
                status.setSMCheckRetries(retries + 1);
                percent = SM_SMCHECK_PERCENTAGE / (maxRetries + 1);
                increment = status.setProgress(percent);
                notifyListener(listener, increment);
                task = new CheckPMAvailabilityTask(status);
                status.getTimer().schedule(task, FO_CHECK_PM_DELAY);
                break;
            case SM_LIST_CHECKED:
                status.setState(PM_CHECKED);
                maxRetries = status.getMaxPMCheckRetries();
                retries = status.getPMCheckRetries();
                status.setPMCheckRetries(retries + 1);
                percent = SM_PMCHECK_PERCENTAGE / (maxRetries + 1);
                increment = status.setProgress(percent);
                notifyListener(listener, increment);
                task = new CheckMinNumConnTask(status);
                status.getTimer().schedule(task, FO_CHECK_MINNUMCONN_DELAY);
                break;
            case PM_CHECKED:
                int remainderPercent = calculateRemainderPercentage(
                        SM_CONN_PERCENTAGE, status.getMaxConnRetries(),
                        status.getConnRetries());
                increment = status.setProgress(remainderPercent);
                notifyListener(listener, increment);
                maxRetries = status.getMaxSMCheckRetries();
                remainderPercent =
                        calculateRemainderPercentage(SM_SMCHECK_PERCENTAGE,
                                maxRetries, status.getSMCheckRetries());
                increment = status.setProgress(remainderPercent);
                notifyListener(listener, increment);
                maxRetries = status.getMaxPMCheckRetries();
                remainderPercent =
                        calculateRemainderPercentage(SM_PMCHECK_PERCENTAGE,
                                maxRetries, status.getPMCheckRetries());
                increment = status.setProgress(remainderPercent);
                notifyListener(listener, increment);
                increment = status.setProgress(SM_MINNUMCONNCHECK_PERCENTAGE);
                notifyListener(listener, increment);
                status.setState(SUCCESSFUL);
                break;
            case SUCCESSFUL:
                // This should never happen
                break;
            case UNSUCCESSFUL:
                break;
        }
    }

    private void checkError(HostStatus status,
            IFailoverProgressListener listener) {
        TimerTask task = null;
        int maxRetries = 0;
        double percent = 0.0;
        long delay = FO_CONN_RETRY_DELAY;
        String msg = null;
        HostInfo hostInfo = status.getHostInfo();
        String host = hostInfo.getHost();
        log.debug("SMFailoverManager checkError called for host: "
                + status.getHostInfo() + " state: " + status.getState()
                + " error: " + status.getLastError());

        if (status.isRetryInProgress() == false) {
            int retries = 0;
            String cause = "";
            ISession session = status.getSession().get();
            switch (status.getState()) {
                case NOT_CONNECTED:
                    // A connection error happened
                    delay = FO_CONN_RETRY_DELAY;
                    maxRetries = status.getMaxConnRetries();
                    retries = status.getConnRetries();
                    status.setConnRetries(retries + 1);
                    percent = SM_CONN_PERCENTAGE / (maxRetries + 1);
                    msg = STL64004_SM_FAILOVER_CONNECTION_RETRY
                            .getDescription(host, retries + 1);
                    session.close();
                    task = new ConnectionRetryTask(status);
                    cause = "Connection attempt";
                    break;
                case CONNECTED:
                    // An SM verification error occurred.
                    delay = FO_CONN_RETRY_DELAY;
                    maxRetries = status.getMaxSMCheckRetries();
                    retries = status.getSMCheckRetries();
                    status.setSMCheckRetries(retries + 1);
                    percent = SM_SMCHECK_PERCENTAGE / (maxRetries + 1);
                    msg = STL64008_SM_FAILOVER_GET_SM_RETRY.getDescription(host,
                            retries + 1);
                    // We need to close the session and go back to the
                    // NOT_CONNECTED state because we don't know in which state
                    // the TCP/IP session is.
                    session.close();
                    status.setState(NOT_CONNECTED);
                    task = new ConnectionRetryTask(status);
                    cause = "SM availability check attempt";
                    break;
                case SM_LIST_CHECKED:
                    // An PM verification error occurred.
                    delay = FO_CONN_RETRY_DELAY;
                    maxRetries = status.getMaxPMCheckRetries();
                    retries = status.getPMCheckRetries();
                    status.setPMCheckRetries(retries + 1);
                    percent = SM_PMCHECK_PERCENTAGE / (maxRetries + 1);
                    msg = STL64009_SM_FAILOVER_GET_PM_RETRY.getDescription(host,
                            retries + 1);
                    task = new CheckPMAvailabilityTask(status);
                    // We need to close the session and go back to the
                    // NOT_CONNECTED state because we don't know in which state
                    // the TCP/IP session is.
                    session.close();
                    status.setState(NOT_CONNECTED);
                    task = new ConnectionRetryTask(status);
                    cause = "PM availability check attempt";
                    break;
                case PM_CHECKED:
                    // There are no retries here
                    delay = FO_CHECK_MINNUMCONN_DELAY;
                    task = new ConnectionRetryTask(status);
                    maxRetries = 0;
                    retries = 1;
                    break;
                case SUCCESSFUL:
                    // This should never happen
                    break;
                case UNSUCCESSFUL:
                    break;
            }

            if (task != null) {
                if (retries < maxRetries) {
                    Throwable t = status.getLastError();
                    retries++;
                    log.warn("{} {}/{} to host {}:{} failed due to error: {}",
                            cause, retries, maxRetries, host,
                            hostInfo.getPort(), StringUtils.getErrorMessage(t));
                    double increment = status.setProgress((int) percent);
                    notifyListener(listener, increment);
                    notifyListener(listener, msg);
                    status.setIsRetryInProgress(true);
                    status.getTimer().schedule(task, delay);
                } else {
                    connectionUnsuccessful(status, listener);
                }
            }
        } // End of if (status.isRetryInProgress() == false)
    }

    private int calculateRemainderPercentage(int totalPercentage,
            int maxRetries, int retries) {
        double sofar = (totalPercentage / (maxRetries + 1)) * retries;
        int percent = (int) (totalPercentage - sofar);
        return percent;
    }

    private void removeHost(HostStatus status) {
        HostInfo host = status.getHostInfo();
        sessions.remove(host);
        status.getTimer().cancel();
        closeSession(status);
    }

    private void closeSession(HostStatus status) {
        AtomicReference<ISession> sessionRef = status.getSession();
        ISession session = sessionRef.get();
        session.close();
        while (!sessionRef.compareAndSet(session, null)) {
            // This shouldn't loop more than once, if a ConnectionRetryTask is
            // setting a new session
            session = sessionRef.get();
            session.close();
        }
    }

    private void connectionUnsuccessful(HostStatus status,
            IFailoverProgressListener listener) {
        HostInfo hostInfo = status.getHostInfo();
        String host = hostInfo.getHost() + ":" + hostInfo.getPort();
        String msg =
                STL64003_SM_FAILOVER_CONNECTION_FAILED.getDescription(host);
        notifyListener(listener, msg);
        // Get whatever is left
        double increment = status.setProgress(100);
        notifyListener(listener, increment);
        status.setState(UNSUCCESSFUL);
    }

    private void cleanup(IFailoverProgressListener listener) {
        for (HostStatus status : sessions.values()) {
            try {
                double increment = status.setProgress(100);
                if (increment > 0.0) {
                    notifyListener(listener, increment);
                }
                if (status.timer != null) {
                    status.getTimer().cancel();
                }
                closeSession(status);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!doNotClearSessions) {
            sessions.clear();
        }
    }

    private void notifyListener(IFailoverProgressListener listener,
            double increment) {
        ApplicationEvent event = new ApplicationEvent(increment);
        listener.onFailoverProgress(event);
    }

    private void notifyListener(IFailoverProgressListener listener,
            String message) {
        ApplicationEvent event = new ApplicationEvent(message);
        listener.onFailoverProgress(event);
    }

    private ISession checkSession(HostStatus status) {
        ISession session = status.getSession().get();
        if (session == null) {
            RuntimeException rte =
                    new RuntimeException("Session closed for host "
                            + status.getHostInfo().getHost());
            throw rte;
        }
        return session;
    }

    protected class HostStatus {
        private final int index;

        private final HostInfo hostInfo;

        private final AtomicReference<ISession> session;

        private final int numConn;

        private final int maxConnRetries;

        private final int maxSMCheckRetries;

        private final int maxPMCheckRetries;

        private double remainder;

        private int connRetries = 0;

        private int smCheckRetries = 0;

        private int pmCheckRetries = 0;

        private Throwable lastError = null;

        private STATE state = NOT_CONNECTED;

        private Timer timer;

        private boolean isRetryInProgress;

        private final SubnetDescription subnet;

        public HostStatus(int index, HostInfo hostInfo,
                SubnetDescription subnet, int numConn, long failoverTimeout)
                        throws IOException {
            this.index = index;
            this.hostInfo = hostInfo;
            ISession session = createSession(hostInfo);
            this.session = new AtomicReference<ISession>(session);
            this.numConn = numConn;
            this.maxConnRetries = calculateConnRetries(failoverTimeout);
            this.maxSMCheckRetries = calculateSMCheckRetries(failoverTimeout);
            this.maxPMCheckRetries = calculatePMCheckRetries(failoverTimeout);
            this.remainder = 1.0;
            this.subnet = subnet;
        }

        public List<SMRecordBean> getOriginalSMList() {
            return subnet.getSMList();
        }

        public void setIsRetryInProgress(boolean isRetryInProgress) {
            this.isRetryInProgress = isRetryInProgress;
        }

        public boolean isRetryInProgress() {
            return this.isRetryInProgress;
        }

        public int getIndex() {
            return index;
        }

        public HostInfo getHostInfo() {
            return hostInfo;
        }

        public AtomicReference<ISession> getSession() {
            return session;
        }

        public int getNumConnections() {
            return numConn;
        }

        public int getMaxConnRetries() {
            return maxConnRetries;
        }

        public int getMaxSMCheckRetries() {
            return maxSMCheckRetries;
        }

        public int getMaxPMCheckRetries() {
            return maxPMCheckRetries;
        }

        public STATE getState() {
            return state;
        }

        public void setState(STATE state) {
            this.state = state;
        }

        public int getConnRetries() {
            return connRetries;
        }

        public void setConnRetries(int connRetries) {
            this.connRetries = connRetries;
        }

        public int getSMCheckRetries() {
            return smCheckRetries;
        }

        public void setSMCheckRetries(int smCheckRetries) {
            this.smCheckRetries = smCheckRetries;
        }

        public int getPMCheckRetries() {
            return pmCheckRetries;
        }

        public void setPMCheckRetries(int pmCheckRetries) {
            this.pmCheckRetries = pmCheckRetries;
        }

        public Throwable getLastError() {
            return lastError;
        }

        public void setLastError(Throwable lastError) {
            this.lastError = lastError;
        }

        public double setProgress(int percentage) {
            if (percentage < 0) {
                percentage = 0;
            }
            if (percentage > 100) {
                percentage = 100;
            }
            double progress = (1.0 * percentage) / 100;
            if (progress > remainder) {
                progress = remainder;
                remainder = 0.0;
            } else {
                remainder = remainder - progress;
            }
            return progress;
        }

        public Timer getTimer() {
            if (timer == null) {
                timer = new Timer(
                        FO_TIMER_THREAD_PREFIX + threadCount.incrementAndGet());
            }
            return timer;
        }
    }

    private class ConnectionRetryTask extends TimerTask {

        private final HostStatus status;

        public ConnectionRetryTask(HostStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            HostInfo hostInfo = status.getHostInfo();
            log.info("Attempting to connect to host {}:{}, attempt {}",
                    hostInfo.getHost(), hostInfo.getPort(),
                    status.getConnRetries() + 1);
            try {
                AtomicReference<ISession> sessionRef = status.getSession();
                ISession currSession = sessionRef.get();
                if (currSession != null) {
                    ISession session = createSession(hostInfo);
                    while (!sessionRef.compareAndSet(currSession, session)) {
                        currSession = sessionRef.get();
                        if (currSession == null) {
                            // This host is being discarded
                            session.close();
                            break;
                        } else {
                            // This should not happen since there is only one
                            // task per host
                            currSession.close();
                        }
                    }
                }
                status.setIsRetryInProgress(false);
            } catch (Exception e) {
                ConnectionResult result = new ConnectionResult(hostInfo, e);
                status.setIsRetryInProgress(false);
                putResult(result);
            }
        }
    }

    private class CheckSMAvailabilityTask extends TimerTask {

        private final HostStatus status;

        public CheckSMAvailabilityTask(HostStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            HostInfo hostInfo = status.getHostInfo();
            String host = hostInfo.getHost();
            log.info("Checking SM availability thru host {}:{}", host,
                    hostInfo.getPort());
            try {
                ISession session = checkSession(status);
                IStatement statement = session.createStatement();
                SAHelper helper = new SAHelper(statement);
                List<SMRecordBean> sms = helper.getSMs();
                ConnectionResult result;
                if (isCorrectSubnet(status.getOriginalSMList(), sms) == true) {
                    result = new ConnectionResult(hostInfo, null);
                    log.debug("CheckSMAvailability successful on host: "
                            + hostInfo);
                } else {
                    result = new ConnectionResult(hostInfo,
                            new SMFailoverException(
                                    STL64006_SM_FAILOVER_GET_SM_ERROR));
                    log.debug(
                            "CheckSMAvailability could noy verify SM on host: "
                                    + hostInfo);
                }
                putResult(result);
            } catch (Exception e) {
                ConnectionResult result = new ConnectionResult(hostInfo, e);
                log.warn(
                        "Exception occurred while checking SM availability thru host {}:{}: {}",
                        hostInfo.getHost(), hostInfo.getPort(),
                        StringUtils.getErrorMessage(e));
                putResult(result);
            }
        }
    }

    private class CheckPMAvailabilityTask extends TimerTask {

        private final HostStatus status;

        public CheckPMAvailabilityTask(HostStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            HostInfo hostInfo = status.getHostInfo();
            log.info("Checking PM availability thru host {}:{}",
                    hostInfo.getHost(), hostInfo.getPort());
            try {
                ISession session = checkSession(status);
                IStatement statement = session.createStatement();
                PAHelper paHelper = new PAHelper(statement);
                ImageInfoBean img = paHelper.getImageInfo(0, 0);
                PMConfigBean pmConfig = paHelper.getPMConfig();
                ConnectionResult result;
                if (pmConfig != null && img != null) {
                    result = new ConnectionResult(hostInfo, null);
                    log.debug("CheckPMAvailability successful on host: "
                            + hostInfo + " pmconfig: " + pmConfig);
                } else {
                    result = new ConnectionResult(hostInfo,
                            new SMFailoverException(
                                    STL64007_SM_FAILOVER_GET_PM_ERROR));
                    log.debug(
                            "CheckPMAvailability could noy verify PM on host: "
                                    + hostInfo);
                }
                putResult(result);
            } catch (Exception e) {
                ConnectionResult result = new ConnectionResult(hostInfo, e);
                log.warn(
                        "Exception occurred while checking PM availability thru host {}:{}: {}",
                        hostInfo.getHost(), hostInfo.getPort(),
                        StringUtils.getErrorMessage(e));
                putResult(result);
            }
        }
    }

    private class CheckMinNumConnTask extends TimerTask {

        private final HostStatus status;

        public CheckMinNumConnTask(HostStatus status) {
            this.status = status;
        }

        @Override
        public void run() {
            HostInfo hostInfo = status.getHostInfo();
            log.info(
                    "Checking minimum number of connections available thru host {}:{}",
                    hostInfo.getHost(), hostInfo.getPort());
            int minNumConn = status.getNumConnections();
            // Subtract the connection we already have in HostStatus
            minNumConn--;
            final Exchanger<Throwable> connXchgr = new Exchanger<Throwable>();
            IConnectionEventListener listener = new IConnectionEventListener() {

                @Override
                public void onConnectionComplete(Connection conn) {
                    try {
                        connXchgr.exchange(null, 1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onConnectionError(Connection conn, Throwable t) {
                    try {
                        connXchgr.exchange(t, 1000, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException | TimeoutException e) {
                        e.printStackTrace();
                    }
                }
            };
            List<ISession> tempSessions = new ArrayList<ISession>(minNumConn);
            try {

                for (int i = 0; i < minNumConn; i++) {
                    ISession tmpSession =
                            helper.createTemporarySession(hostInfo, listener);
                    tempSessions.add(tmpSession);
                    Exception error = (Exception) connXchgr.exchange(null, 5000,
                            TimeUnit.MILLISECONDS);
                    if (error != null) {
                        throw error;
                    }
                }
                ConnectionResult result = new ConnectionResult(hostInfo, null);
                putResult(result);
            } catch (Exception e) {
                ConnectionResult result = new ConnectionResult(hostInfo, e);
                log.warn(
                        "Exception occured while checking minimum number of connection thru host {}:{}: {}",
                        hostInfo.getHost(), hostInfo.getPort(),
                        StringUtils.getErrorMessage(e));
                putResult(result);
            } finally {
                for (ISession tmpSession : tempSessions) {
                    try {
                        tmpSession.close();
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    private class ConnectionResult {

        private final HostInfo hostInfo;

        private final Throwable error;

        public ConnectionResult(HostInfo hostInfo, Throwable t) {
            this.hostInfo = hostInfo;
            this.error = t;
        }

        public HostInfo getHostInfo() {
            return hostInfo;
        }

        public Throwable getError() {
            return error;
        }
    }

    // Determine if the information from the connected subnet matches the
    // originally connected subnet.
    protected boolean isCorrectSubnet(List<SMRecordBean> origSmRecords,
            List<SMRecordBean> smRecords) {
        // Return true if:
        // - any GUID in smRecords matches a GUID in origSmRecords
        // - origSmRecords is null
        // - origSmRecords is empty (we are connecting for the first time).
        //
        // Return false otherwise.
        boolean isFound = false;
        if ((origSmRecords != null) && (origSmRecords.isEmpty() == false)) {
            if ((smRecords != null) && (smRecords.isEmpty() == false)) {
                ArrayList<SMRecordBean> smRecordsAL =
                        (ArrayList<SMRecordBean>) smRecords;
                Iterator<SMRecordBean> smRecordsALIterator =
                        smRecordsAL.iterator();
                // Compare GUIDS in both lists.
                while (smRecordsALIterator.hasNext() && (isFound != true)) {
                    SMRecordBean smRecordsALBean = smRecordsALIterator.next();
                    Iterator<SMRecordBean> subnetManagerRecordsIterator =
                            origSmRecords.iterator();
                    while (subnetManagerRecordsIterator.hasNext()) {
                        SMRecordBean subnetManagerRecordsBean =
                                subnetManagerRecordsIterator.next();
                        if (smRecordsALBean.getSmInfo()
                                .getPortGuid() == subnetManagerRecordsBean
                                        .getSmInfo().getPortGuid()) {
                            // We found a match.
                            isFound = true;
                            break;

                        }
                    }
                }
            }
        } else {
            // First time connecting, we have nothing to compare.
            isFound = true;
        }

        return isFound;
    }

    /**
     *
     * <i>Description:</i> calculates the number of connection retries before a
     * failover timeout occurs
     *
     * @param failoverTimeout
     * @return retries
     */
    private int calculateConnRetries(long failoverTimeout) {
        // we assume it takes 100ms to get a connection response (although it's
        // usually much less if the host is available)
        return (int) (failoverTimeout / (FO_CONN_RETRY_DELAY + 100));
    }

    /**
     *
     * <i>Description:</i> calculates the number of SM availability check
     * retries before a failover timeout occurs
     *
     * @param failoverTimeout
     * @return retries
     */
    private int calculateSMCheckRetries(long failoverTimeout) {
        // we assume it takes 400ms to get a connection, and a SM response
        return (int) (failoverTimeout
                / (FO_CONN_RETRY_DELAY + FO_CHECK_SM_DELAY + 400));
    }

    /**
     *
     * <i>Description:</i> calculates the number of PM availability check
     * retries before a failover timeout occurs
     *
     * @param failoverTimeout
     * @return retries
     */
    private int calculatePMCheckRetries(long failoverTimeout) {
        // we assume it takes 700ms to get a connection response, a SM response
        // and a PM response
        return (int) (failoverTimeout / (FO_CONN_RETRY_DELAY + FO_CHECK_SM_DELAY
                + FO_CHECK_PM_DELAY + 700));
    }

    // The following methods are used in testing
    protected Map<HostInfo, HostStatus> getSessions() {
        return sessions;
    }

    protected void setDoNotClearSessions(boolean doNotClearSessions) {
        this.doNotClearSessions = doNotClearSessions;
    }

    protected HostStatus getHostStatusFor(HostInfo hostInfo) {
        return sessions.get(hostInfo);
    }

}
