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

import static com.intel.stl.common.STLMessages.STL30058_LINK_NOT_FOUND_CACHE_ALL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetException;
import com.intel.stl.configuration.BaseCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.datamanager.DatabaseManager;

public class DBLinkCacheImpl extends BaseCache implements LinkCache {

    private final DatabaseManager dbMgr;

    private final SAHelper helper;

    public DBLinkCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.dbMgr = cacheMgr.getDatabaseManager();
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<LinkRecordBean> getLinks(boolean includeInactive)
            throws SubnetDataNotFoundException {
        List<LinkRecordBean> res = new ArrayList<LinkRecordBean>();
        try {
            List<LinkRecordBean> links = dbMgr.getLinks(getSubnetName());
            if (links != null && !links.isEmpty()) {
                for (LinkRecordBean link : links) {
                    if (includeInactive || link.isActive()) {
                        res.add(link);
                    }
                }
            }
        } catch (DatabaseException e) {
            SubnetException se = SubnetApi.getSubnetException(e);
            log.error("Error getting links", e);
            throw se;
        }

        if (!res.isEmpty()) {
            return Collections.unmodifiableList(res);
        } else {
            throw new SubnetDataNotFoundException(
                    STL30058_LINK_NOT_FOUND_CACHE_ALL);
        }
    }

    @Override
    public LinkRecordBean getLinkBySource(int lid, short portNum)
            throws SubnetDataNotFoundException {

        SubnetDataNotFoundException le = null;
        try {
            return dbMgr.getLinkBySource(getSubnetName(), lid, portNum);
        } catch (DatabaseException e) {
            SubnetException se = SubnetApi.getSubnetException(e);
            log.error("Error getting link by source lid=" + lid + ", portNum="
                    + portNum, e);
            throw se;
        } catch (SubnetDataNotFoundException e) {
            le = e;
        }

        // might be a new link
        log.info("Couldn't find Link by source Lid=" + lid + ", portNum="
                + portNum + "  from cache");
        List<LinkRecordBean> links = null;
        try {
            links = helper.getLinks(lid);
        } catch (Exception exception) {
            SubnetException se = SubnetApi.getSubnetException(exception);
            log.error("Error getting link by source lid=" + lid + ", portNum="
                    + portNum + " from Fabric", exception);
            throw se;
        }
        if (links != null && !links.isEmpty()) {
            for (LinkRecordBean link : links) {
                if (link.getFromPortIndex() == portNum) {
                    cacheMgr.startTopologyUpdateTask();
                    return link;
                }
            }
        }

        // If not found in fabric, throw link not found exception.
        throw le;
    }

    @Override
    public LinkRecordBean getLinkByDestination(int lid, short portNum)
            throws SubnetDataNotFoundException {

        SubnetDataNotFoundException le = null;
        try {
            return dbMgr.getLinkByDestination(getSubnetName(), lid, portNum);
        } catch (DatabaseException e) {
            SubnetException se = SubnetApi.getSubnetException(e);
            log.error("Error getting link by destination lid=" + lid
                    + ", portNum=" + portNum, e);
            throw se;
        } catch (SubnetDataNotFoundException e) {
            le = e;
        }

        // might be a new node
        log.info("Couldn't find Link by destination Lid=" + lid + ", portNum="
                + portNum + "  from cache");
        List<LinkRecordBean> links = null;
        try {
            links = helper.getLinks(lid);
        } catch (Exception exception) {
            SubnetException se = SubnetApi.getSubnetException(exception);
            log.error("Error getting links for lid " + lid, exception);
            throw se;
        }

        if (links != null && !links.isEmpty()) {
            for (LinkRecordBean link : links) {
                if (link.getFromPortIndex() == portNum) {
                    cacheMgr.startTopologyUpdateTask();
                    return new LinkRecordBean(link.getToLID(),
                            link.getToPortIndex(), link.getFromLID(),
                            link.getFromPortIndex());
                }
            }
        }

        // If not found in fabric, throw link not found exception.
        throw le;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.ManagedCache#reset()
     */
    @Override
    public void reset() {
        // this is DB. do nothing
    }

    @Override
    public boolean refreshCache() {
        return true;
    }

    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // Database-related notice processing is done in NoticeProcessingTask
        return true;
    }

    private String getSubnetName() {
        return helper.getSubnetDescription().getName();
    }

}
