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

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.GroupConfigRspBean;
import com.intel.stl.api.performance.VFConfigRspBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

/**
 * Lazy approach based cache. It maintains cache by itself rather than by
 * MemoryCache
 */
public class GroupConfCacheImpl extends MemoryCache<Void> implements
        GroupConfCache {
    private final static Logger log = LoggerFactory
            .getLogger(GroupConfCacheImpl.class);

    private final PAHelper helper;

    private final Map<String, SoftReference<List<GroupConfigRspBean>>> groupConfigs =
            new HashMap<String, SoftReference<List<GroupConfigRspBean>>>();

    private final Map<String, SoftReference<List<VFConfigRspBean>>> vfConfigs =
            new HashMap<String, SoftReference<List<VFConfigRspBean>>>();

    /**
     * Description:
     * 
     * @param cacheMgr
     */
    public GroupConfCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getPAHelper();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.performance.impl.GroupConfCache#getGroupConfig(java
     * .lang.String)
     */
    @Override
    public List<GroupConfigRspBean> getGroupConfig(String name)
            throws Exception {
        synchronized (groupConfigs) {
            SoftReference<List<GroupConfigRspBean>> confRef =
                    groupConfigs.get(name);
            if (confRef == null || confRef.get() == null) {
                List<GroupConfigRspBean> conf = helper.getGroupConfig(name);
                confRef = new SoftReference<List<GroupConfigRspBean>>(conf);
                groupConfigs.put(name, confRef);
            } else {
            }
            return confRef == null ? null : confRef.get();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.performance.impl.GroupConfCache#getVFConfig(java.lang
     * .String)
     */
    @Override
    public List<VFConfigRspBean> getVFConfig(String name) throws Exception {
        synchronized (vfConfigs) {
            SoftReference<List<VFConfigRspBean>> confRef = vfConfigs.get(name);
            if (confRef == null || confRef.get() == null) {
                List<VFConfigRspBean> conf = helper.getVFConfig(name);
                confRef = new SoftReference<List<VFConfigRspBean>>(conf);
                vfConfigs.put(name, confRef);
            }
            return confRef == null ? null : confRef.get();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.MemoryCache#isCacheReady()
     */
    @Override
    public boolean isCacheReady() {
        return cacheReady.get();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.MemoryCache#reset()
     */
    @Override
    public void reset() {
        super.reset();
        synchronized (groupConfigs) {
            groupConfigs.clear();
        }
        synchronized (vfConfigs) {
            vfConfigs.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.MemoryCache#retrieveObjectForCache()
     */
    @Override
    protected Void retrieveObjectForCache() throws Exception {
        // clear caches to force recreating cache when query
        synchronized (groupConfigs) {
            groupConfigs.clear();
        }
        synchronized (vfConfigs) {
            vfConfigs.clear();
        }
        return null;
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
        int lid = notice.getLid();
        refreshGroupConf(lid);
        refreshVfConf(lid);
        return true;
    }

    protected void refreshGroupConf(int lid) {
        synchronized (groupConfigs) {
            boolean found = false;
            for (String group : groupConfigs.keySet()) {
                SoftReference<List<GroupConfigRspBean>> ref =
                        groupConfigs.get(group);
                List<GroupConfigRspBean> confs = ref.get();
                if (confs != null) {
                    for (GroupConfigRspBean conf : confs) {
                        if (conf.getPort().getNodeLid() == lid) {
                            found = true;
                            ref.clear();
                            log.info("Cleared cache for Device Group '" + group
                                    + "'");
                            break;
                        }
                    }
                }
            }
            if (!found) {
                groupConfigs.clear();
            }
        }
    }

    protected void refreshVfConf(int lid) {
        synchronized (vfConfigs) {
            boolean found = false;
            for (String group : vfConfigs.keySet()) {
                SoftReference<List<VFConfigRspBean>> ref = vfConfigs.get(group);
                List<VFConfigRspBean> confs = ref.get();
                if (confs != null) {
                    for (VFConfigRspBean conf : confs) {
                        if (conf.getPort().getNodeLid() == lid) {
                            found = true;
                            ref.clear();
                            log.info("Cleared cache for Virtual Fabric '"
                                    + group + "'");
                            break;
                        }
                    }
                }
            }
            if (!found) {
                vfConfigs.clear();
            }
        }
    }
}
