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
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.intel.stl.api.configuration.PortState;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class LinkCacheImpl extends MemoryCache<List<LinkRecordBean>> implements
        LinkCache {

    private final SAHelper helper;

    public LinkCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
    }

    @Override
    public List<LinkRecordBean> getLinks(boolean includeInactive)
            throws SubnetDataNotFoundException {
        List<LinkRecordBean> res = new ArrayList<LinkRecordBean>();

        List<LinkRecordBean> links = getCachedObject();
        if (links != null && !links.isEmpty()) {
            for (LinkRecordBean link : links) {
                if (includeInactive || link.isActive()) {
                    res.add(link);
                }
            }
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
        List<LinkRecordBean> links = getLinks(true);
        for (LinkRecordBean link : links) {
            if (link.getFromLID() == lid && link.getFromPortIndex() == portNum) {
                return link;
            }
        }

        // might be a new node
        try {
            links = helper.getLinks(lid);
            if (links != null && !links.isEmpty()) {
                setCacheReady(false); // Force a refresh on next call
                for (LinkRecordBean link : links) {
                    if (link.getFromPortIndex() == portNum) {
                        return link;
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting link by source lid=" + lid + ", portNum="
                    + portNum + " from Fabric", e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        throw new SubnetDataNotFoundException(STL30058_LINK_NOT_FOUND_CACHE_ALL);
    }

    @Override
    public LinkRecordBean getLinkByDestination(int lid, short portNum)
            throws SubnetDataNotFoundException {
        List<LinkRecordBean> links = getLinks(true);
        for (LinkRecordBean link : links) {
            if (link.getToLID() == lid && link.getToPortIndex() == portNum) {
                return link;
            }
        }

        // might be a new node
        try {
            links = helper.getLinks(lid);
            if (links != null && !links.isEmpty()) {
                for (LinkRecordBean link : links) {
                    if (link.getFromPortIndex() == portNum) {
                        setCacheReady(false); // Force a refresh on next call
                        return new LinkRecordBean(link.getToLID(),
                                link.getToPortIndex(), link.getFromLID(),
                                link.getFromPortIndex());
                    }
                }
            }
        } catch (Exception e) {
            log.error("Error getting link by destination lid=" + lid
                    + ", portNum=" + portNum + " from Fabric", e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        throw new SubnetDataNotFoundException(STL30058_LINK_NOT_FOUND_CACHE_ALL);
    }

    @Override
    protected List<LinkRecordBean> retrieveObjectForCache() throws Exception {
        List<LinkRecordBean> res = helper.getLinks();
        log.info("Retrieve " + (res == null ? 0 : res.size())
                + " links from FE");
        return res;
    }

    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // If there was an exception during refreshCache(), this will rethrow
        // the exception
        List<LinkRecordBean> links = getCachedObject();
        // If links is null, most probably DBLinkCache is in use
        if (links == null) {
            log.info("No links from FM");
            return false;
        }
        switch (notice.getTrapType()) {
            case GID_NOW_IN_SERVICE:
                resetLinks(notice, true);
                break;
            case GID_OUT_OF_SERVICE:
                resetLinks(notice, false);
                break;
            case LINK_PORT_CHANGE_STATE:
                Map<Short, PortState> portMap = new HashMap<Short, PortState>();
                for (PortRecordBean port : notice.getPorts()) {
                    PortState portState =
                            port.getPortInfo().getPortStates().getPortState();
                    portMap.put(port.getPortNum(), portState);
                }
                resetLinksUsingPortMap(notice, portMap);
                break;
            default:
                break;
        }

        return true;
    }

    @Override
    protected RuntimeException processRefreshCacheException(Exception e) {
        return SubnetApi.getSubnetException(e);
    }

    private void resetLinks(NoticeProcess notice, boolean status) {
        List<LinkRecordBean> links = getCachedObject();
        int lid = notice.getLid();
        List<LinkRecordBean> newLinks = notice.getLinks();
        if (newLinks == null) {
            // No link records in the FM for this lid. The rule is: if no
            // corresponding record in the FM, then record in cache should be
            // set to inactive, just in case this is a transient condition that
            // would correct itself later. A SaveTopology would delete the link
            // if the condition persists.
            for (LinkRecordBean link : links) {
                if (link.getFromLID() == lid || link.getToLID() == lid) {
                    link.setActive(false);
                }
            }
        } else {
            // newLinks is R/O; copy it for this logic
            List<LinkRecordBean> rwNewLinks =
                    new ArrayList<LinkRecordBean>(newLinks);
            for (LinkRecordBean link : links) {
                if (link.getFromLID() == lid || link.getToLID() == lid) {
                    processLink(link, status, rwNewLinks);
                }
            }
            // At this point, newLinks should have only non-matching links that
            // need to be added to the cache
            if (rwNewLinks.size() > 0) {
                for (LinkRecordBean newLink : rwNewLinks) {
                    newLink.setActive(status);
                    links.add(newLink);
                }
            }
        }
    }

    private void processLink(LinkRecordBean memLink, boolean status,
            List<LinkRecordBean> newLinks) {
        Iterator<LinkRecordBean> it = newLinks.iterator();
        boolean found = false;
        while (it.hasNext()) {
            LinkRecordBean newLink = it.next();
            if (memLink.getFromLID() == newLink.getFromLID()
                    && memLink.getFromPortIndex() == newLink.getFromPortIndex()
                    && memLink.getToLID() == newLink.getToLID()
                    && memLink.getToPortIndex() == newLink.getToPortIndex()) {
                found = true;
                memLink.setActive(status);
                // Do not process this link again
                it.remove();
            }
        }
        if (!found) {
            // See the rule about No corresponding records in the FM above
            memLink.setActive(false);
        }

    }

    private void resetLinksUsingPortMap(NoticeProcess notice,
            Map<Short, PortState> portMap) {
        List<LinkRecordBean> links = getCachedObject();
        int lid = notice.getLid();
        for (LinkRecordBean link : links) {
            PortState portState = null;
            if (link.getFromLID() == lid) {
                portState = portMap.get(link.getFromPortIndex());
            } else if (link.getToLID() == lid) {
                portState = portMap.get(link.getToPortIndex());
            }
            if (portState == null) {
                continue;
            }
            if (portState == PortState.ACTIVE) {
                link.setActive(true);
            } else {
                link.setActive(false);
            }
        }
    }
}
