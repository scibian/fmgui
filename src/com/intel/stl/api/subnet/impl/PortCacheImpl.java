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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

import com.intel.stl.api.NodeState;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.impl.PortCacheImpl.PortArray;
import com.intel.stl.common.STLMessages;
import com.intel.stl.common.SimpleCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.configuration.MemoryCache;

public class PortCacheImpl extends MemoryCache<SimpleCache<Integer, PortArray>>
        implements PortCache {
    private final SimpleCache<Integer, NodeState> portsStates;

    private final SimpleCache<Integer, PortArray> portsCache;

    // distribution on all ports include the inactive ones
    private final AtomicReference<EnumMap<NodeType, Long>> portsTypeDist;

    private final AtomicReference<Long> subnetPrefix;

    private final SAHelper helper;

    public PortCacheImpl(CacheManager cacheMgr) {
        super(cacheMgr);
        this.portsTypeDist = new AtomicReference<EnumMap<NodeType, Long>>(null);
        this.subnetPrefix = new AtomicReference<Long>(null);
        this.helper = cacheMgr.getSAHelper();

        portsCache = createPortsCache();
        portsStates = createPortsStatesCache();
    }

    protected SimpleCache<Integer, PortArray> createPortsCache() {
        return new SimpleCache<Integer, PortArray>(100, getTickResolution());
    }

    protected SimpleCache<Integer, NodeState> createPortsStatesCache() {
        return new SimpleCache<Integer, NodeState>(5000, 10 * 60 * 1000);
    }

    @Override
    public List<PortRecordBean> getPorts() throws SubnetDataNotFoundException {
        try {
            List<PortRecordBean> ports = helper.getPorts();
            refresh(ports);
            if (ports != null) {
                return ports;
            }
        } catch (Exception e) {
            throw SubnetApi.getSubnetException(e);
        }
        throw new SubnetDataNotFoundException(
                STLMessages.STL30062_PORT_NOT_FOUND_CACHE_ALL);
    }

    @Override
    public PortRecordBean getPortByPortNum(int lid, short portNum)
            throws SubnetDataNotFoundException {
        PortArray portArray = portsCache.get(lid);
        if (portArray != null) {
            PortRecordBean res = portArray.getPort(portNum);
            if (res != null) {
                return res;
            }
        } else {
            try {
                List<PortRecordBean> ports = helper.getPorts(lid);
                if (ports != null && !ports.isEmpty()) {
                    short maxPort = 0;
                    PortRecordBean res = null;
                    for (PortRecordBean port : ports) {
                        if (port.getPortNum() == portNum) {
                            res = port;
                        }
                        if (port.getPortNum() > maxPort) {
                            maxPort = port.getPortNum();
                        }
                    }
                    portsCache.push(lid, new PortArray(ports, maxPort));
                    if (res != null) {
                        return res;
                    }
                }
            } catch (Exception e) {
                throw SubnetApi.getSubnetException(e);
            }
        }

        throw new SubnetDataNotFoundException(
                STLMessages.STL30063_PORT_NOT_FOUND_CACHE,
                StringUtils.intHexString(lid), portNum);
    }

    @Override
    public PortRecordBean getPortByLocalPortNum(int lid, short localPortNum)
            throws SubnetDataNotFoundException {
        PortArray portArray = portsCache.get(lid);
        if (portArray != null) {
            PortRecordBean res = portArray.getPortByLocalPortNum(localPortNum);
            if (res != null) {
                return res;
            }
        } else {
            try {
                List<PortRecordBean> ports = helper.getPorts(lid);
                if (ports != null && !ports.isEmpty()) {
                    short maxPort = 0;
                    PortRecordBean res = null;
                    for (PortRecordBean port : ports) {
                        if (port.getPortInfo().getLocalPortNum() == localPortNum) {
                            res = port;
                        }
                        if (port.getPortNum() > maxPort) {
                            maxPort = port.getPortNum();
                        }
                    }
                    portsCache.push(lid, new PortArray(ports, maxPort));
                    if (res != null) {
                        return res;
                    }
                }
            } catch (Exception e) {
                throw SubnetApi.getSubnetException(e);
            }
        }

        throw new SubnetDataNotFoundException(
                STLMessages.STL30064_PORT_NOT_FOUND_CACHE_LOCAL, lid,
                localPortNum);
    }

    @Override
    public boolean hasPort(int lid, short portNum) {
        boolean res = false;
        NodeState nodeState = portsStates.get(lid);
        if (nodeState != null) {
            res = nodeState.isActivePort(portNum);
        } else {
            try {
                NodeCache nodeCache = cacheMgr.acquireNodeCache();
                NodeRecordBean node = nodeCache.getNode(lid);
                List<PortRecordBean> ports = helper.getPorts(lid);
                if (ports != null && !ports.isEmpty()) {
                    nodeState =
                            new NodeState(node.getNodeType(), node
                                    .getNodeInfo().getNumPorts());
                    portsStates.push(lid, nodeState);
                    for (PortRecordBean port : ports) {
                        nodeState.setActivePort(port.getPortNum());
                        if (port.getPortNum() == portNum) {
                            res = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    @Override
    public boolean hasLocalPort(int lid, short localPortNum) {
        boolean res = false;
        NodeState nodeState = portsStates.get(lid);
        if (nodeState != null) {
            res = nodeState.isActivePort(localPortNum);
        } else {
            try {
                NodeCache nodeCache = cacheMgr.acquireNodeCache();
                NodeRecordBean node = nodeCache.getNode(lid);
                List<PortRecordBean> ports = helper.getPorts(lid);
                if (ports != null && !ports.isEmpty()) {
                    nodeState =
                            new NodeState(node.getNodeType(), node
                                    .getNodeInfo().getNumPorts());
                    portsStates.push(lid, nodeState);
                    for (PortRecordBean port : ports) {
                        short lpc = port.getPortInfo().getLocalPortNum();
                        nodeState.setActivePort(lpc);
                        if (lpc == localPortNum) {
                            res = true;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return res;
    }

    @Override
    public EnumMap<NodeType, Long> getPortsTypeDist(
            boolean countInternalMgrPort, boolean refresh)
            throws SubnetDataNotFoundException {
        if (refresh || portsTypeDist.get() == null) {
            List<PortRecordBean> ports = getPorts();
            refresh(ports);
        }
        return portsTypeDist.get();
    }

    /**
     * 
     * <i>Description:</i> calculate ports type distribution with
     * InternalMgrPort and update ports state cache
     * 
     * @param ports
     * @return
     * @throws SubnetDataNotFoundException
     */
    protected synchronized void refresh(List<PortRecordBean> ports)
            throws SubnetDataNotFoundException {
        if (ports == null) {
            portsStates.clear();
            portsTypeDist.set(null);
            return;
        }

        NodeCache nodeCache = cacheMgr.acquireNodeCache();
        EnumMap<NodeType, Long> portsTypeDistMap =
                new EnumMap<NodeType, Long>(NodeType.class);
        Map<Integer, NodeState> processed = new HashMap<Integer, NodeState>();
        long desiredTotalPorts = 0;
        long realTotalPorts = 0;
        for (PortRecordBean port : ports) {
            int lid = port.getEndPortLID();
            NodeState state = processed.get(lid);
            if (state == null) {
                NodeRecordBean node = nodeCache.getNode(lid);
                NodeType type = node.getNodeType();
                state = new NodeState(type, node.getNodeInfo().getNumPorts());
                processed.put(lid, state);
                portsStates.push(lid, state);
                switch (type) {
                    case SWITCH:
                        desiredTotalPorts +=
                                node.getNodeInfo().getNumPorts() + 1;
                        break;
                    case HFI:
                        desiredTotalPorts += 1;
                        break;
                    default:
                        break;
                }
            }
            NodeType type = state.getType();
            Long count = portsTypeDistMap.get(type);
            portsTypeDistMap.put(type, count == null ? 1 : (count + 1));
            realTotalPorts += 1;
            if (type == NodeType.HFI) {
                state.setActivePort(port.getPortInfo().getLocalPortNum());
            } else {
                state.setActivePort(port.getPortNum());
            }
        }
        portsTypeDistMap
                .put(NodeType.OTHER, desiredTotalPorts - realTotalPorts);
        portsTypeDist.set(portsTypeDistMap);
    }

    @Override
    public long getSubnetPrefix() {
        if (subnetPrefix.get() == null) {
            List<PortRecordBean> ports;
            try {
                ports = getPorts();
                if (!ports.isEmpty()) {
                    long prefix = ports.get(0).getPortInfo().getSubnetPrefix();
                    subnetPrefix.set(new Long(prefix));
                } else {
                    return 0;
                }
            } catch (SubnetDataNotFoundException e) {
                return 0;
            }
        }
        return subnetPrefix.get();
    }

    @Override
    protected SimpleCache<Integer, PortArray> retrieveObjectForCache()
            throws Exception {
        // do not get ports here, we will do it when we really need it
        return portsCache;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.configuration.MemoryCache#reset()
     */
    @Override
    public void reset() {
        super.reset();
        portsStates.clear();
        portsCache.clear();
        portsTypeDist.set(null);
        subnetPrefix.set(null); // should be unnecessary
    }

    /**
     * Since PortCache is a memory only cache, just update the cache with
     * whatever the FM has. NoticeProcess should have the current Port
     * information.
     */
    @Override
    public boolean refreshCache(NoticeProcess notice) throws Exception {
        portsStates.remove(notice.getLid());
        portsCache.remove(notice.getLid());
        portsTypeDist.set(null);
        return true;
    }

    @Override
    protected RuntimeException processRefreshCacheException(Exception e) {
        return SubnetApi.getSubnetException(e);
    }

    protected class PortArray {
        private final PortRecordBean[] ports;

        public PortArray(List<PortRecordBean> beans, Short maxPort) {
            ports = new PortRecordBean[maxPort + 1];
            for (PortRecordBean bean : beans) {
                ports[bean.getPortNum()] = bean;
            }
        }

        public PortRecordBean getPort(short portNum) {
            if (portNum < ports.length) {
                return ports[portNum];
            } else {
                return null;
            }
        }

        public PortRecordBean getPortByLocalPortNum(short localPortNum) {
            for (PortRecordBean port : ports) {
                if (port != null
                        && port.getPortInfo().getLocalPortNum() == localPortNum) {
                    return port;
                }
            }
            return null;
        }

        public PortRecordBean[] getPorts() {
            return ports;
        }

    }

}
