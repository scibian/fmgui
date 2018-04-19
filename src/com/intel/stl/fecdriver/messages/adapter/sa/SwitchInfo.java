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

import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.api.subnet.SwitchInfoBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * 
 *         <pre>
 * typedef struct {
 *     uint32  LinearFDBCap;       // RO Number of entries supported in the 
 *                                 // Linear Unicast Forwarding Database 
 *     uint32  Reserved20;     
 *     uint32  MulticastFDBCap;    // RO Number of entries supported in the 
 *                                 // Multicast Forwarding Database 
 *     STL_LID_32  LinearFDBTop;   // RW Indicates the top of the Linear 
 *                                 //  Forwarding Table 
 *                                 // POD: 0 
 *     STL_LID_32  MulticastFDBTop;    // RW Indicates the top of the Multicast 
 *                                 //  Forwarding Table 
 *                                 // POD: 0 
 *     uint32  CollectiveCap;      // RO Number of entries supported in the 
 *                                 //  Collective Table 
 *                                 // Reserved in Gen1 
 *     STL_LID_32  CollectiveTop;      // RW Indicates the top of the Collective Table 
 *                                 // POD: 0 
 *                                 // Reserved in Gen1 
 *     uint32  Reserved;
 * 
 *     STL_IPV6_IP_ADDR  IPAddrIPV6;    // RO IP Address - IPV6 
 * 
 *     STL_IPV4_IP_ADDR  IPAddrIPV4;           // RO IP Address - IPV4  
 * 
 *     uint32  Reserved26;
 *     uint32  Reserved27;
 *     uint32  Reserved28;
 *   
 *     uint8   Reserved21;         
 *     uint8   Reserved22; 
 *     uint8   Reserved23;
 * 
 *     union {
 *         uint8   AsReg8;
 *         struct { IB_BITFIELD3( uint8,
 *             LifeTimeValue:      5,  // (STL LifeTimeValue a new attribute) 
 *             PortStateChange:    1,  // RW This bit is set to zero by a 
 *                                     //  management write 
 *             Reserved20:     2 ) 
 *         } s;
 *     } u1;
 * 
 *     uint16  Reserved24; 
 *     uint16  PartitionEnforcementCap;    // RO Specifies te number of entries in the 
 *                                         //  partition enforcement table 
 *     uint8   PortGroupCap;           // RO Specifies the maximum number of 
 *                                     // entries in the port group table 
 *     uint8   PortGroupTop;           // RW The current number of entries in 
 *                                     // port group table. 
 * 
 *     struct {                    // RW (see STL_ROUTING_MODE) 
 *         uint8   Supported;      // Supported routing mode 
 *         uint8   Enabled;        // Enabled routing mode 
 *     } RoutingMode;
 * 
 *     union {
 *         uint8   AsReg8;
 *         struct { IB_BITFIELD6( uint8,
 *             Reserved20:     1,  
 *             Reserved21:     1,
 *             Reserved22:     1,
 *             Reserved23:     1,
 *             EnhancedPort0:  1,  
 *             Reserved:       3 )
 *         } s;
 *     } u2;
 * 
 *     struct { IB_BITFIELD3( uint8,   // Multicast/Collectives masks 
 *         Reserved:           2,
 *         CollectiveMask:     3,  // RW Num of additional upper 1s in 
 *                                 // Collective address 
 *                                 // POD: 1 
 *                                 // Reserved in Gen1 
 *         MulticastMask:      3 ) // RW Num of upper 1s in Multicast address 
 *                                 // POD: 4 
 *     } MultiCollectMask;
 * 
 *     STL_FIELDUNION7(AdaptiveRouting, 16,
 *         Enable:             1,  // RW Enable/Disable AR 
 *         Pause:              1,  // RW Pause AR when true 
 *         Algorithm:          3,  // RW 0 = Random, 1 = Greedy, 
 *                                 // 2 = Random Greedy. 
 *         Frequency:          3,  // RW 0-7. Value expands to 2^F*64ms. 
 *         LostRoutesOnly:     1,  // RW. Indicates that AR should only be done 
 *                                 // for failed links. 
 *         Threshold: 3, // CCA-level at which switch uses AR.
 *         Reserved:           4);
 * 
 *     SWITCH_CAPABILITY_MASK  CapabilityMask;     // RO 
 * 
 *     CAPABILITY_MASK_COLLECTIVES  CapabilityMaskCollectives; // RW 
 *                                                 // Reserved in Gen1 
 * 
 * } PACK_SUFFIX STL_SWITCH_INFO;
 * 
 * // STL IPV4 IP Address (32 bits)
 * typedef struct {
 *     uint8   addr[4];
 * } PACK_SUFFIX STL_IPV4_IP_ADDR;
 * 
 * </pre>
 */
public class SwitchInfo extends SimpleDatagram<SwitchInfoBean> {
    public SwitchInfo() {
        super(84);
    }

    public void setLinearFDBCap(int cap) {
        buffer.putInt(0, cap);
    }

    public void setRandomFDBCap(int cap) {
        buffer.putInt(4, cap);
    }

    public void setMulticastFDBCap(int cap) {
        buffer.putInt(8, cap);
    }

    public void setLinearFDBTop(int top) {
        buffer.putInt(12, top);
    }

    public void setMulticastFDBTop(int top) {
        buffer.putInt(16, top);
    }

    public void setCollectiveCap(int cap) {
        buffer.putInt(20, cap);
    }

    public void setCollectiveTop(int top) {
        buffer.putInt(24, top);
    }

    public void setIPAddrIPV6(byte[] ip) {
        if (ip.length != SAConstants.IPV6_LENGTH) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.IPV6_LENGTH + ", got " + ip.length + ".");
        }

        buffer.position(32);
        buffer.put(ip);
    }

    public void setIPAddrIPV4(byte[] ip) {
        if (ip.length != SAConstants.IPV4_LENGTH) {
            throw new IllegalArgumentException("Invalid data length. Expect "
                    + SAConstants.IPV4_LENGTH + ", got " + ip.length + ".");
        }

        buffer.position(48);
        buffer.put(ip);
    }

    public void setDefaultPort(byte port) {
        buffer.put(64, port);
    }

    public void setDefaultMulticastPrimaryPort(byte port) {
        buffer.put(65, port);
    }

    public void setDefaultMulticastNotPrimaryPort(byte port) {
        buffer.put(66, port);
    }

    public void setU1(byte value) {
        // TODO: need a name for this field
        buffer.put(67, value);
    }

    public void setLIDsPerPort(short num) {
        buffer.putShort(68, num);
    }

    public void setPartitionEnforcementCap(short cap) {
        buffer.putShort(70, cap);
    }

    public void setSupportedRoutingMode(byte mode) {
        buffer.put(74, mode);
    }

    public void setEnabledRoutingMode(byte mode) {
        buffer.put(75, mode);
    }

    public void setCapabilities(byte capabilities) {
        buffer.put(76, capabilities);
    }

    public void setMultiCollectMask(byte mask) {
        buffer.put(77, mask);
    }

    public void setAdaptiveRouting(byte value) {
        buffer.put(78, value);
    }

    public void setCapabilityMask(short mask) {
        buffer.putShort(80, mask);
    }

    public void setCapabilityMaskCollectives(short value) {
        buffer.putShort(82, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public SwitchInfoBean toObject() {
        buffer.clear();
        SwitchInfoBean bean = new SwitchInfoBean();
        bean.setLinearFDBCap(buffer.getInt());

        buffer.getInt(); // Reserved20

        bean.setMulticastFDBCap(buffer.getInt());
        bean.setLinearFDBTop(buffer.getInt());
        bean.setMulticastFDBTop(buffer.getInt());
        bean.setCollectiveCap(buffer.getInt());
        bean.setCollectiveTop(buffer.getInt());

        // uint32 Reserved;
        buffer.getInt();

        byte[] byteVals = new byte[SAConstants.IPV6_LENGTH];
        buffer.get(byteVals);
        bean.setIpAddrIPV6(byteVals);
        byteVals = new byte[SAConstants.IPV4_LENGTH];
        buffer.get(byteVals);
        bean.setIpAddrIPV4(byteVals);

        buffer.getInt(); // Reserved26
        buffer.getInt(); // Reserved27
        buffer.getInt(); // Reserved28

        buffer.get(); // Reserved21
        buffer.get(); // Reserved22
        buffer.get(); // Reserved23

        byte byteVal = buffer.get();
        bean.setLifeTimeValue((byte) ((byteVal >> 3) & 0x1f));
        bean.setPortStateChange((byteVal & 0x4) == 0x4);

        buffer.getShort(); // Reserved24

        bean.setPartitionEnforcementCap(buffer.getShort());
        bean.setPortGroupCap(buffer.get());
        bean.setPortGroupTop(buffer.get());
        bean.setRoutingModeSupported(buffer.get());
        bean.setRoutingModeEnabled(buffer.get());
        byteVal = buffer.get();
        bean.setEnhancedPort0((byteVal & 0x08) == 0x08);
        byteVal = buffer.get();
        bean.setCollectiveMask((byte) ((byteVal >>> 3) & 0x7));
        bean.setMulticastMask((byte) (byteVal & 0x7));
        short shortVal = buffer.getShort();
        bean.setAdaptiveRoutingEnable((shortVal & 0x8000) == 0x8000);
        bean.setAdaptiveRoutingPause((shortVal & 0x4000) == 0x4000);
        bean.setAdaptiveRoutingAlgorithm((byte) ((shortVal >>> 11) & 0x07));
        bean.setAdaptiveRoutingFrequency((byte) ((shortVal >>> 8) & 0x07));
        bean.setAdaptiveRoutingLostRoutesOnly((shortVal & 0x80) == 0x80);
        bean.setAdaptiveRoutingThreshold((byte) ((shortVal >>> 4) & 0x7));
        shortVal = buffer.getShort();
        bean.setAddrRangeConfigSupported((shortVal & 0x4) == 0x4);
        bean.setAdaptiveRoutingSupported((shortVal & 0x1) == 0x1);
        shortVal = buffer.getShort();
        bean.setCapabilityMaskCollectives(shortVal);
        return bean;
    }
}
