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
import static com.intel.stl.common.AppDataUtils.LOGCONFIG_FILE;
import static com.intel.stl.common.STLMessages.STL50002_ERROR_READING_LOG4JPROPERTIES;
import static com.intel.stl.common.STLMessages.STL50004_UNSUPPORTED_APPENDER_TYPE;
import static com.intel.stl.common.STLMessages.STL50005_ERROR_UPDATING_LOGGING_CONFIG;
import static com.intel.stl.common.STLMessages.STL50007_ERROR_UPDATING_CUSTOMSETTINGS;
import static com.intel.stl.configuration.AppSettings.APP_LOG_FOLDER;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.AppenderConfig;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.ConsoleAppender;
import com.intel.stl.api.configuration.LoggerConfig;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.LoggingThreshold;
import com.intel.stl.api.configuration.RollingFileAppender;
import com.intel.stl.common.AppDataUtils;

public class LogbackConfigurationHelper {

    private static Logger log = LoggerFactory
            .getLogger(LogbackConfigurationHelper.class);

    private static final String FMGUI_APPENDER = "RollingFile-1";

    private static final String LOGDIR_SYS_PROP = "${" + FM_GUI_DIR + "}";

    protected static String logbackConfigFileName = LOGCONFIG_FILE;

    protected static String LOGCONFIG_TEMP_XML = "logconfignew.xml";

    private static final Set<String> internalLogs;

    static {
        Set<String> logs = new HashSet<String>();
        logs.add("HibernateSql");
        logs.add("DbMgr");
        logs.add("FecDriver");
        internalLogs = Collections.unmodifiableSet(logs);
    }

    public static LoggingConfiguration getLoggingConfiguration(
            String appDataPath) {
        String logConfigurationFilePath =
                appDataPath + File.separatorChar + logbackConfigFileName;
        File configFile = getConfigFile(logConfigurationFilePath);
        return getLoggingConfiguration(configFile);
    }

    public static void updateLoggingConfiguration(String appDataPath,
            LoggingConfiguration config) {
        String logConfigurationFilePath =
                appDataPath + File.separatorChar + logbackConfigFileName;
        File configFile = getConfigFile(logConfigurationFilePath);
        saveLoggingConfiguration(config, configFile, appDataPath);
    }

    public static LoggingConfiguration getLoggingConfiguration(File logConfig) {
        LoggingConfiguration loggingConfig = new LoggingConfiguration();
        List<AppenderConfig> appenders = new ArrayList<AppenderConfig>();
        List<LoggerConfig> loggers = new ArrayList<LoggerConfig>();
        LogbackConfigFactory factory = new LogbackConfigFactory(logConfig);

        // Get the root log level and create the root logger
        Node node = factory.getRootLogLevel();
        LoggingThreshold rootLogLevel =
                LoggingThreshold.valueOf(node.getNodeValue());
        LoggerConfig rootLogger = new LoggerConfig("Root", rootLogLevel);
        loggingConfig.setRootLogger(rootLogger);

        // Get the list of appenders
        NodeList nodes = factory.getAppenders();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String appenderName = e.getAttribute("name");
            if (internalLogs.contains(appenderName)) {
                continue;
            }
            String appenderClass = e.getAttribute("class");
            SupportedAppenderType type =
                    SupportedAppenderType
                            .getSupportedAppenderTypeFor(appenderClass);
            if (type == null) {
                log.error(STL50004_UNSUPPORTED_APPENDER_TYPE
                        .getDescription(appenderClass), e);
            } else {
                AppenderConfig appender = null;
                switch (type) {
                    case CONSOLE_APPENDER: {
                        ConsoleAppender consoleAppender = new ConsoleAppender();
                        consoleAppender.populateFromNode(e, factory);
                        appender = consoleAppender;
                        break;
                    }
                    case ROLLINGFILE_APPENDER: {
                        RollingFileAppender fileAppender =
                                new RollingFileAppender();
                        fileAppender.populateFromNode(e, factory);
                        resetFileLocationIfNeeded(fileAppender);
                        appender = fileAppender;
                        break;
                    }
                }
                appenders.add(appender);
            }
        }
        loggingConfig.setAppenders(appenders);

        // Get the list of loggers
        nodes = factory.getLoggers();
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String loggerName = e.getAttribute("name");
            String loggerLevel = e.getAttribute("level");
            LoggerConfig logger =
                    new LoggerConfig(loggerName,
                            LoggingThreshold.valueOf(loggerLevel));
            loggers.add(logger);
        }
        loggingConfig.setLoggers(loggers);

        return loggingConfig;
    }

    public static void saveLoggingConfiguration(LoggingConfiguration config,
            File logConfig, String appDataPath) {
        LogbackConfigFactory factory = new LogbackConfigFactory(logConfig);

        updateDocument(factory, config, appDataPath);

        String parentFolder = logConfig.getParent();
        String tempLogConfig =
                parentFolder + File.separatorChar + LOGCONFIG_TEMP_XML;
        File tempFile = new File(tempLogConfig);

        factory.saveConfig(tempFile);

        SimpleDateFormat formatter =
                new SimpleDateFormat("yyyy-MM-dd_HH-mm-SS");
        Date now = new Date();
        String reName = LOGCONFIG_FILE + "." + formatter.format(now);
        String logConfigBackup = parentFolder + File.separatorChar + reName;
        File backupFile = new File(logConfigBackup);

        try {
            @SuppressWarnings("unused")
            boolean renameOK = logConfig.renameTo(backupFile);
            renameOK = tempFile.renameTo(logConfig);
        } catch (SecurityException se) {
            ConfigurationException ce =
                    new ConfigurationException(
                            STL50005_ERROR_UPDATING_LOGGING_CONFIG, se,
                            StringUtils.getErrorMessage(se));
            log.error(StringUtils.getErrorMessage(ce), se);
            throw ce;
        }
    }

    protected static void updateDocument(LogbackConfigFactory factory,
            LoggingConfiguration config, String appDataPath) {
        if (config == null) {
            return;
        }
        NodeList nodes = factory.getAppenders();
        Map<String, AppenderConfig> appenders =
                new HashMap<String, AppenderConfig>();
        for (AppenderConfig appender : config.getAppenders()) {
            String appenderName = appender.getName();
            appenders.put(appenderName, appender);
        }
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String appenderName = e.getAttribute("name");
            if (FMGUI_APPENDER.equals(appenderName)) {
                // We need to reset FM_GUI_DIR before anything else because
                // FileLocation is replaced with the environment variable when
                // we update the DOM
                AppenderConfig appender = appenders.get(appenderName);
                if (appender != null) {
                    resetLogDir((RollingFileAppender) appender, appDataPath);
                    factory.setRootLogLevel(config.getRootLogger().getLevel()
                            .name());
                }
                break;
            }
        }
        Element lastUserAppender = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element e = (Element) nodes.item(i);
            String appenderName = e.getAttribute("name");
            if (internalLogs.contains(appenderName)) {
                continue;
            }
            lastUserAppender = e;
            AppenderConfig appender = appenders.get(appenderName);
            if (appender == null) {
                Node parent = e.getParentNode();
                parent.removeChild(e);
                log.info("Removing appender '" + appenderName
                        + "' from logging configuration.");
            } else {
                appender.updateNode(e, factory);
                appenders.remove(appenderName);
            }
        }
        if (appenders.size() > 0 && lastUserAppender != null) {
            for (String name : appenders.keySet()) {
                AppenderConfig newAppender = appenders.get(name);
                Node newElement = newAppender.createNode(factory);
                lastUserAppender.getParentNode().insertBefore(newElement,
                        lastUserAppender.getNextSibling());
            }

        }
    }

    private static void resetFileLocationIfNeeded(RollingFileAppender appender) {
        String location = appender.getFileLocation();
        String logDir = System.getProperty(FM_GUI_DIR);
        if (location != null && location.contains(LOGDIR_SYS_PROP)) {
            location = location.replace(LOGDIR_SYS_PROP, logDir);
            location = location.replace("/", File.separator);
            appender.setFileLocation(location);
        }
        String namePattern = appender.getFileNamePattern();
        if (namePattern != null && namePattern.contains(LOGDIR_SYS_PROP)) {
            namePattern = namePattern.replace(LOGDIR_SYS_PROP, logDir);
            namePattern = namePattern.replace("/", File.separator);
            appender.setFileNamePattern(namePattern);
        }
    }

    private static void resetLogDir(RollingFileAppender appender,
            String appDataPath) {
        File logFile = new File(appender.getFileLocation());
        String logDir = System.getProperty(FM_GUI_DIR);
        String filePath = logFile.getParent();
        if (!filePath.equals(logDir)) {
            try {
                Properties customSettings =
                        AppDataUtils.getCustomSettings(appDataPath);
                customSettings.put(APP_LOG_FOLDER, filePath);
                AppDataUtils.saveCustomSettings(customSettings, appDataPath);
                System.setProperty(FM_GUI_DIR,
                        filePath.replace(File.separator, "/"));
            } catch (Exception e) {
                ConfigurationException ce =
                        new ConfigurationException(
                                STL50007_ERROR_UPDATING_CUSTOMSETTINGS, e,
                                StringUtils.getErrorMessage(e));
                log.error(StringUtils.getErrorMessage(ce), e);
                throw ce;
            }
        }
    }

    private static File getConfigFile(String configFilePath) {
        File configFile = new File(configFilePath);
        if (!configFile.exists()) {
            ConfigurationException ce =
                    new ConfigurationException(
                            STL50002_ERROR_READING_LOG4JPROPERTIES, 0,
                            "File not found");
            log.error(StringUtils.getErrorMessage(ce));
            throw ce;
        }
        return configFile;
    }

    public static void setLogbackConfigFileName(String logbackConfigFileName) {
        LogbackConfigurationHelper.logbackConfigFileName =
                logbackConfigFileName;
    }

}
