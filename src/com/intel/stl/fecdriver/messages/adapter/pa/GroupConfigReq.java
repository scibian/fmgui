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

import com.intel.stl.api.performance.GroupConfigReqBean;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 *  typedef struct _STL_PA_Group_Cfg_Req {
 * [64]     char                    groupName[STL_PM_GROUPNAMELEN];
 * [80]     STL_PA_IMAGE_ID_DATA    imageId;
 *  } PACK_SUFFIX STL_PA_PM_GROUP_CFG_REQ;
 *
 *  typedef struct _STL_PA_Image_ID_Data {
 *   uint64                  imageNumber;
 *   int32                   imageOffset;
 *   union {
 *       uint32              absoluteTime;
 *       int32               timeOffset;
 *   }
 *  } PACK_SUFFIX STL_PA_IMAGE_ID_DATA;
 *
 *  #define STL_PM_GROUPNAMELEN      64
 * </pre>
 */
public class GroupConfigReq extends SimpleDatagram<GroupConfigReqBean> {
    public GroupConfigReq() {
        super(80);
    }

    public void setGroupName(String name) {
        StringUtils.setString(name, buffer, 0, PAConstants.STL_PM_GROUPNAMELEN);
    }

    public void setImageNumber(long imageNumber) {
        buffer.putLong(64, imageNumber);
    }

    public void setImageOffset(int imageOffset) {
        buffer.putInt(72, imageOffset);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public GroupConfigReqBean toObject() {
        buffer.clear();
        GroupConfigReqBean bean = new GroupConfigReqBean();
        bean.setGroupName(StringUtils.toString(buffer.array(),
                buffer.arrayOffset(), PAConstants.STL_PM_GROUPNAMELEN));
        buffer.position(64);
        bean.setImageId(new ImageIdBean(buffer.getLong(), buffer.getInt(),
                buffer.getInt()));
        return bean;
    }

}
