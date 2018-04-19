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
import java.util.EnumMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetException;
import com.intel.stl.configuration.BaseCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.datamanager.DatabaseManager;

public class DBNodeCacheImpl extends BaseCache implements NodeCache {

    private final DatabaseManager dbMgr;

    private final SAHelper helper;

    // distribution with active nodes only
    private final AtomicReference<EnumMap<NodeType, Integer>> activeNodesTypeDist;

    public DBNodeCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.dbMgr = cacheMgr.getDatabaseManager();
        this.helper = cacheMgr.getSAHelper();
        this.activeNodesTypeDist =
                new AtomicReference<EnumMap<NodeType, Integer>>(null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.subnet.impl.NodeCache#getNodes()
     */
    @Override
    public List<NodeRecordBean> getNodes(boolean includeInactive)
            throws SubnetDataNotFoundException {
        List<NodeRecordBean> res = new ArrayList<NodeRecordBean>();
        try {
            List<NodeRecordBean> nodes = dbMgr.getNodes(getSubnetName());
            if (nodes != null && !nodes.isEmpty()) {
                for (NodeRecordBean node : nodes) {
                    if (includeInactive || node.isActive()) {
                        res.add(node);
                    }
                }
            }
        } catch (DatabaseException e) {
            log.error("Received a database exception while getting all nodes",
                    e);
            throw SubnetApi.getSubnetException(e);
        }
        return res;
    }

    @Override
    public NodeRecordBean getNode(int lid) throws SubnetDataNotFoundException {
        SubnetDataNotFoundException ne = null;
        try {
            return dbMgr.getNode(getSubnetName(), lid);
        } catch (DatabaseException e) {
            log.error(
                    "Received a database exception while getting a node with lid "
                            + lid, e);
            throw SubnetApi.getSubnetException(e);
        } catch (SubnetDataNotFoundException e) {
            ne = e;
        }

        // might be a new node
        log.info("Couldn't find node lid=" + lid + "  from database");
        NodeRecordBean node = null;
        try {
            node = helper.getNode(lid);
        } catch (Exception exception) {
            SubnetException se = SubnetApi.getSubnetException(exception);
            log.error("Error while getting node with lid=" + lid
                    + " from Fabric", exception);
            throw se;
        }

        if (node != null) {
            activeNodesTypeDist.set(null);
            cacheMgr.startTopologyUpdateTask();
            return node;
        } else {
            throw ne;
        }
    }

    @Override
    public NodeRecordBean getNode(long portGuid)
            throws SubnetDataNotFoundException {
        SubnetDataNotFoundException ne = null;
        try {
            return dbMgr.getNodeByPortGUID(getSubnetName(), portGuid);
        } catch (DatabaseException e) {
            log.error(
                    "Received a database exception while getting a node with port Guid "
                            + portGuid, e);
            throw SubnetApi.getSubnetException(e);
        } catch (SubnetDataNotFoundException e) {
            ne = e;
        }

        // might be a new node
        log.info("Couldn't find node guid="
                + StringUtils.longHexString(portGuid) + "  from database");
        NodeRecordBean node = null;
        try {
            node = helper.getNode(portGuid);
        } catch (Exception exception) {
            SubnetException se = SubnetApi.getSubnetException(exception);
            log.error("Error while getting node with portGuid=" + portGuid
                    + " from Fabric", exception);
            throw se;
        }

        if (node != null) {
            activeNodesTypeDist.set(null);
            cacheMgr.startTopologyUpdateTask();
            return node;
        } else {
            throw ne;
        }
    }

    @Override
    public EnumMap<NodeType, Integer> getNodesTypeDist(boolean includeInactive,
            boolean refresh) throws SubnetDataNotFoundException {
        if (includeInactive) {
            // argument refresh is unnecessary because we always calculate
            // distribution
            return getNodesTypeDist();
        } else {
            return getActiveNodesTypeDist(refresh);
        }
    }

    protected EnumMap<NodeType, Integer> getNodesTypeDist()
            throws SubnetDataNotFoundException {
        try {
            return dbMgr.getNodeTypeDist(getSubnetName());
        } catch (DatabaseException e) {
            // int error = e.getErrorCode();
            log.error(
                    "Received a database exception while getting a node type distribution",
                    e);
            throw SubnetApi.getSubnetException(e);
        }
    }

    protected EnumMap<NodeType, Integer> getActiveNodesTypeDist(boolean refresh)
            throws SubnetDataNotFoundException {
        if (refresh || activeNodesTypeDist.get() == null) {
            List<NodeRecordBean> nodes = getNodes(false);
            EnumMap<NodeType, Integer> nodesTypeDistMap =
                    new EnumMap<NodeType, Integer>(NodeType.class);
            for (NodeRecordBean node : nodes) {
                if (node.isActive()) {
                    NodeType type = node.getNodeType();
                    Integer count = nodesTypeDistMap.get(type);
                    nodesTypeDistMap.put(type, count == null ? 1 : (count + 1));
                }
            }
            activeNodesTypeDist.set(nodesTypeDistMap);
        }
        return activeNodesTypeDist.get();
    }

    private String getSubnetName() {
        return helper.getSubnetDescription().getName();
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
        activeNodesTypeDist.set(null);
        return true;
    }

    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        activeNodesTypeDist.set(null);
        return true;
    }

}
