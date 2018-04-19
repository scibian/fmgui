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

import java.util.List;
import java.util.concurrent.Future;

import com.intel.stl.api.performance.VFFocusPortsRspBean;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFFocusPortSubscriber;

public class VFFocusPortProvider extends
        SimpleDataProvider<List<VFFocusPortsRspBean>, GroupSource> {
    private final Selection selection;

    private final int range;

    /**
     * Description:
     * 
     * @param groupName
     * @param selection
     */
    public VFFocusPortProvider(Selection selection, int range) {
        super();
        this.selection = selection;
        this.range = range;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#refresh(java
     * .lang.String)
     */
    @Override
    protected List<VFFocusPortsRspBean> refresh(GroupSource sourceName) {
        return scheduler.getPerformanceApi().getVFFocusPorts(
                sourceName.getGroup(), selection, range);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.SimpleDataProvider#registerTask(int,
     * com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Task<List<VFFocusPortsRspBean>> registerTask(
            GroupSource sourceName,
            ICallback<List<VFFocusPortsRspBean>> callback) {

        // Get the virtual fabrics focus port counter subscriber from the
        // task scheduler
        VFFocusPortSubscriber vfFocusPortSubscriber =
                (VFFocusPortSubscriber) scheduler
                        .getSubscriber(SubscriberType.VF_FOCUS_PORTS);

        return vfFocusPortSubscriber.registerVFFocusPorts(
                sourceName.getGroup(), selection, range, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.SimpleDataProvider#deregisterTask
     * (com.intel.stl.ui.publisher.Task, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected void deregisterTask(Task<List<VFFocusPortsRspBean>> task,
            ICallback<List<VFFocusPortsRspBean>> callback) {

        // Get the virtual fabrics focus port counter subscriber from the
        // task scheduler
        VFFocusPortSubscriber vfFocusPortSubscriber =
                (VFFocusPortSubscriber) scheduler
                        .getSubscriber(SubscriberType.VF_FOCUS_PORTS);

        vfFocusPortSubscriber.deregisterVFFocusPorts(task, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#initHistory(
     * java.lang.String, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Future<Void> initHistory(GroupSource sourceName,
            ICallback<List<VFFocusPortsRspBean>[]> callback) {
        throw new UnsupportedOperationException();
    }

}
