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
public interface IDatagram<E> {

    /**
     * 
     * <i>Description:</i> Creates buffers for the datagram and initializes
     * fields
     * 
     * @param force
     *            forces recreation of buffers
     * @return length in bytes of the datagram
     */
    int build(boolean force);

    /**
     * 
     * <i>Description:</i> wraps a byte array into this datagram
     * 
     * @param data
     *            the byte array that will back the datagram
     * @param offset
     *            the offset of the subarray to be used
     * @return the new offset after a slice of the byte array is applied to this
     *         datagram
     */
    int wrap(byte[] data, int offset);

    /**
     * 
     * <i>Description:</i> tells whether this datagram has its buffers created
     * 
     * @return boolean
     */
    boolean hasBuffer();

    /**
     * 
     * <i>Description:</i> returns the ByteBuffers conforming this datagram
     * 
     * @return a ByteBuffer array
     */
    ByteBuffer[] getByteBuffers();

    /**
     * 
     * <i>Description:</i> returns the total length of this datagram
     * 
     * @return the length
     */
    int getLength();

    /**
     * 
     * <i>Description:</i> returns the byte order for the buffers in this
     * datagram
     * 
     * @return the byte order
     */
    ByteOrder getByteOrder();

    /**
     * 
     * <i>Description:</i> prints in string format the contents of this datagram
     * 
     * @param prefix
     *            a prefix added to each line in the formatted contents
     * @param out
     *            a PrintStream
     */
    void dump(String prefix, PrintStream out);

    /**
     * 
     * <i>Description:</i> converts the contents of this datagram into a
     * formatted string
     * 
     * @param prefix
     *            a prefix added to each line in the formatted contents
     * @return a formatted string
     */
    String toString(String prefix);

    /**
     * 
     * <i>Description:</i> parses this datagram and generates an object
     * 
     * @return an object
     */
    E toObject();
}
