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

package com.intel.stl.ui.admin.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.TitledBorder;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.LoginBean;

public class LoginPanel extends JPanel {

    protected JFormattedTextField hostField;

    protected JFormattedTextField userField;

    protected JFormattedTextField portField;

    protected JPasswordField passwordField;

    private LoginBean credentials = new LoginBean();

    private JTextArea messageArea;

    protected JProgressBar progressBar;

    protected ILoginListener listener;

    private JButton cancelBtn;

    private JButton loginBtn;

    protected JPanel btnPanel;

    protected GridBagConstraints gc;

    private static final long serialVersionUID = -3922793376630351870L;

    // Constructor to create this class when the login listener isn't ready yet
    public LoginPanel() {
        initLoginPanel();
    }

    public LoginPanel(ILoginListener listener) {
        this.listener = listener;
        initLoginPanel();
    }

    protected void initLoginPanel() {
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(UIConstants.INTEL_BLUE, 2),
                STLConstants.K1050_LOGIN.getValue(), TitledBorder.LEFT,
                TitledBorder.TOP, UIConstants.H5_FONT.deriveFont(Font.BOLD)));

        setLayout(new GridBagLayout());
        gc = new GridBagConstraints();
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;

        // Add a message area for errors
        messageArea = createMessageArea();
        this.add(messageArea, gc);

        // Add Host, Port, User, and Password fields
        int row = addCredentialFields(2);

        gc.gridx = 1;
        gc.gridy = row++;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.anchor = GridBagConstraints.CENTER;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        progressBar = createProgressBar();
        this.add(progressBar, gc);

        // Create the button panel for Cancel and Login
        gc.gridx = 1;
        gc.gridy = row;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        btnPanel = createButtonPanel();
        this.add(btnPanel, gc);

        this.setBackground(UIConstants.INTEL_WHITE);
        this.setPreferredSize(new Dimension(400, 400));
    }

    protected JTextArea createMessageArea() {
        messageArea = new JTextArea();

        // Initial message to user to request login credentials for the server
        messageArea.setText("");
        messageArea.setFont(UIConstants.H5_FONT.deriveFont(Font.PLAIN));
        messageArea.setWrapStyleWord(true);
        messageArea.setLineWrap(true);
        messageArea.setEditable(false);
        messageArea.setFocusable(false);

        return messageArea;
    }

    protected JProgressBar createProgressBar() {
        progressBar = new JProgressBar();
        progressBar.setStringPainted(false);

        return progressBar;
    }

    protected int addCredentialFields(int row) {
        gc.gridx = 0;
        gc.gridy = row;
        gc.insets = new Insets(10, 10, 10, 10);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 0;
        gc.gridwidth = 1;
        JLabel label =
                ComponentFactory.getH5Label(STLConstants.K0051_HOST.getValue()
                        + " :", Font.BOLD);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label, gc);

        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.gridx = 1;
        hostField =
                ComponentFactory.createTextField(null, true, 100,
                        (DocumentListener[]) null);
        hostField.setText("");
        hostField.setEnabled(true);

        // For authentication the host name is being taken from the HostInfo;
        // in most cases it is not editable. In case if you need to make
        // this field editable, call method setHostFieldEditable(true)
        hostField.setEditable(false);
        this.add(hostField, gc);

        row++;
        gc.gridx = 0;
        gc.gridy = row;
        gc.weightx = 0;
        gc.gridwidth = 1;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K0404_PORT_NUMBER.getValue() + " :",
                        Font.BOLD);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        portField =
                ComponentFactory.createTextField("0123456789", false, 5,
                        (DocumentListener[]) null);
        // Set to default ssh port
        portField.setText("22");
        this.add(portField, gc);

        row++;
        gc.weightx = 0;
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K0602_USER_NAME.getValue() + " :",
                        Font.BOLD);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label, gc);

        gc.gridx = 1;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        userField =
                ComponentFactory.createTextField(null, true, 40,
                        (DocumentListener[]) null);
        userField.setText("");
        this.add(userField, gc);

        row++;
        gc.weightx = 0;
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 1;
        label =
                ComponentFactory.getH5Label(
                        STLConstants.K1049_PASSWORD.getValue() + " :",
                        Font.BOLD);
        label.setHorizontalAlignment(SwingConstants.RIGHT);
        this.add(label, gc);

        gc.weightx = 1;
        gc.gridx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        passwordField =
                ComponentFactory.createPasswordField((DocumentListener[]) null);
        passwordField.setInputVerifier(null);
        passwordField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginBtnAction();
            }

        });
        this.add(passwordField, gc);
        row++;

        return row;
    }

    protected JPanel createButtonPanel() {
        cancelBtn =
                ComponentFactory.getIntelActionButton(STLConstants.K0621_CANCEL
                        .getValue());
        cancelBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelBtnAction();
            }
        });

        btnPanel = new JPanel();
        btnPanel.setLayout(new GridBagLayout());
        btnPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.weightx = 1;
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 0, 10, 10);

        btnPanel.add(cancelBtn, gbc);

        loginBtn =
                ComponentFactory.getIntelActionButton(STLConstants.K1050_LOGIN
                        .getValue());
        loginBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loginBtnAction();
            }

        });

        gbc.gridx = 1;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        btnPanel.add(loginBtn, gbc);

        return btnPanel;
    }

    protected void loginBtnAction() {
        String hostName = hostField.getText();
        credentials.setHostName(hostName);
        credentials.setPortNum(portField.getText());
        credentials.setUserName(userField.getText());
        credentials.setPassword(passwordField.getPassword());

        // Clear the message area
        setMessage("");

        if (!(hostName == null || hostName.isEmpty())) {
            // If valid credentials have been provided by user, hide login card
            progressBar.setIndeterminate(true);
            listener.credentialsReady();

        } else {
            // Show error message in login card...
            setMessage(STLConstants.K5004_LOGIN_ERROR_MSG.getValue());
        }

    }

    protected void cancelBtnAction() {
        listener.cancelLogin();
        progressBar.setIndeterminate(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#setEnabled(boolean)
     */
    @Override
    public void setEnabled(boolean enabled) {
        hostField.setEnabled(enabled);
        userField.setEnabled(enabled);
        portField.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        cancelBtn.setEnabled(enabled);
        loginBtn.setEnabled(enabled);
        super.setEnabled(enabled);
    }

    /**
     * <i>Description:</i> Clear the password text field and the password data
     * saved in the LoginBean credentials.
     * 
     */
    public void clearLoginData() {
        // Clear the password text field
        DocumentListener[] listeners =
                ((AbstractDocument) passwordField.getDocument())
                        .getDocumentListeners();
        for (DocumentListener listener : listeners) {
            passwordField.getDocument().removeDocumentListener(listener);
        }
        passwordField.setText("");
        passwordField.setCaretPosition(0);
        for (DocumentListener listener : listeners) {
            passwordField.getDocument().addDocumentListener(listener);
        }

        // Stop progress bar
        progressBar.setIndeterminate(false);

        // Clear the password in the LoginBean:
        credentials.setPassword(null);
    }

    public LoginBean getCredentials() {
        return credentials;
    }

    public void setCredentials(LoginBean credentials) {
        this.credentials = credentials;
    }

    public void setHostNameField(String host) {
        hostField.setText(host);
    }

    public void setUserNameField(String userName) {
        userField.setText(userName);
    }

    /**
     * <i>Description:<i> Set the port number in the event the normal SSH port
     * (22) isn't being used
     */
    public void setPortNumber(String portNum) {
        portField.setText(portNum);
    }

    /**
     * <i>Description:</i> Set message text to appear at the top of the login
     * panel.
     */
    public void setMessage(String msg) {
        messageArea.setText(msg);
    }

    /*
     * Show infinite progress while logging in.
     */
    public void showProgress(boolean show) {
        progressBar.setIndeterminate(show);
    }

    public String getUserNameFieldStr() {
        return userField.getText();
    }

    public String getPortFieldStr() {
        return portField.getText();
    }

    /**
     * Set the login listener
     */
    public void setListener(ILoginListener listener) {
        this.listener = listener;
    }

    /*
     * Make the Host name text field editable. This might be needed for other
     * use cases of the login panel.
     */
    public void setHostFieldEditable(boolean editable) {
        hostField.setEditable(editable);
    }

    public void setHostFieldEnabled(boolean b) {
        hostField.setEnabled(b);
    }
}
