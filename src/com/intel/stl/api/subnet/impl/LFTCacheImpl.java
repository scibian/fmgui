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
import com.intel.stl.api.subnet.LFTRecordBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class LFTCacheImpl extends
        MemoryCache<Map<Integer, List<LFTRecordBean>>> implements LFTCache {

    private final SAHelper helper;

    public LFTCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<LFTRecordBean> getLFTs() {
        Map<Integer, List<LFTRecordBean>> map = getCachedObject();

        List<LFTRecordBean> res = new ArrayList<LFTRecordBean>();
        if (map != null && !map.isEmpty()) {
            for (List<LFTRecordBean> lfts : map.values()) {
                for (LFTRecordBean lft : lfts) {
                    res.add(lft);
                }
            }
        }

        if (!res.isEmpty()) {
            return res;
        } else {
            // might be a new
            try {
                res = helper.getLFTs();
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
    public List<LFTRecordBean> getLFT(int lid) {
        Map<Integer, List<LFTRecordBean>> map = getCachedObject();
        List<LFTRecordBean> res = new ArrayList<LFTRecordBean>();
        if (map != null) {
            res = map.get(lid);
            if (res != null && !res.isEmpty()) {
                return res;
            }
        }

        // might be a new node
        try {
            res = helper.getLFTs(lid);
            if (res != null && !res.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
            }
        } catch (Exception e) {
            log.error("Error getting LFT by lid " + lid, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }
        return res;
    }

    @Override
    protected Map<Integer, List<LFTRecordBean>> retrieveObjectForCache()
            throws Exception {
        List<LFTRecordBean> lfts = helper.getLFTs();
        log.info("Retrieve " + (lfts == null ? 0 : lfts.size())
                + " LFTs from FE");
        Map<Integer, List<LFTRecordBean>> map = null;
        if (lfts != null) {
            map = new HashMap<Integer, List<LFTRecordBean>>();
            for (LFTRecordBean lft : lfts) {
                int lid = lft.getLid();
                if (map.containsKey(lid)) {
                    map.get(lid).add(lft);
                } else {
                    List<LFTRecordBean> list = new ArrayList<LFTRecordBean>();
                    list.add(lft);
                    map.put(lft.getLid(), list);
                }
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
        // No notice applies to this cache so far
        Map<Integer, List<LFTRecordBean>> map = getCachedObject();
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
