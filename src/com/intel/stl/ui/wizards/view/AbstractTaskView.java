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

package com.intel.stl.ui.wizards.view;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.impl.InteractionType;

public abstract class AbstractTaskView extends JPanel implements ITaskView {

    private static final long serialVersionUID = 3149874617834752585L;

    private static Logger log = LoggerFactory.getLogger(AbstractTaskView.class);

    private StatusPanel statusPanel;

    public AbstractTaskView() {
    }

    /**
     * Description:
     * 
     * @param title
     */
    public AbstractTaskView(String title) {
        super();
        initComponents(title);
        // resetPanel();
    }

    protected void initComponents(String title) {
        setOpaque(true);
        setBackground(MultinetWizardView.WIZARD_COLOR);
        setLayout(new BorderLayout());

        // Add the status panel at the top of the view
        statusPanel = new StatusPanel();
        statusPanel.getContentPane().setBackground(
                MultinetWizardView.WIZARD_COLOR);
        JComponent optionComp = getOptionComponent();
        add(optionComp, BorderLayout.CENTER);
    }

    protected abstract JComponent getOptionComponent();

    public void setWizardListener(IWizardTask listener) {
        statusPanel.setWizardTaskController(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#openStatusPanel()
     */
    @Override
    public void openStatusPanel() {
        statusPanel.openStatusPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#closeStatusPanel()
     */
    @Override
    public void closeStatusPanel() {
        statusPanel.closeStatusPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#toggleStatusPanel()
     */
    @Override
    public void toggleStatusPanel() {
        statusPanel.toggleStatusPanel();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.ITaskView#showMessage(java.lang.String,
     * java.lang.String, com.intel.stl.ui.wizards.impl.InteractionType, int,
     * Object)
     */
    @Override
    public void showMessage(String message, InteractionType action,
            int messageType, Object... data) {
        statusPanel.showMessage(message, action, messageType, data);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.ITaskView#showMessage(java.lang.String,
     * java.lang.String, com.intel.stl.ui.wizards.impl.InteractionType, int,
     * Object)
     */
    @Override
    public void logMessage(String message) {
        log.error(message);
    }
}
