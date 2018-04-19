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

import static com.intel.stl.api.configuration.AppInfo.PROPERTIES_DATABASE;

import java.util.Map;
import java.util.Properties;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.configuration.AsyncTask;
import com.intel.stl.datamanager.DatabaseManager;

public class GroupInfoPurgeTask extends AsyncTask<Integer> {
    protected static final String LAST_GROUPINFO_PURGE = "lastgroupinfopurge-";

    private final DatabaseManager dbMgr;

    private final PAHelper helper;

    private final long ago;

    public GroupInfoPurgeTask(PAHelper helper, DatabaseManager dbMgr, long ago) {
        // Check for not null arguments (submitter should handle this rather
        // than the background task)
        checkArguments(helper, dbMgr, ago);
        this.helper = helper;
        this.dbMgr = dbMgr;
        this.ago = ago;
    }

    @Override
    public Integer process() throws Exception {
        SubnetDescription subnet = helper.getSubnetDescription();
        String subnetName = helper.getSubnetDescription().getName();
        int deleted = dbMgr.purgeGroupInfos(subnetName, ago);
        Map<String, Properties> appProps =
                dbMgr.getAppInfo().getPropertiesMap();
        Properties dbProps = appProps.get(PROPERTIES_DATABASE);
        if (dbProps == null) {
            dbProps = new Properties();
            appProps.put(PROPERTIES_DATABASE, dbProps);
        }
        long now = System.currentTimeMillis();
        dbProps.put(LAST_GROUPINFO_PURGE + subnet.getSubnetId(),
                new Long(now).toString());
        dbMgr.saveAppProperties(appProps);
        return deleted;
    }

}
