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

package com.intel.stl.api.configuration;

import java.util.List;

import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.notice.IEmailEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;

/**
 */
public interface IConfigurationApi {
    boolean tryToConnect(SubnetDescription subnet)
            throws SubnetConnectionException;

    void registerCertsAssistant(ICertsAssistant providor);

    AppInfo getAppInfo();

    List<SubnetDescription> getSubnets();

    SubnetDescription defineSubnet(SubnetDescription subnet);

    void updateSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException;;

    void removeSubnet(long subnetId) throws SubnetDataNotFoundException;

    SubnetDescription getSubnet(String subnetName)
            throws SubnetDataNotFoundException;

    SubnetDescription getSubnet(long subnetId);

    LoggingConfiguration getLoggingConfig() throws ConfigurationException;

    void saveLoggingConfiguration(LoggingConfiguration loggingConfig)
            throws ConfigurationException;

    List<EventRule> getEventRules() throws EventNotFoundException;

    void saveEventRules(List<EventRule> rules) throws ConfigurationException;

    String getLogPropertyPath();

    UserSettings getUserSettings(String subnetName, String userName)
            throws UserNotFoundException;

    void saveUserSettings(String subnetName, UserSettings settings);

    void startSimulatedFailover(String subnetName);

    void cleanup();

    PMConfigBean getPMConfig(SubnetDescription subnet);

    String getHostIp(String hostName) throws SubnetConnectionException;

    boolean isHostReachable(String hostName);

    boolean isHostConnectable(SubnetDescription subnet)
            throws ConfigurationException;

    /**
     *
     * <i>Description:</i> Save application-level settings to the database.
     *
     * @param appInfo
     */
    void saveAppInfo(AppInfo appInfo);

    /**
     *
     * <i>Description:</i> Change SMTP server properties. This will cause the
     * mail sender to create a new mail transport.
     *
     * @param properties
     */
    void updateMailProperties(MailProperties properties);

    /**
     *
     * <i>Description:</i> Get SMTP server properties.
     *
     * @return MailProperties
     */
    MailProperties getMailProperties();

    /**
     *
     * <i>Description:</i> Send a mail message with the specified message
     * subject and body to the list of recipients.
     *
     * @param subject
     * @param body
     * @param recipients
     */
    void submitMessage(String subject, String body, List<String> recipients);

    /**
     *
     * <i>Description:</i> Send a test mail message with the specified message
     * subject and body to the recipient. A temporary mail transport is created
     * with the parameters specified in properties.
     *
     * @param properties
     * @param recipient
     * @param messageSubject
     * @param meaasgeBody
     */
    void sendTestMail(MailProperties properties, String recipient,
            String messageSubject, String messageBody);

    /**
     *
     * <i>Description:</i> Register listener for email events.
     *
     * @param listener
     */
    public void addEmailEventListener(IEmailEventListener<NoticeBean> listener);

    /**
     *
     * <i>Description:</i> De-register listener for email events.
     *
     * @param listener
     */
    public void removeEmailListener(IEmailEventListener<NoticeBean> listener);

    /**
     *
     * <i>Description:</i> Check if SMTP settings are present and valid
     *
     */
    public boolean isSmtpSettingsValid();

    boolean isEmailValid(String email);
}
