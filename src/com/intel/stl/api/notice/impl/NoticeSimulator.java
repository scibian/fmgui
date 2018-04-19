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

package com.intel.stl.api.notice.impl;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.NoticeType;
import com.intel.stl.api.notice.ProducerType;
import com.intel.stl.api.notice.TrapType;
import com.intel.stl.api.subnet.GIDGlobal;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.impl.NodeCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.fecdriver.messages.adapter.sa.GID;

public class NoticeSimulator {
    private static Logger log = LoggerFactory.getLogger(NoticeSimulator.class);

    private static final String SIM_THREAD_PREFIX = "simthread-";

    private List<NodeRecordBean> nodes;

    private final List<IEventListener<NoticeBean>> listeners =
            new CopyOnWriteArrayList<IEventListener<NoticeBean>>();

    private final CacheManager cacheMgr;

    private Thread worker;

    private boolean stop;

    private final Random random = new Random();

    private final int minSleepTime = 10000; // 1 sec

    private final INoticeCreator hfiActiveCreator = new INoticeCreator() {

        @Override
        public NoticeBean createNotice(NodeRecordBean node) {
            NoticeBean res = new NoticeBean(true);
            GenericNoticeAttrBean attr = new GenericNoticeAttrBean();
            attr.setGeneric(true);
            attr.setType(NoticeType.INFO.getId());
            attr.setProducerType(ProducerType.CA.getId());
            attr.setTrapNumber(TrapType.GID_NOW_IN_SERVICE.getId());
            res.setAttributes(attr);
            res.setIssuerLID(node.getLid());
            res.setIssuerGID(new GIDGlobal());
            GID.Global gid = new GID.Global();
            gid.build(true);
            gid.setInterfaceId(node.getNodeInfo().getPortGUID());
            res.setData(gid.getByteBuffer().array());
            return res;
        }

    };

    private final INoticeCreator hfiInactiveCreator = new INoticeCreator() {

        @Override
        public NoticeBean createNotice(NodeRecordBean node) {
            NoticeBean res = new NoticeBean(true);
            GenericNoticeAttrBean attr = new GenericNoticeAttrBean();
            attr.setGeneric(true);
            attr.setType(NoticeType.FATAL.getId());
            attr.setProducerType(ProducerType.CA.getId());
            attr.setTrapNumber(TrapType.GID_OUT_OF_SERVICE.getId());
            res.setAttributes(attr);
            res.setIssuerLID(node.getLid());
            res.setIssuerGID(new GIDGlobal());
            GID.Global gid = new GID.Global();
            gid.build(true);
            gid.setInterfaceId(node.getNodeInfo().getPortGUID());
            res.setData(gid.getByteBuffer().array());
            return res;
        }

    };

    private final INoticeCreator linkChangeCreator = new INoticeCreator() {

        @Override
        public NoticeBean createNotice(NodeRecordBean node) {
            NoticeBean res = new NoticeBean(true);
            GenericNoticeAttrBean attr = new GenericNoticeAttrBean();
            attr.setGeneric(true);
            attr.setType(NoticeType.URGENT.getId());
            attr.setProducerType(ProducerType.SWITCH.getId());
            attr.setTrapNumber(TrapType.LINK_PORT_CHANGE_STATE.getId());
            res.setAttributes(attr);
            res.setIssuerLID(node.getLid());
            res.setIssuerGID(new GIDGlobal());
            ByteBuffer buffer = ByteBuffer.allocate(4);
            buffer.putInt(node.getLid());
            res.setData(buffer.array());
            return res;
        }

    };

    /**
     * Description:
     * 
     * @param subnetApi
     */
    public NoticeSimulator(CacheManager cacheMgr) {
        super();
        this.cacheMgr = cacheMgr;
    }

    /**
     * Description:
     * 
     * @param seed
     */
    public void setSeed(long seed) {
        random.setSeed(seed);
    }

    public synchronized void run() {
        if (worker != null && !stop) {
            return;
        }

        stop = false;
        worker = new Thread(new Runnable() {
            @Override
            public void run() {
                simulate();
            }
        });
        SubnetDescription subnet =
                cacheMgr.getSAHelper().getSubnetDescription();
        worker.setName(SIM_THREAD_PREFIX + subnet.getSubnetId());
        worker.start();
    }

    public synchronized void stop() {
        stop = true;
    }

    protected void simulate() {
        while (!stop) {
            long sleepTime = (long) (random.nextDouble() * 2000) + minSleepTime;
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
            }
            if (nodes == null) {
                NodeCache nodeCache = cacheMgr.acquireNodeCache();
                try {
                    nodes = nodeCache.getNodes(false);
                } catch (SubnetDataNotFoundException e) {
                }
            }
            if (nodes != null) {
                NoticeBean[] notices = new NoticeBean[10];
                for (int i = 0; i < notices.length; i++) {
                    notices[i] = createNotice();
                }
                String subnetName =
                        cacheMgr.getSAHelper().getSubnetDescription().getName();
                cacheMgr.getDatabaseManager().saveNotices(subnetName, notices);
                fireNotice(notices);
            }
        }
    }

    protected NoticeBean createNotice() {
        int nodeIndex = random.nextInt(nodes.size());
        NodeRecordBean node = nodes.get(nodeIndex);
        NoticeBean notice = null;
        if (node.getNodeInfo().getNodeType() == NodeType.HFI.getId()) {
            if (random.nextBoolean()) {
                notice = hfiActiveCreator.createNotice(node);
            } else {
                notice = hfiInactiveCreator.createNotice(node);
            }
        } else {
            notice = linkChangeCreator.createNotice(node);
        }
        return notice;
    }

    protected void fireNotice(NoticeBean[] notices) {
        for (IEventListener<NoticeBean> listener : listeners) {
            listener.onNewEvent(notices);
        }
    }

    public void addEventListener(IEventListener<NoticeBean> listener) {
        listeners.add(listener);
    }

    public void removeEventListener(IEventListener<NoticeBean> listener) {
        listeners.remove(listener);
    }

    private interface INoticeCreator {
        NoticeBean createNotice(NodeRecordBean node);
    }

}
