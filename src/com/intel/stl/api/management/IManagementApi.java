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

package com.intel.stl.api.management;

import com.intel.stl.api.management.applications.IApplicationManagement;
import com.intel.stl.api.management.devicegroups.IDeviceGroupManagement;
import com.intel.stl.api.management.virtualfabrics.IVirtualFabricManagement;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;

public interface IManagementApi extends IApplicationManagement,
        IDeviceGroupManagement, IVirtualFabricManagement {

    /**
     * 
     * <i>Description:</i> reset the ManagementApi so that when we need to get
     * opafm.xml it will try to get a fresh copy from FM. Typical use case is
     * that after a fail over, we reset this ManagementApi.
     * 
     */
    void reset();

    /**
     * 
     * <i>Description:</i> indicate whether there are changes on opafm.xml
     * 
     * @return
     */
    boolean hasChanges();

    /**
     * 
     * <i>Description:</i> deploy local opafm.xml to current SM
     * 
     * @param password
     * @param restart
     *            indicate whether we restart FM after copy file to SMs
     */
    void deploy(char[] password, boolean restart) throws Exception;

    /**
     * 
     * <i>Description:</i> deploy conf changes to a specified SM
     * 
     * @param password
     * @param target
     * @throws Exception
     */
    void deployTo(char[] password, HostInfo target) throws Exception;

    /**
     * 
     * <i>Description:</i> get SubnetDescription
     */
    public SubnetDescription getSubnetDescription();

    /**
     * 
     * <i>Description:</i> returns true if opafm.xml config file from server is
     * present in the FMConfHelper
     */
    public boolean isConfigReady();

    /**
     * 
     * <i>Description:</i> return true if we already have a valid ssh connection
     * with the subnet
     * 
     * @return
     */
    public boolean hasSession();

    /**
     * 
     * <i>Description:</i> Try to ssh to the server and cache opafm.xml in
     * FMConfHelper
     * 
     * @param password
     *            password to use for the ssh connection
     */
    public void fetchConfigFile(char[] password) throws Exception;

    /**
     * <i>Description:</i> call this method on login cancellation by user to
     * terminate ssh/ftp/sftp connection which might be still in progress
     * 
     */
    public void onCancelFetchConfig(SubnetDescription subnet);

    /**
     * 
     * <i>Description: Shut down the session when the subnet is closed </i>
     * 
     */
    public void cleanup();

}
