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
import com.intel.stl.api.subnet.CableRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class CableCacheImpl extends
        MemoryCache<Map<Integer, List<CableRecordBean>>> implements CableCache {

    private final SAHelper helper;

    public CableCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.subnet.impl.CableCache#getCables()
     */
    @Override
    public List<CableRecordBean> getCables() {
        Map<Integer, List<CableRecordBean>> map = getCachedObject();

        List<CableRecordBean> res = new ArrayList<CableRecordBean>();
        if (map != null && !map.isEmpty()) {
            for (List<CableRecordBean> cables : map.values()) {
                for (CableRecordBean cable : cables) {
                    res.add(cable);
                }
            }
        }

        if (!res.isEmpty()) {
            return res;
        } else {
            // might be a new
            try {
                res = helper.getCables();
                if (res != null) {
                    setCacheReady(false); // Force a refresh on next call;
                }
                return res;
            } catch (Exception e) {
                throw SubnetApi.getSubnetException(e);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.subnet.impl.CableCache#getCable(int)
     */
    @Override
    public List<CableRecordBean> getCable(int lid) {
        Map<Integer, List<CableRecordBean>> map = getCachedObject();
        List<CableRecordBean> res = new ArrayList<CableRecordBean>();
        if (map != null) {
            res = map.get(lid);
        }
        if (res != null && !res.isEmpty()) {
            return res;
        }

        // might be a new node
        try {
            res = helper.getCables(lid);
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

    /**
     * Cable info are in two records.
     *
     * @throws SubnetDataNotFoundException
     */
    @Override
    public CableRecordBean getCable(int lid, short portNum) {
        Map<Integer, List<CableRecordBean>> map = getCachedObject();
        CableRecordBean res = null;
        if (map != null) {
            List<CableRecordBean> cables = map.get(lid);
            if (cables != null) {
                for (CableRecordBean cable : cables) {
                    if (cable.getLid() == lid && cable.getPort() == portNum) {
                        res = cable;
                    }
                }
            }
        }

        if (res != null) {
            return res;
        }

        // might be a new node
        try {
            List<CableRecordBean> cablesFromFE = helper.getCables(lid);
            if (cablesFromFE != null && !cablesFromFE.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call;
                for (CableRecordBean cable : cablesFromFE) {
                    if (cable.getLid() == lid && cable.getPort() == portNum) {
                        res = cable;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting Cable Infos by lid " + lid + ", portNum="
                    + portNum, e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }
        return res;
    }

    @Override
    protected Map<Integer, List<CableRecordBean>> retrieveObjectForCache()
            throws Exception {
        List<CableRecordBean> cables = helper.getCables();
        Map<Integer, List<CableRecordBean>> map = null;
        if (cables != null) {
            log.info("Retrieve " + (cables == null ? 0 : cables.size())
                    + " Cable Infos from FE");
            map = new HashMap<Integer, List<CableRecordBean>>();
            for (CableRecordBean cable : cables) {
                int lid = cable.getLid();
                if (map.containsKey(lid)) {
                    map.get(lid).add(cable);
                } else {
                    List<CableRecordBean> list =
                            new ArrayList<CableRecordBean>();
                    list.add(cable);
                    map.put(cable.getLid(), list);
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
        Map<Integer, List<CableRecordBean>> map = getCachedObject();
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
