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
 * CableInfoRecord
 * 
 * STL Differences:
 *      LID lengthened to 32 bits.
 *      Reserved2 field shortened from 20 bits to 4 to preserve word-alignment.
 * 
 * #define STL_CIR_DATA_SIZE       64
 * typedef struct {
 *   struct {
 *     uint32  LID;
 *     uint8   Port;
 *     IB_BITFIELD2(uint8,
 *                 Length:7,
 *                 Reserved:1);
 *     IB_BITFIELD2(uint16,
 *                 Address:12,
 *                 PortType:4); // Port type for response only 
 *     };
 *     
 *     uint8       Data[STL_CIR_DATA_SIZE];
 * 
 * } PACK_SUFFIX STL_CABLE_INFO_RECORD;
 * 
 * 
 * CableInfo
 * 
 * Attribute Modifier as: 0AAA AAAA AAAA ALLL LLL0 0000 PPPP PPPP
 *                        A: Starting address of cable data
 *                        L: Length (bytes) of cable data - 1
 *                           (L+1 bytes of data read)  
 *                        P: Port number (0 - management port, switches only)
 * 
 * NOTE: Cable Info is mapped onto a linear 4096-byte address space (0-4095).
 * Cable Info can only be read within 128-byte pages; that is, a single
 * read cannot cross a 128-byte (page) boundary.
 * 
 * typedef struct {
 *     uint8   Data[64];           // RO Cable Info data (up to 64 bytes) 
 *         
 * } PACK_SUFFIX STL_CABLE_INFO;
 * 
 */
import java.io.Serializable;

import com.intel.stl.api.Utils;

/**
 * Cable Record from SA populated by the connect manager.
 */
public class CableRecordBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int lid;

    private short port; // promote to handle unsigned byte

    private byte length;

    private short address;

    private byte portType;

    private CableInfoBean cableInfo;

    public CableRecordBean() {
        super();
    }

    public CableRecordBean(int lid, byte port, byte length, short address,
            byte portType, CableInfoBean cableInfo) {
        super();
        this.lid = lid;
        this.port = Utils.unsignedByte(port);
        this.length = length;
        this.address = address;
        this.portType = portType;
        this.cableInfo = cableInfo;
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
     * @return the port
     */
    public short getPort() {
        return port;
    }

    /**
     * @return the length
     */
    public byte getLength() {
        return length;
    }

    /**
     * @return the address
     */
    public short getAddress() {
        return address;
    }

    /**
     * @return the portType
     */
    public byte getPortType() {
        return portType;
    }

    /**
     * @return the cableInfo
     */
    public CableInfoBean getCableInfo() {
        return cableInfo;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(short port) {
        this.port = port;
    }

    /**
     * @param port
     *            the port to set
     */
    public void setPort(byte port) {
        this.port = Utils.unsignedByte(port);
    }

    /**
     * @param length
     *            the length to set
     */
    public void setLength(byte length) {
        this.length = length;
    }

    /**
     * @param address
     *            the address to set
     */
    public void setAddress(short address) {
        this.address = address;
    }

    /**
     * @param portType
     *            the portType to set
     */
    public void setPortType(byte portType) {
        this.portType = portType;
    }

    /**
     * @param cableInfo
     *            the cableInfo to set
     */
    public void setCableInfo(CableInfoBean cableInfo) {
        this.cableInfo = cableInfo;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "CableRecordBean [lid=" + lid + ", port=" + port + ", length="
                + length + ",  address=" + address + ",  portType=" + portType
                + ", cableInfo=" + cableInfo + "]";
    }

}
