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
package com.intel.stl.api.subnet.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.SC2SLMTRecordBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class SC2SLMTCacheImpl extends
        MemoryCache<Map<Integer, SC2SLMTRecordBean>> implements SC2SLMTCache {

    private final SAHelper helper;

    public SC2SLMTCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<SC2SLMTRecordBean> getSC2SLMTs() {
        Map<Integer, SC2SLMTRecordBean> map = getCachedObject();

        List<SC2SLMTRecordBean> res = new ArrayList<SC2SLMTRecordBean>();
        if (map != null && !map.isEmpty()) {
            for (SC2SLMTRecordBean sc2sl : map.values()) {
                res.add(sc2sl);
            }
        }
        if (!res.isEmpty()) {
            return res;
        } else {

            // might be a new
            try {
                res = helper.getSC2SLMTs();
                if (res != null) {
                    setCacheReady(false); // Force a refresh on next call;
                }
                return res;
            } catch (Exception e) {
                throw SubnetApi.getSubnetException(e);
            }
        }
    }

    @Override
    public SC2SLMTRecordBean getSC2SLMT(int lid) {
        Map<Integer, SC2SLMTRecordBean> map = getCachedObject();
        if (map != null) {
            return map.get(lid);
        }

        // might be a new node
        try {
            SC2SLMTRecordBean res = helper.getSC2SLMT(lid);
            if (res != null) {
                setCacheReady(false); // Force a refresh on next call;
            }

            return res;
        } catch (Exception e) {
            log.error("Error getting Cable Infos by lid " + lid, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }
    }

    @Override
    protected Map<Integer, SC2SLMTRecordBean> retrieveObjectForCache()
            throws Exception {
        List<SC2SLMTRecordBean> sc2sls = helper.getSC2SLMTs();
        log.info("Retrieve " + (sc2sls == null ? 0 : sc2sls.size())
                + " SC2SLMT Infos from FE");
        Map<Integer, SC2SLMTRecordBean> map = null;
        if (sc2sls != null) {
            map = new HashMap<Integer, SC2SLMTRecordBean>();
            for (SC2SLMTRecordBean sc2sl : sc2sls) {
                map.put(sc2sl.getLid(), sc2sl);
            }
        }
        return map;
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
        // No notice applies to this cache
        Map<Integer, SC2SLMTRecordBean> map = getCachedObject();
        if (map != null && !map.isEmpty()) {
            map.remove(notice.getLid());
        }
        return true;
    }

    @Override
    protected RuntimeException processRefreshCacheException(Exception e) {
        return SubnetApi.getSubnetException(e);
    }
}
