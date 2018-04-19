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

package com.intel.stl.ui.publisher.subscriber;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.publisher.BatchedCallback;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.HistoryQueryTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Subscriber class to schedule tasks for collecting virtual fabric port counter
 * beans
 */
public class VFPortCounterSubscriber extends Subscriber<VFPortCountersBean> {

    private static Logger log =
            LoggerFactory.getLogger(PortCounterSubscriber.class);

    public VFPortCounterSubscriber(IRegisterTask taskScheduler,
            IPerformanceApi perfApi) {
        super(taskScheduler, perfApi);
    }

    public synchronized Task<VFPortCountersBean> registerVFPortCounters(
            final String vfName, final int lid, final short portNum,
            ICallback<VFPortCountersBean> callback) {
        Task<VFPortCountersBean> task = new Task<VFPortCountersBean>(
                PAConstants.STL_PA_ATTRID_GET_VF_PORT_CTRS,
                vfName + ":" + lid + ":" + portNum,
                UILabels.STL40011_VFPORTCOUNTERS_TASK.getDescription(lid,
                        portNum));
        Callable<VFPortCountersBean> caller =
                new Callable<VFPortCountersBean>() {
                    @Override
                    public VFPortCountersBean call() throws Exception {
                        VFPortCountersBean portCounters =
                                perfApi.getVFPortCounters(vfName, lid, portNum);
                        return portCounters;
                    }
                };
        try {
            Task<VFPortCountersBean> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);

            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public synchronized void deregisterVFPortCounters(
            Task<VFPortCountersBean> task,
            ICallback<VFPortCountersBean> callback) {
        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public synchronized List<Task<VFPortCountersBean>> registerVFPortCounters(
            String vfName, int[] lids, short[] portNums,
            ICallback<VFPortCountersBean[]> callback) {
        List<Task<VFPortCountersBean>> tasks =
                new ArrayList<Task<VFPortCountersBean>>();
        int size = lids.length;
        BatchedCallback<VFPortCountersBean> bCallback =
                new BatchedCallback<VFPortCountersBean>(size, callback,
                        VFPortCountersBean.class);
        for (int i = 0; i < size; i++) {
            Task<VFPortCountersBean> task = registerVFPortCounters(vfName,
                    lids[i], portNums[i], bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    /**
     *
     * Description: This method returns an array of PortCounterBeans for the
     * node specified by lid and the list ports.
     *
     * @param lid
     *            - lid for a specific node
     *
     * @param portNumList
     *            - list of port numbers associated with lid
     *
     * @param rateInSeconds
     *            - rate at which to invoke the callback
     *
     * @param callback
     *            - method to call
     *
     * @return - array of PortCounterBeans associated with specified node
     */
    public synchronized List<Task<VFPortCountersBean>> registerVFPortCounters(
            String vfName, int lid, List<Short> portNumList,
            ICallback<VFPortCountersBean[]> callback) {
        List<Task<VFPortCountersBean>> tasks =
                new ArrayList<Task<VFPortCountersBean>>();
        BatchedCallback<VFPortCountersBean> bCallback =
                new BatchedCallback<VFPortCountersBean>(portNumList.size(),
                        callback, VFPortCountersBean.class);
        for (int i = 0; i < portNumList.size(); i++) {
            Task<VFPortCountersBean> task = registerVFPortCounters(vfName, lid,
                    portNumList.get(i), bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    public synchronized void deregisterVFPortCounters(
            List<Task<VFPortCountersBean>> tasks,
            ICallback<VFPortCountersBean[]> callbacks) {
        try {
            taskScheduler.removeTask(taskList, tasks, callbacks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Future<Void> initVFPortCountersHistory(final String vfName,
            final int lid, final short portNum, HistoryType type,
            final ICallback<VFPortCountersBean> callback) {
        PMConfigBean conf = perfApi.getPMConfig();
        int sweepInterval = conf.getSweepInterval();
        HistoryQueryTask<VFPortCountersBean> historyQueryTask =
                new HistoryQueryTask<VFPortCountersBean>(perfApi, sweepInterval,
                        taskScheduler.getRefreshRate(), type,
                        CallbackAdapter.asArrayCallbak(callback)) {

                    @Override
                    protected VFPortCountersBean[] queryHistory(long[] imageIDs,
                            int offset) {
                        VFPortCountersBean portCounters =
                                perfApi.getVFPortCountersHistory(vfName, lid,
                                        portNum, imageIDs[0], offset);
                        return new VFPortCountersBean[] { portCounters };
                    }

                    @Override
                    protected ImageIdBean[] queryImageId() {
                        VFPortCountersBean portCountersBean =
                                perfApi.getVFPortCountersHistory(vfName, lid,
                                        portNum, 0L, -2);
                        ImageIdBean imageIdBean = portCountersBean.getImageId();

                        return new ImageIdBean[] { imageIdBean };

                    }

                };
        return submitHistoryQueryTask(historyQueryTask);
    }

    public Future<Void> initVFPortCountersHistory(final String vfName,
            final int lid, final List<Short> portNumList, HistoryType type,
            final ICallback<VFPortCountersBean[]> callback) {
        PMConfigBean conf = perfApi.getPMConfig();
        int sweepInterval = conf.getSweepInterval();
        HistoryQueryTask<VFPortCountersBean> historyQueryTask =
                new HistoryQueryTask<VFPortCountersBean>(perfApi, sweepInterval,
                        taskScheduler.getRefreshRate(), type, callback) {

                    @Override
                    protected VFPortCountersBean[] queryHistory(long[] imageIDs,
                            int offset) {
                        VFPortCountersBean[] res =
                                new VFPortCountersBean[portNumList.size()];
                        for (int i = 0; i < portNumList.size(); i++) {
                            VFPortCountersBean portCounters =
                                    perfApi.getVFPortCountersHistory(vfName,
                                            lid, portNumList.get(i),
                                            imageIDs[i], offset);
                            if (portCounters != null) {
                                res[i] = portCounters;
                            } else {
                                return null;
                            }
                        }
                        return res;
                    }

                    @Override
                    protected ImageIdBean[] queryImageId() {
                        ImageIdBean[] imageIdBeans =
                                new ImageIdBean[portNumList.size()];
                        for (int i = 0; i < portNumList.size(); i++) {
                            VFPortCountersBean portCountersBean =
                                    perfApi.getVFPortCountersHistory(vfName,
                                            lid, portNumList.get(i), 0L, -2);
                            ImageIdBean imageIdBean =
                                    portCountersBean.getImageId();

                            imageIdBeans[i] = imageIdBean;
                        }

                        return imageIdBeans;
                    }

                };
        return submitHistoryQueryTask(historyQueryTask);
    }

}
