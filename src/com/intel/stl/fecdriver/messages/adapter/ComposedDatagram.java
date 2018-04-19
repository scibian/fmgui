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
package com.intel.stl.fecdriver.messages.adapter;

import java.io.PrintStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.List;

/**
 */
public class ComposedDatagram<E> implements IDatagram<E> {
    private int length = 0;

    private List<ByteBuffer> buffers = new ArrayList<ByteBuffer>();

    private ByteOrder order = null;

    private boolean hasConsistentOrder = true;

    private List<IDatagram<?>> datagrams = new ArrayList<IDatagram<?>>();

    private boolean dirty = true;

    public ComposedDatagram() {
    }

    public ComposedDatagram(IDatagram<?>... datagrams) {
        for (IDatagram<?> datagram : datagrams) {
            this.datagrams.add(datagram);
        }
    }

    @Override
    public int build(boolean force) {
        int len = 0;
        for (IDatagram<?> datagram : datagrams) {
            len += datagram.build(force);
        }
        if (len > 0)
            refresh();
        return getLength();
    }

    @Override
    public int wrap(byte[] data, int offset) {
        int pos = offset;
        for (IDatagram<?> datagram : datagrams) {
            pos = datagram.wrap(data, pos);
        }
        refresh();
        return pos;
    }

    /**
     * NOTE: if a sub ComposedDatagram changed, it's the user's responsibility
     * to call this method to ensure parent ComposedDatagram has correct
     * information TODO: add listener to automatically do this
     */
    public void refresh() {
        length = 0;
        buffers.clear();
        order = null;

        for (IDatagram<?> datagram : datagrams) {
            if (!datagram.hasBuffer())
                throw new IllegalArgumentException(
                        "Datagram has no buffer! Please initialize it with #build or #wrap method first.");

            length += datagram.getLength();

            ByteBuffer[] localBuffers = datagram.getByteBuffers();
            if (localBuffers != null) {
                for (ByteBuffer buffer : localBuffers) {
                    buffers.add(buffer);
                }
            }

            if ((datagram instanceof ComposedDatagram)
                    && !((ComposedDatagram<?>) datagram).hasConsistentOrder) {
                hasConsistentOrder = false;
                continue;
            }

            if (order == null)
                order = datagram.getByteOrder();
            else if (hasConsistentOrder) {
                if (order != datagram.getByteOrder()) {
                    hasConsistentOrder = false;
                }
            }
        }

        dirty = false;
    }

    public List<IDatagram<?>> getDatagrams() {
        return datagrams;
    }

    public void addDatagram(IDatagram<?> datagram) {
        if (datagram == null)
            return;

        datagrams.add(datagram);
        if (!datagram.hasBuffer()) {
            dirty = true;
            return;
        }

        length += datagram.getLength();

        ByteBuffer[] localBuffers = datagram.getByteBuffers();
        if (localBuffers != null) {
            for (ByteBuffer buffer : localBuffers) {
                buffers.add(buffer);
            }
        }

        if ((datagram instanceof ComposedDatagram)
                && !((ComposedDatagram<?>) datagram).hasConsistentOrder) {
            hasConsistentOrder = false;
            return;
        }

        if (order == null)
            order = datagram.getByteOrder();
        else if (hasConsistentOrder) {
            if (order != datagram.getByteOrder()) {
                hasConsistentOrder = false;
            }
        }
    }

    public void removeDatagram(IDatagram<?> datagram) {
        if (datagram == null)
            return;

        if (!datagrams.remove(datagram))
            return;
        if (!datagram.hasBuffer()) {
            dirty = true;
            return;
        }

        length -= datagram.getLength();

        ByteBuffer[] localBuffers = datagram.getByteBuffers();
        if (localBuffers != null) {
            for (ByteBuffer buffer : localBuffers) {
                buffers.remove(buffer);
            }
        }

        if (!hasConsistentOrder) {
            order = null;
            hasConsistentOrder = true;
            for (IDatagram<?> dg : datagrams) {
                if ((datagram instanceof ComposedDatagram)
                        && !((ComposedDatagram<?>) datagram).hasConsistentOrder) {
                    hasConsistentOrder = false;
                    break;
                }

                if (order == null)
                    order = dg.getByteOrder();
                else {
                    if (order != dg.getByteOrder()) {
                        hasConsistentOrder = false;
                        break;
                    }
                }
            }
        }
    }

    @Override
    public ByteBuffer[] getByteBuffers() {
        if (dirty)
            refresh();

        return buffers.toArray(new ByteBuffer[0]);
    }

    @Override
    public int getLength() {
        if (dirty)
            refresh();

        return length;
    }

    @Override
    public ByteOrder getByteOrder() {
        if (dirty)
            refresh();

        if (hasConsistentOrder)
            return order;
        else
            throw new RuntimeException("No consistent ByteOrder");
    }

    @Override
    public boolean hasBuffer() {
        return !dirty;
    }

    @Override
    public void dump(String prefix, PrintStream out) {
        out.println(prefix + getClass().getSimpleName());
        for (IDatagram<?> datagram : datagrams) {
            datagram.dump(prefix + "  ", out);
        }
    }

    @Override
    public String toString(String prefix) {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(prefix);
        strBuff.append(getClass().getSimpleName());
        for (IDatagram<?> datagram : datagrams) {
            strBuff.append(datagram.toString(prefix + "  "));
        }
        return strBuff.toString();
    }

    @Override
    public E toObject() {
        return null;
    }

}
