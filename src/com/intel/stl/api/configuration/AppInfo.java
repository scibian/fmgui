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

import java.io.Serializable;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class AppInfo implements Serializable {

    public final static String PROPERTIES_SUBNET_FRAMES = "SubnetFrames";

    public final static String PROPERTIES_SUBNET_STATE_SUFFIX = "-State";

    public final static String PROPERTIES_SMTP_SETTINGS = "SMTPSettings";

    public final static String PROPERTIES_FM_GUI_APP = "FMGUIApp";

    private static final long serialVersionUID = 1L;

    public static final String PROPERTIES_DATABASE = "Database";

    private int appVersion;

    private int appRelease;

    private int appModLevel;

    private int appSchemaLevel;

    private String appName;

    private String opaFmVersion;

    private String appBuildId;

    private String appBuildDate;

    private Map<String, Properties> propertiesMap =
            new ConcurrentHashMap<String, Properties>();

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    /**
     * 
     * <i>Description:</i> returns the FM GUI internal version number
     * 
     * @return
     */
    public int getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(int appVersion) {
        this.appVersion = appVersion;
    }

    /**
     * 
     * <i>Description:</i> returns the FM GUI internal release number
     * 
     * @return
     */
    public int getAppRelease() {
        return appRelease;
    }

    public void setAppRelease(int appRelease) {
        this.appRelease = appRelease;
    }

    /**
     * 
     * <i>Description:</i> returns the FM GUI internal modification level
     * 
     * @return
     */
    public int getAppModLevel() {
        return appModLevel;
    }

    public void setAppModLevel(int appModLevel) {
        this.appModLevel = appModLevel;
    }

    /**
     * 
     * <i>Description:</i> returns the Build Id, as set in RELEASE_TAG
     * 
     * @return
     */
    public String getAppBuildId() {
        return appBuildId;
    }

    public void setAppBuildId(String appBuildId) {
        this.appBuildId = appBuildId;
    }

    /**
     * @return the appBuildDate
     */
    public String getAppBuildDate() {
        return appBuildDate;
    }

    /**
     * @param appBuildDate
     *            the appBuildDate to set
     */
    public void setAppBuildDate(String appBuildDate) {
        this.appBuildDate = appBuildDate;
    }

    /**
     * 
     * <i>Description:</i> returns the application database schema level
     * 
     * @return
     */
    public int getAppSchemaLevel() {
        return appSchemaLevel;
    }

    public void setAppSchemaLevel(int appSchemaLevel) {
        this.appSchemaLevel = appSchemaLevel;
    }

    /**
     * 
     * <i>Description:</i> returns the OPA FM version corresponding to this
     * release of the FM GUI
     * 
     * @return the OPA FM version
     */
    public String getOpaFmVersion() {
        return opaFmVersion;
    }

    /**
     * 
     * <i>Description:</i> set the OPA FM version
     * 
     * @param opaFmVersion
     */
    public void setOpaFmVersion(String opaFmVersion) {
        this.opaFmVersion = opaFmVersion;
    }

    public Map<String, Properties> getPropertiesMap() {
        return this.propertiesMap;
    }

    public void setPropertiesMap(Map<String, Properties> propertiesMap) {
        this.propertiesMap = propertiesMap;
    }

    public void setProperty(String string, Properties properties) {
        if ((string != null) && (properties != null)) {
            propertiesMap.put(string, properties);
        }
    }

    public Properties getProperty(String string) {
        if (string != null) {
            return propertiesMap.get(string);
        } else {
            return new Properties();
        }
    }

}
