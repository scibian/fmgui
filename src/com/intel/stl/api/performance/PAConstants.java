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
package com.intel.stl.api.performance;

/**
 */
public interface PAConstants {
	byte STL_PM_CLASS_VERSION					= (byte)0x80; 	/* Performance Management version */
	/* Performance Analysis methods */
	byte STL_PA_CMD_GET                = (byte)0x01;
	byte STL_PA_CMD_GET_RESP           = (byte)0x81;
	byte STL_PA_CMD_GETTABLE           = (byte)0x12;
	byte STL_PA_CMD_GETTABLE_RESP      = (byte)0x92;

	short STL_PM_ATTRIB_ID_PORT_STATUS			= 0x40;
	short STL_PM_ATTRIB_ID_CLEAR_PORT_STATUS	= 0x41;
	short STL_PM_ATTRIB_ID_DATA_PORT_COUNTERS	= 0x42;
	short STL_PM_ATTRIB_ID_ERROR_PORT_COUNTERS	= 0x43;
	short STL_PM_ATTRIB_ID_ERROR_INFO			= 0x44;
	
	/* Performance Analysis attribute IDs */
	short STL_PA_ATTRID_GET_CLASSPORTINFO	 = 0x01;
	short STL_PA_ATTRID_GET_GRP_LIST		 = 0xA0;
	short STL_PA_ATTRID_GET_GRP_INFO		 = 0xA1;
	short STL_PA_ATTRID_GET_GRP_CFG		 	 = 0xA2;
	short STL_PA_ATTRID_GET_PORT_CTRS		 = 0xA3;
	short STL_PA_ATTRID_CLR_PORT_CTRS		 = 0xA4;
	short STL_PA_ATTRID_CLR_ALL_PORT_CTRS	 = 0xA5;
	short STL_PA_ATTRID_GET_PM_CONFIG		 = 0xA6;
	short STL_PA_ATTRID_FREEZE_IMAGE		 = 0xA7;
	short STL_PA_ATTRID_RELEASE_IMAGE		 = 0xA8;
	short STL_PA_ATTRID_RENEW_IMAGE			 = 0xA9;
	short STL_PA_ATTRID_GET_FOCUS_PORTS	 	 = 0xAA;
	short STL_PA_ATTRID_GET_IMAGE_INFO	 	 = 0xAB;
	short STL_PA_ATTRID_MOVE_FREEZE_FRAME	 = 0xAC;
	short STL_PA_ATTRID_GET_VF_LIST      	 = 0xAD;
	short STL_PA_ATTRID_GET_VF_INFO      	 = 0xAE;
	short STL_PA_ATTRID_GET_VF_CONFIG    	 = 0xAF;
	short STL_PA_ATTRID_GET_VF_PORT_CTRS 	 = 0xB0;
	short STL_PA_ATTRID_CLR_VF_PORT_CTRS 	 = 0xB1;
	short STL_PA_ATTRID_GET_VF_FOCUS_PORTS 	 = 0xB2;
	
	int STL_PM_GROUPNAMELEN = 64;
	int STL_PM_VFNAMELEN = 64;
	int STL_PM_UTIL_GRAN_PERCENT = 10;
	int STL_PM_UTIL_BUCKETS = (100 / STL_PM_UTIL_GRAN_PERCENT);
	int PM_ERR_GRAN_PERCENT = 25;
	int PM_ERR_BUCKETS = ((100/PM_ERR_GRAN_PERCENT)+1);
}
