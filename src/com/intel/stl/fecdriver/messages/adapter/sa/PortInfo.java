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

import java.nio.ByteOrder;

import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortStatesBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_sm_types.h
 * commit a86e948b247e4d9fd98434e350b00f112ba93c39
 * date 2017-08-16 10:28:01
 *
 *  NOTE - first-pass ordering of PortInfo members:
 *    1  RW members before RO members;
 *    2  Roughly prioritize RW and RO sections;
 *    3  No separation of RO and RW members within sub-structures.
 *
 *   Attribute Modifier as NNNN NNNN 0000 0000 0000 000A PPPP PPPP
 *
 *   N = number of ports
 *   A = 1 - All ports starting at P
 *   P = port number
 *
 *  typedef struct {
 * [0]  STL_LID_32  LID;                // RW/HSPE H-PE: base LID of this node
 *                                   //               POD: 0
 *                                   //         -S--: base LID of neighbor node
 *                                   //               POD/LUD: 0
 *
 * [4]  uint32  FlowControlMask;        // RW/HS-- Flow control mask (1 bit per VL)
 *                                   // POD/LUD: flow control enabled all VLs except VL15
 *
 * [8]  struct {
 * [8]      uint8   PreemptCap;
 *
 * [9]      struct { IB_BITFIELD2( uint8,
 *           Reserved:       3,
 *           Cap:            5 )     // RO/HS-E Virtual Lanes supported on this port
 *          } s2;
 *
 * [10]      uint16  HighLimit;          // RW/HS-E Limit of high priority component of
 *                                   //  VL Arbitration table
 *                                   // POD: 0
 * [12]      uint16  PreemptingLimit;    // RW/HS-E Limit of preempt component of
 *                                   //  VL Arbitration table
 *                                   // POD: 0
 *           union {
 * [14]        uint8   ArbitrationHighCap; // RO/HS-E
 *           };
 * [15]      uint8   ArbitrationLowCap;  // RO/HS-E
 *      } VL;
 *
 * [16]  STL_PORT_STATES  PortStates;        // Port states
 *
 * [20]  STL_FIELDUNION2(PortPhyConfig,8,
 *           Reserved:4,             // Reserved
 *           PortType:4);            // RO/HS-- PORT_TYPE
 *
 * [21]  struct { IB_BITFIELD3( uint8,   // Multicast/Collectives masks
 *       Reserved:           2,
 *       CollectiveMask:     3,  // RW/H--- Num of additional upper 1s in
 *                               // Collective address
 *                               // POD: 1
 *                               // Reserved in Gen1
 *       MulticastMask:      3 ) // RW/H--- Num of upper 1s in Multicast address
 *                               // POD: 4
 *       } MultiCollectMask;
 *
 * [22]  struct { IB_BITFIELD3( uint8,
 *       M_KeyProtectBits:   2,      // RW/H-PE see mgmt key usage
 *       Reserved:           2,      // reserved, shall be zero
 *       LMC:                4 )     // RW/HSPE LID mask for multipath support
 *                                   //    H---: POD: 0
 *                                   //    --PE: POD/LUD: 0
 *                                   //    -S--: LID mask for Neighbor node
 *                                   //      POD/LUD: 0
 *       } s1;
 *
 * [23]  struct { IB_BITFIELD2( uint8,
 *       Reserved:           3,
 *       MasterSMSL:         5 ) // RW/H-PE The adminstrative SL of the master
 *                               // SM that is managing this port
 *       } s2;
 *
 * [24]  struct { IB_BITFIELD5( uint8,
 *       LinkInitReason:                 4,  //RW/HSPE POD: 1, see STL_LINKINIT_REASON
 *       PartitionEnforcementInbound:    1,  // RW/-S--
 *                                           // LUD: 1 neighbor is HFI, 0 else
 *       PartitionEnforcementOutbound:   1,  // RW/-S--
 *                                           // LUD: 1 neighbor is HFI, 0 else
 *       Reserved20:         1,
 *       Reserved21:         1 )
 *       } s3;
 *
 * [25]  struct { IB_BITFIELD2( uint8,
 *       Reserved:           3,
 *       OperationalVL:      5 )         // RW/HS-E Virtual Lanes operational this port
 *       } s4;
 *
 * [26]  struct {                        // STL Partial P_Keys
 * [26]      uint16  P_Key_8B;           // RW/HS-E Implicit 8B P_Key
 * [28]      uint16  P_Key_10B;          // RW/HS-E Partial upper 10B P_Key
 *                                   //  (12 bits, lower 4 bits reserved)
 *       } P_Keys;                       // POD/LUD: 0
 *
 * [30]  struct {
 * [30]      uint16  M_Key;      // RW/H-PE
 * [32]      uint16  P_Key;      // RW/H-PE
 * [34]      uint16  Q_Key;      // RW/H-PE
 *       } Violations;           // POD: 0
 *
 * [36]  STL_FIELDUNION2(SM_TrapQP, 32,
 *         Reserved:   8,
 *         QueuePair:  24 );        // RW/HS-E SM Trap QP. POD/LUD: 0
 *
 * [40]  STL_FIELDUNION2(SA_QP, 32,
 *         Reserved:   8,
 *         QueuePair:  24 );        // RW/HS-E SA QP. POD/LUD: 1
 *
 * [44]  uint8   NeighborPortNum;    // RO/HS-- Port number of neighbor node
 *
 * [45]  uint8   LinkDownReason;     // RW/HS-E Link Down Reason (see STL_LINKDOWN_REASON_XXX)
 *                               // POD: 0
 *
 * [46]  uint8   NeighborLinkDownReason; // RW/HS-E Neighbor Link Down Reason - STL_LINKDOWN_REASON
 *                               // POD: 0
 * [47]  struct { IB_BITFIELD3( uint8,
 *       ClientReregister:   1,  // RW/H-PE POD/LUD: 0
 *       MulticastPKeyTrapSuppressionEnabled:2,  // RW/H-PE
 *       Timeout:            5 ) // RW/H-PE Timer value used for subnet timeout
 *       } Subnet;
 *
 * [48]  struct {                    // Link speed (see STL_LINK_SPEED_XXX) LinkBounce
 * [48]      uint16  Supported;      // RO/HS-E Supported link speed
 * [50]      uint16  Enabled;        // RW/HS-E Enabled link speed POD: = supported
 * [52]      uint16  Active;         // RO/HS-E Active link speed
 *       } LinkSpeed;
 *
 * [54]  struct {                    // 9(12) of each 16 bits used (see STL_LINK_WIDTH_XXX)
 *                               // LinkBounce
 * [54]      uint16  Supported;      // RO/HS-E Supported link width
 * [56]      uint16  Enabled;        // RW/HS-E Enabled link width POD: = supported
 * [58]      uint16  Active;         // RO/HS-E link width negotiated by LNI
 *       } LinkWidth;
 *
 * [60]  struct {                    // Downgrade of link on error (see STL_LINK_WIDTH_XXX)
 * [60]      uint16  Supported;      // RO/HS-E Supported downgraded link width
 * [62]      uint16  Enabled;        // RW/HS-E Enabled link width downgrade
 *                                   // POD/LUD: = supported
 * [64]      uint16  TxActive;       // RO/HS-E Currently active link width in tx dir
 * [66]      uint16  RxActive;       // RO/HS Currently active link width in Rx dir
 *       } LinkWidthDowngrade;
 *
 * [68]  STL_FIELDUNION4(PortLinkMode,16,    // STL/Eth Port Link Modes
 *                                       // (see STL_PORT_LINK_MODE_XXX)
 *       Reserved:   1,
 *       Supported:  5,                  // RO/HS-E Supported port link mode
 *       Enabled:    5,                  // RW/HS-E Enabled port link mode POD: from FW INI
 *       Active:     5 );                // RO/HS-E Active port link mode
 *
 * [70]  STL_FIELDUNION4(PortLTPCRCMode, 16, // STL Port LTP CRC Modes
 *                                       // (see STL_PORT_LTP_CRC_MODE_XXX)
 *       Reserved:   4,
 *       Supported:  4,                  // RO/HS-E Supported port LTP mode
 *       Enabled:    4,                  // RW/HS-E Enabled port LTP mode POD: from FW INI
 *       Active:     4 );                // RO/HS-E Active port LTP mode
 *
 * [72]  STL_FIELDUNION7(PortMode, 16,       // General port modes
 *       Reserved:               9,
 *       IsActiveOptimizeEnabled:    1,  // RW/HS-- Optimized Active handling
 *                                       // POD/LUD: 0
 *       IsPassThroughEnabled:   1,      // RW/-S-- Pass-Through LUD: 0
 *       IsVLMarkerEnabled:      1,      // RW/HS-- VL Marker LUD: 0
 *       Reserved2:              2,
 *       Is16BTrapQueryEnabled:  1,      // RW/H-PE 16B Traps & SA/PA Queries (else 9B)
 *                                       // LUD: 0
 *       Reserved3:              1 );    // RW/-S-- SMA Security Checking
 *                                       // LUD: 1
 *
 * [74]  struct {                        // Packet formats
 *                                   // (see STL_PORT_PACKET_FORMAT_XXX)
 * [74]      uint16  Supported;          // RO/HSPE Supported formats
 * [76]      uint16  Enabled;            // RW/HSPE Enabled formats
 *       } PortPacketFormats;
 *
 * [78]  struct {                        // Flit control LinkBounce
 * [78]      union {
 *           uint16  AsReg16;
 *           struct { IB_BITFIELD5( uint16,  // Flit interleaving
 *               Reserved:           2,
 *               DistanceSupported:  2,  // RO/HS-E Supported Flit distance mode
 *                                       // (see STL_PORT_FLIT_DISTANCE_MODE_XXX)
 *               DistanceEnabled:    2,  // RW/HS-E Enabled Flit distance mode
 *                                       // (see STL_PORT_FLIT_DISTANCE_MODE_XXX)
 *                                       // LUD: mode1
 *               MaxNestLevelTxEnabled:      5,  // RW/HS-E Max nest level enabled Flit Tx
 *                                               // LUD: 0
 *               MaxNestLevelRxSupported:    5 ) // RO/HS-E Max nest level supported Flit Rx
 *           } s;
 *           } Interleave;
 *
 * [80]      struct Preemption_t {               // Flit preemption
 * [80]          uint16  MinInitial; // RW/HS-E Min bytes before preemption Head Flit
 *                                   // Range 8 to 10240 bytes
 * [82]          uint16  MinTail;    // RW/HS-E Min bytes before preemption Tail Flit
 *                                   // Range 8 to 10240 bytes
 * [84]          uint8   LargePktLimit;  // RW/HS-E Size of packet that can be preempted
 *                                   // Packet Size >= 512+(512*LargePktLimit)
 *                                   // Packet Size Range >=512 to >=8192 bytes
 * [85]          uint8   SmallPktLimit;  // RW/HS-E Size of packet that can preempt
 *                                   // Packet Size <= 32+(32*SmallPktLimit)
 *                                   // Packet Size Range <=32 to <=8192 bytes
 *                                   // MaxSmallPktLimit sets upper bound allowed
 * [86]          uint8   MaxSmallPktLimit;// RO/HS-E Max value for SmallPktLimit
 *                                   // Packet Size <= 32+(32*MaxSmallPktLimit)
 *                                   // Packet Size Range <=32 to <=8192 bytes
 * [87]          uint8   PreemptionLimit;// RW/HS-E Num bytes of preemption
 *                                   // limit = (256*PreemptionLimit)
 *                                   // Limit range 0 to 65024, 0xff=unlimited
 *           } Preemption;
 *
 *       } FlitControl;
 *
 * [88]  uint32  Reserved13;
 *
 * [92]  union _PortErrorAction {
 *           uint32  AsReg32;
 *           struct { IB_BITFIELD25( uint32,     // RW/HS-E Port Error Action Mask
 *                                           // POD: 0
 *           ExcessiveBufferOverrun:         1,
 *           Reserved:                       7,
 *           FmConfigErrorExceedMulticastLimit:  1,
 *           FmConfigErrorBadControlFlit:    1,
 *           FmConfigErrorBadPreempt:        1,
 *           FmConfigErrorUnsupportedVLMarker:   1,
 *           FmConfigErrorBadCrdtAck:        1,
 *           FmConfigErrorBadCtrlDist:       1,
 *           FmConfigErrorBadTailDist:       1,
 *           FmConfigErrorBadHeadDist:       1,
 *           Reserved2:                      2,
 *           PortRcvErrorBadVLMarker:        1,
 *           PortRcvErrorPreemptVL15:        1,
 *           PortRcvErrorPreemptError:       1,
 *           Reserved3:                      1,
 *           PortRcvErrorBadMidTail:         1,
 *           PortRcvErrorReserved:           1,
 *           PortRcvErrorBadSC:              1,
 *           PortRcvErrorBadL2:              1,
 *           PortRcvErrorBadDLID:            1,
 *           PortRcvErrorBadSLID:            1,
 *           PortRcvErrorPktLenTooShort:     1,
 *           PortRcvErrorPktLenTooLong:      1,
 *           PortRcvErrorBadPktLen:          1,
 *           Reserved4:                      1 )
 *          } s;
 *       } PortErrorAction;
 *
 * [96]  struct {                    // Pass through mode control
 * [96]      uint8   EgressPort;     // RW/-S-- Egress port: 0-disable pass through
 *                               // LUD: 0
 *
 * [97]      IB_BITFIELD2( uint8,
 *       Reserved:   7,
 *       DRControl:  1 )         // RW/-S-- DR: 0-normal process, 1-repeat on egress port
 *                               // LUD: 0
 *
 *       } PassThroughControl;
 *
 * [98]  uint16  M_KeyLeasePeriod;   // RW/H-PE LUD: 0
 *
 * [100]  STL_FIELDUNION5(BufferUnits, 32, // VL bfr & ack unit sizes (bytes)
 *       Reserved:       9,
 *       VL15Init:       12,     // RO/HS-E Initial VL15 units (N)
 *       VL15CreditRate: 5,      // RW/HS-E VL15 Credit rate (32*2^N)
 *                                   // LUD: if neighbor is STL HFI: 18, otherwise 0
 *       CreditAck:      3,      // RO/HS-E Credit ack unit (BufferAlloc*2^N)
 *       BufferAlloc:    3 );    // RO/HS-E Buffer alloc unit (8*2^N)
 *
 * [104]  uint32  Reserved14;
 *
 * [108]  STL_LID_32  MasterSMLID;    // RW/H-PE The base LID of the master SM that is
 *                               // managing this port
 *                               // POD/LUD: 0
 *
 * [112]  uint64  M_Key;              // RW/H-PE The 8-byte management key
 *                               // POD/LUD: 0
 *
 * [120]  uint64  SubnetPrefix;       // RW/H-PE Subnet prefix for this port
 *                               // Set to default value if no
 *                               // other subnet interaction
 *                               // POD: 0xf8000000:00000000
 *
 * [128]  STL_VL_TO_MTU  NeighborMTU[STL_MAX_VLS / 2];    // RW/HS-E Neighbor MTU values per VL
 *                                                   // VL15 LUD: 2048 STL mode
 *
 * [144]  struct XmitQ_t { IB_BITFIELD2( uint8,   // Transmitter Queueing Controls
 *                                   // per VL
 *       VLStallCount:   3,          // RW/-S-- Applies to switches only
 *                                   // LUD: 7
 *       HOQLife:        5 )         // RW/-S-- Applies to routers & switches only
 *                                   // LUD: infinite
 *        } XmitQ[STL_MAX_VLS];
 *
 *  // END OF RW SECTION
 *
 *  // BEGINNING OF RO SECTION
 * [176]  STL_IPV6_IP_ADDR  IPAddrIPV6;    // RO/H-PE IP Address - IPV6
 *
 * [192]  STL_IPV4_IP_ADDR  IPAddrIPV4;  // RO/H-PE IP Address - IPV4
 *
 * [196]  uint32 Reserved26;
 *        uint32 Reserved27;
 *        uint32 Reserved28;
 *
 * [208]  uint64  NeighborNodeGUID;   // RO/-S-E GUID of neighbor connected to this port
 *
 * [216]  STL_CAPABILITY_MASK  CapabilityMask;    // RO/H-PE Capability Mask
 *
 * [220]  uint16  Reserved20;
 *
 * [222]  STL_CAPABILITY_MASK3  CapabilityMask3;  // RO/H-PE Capability Mask 3
 *
 * [224]  uint32  Reserved23;
 *
 * [228]  uint16  OverallBufferSpace;     // RO/HS-E Overall dedicated + shared space
 *
 * [230]  uint16  Reserved21;
 *
 * [232]  STL_FIELDUNION3(DiagCode, 16,   // RO/H-PE Diagnostic code, Refer Node Diagnostics
 *       UniversalDiagCode:      4,
 *       VendorDiagCode:         11,
 *       Chain:                  1 );
 *
 * [234]  struct {                        // Replay depths
 * [234]      uint8   BufferDepth;        // RO/HS-E Replay buffer depth in LTP units
 * [235]      uint8   WireDepth;          // RO/HS-E Replay wire depth in LTP units
 *        } ReplayDepth;
 *
 * [236]  struct { IB_BITFIELD4( uint8,   // RO/HS-E Port modes based on neighbor
 *       Reserved:               4,
 *       MgmtAllowed:            1,  // RO/H--- neighbor allows this node to be mgmt
 *                                   // Switch: mgmt is allowed for neighbor
 *                                   // EP0: mgmt is allowed for port
 *       NeighborFWAuthenBypass: 1,  // RO/-S-E 0=Authenticated, 1=Not Authenticated
 *       NeighborNodeType:       2 ) // RO/-S-E 0=WFR (not trusted), 1=PRR (trusted)
 *        } PortNeighborMode;
 *
 * [237]  struct { IB_BITFIELD2( uint8,
 *       Reserved20:     4,
 *       Cap:            4 )         // RO/HS-E Max MTU supported by this port
 *        } MTU;
 *
 * [238]  struct { IB_BITFIELD2( uint8,
 *       Reserved:   3,
 *       TimeValue:  5 )             // RO/H-PE
 *        } Resp;
 *
 * [239]  uint8   LocalPortNum;           // RO/HSPE The link port number this SMP came on in
 *
 * [240]  uint8   Reserved25;
 * [241]  uint8   Reserved24;
 *
 *  } PACK_SUFFIX STL_PORT_INFO;
 *
 *  typedef union {
 *     uint32  AsReg32;
 *     struct { IB_BITFIELD8( uint32,  // Port states
 *         Reserved:                   9,
 *         LEDEnabled:                 1,  // RW/HS-- Set to 1 if the port LED is active.
 *         IsSMConfigurationStarted:   1,  // RO/HS-E - POD/LUD: 0
 *         NeighborNormal:             1,  // RO/HS--
 *                                         // POD/LUD: 0
 *         OfflineDisabledReason:      4,  // RO/HS-E Reason for Offline (see STL_OFFDIS_REASON_XXX)
 *         Reserved2:                  8,
 *         PortPhysicalState:          4,  // RW/HS-E Port Physical State (see STL_PORT_PHYS_XXX)
 *         PortState:                  4 ) // RW/HS-E Port State (see STL_PORT_XXX)
 *     } s;
 *  } STL_PORT_STATES;
 *
 * typedef union {
 *     uint8   AsReg8;
 *     struct { IB_BITFIELD2( uint8,   // RW/HS-E Neighbor MTU values per VL
 *                                     // LUD: 2048 MTU for STL VL15
 *                 VL0_to_MTU:     4,
 *                 VL1_to_MTU:     4 )
 *     } s;
 * } STL_VL_TO_MTU;
 *
 *  typedef struct {
 *     uint8   addr[16];
 *  } PACK_SUFFIX STL_IPV6_IP_ADDR;
 *
 *  typedef STL_FIELDUNION16(STL_CAPABILITY_MASK, 32,
 *       CmReserved6:                        1,      // shall be zero
 *       CmReserved24:                       2,      // shall be zero
 *       CmReserved5:                        2,      // shall be zero
 *       CmReserved23:                       4,      // shall be zero
 *       IsCapabilityMaskNoticeSupported:    1,
 *       CmReserved22:                       1,      // shall be zero
 *       IsVendorClassSupported:             1,
 *       IsDeviceManagementSupported:        1,
 *       CmReserved21:                       2,      // shall be zero
 *       IsConnectionManagementSupported:    1,
 *       CmReserved25:                      10,      // shall be zero
 *       IsAutomaticMigrationSupported:      1,
 *       CmReserved2:                        1,      // shall be zero
 *       CmReserved20:                       2,
 *       IsSM:                               1,
 *       CmReserved1:                        1 );    // shall be zero
 *
 *  // Capability Mask 3 - a bit set to 1 for affirmation of supported capability
 *   * by a given port
 *
 *  typedef STL_FIELDUNION9(STL_CAPABILITY_MASK3, 16,          // RO/H-PE
 *       CmReserved:                 8,
 *       IsSnoopSupported:           1,      // RO/--PE Packet snoop
 *                                           // Reserved in Gen1
 *       IsAsyncSC2VLSupported:      1,      // RO/H-PE Port 0 indicates whole switch
 *       IsAddrRangeConfigSupported: 1,      // RO/H-PE Can addr range for Multicast
 *                                           // and Collectives be configured
 *                                           // Port 0 indicates whole switch
 *       IsPassThroughSupported:     1,      // RO/--PE Packet pass through
 *                                           // Port 0 indicates whole switch
 *       IsSharedSpaceSupported:     1,      // RO/H-PE Shared Space
 *                                           // Port 0 indicates whole switch
 *       CmReserved2:                1,
 *       IsVLMarkerSupported:        1,      // RO/H-PE VL Marker
 *                                           // Port 0 indicates whole switch
 *       IsVLrSupported:             1 );     // RO/H-PE SC->VL_r table
 *                                           // Reserved in Gen1
 *                                           // Port 0 indicates whole switch
 *
 *  #define STL_MAX_VLS         32          // Max number of VLs
 * </pre>
 *
 */
public class PortInfo extends SimpleDatagram<PortInfoBean> {
    private VirtualLane virtualLane = null;

    private FlitControl flitControl = null;

    public PortInfo() {
        super(242);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vieo.fv.resource.stl.data.SimpleDatagram#build(boolean,
     * java.nio.ByteOrder)
     */
    @Override
    public int build(boolean force, ByteOrder order) {
        int res = super.build(force, order);
        virtualLane = new VirtualLane();
        byte[] bytes = buffer.array();
        virtualLane.wrap(bytes, buffer.arrayOffset() + 8, order);
        flitControl = new FlitControl();
        flitControl.wrap(bytes, 78, order);
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.vieo.fv.resource.stl.data.SimpleDatagram#wrap(byte[], int,
     * java.nio.ByteOrder)
     */
    @Override
    public int wrap(byte[] data, int offset, ByteOrder order) {
        int res = super.wrap(data, offset, order);
        virtualLane = new VirtualLane();
        virtualLane.wrap(data, offset + 8, order);
        flitControl = new FlitControl();
        flitControl.wrap(data, offset + 78, order);
        return res;
    }

    public void setLid(int lid) {
        buffer.putInt(0, lid);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public PortInfoBean toObject() {
        buffer.clear();
        PortInfoBean bean = new PortInfoBean();
        bean.setLid(buffer.getInt());
        bean.setFlowControlMask(buffer.getInt());
        bean.setVl(virtualLane.toObject());
        buffer.position(16);
        int intVal = buffer.getInt();
        PortStatesBean psb = new PortStatesBean((intVal & 0x400000) == 0x400000,
                (intVal & 0x200000) == 0x200000,
                (intVal & 0x100000) == 0x100000,
                (byte) ((intVal >>> 16) & 0x0f), (byte) ((intVal >>> 4) & 0x0f),
                (byte) (intVal & 0x0f));
        bean.setPortStates(psb);
        bean.setPortType((byte) (buffer.get() & 0x0f));
        byte byteVal = buffer.get();
        bean.setCollectiveMask((byte) ((byteVal >>> 3) & 0x7));
        bean.setMulticastMask((byte) (byteVal & 0x7));
        byteVal = buffer.get();
        bean.setMKeyProtectBits((byte) ((byteVal >>> 6) & 0x3));
        bean.setLmc((byte) (byteVal & 0xf));
        byteVal = buffer.get();
        bean.setMasterSMSL((byte) (byteVal & 0x1f));
        byteVal = buffer.get();
        bean.setLinkInitReason((byte) ((byteVal >>> 4) & 0x0f));
        bean.setPartitionEnforcementInbound((byteVal & 0x8) == 0x08);
        bean.setPartitionEnforcementOutbound((byteVal & 0x4) == 0x4);
        byteVal = buffer.get();
        bean.setOperationalVL((byte) (byteVal & 0x1f));
        bean.setPKey8B(buffer.getShort());
        bean.setPKey10B(buffer.getShort());
        bean.setMKeyViolation(buffer.getShort());
        bean.setPKeyViolation(buffer.getShort());
        bean.setQKeyViolation(buffer.getShort());
        intVal = buffer.getInt();
        bean.setSmTrapQueuePair(intVal & 0xffffff);
        intVal = buffer.getInt();
        bean.setSaQueuePair(intVal & 0xffffff);
        bean.setNeighborPortNum(buffer.get());
        bean.setLinkDownReason(buffer.get());
        bean.setNeighborLinkDownReason(buffer.get());
        byteVal = buffer.get();
        bean.setClientReregister((byteVal & 0x80) == 0x80);
        bean.setMulPKeyTrapSuppressionEnabled((byte) ((byteVal >>> 5) & 0x3));
        bean.setSubnetTimeout((byte) (byteVal & 0x1f));
        bean.setLinkSpeedSupported(buffer.getShort());
        bean.setLinkSpeedEnabled(buffer.getShort());
        bean.setLinkSpeedActive(buffer.getShort());
        bean.setLinkWidthSupported(buffer.getShort());
        bean.setLinkWidthEnabled(buffer.getShort());
        bean.setLinkWidthActive(buffer.getShort());
        bean.setLinkWidthDownSupported(buffer.getShort());
        bean.setLinkWidthDownEnabled(buffer.getShort());
        bean.setLinkWidthDownTxActive(buffer.getShort());
        bean.setLinkWidthDownRxActive(buffer.getShort());
        buffer.position(68);
        short shortVal = buffer.getShort();
        bean.setPlmSupported((byte) ((shortVal >>> 10) & 0x1f));
        bean.setPlmEnabled((byte) ((shortVal >>> 5) & 0x1f));
        bean.setPlmActive((byte) (shortVal & 0x1f));
        shortVal = buffer.getShort();
        bean.setPLTPCRCModeSupported((byte) ((shortVal >>> 8) & 0xf));
        bean.setPLTPCRCModeEnabled((byte) ((shortVal >>> 4) & 0xf));
        bean.setPLTPCRCModeActive((byte) (shortVal & 0xf));
        shortVal = buffer.getShort();
        bean.setActiveOptimizeEnabled((shortVal & 0x40) == 0x40);
        bean.setPassThroughEnabled((shortVal & 0x20) == 0x20);
        bean.setVLMarkerEnabled((shortVal & 0x10) == 0x10);
        bean.set16BTrapQueryEnabled((shortVal & 0x2) == 0x2);
        bean.setPpfSupported(buffer.getShort());
        bean.setPpfEnabled(buffer.getShort());
        bean.setFlitControl(flitControl.toObject());
        // uint32 Reserved13;
        buffer.position(92);
        bean.setPortErrorAction(buffer.getInt());
        bean.setEgressPort(buffer.get());
        bean.setDrControl((buffer.get() & 0x1) == 0x1);
        bean.setMKeyLeasePeriod(buffer.getShort());
        intVal = buffer.getInt();
        bean.setVl15Init((short) ((intVal >>> 11) & 0xfff));
        bean.setVl15CreditRate((byte) ((intVal >>> 6) & 0x1f));
        bean.setCreditAck((byte) ((intVal >>> 3) & 0x7));
        bean.setBufferAlloc((byte) (intVal & 0x1f));
        // uint32 Reserved14;
        buffer.position(108);
        bean.setMasterSMLID(buffer.getInt());
        bean.setMKey(buffer.getLong());
        bean.setSubnetPrefix(buffer.getLong());
        byte[] byteVals = new byte[SAConstants.STL_MAX_VLS / 2];
        buffer.get(byteVals);
        byte[] vl0MTU = new byte[byteVals.length];
        byte[] vl1MTU = new byte[byteVals.length];
        for (int i = 0; i < byteVals.length; i++) {
            vl0MTU[i] = (byte) ((byteVals[i] >>> 4) & 0x0f);
            vl1MTU[i] = (byte) (byteVals[i] & 0x0f);
        }
        bean.setNeighborVL0MTU(vl0MTU);
        bean.setNeighborVL1MTU(vl1MTU);
        byteVals = new byte[SAConstants.STL_MAX_VLS];
        buffer.get(byteVals);
        byte[] vlStallCounts = new byte[SAConstants.STL_MAX_VLS];
        byte[] hoqLifes = new byte[SAConstants.STL_MAX_VLS];
        for (int i = 0; i < byteVals.length; i++) {
            vlStallCounts[i] = (byte) ((byteVals[i] >>> 5) & 0x7);
            hoqLifes[i] = (byte) (byteVals[i] & 0x1f);
        }
        bean.setVlStallCount(vlStallCounts);
        bean.setHoqLife(hoqLifes);
        byteVals = new byte[SAConstants.IPV6_LENGTH];
        buffer.get(byteVals);
        bean.setIpAddrIPV6(byteVals);
        byteVals = new byte[SAConstants.IPV4_LENGTH];
        buffer.get(byteVals);
        bean.setIpAddrIPV4(byteVals);
        buffer.position(208);
        bean.setNeighborNodeGUID(buffer.getLong());
        bean.setCapabilityMask(buffer.getInt());
        buffer.position(222);
        bean.setCapabilityMask3(buffer.getShort());
        buffer.position(228);
        bean.setOverallBufferSpace(buffer.getShort());
        buffer.position(232);
        shortVal = buffer.getShort();
        bean.setUniversalDiagCode((byte) ((shortVal >>> 12) & 0xf));
        bean.setVendorDiagCode((short) ((shortVal >>> 1) & 0x7ff));
        bean.setChain((shortVal & 0x1) == 0x1);
        bean.setBufferDepth(buffer.get());
        bean.setWireDepth(buffer.get());
        byteVal = buffer.get();
        bean.setMgmtAllowed((byteVal & 0x8) == 0x8);
        bean.setNeighborFWAuthenBypass((byteVal & 0x4) == 0x4);
        bean.setNeighborNodeType((byte) (byteVal & 0x3));
        byteVal = buffer.get();
        bean.setMtuCap((byte) (byteVal & 0xf));
        byteVal = buffer.get();
        bean.setRespTimeValue((byte) (byteVal & 0x1f));
        bean.setLocalPortNum(buffer.get());
        return bean;
    }
}
