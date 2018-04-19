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

package com.intel.stl.ui.admin.view.logs;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.JXLabel;

import com.intel.stl.ui.admin.impl.SMLogModel;
import com.intel.stl.ui.admin.impl.logs.ILogViewListener;
import com.intel.stl.ui.admin.view.LoginPanel;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.LoginBean;
import com.intel.stl.ui.model.LogConfigTypeViz;

public class SMLoginView extends LoginPanel {

    private static final long serialVersionUID = -2884097977886279977L;

    private JLabel lblLogFileName;

    private JFormattedTextField txtfldLogFilePath;

    private JRadioButton rbAutoConfig;

    private JRadioButton rbCustomConfig;

    private ILogViewListener logViewListener;

    public void setLoginViewListener(ILogViewListener listener) {
        logViewListener = listener;
    }

    @Override
    protected void initLoginPanel() {
        super.initLoginPanel();
        removeAll();

        // Add a message area for errors
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(10, 75, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        this.add(createMessageArea(), gc);

        // Add a panel to hold the radio buttons
        gc.gridx = 0;
        gc.gridy = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.WEST;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.insets = new Insets(0, 75, 0, 10);
        add(createConfigPanel(), gc);

        // Add the credentials fields
        int row = addCredentialFields(2);

        // Add the log file name label
        gc.weightx = 0.1;
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        lblLogFileName =
                ComponentFactory.getH5Label(
                        STLConstants.K2163_LOG_FILE.getValue() + " :",
                        Font.BOLD);
        lblLogFileName.setHorizontalAlignment(SwingConstants.RIGHT);
        add(lblLogFileName, gc);

        // Add the log file name text field
        gc.weightx = 0.8;
        gc.gridx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        txtfldLogFilePath =
                ComponentFactory.createTextField(null, true, 40,
                        (DocumentListener[]) null);
        txtfldLogFilePath.setText("");
        txtfldLogFilePath.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent event) {
                if (event.getKeyChar() == KeyEvent.VK_ENTER) {
                    loginBtnAction();
                }
            }
        });
        add(txtfldLogFilePath, gc);

        // Add the progress bar
        row++;
        gc.gridx = 1;
        gc.gridy = row;
        gc.weightx = 1;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(createProgressBar(), gc);

        // Add the button panel
        row++;
        gc.gridx = 1;
        gc.gridy = row;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        add(createButtonPanel(), gc);

        // Add a label with a message explaining the ESM Syslog
        row++;
        gc.gridx = 0;
        gc.gridy = row;
        gc.anchor = GridBagConstraints.WEST;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        JPanel pnlEsmNote = createEsmNotePanel();
        this.add(pnlEsmNote, gc);

        // Set Login panel size
        this.setPreferredSize(new Dimension(400, 550));

        // Enable the host field
        setHostFieldEditable(true);

        // Default the form to disabled for auto configuration
        enableForm(false);
    }

    protected JPanel createEsmNotePanel() {

        JPanel pnlEsmNote = new JPanel(new BorderLayout());
        pnlEsmNote.setBorder(BorderFactory
                .createLineBorder(UIConstants.INTEL_GRAY));
        JXLabel lblEsmNote =
                new JXLabel(UILabels.STL50219_ESM_SYSLOG_NOTE.getDescription());
        lblEsmNote.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        lblEsmNote.setForeground(UIConstants.INTEL_BLUE);
        lblEsmNote.setLineWrap(true);
        lblEsmNote.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlEsmNote.add(lblEsmNote, BorderLayout.CENTER);

        return pnlEsmNote;
    }

    protected JPanel createConfigPanel() {

        JPanel pnlConfig = new JPanel();
        pnlConfig.setLayout(new BoxLayout(pnlConfig, BoxLayout.X_AXIS));
        pnlConfig.setBackground(UIConstants.INTEL_WHITE);

        // Create the auto configuration radio button
        rbAutoConfig =
                new JRadioButton(LogConfigTypeViz.AUTO_CONFIG.getValue());
        rbAutoConfig.setOpaque(false);
        rbAutoConfig.setFont(UIConstants.H5_FONT);
        rbAutoConfig.setForeground(UIConstants.INTEL_DARK_GRAY);
        rbAutoConfig.setSelected(true);
        rbAutoConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                enableForm(false);
                restoreAutoConfigView();
            }
        });

        // Create the custom configuration radio button
        rbCustomConfig =
                new JRadioButton(LogConfigTypeViz.CUSTOM_CONFIG.getValue());
        rbCustomConfig.setOpaque(false);
        rbCustomConfig.setFont(UIConstants.H5_FONT);
        rbCustomConfig.setForeground(UIConstants.INTEL_DARK_GRAY);
        rbCustomConfig.setSelected(false);
        rbCustomConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                enableForm(true);
            }
        });

        // Add the radio buttons to a button group to make them mutually
        // exclusive
        ButtonGroup grpButtons = new ButtonGroup();
        grpButtons.add(rbAutoConfig);
        grpButtons.add(rbCustomConfig);

        // Add the radio buttons to the panel
        pnlConfig.add(Box.createHorizontalStrut(45));
        pnlConfig.add(rbAutoConfig);
        pnlConfig.add(Box.createHorizontalStrut(50));
        pnlConfig.add(rbCustomConfig);

        return pnlConfig;
    }

    public void enableForm(boolean b) {

        if (rbCustomConfig != null) {
            rbCustomConfig.setSelected(b);
        }

        if (rbAutoConfig != null) {
            rbAutoConfig.setSelected(!b);
        }

        if (hostField != null) {
            hostField.setEnabled(b);
        }

        if (portField != null) {
            portField.setEnabled(b);
        }

        if (txtfldLogFilePath != null) {
            txtfldLogFilePath.setEnabled(b);
        }
    }

    public LogConfigTypeViz getConfigType() {

        return rbAutoConfig.isSelected() ? LogConfigTypeViz.AUTO_CONFIG
                : LogConfigTypeViz.CUSTOM_CONFIG;
    }

    public String getLogFilePath() {
        return txtfldLogFilePath.getText();
    }

    @Override
    protected void cancelBtnAction() {
        super.cancelBtnAction();
        logViewListener.updateLoginView();
    }

    public void resetLogin() {
        clearLoginData();
    }

    public void updateView(SMLogModel model) {
        LoginBean credentials = model.getCredentials();
        hostField.setText(credentials.getHostName());
        portField.setText(credentials.getPortNum());
        userField.setText(credentials.getUserName());
        passwordField.setText("");
        txtfldLogFilePath.setText(model.getLogFilePath());
        enableForm(getConfigType().equals(LogConfigTypeViz.CUSTOM_CONFIG));
    }

    protected void restoreAutoConfigView() {
        logViewListener.restoreAutoConfigView();
    }
}
