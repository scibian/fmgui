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

import static com.intel.stl.common.STLMessages.STL20003_CONNECTION_TIMEOUT;
import static com.intel.stl.common.STLMessages.STL20004_SM_UNAVAILABLE;
import static com.intel.stl.common.STLMessages.STL20005_PM_UNAVAILABLE;
import static com.intel.stl.common.STLMessages.STL20006_CHANNEL_CLOSED;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.MadException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.common.Constants;
import com.intel.stl.fecdriver.ICommand;
import com.intel.stl.fecdriver.IResponse;
import com.intel.stl.fecdriver.messages.adapter.CommonMad;
import com.intel.stl.fecdriver.messages.adapter.NetHeader;
import com.intel.stl.fecdriver.messages.adapter.NetPacket;
import com.intel.stl.fecdriver.messages.adapter.OobPacket;
import com.intel.stl.fecdriver.messages.adapter.RmppMad;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;
import com.intel.stl.fecdriver.messages.response.sa.FVRspNotice;
import com.intel.stl.fecdriver.session.RequestCancelledByUserException;

/**
 * Connection is the handler for FE-related events. It is a state machine that
 * it's driven by the dispatcher and keeps its state in relation to events
 * coming from the application (new commands) and from the FE (responses).
 * 
 * It handles a SocketChannel that is registered to a Selector in the dispatcher
 * through a SelectionKey. This SelectionKey is used only for read-only, to
 * drive processing of a event; it should never be modified by this handler for
 * it is the link between the dispatcher and this handler.
 */
public class Connection extends Handler<Void> implements IConnection {
    private static final int DEFAULT_IN_BUFFER_SIZE = 16921;

    protected static int CONN_TIMEOUT = 30000; // 30 secs

    protected static Logger log = LoggerFactory.getLogger(Connection.class);

    private final NetHeader netHeader;

    private ByteBuffer inBuffer;

    private ByteBuffer[] outBuffers = null;

    private Thread connectionTimeoutThread = null;

    protected long outRemaining;

    private int inRemaining;

    protected int interestOps;

    private boolean isTemporaryConnection = false;

    protected boolean connected = false;

    protected boolean initialHandshake = false;

    protected boolean closing = false;

    protected boolean processing = false;

    protected SelectionKey selectionKey;

    private final List<IEventListener<NoticeBean>> noticeListeners =
            new CopyOnWriteArrayList<IEventListener<NoticeBean>>();

    private final ConcurrentLinkedQueue<OobPacket> pendingPackets =
            new ConcurrentLinkedQueue<OobPacket>();

    private final ConcurrentHashMap<Long, ICommand<?, ?>> pendingCmds =
            new ConcurrentHashMap<Long, ICommand<?, ?>>();

    protected final IConnectionEventListener listener;

    protected final SocketChannel channel;

    protected final HostInfo hostInfo;

    public Connection(HostInfo hostInfo, SocketChannel channel,
            IConnectionEventListener listener) {
        this.hostInfo = hostInfo;
        this.channel = channel;
        this.listener = listener;
        netHeader = new NetHeader();
        netHeader.build(true);
        inBuffer = ByteBuffer.allocate(DEFAULT_IN_BUFFER_SIZE);
        inBuffer.limit(0);
        inRemaining = 0;
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    @Override
    public void close() throws IOException {
        closing = true;
        if (connectionTimeoutThread != null) {
            connectionTimeoutThread.interrupt();
            connectionTimeoutThread = null;
        }
        if (selectionKey != null) {
            selectionKey.cancel();
        }
        if (channel != null) {
            channel.close();
        }
    }

    public HostInfo getHostInfo() {
        return hostInfo;
    }

    public boolean isClosing() {
        return closing;
    }

    protected void setProcessing(boolean processing) {
        this.processing = processing;
    }

    protected boolean isProcessing() {
        return processing;
    }

    protected void initialize(Selector selector, IRequestDispatcher dispatcher)
            throws Exception {
        try {
            String host = hostInfo.getHost();
            int port = hostInfo.getPort();
            InetSocketAddress feAddress = new InetSocketAddress(host, port);

            // Here we should request user sign in information (when supported)
            // through the adapter

            channel.configureBlocking(false);
            this.selectionKey =
                    channel.register(selector, SelectionKey.OP_CONNECT, this);
            log.debug("Connection {} is connecting to {}", this, feAddress);
            channel.connect(feAddress);
            startConnTimeoutThread(dispatcher);
        } catch (Exception e) {
            log.error("Error initializing connection {}", this, e);
            notifyError(e);
            throw e;
        }
    }

    protected void assignCmd(ICommand<?, ?> cmd) {
        cmd.setConnectionInProgress(!connected);
        long id = cmd.getMessageID();
        OobPacket packet = cmd.getPacket();
        pendingCmds.putIfAbsent(id, cmd);
        pendingPackets.add(packet);
    }

    protected SelectionKey getSelectionKey() {
        return selectionKey;
    }

    protected Collection<ICommand<?, ?>> getPendingCommands() {
        return pendingCmds.values();
    }

    @Override
    protected Void handle() throws Exception {
        if (log.isDebugEnabled() && selectionKey.isValid()) {
            String inBufferState = buffer2String(inBuffer);
            log.debug(
                    "Handle connection {}. ReadyOps: {}; inBuffer: {}; outRemaining: {}",
                    this, selectionKey.readyOps(), inBufferState, outRemaining);
        }
        // Since the ready operations are cumulative,
        // need to check readiness for each operation
        if (selectionKey.isValid() && selectionKey.isConnectable()) {
            try {
                handleConnectionFinish();
            } catch (Exception e) {
                notifyError(e);
                throw e;
            }
        }
        if (selectionKey.isValid() && selectionKey.isReadable()) {
            processRead();
        }
        if (selectionKey.isValid() && selectionKey.isWritable()) {
            processWrite();
        }
        return null;
    }

    private void processRead() throws Exception {
        turnOpsOff(SelectionKey.OP_READ);
        try {
            handleRead();
        } catch (Exception e) {
            notifyError(e);
            throw e;
        } finally {
            turnOpsOn(SelectionKey.OP_READ);
        }
    }

    private void processWrite() throws Exception {
        try {
            handleWrite();
        } catch (Exception e) {
            notifyError(e);
            throw e;
        } finally {
            if (outRemaining == 0 && !arePacketsPending()) {
                turnOpsOn(SelectionKey.OP_READ);
                turnOpsOff(SelectionKey.OP_WRITE);
            } else {
                turnOpsOn(SelectionKey.OP_WRITE);
            }
        }
    }

    protected void handleRead() throws IOException {
        int len;
        do {
            if (inRemaining > 0) {
                len = readBuffer(inBuffer);
                inRemaining = inRemaining - len;
                log.debug("Read {}, remaining {}. ", len, inRemaining);
            } else {
                ByteBuffer netHeaderBuff = netHeader.getByteBuffer();
                if (!netHeaderBuff.hasRemaining()) {
                    netHeaderBuff.clear();
                }
                len = readBuffer(netHeaderBuff);
                log.debug("Read NetHeader: len = {} isValid = {}.", len,
                        netHeader.isValid());
                if (netHeaderBuff.hasRemaining()) {
                    return; // keep reading until we get the full netHeader
                }
                if (netHeader.isValid()) {
                    inBuffer.clear();
                    inRemaining =
                            netHeader.getMsgLength() - netHeader.getLength();
                    if (inRemaining > inBuffer.capacity()) {
                        inBuffer = ByteBuffer.allocate(inRemaining);
                        log.info("Increased input buffer size to {}.",
                                inRemaining);
                    } else {
                        inBuffer.clear();
                        inBuffer.limit(inRemaining);
                    }
                    len = readBuffer(inBuffer);
                    inRemaining = inRemaining - len;
                    log.debug("Read {}, remaining {}.", len, inRemaining);
                } else {
                    log.error("NetHeader is invalid: {} ",
                            netHeader.toString(""));
                    invalidateAllCmds();
                }
            }
            if (inRemaining == 0) {
                processResponse();
            }
        } while (len > 0 && pendingCmds.size() > 0);
    }

    protected void handleWrite() throws IOException {
        if (outRemaining == 0) {
            if (arePacketsPending()) {
                OobPacket packet = getNextPacket();
                if (packet == null) {
                    log.warn("packet is null!");
                    return;
                }
                if (log.isDebugEnabled()) {
                    log.debug("Sending request with message id '{}' ", packet
                            .getRmppMad().getCommonMad().getTransactionId());
                }

                NetPacket netPacket = new NetPacket();
                netPacket.build(true);
                netPacket.setData(packet);
                outBuffers = netPacket.getByteBuffers();
                for (ByteBuffer buffer : outBuffers) {
                    buffer.clear();
                    outRemaining += buffer.capacity();
                }

                if (log.isTraceEnabled()) {
                    netPacket.dump("", System.out);
                }
                long len = writeBuffers(outBuffers);
                outRemaining -= len;
                log.debug("Write {}, remaining {}.", len, outRemaining);
            }
        } else {
            long len = writeBuffers(outBuffers);
            outRemaining -= len;
            log.debug("Write {}, remaining {}.", len, outRemaining);
        }
    }

    private void handleConnectionFinish() throws Exception {
        if (channel.isConnectionPending()) {
            boolean success = channel.finishConnect();
            if (success) {
                turnOpsOff(SelectionKey.OP_CONNECT);
                onConnectionComplete();
            } else {
                turnOpsOn(SelectionKey.OP_CONNECT);
            }
        }
    }

    protected void onConnectionComplete() throws IOException {
        // At this point, the socket is connected; if a secure connection,
        // handshake is complete. When implementing signon to the FM, here is
        // where it should be inserted.
        this.connected = true;
        outRemaining = 0;
        for (ICommand<?, ?> cmd : pendingCmds.values()) {
            cmd.setConnectionInProgress(false);
        }
        log.info("Connection {} is now connected to FE at {}", this,
                hostInfo.toString());
        stopConnTimeoutThread();
        if (listener != null) {
            try {
                listener.onConnectionComplete(this);
            } catch (Exception e) {
                log.error("Connection event listener had an exception", e);
            }
        }
        if (arePacketsPending()) {
            try {
                processWrite();
            } catch (IOException e) {
                throw e;
            } catch (Exception e) {
                if (e instanceof RuntimeException) {
                    RuntimeException rte = (RuntimeException) e;
                    throw rte;
                } else {
                    // This cannot be but just in case
                    e.printStackTrace();
                }
            }
        }
    }

    protected void notifyError(Exception exception) {
        if (listener != null) {
            try {
                listener.onConnectionError(this, exception);
            } catch (Exception e) {
                log.error("Connection event listener had an exception", e);
            }
        }
    }

    protected int getInterestOps() {
        return interestOps;
    }

    protected void setInterestOps(int ops) {
        this.interestOps = ops;
    }

    protected void turnOpsOn(int opsOn) {
        interestOps = interestOps | opsOn;
    }

    protected void turnOpsOff(int opsOff) {
        interestOps = interestOps & (~opsOff);
    }

    protected boolean arePacketsPending() {
        return !pendingPackets.isEmpty();
    }

    protected boolean areRepliesPending() {
        return !pendingCmds.isEmpty();
    }

    protected OobPacket getNextPacket() {
        OobPacket packet = null;
        while (!pendingPackets.isEmpty()) {
            packet = pendingPackets.poll();
            long msgId = packet.getRmppMad().getCommonMad().getTransactionId();
            if (packet.getExpireTime() > System.currentTimeMillis()
                    && pendingCmds.containsKey(msgId)) {
                break;
            }
            log.info("Ignore packet id={} expire={} {}", msgId,
                    packet.getExpireTime(), (pendingCmds.containsKey(msgId)));
        }
        return packet;
    }

    protected void processResponse() throws IOException {
        int limit = inBuffer.limit();
        if (limit == 0) {
            // This sometimes happens after initial connection; we safeguard
            // here
            return;
        }
        byte[] bytes = inBuffer.array();
        OobPacket packet = new OobPacket();
        int offset = inBuffer.arrayOffset();
        int pos = packet.wrap(bytes, offset);

        RmppMad mad = new RmppMad();
        pos = mad.wrap(bytes, pos);
        int remainingBytes = offset + limit - pos;
        CommonMad comMad = mad.getCommonMad();
        long transId = comMad.getTransactionId();
        short attrId = comMad.getAttributeID();
        if (attrId == SAConstants.STL_SA_ATTR_NOTICE) {
            byte[] noticeBytes =
                    Arrays.copyOfRange(bytes, pos, pos + remainingBytes);
            setRmppData(mad, noticeBytes, 0, remainingBytes);
            FVRspNotice response = new FVRspNotice();
            response.processMad(mad);
            fireNotice(response.get().toArray(new NoticeBean[0]));
        } else {
            setRmppData(mad, bytes, pos, remainingBytes);
            ICommand<?, ?> cmd = pendingCmds.remove(transId);
            if (cmd != null && cmd.getResponse() != null) {
                IResponse<?> response = cmd.getResponse();
                short status = comMad.getNSStatus();
                if (status != Constants.MAD_STATUS_SUCCESS) {
                    if (status == Constants.MAD_STATUS_SM_UNAVAILABLE) {
                        String emsg = STL20004_SM_UNAVAILABLE.getDescription();
                        ClosedChannelException cce =
                                processSMUnavailable(cmd, transId, emsg);
                        throw cce;
                    } else if (status == Constants.MAD_STATUS_PM_UNAVAILABLE) {
                        String emsg = STL20005_PM_UNAVAILABLE.getDescription();
                        ClosedChannelException cce =
                                processSMUnavailable(cmd, transId, emsg);
                        throw cce;
                    }
                    MadException madException =
                            new MadException(response.getClass(), attrId,
                                    status, response.getDescription());
                    response.setError(madException);
                    log.error(madException.getMessage());
                } else {
                    log.debug("Replying to request with message id '{}'",
                            transId);
                    response.processMad(mad);
                    Exception error = response.getError();
                    if (error != null) {
                        if (error instanceof RequestCancelledByUserException) {
                            log.warn(
                                    "Response {} for message id '{}' cancelled by user",
                                    response.getClass().getSimpleName(),
                                    transId);
                        } else {
                            log.error(
                                    "Failed processing data for response {} with message id '{}'",
                                    response.getClass().getSimpleName(),
                                    transId, error);
                        }
                    }
                }
            } else {
                // Ignore response already cancelled
                // mad.dump("", System.out);
                log.error(
                        "No response found for mad associated with message id '{}'; AttributeID={}",
                        transId, StringUtils.shortHexString(attrId));
            }
        }
    }

    private ClosedChannelException processSMUnavailable(ICommand<?, ?> cmd,
            long transId, String emsg) {
        ClosedChannelException cce = new ClosedChannelException();
        IOException ioe = new IOException(emsg);
        cce.initCause(ioe);
        IResponse<?> response = cmd.getResponse();
        if (isTemporaryConnection) {
            response.setError(ioe);
        } else {
            // Put the command back as pending so that it gets reprocessed after
            // failover
            pendingCmds.put(transId, cmd);
        }
        log.debug(emsg, cce);
        return cce;
    }

    protected boolean isTemporaryConnection() {
        return isTemporaryConnection;
    }

    protected void setTemporaryConnection(boolean isTemporaryConnection) {
        this.isTemporaryConnection = isTemporaryConnection;
    }

    protected int readBuffer(ByteBuffer appBuffer) throws IOException {
        int len = read(appBuffer);
        int read = len;
        while (len > 0 && appBuffer.hasRemaining()) {
            len = read(appBuffer);
            read = read + len;
        }
        return read;
    }

    private int read(ByteBuffer appBuffer) throws IOException {
        int len = channel.read(appBuffer);
        if (log.isDebugEnabled()) {
            String appBufferState = buffer2String(appBuffer);
            log.debug("Read {} bytes from socket. appBuffer: {}", len,
                    appBufferState);
        }
        if (len == -1) {
            ClosedChannelException cce = new ClosedChannelException();
            String emsg = STL20006_CHANNEL_CLOSED.getDescription();
            IOException ioe = new IOException(emsg);
            cce.initCause(ioe);
            throw cce;
        }
        return len;
    }

    private void setRmppData(RmppMad mad, byte[] bytes, int pos, int size) {
        SimpleDatagram<Void> data = new SimpleDatagram<Void>(size);
        data.wrap(bytes, pos);
        mad.setData(data);
        if (log.isTraceEnabled()) {
            String formatted =
                    mad.toString(Thread.currentThread().getName() + " ");
            log.trace(formatted);
        }
        return;
    }

    protected long writeBuffers(ByteBuffer[] appBuffers) throws IOException {
        return channel.write(appBuffers);
    }

    protected void addNoticeListener(IEventListener<NoticeBean> listener) {
        noticeListeners.add(listener);
    }

    protected void removeNoticeListener(IEventListener<NoticeBean> listener) {
        noticeListeners.add(listener);
    }

    protected void fireNotice(NoticeBean[] notices) {
        log.info("Fire {} notices: {}", notices.length,
                Arrays.toString(notices));
        for (IEventListener<NoticeBean> listener : noticeListeners) {
            try {
                listener.onNewEvent(notices);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void invalidateAllCmds() throws IOException {
        IOException ioe = new IOException("Illegal Data");
        for (ICommand<?, ?> cmd : pendingCmds.values()) {
            IResponse<?> resp = cmd.getResponse();
            resp.setError(ioe);
        }
        throw ioe;
    }

    private void startConnTimeoutThread(final IRequestDispatcher dispatcher) {
        if (connectionTimeoutThread == null) {
            connectionTimeoutThread = new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(CONN_TIMEOUT);
                        String hostName = hostInfo.getHost();
                        log.info("Timeout attempting to connect to host {}:{}",
                                hostName, hostInfo.getPort());
                        TimeoutException toe =
                                new TimeoutException(
                                        STL20003_CONNECTION_TIMEOUT
                                                .getDescription(hostName));
                        // This will start failover
                        dispatcher.onRequestTimeout(toe);
                        notifyError(toe);
                    } catch (InterruptedException e) {
                    }

                }
            });
            String connName = toString();
            int x = connName.indexOf("@");
            connName = connName.substring(x + 1);
            connectionTimeoutThread.setName(hostInfo.getHost() + "-" + connName
                    + "-conntimeout");
            connectionTimeoutThread.start();
        }
    }

    private void stopConnTimeoutThread() {
        if (connectionTimeoutThread != null) {
            connectionTimeoutThread.interrupt();
            connectionTimeoutThread = null;
        }
    }

    protected String buffer2String(ByteBuffer buffer) {
        String buffState =
                "[pos=" + buffer.position() + " lim=" + buffer.limit()
                        + " cap=" + buffer.capacity() + "]";
        return buffState;
    }

    // For testting
    protected SocketChannel getSocketChannel() {
        return channel;
    }

    protected void setSelectionKey(SelectionKey selectionKey) {
        this.selectionKey = selectionKey;
    }

    protected ConcurrentHashMap<Long, ICommand<?, ?>> getPendingCmds() {
        return pendingCmds;
    }

}
