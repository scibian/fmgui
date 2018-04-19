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

import com.intel.stl.api.subnet.PathRecordBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAcess/Common/Inc/ib_sa_records.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * typedef struct _IB_PATH_RECORD {
 * 	uint64		ServiceID;
 * 	IB_GID		DGID;				// Destination GID
 * 	IB_GID		SGID;				// Source GID
 * 	uint16		DLID;				// Destination LID
 * 	uint16		SLID;				// Source LID
 * 	union {
 * 		uint32 AsReg32;
 * 		struct {
 * #if CPU_BE
 * 			uint32		RawTraffic	:1;	// 0 for IB Packet (P_Key must be valid)
 * 										// 1 for Raw Packet (No P_Key)
 * 			uint32		Reserved	:3;
 * 			uint32		FlowLabel	:20;	// used in GRH
 * 			uint32		HopLimit	:8;		// Hop limit used in GRH
 * #else
 * 			uint32		HopLimit	:8;		// Hop limit used in GRH
 * 			uint32		FlowLabel	:20;	// used in GRH
 * 			uint32		Reserved	:3;
 * 			uint32		RawTraffic	:1;	// 0 for IB Packet (P_Key must be valid)
 * 										// 1 for Raw Packet (No P_Key)
 * #endif
 * 		} PACK_SUFFIX s;
 * 	} u1;
 *
 * 	uint8		TClass;				// Traffic Class used in GRH
 * #if CPU_BE
 * 	uint8		Reversible	:1;
 * 	uint8		NumbPath	:7;		// Max number of paths to (be) return(ed)
 *
 * #else // CPU_BE
 * 	uint8		NumbPath	:7;		// Max number of paths to (be) return(ed)
 * 	uint8		Reversible	:1;
 * #endif // CPU_BE
 * 	uint16		P_Key;				// Partition Key for this path
 * 	union {
 * 		uint16			AsReg16;
 * 		struct {
 * #if CPU_BE
 * 			uint16		QosType		: 2;
 * 			uint16		Reserved2	: 2;
 * 			uint16		QosPriority	: 8;
 * 			uint16		SL			: 4;
 * #else
 * 			uint16		SL			: 4;
 * 			uint16		QosPriority	: 8;
 * 			uint16		Reserved2	: 2;
 * 			uint16		QosType		: 2;
 * #endif
 * 		} PACK_SUFFIX s;
 * 	} u2;
 *
 * #if CPU_BE
 * 	uint8		MtuSelector	:2;		// enum IB_SELECTOR
 * 	uint8		Mtu			:6;		// enum IB_MTU
 * #else
 * 	uint8		Mtu			:6;		// enum IB_MTU
 * 	uint8		MtuSelector	:2;		// enum IB_SELECTOR
 * #endif
 *
 * #if CPU_BE
 * 	uint8		RateSelector:2;		// enum IB_SELECTOR
 * 	uint8		Rate		:6;		// enum IB_STATIC_RATE
 * #else
 * 	uint8		Rate		:6;		// enum IB_STATIC_RATE
 * 	uint8		RateSelector:2;		// enum IB_SELECTOR
 * #endif
 *
 * 	// *****************************************
 * 	// *** User be aware that the CM LifeTime &
 * 	// *** TimeOut values are only 5-bit wide.
 * 	// *****************************************
 * 	//
 * 	// Accumulated packet life time for the path specified by an enumeration
 * 	// deried from 4.096 usec * 2^PktLifeTime
 * #if CPU_BE
 * 	uint8		PktLifeTimeSelector:2;	// enum IB_SELECTOR
 * 	uint8		PktLifeTime	:6;
 * #else
 * 	uint8		PktLifeTime	:6;
 * 	uint8		PktLifeTimeSelector:2;	// enum IB_SELECTOR
 * #endif
 *
 * 	uint8		Preference;	// 1.1 specific. see page 800 of volume 1
 * 	uint8		Reserved2 [6];
 * } PACK_SUFFIX IB_PATH_RECORD;
 *
 * </pre>
 *
 */
public class PathRecord extends SimpleDatagram<PathRecordBean> {
    private GID<?> sGid, dGid;

    public PathRecord() {
        super(64);
    }

    public void setServiceId(long id) {
        buffer.putLong(0, id);
    }

    public void setDGID(GID<?> gid) {
        dGid = gid;
        buffer.position(8);
        buffer.put(gid.getByteBuffer().array(),
                gid.getByteBuffer().arrayOffset(), gid.getLength());
    }

    public GID<?> getDGid() {
        if (dGid == null) {
            dGid = new GID.Global();
            dGid.wrap(buffer.array(), buffer.arrayOffset() + 8);
        }
        return dGid;
    }

    public void setSGID(GID<?> gid) {
        sGid = gid;
        buffer.position(24);
        buffer.put(gid.getByteBuffer().array(),
                gid.getByteBuffer().arrayOffset(), gid.getLength());
    }

    public GID<?> getSGid() {
        if (sGid == null) {
            sGid = new GID.Global();
            sGid.wrap(buffer.array(), buffer.arrayOffset() + 24);
        }
        return sGid;
    }

    public void setDLidLow(short lid) {
        buffer.putShort(40, lid);
    }

    public void setSLidLow(short lid) {
        buffer.putShort(42, lid);
    }

    public void setGRH(int value) {
        buffer.putInt(44, value);
    }

    public void setTClass(byte tClass) {
        buffer.put(48, tClass);
    }

    public void setReversible(boolean b) {
        byte old = buffer.get(49);
        int value = b ? (old | 0x80) : (old & 0x7f);
        buffer.put(49, (byte) value);
    }

    public void setNumPath(byte num) {
        byte old = buffer.get(49);
        int value = (old & 0x80) | (num & 0x7f);
        buffer.put(49, (byte) value);
    }

    public void setPKey(short key) {
        buffer.putShort(50, key);
    }

    // public void setQos(short value) {
    // buffer.putShort(52, value);
    // }

    public void setQosType(byte type) {
        short old = buffer.getShort(52);
        int value = ((type & 0x03) << 14) | (old & 0x3fff);
        buffer.putShort(52, (byte) value);
    }

    public void setQosPriority(byte priority) {
        short old = buffer.getShort(52);
        int value = (priority << 4) | (old & 0xf00f);
        buffer.putShort(52, (byte) value);
    }

    public void setSL(byte sl) {
        short old = buffer.getShort(52);
        int value = (sl & 0x0f) | (old & 0xfff0);
        buffer.putShort(52, (byte) value);
    }

    public void setMtu(byte value) {
        buffer.put(54, value);
    }

    public void setRate(byte value) {
        buffer.put(55, value);
    }

    public void setPktLifeTime(byte value) {
        buffer.put(56, value);
    }

    public void setPreference(byte preference) {
        buffer.put(57, preference);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
     */
    @Override
    public PathRecordBean toObject() {
        buffer.clear();
        PathRecordBean bean = new PathRecordBean();
        bean.setServiceId(buffer.getLong());
        bean.setDGid(getDGid().toObject());
        bean.setSGid(getSGid().toObject());
        buffer.position(40);
        bean.setDLid(buffer.getShort());
        bean.setSLid(buffer.getShort());
        int intVal = buffer.getInt();
        bean.setRawTraffic((intVal & 0x80000000) == 0x80000000);
        bean.setFlowLabel((intVal >>> 8) & 0xfffff);
        bean.setHopLimit((byte) (intVal & 0xff));
        bean.setTClass(buffer.get());
        byte byteVal = buffer.get();
        bean.setReversible((byteVal & 0x80) == 0x80);
        bean.setNumbPath((byte) (byteVal & 0x7f));
        bean.setPKey(buffer.getShort());
        short shortVal = buffer.getShort();
        bean.setQosType((byte) ((shortVal >>> 14) & 0x03));
        bean.setQosPriority((byte) ((shortVal >>> 4) & 0x0f));
        bean.setSL((byte) (shortVal & 0x0f));
        byteVal = buffer.get();
        bean.setMtuSelector((byte) ((byteVal >>> 6) & 0x03));
        bean.setMtu((byte) (byteVal & 0x3f));
        byteVal = buffer.get();
        bean.setRateSelector((byte) ((byteVal >>> 6) & 0x03));
        bean.setRate((byte) (byteVal & 0x3f));
        byteVal = buffer.get();
        bean.setPktLifeTimeSelector((byte) ((byteVal >>> 6) & 0x03));
        bean.setPktLifeTime((byte) (byteVal & 0x3f));
        bean.setPreference(buffer.get());
        return bean;
    }

}
