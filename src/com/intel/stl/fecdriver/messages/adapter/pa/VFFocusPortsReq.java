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
package com.intel.stl.fecdriver.messages.adapter.pa;

import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.performance.VFFocusPortsReqBean;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa.h v1.49
 * 
 * <pre>
 *  typedef struct _STL_PA_VF_FOCUS_PORTS_REQ {
 * [64]     char                vfName[STL_PM_VFNAMELEN];
 * [72]     uint64              reserved;
 * [88]     STL_PA_IMAGE_ID_DATA imageId;
 * [92]     uint32              select;
 * [96]     uint32              start;
 * [100]     uint32              range;
 *  } PACK_SUFFIX STL_PA_VF_FOCUS_PORTS_REQ;
 *   
 *   typedef struct _STL_PA_Image_ID_Data {
 *   	uint64					imageNumber;
 *   	int32					imageOffset;
 *   	uint32					reserved;
 *   } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 * 
 * </pre>
 * 
 */
public class VFFocusPortsReq extends SimpleDatagram<VFFocusPortsReqBean> {
    public VFFocusPortsReq() {
        super(100);
    }

    public void setVfName(String name) {
        StringUtils.setString(name, buffer, 0, PAConstants.STL_PM_GROUPNAMELEN);
    }

    public void setImageNumber(long imageNumber) {
        buffer.putLong(72, imageNumber);
    }

    public void setImageOffset(int imageOffset) {
        buffer.putInt(80, imageOffset);
    }

    public void setSelect(int select) {
        buffer.putInt(88, select);
    }

    public void setStart(int start) {
        buffer.putInt(92, start);
    }

    public void setRange(int range) {
        buffer.putInt(96, range);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public VFFocusPortsReqBean toObject() {
        buffer.clear();
        VFFocusPortsReqBean bean = new VFFocusPortsReqBean();
        bean.setVfName(StringUtils.toString(buffer.array(),
                buffer.arrayOffset(), PAConstants.STL_PM_GROUPNAMELEN));
        buffer.position(72);
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt()));
        buffer.position(88);
        bean.setSelect(buffer.getInt());
        bean.setStart(buffer.getInt());
        bean.setRange(buffer.getInt());
        return bean;
    }

}
