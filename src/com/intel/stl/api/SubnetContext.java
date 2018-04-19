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

package com.intel.stl.api;

import java.beans.PropertyChangeListener;

import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.logs.ILogApi;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.notice.INoticeApi;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDescription;

public interface SubnetContext {

    /**
     *
     * <i>Description:</i> returns the Configuration API
     *
     * @return {@link com.intel.stl.api.configuration.IConfigurationApi}
     */
    IConfigurationApi getConfigurationApi();

    /**
     *
     * <i>Description:</i> returns the Subnet API
     *
     * @return {@link com.intel.stl.api.subnet.ISubnetApi}
     */
    ISubnetApi getSubnetApi();

    /**
     *
     * <i>Description:</i> returns the Performance API
     *
     * @return {@link com.intel.stl.api.performance.IPerformanceApi}
     */
    IPerformanceApi getPerformanceApi();

    /**
     *
     * <i>Description:</i> returns the Notice API
     *
     * @return {@link com.intel.stl.api.notice.INoticeApi}
     */
    INoticeApi getNoticeApi();

    /**
     *
     * <i>Description:</i> returns the Management API
     *
     * @return {@link com.intel.stl.api.management.IManagementApi}
     */
    IManagementApi getManagementApi();

    /**
     *
     * <i>Description: returns the Log API</i>
     *
     * @return {@link com.intel.stl.api.logs.ILogApi}
     */
    public ILogApi getLogApi();

    /**
     *
     * <i>Description:</i> returns the subnet description for this context
     *
     * @return {@link com.intel.stl.api.subnet.SubnetDescription}
     */
    SubnetDescription getSubnetDescription();

    /**
     *
     * <i>Description:</i> returns the user settings for the specified user name
     *
     * @param userName
     * @return {@link com.intel.stl.api.configuration.UserSettings}
     * @throws UserNotFoundException
     */
    UserSettings getUserSettings(String userName) throws UserNotFoundException;

    /**
     *
     * <i>Description:</i> refreshes the user settings in this SubnetContext to
     * those of the specified user
     *
     * @param userName
     *            the user name
     * @throws UserNotFoundException
     */
    void refreshUserSettings(String userName) throws UserNotFoundException;

    /**
     *
     * <i>Description:</i> gets the specified application setting name for the
     * application, returning the provided default value if not defined.
     * Application settings are defined through the settings.xml file; they are
     * used to fine tune the application.
     *
     * @param settingName
     * @param defaultValue
     *            the default value if the setting has not been defined
     * @return the setting value
     */
    String getAppSetting(String settingName, String defaultValue);

    /**
     *
     * <i>Description:</i> initializes this SubnetContext. This method should be
     * invoked only once.
     *
     * @throws SubnetConnectionException
     */
    void initialize() throws SubnetConnectionException;

    /**
     *
     * <i>Description:</i> starts or stops the notice simulator to simulate
     * fabric activity and showcase the UI.
     *
     * @param random
     *            a boolean; a value of true starts the notice simulator and a
     *            value of false stops it
     */
    void setRandom(boolean random);

    /**
     *
     * <i>Description:</i> adds a subnet event listener interested on subnet
     * events
     *
     * @param listener
     */
    void addSubnetEventListener(ISubnetEventListener listener);

    /**
     *
     * <i>Description:</i> removes the subnet event listener
     *
     * @param listener
     */
    void removeSubnetEventListener(ISubnetEventListener listener);

    /**
     *
     * <i>Description:</i> adds a failover progress listener; this listener
     * receives events related to a failover. A failover occurs when the Subnet
     * Manager and/or its components become unresponsive and the application
     * needs to use one of the secondary managers.
     *
     * @param listener
     */
    void addFailoverProgressListener(PropertyChangeListener listener);

    /**
     *
     * <i>Description:</i> remove a failover progress listener.
     *
     * @param listener
     */
    void removeFailoverProgressListener(PropertyChangeListener listener);

    /**
     *
     * <i>Description:</i> cancels the failover process.
     *
     */
    void cancelFailover();

    /**
     *
     * <i>Description:</i> cleans up this SubnetContext. This usually happens
     * when the UI viewer closes
     *
     */
    void cleanup();

    /**
     *
     * <i>Description:</i> sets the deleted flag
     *
     * @param deleted
     */
    void setDeleted(boolean deleted);

    /**
     *
     * <i>Description:</i> indicates whether this SubnetContext is valid.
     * Typically, communications errors with the FE may render a SubnetContext
     * invalid.
     *
     * @return valid flag
     */
    boolean isValid();

    /**
     *
     * <i>Description:</i> indicates whether the FE session has been closed.
     * This usually happens when the UI viewer for this subnet is closed.
     *
     * @return closed flag
     */
    boolean isClosed();

    /**
     *
     * <i>Description:</i> indicates whether the subnet definition associated
     * with this SubnetContext has been deleted. This happens when the user
     * deletes the definition through the Setup Wizard.
     *
     * @return deleted flag
     */
    boolean isDeleted();

    /**
     *
     * <i>Description:</i> reset to clear all cached data
     *
     */
    void reset();
}
