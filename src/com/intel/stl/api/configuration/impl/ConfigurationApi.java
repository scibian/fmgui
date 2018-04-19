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

package com.intel.stl.api.configuration.impl;

import static com.intel.stl.common.AppDataUtils.FM_GUI_DIR;
import static com.intel.stl.common.STLMessages.STL30022_SUBNET_NOT_FOUND;
import static com.intel.stl.common.STLMessages.STL30038_ERROR_GETTING_SUBNETS;
import static com.intel.stl.common.STLMessages.STL30039_ERROR_GETTING_USER;
import static com.intel.stl.common.STLMessages.STL30040_ERROR_SAVING_USER;
import static com.intel.stl.common.STLMessages.STL30041_ERROR_SAVING_SUBNET;
import static com.intel.stl.common.STLMessages.STL30042_ERROR_GETTING_EVENT;
import static com.intel.stl.common.STLMessages.STL30043_ERROR_SAVING_EVENT;
//import static com.intel.stl.common.STLMessages.STL40005_DATABASE_ERROR_CONFIG;
import static com.intel.stl.common.STLMessages.STL50008_SUBNET_CONNECTION_ERROR;
import static com.intel.stl.common.STLMessages.STL50012_SOCKET_CLOSE_FAILURE;
import static com.intel.stl.configuration.AppSettings.APP_DATA_PATH;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.ClosedSelectorException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.channels.UnresolvedAddressException;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.AppInfo;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.EventNotFoundException;
import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.MailProperties;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.notice.IEmailEventListener;
import com.intel.stl.api.notice.NoticeBean;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.STLMessages;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.configuration.AppSettings;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.IStatement;
import com.intel.stl.fecdriver.adapter.IAdapter;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetPMConfig;
import com.intel.stl.fecdriver.messages.response.FVResponse;
import com.intel.stl.fecdriver.session.ISession;

/**
 */
public class ConfigurationApi implements IConfigurationApi {

    private static Logger log = LoggerFactory.getLogger(ConfigurationApi.class);

    private final int PING_TIMEOUT_MS = 2000;

    private final IAdapter adapter;

    private final DatabaseManager dbMgr;

    private final MailManager mailMgr;

    private final String appDataPath;

    private ICertsAssistant certsAssistant;

    public ConfigurationApi(IAdapter adapter, DatabaseManager dbMgr,
            MailManager mailMgr, AppSettings appSettings)
                    throws AppConfigurationException {
        this.adapter = adapter;
        this.dbMgr = dbMgr;
        this.mailMgr = mailMgr;
        // We check that APP_DATA_PATH is defined since Logging Configuration in
        // the Setup Wizard depends on it.
        this.appDataPath = appSettings.getConfigOption(APP_DATA_PATH);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.configuration.IConfigurationApi#registerCertsProvider
     * (com.intel.stl.api.ICertsProvidor)
     */
    /**
     * We do NOT support dynamic CertsProvidor changing! Only register once!
     */
    @Override
    public void registerCertsAssistant(ICertsAssistant assistant) {
        if (certsAssistant == null) {
            certsAssistant = assistant;
            adapter.registerCertsAssistant(assistant);
        } else {
            throw new IllegalStateException(
                    "Certs Assistant " + certsAssistant + "alreday exists");
        }
    }

    @Override
    public boolean tryToConnect(SubnetDescription subnet)
            throws SubnetConnectionException {
        boolean isConnected = false;
        ISession session = null;
        try {
            session =
                    adapter.createTemporarySession(subnet.getCurrentFE(), null);
            IStatement statement = session.createStatement();
            FVCmdGetPMConfig cmd = new FVCmdGetPMConfig();
            statement.execute(cmd);
            cmd.getResponse().get();
            isConnected = true;
        } catch (Exception e) {
            SubnetConnectionException sce = new SubnetConnectionException(
                    STL50008_SUBNET_CONNECTION_ERROR, e, subnet.getName(),
                    e.getMessage());
            throw sce;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return isConnected;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.configuration.IConfigurationApi#getHostIp(java.lang
     * .String)
     */
    @Override
    public String getHostIp(String hostName) throws SubnetConnectionException {

        InetAddress inet = null;

        try {
            inet = InetAddress.getByName(hostName);
        } catch (Exception e) {
            SubnetConnectionException sce = new SubnetConnectionException(
                    STL50008_SUBNET_CONNECTION_ERROR, e, hostName,
                    StringUtils.getErrorMessage(e));
            throw sce;
        }

        return (inet != null) ? inet.getHostAddress() : null;
    }

    @Override
    public boolean isHostReachable(String hostName) {

        boolean reachable = false;

        try {
            InetAddress inet = InetAddress.getByName(hostName);
            reachable = inet.isReachable(PING_TIMEOUT_MS);

        } catch (UnknownHostException e) {
        } catch (IOException e) {
            // Fail silently
        }

        return reachable;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.configuration.IConfigurationApi#isHostConnectable(com
     * .intel.stl.api.subnet.SubnetDescription)
     */
    @Override
    public boolean isHostConnectable(SubnetDescription subnet)
            throws ConfigurationException {

        Selector selector;
        SocketChannel socketChannel = null;
        String host = subnet.getCurrentFE().getHost();
        int port = subnet.getCurrentFE().getPort();
        boolean connectable = false;

        try {
            // Open the selector
            selector = Selector.open();

            // Create a non-blocking socket channel
            socketChannel = SocketChannel.open();
            socketChannel.configureBlocking(false);

            // Register the connect key
            socketChannel.register(selector, SelectionKey.OP_CONNECT);

            // Attempt to connect
            socketChannel.connect(new InetSocketAddress(host, port));

            // Wait for the channel to be selected
            selector.select();

            // Finish the connect operation
            connectable = socketChannel.finishConnect();

        } catch (IOException e) {
        } catch (ClosedSelectorException e) {
        } catch (UnresolvedAddressException e) {
            ConfigurationException ce = new ConfigurationException(
                    STL50008_SUBNET_CONNECTION_ERROR, e, subnet.getName(),
                    StringUtils.getErrorMessage(e));
            log.error(StringUtils.getErrorMessage(ce), e);
            throw ce;

        } finally {
            try {
                // Close the socket
                if (socketChannel != null) {
                    socketChannel.close();
                }
            } catch (IOException e) {
                ConfigurationException ce = new ConfigurationException(
                        STL50012_SOCKET_CLOSE_FAILURE, e, subnet.getName(),
                        StringUtils.getErrorMessage(e));
                log.error(StringUtils.getErrorMessage(ce), e);
                throw ce;
            }
        }

        return connectable;
    }

    @Override
    public PMConfigBean getPMConfig(SubnetDescription subnet) {
        PMConfigBean config = null;
        ISession session = null;
        try {
            session =
                    adapter.createTemporarySession(subnet.getCurrentFE(), null);
            IStatement statement = session.createStatement();
            FVCmdGetPMConfig cmd = new FVCmdGetPMConfig();
            statement.execute(cmd);
            FVResponse<PMConfigBean> response = cmd.getResponse();
            List<PMConfigBean> results = response.get();
            if (results != null && !results.isEmpty()) {
                session.close();
                return results.get(0);
            }
        } catch (Exception e) {
            ConfigurationException ce = getConfigException(
                    STL50008_SUBNET_CONNECTION_ERROR, e, subnet.getPrimaryFE(),
                    StringUtils.getErrorMessage(e));
            throw ce;
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return config;
    }

    @Override
    public AppInfo getAppInfo() {
        return dbMgr.getAppInfo();
    }

    @Override
    public void saveAppInfo(AppInfo appInfo) {

        dbMgr.saveAppProperties(appInfo.getPropertiesMap());
    }

    @Override
    public List<SubnetDescription> getSubnets() {
        List<SubnetDescription> subnets = null;
        try {
            subnets = dbMgr.getSubnets();
        } catch (Exception e) {
            ConfigurationException ce =
                    getConfigException(STL30038_ERROR_GETTING_SUBNETS, e);
            throw ce;
        }
        return subnets;
    }

    // @Override
    // public List<SubnetDescription> getSubnets() throws DatabaseException {
    // return dbMgr.getSubnets();
    // }

    @Override
    public UserSettings getUserSettings(String subnetName, String userName)
            throws UserNotFoundException {
        try {
            return dbMgr.getUserSettings(subnetName, userName);
        } catch (DatabaseException e) {
            ConfigurationException ce =
                    getConfigException(STL30039_ERROR_GETTING_USER, e);
            throw ce;
        }
    }

    /**
     * TODO: save operations already handles exceptions in DAO. Why does DAO
     * still throw it? Shall I create a FMException for this? No just throw Run
     * time exception (ConfigException) with specific information. Use
     * STL30006??
     *
     */
    @Override
    public void saveUserSettings(String subnetName, UserSettings userSettings) {
        try {
            dbMgr.saveUserSettings(subnetName, userSettings);
        } catch (DatabaseException e) {
            ConfigurationException ce =
                    getConfigException(STL30040_ERROR_SAVING_USER, e);
            throw ce;
        }

    }

    /**
     * @return the logPropertyPath
     */
    @Override
    public String getLogPropertyPath() {
        return System.getProperty(FM_GUI_DIR);
    }

    /**
     * @param logPropertyPath
     *            the logPropertyPath to set
     */
    public void setLogPropertyPath(String logPropertyPath) {
        System.setProperty(FM_GUI_DIR, logPropertyPath);
    }

    @Override
    public SubnetDescription defineSubnet(SubnetDescription subnet) {
        try {
            return dbMgr.defineSubnet(subnet);
        } catch (DatabaseException e) {
            ConfigurationException ce =
                    getConfigException(STL30041_ERROR_SAVING_SUBNET, e);
            throw ce;
        }
    }

    @Override
    public void updateSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException {
        dbMgr.updateSubnet(subnet);
        adapter.refreshSubnetDescription(subnet);
    }

    @Override
    public void removeSubnet(long subnetId) throws SubnetDataNotFoundException {
        dbMgr.removeSubnet(subnetId);
    }

    @Override
    public SubnetDescription getSubnet(String subnetName)
            throws SubnetDataNotFoundException {
        return dbMgr.getSubnet(subnetName);
    }

    @Override
    public SubnetDescription getSubnet(long subnetId) {
        SubnetDescription subnet = dbMgr.getSubnet(subnetId);
        if (subnet == null) {
            ConfigurationException ce = new ConfigurationException(
                    STL30022_SUBNET_NOT_FOUND, subnetId);
            throw ce;
        }
        return subnet;
    }

    @Override
    public List<EventRule> getEventRules() throws EventNotFoundException {
        try {
            return dbMgr.getEventRules();
        } catch (DatabaseException e) {
            ConfigurationException ce =
                    getConfigException(STL30042_ERROR_GETTING_EVENT, e);
            throw ce;
        }
    }

    @Override
    public void saveEventRules(List<EventRule> rules) {

        try {
            dbMgr.saveEventRules(rules);
        } catch (DatabaseException e) {
            ConfigurationException ce =
                    getConfigException(STL30043_ERROR_SAVING_EVENT, e);
            throw ce;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.configuration.IConfigurationApi#getLoggingConfig()
     */
    @Override
    public LoggingConfiguration getLoggingConfig()
            throws ConfigurationException {
        return LogbackConfigurationHelper.getLoggingConfiguration(appDataPath);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.configuration.IConfigurationApi#
     * saveLoggingConfiguration
     * (com.intel.stl.api.configuration.LoggingConfiguration)
     */
    @Override
    public void saveLoggingConfiguration(LoggingConfiguration config)
            throws ConfigurationException {
        LogbackConfigurationHelper.updateLoggingConfiguration(appDataPath,
                config);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.configuration.IConfigurationApi#updateMailProperties
     * (com.intel.stl.api.configuration.MailProperties)
     */
    @Override
    public void updateMailProperties(MailProperties properties) {
        mailMgr.updateMailProperties(properties);
    }

    @Override
    public MailProperties getMailProperties() {
        return mailMgr.getMailProperties();
    }

    @Override
    public void submitMessage(String subject, String body,
            List<String> recipients) {
        mailMgr.submitMessage(subject, body, recipients);
    }

    @Override
    public void sendTestMail(MailProperties properties, String recipient,
            String messageSubject, String messageBody) {
        mailMgr.sendTestMail(properties, recipient, messageSubject,
                messageBody);
    }

    @Override
    public void addEmailEventListener(
            IEmailEventListener<NoticeBean> listener) {
        mailMgr.addEmailEventListener(listener);
    }

    @Override
    public void removeEmailListener(IEmailEventListener<NoticeBean> listener) {
        mailMgr.removeEmailListener(listener);
    }

    @Override
    public void startSimulatedFailover(String subnetName) {
        adapter.startSimulatedFailover(subnetName);
    }

    private ConfigurationException getConfigException(STLMessages msg,
            Exception e, Object... arguments) {
        ConfigurationException ce =
                new ConfigurationException(msg, e, arguments);
        log.error(StringUtils.getErrorMessage(ce), e);
        return ce;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.configuration.IConfigurationApi#cleanup()
     */
    @Override
    public void cleanup() {
        // so far, nothing to do.
    }

    @Override
    public boolean isSmtpSettingsValid() {
        return mailMgr.isSmtpSettingsValid();
    }

    @Override
    public boolean isEmailValid(String email) {
        return mailMgr.isEmailValid(email);
    }

}
