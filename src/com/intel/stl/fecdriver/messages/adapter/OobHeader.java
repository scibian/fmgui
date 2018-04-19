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

import com.intel.stl.common.Constants;

/**
 * There is difference between spec and iba_fequery. I'm using iba_fequery. see
 * /ALL_EMB/IbaTools/iba_fequery/fe_net.h
 * 
 * <pre>
 * typedef struct __OOBHeader {
 * 	uint32_t 		HeaderVersion; 		// Version of the FE protocol header 
 * 	uint32_t 		Length; 			// Length of the message data payload
 * 	uint32_t 		Reserved[2]; 		// Reserved
 * } OOBHeader;
 * </pre>
 * 
 */
public class OobHeader extends SimpleDatagram<Void> {

    public OobHeader() {
        super(16);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vieo.fv.resource.stl.data.SimpleDatagram#initData()
     */
    @Override
    protected void initData() {
        super.initData();
        setVersion();
    }

    public void setVersion() {
        setVersion(Constants.PROTOCAL_VERSION);
    }

    public void setVersion(int version) {
        buffer.putInt(0, version);
    }

    public void setPayloadSize(int length) {
        buffer.putInt(4, length);
    }

}
