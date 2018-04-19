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

import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.NoticeAttrBean;
import com.intel.stl.api.notice.VendorNoticeAttrBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAcess/Common/Inc/stl_mad.h v1.19
 * 
 * <pre>
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
 * </pre>
 */
public abstract class NoticeAttr<E extends NoticeAttrBean> extends
        SimpleDatagram<E> {

    public NoticeAttr() {
        super(6);
    }

    public boolean isGeneric() {
        int intVal = buffer.getInt(0);
        return (intVal & 0x80000000) == 0x80000000;
    }

    public static class Generic extends NoticeAttr<GenericNoticeAttrBean> {
        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public GenericNoticeAttrBean toObject() {
            GenericNoticeAttrBean bean = new GenericNoticeAttrBean();
            buffer.clear();
            int intVal = buffer.getInt();
            bean.setGeneric((intVal & 0x80000000) == 0x80000000);
            bean.setType((byte) ((intVal >>> 24) & 0x7f));
            bean.setProducerType(intVal & 0xffffff);
            bean.setTrapNumber(buffer.getShort());
            return bean;
        }
    }

    public static class Vendor extends NoticeAttr<VendorNoticeAttrBean> {
        /*
         * (non-Javadoc)
         * 
         * @see com.intel.hpc.stl.resourceadapter.data.SimpleDatagram#toObject()
         */
        @Override
        public VendorNoticeAttrBean toObject() {
            VendorNoticeAttrBean bean = new VendorNoticeAttrBean();
            buffer.clear();
            int intVal = buffer.getInt(0);
            bean.setGeneric((intVal & 0x80000000) == 0x80000000);
            bean.setType((byte) ((intVal >>> 24) & 0x7f));
            bean.setVendorID(intVal & 0xffffff);
            bean.setDeviceID(buffer.getShort());
            return bean;
        }
    }

}
