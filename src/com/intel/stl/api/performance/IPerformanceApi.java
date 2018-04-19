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

package com.intel.stl.api.performance;

import java.util.List;

import com.intel.stl.api.IRandomable;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.api.subnet.SubnetDescription;

/**
 */
public interface IPerformanceApi extends IRandomable {
    SubnetDescription getConnectionDescription();

    ImageInfoBean getImageInfo(long imageNumber, int imageOffset);

    ImageInfoBean getLatestImageInfo();

    List<GroupListBean> getGroupList();

    GroupInfoBean getGroupInfo(String name);

    GroupInfoBean getGroupInfoHistory(String name, long imageID, int imageOffset);

    List<GroupConfigRspBean> getGroupConfig(String name);

    List<FocusPortsRspBean> getFocusPorts(String groupName,
            Selection selection, int n);

    List<FocusPortsRspBean> getTopBandwidth(String groupName, int n);

    List<FocusPortsRspBean> getTopCongestion(String groupName, int n);

    List<String> getDeviceGroup(int lid);

    PortCountersBean getPortCounters(int lid, short portNum);

    PortCountersBean getPortCountersHistory(int lid, short portNum,
            long imageID, int imageOffset);

    List<VFListBean> getVFList();

    VFInfoBean getVFInfo(String name);

    VFInfoBean getVFInfoHistory(String name, long imageID, int imageOffset);

    List<VFConfigRspBean> getVFConfig(String name);

    List<String> getVFNames(int lid);

    List<VFFocusPortsRspBean> getVFFocusPorts(String vfName,
            Selection selection, int n);

    VFPortCountersBean getVFPortCounters(String vfName, int lid, short portNum);

    VFPortCountersBean getVFPortCountersHistory(String vfName, int lid,
            short portNum, long imageID, int imageOffset);

    void saveGroupConfig(String groupName, List<PortConfigBean> ports);

    List<GroupConfigRspBean> getGroupConfig(String subnetName, String groupName)
            throws PerformanceDataNotFoundException;

    List<PortConfigBean> getPortConfig(String subnetName)
            throws PerformanceDataNotFoundException;

    List<GroupInfoBean> getGroupInfo(String subnetName, String groupName,
            long startTime, long stopTime)
            throws PerformanceDataNotFoundException;

    PMConfigBean getPMConfig();

    void reset();

    void cleanup();
}
