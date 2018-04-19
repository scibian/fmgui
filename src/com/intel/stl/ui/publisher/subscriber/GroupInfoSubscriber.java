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

import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.publisher.BatchedCallback;
import com.intel.stl.ui.publisher.HistoryQueryTask;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;

/**
 * Subscriber class to schedule tasks for collecting group info beans
 */
public class GroupInfoSubscriber extends Subscriber<GroupInfoBean> {
    private final static boolean DUMP_DATA = false;

    private static Logger log =
            LoggerFactory.getLogger(GroupInfoSubscriber.class);

    public GroupInfoSubscriber(IRegisterTask taskScheduler,
            IPerformanceApi perfApi) {
        super(taskScheduler, perfApi);
    }

    public synchronized Task<GroupInfoBean> registerGroupInfo(
            final String group, ICallback<GroupInfoBean> callback) {
        Task<GroupInfoBean> task = new Task<GroupInfoBean>(
                PAConstants.STL_PA_ATTRID_GET_GRP_INFO, group,
                UILabels.STL40008_GROUPINFO_TASK.getDescription(group));
        Callable<GroupInfoBean> caller = new Callable<GroupInfoBean>() {
            @Override
            public GroupInfoBean call() throws Exception {
                GroupInfoBean groupInfo = perfApi.getGroupInfo(group);
                if (DUMP_DATA) {
                    System.out.println(group + " " + groupInfo);
                }
                return groupInfo;
            }
        };
        try {
            // Task<GroupInfoBean> res =
            // registerTask(groupInfoTasks, task, callback, caller);
            Task<GroupInfoBean> submittedTask = taskScheduler
                    .scheduleTask(taskList, task, callback, caller);

            return submittedTask;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return null;
        }
    }

    public synchronized void deregisterGroupInfo(Task<GroupInfoBean> task,
            ICallback<GroupInfoBean> callback) {
        try {
            taskScheduler.removeTask(taskList, task, callback);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public synchronized List<Task<GroupInfoBean>> registerGroupInfo(
            final String[] groups, ICallback<GroupInfoBean[]> callback) {
        List<Task<GroupInfoBean>> tasks = new ArrayList<Task<GroupInfoBean>>();
        BatchedCallback<GroupInfoBean> bCallback =
                new BatchedCallback<GroupInfoBean>(groups.length, callback,
                        GroupInfoBean.class);
        for (int i = 0; i < groups.length; i++) {
            Task<GroupInfoBean> task =
                    registerGroupInfo(groups[i], bCallback.getCallback(i));
            tasks.add(task);
        }
        return tasks;
    }

    public synchronized void deregisterGroupInfo(
            List<Task<GroupInfoBean>> tasks,
            ICallback<GroupInfoBean[]> callbacks) {
        try {
            taskScheduler.removeTask(taskList, tasks, callbacks);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public Future<Void> initGroupInfoHistory(final String[] groups,
            HistoryType type, ICallback<GroupInfoBean[]> callback) {
        PMConfigBean conf = perfApi.getPMConfig();
        int sweepInterval = conf.getSweepInterval();
        HistoryQueryTask<GroupInfoBean> historyQueryTask =
                new HistoryQueryTask<GroupInfoBean>(perfApi, sweepInterval,
                        taskScheduler.getRefreshRate(), type, callback) {

                    @Override
                    protected GroupInfoBean[] queryHistory(long[] imageIDs,
                            int offset) {
                        GroupInfoBean[] res = new GroupInfoBean[groups.length];
                        for (int i = 0; i < groups.length; i++) {
                            GroupInfoBean gib = perfApi.getGroupInfoHistory(
                                    groups[i], imageIDs[i], offset);
                            if (gib != null) {
                                res[i] = gib;
                                if (DUMP_DATA) {
                                    System.out.println(imageIDs[i] + " "
                                            + offset + " " + groups[i] + " "
                                            + gib.getTimestamp() + " "
                                            + gib.getTimestampDate() + gib);
                                }
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
                            GroupInfoBean groupInfoBean = perfApi
                                    .getGroupInfoHistory(groups[i], 0L, -2);
                            ImageIdBean imageIdBean =
                                    groupInfoBean.getImageId();

                            imageIdBeans[i] = imageIdBean;
                        }

                        return imageIdBeans;
                    }
                };
        return submitHistoryQueryTask(historyQueryTask);
    }
}
