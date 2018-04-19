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

package com.intel.stl.ui.performance;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.performance.provider.DataProviderName;

public class GroupChartArgument extends ChartArgument<GroupSource> {
    private static final long serialVersionUID = -4053098986435251698L;

    public static final String GROUPS = "Groups";

    public static final String VFS = "VFs";

    @Override
    public void setSources(GroupSource[] groups) {
        String[] tmp = new String[groups.length];
        String key = null;
        for (int i = 0; i < tmp.length; i++) {
            if (key == null && groups[i] instanceof VFSource) {
                key = VFS;
            }
            tmp[i] = groups[i].getGroup();
        }
        if (key == null) {
            key = GROUPS;
        }
        put(key, toString(tmp));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.ChartArgument#getSources()
     */
    @Override
    public GroupSource[] getSources() {
        String str = getProperty(VFS);
        if (str != null) {
            String[] groups = toArray(str);
            VFSource[] res = new VFSource[groups.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = new VFSource(groups[i]);
            }
            return res;
        }

        str = getProperty(GROUPS);
        if (str != null) {
            String[] groups = toArray(str);
            GroupSource[] res = new GroupSource[groups.length];
            for (int i = 0; i < res.length; i++) {
                res[i] = new GroupSource(groups[i]);
            }
            return res;
        }

        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.PinArgument#getSourceDescription()
     */
    @Override
    public Map<String, String> getSourceDescription() {
        Map<String, String> res = new LinkedHashMap<String, String>();
        String provider = getProvider();
        if (provider != null
                && provider.equals(DataProviderName.VIRTUAL_FABRIC.name())) {
            String key = STLConstants.K0116_VIRTUAL_FABRIC.getValue();
            res.put(key, getProperty(VFS));
        } else {
            String key = STLConstants.K1030_GROUP_NAME.getValue();
            res.put(key, getProperty(GROUPS));
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + Arrays.hashCode(getSources());
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        GroupChartArgument other = (GroupChartArgument) obj;
        if (!Arrays.equals(getSources(), other.getSources())) {
            return false;
        }
        return true;
    }

}
