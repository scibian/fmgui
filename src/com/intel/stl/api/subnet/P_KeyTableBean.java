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

/**
 * Title:        P_KeyBean
 * Description:  P_Key element data for the Fabric View API.
 *               Implementation of an element of a partition table.
 *
 * @version 0.0
*/ 
import java.io.*;


/**
 * An element of a partition table.
 */
public class P_KeyTableBean implements Serializable{
    
	private static final long serialVersionUID = 1L;

	private boolean full;//MembershipType 0=Limited, 1=Full
    private short base;
    
    /**
     * Null Constructor
     */
    public P_KeyTableBean() {

    }
    
    public P_KeyTableBean(boolean full, short base) {
		super();
		this.full = full;
		this.base = base;
	}

	/**
     * Mutator.
     *
     * @param pFull -- true if membership is full, false if limited.
     */
    public void setFull(boolean pFull) {
        full = pFull;
    }
    
    /**
     * Accessor.
     *
     * @return the Virtual Lane
     */
    public boolean getFull() {
        return full;
    }
    

    /**
     * Mutator.
     *
     * @param pBase -- a new key for the Partition Table of a port.
     */
    public void setBase(short pBase) {
    	//Make sure the base value of the P_Key is within the range.  Refer to stl_sm.h:
    	//
    	//      #define MAX_PKEY_BLOCK_NUM			0x7FF
    	//      #define PKEY_BLOCK_NUM_MASK			0x7FF
    	//      #define STL_DEFAULT_PKEY		    0x7FFF
    	//      #define STL_DEFAULT_APP_PKEY		0x8001
    	//      #define STL_DEFAULT_FM_PKEY         0xFFFF
    	//      #define STL_DEFAULT_CLIENT_PKEY		0x7FFF
        if (((pBase >= 0)  && (pBase <= 0x7FFF)) || (pBase == 0xFFFF)) {
            base = pBase;
        }
    }
    
    /**
     * Accessor.
     *
     * @return the weight of a Virtual Lane
     */
    public short getBase() {
        return base;
    }

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "P_KeyTableBean [full=" + full + ", base=" + base + "]";
	}
    
}
