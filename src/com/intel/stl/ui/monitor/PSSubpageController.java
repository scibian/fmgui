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
import java.util.EnumMap;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.ImageIcon;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.api.performance.GroupConfigRspBean;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.VFConfigRspBean;
import com.intel.stl.api.subnet.DefaultDeviceGroup;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.ui.common.ChartsSectionController;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ISectionController;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.event.GroupsSelectedEvent;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.DevicesStatistics;
import com.intel.stl.ui.model.StateSummary;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.PSGraphSectionView;
import com.intel.stl.ui.monitor.view.PSInfoSectionView;
import com.intel.stl.ui.monitor.view.SummarySubpageView;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.VFSource;
import com.intel.stl.ui.performance.provider.DataProviderName;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.IEventFilter;
import com.intel.stl.ui.publisher.IStateChangeListener;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.EventSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;

import net.engio.mbassy.bus.MBassador;

/**
 * Controller for the summary subpage view
 */
public class PSSubpageController
        implements IPerfSubpageController, IStateChangeListener {

    private final SummarySubpageView mSubpageView;

    private ISubnetApi subnetApi;

    private IPerformanceApi perfApi;

    private TaskScheduler mTaskScheduler;

    private final List<ISectionController<?>> mSections;

    private final Map<TreeNodeType, NodeType> deviceTypeMap =
            new HashMap<TreeNodeType, NodeType>();

    private final Map<TreeNodeType, DefaultDeviceGroup> deviceGroupMap =
            new HashMap<TreeNodeType, DefaultDeviceGroup>();

    private PSInfoSection mInfoSectionController;

    private DevicesStatistics dgStats;

    private final Object dgStatsLock = new Object();

    private ChartsSectionController mGraphSectionController;

    private Context mContext;

    private FVResourceNode selectedTreeNode;

    private final MBassador<IAppEvent> eventBus;

    private NodeType mNodeType;

    private Set<Integer> mNodes;

    private boolean isNewContext;

    private EventSubscriber eventSubscriber;

    private ICallback<StateSummary> stateSummaryCallback;

    private Task<StateSummary> stateSummaryTask;

    public PSSubpageController(SummarySubpageView pSubpageView,
            MBassador<IAppEvent> eventBus) {
        mSubpageView = pSubpageView;
        this.eventBus = eventBus;
        mSections = getSections();

        isNewContext = false;

        List<JSectionView<?>> sectionViews = new ArrayList<JSectionView<?>>();
        for (ISectionController<?> section : mSections) {
            sectionViews.add(section.getView());
        }
        mSubpageView.installSectionViews(sectionViews);

        // Initialize the device type and group maps
        deviceTypeMap.put(TreeNodeType.HCA_GROUP, NodeType.HFI);
        deviceTypeMap.put(TreeNodeType.SWITCH_GROUP, NodeType.SWITCH);

        deviceGroupMap.put(TreeNodeType.ALL, DefaultDeviceGroup.ALL);
        deviceGroupMap.put(TreeNodeType.HCA_GROUP, DefaultDeviceGroup.HFI);
        deviceGroupMap.put(TreeNodeType.SWITCH_GROUP, DefaultDeviceGroup.SW);
    }

    protected List<ISectionController<?>> getSections() {
        List<ISectionController<?>> sections =
                new ArrayList<ISectionController<?>>();

        mInfoSectionController =
                new PSInfoSection(new PSInfoSectionView(), eventBus);
        sections.add(mInfoSectionController);

        mGraphSectionController =
                new PSGraphSection(new PSGraphSectionView(), eventBus);
        sections.add(mGraphSectionController);

        return sections;
    }

    @Override
    public void setContext(Context pContext, IProgressObserver observer) {
        clear();
        uninstallEventMonitor();

        isNewContext = mContext == pContext;
        mContext = pContext;
        subnetApi = mContext.getSubnetApi();
        perfApi = mContext.getPerformanceApi();
        mTaskScheduler = mContext.getTaskScheduler();
        installEventMonitor();

        mGraphSectionController.setContext(pContext, observer);
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
    public void onRefresh(IProgressObserver observer) {
        // page refresh comes from re-select a node. This method only applies
        // when we do a local refresh within this subpage. So far, no
        // requirement on this. So the following untested codes are commented
        // out

        // viewClear();
        //
        // if (selectedTreeNode != null) {
        // if (selectedTreeNode.getType() == TreeNodeType.VIRTUAL_FABRIC) {
        // processVFTreeNode(selectedTreeNode, observer);
        // } else {
        // processTreeNode(selectedTreeNode, observer);
        // }
        // }
        //
        // if (observer != null) {
        // observer.setProgress(1);
        // }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        for (ISectionController<?> section : mSections) {
            section.clear();
        }

        selectedTreeNode = null;
    }

    protected void installEventMonitor() {
        mContext.getEvtCal().addListener(this);

        eventSubscriber = (EventSubscriber) mTaskScheduler
                .getSubscriber(SubscriberType.EVENT);
        stateSummaryCallback = new CallbackAdapter<StateSummary>() {
            /*
             * (non-Javadoc)
             *
             * @see
             * com.intel.hpc.stl.ui.publisher.CallBackAdapter#onDone(java.lang
             * .Object)
             */
            @Override
            public synchronized void onDone(StateSummary result) {
                if (result != null) {
                    onStateChange(result);
                }
            }
        };
        stateSummaryTask =
                eventSubscriber.registerStateSummary(stateSummaryCallback);

    }

    protected void uninstallEventMonitor() {
        if (mContext != null && mContext.getEvtCal() != null) {
            mContext.getEvtCal().removeListener(this);
        }
        if (eventSubscriber != null && stateSummaryTask != null) {
            eventSubscriber.deregisterStateSummary(stateSummaryTask,
                    stateSummaryCallback);
        }
    }

    @Override
    public void setParentController(
            PerformanceTreeController parentController) {
    }

    /**
     *
     * Description: Populates the device group statistics and calls the
     * controller to update the screen
     *
     * @param groupList
     *            - list of GroupConfigBeans for the groups listed in the
     *            deviceList
     *
     * @param group
     *            - group config bean
     */
    private void processGroupConfig(List<GroupConfigRspBean> portList,
            long internalPorts, long externalPorts) {
        synchronized (dgStatsLock) {
            dgStats = getDevicesStats(portList);
            dgStats.setInternalPorts(internalPorts);
            dgStats.setExternalPorts(externalPorts);
        }

        // Update the view
        mInfoSectionController.updateStatistics(dgStats);
    }

    private void processVfConfig(List<VFConfigRspBean> portList,
            int internalPorts, int externalPorts) {
        synchronized (dgStatsLock) {
            dgStats = getVFDevicesStats(portList);
            dgStats.setInternalPorts(dgStats.getNumAtivePorts());
        }

        // Update the view
        mInfoSectionController.updateStatistics(dgStats);
    }

    /**
     * Description: Generate DeviceGroupStatistics based on the specified
     * GroupConfigBean
     *
     * @param portList
     *            - a list of port config bean
     *
     * @return devices statistics
     */
    private DevicesStatistics getDevicesStats(
            List<GroupConfigRspBean> portList) {
        DevicesStatistics res = new DevicesStatistics();
        Map<Integer, NodeType> nodeTypes = new HashMap<Integer, NodeType>();
        EnumMap<NodeType, Long> portsTypeDist =
                new EnumMap<NodeType, Long>(NodeType.class);
        EnumMap<NodeType, Integer> nodesTypeDist =
                new EnumMap<NodeType, Integer>(NodeType.class);
        long realNumPorts = 0;
        long desiredTotalPorts = 0;
        for (GroupConfigRspBean port : portList) {
            int lid = port.getPort().getNodeLid();
            NodeType nodeType = nodeTypes.get(lid);
            if (nodeType == null) {
                NodeRecordBean nrb = null;
                try {
                    nrb = subnetApi.getNode(lid);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (nrb == null) {
                    continue;
                }
                nodeType =
                        NodeType.getNodeType(nrb.getNodeInfo().getNodeType());
                nodeTypes.put(lid, nodeType);
                switch (nodeType) {
                    case SWITCH:
                        desiredTotalPorts +=
                                nrb.getNodeInfo().getNumPorts() + 1;
                        break;
                    case HFI:
                        desiredTotalPorts += 1;
                        break;
                    default:
                        break;
                }
                Integer nodesCount = nodesTypeDist.get(nodeType);
                nodesTypeDist.put(nodeType,
                        nodesCount == null ? 1 : nodesCount + 1);
            }
            Long portsCount = portsTypeDist.get(nodeType);
            portsTypeDist.put(nodeType,
                    portsCount == null ? 1 : portsCount + 1);
            realNumPorts += 1;
        } // for
        portsTypeDist.put(NodeType.OTHER, desiredTotalPorts - realNumPorts);

        res.setNodeTypesDist(nodesTypeDist);
        res.setNumNodes(nodeTypes.size());
        res.setPortTypesDist(portsTypeDist);
        res.setNumActivePorts(realNumPorts);
        return res;
    }

    private DevicesStatistics getVFDevicesStats(
            List<VFConfigRspBean> portList) {
        DevicesStatistics res = new DevicesStatistics();
        Map<Integer, NodeType> nodeTypes = new HashMap<Integer, NodeType>();
        EnumMap<NodeType, Long> portsTypeDist =
                new EnumMap<NodeType, Long>(NodeType.class);
        EnumMap<NodeType, Integer> nodesTypeDist =
                new EnumMap<NodeType, Integer>(NodeType.class);
        long realNumPorts = 0;
        long desiredTotalPorts = 0;
        for (VFConfigRspBean port : portList) {
            int lid = port.getPort().getNodeLid();
            NodeType nodeType = nodeTypes.get(lid);
            if (nodeType == null) {
                NodeRecordBean nrb = null;
                try {
                    nrb = subnetApi.getNode(lid);
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                if (nrb == null) {
                    continue;
                }
                nodeType =
                        NodeType.getNodeType(nrb.getNodeInfo().getNodeType());
                nodeTypes.put(lid, nodeType);
                switch (nodeType) {
                    case SWITCH:
                        desiredTotalPorts +=
                                nrb.getNodeInfo().getNumPorts() + 1;
                        break;
                    case HFI:
                        desiredTotalPorts += 1;
                        break;
                    default:
                        break;
                }
                Integer nodesCount = nodesTypeDist.get(nodeType);
                nodesTypeDist.put(nodeType,
                        nodesCount == null ? 1 : nodesCount + 1);
            }
            Long portsCount = portsTypeDist.get(nodeType);
            portsTypeDist.put(nodeType,
                    portsCount == null ? 1 : portsCount + 1);
            realNumPorts += 1;
        } // for
        portsTypeDist.put(NodeType.OTHER, desiredTotalPorts - realNumPorts);

        res.setNodeTypesDist(nodesTypeDist);
        res.setNumNodes(nodeTypes.size());
        res.setPortTypesDist(portsTypeDist);
        res.setNumActivePorts(realNumPorts);
        return res;
    }

    private void processNewEvent(StateSummary summary, NodeType type,
            final Set<Integer> nodes) {
        if (summary == null) {
            return;
        }

        int total = 0;
        EnumMap<NoticeSeverity, Integer> severityMap = null;
        synchronized (dgStatsLock) {
            if (dgStats == null || dgStats.getNodeTypesDist() == null) {
                return;
            }

            EnumMap<NodeType, Integer> currenDist = dgStats.getNodeTypesDist();
            if (currenDist == null) {
                return;
            }

            if (type == NodeType.HFI) {
                total = Math.max(summary.getBaseTotalHFIs(),
                        currenDist.get(NodeType.HFI));
                severityMap = summary.getHfiStates();
            } else if (type == NodeType.SWITCH) {
                total = Math.max(summary.getBaseTotalSWs(),
                        currenDist.get(NodeType.SWITCH));
                severityMap = summary.getSwitchStates();
            } else if (selectedTreeNode != null
                    && selectedTreeNode.getType() == TreeNodeType.ALL) {
                total = Math.max(summary.getBaseTotalNodes(),
                        dgStats.getNumNodes());
                severityMap = summary.getStates(null);
            } else if (nodes != null && !nodes.isEmpty()) {
                total = nodes.size();
                severityMap = summary.getStates(new IEventFilter() {
                    @Override
                    public boolean accept(int nodeLid, NodeType nodeType) {
                        return nodes.contains(nodeLid);
                    }
                });
            }
        }
        mInfoSectionController.updateStates(severityMap, total);
    }

    @Override
    public String getName() {
        // Summary subpage tab has been renamed to Performance
        return STLConstants.K0200_PERFORMANCE.getValue();
    }

    @Override
    public String getDescription() {
        return STLConstants.K0412_SUMMARY_DESCRIPTION.getValue();
    }

    @Override
    public Component getView() {
        return mSubpageView;
    }

    @Override
    public ImageIcon getIcon() {
        return null;
    }

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
     * @see
     * com.intel.stl.ui.common.IPerfSubpageController#showNode(com.intel.stl
     * .ui.monitor.FVResourceNode)
     */
    @Override
    public void showNode(final FVResourceNode treeNode,
            final IProgressObserver observer) {

        if (perfApi == null || treeNode == null) {
            return;
        }

        boolean newNode = !treeNode.equals(selectedTreeNode);
        if (newNode) {
            clear();
            // Capture the selected tree node so the custom states callback
            // knows what kind of severity statistics to update
            selectedTreeNode = treeNode;
        }

        if (treeNode.getType() == TreeNodeType.VIRTUAL_FABRIC) {
            processVFTreeNode(treeNode, observer);
        } else {
            processTreeNode(treeNode, observer);
        }

        if (!newNode) {
            return;
        }
        // FIXME: need to handle synchronization
        mNodeType = getNodeType(treeNode);
        mNodes = mNodeType == null ? getNodes(treeNode) : null;

        if (isNewContext == true) {
            StateSummary summary = mContext.getEvtCal().getSummary();
            if (summary != null) {
                isNewContext = false;
                initEvents(summary);
            }
        }

        mGraphSectionController.setOrigin(new GroupsSelectedEvent(this,
                PerformancePage.NAME, treeNode.getTitle(), treeNode.getType()));
    }

    protected void processTreeNode(FVResourceNode treeNode,
            final IProgressObserver observer) {
        // Calculate node and port distribution across switches and channels
        // for the specified group
        DefaultDeviceGroup dg = deviceGroupMap.get(treeNode.getType());
        final String name = dg != null ? dg.getName() : treeNode.getTitle();
        mTaskScheduler.submitToBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    List<GroupConfigRspBean> group =
                            perfApi.getGroupConfig(name);
                    GroupInfoBean bean = perfApi.getGroupInfo(name);
                    if (group != null && bean != null) {
                        processGroupConfig(group, bean.getNumInternalPorts(),
                                bean.getNumExternalPorts());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    observer.onFinish();
                }
            }
        });

        TreeNodeType type = treeNode.getType();
        if (type == TreeNodeType.ALL) {
            mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                    DataType.EXTERNAL, DataType.TRANSMIT, DataType.RECEIVE);
        } else if (type == TreeNodeType.HCA_GROUP) {
            mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                    DataType.INTERNAL);
        } else if (type == TreeNodeType.DEVICE_GROUP) {
            if (treeNode.getTitle().equals(DefaultDeviceGroup.HFI.getName())) {
                mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                        DataType.INTERNAL);
            } else if (treeNode.getTitle()
                    .equals(DefaultDeviceGroup.ALL.getName())) {
                mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                        DataType.EXTERNAL, DataType.TRANSMIT, DataType.RECEIVE);
            } else {
                mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                        (DataType[]) null);
            }
        } else {
            mGraphSectionController.setDisabledDataTypes(null,
                    (DataType[]) null);
        }
        mGraphSectionController.setDataProvider(DataProviderName.PORT_GROUP);
        mGraphSectionController.setSource(new GroupSource(name));
    }

    protected void processVFTreeNode(FVResourceNode treeNode,
            final IProgressObserver observer) {
        // Calculate node and port distribution across switches and channels
        // for the specified group
        final String name = treeNode.getTitle();
        mTaskScheduler.submitToBackground(new Runnable() {
            @Override
            public void run() {
                try {
                    List<VFConfigRspBean> group = perfApi.getVFConfig(name);
                    if (group != null) {
                        processVfConfig(group, 0, 0);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    observer.onFinish();
                }
            }
        });
        mGraphSectionController.setDisabledDataTypes(DataType.ALL,
                DataType.EXTERNAL, DataType.TRANSMIT, DataType.RECEIVE);
        mGraphSectionController
                .setDataProvider(DataProviderName.VIRTUAL_FABRIC);
        mGraphSectionController.setSource(new VFSource(name));
    }

    protected NodeType getNodeType(FVResourceNode treeNode) {
        switch (treeNode.getType()) {
            case HCA_GROUP:
                return NodeType.HFI;
            case SWITCH_GROUP:
                return NodeType.SWITCH;
            case DEVICE_GROUP:
                DefaultDeviceGroup dg =
                        DefaultDeviceGroup.getType(treeNode.getTitle());
                if (dg != null) {
                    switch (dg) {
                        case SW:
                            return NodeType.SWITCH;
                        case HFI:
                        case TFI:
                            return NodeType.HFI;
                        default:
                            break;
                    }
                }
                break;
            default:
                break;
        }
        return null;
    }

    protected Set<Integer> getNodes(FVResourceNode treeNode) {
        Set<Integer> res = new HashSet<Integer>();
        for (FVResourceNode child : treeNode.getChildren()) {
            res.add(child.getId());
        }
        return res;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    @Override
    public void onStateChange(StateSummary summary) {
        if (summary != null) {
            processNewEvent(summary, mNodeType, mNodes);
        }
    }

    @Override
    public String toString() {
        return "PSSubpageController";
    }

    private void initEvents(StateSummary summary) {
        int total = 1;
        try {
            ISubnetApi subnetApi = mContext.getSubnetApi();
            if (subnetApi != null) {
                total = subnetApi.getNodes(false).size();
            }
        } catch (SubnetDataNotFoundException e) {
            e.printStackTrace();
        }
        EnumMap<NoticeSeverity, Integer> severityMap = null;
        severityMap = summary.getStates(null);
        mInfoSectionController.updateStates(severityMap, total);
    }
}
