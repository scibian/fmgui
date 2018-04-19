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

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import net.engio.mbassy.bus.MBassador;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.LinkQuality;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.PerformanceRequestCancelledException;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ISectionController;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.UndoableJumpEvent;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.event.NodesSelectedEvent;
import com.intel.stl.ui.event.PortsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.PerformanceTableModel;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.PerformanceChartsSectionView;
import com.intel.stl.ui.monitor.view.PerformanceView;
import com.intel.stl.ui.monitor.view.PerformanceXTableView;
import com.intel.stl.ui.performance.PortSourceName;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.PortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFPortCounterSubscriber;

/**
 * This class is the controller for the Performance "Node" view which holds the
 * performance table and Tx/Rx packet graphs
 */
public class PerformanceNodeController implements IPerfSubpageController,
        IPortSelectionListener {

    private final static Logger log = LoggerFactory
            .getLogger(PerformanceNodeController.class);

    private final List<ISectionController<?>> sections;

    private PerformanceTableSection tableSection;

    private PerformanceChartsSection chartsSection;

    private final PerformanceView performanceView;

    private Context context;

    private TaskScheduler taskScheduler;

    private final AtomicReference<Future<?>> refreshTask =
            new AtomicReference<Future<?>>(null);

    private List<Task<PortCountersBean>> portCounterTask;

    private ICallback<PortCountersBean[]> portCounterCallback;

    private List<Task<VFPortCountersBean>> vfPortCounterTask;

    private ICallback<VFPortCountersBean[]> vfPortCounterCallback;

    private String vfName;

    private int lid = -1;

    private String nodeDesc;

    private List<Short> portNumList;

    private int previewPortIndex = 0;

    private final MBassador<IAppEvent> eventBus;

    private PortCounterSubscriber portCounterSubscriber;

    private VFPortCounterSubscriber vfPortCounterSubscriber;

    private FVResourceNode currentNode;

    protected UndoHandler undoHandler;

    private final String origin = PerformancePage.NAME;

    public PerformanceNodeController(PerformanceView performanceView,
            MBassador<IAppEvent> eventBus) {
        this.performanceView = performanceView;
        this.eventBus = eventBus;

        sections = getSections();
        List<JSectionView<?>> sectionViews = new ArrayList<JSectionView<?>>();
        for (ISectionController<?> section : sections) {
            sectionViews.add(section.getView());
        }
        performanceView.installSectionViews(sectionViews);
    }

    protected List<ISectionController<?>> getSections() {
        List<ISectionController<?>> sections =
                new ArrayList<ISectionController<?>>();

        final PerformanceTableModel tableModel = new PerformanceTableModel();
        final PerformanceXTableView tableView =
                new PerformanceXTableView(tableModel);
        tableView.setPortSelectionListener(this);
        JSectionView<ISectionListener> tableSectionView =
                new JSectionView<ISectionListener>(
                        STLConstants.K0208_PORTS_TABLE.getValue()) {
                    private static final long serialVersionUID =
                            6166893610476283350L;

                    @Override
                    protected JComponent getMainComponent() {
                        return tableView;
                    }
                };

        tableSection =
                new PerformanceTableSection(tableModel, tableView,
                        tableSectionView, eventBus);
        sections.add(tableSection);

        chartsSection =
                new PerformanceChartsSection(new PerformanceChartsSectionView(
                        UILabels.STL60100_PORT_PREVIEW.getDescription("")),
                        true, eventBus);
        chartsSection.setPinID(PinID.PERF_NODE);
        sections.add(chartsSection);

        return sections;
    }

    public PerformanceView getPerformanceView() {
        return performanceView;
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
        this.context = context;
        if (context == null) {
            return;
        }

        taskScheduler = this.context.getTaskScheduler();

        // Get the port counter subscriber from the task scheduler
        portCounterSubscriber =
                (PortCounterSubscriber) taskScheduler
                        .getSubscriber(SubscriberType.PORT_COUNTER);
        portCounterCallback = new CallbackAdapter<PortCountersBean[]>() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(PortCountersBean[] portCounterBeans) {
                if (portCounterBeans != null) {
                    processPortCounters(portCounterBeans);
                }
            }
        };

        // Get the virtual fabrics port counter subscriber from the task
        // scheduler
        vfPortCounterSubscriber =
                (VFPortCounterSubscriber) taskScheduler
                        .getSubscriber(SubscriberType.VF_PORT_COUNTER);
        vfPortCounterCallback = new CallbackAdapter<VFPortCountersBean[]>() {
            /*
             * (non-Javadoc)
             * 
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(
                    VFPortCountersBean[] portCounterBeans) {
                if (portCounterBeans != null) {
                    processVFPortCounters(portCounterBeans);
                }
            }
        };

        chartsSection.setContext(context, observer);

        if (context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }
        observer.onFinish();
    } // setContext

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(final IProgressObserver observer) {
        Future<?> oldRefreshTask = refreshTask.get();
        Runnable refreshRunnable =
                createRefreshRunnable(vfName, lid,
                        portNumList.toArray(new Short[0]), observer);
        Future<?> newRefreshTask =
                taskScheduler.submitToBackground(refreshRunnable);
        while (!refreshTask.compareAndSet(oldRefreshTask, newRefreshTask)) {
            oldRefreshTask = refreshTask.get();
        }
        if (oldRefreshTask != null && !oldRefreshTask.isDone()) {
            oldRefreshTask.cancel(true);
        }
    }

    @Override
    public void setParentController(PerformanceTreeController parentController) {
    }

    protected Runnable createRefreshRunnable(final String vfName,
            final int lid, final Short[] ports, final IProgressObserver observer) {
        Runnable refreshTask = new Runnable() {
            @Override
            public void run() {
                boolean refreshCancelled = false;
                try {
                    // The observer is not passed to the Charts section because
                    // the
                    // process below is more time consuming
                    chartsSection.onRefresh(null);

                    tableSection.clear();

                    IPerformanceApi perfApi = taskScheduler.getPerformanceApi();
                    if (vfName == null) {
                        PortCountersBean[] res =
                                new PortCountersBean[ports.length];
                        for (int i = 0; i < res.length; i++) {
                            res[i] = perfApi.getPortCounters(lid, ports[i]);
                        }
                        portCounterCallback.onDone(res);
                    } else {
                        VFPortCountersBean[] res =
                                new VFPortCountersBean[ports.length];
                        for (int i = 0; i < res.length; i++) {
                            res[i] =
                                    perfApi.getVFPortCounters(vfName, lid,
                                            ports[i]);
                        }
                        vfPortCounterCallback.onDone(res);
                    }
                } catch (PerformanceRequestCancelledException e) {
                    refreshCancelled = true;
                } finally {
                    if (!refreshCancelled) {
                        observer.onFinish();
                    }
                }
            }
        };
        return refreshTask;
    }

    protected void processPortCounters(PortCountersBean[] beanList) {
        tableSection.updateTable(beanList, previewPortIndex);
        if (previewPortIndex >= 0) {
            PortCountersBean bean = beanList[previewPortIndex];
            if (bean != null) {
                chartsSection.updateLinkQualityIcon(bean
                        .getLinkQualityIndicator());
            } else {
                log.error(UILabels.STL80002_INVALID_PORT_NUMBER
                        .getDescription(previewPortIndex));
            }
        }
    }

    protected void processVFPortCounters(VFPortCountersBean[] beanList) {
        tableSection.updateTable(beanList, previewPortIndex);
        if (previewPortIndex >= 0) {
            VFPortCountersBean bean = beanList[previewPortIndex];
            if (bean != null) {
                chartsSection.updateLinkQualityIcon(LinkQuality.UNKNOWN
                        .getValue());
            } else {
                log.error(UILabels.STL80002_INVALID_PORT_NUMBER
                        .getDescription(previewPortIndex));
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
    public void showNode(final FVResourceNode treeNode,
            final IProgressObserver observer) {
        currentNode = treeNode;
        previewPortIndex = 0;
        if (portCounterTask != null) {
            portCounterSubscriber.deregisterPortCountersArray(portCounterTask,
                    portCounterCallback);
            chartsSection.clear();
        }
        if (vfPortCounterTask != null) {
            vfPortCounterSubscriber.deregisterVFPortCounters(vfPortCounterTask,
                    vfPortCounterCallback);
            chartsSection.clear();
        }

        // Collect all of the port numbers associated with this node
        int lid = treeNode.getId();
        List<Short> portNumList = new ArrayList<Short>();
        for (FVResourceNode portNode : treeNode.getChildren()) {
            if (portNode.getType() == TreeNodeType.ACTIVE_PORT) {
                portNumList.add((short) portNode.getId());
            }
        } // for

        // Clear the performance table
        tableSection.clear();

        // Register for the list of port counter beans associated
        // with the list
        // of port numbers
        FVResourceNode parent = treeNode.getParent();
        String vfName = null;
        if (parent.getType() == TreeNodeType.VIRTUAL_FABRIC) {
            vfName = parent.getTitle();
            vfPortCounterTask =
                    vfPortCounterSubscriber.registerVFPortCounters(vfName, lid,
                            portNumList, vfPortCounterCallback);

        } else {
            portCounterTask =
                    portCounterSubscriber.registerPortCounters(lid,
                            portNumList, portCounterCallback);
        }

        previewPortIndex = 0;
        PortSourceName portSource =
                new PortSourceName(vfName, treeNode.getTitle(), lid,
                        portNumList.get(previewPortIndex));
        chartsSection.setSource(portSource);

        // This is running on the EDT, so no need to synchronize
        this.vfName = vfName;
        this.lid = lid;
        this.nodeDesc = treeNode.getTitle();
        this.portNumList = portNumList;
        onRefresh(observer);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.IPortSelectionListener#onSelect(int)
     */
    @Override
    public void onPortSelection(int rowIndex) {
        if (rowIndex != previewPortIndex) {
            PortSourceName portSource =
                    new PortSourceName(vfName, nodeDesc, lid,
                            portNumList.get(rowIndex));
            chartsSection.setSource(portSource);

            // when we refresh or respond to a notice, StackPanel will remove
            // selections first and then add them back. This will trigger two
            // valueChanged calls. Checking whether currentSelection is null or
            // not allows us ignore the case of removing all selections, i.e.
            // currentSelection is null
            if (rowIndex >= 0) {
                if (undoHandler != null && !undoHandler.isInProgress()) {
                    UndoablePortPreviewSelection undoSel =
                            new UndoablePortPreviewSelection(
                                    tableSection.getTableView(),
                                    previewPortIndex, rowIndex);
                    undoHandler.addUndoAction(undoSel);
                }
            }

            previewPortIndex = rowIndex;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.IPortSelectionListener#onJumpToPort(int)
     */
    @Override
    public void onJumpToPort(int lid, short portNum, String destination) {
        if (eventBus != null) {
            PortsSelectedEvent pse =
                    new PortsSelectedEvent(lid, portNum, this, destination);
            eventBus.publish(pse);

            if (currentNode != null && undoHandler != null
                    && !undoHandler.isInProgress()) {
                NodeType type = TreeNodeType.getNodeType(currentNode.getType());
                UndoableJumpEvent undoSel =
                        new UndoableJumpEvent(eventBus, new NodesSelectedEvent(
                                currentNode.getId(), type, this, origin), pse);
                undoHandler.addUndoAction(undoSel);
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return performanceView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
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
                portCounterSubscriber.deregisterPortCountersArray(
                        portCounterTask, portCounterCallback);
            }
            if (vfPortCounterTask != null) {
                vfPortCounterSubscriber.deregisterVFPortCounters(
                        vfPortCounterTask, vfPortCounterCallback);
            }
        }

        lid = -1;
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
