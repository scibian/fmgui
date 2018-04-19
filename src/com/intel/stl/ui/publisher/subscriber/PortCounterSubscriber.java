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
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.publisher.BatchedCallback;
import com.intel.stl.ui.publisher.HistoryQueryTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Subscriber class to schedule tasks for collecting port counter beans
 */
public class PortCounterSubscriber extends Subscriber<PortCountersBean> {
    private static Logger log =
            LoggerFactory.getLogger(PortCounterSubscriber.class);

    private final static boolean DUMP_DATA = false;

    public PortCounterSubscriber(IRegisterTask taskScheduler,
            IPerformanceApi perfApi) {
        super(taskScheduler, perfApi);
    }

    /**
     *
     * <i>Description: Register to receive updates of a single port counter </i>
     *
     * @param lid
     *            local id for the port of interest
     *
     * @param portNum
     *            port number
     *
     * @param callback
     *            method to call once update is complete
     *
     * @return submittedTask task submitted to the task scheduler for processing
     */
    public synchronized Task<PortCountersBean> registerPortCounters(
            final int lid, final short portNum,
            ICallback<PortCountersBean> callback) {
        Task<PortCountersBean> task = new Task<PortCountersBean>(
                PAConstants.STL_PA_ATTRID_GET_PORT_CTRS, lid + ":" + portNum,
                UILabels.STL40010_PORTCOUNTERS_TASK.getDescription(lid,
                        portNum));
        Callable<PortCountersBean> caller = new Callable<PortCountersBean>() {
            @Override
            public PortCountersBean call() throws Exception {
                PortCountersBean portCounters =
                        perfApi.getPortCounters(lid, portNum);
                if (DUMP_DATA) {
                    System.out
                            .println(lid + ":" + portNum + " " + portCounters);
                }
                return portCounters;
            }
        };
        try {
            Task<PortCountersBean> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);
            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    /**
     *
     * <i>Description: De-register a task that receives updates of a single port
     * counter</i>
     *
     * @param task
     *            task to be cancelled
     *
     * @param callback
     *            callback to removed
     */
    public synchronized void deregisterPortCounters(Task<PortCountersBean> task,
            ICallback<PortCountersBean> callback) {

        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    /**
     *
     * <i>Description: Register to receive updates of an array of port
     * counters </i>
     *
     * @param lids
     *            array of local ids for the port of interest
     *
     * @param portNums
     *            array of port number
     *
     * @param callbacks
     *            array of methods to call once update is complete
     *
     * @return submittedTask task submitted to the task scheduler for processing
     */
    public synchronized List<Task<PortCountersBean>> registerPortCountersArray(
            int[] lids, short[] portNums,
            ICallback<PortCountersBean[]> callbacks) {
        List<Task<PortCountersBean>> tasks =
                new ArrayList<Task<PortCountersBean>>();
        int size = lids.length;
        BatchedCallback<PortCountersBean> bCallback =
                new BatchedCallback<PortCountersBean>(size, callbacks,
                        PortCountersBean.class);
        for (int i = 0; i < size; i++) {
            Task<PortCountersBean> task = registerPortCounters(lids[i],
                    portNums[i], bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    public synchronized void deregisterPortCountersArray(
            List<Task<PortCountersBean>> tasks,
            ICallback<PortCountersBean[]> callbacks) {
        try {
            taskScheduler.removeTask(taskList, tasks, callbacks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
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
    public synchronized List<Task<PortCountersBean>> registerPortCounters(
            int lid, List<Short> portNumList,
            ICallback<PortCountersBean[]> callback) {
        List<Task<PortCountersBean>> tasks =
                new ArrayList<Task<PortCountersBean>>();
        BatchedCallback<PortCountersBean> bCallback =
                new BatchedCallback<PortCountersBean>(portNumList.size(),
                        callback, PortCountersBean.class);

        log.info("portNumList.size() = " + portNumList.size());
        for (int i = 0; i < portNumList.size(); i++) {
            Task<PortCountersBean> task = registerPortCounters(lid,
                    portNumList.get(i), bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    /**
     *
     * Description: Initiate Port Counters history with an offset calculated
     * with FV refresh rate and sweep interval. Usually, the refresh rate is
     * higher than sweep interval,so, we just want to do a sampling within a
     * refresh. We would like to return each history for each offset and UI just
     * update data set for JfreeChart.
     *
     * @param lid
     * @param portNumList
     * @param type
     * @param callback
     */
    public Future<Void> initPortCountersHistory(final int lid,
            final List<Short> portNumList, HistoryType type,
            final ICallback<PortCountersBean[]> callback) {
        PMConfigBean conf = perfApi.getPMConfig();
        int sweepInterval = conf.getSweepInterval();
        HistoryQueryTask<PortCountersBean> historyQueryTask =
                new HistoryQueryTask<PortCountersBean>(perfApi, sweepInterval,
                        taskScheduler.getRefreshRate(), type, callback) {

                    @Override
                    protected PortCountersBean[] queryHistory(long[] imageIDs,
                            int offset) {
                        PortCountersBean[] res =
                                new PortCountersBean[portNumList.size()];
                        for (int i = 0; i < portNumList.size(); i++) {
                            PortCountersBean portCounters =
                                    perfApi.getPortCountersHistory(lid,
                                            portNumList.get(i), imageIDs[i],
                                            offset);
                            if (portCounters != null) {
                                if (DUMP_DATA) {
                                    System.out.println(imageIDs[i] + " "
                                            + offset + " " + lid + ":"
                                            + portNumList.get(i)
                                            + portCounters.getTimestampDate()
                                            + " " + portCounters);
                                }
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
                            PortCountersBean portCountersBean =
                                    perfApi.getPortCountersHistory(lid,
                                            portNumList.get(i), 0L, -2);
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
