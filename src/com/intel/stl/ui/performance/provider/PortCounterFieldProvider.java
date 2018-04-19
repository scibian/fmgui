/**
 * Copyright (c) 2016, Intel Corporation
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

import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.ui.performance.PortCounterSourceName;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.subscriber.PortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;

public class PortCounterFieldProvider
        extends SimpleDataProvider<PortCountersBean, PortCounterSourceName> {

    public PortCounterFieldProvider() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#refresh(com.
     * intel.stl.ui.performance.ISource)
     */
    @Override
    protected PortCountersBean refresh(PortCounterSourceName sourceName) {
        return scheduler.getPerformanceApi()
                .getPortCounters(sourceName.getLid(), sourceName.getPortNum());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#registerTask(com
     * .intel.stl.ui.performance.ISource, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Task<PortCountersBean> registerTask(
            PortCounterSourceName sourceName,
            ICallback<PortCountersBean> callback) {
        PortCounterSubscriber subscriber = (PortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.PORT_COUNTER);
        return subscriber.registerPortCounters(sourceName.getLid(),
                sourceName.getPortNum(), callback);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#deregisterTask(
     * com.intel.stl.ui.publisher.Task, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected void deregisterTask(Task<PortCountersBean> task,
            ICallback<PortCountersBean> callback) {
        PortCounterSubscriber subscriber = (PortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.PORT_COUNTER);
        subscriber.deregisterPortCounters(task, callback);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#initHistory(com.
     * intel.stl.ui.performance.ISource, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Future<Void> initHistory(PortCounterSourceName sourceName,
            ICallback<PortCountersBean[]> callback) {
        PortCounterSubscriber subscriber = (PortCounterSubscriber) scheduler
                .getSubscriber(SubscriberType.PORT_COUNTER);
        return subscriber.initPortCountersHistory(sourceName.getLid(),
                Collections.singletonList(sourceName.getPortNum()), historyType,
                callback);
    }

}
