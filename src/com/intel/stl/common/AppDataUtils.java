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
package com.intel.stl.common;

/**
 */
import static com.intel.stl.common.STLMessages.STL10014_CANNOT_OVERRIDE_SETTING;
import static com.intel.stl.common.STLMessages.STL10015_OVERRIDING_SETTING;
import static com.intel.stl.common.STLMessages.STL10023_ERROR_READING_RESOURCE;
import static com.intel.stl.configuration.AppSettings.APP_DATA_PATH;
import static com.intel.stl.configuration.AppSettings.APP_INTEL_PATH;
import static com.intel.stl.configuration.AppSettings.APP_LOG_FOLDER;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.Writer;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.InvalidPropertiesFormatException;
import java.util.Properties;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.intel.stl.configuration.AppSettings;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.core.util.StatusPrinter;

public class AppDataUtils {

    // Do not initialize this Logger here; this class is used to initialize
    // Log4j and that would trigger the Log4j initialization
    private static Logger log;

    private static final boolean DEBUG_LOGGING = false;

    // If you change the following two variables, please keep in mind that they
    // are used to build a path to a file system location where this application
    // stores its data. The un-installers also use these values to delete the
    // location on uninstall.
    protected static final String INTEL_NAME = "Intel";

    protected static final String APPLICATION_NAME = "FabricManagerGUI";

    protected static final String CONFIGURATION_URL =
            "/com/intel/stl/configuration/";

    private static final String DATABASE_FOLDER_NAME = "db";

    private static final String LOGS_FOLDER_NAME = "logs";

    private static final String UTIL_FOLDER_NAME = "util";

    private static final String APPLICATION_SETTINGS_FILE = "settings.xml";

    private static final String DEFAULT_USEROPTIONS_FILE =
            "defaultuseroptions.xml";

    public static final String LOGCONFIG_FILE = "logconfig.xml";

    protected static final String LOCK_FILE = APPLICATION_NAME + ".lock";

    public static final String FM_GUI_DIR = "fmgui.logs.dir";

    public static final String LOGBACK_CONFIGFILE = "logback.configurationFile";

    protected static final String APPDATA_STR = "APPDATA";

    protected static SystemFunctions sysfunctions = new SystemFunctionsImpl();

    public static String getIntelDataPath() {

        String appDataPath = "";
        String osName = sysfunctions.getSystemProperty("os.name").toLowerCase();
        if (osName.indexOf("windows") != -1) {
            appDataPath = sysfunctions.getEnvironmentVariable(APPDATA_STR);
            String osmaj = sysfunctions.getSystemProperty("os.version")
                    .split("\\.")[0];
            int osver = Integer.parseInt(osmaj);
            if (osver >= 5) {
                // %APPDATA% locations
                // Vista or later: C:\Users\<username>\AppData\Roaming
                // XP or 2k3: C:\Documents and Settings\<username>\Application
                // Data
                appDataPath = sysfunctions.getEnvironmentVariable(APPDATA_STR)
                        + File.separatorChar + INTEL_NAME;
            } else {
                // too old
            }

        } else if (osName.indexOf("mac") != -1) {
            // On Mac OS X, user data should be in
            // System.getProperty("user.home") + "/Library/" + "Your App Name"
            appDataPath = sysfunctions.getSystemProperty("user.home")
                    + File.separatorChar + "Library" + File.separatorChar
                    + INTEL_NAME;
        } else {
            appDataPath = sysfunctions.getSystemProperty("user.home")
                    + File.separatorChar + "." + INTEL_NAME;
        }

        return appDataPath;
    }

    public static void createIntelDataFolder() {
        createFolder(getIntelDataPath());
    }

    public static String getApplicationDataPath() {
        return getIntelDataPath() + File.separatorChar + APPLICATION_NAME;
    }

    public static void createApplicationDataFolder() {
        createFolder(getApplicationDataPath());
    }

    public static String getDatabaseDataPath() {
        return getApplicationDataPath() + File.separatorChar
                + DATABASE_FOLDER_NAME;
    }

    public static void createDatabaseDataFolder() {
        createFolder(getDatabaseDataPath());
    }

    public static String getLogPath() {
        return getApplicationDataPath() + File.separatorChar + LOGS_FOLDER_NAME;
    }

    public static void createLogFolder() {
        createFolder(getLogPath());
    }

    public static String getLogPropertyPath() {
        return getApplicationDataPath() + File.separatorChar + LOGCONFIG_FILE;
    }

    public static String getLockFilePath() {
        return getApplicationDataPath() + File.separatorChar + LOCK_FILE;
    }

    protected static void createFolder(String folderPath) {
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdir();
        }
    }

    public static AppSettings getApplicationSettings()
            throws InvalidPropertiesFormatException, IOException {
        return new AppSettings(getApplicationSettings(APPLICATION_SETTINGS_FILE,
                CONFIGURATION_URL, getApplicationDataPath()));
    }

    protected static Properties getApplicationSettings(String settingsFilename,
            String settingsUrl, String settingsLocation)
                    throws InvalidPropertiesFormatException, IOException {
        log = LoggerFactory.getLogger(AppDataUtils.class);
        Properties settings = new Properties();
        URL settingsResource =
                sysfunctions.getResource(settingsUrl + settingsFilename);
        if (settingsResource == null) {
            // this resource should always exist; return no settings if not
            // found
            return settings;
        }
        InputStream sis = AppDataUtils.class
                .getResourceAsStream(settingsUrl + settingsFilename);
        try {
            settings.loadFromXML(sis);
        } finally {
            // loadFromXML will try to close sis. but it's not called in
            // finally, so we try to close it again here.
            try {
                sis.close();
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
        }

        Properties overrides =
                getCustomSettings(settingsFilename, settingsLocation);
        Set<Object> keys = overrides.keySet();
        for (Object obj : keys) {
            String key = obj.toString().toLowerCase();
            String oldvalue = settings.getProperty(key);
            String newvalue = overrides.getProperty(key);
            if (key.equals(APP_INTEL_PATH) || key.equals(APP_DATA_PATH)) {
                log.warn(STL10014_CANNOT_OVERRIDE_SETTING.getDescription(key));
            } else {
                log.warn(STL10015_OVERRIDING_SETTING.getDescription(key,
                        oldvalue, newvalue));
                settings.setProperty(key, newvalue);
            }
        }
        return settings;
    }

    public static Properties getCustomSettings(String settingsLocation)
            throws InvalidPropertiesFormatException, IOException {
        return getCustomSettings(APPLICATION_SETTINGS_FILE, settingsLocation);
    }

    protected static Properties getCustomSettings(String settingsFilename,
            String settingsLocation)
                    throws InvalidPropertiesFormatException, IOException {
        File settingsFile = new File(
                settingsLocation + File.separatorChar + settingsFilename);
        Properties customSettings = new Properties();
        if (settingsFile.exists()) {
            InputStream ois = new FileInputStream(settingsFile);
            try {
                customSettings.loadFromXML(ois);
            } finally {
                // loadFromXML will try to close ois. but it's not called in
                // finally, so we try to close it again here.
                try {
                    ois.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
        return customSettings;
    }

    public static void saveCustomSettings(Properties customSettings,
            String settingsLocation) throws IOException {
        saveCustomSettings(customSettings, APPLICATION_SETTINGS_FILE,
                settingsLocation);
    }

    protected static void saveCustomSettings(Properties customSettings,
            String settingsFilename, String settingsLocation)
                    throws IOException {
        File settingsFile = new File(
                settingsLocation + File.separatorChar + settingsFilename);
        FileOutputStream fileOut = new FileOutputStream(settingsFile);
        try {
            customSettings.storeToXML(fileOut, "Saved");
        } catch (IOException e) {
            log.error("Error saving custom settings to file '"
                    + settingsFile.getAbsolutePath() + "'.", e);
            throw e;
        } finally {
            fileOut.close();
        }
    }

    /*
     * Sets up the log4j system. This routine runs before anything in the
     * application startup because we need log4j to report any errors.
     * Therefore, this routine does only what's absolutely necessary to set up
     * log4j.
     */
    public static void initializeLogging() {
        // Intel and Data folders cannot be changed through custom settings
        createIntelDataFolder();
        createApplicationDataFolder();
        String appDataPath = getApplicationDataPath();
        copyLogConfigurationIfNonExistent(LOGCONFIG_FILE, appDataPath,
                CONFIGURATION_URL);
        String logPath;
        try {
            Properties customSettings = getCustomSettings(appDataPath);
            logPath = customSettings.getProperty(APP_LOG_FOLDER, getLogPath());
        } catch (Exception e) {
            createLogFolder();
            logPath = getLogPath();
        }
        logPath = logPath.replace("\\", "/");
        System.setProperty(FM_GUI_DIR, logPath);
        String log4jFile = getLogPropertyPath();
        log4jFile = log4jFile.replace("\\", "/");
        System.setProperty(LOGBACK_CONFIGFILE, log4jFile);
        System.setProperty("org.jboss.logging.provider", "slf4j");

        if (DEBUG_LOGGING) {
            LoggerContext context =
                    (LoggerContext) LoggerFactory.getILoggerFactory();
            StatusPrinter.print(context);
        }
        // JoranConfigurator configurator = new JoranConfigurator();
        // configurator.setContext(context);
        // context.reset();
        // configurator.doConfigure(args[0]);
        log = LoggerFactory.getLogger(AppDataUtils.class);
    }

    protected static void copyLogConfigurationIfNonExistent(
            String log4jPropertiesFilename, String log4PropertiesLocation,
            String settingsUrl) {
        File log4jProps = new File(log4PropertiesLocation + File.separatorChar
                + log4jPropertiesFilename);
        if (!log4jProps.exists()) {
            URL log4jResource = sysfunctions
                    .getResource(settingsUrl + log4jPropertiesFilename);
            if (log4jResource != null) {
                copyResourceToFile(log4jResource, log4jProps);
            }
        }
    }

    public static void copyResourceToFile(URL resource, File file) {
        InputStream istream = null;
        StringBuilder contents = new StringBuilder();
        try {
            istream = resource.openStream();
            BufferedReader in = sysfunctions.getReader(istream);
            String line = in.readLine();
            while (line != null) {
                contents.append(line + System.getProperty("line.separator"));
                line = in.readLine();
            }
        } catch (IOException e) {
            String errMsg = STL10023_ERROR_READING_RESOURCE
                    .getDescription(resource.getFile(), e.getMessage());
            RuntimeException rte = new RuntimeException(errMsg, e);
            // LogLog.error(errMsg, e);
            throw rte;
        } finally {
            if (istream != null) {
                try {
                    istream.close();
                } catch (IOException e) {
                }
            }
        }
        try {
            writeFile(file, contents.toString());
        } catch (IOException e) {
            // LogLog.error(
            // "I/O exception while writing to file '"
            // + file.getAbsolutePath() + "'", e);
        }
    }

    public static void writeFile(File file, String contents)
            throws IOException {
        Writer output = new BufferedWriter(new FileWriter(file));
        try {
            output.write(contents);
        } catch (IOException e) {
            throw e;
        } finally {
            output.close();
        }
    }

    public static String getDefaultUserOptions() {
        String resourceFile = CONFIGURATION_URL + DEFAULT_USEROPTIONS_FILE;
        try {

            return readResourceFile(resourceFile);
        } catch (IOException e) {
            String errMsg = STL10023_ERROR_READING_RESOURCE
                    .getDescription(resourceFile, e.getMessage());
            log.error(errMsg, e);
            RuntimeException rte = new RuntimeException(errMsg, e);
            throw rte;
        }

    }

    public static String readResourceFile(String resourceFile)
            throws IOException {
        URL resourceUrl = sysfunctions.getResource(resourceFile);
        if (resourceUrl != null) {
            InputStream istream = null;
            try {
                istream = resourceUrl.openStream();
                BufferedReader in = sysfunctions.getReader(istream);
                String line = in.readLine();
                StringBuffer xmlFile = new StringBuffer();
                while (line != null) {
                    String trimmedLine = line.trim();
                    xmlFile.append(trimmedLine);
                    line = in.readLine();
                }
                return xmlFile.toString();
            } catch (IOException e) {
                log.error("IOException reading resource '" + resourceFile + "'",
                        e);
                throw e;
            } finally {
                if (istream != null) {
                    istream.close();
                }
            }
        } else {
            RuntimeException rte = new RuntimeException("Resource file '"
                    + resourceFile + "' not found in classpath.");
            throw rte;
        }
    }

    public static final Element convertToDOM(String xml)
            throws IOException, ParserConfigurationException, SAXException {
        InputSource sourceXML = new InputSource(new StringReader(xml));

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document xmlDoc = builder.parse(sourceXML);
        Element ele = xmlDoc.getDocumentElement();
        ele.normalize();
        return ele;
    }

    public static final void prettyXmlPrint(Node xml, OutputStream out)
            throws TransformerConfigurationException,
            TransformerFactoryConfigurationError, TransformerException {
        Transformer transformer =
                TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(
                "{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(new DOMSource(xml), new StreamResult(out));
    }

    public static final String getPostSetupScript() {
        String fmguiJarPath = getAppInstallPath();
        fmguiJarPath = fmguiJarPath + File.separatorChar + UTIL_FOLDER_NAME
                + File.separatorChar;
        String postSetupScript = null;
        String osName = sysfunctions.getSystemProperty("os.name").toLowerCase();
        if (osName.indexOf("windows") != -1) {
            postSetupScript =
                    fmguiJarPath + File.separatorChar + "postsetup.bat";
        } else if (osName.indexOf("mac") != -1) {
            // No support for Mac OS X yet
        } else {
            postSetupScript =
                    fmguiJarPath + File.separatorChar + "postsetup.sh";
        }
        return postSetupScript;
    }

    public static final boolean isPostSetupNeeded() {
        boolean needed = true;
        String osName = sysfunctions.getSystemProperty("os.name").toLowerCase();
        String cleanScript;
        if (osName.indexOf("windows") != -1) {
            cleanScript = "fmguiclear.bat";
        } else if (osName.indexOf("mac") != -1) {
            return false;
        } else {
            cleanScript = "fmguiclear.sh";
        }
        String dataPath = getApplicationDataPath();
        File copiedFile = new File(dataPath + File.separatorChar + cleanScript);
        if (copiedFile.exists()) {
            String fmguiJarPath = getAppInstallPath();
            File origFile = new File(fmguiJarPath + File.separatorChar
                    + UTIL_FOLDER_NAME + File.separatorChar + cleanScript);
            if (origFile.exists()) {
                if (copiedFile.lastModified() == origFile.lastModified()) {
                    needed = false;
                }
            }
        }
        return needed;
    }

    private static String getAppInstallPath() {
        String fmguiJarPath = "";
        try {
            fmguiJarPath = sysfunctions.getClass().getProtectionDomain()
                    .getCodeSource().getLocation().toURI().getPath();
            if (fmguiJarPath.endsWith(".jar")) {
                File jarPath = new File(fmguiJarPath);
                fmguiJarPath = jarPath.getParent();
            }
        } catch (URISyntaxException e) {
        }
        return fmguiJarPath;
    }

}
