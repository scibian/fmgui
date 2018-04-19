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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;
import com.intel.stl.datamanager.DatabaseManager;

public class GroupCacheImpl extends MemoryCache<Map<String, String>> implements
        GroupCache {

    private final DatabaseManager dbMgr;

    private final PAHelper helper;

    private final String subnetName;

    public GroupCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.dbMgr = cacheMgr.getDatabaseManager();
        this.helper = cacheMgr.getPAHelper();
        this.subnetName = helper.getSubnetDescription().getName();
    }

    @Override
    public boolean isGroupDefined(String groupName) {
        Map<String, String> groupList = getCachedObject();
        boolean defined = groupList.containsKey(groupName);
        if (!defined) {
            // This will force a refreshCache() which will call
            // retrieveObjectForCache(), below. Method updateCache is
            // synchronized, which will put all others request on hold until
            // refreshCache() is finished.
            setCacheReady(false);
            updateCache();
            defined = groupList.containsKey(groupName);
        }
        return defined;
    }

    @Override
    protected Map<String, String> retrieveObjectForCache() throws Exception {
        List<GroupListBean> groupList = helper.getGroupList();
        if (groupList == null || groupList.isEmpty()) {
            return new HashMap<String, String>();
        }
        Map<String, String> groups =
                new HashMap<String, String>(groupList.size());
        for (GroupListBean group : groupList) {
            groups.put(group.getGroupName(), null);
        }
        try {
            dbMgr.saveGroupList(subnetName, groupList);
        } catch (Exception e) {
            // Do not let database errors stop this cache from working
            e.printStackTrace();
        }
        return groups;
    }

    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // Don't know about any notice that would apply to this cache
        return true;
    }
}
