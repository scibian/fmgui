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

import static com.intel.stl.configuration.AppSettings.APP_DB_SUBNET;
import static com.intel.stl.configuration.MemCacheType.CABLE;
import static com.intel.stl.configuration.MemCacheType.GROUP;
import static com.intel.stl.configuration.MemCacheType.GROUP_CONF;
import static com.intel.stl.configuration.MemCacheType.LFT;
import static com.intel.stl.configuration.MemCacheType.LINK;
import static com.intel.stl.configuration.MemCacheType.MFT;
import static com.intel.stl.configuration.MemCacheType.NODE;
import static com.intel.stl.configuration.MemCacheType.PKEYTABLE;
import static com.intel.stl.configuration.MemCacheType.PM_CONF;
import static com.intel.stl.configuration.MemCacheType.PORT;
import static com.intel.stl.configuration.MemCacheType.SC2SL;
import static com.intel.stl.configuration.MemCacheType.SC2VLNT;
import static com.intel.stl.configuration.MemCacheType.SC2VLT;
import static com.intel.stl.configuration.MemCacheType.SM;
import static com.intel.stl.configuration.MemCacheType.SWITCH;
import static com.intel.stl.configuration.MemCacheType.VLARBTABLE;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.impl.SubnetContextImpl;
import com.intel.stl.api.notice.impl.NoticeProcess;
import com.intel.stl.api.performance.impl.GroupCache;
import com.intel.stl.api.performance.impl.GroupConfCache;
import com.intel.stl.api.performance.impl.PAHelper;
import com.intel.stl.api.performance.impl.PMConfigCache;
import com.intel.stl.api.subnet.impl.CableCache;
import com.intel.stl.api.subnet.impl.LFTCache;
import com.intel.stl.api.subnet.impl.LinkCache;
import com.intel.stl.api.subnet.impl.MFTCache;
import com.intel.stl.api.subnet.impl.NodeCache;
import com.intel.stl.api.subnet.impl.PKeyTableCache;
import com.intel.stl.api.subnet.impl.PortCache;
import com.intel.stl.api.subnet.impl.SAHelper;
import com.intel.stl.api.subnet.impl.SC2SLMTCache;
import com.intel.stl.api.subnet.impl.SC2VLNTMTCache;
import com.intel.stl.api.subnet.impl.SC2VLTMTCache;
import com.intel.stl.api.subnet.impl.SMCache;
import com.intel.stl.api.subnet.impl.SwitchCache;
import com.intel.stl.api.subnet.impl.TopologyUpdateTask;
import com.intel.stl.api.subnet.impl.VLArbTableCache;
import com.intel.stl.common.STLMessages;
import com.intel.stl.datamanager.DatabaseManager;
import com.intel.stl.fecdriver.session.ISession;

public class CacheManagerImpl implements CacheManager {

    // After a topology update task failure, no retries for 5 minutes
    private static final long THROTTLE_TIMEOUT = 300000L;

    private static Logger log = LoggerFactory.getLogger(CacheManagerImpl.class);

    private final SubnetContextImpl subnetContext;

    private final Map<String, ManagedCache> allCaches =
            new ConcurrentHashMap<String, ManagedCache>(10, 0.9f, 1);

    private final Map<MemCacheType, ManagedCache> caches =
            new ConcurrentHashMap<MemCacheType, ManagedCache>(8, 0.9f, 1);

    private final boolean useDB;

    private final AtomicReference<TopologyUpdateTask> topologyUpdateRef;

    private boolean throttleTask = false;

    private long lastErrorTimestamp = 0;

    public CacheManagerImpl(SubnetContextImpl subnetContext) {
        this.subnetContext = subnetContext;
        this.topologyUpdateRef = new AtomicReference<TopologyUpdateTask>(null);
        this.useDB = Boolean.parseBoolean(
                subnetContext.getAppSetting(APP_DB_SUBNET, "true"));
    }

    @Override
    public SerialProcessingService getProcessingService() {
        return subnetContext.getProcessingService();
    }

    @Override
    public DatabaseManager getDatabaseManager() {
        return subnetContext.getDatabaseManager();
    }

    @Override
    public SAHelper getSAHelper() {
        ISession session = subnetContext.getSession();
        return session.getSAHelper();
    }

    @Override
    public PAHelper getPAHelper() {
        ISession session = subnetContext.getSession();
        return session.getPAHelper();
    }

    @Override
    public NodeCache acquireNodeCache() {
        return (NodeCache) getManagedCache(NODE);
    }

    @Override
    public LinkCache acquireLinkCache() {
        return (LinkCache) getManagedCache(LINK);
    }

    @Override
    public PortCache acquirePortCache() {
        return (PortCache) getManagedCache(PORT);
    }

    @Override
    public SwitchCache acquireSwitchCache() {
        return (SwitchCache) getManagedCache(SWITCH);
    }

    @Override
    public LFTCache acquireLFTCache() {
        return (LFTCache) getManagedCache(LFT);
    }

    @Override
    public MFTCache acquireMFTCache() {
        return (MFTCache) getManagedCache(MFT);
    }

    @Override
    public CableCache acquireCableCache() {
        return (CableCache) getManagedCache(CABLE);
    }

    @Override
    public PKeyTableCache acquirePKeyTableCache() {
        return (PKeyTableCache) getManagedCache(PKEYTABLE);
    }

    @Override
    public VLArbTableCache acquireVLArbTableCache() {
        return (VLArbTableCache) getManagedCache(VLARBTABLE);
    }

    @Override
    public SMCache acquireSMCache() {
        return (SMCache) getManagedCache(SM);
    }

    @Override
    public GroupCache acquireGroupCache() {
        return (GroupCache) getManagedCache(GROUP);
    }

    @Override
    public GroupConfCache acquireGroupConfCache() {
        return (GroupConfCache) getManagedCache(GROUP_CONF);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.configuration.CacheManager#getPmConfCache()
     */
    @Override
    public PMConfigCache acquirePMConfigCache() {
        return (PMConfigCache) getManagedCache(PM_CONF);
    }

    @Override
    public SC2SLMTCache acquireSC2SLMTCache() {
        return (SC2SLMTCache) getManagedCache(SC2SL);
    }

    @Override
    public SC2VLTMTCache acquireSC2VLTMTCache() {
        return (SC2VLTMTCache) getManagedCache(SC2VLT);
    }

    @Override
    public SC2VLNTMTCache acquireSC2VLNTMTCache() {
        return (SC2VLNTMTCache) getManagedCache(SC2VLNT);
    }

    @Override
    public void startTopologyUpdateTask() {
        log.info("Starting topology update task");
        startTopologyUpdate();
    }

    /**
     *
     * Description: getManagedCache update memory cache but not DB. It basically
     * retrieves nodes from FE. the TopologyUpdateTask does update DB. It
     * serializes the cache accesses by different threads. acquireNodeCache() ->
     * (NodeCache) getManagedCache(NODE_CACHE) -> MemoryCache.refreshCache() ->
     * NodeCacheImpl.retrieveObjectForCache()
     *
     * @param cacheId
     * @return
     */
    private ManagedCache getManagedCache(MemCacheType cacheType) {
        ManagedCache cache = caches.get(cacheType);
        if (cache == null) {
            throw new RuntimeException(STLMessages.STL30073_NO_CACHE_FOUND
                    .getDescription(cacheType));
        }
        boolean cacheReady = cache.isCacheReady();
        if (!cacheReady) {
            cache.updateCache();
        }
        return cache;
    }

    /**
     *
     * Description: goes through all managed caches to request they update
     * themselves based on the notice information
     *
     * @throws Exception
     *
     */
    @Override
    public void updateCaches(NoticeProcess notice) throws Exception {
        for (ManagedCache cache : allCaches.values()) {
            cache.processNotice(notice);
        }
    }

    public synchronized void initialize() {
        createCaches();
    }

    /**
     *
     * Description: All caches are supposed to be created except DB caches. DB
     * must be synchronized and populated with FM before the DB cahces are
     * created.
     *
     */
    private void createCaches() {
        MemCacheType[] memCaches = MemCacheType.values();

        for (int i = 0; i < memCaches.length; i++) {
            MemCacheType cacheType = memCaches[i];
            String cacheName = cacheType.getImplementingClassName();
            if (allCaches.get(cacheName) == null) {
                try {
                    ManagedCache cache = cacheType.getInstance(this);
                    allCaches.put(cacheName, cache);
                    caches.put(cacheType, cache);
                } catch (Exception e) {
                    String emsg = "Error instantiating cache '" + cacheName
                            + "': " + StringUtils.getErrorMessage(e);
                    log.error(emsg, e);
                    RuntimeException rte = new RuntimeException(emsg, e);
                    throw rte;
                }
            }
        }

        DBCacheType[] dbCaches = DBCacheType.values();
        for (int i = 0; i < dbCaches.length; i++) {
            DBCacheType dbCacheType = dbCaches[i];
            String cacheName = dbCacheType.getImplementingClassName();
            if (allCaches.get(cacheName) == null) {
                try {
                    ManagedCache cache = dbCacheType.getInstance(this);
                    allCaches.put(cacheName, cache);
                } catch (Exception e) {
                    String emsg = "Error instantiating cache '" + cacheName
                            + "': " + StringUtils.getErrorMessage(e);
                    log.error(emsg, e);
                    RuntimeException rte = new RuntimeException(emsg, e);
                    throw rte;
                }
            }
        }
    }

    private void startTopologyUpdate() {

        if (!useDB) {
            log.info(
                    "Topology update task not started because of user setting");
            return;
        }
        TopologyUpdateTask topologyUpdate = topologyUpdateRef.get();
        if (topologyUpdate == null) {

            SAHelper helper = subnetContext.getSession().getSAHelper();
            topologyUpdate =
                    new TopologyUpdateTask(helper, getDatabaseManager());
            boolean updated =
                    topologyUpdateRef.compareAndSet(null, topologyUpdate);
            if (!updated) {
                // Somebody else just set it; let him do the submitting
                return;
            }
        }
        if (topologyUpdate.getFuture() != null
                && !topologyUpdate.getFuture().isDone()) {
            log.warn(
                    "Attempting to start a topology update task but the previous has not finished yet");
            return;
        }
        if (throttleTask) {
            long now = System.currentTimeMillis();
            if ((now - lastErrorTimestamp) < THROTTLE_TIMEOUT) {
                log.warn(
                        "Topology update task was not started due to throttle");
                return;
            }
        }

        getProcessingService().submitSerial(topologyUpdate,
                new ResultHandler<Void>() {

                    @Override
                    public void onTaskCompleted(Future<Void> result) {
                        try {
                            result.get();
                            DBCacheType[] dbCaches = DBCacheType.values();
                            for (int i = 0; i < dbCaches.length; i++) {
                                DBCacheType dbCacheType = dbCaches[i];
                                MemCacheType cacheType =
                                        dbCacheType.getMemCacheType();
                                ManagedCache cache = caches.get(cacheType);
                                if (cache != null && !cache.getClass()
                                        .getCanonicalName().equals(dbCacheType
                                                .getImplementingClassName())) {
                                    ManagedCache dbCache =
                                            allCaches.get(dbCacheType
                                                    .getImplementingClassName());
                                    caches.put(cacheType, dbCache);
                                }
                            }
                            topologyUpdateRef.set(null);
                            throttleTask = false;
                        } catch (InterruptedException e) {
                            log.error("Topology update task was interrupted",
                                    e);
                            topologyUpdateRef.set(null);
                            lastErrorTimestamp = System.currentTimeMillis();
                            throttleTask = true;
                        } catch (ExecutionException e) {
                            log.error(
                                    "Exception caught during topology update task",
                                    e.getCause());
                            topologyUpdateRef.set(null);
                            lastErrorTimestamp = System.currentTimeMillis();
                            throttleTask = true;
                        }
                    }

                });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.configuration.CacheManager#clear()
     */
    @Override
    public void reset() {
        for (ManagedCache cache : caches.values()) {
            cache.reset();
        }
        // replace DB caches with memory caches. The topology update task will
        // replace these memory caches with DB caches after data is ready
        for (DBCacheType dbct : DBCacheType.values()) {
            MemCacheType mct = dbct.getMemCacheType();
            String cacheName = mct.getImplementingClassName();
            ManagedCache cache = allCaches.get(cacheName);
            if (cache != null) {
                cache.reset();
                caches.put(mct, cache);
            } else {
                // shouldn't happen
                log.warn("Cannot find cache '" + cacheName + "'");
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.configuration.CacheManager#cleancup()
     */
    @Override
    public void cleanup() {
        // Nothing to cleanup yet
    }

    // For testing
    protected Map<MemCacheType, ManagedCache> getCaches() {
        return caches;
    }

    protected TopologyUpdateTask getTopologyUpdateTask() {
        return topologyUpdateRef.get();
    }
}
