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

package com.intel.stl.ui.monitor.tree;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IContextAware;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.TimeDrivenProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.TreeSearchType;
import com.intel.stl.ui.monitor.TreeTypeEnum;

/**
 * This class builds trees for the channel adapters, switches, routers, device
 * groups, virtual fabrics, and others
 */
public class FVTreeManager implements IContextAware {

    private static final String NAME = "TreeManager";

    Logger mLog = LoggerFactory.getLogger(FVTreeManager.class);

    SubnetDescription subnet;

    /**
     * Subnet API
     */
    ISubnetApi mSubnetApi;

    /**
     * Performance API
     */
    IPerformanceApi mPerformanceApi;

    SearchTreeBuilder searchBuilder;

    private final EnumMap<TreeTypeEnum, TreeManagementModel> mgrModels;

    /**
     * 
     * Description: Constructor for the FVTreeBuilder class
     * 
     * @param pContext
     *            - handle to the APIs
     */
    public FVTreeManager() {
        mgrModels =
                new EnumMap<TreeTypeEnum, TreeManagementModel>(
                        TreeTypeEnum.class);
        for (TreeTypeEnum type : TreeTypeEnum.values()) {
            mgrModels.put(type, new TreeManagementModel());
        }

        searchBuilder = new SearchTreeBuilder();
    }

    @Override
    public synchronized void setContext(Context pContext,
            IProgressObserver observer) {
        mLog.info("Clear trees because context being set for "
                + pContext.getSubnetDescription());
        subnet = pContext.getSubnetDescription();
        mSubnetApi = pContext.getSubnetApi();
        mPerformanceApi = pContext.getPerformanceApi();
        reset();
    }

    @Override
    public String getName() {
        return NAME;
    }

    /**
     * 
     * Description: Builds the type of tree specified by the input parameter
     * 
     * @param pTreeType
     *            - type of tree to build
     * 
     * @return root node of the tree
     */
    public synchronized FVResourceNode buildTree(TreeTypeEnum pTreeType,
            IProgressObserver observer) {
        long t = System.currentTimeMillis();
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        // observer.setNote(pTreeType.getName());
        FVResourceNode node = null;

        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                node = createDeviceTypesTree(observer);
                break;

            case DEVICE_GROUPS_TREE:
                node = createDeviceGroupsTree(observer);
                break;

            case VIRTUAL_FABRICS_TREE:
                node = createVFsTree(observer);
                break;

            case TOP_10_CONGESTED_TREE:
                observer.onFinish();
                break;

            default:
                break;
        } // switch

        mLog.info("Build tree " + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
        return node;
    }

    public synchronized void updateTree(TreeTypeEnum pTreeType,
            IProgressObserver observer) {
        long t = System.currentTimeMillis();
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        // observer.setNote(pTreeType.getName());
        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                updateDeviceTypesTree(observer);
                break;

            case DEVICE_GROUPS_TREE:
                updateDeviceGroupsTree(observer);
                break;

            case VIRTUAL_FABRICS_TREE:
                updateVFsTree(observer);
                break;

            case TOP_10_CONGESTED_TREE:
                observer.onFinish();
                break;

            default:
                break;
        } // switch

        mLog.info("Update tree " + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
    }

    public synchronized void updateTreeNode(int lid, TreeTypeEnum pTreeType) {
        long t = System.currentTimeMillis();

        switch (pTreeType) {
            case DEVICE_TYPES_TREE:
                updateDeviceTypesTreeNode(lid);
                break;

            case DEVICE_GROUPS_TREE:
                updateDeviceGroupsTreeNode(lid);
                break;

            case VIRTUAL_FABRICS_TREE:
                updateVFsTreeNode(lid);
                break;

            case TOP_10_CONGESTED_TREE:
                break;

            default:
                break;
        } // switch

        mLog.info("Update tree node" + pTreeType + " in "
                + (System.currentTimeMillis() - t) + " ms");
    }

    public synchronized SearchResult searchTreeNode(TreeTypeEnum treeType,
            TreeSearchType searchType, String searchKey,
            TimeDrivenProgressObserver observer,
            ICancelIndicator cancelIndicator) {
        TreeManagementModel model = mgrModels.get(treeType);

        SearchResult result = null;

        if (model != null && model.isValid()) {
            FVResourceNode treeRoot = model.getTree();
            result =
                    searchBuilder.searchAndBuildTree(treeType, searchType,
                            searchKey, treeRoot, observer, cancelIndicator);
        } else if (treeType != TreeTypeEnum.TOP_10_CONGESTED_TREE) {
            throw new RuntimeException("Tree is under change " + model);
        }

        return result;
    }

    public void addMonitor(TreeTypeEnum treeType, ITreeMonitor monitor) {
        TreeManagementModel model = mgrModels.get(treeType);
        if (model != null) {
            model.addTreeMonitor(monitor);
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find TreeManagementModel for " + treeType);
        }
    }

    public void removeMonitor(TreeTypeEnum treeType, ITreeMonitor monitor) {
        TreeManagementModel model = mgrModels.get(treeType);
        if (model != null) {
            model.removeTreeMonitor(monitor);
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find TreeManagementModel for " + treeType);
        }
    }

    /**
     * 
     * Description: reset this builder so the cached <code>subnet</code> will be
     * created from scratch. call this method when we switch to another subnet
     * or current subnet is changed.
     * 
     */
    public synchronized void reset() {
        for (TreeManagementModel model : mgrModels.values()) {
            model.reset();
        }
    }

    public synchronized void setDirty() {
        for (TreeManagementModel model : mgrModels.values()) {
            model.setDirty(true);
        }
    }

    /**
     * 
     * Description: Creates the device types tree consisting of channel
     * adapters, switches, and routers
     * 
     * @return rootNode - root node of the device types tree
     */
    protected FVResourceNode createDeviceTypesTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isValid()) {
            observer.onFinish();
            return model.getTree();
        }

        // Create the root node of the tree
        FVResourceNode deviceTypesTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());

        DeviceTypesTreeSynchronizer treeUpdater =
                new DeviceTypesTreeSynchronizer(mSubnetApi);
        treeUpdater.updateTree(deviceTypesTree, null, observer);
        model.setTree(deviceTypesTree);
        return deviceTypesTree;
    }

    /**
     * 
     * <i>Description:</i> update device types tree
     * 
     * @param observer
     *            progress observer used to notify update progress
     * @param model
     *            TreeModel used to fire tree change events when necessary
     */
    protected void updateDeviceTypesTree(final IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isEmpty()) {
            createDeviceTypesTree(observer);
            return;
        }

        if (model.isDirty()) {
            DeviceTypesTreeSynchronizer treeUpdater =
                    new DeviceTypesTreeSynchronizer(mSubnetApi);
            treeUpdater.updateTree(model.getTree(), model.getMonitors(),
                    observer);
            model.setDirty(false);
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateDeviceTypesTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_TYPES_TREE);
        if (model.isEmpty()) {
            return;
        } else {
            // Don't need to set model dirty so don't check if it's dirty.
            DeviceTypesTreeUpdater treeUpdater =
                    new DeviceTypesTreeUpdater(mSubnetApi);
            FVResourceNode tree = model.getTree();
            List<ITreeMonitor> monitors = model.getMonitors();
            treeUpdater.updateNode(lid, tree, monitors);
        }
    }

    /**
     * 
     * Description: Creates the device groups tree consisting of groups of
     * network resources
     * 
     * @return root node of the device groups tree
     */
    protected FVResourceNode createDeviceGroupsTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);
        if (model.isValid()) {
            return model.getTree();
        }
        IProgressObserver[] subObservers = observer.createSubObservers(2);

        FVResourceNode deviceGroupsTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());
        FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
        subObservers[0].onFinish();
        DeviceGroupsTreeSynchronizer treeUpdater =
                new DeviceGroupsTreeSynchronizer(mPerformanceApi, subnetTree);
        treeUpdater.updateTree(deviceGroupsTree, null, subObservers[1]);
        subObservers[1].onFinish();
        model.setTree(deviceGroupsTree);
        return deviceGroupsTree;
    }

    protected void updateDeviceGroupsTree(IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);

        if (model.isEmpty()) {
            createDeviceGroupsTree(observer);
            return;
        }

        if (model.isDirty()) {
            if (observer == null) {
                observer = new ObserverAdapter();
            }
            final IProgressObserver[] subObservers =
                    observer.createSubObservers(2);

            FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
            subObservers[0].onFinish();
            DeviceGroupsTreeSynchronizer treeUpdater =
                    new DeviceGroupsTreeSynchronizer(mPerformanceApi,
                            subnetTree);
            treeUpdater.updateTree(model.getTree(), model.getMonitors(),
                    subObservers[1]);
            subObservers[1].onFinish();
            model.setDirty(false);
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateDeviceGroupsTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.DEVICE_GROUPS_TREE);

        if (model.isEmpty()) {
            return;
        } else {
            DeviceGroupsTreeUpdater treeUpdater =
                    new DeviceGroupsTreeUpdater(mSubnetApi, mPerformanceApi);
            FVResourceNode tree = model.getTree();
            List<ITreeMonitor> monitors = model.getMonitors();
            treeUpdater.updateNode(lid, tree, monitors);
        }
    }

    /**
     * 
     * Description: Creates the virtual fabric tree consisting of vFabrics of
     * network resources
     * 
     * @return root node of the virtual fabric tree
     */
    protected FVResourceNode createVFsTree(IProgressObserver observer) {
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isValid()) {
            observer.onFinish();
            return model.getTree();
        }

        IProgressObserver[] subObsevers = observer.createSubObservers(2);

        FVResourceNode vfTree =
                new FVResourceNode(subnet.getName(), TreeNodeType.ALL,
                        TreeNodeType.ALL.ordinal());
        FVResourceNode subnetTree = createDeviceTypesTree(subObsevers[0]);
        subObsevers[0].onFinish();
        VirtualFabricsTreeSynchronizer treeUpdater =
                new VirtualFabricsTreeSynchronizer(mPerformanceApi, subnetTree);
        treeUpdater.updateTree(vfTree, null, subObsevers[1]);
        subObsevers[1].onFinish();
        model.setTree(vfTree);
        return vfTree;
    }

    protected void updateVFsTree(IProgressObserver observer) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isEmpty()) {
            createVFsTree(observer);
            return;
        }

        if (model.isDirty()) {
            if (observer == null) {
                observer = new ObserverAdapter();
            }
            final IProgressObserver[] subObservers =
                    observer.createSubObservers(2);

            FVResourceNode subnetTree = createDeviceTypesTree(subObservers[0]);
            subObservers[0].onFinish();
            VirtualFabricsTreeSynchronizer treeUpdater =
                    new VirtualFabricsTreeSynchronizer(mPerformanceApi,
                            subnetTree);
            treeUpdater.updateTree(model.getTree(), model.getMonitors(),
                    subObservers[1]);
            subObservers[1].onFinish();
            model.setDirty(false);
        }

        if (observer != null) {
            observer.onFinish();
        }
    }

    protected void updateVFsTreeNode(final int lid) {
        final TreeManagementModel model =
                mgrModels.get(TreeTypeEnum.VIRTUAL_FABRICS_TREE);
        if (model.isEmpty()) {
            return;
        } else {
            VirtualFabricsTreeUpdater treeUpdater =
                    new VirtualFabricsTreeUpdater(mSubnetApi, mPerformanceApi);
            treeUpdater.updateNode(lid, model.getTree(), model.getMonitors());
        }
    }

    public class TreeManagementModel {
        private FVResourceNode tree;

        private boolean isDirty = true;

        private final List<ITreeMonitor> monitors =
                new ArrayList<ITreeMonitor>();

        /**
         * @return the tree
         */
        public FVResourceNode getTree() {
            return tree;
        }

        /**
         * @param tree
         *            the tree to set
         */
        public void setTree(FVResourceNode tree) {
            this.tree = tree;
            isDirty = false;
        }

        /**
         * @return the isDirty
         */
        public boolean isDirty() {
            return isDirty;
        }

        /**
         * @param isDirty
         *            the isDirty to set
         */
        public void setDirty(boolean isDirty) {
            this.isDirty = isDirty;
        }

        public void addTreeMonitor(ITreeMonitor monitor) {
            monitors.add(monitor);
        }

        public void removeTreeMonitor(ITreeMonitor monitor) {
            monitors.remove(monitor);
        }

        /**
         * @return the monitors
         */
        public List<ITreeMonitor> getMonitors() {
            return monitors;
        }

        public boolean isValid() {
            return tree != null && !isDirty;
        }

        public boolean isEmpty() {
            return tree == null;
        }

        public void reset() {
            tree = null;
            isDirty = true;
        }

    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    /**
     * @return the mgrModels
     */
    public EnumMap<TreeTypeEnum, TreeManagementModel> getMgrModels() {
        return mgrModels;
    }
} // FVTreeBuilder
