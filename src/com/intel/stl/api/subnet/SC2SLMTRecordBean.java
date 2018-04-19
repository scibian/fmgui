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

/* SC2SLMappingTableRecord
 * 
 * STL Differences:
 *      New for STL.
 * 
 * typedef struct {
 *     struct {
 *         uint32  LID;    
 *         uint16  Reserved;               
 *     } PACK_SUFFIX RID;
 *    
 *     uint16      Reserved2;
 *    
 *     STL_SL      SCSLMap[STL_MAX_SLS];   
 *    
 * } PACK_SUFFIX STL_SC2SL_MAPPING_TABLE_RECORD;
 * 
 */
/**
 * Title:        SC2SLMTRecordBean
 * Description:  SC2SL Mapping Record from SA populated by the connect manager.
 * 
 * @version 0.0
 */
import java.io.Serializable;
import java.util.Arrays;

public class SC2SLMTRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int lid;

    private byte[] data;

    // How about number of blocks? List<LFTRecordBean> size will be it.

    public SC2SLMTRecordBean() {
        super();
    }

    public SC2SLMTRecordBean(int lid, byte[] data) {
        super();
        this.lid = lid;
        this.data = data;
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
     * @return the data
     */
    public byte[] getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(byte[] data) {
        this.data = data;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "SC2SLMTRecordBean [lid=" + lid + ", data="
                + Arrays.toString(data) + "]";
    }

}
