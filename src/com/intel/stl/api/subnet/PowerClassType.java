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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum PowerClassType {
    CLASS1(0x00, 0x00),
    CLASS2(0x00, 0x01),
    CLASS3(0x00, 0x02),
    CLASS4(0x00, 0x03),
    CLASS5(0x01),
    CLASS6(0x02),
    CLASS7(0x03),
    UNDEFINED(-1);

    private final int codeHigh;

    private int codeLow;

    private static Logger log = LoggerFactory.getLogger(PowerClassType.class);

    private PowerClassType(int codeHigh, int codeLow) {
        this.codeHigh = codeHigh;
        this.codeLow = codeLow;
    }

    private PowerClassType(int codeHigh) {
        this.codeHigh = codeHigh;
    }

    /**
     * @return the codeHigh
     */
    public int getCodeHigh() {
        return codeHigh;
    }

    /**
     * @return the codeLow
     */
    public int getCodeLow() {
        return codeLow;
    }

    public static PowerClassType getPowerClassType(int codeHigh, int codeLow) {
        for (PowerClassType pc : PowerClassType.values()) {
            if (pc.getCodeHigh() == codeHigh && pc.getCodeLow() == codeLow) {
                return pc;
            }
        }
        log.error("Undefined PowerClassType : codeHigh ='" + codeHigh + "'"
                + ", codeLow ='" + codeLow + "'");
        return UNDEFINED;
    }

}
