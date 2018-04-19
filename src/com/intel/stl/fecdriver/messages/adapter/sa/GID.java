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

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.GIDAsReg32s;
import com.intel.stl.api.subnet.GIDAsReg64s;
import com.intel.stl.api.subnet.GIDBean;
import com.intel.stl.api.subnet.GIDGlobal;
import com.intel.stl.api.subnet.GIDLinkLocal;
import com.intel.stl.api.subnet.GIDMulticast;
import com.intel.stl.api.subnet.GIDSiteLocal;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/ib_type.h v1.63
 * 
 * <pre>
 * typedef union _IB_GID {
 * 	uchar	Raw[16];
 * 	struct {
 * #if CPU_BE
 * 		uint32	HH;
 * 		uint32	HL;
 * 		uint32	LH;
 * 		uint32	LL;
 * #else
 * 		uint32	LL;
 * 		uint32	LH;
 * 		uint32	HL;
 * 		uint32	HH;
 * #endif
 * 	} AsReg32s;
 * 	struct {
 * #if CPU_BE
 * 		uint64	H;
 * 		uint64	L;
 * #else
 * 		uint64	L;
 * 		uint64	H;
 * #endif
 * 	} AsReg64s;
 * 	union _IB_GID_TYPE {
 * 		struct {
 * #if CPU_BE
 * 			struct {
 * 				uint64		FormatPrefix:	10;
 * 				uint64		Reserved:		54;	// Must be zero 
 * 			} s;
 * 			EUI64		InterfaceID;
 * #else
 * 			EUI64		InterfaceID;
 * 			struct {
 * 				uint64		Reserved:		54;	// Must be zero 
 * 				uint64		FormatPrefix:	10;
 * 			} s;
 * #endif
 * 		} LinkLocal;
 * 		
 * 		struct {
 * #if CPU_BE
 * 			struct {
 * 				uint64		FormatPrefix:	10;
 * 				uint64		Reserved:		38;	// Must be zero 
 * 				uint64		SubnetPrefix:	16;
 * 			} s;
 * 			EUI64		InterfaceID;
 * #else
 * 			EUI64		InterfaceID;
 * 			struct {
 * 				uint64		SubnetPrefix:	16;
 * 				uint64		Reserved:		38;	// Must be zero 
 * 				uint64		FormatPrefix:	10;
 * 			} s;
 * #endif
 * 		} SiteLocal;
 * 		
 * 		struct {
 * #if CPU_BE
 * 			uint64		SubnetPrefix;
 * 			EUI64		InterfaceID;
 * #else
 * 			EUI64		InterfaceID;
 * 			uint64		SubnetPrefix;
 * #endif
 * 		} Global;
 * 		
 * 		struct {
 * #if CPU_BE
 * 			struct {
 * 				uint16		FormatPrefix:	8;
 * 				uint16		Flags:		4;
 * 				uint16		Scope:		4;
 * 			} s;
 * 			uchar			GroupId[14];
 * #else
 * 			uchar			GroupId[14];
 * 			struct {
 * 				uint16		Scope:			4;
 * 				uint16		Flags:			4;
 * 				uint16		FormatPrefix:	8;
 * 			} s;
 * #endif
 * 		} Multicast;
 * 	} Type;
 * } PACK_SUFFIX IB_GID;
 * </pre>
 * 
 */
public abstract class GID<E extends GIDBean> extends SimpleDatagram<E> {

    public GID() {
        super(16);
    }

    public static class AsReg32s extends GID<GIDAsReg32s> {
        public void setHH(int hh) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putInt(0, hh);
            } else {
                buffer.putInt(12, hh);
            }
        }

        public int getHH() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getInt(0);
            } else {
                return buffer.getInt(12);
            }
        }

        public void setHL(int hl) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putInt(4, hl);
            } else {
                buffer.putInt(8, hl);
            }
        }

        public int getHL() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getInt(4);
            } else {
                return buffer.getInt(8);
            }
        }

        public void setLH(int lh) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putInt(8, lh);
            } else {
                buffer.putInt(4, lh);
            }
        }

        public int getLH() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getInt(8);
            } else {
                return buffer.getInt(4);
            }
        }

        public void setLL(int ll) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putInt(12, ll);
            } else {
                buffer.putInt(0, ll);
            }
        }

        public int getLL() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getInt(12);
            } else {
                return buffer.getInt(0);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDAsReg32s toObject() {
            GIDAsReg32s bean =
                    new GIDAsReg32s(getHH(), getHL(), getLH(), getLL());
            return bean;
        }

    }

    public static class AsReg64s extends GID<GIDAsReg64s> {
        public void setH(long h) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(0, h);
            } else {
                buffer.putLong(8, h);
            }
        }

        public long getH() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(0);
            } else {
                return buffer.getLong(8);
            }
        }

        public void setL(long l) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(8, l);
            } else {
                buffer.putLong(0, l);
            }
        }

        public long getL() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(8);
            } else {
                return buffer.getLong(0);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDAsReg64s toObject() {
            GIDAsReg64s bean = new GIDAsReg64s(getH(), getL());
            return bean;
        }

    }

    public static class LinkLocal extends GID<GIDLinkLocal> {
        // TODO: test it's correct
        public void setFormatPrefix(short prefix) {
            long value = (prefix & 0x3ffL) << 54;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(0, value);
            } else {
                buffer.putLong(8, value);
            }
        }

        public short getFormatPrefix() {
            long value = 0;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                value = buffer.getLong(0);
            } else {
                value = buffer.getLong(8);
            }
            return (short) ((value >>> 54) & 0x3ff);
        }

        public void setInterfaceId(long id) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(8, id);
            } else {
                buffer.putLong(0, id);
            }
        }

        public long getInterfaceId() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(8);
            } else {
                return buffer.getLong(0);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDLinkLocal toObject() {
            GIDLinkLocal bean =
                    new GIDLinkLocal(getFormatPrefix(), getInterfaceId());
            return bean;
        }

    }

    public static class SiteLocal extends GID<GIDSiteLocal> {
        // TODO: test it's correct
        public void setFormatPrefix(short prefix) {
            long old =
                    getByteOrder() == ByteOrder.BIG_ENDIAN ? buffer.getLong(0)
                            : buffer.getLong(8);
            long value = ((prefix & 0x3ffL) << 54) | (old & 0xffff);
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(0, value);
            } else {
                buffer.putLong(8, value);
            }
        }

        public short getFormatPrefix() {
            long value = 0;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                value = buffer.getLong(0);
            } else {
                value = buffer.getLong(8);
            }
            return (short) ((value >>> 54) & 0x3ffL);
        }

        public void setSubnetPrefix(short prefix) {
            long old =
                    getByteOrder() == ByteOrder.BIG_ENDIAN ? buffer.getLong(0)
                            : buffer.getLong(8);
            long value = (old & 0xffc0000000000000L) | ((long) prefix & 0xffff);
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(0, value);
            } else {
                buffer.putLong(8, value);
            }
        }

        public short getSubnetPrefix() {
            long value = 0;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                value = buffer.getLong(0);
            } else {
                value = buffer.getLong(8);
            }
            return (short) (value & 0xffffL);
        }

        public void setInterfaceId(long id) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(8, id);
            } else {
                buffer.putLong(0, id);
            }
        }

        public long getInterfaceId() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(8);
            } else {
                return buffer.getLong(0);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDSiteLocal toObject() {
            GIDSiteLocal bean =
                    new GIDSiteLocal(getFormatPrefix(), getSubnetPrefix(),
                            getInterfaceId());
            return bean;
        }

    }

    public static class Global extends GID<GIDGlobal> {
        public Global() {
            super();
        }

        public Global(long interfaceId) {
            super();
            build(true);
            setInterfaceId(interfaceId);
        }

        public Global(long subnetPrefix, long interfaceId) {
            super();
            build(true);
            setSubnetPrefix(subnetPrefix);
            setInterfaceId(interfaceId);
        }

        public void setSubnetPrefix(long prefix) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(0, prefix);
            } else {
                buffer.putLong(8, prefix);
            }
        }

        public long getSubnetPrefix() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(0);
            } else {
                return buffer.getLong(8);
            }
        }

        public void setInterfaceId(long id) {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putLong(8, id);
            } else {
                buffer.putLong(0, id);
            }
        }

        public long getInterfaceId() {
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                return buffer.getLong(8);
            } else {
                return buffer.getLong(0);
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDGlobal toObject() {
            GIDGlobal bean = new GIDGlobal(getSubnetPrefix(), getInterfaceId());
            return bean;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Global [getSubnetPrefix()="
                    + StringUtils.longHexString(getSubnetPrefix())
                    + ", getInterfaceId()="
                    + StringUtils.longHexString(getInterfaceId()) + "]";
        }

    }

    public static class Multicast extends GID<GIDMulticast> {
        public void setFormatPrefix(byte prefix) {
            short old =
                    getByteOrder() == ByteOrder.BIG_ENDIAN ? buffer.getShort(0)
                            : buffer.getShort(14);
            short value = (short) ((prefix << 8) | (old & 0xff));
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putShort(0, value);
            } else {
                buffer.putShort(14, value);
            }
        }

        public byte getFormatPrefix() {
            short value =
                    (getByteOrder() == ByteOrder.BIG_ENDIAN) ? buffer
                            .getShort(0) : buffer.getShort(14);
            return (byte) (value >>> 8);
        }

        public void setFlags(byte flag) {
            short old =
                    getByteOrder() == ByteOrder.BIG_ENDIAN ? buffer.getShort(0)
                            : buffer.getShort(14);
            short value = (short) ((old & 0xff0f) | (flag << 4));
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putShort(0, value);
            } else {
                buffer.putShort(14, value);
            }
        }

        public byte getFlags() {
            short value = 0;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                value = buffer.getShort(0);
            } else {
                value = buffer.getShort(14);
            }
            return (byte) ((value >>> 4) & 0x0f);
        }

        public void setScope(byte scope) {
            short old =
                    getByteOrder() == ByteOrder.BIG_ENDIAN ? buffer.getShort(0)
                            : buffer.getShort(14);
            short value = (short) ((old & 0xfff0) | (scope & 0x0f));
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                buffer.putShort(0, value);
            } else {
                buffer.putShort(14, value);
            }
        }

        public byte getScope() {
            short value = 0;
            if (getByteOrder() == ByteOrder.BIG_ENDIAN) {
                value = buffer.getShort(0);
            } else {
                value = buffer.getShort(14);
            }
            return (byte) (value & 0x0f);
        }

        public void setGroupId(byte[] bytes) {
            int pos = (getByteOrder() == ByteOrder.BIG_ENDIAN) ? 14 : 0;
            buffer.position(pos);
            buffer.put(bytes, 0, 14);
        }

        public byte[] getGroupId() {
            byte[] res = new byte[14];
            int pos = (getByteOrder() == ByteOrder.BIG_ENDIAN) ? 14 : 0;
            buffer.position(pos);
            buffer.get(res);
            return res;
        }

        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GIDMulticast toObject() {
            GIDMulticast bean =
                    new GIDMulticast(getFormatPrefix(), getFlags(), getScope(),
                            getGroupId());
            return bean;
        }

    }
}
