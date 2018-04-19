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

import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm.h v1.115
 * 
 * <pre>
 * typedef struct {
 * 
 * 	uint8	BaseVersion;		// RO Supported MAD Base Version 
 * 	uint8	ClassVersion;		// RO Supported Subnet Management Class 
 * 								// (SMP) Version 
 * 	uint8	NodeType;	
 * 	uint8	NumPorts;			// RO Number of link ports on this node 
 * 
 * 	uint32	Reserved;
 * 
 * 	uint64	SystemImageGUID;		
 * 
 * 	uint64	NodeGUID;			// RO GUID of the HFI or switch 
 * 
 * 	uint64	PortGUID;			// RO GUID of this end port itself 
 * 
 * 	uint16	PartitionCap;		// RO Number of entries in the Partition Table 
 * 								// for end ports 
 * 	uint16	DeviceID;			// RO Device ID information as assigned by 
 * 								// device manufacturer 
 * 	uint32	Revision;			// RO Device revision, assigned by manufacturer 
 * 
 * 	STL_FIELDUNION2(u1, 32, 
 * 			LocalPortNum:	8,		// RO The link port number this 
 * 									// SMP came on in 
 * 			VendorID:		24);	// RO Device vendor, per IEEE 
 * 
 * } PACK_SUFFIX STL_NODE_INFO;
 * </pre>
 * 
 */
public class NodeInfo extends SimpleDatagram<NodeInfoBean> {

    public NodeInfo() {
        super(44);
    }

    public void setBaseVersion(byte version) {
        buffer.put(0, version);
    }

    public void setClassVersion(byte version) {
        buffer.put(1, version);
    }

    public void setNodeType(byte type) {
        buffer.put(2, type);
    }

    public void setNumPorts(byte num) {
        buffer.put(3, num);
    }

    public void setSystemImageGUID(long guid) {
        buffer.putLong(8, guid);
    }

    public void setNodeGUID(long guid) {
        buffer.putLong(16, guid);
    }

    public void setPortGUID(long guid) {
        buffer.putLong(24, guid);
    }

    public void setPartitionCap(short cap) {
        buffer.putShort(32, cap);
    }

    public void setDeviceID(short id) {
        buffer.putShort(34, id);
    }

    public void setRevision(int revision) {
        buffer.putInt(36, revision);
    }

    public void setLocalPortNum(byte num) {
        buffer.put(40, num);
    }

    public void setVendorID(int num) {
        // TODO: test this work correctly on both byte orders
        int old = buffer.getInt(40);
        int value = (old & 0xff000000) | (num & 0xffffff);
        buffer.putInt(40, value);
    }

    public int getVendorID() {
        int raw = buffer.getInt(40);
        return raw & 0xffffff;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public NodeInfoBean toObject() {
        buffer.clear();
        byte baseVersion = buffer.get();
        byte classVersion = buffer.get();
        byte nodeType = buffer.get();
        byte numPorts = buffer.get();
        buffer.position(8);
        long sysImageGUID = buffer.getLong();
        long nodeGUID = buffer.getLong();
        long portGUID = buffer.getLong();
        short partitionCap = buffer.getShort();
        short deviceID = buffer.getShort();
        int revision = buffer.getInt();
        byte localPortNum = buffer.get();
        int vendorID = getVendorID();
        return new NodeInfoBean(baseVersion, classVersion, nodeType, numPorts,
                sysImageGUID, nodeGUID, portGUID, partitionCap, deviceID,
                revision, localPortNum, vendorID);
    }

}
