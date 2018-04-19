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

public class PCErrObserver
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
    public PCErrObserver(PortCounterFieldItem controller) {
        super(controller);
        initProcessorMap();

    }

    private void initProcessorMap() {
        // Receive
        processorMap.put(STLConstants.K0519_RX_ERRORS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvErrors();
                    }
                });
        processorMap.put(STLConstants.K0522_RX_PORT_CONSTRAINT.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvConstraintErrors();
                    }
                });
        processorMap.put(STLConstants.K0717_REC_SW_REL_ERR.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvSwitchRelayErrors();
                    }
                });
        processorMap.put(STLConstants.K0520_RX_REMOTE_PHY_ERRORS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvRemotePhysicalErrors();
                    }
                });
        processorMap.put(STLConstants.K0837_RX_FECN.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvFECN();
                    }
                });
        processorMap.put(STLConstants.K0838_RX_BECN.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvBECN();
                    }
                });
        processorMap.put(STLConstants.K0842_RX_BUBBLE.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortRcvBubble();
                    }
                });

        // Transmit
        processorMap.put(STLConstants.K0714_TRAN_DISCARDS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitDiscards();
                    }
                });
        processorMap.put(STLConstants.K0521_TX_PORT_CONSTRAINT.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitConstraintErrors();
                    }
                });
        processorMap.put(STLConstants.K0836_TX_WAIT.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitWait();
                    }
                });
        processorMap.put(STLConstants.K0839_TX_TIME_CONG.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitTimeCong();
                    }
                });
        processorMap.put(STLConstants.K0840_TX_WASTED_BW.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitWastedBW();
                    }
                });
        processorMap.put(STLConstants.K0841_TX_WAIT_DATA.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortXmitWaitData();
                    }
                });

        // Others
        processorMap.put(STLConstants.K0718_LOCAL_LINK_INTEG_ERR.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getLocalLinkIntegrityErrors();
                    }
                });
        processorMap.put(STLConstants.K0720_FM_CONFIG_ERR.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getFmConfigErrors();
                    }
                });
        processorMap.put(STLConstants.K0719_EXCESS_BUFF_OVERRUNS.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getExcessiveBufferOverruns();
                    }
                });
        processorMap.put(STLConstants.K0835_SW_PORT_CONG.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getSwPortCongestion();
                    }
                });
        processorMap.put(STLConstants.K0843_MARK_FECN.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getPortMarkFECN();
                    }
                });
        processorMap.put(STLConstants.K0517_LINK_RECOVERIES.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getLinkErrorRecovery();
                    }
                });
        processorMap.put(STLConstants.K0518_LINK_DOWN.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getLinkDowned();
                    }
                });
        processorMap.put(STLConstants.K0716_UNCORR_ERR.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getUncorrectableErrors();
                    }
                });
        processorMap.put(STLConstants.K2068_LINK_QUALITY.getValue(),
                new IPortCountersProcessor() {
                    @Override
                    public long getValue(PortCountersBean bean) {
                        return bean.getLinkQualityIndicator();
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
