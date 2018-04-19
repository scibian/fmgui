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

package com.intel.stl.api.performance.impl;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class PMConfigCacheImpl extends MemoryCache<PMConfigBean>
        implements PMConfigCache {
    private final PAHelper helper;

    /**
     * Description:
     *
     * @param cacheMgr
     */
    public PMConfigCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        helper = cacheMgr.getPAHelper();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.performance.impl.PmConfCache#getPMConfig()
     */
    @Override
    public PMConfigBean getPMConfig() {
        PMConfigBean res = getCachedObject();
        if (res == null) {
            // happens after we reset this cache
            updateCache();
            res = getCachedObject();
        }
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.configuration.MemoryCache#retrieveObjectForCache()
     */
    @Override
    protected PMConfigBean retrieveObjectForCache() throws Exception {
        PMConfigBean res = helper.getPMConfig();
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.configuration.BaseCache#refreshCache(com.intel.stl.api.
     * notice.impl.NoticeProcess)
     */
    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // PmConf is a global setting. Nothing to do here.
        return true;
    }

}
