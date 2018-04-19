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

package com.intel.stl.ui.monitor;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.subnet.DefaultDeviceGroup;
import com.intel.stl.ui.common.ChartsSectionController;
import com.intel.stl.ui.common.view.ChartsSectionView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.performance.BaseGroupFactory;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.IGroupController;

public class PSGraphSection extends ChartsSectionController {
    private IGroupController<GroupSource>[] utilGroups;

    private IGroupController<GroupSource>[] errGroups;

    public PSGraphSection(ChartsSectionView view, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseSectionController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getGeneralSummary();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.ChartsSectionController#getUtilGroups()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected IGroupController<GroupSource>[] getUtilGroups() {
        if (utilGroups == null) {
            GroupSource[] source =
                    new GroupSource[] { new GroupSource(
                            DefaultDeviceGroup.ALL.getName()) };
            utilGroups =
                    new IGroupController[] {
                            BaseGroupFactory.createBandwidthGroup(eventBus,
                                    topN, DataType.ALL, HistoryType.CURRENT,
                                    source),
                            BaseGroupFactory.createPacketRateGroup(eventBus,
                                    topN, DataType.ALL, HistoryType.CURRENT,
                                    source) };
        }
        return utilGroups;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.main.ChartsSectionController#getErrorGroups()
     */
    @SuppressWarnings("unchecked")
    @Override
    protected IGroupController<GroupSource>[] getErrorGroups() {
        if (errGroups == null) {
            GroupSource[] source =
                    new GroupSource[] { new GroupSource(
                            DefaultDeviceGroup.ALL.getName()) };
            errGroups =
                    new IGroupController[] {
                            BaseGroupFactory.createCongestionGroup(eventBus,
                                    topN, DataType.ALL, HistoryType.CURRENT,
                                    source),
                            BaseGroupFactory.createSmaCongestionGroup(eventBus,
                                    topN, DataType.ALL, HistoryType.CURRENT,
                                    source),
                            BaseGroupFactory.createSignalIntegrityGroup(
                                    eventBus, topN, DataType.ALL,
                                    HistoryType.CURRENT, source),
                            BaseGroupFactory.createBubbleGroup(eventBus, topN,
                                    DataType.ALL, HistoryType.CURRENT, source),
                            BaseGroupFactory.createSecurityGroup(eventBus,
                                    topN, DataType.ALL, HistoryType.CURRENT,
                                    source),
                            BaseGroupFactory.createRoutingGroup(eventBus, topN,
                                    DataType.ALL, HistoryType.CURRENT, source) };
        }
        return errGroups;
    }
}
