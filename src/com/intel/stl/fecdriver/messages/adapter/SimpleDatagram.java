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

/**
 */
public class SimpleDatagram<E> implements IDatagram<E> {
    protected int length;

    protected ByteBuffer buffer;

    public SimpleDatagram(int length) {
        this.length = length;
    }

    public int build(boolean force) {
        return build(force, ByteOrder.BIG_ENDIAN);
    }

    public int build(boolean force, ByteOrder order) {
        if (buffer == null || force) {
            buffer = ByteBuffer.allocate(length);
            buffer.order(order);
            initData();
            return buffer.capacity();
        } else
            return 0;
    }

    protected void initData() {
        buffer.clear();
        buffer.put(new byte[buffer.capacity()]);
    }

    public int wrap(byte[] data, int offset) {
        return wrap(data, offset, ByteOrder.BIG_ENDIAN);
    }

    public int wrap(byte[] data, int offset, ByteOrder order) {
        buffer = ByteBuffer.wrap(data, offset, length).slice();
        buffer.order(order);
        return offset + length;
    }

    @Override
    public boolean hasBuffer() {
        return buffer != null;
    }

    public ByteBuffer getByteBuffer() {
        return buffer;
    }

    @Override
    public ByteBuffer[] getByteBuffers() {
        if (buffer == null)
            return null;

        return new ByteBuffer[] { buffer };
    }

    @Override
    public ByteOrder getByteOrder() {
        return buffer.order();
    }

    @Override
    public int getLength() {
        return length;
    }

    @Override
    public void dump(String prefix, PrintStream out) {
        String formatted = format(prefix);
        out.print(formatted);
    }

    @Override
    public E toObject() {
        return null;
    }

    @Override
    public String toString(String prefix) {
        return format(prefix);
    }

    private String format(String prefix) {
        StringBuffer strBuff = new StringBuffer();
        strBuff.append(prefix);
        strBuff.append(getClass().getSimpleName());
        strBuff.append("\n");
        if (buffer == null) {
            strBuff.append(prefix);
            strBuff.append("null");
            strBuff.append("\n");
            return strBuff.toString();
        }

        byte[] bytes = buffer.array();
        int offset = buffer.arrayOffset();
        strBuff.append(prefix);
        for (int i = 0; i < length; i++) {
            strBuff.append(String.format("%02x", bytes[i + offset] & 0xff)
                    + " ");
            if ((i + 1) % 8 == 0)
                strBuff.append(" ");
            if ((i + 1) % 16 == 0) {
                strBuff.append("\n");
                if ((i + 1) < length)
                    strBuff.append(prefix);
            }
        }
        if (length == 0 || length % 16 != 0) {
            strBuff.append("\n");
        }
        return strBuff.toString();
    }

}
