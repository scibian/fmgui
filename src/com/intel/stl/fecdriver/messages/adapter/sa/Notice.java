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

import java.nio.ByteBuffer;

import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.fecdriver.messages.adapter.ComposedDatagram;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_mad.h v1.19
 * 
 * <pre>
 * Notice 
 * 
 * All STL fabrics should use the STL Notice structure when communicating with
 * STL devices and applications. When forwarding notices to IB applications,
 * the SM shall translate them into IB format, when IB equivalents exist.
 * 
 * STL Differences:
 *      IssuerLID is now 32 bits.
 *      Moved fields to maintain word alignment.
 *      Data and ClassTrapSpecificData combined into a single field.
 * 
 * typedef struct {
 *  union {
 *      // Generic Notice attributes 
 *      struct //_GENERIC {
 *          STL_FIELDUNION3(u, 32,
 *              IsGeneric:1,            // RO
 *              Type:7,                 // RO
 *              ProducerType:24);       // RO
 *          uint16  TrapNumber;         // RO
 *      } PACK_SUFFIX Generic;
 * 
 *      // Vendor specific Notice attributes 
 *      struct //_VENDOR {
 *          STL_FIELDUNION3(u, 32,
 *              IsGeneric:1,            // RO
 *              Type:7,                 // RO
 *              VendorID:24);           // RO
 *          uint16  DeviceID;           // RO
 *      } PACK_SUFFIX Vendor;
 *  } PACK_SUFFIX Attributes;
 * 
 *  STL_FIELDUNION2(Stats, 16, 
 *              Toggle:1,               // RW
 *              Count:15);              // RW
 * 
 *  // 8 bytes 
 *  uint32      IssuerLID;              // RO: Extended for STL
 *  uint32      Reserved2;              // Added for qword alignment
 *  // 16 bytes 
 *  IB_GID      IssuerGID;              // RO
 *  // 32 bytes 
 *  uint8       Data[64];               // RO. 
 *  // 96 bytes 
 *  uint8       ClassData[0];           // RO. Variable length.
 * } PACK_SUFFIX STL_NOTICE;
 * </pre>
 */
public class Notice extends ComposedDatagram<NoticeBean> {
    private final SimpleDatagram<Void> header;

    private SimpleDatagram<Void> classData;

    public Notice() {
        header = new SimpleDatagram<Void>(96);
        addDatagram(header);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#wrap(byte[],
     * int)
     */
    @Override
    public int wrap(byte[] data, int offset) {
        int pos = header.wrap(data, offset);
        int size = data.length - offset;
        classData = new SimpleDatagram<Void>(size - header.getLength());
        pos = classData.wrap(data, pos);
        addDatagram(classData);
        return pos;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public NoticeBean toObject() {
        NoticeBean bean = new NoticeBean(true);

        ByteBuffer buffer = header.getByteBuffer();
        buffer.clear();

        int intVal = buffer.getInt();
        boolean isGeneric = (intVal & 0x80000000) == 0x80000000;
        if (isGeneric) {
            NoticeAttr.Generic attr = new NoticeAttr.Generic();
            attr.wrap(buffer.array(), buffer.arrayOffset());
            bean.setAttributes(attr.toObject());
        } else {
            NoticeAttr.Vendor attr = new NoticeAttr.Vendor();
            attr.wrap(buffer.array(), buffer.arrayOffset());
            bean.setAttributes(attr.toObject());
        }
        buffer.position(6);
        short shortVal = buffer.getShort();
        bean.setToggle((shortVal & 0x8000) == 0x8000);
        bean.setNoticeCount((short) (shortVal & 0x7fff));
        bean.setIssuerLID(buffer.getInt());
        GID.Global gid = new GID.Global();
        gid.wrap(buffer.array(), buffer.arrayOffset() + 16);
        bean.setIssuerGID(gid.toObject());
        buffer.position(32);
        byte[] byteArray = new byte[64];
        buffer.get(byteArray);
        bean.setData(byteArray);

        buffer = classData.getByteBuffer();
        buffer.clear();
        byteArray = new byte[classData.getLength()];
        buffer.get(byteArray);
        bean.setClassData(byteArray);
        return bean;
    }

}
