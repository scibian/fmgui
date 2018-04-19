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

//typedef struct {
//	struct {
//		uint32	LID;	
//		IB_BITFIELD2(uint32, 
//				Reserved:14,
//				BlockNum:18);
//	} PACK_SUFFIX RID;
//
//	/* 8 bytes */
//
//	uint8 		LinearFdbData[64];
//	
//	/* 72 bytes */
//} PACK_SUFFIX STL_LINEAR_FORWARDING_TABLE_RECORD;
//
//typedef struct {
//	PORT  LftBlock[MAX_LFT_ELEMENTS_BLOCK];
//
//} PACK_SUFFIX STL_LINEAR_FORWARDING_TABLE;

/**
 * Title:        LFTRecordBean
 * Description:  Linear Forwarding Table Record from SA populated by the connect manager.
 * 
 * @version 0.0
*/
import java.io.*;
import java.util.Arrays;

public class LFTRecordBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int lid;
	private int blockNum;
	private byte[] linearFdbData;
	
	public LFTRecordBean() {
		super();
	}
	
	public LFTRecordBean(int lid, int blockNum, byte[] linearFdbData) {
		super();
		this.lid = lid;
		this.blockNum = blockNum;
		this.linearFdbData = linearFdbData;
	}

	/**
	 * @return the lid
	 */
	public int getLid() {
		return lid;
	}
	/**
	 * @param lid the lid to set
	 */
	public void setLid(int lid) {
		this.lid = lid;
	}
	/**
	 * @return the blockNum
	 */
	public int getBlockNum() {
		return blockNum;
	}
	/**
	 * @param blockNum the blockNum to set
	 */
	public void setBlockNum(int blockNum) {
		this.blockNum = blockNum;
	}
	/**
	 * @return the linearFdbData
	 */
	public byte[] getLinearFdbData() {
		return linearFdbData;
	}
	/**
	 * @param linearFdbData the linearFdbData to set
	 */
	public void setLinearFdbData(byte[] linearFdbData) {
		this.linearFdbData = linearFdbData;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "LFTRecordBean [lid=" + lid + ", blockNum=" + blockNum
				+ ", linearFdbData=" + Arrays.toString(linearFdbData) + "]";
	}

}
