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
 * The following static classes refer to the different usages of the GID, as
 * stated in the IB specification (section 4.1.1) and the corresponding header
 * file in the FM implementation. They are not used in the GUI application; they
 * are included here for completeness
 * 
 * /ALL_EMB/IbAcess/Common/Inc/ib_type.h
 * 
 */
public class GIDAsReg32s extends GIDBean {
    private static final long serialVersionUID = 1891408975809955819L;

    private int hh, hl, lh, ll;

    public GIDAsReg32s() {
        super();
    }

    public GIDAsReg32s(int hh, int hl, int lh, int ll) {
        super();
        this.hh = hh;
        this.hl = hl;
        this.lh = lh;
        this.ll = ll;
    }

    /**
     * @return the hh
     */
    public int getHh() {
        return hh;
    }

    /**
     * @param hh
     *            the hh to set
     */
    public void setHh(int hh) {
        this.hh = hh;
    }

    /**
     * @return the hl
     */
    public int getHl() {
        return hl;
    }

    /**
     * @param hl
     *            the hl to set
     */
    public void setHl(int hl) {
        this.hl = hl;
    }

    /**
     * @return the lh
     */
    public int getLh() {
        return lh;
    }

    /**
     * @param lh
     *            the lh to set
     */
    public void setLh(int lh) {
        this.lh = lh;
    }

    /**
     * @return the ll
     */
    public int getLl() {
        return ll;
    }

    /**
     * @param ll
     *            the ll to set
     */
    public void setLl(int ll) {
        this.ll = ll;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AsReg32s [hh=" + hh + ", hl=" + hl + ", lh=" + lh + ", ll="
                + ll + "]";
    }
}
