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
package com.intel.stl.dbengine.impl;

import static com.intel.stl.common.STLMessages.STL30006_SQLEXCEPTION;
import static com.intel.stl.common.STLMessages.STL30007_ERROR_STARTING_DB_ENGINE;
import static com.intel.stl.configuration.AppSettings.APP_DB_PATH;
import static com.intel.stl.configuration.AppSettings.DB_CONNECTION_PASSWORD;
import static com.intel.stl.configuration.AppSettings.DB_CONNECTION_URL;
import static com.intel.stl.configuration.AppSettings.DB_CONNECTION_USER;
import static com.intel.stl.configuration.AppSettings.DB_NAME;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.EntityManager;

import org.hsqldb.Server;
import org.hsqldb.jdbc.JDBCPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.AppInfo;
import com.intel.stl.common.STLMessages;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.configuration.AppSettings;
import com.intel.stl.dbengine.DatabaseEngine;

/**
 * This class is responsible for initializing, starting up and shutting down the
 * database under HyperSQL
 *
 */
public class HSqlDbEngine implements DatabaseEngine {
    private static Logger log = LoggerFactory.getLogger(HSqlDbEngine.class);

    private static final String DB_ENGINE_NAME = "HSQLDB";

    private static final String DB_ENGINE_VERSION = "2.3";

    public static final String DB_DEFINITION_FILE = "hsqldbdef.sql";

    private static final String HSQLDB_SERVER_URL = "jdbc:hsqldb:hsql:";

    private static final int HSQLDB_SERVER_DEFAULT_PORT = 9001;

    private static final String LOCALHOST_SERVER = "localhost";

    private static final String SLASHES = "//";

    private final String databaseDefinition;

    private String databaseFolder;

    private String databaseName;

    private String connectionUrl;

    private String user;

    private Server server;

    private final JDBCPool pool;

    public HSqlDbEngine(AppSettings settings) throws AppConfigurationException {
        this(settings, DB_DEFINITION_FILE, new JDBCPool());
    }

    public HSqlDbEngine(AppSettings settings, String definitionFile)
            throws AppConfigurationException {
        this(settings, definitionFile, new JDBCPool());
    }

    public HSqlDbEngine(AppSettings settings, String definitionFile,
            JDBCPool pool) throws AppConfigurationException {
        applySettings(settings);
        this.databaseDefinition = definitionFile;
        this.pool = pool;
        String password = settings.getConfigOption(DB_CONNECTION_PASSWORD);
        pool.setUrl(connectionUrl);
        pool.setUser(user);
        pool.setPassword(password);
    }

    private void applySettings(AppSettings settings)
            throws AppConfigurationException {
        this.databaseFolder = settings.getConfigOption(APP_DB_PATH);
        this.databaseName = settings.getConfigOption(DB_NAME);
        this.user = settings.getConfigOption(DB_CONNECTION_USER);
        String connectionUrl;
        try {
            connectionUrl = settings.getConfigOption(DB_CONNECTION_URL);
        } catch (AppConfigurationException e) {
            log.info(e.getMessage());
            connectionUrl = null;
        }
        if (connectionUrl == null) {
            this.connectionUrl = "jdbc:hsqldb:file:" + databaseFolder
                    + File.separatorChar + databaseName
                    + ";hsqldb.default_table_type=cached";
        } else {
            this.connectionUrl = connectionUrl;
        }

    }

    @Override
    public void start() throws DatabaseException {
        String urlPrefix = getConnectionUrlPrefix();
        String host = getConnectionUrlHost();
        if (HSQLDB_SERVER_URL.equalsIgnoreCase(urlPrefix)
                && LOCALHOST_SERVER.equalsIgnoreCase(host)) {
            server = new Server();
            server.setDaemon(true);
            server.setDatabasePath(0, databaseFolder);
            server.setDatabaseName(0, databaseName);
            int port = getConnectionUrlPort();
            server.setPort(port);
            server.start();
        }
    }

    @Override
    public void stop(boolean compact) throws DatabaseException {
        Connection conn = getConnection();
        try {
            PreparedStatement shutdown =
                    conn.prepareStatement("SHUTDOWN COMPACT");
            try {
                shutdown.execute();
            } finally {
                shutdown.close();
            }
            conn.close();
        } catch (SQLException e) {
            log.error(
                    STLMessages.STL30006_SQLEXCEPTION.getDescription(
                            e.getErrorCode(), StringUtils.getErrorMessage(e)),
                    e);
        }
    }

    @Override
    public void updateSchema() throws AppConfigurationException {
        Connection conn = null;
        try {
            conn = getConnection();
            PreparedStatement drop =
                    conn.prepareStatement("DROP SCHEMA PUBLIC CASCADE");
            try {
                drop.execute();
            } finally {
                drop.close();
            }
            DatabaseUtils.defineDatabase(conn, databaseDefinition);
            if (!DatabaseUtils.checkDatabase(conn)) {
                DatabaseUtils.defineDatabase(conn, databaseDefinition);
            } else {

            }
        } catch (SQLException e) {
            // TODO Define message for this condition
            AppConfigurationException ace =
                    new AppConfigurationException("SQLException", e);
            throw ace;
        } catch (DatabaseException e) {
            // TODO Define message for this condition
            AppConfigurationException ace =
                    new AppConfigurationException("DatabaseException", e);
            throw ace;
        }

        try {
            conn.close();
        } catch (SQLException e) {
            String errMsg = STL30007_ERROR_STARTING_DB_ENGINE.getDescription(
                    DB_ENGINE_NAME, "close()", e.getErrorCode());
            log.error(errMsg, e);
            AppConfigurationException ace =
                    new AppConfigurationException(errMsg, e);
            throw ace;
        }
    }

    private void checkSchemaTimestamp() throws DatabaseException {
        long defTimestamp = DatabaseUtils
                .getDatabaseDefinitionTimestamp(DB_DEFINITION_FILE);
        // File dbTimestampFile = new File(databaseFolder + File.separatorChar
        // + databaseName + DB_TIMESTAMP_EXT);
        // long dbTimestamp =
        // DatabaseUtils.getDatabaseTimestamp(dbTimestampFile);
        // Date dbSchemaDate = new Date(dbTimestamp);

    }

    @Override
    public Connection getConnection() throws DatabaseException {
        Connection conn = null;
        try {
            conn = pool.getConnection();
        } catch (SQLException e) {
            DatabaseException dbe = new DatabaseException(STL30006_SQLEXCEPTION,
                    e, e.getErrorCode(), StringUtils.getErrorMessage(e));
            log.error(dbe.getMessage(), e);
            throw dbe;
        }
        return conn;
    }

    @Override
    public EntityManager getEntityManager() {
        // No JPA support here
        return null;
    }

    @Override
    public String getConnectionUrl() {
        return connectionUrl;
    }

    @Override
    public String getUser() {
        return user;
    }

    @Override
    public String getEngineName() {
        return DB_ENGINE_NAME;
    }

    @Override
    public String getEngineVersion() {
        return DB_ENGINE_VERSION;
    }

    private void renameDbFolder() {
        Date dateSuffix = new Date();
        String rename = databaseFolder + "-"
                + new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss")
                        .format(dateSuffix);
        File dbFolderRename = new File(rename);
        File dbFolder = new File(databaseFolder);
        if (!dbFolder.renameTo(dbFolderRename)) {

        }
    }

    @Override
    public AppInfo getAppInfo() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void saveAppInfo(AppInfo info) throws DatabaseException {
        // TODO Auto-generated method stub

    }

    private String getConnectionUrlPrefix() {
        return connectionUrl.substring(0, HSQLDB_SERVER_URL.length());
    }

    private String getConnectionUrlHost() {
        int x = HSQLDB_SERVER_URL.length();
        if (x <= connectionUrl.length()) {
            return "";
        }
        String slashes = connectionUrl.substring(x, x + 2);
        if (SLASHES.equalsIgnoreCase(slashes)) {
            x = x + 2;
        }
        int colon = connectionUrl.indexOf(":", x);
        if (colon >= 0) {
            return connectionUrl.substring(x, colon);
        } else {
            int slash = connectionUrl.indexOf("/", x);
            if (slash >= 0) {
                return connectionUrl.substring(x, slash);
            } else {
                return connectionUrl.substring(x);
            }
        }
    }

    private int getConnectionUrlPort() {
        int x = HSQLDB_SERVER_URL.length();
        if (x < connectionUrl.length()) {
            return HSQLDB_SERVER_DEFAULT_PORT;
        }
        int colon = connectionUrl.indexOf(":", x);
        if (colon >= 0) {
            int slash = connectionUrl.indexOf("/", colon + 1);
            if (slash >= 0) {
                return Integer
                        .parseInt(connectionUrl.substring(colon + 1, slash));
            } else {
                return Integer.parseInt(connectionUrl.substring(colon + 1));
            }
        } else {
            return HSQLDB_SERVER_DEFAULT_PORT;
        }
    }
}
