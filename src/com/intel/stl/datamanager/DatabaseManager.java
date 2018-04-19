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
package com.intel.stl.datamanager;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Future;

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

public interface DatabaseManager {

    AppInfo getAppInfo();

    void saveAppProperties(Map<String, Properties> appProperties);

    List<SubnetDescription> getSubnets();

    SubnetDescription getSubnet(String subnetName);

    SubnetDescription getSubnet(long subnetId);

    SubnetDescription defineSubnet(SubnetDescription subnet);

    void updateSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException;

    void removeSubnet(long subnetId) throws SubnetDataNotFoundException;

    void saveEventRules(List<EventRule> rules);

    List<EventRule> getEventRules();

    List<NodeRecordBean> getNodes(String subnetName)
            throws SubnetDataNotFoundException;

    List<LinkRecordBean> getLinks(String subnetName)
            throws SubnetDataNotFoundException;

    List<LinkRecordBean> getLinks(String subnetName, int lid)
            throws SubnetDataNotFoundException;

    NodeRecordBean getNode(String subnetName, int lid)
            throws SubnetDataNotFoundException;

    NodeRecordBean getNode(String subnetName, long nodeGUID)
            throws SubnetDataNotFoundException;

    /**
     * Description: returns a node by its port GUID
     *
     * @param subnetName
     * @param portGuid
     * @return
     */
    NodeRecordBean getNodeByPortGUID(String subnetName, long portGuid)
            throws SubnetDataNotFoundException;

    void saveTopology(String subnetName, List<NodeRecordBean> nodes,
            List<LinkRecordBean> links) throws SubnetDataNotFoundException;

    long getTopologyId(String subnetName) throws SubnetDataNotFoundException;

    /**
     * Description: returns the node type distribution for the subnet
     *
     * @param subnetName
     * @return enumeration map of NodeType
     */
    EnumMap<NodeType, Integer> getNodeTypeDist(String subnetName)
            throws SubnetDataNotFoundException;

    LinkRecordBean getLinkBySource(String subnetName, int lid, short portNum)
            throws SubnetDataNotFoundException;

    LinkRecordBean getLinkByDestination(String subnetName, int lid,
            short portNum) throws SubnetDataNotFoundException;

    UserSettings getUserSettings(String subnetName, String userName)
            throws UserNotFoundException;

    void saveUserSettings(String subnetName, UserSettings userSettings);

    void saveGroupInfos(String subnetName, List<GroupInfoBean> groupInfos);

    void saveImageInfos(String subnetName, List<ImageInfoBean> imageInfos);

    int purgeGroupInfos(String subnetName, long ago);

    void saveGroupConfig(String subnetName, String groupName,
            List<PortConfigBean> ports);

    void saveGroupList(String subnetName, List<GroupListBean> groupList);

    List<GroupInfoBean> getGroupInfo(String subnetName, String groupName,
            long startTime, long stopTime)
                    throws PerformanceDataNotFoundException;

    List<GroupConfigRspBean> getGroupConfig(String subnetName, String groupName)
            throws PerformanceDataNotFoundException;

    List<PortConfigBean> getPortConfig(String subnetName)
            throws PerformanceDataNotFoundException;

    void saveNotices(String subnetName, NoticeBean[] notices);

    Future<Boolean> processNotices(String subnetName,
            List<NoticeProcess> notices);

    void resetNotices(String subnetName);

    void updateNotice(String subnetName, long noticeId,
            NoticeStatus noticeStatus);

    List<NoticeBean> getNotices(String subnetName, NoticeStatus status);

    List<NoticeBean> getNotices(String subnetName, NoticeStatus status,
            NoticeStatus newStatus);

}
