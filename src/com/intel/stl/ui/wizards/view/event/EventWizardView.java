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

package com.intel.stl.ui.wizards.view.event;

import javax.swing.JComponent;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.impl.event.IEventControl;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.event.EventRulesTableModel;
import com.intel.stl.ui.wizards.view.AbstractTaskView;
import com.intel.stl.ui.wizards.view.IMultinetWizardView;
import com.intel.stl.ui.wizards.view.IWizardView;

/**
 * This class replaces the EventRulesPanel, in the Event Wizard, with a event
 * rules table on a single panel.
 */
public class EventWizardView extends AbstractTaskView {

    private static final long serialVersionUID = 5308404765504020477L;

    private EventTableView tableView;

    private EventRulesTableModel model;

    @SuppressWarnings("unused")
    private IWizardView wizardViewListener = null;

    private IMultinetWizardView multinetWizardViewListener = null;

    private IEventControl eventControlListener;

    public EventWizardView(EventRulesTableModel model,
            IWizardView wizardViewListener) {
        this.model = model;
        this.wizardViewListener = wizardViewListener;
        initComponents("");
        resetPanel();
    }

    public EventWizardView(EventRulesTableModel model,
            IMultinetWizardView wizardViewListener) {
        this.model = model;
        this.multinetWizardViewListener = wizardViewListener;
        initComponents("");
        resetPanel();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.AbstractTaskView#getOptionComponent()
     */
    @Override
    protected JComponent getOptionComponent() {
        if (tableView == null) {
            tableView = new EventTableView(model, multinetWizardViewListener);
            tableView.setName(WidgetName.SW_E_TABBLE.name());
        }
        return tableView;
    }

    @Override
    public void setWizardListener(IWizardTask listener) {
        super.setWizardListener(listener);
        tableView.setWizardListener(listener);
    }

    public void setEventControlListener(IEventControl listener) {

        eventControlListener = listener;
    }

    /**
     * @return the model
     */
    public EventRulesTableModel getModel() {
        return model;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.ITaskView#updatePanel()
     */
    @Override
    public void resetPanel() {
        if (tableView != null) {
            tableView.setDirty(false);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.ITaskView#isDirty()
     */
    @Override
    public boolean isDirty() {
        return tableView.isDirty();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.ITaskView#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean dirty) {
        tableView.setDirty(dirty);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.ITaskView#setSubnet(com.intel.stl.api.
     * subnet .SubnetDescription)
     */
    @Override
    public void setSubnet(SubnetDescription subnet) {
        resetPanel();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.ITaskView#update(com.intel.stl.ui.wizards
     * .model.MultinetWizardModel)
     */
    @Override
    public void updateView(MultinetWizardModel mwModel) {

        this.model = mwModel.getEventsModel().getEventsRulesModel();
        tableView.setModel(model);
        eventControlListener.updateTable(model);
    }

    /**
     * <i>Description:</i>
     *
     * @return
     */
    public boolean isEditValid() {
        return true;
    }
}
