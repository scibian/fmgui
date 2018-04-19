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
import com.intel.stl.api.subnet.SC2VLMTRecordBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class SC2VLNTMTCacheImpl extends
        MemoryCache<Map<Integer, List<SC2VLMTRecordBean>>> implements
        SC2VLNTMTCache {

    private final SAHelper helper;

    public SC2VLNTMTCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<SC2VLMTRecordBean> getSC2VLNTMTs() {
        Map<Integer, List<SC2VLMTRecordBean>> map = getCachedObject();

        List<SC2VLMTRecordBean> res = new ArrayList<SC2VLMTRecordBean>();
        if (map != null && !map.isEmpty()) {
            for (List<SC2VLMTRecordBean> sc2vlnts : map.values()) {
                for (SC2VLMTRecordBean sc2vlnt : sc2vlnts) {
                    res.add(sc2vlnt);
                }
            }
        }

        if (!res.isEmpty()) {
            return res;
        } else {
            // might be a new
            try {
                res = helper.getSC2VLNTMTs();
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
    public List<SC2VLMTRecordBean> getSC2VLNTMT(int lid) {
        Map<Integer, List<SC2VLMTRecordBean>> map = getCachedObject();
        List<SC2VLMTRecordBean> res = new ArrayList<SC2VLMTRecordBean>();
        if (map != null) {
            res = map.get(lid);
        }
        if (res != null && !res.isEmpty()) {
            return res;
        }
        // might be a new node
        try {
            res = helper.getSC2VLNTMT(lid);
            if (res != null && !res.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
            }
        } catch (Exception e) {
            log.error("Error getting Cable Infos by lid " + lid, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }
        return res;
    }

    @Override
    public SC2VLMTRecordBean getSC2VLNTMT(int lid, short portNum) {
        Map<Integer, List<SC2VLMTRecordBean>> map = getCachedObject();
        if (map != null) {
            List<SC2VLMTRecordBean> sc2vlntmts = map.get(lid);
            if (sc2vlntmts != null) {
                for (SC2VLMTRecordBean sc2vlnt : sc2vlntmts) {
                    if (sc2vlnt.getLid() == lid && sc2vlnt.getPort() == portNum) {
                        return sc2vlnt;
                    }
                }
            }
        }

        // might be a new node
        try {
            List<SC2VLMTRecordBean> sc2vlntFromFE = helper.getSC2VLNTMT(lid);
            if (sc2vlntFromFE != null && !sc2vlntFromFE.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
                for (SC2VLMTRecordBean sc2vlnt : sc2vlntFromFE) {
                    if (sc2vlnt.getPort() == portNum) {
                        return sc2vlnt;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting SC2VLnt Infos by lid " + lid
                    + ", portNum=" + portNum, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        return null;
    }

    @Override
    protected Map<Integer, List<SC2VLMTRecordBean>> retrieveObjectForCache()
            throws Exception {
        List<SC2VLMTRecordBean> sc2vls = helper.getSC2VLNTMTs();
        log.info("Retrieve " + (sc2vls == null ? 0 : sc2vls.size())
                + " SC2VLNTMT Infos from FE");
        Map<Integer, List<SC2VLMTRecordBean>> map = null;
        if (sc2vls != null) {
            map = new HashMap<Integer, List<SC2VLMTRecordBean>>();
            for (SC2VLMTRecordBean sc2vl : sc2vls) {
                int lid = sc2vl.getLid();
                if (map.containsKey(lid)) {
                    map.get(lid).add(sc2vl);
                } else {
                    List<SC2VLMTRecordBean> list =
                            new ArrayList<SC2VLMTRecordBean>();
                    list.add(sc2vl);
                    map.put(sc2vl.getLid(), list);
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
        // No notice applies to this cache
        Map<Integer, List<SC2VLMTRecordBean>> map = getCachedObject();
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
