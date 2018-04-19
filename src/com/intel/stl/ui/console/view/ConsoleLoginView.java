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

package com.intel.stl.ui.console.view;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.IConsoleEventListener;
import com.intel.stl.ui.console.IConsoleLogin;
import com.intel.stl.ui.console.ITabListener;
import com.intel.stl.ui.console.LoginBean;

/**
 * Custom dialog view for logging into a remote host
 */
public class ConsoleLoginView extends JPanel implements IConsoleLogin {

    private static final long serialVersionUID = -8589239292130514515L;

    private JTextField txtFldUserName;

    private JPasswordField txtFldPassword;

    private JTextField txtFldHostName;

    private JTextField txtFldPortNum;

    private JTextArea txtAreaStatus;

    private JLabel lblStatusIcon;

    private JButton btnLogin;

    private JButton btnCancel;

    private DocumentListener setDirtyListener;

    private final IConsoleLoginListener loginListener;

    private IConsoleEventListener consoleEventListener;

    private final ITabListener tabListener;

    private LoginBean loginBean;

    private boolean newConsole;

    private int consoleId = 0;

    public ConsoleLoginView(IConsoleLoginListener parentDocListener,
            ITabListener tabListener) {

        this.loginListener = parentDocListener;
        this.tabListener = tabListener;
        createDocumentListener();
        initComponents();
    }

    protected void initComponents() {
        // Login View
        setLayout(new GridBagLayout());
        setBackground(UIConstants.INTEL_WHITE);
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        STLConstants.K1050_LOGIN.getValue()),
                BorderFactory.createEmptyBorder(5, 2, 5, 2)));

        // Gridbag Constraints
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(1, 10, 3, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.ipadx = 10;

        // JLabel: User Name
        gc.gridx = 0;
        gc.gridy = 0;
        JLabel lblUserName = ComponentFactory.getH5Label(
                STLConstants.K0602_USER_NAME.getValue() + ": ", Font.BOLD);
        lblUserName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblUserName, gc);

        // JTextFIeld: User Name
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridx++;
        gc.gridy = 0;
        txtFldUserName = ComponentFactory.createTextField(null, true, 40,
                setDirtyListener);
        txtFldUserName.setColumns(10);
        txtFldUserName.setName(WidgetName.ADMIN_CONSOLE_LOGIN_USERNAME.name());
        add(txtFldUserName, gc);

        // JLabel: Host Name
        gc.insets = new Insets(1, 3, 3, 2);
        gc.weightx = 0;
        gc.gridx++;
        gc.gridy = 0;
        JLabel lblHostName = ComponentFactory.getH5Label(
                STLConstants.K0051_HOST.getValue() + ": ", Font.BOLD);
        lblHostName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblHostName, gc);

        // JTextField: Host Name
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridx++;
        gc.gridy = 0;
        txtFldHostName = ComponentFactory.createTextField(null, true, 100,
                setDirtyListener);
        txtFldHostName.setColumns(10);
        txtFldHostName.setName(WidgetName.ADMIN_CONSOLE_LOGIN_HOST.name());
        add(txtFldHostName, gc);

        // JButton: Login
        gc.insets = new Insets(1, 15, 3, 2);
        gc.weightx = 0;
        gc.gridx++;
        gc.gridy = 0;
        btnLogin = ComponentFactory
                .getIntelActionButton(STLConstants.K1050_LOGIN.getValue());
        btnLogin.setEnabled(true);
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onLogin();
            }
        });
        btnLogin.setName(WidgetName.ADMIN_CONSOLE_LOGIN_BUTTON.name());
        add(btnLogin, gc);

        // JLabel: Password
        gc.insets = new Insets(2, 10, 2, 2);
        gc.gridx = 0;
        gc.gridy = 1;
        JLabel lblPasswordName = ComponentFactory.getH5Label(
                STLConstants.K1049_PASSWORD.getValue() + ": ", Font.BOLD);
        lblPasswordName.setHorizontalAlignment(JLabel.RIGHT);
        add(lblPasswordName, gc);

        // JPasswordField: Password
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridx++;
        gc.gridy = 1;
        txtFldPassword = ComponentFactory.createPasswordField(setDirtyListener);
        txtFldPassword.setInputVerifier(null);
        txtFldPassword.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    onLogin();
                }
            }
        });
        txtFldPassword.setName(WidgetName.ADMIN_CONSOLE_LOGIN_PASSWORD.name());
        add(txtFldPassword, gc);

        // JLabel: Port #
        gc.insets = new Insets(2, 3, 2, 2);
        gc.weightx = 0;
        gc.gridx++;
        gc.gridy = 1;
        JLabel lblPortNum = ComponentFactory.getH5Label(
                STLConstants.K1035_CONFIGURATION_PORT.getValue() + ": ",
                Font.BOLD);
        lblPortNum.setHorizontalAlignment(JLabel.RIGHT);
        add(lblPortNum, gc);

        // JTextField: Port #
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridx++;
        gc.gridy = 1;
        txtFldPortNum = ComponentFactory.createNumericTextField(65535,
                setDirtyListener);
        txtFldPortNum.setName(WidgetName.ADMIN_CONSOLE_LOGIN_PORT.name());
        add(txtFldPortNum, gc);

        // JButton: Cancel
        gc.insets = new Insets(2, 15, 2, 2);
        gc.weightx = 0;
        gc.gridx++;
        gc.gridy = 1;
        btnCancel = ComponentFactory
                .getIntelActionButton(STLConstants.K0621_CANCEL.getValue());
        btnCancel.setEnabled(true);
        btnCancel.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (newConsole) {
                    tabListener.closeConsole(tabListener.getCurrentTabView());
                }
                killProgress();
                hideLogin();
                setDirty();
                loginListener.enableLock(false);
                setVisible(false);
            }
        });
        btnCancel.setName(WidgetName.ADMIN_CONSOLE_CANCEL_BUTTON.name());
        add(btnCancel, gc);

        // Status Panel (Status Icon)
        gc.insets = new Insets(2, 10, 2, 2);
        gc.gridx = 0;
        gc.gridy = 2;
        lblStatusIcon = new JLabel();
        lblStatusIcon.setIcon(UIImages.RUNNING.getImageIcon());
        lblStatusIcon.setVisible(false);
        lblStatusIcon
                .setName(WidgetName.ADMIN_CONSOLE_LOGIN_STATUS_ICON.name());
        add(lblStatusIcon, gc);

        // Status Panel (Text Area)
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridy = 2;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        txtAreaStatus = new JTextArea();
        txtAreaStatus.setLineWrap(true);
        txtAreaStatus.setWrapStyleWord(true);
        txtAreaStatus.setOpaque(true);
        txtAreaStatus.setEditable(false);
        txtAreaStatus.setFont(UIConstants.H5_FONT);
        txtAreaStatus.setForeground(UIConstants.INTEL_DARK_GRAY);
        txtAreaStatus.setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
        txtAreaStatus.setPreferredSize(new Dimension(1, 40));
        txtAreaStatus.setName(WidgetName.ADMIN_CONSOLE_LOGIN_TEXT_AREA.name());
        add(txtAreaStatus, gc);
    }

    public void createDocumentListener() {

        setDirtyListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDirty();
            }
        };
    }

    protected void onLogin() {

        // Clear the status info
        txtAreaStatus.setText("");
        txtAreaStatus.setToolTipText(null);

        LoginBean loginBean = new LoginBean();
        loginBean.setUserName(getUserName());
        loginBean.setPassword(getPassword());
        loginBean.setHostName(getHostName());
        loginBean.setPortNum(getPortNum());

        startProgress();

        if (newConsole) {
            int id = consoleId;
            consoleEventListener.initializeConsoleThread(id, loginBean, null);
        } else {
            if (consoleId > 0) {
                consoleEventListener.onUnlockThread(consoleId, getPassword());
            }
        }
    }

    /**
     * @return the loginBean
     */
    public LoginBean getLoginBean() {
        return loginBean;
    }

    /**
     * @param loginBean
     *            the loginBean to set
     */
    public void setLoginBean(LoginBean loginBean) {
        this.loginBean = loginBean;
    }

    @Override
    public void showLogin(final LoginBean loginBean) {

        if (loginBean != null) {
            setLoginBean(loginBean);

            // Populate the fields with the default login information
            txtFldHostName.setText(loginBean.getHostName());
            txtFldPortNum.setText(loginBean.getPortNum());
        }

        // Enable the OK button
        if (getUserName().length() == 0) {
            txtFldUserName.requestFocus();
        } else if (getPassword().length == 0) {
            txtFldPassword.requestFocus();
        } else if (getHostName().length() == 0) {
            txtFldHostName.requestFocus();
        } else if (getPortNum().length() == 0) {
            txtFldPortNum.requestFocus();
        }
        setVisible(true);
        setDirty();
    }

    @Override
    public void showLogin(LoginBean loginBean, boolean newConsole,
            int consoleId) {

        this.newConsole = newConsole;
        this.consoleId = consoleId;

        // Set only password field enabled if this is an existing
        // console that was locked
        if (newConsole) {
            txtFldUserName.setEnabled(true);
            txtFldPassword.setEnabled(true);
            txtFldHostName.setEnabled(true);
            txtFldPortNum.setEnabled(true);
        } else {
            txtFldUserName.setEnabled(false);
            txtFldPassword.setEnabled(true);
            txtFldHostName.setEnabled(false);
            txtFldPortNum.setEnabled(false);
        }

        showLogin(loginBean);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleLogin#hideDialog()
     */
    @Override
    public void hideLogin() {

        Runnable hideIt = new Runnable() {

            @Override
            public void run() {
                txtFldPassword.getDocument()
                        .removeDocumentListener(setDirtyListener);
                txtFldPassword.setText("");
                txtFldPassword.getDocument()
                        .addDocumentListener(setDirtyListener);

                if (isVisible()) {
                    setVisible(false);
                }
                // setDirty();
            }
        };
        Util.runInEDT(hideIt);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.console.IConsoleLogin#showMessage(java.lang.String)
     */
    @Override
    public void showMessage(String message) {
        txtAreaStatus.setText(message);
        txtAreaStatus.setToolTipText(message);
    }

    protected void setDirty() {

        killProgress();

        if (txtAreaStatus != null) {
            showMessage(null);
        }

        /*-
         * PRR switches can be configure thru command loginmode to not require a user
         * and password. We are now allowing users to login without specifying either
         * of them
         * if ((getUserName().length() > 0) && (getPassword().length > 0)
         *         && (getHostName().length() > 0) && (getPortNum().length() > 0)) {
         */
        if ((getHostName().length() > 0) && (getPortNum().length() > 0)) {
            btnLogin.setEnabled(true);
        } else {
            btnLogin.setEnabled(false);
        }

        // Update the ConsoleTerminalView
        loginListener.updateUIComponents(btnLogin.isEnabled());

    }

    @Override
    public void startProgress() {
        lblStatusIcon.setVisible(true);
    }

    @Override
    public void killProgress() {
        lblStatusIcon.setVisible(false);
    }

    @Override
    public String getUserName() {
        return txtFldUserName.getText();
    }

    @Override
    public char[] getPassword() {
        return txtFldPassword.getPassword();
    }

    @Override
    public String getHostName() {
        return txtFldHostName.getText();
    }

    @Override
    public String getPortNum() {
        return txtFldPortNum.getText();
    }

    @Override
    public void setConsoleEventListener(
            IConsoleEventListener consoleEventListener) {
        this.consoleEventListener = consoleEventListener;
    }

}
