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

package com.intel.stl.ui.performance.item;

import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.performance.PortSourceName;
import com.intel.stl.ui.performance.observer.CounterRateObserver;
import com.intel.stl.ui.performance.observer.VFCounterRateObserver;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.performance.provider.PortCountersProvider;
import com.intel.stl.ui.performance.provider.VFPortCountersProvider;

public class RxPktRateItem extends PortCountersItem {

    public RxPktRateItem() {
        this(DEFAULT_DATA_POINTS);
    }

    public RxPktRateItem(int maxDataPoints) {
        super(STLConstants.K0828_REC_PACKETS_RATE.getValue(),
                STLConstants.K0868_SHORT_REC_PKTS_RATE.getValue(),
                STLConstants.K0828_REC_PACKETS_RATE.getValue(), maxDataPoints);
    }

    public RxPktRateItem(RxPktRateItem item) {
        super(item);
    }

    @Override
    protected void copyPreparation(
            AbstractPerformanceItem<PortSourceName> item) {
        PortCountersProvider provider = new PortCountersProvider();
        CounterRateObserver observer = new CounterRateObserver(this) {
            @Override
            protected long getValue(PortCountersBean bean) {
                return bean.getPortRcvPkts();
            }
        };
        registerDataProvider(DataProviderName.PORT, provider, observer);

        VFPortCountersProvider vfProvider = new VFPortCountersProvider();
        VFCounterRateObserver vfObserver = new VFCounterRateObserver(this) {
            @Override
            protected long getValue(VFPortCountersBean bean) {
                return bean.getPortVFRcvPkts();
            }
        };
        registerDataProvider(DataProviderName.VF_PORT, vfProvider, vfObserver);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#copy()
     */
    @Override
    public IPerformanceItem<PortSourceName> copy() {
        return new RxPktRateItem(this);
    }

}
