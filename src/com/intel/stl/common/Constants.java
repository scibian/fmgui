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

package com.intel.stl.common;

/**
 */
public interface Constants {
    int PROTOCAL_VERSION = 0x80;

    byte STL_BASE_VERSION = (byte) 0x80;

    byte IB_BASE_VERSION = 1;

    byte MCLASS_SUBN_ADM = 0x03; /* Subnet Administration class */

    byte MCLASS_PERF = 0x04; /* Performance Management class */

    byte MCLASS_VFI_PM = 0x32; /* PM VFI mclass value */

    int STL_MAD_BLOCK_SIZE = 2048;

    short MAD_STATUS_SUCCESS = 0x0000;

    short MAD_STATUS_BUSY = 0x0001;

    short MAD_STATUS_REDIRECT_REQD = 0x0002;

    short MAD_STATUS_SM_UNAVAILABLE = 0x0100;

    short MAD_STATUS_PM_UNAVAILABLE = 0x0A00;

    // see oib_utils_pa.c and oib_utils_sa.c for more status
    short MAD_STATUS_UNSUPPORTED = 0x0200;

    String MAIL_LIST_DELIMITER = ";";

}
