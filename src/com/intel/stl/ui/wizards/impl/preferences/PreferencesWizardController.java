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

package com.intel.stl.ui.wizards.impl.preferences;

import javax.swing.JComponent;

import com.intel.stl.api.IMessage;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.wizards.impl.IMultinetWizardListener;
import com.intel.stl.ui.wizards.impl.IMultinetWizardTask;
import com.intel.stl.ui.wizards.impl.IWizardListener;
import com.intel.stl.ui.wizards.impl.InteractionType;
import com.intel.stl.ui.wizards.impl.WizardValidationException;
import com.intel.stl.ui.wizards.model.IModelChangeListener;
import com.intel.stl.ui.wizards.model.IWizardModel;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.preferences.PreferencesModel;
import com.intel.stl.ui.wizards.view.preferences.PreferencesWizardView;

/**
 * Controller for the User Preferences Wizard
 */
public class PreferencesWizardController
        implements IMultinetWizardTask, IModelChangeListener<IWizardModel> {

    private final PreferencesWizardView view;

    private PreferencesModel preferencesModel;

    @SuppressWarnings("unused")
    private IWizardListener wizardController;

    private IMultinetWizardListener multinetWizardController;

    private boolean done;

    private PreferencesInputValidator validator;

    private boolean firstPass = true;

    private boolean connectable;

    public PreferencesWizardController(PreferencesWizardView view) {
        this.view = view;
    }

    public PreferencesWizardController(PreferencesWizardView view,
            PreferencesModel preferencesModel) {
        this(view);
        view.setDirty(false);
        this.preferencesModel = preferencesModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#getName()
     */
    @Override
    public String getName() {

        return STLConstants.K3005_PREFERENCES.getValue();
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

        // Singleton logging validator
        validator = PreferencesInputValidator.getInstance();

        if (firstPass || multinetWizardController.isNewWizard()) {
            view.resetPanel();
        }

        firstPass = false;
        view.setDirty(false);
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

        // Only try to get the PMConfig if the host is reachable
        int sweepInterval = 0;
        if (connectable) {

            try {
                // The call to getPMConfig() may fail silently since the user
                // may be setting up subnets that are known to be disconnected
                PMConfigBean pmConfig = null;
                pmConfig = multinetWizardController.getPMConfig();

                if (pmConfig != null) {
                    sweepInterval = pmConfig.getSweepInterval();
                }

            } catch (ConfigurationException e) {
            } catch (Exception e) {
                Util.showError(view, e);
                return false;
            }
        } else {
            PreferencesValidatorError error =
                    PreferencesValidatorError.UNABLE_TO_VALIDATE;
            throw new WizardValidationException(error.getLabel());
        }

        // Since it is possible to be unable to connect to the host,
        // update the model whether it is valid or not
        updateModel();

        int errorCode = validator.validate(preferencesModel, sweepInterval);
        if (errorCode == PreferencesValidatorError.OK.getId()) {
            success = true;
        } else {
            view.logMessage(PreferencesValidatorError.getValue(errorCode));
            IMessage message = PreferencesValidatorError.getMessage(errorCode);
            if (message != null) {
                throw new WizardValidationException(message,
                        PreferencesValidatorError.getData(errorCode));
            } else {
                throw new WizardValidationException();
            }
        }

        return (success && connectable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onPrevious()
     */
    @Override
    public void onPrevious() {

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onReset()
     */
    @Override
    public void onReset() {

        view.resetPanel();
        view.setDirty(false);
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
        multinetWizardController.setDirty(dirty);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#doInteractiveAction(com.intel
     * .stl.ui.wizards.impl.InteractionType, java.lang.Object[])
     */
    @Override
    public void doInteractiveAction(InteractionType action, Object... data) {

        switch (action) {

            case CHANGE_WIZARDS:

                if (data == null) {
                    return;
                }

                String taskName = (String) data[0];

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
    public void updateModel() {

        // Update the local preferences model
        preferencesModel.setRefreshRate(view.getRefreshRate());
        preferencesModel.setRefreshRateUnits(view.getRefreshRateUnits());
        preferencesModel
                .setTimingWindowInSeconds(view.getTimeWindowInSeconds());
        preferencesModel.setNumWorstNodes(view.getNumWorstNodes());
        preferencesModel.setMailRecipients(view.getEmailList());
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

        // Promote the preferences model to the top model
        topModel.setPreferencesModel(preferencesModel);
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
     * .stl.ui.wizards.impl.IMultinetWizardListener)
     */
    @Override
    public void setWizardController(IMultinetWizardListener controller) {
        multinetWizardController = controller;
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

        this.wizardController = controller;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#clear()
     */
    @Override
    public void clear() {
        view.clearPanel();
        preferencesModel.clear();
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

    public void onEmailTest(String recipients) {
        multinetWizardController.onEmailTest(recipients);
    }

}
