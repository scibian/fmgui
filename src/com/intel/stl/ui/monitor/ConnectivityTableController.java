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

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.ListSelectionModel;

import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.LinkQuality;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetException;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.GraphEdge;
import com.intel.stl.ui.model.PortProperties;
import com.intel.stl.ui.monitor.ConnectivityTableData.PerformanceData;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.CancellableCall;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.SingleTaskManager;
import com.intel.stl.ui.publisher.Task;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.publisher.subscriber.PortCounterSubscriber;
import com.intel.stl.ui.publisher.subscriber.SubscriberType;
import com.intel.stl.ui.publisher.subscriber.VFPortCounterSubscriber;

public class ConnectivityTableController {

    private final static Logger log =
            LoggerFactory.getLogger(ConnectivityTableController.class);

    private static final boolean TEST_SLOW_LINKS = false;

    private ISubnetApi subnetApi;

    private final ConnectivityTableModel model;

    private final JXTable view;

    private TaskScheduler taskScheduler;

    private final Map<Point, PortSchedule<?>> schedules;

    private final SingleTaskManager taskMgr;

    private int currentLid;

    private String currentVFName;

    private short[] currentPorts;

    private LinkedHashMap<GraphEdge, Short> currentPaths;

    private PortCounterSubscriber portCounterSubscriber;

    private VFPortCounterSubscriber vfPortCounterSubscriber;

    /**
     * Description:
     *
     * @param subnetApi
     */
    public ConnectivityTableController(ConnectivityTableModel model,
            JXTable view) {
        super();
        this.model = model;
        this.view = view;
        schedules = new HashMap<Point, PortSchedule<?>>();
        taskMgr = new SingleTaskManager();
    }

    public void setContext(Context context, IProgressObserver observer) {
        subnetApi = context.getSubnetApi();
        taskScheduler = context.getTaskScheduler();

        // Get the port counter subscriber from the task scheduler
        portCounterSubscriber = (PortCounterSubscriber) taskScheduler
                .getSubscriber(SubscriberType.PORT_COUNTER);
        vfPortCounterSubscriber = (VFPortCounterSubscriber) taskScheduler
                .getSubscriber(SubscriberType.VF_PORT_COUNTER);

        clear();

        if (observer != null) {
            observer.onFinish();
        }
    }

    public synchronized void showConnectivity(int nodeLid, String vfName,
            IProgressObserver observer, short... portList) {
        // If current selected node lid, ports, vf name are all same as previous
        // selected, don't do anything.
        if ((currentPorts != null) && (Arrays.equals(currentPorts, portList))
                && (currentLid == nodeLid)
                && ((currentVFName == null && vfName == null)
                        || (currentVFName != null
                                && currentVFName.equals(vfName)))) {
            return;
        }

        clearScheduledTasks();
        currentLid = nodeLid;
        currentVFName = vfName;
        currentPorts = portList;
        refreshConnectivity(observer);
    }

    public synchronized void refreshConnectivity(
            final IProgressObserver observer) {
        CancellableCall<List<ConnectivityTableData>> caller =
                new CancellableCall<List<ConnectivityTableData>>() {
                    @Override
                    public List<ConnectivityTableData> call(
                            ICancelIndicator cancelIndicator) throws Exception {
                        List<ConnectivityTableData> data =
                                createTable(currentLid, currentVFName,
                                        cancelIndicator, currentPorts);
                        return data;
                    }
                };

        ICallback<List<ConnectivityTableData>> callback =
                new CallbackAdapter<List<ConnectivityTableData>>() {

                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onDone(java
                     * .lang.Object)
                     */
                    @Override
                    public void onDone(List<ConnectivityTableData> result) {
                        if (result != null) {
                            updateTable(result);
                        }
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onError(java.
                     * lang.Throwable[])
                     */
                    @Override
                    public void onError(Throwable... errors) {
                        Util.showErrors(view, Arrays.asList(errors));
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onFinally()
                     */
                    @Override
                    public void onFinally() {
                        if (observer != null) {
                            observer.onFinish();
                        }
                    }

                };
        taskMgr.submit(caller, callback);
    }

    public synchronized void showPathConnectivity(
            LinkedHashMap<GraphEdge, Short> portMap, String vfName,
            IProgressObserver observer) {
        if ((currentPaths != null) && (currentPaths.equals(portMap))
                && ((currentVFName == null && vfName == null)
                        || (currentVFName != null
                                && currentVFName.equals(vfName)))) {
            return;
        }

        clearScheduledTasks();
        currentPaths = portMap;
        currentVFName = vfName;
        refreshPathConnectivity(observer);
    }

    public synchronized void refreshPathConnectivity(
            final IProgressObserver observer) {
        CancellableCall<List<ConnectivityTableData>> caller =
                new CancellableCall<List<ConnectivityTableData>>() {
                    @Override
                    public List<ConnectivityTableData> call(
                            ICancelIndicator cancelIndicator) throws Exception {
                        List<ConnectivityTableData> data = createPathTable(
                                currentPaths, currentVFName, cancelIndicator);
                        return data;
                    }
                };

        ICallback<List<ConnectivityTableData>> callback =
                new CallbackAdapter<List<ConnectivityTableData>>() {

                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onDone(java
                     * .lang.Object)
                     */
                    @Override
                    public void onDone(List<ConnectivityTableData> result) {
                        if (result != null) {
                            updateTable(result);
                        }
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onFinally()
                     */
                    @Override
                    public void onFinally() {
                        if (observer != null) {
                            observer.onFinish();
                        }
                    }
                };
        taskMgr.submit(caller, callback);
    }

    /**
     *
     * Description:
     *
     * @param lid
     *            - node lid
     * @param portList
     *            - port numbers for a Switch, and local port numbers for a HFI
     * @return
     * @throws SubnetException
     * @throws SubnetDataNotFoundException
     */
    protected List<ConnectivityTableData> createTable(int lid, String vfName,
            ICancelIndicator indicator, short... portList)
                    throws SubnetException, SubnetDataNotFoundException {
        // TODO: improve performance by querying all ports once!!
        NodeRecordBean nodeBean = null;
        try {
            nodeBean = subnetApi.getNode(lid);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        if (nodeBean == null) {
            return null;
        }

        List<ConnectivityTableData> dataList =
                new ArrayList<ConnectivityTableData>();
        LinkRecordBean linkBean = null;
        for (short port : portList) {
            if (indicator != null && indicator.isCancelled()) {
                return null;
            }

            boolean isHFI = nodeBean.getNodeType() == NodeType.HFI;
            short portNum = port;
            boolean isActive;
            if (isHFI) {
                // it's very easy we have messed port number and local port
                // number for a HFI. So we do a check here to ensure we have
                // correct port number and local port number for HFI
                port = nodeBean.getNodeInfo().getLocalPortNum();
                portNum = 1;
                isActive = subnetApi.hasLocalPort(lid, port);
            } else {
                isActive = subnetApi.hasPort(lid, port);
            }
            // Add a partial record if the port is inactive
            if (!isActive) {
                ConnectivityTableData nodeData = new ConnectivityTableData(
                        nodeBean.getLid(), nodeBean.getNodeInfo().getNodeGUID(),
                        nodeBean.getNodeType(), port, false);
                nodeData.clear();
                nodeData.setNodeName(nodeBean.getNodeDesc());
                nodeData.setLinkState(STLConstants.K0524_INACTIVE.getValue());
                dataList.add(nodeData);
            } else {
                try {
                    linkBean = subnetApi.getLinkBySource(lid, portNum);
                } catch (Exception e) {
                    e.printStackTrace();
                    continue;
                }

                // Add the data to the list
                PortRecordBean portBean =
                        subnetApi.getPortByPortNum(lid, portNum);
                ConnectivityTableData nodeData = createTableEntry(
                        dataList.size(), linkBean.getFromLID(), vfName, portNum,
                        portBean, linkBean, nodeBean, false);
                if (nodeData != null) {
                    // Update the slow link state
                    nodeData.setSlowLinkState(
                            Utils.isSlowPort(portBean.getPortInfo()));
                    dataList.add(nodeData);
                }

                NodeRecordBean nbrNodeBean = null;
                // Find all the same information for the neighboring node
                linkBean = subnetApi.getLinkByDestination(lid, portNum);
                nbrNodeBean = subnetApi.getNode(linkBean.getFromLID());
                portNum = linkBean.getFromPortIndex();
                portBean = subnetApi.getPortByPortNum(linkBean.getFromLID(),
                        portNum);
                nodeData = createTableEntry(dataList.size(),
                        linkBean.getFromLID(), vfName, portNum, portBean,
                        linkBean, nbrNodeBean, true);
                if (nodeData != null) {
                    // Update the slow link state
                    nodeData.setSlowLinkState(
                            Utils.isSlowPort(portBean.getPortInfo()));
                    dataList.add(nodeData);
                }
            } // else
        } // for

        return dataList;
    }

    /**
     *
     * Description:
     *
     * @param lid
     *            - node lid
     * @param portList
     *            - port numbers for a Switch, and local port numbers for a HFI
     * @return
     * @throws SubnetException
     * @throws SubnetDataNotFoundException
     */
    protected List<ConnectivityTableData> createPathTable(
            LinkedHashMap<GraphEdge, Short> portMap, String vfName,
            ICancelIndicator indicator)
                    throws SubnetException, SubnetDataNotFoundException {
        List<ConnectivityTableData> res =
                new ArrayList<ConnectivityTableData>();
        for (GraphEdge edge : portMap.keySet()) {
            if (indicator != null && indicator.isCancelled()) {
                return null;
            }

            int lid = edge.getFromLid();
            short port = portMap.get(edge);
            List<ConnectivityTableData> tableData =
                    createTable(lid, vfName, indicator, port);
            if (tableData != null) {
                res.addAll(tableData);
            }
        }
        return res;
    }

    /**
     *
     * Description
     *
     * @param lid
     * @param portNum
     *            - port number for a Switch, and local port number for a HFI
     * @param portBean
     * @param linkBean
     * @param nodeBean
     * @return
     */
    @SuppressWarnings("deprecation")
    private ConnectivityTableData createTableEntry(int index, int lid,
            String vfName, short portNum, PortRecordBean portBean,
            LinkRecordBean linkBean, NodeRecordBean nodeBean,
            boolean isNeighbor) {
        ConnectivityTableData nodeData = null;
        if (nodeBean.getNodeType() == NodeType.HFI) {
            portNum = 1; // we use local port number for display
        }

        // TODO This is just a test - remove it!
        if (TEST_SLOW_LINKS) {
            boolean isSwitch3Port7 =
                    (nodeBean.getNodeDesc().equals("MOOSE_STL_SWITCH3"))
                            && (portNum == 7);
            boolean isSwitch0Port7 =
                    (nodeBean.getNodeDesc().equals("MOOSE_STL_SWITCH0"))
                            && (portNum == 7);
            if (isSwitch3Port7 || isSwitch0Port7) {
                portBean.getPortInfo().setLinkSpeedActive((short) 0x80);
            }
        }

        PortProperties portProperties =
                new PortProperties(portBean, nodeBean, linkBean);

        nodeData = new ConnectivityTableData(nodeBean.getLid(),
                nodeBean.getNodeInfo().getNodeGUID(), nodeBean.getNodeType(),
                portNum, isNeighbor);
        nodeData.clear();

        nodeData.setNodeName(nodeBean.getNodeDesc());
        nodeData.setLinkState(portProperties.getState());
        nodeData.setPhysicalLinkState(portProperties.getPhysicalState());

        nodeData.setActiveLinkWidth(portProperties.getLinkWidthActive());
        nodeData.setEnabledLinkWidth(portProperties.getLinkWidthEnabled());
        nodeData.setSupportedLinkWidth(portProperties.getLinkWidthSupported());

        nodeData.setActiveLinkWidthDnGrdTx(
                portProperties.getLinkWidthDnGrdTx());
        nodeData.setActiveLinkWidthDnGrdRx(
                portProperties.getLinkWidthDnGrdRx());
        nodeData.setEnabledLinkWidthDnGrd(
                portProperties.getLinkWidthDnGrdEnabled());
        nodeData.setSupportedLinkWidthDnGrd(
                portProperties.getLinkWidthDnGrdSupported());

        nodeData.setActiveLinkSpeed(portProperties.getLinkSpeedActive());
        nodeData.setEnabledLinkSpeed(portProperties.getLinkSpeedEnabled());
        nodeData.setSupportedLinkSpeed(portProperties.getLinkSpeedSupported());

        // Give the cableInfo a value so the icon will appear
        nodeData.setCableInfo("");

        PortSchedule<?> schedule = null;
        if (vfName == null) {
            schedule =
                    schedulePortPerformanceTask(index, nodeData, lid, portNum);
        } else {
            schedule = scheduleVFPortPerformanceTask(index, nodeData, lid,
                    vfName, portNum);
        }
        schedule.refresh();

        return nodeData;
    }

    protected PortSchedule<PortCountersBean> schedulePortPerformanceTask(
            final int index, final ConnectivityTableData dataEntrty,
            final int lid, final short portNum) {
        ICallback<PortCountersBean> callback =
                new CallbackAdapter<PortCountersBean>() {
                    @Override
                    public synchronized void onDone(PortCountersBean pcBean) {
                        if (pcBean == null) {
                            log.error(STLConstants.K3047_PORT_BEAN_COUNTERS_NULL
                                    .getValue());
                            return;
                        }

                        final PerformanceData perfData = new PerformanceData();
                        perfData.setTxPackets(pcBean.getPortXmitPkts());
                        perfData.setRxPackets(pcBean.getPortRcvPkts());
                        perfData.setNumLinkRecoveries(
                                pcBean.getLinkErrorRecovery());
                        perfData.setNumLinkDown(pcBean.getLinkDowned());
                        perfData.setNumLanesDown(pcBean.getNumLanesDown());
                        perfData.setRxErrors(pcBean.getPortRcvErrors());
                        perfData.setRxRemotePhysicalErrors(
                                pcBean.getPortRcvRemotePhysicalErrors());
                        perfData.setTxDiscards(pcBean.getPortXmitDiscards());
                        perfData.setLocalLinkIntegrityErrors(
                                pcBean.getLocalLinkIntegrityErrors());
                        perfData.setExcessiveBufferOverruns(
                                pcBean.getExcessiveBufferOverruns());
                        perfData.setSwitchRelayErrors(
                                pcBean.getPortRcvSwitchRelayErrors());
                        perfData.setTxConstraints(
                                pcBean.getPortXmitConstraintErrors());
                        perfData.setRxConstraints(
                                pcBean.getPortRcvConstraintErrors());
                        // perfData.setVl15Dropped(???);
                        perfData.setPortRcvData(pcBean.getPortRcvData());
                        perfData.setPortXmitData(pcBean.getPortXmitData());

                        perfData.setFmConfigErrors(pcBean.getFmConfigErrors());
                        perfData.setPortMulticastRcvPkts(
                                pcBean.getPortMulticastRcvPkts());
                        perfData.setPortRcvFECN(pcBean.getPortRcvFECN());
                        perfData.setPortRcvBECN(pcBean.getPortRcvBECN());
                        perfData.setPortRcvBubble(pcBean.getPortRcvBubble());

                        perfData.setPortMulticastXmitPkts(
                                pcBean.getPortMulticastXmitPkts());
                        perfData.setPortXmitWait(pcBean.getPortXmitWait());
                        perfData.setPortXmitTimeCong(
                                pcBean.getPortXmitTimeCong());
                        perfData.setPortXmitWastedBW(
                                pcBean.getPortXmitWastedBW());
                        perfData.setPortXmitWaitData(
                                pcBean.getPortXmitWaitData());
                        perfData.setPortMarkFECN(pcBean.getPortMarkFECN());
                        perfData.setUncorrectableErrors(
                                pcBean.getUncorrectableErrors());
                        perfData.setSwPortCongestion(
                                pcBean.getSwPortCongestion());
                        final byte linkQuality =
                                pcBean.getLinkQualityIndicator();
                        Util.runInEDT(new Runnable() {
                            @Override
                            public void run() {
                                dataEntrty.setPerformanceData(perfData);
                                dataEntrty.setLinkQualityData(linkQuality);
                                if (model.getRowCount() > 0
                                        && index < model.getRowCount()) {
                                    model.fireTableRowsUpdated(index, index);
                                }
                            }
                        });
                    }
                };

        Task<PortCountersBean> task = portCounterSubscriber
                .registerPortCounters(lid, portNum, callback);
        PortSchedule<PortCountersBean> ps =
                new PortSchedule<PortCountersBean>(callback, task) {
                    @Override
                    public void refresh() {
                        PortCountersBean counters =
                                taskScheduler.getPerformanceApi()
                                        .getPortCounters(lid, portNum);
                        callback.onDone(counters);
                    }

                    @Override
                    public void clear() {
                        portCounterSubscriber.deregisterPortCounters(task,
                                callback);
                    }
                };

        synchronized (schedules) {
            schedules.put(new Point(lid, portNum), ps);
        }
        return ps;
    }

    protected PortSchedule<VFPortCountersBean> scheduleVFPortPerformanceTask(
            final int index, final ConnectivityTableData dataEntrty,
            final int lid, final String vfName, final short portNum) {
        ICallback<VFPortCountersBean> callback =
                new CallbackAdapter<VFPortCountersBean>() {
                    @Override
                    public synchronized void onDone(VFPortCountersBean pcBean) {
                        if (pcBean == null) {
                            log.error(STLConstants.K3047_PORT_BEAN_COUNTERS_NULL
                                    .getValue());
                            return;
                        }

                        final PerformanceData perfData = new PerformanceData();
                        perfData.setTxPackets(pcBean.getPortVFXmitPkts());
                        perfData.setRxPackets(pcBean.getPortVFRcvPkts());
                        perfData.setTxDiscards(pcBean.getPortVFXmitDiscards());
                        perfData.setPortRcvData(pcBean.getPortVFRcvData());
                        perfData.setPortXmitData(pcBean.getPortVFXmitData());

                        perfData.setPortRcvFECN(pcBean.getPortVFRcvFECN());
                        perfData.setPortRcvBECN(pcBean.getPortVFRcvBECN());
                        perfData.setPortRcvBubble(pcBean.getPortVFRcvBubble());

                        perfData.setPortXmitWait(pcBean.getPortVFXmitWait());
                        perfData.setPortXmitTimeCong(
                                pcBean.getPortVFXmitTimeCong());
                        perfData.setPortXmitWastedBW(
                                pcBean.getPortVFXmitWastedBW());
                        perfData.setPortXmitWaitData(
                                pcBean.getPortVFXmitWaitData());

                        Util.runInEDT(new Runnable() {
                            @Override
                            public void run() {
                                dataEntrty.setPerformanceData(perfData);
                                dataEntrty.setLinkQualityData(
                                        LinkQuality.UNKNOWN.getValue());
                                if (model.getRowCount() > 0
                                        && index < model.getRowCount()) {
                                    model.fireTableRowsUpdated(index, index);
                                }
                            }
                        });
                    }
                };

        Task<VFPortCountersBean> task = vfPortCounterSubscriber
                .registerVFPortCounters(vfName, lid, portNum, callback);
        PortSchedule<VFPortCountersBean> ps =
                new PortSchedule<VFPortCountersBean>(callback, task) {
                    @Override
                    public void refresh() {
                        VFPortCountersBean counters = taskScheduler
                                .getPerformanceApi()
                                .getVFPortCounters(vfName, lid, portNum);
                        callback.onDone(counters);
                    }

                    @Override
                    public void clear() {
                        vfPortCounterSubscriber.deregisterVFPortCounters(task,
                                callback);
                    }
                };

        synchronized (schedules) {
            schedules.put(new Point(lid, portNum), ps);
        }
        return ps;
    }

    protected synchronized void clearScheduledTasks() {
        synchronized (schedules) {
            for (PortSchedule<?> schedule : schedules.values()) {
                schedule.clear();
            }
            schedules.clear();
        }
    }

    protected void updateTable(final List<ConnectivityTableData> dataList) {
        Map<ConnectivityTableData, Integer> newDataMap =
                new HashMap<ConnectivityTableData, Integer>();
        for (int i = 0; i < dataList.size(); i++) {
            newDataMap.put(dataList.get(i), i);
        }
        int[] selRows = view.getSelectedRows();
        List<Integer> newSelRows = new ArrayList<Integer>();
        for (int i = 0; i < selRows.length; i++) {
            ConnectivityTableData data = model.getEntry(selRows[i]);
            Integer index = newDataMap.get(data);
            if (index != null) {
                newSelRows.add(index);
            }
        }
        model.setEntries(dataList);
        model.fireTableDataChanged();
        view.packAll();
        ListSelectionModel selModel = view.getSelectionModel();
        selModel.setValueIsAdjusting(true);
        for (int row : newSelRows) {
            selModel.addSelectionInterval(row, row);
        }
        selModel.setValueIsAdjusting(false);
    }

    public void clear() {
        clearScheduledTasks();
        model.clear();
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                model.fireTableDataChanged();
            }
        });

    }

    abstract class PortSchedule<E> {
        ICallback<E> callback;

        Task<E> task;

        /**
         * Description:
         *
         * @param callback
         * @param task
         */
        public PortSchedule(ICallback<E> callback, Task<E> task) {
            super();
            this.callback = callback;
            this.task = task;
        }

        public abstract void refresh();

        public abstract void clear();
    }
}
