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

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLEngineResult.Status;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.HostInfo;

public class SecureConnection extends Connection {

    protected static Logger log = LoggerFactory
            .getLogger(SecureConnection.class);

    public static final String[] CIPHER_LIST =
            { "TLS_ECDHE_ECDSA_WITH_AES_128_CBC_SHA256" };

    public static final long TIME_OUT = 30000; // 30 sec

    private int netBufferSize;

    private int appBufferSize;

    private ByteBuffer myNetData;

    private ByteBuffer peerNetData;

    private ByteBuffer myAppData;

    private ByteBuffer peerAppData;

    private SSLSession sslSession;

    private HandshakeStatus handshakeStatus = HandshakeStatus.NOT_HANDSHAKING;

    private SSLEngine sslEngine;

    public SecureConnection(HostInfo hostInfo, SocketChannel channel,
            IConnectionEventListener listener) {
        super(hostInfo, channel, listener);
    }

    @Override
    protected void initialize(Selector selector, IRequestDispatcher dispatcher)
            throws Exception {
        this.sslEngine = dispatcher.getSSLEngine(hostInfo);
        // Initialize the parent after the SSLEngine is created because
        // getSSLEngine might result on a UI request that could take a long
        // time. Connection starts a timeout thread to protect itself against a
        // non-responsive server
        super.initialize(selector, dispatcher);

        this.sslSession = sslEngine.getSession();
        netBufferSize = sslSession.getPacketBufferSize();
        appBufferSize = sslSession.getApplicationBufferSize();

        myNetData = ByteBuffer.allocate(netBufferSize);
        peerNetData = ByteBuffer.allocate(netBufferSize);

        myAppData = ByteBuffer.allocate(appBufferSize);
        peerAppData = ByteBuffer.allocate(appBufferSize);
    }

    @Override
    protected void onConnectionComplete() throws IOException {
        sslEngine.setEnabledCipherSuites(CIPHER_LIST);
        sslEngine.beginHandshake();
        handshakeStatus = sslEngine.getHandshakeStatus();
        peerNetData.flip();
        peerAppData.flip();
        myNetData.clear();
        initialHandshake = true;
        handshake();
    }

    @Override
    protected void handleWrite() throws IOException {
        turnOpsOff(SelectionKey.OP_WRITE);
        if (flush()) {
            if (initialHandshake) {
                handshake();
            } else if (closing) {
                close();
            } else {
                super.handleWrite();
            }
        }
    }

    @Override
    protected void handleRead() throws IOException {
        if (initialHandshake) {
            handshake();
        } else if (closing) {
            close();
        } else {
            super.handleRead();
        }
    }

    /*-
     * NIO operations should be handled thru state machines. In SSL, the byte
     * buffers used to handle the SSL protocol exchange are part of the state
     * machine. On entry to readBuffer, this is the expected state of
     * peerNetData and peerAppData:
     *  - peerNetData should be ready to be written to; that is, the position
     *    in the buffer should be after all the bytes that have not been
     *    processed yet by the SSLEngine (unwrapped) and the limit set to
     *    capacity.
     *  - peerAppData should be ready to be read from; that is, the position
     *    should be at the first byte that has been processed by the SSLEngine
     *    and the limit set to the number of bytes unwrapped. 
     */
    @Override
    protected int readBuffer(ByteBuffer dst) throws IOException {
        debugBuffers("in", dst);
        int pos = dst.position();

        // read from peerAppData if we have left over data in it
        if (peerAppData.hasRemaining()) {
            putData(dst);
        }

        if (!dst.hasRemaining()) {
            int read = dst.position() - pos;
            log.debug("Read {} bytes from peerAppData", read);
            return read;
        }

        // Ready peerAppData to get more data
        peerAppData.compact();
        int len = read();
        while (len > 0 && dst.hasRemaining()) {
            Status status;
            boolean hasRemainder;
            // Process all TLS records just read (there could one or more, not
            // necessarily related to this particular request
            do {
                peerNetData.flip(); // Ready to read from
                SSLEngineResult result =
                        sslEngine.unwrap(peerNetData, peerAppData);
                debugHandshake("readBuffer: ", result);
                hasRemainder = peerNetData.hasRemaining();
                peerNetData.compact(); // Ready to put to
                status = result.getStatus();
                switch (status) {
                    case OK:
                        if (dst.hasRemaining()) {
                            peerAppData.flip(); // Ready to read from
                            putData(dst);
                            peerAppData.compact(); // Ready to put to
                        }
                        if (dst.hasRemaining()) {
                            // If the destination buffer has still room;
                            // peerAppData has been depleted and we can attempt
                            // another read from the network
                            len = read();
                            hasRemainder = (peerNetData.position() > 0);
                        }
                        break;
                    case BUFFER_UNDERFLOW:
                        // The TLS record is incomplete: check first if we need
                        // a bigger buffer size and then attempt to read more
                        // from the network
                        int netBuffSize =
                                sslEngine.getSession().getPacketBufferSize();
                        if (netBuffSize > peerNetData.capacity()) {
                            peerNetData =
                                    resizeBuffer(peerNetData, netBuffSize);
                            if (log.isDebugEnabled()) {
                                String peerNetDataState =
                                        buffer2String(peerNetData);
                                log.debug("peerNetData resized: {}",
                                        peerNetDataState);
                            }
                        }
                        len = read();
                        hasRemainder = (len > 0);
                        status = Status.OK;
                        break;
                    case BUFFER_OVERFLOW:
                        checkNetAppBufferSize();
                        break;
                    case CLOSED:
                        throw new ClosedChannelException();
                }
                if (result.getHandshakeStatus() != HandshakeStatus.NOT_HANDSHAKING) {
                    handshake();
                }
            } while (hasRemainder && status == Status.OK);

        }
        if (len == -1) {
            try {
                sslEngine.closeInbound(); // probably throws exception
            } catch (SSLException se) {
                // This condition happens when the remote host unexpectedly
                // closes the socket.
                ClosedChannelException cce = new ClosedChannelException();
                cce.initCause(se);
                throw cce;
            }
        }
        if (dst.hasRemaining()) {
            turnOpsOn(SelectionKey.OP_READ);
        }
        // Ready peerAppData to be read from
        peerAppData.flip();
        debugBuffers("out", dst);
        return (dst.position() - pos);
    }

    private void putData(ByteBuffer dst) {
        if (log.isDebugEnabled()) {
            String peerAppDataState = buffer2String(peerAppData);
            String dstState = buffer2String(dst);
            log.debug("Putting data. peerAppData: {}; dst: {}",
                    peerAppDataState, dstState);
        }
        if (peerAppData.remaining() > dst.remaining()) {
            int limit = dst.remaining();
            for (int i = 0; i < limit; i++) {
                dst.put(peerAppData.get());
            }
        } else {
            dst.put(peerAppData);
        }
    }

    @Override
    public long writeBuffers(ByteBuffer[] appBuffers) throws IOException {
        int retValue = 0;

        if (!flush()) {
            return retValue;
        }

        /*
         * The data buffer is empty, we can reuse the entire buffer.
         */
        myNetData.clear();

        SSLEngineResult result = sslEngine.wrap(appBuffers, myNetData);
        debugHandshake("writeBuffers: ", result);
        retValue = result.bytesConsumed();

        switch (result.getStatus()) {
            case OK:
                myNetData.flip();
                flush();
                if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
                    doTasks();
                }
                break;
            default:
                throw new IOException("sslEngine error during data write: "
                        + result.getStatus());
        }

        return retValue;
    }

    @Override
    public void close() throws IOException {
        closing = true;
        myAppData.clear();
        myAppData.flip();
        myNetData.compact();
        sslEngine.closeOutbound();
        SSLEngineResult result = null;
        while (!sslEngine.isOutboundDone()) {
            result = sslEngine.wrap(myAppData, myNetData);
            debugHandshake("CLOSE: ", result);
            myNetData.flip();
            while (myNetData.hasRemaining()) {
                int written = channel.write(myNetData);
                if (written == -1) {
                    // Channel has been closed
                    super.close();
                    return;
                }
            }
            myNetData.compact();
        }
        super.close();
    }

    private void handshake() throws IOException {
        SSLEngineResult result;
        while (true) {
            if (log.isDebugEnabled()) {
                String peerNetDataState = buffer2String(peerNetData);
                String peerAppDataState = buffer2String(peerAppData);
                String myNetDataState = buffer2String(myNetData);
                String myAppDataState = buffer2String(myAppData);
                log.debug(
                        "Handshake status: {}; peerNetData: {}; peerAppData: {}; myAppData: {}; myNetData: {}",
                        handshakeStatus.toString(), peerNetDataState,
                        peerAppDataState, myAppDataState, myNetDataState);
            }
            switch (handshakeStatus) {
                case NEED_UNWRAP:
                    if (!peerNetData.hasRemaining()) {
                        peerNetData.compact();
                        int read = read();
                        peerNetData.flip();
                        if (read == 0) {
                            turnOpsOn(SelectionKey.OP_READ);
                            turnOpsOff(SelectionKey.OP_WRITE);
                            return;
                        }
                    }
                    peerAppData.compact();
                    result = sslEngine.unwrap(peerNetData, peerAppData);
                    peerAppData.flip();
                    handshakeStatus = result.getHandshakeStatus();
                    debugHandshake("NEED_UNWRAP: ", result);
                    switch (result.getStatus()) {
                        case OK:
                            break;
                        case BUFFER_UNDERFLOW:
                            peerNetData.compact();
                            int read = read();
                            if (read > 0) {
                                peerNetData.flip();
                            } else if (read == 0) {
                                peerNetData.flip();
                                turnOpsOn(SelectionKey.OP_READ);
                                turnOpsOff(SelectionKey.OP_WRITE);
                                return;
                            } else {
                                sslEngine.closeInbound();
                                log.info("Not receiving any more data");
                            }
                            break;
                        case BUFFER_OVERFLOW:
                            checkNetAppBufferSize();
                            break;
                        case CLOSED:
                            throw new ClosedChannelException();
                    }
                    break;
                case NEED_WRAP:
                    // Encrypt handshaking data
                    if (!myNetData.hasRemaining()) {
                        myNetData.compact();
                    }
                    result = sslEngine.wrap(myAppData, myNetData);
                    handshakeStatus = result.getHandshakeStatus();
                    debugHandshake("NEED_WRAP: ", result);
                    switch (result.getStatus()) {
                        case OK:
                            myNetData.flip();
                            if (!flush()) {
                                // There are more data to be sent (next time
                                // this handler is dispatched)
                                return;
                            }
                            break;
                        case BUFFER_OVERFLOW:
                            if (!flush()) {
                                // There are more data to be sent (next time
                                // this handler is dispatched)
                                return;
                            }
                            myNetData.clear();
                            break;
                        default: // BUFFER_UNDERFLOW/CLOSED:
                            throw new IOException("Received"
                                    + result.getStatus()
                                    + "during initial handshaking");
                    }
                    break;
                case NEED_TASK:
                    handshakeStatus = doTasks();
                    break;
                case FINISHED:
                    if (myNetData.hasRemaining()) {
                        flush();
                    }
                    boolean handshakeComplete = !myNetData.hasRemaining();
                    log.debug("FINISHED: " + handshakeComplete);
                    if (handshakeComplete) {
                        if (initialHandshake) {
                            completeInitialHandshake();
                        }
                    } else {
                        turnOpsOn(SelectionKey.OP_WRITE);
                    }
                    return;
                case NOT_HANDSHAKING:
                    log.debug("Handshake is in NOT_HANDSHAKING state");
                    return;
            }
        }

    }

    private int read() throws IOException {
        int read = channel.read(peerNetData);
        if (log.isDebugEnabled()) {
            String peerNetDataState = buffer2String(peerNetData);
            log.debug("Read {} bytes from socket. peerNetData: {}", read,
                    peerNetDataState);
        }
        return read;
    }

    private void completeInitialHandshake() throws IOException {
        initialHandshake = false;
        peerNetData.compact();
        // This flip should leave peerAppData with no
        // remaining bytes so that the first application
        // read does not try to read any leftover from
        // handshake
        peerAppData.flip();
        super.onConnectionComplete();
    }

    private boolean flush() throws IOException {
        if (myNetData.hasRemaining()) {
            int written = channel.write(myNetData);
            if (log.isDebugEnabled()) {
                String myNetDataState = buffer2String(myNetData);
                log.debug("Written {} bytes to socket; myNetData: {}", written,
                        myNetDataState);
            }
            if (myNetData.hasRemaining()) {
                turnOpsOn(SelectionKey.OP_WRITE);
                return false;
            }
        }
        return true;
    }

    /*
     * Do all the outstanding handshake tasks.
     */
    private HandshakeStatus doTasks() {
        Runnable runnable;

        while ((runnable = sslEngine.getDelegatedTask()) != null) {
            runnable.run();
        }

        return sslEngine.getHandshakeStatus();
    }

    private void checkNetAppBufferSize() {
        int appBuffSize = sslEngine.getSession().getApplicationBufferSize();
        int newSize =
                ((peerAppData.capacity() / appBuffSize) + 1) * appBuffSize;
        peerAppData = resizeBuffer(peerAppData, newSize);
        if (log.isDebugEnabled()) {
            String peerAppDataState = buffer2String(peerAppData);
            log.debug("peerAppData resized: {}", peerAppDataState);
        }
    }

    private ByteBuffer resizeBuffer(ByteBuffer buffer, int newSize) {
        ByteBuffer bb = ByteBuffer.allocate(newSize);
        buffer.flip();
        bb.put(buffer);
        return bb;
    }

    private void debugHandshake(String label, SSLEngineResult result) {
        if (log.isDebugEnabled()) {
            log.debug(label + result.toString());
        }
    }

    /*
     * The ByteBuffer.toString() calls are included to take a snapshot at the
     * time off logging. Passing the ByteBuffers just as parameters may result
     * in incongruent results due to the asynchronous nature of logging
     */
    private void debugBuffers(String label, ByteBuffer dst) {
        if (log.isDebugEnabled()) {
            String peerNetDataState = buffer2String(peerNetData);
            String peerAppDataState = buffer2String(peerAppData);
            String dstState = buffer2String(dst);
            log.debug(
                    "readBuffer {}: peerNetData: {}; peerAppData: {}; dst: {}",
                    label, peerNetDataState, peerAppDataState, dstState);
        }
    }

}
