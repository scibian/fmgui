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
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.publisher.BatchedCallback;
import com.intel.stl.ui.publisher.HistoryQueryTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Subscriber class to schedule tasks for collecting virtual fabric info beans
 */
public class VFInfoSubscriber extends Subscriber<VFInfoBean> {

    private static Logger log = LoggerFactory.getLogger(VFInfoSubscriber.class);

    public VFInfoSubscriber(IRegisterTask taskScheduler,
            IPerformanceApi perfApi) {
        super(taskScheduler, perfApi);
    }

    public synchronized Task<VFInfoBean> registerVFInfo(final String name,
            ICallback<VFInfoBean> callback) {
        Task<VFInfoBean> task = new Task<VFInfoBean>(
                PAConstants.STL_PA_ATTRID_GET_VF_INFO, name,
                UILabels.STL40009_VFINFO_TASK.getDescription(name));
        Callable<VFInfoBean> caller = new Callable<VFInfoBean>() {
            @Override
            public VFInfoBean call() throws Exception {
                VFInfoBean groupInfo = perfApi.getVFInfo(name);
                // System.out.println("->"+group+" "+groupInfo);
                return groupInfo;
            }
        };
        try {
            Task<VFInfoBean> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);
            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public synchronized void deregisterVFInfo(Task<VFInfoBean> task,
            ICallback<VFInfoBean> callback) {
        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public synchronized List<Task<VFInfoBean>> registerVFInfo(
            final String[] names, ICallback<VFInfoBean[]> callback) {
        List<Task<VFInfoBean>> tasks = new ArrayList<Task<VFInfoBean>>();
        BatchedCallback<VFInfoBean> bCallback = new BatchedCallback<VFInfoBean>(
                names.length, callback, VFInfoBean.class);
        for (int i = 0; i < names.length; i++) {
            Task<VFInfoBean> task =
                    registerVFInfo(names[i], bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    public synchronized void deregisterVFInfo(List<Task<VFInfoBean>> tasks,
            ICallback<VFInfoBean[]> callbacks) {
        try {
            taskScheduler.removeTask(taskList, tasks, callbacks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Future<Void> initVFInfoHistory(final String[] groups,
            HistoryType type, final ICallback<VFInfoBean[]> callback) {
        PMConfigBean conf = perfApi.getPMConfig();
        int sweepInterval = conf.getSweepInterval();
        HistoryQueryTask<VFInfoBean> historyQueryTask =
                new HistoryQueryTask<VFInfoBean>(perfApi, sweepInterval,
                        taskScheduler.getRefreshRate(), type, callback) {

                    @Override
                    protected VFInfoBean[] queryHistory(long[] imageIDs,
                            int offset) {
                        VFInfoBean[] res = new VFInfoBean[groups.length];
                        for (int i = 0; i < groups.length; i++) {
                            VFInfoBean gib = perfApi.getVFInfoHistory(groups[i],
                                    imageIDs[i], offset);
                            if (gib != null) {
                                res[i] = gib;
                            } else {
                                return null;
                            }
                        }
                        return res;
                    }

                    @Override
                    protected ImageIdBean[] queryImageId() {
                        ImageIdBean[] imageIdBeans =
                                new ImageIdBean[groups.length];
                        for (int i = 0; i < groups.length; i++) {
                            VFInfoBean vfInfoBean =
                                    perfApi.getVFInfoHistory(groups[i], 0L, -2);
                            ImageIdBean imageIdBean = vfInfoBean.getImageId();

                            imageIdBeans[i] = imageIdBean;
                        }

                        return imageIdBeans;
                    }
                };
        return submitHistoryQueryTask(historyQueryTask);
    }

}
