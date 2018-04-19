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

/**
 * ref: /ALL_EMB/IbaTools/iba_fequery/fe_net.h
 * <pre>
 * typedef struct _OOBPacket {
 * 	OOBHeader Header;
 * 	MAD_RMPP MadData;
 * } OOBPacket;
 * </pre> 
 */
public class OobPacket extends ComposedDatagram<Void> {
	private OobHeader oobHeader;
	private RmppMad rmppMad;
	private long expireTime;
	
	public OobPacket() {
		oobHeader = new OobHeader();
		addDatagram(oobHeader);
	}
	
	public OobPacket(RmppMad rmppMad) {
		this();
		this.rmppMad = rmppMad;
		addDatagram(rmppMad);
	}

	/**
	 * @param rmppMad the rmppMad to set
	 */
	public void setRmppMad(RmppMad rmppMad) {
		this.rmppMad = rmppMad;
		addDatagram(rmppMad);
	}

	/**
	 * @return the rmppMad
	 */
	public RmppMad getRmppMad() {
		return rmppMad;
	}

	/**
	 * @return the oobHeader
	 */
	public OobHeader getOobHeader() {
		return oobHeader;
	}
	
	public void fillPayloadSize() {
		int len = getLength() - oobHeader.getLength();
		oobHeader.setPayloadSize(len);
	}

	/**
	 * @return the expireTime
	 */
	public long getExpireTime() {
		return expireTime;
	}

	/**
	 * @param expireTime the expireTime to set
	 */
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}
	
}
