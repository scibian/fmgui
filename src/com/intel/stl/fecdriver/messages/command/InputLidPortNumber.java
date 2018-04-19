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
package com.intel.stl.fecdriver.messages.command;

/**
 */
public class InputLidPortNumber extends InputImageId {
    private final int lid;

    private final byte portNumber;

    private final boolean delta;

    public InputLidPortNumber(int lid, byte portNumber) {
        this(lid, portNumber, false, 0, 0);
    }

    public InputLidPortNumber(int lid, byte portNumber, boolean delta) {
        this(lid, portNumber, delta, 0, 0);
    }

    public InputLidPortNumber(int lid, byte portNumber, long imageNumber,
            int imageOffset) {
        this(lid, portNumber, false, imageNumber, imageOffset);
    }

    public InputLidPortNumber(int lid, byte portNumber, boolean delta,
            long imageNumber, int imageOffset) {
        super(imageNumber, imageOffset);
        this.lid = lid;
        this.portNumber = portNumber;
        this.delta = delta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.message.command.argument.InputImageId#getType()
     */
    @Override
    public InputType getType() {
        return InputType.InputTypeLidPortNumber;
    }

    /**
     * @return the lid
     */
    @Override
    public int getLid() {
        return lid;
    }

    /**
     * @return the portNumber
     */
    @Override
    public byte getPortNumber() {
        return portNumber;
    }

    /**
     * @return the delta
     */
    @Override
    public boolean isDelta() {
        return delta;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "InputLidPortNumber [lid=" + lid + ", portNumber=" + portNumber
                + ", delta=" + delta + ", getImageId()=" + getImageId() + "]";
    }

}
