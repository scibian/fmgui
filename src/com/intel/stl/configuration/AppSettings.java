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
package com.intel.stl.configuration;

import java.util.Properties;
import java.util.Set;

import com.intel.stl.common.STLMessages;

/**
 */
public class AppSettings {

    // Application configuration options
    public static final String APP_DB_SUBNET = "app.db.subnet";

    public static final String APP_DB_SUBNET_INCLUDE_INACTIVE =
            "app.db.subnet.include_inactive";

    public static final String APP_DB_PATH = "app.db.path";

    public static final String APP_UI_PLUGIN = "app.ui.plugin";

    public static final String APP_LOG_FOLDER = "app.log.folder";

    // Database configuration options
    public static final String DB_ENGINE = "db.engine";

    public static final String DB_NAME = "db.name";

    public static final String DB_PERSISTENCE_PROVIDER =
            "db.persistence.provider";

    public static final String DB_PERSISTENCE_PROVIDER_NAME =
            "db.persistence.provider.name";

    public static final String DB_CONNECTION_DRIVER = "db.connection.driver";

    public static final String DB_CONNECTION_URL = "db.connection.url";

    public static final String DB_CONNECTION_USER = "db.connection.user";

    public static final String DB_CONNECTION_PASSWORD =
            "db.connection.password";

    public static final String DB_HIBERNATE_DIALECT = "db.hibernate.dialect";

    public static final String DB_DATABASE_PROVIDER_NAME =
            "db.database.provider.name";

    // The following application versioning values are set from the manifest and
    // cannot be overwritten
    public static final String APP_NAME = "app.name";

    public static final String APP_VERSION = "app.version";

    public static final String APP_RELEASE = "app.release";

    public static final String APP_MODLEVEL = "app.modlevel";

    public static final String APP_OPA_FM = "app.opa.fm";

    public static final String APP_BUILD_ID = "app.build.id";

    public static final String APP_BUILD_DATE = "app.build.date";

    public static final String APP_SCHEMA_LEVEL = "app.schema.level";

    // The following application configuration options are set internally and
    // cannot be overwritten
    public static final String APP_INTEL_PATH = "app.intel.path";

    public static final String APP_DATA_PATH = "app.data.path";

    // This setting controls the settings for javax.net.debug
    public static final String APP_NET_DEBUG = "app.net.debug";

    // This setting may be used to specify the failover timeout (in seconds)
    public static final String APP_FAILOVER_TIMEOUT = "app.failover.timeout";

    // This setting controls the use of the new FE adapter
    public static final String APP_ADAPTER_USENEW = "app.adapter.usenew";

    // Performance API settings
    public static final String PERF_IMAGEINFO_CACHESIZE =
            "perf.imageinfo.cachesize";

    public static final String PERF_IMAGEINFO_SAVEBATCH =
            "perf.imageinfo.savebatch";

    public static final String PERF_GROUPINFO_SAVEBATCH =
            "perf.groupinfo.savebatch";

    public static final String PERF_GROUPINFO_PURGEFREQ =
            "perf.groupinfo.purgefreq";

    public static final String PERF_GROUPINFO_RETENTION =
            "perf.groupinfo.retention";

    private final Properties properties;

    public AppSettings(Properties properties) {
        this.properties = properties;
    }

    public String getConfigOption(String option)
            throws AppConfigurationException {
        checkSettingExists(option);
        return properties.getProperty(option);
    }

    public String getConfigOption(String option, String defaultValue) {
        if (!properties.containsKey(option)) {
            return defaultValue;
        }
        return properties.getProperty(option);
    }

    public void setConfigOption(String option, String value) {
        properties.put(option, value);
    }

    public void setConfigOption(String option, Object value) {
        properties.put(option, value);
    }

    public void removeConfigOption(String option) {
        properties.remove(option);
    }

    public Set<Object> keySet() {
        return properties.keySet();
    }

    public String getAppName() throws AppConfigurationException {
        checkSettingExists(APP_NAME);
        return (String) properties.get(APP_NAME);
    }

    public Integer getAppVersion() throws AppConfigurationException {
        checkSettingExists(APP_VERSION);
        return (Integer) properties.get(APP_VERSION);
    }

    public Integer getAppRelease() throws AppConfigurationException {
        checkSettingExists(APP_RELEASE);
        return (Integer) properties.get(APP_RELEASE);
    }

    public Integer getAppModLevel() throws AppConfigurationException {
        checkSettingExists(APP_MODLEVEL);
        return (Integer) properties.get(APP_MODLEVEL);
    }

    public String getOpaFmVersion() throws AppConfigurationException {
        checkSettingExists(APP_OPA_FM);
        return (String) properties.get(APP_OPA_FM);
    }

    public String getAppBuildId() throws AppConfigurationException {
        checkSettingExists(APP_BUILD_ID);
        return (String) properties.get(APP_BUILD_ID);
    }

    public String getAppBuildDate() throws AppConfigurationException {
        checkSettingExists(APP_BUILD_DATE);
        return (String) properties.get(APP_BUILD_DATE);
    }

    public Integer getAppSchemaLevel() throws AppConfigurationException {
        checkSettingExists(APP_SCHEMA_LEVEL);
        return (Integer) properties.get(APP_SCHEMA_LEVEL);
    }

    private void checkSettingExists(String setting)
            throws AppConfigurationException {
        if (!properties.containsKey(setting)) {
            AppConfigurationException ace =
                    new AppConfigurationException(
                            STLMessages.STL10007_CONFIGURATION_OPTION_NOT_SET
                                    .getDescription(setting));
            throw ace;
        }

    }

}
