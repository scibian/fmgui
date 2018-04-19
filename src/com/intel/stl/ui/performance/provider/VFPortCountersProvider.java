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

package com.intel.stl.ui.performance.provider;

import java.util.Collections;
import java.util.concurrent.Future;

import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.performance.PortSourceName;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFPortCounterSubscriber;

public class VFPortCountersProvider
        extends SimpleDataProvider<VFPortCountersBean, PortSourceName> {
    /**
     * Description:
     *
     */
    public VFPortCountersProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#refresh(java
     * .lang.String)
     */
    @Override
    protected VFPortCountersBean refresh(PortSourceName sourceName) {
        return scheduler.getPerformanceApi().getVFPortCounters(
                sourceName.getVfName(), sourceName.getLid(),
                sourceName.getPortNum());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#registerTask
     * (java.lang.String, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Task<VFPortCountersBean> registerTask(PortSourceName sourceName,
            ICallback<VFPortCountersBean> callback) {
        VFPortCounterSubscriber subscriber = (VFPortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.VF_PORT_COUNTER);
        return subscriber.registerVFPortCounters(sourceName.getVfName(),
                sourceName.getLid(), sourceName.getPortNum(), callback);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#deregisterTask
     * (com.intel.stl.ui.publisher.Task, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected void deregisterTask(Task<VFPortCountersBean> task,
            ICallback<VFPortCountersBean> callback) {
        VFPortCounterSubscriber subscriber = (VFPortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.VF_PORT_COUNTER);
        subscriber.deregisterVFPortCounters(task, callback);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#initHistory(
     * java.lang.String[], com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Future<Void> initHistory(PortSourceName sourceName,
            ICallback<VFPortCountersBean[]> callback) {
        VFPortCounterSubscriber subscriber = (VFPortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.VF_PORT_COUNTER);
        return subscriber.initVFPortCountersHistory(sourceName.getVfName(),
                sourceName.getLid(),
                Collections.singletonList(sourceName.getPortNum()), historyType,
                callback);
    }
}
