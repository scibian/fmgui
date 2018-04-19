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

package com.intel.stl.ui.performance;

import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.ChartGroup;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.performance.provider.DataProviderName;

public interface IGroupController<S extends ISource> {
    /**
     * 
     * Description: set Context
     * 
     * @param context
     * @param observer
     */
    void setContext(Context context, IProgressObserver observer);

    /**
     * 
     * <i>Description:</i>
     * 
     * @param observer
     */
    void onRefresh(IProgressObserver observer);

    /**
     * 
     * Description: select one of the registered data providers as the current
     * one
     * 
     * @param name
     */
    void setDataProvider(DataProviderName name);

    /**
     * 
     * Description: Set Device Group or VFabric names this will controller will
     * collect data from.
     * 
     * @param names
     */
    void setDataSources(S[] names);

    void setDisabledDataTypes(DataType defaultType, DataType... types);

    /**
     * 
     * Description: return a ChartGroup that organizes its charts in
     * hierarchical structure
     * 
     * @return the ChartGroup
     */
    ChartGroup getGroup();

    /**
     * 
     * Description: When we are under sleep mode, we deregister all tasks except
     * the one that will be used for sparkline displaying from
     * {@link com.intel.stl.ui.publisher.TaskScheduler}. When we leave sleep
     * mode, all tasks should re-register to
     * {@link com.intel.stl.ui.publisher.TaskScheduler}. This is useful when we
     * want to reduce FE traffic
     * 
     * @param names
     */
    void setSleepMode(boolean b);

    boolean isSleepMode();

    /**
     * <i>Description:</i> set where this GroupController is. So when we undo,
     * we know where to jump back. When we change data source for the
     * GroupConroller, we need to update <code>origin</code> as well, so when we
     * jump back, we point to the right data source
     * 
     * @param origin
     */
    void setOrigin(JumpToEvent origin);
}
