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

package com.intel.stl.ui.wizards.impl;

import static com.intel.stl.ui.wizards.view.WizardViewType.WELCOME;
import static com.intel.stl.ui.wizards.view.WizardViewType.WIZARD;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.alert.MailNotifier;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.IFabricController;
import com.intel.stl.ui.main.ISubnetManager;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.wizards.impl.event.EventWizardController;
import com.intel.stl.ui.wizards.impl.preferences.PreferencesWizardController;
import com.intel.stl.ui.wizards.impl.subnet.SubnetWizardController;
import com.intel.stl.ui.wizards.model.IModelChangeListener;
import com.intel.stl.ui.wizards.model.IWizardModel;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.event.EventRulesTableModel;
import com.intel.stl.ui.wizards.model.event.EventsModel;
import com.intel.stl.ui.wizards.model.preferences.PreferencesModel;
import com.intel.stl.ui.wizards.model.subnet.SubnetModel;
import com.intel.stl.ui.wizards.view.IMultinetWizardView;
import com.intel.stl.ui.wizards.view.ITaskView;
import com.intel.stl.ui.wizards.view.MultinetWizardView;
import com.intel.stl.ui.wizards.view.event.EventWizardView;
import com.intel.stl.ui.wizards.view.preferences.PreferencesWizardView;
import com.intel.stl.ui.wizards.view.subnet.SubnetWizardView;

/**
 * Top level Multinet Setup Wizard
 */
public class MultinetWizardController
        implements IMultinetWizardListener, IModelChangeListener<IWizardModel> {

    private final MultinetWizardView view;

    private final ISubnetManager subnetMgr;

    private final List<IMultinetWizardTask> tasks;

    private IWizardTask currentTask;

    private final IWizardTask firstTask;

    private final IWizardTask lastTask;

    private String userName;

    private UserSettings userSettings;

    @SuppressWarnings("unused")
    private IFabricController controller;

    private boolean isFirstRun;

    private final MultinetWizardModel wizardModel;

    private SubnetWizardView subnetView;

    private static MultinetWizardController instance;

    private SubnetWizardController subnetController;

    private EventWizardController eventController;

    private SubnetDescription subnet;

    private boolean workerStatus;

    private ConfigureSubnetTask configTask;

    /**
     *
     * Description: Private constructor for the singleton
     * MultinetWizardController
     *
     * @param view
     *            view for the setup wizard
     */
    protected MultinetWizardController(MultinetWizardView view,
            MultinetWizardModel wizardModel, ISubnetManager subnetMgr) {

        this.view = view;
        this.wizardModel = wizardModel;
        this.subnetMgr = subnetMgr;
        this.isFirstRun = subnetMgr.isFirstRun();
        this.view.setWizardListener(this);

        tasks = getTasks(subnetMgr);
        installTasks(tasks);
        this.view.setTasks(tasks);
        firstTask = tasks.get(0);
        lastTask = tasks.get(tasks.size() - 1);

        HelpAction helpAction = getHelpAction();
        helpAction.getHelpBroker().enableHelpOnButton(view.getHelpButton(),
                helpAction.getSetupWizard(), helpAction.getHelpSet());
    }

    public static MultinetWizardController getInstance(IFabricView owner,
            ISubnetManager subnetMgr) {

        if (instance == null) {
            MultinetWizardView wizardView = new MultinetWizardView(owner);
            MultinetWizardModel wizardModel = new MultinetWizardModel();
            instance = new MultinetWizardController(wizardView, wizardModel,
                    subnetMgr);
        }

        return instance;
    }

    /**
     *
     * <i>Description: Builds a list of IConfigTask controllers</i>
     *
     * @return tasks - list of IConfigTask
     */
    protected List<IMultinetWizardTask> getTasks(ISubnetManager subnetMgr) {

        List<IMultinetWizardTask> tasks = new ArrayList<IMultinetWizardTask>();

        // Add this object as a listener to the top model
        wizardModel.addModelListener(this, WizardType.MULTINET);

        // Subnet Wizard
        SubnetModel subnetModel = new SubnetModel();
        subnetView = new SubnetWizardView(view);
        subnetController =
                new SubnetWizardController(subnetView, subnetModel, subnetMgr);
        subnetView.setWizardListener(subnetController);
        wizardModel.setSubnetModel(subnetModel);
        tasks.add(subnetController);
        wizardModel.addModelListener(subnetController, WizardType.SUBNET);

        // Event Wizard
        EventRulesTableModel eventRulesTableModel = new EventRulesTableModel();
        EventsModel eventModel = new EventsModel(eventRulesTableModel);
        EventWizardView eventView =
                new EventWizardView(eventRulesTableModel, view);
        eventController = new EventWizardController(eventView, eventModel);
        eventView.setWizardListener(eventController);
        wizardModel.setEventsModel(eventModel);
        tasks.add(eventController);
        wizardModel.addModelListener(eventController, WizardType.EVENT);

        // User Preferences
        PreferencesModel preferencesModel = new PreferencesModel();
        PreferencesWizardView preferencesView =
                new PreferencesWizardView(view, preferencesModel);
        PreferencesWizardController preferencesController =
                new PreferencesWizardController(preferencesView,
                        preferencesModel);
        preferencesView.setWizardListener(preferencesController);
        wizardModel.setPreferencesModel(preferencesModel);
        tasks.add(preferencesController);
        wizardModel.addModelListener(preferencesController,
                WizardType.PREFERENCES);

        return tasks;
    }

    protected HelpAction getHelpAction() {
        return HelpAction.getInstance();
    }

    protected IWizardTask getTaskByName(String name) {

        boolean found = false;
        IWizardTask currentTask = null;
        Iterator<IMultinetWizardTask> it = tasks.iterator();

        while (!found && it.hasNext()) {

            currentTask = it.next();
            found = (currentTask.getName().equals(name));
        }

        return currentTask;
    }

    private void installTasks(List<IMultinetWizardTask> tasks) {
        for (IMultinetWizardTask task : tasks) {
            task.setWizardController(this);
        }
    }

    public void setReady(boolean b) {
    }

    public void cleanup() {

        for (IWizardTask task : tasks) {
            try {
                task.cleanup();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onPrevious()
     */
    @Override
    public void onPrevious() {
        IWizardTask previousTask = tasks.get(tasks.indexOf(currentTask) - 1);

        // Assume both buttons to be enabled and move to the previous task
        view.enableNext(true);
        view.enablePrevious(true);
        previousTask.onPrevious();

        boolean validTab = view.previousTab();
        if (validTab) {
            selectStep(previousTask.getName());

            if (currentTask != null) {
                // If the first task is current, disable the previous button
                if (currentTask.getName().equals(firstTask.getName())) {
                    view.enablePrevious(false);
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onNext()
     */
    @Override
    public boolean onNext() {
        boolean success = false;

        // Commit the changes
        try {
            success = currentTask.validateUserEntry();
            currentTask.promoteModel(wizardModel);

            if (success) {
                // At first, enable both buttons
                view.enableNext(true);
                view.enablePrevious(true);

                // Move to the next tab or change to the welcome window
                boolean validTab = view.nextTab();
                if (validTab) {
                    selectStep(tasks.get(tasks.indexOf(currentTask) + 1)
                            .getName());
                }
            }

        } catch (WizardValidationException e) {
            view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }

        return success;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#onTab()
     */
    @Override
    public void onTab(String tabName) {
        IWizardTask task =
                (tabName != null) ? getTaskByName(tabName) : currentTask;

        if (task != null) {
            task.promoteModel(wizardModel);
            showStep(tasks.get(tasks.indexOf(task)).getName());
        } else {
            view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    STLConstants.K3043_INVALID_WIZARD_TASK.getValue());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#onFinish()
     */
    @Override
    public void onFinish() {
        String subnetName = view.getSubnetName();
        wizardModel.getSubnetModel().getSubnet().setName(subnetName);
        configTask = new ConfigureSubnetTask();
        configTask.execute();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#cancelConfiguration
     * ()
     */
    @Override
    public void cancelConfiguration() {
        if (configTask != null) {
            configTask.cancel(true);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#validateEntry()
     */
    @Override
    public void validateEntry() {

        SwingWorker<Boolean, Void> worker = new SwingWorker<Boolean, Void>() {

            @Override
            protected Boolean doInBackground() throws Exception {

                boolean success = false;

                try {
                    success = currentTask.validateUserEntry();
                } catch (WizardValidationException e) {
                    view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                            e.getMessage());
                }

                return success;
            }

            @Override
            protected void done() {
                try {
                    workerStatus = get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                            e);
                }
            }
        };
        worker.execute();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#isValidEntry()
     */
    @Override
    public boolean isValidEntry() {

        return workerStatus;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#setWorkerStatus()
     */
    @Override
    public void resetWorkerStatus() {

        workerStatus = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#checkHost()
     */
    @Override
    public void checkHost() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#saveConfiguration()
     */
    @Override
    public void saveConfiguration() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onApply()
     */
    @Override
    public void onApply() {

    }

    private boolean updateDatabase() throws Exception {

        boolean result = false;

        // Save the current subnet to the database, update the models/maps
        boolean success = saveSubnet();

        if (success) {
            // Get the current user settings from the subnet manager
            SubnetDescription subnetDescription = subnet;
            String currentSubnetName = null;
            if (subnetDescription != null) {
                currentSubnetName = subnetDescription.getName();
            }
            UserSettings userSettings =
                    subnetMgr.getUserSettings(currentSubnetName, userName);

            // Save the event rules to the user settings
            EventRulesTableModel eventRulesTable =
                    wizardModel.getEventsModel().getEventsRulesModel();
            userSettings.setEventRules(eventRulesTable.getEventRules());

            // Save the user preferences to the user settings
            PreferencesModel preferencesModel =
                    wizardModel.getPreferencesModel();
            userSettings.setPreferences(preferencesModel.getPreferencesMap());

            // Save the user settings to the database
            subnetMgr.saveUserSettings(currentSubnetName, userSettings);

            // Notify view that new wizard is complete
            view.setNewWizardInProgress(false);

            // Loop through all tasks and set dirty to false
            for (IMultinetWizardTask task : tasks) {
                task.setDirty(false);
            }

            result = true;
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onClose()
     */
    @Override
    public void onClose() {

        view.closeWizard();
        this.isFirstRun = false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardListener#selectStep(java.lang.String
     * )
     */
    @Override
    public void selectStep(String taskName) {

        // Get the task by name
        IWizardTask task = getTaskByName(taskName);

        // Initialize the current task and show the view
        if (task != null) {
            currentTask = task;
            currentTask.init();
        }
        view.showTaskView(taskName);

        // If this is not the last task, keep next button "Next"
        if (lastTask != null) {
            if (task != null && !task.getName().equals(lastTask.getName())) {
                view.updateNextButton(STLConstants.K0622_NEXT.getValue());
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardListener#showStep(java.lang.String)
     */
    @Override
    public void showStep(String taskName) {

        // Get the task by name
        IWizardTask task = getTaskByName(taskName);

        // Initialize the current task and show the view
        currentTask = task;
        view.showTaskView(taskName);

        // If this is not the last task, keep next button "Next"
        if (lastTask != null) {
            if (task != null && !task.getName().equals(lastTask.getName())) {
                view.updateNextButton(STLConstants.K0622_NEXT.getValue());
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#showView(boolean)
     */
    @Override
    public void showView(SubnetDescription subnet, String userName,
            IFabricController callingController) {
        this.subnet = subnet;
        List<SubnetDescription> subnets = subnetMgr.getSubnets();
        if ((subnet.getSubnetId() == 0) && (subnets.size() > 0)) {
            subnet = subnetMgr.getSubnets().get(0);
        }

        this.userName = userName;
        IFabricView mainFrame = callingController.getView();

        try {
            this.userSettings =
                    subnetMgr.getUserSettings(subnet.getName(), userName);
        } catch (UserNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if (subnets.size() > 0) {
            view.setWizardViewType(WIZARD);
            updateModels(subnet);
        } else {
            view.setWizardViewType(WELCOME);
        }

        view.setSubnets(subnets);

        if (subnet.getSubnetId() != 0) {
            view.setSelectedSubnet(subnet);
            wizardModel.getSubnetModel().setSubnet(subnet);
        }

        // By default select first step
        for (IWizardTask task : tasks) {
            task.init();
        }
        currentTask = tasks.get(0);
        view.enablePrevious(false);

        view.showWizard(subnet, subnetMgr.isFirstRun(), mainFrame);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getView()
     */
    @Override
    public IMultinetWizardView getView() {

        return view;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#closeStatusPanels()
     */
    @Override
    public void closeStatusPanels() {

        for (IWizardTask task : tasks) {
            ((ITaskView) task.getView()).closeStatusPanel();
        }
    }

    @Override
    public UserSettings getUserSettings() {

        return userSettings;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#isFirstRun()
     */
    @Override
    /**
     * @return the isFirstRun
     */
    public boolean isFirstRun() {
        return isFirstRun;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#onRun()
     */
    @Override
    public boolean onRun() {

        boolean result = false;
        String hostName = null;
        String subnetName = wizardModel.getSubnetModel().getSubnet().getName();

        try {
            hostName = wizardModel.getSubnetModel().getSubnet().getCurrentFE()
                    .getHost();
        } catch (IllegalArgumentException e) {
            Util.showError(view, e);
        }

        if (isHostConnectable()) {
            try {
                subnetMgr.startSubnet(subnetName);
                result = true;
            } catch (SubnetConnectionException e) {
                view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                        e.getMessage(),
                        STLConstants.K2004_CONNECTION.getValue());
            }
        } else {
            view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    UILabels.STL50050_CONNECTION_FAIL.getDescription(subnetName,
                            hostName));
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onReset()
     */
    @Override
    public void onReset() {
        for (IWizardTask task : tasks) {
            task.onReset();
        }
        view.setDirty(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onNewSubnet()
     */
    @Override
    public void onNewSubnet() {
        // Add a new subnet, display new empty tabs and subnet button
        // This subnet will be updated when the subnet controller updates
        // the model with a subnet from the subnetMgr
        this.subnet = new SubnetDescription(
                STLConstants.K3018_UNKNOWN_SUBNET.getValue(),
                STLConstants.K3018_UNKNOWN_SUBNET.getValue(),
                SubnetModel.DEFAULT_PORT_NUM);
        view.addSubnet(subnet);
        wizardModel.getSubnetModel().setSubnet(subnet);
        wizardModel.notifyModelChange(WizardType.SUBNET);
        for (IMultinetWizardTask task : tasks) {
            task.clear();
            task.promoteModel(wizardModel);
        }

        // Update all models
        wizardModel.notifyModelChange();

        view.setSelectedSubnet(subnet);
    }

    @Override
    public void onSelectSubnet(SubnetDescription subnet) {
        this.subnet = subnet;
        updateModels(subnet);
        for (IMultinetWizardTask task : tasks) {
            task.setDirty(false);
        }
        // This changes the Subnet Name field, which dirties the view
        view.setSelectedSubnet(subnet);
    }

    @Override
    public boolean haveUnsavedChanges() {
        if (view.isDirty()) {
            return true;
        }
        // Check if any of the wizards have been edited
        for (IWizardTask task : tasks) {
            if (task.isDirty()) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#onDelete()
     */
    @Override
    public void onDelete() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#deleteSubnet(com
     * .intel.stl.api.subnet.SubnetDescription)
     */
    @Override
    public void deleteSubnet(SubnetDescription subnet) {
        if (subnet.getSubnetId() != 0) {
            try {
                subnetMgr.removeSubnet(subnet);
            } catch (SubnetDataNotFoundException e) {
                view.showErrorMessage(
                        STLConstants.K0329_PORT_SUBNET_MANAGER.getValue() + " "
                                + STLConstants.K0030_ERROR.getValue(),
                        e.getMessage());
            }
        }
        for (IMultinetWizardTask task : tasks) {
            task.setDirty(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#getSubnets()
     */
    @Override
    public List<SubnetDescription> getSubnets() {

        return subnetMgr.getSubnets();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.model.IModelChangeListener#onModelChange(com
     * .intel.stl.ui.wizards.model.IWizardModel)
     */
    @Override
    public void onModelChange(IWizardModel m) {
        MultinetWizardModel model = (MultinetWizardModel) m;

        SubnetDescription subnet = model.getSubnetModel().getSubnet();

        // Update the subnet in the view
        view.setSelectedSubnet(subnet);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getHostIp(java.
     * lang.String)
     */
    @Override
    public String getHostIp(String hostName) {

        String ipAddress = null;

        try {
            ipAddress = subnetMgr.getHostIp(hostName);
        } catch (SubnetConnectionException e) {
            view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }

        return ipAddress;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#isReachable()
     */
    @Override
    public boolean isHostReachable() {

        return subnetMgr.isHostReachable(wizardModel.getSubnetModel()
                .getSubnet().getCurrentFE().getHost());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#isHostConnectable()
     */
    @Override
    public boolean isHostConnectable() {

        boolean result = false;

        try {
            result = subnetMgr.isHostConnectable(
                    wizardModel.getSubnetModel().getSubnet());
        } catch (ConfigurationException e) {
            view.showErrorMessage(
                    STLConstants.K2004_CONNECTION.getValue() + " "
                            + STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }

        return result;

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#tryToConnect()
     */
    @Override
    public boolean tryToConnect() {

        boolean result = false;

        try {
            result = subnetMgr
                    .tryToConnect(wizardModel.getSubnetModel().getSubnet());
        } catch (SubnetConnectionException e) {
            view.showErrorMessage(
                    STLConstants.K2004_CONNECTION.getValue() + " "
                            + STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardListener#tryToConnect(com.intel.
     * stl.api.subnet.SubnetDescription)
     */
    @Override
    public boolean tryToConnect(SubnetDescription subnet)
            throws SubnetConnectionException {

        boolean result = false;

        try {
            result = subnetMgr.tryToConnect(subnet);
        } catch (SubnetConnectionException e) {
            view.showErrorMessage(
                    STLConstants.K2004_CONNECTION.getValue() + " "
                            + STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }
        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#getPMConfig()
     */
    @Override
    public PMConfigBean getPMConfig() {

        PMConfigBean pmConfigBean = null;
        pmConfigBean =
                subnetMgr.getPMConfig(wizardModel.getSubnetModel().getSubnet());

        return pmConfigBean;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#getTaskScheduler()
     */
    @Override
    public TaskScheduler getTaskScheduler() {

        return subnetMgr
                .getTaskScheduler(wizardModel.getSubnetModel().getSubnet());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#saveUserSettings()
     */
    @Override
    public void saveUserSettings() {

        subnetMgr.saveUserSettings(
                wizardModel.getSubnetModel().getSubnet().getName(),
                userSettings);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardListener#getSubnet()
     */
    @Override
    public SubnetDescription getSubnet() {

        return wizardModel.getSubnetModel().getSubnet();
    }

    protected boolean saveSubnet() {

        boolean result = false;

        try {
            SubnetDescription savedSubnet = subnetMgr
                    .saveSubnet(wizardModel.getSubnetModel().getSubnet());

            // Update the Subnet Model with the saved subnet (containing the
            // ID)
            wizardModel.getSubnetModel().setSubnet(savedSubnet);
            this.subnet = savedSubnet;
            view.resetSubnet(savedSubnet);
            result = true;
        } catch (SubnetDataNotFoundException e) {
            view.showErrorMessage(
                    STLConstants.K2004_CONNECTION.getValue() + " "
                            + STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }

        return result;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#
     * retrieveUserSettings ()
     */
    @Override
    public UserSettings retrieveUserSettings() {

        return retrieveUserSettings(subnet);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#
     * retrieveUserSettings
     */
    @Override
    public UserSettings retrieveUserSettings(SubnetDescription subnet) {

        UserSettings userSettings = null;

        try {
            userSettings =
                    subnetMgr.getUserSettings(subnet.getName(), userName);

        } catch (UserNotFoundException e) {
            view.showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    e.getMessage());
        }
        return userSettings;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getCurrentSubnet()
     */
    @Override
    public SubnetDescription getCurrentSubnet() {
        return subnet;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getCurrentTask()
     */
    @Override
    public IWizardTask getCurrentTask() {
        return currentTask;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#setCurrentTask(
     * com.intel.stl.ui.wizards.impl.IWizardTask)
     */
    @Override
    public void setCurrentTask(int taskPosition) {
        if (taskPosition >= 0) {
            this.currentTask = tasks.get(taskPosition);
        }
    }

    private void updateModels(SubnetDescription subnet) {

        // Set the Subnet model
        wizardModel.getSubnetModel().setSubnet(subnet);

        // Set the Events model
        UserSettings userSettings = retrieveUserSettings(subnet);
        List<EventRule> userEventRules = null;
        Map<String, Properties> preferencesMap = null;
        if (userSettings != null) {
            userEventRules = userSettings.getEventRules();
            preferencesMap = userSettings.getPreferences();
        }
        EventRulesTableModel eventRulesTableModel =
                eventController.updateEventRulesTableModel(userEventRules);
        wizardModel.getEventsModel().setEventsRulesModel(eventRulesTableModel);

        // Set the Preferences Model
        wizardModel.getPreferencesModel().setPreferencesMap(preferencesMap);
        // Update the all models
        wizardModel.notifyModelChange();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IMultinetWizardListener#clearTasks()
     */
    @Override
    public void clearTasks() {
        for (IMultinetWizardTask task : tasks) {
            task.clear();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getNewWizardStatus
     * ()
     */
    @Override
    public boolean isNewWizard() {

        return view.getNewWizardStatus();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#getSubnetView()
     */
    @Override
    public SubnetWizardView getSubnetView() {

        return subnetView;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean dirty) {
        view.enableApply(dirty);
        view.enableReset(dirty);
    }

    private class ConfigureSubnetTask
            extends SwingWorker<Void, ConfigTaskStatus> {

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected Void doInBackground() throws Exception {

            // Disable the Ok button on the welcome panel
            view.setWelcomeOkEnabled(false);

            // Update the models and promote
            for (IWizardTask task : tasks) {
                task.updateModel();
                task.promoteModel(wizardModel);
            }

            // Check if the host is reachable and publish the result
            view.setProgress(ConfigTaskType.CHECK_HOST);
            boolean isConnectable = isHostConnectable();
            ConfigTaskStatus connectable = new ConfigTaskStatus(
                    ConfigTaskType.CHECK_HOST, isConnectable, null);
            publish(connectable);

            // If host is reachable validate and publish
            view.setProgress(ConfigTaskType.VALIDATE_ENTRY);

            int numPass = 0;
            List<WizardValidationException> errors =
                    new ArrayList<WizardValidationException>();
            boolean status = false;
            for (IWizardTask task : tasks) {
                task.setConnectable(isConnectable);
                try {
                    boolean pass = task.validateUserEntry();
                    if (pass) {
                        numPass++;
                    }
                } catch (WizardValidationException e) {
                    e.printStackTrace();
                    errors.add(e);
                }
            }
            status = (numPass == tasks.size()) ? true : false;

            currentTask.promoteModel(wizardModel);
            ConfigTaskStatus valid = new ConfigTaskStatus(
                    ConfigTaskType.VALIDATE_ENTRY, status, errors);
            publish(valid);

            try {
                // Update the database and publish the result
                view.setProgress(ConfigTaskType.UPDATE_DATABASE);
                ConfigTaskStatus updated = new ConfigTaskStatus(
                        ConfigTaskType.UPDATE_DATABASE, updateDatabase(), null);
                publish(updated);
            } catch (Exception e) {
                publish(new ConfigTaskStatus(ConfigTaskType.UPDATE_DATABASE,
                        false, null));
            }

            return null;
        }

        /*-
         * Note that process runs on the EDT
         * @see http://docs.oracle.com/javase/7/docs/api/javax/swing/SwingWorker.html#process(java.util.List)
         */
        @Override
        protected void process(List<ConfigTaskStatus> statusList) {

            for (ConfigTaskStatus status : statusList) {
                view.updateConfigStatus(status);
            }
        }

        @Override
        protected void done() {

            try {
                get();
            } catch (InterruptedException e) {
            } catch (ExecutionException e) {
            }

            // We must update the event rules and recipients list in the local
            // copy in the MailNotifier.
            // The update of these items in the database takes place in the
            // config task below.
            SubnetDescription subnet = getCurrentSubnet();
            Context context = subnetMgr.getContext(subnet);
            if (context != null) {
                MailNotifier mailNotifier =
                        (MailNotifier) context.getEmailNotifier();

                EventRulesTableModel eventModel =
                        wizardModel.getEventsModel().getEventsRulesModel();
                List<EventRule> rulesList = eventModel.getEventRules();
                mailNotifier.setEventRules(rulesList);

                String recipients =
                        wizardModel.getPreferencesModel().getMailRecipients();
                List<String> recipientsList = Utils.concatenatedStringToList(
                        recipients, UIConstants.MAIL_LIST_DELIMITER);
                mailNotifier.setRecipients(recipientsList);
            }

            for (IMultinetWizardTask task : tasks) {
                task.setDirty(false);
            }
            view.setDirty(false);
            view.enableNavPanel(true);
            view.enableSubnetModifiers(true);

            // Enable the Ok button on the welcome panel
            view.setWelcomeOkEnabled(true);
        }
    } // class ConfigureSubnetTask

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#onEmailTest(java
     * .lang.String)
     */
    @Override
    public void onEmailTest(String recipients) {
        subnetMgr.onEmailTest(recipients);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardListener#validateEmail(java.
     * lang.String)
     */
    @Override
    public boolean isEmailValid(String email) {
        return subnetMgr.isEmailValid(email);
    }
}
