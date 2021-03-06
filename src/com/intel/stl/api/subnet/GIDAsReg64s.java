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
 * are included here for completeness /ALL_EMB/IbAcess/Common/Inc/ib_type.h
 * 
 */
public class GIDAsReg64s extends GIDBean {
    private static final long serialVersionUID = 1L;

    private long h, l;

    public GIDAsReg64s() {
        super();
    }

    public GIDAsReg64s(long h, long l) {
        super();
        this.h = h;
        this.l = l;
    }

    /**
     * @return the h
     */
    public long getH() {
        return h;
    }

    /**
     * @param h
     *            the h to set
     */
    public void setH(long h) {
        this.h = h;
    }

    /**
     * @return the l
     */
    public long getL() {
        return l;
    }

    /**
     * @param l
     *            the l to set
     */
    public void setL(long l) {
        this.l = l;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "AsRegs64s [h=" + h + ", l=" + l + "]";
    }
}
