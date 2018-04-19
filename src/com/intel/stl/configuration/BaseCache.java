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

import java.util.concurrent.atomic.AtomicBoolean;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.impl.NoticeProcess;

/**
 * BaseCache is the base class from which all ManagedCache implementations
 * should extend from. It uses a flag (cacheReady) to direct access to a
 * particular cache; when set to false, all threads attempting to acquire the
 * same cache (through CacheManager) will be directed to the updateCache()
 * method, which is synchronized, effectively queuing up all requests until the
 * update of the cache is completed by the first thread that found the flag set
 * to false is finished. Something similar happens when processing a notice: the
 * flag is set to false and all subsequent requests are queued up until the
 * notice is processed.
 * 
 * 
 */
public abstract class BaseCache implements ManagedCache {

    protected static Logger log = LoggerFactory.getLogger(CacheManager.class);

    protected final AtomicBoolean cacheReady = new AtomicBoolean(false);

    protected CacheManager cacheMgr;

    public BaseCache(CacheManager cacheMgr) {
        this.cacheMgr = cacheMgr;
    }

    @Override
    public boolean isCacheReady() {
        return cacheReady.get();
    }

    @Override
    public void setCacheReady(boolean ready) {
        this.cacheReady.set(ready);
    }

    @Override
    public synchronized void updateCache() {
        if (!cacheReady.get()) {
            boolean success = refreshCache();
            cacheReady.set(success);
        }
    }

    @Override
    public synchronized void processNotice(NoticeProcess notice)
            throws Exception {
        if (!cacheReady.get()) {
            // If cache is not ready, do nothing. Next time cache is acquired,
            // it will get fresh data from the FM
            return;
        }
        boolean cacheStatus = cacheReady.get();
        cacheReady.set(false);
        try {
            // Refresh cache according to notice.
            boolean success = refreshCache(notice);
            if (!success) {
                log.error("Error processing notice in cache "
                        + this.getClass().getSimpleName());
            }
        } catch (Exception e) {
            log.error("Exception processing notice in cache "
                    + this.getClass().getSimpleName(), e);
            throw e;
        } finally {
            // reset whatever cacheStatus was so that other threads can do
            // whatever they were trying to do for this cache.
            cacheReady.set(cacheStatus);
        }
    }

    public abstract boolean refreshCache();

    public abstract boolean refreshCache(NoticeProcess notice) throws Exception;
}
