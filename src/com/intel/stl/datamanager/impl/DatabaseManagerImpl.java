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
package com.intel.stl.datamanager.impl;

import static com.intel.stl.api.subnet.NodeType.HFI;
import static com.intel.stl.api.subnet.NodeType.ROUTER;
import static com.intel.stl.api.subnet.NodeType.SWITCH;
import static com.intel.stl.api.subnet.NodeType.UNKNOWN;
import static com.intel.stl.common.STLMessages.STL10006_ERROR_STARTING_DATABASE_ENGINE;
import static com.intel.stl.common.STLMessages.STL10018_DATABASE_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10025_STARTING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10026_STOPPING_COMPONENT;
import static com.intel.stl.common.STLMessages.STL10028_COMPACTING_DATABASE;
import static com.intel.stl.common.STLMessages.STL30001_STARTING_DATABASE_ENGINE;
import static com.intel.stl.common.STLMessages.STL30010_STARTING_SCHEMA_UPDATE;
import static com.intel.stl.common.STLMessages.STL30011_ERROR_UPDATING_SCHEMA;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

import javax.persistence.EntityManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.StartupProgressObserver;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.AppInfo;
import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.GroupConfigRspBean;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.PerformanceDataNotFoundException;
import com.intel.stl.api.performance.PortConfigBean;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.configuration.AppComponent;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.configuration.AppSettings;
import com.intel.stl.datamanager.DatabaseCall;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.datamanager.DatabaseRecord;
import com.intel.stl.datamanager.GroupConfigId;
import com.intel.stl.datamanager.NoticeStatus;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.TopologyRecord;
import com.intel.stl.datamanager.UserRecord;
import com.intel.stl.dbengine.ConfigurationDAO;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.DatabaseEngine;
import com.intel.stl.dbengine.DatabaseServer;
import com.intel.stl.dbengine.GroupDAO;
import com.intel.stl.dbengine.NoticeDAO;
import com.intel.stl.dbengine.PerformanceDAO;
import com.intel.stl.dbengine.Scheduler;
import com.intel.stl.dbengine.SubnetDAO;
import com.intel.stl.dbengine.impl.DatabaseServerImpl;
import com.intel.stl.dbengine.impl.DatabaseUtils;
import com.intel.stl.dbengine.impl.SchedulerImpl;

public class DatabaseManagerImpl implements DatabaseManager, AppComponent {
    private static final String DATABASEMANAGER_COMPONENT =
            STL10018_DATABASE_COMPONENT.getDescription();

    private static final String PROGRESS_MESSAGE = STL10025_STARTING_COMPONENT
            .getDescription(DATABASEMANAGER_COMPONENT);

    private static final String SHUTDOWN_MESSAGE = STL10026_STOPPING_COMPONENT
            .getDescription(DATABASEMANAGER_COMPONENT);

    protected static int THREAD_POOL_SIZE = 2;

    private static Logger log =
            LoggerFactory.getLogger(DatabaseManagerImpl.class);

    private final Scheduler scheduler;

    private final DatabaseEngine engine;

    private boolean compactNeeded = false;

    public DatabaseManagerImpl(DatabaseEngine engine) {
        this.engine = engine;
        DatabaseServer server = new DatabaseServerImpl(engine);
        scheduler = new SchedulerImpl(server, THREAD_POOL_SIZE);
    }

    @Override
    public void initialize(AppSettings settings,
            StartupProgressObserver observer) throws AppConfigurationException {
        if (observer != null) {
            observer.setProgress(PROGRESS_MESSAGE);
        }
        log.info(STL30001_STARTING_DATABASE_ENGINE.getDescription(
                engine.getEngineName(), engine.getEngineVersion()));
        try {
            engine.start();
            AppInfo appInfo = engine.getAppInfo();
            if (appInfo == null || appInfo.getAppSchemaLevel() < settings
                    .getAppSchemaLevel()) {
                updateSchema(settings, appInfo);
            } else {
                if (!appInfo.getAppBuildId().equals(settings.getAppBuildId())
                        || !appInfo.getAppBuildDate()
                                .equals(settings.getAppBuildDate())) {
                    saveAppInfo(appInfo, settings);
                }
            }
        } catch (DatabaseException e) {
            String errMsg =
                    STL10006_ERROR_STARTING_DATABASE_ENGINE.getDescription(
                            engine.getEngineName(), engine.getEngineVersion(),
                            StringUtils.getErrorMessage(e));
            log.error(errMsg, e);
            AppConfigurationException ace =
                    new AppConfigurationException(errMsg, e);
            throw ace;
        }
        log.info("Database initialization finished");
    }

    @Override
    public String getComponentDescription() {
        return DATABASEMANAGER_COMPONENT;
    }

    @Override
    public int getInitializationWeight() {
        return 80;
    }

    @Override
    public void shutdown(StartupProgressObserver observer) {
        if (observer != null) {
            observer.setProgress(SHUTDOWN_MESSAGE);
        }
        try {
            if (scheduler != null) {
                scheduler.shutdown();
            }
        } finally {
            if (engine != null) {
                if (observer != null && compactNeeded) {
                    observer.setProgress(
                            STL10028_COMPACTING_DATABASE.getDescription());
                }
                try {
                    engine.stop(compactNeeded);
                } catch (DatabaseException e) {
                    log.error("Error during DatabaseEngine stop: ", e);
                }
            }
        }
    }

    @Override
    public AppInfo getAppInfo() {
        return engine.getAppInfo();
    }

    @Override
    public void saveAppProperties(Map<String, Properties> appProperties) {
        AppInfo appInfo = engine.getAppInfo();
        appInfo.setPropertiesMap(appProperties);
        engine.saveAppInfo(appInfo);
    }

    @Override
    public List<SubnetDescription> getSubnets() {
        DatabaseCall<List<SubnetDescription>> call =
                new DatabaseCallImpl<List<SubnetDescription>>() {

                    @Override
                    public List<SubnetDescription> execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getSubnets();
                    }
                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public SubnetDescription getSubnet(final String subnetName) {
        DatabaseCall<SubnetDescription> call =
                new DatabaseCallImpl<SubnetDescription>() {

                    @Override
                    public SubnetDescription execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        SubnetRecord record = subnetDao.getSubnet(subnetName);
                        if (record == null) {
                            return null;
                        }
                        return record.getSubnetDescription();
                    }
                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public SubnetDescription getSubnet(final long subnetId) {
        DatabaseCall<SubnetDescription> call =
                new DatabaseCallImpl<SubnetDescription>() {

                    @Override
                    public SubnetDescription execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        SubnetRecord record = subnetDao.getSubnet(subnetId);
                        if (record == null
                                || !record.getUniqueName().startsWith("1")) {
                            return null;
                        }
                        return record.getSubnetDescription();
                    }
                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public SubnetDescription defineSubnet(final SubnetDescription subnet) {
        DatabaseCall<SubnetDescription> call =
                new DatabaseCallImpl<SubnetDescription>() {

                    @Override
                    public SubnetDescription execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.defineSubnet(subnet);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public void updateSubnet(final SubnetDescription subnet)
            throws SubnetDataNotFoundException {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                SubnetDAO subnetDao = ctx.getSubnetDAO();
                subnetDao.updateSubnet(subnet);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public void removeSubnet(final long subnetId)
            throws SubnetDataNotFoundException {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                SubnetDAO subnetDao = ctx.getSubnetDAO();
                subnetDao.removeSubnet(subnetId);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public List<NodeRecordBean> getNodes(final String subnetName)
            throws SubnetDataNotFoundException {
        DatabaseCall<List<NodeRecordBean>> call =
                new DatabaseCallImpl<List<NodeRecordBean>>() {

                    @Override
                    public List<NodeRecordBean> execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getNodes(subnetName);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public NodeRecordBean getNode(final String subnetName, final long nodeGUID)
            throws SubnetDataNotFoundException {
        DatabaseCall<NodeRecordBean> call =
                new DatabaseCallImpl<NodeRecordBean>() {

                    @Override
                    public NodeRecordBean execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getNode(subnetName, nodeGUID);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public NodeRecordBean getNode(final String subnetName, final int lid)
            throws SubnetDataNotFoundException {
        DatabaseCall<NodeRecordBean> call =
                new DatabaseCallImpl<NodeRecordBean>() {

                    @Override
                    public NodeRecordBean execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getNode(subnetName, lid);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public NodeRecordBean getNodeByPortGUID(final String subnetName,
            final long portGuid) throws SubnetDataNotFoundException {
        DatabaseCall<NodeRecordBean> call =
                new DatabaseCallImpl<NodeRecordBean>() {

                    @Override
                    public NodeRecordBean execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getNodeByPortGUID(subnetName,
                                portGuid);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public List<LinkRecordBean> getLinks(final String subnetName)
            throws SubnetDataNotFoundException {
        DatabaseCall<List<LinkRecordBean>> call =
                new DatabaseCallImpl<List<LinkRecordBean>>() {

                    @Override
                    public List<LinkRecordBean> execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getLinks(subnetName);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.datamanager.DatabaseManager#getLinks(java.lang.String,
     * int)
     */
    @Override
    public List<LinkRecordBean> getLinks(final String subnetName, final int lid)
            throws SubnetDataNotFoundException {
        DatabaseCall<List<LinkRecordBean>> call =
                new DatabaseCallImpl<List<LinkRecordBean>>() {

                    @Override
                    public List<LinkRecordBean> execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getLinks(subnetName, lid);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public LinkRecordBean getLinkBySource(final String subnetName,
            final int lid, final short portNum)
                    throws SubnetDataNotFoundException {
        DatabaseCall<LinkRecordBean> call =
                new DatabaseCallImpl<LinkRecordBean>() {

                    @Override
                    public LinkRecordBean execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getLinkBySource(subnetName, lid,
                                portNum);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public LinkRecordBean getLinkByDestination(final String subnetName,
            final int lid, final short portNum)
                    throws SubnetDataNotFoundException {
        DatabaseCall<LinkRecordBean> call =
                new DatabaseCallImpl<LinkRecordBean>() {

                    @Override
                    public LinkRecordBean execute(DatabaseContext ctx)
                            throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        return subnetDao.getLinkByDestination(subnetName, lid,
                                portNum);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public EnumMap<NodeType, Integer> getNodeTypeDist(final String subnetName)
            throws SubnetDataNotFoundException {
        DatabaseCall<EnumMap<NodeType, Integer>> call =
                new DatabaseCallImpl<EnumMap<NodeType, Integer>>() {

                    @Override
                    public EnumMap<NodeType, Integer> execute(
                            DatabaseContext ctx) throws Exception {
                        SubnetDAO subnetDao = ctx.getSubnetDAO();
                        EnumMap<NodeType, Integer> nodeTypeDist =
                                new EnumMap<NodeType, Integer>(NodeType.class);
                        TopologyRecord topology =
                                subnetDao.getTopology(subnetName);
                        nodeTypeDist.put(HFI, (int) topology.getNumCAs());
                        nodeTypeDist.put(ROUTER, topology.getNumRouters());
                        nodeTypeDist.put(SWITCH,
                                (int) topology.getNumSwitches());
                        nodeTypeDist.put(UNKNOWN, topology.getNumUnknown());
                        return nodeTypeDist;
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);

    }

    @Override
    public void saveEventRules(final List<EventRule> rules) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                ConfigurationDAO configDao = ctx.getConfigurationDAO();
                configDao.saveEventRules(rules);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public List<EventRule> getEventRules() {
        DatabaseCall<List<EventRule>> call =
                new DatabaseCallImpl<List<EventRule>>() {

                    @Override
                    public List<EventRule> execute(DatabaseContext ctx)
                            throws Exception {
                        ConfigurationDAO configDao = ctx.getConfigurationDAO();
                        return configDao.getEventRules();
                    }

                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public void saveTopology(final String subnetName,
            final List<NodeRecordBean> nodes, final List<LinkRecordBean> links)
                    throws SubnetDataNotFoundException {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                SubnetDAO subnetDao = ctx.getSubnetDAO();
                subnetDao.saveTopology(subnetName, nodes, links);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public long getTopologyId(final String subnetName)
            throws SubnetDataNotFoundException {
        DatabaseCall<Long> call = new DatabaseCallImpl<Long>() {

            @Override
            public Long execute(DatabaseContext ctx) throws Exception {
                SubnetDAO subnetDao = ctx.getSubnetDAO();
                return subnetDao.getTopology(subnetName).getId();
            }

        };
        scheduler.enqueue(call);
        return call.getResult(SubnetDataNotFoundException.class);
    }

    @Override
    public UserSettings getUserSettings(final String subnetName,
            final String userName) throws UserNotFoundException {
        DatabaseCall<UserSettings> call = new DatabaseCallImpl<UserSettings>() {

            @Override
            public UserSettings execute(DatabaseContext ctx) throws Exception {
                ConfigurationDAO configDao = ctx.getConfigurationDAO();
                SubnetDAO subnetDao = ctx.getSubnetDAO();
                SubnetRecord subnet = subnetDao.getSubnet(subnetName);
                return configDao.getUserSettings(subnet, userName);
            }

        };
        scheduler.enqueue(call);
        return call.getResult(UserNotFoundException.class);
    }

    @Override
    public void saveUserSettings(final String subnetName,
            final UserSettings userSettings) {
        DatabaseCall<DatabaseRecord> call =
                new DatabaseCallImpl<DatabaseRecord>() {

                    @Override
                    public DatabaseRecord execute(DatabaseContext ctx)
                            throws Exception {
                        ConfigurationDAO configDao = ctx.getConfigurationDAO();
                        configDao.saveUserSettings(ctx.getSubnet(subnetName),
                                userSettings);
                        return null;
                    }

                };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public void saveGroupConfig(final String subnetName, final String groupName,
            final List<PortConfigBean> ports) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                GroupDAO groupDao = ctx.getGroupDAO();
                groupDao.saveGroupConfig(ctx.getSubnet(subnetName), groupName,
                        ports);

                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public void saveGroupList(final String subnetName,
            final List<GroupListBean> groupList) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                GroupDAO groupDao = ctx.getGroupDAO();
                groupDao.saveGroupList(ctx.getSubnet(subnetName), groupList);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.datamanager.DatabaseManager#getGroupConfig(com.intel.stl
     * .datamanager.GroupConfigId)
     */
    @Override
    public List<GroupConfigRspBean> getGroupConfig(final String subnetName,
            final String groupName) throws PerformanceDataNotFoundException {
        DatabaseCall<List<GroupConfigRspBean>> call =
                new DatabaseCallImpl<List<GroupConfigRspBean>>() {

                    @Override
                    public List<GroupConfigRspBean> execute(DatabaseContext ctx)
                            throws Exception {
                        GroupDAO groupDao = ctx.getGroupDAO();
                        SubnetRecord subnet = ctx.getSubnet(subnetName);

                        GroupConfigId configId = new GroupConfigId();
                        configId.setFabricId(subnet.getId());
                        configId.setSubnetGroup(groupName);

                        List<PortConfigBean> ports =
                                groupDao.getGroupConfig(configId);
                        List<GroupConfigRspBean> res =
                                new ArrayList<GroupConfigRspBean>(ports.size());
                        for (PortConfigBean port : ports) {
                            GroupConfigRspBean gcRspBean =
                                    new GroupConfigRspBean();
                            gcRspBean.setPort(port);
                            res.add(gcRspBean);
                        }
                        return res;
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(PerformanceDataNotFoundException.class);
    }

    @Override
    public List<PortConfigBean> getPortConfig(final String subnetName)
            throws PerformanceDataNotFoundException {
        DatabaseCall<List<PortConfigBean>> call =
                new DatabaseCallImpl<List<PortConfigBean>>() {
                    @Override
                    public List<PortConfigBean> execute(DatabaseContext ctx)
                            throws Exception {
                        GroupDAO groupDao = ctx.getGroupDAO();
                        return groupDao
                                .getPortConfig(ctx.getSubnet(subnetName));
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(PerformanceDataNotFoundException.class);
    }

    @Override
    public void saveGroupInfos(final String subnetName,
            final List<GroupInfoBean> groupInfoBeans) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                GroupDAO groupDao = ctx.getGroupDAO();
                groupDao.saveGroupInfos(ctx.getSubnet(subnetName),
                        groupInfoBeans);

                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public int purgeGroupInfos(final String subnetName, final long ago) {
        DatabaseCall<Integer> call = new DatabaseCallImpl<Integer>() {

            @Override
            public Integer execute(DatabaseContext ctx) throws Exception {
                GroupDAO groupDao = ctx.getGroupDAO();
                int deleted = groupDao
                        .purgeGroupInfos(ctx.getSubnet(subnetName), ago);

                return deleted;
            }

        };
        scheduler.enqueue(call);
        int count = call.getResult();
        this.compactNeeded = true;
        return count;
    }

    @Override
    public void saveImageInfos(final String subnetName,
            final List<ImageInfoBean> imageInfoBeans) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                PerformanceDAO performanceDao = ctx.getPerformanceDAO();
                performanceDao.saveImageInfos(ctx.getSubnet(subnetName),
                        imageInfoBeans);

                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.datamanager.DatabaseManager#getGroupInfo(com.intel.stl.
     * datamanager.GroupInfoId)
     */
    @Override
    public List<GroupInfoBean> getGroupInfo(final String subnetName,
            final String groupName, final long startTime, final long stopTime)
                    throws PerformanceDataNotFoundException {
        DatabaseCall<List<GroupInfoBean>> call =
                new DatabaseCallImpl<List<GroupInfoBean>>() {

                    @Override
                    public List<GroupInfoBean> execute(DatabaseContext ctx)
                            throws Exception {
                        GroupDAO groupDao = ctx.getGroupDAO();
                        return groupDao.getGroupInfoList(
                                ctx.getSubnet(subnetName), groupName, startTime,
                                stopTime);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult(PerformanceDataNotFoundException.class);
    }

    @Override
    public void saveNotices(final String subnetName,
            final NoticeBean[] notices) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                NoticeDAO noticeDao = ctx.getNoticeDAO();
                noticeDao.saveNotices(ctx.getSubnet(subnetName), notices);

                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public Future<Boolean> processNotices(final String subnetName,
            final List<NoticeProcess> notices) {
        DatabaseCall<Boolean> call = new DatabaseCallImpl<Boolean>() {

            @Override
            public Boolean execute(DatabaseContext ctx) throws Exception {
                NoticeDAO noticeDao = ctx.getNoticeDAO();
                return noticeDao.processNotices(ctx.getSubnet(subnetName),
                        notices);
            }

        };
        return scheduler.enqueue(call);
    }

    @Override
    public List<NoticeBean> getNotices(final String subnetName,
            final NoticeStatus status) {
        DatabaseCall<List<NoticeBean>> call =
                new DatabaseCallImpl<List<NoticeBean>>() {

                    @Override
                    public List<NoticeBean> execute(DatabaseContext ctx)
                            throws Exception {
                        NoticeDAO noticeDao = ctx.getNoticeDAO();
                        return noticeDao.getNotices(ctx.getSubnet(subnetName),
                                status);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public List<NoticeBean> getNotices(final String subnetName,
            final NoticeStatus status, final NoticeStatus newStatus) {
        DatabaseCall<List<NoticeBean>> call =
                new DatabaseCallImpl<List<NoticeBean>>() {

                    @Override
                    public List<NoticeBean> execute(DatabaseContext ctx)
                            throws Exception {
                        NoticeDAO noticeDao = ctx.getNoticeDAO();
                        return noticeDao.getNotices(ctx.getSubnet(subnetName),
                                status, newStatus);
                    }

                };
        scheduler.enqueue(call);
        return call.getResult();
    }

    @Override
    public void resetNotices(final String subnetName) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                NoticeDAO noticeDao = ctx.getNoticeDAO();
                noticeDao.resetNotices(ctx.getSubnet(subnetName));
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    @Override
    public void updateNotice(final String subnetName, final long noticeId,
            final NoticeStatus noticeStatus) {
        DatabaseCall<Void> call = new DatabaseCallImpl<Void>() {

            @Override
            public Void execute(DatabaseContext ctx) throws Exception {
                NoticeDAO noticeDao = ctx.getNoticeDAO();
                noticeDao.updateNotice(ctx.getSubnet(subnetName), noticeId,
                        noticeStatus);
                return null;
            }

        };
        scheduler.enqueue(call);
        call.getResult();
    }

    private void updateSchema(AppSettings settings, AppInfo appInfo)
            throws AppConfigurationException {
        int schemaLevel = settings.getAppSchemaLevel();
        log.info(STL30010_STARTING_SCHEMA_UPDATE.getDescription(schemaLevel));
        if (appInfo == null) {
            appInfo = new AppInfo();
        }

        List<SubnetRecord> subnets = null;
        List<UserRecord> users = null;
        try {
            EntityManager em = engine.getEntityManager();
            subnets = DatabaseMigrationHelper.getSubnetRecords(em);
            users = DatabaseMigrationHelper.getUserRecords(em);
            em.clear();
            em.close();
        } catch (Exception e) {
            log.error("Could not retrieve previous database definitions: "
                    + StringUtils.getErrorMessage(e));
        }

        try {
            engine.updateSchema();
            DatabaseUtils.populateRequiredTables(engine.getEntityManager());
            saveAppInfo(appInfo, settings);
        } catch (AppConfigurationException ace) {
            throw ace;
        } catch (Exception e) {
            String msg =
                    STL30011_ERROR_UPDATING_SCHEMA.getDescription(schemaLevel);
            log.error(msg, e);
            AppConfigurationException ace =
                    new AppConfigurationException(msg, e);
            throw ace;
        }
        try {
            EntityManager em = engine.getEntityManager();
            if (subnets != null) {
                for (SubnetRecord subnet : subnets) {
                    long id = subnet.getId();
                    subnet.setId(0L);
                    subnet.setTopology(null);
                    SubnetRecord newRec = DatabaseMigrationHelper
                            .insertSubnetRecord(em, subnet);
                    if (users != null) {
                        for (UserRecord user : users) {
                            if (user.getId().getFabricId() == id) {
                                user.getId().setFabricId(newRec.getId());
                                DatabaseMigrationHelper.insertUserRecord(em,
                                        user);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("Could not save previous database definitions: "
                    + StringUtils.getErrorMessage(e), e);
        }
        log.info("Schema updated.");
    }

    private void saveAppInfo(AppInfo appInfo, AppSettings settings)
            throws AppConfigurationException {
        log.info("Save AppSettings: " + settings.getAppName() + " "
                + settings.getAppVersion() + " " + settings.getAppBuildId()
                + " " + settings.getAppBuildDate());
        appInfo.setAppName(settings.getAppName());
        appInfo.setAppVersion(settings.getAppVersion());
        appInfo.setAppRelease(settings.getAppRelease());
        appInfo.setAppModLevel(settings.getAppModLevel());
        appInfo.setOpaFmVersion(settings.getOpaFmVersion());
        appInfo.setAppBuildId(settings.getAppBuildId());
        appInfo.setAppBuildDate(settings.getAppBuildDate());
        appInfo.setAppSchemaLevel(settings.getAppSchemaLevel());
        engine.saveAppInfo(appInfo);
    }
}
