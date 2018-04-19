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

package com.intel.stl.api.management.impl;

import java.util.List;
import java.util.Set;

import com.intel.stl.api.management.FMConfHelper;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.management.TmpFmConfHelper;
import com.intel.stl.api.management.applications.Application;
import com.intel.stl.api.management.applications.ApplicationException;
import com.intel.stl.api.management.applications.impl.ApplicationManagement;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.devicegroups.DeviceGroupException;
import com.intel.stl.api.management.devicegroups.impl.DeviceGroupManagement;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.api.management.virtualfabrics.VirtualFabricException;
import com.intel.stl.api.management.virtualfabrics.impl.VirtualFabricManagement;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.fecdriver.network.ssh.SshKeyType;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSession;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSessionFactory;

/**
 * This class provides access to FM configuration management functions. It
 * intend to be stateless. It's the caller's responsibility to do the
 * synchronizations etc based on business logic. This class also provide safe
 * check to ensure the data to be change is valid.
 */
public class ManagementApi implements IManagementApi {
    private final FMConfHelper confHelper;

    private final ApplicationManagement appMgt;

    private final DeviceGroupManagement groupMgt;

    private final VirtualFabricManagement vfMgt;

    private final SubnetDescription subnet;

    public ManagementApi(SubnetDescription subnet) {
        confHelper = FMConfHelper.getInstance(subnet);
        appMgt = new ApplicationManagement(confHelper);
        groupMgt = new DeviceGroupManagement(confHelper);
        vfMgt = new VirtualFabricManagement(confHelper);
        this.subnet = subnet;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#reset()
     */
    @Override
    public void reset() {
        confHelper.reset();
        appMgt.reset();
        groupMgt.reset();
        vfMgt.reset();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#hasChanges()
     */
    @Override
    public boolean hasChanges() {
        return appMgt.hasChanges() || groupMgt.hasChanges()
                || vfMgt.hasChanges();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#deplaoy(boolean)
     */
    @Override
    public void deploy(char[] password, boolean restart) throws Exception {
        confHelper.deployConf(password);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#deployTo(char[],
     * com.intel.stl.api.subnet.HostInfo)
     */
    @Override
    public void deployTo(char[] password, HostInfo target) throws Exception {
        FMConfHelper tmpConfHelper = new TmpFmConfHelper(target);
        tmpConfHelper.fetchConfigFile(password);
        ApplicationManagement tmpAppMgt =
                new ApplicationManagement(tmpConfHelper);
        appMgt.applyChangesTo(tmpAppMgt);
        DeviceGroupManagement tmpGroupMgt =
                new DeviceGroupManagement(tmpConfHelper);
        groupMgt.applyChangesTo(tmpGroupMgt);
        VirtualFabricManagement tmpVfMgt =
                new VirtualFabricManagement(tmpConfHelper);
        vfMgt.applyChangesTo(tmpVfMgt);
        tmpConfHelper.deployConf(password);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.applications.IApplicationManangement#
     * getReservedApplications()
     */
    @Override
    public Set<String> getReservedApplications() {
        return appMgt.getReservedApplications();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * getApplications()
     */
    @Override
    public List<Application> getApplications() throws ApplicationException {
        return appMgt.getApplications();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * getApplication(java.lang.String)
     */
    @Override
    public Application getApplication(String name) throws ApplicationException {
        return appMgt.getApplication(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * addApplication(com.intel.stl.api.management.application.Application)
     */
    @Override
    public void addApplication(Application app) throws ApplicationException {
        appMgt.addApplication(app);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * removeApplication(java.lang.String)
     */
    @Override
    public void removeApplication(String appName) throws ApplicationException {
        appMgt.removeApplication(appName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * updateApplication(com.intel.stl.api.management.application.Application)
     */
    @Override
    public void updateApplication(String oldName, Application app)
            throws ApplicationException {
        appMgt.updateApplication(oldName, app);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.application.IApplicationManangement#
     * addOrUpdateApplication
     * (com.intel.stl.api.management.application.Application)
     */
    @Override
    public void addOrUpdateApplication(String oldName, Application app)
            throws ApplicationException {
        appMgt.addOrUpdateApplication(oldName, app);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * getReservedDeviceGroups()
     */
    @Override
    public Set<String> getReservedDeviceGroups() {
        return groupMgt.getReservedDeviceGroups();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * getDeviceGroups()
     */
    @Override
    public List<DeviceGroup> getDeviceGroups() throws DeviceGroupException {
        return groupMgt.getDeviceGroups();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * getDeviceGroup(java.lang.String)
     */
    @Override
    public DeviceGroup getDeviceGroup(String name) throws DeviceGroupException {
        return groupMgt.getDeviceGroup(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * addDeviceGroup(com.intel.stl.api.management.devicegroups.DeviceGroup)
     */
    @Override
    public void addDeviceGroup(DeviceGroup group) throws DeviceGroupException {
        groupMgt.addDeviceGroup(group);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * removeDeviceGroup(java.lang.String)
     */
    @Override
    public void removeDeviceGroup(String name) throws DeviceGroupException {
        groupMgt.removeDeviceGroup(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * updateDeviceGroup(java.lang.String,
     * com.intel.stl.api.management.devicegroups.DeviceGroup)
     */
    @Override
    public void updateDeviceGroup(String oldName, DeviceGroup group)
            throws DeviceGroupException {
        groupMgt.updateDeviceGroup(oldName, group);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.devicegroups.IDeviceGroupManagement#
     * addOrUpdateDeviceGroup(java.lang.String,
     * com.intel.stl.api.management.devicegroups.DeviceGroup)
     */
    @Override
    public void addOrUpdateDeviceGroup(String oldName, DeviceGroup group)
            throws DeviceGroupException {
        groupMgt.addOrUpdateDeviceGroup(oldName, group);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getReservedVirtualFabrics()
     */
    @Override
    public Set<String> getReservedVirtualFabrics() {
        return vfMgt.getReservedVirtualFabrics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabrics()
     */
    @Override
    public List<VirtualFabric> getVirtualFabrics()
            throws VirtualFabricException {
        return vfMgt.getVirtualFabrics();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * getVirtualFabric(java.lang.String)
     */
    @Override
    public VirtualFabric getVirtualFabric(String name)
            throws VirtualFabricException {
        return vfMgt.getVirtualFabric(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addVirtualFabric
     * (com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addVirtualFabric(VirtualFabric vf)
            throws VirtualFabricException {
        vfMgt.addVirtualFabric(vf);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * removeVirtualFabric(java.lang.String)
     */
    @Override
    public void removeVirtualFabric(String name) throws VirtualFabricException {
        vfMgt.removeVirtualFabric(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * updateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void updateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        vfMgt.updateVirtualFabric(oldName, vf);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement#
     * addOrUpdateVirtualFabric(java.lang.String,
     * com.intel.stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void addOrUpdateVirtualFabric(String oldName, VirtualFabric vf)
            throws VirtualFabricException {
        vfMgt.addOrUpdateVirtualFabric(oldName, vf);
    }

    @Override
    public SubnetDescription getSubnetDescription() {
        return subnet;
    }

    @Override
    public boolean isConfigReady() {
        return confHelper.checkConfigFilePresense();
    }

    @Override
    public boolean hasSession() {
        boolean connectionStatus = false;

        // Check if the factory has a session for this subnet
        // and if it does, verify that it is connected
        JSchSession session = JSchSessionFactory.getSessionFromMap(
                SshKeyType.MANAGEMENT_KEY.getKey(subnet.getSubnetId()));

        if (session != null) {
            connectionStatus = session.isConnected();
        }

        return connectionStatus;
    }

    @Override
    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#tryToConnect(char[])
     *
     * This method will try to connect to the server and retrieve the config
     * file To check if connection is successful, call
     * doWeHaveConfigFileInHelper()
     */
    public void fetchConfigFile(char[] password) throws Exception {
        confHelper.fetchConfigFile(password);
    }

    @Override
    public void onCancelFetchConfig(SubnetDescription subnet) {
        // Terminate any open or in-progress ssh/ftp/sftp connections
        confHelper.cancelFetchConfigFile(subnet);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.management.IManagementApi#cleanup()
     */
    @Override
    public void cleanup() {
        subnet.getCurrentFE().setHostType(null);
        JSchSessionFactory.closeSession(
                SshKeyType.MANAGEMENT_KEY.getKey(subnet.getSubnetId()));
        confHelper.reset();
    }
}
