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

package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;
import static com.intel.stl.ui.common.STLConstants.K0300_PROPERTIES;
import static com.intel.stl.ui.common.STLConstants.K0301_PROPERTIES_DESCRIPTION;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CancellationException;

import javax.swing.ImageIcon;
import javax.swing.Timer;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.ResourceType;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.FinishObserver;
import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.ObserverAdapter;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.configuration.view.DevicePropertiesPanel;
import com.intel.stl.ui.configuration.view.DevicePropertyGroupPanel;
import com.intel.stl.ui.configuration.view.IPropertyListener;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.framework.AbstractController;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.DeviceProperties;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.model.PropertyGroupViz;
import com.intel.stl.ui.monitor.PerformanceTreeController;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class DevicePropertiesController
        extends
        AbstractController<DeviceProperties, DevicePropertiesPanel, DevicePropertiesController>
        implements IPerfSubpageController, IPropertyListener {
    private GetDevicePropertiesTask propertiesTask;

    private Timer viewClearTimer;

    private UserSettings userSettings;

    private FVResourceNode currentNode;

    private PerformanceTreeController parentController;

    private final PropertyVizStyle style = new PropertyVizStyle();

    public DevicePropertiesController(DeviceProperties model,
            DevicePropertiesPanel view, MBassador<IAppEvent> eventBus) {
        super(model, view, eventBus);
    }

    @Override
    public void initModel() {
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        try {
            super.setContext(context);
            this.userSettings = context.getUserSettings();
        } finally {
            observer.onFinish();
        }
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
        if (observer == null) {
            observer = new ObserverAdapter();
        }

        if (currentNode != null) {
            showNode(currentNode, observer);
        }

        observer.onFinish();
    }

    // We override the default behavior to control the building of the
    // different pieces in the view
    @Override
    public void onTaskSuccess() {
        clearTimer();
        view.clearPanel();
        for (DevicePropertyGroup group : model.getGroups()) {
            try {
                DevicePropertyGroupPanel groupPanel =
                        new DevicePropertyGroupPanel(style);
                String helpID = getHelpID(model.getResourceType(), group);
                new PropertyGroupController(group, groupPanel, eventBus, helpID);
                view.addPropertyGroupPanel(groupPanel);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        List<PropertyGroup> groups =
                userSettings.getPropertiesDisplayOptions().get(
                        model.getResourceType());
        if (groups != null) {
            view.initUserSettings(groups);
        }
        notifyModelChanged();
    }

    protected String getHelpID(ResourceType type, DevicePropertyGroup group) {
        String title = group.getGroupName();
        PropertyGroupViz pgViz =
                PropertyGroupViz.getPropertyGroupVizByTitle(title);
        HelpAction helpAction = HelpAction.getInstance();
        switch (pgViz) {
            case GENERAL:
                return helpAction.getNodeGeneral();
            case SWITCH_INFO:
                return helpAction.getSwitchInformation();
            case ROUTING_INFO:
                return helpAction.getRoutingInformation();
            case DEVICE_GROUP:
                return helpAction.getDeviceGroup();
            case MFT:
                return helpAction.getMft();
            case LFT:
                return helpAction.getLft();
            case DEVICE_INFO:
                return helpAction.getPortDevInfo();
            case PORT_LINK:
                return helpAction.getPortLink();
            case LINK_CONNECTION:
                return helpAction.getPortLinkConn();
            case PORT_CAPABILITY:
                return helpAction.getPortCap();
            case VIRTUAL_LANE:
                return helpAction.getVL();
            case PORT_DIAGNOSTICS:
                return helpAction.getDiagnostics();
            case PORT_PARTITION_ENFORCEMENT:
                return helpAction.getPartition();
            case PORT_MANAGEMENT:
                return helpAction.getManagement();
            case FLIT_CONTROL:
                return helpAction.getFlitControl();
            case PORT_ERROR_ACTIONS:
                return helpAction.getPortErrorActions();
            case MISCELLANEOUS:
                return helpAction.getMisc();
            case MTU:
                return helpAction.getMTUByVL();
            case HOQLIFE:
                return helpAction.getHoQLifeByVL();
            case VL_STALL_COUNT:
                return helpAction.getStallCountByVL();
            case CABLE_INFO:
                return helpAction.getQSFP();
            case SC2SLMT:
                return helpAction.getSC2SL();
            case SC2VLTMT:
                return helpAction.getSC2VLT();
            case SC2VLNTMT:
                return helpAction.getSC2VLNT();
            case LINK_DOWN_ERROR_REASON:
                return helpAction.getLinkDownError();
            default:
                return null;

        }
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        clearTimer();
        notifyModelUpdateFailed(caught);
    }

    @Override
    public String getName() {
        return K0300_PROPERTIES.getValue();
    }

    @Override
    public String getDescription() {
        return K0301_PROPERTIES_DESCRIPTION.getValue();
    }

    @Override
    public Component getView() {
        return view;
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
        if (viewClearTimer == null) {
            viewClearTimer =
                    new Timer(UIConstants.UPDATE_TIME, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (viewClearTimer != null) {
                                view.clear();
                            }
                        }
                    });
            viewClearTimer.setRepeats(false);
        }
        viewClearTimer.restart();
    }

    @Override
    public void showNode(FVResourceNode node, IProgressObserver observer) {
        if (propertiesTask != null) {
            if (propertiesTask.isSubmitted() && !propertiesTask.isDone()) {
                try {
                    propertiesTask.cancel(true);
                } catch (CancellationException e) {
                    // Ignore exception since this is what we expect
                }
            }
        }
        ResourceType resourceType =
                TreeNodeType.getResourceTypeFor(node.getType());
        if (resourceType == null) {
            currentNode = node;
            return;
        }
        // This clears all categories selected in the model
        model.setResourceType(resourceType);

        propertiesTask = new GetDevicePropertiesTask(model, node, observer);
        submitTask(propertiesTask);
        currentNode = node;
    }

    @Override
    public void setParentController(PerformanceTreeController parentController) {
        this.parentController = parentController;
    }

    protected void clearTimer() {
        if (viewClearTimer != null) {
            if (viewClearTimer.isRunning()) {
                viewClearTimer.stop();
            }
            viewClearTimer = null;
        }
    }

    public UserSettings getUserSettings() {
        return userSettings;
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    // for testing
    protected GetDevicePropertiesTask getTask() {
        return propertiesTask;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#onShowBorder
     * (boolean)
     */
    @Override
    public void onShowBorder(boolean isSelected) {
        style.setShowBorder(isSelected);
        onTaskSuccess();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#showAlternation
     * (boolean)
     */
    @Override
    public void onShowAlternation(boolean isSelected) {
        style.setAlternateRows(isSelected);
        onTaskSuccess();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyListener#onDisplayChanged
     * (java.util.Map)
     */
    @Override
    public void onDisplayChanged(Map<String, Boolean> newSelections) {
        if (applyChange(newSelections)) {
            if (parentController != null) {
                parentController.setRunning(true);
                showNode(currentNode, new FinishObserver() {
                    @Override
                    public void onFinish() {
                        parentController.setRunning(false);
                    }
                });
            } else {
                showNode(currentNode, null);
            }
        }
    }

    protected boolean applyChange(Map<String, Boolean> newSelections) {
        boolean changed = false;
        List<PropertyGroup> groups =
                userSettings.getPropertiesDisplayOptions().get(
                        model.getResourceType());
        if (groups != null) {
            for (PropertyGroup group : groups) {
                Boolean newSelection = newSelections.get(group.getName());
                if (newSelection != null
                        && !newSelection.equals(group.isDisplayed())) {
                    changed = true;
                    group.setDisplayed(newSelection);
                }
            }
        }
        return changed;
    }
}
