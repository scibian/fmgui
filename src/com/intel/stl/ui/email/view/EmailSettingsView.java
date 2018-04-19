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

package com.intel.stl.ui.email.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.email.IEmailController;
import com.intel.stl.ui.main.view.FVMainFrame;
import com.intel.stl.ui.main.view.IFabricView;

public class EmailSettingsView extends JDialog implements DocumentListener {

    private static final long serialVersionUID = 450669593032341011L;

    private boolean dirty = false;

    private JButton okBtn;

    private JButton cancelBtn;

    private JButton resetBtn;

    private JButton testBtn;

    private JTextArea messageArea;

    private final JTextField serverNameTxtField =
            ComponentFactory.createTextField(null, true, 100, this);

    private final JTextField serverPortTxtField =
            ComponentFactory.createTextField("0123456789", false, 5, this);

    private final JTextField fromAddrTxtField =
            ComponentFactory.createTextField(null, true, 100, this);

    private final JTextField testAddressTxtField =
            ComponentFactory.createTextField(null, true, 100, this);

    private IEmailController listener;

    private JCheckBox enableEmailFeature;

    private final String helpTextStr =
            STLConstants.K5014_EMAIL_HINT_TEXT.getValue();

    private final String helpTooltipStr =
            STLConstants.K5015_EMAIL_TOOLTIP_HINT_TEXT.getValue();

    private final Color disabledTextColor = UIConstants.INTEL_GRAY;

    private final Color enabledTextColor = java.awt.Color.black;

    public EmailSettingsView(IFabricView owner) {
        // Construct application modal dialog - should block all the windows
        // in the application.
        super((JFrame) owner);
        setTitle(STLConstants.K5010_EMAIL_SETTINGS_TITLE.getValue());
        setModal(true);
        setModalityType(ModalityType.APPLICATION_MODAL);

        initComponents();

        // First pack() call is to get the necessary height for the dialog
        pack();

        // Update preferred size - making the dialog wider for nicer look
        setPreferredSize(
                new Dimension(500, (int) this.getPreferredSize().getHeight()));

        // The second pack is to re-package the dialog after we updated
        // its preferred size above.
        pack();
        setLocationRelativeTo((Component) owner);
    }

    /**
     * <i>Description:</i> Create and layout components
     *
     */
    private void initComponents() {
        addSettingsPanel();
        addButtonsPanel();
    }

    private void addSettingsPanel() {
        JPanel settingsPanel = new JPanel(new GridBagLayout());
        settingsPanel.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;

        enableEmailFeature = ComponentFactory.getIntelCheckBox(
                STLConstants.K5012_EMAIL_ENABLE_OPTION_TXT.getValue());
        enableEmailFeature.setName(WidgetName.ES_ENABLE_NOTIFICATION.name());
        enableEmailFeature.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        enableEmailFeature.setForeground(UIConstants.INTEL_DARK_GRAY);
        enableEmailFeature.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                dirty = true;

                // Set all the components on the page enabled if checkbox is
                // selected and disabled if checkbox is unchecked(deselected)
                setComponentsEnabled(e.getStateChange() == ItemEvent.SELECTED
                        ? true : false);
                updateMessageArea();
            }
        });
        gc.gridwidth = GridBagConstraints.REMAINDER;
        settingsPanel.add(enableEmailFeature, gc);

        gc.gridy++;
        gc.weightx = 0.1;
        gc.gridwidth = 1;
        JLabel smtpNameLbl = ComponentFactory.getH5Label(
                STLConstants.K5002_SMTP_HOST.getValue() + " :", Font.BOLD);
        smtpNameLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        settingsPanel.add(smtpNameLbl, gc);

        gc.gridx = 1;
        gc.weightx = 0.6;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        serverNameTxtField.setName(WidgetName.ES_SMTP_HOST.name());
        serverNameTxtField.setText("");
        settingsPanel.add(serverNameTxtField, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 0.1;
        gc.gridwidth = 1;
        JLabel smtpPortLbl = ComponentFactory.getH5Label(
                STLConstants.K0404_PORT_NUMBER.getValue() + " :", Font.BOLD);
        smtpPortLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        settingsPanel.add(smtpPortLbl, gc);

        gc.gridx = 1;
        gc.weightx = 0.6;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        serverPortTxtField.setName(WidgetName.ES_SMTP_PORT.name());
        serverPortTxtField.setText("25");
        settingsPanel.add(serverPortTxtField, gc);

        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 0.1;
        gc.gridwidth = 1;
        JLabel senderLbl = ComponentFactory.getH5Label(
                STLConstants.K5003_FROM_LBL.getValue() + ":", Font.BOLD);
        senderLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        settingsPanel.add(senderLbl, gc);

        gc.gridx = 1;
        gc.weightx = 0.6;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        fromAddrTxtField.setName(WidgetName.ES_SENDER.name());
        fromAddrTxtField.setText(helpTextStr);
        fromAddrTxtField.setToolTipText(helpTooltipStr);
        fromAddrTxtField.setForeground(disabledTextColor);
        fromAddrTxtField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent fEvt) {
                // On focus gained clear the help string
                if (fromAddrTxtField.getText().equalsIgnoreCase(helpTextStr)
                        && enableEmailFeature.isSelected()) {
                    fromAddrTxtField.setText("");
                    fromAddrTxtField.setForeground(enabledTextColor);
                }
            }

            @Override
            public void focusLost(FocusEvent fEvt) {
                // On focus lost, set the help text in case if user did not
                // enter an email
                if (fromAddrTxtField.getText().trim().isEmpty()
                        && enableEmailFeature.isSelected()) {
                    fromAddrTxtField.setText(helpTextStr);
                    fromAddrTxtField.setForeground(disabledTextColor);
                }
            }
        });

        settingsPanel.add(fromAddrTxtField, gc);

        JPanel testPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        testPanel.setBackground(UIConstants.INTEL_WHITE);
        testPanel
                .setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.INTEL_BORDER_GRAY, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        messageArea = new JTextArea();
        messageArea.setText(STLConstants.K5011_EMAIL_TEST_HELP_MSG.getValue());
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);
        messageArea.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        messageArea.setForeground(UIConstants.INTEL_DARK_GRAY);
        Util.makeUndoable(messageArea);
        testPanel.add(messageArea, gbc);

        gbc.gridy = 1;
        gbc.weightx = 0.6;
        gbc.gridwidth = 2;
        testAddressTxtField.setName(WidgetName.ES_RECEIVERS.name());
        testAddressTxtField.setToolTipText(helpTooltipStr);
        testPanel.add(testAddressTxtField, gbc);

        gbc.gridx = 3;
        gbc.weightx = 0.1;
        gbc.gridwidth = 1;
        testBtn = ComponentFactory.getIntelActionButton(
                STLConstants.K5009_WIZARD_EMAIL_TEST_LABEL_TEXT.getValue());
        testBtn.setName(WidgetName.ES_TEST.name());
        testBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                testBtnAction();
            }
        });
        // the document listen makes testBtn lose focus even when we click on
        // it. Turns out we have to click twice to fire the test action. The
        // following code gives this button focus when we mouse over it, so it
        // will behavior as desired
        testBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                testBtn.requestFocusInWindow();
            }
        });
        testBtn.setEnabled(false);
        testBtn.setToolTipText(
                STLConstants.K5016_EMAIL_TEST_BTN_TOOLTIP_TEXT.getValue());
        testPanel.add(testBtn, gbc);

        gc.gridy++;
        gc.gridx = 0;
        gc.weightx = 3;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        settingsPanel.add(testPanel, gc);

        this.getContentPane().add(settingsPanel, BorderLayout.CENTER);

    }

    private void addButtonsPanel() {
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setBackground(UIConstants.INTEL_WHITE);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 10));

        okBtn = ComponentFactory
                .getIntelActionButton(STLConstants.K0645_OK.getValue());
        okBtn.setName(WidgetName.ES_OK.name());
        okBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okBtnAction();
            }
        });
        // Disable ok button until user makes some changes to the settings
        okBtn.setEnabled(false);

        // In the beginning set Cancel button to be default
        cancelBtn = ComponentFactory
                .getIntelActionButton(STLConstants.K0621_CANCEL.getValue());
        cancelBtn.setName(WidgetName.ES_CANCEL.name());
        getRootPane().setDefaultButton(cancelBtn);
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtnAction();
            }
        });

        resetBtn = ComponentFactory.getIntelActionButton("Reset");
        resetBtn.setName(WidgetName.ES_RESET.name());
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                resetBtnAction();
            }
        });

        JButton[] btnGroup = { resetBtn, cancelBtn, okBtn, testBtn };
        ComponentFactory.makeSameWidthButtons(btnGroup);

        buttonsPanel.add(resetBtn);
        buttonsPanel.add(Box.createGlue());

        buttonsPanel.add(cancelBtn);
        buttonsPanel.add(Box.createHorizontalStrut(10));
        buttonsPanel.add(okBtn);

        getContentPane().add(buttonsPanel, BorderLayout.SOUTH);
    }

    // OK saves changes if any
    private void okBtnAction() {
        if (dirty) {
            // Send changes to the controller
            listener.onOK();
        }

        // Hide the dialog
        setVisible(false);
    }

    // Cancel button discards changes and hides the dialog
    private void cancelBtnAction() {
        updateMessageArea();
        // Hide the dialog
        setVisible(false);
    }

    @Override
    public void setVisible(boolean visible) {
        resetBtnAction();

        super.setVisible(visible);
        setLocationRelativeTo(this.getParent());

        // This is to make sure that regardless of the number of open main
        // FV frames this dialog always shows up on top of all the frames.
        if (visible) {
            this.setAlwaysOnTop(true);
        }
    }

    // The controller needs to update the view with cached values.
    // leave the dialog visible.
    private void resetBtnAction() {
        listener.onReset();

        // disable ok button
        okBtn.setEnabled(false);

        // and set Cancel to be the default button on the dialog
        getRootPane().setDefaultButton(cancelBtn);

        // Giving everything a fresh start
        dirty = false;
        updateMessageArea();
    }

    private void testBtnAction() {
        listener.onTest();
    }

    public void setEmailSettingsListener(IEmailController listener) {
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void insertUpdate(DocumentEvent e) {
        updateValues();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void removeUpdate(DocumentEvent e) {
        updateValues();
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.
     * DocumentEvent)
     */
    @Override
    public void changedUpdate(DocumentEvent e) {
        updateValues();
    }

    private void updateValues() {
        // One of the text fields got updated...

        // boolean canTest = false;
        if (isVisible()) {
            updateMessageArea();
            testBtn.setEnabled(isReadyToTest());

            dirty = true;

            makeOkBtnDefault();
        }
    }

    private void updateMessageArea() {
        if (messageArea.getText() != STLConstants.K5011_EMAIL_TEST_HELP_MSG
                .getValue()) {
            messageArea
                    .setText(STLConstants.K5011_EMAIL_TEST_HELP_MSG.getValue());
        }
    }

    private void makeOkBtnDefault() {
        // Enable ok button so that user could now save the settings...
        // even if they are incorrect
        okBtn.setEnabled(true);

        // Set the default action to ok buttons now
        getRootPane().setDefaultButton(okBtn);
    }

    // Getter methods to retrieve the values entered in the text fields:
    public String getSmtpServerNameStr() {
        return serverNameTxtField.getText();
    }

    public String getSmtpServerPortStr() {
        return serverPortTxtField.getText();
    }

    public String getFromAddrStr() {
        // If helper string is being displayed, replace it with empty string
        if (fromAddrTxtField.getText().trim().equalsIgnoreCase(helpTextStr)) {
            return "";
        }

        // otherwise return the contents of fromAddrTxtField
        return fromAddrTxtField.getText();
    }

    public String getToAddrStr() {
        return testAddressTxtField.getText();
    }

    public boolean getEnableEmail() {
        return enableEmailFeature.isSelected();
    }

    // Setter methods to set the values in the text fields:
    public void setSmtpServerNameStr(String serverName) {
        serverNameTxtField.setText(serverName);
    }

    public void setSmtpServerPortStr(String serverPort) {
        serverPortTxtField.setText(serverPort);
    }

    public void setFromAddrStr(String fromAddr) {
        if (fromAddr.trim().equals("")) {
            fromAddrTxtField.setText(helpTextStr);
            fromAddrTxtField.setForeground(disabledTextColor);
        } else {
            fromAddrTxtField.setText(fromAddr);
            fromAddrTxtField.setForeground(enabledTextColor);
        }
    }

    public void setToAddrStr(String toAddr) {
        testAddressTxtField.setText(toAddr);
    }

    public void setEnableEmailChkbox(boolean isEnabled) {
        this.enableEmailFeature.setSelected(isEnabled);

        // Update state of the editable components depending on the on/off
        // state of the email notifications feature.
        setComponentsEnabled(isEnabled);
    }

    /**
     * <i>Description:</i>
     *
     * @param owner
     */
    public void setOwner(FVMainFrame owner) {
        setLocationRelativeTo(owner);
    }

    /**
     *
     * <i>Description:</i> Logic to detect if all the needed settings for email
     * notification test have been provided by the user in the email settings
     * dialog. Required fields are: smtp server name, From: (email formatted
     * string), To: email recipient address (email formatted string)
     *
     * @return true if all the required settings have been provided by the user.
     *         false otherwise.
     */
    private boolean isReadyToTest() {
        boolean canTest = false;
        if (isVisible()) {
            boolean isFromTxtValid =
                    listener.isEmailValid(fromAddrTxtField.getText().trim());

            boolean isTestTxtValid =
                    listener.isEmailValid(testAddressTxtField.getText().trim());

            // If string entered, matches email pattern AND server name
            // has been entered, enable test button:
            if (isFromTxtValid && isTestTxtValid
                    && !serverNameTxtField.getText().trim().isEmpty()) {
                canTest = true;
            }

            testBtn.setEnabled(canTest);
        }
        return canTest;
    }

    public void setComponentsEnabled(boolean enabled) {
        // serverNameTxtField.setEditable(enabled);
        serverNameTxtField.setEnabled(enabled);

        serverPortTxtField.setEnabled(enabled);

        fromAddrTxtField.setEnabled(enabled);
        // Check if the help string is being displayed - then make font grey
        if (fromAddrTxtField.getText().trim().equalsIgnoreCase(helpTextStr)) {
            fromAddrTxtField.setForeground(disabledTextColor);
        }

        testAddressTxtField.setEnabled(enabled);

        makeOkBtnDefault();
        testBtn.setEnabled(enabled && isReadyToTest());

    }

    public void showTesting(boolean isTesting) {
        if (isTesting) {
            messageArea.setText(STLConstants.K5017_SENDING_EMAIL.getValue());
            testBtn.setEnabled(false);
        } else {
            messageArea.setText(STLConstants.K5018_EMAIL_SENT_OUT.getValue());
            testBtn.setEnabled(true);
        }
    }
}
