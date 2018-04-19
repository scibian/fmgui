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

import com.intel.stl.fecdriver.messages.adapter.ComposedDatagram;
import com.intel.stl.fecdriver.messages.adapter.IDatagram;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/ib_generalServices.h
 * 
 * <pre>
 * #define STL_MAD_BLOCK_SIZE			2048
 * #define MAD_BLOCK_SIZE				STL_MAD_BLOCK_SIZE
 * 
 * #define		IBA_SUBN_ADM_HDRSIZE	56 // common + class specific header
 * #define		IBA_SUBN_ADM_DATASIZE	(MAD_BLOCK_SIZE - IBA_SUBN_ADM_HDRSIZE) // what's left for class payload
 * #define		STL_IBA_SUBN_ADM_DATASIZE	IBA_SUBN_ADM_DATASIZE
 * 
 * typedef struct _SA_MAD {
 * 	MAD_COMMON	common;	// Generic MAD Header
 * 	RMPP_HEADER	RmppHdr;		// RMPP header
 * 	SA_HDR		SaHdr;			// SA class specific header
 * 	uint8		Data[STL_IBA_SUBN_ADM_DATASIZE];
 * } PACK_SUFFIX SA_MAD, *PSA_MAD;
 * </pre>
 * 
 */
public class SAData extends ComposedDatagram<Void> {
    protected SAHeader saHeader;

    protected IDatagram<?> data;

    public SAData() {
        saHeader = new SAHeader();
        addDatagram(saHeader);
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(IDatagram<?> data) {
        if (this.data != null)
            removeDatagram(this.data);
        this.data = data;
        addDatagram(data);
    }

    public IDatagram<?> getData() {
        return data;
    }

    /**
     * @return the saHeader
     */
    public SAHeader getSaHeader() {
        return saHeader;
    }

}
