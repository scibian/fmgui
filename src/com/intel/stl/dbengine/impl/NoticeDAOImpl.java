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

package com.intel.stl.dbengine.impl;

import static com.intel.stl.common.STLMessages.STL30013_ERROR_SAVING_ENTITY;
import static com.intel.stl.common.STLMessages.STL30025_TOPOLOGY_NOT_FOUND;
import static com.intel.stl.datamanager.NoticeStatus.INFLIGHT;
import static com.intel.stl.datamanager.NoticeStatus.RECEIVED;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.configuration.PortState;
import com.intel.stl.api.notice.GenericNoticeAttrBean;
import com.intel.stl.api.notice.NoticeAttrBean;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.VendorNoticeAttrBean;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.datamanager.GenericNoticeRecord;
import com.intel.stl.datamanager.NodeRecord;
import com.intel.stl.datamanager.NoticeRecord;
import com.intel.stl.datamanager.NoticeStatus;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.TopologyLinkId;
import com.intel.stl.datamanager.TopologyLinkRecord;
import com.intel.stl.datamanager.TopologyNodeId;
import com.intel.stl.datamanager.TopologyNodeRecord;
import com.intel.stl.datamanager.TopologyRecord;
import com.intel.stl.datamanager.VendorNoticeRecord;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.NoticeDAO;
import com.intel.stl.dbengine.SubnetDAO;

public class NoticeDAOImpl extends BaseDAO implements NoticeDAO {
    private static Logger log = LoggerFactory.getLogger("org.hibernate.SQL");

    protected static int BATCH_SIZE = 100;

    private final SubnetDAO subnetDAO;

    public NoticeDAOImpl(EntityManager entityManager,
            DatabaseContext databaseCtx) {
        super(entityManager, databaseCtx);
        this.subnetDAO = databaseCtx.getSubnetDAO();
    }

    @Override
    public List<NoticeRecord> saveNotices(SubnetRecord subnetRec,
            NoticeBean[] notices) {

        List<NoticeRecord> noticeRecords = new ArrayList<NoticeRecord>();
        NoticeRecord noticeRec = null;
        for (NoticeBean notice : notices) {
            noticeRec = createNoticeRecord(subnetRec, notice);
            noticeRecords.add(noticeRec);
        }
        return noticeRecords;
    }

    @Override
    public Boolean processNotices(SubnetRecord subnet,
            List<NoticeProcess> notices) {
        TopologyRecord topology = subnet.getTopology();
        if (topology == null) {
            DatabaseException dbe =
                    new DatabaseException(STL30025_TOPOLOGY_NOT_FOUND,
                            subnet.getSubnetDescription().getName());
            throw dbe;
        }
        /*-
         *  The overall logic is as follows:
         *  - For each notice:
         *    * Check if the node associated with the notice exists in the
         *      database; if not, add it to the newNodes set
         *    * Check if the node is part of the current topology (a
         *      TopologyNodeRecord exists); if not, add it to the newTopoNodes set.
         *      Set the status of the node according to the TrapType in the notice.
         *    * Convert the links retrieved from the FM into TopologyLinkRecords,
         *      resolving LIDs to GUIDs (since the FromLid or the ToLid must match
         *      the LID of the notice, we only resolve the other one). Set the
         *      status of the link according to the TrapType in the notice
         *    * Retrieve the TopologyLinkRecords currently in the database for the
         *      LID in the notice (we use the GUID of the node). For each link found,
         *      we query the reverse link. Set the status of the link according to the
         *      TrapType in the notice
         *    * From the set of links retrieved from the FM, we remove those already
         *      in the database; the remaining links are added to the newLinks set
         *    * If a record is already in the database, it must end up in the
         *      currTopoNodes set or the currLinks set, accordingly.
         *    * If any new TopologyNodeRecord or TopologyLinkRecord is created, a
         *      copyTopology() is triggered (that is, a new topology is created);
         *      otherwise, the database records are just updated.
         */
        Set<NodeRecord> newNodes = new HashSet<NodeRecord>();
        Set<TopologyNodeRecord> newTopoNodes =
                new HashSet<TopologyNodeRecord>();
        Set<TopologyLinkRecord> newLinks = new HashSet<TopologyLinkRecord>();
        Set<TopologyNodeRecord> currTopoNodes =
                new HashSet<TopologyNodeRecord>();
        Set<TopologyLinkRecord> currLinks = new HashSet<TopologyLinkRecord>();

        prepareDatabaseData(topology, notices, newNodes, newTopoNodes, newLinks,
                currTopoNodes, currLinks);

        boolean topologyChanged =
                (newTopoNodes.size() > 0 || newLinks.size() > 0);

        if (topologyChanged) {
            subnetDAO.copyTopology(subnet, newTopoNodes, currTopoNodes,
                    newLinks, currLinks);
        } else {
            updateTopology(newNodes, newTopoNodes, currTopoNodes, newLinks,
                    currLinks);
        }

        return topologyChanged;
    }

    @Override
    public List<NoticeBean> getNotices(SubnetRecord subnet,
            NoticeStatus status) {
        List<NoticeRecord> noticeRecords = readNoticeRecords(subnet, status);

        List<NoticeBean> noticeBeans = new ArrayList<NoticeBean>();
        if (noticeRecords == null || noticeRecords.size() == 0) {
            return noticeBeans;
        }

        for (NoticeRecord notice : noticeRecords) {
            noticeBeans.add(notice.getNotice());
        }

        return noticeBeans;
    }

    @Override
    public List<NoticeBean> getNotices(SubnetRecord subnet, NoticeStatus status,
            NoticeStatus newStatus) {
        List<NoticeRecord> noticeRecords = readNoticeRecords(subnet, status);

        List<NoticeBean> noticeBeans = new ArrayList<NoticeBean>();
        if (noticeRecords == null || noticeRecords.size() == 0) {
            return noticeBeans;
        }

        for (NoticeRecord notice : noticeRecords) {
            startTransaction();
            notice.setNoticeStatus(newStatus);
            em.merge(notice);
            try {
                commitTransaction();
                noticeBeans.add(notice.getNotice());
            } catch (Exception e) {
                // Most probably, the update will fail because another thread
                // has updated it already. Just ignore the record and continue
                log.info(e.getMessage(), e);
            }
        }

        return noticeBeans;
    }

    @Override
    public void resetNotices(SubnetRecord subnet) {
        List<NoticeRecord> rcvdRecs = readNoticeRecords(subnet, RECEIVED);
        List<NoticeRecord> inflightRecs = readNoticeRecords(subnet, INFLIGHT);

        if (rcvdRecs.size() > 0 || inflightRecs.size() > 0) {
            int updates = 0;
            startTransaction();
            for (NoticeRecord notice : rcvdRecs) {
                updates = resetNotice(notice, updates);
            }
            for (NoticeRecord notice : inflightRecs) {
                updates = resetNotice(notice, updates);
            }

            if (updates > 0) {
                flush();
                clear();
            }
            commitTransaction();
        }
    }

    @Override
    public void updateNotice(SubnetRecord subnet, long noticeId,
            NoticeStatus noticeStatus) {
        TypedQuery<NoticeRecord> query = em
                .createNamedQuery("NoticeRecord.findById", NoticeRecord.class);
        query.setParameter("subnetId", subnet.getId());
        query.setParameter("id", noticeId);

        List<NoticeRecord> noticeRecs = query.getResultList();
        NoticeRecord notice = noticeRecs.get(0);
        notice.setNoticeStatus(noticeStatus);
        startTransaction();
        em.merge(notice);
        commitTransaction();
    }

    private void prepareDatabaseData(TopologyRecord topology,
            List<NoticeProcess> notices, Set<NodeRecord> newNodes,
            Set<TopologyNodeRecord> newTopoNodes,
            Set<TopologyLinkRecord> newLinks,
            Set<TopologyNodeRecord> currTopoNodes,
            Set<TopologyLinkRecord> currLinks) {
        for (NoticeProcess notice : notices) {
            int lid = notice.getLid();
            NodeRecordBean node = notice.getNode();
            if (node != null) {
                prepareNodeData(topology, notice, newNodes, newTopoNodes,
                        currTopoNodes);
                prepareLinkData(topology, notice, newLinks, currLinks);
            } else {
                // If node not available from FM, what node was this notice
                // reporting about (No NodeRecordBean associated with lid)?
                log.error("Could not find node information for lid: " + lid);
            }
        }
    }

    private void prepareNodeData(TopologyRecord topology, NoticeProcess notice,
            Set<NodeRecord> newNodes, Set<TopologyNodeRecord> newTopoNodes,
            Set<TopologyNodeRecord> currTopoNodes) {
        NodeRecordBean node = notice.getNode();
        long nodeGUID = node.getNodeInfo().getNodeGUID();
        int lid = node.getLid();
        NodeRecord nodeRec = em.find(NodeRecord.class, nodeGUID);
        TopologyNodeRecord topoNode;
        boolean topoNodeInSet = false;
        if (nodeRec == null) {
            nodeRec = new NodeRecord(node);
            newNodes.add(nodeRec);
            topoNode = createTopologyNode(topology, nodeRec, lid);
            newTopoNodes.add(topoNode);
            topoNodeInSet = true;
        } else {
            TopologyNodeId id = new TopologyNodeId();
            id.setTopologyId(topology.getId());
            id.setTopologyNode(nodeGUID);
            topoNode = em.find(TopologyNodeRecord.class, id);
            if (topoNode == null) {
                // The following is done to force Hibernate to read the
                // NodeRecord and the NodeRecordType; otherwise, subsequent
                // attempts to access this information will fail with a
                // LazyInitializationException
                nodeRec.getNode();

                topoNode = createTopologyNode(topology, nodeRec, lid);
                newTopoNodes.add(topoNode);
                topoNodeInSet = true;
            } else {
                topoNode.setLid(lid);
            }
        }
        switch (notice.getTrapType()) {
            case GID_NOW_IN_SERVICE:
                topoNode.setActive(true);
                if (!topoNodeInSet) {
                    currTopoNodes.add(topoNode);
                }
                break;
            case GID_OUT_OF_SERVICE:
                topoNode.setActive(false);
                if (!topoNodeInSet) {
                    currTopoNodes.add(topoNode);
                }
                break;
            case LINK_PORT_CHANGE_STATE:
                // Node status is not affected by this
                break;
            default:
        }
    }

    private TopologyNodeRecord createTopologyNode(TopologyRecord topology,
            NodeRecord node, int lid) {
        TopologyNodeRecord topoNode = new TopologyNodeRecord();
        topoNode.setTopology(topology);
        topoNode.setNode(node);
        topoNode.setLid(lid);
        return topoNode;
    }

    private void prepareLinkData(TopologyRecord topology, NoticeProcess notice,
            Set<TopologyLinkRecord> newLinks,
            Set<TopologyLinkRecord> currLinks) {
        Set<TopologyLinkRecord> dbLinks =
                getTopologyLinkRecords(topology, notice);
        Set<TopologyLinkRecord> fmLinks = getLinkSet(topology, notice);
        // Remove existing links from new links
        fmLinks.removeAll(dbLinks);
        newLinks.addAll(fmLinks);
        currLinks.addAll(dbLinks);
    }

    private Set<TopologyLinkRecord> getLinkSet(TopologyRecord topology,
            NoticeProcess notice) {
        List<LinkRecordBean> links = notice.getLinks();
        if (links == null) {
            return new HashSet<TopologyLinkRecord>();
        }
        Set<TopologyLinkRecord> linkSet =
                new HashSet<TopologyLinkRecord>(links.size());
        switch (notice.getTrapType()) {
            case GID_NOW_IN_SERVICE:
                createLinkRecordsAndSetStatus(topology, notice, linkSet, true);
                break;
            case GID_OUT_OF_SERVICE:
                createLinkRecordsAndSetStatus(topology, notice, linkSet, false);
                break;
            case LINK_PORT_CHANGE_STATE:
                Map<Short, PortState> portMap = new HashMap<Short, PortState>();
                for (PortRecordBean port : notice.getPorts()) {
                    PortState portState =
                            port.getPortInfo().getPortStates().getPortState();
                    portMap.put(port.getPortNum(), portState);
                }
                createLinkRecordsUsingPortStatus(topology, notice, linkSet,
                        portMap);
                break;
            default:
        }
        return linkSet;
    }

    private void createLinkRecordsAndSetStatus(TopologyRecord topology,
            NoticeProcess notice, Set<TopologyLinkRecord> linkSet,
            boolean status) {
        List<LinkRecordBean> links = notice.getLinks();
        int lid = notice.getLid();
        long topologyId = topology.getId();
        long nodeGUID = notice.getNode().getNodeInfo().getNodeGUID();
        for (LinkRecordBean link : links) {
            TopologyLinkRecord topoLink =
                    createTopologyLink(link, topologyId, nodeGUID, lid);
            if (topoLink != null) {
                topoLink.setActive(status);
                linkSet.add(topoLink);
            }
        }
    }

    private void createLinkRecordsUsingPortStatus(TopologyRecord topology,
            NoticeProcess notice, Set<TopologyLinkRecord> linkSet,
            Map<Short, PortState> portMap) {
        List<LinkRecordBean> links = notice.getLinks();
        int lid = notice.getLid();
        long topologyId = topology.getId();
        long nodeGUID = notice.getNode().getNodeInfo().getNodeGUID();
        for (LinkRecordBean link : links) {
            TopologyLinkRecord topoLink =
                    createTopologyLink(link, topologyId, nodeGUID, lid);
            if (topoLink != null) {
                PortState portState;
                if (link.getFromLID() == lid) {
                    portState = portMap.get(link.getFromPortIndex());
                } else {
                    portState = portMap.get(link.getToPortIndex());
                }
                if (portState == PortState.ACTIVE) {
                    topoLink.setActive(true);
                } else {
                    topoLink.setActive(false);
                }
                linkSet.add(topoLink);
            }
        }
    }

    private TopologyLinkRecord createTopologyLink(LinkRecordBean link,
            long topologyId, long nodeGUID, int lid) {
        TopologyNodeRecord topoNode;
        TopologyLinkRecord topoLink = new TopologyLinkRecord(link);
        TopologyLinkId id = topoLink.getId();
        id.setLinkTopology(topologyId);
        int fromLid = link.getFromLID();
        int toLid = link.getToLID();
        if (fromLid == lid) {
            id.setSourceNode(nodeGUID);
            topoNode = subnetDAO.getTopologyNodeRecord(topologyId, toLid);
            // The TopologyNodeRecord might be null in the following
            // scenario: suppose a switch with lid A with all its connected
            // nodes are being powered on; suppose one of those nodes have a
            // lid B; when we get the notice for lid A, node with lid B is
            // not yet in the database so we can add the switch to the
            // database but not the link to lid B (we don't know the node
            // GUID for that yet). We would expected another notice with lid
            // B to be processed; at that moment, we will be able to add the
            // link. So here, we just ignore the link, hoping for the other
            // notice.
            if (topoNode == null) {
                log.info("Cannot add link because lid '" + toLid
                        + "' is not yet in database: " + link);
                return null;
            } else {
                id.setTargetNode(topoNode.getNode().getNodeGUID());
                return topoLink;
            }
        } else if (toLid == lid) {
            id.setTargetNode(nodeGUID);
            topoNode = subnetDAO.getTopologyNodeRecord(topologyId, fromLid);
            // See comments above
            if (topoNode == null) {
                log.info("Cannot add link because lid '" + fromLid
                        + "' is not yet in database: " + link);
                return null;
            } else {
                id.setSourceNode(topoNode.getNode().getNodeGUID());
                return topoLink;
            }
        } else {
            log.error("Link is not related to lid '" + lid + "': " + link);
            return null;
        }
    }

    private Set<TopologyLinkRecord> getTopologyLinkRecords(
            TopologyRecord topology, NoticeProcess notice) {
        long topologyId = topology.getId();
        long nodeGUID = notice.getNode().getNodeInfo().getNodeGUID();
        // This returns only the TopologyLinkRecords where nodeGUID is the
        // source node; attempting to find TopologyLinkRecords where nodeGUID is
        // the target node would trigger a full scan of the table.
        List<TopologyLinkRecord> links =
                subnetDAO.getTopologyLinkRecords(topologyId, nodeGUID);
        if (links == null) {
            return new HashSet<TopologyLinkRecord>();
        }

        Set<TopologyLinkRecord> linkSet =
                new HashSet<TopologyLinkRecord>(links.size() * 2);
        switch (notice.getTrapType()) {
            case GID_NOW_IN_SERVICE:
                populateLinkSetAndSetStatus(links, linkSet, true);
                break;
            case GID_OUT_OF_SERVICE:
                populateLinkSetAndSetStatus(links, linkSet, false);
                break;
            case LINK_PORT_CHANGE_STATE:
                Map<Short, PortState> portMap = new HashMap<Short, PortState>();
                for (PortRecordBean port : notice.getPorts()) {
                    PortState portState =
                            port.getPortInfo().getPortStates().getPortState();
                    portMap.put(port.getPortNum(), portState);
                }
                populateLinkSetUsingPortStatus(links, linkSet, portMap);
                break;
            default:
        }
        return linkSet;
    }

    private void populateLinkSetAndSetStatus(List<TopologyLinkRecord> links,
            Set<TopologyLinkRecord> linkSet, boolean status) {
        for (TopologyLinkRecord topoLink : links) {
            TopologyLinkId id = topoLink.getId();
            TopologyLinkId revId = new TopologyLinkId();
            revId.setLinkTopology(id.getLinkTopology());
            revId.setSourceNode(id.getTargetNode());
            revId.setSourcePort(id.getTargetPort());
            revId.setTargetNode(id.getSourceNode());
            revId.setTargetPort(id.getSourcePort());
            TopologyLinkRecord revTopoLink =
                    em.find(TopologyLinkRecord.class, revId);
            if (revTopoLink == null) {
                // happens when we have a new active link
                revTopoLink = new TopologyLinkRecord();
                revTopoLink.setId(revId);
            }
            revTopoLink.setActive(status);
            linkSet.add(revTopoLink);

            topoLink.setActive(status);
            linkSet.add(topoLink);
        }
    }

    private void populateLinkSetUsingPortStatus(List<TopologyLinkRecord> links,
            Set<TopologyLinkRecord> linkSet, Map<Short, PortState> portMap) {
        for (TopologyLinkRecord topoLink : links) {
            TopologyLinkId id = topoLink.getId();
            TopologyLinkId revId = new TopologyLinkId();
            revId.setLinkTopology(id.getLinkTopology());
            revId.setSourceNode(id.getTargetNode());
            revId.setSourcePort(id.getTargetPort());
            revId.setTargetNode(id.getSourceNode());
            revId.setTargetPort(id.getSourcePort());
            // This record must exist in the database
            TopologyLinkRecord revTopoLink =
                    em.find(TopologyLinkRecord.class, revId);
            PortState portState = portMap.get(id.getSourcePort());
            if (portState == PortState.ACTIVE) {
                topoLink.setActive(true);
                if (revTopoLink != null) {
                    revTopoLink.setActive(true);
                }
            } else {
                topoLink.setActive(false);
                if (revTopoLink != null) {
                    revTopoLink.setActive(false);
                }
            }
            linkSet.add(topoLink);
            if (revTopoLink != null) {
                linkSet.add(revTopoLink);
            }
        }
    }

    private void updateTopology(Set<NodeRecord> newNodes,
            Set<TopologyNodeRecord> newTopoNodes,
            Set<TopologyNodeRecord> currTopoNodes,
            Set<TopologyLinkRecord> newTopoLinks,
            Set<TopologyLinkRecord> currTopoLinks) {
        if (newNodes.size() > 0) {
            startTransaction();
            for (NodeRecord node : newNodes) {
                em.persist(node);
            }
            commitTransaction();
        }
        startTransaction();
        for (TopologyNodeRecord topoNode : newTopoNodes) {
            em.persist(topoNode);
        }
        for (TopologyLinkRecord topoLink : newTopoLinks) {
            em.persist(topoLink);
        }
        for (TopologyNodeRecord topoNode : currTopoNodes) {
            em.merge(topoNode);
        }
        for (TopologyLinkRecord topoLink : currTopoLinks) {
            em.merge(topoLink);
        }
        commitTransaction();
    }

    private NoticeRecord createNoticeRecord(SubnetRecord subnetRec,
            NoticeBean noticeBean) {

        // Either Generic or Vendor
        NoticeRecord noticeRec = null;
        NoticeAttrBean attr = noticeBean.getAttributes();
        if (attr.isGeneric()) {
            noticeRec = new GenericNoticeRecord();
            ((GenericNoticeRecord) noticeRec)
                    .setGenericNoticeAttr((GenericNoticeAttrBean) attr);
        } else {
            noticeRec = new VendorNoticeRecord();
            ((VendorNoticeRecord) noticeRec)
                    .setVendorNoticeAttr((VendorNoticeAttrBean) attr);
        }
        noticeRec.setNoticeStatus(RECEIVED);
        noticeRec.setNotice(noticeBean);
        noticeRec.setSubnet(subnetRec);

        startTransaction();
        em.persist(noticeRec);

        try {
            commitTransaction();
        } catch (Exception e) {
            throwPersistDatabaseException(e, "NoticeRecord",
                    noticeRec.getId().getNoticeId());
        }

        return noticeRec;
    }

    private List<NoticeRecord> readNoticeRecords(SubnetRecord subnet,
            NoticeStatus status) {
        TypedQuery<NoticeRecord> query = em.createNamedQuery(
                "NoticeRecord.findBySubnet", NoticeRecord.class);
        query.setParameter("subnetId", subnet.getId());
        query.setParameter("noticeStatus", status);
        return query.getResultList();
    }

    private int resetNotice(NoticeRecord notice, int updates) {
        notice.setNoticeStatus(NoticeStatus.PROCESSED);
        em.merge(notice);
        updates++;
        if (updates >= BATCH_SIZE) {
            flush();
            clear();
            updates = 0;
        }
        return updates;
    }

    private void throwPersistDatabaseException(Throwable cause, String entity,
            Object entityId) throws DatabaseException {
        Throwable last = cause;
        while (last.getCause() != null) {
            last = last.getCause();
        }
        DatabaseException dbe =
                new DatabaseException(STL30013_ERROR_SAVING_ENTITY, cause,
                        entity, entityId, last.getMessage());
        throw dbe;
    }
}
