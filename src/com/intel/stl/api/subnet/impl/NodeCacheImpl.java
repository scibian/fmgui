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

import static com.intel.stl.common.STLMessages.STL30055_NODE_NOT_FOUND_IN_CACHE_LID;
import static com.intel.stl.common.STLMessages.STL30056_NODE_NOT_FOUND_IN_CACHE_PORT_GUID;
import static com.intel.stl.common.STLMessages.STL30057_NODE_TYPE_DIST_FOUND_IN_CACHE;
import static com.intel.stl.common.STLMessages.STL30061_NODE_NOT_FOUND_CACHE_ALL;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class NodeCacheImpl extends MemoryCache<Map<Integer, NodeRecordBean>>
        implements NodeCache {

    // distribution with active nodes only
    private final AtomicReference<EnumMap<NodeType, Integer>> nodesTypeDist;

    // distribution with active and inactive nodes
    private final AtomicReference<EnumMap<NodeType, Integer>> nodesTypeDist2;

    private final SAHelper helper;

    public NodeCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.helper = cacheMgr.getSAHelper();
        this.nodesTypeDist =
                new AtomicReference<EnumMap<NodeType, Integer>>(null);
        this.nodesTypeDist2 =
                new AtomicReference<EnumMap<NodeType, Integer>>(null);
    }

    @Override
    public List<NodeRecordBean> getNodes(boolean includeInactive)
            throws SubnetDataNotFoundException {
        Map<Integer, NodeRecordBean> map = getCachedObject();

        List<NodeRecordBean> res = new ArrayList<NodeRecordBean>();
        if (map != null && !map.isEmpty()) {
            for (NodeRecordBean node : map.values()) {
                if (includeInactive || node.isActive()) {
                    res.add(node);
                }
            }
        }
        if (!res.isEmpty()) {
            return res;
        } else {
            throw new SubnetDataNotFoundException(
                    STL30061_NODE_NOT_FOUND_CACHE_ALL);
        }
    }

    @Override
    public NodeRecordBean getNode(int lid) throws SubnetDataNotFoundException {
        Map<Integer, NodeRecordBean> nodeMap = getCachedObject();

        if (nodeMap != null && !nodeMap.isEmpty()) {
            NodeRecordBean node = nodeMap.get(lid);
            if (node != null) {
                return node;
            }
        }

        // might be a new node
        log.info("Couldn't find node lid=" + StringUtils.longHexString(lid)
                + "  from cache");
        NodeRecordBean node = null;
        try {
            node = helper.getNode(lid);
        } catch (Exception e) {
            log.error("Error while getting node with lid=" + lid
                    + " from Fabric", e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        if (node != null) {
            setCacheReady(false); // Force a refresh on next call
            return node;
        } else {
            throw new SubnetDataNotFoundException(
                    STL30055_NODE_NOT_FOUND_IN_CACHE_LID, lid);
        }
    }

    @Override
    public NodeRecordBean getNode(long portGuid)
            throws SubnetDataNotFoundException {
        Map<Integer, NodeRecordBean> nodeMap = getCachedObject();

        if (nodeMap != null && !nodeMap.isEmpty()) {
            Collection<NodeRecordBean> nodes = nodeMap.values();
            for (NodeRecordBean node : nodes) {
                if (node.getNodeInfo().getPortGUID() == portGuid) {
                    return node;
                }
            }
        }

        // might be a new node
        log.info("Couldn't find node guid="
                + StringUtils.longHexString(portGuid) + "  from cache");
        NodeRecordBean node = null;
        try {
            node = helper.getNode(portGuid);
        } catch (Exception e) {
            log.error("Error while getting node with portGuid=" + portGuid
                    + " from Fabric", e);
            e.printStackTrace();
            throw SubnetApi.getSubnetException(e);
        }

        if (node != null) {
            setCacheReady(false); // Force a refresh on next call
            return node;
        } else {
            throw new SubnetDataNotFoundException(
                    STL30056_NODE_NOT_FOUND_IN_CACHE_PORT_GUID, portGuid);
        }
    }

    @Override
    public EnumMap<NodeType, Integer> getNodesTypeDist(boolean includeInactive,
            boolean refresh) throws SubnetDataNotFoundException {
        if (includeInactive) {
            return getNodesTypeDist(refresh);
        } else {
            return getActiveNodesTypeDist(refresh);
        }
    }

    protected EnumMap<NodeType, Integer> getActiveNodesTypeDist(boolean refresh)
            throws SubnetDataNotFoundException {
        if (refresh || nodesTypeDist.get() == null) {
            Map<Integer, NodeRecordBean> nodeMap = getCachedObject();

            if (nodeMap != null && !nodeMap.isEmpty()) {
                EnumMap<NodeType, Integer> nodesTypeDistMap =
                        new EnumMap<NodeType, Integer>(NodeType.class);

                Collection<NodeRecordBean> nodes = nodeMap.values();
                for (NodeRecordBean node : nodes) {
                    if (node.isActive()) {
                        NodeType type = node.getNodeType();
                        Integer count = nodesTypeDistMap.get(type);
                        nodesTypeDistMap.put(type, count == null ? 1
                                : (count + 1));
                    }
                }
                nodesTypeDist.set(nodesTypeDistMap);
            } else {
                throw new SubnetDataNotFoundException(
                        STL30057_NODE_TYPE_DIST_FOUND_IN_CACHE);
            }
        }
        return nodesTypeDist.get();
    }

    protected EnumMap<NodeType, Integer> getNodesTypeDist(boolean refresh)
            throws SubnetDataNotFoundException {
        if (refresh || nodesTypeDist2.get() == null) {
            Map<Integer, NodeRecordBean> nodeMap = getCachedObject();

            if (nodeMap != null && !nodeMap.isEmpty()) {
                EnumMap<NodeType, Integer> nodesTypeDistMap =
                        new EnumMap<NodeType, Integer>(NodeType.class);

                Collection<NodeRecordBean> nodes = nodeMap.values();
                for (NodeRecordBean node : nodes) {
                    NodeType type = node.getNodeType();
                    Integer count = nodesTypeDistMap.get(type);
                    nodesTypeDistMap.put(type, count == null ? 1 : (count + 1));
                }
                nodesTypeDist2.set(nodesTypeDistMap);
            } else {
                throw new SubnetDataNotFoundException(
                        STL30057_NODE_TYPE_DIST_FOUND_IN_CACHE);
            }
        }
        return nodesTypeDist2.get();
    }

    @Override
    protected Map<Integer, NodeRecordBean> retrieveObjectForCache()
            throws Exception {
        List<NodeRecordBean> nodes = helper.getNodes();
        log.info("Retrieve " + (nodes == null ? 0 : nodes.size())
                + " nodes from FE");
        Map<Integer, NodeRecordBean> map = null;
        if (nodes != null) {
            map = new HashMap<Integer, NodeRecordBean>();
            for (NodeRecordBean node : nodes) {
                map.put(node.getLid(), node);
            }
        }
        nodesTypeDist.set(null);
        nodesTypeDist2.set(null);
        return map;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.MemoryCache#reset()
     */
    @Override
    public void reset() {
        super.reset();
        updateCache();
    }

    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        // If there was an exception during refreshCache(), this will rethrow
        // the exception
        Map<Integer, NodeRecordBean> nodeMap = getCachedObject();
        // If nodeMap is null, most probably DBNodeCache is in use
        if (nodeMap == null) {
            log.info("Node map is null");
            return false;
        }
        switch (notice.getTrapType()) {
            case GID_NOW_IN_SERVICE:
                resetNode(notice, true);
                break;
            case GID_OUT_OF_SERVICE:
                resetNode(notice, false);
            default:
                break;
        }
        nodesTypeDist.set(null);
        nodesTypeDist2.set(null);

        return true;
    }

    @Override
    protected RuntimeException processRefreshCacheException(Exception e) {
        return SubnetApi.getSubnetException(e);
    }

    private void resetNode(NoticeProcess notice, boolean status) {
        Map<Integer, NodeRecordBean> nodeMap = getCachedObject();
        int lid = notice.getLid();
        NodeRecordBean node = nodeMap.get(lid);
        NodeRecordBean newNode = notice.getNode();
        if (node != null && newNode != null) {
            NodeInfoBean nodeInfo = node.getNodeInfo();
            NodeInfoBean newNodeInfo = newNode.getNodeInfo();
            if (nodeInfo.getNodeGUID() != newNodeInfo.getNodeGUID()) {
                // LID has changed
                log.info("Node GUID in cache does not match GUID in FM");
                setCacheReady(false);
            }
            node.setActive(status);
        } else {
            if (node == null) {
                // No node in cache
                if (newNode != null) {
                    newNode.setActive(status);
                    nodeMap.put(lid, newNode);
                } else {
                    log.error("Notice for node with lid " + lid
                            + " to set active status to " + status
                            + " but no node from subnet");
                }
            } else {
                // There is a node in cache but no node in the FM. Apply rule,
                // no FM definition, resource set to inactive
                node.setActive(false);
            }
        }
    }

}
