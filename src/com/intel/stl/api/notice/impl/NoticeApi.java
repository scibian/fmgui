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

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.MDC;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.configuration.impl.SubnetContextImpl;
import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.FESource;
import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.IEventListener;
import com.intel.stl.api.notice.IEventSource;
import com.intel.stl.api.notice.INoticeApi;
import com.intel.stl.api.notice.NodeSource;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.notice.NoticeWrapper;
import com.intel.stl.api.notice.PortSource;
import com.intel.stl.api.notice.TrapLinkBean;
import com.intel.stl.api.notice.TrapSwitchPKeyBean;
import com.intel.stl.api.notice.TrapSysguidBean;
import com.intel.stl.api.notice.TrapType;
import com.intel.stl.api.subnet.GIDBean;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetException;
import com.intel.stl.api.subnet.impl.NodeCache;
import com.intel.stl.configuration.CacheManager;
import com.intel.stl.fecdriver.messages.adapter.sa.trap.TrapDetail;

public class NoticeApi implements INoticeApi {
    private static final String THREAD_NAME_PREFIX = "nedthread-";

    private static boolean DEBUG = false;

    private final SubnetContextImpl subnetContext;

    private final CacheManager cacheMgr;

    private final EventDispatcher worker;

    private final Map<EventType, NoticeSeverity> eventSeverityMap =
            new HashMap<EventType, NoticeSeverity>();

    public NoticeApi(SubnetContextImpl subnetContext) {
        worker = new EventDispatcher();
        this.subnetContext = subnetContext;
        this.cacheMgr = subnetContext.getCacheManager();

        startWorker();
    }

    protected void startWorker() {
        MDC.put("subnet", subnetContext.getSubnetDescription().getName());
        worker.setLoggingContextMap(MDC.getCopyOfContextMap());
        worker.setDaemon(true);
        worker.setName(THREAD_NAME_PREFIX
                + subnetContext.getSubnetDescription().getSubnetId());
        worker.start();
    }

    @Override
    public void addNewEventDescriptions(NoticeWrapper[] data) {
        if (DEBUG) {
            for (NoticeWrapper bean : data) {
                System.out.println(bean);
            }
        }
        List<EventDescription> events = new ArrayList<EventDescription>();
        for (NoticeWrapper nw : data) {
            EventDescription eventDescription = asEventDescription(nw);
            events.add(eventDescription);
        }
        worker.addEvents(events);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.notice.INoticeApi#addNoticeListener(com.intel.stl.api
     * .notice.INoticeListener)
     */
    @Override
    public void addEventListener(IEventListener<EventDescription> listener) {
        worker.addEventListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.notice.INoticeApi#removeNoticeListener(com.intel.stl
     * .api.notice.INoticeListener)
     */
    @Override
    public void removeEventListener(IEventListener<EventDescription> listener) {
        worker.removeEventListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.notice.INoticeApi#cleanup()
     */
    @Override
    public void cleanup() {
        worker.cleanup();
        worker.setStop(true);
    }

    protected EventDescription asEventDescription(NoticeWrapper noticeWrapper) {
        NoticeBean bean = noticeWrapper.getNotice();
        EventDescription res = new EventDescription();
        res.setId(bean.getId());
        res.setDate(new Date(bean.getReceiveTimestamp()));
        res.setRelatedNodes(noticeWrapper.getRelatedNodes());
        GenericNoticeAttrBean attr =
                (GenericNoticeAttrBean) bean.getAttributes();
        TrapType trap = TrapType.getTrapType(attr.getTrapNumber());
        IEventSource source = null;

        try {

            EventType eventType = EventType.getEventType(trap);
            res.setType(eventType);

            // Override default severity
            res.setSeverity(eventSeverityMap.get(eventType));

            switch (trap) {
                case SM_CONNECTION_LOST:
                case SM_CONNECTION_ESTABLISH:
                    source = getSMSource(bean);
                    break;
                case FE_CONNECTION_LOST:
                case FE_CONNECTION_ESTABLISH:
                    source = getFESource(bean);
                    break;
                case GID_NOW_IN_SERVICE:
                case GID_OUT_OF_SERVICE:
                case ADD_MULTICAST_GROUP:
                case DEL_MULTICAST_GROUP:
                    source = getEndPortSource(bean);
                    break;
                case LINK_INTEGRITY:
                case BUFFER_OVERRUN:
                case FLOW_WATCHDOG:
                    source = getPortSource(bean);
                    break;
                case LINK_PORT_CHANGE_STATE:
                case CHANGE_CAPABILITY:
                case BAD_M_KEY:
                case SMA_TRAP_LINK_WIDTH:
                case BAD_P_KEY:
                case BAD_Q_KEY:
                    int lid = TrapDetail.getLid(bean.getData());
                    source = getNodeSource(lid);
                    break;
                case SWITCH_BAD_PKEY:
                    TrapSwitchPKeyBean key =
                            TrapDetail.getTrapSwitchPKey(bean.getData());
                    source = getNodeSource(key.getLid1());
                    break;
                case CHANGE_SYSGUID:
                    TrapSysguidBean sysguid =
                            TrapDetail.getTrapSysguid(bean.getData());
                    source = getNodeSource(sysguid.getLid());
                    break;
                default:
                    source = getNodeSource(0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            res.setSource(source);
        }
        return res;
    }

    protected NodeSource getSMSource(NoticeBean bean) throws Exception {
        int lid = bean.getIssuerLID();
        NodeRecordBean node = null;
        NodeCache nodeCache = cacheMgr.acquireNodeCache();
        node = nodeCache.getNode(lid);
        NodeType type = node.getNodeType();
        return new NodeSource(lid, node.getNodeDesc(), type);
    }

    protected NodeSource getNodeSource(int lid) throws Exception {
        NodeRecordBean node = null;
        NodeCache nodeCache = cacheMgr.acquireNodeCache();
        node = nodeCache.getNode(lid);
        NodeType type = node.getNodeType();
        return new NodeSource(lid, node.getNodeDesc(), type);
    }

    protected FESource getFESource(NoticeBean bean) {
        HostInfo hi = subnetContext.getSubnetDescription().getCurrentFE();
        return new FESource(hi.getHost(), hi.getPort());
    }

    protected PortSource getEndPortSource(NoticeBean bean) {
        NodeCache nodeCache = cacheMgr.acquireNodeCache();
        GIDBean gid = TrapDetail.getGID(bean.getData());
        NodeRecordBean node = null;
        try {
            // This node might be from database. We don't know if it's in fabric
            // or not.
            node = nodeCache.getNode(gid.getInterfaceID());
            NodeType type = node.getNodeType();

            return new PortSource(node.getLid(), node.getNodeDesc(), type, node
                    .getNodeInfo().getLocalPortNum());
        } catch (Exception e) {
            IllegalArgumentException iae =
                    new IllegalArgumentException("Invalid GID " + gid);
            iae.initCause(e);
            throw iae;
        }
    }

    protected PortSource getPortSource(NoticeBean bean) throws SubnetException,
            SubnetDataNotFoundException {
        NodeCache nodeCache = cacheMgr.acquireNodeCache();
        TrapLinkBean link = TrapDetail.getTrapLink(bean.getData());
        NodeRecordBean node = null;
        node = nodeCache.getNode(link.getLid());
        NodeType type = NodeType.getNodeType(node.getNodeInfo().getNodeType());
        return new PortSource(link.getLid(), node.getNodeDesc(), type,
                link.getPort());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.notice.INoticeApi#setUserSettings(com.intel.stl.api
     * .configuration.UserSettings)
     */
    @Override
    public void setUserSettings(UserSettings userSettings) {
        List<EventRule> eventRules = userSettings.getEventRules();
        if (eventRules != null) {
            for (EventRule er : eventRules) {
                eventSeverityMap.put(er.getEventType(), er.getEventSeverity());
            }
        }
    }

}
