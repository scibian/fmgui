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

import static com.intel.stl.common.STLMessages.STL20001_CONNECTION_ERROR;
import static com.intel.stl.common.STLMessages.STL20003_CONNECTION_TIMEOUT;

import java.io.IOException;
import java.net.ConnectException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.SSLEngine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.DefaultConnectionAssistant;
import com.intel.stl.api.FMException;
import com.intel.stl.api.IConnectionAssistant;
import com.intel.stl.api.SSLStoreCredentialsDeniedException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.failure.BaseTaskFailure;
import com.intel.stl.api.failure.IFailureEvaluator;
import com.intel.stl.api.failure.IFailureManagement;
import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.NoticeType;
import com.intel.stl.api.notice.TrapType;
import com.intel.stl.api.notice.impl.NoticeSaveTask;
import com.intel.stl.api.subnet.GIDGlobal;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.configuration.ResultHandler;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.ApplicationEvent;
import com.intel.stl.fecdriver.ICommand;
import com.intel.stl.fecdriver.IFailoverEventListener;
import com.intel.stl.fecdriver.IFailoverManager;
import com.intel.stl.fecdriver.IFailoverProgressListener;
import com.intel.stl.fecdriver.IResponse;
import com.intel.stl.fecdriver.adapter.IAdapter;
import com.intel.stl.fecdriver.adapter.ISMEventListener;
import com.intel.stl.fecdriver.session.ISession;
import com.intel.stl.fecdriver.session.Session;

/**
 * SubnetRequestDispatcher is the reactor for FE-related events (new commands,
 * responses from the FE).
 *
 * It runs on its own thread whose name is THREAD_NAME_PREFIX followed by the
 * subnet id. It is responsible for managing connections:
 *
 * - Creating connections
 *
 * - Assigning command requests to a connection
 *
 * - Handling connection errors
 *
 * - Dispatching connections
 *
 * Each connection is a handler, which is in charge of managing one resource,
 * namely a SocketChannel. Every time an event is signaled related to the
 * resource (like a new command to be submitted or a response from the FE), the
 * SubnetRequestDispatcher finds out which handler (connection) needs to handle
 * the event and dispatches it for processing on a separate thread. At that
 * point, the handler (connection) is running single-threaded and no-one else is
 * allowed to access its state (except for the dispatcher, who can assign new
 * commands or close the connection).
 *
 * WARNING: the mechanism to avoid a connection to be selected multiple times by
 * the Selector is to set the interest operations to 0 before it is dispatched.
 * The interest operations are then reset after the handler has finished
 * processing an event (using a value set by the handler). Never ever change the
 * interest operations outside the dispatcher thread; interest operations in the
 * SelectionKey are not thread safe.
 *
 */
public class SubnetRequestDispatcher extends Reactor<Connection, Void>
        implements IRequestDispatcher, IConnectionEventListener,
        IEventListener<NoticeBean>, IFailoverProgressListener {
    private static Logger log =
            LoggerFactory.getLogger(SubnetRequestDispatcher.class);

    private static final int THREAD_POOL_SIZE = 3;

    protected static int MAX_CONN_POOL_SIZE = 10;

    public static int MIN_CONN_POOL_SIZE = 3;

    private static final String THREAD_NAME_PREFIX = "srdthread-";

    private boolean running;

    private boolean connectivityError = false;

    private boolean failoverInProgress = false;

    private boolean failoverMgrRunning = false;

    private Exception connectivityException;

    private final AtomicInteger connCompleted = new AtomicInteger(0);

    private final AtomicReference<Connection> noticeConnection =
            new AtomicReference<Connection>(null);

    private int invConnections = 0;

    private int targetNumConnections = 0;

    private int lastNumSessions = 0;

    private HostInfo hostInfo;

    private final ConcurrentLinkedQueue<HandlerTask> pendingResults =
            new ConcurrentLinkedQueue<HandlerTask>();

    private final ConcurrentLinkedQueue<PendingCommand> pendingCmds =
            new ConcurrentLinkedQueue<PendingCommand>();

    private final ConcurrentLinkedQueue<Connection> pendingConns =
            new ConcurrentLinkedQueue<Connection>();

    private final List<ISMEventListener> snEventListeners =
            new CopyOnWriteArrayList<ISMEventListener>();

    protected final List<ISession> sessions =
            Collections.synchronizedList(new ArrayList<ISession>());

    protected final List<Connection> connPool;

    private final SubnetDescription subnet;

    private final IAdapter adapter;

    private final DatabaseManager dbMgr;

    private final IPoolingPolicy<Connection> poolingPolicy;

    private final Selector selector;

    public SubnetRequestDispatcher(SubnetDescription subnet, IAdapter adapter,
            DatabaseManager dbMgr) throws IOException {
        this(subnet, adapter, dbMgr, new ConnectionPoolingPolicy(
                MAX_CONN_POOL_SIZE, MIN_CONN_POOL_SIZE), Selector.open());
    }

    public SubnetRequestDispatcher(SubnetDescription subnet, IAdapter adapter,
            DatabaseManager dbMgr, IPoolingPolicy<Connection> poolingPolicy)
            throws IOException {
        this(subnet, adapter, dbMgr, poolingPolicy, Selector.open());
    }

    protected SubnetRequestDispatcher(SubnetDescription subnet,
            IAdapter adapter, DatabaseManager dbMgr,
            IPoolingPolicy<Connection> poolingPolicy, Selector selector) {
        super(Executors.newFixedThreadPool(THREAD_POOL_SIZE,
                new DispatcherThreadFactory(THREAD_NAME_PREFIX, subnet)));
        this.subnet = subnet;
        this.adapter = adapter;
        this.dbMgr = dbMgr;
        this.selector = selector;
        this.connPool =
                Collections.synchronizedList(new ArrayList<Connection>());
        this.poolingPolicy = poolingPolicy;
        setName(THREAD_NAME_PREFIX + subnet.getSubnetId());
        setDaemon(true);
        this.running = true;
        IConnectionAssistant assistant = subnet.getConnectionAssistant();
        for (HostInfo host : subnet.getFEList()) {
            host.setConnectionAssistant(assistant);
        }
        setHostInfo();
    }

    @Override
    public ISession createSession() {
        Session session = new Session(this);
        sessions.add(session);
        wakeupDispatcher();
        return session;
    }

    @Override
    public ISession createSession(ISMEventListener listener) {
        if (listener != null) {
            snEventListeners.add(listener);
        }
        return createSession();
    }

    @Override
    public void removeSession(ISession session) {
        synchronized (sessions) {
            Iterator<ISession> it = sessions.iterator();
            while (it.hasNext()) {
                if (it.next().equals(session)) {
                    it.remove();
                    break;
                }
            }
        }
        if (sessions.size() == 0) {
            adapter.shutdownSubnet(subnet);
        } else {
            wakeupDispatcher();
        }
    }

    @Override
    public void cancelFailover() {
        if (failoverMgrRunning) {
            super.interrupt();
        }
    }

    @Override
    public <E extends IResponse<F>, F> void queueCmd(ICommand<E, F> cmd) {
        if (connectivityError) {
            cmd.getResponse().setError(connectivityException);
        } else {
            addPendingCommand(cmd, null);
            wakeupDispatcher();
        }
    }

    @Override
    protected void onHandlerDone(HandlerTask future) {
        pendingResults.add(future);
        wakeupDispatcher();
    }

    @Override
    public void onRequestTimeout(TimeoutException toe) {
        // We simulate a TimeoutException in a HandlerTask so that dispatcher
        // thread will process it accordingly (start failover)
        HandlerTask future = createHandlerTask(new ConnectionTimeout(toe));
        try {
            future.run();
        } catch (Exception e) {
            // Ignore the timeout exception
        }
        onHandlerDone(future);
    }

    @Override
    public void run() {
        MDC.put("subnet", subnet.getName());
        try {
            log.info("Starting Subnet Request Dispatcher for subnet {}",
                    subnet.getName());
            targetNumConnections = resizeConnectionPool();
            processPendingConnections();
            while (running) {
                try {
                    // Wait for an event
                    selector.select();
                } catch (IOException e) {
                    // Handle error with selector
                    log.error("selector.selection exception: " + e.getMessage(),
                            e);
                    break;
                }
                if (lastNumSessions != sessions.size()) {
                    targetNumConnections += resizeConnectionPool();
                }
                processPendingConnections();
                processPendingResults();
                processPendingCmds();
                processSelectedKeys();
            }
            closeConnections();
        } finally {
            try {
                selector.close();
            } catch (IOException e) {
                log.debug("IOException closing selector", e);
            }
            this.running = false;
            log.info(
                    "Subnet Request Dispatcher for subnet {} has been shut down.",
                    subnet.getName());
        }

    }

    @Override
    public void onConnectionComplete(Connection connection) {
        int numConn = connCompleted.incrementAndGet();
        Connection noticeConn = noticeConnection.get();
        if (noticeConn == null) {
            if (noticeConnection.compareAndSet(null, connection)) {
                connection.addNoticeListener(this);
            }
        }
        if (numConn == targetNumConnections) {
            NoticeBean notice = createConnEstablishNotice();
            onNewEvent(new NoticeBean[] { notice });
        }
    }

    @Override
    public void onConnectionError(Connection connection, Throwable t) {
        // This is not where we handle connection errors;
    }

    @Override
    public synchronized void onNewEvent(final NoticeBean[] notices) {
        NoticeSaveTask noticeSaveTask =
                new NoticeSaveTask(dbMgr, subnet.getName(), notices);
        adapter.submitTask(noticeSaveTask, new ResultHandler<Void>() {

            @Override
            public void onTaskCompleted(Future<Void> saveResult) {
                try {
                    saveResult.get();
                    // Should we fire them anyway? NoticeManager should rely on
                    // what's in the database
                    fireNotices(notices);
                } catch (InterruptedException e) {
                    log.error("notice save task was interrupted", e);
                } catch (ExecutionException e) {
                    log.error("Exception caught during notice save task",
                            e.getCause());
                }
            }
        });
    }

    @Override
    public void onFailoverProgress(ApplicationEvent event) {
        for (IFailoverEventListener listener : snEventListeners) {
            try {
                listener.onFailoverProgress(event);
            } catch (Exception e) {
                log.error("FailoverEventListener '{}' had an exception",
                        listener.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    public SubnetDescription getSubnetDescription() {
        return subnet;
    }

    @Override
    public void refreshSubnetDescription(SubnetDescription subnet) {
        if (this.subnet.getSubnetId() == subnet.getSubnetId()) {
            this.subnet.setName(subnet.getName());
            this.subnet.setPrimaryFEIndex(subnet.getPrimaryFEIndex());
            HostInfo currFE = this.subnet.getCurrentFE();
            List<HostInfo> feList = subnet.getFEList();
            int currFEIdx = -1;
            IConnectionAssistant assistant =
                    this.subnet.getConnectionAssistant();
            for (int i = 0; i < feList.size(); i++) {
                HostInfo host = feList.get(i);
                host.setConnectionAssistant(assistant);
                if (currFE.equals(host)) {
                    currFEIdx = i;
                }
            }
            this.subnet.setFEList(feList);
            if (currFEIdx >= 0) {
                this.subnet.setCurrentFEIndex(currFEIdx);
            } else {
                this.subnet.setCurrentFEIndex(subnet.getPrimaryFEIndex());
            }
        }
    }

    @Override
    public SSLEngine getSSLEngine(HostInfo hostInfo) throws Exception {
        SSLEngine engine = adapter.getSSLEngine(hostInfo);
        if (engine != null) {
            return engine;
        } else {
            IConnectionAssistant assistant = hostInfo.getConnectionAssistant();
            if (assistant == null) {
                assistant = new DefaultConnectionAssistant();
            }
            while (true) {
                try {
                    CertsDescription certs =
                            assistant.getSSLStoreCredentials(hostInfo);
                    engine = adapter.getSSLEngine(hostInfo, certs);
                    certs.clearPwds();
                    return engine;
                } catch (SSLStoreCredentialsDeniedException e) {
                    log.error("Connection prevented by user action", e);
                    throw e;
                } catch (FMException e) {
                    hostInfo.getCertsDescription().clearPwds();
                    assistant.onSSLStoreError(e);
                } catch (Exception e) {
                    log.error("Error calling Connection Assistant", e);
                    throw e;
                }
            }
        }
    }

    private void processSelectedKeys() {
        Set<SelectionKey> keys = selector.selectedKeys();
        for (SelectionKey selKey : keys) {
            Connection conn = (Connection) selKey.attachment();
            if (conn.isProcessing()) {
                continue;
            }
            conn.setProcessing(true);
            try {
                int ops = selKey.interestOps();
                log.debug("Dispatching connection {}. InterestOps: {}", conn,
                        ops);
                selKey.interestOps(0);
                conn.setInterestOps(ops);
                dispatch(conn);
            } catch (Exception e) {
                log.error(
                        "Exception processing selection key for connection {}",
                        conn, e);
            }
        }
        keys.clear();
    }

    private void processPendingCmds() {
        while (!pendingCmds.isEmpty()) {
            PendingCommand pCmd = pendingCmds.poll();
            if (pCmd != null) {
                ICommand<?, ?> cmd = pCmd.getCommand();
                if (cmd.getResponse().isCancelled()) {
                    // If the command has been cancelled, just skip it
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "Command {} with id '{}' has been cancelled by user. Ignored",
                                cmd, cmd.getMessageID());
                    }
                    continue;
                }
                Connection conn = pCmd.getConnection();
                try {
                    if (conn == null) {
                        conn = poolingPolicy.nextHandler(connPool);
                    }
                    conn.assignCmd(cmd);
                    SelectionKey selKey = conn.getSelectionKey();
                    int ops = selKey.interestOps();
                    ops = ops | SelectionKey.OP_WRITE;
                    selKey.interestOps(ops);
                    if (log.isDebugEnabled()) {
                        log.debug(
                                "Command {} with id '{}' assigned to connection {}. InterestOps: {}",
                                cmd, cmd.getMessageID(), conn, ops);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void processPendingResults() {
        while (!pendingResults.isEmpty()) {
            HandlerTask result = pendingResults.poll();
            if (result != null) {
                processResult(result);
            }
        }
    }

    private void processPendingConnections() {
        Connection initConn = null;
        Exception initException = null;
        while (!pendingConns.isEmpty()) {
            Connection conn = pendingConns.poll();
            connPool.add(conn);
            try {
                if (log.isDebugEnabled()) {
                    log.debug("Initializing connection to host {}...",
                            conn.getHostInfo());
                }
                conn.initialize(selector, this);
            } catch (SSLStoreCredentialsDeniedException e) {
                // Attempt to get store credentials from the UI either was
                // cancelled by user or the UI gave up. No point in continuing
                connectionLost(e);
                pendingConns.clear();
                List<ICommand<?, ?>> cmds = new ArrayList<ICommand<?, ?>>();
                for (PendingCommand cmd : pendingCmds) {
                    cmds.add(cmd.getCommand());
                }
                cancelPendingCmds(cmds, e);
            } catch (Exception e) {
                initConn = conn;
                initException = e;
                invConnections++;
            }
        }
        if (initException != null) {
            if (failoverInProgress) {
                // At this point, we are dead in the water. FailoverManager has
                // provided a HostInfo to which we cannot connect, so in other
                // words failover failed. This RuntimeException will be caught
                // in the failover method, not the main dispatcher loop.
                throw new RuntimeException("Exception reconnecting to subnet "
                        + subnet.getName() + " after failover", initException);
            } else {
                processConnectionError(createConnException(initException),
                        initConn);
            }
        }
    }

    private void processResult(HandlerTask result) {
        Connection conn = result.getCallable();
        try {
            // Do not check exceptions for connections that are closing
            if (!conn.isClosing()) {
                result.get();
            }
        } catch (ExecutionException ee) {
            final Exception cause = (Exception) ee.getCause();
            // Rethrow the exception to refine what to do
            Callable<Void> callable = new Callable<Void>() {

                @Override
                public Void call() throws Exception {
                    throw cause;
                }
            };
            try {
                callable.call();
            } catch (ClosedChannelException cce) {
                processConnectionError(createConnException(cce), conn);
            } catch (IOException ioe) {
                processIOException(ioe, conn);
            } catch (TimeoutException toe) {
                processConnectionError(createTimeoutException(toe), conn);
            } catch (Exception e) {
                processRequestError(e, conn);
            }
        } catch (Exception e) {
            log.error("Exception processing request handler", e);
        } finally {
            conn.setProcessing(false);
            SelectionKey selKey = conn.getSelectionKey();
            if (selKey != null && selKey.isValid()) {
                int ops = selKey.interestOps();
                int newops = conn.getInterestOps();
                ops = ops | newops | SelectionKey.OP_READ;
                selKey.interestOps(ops);
                log.debug(
                        "Processing for connection {} is complete. InterestOps: {}",
                        conn, ops);
            }
        }
    }

    protected void processConnectionError(Exception ce, Connection conn) {
        try {
            conn.close();
        } catch (IOException e) {
            // Ignore any errors
        } finally {
            if (!connectivityError) {
                failover(ce);
            }
        }
    }

    protected void processIOException(IOException ioe, Connection conn) {
        String msg = ioe.getMessage();
        if ((ioe instanceof ConnectException) || (msg != null
                && msg.contains("connection was forcibly closed"))) {
            // We need to send a ping request to the FE to see if connection
            // is still available; in the meantime, we do this ugly check
            log.error("Connection {} was forcibly closed", conn, ioe);
            processConnectionError(createConnException(ioe), conn);
        } else {
            processRequestError(ioe, conn);
        }
    }

    protected void processRequestError(final Exception re,
            final Connection conn) {
        log.error("Request error '{}' being handled by failure management",
                StringUtils.getErrorMessage(re));
        IFailureManagement failureMgr = adapter.getFailureManager();
        IFailureEvaluator failureEvaluator = adapter.getFailureEvaluator();
        BaseTaskFailure<Void> taskFailure = new BaseTaskFailure<Void>(
                conn.getSelectionKey(), failureEvaluator) {

            @Override
            public Callable<Void> getTask() {
                // we do NOT retry!
                return null;
            }

            @Override
            public void onFatal() {
                log.info("Fatal Failure - Close connection!");
                processConnectionError(re, conn);
            }

        };
        failureMgr.submit(taskFailure, re);
    }

    private RuntimeException createConnException(Exception e) {
        RuntimeException rte =
                new RuntimeException(STL20001_CONNECTION_ERROR.getDescription(
                        subnet.getName(), StringUtils.getErrorMessage(e)), e);
        return rte;
    }

    private RuntimeException createTimeoutException(Exception e) {
        RuntimeException rte = new RuntimeException(
                STL20003_CONNECTION_TIMEOUT.getDescription(subnet.getName()));

        return rte;
    }

    private void failover(Exception originalException) {
        log.info("Starting failover processing for subnet {}...",
                subnet.getName());
        fireFailoverStart();
        List<ICommand<?, ?>> pendingCmds = new ArrayList<ICommand<?, ?>>();
        Exception cause = null;
        try {
            failoverInProgress = true;
            IFailoverManager failoverMgr = adapter.getFailoverManager();
            long waitExtension = failoverMgr.getFailoverTimeout();
            resetConnections(pendingCmds, waitExtension);
            failoverMgrRunning = true;
            SubnetDescription modSubnet =
                    failoverMgr.connectionLost(subnet, this);
            failoverMgrRunning = false;
            log.info("Failover completed; returned {}", modSubnet);
            subnet.setCurrentFEIndex(modSubnet.getCurrentFEIndex());
            setHostInfo();
            invConnections = 0;
            targetNumConnections = resizeConnectionPool();
            processPendingConnections();
            requeueCmds(pendingCmds);
            processPendingCmds();
        } catch (Exception e) {
            cause = e;
            log.error("Failover for subnet {} has thrown an exception: {}",
                    subnet.getName(), e.getMessage(), e);
            connectionLost(originalException);
            cancelPendingCmds(pendingCmds, originalException);
        } finally {
            failoverMgrRunning = false;
            failoverInProgress = false;
            fireFailoverEnd(cause);
        }
    }

    private void connectionLost(Exception e) {
        connectivityError = true;
        connectivityException = e;
        NoticeBean notice = createConnLostNotice();
        onNewEvent(new NoticeBean[] { notice });
    }

    private void resetConnections(List<ICommand<?, ?>> cmds,
            long waitExtension) {
        Iterator<Connection> it = connPool.iterator();
        while (it.hasNext()) {
            Connection conn = it.next();
            Collection<ICommand<?, ?>> assignedCmds = conn.getPendingCommands();
            closeConnection(conn);
            it.remove();
            cmds.addAll(assignedCmds);
            for (ICommand<?, ?> cmd : assignedCmds) {
                cmd.getResponse().extendWaitTime(waitExtension);
            }
        }
        connPool.clear();
        connCompleted.set(0);
        // set noticeConnection to null, so we can reset notice listener to
        // next new connection
        noticeConnection.set(null);
    }

    protected void closeConnection(Connection conn) {
        try {
            conn.close();
        } catch (Exception e) {
            if (log.isDebugEnabled()) {
                log.debug("Exception closing connection {}", conn, e);
            }
        }
    }

    protected void cancelPendingCmds(List<ICommand<?, ?>> cmds, Exception e) {
        for (ICommand<?, ?> cmd : cmds) {
            cmd.getResponse().setError(e);
        }
        while (!pendingCmds.isEmpty()) {
            PendingCommand pCmd = pendingCmds.poll();
            ICommand<?, ?> cmd = pCmd.getCommand();
            cmd.getResponse().setError(e);
        }
    }

    private void requeueCmds(List<ICommand<?, ?>> cmds) {
        for (ICommand<?, ?> cmd : cmds) {
            addPendingCommand(cmd, null);
        }
    }

    private void fireNotices(NoticeBean[] notices) {
        if (log.isDebugEnabled()) {
            log.debug("Fire {} notices: {} to {}", notices.length,
                    Arrays.toString(notices), snEventListeners);
        }
        for (ISMEventListener listener : snEventListeners) {
            try {
                listener.onNewEvent(notices);
            } catch (Exception e) {
                log.error("SubnetEventListener '{}' had an exception",
                        listener.getClass().getSimpleName(), e);
            }
        }
    }

    protected void fireFailoverStart() {
        ApplicationEvent event = new ApplicationEvent(subnet);
        for (IFailoverEventListener listener : snEventListeners) {
            try {
                listener.onFailoverStart(event);
            } catch (Exception e) {
                log.error("FailoverEventListener '{}' had an exception",
                        listener.getClass().getSimpleName(), e);
            }
        }
    }

    protected void fireFailoverEnd(Throwable result) {
        ApplicationEvent event = new ApplicationEvent(subnet, result);
        for (IFailoverEventListener listener : snEventListeners) {
            try {
                listener.onFailoverEnd(event);
            } catch (Exception e) {
                log.error("FailoverEventListener '{}' had an exception",
                        listener.getClass().getSimpleName(), e);
            }
        }
    }

    @Override
    public void interrupt() {
        super.interrupt();
    }

    @Override
    public void shutdown() {
        this.running = false;
        // First, let all pending handlers finish
        super.shutdown();
        // Make a shallow copy of all sessions
        List<ISession> sessionList = new ArrayList<ISession>();
        synchronized (sessions) {
            Iterator<ISession> it = sessions.iterator();
            while (it.hasNext()) {
                sessionList.add(it.next());
            }
        }
        // Close all sessions (this will call removeSession, above)
        for (ISession session : sessionList) {
            session.close();
        }
        // Finally end the thread
        wakeupDispatcher();
    }

    private void closeConnections() {
        // Invoked during shutdown
        for (Connection conn : connPool) {
            try {
                conn.close();
            } catch (Exception e) {
                log.error("Exception closing connection {} during shutdown",
                        conn, e);
            }
        }
    }

    private void setHostInfo() {
        List<HostInfo> feList = subnet.getFEList();
        if (feList == null || feList.size() == 0) {
            this.hostInfo = null;
        } else {
            HostInfo hostInfo = subnet.getCurrentFE();
            this.hostInfo = hostInfo.copy();
        }
    }

    private int resizeConnectionPool() {
        int numSessions = sessions.size();
        int connections = connPool.size() + pendingConns.size();
        int delta =
                poolingPolicy.calculateNumHandlers(connections, numSessions);
        if (delta > 0) {
            for (int i = 0; i < delta; i++) {
                Connection conn = createConnection(hostInfo, this, false);
                addPendingConnection(conn);
            }
        }
        lastNumSessions = numSessions;
        return delta;
    }

    protected void wakeupDispatcher() {
        selector.wakeup();
    }

    protected void addPendingConnection(Connection conn) {
        pendingConns.add(conn);
    }

    protected void addPendingCommand(ICommand<?, ?> cmd, Connection conn) {
        // We set this value here to true for compatibility with the old adapter
        // When the command is assigned to a connection, this value is reset to
        // the connection status
        cmd.setConnectionInProgress(true);
        PendingCommand pCmd = new PendingCommand(cmd, conn);
        pendingCmds.add(pCmd);
    }

    protected Connection createConnection(HostInfo host,
            IConnectionEventListener listener, boolean isTemporaryConnection) {
        if (host == null) {
            throw new IllegalArgumentException(
                    "Attempting to create a connection with null host");
        }
        Connection conn;
        SocketChannel channel = adapter.createChannel();
        if (host.isSecureConnect()) {
            conn = new SecureConnection(host, channel, listener);
        } else {
            conn = new Connection(host, channel, listener);
        }
        conn.setTemporaryConnection(isTemporaryConnection);
        return conn;
    }

    private NoticeBean createConnEstablishNotice() {
        NoticeBean bean = new NoticeBean(true);
        GenericNoticeAttrBean attr = new GenericNoticeAttrBean();
        attr.setGeneric(true);
        attr.setType(NoticeType.INFO.getId());
        attr.setTrapNumber(TrapType.FE_CONNECTION_ESTABLISH.getId());
        bean.setAttributes(attr);
        bean.setData(new byte[0]);
        bean.setIssuerGID(new GIDGlobal());
        bean.setClassData(new byte[0]);
        return bean;
    }

    private NoticeBean createConnLostNotice() {
        NoticeBean bean = new NoticeBean(true);
        GenericNoticeAttrBean attr = new GenericNoticeAttrBean();
        attr.setGeneric(true);
        attr.setType(NoticeType.FATAL.getId());
        attr.setTrapNumber(TrapType.FE_CONNECTION_LOST.getId());
        bean.setAttributes(attr);
        bean.setData(new byte[0]);
        bean.setIssuerGID(new GIDGlobal());
        bean.setClassData(new byte[0]);
        return bean;
    }

    protected class PendingCommand {
        private final ICommand<?, ?> command;

        private final Connection connection;

        PendingCommand(ICommand<?, ?> cmd, Connection conn) {
            this.command = cmd;
            this.connection = conn;
        }

        public ICommand<?, ?> getCommand() {
            return command;
        }

        public Connection getConnection() {
            return connection;
        }
    }

    private class ConnectionTimeout extends Connection {

        private final TimeoutException toe;

        public ConnectionTimeout(TimeoutException toe) {
            super(null, null, null);
            this.toe = toe;
        }

        @Override
        public Void handle() throws Exception {
            throw toe;
        }

    }

    // For testing
    protected List<Connection> getConnectionPool() {
        return connPool;
    }

    protected IPoolingPolicy<Connection> getPoolingPolicy() {
        return poolingPolicy;
    }

}
