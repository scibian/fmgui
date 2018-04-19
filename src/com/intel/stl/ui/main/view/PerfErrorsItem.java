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

package com.intel.stl.ui.main.view;

/**
 * Performance Errors Item
 */
public class PerfErrorsItem {

    private String keyStr;

    private String valStr;

    private String helpID;

    private boolean isTtl = false;

    private boolean fromNeighbor = false;

    public PerfErrorsItem(String inK, String inV, String inHelpID,
            boolean fromNeighbor) {
        keyStr = inK;
        valStr = inV;
        helpID = inHelpID;
        this.fromNeighbor = fromNeighbor;
    }

    public PerfErrorsItem(String inK, long inV, boolean flag) {
        keyStr = inK;
        valStr = String.valueOf(inV);
        isTtl = flag;
    }

    /**
     * @return the keyStr
     */
    public String getKeyStr() {
        return keyStr;
    }

    /**
     * @return the valStr
     */
    public String getValStr() {
        return valStr;
    }

    public String getHelpID() {
        return helpID;
    }

    /**
     * @return the isTtl
     */
    public boolean isTtl() {
        return isTtl;
    }

    /**
     * @param keyStr
     *            the keyStr to set
     */
    public void setKeyStr(String keyStr) {
        this.keyStr = keyStr;
    }

    /**
     * @param valStr
     *            the valStr to set
     */
    public void setValStr(String valStr) {
        this.valStr = valStr;
    }

    public void setHelpID(String helpID) {
        this.helpID = helpID;
    }

    /**
     * @param isTtl
     *            the isTtl to set
     */
    public void setTtl(boolean isTtl) {
        this.isTtl = isTtl;
    }

    /**
     * @return fromNeighbor
     */
    public boolean isFromNeighbor() {
        return fromNeighbor;
    }

    /**
     * @param fromNeighbor
     *            the fromNeighbor to set
     */
    public void setFromNeighbor(boolean fromNeighbor) {
        this.fromNeighbor = fromNeighbor;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ViewItem [keyStr=" + keyStr + ", valStr=" + valStr + ", desc="
                + helpID + ", isTtl=" + isTtl + ", fromNeighbor=" + fromNeighbor
                + "]";
    }
}
