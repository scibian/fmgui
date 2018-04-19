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

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;

import com.intel.stl.api.configuration.LinkQuality;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ISectionController;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.PerformanceChartsSectionView;
import com.intel.stl.ui.monitor.view.PerformanceErrorsSectionView;
import com.intel.stl.ui.monitor.view.PerformanceView;
import com.intel.stl.ui.performance.PortCounterSourceName;
import com.intel.stl.ui.performance.PortSourceName;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.PortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFPortCounterSubscriber;

import net.engio.mbassy.bus.MBassador;

/**
 * This class is the controller for the Performance "Port" view which holds the
 * Tx/Rx data and packet performance graphs, and error counters
 */
public class PerformancePortController implements IPerfSubpageController {
    private final static boolean DEBUG = true;

    private TaskScheduler taskScheduler;

    @SuppressWarnings("unused")
    private Context context;

    private int lastLid = -1;

    private short lastPortNum = -1;

    private String lastVfName = null;

    private Task<PortCountersBean> portCounterTask;

    private ICallback<PortCountersBean> portCounterCallback;

    private Task<VFPortCountersBean> vfPortCounterTask;

    private ICallback<VFPortCountersBean> vfPortCounterCallback;

    private final List<ISectionController<?>> sections;

    private PerformanceChartsSection graphSection;

    private PerformanceErrorsSection errorsSection;

    private final PerformanceView performancePortView;

    private final MBassador<IAppEvent> eventBus;

    private PortCounterSubscriber portCounterSubscriber;

    private VFPortCounterSubscriber vfPortCounterSubscriber;

    public PerformancePortController(PerformanceView performancePortView,
            MBassador<IAppEvent> eventBus) {
        this.performancePortView = performancePortView;
        this.eventBus = eventBus;
        sections = getSections();
        List<JSectionView<?>> sectionViews = new ArrayList<JSectionView<?>>();
        for (ISectionController<?> section : sections) {
            sectionViews.add(section.getView());
        }
        performancePortView.installSectionViews(sectionViews);
    }

    protected List<ISectionController<?>> getSections() {
        List<ISectionController<?>> sections =
                new ArrayList<ISectionController<?>>();

        graphSection = new PerformanceChartsSection(
                new PerformanceChartsSectionView(
                        STLConstants.K0200_PERFORMANCE.getValue()),
                false, eventBus);
        graphSection.setPinID(PinID.PERF_PORT);
        sections.add(graphSection);

        errorsSection = new PerformanceErrorsSection(
                new PerformanceErrorsSectionView(), eventBus);
        errorsSection.setPinID(PinID.PERF_PORT_ERR);
        sections.add(errorsSection);

        return sections;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#setContext(com.intel.stl.ui.main
     * .Context)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        clear();
        this.context = context;

        taskScheduler = context.getTaskScheduler();

        // Get the port counter subscriber from the task scheduler
        portCounterSubscriber = (PortCounterSubscriber) taskScheduler
                .getSubscriber(SubscriberType.PORT_COUNTER);

        // Get the virtual fabrics port counter subscriber from the task
        // scheduler
        vfPortCounterSubscriber = (VFPortCounterSubscriber) taskScheduler
                .getSubscriber(SubscriberType.VF_PORT_COUNTER);

        graphSection.setContext(context, observer);
        errorsSection.setContext(context, observer);

        observer.onFinish();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(final IProgressObserver observer) {
        taskScheduler.submitToBackground(new Runnable() {
            @Override
            public void run() {
                refresh(observer);
            }
        });
    }

    @Override
    public void setParentController(
            PerformanceTreeController parentController) {
    }

    protected void refresh(IProgressObserver observer) {
        int lid = -1;
        short portNum = -1;
        String vfName = null;
        synchronized (PerformancePortController.this) {
            lid = lastLid;
            portNum = lastPortNum;
            vfName = lastVfName;
        }

        try {
            if (lid == -1 || portNum == -1) {
                return;
            }

            graphSection.onRefresh(observer);
            errorsSection.onRefresh(observer);

            IPerformanceApi perfApi = taskScheduler.getPerformanceApi();
            if (vfName != null) {
                VFPortCountersBean res =
                        perfApi.getVFPortCounters(vfName, lid, portNum);
                vfPortCounterCallback.onDone(res);
            } else {
                PortCountersBean res = perfApi.getPortCounters(lid, portNum);
                portCounterCallback.onDone(res);
            }
        } finally {
            if (observer != null) {
                observer.onFinish();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IPerfSubpageController#showNode(com.intel.stl
     * .ui.monitor.FVResourceNode)
     */
    @Override
    public void showNode(final FVResourceNode node,
            final IProgressObserver observer) {
        // check node type, reject if it's the type we don't support
        if (node.getType() == TreeNodeType.ACTIVE_PORT) {
            taskScheduler.submitToBackground(new Runnable() {
                @Override
                public void run() {
                    try {
                        FVResourceNode parent = node.getParent();
                        int lid = parent.getId();
                        short portNum = (short) node.getId();
                        String vfName = null;
                        FVResourceNode group = parent.getParent();
                        if (group.getType() == TreeNodeType.VIRTUAL_FABRIC) {
                            vfName = group.getTitle();
                        }
                        if (lid != lastLid || portNum != lastPortNum
                                || (vfName == null && lastVfName != null)
                                || ((vfName != null)
                                        && !vfName.equals(lastVfName))) {
                            setPort(parent.getTitle(), lid, portNum, vfName);
                        }
                        refresh(null);
                    } catch (Exception e) {
                        e.printStackTrace();

                    } finally {
                        observer.onFinish();
                    }
                }
            });
        }
    }

    protected synchronized void setPort(String nodeDesc, int lid, short portNum,
            String vfName) {
        if (portCounterTask != null || vfPortCounterTask != null) {
            clear();
        }

        PortSourceName graphPortSource =
                new PortSourceName(vfName, nodeDesc, lid, portNum);
        graphSection.setSource(graphPortSource);

        PortCounterSourceName errPortSource =
                new PortCounterSourceName(vfName, nodeDesc, lid, portNum);
        errorsSection.setSource(errPortSource);

        // register to query PortCounters periodically
        if (vfName != null) {
            vfPortCounterCallback = createCallback(vfName);
            vfPortCounterTask = vfPortCounterSubscriber.registerVFPortCounters(
                    vfName, lid, portNum, vfPortCounterCallback);
        } else {
            portCounterCallback = createCallback();
            portCounterTask = portCounterSubscriber.registerPortCounters(lid,
                    portNum, portCounterCallback);
        }

        lastLid = lid;
        lastPortNum = portNum;
        lastVfName = vfName;
    }

    protected ICallback<PortCountersBean> createCallback() {
        portCounterCallback = new CallbackAdapter<PortCountersBean>() {
            /*
             * (non-Javadoc)
             *
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(PortCountersBean result) {
                if (result != null) {
                    graphSection.updateLinkQualityIcon(
                            result.getLinkQualityIndicator());
                    errorsSection.updateErrors(result);
                }
            }
        };

        return portCounterCallback;

    }

    protected ICallback<VFPortCountersBean> createCallback(String vfName) {
        vfPortCounterCallback = new CallbackAdapter<VFPortCountersBean>() {
            /*
             * (non-Javadoc)
             *
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(VFPortCountersBean result) {
                if (result != null) {
                    graphSection.updateLinkQualityIcon(
                            LinkQuality.UNKNOWN.getValue());
                    errorsSection.updateErrors(result);
                }
            }
        };

        return vfPortCounterCallback;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public PerformanceView getView() {
        return performancePortView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        for (ISectionController<?> section : sections) {
            section.clear();
        }

        if (taskScheduler != null) {
            if (portCounterTask != null) {
                portCounterSubscriber.deregisterPortCounters(portCounterTask,
                        portCounterCallback);
            }
            if (vfPortCounterTask != null) {
                vfPortCounterSubscriber.deregisterVFPortCounters(
                        vfPortCounterTask, vfPortCounterCallback);
            }
        }

        graphSection.clear();
        errorsSection.clear();

        lastLid = lastPortNum = -1;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

}
