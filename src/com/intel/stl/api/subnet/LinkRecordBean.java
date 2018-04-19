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
package com.intel.stl.api.subnet;

/**
 * Title:        LinkRecordBean
 * Description:  Link Record from SA populated by the connect manager.
 *               Implementation of a representation of a connection between two ports.
 *               An abstraction.
 *               
 * @version 0.0
 */
import java.io.Serializable;

import com.intel.stl.api.Utils;

public class LinkRecordBean implements Serializable {
    private static final long serialVersionUID = 8951077237091003130L;

    private short fromPortIndex; // promote to handle unsigned byte

    private int fromLID;

    private short toPortIndex; // promote to handle unsigned byte

    private int toLID;

    // State is based on source
    private boolean active = true;

    public LinkRecordBean() {
        super();
    }

    public LinkRecordBean(int fromLID, byte fromPortIndex, int toLID,
            byte toPortIndex) {
        this(fromLID, Utils.unsignedByte(fromPortIndex), toLID, Utils
                .unsignedByte(toPortIndex));
    }

    /**
     * Description:
     * 
     * @param fromLID
     * @param fromPortIndex
     * @param toLID
     * @param toPortIndex
     */
    public LinkRecordBean(int fromLID, short fromPortIndex, int toLID,
            short toPortIndex) {
        super();
        this.fromLID = fromLID;
        this.fromPortIndex = fromPortIndex;
        this.toLID = toLID;
        this.toPortIndex = toPortIndex;
    }

    /**
     * @return the fromPortIndex
     */
    public short getFromPortIndex() {
        return fromPortIndex;
    }

    /**
     * @param fromPortIndex
     *            the fromPortIndex to set
     */
    public void setFromPortIndex(short fromPortIndex) {
        this.fromPortIndex = fromPortIndex;
    }

    /**
     * @param fromPortIndex
     *            the fromPortIndex to set
     */
    public void setFromPortIndex(byte fromPortIndex) {
        this.fromPortIndex = Utils.unsignedByte(fromPortIndex);
    }

    /**
     * @return the fromLid
     */
    public int getFromLID() {
        return fromLID;
    }

    /**
     * @param fromLid
     *            the fromLid to set
     */
    public void setFromLID(int fromLid) {
        this.fromLID = fromLid;
    }

    /**
     * @return the toPortIndex
     */
    public short getToPortIndex() {
        return toPortIndex;
    }

    /**
     * @param toPortIndex
     *            the toPortIndex to set
     */
    public void setToPortIndex(short toPortIndex) {
        this.toPortIndex = toPortIndex;
    }

    /**
     * @param toPortIndex
     *            the toPortIndex to set
     */
    public void setToPortIndex(byte toPortIndex) {
        this.toPortIndex = Utils.unsignedByte(toPortIndex);
    }

    /**
     * @return the toLid
     */
    public int getToLID() {
        return toLID;
    }

    /**
     * @param toLid
     *            the toLid to set
     */
    public void setToLID(int toLid) {
        this.toLID = toLid;
    }

    /**
     * @return the active
     */
    public boolean isActive() {
        return active;
    }

    /**
     * @param active
     *            the active to set
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "LinkRecordBean [fromPortIndex=" + fromPortIndex + ", fromLID="
                + fromLID + ", toPortIndex=" + toPortIndex + ", toLID=" + toLID
                + ", active=" + active + "]";
    }

}
