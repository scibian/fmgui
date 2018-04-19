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

import static com.intel.stl.common.STLMessages.STL30069_NO_IMAGEINFO;
import static com.intel.stl.common.STLMessages.STL30070_IMAGE_NUMBER_NOT_FOUND;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.PerformanceDataNotFoundException;
import com.intel.stl.datamanager.ImageInfoId;
import com.intel.stl.datamanager.ImageInfoRecord;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.dbengine.PerformanceDAO;

public class PerformanceDAOImpl extends BaseDAO implements PerformanceDAO {

    private static Logger log = LoggerFactory.getLogger("org.hibernate.SQL");

    /**
     * Description: data access object with methods related to the Performance
     * API
     * 
     * @param entityManager
     */
    public PerformanceDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public PerformanceDAOImpl(EntityManager entityManager,
            DatabaseContext databaseCtx) {
        super(entityManager, databaseCtx);
    }

    @Override
    public void saveImageInfos(SubnetRecord subnet,
            List<ImageInfoBean> imageInfos) {
        StringBuffer keys = new StringBuffer();
        keys.append(subnet.getSubnetDescription().getName());
        char separator = '|';
        startTransaction();
        long subnetId = subnet.getId();
        for (ImageInfoBean imageInfo : imageInfos) {
            ImageInfoId id = new ImageInfoId();
            id.setFabricId(subnetId);
            long sweepTimestamp = imageInfo.getSweepStart();
            id.setSweepTimestamp(sweepTimestamp);
            ImageInfoRecord imageRecord = em.find(ImageInfoRecord.class, id);
            if (imageRecord == null) {
                imageRecord = new ImageInfoRecord(subnetId, imageInfo);
                em.persist(imageRecord);
            } else {
                imageRecord.setImageInfo(imageInfo);
                em.merge(imageRecord);
            }
            keys.append(separator);
            keys.append(sweepTimestamp);
            separator = ',';
        }
        try {
            commitTransaction();
        } catch (Exception e) {
            DatabaseException dbe =
                    DatabaseUtils.createPersistDatabaseException(e,
                            ImageInfoRecord.class, keys);
            log.error(dbe.getMessage(), e);
            throw dbe;
        }
    }

    @Override
    public List<ImageInfoBean> getImageInfo(SubnetRecord subnet,
            long imageNumber) throws PerformanceDataNotFoundException {
        TypedQuery<ImageInfoBean> query =
                em.createNamedQuery("ImageInfo.findByImageNum",
                        ImageInfoBean.class);
        query.setParameter("subnetId", subnet.getId());
        query.setParameter("imageNumber", imageNumber);
        List<ImageInfoBean> imageInfos = query.getResultList();
        if (imageInfos == null || imageInfos.size() == 0) {
            PerformanceDataNotFoundException pdnf =
                    new PerformanceDataNotFoundException(
                            STL30070_IMAGE_NUMBER_NOT_FOUND, imageNumber,
                            subnet.getSubnetDescription().getName());
            throw pdnf;
        }
        return imageInfos;
    }

    @Override
    public ImageInfoBean getLastImageInfo(SubnetRecord subnet)
            throws PerformanceDataNotFoundException {
        TypedQuery<Long> query =
                em.createNamedQuery("ImageInfo.findLatest", Long.class);
        query.setParameter("subnetId", subnet.getId());
        Long latestSweepstart = query.getSingleResult();
        if (latestSweepstart == null) {
            PerformanceDataNotFoundException pdnf =
                    new PerformanceDataNotFoundException(STL30069_NO_IMAGEINFO,
                            subnet.getSubnetDescription().getName());
            throw pdnf;
        }
        ImageInfoId id = new ImageInfoId();
        id.setFabricId(subnet.getId());
        id.setSweepTimestamp(latestSweepstart);
        ImageInfoRecord latest = em.find(ImageInfoRecord.class, id);
        return latest.getImageInfo();
    }
}
