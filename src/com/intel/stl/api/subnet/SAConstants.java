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

public interface SAConstants {
    byte STL_SA_CLASS_VERSION = (byte) 0x80;

    byte IB_SUBN_ADM_CLASS_VERSION = 2;

    /*
     * /ALL_EMB/IbAccess/Common/Inc/stl_sa.h Subnet Administration Attribute IDs
     * Adapted from IB
     */
    short STL_SA_ATTR_CLASS_PORT_INFO = 0x0001;

    short STL_SA_ATTR_NOTICE = 0x0002;

    short STL_SA_ATTR_INFORM_INFO = 0x0003;

    short STL_SA_ATTR_NODE_RECORD = 0x0011;

    short STL_SA_ATTR_PORTINFO_RECORD = 0x0012;

    short STL_SA_ATTR_SC_MAPTBL_RECORD = 0x0013;// REPLACES SL TO VL!

    short STL_SA_ATTR_SWITCHINFO_RECORD = 0x0014;

    short STL_SA_ATTR_LINEAR_FWDTBL_RECORD = 0x0015;

    short STL_SA_ATTR_RANDOM_FWDTBL_RECORD = 0x0016;

    short STL_SA_ATTR_MCAST_FWDTBL_RECORD = 0x0017;

    short STL_SA_ATTR_SMINFO_RECORD = 0x0018;

    short STL_SA_ATTR_LINK_SPD_WDTH_PAIRS_RECORD = 0x0019;// Defined by never
                                                          // impl'ed

    // Available 0x001A-0x001F
    short STL_SA_ATTR_LINK_RECORD = 0x0020;

    // short STL_SA_ATTR_GUIDINFO_RECORD = 0x0030; // Undefined in STL.
    short STL_SA_ATTR_SERVICE_RECORD = 0x0031;

    short STL_SA_ATTR_P_KEY_TABLE_RECORD = 0x0033;

    short STL_SA_ATTR_PATH_RECORD = 0x0035;

    short STL_SA_ATTR_VLARBTABLE_RECORD = 0x0036;

    short STL_SA_ATTR_MCMEMBER_RECORD = 0x0038;

    short STL_SA_ATTR_TRACE_RECORD = 0x0039;

    short STL_SA_ATTR_MULTIPATH_GID_RECORD = 0x003A;

    short STL_SA_ATTR_SERVICEASSOCIATION_RECORD = 0x003B;

    // Available 0x003C-0x007F
    short STL_SA_ATTR_INFORM_INFO_RECORD = 0x00F3;

    /*
     * Subnet Administration Attribute IDs New for STL
     */
    short STL_SA_ATTR_SC2SL_MAPTBL_RECORD = 0x0081;

    short STL_SA_ATTR_SC2VL_NT_MAPTBL_RECORD = 0x0082;

    short STL_SA_ATTR_SC2VL_T_MAPTBL_RECORD = 0x0083;

    short STL_SA_ATTR_SC2VL_R_MAPTBL_RECORD = 0x0084;

    short STL_SA_ATTR_PGROUP_FWDTBL_RECORD = 0x0085;

    short STL_SA_ATTR_MULTIPATH_GUID_RECORD = 0x0086;

    short STL_SA_ATTR_MULTIPATH_LID_RECORD = 0x0087;

    short STL_SA_ATTR_CABLE_INFO_RECORD = 0x0088;

    short STL_SA_ATTR_VF_INFO_RECORD = 0x0089; // Previously vendor specific

    short STL_SA_ATTR_PORT_STATE_INFO_RECORD = 0x008A;

    short STL_SA_ATTR_PORTGROUP_TABLE_RECORD = 0x008B;

    short STL_SA_ATTR_BUFF_CTRL_TAB_RECORD = 0x008C;

    short STL_SA_ATTR_FABRICINFO_RECORD = 0x008D;

    // Available 0x008E-0x008F;
    short STL_SA_ATTR_QUARANTINED_NODE_RECORD = 0x0090; // Previously vendor
                                                        // specific

    short STL_SA_ATTR_CONGESTION_INFO_RECORD = 0x0091; // Previously vendor
                                                       // specific

    short STL_SA_ATTR_SWITCH_CONG_RECORD = 0x0092; // Previously vendor specific

    short STL_SA_ATTR_SWITCH_PORT_CONG_RECORD = 0x0093; // Previously vendor
                                                        // specific

    short STL_SA_ATTR_HFI_CONG_RECORD = 0x0094; // Previously vendor specific

    short STL_SA_ATTR_HFI_CONG_CTRL_RECORD = 0x0095; // Previously vendor
                                                     // specific

    short SA_ATTRIB_MCAST_FWDTBL_RECORD = 0x0017;

    /* ComponentMask bits */;

    long STL_NODE_RECORD_COMP_LID = 0x00000001;
    /* reserved field = 0x00000002 */;

    long STL_NODE_RECORD_COMP_BASEVERSION = 0x00000004;

    long STL_NODE_RECORD_COMP_CLASSVERSION = 0x00000008;

    long STL_NODE_RECORD_COMP_NODETYPE = 0x00000010;

    long STL_NODE_RECORD_COMP_NUMPORTS = 0x00000020;
    /* reserved field = 0x00000040 */;

    long STL_NODE_RECORD_COMP_SYSIMAGEGUID = 0x00000080;

    long STL_NODE_RECORD_COMP_NODEGUID = 0x00000100;

    long STL_NODE_RECORD_COMP_PORTGUID = 0x00000200;

    long STL_NODE_RECORD_COMP_PARTITIONCAP = 0x00000400;

    long STL_NODE_RECORD_COMP_DEVICEID = 0x00000800;

    long STL_NODE_RECORD_COMP_REVISION = 0x00001000;

    long STL_NODE_RECORD_COMP_LOCALPORTNUM = 0x00002000;

    long STL_NODE_RECORD_COMP_VENDORID = 0x00004000;

    long STL_NODE_RECORD_COMP_NODEDESC = 0x00008000;

    // CableInfoRecord size
    int STL_CIR_DATA_SIZE = 64;

    /*
     * /ALL_EMB/IbAccess/Common/Inc/ib_sa_records.h Subnet Administration
     * methods
     */
    byte SUBN_ADM_GET = (byte) 0x01;

    byte SUBN_ADM_GET_RESP = (byte) 0x81;

    byte SUBN_ADM_SET = (byte) 0x02;

    byte SUBN_ADM_REPORT = (byte) 0x06;

    byte SUBN_ADM_REPORT_RESP = (byte) 0x86;

    byte SUBN_ADM_GETTABLE = (byte) 0x12;

    byte SUBN_ADM_GETTABLE_RESP = (byte) 0x92;

    byte SUBN_ADM_GETTRACETABLE = (byte) 0x13; /* optional */
    byte SUBN_ADM_GETMULTI = (byte) 0x14; /* optional */

    byte SUBN_ADM_GETMULTI_RESP = (byte) 0x94; /* optional */
    byte SUBN_ADM_DELETE = (byte) 0x15;

    byte SUBN_ADM_DELETE_RESP = (byte) 0x95;

    /*
     * --------------------------------------------------------------------------
     * Link Record - details about a link in the fabric
     */
    /* ComponentMask bits */
    long IB_LINK_RECORD_COMP_FROMLID = 0x00000001;

    long IB_LINK_RECORD_COMP_FROMPORT = 0x00000002;

    long IB_LINK_RECORD_COMP_TOPORT = 0x00000004;

    long IB_LINK_RECORD_COMP_TOLID = 0x00000008;

    /*
     * --------------------------------------------------------------------------
     * Path Record - describes path between 2 end nodes in the fabric
     */
    /* ComponentMask bits */
    /*
     * CA13-6: The component mask bits for ServiceID8MSB and ServiceID56LSB
     * shall have the same value. They shall be either both set to one or both
     * cleared.
     */
    long IB_PATH_RECORD_COMP_SERVICEID = 0x00000003;

    long IB_PATH_RECORD_COMP_DGID = 0x00000004;

    long IB_PATH_RECORD_COMP_SGID = 0x00000008;

    long IB_PATH_RECORD_COMP_DLID = 0x00000010;

    long IB_PATH_RECORD_COMP_SLID = 0x00000020;

    long IB_PATH_RECORD_COMP_RAWTRAFFIC = 0x00000040;
    /* reserved field 0x00000080 */;

    long IB_PATH_RECORD_COMP_FLOWLABEL = 0x00000100;

    long IB_PATH_RECORD_COMP_HOPLIMIT = 0x00000200;

    long IB_PATH_RECORD_COMP_TCLASS = 0x00000400;

    long IB_PATH_RECORD_COMP_REVERSIBLE = 0x00000800;

    long IB_PATH_RECORD_COMP_NUMBPATH = 0x00001000;

    long IB_PATH_RECORD_COMP_PKEY = 0x00002000;

    long IB_PATH_RECORD_COMP_QOS_CLASS = 0x00004000;

    long IB_PATH_RECORD_COMP_SL = 0x00008000;

    long IB_PATH_RECORD_COMP_MTUSELECTOR = 0x00010000;

    long IB_PATH_RECORD_COMP_MTU = 0x00020000;

    long IB_PATH_RECORD_COMP_RATESELECTOR = 0x00040000;

    long IB_PATH_RECORD_COMP_RATE = 0x00080000;

    long IB_PATH_RECORD_COMP_PKTLIFESELECTOR = 0x00100000;

    long IB_PATH_RECORD_COMP_PKTLIFE = 0x00200000;

    long IB_PATH_RECORD_COMP_PREFERENCE = 0x00400000;
    /* reserved field = 0x00800000 */;

    long IB_PATH_RECORD_COMP_ALL = 0x007fff7f;

    long STL_PKEYTABLE_RECORD_COMP_LID = 0x00000001;

    long STL_PKEYTABLE_RECORD_COMP_BLOCKNUM = 0x00000002;

    long STL_PKEYTABLE_RECORD_COMP_PORTNUM = 0x00000004;

    long STL_LFT_RECORD_COMP_LID = 0x0000000000000001;

    /* Reserved 0x0000000000000002 */
    long STL_LFT_RECORD_COMP_BLOCKNUM = 0x0000000000000004;

    long STL_VLARB_COMPONENTMASK_LID = 0x0000000000000001;

    long STL_VLARB_COMPONENTMASK_OUTPORTNUM = 0x0000000000000002;

    long STL_VLARB_COMPONENTMASK_BLOCKNUM = 0x0000000000000004;

    long STL_MFTB_RECORD_COMP_LID = 0x0000000000000001;

    long STL_MFTB_RECORD_COMP_POSITIONl = 0x0000000000000002;

    /* Reserved 0x0000000000000004 */
    long STL_MFTB_RECORD_COMP_BLOCKNUM = 0x0000000000000008;

    long STL_CIR_COMP_LID = 0x1;

    long STL_CIR_COMP_PORT = 0x2;

    long STL_CIR_COMP_LEN = 0x4;

    // Reserved 0x8
    long STL_CIR_COMP_ADDR = 0x10;

    // SC2SLMT
    long STL_SC2SL_RECORD_COMP_LID = 0x0000000000000001;

    // SC2VLMT
    long STL_SC2VL_R_RECORD_COMP_LID = 0x0000000000000001;

    // Reserved2 0x20ul

    int LID_MCAST_START = 0xc000;

    int LID_MCAST_END = 0xfffe;

    // /ALL_EMB/IbaTools/iba_fequery/fe_sa.c
    byte PATHRECORD_NUMBPATH = 32;

    int STL_MAX_VLS = 32;

    int IPV6_LENGTH = 16;

    int IPV4_LENGTH = 4;

    int NODE_DESC_LENGTH = 64;

    int FDB_DATA_LENGTH = 64;

    int STL_NUM_MFT_ELEMENTS_BLOCK = 8;

    int NUM_PKEY_ELEMENTS_BLOCK = 32;

    int VLARB_TABLE_LENGTH = 128;

    int STL_NUM_LINKDOWN_REASONS = 8;

    // ALL_EMB/IbAccess/Common/Inc/stl_types.h
    int STL_MAX_SLS = 32;

    // ALL_EMB/IbAccess/Common/Inc/stl_sm.h
    int STL_CABLE_INFO_PAGESZ = 128;

    int STL_CIB_STD_START_ADDR = 128;

    // ALL_EMB/IbAccess/Common/Inc/stl_helper.h
    byte CABLEINFO_OPA_CERTIFIED = (byte) 0xab;

    byte CABLEINFO_CONNECTOR_NOSEP = (byte) 0x23;

}
