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
 * VLArbitrationRecord
 * 
 * STL Differences:
 *		Switch LID extended.
 *		Blocknum now defined as 0 - 3 as per the VL Arbitration Table MAD.
 *		Length of Low, High tables extended to 128 bytes.
 *		Preempt table added.
 */
//typedef struct {
//	struct {
//		uint32	LID;				
//		uint8	OutputPortNum;	
//		uint8	BlockNum;
//	} PACK_SUFFIX RID;
//
//	uint16		Reserved;
//	
//	STL_VLARB_TABLE VLArbTable;
//	
//} PACK_SUFFIX STL_VLARBTABLE_RECORD;
//
//
//#define VLARB_TABLE_LENGTH 128
//typedef union {
//	STL_VLARB_TABLE_ELEMENT  Elements[VLARB_TABLE_LENGTH]; /* RW */
//	uint32                   Matrix[STL_MAX_VLS];	/* RW */
//													/* POD: 0 */
//
//} PACK_SUFFIX STL_VLARB_TABLE;

//typedef struct {
//struct { IB_BITFIELD2( uint8,
//	Reserved:		3,
//	VL:				5 )		/* RW */
//} s;
//
//uint8   Weight;				/* RW */
//
//} PACK_SUFFIX STL_VLARB_TABLE_ELEMENT;

import java.io.Serializable;
import java.util.Arrays;

import com.intel.stl.api.Utils;

/**
 * VLArb Table Record from SA populated by the connect manager.
 */
public class VLArbTableRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int lid;

    private short outputPortNum; // promote to handle unsigned byte

    private short blockNum; // promote to handle unsigned byte

    private VLArbTableBean[] vlArbTableElement;

    private int[] matrix;

    public VLArbTableRecordBean() {
        super();
    }

    public VLArbTableRecordBean(int lid, byte outputPortNum, byte blockNum) {
        super();
        this.lid = lid;
        this.outputPortNum = Utils.unsignedByte(outputPortNum);
        this.blockNum = Utils.unsignedByte(blockNum);
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
     * @return the outputPortNum
     */
    public short getOutputPortNum() {
        return outputPortNum;
    }

    /**
     * @param outputPortNum
     *            the outputPortNum to set
     */
    public void setOutputPortNum(short outputPortNum) {
        this.outputPortNum = outputPortNum;
    }

    /**
     * @param outputPortNum
     *            the outputPortNum to set
     */
    public void setOutputPortNum(byte outputPortNum) {
        this.outputPortNum = Utils.unsignedByte(outputPortNum);
    }

    /**
     * @return the blockNum
     */
    public short getBlockNum() {
        return blockNum;
    }

    /**
     * @param blockNum
     *            the blockNum to set
     */
    public void setBlockNum(short blockNum) {
        this.blockNum = blockNum;
    }

    /**
     * @param blockNum
     *            the blockNum to set
     */
    public void setBlockNum(byte blockNum) {
        this.blockNum = Utils.unsignedByte(blockNum);
    }

    /**
     * @return the vlArbTableElement
     */
    public VLArbTableBean[] getVlArbTableElement() {
        return vlArbTableElement;
    }

    /**
     * @param vlArbTableElement
     *            the vlArbTableElement to set
     */
    public void setVlArbTableElement(VLArbTableBean[] vlArbTableElement) {
        this.vlArbTableElement = vlArbTableElement;
    }

    /**
     * @return the matrix
     */
    public int[] getMatrix() {
        return matrix;
    }

    /**
     * @param matrix
     *            the matrix to set
     */
    public void setMatrix(int[] matrix) {
        this.matrix = matrix;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "VLArbTableRecordBean [lid=" + lid + ", outputPortNum="
                + outputPortNum + ", blockNum=" + blockNum
                + ", vlArbTableElement=" + Arrays.toString(vlArbTableElement)
                + ", matrix=" + Arrays.toString(matrix) + "]";
    }

}
