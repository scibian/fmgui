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

package com.intel.stl.fecdriver.messages.adapter.sa;

import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_generalServices.h
 * commit c99bf49fe583352896dc672eb8a972a54a3dbc85
 * date 2017-08-02 07:48:23
 *
 * // Subnet Administration MAD format - Class Version 2
 * typedef struct _SA_HDR {
 * 	uint64		SmKey;			// SM_KEY
 * 	uint16		AttributeOffset;// attribute record size in units of uint64's
 * 	uint16		Reserved;
 * 	uint64		ComponentMask;	// Component mask for query operation
 * } PACK_SUFFIX SA_HDR;
 * </pre>
 *
 */
public class SAHeader extends SimpleDatagram<Void> {

    public SAHeader() {
        super(20);
    }

    public void setSmKey(long key) {
        buffer.putLong(0, key);
    }

    public void setAttributeOffset(short offset) {
        buffer.putShort(8, offset);
    }

    public int getAttributeOffset() {
        return buffer.getShort(8) & 0xffff;
    }

    public void setComponentMask(long mask) {
        buffer.putLong(12, mask);
    }
}
