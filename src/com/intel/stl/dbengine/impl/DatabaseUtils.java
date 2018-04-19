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

import static com.intel.stl.common.STLMessages.STL10009_ERROR_READING_FILE;
import static com.intel.stl.common.STLMessages.STL30003_DATABASE_DEFINITION_FILE_NOT_FOUND;
import static com.intel.stl.common.STLMessages.STL30004_ERROR_READING_DATABASE_DEFINITION;
import static com.intel.stl.common.STLMessages.STL30005_ERRORS_DURING_DATABASE_DEFINITION;
import static com.intel.stl.common.STLMessages.STL30013_ERROR_SAVING_ENTITY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.common.STLMessages;
import com.intel.stl.datamanager.EventActionRecord;
import com.intel.stl.datamanager.NodeTypeRecord;

/**
 */
public class DatabaseUtils {

    private static final String SQL_CMD_SEPARATOR = "GO";

    private static final String SQL_SELECT_COUNT = "SELECT COUNT(*) FROM ";

    protected static String[] SQL_CHECK_TABLES = { "Subnet", "Node", "Port",
            "Topology" };

    private static Logger log = LoggerFactory.getLogger(DatabaseUtils.class);

    public static DatabaseException createPersistDatabaseException(
            Throwable cause, Class<?> entityClass, Object entityId) {
        Throwable last = cause;
        while (last.getCause() != null) {
            last = last.getCause();
        }
        String entity = entityClass.getSimpleName();
        DatabaseException dbe =
                new DatabaseException(STL30013_ERROR_SAVING_ENTITY, cause,
                        entity, entityId, StringUtils.getErrorMessage(last));
        return dbe;
    }

    public static boolean checkDatabase(Connection conn) {
        boolean databaseCreated = true;
        try {
            Statement stmt = conn.createStatement();
            try {
                for (int i = 0; i < SQL_CHECK_TABLES.length; i++) {
                    try {

                        ResultSet rs =
                                stmt.executeQuery(SQL_SELECT_COUNT
                                        + SQL_CHECK_TABLES[i] + ";");
                        if (rs != null) {
                            rs.close();
                        }
                    } catch (SQLException e) {
                        // This is the error code for HSqlDb; should make it
                        // less
                        // dependent
                        if (e.getErrorCode() != -5501) {
                            log.error(STLMessages.STL30006_SQLEXCEPTION
                                    .getDescription(e.getErrorCode(),
                                            e.getMessage()));
                        }
                        databaseCreated = false;
                    }
                }
            } finally {
                stmt.close();
            }
            return databaseCreated;
        } catch (SQLException e) {
            log.error(STLMessages.STL30006_SQLEXCEPTION.getDescription(
                    e.getErrorCode(), e.getMessage()));
            databaseCreated = false;
            return databaseCreated;
        }
    }

    public static void defineDatabase(Connection conn, String definitionFile)
            throws DatabaseException {
        URL dbDefUrl = DatabaseUtils.class.getResource(definitionFile);

        if (dbDefUrl != null) {
            InputStream istream = null;
            BufferedReader in = null;
            try {
                istream = dbDefUrl.openStream();
            } catch (IOException e) {
                DatabaseException dbe =
                        new DatabaseException(
                                STL30004_ERROR_READING_DATABASE_DEFINITION, e);
                log.error(dbe.getMessage(), e);
                throw dbe;
            }
            in = new BufferedReader(new InputStreamReader(istream));
            try {
                String line = in.readLine();
                StringBuffer sqlCmd = new StringBuffer();
                boolean definitionError = false;
                while (line != null) {
                    String trimmedLine = line.trim();
                    if (SQL_CMD_SEPARATOR.equalsIgnoreCase(trimmedLine)) {
                        String sqlStr = sqlCmd.toString();
                        if (!executeSQLCmd(conn, sqlStr)) {
                            definitionError = true;
                        }
                        sqlCmd = new StringBuffer();
                    } else {
                        sqlCmd.append(trimmedLine);
                        sqlCmd.append(" ");
                    }
                    line = in.readLine();
                }
                if (definitionError) {
                    DatabaseException dbe =
                            new DatabaseException(
                                    STL30005_ERRORS_DURING_DATABASE_DEFINITION);
                    throw dbe;
                }
            } catch (IOException e) {
                DatabaseException dbe =
                        new DatabaseException(
                                STL30004_ERROR_READING_DATABASE_DEFINITION, e);
                log.error(dbe.getMessage(), e);
                throw dbe;
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
        } else {
            DatabaseException dbe =
                    new DatabaseException(
                            STLMessages.STL30003_DATABASE_DEFINITION_FILE_NOT_FOUND);
            throw dbe;
        }
    }

    public static boolean executeSQLCmd(Connection conn, String sqlCmd) {
        log.debug("Executing SQL command: " + sqlCmd);
        boolean result = true;
        try {
            Statement stmt = conn.createStatement();
            try {
                stmt.execute(sqlCmd);
            } finally {
                stmt.close();
            }
        } catch (SQLException se) {
            log.error(STLMessages.STL30002_DATABASE_ENGINE_ERROR
                    .getDescription(se.getErrorCode(), sqlCmd), se);
            result = false;
        }
        return result;
    }

    public static void populateRequiredTables(EntityManager em) {
        NodeType[] types = NodeType.class.getEnumConstants();
        EventRuleAction[] actions = EventRuleAction.class.getEnumConstants();

        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (int i = 0; i < types.length; i++) {
            NodeTypeRecord type = new NodeTypeRecord();
            type.setId(types[i].getId());
            type.setNodeType(types[i]);
            em.persist(type);
        }
        for (int i = 0; i < actions.length; i++) {
            EventActionRecord action = new EventActionRecord();
            action.setId(actions[i].name());
            action.setAction(actions[i]);
            em.persist(action);
        }
        tx.commit();
    }

    public static long getDatabaseDefinitionTimestamp(String definitionFile)
            throws DatabaseException {
        URL dbDefUrl = DatabaseUtils.class.getResource(definitionFile);
        long timestamp = 0;
        if (dbDefUrl != null) {
            try {
                URLConnection urlConn = dbDefUrl.openConnection();
                timestamp = urlConn.getLastModified();
            } catch (IOException e) {
                DatabaseException dbe =
                        new DatabaseException(STL10009_ERROR_READING_FILE, e,
                                StringUtils.getErrorMessage(e));
                log.error(dbe.getMessage(), e);
                throw dbe;
            }
        } else {
            DatabaseException dbe =
                    new DatabaseException(
                            STL30003_DATABASE_DEFINITION_FILE_NOT_FOUND);
            throw dbe;
        }
        return timestamp;
    }

    public static long getDatabaseTimestamp(File timestampFile)
            throws DatabaseException {
        long timestamp = 0;
        try {
            BufferedReader in =
                    new BufferedReader(new FileReader(timestampFile));
            try {
                String line = in.readLine();
                if (line != null) {
                    timestamp = Long.parseLong(line);
                }
            } finally {
                in.close();
            }
        } catch (FileNotFoundException e) {
        } catch (IOException e) {
            DatabaseException dbe =
                    new DatabaseException(STL10009_ERROR_READING_FILE, e,
                            timestampFile.getAbsolutePath(),
                            StringUtils.getErrorMessage(e));
            log.error(dbe.getMessage(), e);
            throw dbe;
        }
        return timestamp;
    }

    public static void setSqlCheckTables(String[] tables) {
        SQL_CHECK_TABLES = tables;
    }
}
