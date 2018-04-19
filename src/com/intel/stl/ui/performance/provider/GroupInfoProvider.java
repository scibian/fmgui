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

import java.util.concurrent.Future;

import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.subscriber.GroupInfoSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;

public class GroupInfoProvider extends
        SimpleDataProvider<GroupInfoBean, GroupSource> {

    /**
     * Description:
     * 
     * @param sourceName
     */
    public GroupInfoProvider() {
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
    protected GroupInfoBean refresh(GroupSource sourceName) {
        return scheduler.getPerformanceApi()
                .getGroupInfo(sourceName.getGroup());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.CombinedDataProvider#registerTask
     * (int, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Task<GroupInfoBean> registerTask(GroupSource sourceName,
            ICallback<GroupInfoBean> callback) {

        // Get the group info subscriber from the task scheduler
        GroupInfoSubscriber groupInfoSubscriber =
                (GroupInfoSubscriber) scheduler
                        .getSubscriber(SubscriberType.GROUP_INFO);
        return groupInfoSubscriber.registerGroupInfo(sourceName.getGroup(),
                callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.performance.CombinedDataProvider#deregisterTask
     * (java.util.List, com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected void deregisterTask(Task<GroupInfoBean> task,
            ICallback<GroupInfoBean> callback) {

        // Get the group info subscriber from the task scheduler
        GroupInfoSubscriber groupInfoSubscriber =
                (GroupInfoSubscriber) scheduler
                        .getSubscriber(SubscriberType.GROUP_INFO);
        groupInfoSubscriber.deregisterGroupInfo(task, callback);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.performance.provider.SimpleDataProvider#initHistory(
     * java.lang.String[], com.intel.stl.ui.publisher.ICallback)
     */
    @Override
    protected Future<Void> initHistory(GroupSource sourceName,
            ICallback<GroupInfoBean[]> callback) {
        // Get the group info subscriber from the task scheduler
        GroupInfoSubscriber groupInfoSubscriber =
                (GroupInfoSubscriber) scheduler
                        .getSubscriber(SubscriberType.GROUP_INFO);
        return groupInfoSubscriber.initGroupInfoHistory(
                new String[] { sourceName.getGroup() }, historyType, callback);
    }

}
