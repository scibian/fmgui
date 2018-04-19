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
//		uint16	Blocknum;
//		uint8	PortNum;
//	} PACK_SUFFIX RID;
//	
//	uint8		Reserved;	 
//	
//	STL_PARTITION_TABLE	PKeyTblData;
//	
//} PACK_SUFFIX STL_P_KEY_TABLE_RECORD;
//
//
//typedef struct {
//
//	STL_PKEY_ELEMENT PartitionTableBlock[NUM_PKEY_ELEMENTS_BLOCK];	/* RW List of P_Key Block elements */
//
//} PACK_SUFFIX STL_PARTITION_TABLE;

//typedef union {
//uint16  AsReg16;
//struct { IB_BITFIELD2( uint16,
//	MembershipType:		1,				/* 0=Limited, 1=Full */
//	P_KeyBase:			15 )			/* Base value of the P_Key that */
//										/*  the endnode will use to check */
//										/*  against incoming packets */
//} s;
//
//} PACK_SUFFIX STL_PKEY_ELEMENT;
//

import java.io.Serializable;
import java.util.Arrays;

import com.intel.stl.api.Utils;

/**
 * P_Key Table Record from SA populated by the connect manager.
 */
public class P_KeyTableRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int lid;

    private int blockNum; // promote to handle unsigned short

    private short portNum; // promote to handle unsigned byte

    private P_KeyTableBean[] pKeyTableData;

    public P_KeyTableRecordBean() {
        super();
    }

    public P_KeyTableRecordBean(int lid, short blockNum, byte portNum,
            P_KeyTableBean[] pKeyTableData) {
        super();
        this.lid = lid;
        this.blockNum = Utils.unsignedShort(blockNum);
        this.portNum = Utils.unsignedByte(portNum);
        this.pKeyTableData = pKeyTableData;
    }

    /**
     * @return the lid
     */
    public int getLid() {
        return lid;
    }

    /**
     * @param lid
     *            the lid to set
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
     * @param blockNum
     *            the blockNum to set
     */
    public void setBlockNum(int blockNum) {
        this.blockNum = blockNum;
    }

    /**
     * @param blockNum
     *            the blockNum to set
     */
    public void setBlockNum(short blockNum) {
        this.blockNum = Utils.unsignedShort(blockNum);
    }

    /**
     * @return the portNum
     */
    public short getPortNum() {
        return portNum;
    }

    /**
     * @param portNum
     *            the portNum to set
     */
    public void setPortNum(short portNum) {
        this.portNum = portNum;
    }

    /**
     * @param portNum
     *            the portNum to set
     */
    public void setPortNum(byte portNum) {
        this.portNum = Utils.unsignedByte(portNum);
    }

    /**
     * @return the pKeyTableData
     */
    public P_KeyTableBean[] getpKeyTableData() {
        return pKeyTableData;
    }

    /**
     * @param pKeyTableData
     *            the pKeyTableData to set
     */
    public void setpKeyTableData(P_KeyTableBean[] pKeyTableData) {
        this.pKeyTableData = pKeyTableData;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "P_KeyTableRecordBean [lid=" + lid + ", blockNum=" + blockNum
                + ", portNum=" + portNum + ", pKeyTableData="
                + Arrays.toString(pKeyTableData) + "]";
    }

}
