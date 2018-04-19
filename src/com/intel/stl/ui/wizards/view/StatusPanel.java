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
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;

import org.jdesktop.swingx.JXCollapsiblePane;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.impl.InteractionType;

/**
 * View for a Wizard's status panel
 */
public class StatusPanel extends JXCollapsiblePane implements IStatusView {

    private static final long serialVersionUID = 7169689232956883008L;

    private IWizardTask wizardTaskController;

    private boolean isStatusOpen = true;

    private JTextArea txtAreaStatus;

    private JButton collapseButton;

    private Object[] data;

    private JPanel pnlAnswer;

    private InteractionType action;

    private final int maxHeight;

    public StatusPanel() {
        initComponents();
        maxHeight = getPreferredSize().height;
        closeStatusPanel();
        setAnimated(true);
    }

    protected void initComponents() {

        // Add a status panel to hold any messages coming from the views
        setLayout(new BorderLayout());

        collapseButton =
                new JButton(getActionMap().get(JXCollapsiblePane.TOGGLE_ACTION));
        txtAreaStatus = new JTextArea(3, 30);
        txtAreaStatus.setOpaque(true);
        txtAreaStatus.setEditable(false);
        txtAreaStatus.setLineWrap(true);
        txtAreaStatus.setWrapStyleWord(true);
        txtAreaStatus.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        txtAreaStatus.setBorder(BorderFactory.createLoweredBevelBorder());
        txtAreaStatus.setBackground(UIConstants.INTEL_RED);
        add(txtAreaStatus, BorderLayout.CENTER);

        // Create a panel with the Yes/No buttons on it
        pnlAnswer = new JPanel();
        pnlAnswer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlAnswer.setOpaque(false);
        pnlAnswer.setLayout(new GridLayout(2, 5, 0, 5));

        JButton btnYes =
                ComponentFactory.getIntelActionButton(STLConstants.K0081_YES
                        .getValue());
        btnYes.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                wizardTaskController.doInteractiveAction(action, data);
            }
        });
        pnlAnswer.add(btnYes);

        JButton btnNo =
                ComponentFactory.getIntelActionButton(STLConstants.K0082_NO
                        .getValue());
        btnNo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                wizardTaskController.setDone(false);
                closeStatusPanel();
                wizardTaskController.doInteractiveAction(action, (Object) null);
            }
        });
        pnlAnswer.add(btnNo);

        // Add the answer panel to the status panel
        add(pnlAnswer, BorderLayout.EAST);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.IStatusView#openStatusPanel()
     */
    @Override
    public void openStatusPanel() {
        if (!isStatusOpen) {
            collapseButton.doClick();
            isStatusOpen = true;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.IStatusView#closeStatusPanel()
     */
    @Override
    public void closeStatusPanel() {
        if (isStatusOpen) {
            collapseButton.doClick();
            isStatusOpen = false;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.IStatusView#toggleStatusPanel()
     */
    @Override
    public void toggleStatusPanel() {
        collapseButton.doClick();
        isStatusOpen = !isStatusOpen;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.IStatusView#showMessage(java.lang.String,
     * InteractionType, java.awt.Color, java.awt.Color, java.lang.Object[])
     */
    @Override
    public void showMessage(String message, InteractionType action,
            int messageType, Object... data) {

        this.action = action;
        boolean interactive = (action == null) ? false : true;

        // This is used in the interactive action
        this.data = data;

        // Hide or display answer panel depending on interactive flag
        pnlAnswer.setVisible(interactive);

        // Set the background and foreground of the text area
        setMessageType(messageType);

        // Set the status message on the text area
        txtAreaStatus.setText(message);

        // Simulate a button click to expand the status panel
        openStatusPanel();
    }

    protected void setMessageType(int messageType) {
        if (messageType == JOptionPane.ERROR_MESSAGE) {
            txtAreaStatus.setBackground(UIConstants.INTEL_RED);
            txtAreaStatus.setForeground(UIConstants.INTEL_DARK_GRAY);
        } else if (messageType == JOptionPane.WARNING_MESSAGE) {
            txtAreaStatus.setBackground(UIConstants.INTEL_LIGHT_YELLOW);
            txtAreaStatus.setForeground(UIConstants.INTEL_DARK_GRAY);
        } else {
            txtAreaStatus.setBackground(UIConstants.INTEL_DARK_GREEN);
            txtAreaStatus.setForeground(UIConstants.INTEL_DARK_GRAY);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.IStatusView#setWizardTaskController(com
     * .intel.stl.ui.wizards.impl.IWizardTask)
     */
    @Override
    public void setWizardTaskController(IWizardTask wizardTaskController) {
        this.wizardTaskController = wizardTaskController;
    }

    /**
     * @return the openHeight
     */
    public int getMaxHeight() {
        return maxHeight;
    }

}
