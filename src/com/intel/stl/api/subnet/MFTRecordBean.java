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

/*
 * MFTRecord 
 * 
 * NOTES:
 * 		In IB the width of the PORTMASK data type was defined as only 16 
 *		bits, requiring the SM to iterate over 3 different positions values 
 *		to retrieve the MFTs for a 48-port switch. 
 *		For this reason PORTMASK is now defined as 64 bits wide, eliminating 
 *		the need to use the "position" attribute in the Gen 1 & Gen 2 
 *		generations of hardware. 
 *
 *		As above, a "block" is defined as 64 bytes; therefore a single block 
 *		will contain 8 MFT records. The consumer should use GetTable() and 
 *		RMPP to retrieve more than one block. As with the RFT, BlockNum is 
 *		defined as 21 bits, providing for a total of 2^24 LIDs.
 *
 * STL Differences:
 *		PORTMASK is now 64 bits.
 *		LID is now 32 bits.
 *		Position is now 2 bits.
 *		Reserved is now 9 bits.
 *		BlockNum is now 21 bits.
 *		Reserved2 removed to preserve word alignment.
 */
//typedef struct _STL_MULTICAST_FORWARDING_TABLE_RECORD {
//	struct {
//		uint32		LID; 				// Port 0 of the switch.	
//	
//		STL_FIELDUNION3(u1, 32,
//				Position:2,			
//				Reserved:9,
//				BlockNum:21);
//	} PACK_SUFFIX RID;
//
//	STL_MULTICAST_FORWARDING_TABLE MftTable;
//	
//} PACK_SUFFIX STL_MULTICAST_FORWARDING_TABLE_RECORD;

//typedef struct {
//	STL_PORTMASK  MftBlock[STL_NUM_MFT_ELEMENTS_BLOCK];
//
//} PACK_SUFFIX STL_MULTICAST_FORWARDING_TABLE;

/**
 * Title:        MFTRecordBean
 * Description:  Multicast Forwarding Table Record from SA populated by the connect manager.
 * 
 * @version 0.0
*/
import java.io.*;
import java.util.Arrays;

public class MFTRecordBean implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int lid;
	private byte position;
	private int blockNum;
	
	private long[] mftTable;
    //How about number of blocks? List<LFTRecordBean> size will be it.
	
	public MFTRecordBean() {
		super();
	}
	
	public MFTRecordBean(int lid, byte position, int blockNum) {
		super();
		this.lid = lid;
		this.position = position;
		this.blockNum = blockNum;
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
	 * @return the position
	 */
	public byte getPosition() {
		return position;
	}

	/**
	 * @param position the position to set
	 */
	public void setPosition(byte position) {
		this.position = position;
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
	 * @return the mftTable
	 */
	public long[] getMftTable() {
		return mftTable;
	}

	/**
	 * @param mftTable the mftTable to set
	 */
	public void setMftTable(long[] mftTable) {
		this.mftTable = mftTable;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "MFTRecordBean [lid=" + lid + ", position=" + position
				+ ", blockNum=" + blockNum + ", mftTable="
				+ Arrays.toString(mftTable) + "]";
	}

}
