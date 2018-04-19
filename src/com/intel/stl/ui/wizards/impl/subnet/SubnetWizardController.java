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

package com.intel.stl.ui.wizards.impl.subnet;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.main.ISubnetManager;
import com.intel.stl.ui.wizards.impl.IMultinetWizardListener;
import com.intel.stl.ui.wizards.impl.IMultinetWizardTask;
import com.intel.stl.ui.wizards.impl.IWizardListener;
import com.intel.stl.ui.wizards.impl.InteractionType;
import com.intel.stl.ui.wizards.impl.WizardValidationException;
import com.intel.stl.ui.wizards.model.IModelChangeListener;
import com.intel.stl.ui.wizards.model.IWizardModel;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.subnet.SubnetModel;
import com.intel.stl.ui.wizards.view.subnet.HostInfoPanel;
import com.intel.stl.ui.wizards.view.subnet.SubnetWizardView;

/**
 * Controller for the Subnet Wizard
 */
public class SubnetWizardController implements IMultinetWizardTask,
        ISubnetControl, IModelChangeListener<IWizardModel> {

    private static Logger log = LoggerFactory
            .getLogger(SubnetWizardController.class);

    private LinkedHashMap<String, SubnetDescription> subnets;

    private SubnetWizardView view = null;

    private final ISubnetManager subnetMgr;

    private boolean done;

    private IMultinetWizardListener multinetWizardController;

    private SubnetModel subnetModel;

    private SubnetDescription newSubnet;

    @SuppressWarnings("unused")
    private boolean connectable;

    private boolean connectionTest = false;

    /**
     * 
     * Description Constructor for the Subnet Wizard Controller:
     * 
     * @param view
     *            - View for the Subnet Wizard Controller
     * 
     * @param subnetMgr
     *            - subnet manager
     */
    public SubnetWizardController(SubnetWizardView view,
            ISubnetManager subnetMgr) {

        this.subnetMgr = subnetMgr;
        loadSubnets();

        if (view != null) {
            this.view = view;
            view.setControlListener(this);
            view.setWizardListener(this);
            view.setDirty(false);
        } else {
            log.error(STLConstants.K3045_SUBNET_VIEW_NULL.getValue());
        }
    }

    /**
     * 
     * Description: Constructor for the Multinet Subnet Wizard Controller
     * 
     * @param view
     *            - View for the Subnet Wizard Controller
     * 
     * @param subnetModel
     *            - data model for the subnet
     * 
     * @param subnetMgr
     *            - subnet manager
     */
    public SubnetWizardController(SubnetWizardView view,
            SubnetModel subnetModel, ISubnetManager subnetMgr) {

        this(view, subnetMgr);
        this.subnetModel = subnetModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.ISubnetControl#addSubnetListener(com.intel
     * .stl.ui.admin.ISubnetListener)
     */
    @Override
    public void addSubnetListener(ISubnetListener listener) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#getName()
     */
    @Override
    public String getName() {
        return STLConstants.K0052_HOSTS.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#getView()
     */
    @Override
    public JComponent getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#init()
     */
    @Override
    public void init() {
        view.setDirty(false);
        view.enableNext(false);
        done = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#setDone(boolean)
     */
    @Override
    public void setDone(boolean done) {

        this.done = done;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#isDone()
     */
    @Override
    public boolean isDone() {

        return done;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onApply()
     */
    @Override
    public boolean validateUserEntry() throws WizardValidationException {

        boolean success = false;
        List<HostInfoPanel> hostPanelList = view.getHostPanelList();

        // Validate port entries
        for (HostInfoPanel pnl : hostPanelList) {

            try {
                int portNum = Integer.valueOf(pnl.getPortNum());
                if (portNum <= 0) {
                    throw new WizardValidationException(
                            UILabels.STL80002_INVALID_PORT_NUMBER, portNum);
                }
            } catch (NumberFormatException nfe) {
                WizardValidationException wve =
                        new WizardValidationException(
                                UILabels.STL80007_PORT_INVALID_FORMAT,
                                pnl.getPortNum());
                throw wve;
            }
        }

        // If not doing a connection test, update model with the valid entries
        // This makes it possible to reset the host values if changes need
        // to be abandoned
        if (!connectionTest) {
            updateModel();
        }

        // If we've made it this far, it's a success
        done = true;
        success = true;
        return success;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.ISubnetControl#connectActionPerformed(String
     * )
     */
    @Override
    public void connectActionPerformed(final HostInfo hostInfo) {

        // Create a subnet for this host
        final SubnetDescription subnet =
                new SubnetDescription(hostInfo.getHost(), hostInfo.getHost(),
                        Integer.valueOf(hostInfo.getPort()));
        List<HostInfo> hostInfoList = new ArrayList<HostInfo>();
        hostInfoList.add(hostInfo);
        subnet.setFEList(hostInfoList);
        subnet.setCurrentFEIndex(0);

        try {
            connectionTest = true;

            if (validateUserEntry()) {
                SwingWorker<Boolean, Void> worker =
                        new SwingWorker<Boolean, Void>() {

                            @Override
                            protected Boolean doInBackground() throws Exception {
                                boolean isConnected =
                                        multinetWizardController
                                                .tryToConnect(subnet);
                                return isConnected;
                            }

                            /*
                             * (non-Javadoc)
                             * 
                             * @see javax.swing.SwingWorker#done()
                             */
                            @Override
                            protected void done() {
                                Boolean isConnected = false;
                                Throwable error = null;
                                try {
                                    isConnected = get();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                } catch (ExecutionException e) {
                                    e.printStackTrace();
                                    error = e.getCause();
                                }
                                subnet.setLastStatus(isConnected ? SubnetDescription.Status.VALID
                                        : SubnetDescription.Status.INVALID);

                                if (isConnected) {
                                    // Enable the "Apply" and "Next" buttons
                                    multinetWizardController.getView()
                                            .enableApply(true);
                                    multinetWizardController.getView()
                                            .enableNext(true);
                                    view.setConnectionStatus(
                                            hostInfo.getHost(),
                                            STLConstants.K3031_PASS.getValue());
                                    view.logMessage(UILabels.STL50017_CONNECTION_EST
                                            .getDescription());
                                } else if (error != null) {
                                    view.setConnectionStatus(
                                            hostInfo.getHost(),
                                            STLConstants.K3032_FAIL.getValue());
                                    view.showMessage(
                                            UILabels.STL50059_CONNECTION_FAILURE_PROCEED
                                                    .getDescription(
                                                            subnet.getName(),
                                                            subnet.getCurrentFE()
                                                                    .getHost(),
                                                            StringUtils
                                                                    .getErrorMessage(error)),
                                            JOptionPane.WARNING_MESSAGE);
                                } else {
                                    view.setConnectionStatus(
                                            hostInfo.getHost(),
                                            STLConstants.K3032_FAIL.getValue());
                                    view.showMessage(
                                            UILabels.STL50059_CONNECTION_FAILURE_PROCEED
                                                    .getDescription(
                                                            subnet.getName(),
                                                            subnet.getCurrentFE()
                                                                    .getHost(),
                                                            STLConstants.K0016_UNKNOWN
                                                                    .getValue()),
                                            JOptionPane.WARNING_MESSAGE);
                                }
                            }

                        };
                worker.execute();
            } else {
                view.setConnectionStatus(subnet.getName(),
                        STLConstants.K3032_FAIL.getValue());
            }
        } catch (WizardValidationException e) {
            e.printStackTrace();
        } finally {

            connectionTest = false;
        }
    }

    protected void loadSubnets() {
        List<SubnetDescription> dbSubnets = null;
        dbSubnets = subnetMgr.getSubnets();
        if (dbSubnets != null) {
            subnets = new LinkedHashMap<String, SubnetDescription>();
            for (SubnetDescription dbSubnet : dbSubnets) {
                subnets.put(dbSubnet.getName(), dbSubnet);
            }
            log.info("Subnets: " + subnets);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.subnet.ISubnetControl#getSubnet()
     */
    @Override
    public SubnetDescription getSubnet() {

        return subnetModel.getSubnet();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#selectStep(java.lang.String)
     */
    @Override
    public void selectStep(String taskName) {

        multinetWizardController.selectStep(taskName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onPrevious()
     */
    @Override
    public void onPrevious() {

        view.enableNext(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onReset()
     */
    @Override
    public void onReset() {

        // view.resetPanel();
        view.update(subnetModel);
        view.setDirty(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#clear()
     */
    @Override
    public void clear() {
        view.clearPanel();
        subnetModel.clear();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#isDirty()
     */
    @Override
    public boolean isDirty() {

        return view.isDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean dirty) {

        view.setDirty(dirty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#doInteractiveAction(com.intel
     * .stl.ui.wizards.impl.InteractionType, java.lang.Object)
     */
    @Override
    public void doInteractiveAction(InteractionType action, Object... data) {

        switch (action) {

            case CHANGE_WIZARDS:

                if (data == null) {
                    return;
                }

                String taskName = (String) data[0];

                // setDone(true);
                if (taskName != null) {
                    onReset();
                    view.closeStatusPanel();
                    selectStep(taskName);
                }
                break;

            case SAVE_LOGGING:
                // NOP
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#updateModel()
     */
    @Override
    public synchronized void updateModel() {

        if (subnetModel == null) {
            return;
        }

        // Update the local subnet model
        if (subnetModel.getSubnet() != null) {

            // Get the subnet from the model
            newSubnet = subnetModel.getSubnet();
            newSubnet.setAutoConnect(view.isAutoConnected());

            // Loop through the backup hosts
            List<HostInfo> feList = new ArrayList<HostInfo>();
            for (HostInfoPanel hostInfoPanel : view.getHostPanelList()) {
                HostInfo hostInfo = new HostInfo();
                hostInfo.setHost(hostInfoPanel.getHostName());
                hostInfo.setPort(Integer.valueOf(hostInfoPanel.getPortNum()));
                hostInfo.setSecureConnect(hostInfoPanel.isSecureConnection());
                CertsDescription certs = new CertsDescription();
                certs.setKeyStoreFile(hostInfoPanel.getKeyStoreFile());
                certs.setTrustStoreFile(hostInfoPanel.getTrustStoreFile());
                hostInfo.setCertsDescription(certs);

                feList.add(hostInfo);
            }
            newSubnet.setFEList(feList);

            subnetModel.setSubnet(newSubnet);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.subnet.ISubnetControl#getNewsubnet()
     */
    @Override
    public SubnetDescription getNewSubnet() {
        return newSubnet;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#promoteModel(com.intel.stl.
     * ui.wizards.model.MultinetWizardModel)
     */
    @Override
    public void promoteModel(MultinetWizardModel topModel) {

        // Promote the subnet model to the top model
        topModel.setSubnetModel(subnetModel);
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
        view.updateView(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#setWizardController(com.intel
     * .stl.ui.wizards.impl.IWizardListener)
     */
    @Override
    public void setWizardController(IWizardListener controller) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardTask#setWizardController
     * (com.intel.stl.ui.wizards.impl.IMultinetWizardListener)
     */
    @Override
    public void setWizardController(IMultinetWizardListener controller) {
        multinetWizardController = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.subnet.ISubnetControl#getSubnetModel()
     */
    @Override
    public SubnetModel getSubnetModel() {

        return subnetModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#setConnectable(boolean)
     */
    @Override
    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#isEditValid()
     */
    @Override
    public boolean isEditValid() {
        return view.isEditValid();
    }

}
