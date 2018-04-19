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

package com.intel.stl.ui.main;

import java.util.List;

import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.main.view.SplashScreen;
import com.intel.stl.ui.publisher.TaskScheduler;

public interface ISubnetManager {

    boolean isFirstRun();

    List<SubnetDescription> getSubnets();

    SubnetDescription getNewSubnet();

    UserSettings getUserSettings(String subnetName, String userName)
            throws UserNotFoundException;

    PMConfigBean getPMConfig(SubnetDescription subnet);

    TaskScheduler getTaskScheduler(SubnetDescription subnet);

    SubnetDescription saveSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException;

    void removeSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException;

    void saveUserSettings(String subnetName, UserSettings userSettings);

    boolean tryToConnect(SubnetDescription subnet)
            throws SubnetConnectionException;

    void startSubnet(String subnetName) throws SubnetConnectionException;

    void stopSubnet(String subnetName, boolean forceWindowClose);

    void selectSubnet(String subnetName) throws SubnetConnectionException;

    void showSetupWizard(String subnetName, IFabricController controller);

    void init(boolean isFirstRun);

    void startSubnets(SplashScreen splashScreen);

    void cleanup();

    void saveLoggingConfiguration(LoggingConfiguration loggingConfig);

    LoggingConfiguration getLoggingConfig();

    public String getHostIp(String hostName) throws SubnetConnectionException;

    public boolean isHostReachable(String hostName);

    public boolean isHostConnectable(SubnetDescription subnet)
            throws ConfigurationException;

    public void clearSubnetFactories(SubnetDescription subnet);

    public IConfigurationApi getConfigurationApi();

    /**
     * <i>Description:</i>
     *
     * @param subnetName
     * @return
     */
    SubnetDescription getSubnet(String subnetName);

    /**
     * <i>Description: Send test email to addresses listed in recipients.</i>
     *
     * @param recipients
     */
    void onEmailTest(String recipients);

    /**
     * <i>Description: Get Context object.</i>
     *
     * @param subnetDescription
     * @return
     */
    public Context getContext(SubnetDescription subnetDescription);

    /**
     * <i>Description:</i> cancels the failover process for the specified
     * subnet. NOTE: we need this support in the SubnetManager because the
     * Context might not be initialized yet in FabricController when a failover
     * occurs.
     *
     * @param subnetName
     */
    void cancelFailoverFor(String subnetName);

    boolean isEmailValid(String email);

}
