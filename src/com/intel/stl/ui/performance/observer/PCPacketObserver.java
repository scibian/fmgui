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

package com.intel.stl.ui.performance.observer;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.performance.IPortCountersProcessor;
import com.intel.stl.ui.performance.PortCounterSourceName;
import com.intel.stl.ui.performance.item.PortCounterFieldItem;

public class PCPacketObserver
        extends AbstractDataObserver<PortCountersBean, PortCounterFieldItem>
        implements IFieldObserver {

    private final Map<String, IPortCountersProcessor> processorMap =
            new HashMap<String, IPortCountersProcessor>();

    private String field;

    /**
     * Description:
     *
     * @param controller
     */
    public PCPacketObserver(PortCounterFieldItem controller) {
        super(controller);
        initProcessorMap();
    }

    private void initProcessorMap() {
        processorMap.put(STLConstants.K0728_RX_CUMULATIVE_PACKETS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvPkts();
                    }
                });

        processorMap.put(STLConstants.K0834_RX_MULTICAST_PACKETS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortMulticastRcvPkts();
                    }
                });

        processorMap.put(STLConstants.K0734_TX_CUMULATIVE_PACKETS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitPkts();
                    }
                });

        processorMap.put(STLConstants.K0833_TX_MULTICAST_PACKETS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortMulticastXmitPkts();
                    }
                });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.observer.IDataObserver#processData(java.
     * lang.Object)
     */
    @Override
    public synchronized void processData(PortCountersBean bean) {
        if (bean == null) {
            return;
        }

        PortCounterSourceName sourceName = new PortCounterSourceName(
                bean.getNodeLid(), bean.getPortNumber(), field);
        Date time = bean.getTimestampDate();
        int interval = bean.getImageInterval();
        if (field != null) {
            IPortCountersProcessor processor = processorMap.get(field);
            if (processor != null) {
                controller.updateTrend(processor.getValue(bean), time, interval,
                        sourceName);
            }
        }
    }

    @Override
    public synchronized void setFieldName(String name) {
        field = name;
    }
}
