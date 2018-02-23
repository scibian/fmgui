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

package com.intel.stl.fecdriver.messages.adapter.pa;

import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.fecdriver.messages.adapter.SimpleDatagram;

/**
 *
 * ref: /ALL_EMB/IbAccess/Common/Inc/stl_pa_types.h commit
 * b0d0c6e7e1803a2416236b3918280b0b3a0d1205 date 2017-07-31 13:52:56
 *
 * <pre>
 * typedef struct _STL_PA_PM_Cfg_Data {
 *     uint32                  sweepInterval;
 *     uint32                  maxClients;
 *     uint32                  sizeHistory;
 *     uint32                  sizeFreeze;
 *     uint32                  lease;
 *     uint32                  pmFlags;
 *     STL_CONGESTION_WEIGHTS_T congestionWeights;
 *     STL_PM_CATEGORY_THRESHOLDS   errorThresholds;
 *     STL_INTEGRITY_WEIGHTS_T integrityWeights;
 *     uint64                  memoryFootprint;
 *     uint32                  maxAttempts;
 *     uint32                  respTimeout;
 *     uint32                  minRespTimeout;
 *     uint32                  maxParallelNodes;
 *     uint32                  pmaBatchSize;
 *     uint8                   errorClear;
 *     uint8                   reserved[3];
 * } PACK_SUFFIX STL_PA_PM_CFG_DATA;
 *
 * typedef struct _STL_CONGESTION_WEIGHTS {
 *     uint8                   PortXmitWait;
 *     uint8                   SwPortCongestion;
 *     uint8                   PortRcvFECN;
 *     uint8                   PortRcvBECN;
 *     uint8                   PortXmitTimeCong;
 *     uint8                   PortMarkFECN;
 *     uint16                  reserved;
 * } PACK_SUFFIX STL_CONGESTION_WEIGHTS_T;
 *
 * typedef struct _STL_PM_CATEGORY_THRESHOLDS {
 *     uint32                  integrityErrors;
 *     uint32                  congestionErrors;
 *     uint32                  smaCongestionErrors;
 *     uint32                  bubbleErrors;
 *     uint32                  securityErrors;
 *     uint32                  routingErrors;
 * } PACK_SUFFIX STL_PM_CATEGORY_THRESHOLDS;
 *
 * typedef struct _STL_INTEGRITY_WEIGHTS {
 *     uint8                   LocalLinkIntegrityErrors;
 *     uint8                   PortRcvErrors;
 *     uint8                   ExcessiveBufferOverruns;
 *     uint8                   LinkErrorRecovery;
 *     uint8                   LinkDowned;
 *     uint8                   UncorrectableErrors;
 *     uint8                   FMConfigErrors;
 *     uint8                   LinkQualityIndicator;
 *     uint8                   LinkWidthDowngrade;
 *     uint8                   reserved[7];
 * } PACK_SUFFIX STL_INTEGRITY_WEIGHTS_T;
 *
 * </pre>
 */
public class PMConfig extends SimpleDatagram<PMConfigBean> {

    public PMConfig() {
        super(104);
    }

    @Override
    public PMConfigBean toObject() {
        buffer.clear();
        PMConfigBean bean = new PMConfigBean();
        bean.setSweepInterval(buffer.getInt());
        bean.setMaxClients(buffer.getInt());
        bean.setSizeHistory(buffer.getInt());
        bean.setSizeFreeze(buffer.getInt());
        bean.setLease(buffer.getInt());
        bean.setPmFlags(buffer.getInt());
        // STL_CONGESTION_WEIGHTS_T
        bean.setPortXmitWait(buffer.get());
        bean.setSwPortCongestion(buffer.get());
        bean.setPortRcvFECN(buffer.get());
        bean.setPortRcvBECN(buffer.get());
        bean.setPortXmitTimeCong(buffer.get());
        bean.setPortMarkFECN(buffer.get());
        // Reserved
        buffer.getShort();
        // _STL_PM_ERR_THRESHOLDS
        bean.setIntegrityErrors(buffer.getInt());
        bean.setCongestionErrors(buffer.getInt());
        bean.setSmaCongestionErrors(buffer.getInt());
        bean.setBubbleErrors(buffer.getInt());
        bean.setSecurityErrors(buffer.getInt());
        bean.setRoutingErrors(buffer.getInt());
        // _STL_INTEGRITY_WEIGHTS
        bean.setLocalLinkIntegrityErrors(buffer.get());
        bean.setPortRcvErrors(buffer.get());
        bean.setExcessiveBufferOverruns(buffer.get());
        bean.setLinkErrorRecovery(buffer.get());
        bean.setLinkDowned(buffer.get());
        bean.setUncorrectableErrors(buffer.get());
        bean.setFmConfigErrors(buffer.get());
        bean.setLinkQualityIndicator(buffer.get());
        bean.setLinkWidthDowngrade(buffer.get());
        // Reserved
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();
        buffer.get();
        //
        bean.setMemoryFootprint(buffer.getLong());
        bean.setMaxAttempts(buffer.getInt());
        bean.setResponseTimeout(buffer.getInt());
        bean.setMinResponseTimeout(buffer.getInt());
        bean.setMaxParallelNodes(buffer.getInt());
        bean.setPmaBatchSize(buffer.getInt());
        bean.setErrorClear(buffer.get());
        // Reserved bytes
        buffer.get();
        buffer.get();
        buffer.get();
        return bean;
    }

}
