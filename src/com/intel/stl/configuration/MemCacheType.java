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

import com.intel.stl.api.performance.impl.GroupCacheImpl;
import com.intel.stl.api.performance.impl.GroupConfCacheImpl;
import com.intel.stl.api.performance.impl.PMConfigCacheImpl;
import com.intel.stl.api.subnet.impl.CableCacheImpl;
import com.intel.stl.api.subnet.impl.LFTCacheImpl;
import com.intel.stl.api.subnet.impl.LinkCacheImpl;
import com.intel.stl.api.subnet.impl.MFTCacheImpl;
import com.intel.stl.api.subnet.impl.NodeCacheImpl;
import com.intel.stl.api.subnet.impl.PKeyTableCacheImpl;
import com.intel.stl.api.subnet.impl.PortCacheImpl;
import com.intel.stl.api.subnet.impl.SC2SLMTCacheImpl;
import com.intel.stl.api.subnet.impl.SC2VLNTMTCacheImpl;
import com.intel.stl.api.subnet.impl.SC2VLTMTCacheImpl;
import com.intel.stl.api.subnet.impl.SMCacheImpl;
import com.intel.stl.api.subnet.impl.SwitchCacheImpl;
import com.intel.stl.api.subnet.impl.VLArbTableCacheImpl;

/**
 * This enumeration is used by CacheManager to create all the caches used in the
 * application. Please note all cache implementations must have a constructor
 * that accepts a CacheManager as unique parameter; using this parameter, you
 * should be able to get references to the DatabaseManager, the
 * ProcessingService and the helpers that you might need in the cache
 * implementation.
 */
public enum MemCacheType {

    NODE(NodeCacheImpl.class),

    LINK(LinkCacheImpl.class),

    PORT(PortCacheImpl.class),

    SWITCH(SwitchCacheImpl.class),

    LFT(LFTCacheImpl.class),

    MFT(MFTCacheImpl.class),

    CABLE(CableCacheImpl.class),

    PKEYTABLE(PKeyTableCacheImpl.class),

    VLARBTABLE(VLArbTableCacheImpl.class),

    SM(SMCacheImpl.class),

    GROUP(GroupCacheImpl.class),

    GROUP_CONF(GroupConfCacheImpl.class),

    PM_CONF(PMConfigCacheImpl.class),

    SC2SL(SC2SLMTCacheImpl.class),

    SC2VLT(SC2VLTMTCacheImpl.class),

    SC2VLNT(SC2VLNTMTCacheImpl.class);

    private final Class<? extends ManagedCache> implementingClass;

    private MemCacheType(Class<? extends ManagedCache> implementingClass) {
        this.implementingClass = implementingClass;
    }

    public String getImplementingClassName() {
        return implementingClass.getCanonicalName();
    }

    public ManagedCache getInstance(CacheManager cacheManager)
            throws Exception {
        ManagedCache cache = implementingClass
                .getConstructor(CacheManager.class).newInstance(cacheManager);
        return cache;
    }
}
