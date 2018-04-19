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
/*******************************************************************************
 *                       I N T E L   C O R P O R A T I O N
 *	
 *  Functional Group: Fabric Viewer Application
 *
 *  File Name: GroupDAOImpl.java
 *
 *
 *  Overview: 
 *
 ******************************************************************************/
package com.intel.stl.dbengine.impl;

import static com.intel.stl.common.STLMessages.STL30045_GROUP_CONFIG_NOT_FOUND;
import static com.intel.stl.common.STLMessages.STL30053_PORT_CONFIG_NOT_FOUND_SUBNET;
import static com.intel.stl.common.STLMessages.STL30054_GROUP_INFO_NOT_FOUND_TIME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.PerformanceDataNotFoundException;
import com.intel.stl.api.performance.PortConfigBean;
import com.intel.stl.datamanager.GroupConfigId;
import com.intel.stl.datamanager.GroupConfigRecord;
import com.intel.stl.datamanager.GroupInfoId;
import com.intel.stl.datamanager.GroupInfoRecord;
import com.intel.stl.datamanager.PortConfigId;
import com.intel.stl.datamanager.PortConfigRecord;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.TopologyRecord;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.GroupDAO;

public class GroupDAOImpl extends BaseDAO implements GroupDAO {
    private static Logger log = LoggerFactory.getLogger("org.hibernate.SQL");

    protected static int BATCH_SIZE = 1000;

    protected static int BATCH_DELETE = 100;

    public GroupDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public GroupDAOImpl(EntityManager entityManager, DatabaseContext databaseCtx) {
        super(entityManager, databaseCtx);
    }

    /**
     * 
     * Create GroupConfigRecord and save it to database.
     * 
     * Even for each default group(Switches, routers, HCAs, nodes), this will be
     * invoked. For default group, the List<PortConfigBean> will be null.
     * However, we just want to treat this as a general group.
     * 
     * 
     */
    @Override
    public GroupConfigRecord saveGroupConfig(SubnetRecord subnet,
            String groupName, List<PortConfigBean> ports) {
        GroupConfigRecord newGroupConfig = null;
        newGroupConfig = createGroupConfig(subnet, groupName, ports);

        return newGroupConfig;
    }

    @Override
    public void saveGroupList(SubnetRecord subnet, List<GroupListBean> groupList) {
        int numGroups = groupList.size();
        // String[] groups = groupList.getGroupNames();
        List<GroupConfigId> saveList = new ArrayList<GroupConfigId>();
        for (int i = 0; i < numGroups; i++) {
            GroupConfigId groupConfigId = new GroupConfigId();
            groupConfigId.setFabricId(subnet.getId());
            groupConfigId.setSubnetGroup(groupList.get(i).getGroupName());
            GroupConfigRecord groupConfig =
                    em.find(GroupConfigRecord.class, groupConfigId);
            if (groupConfig == null) {
                saveList.add(groupConfigId);
            }
        }
        if (saveList.size() > 0) {
            StringBuffer keys = new StringBuffer();
            keys.append(subnet.getSubnetDescription().getName());
            char separator = '|';
            startTransaction();
            for (GroupConfigId id : saveList) {
                GroupConfigRecord groupConfigRec = new GroupConfigRecord();
                groupConfigRec.setId(id);
                keys.append(separator);
                keys.append(id.getSubnetGroup());
                separator = ',';
                em.persist(groupConfigRec);
            }
            try {
                commitTransaction();
            } catch (Exception e) {
                throw createPersistDatabaseException(e,
                        GroupConfigRecord.class, keys);
            }
        }

    }

    /**
     * 
     * Description: Populate the GROUPS table. All Primary keys and non null
     * columns should be populated.
     * 
     * 
     * @param subnetRec
     * @param groupName
     * @param ports
     * @return
     * @throws DatabaseException
     */
    private GroupConfigRecord createGroupConfig(SubnetRecord subnet,
            String groupName, List<PortConfigBean> ports) {
        // Create an object of GroupConfigRecord and set non null fields before
        // trying to save it to DB.
        GroupConfigRecord groupConfigRec = new GroupConfigRecord();
        GroupConfigId groupConfigId = new GroupConfigId();
        groupConfigId.setFabricId(subnet.getId());
        groupConfigId.setSubnetGroup(groupName);
        groupConfigRec.setId(groupConfigId);

        startTransaction();
        em.persist(groupConfigRec);
        // Create the list of the PortConfigRecord from the list of the
        // PortConfigBean
        // and save it to DB.
        persistGroupPorts(groupConfigRec, ports);

        try {
            commitTransaction();
        } catch (Exception e) {
            throw createPersistDatabaseException(e, GroupConfigRecord.class,
                    groupName);
        }
        return groupConfigRec;
    }

    /**
     * 
     * Description: Populate the GROUPS_NODES_PORTS table. All Primary keys and
     * non null columns should be populated.
     * 
     * @param groupConfig
     * @param ports
     * @throws DatabaseException
     */
    private void persistGroupPorts(GroupConfigRecord groupConfig,
            List<PortConfigBean> ports) {
        long updates = 0;

        // From PortConfigBean get nodeGUID, port number and pass them to
        // PortConfigRecord.
        if (ports != null) {
            for (int i = 0; i < ports.size(); i++) {
                PortConfigBean portConfigBean = ports.get(i);

                PortConfigRecord portConfigRec = new PortConfigRecord();
                PortConfigId portConfigId = new PortConfigId();

                // Populate all primary and non null columns.
                portConfigId.setGroupId(groupConfig.getId());
                portConfigId.setNodeGUID(portConfigBean.getNodeGUID());
                portConfigId.setPortNumber(portConfigBean.getPortNumber());

                portConfigRec.setId(portConfigId);
                portConfigRec.setGroupConfig(groupConfig);

                em.persist(portConfigRec);
                updates++;
                if (updates >= BATCH_SIZE) {
                    flush();
                    clear();
                    updates = 0;
                }
            }
            if (updates > 0) {
                flush();
                clear();
            }
        }

    }

    /**
     * Retrieve the list of PortConfigBean from joins among TOPOLOGIES_NODES,
     * NODES and GROUP_NODES_PORTS database tables with topology ID and node
     * GUID.
     * 
     */
    @Override
    public List<PortConfigBean> getPortConfig(SubnetRecord subnetRec)
            throws PerformanceDataNotFoundException {

        TopologyRecord topology = subnetRec.getTopology();
        long topologyId = topology.getId();

        TypedQuery<PortConfigBean> query =
                em.createNamedQuery("PortConfigBean.findByTopId",
                        PortConfigBean.class);
        query.setParameter("topologyId", topologyId);
        List<PortConfigBean> portConfigBean = query.getResultList();
        if (portConfigBean == null || portConfigBean.size() == 0) {
            throw createPortConfigNotFoundException(topologyId);
        }
        return portConfigBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.dbengine.GroupDAO#getGroupConfig(com.intel.stl.datamanager
     * .GroupConfigId)
     */
    @Override
    public List<PortConfigBean> getGroupConfig(GroupConfigId groupConfigId)
            throws PerformanceDataNotFoundException {
        long subnetId = groupConfigId.getFabricId();
        String groupName = groupConfigId.getSubnetGroup();
        TypedQuery<PortConfigBean> query =
                em.createNamedQuery("PortConfigBean.findByGroupName",
                        PortConfigBean.class);
        query.setParameter("subnetId", subnetId);
        query.setParameter("groupName", groupName);
        List<PortConfigBean> portConfigBean = query.getResultList();
        if (portConfigBean == null || portConfigBean.size() == 0) {
            GroupConfigRecord rec =
                    em.find(GroupConfigRecord.class, groupConfigId);
            if (rec == null) {
                throw createGroupConfigNotFoundException(
                        groupConfigId.getFabricId(),
                        groupConfigId.getSubnetGroup());
            } else {
                portConfigBean = Collections.emptyList();
            }
        }
        return portConfigBean;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.dbengine.GroupDAO#saveGroupInfo(java.lang.String,
     * java.lang.String, com.intel.stl.datamanager.GroupConfigRecord)
     */
    @Override
    public void saveGroupInfos(SubnetRecord subnet,
            List<GroupInfoBean> groupInfoBeans) {
        StringBuffer keys = new StringBuffer();
        keys.append(subnet.getSubnetDescription().getName());
        char separator = '|';

        startTransaction();
        long now = System.currentTimeMillis();
        for (GroupInfoBean groupInfo : groupInfoBeans) {
            GroupInfoRecord groupInfoRec = createGroupInfo(subnet, groupInfo);
            GroupInfoRecord dbGroupInfo =
                    em.find(GroupInfoRecord.class, groupInfoRec.getId());
            if (dbGroupInfo == null) {
                em.persist(groupInfoRec);
                keys.append(separator);
                GroupInfoId groupInfoId = groupInfoRec.getId();
                if (groupInfoId != null) {
                    GroupConfigId groupConfigId = groupInfoId.getGroupID();
                    if (groupConfigId != null) {
                        String subnetGroup = groupConfigId.getSubnetGroup();
                        keys.append(subnetGroup);
                        keys.append('-');
                        keys.append(groupInfoRec.getGroupInfo().getTimestamp());
                        separator = ',';
                    } else {
                        throw new RuntimeException(
                                "groupConfigId is null in saveGroupInfos.");
                    }
                } else {
                    throw new RuntimeException(
                            "groupInfoId is null in saveGroupInfos.");
                }
            }
        }

        try {
            commitTransaction();
        } catch (Exception e) {
            throw createPersistDatabaseException(e, GroupInfoRecord.class, keys);
        }
    }

    @Override
    public int purgeGroupInfos(SubnetRecord subnet, Long ago) {
        // Hibernate currently does not support cascading delete of
        // ElementCollections for bulk deletes, we need then to delete record by
        // record, which might be memory expensive. We batch deletions to
        // improve performance.
        int offset = 0;
        int deleted = 0;
        String select =
                "select rec from GroupInfoRecord as rec where rec.id.groupId.fabricId = :subnetId and rec.id.sweepTimestamp < :stopTime";
        TypedQuery<GroupInfoRecord> query =
                em.createQuery(select, GroupInfoRecord.class);
        query.setParameter("subnetId", subnet.getId());
        long linuxTime = ago / 1000;
        query.setParameter("stopTime", linuxTime);
        query.setFirstResult(offset);
        query.setMaxResults(BATCH_DELETE);
        List<GroupInfoRecord> recs = query.getResultList();

        while (recs.size() > 0) {
            startTransaction();
            StringBuffer keys = new StringBuffer();
            char separator = '|';
            for (GroupInfoRecord rec : recs) {
                keys.append(separator);
                keys.append(rec.getId().getGroupID().getSubnetGroup());
                keys.append('-');
                keys.append(rec.getGroupInfo().getTimestamp());
                separator = ',';
                em.remove(rec);
            }
            try {
                deleted += recs.size();
                flush();
                clear();
                commitTransaction();
                log.info("Deleted {} GroupInfo records before {}", recs.size(),
                        ago);
                recs = query.getResultList();
            } catch (Exception e) {
                throw createPersistDatabaseException(e, GroupInfoRecord.class,
                        keys);
            }
        }
        return deleted;
    }

    /**
     * Description: Populate the GROUPS_INFOS table. All Primary keys and non
     * null columns should be populated.
     * 
     * @param subnetName
     * @param groupName
     * @return
     * @throws DatabaseException
     */
    private GroupInfoRecord createGroupInfo(SubnetRecord subnet,
            GroupInfoBean groupInfo) {
        // Create an object of GroupConfigRecord and set non null fields before
        // trying to save it to DB.
        GroupInfoRecord groupInfoRec = new GroupInfoRecord();
        GroupInfoId groupInfoId = new GroupInfoId();

        groupInfoId.setSweepTimestamp(groupInfo.getTimestamp());

        GroupConfigId groupConfigId = new GroupConfigId();
        groupConfigId.setFabricId(subnet.getId());
        groupConfigId.setSubnetGroup(groupInfo.getGroupName());

        groupInfoId.setGroupID(groupConfigId);

        // Don't need the following. GroupInfoBean includes all non null column
        // data.
        // However, note in the GroupDAOImplTest that the GROUPS table need to
        // have an entry for this
        // groupConfig before being able to persist this GroupInfoRecord because
        // it's a foreign key.
        // GroupConfigRecord groupConfigRec =
        // getGroupConfigWithException(groupConfigId);
        // groupInfoRec.setGroupConfig(groupConfigRec);

        groupInfoRec.setId(groupInfoId);
        groupInfoRec.setGroupInfo(groupInfo);

        return groupInfoRec;
    }

    /**
     * Retrieve group info for a specific time span.
     * 
     */
    @Override
    public List<GroupInfoBean> getGroupInfoList(SubnetRecord subnet,
            String groupName, long startTime, long stopTime)
            throws PerformanceDataNotFoundException {
        TypedQuery<GroupInfoRecord> query =
                em.createNamedQuery("GroupInfoBean.findByTime",
                        GroupInfoRecord.class);
        query.setParameter("subnetId", subnet.getId());
        query.setParameter("groupName", groupName);
        query.setParameter("startTime", startTime);
        query.setParameter("stopTime", stopTime);
        List<GroupInfoRecord> groupInfoRecs = query.getResultList();

        if (groupInfoRecs == null || groupInfoRecs.size() == 0) {
            throw createGroupInfoNotFoundException(subnet
                    .getSubnetDescription().getName(), groupName, startTime,
                    stopTime);
        }
        List<GroupInfoBean> groupInfoBeans =
                new ArrayList<GroupInfoBean>(groupInfoRecs.size());
        for (GroupInfoRecord groupInfoRec : groupInfoRecs) {
            groupInfoBeans.add(groupInfoRec.getGroupInfo());
        }
        return groupInfoBeans;

    }

    private DatabaseException createPersistDatabaseException(Throwable cause,
            Class<?> entityClass, Object entityId) {
        DatabaseException dbe =
                DatabaseUtils.createPersistDatabaseException(cause,
                        entityClass, entityId);
        log.error(dbe.getMessage(), cause);
        return dbe;
    }

    private PerformanceDataNotFoundException createPortConfigNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException ge =
                new PerformanceDataNotFoundException(
                        STL30053_PORT_CONFIG_NOT_FOUND_SUBNET, arguments);
        return ge;
    }

    private PerformanceDataNotFoundException createGroupInfoNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException pe =
                new PerformanceDataNotFoundException(
                        STL30054_GROUP_INFO_NOT_FOUND_TIME, arguments);
        return pe;
    }

    private PerformanceDataNotFoundException createGroupConfigNotFoundException(
            Object... arguments) {
        PerformanceDataNotFoundException ge =
                new PerformanceDataNotFoundException(
                        STL30045_GROUP_CONFIG_NOT_FOUND, arguments);
        return ge;
    }
}
