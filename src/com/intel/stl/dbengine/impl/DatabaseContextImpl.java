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

import static com.intel.stl.common.STLMessages.STL30022_SUBNET_NOT_FOUND;

import javax.persistence.EntityManager;

import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.failure.IFailureManagement;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.dbengine.ConfigurationDAO;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.GroupDAO;
import com.intel.stl.dbengine.NoticeDAO;
import com.intel.stl.dbengine.PerformanceDAO;
import com.intel.stl.dbengine.SubnetDAO;

public class DatabaseContextImpl implements DatabaseContext {

    private final EntityManager em;

    private ConfigurationDAO configurationDAO;

    private GroupDAO groupDAO;

    private NoticeDAO noticeDAO;

    private final SubnetDAO subnetDAO;

    private final PerformanceDAO performanceDAO;

    private final IFailureManagement failureMgr;

    private long lastUsed;

    public DatabaseContextImpl(EntityManager em, IFailureManagement failureMgr) {
        this.em = em;
        this.failureMgr = failureMgr;
        this.subnetDAO = new SubnetDAOImpl(em, this);
        this.performanceDAO = new PerformanceDAOImpl(em, this);
    }

    @Override
    public EntityManager getEntityManager() {
        return em;
    }

    @Override
    public IFailureManagement getFailureManagement() {
        return failureMgr;
    }

    @Override
    public SubnetRecord getSubnet(String subnetName) {
        SubnetRecord subnet = subnetDAO.getSubnet(subnetName);
        if (subnet == null) {
            ConfigurationException ce =
                    new ConfigurationException(STL30022_SUBNET_NOT_FOUND,
                            subnetName);
            throw ce;
        }
        return subnet;
    }

    @Override
    public ConfigurationDAO getConfigurationDAO() {
        if (configurationDAO == null) {
            configurationDAO = new ConfigurationDAOImpl(em, this);
        }
        return configurationDAO;
    }

    @Override
    public GroupDAO getGroupDAO() {
        if (groupDAO == null) {
            groupDAO = new GroupDAOImpl(em, this);
        }
        return groupDAO;
    }

    @Override
    public NoticeDAO getNoticeDAO() {
        if (noticeDAO == null) {
            noticeDAO = new NoticeDAOImpl(em, this);
        }
        return noticeDAO;
    }

    @Override
    public SubnetDAO getSubnetDAO() {
        return subnetDAO;
    }

    @Override
    public PerformanceDAO getPerformanceDAO() {
        return performanceDAO;
    }

    protected long getLastUsed() {
        return lastUsed;
    }

    protected void setLastUsed(long lastUsed) {
        this.lastUsed = lastUsed;
    }
}
