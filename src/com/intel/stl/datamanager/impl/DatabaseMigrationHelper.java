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

package com.intel.stl.datamanager.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.persistence.TypedQuery;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.jdbc.ReturningWork;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.UserRecord;

public class DatabaseMigrationHelper {
    private static Logger log = LoggerFactory
            .getLogger(DatabaseMigrationHelper.class);

    private static final String INSERT_SUBNETRECORD =
            "INSERT INTO SUBNETS (subnetId, uniqueName, port, autoConnect, secureConnect, statusTimestamp) VALUES(?, ?, ?, ?, ?, ?)";

    private static final String INSERT_USERRECORD =
            "INSERT INTO USERS (subnetId, userName, lastUpdate, userDescription, userOptionXml) VALUES(?, ?, ?, ?, ?)";

    public static boolean checkTable(EntityManager em, final Class<?> tableClass)
            throws HibernateException {
        Table table = tableClass.getAnnotation(Table.class);
        final String tableName = table.name();
        Boolean res =
                em.unwrap(Session.class).doReturningWork(
                        new ReturningWork<Boolean>() {
                            @Override
                            public Boolean execute(Connection conn)
                                    throws SQLException {
                                ResultSet rs =
                                        conn.getMetaData().getTables(null,
                                                null, tableName, null);
                                boolean result = false;
                                try {
                                    result = rs.next();
                                } catch (SQLException e) {
                                    log.error(
                                            "Could not get next result from result set. '"
                                                    + StringUtils
                                                            .getErrorMessage(e),
                                            e);
                                } finally {
                                    rs.close();
                                }
                                return result;
                            }
                        });
        return res;
    }

    public static List<SubnetRecord> getSubnetRecords(EntityManager em) {
        if (!checkTable(em, SubnetRecord.class)) {
            return null;
        }

        TypedQuery<SubnetRecord> query =
                em.createNamedQuery("Subnet.All", SubnetRecord.class);
        return query.getResultList();
    }

    public static List<UserRecord> getUserRecords(EntityManager em) {
        if (!checkTable(em, UserRecord.class)) {
            return null;
        }

        TypedQuery<UserRecord> query =
                em.createNamedQuery("User.All", UserRecord.class);
        return query.getResultList();
    }

    public static SubnetRecord insertSubnetRecord(EntityManager em,
            SubnetRecord subnet) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        String subnetName = subnet.getSubnetDescription().getName();
        String uniqueName = subnet.getUniqueName();
        List<HostInfo> feList = subnet.getSubnetDescription().getFEList();
        List<HostInfo> newfeList = new ArrayList<HostInfo>(feList);
        subnet.getSubnetDescription().setFEList(newfeList);
        em.persist(subnet);
        try {
            tx.commit();
            TypedQuery<SubnetRecord> query =
                    em.createNamedQuery("Subnet.findByName", SubnetRecord.class);
            query.setParameter("subnetName", uniqueName);
            return query.getSingleResult();
        } catch (Exception e) {
            log.error("Could not save previously defined subnet '" + subnetName
                    + "': " + StringUtils.getErrorMessage(e), e);
            return null;
        }
    }

    public static void insertUserRecord(EntityManager em, UserRecord user) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        em.persist(user);
        try {
            tx.commit();
        } catch (Exception e) {
            log.error("Could not save previously defined user settings '"
                    + user.getId() + "': " + StringUtils.getErrorMessage(e), e);
        }
    }

    public static void insertSpecialRecord(EntityManager em, SubnetRecord subnet) {
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        Query query = em.createNativeQuery(INSERT_SUBNETRECORD);
        query.setParameter(1, subnet.getId());
        query.setParameter(2, subnet.getUniqueName());
        SubnetDescription desc = subnet.getSubnetDescription();
        query.setParameter(3, desc.getCurrentFE().getPort());
        query.setParameter(4, desc.isAutoConnect());
        query.setParameter(5, desc.getCurrentFE().isSecureConnect());
        query.setParameter(6, desc.getStatusTimestamp());
        try {
            System.out.println("Inserting special record");
            query.executeUpdate();
            System.out.println("Execute done");
            tx.commit();
        } catch (Exception e) {
            log.error("Could not insert special record '" + subnet.getId()
                    + "': " + StringUtils.getErrorMessage(e), e);
        }
    }
}
