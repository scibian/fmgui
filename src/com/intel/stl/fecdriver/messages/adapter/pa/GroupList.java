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

import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.common.StringUtils;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 * <pre>
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa_types.h
 * commit b0d0c6e7e1803a2416236b3918280b0b3a0d1205
 * date 2017-07-31 13:52:56
 *
 * typedef struct _STL_PA_Group_List {
 * 	char					groupNames[STL_PM_GROUPNAMELEN];	// \0 terminated
 * } PACK_SUFFIX STL_PA_GROUP_LIST;
 * #define STL_PM_GROUPNAMELEN		64
 * </pre>
 *
 */
public class GroupList extends SimpleDatagram<GroupListBean> {
    public GroupList() {
        super(PAConstants.STL_PM_GROUPNAMELEN);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.resourceadapter.data.ComposedDatagram#toObject()
     */
    @Override
    public GroupListBean toObject() {
        buffer.clear();
        byte[] raw = buffer.array();
        String name = StringUtils.toString(raw, buffer.arrayOffset(),
                PAConstants.STL_PM_GROUPNAMELEN);
        GroupListBean bean = new GroupListBean(name);
        return bean;
    }

}
