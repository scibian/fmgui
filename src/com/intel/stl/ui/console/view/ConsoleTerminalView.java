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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IntelComboBoxUI;
import com.intel.stl.ui.console.ConsoleDispatchManager;
import com.intel.stl.ui.console.IConsoleListener;
import com.intel.stl.ui.console.IConsoleLogin;
import com.intel.stl.ui.console.ITabListener;
import com.intel.stl.ui.console.LoginBean;
import com.intel.stl.ui.main.view.IFabricView;

/**
 * View for the overall console subpage containing: 1. Info panel with Host,
 * Port, and User 2. Command panel with command field and current/new radio
 * buttons 3. SSH console into selected remote system
 */
public class ConsoleTerminalView extends JPanel
        implements IConsoleLoginListener {

    private static final long serialVersionUID = 4230538658726413678L;

    private JPanel serverInfoPanel;

    private JLabel lblHostValue;

    private JLabel lblPortValue;

    private JLabel lblUserValue;

    private JButton btnLock;

    private JComboBox<String> boxCommand;

    private JButton btnSend;

    private JPanel optionPanel;

    private JRadioButton rbtnCurrentTab;

    private JRadioButton rbtnNewTab;

    private TerminalCardView terminalCardView;

    private String hostName = new String("");

    private String portNum = new String("");

    private String userName = new String("");

    private LoginBean loginBean;

    private IConsoleListener consoleListener;

    private final ITabListener tabListener;

    private JPanel pnlControl;

    private final IFabricView owner;

    private ConsoleLoginView loginView;

    // Added this comment to correct PR 126675 comment above
    public ConsoleTerminalView(IFabricView owner, ITabListener tabListener) {
        super();
        this.owner = owner;
        this.tabListener = tabListener;
        initComponents();
        createButtonGroup();
    }

    protected void initComponents() {
        setLayout(new BorderLayout(0, 5));
        setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));

        JPanel pnlInfo = new JPanel();
        pnlInfo.setLayout(new BorderLayout(0, 0));

        loginView = new ConsoleLoginView(this, tabListener);
        pnlInfo.add(loginView, BorderLayout.NORTH);

        pnlControl = getControlPanel();
        pnlInfo.add(pnlControl, BorderLayout.CENTER);

        add(pnlInfo, BorderLayout.NORTH);

        terminalCardView =
                new TerminalCardView(STLConstants.K2107_ADM_CONSOLE.getValue());
        add(terminalCardView, BorderLayout.CENTER);
    }

    public void enableHelp(boolean b) {
        terminalCardView.enableHelp(b);
    }

    public JButton getHelpButton() {
        return terminalCardView.getHelpButton();
    }

    protected JPanel getControlPanel() {

        JPanel ctrPanel = new JPanel();

        ctrPanel = new JPanel(new GridBagLayout());
        ctrPanel.setBorder(BorderFactory.createTitledBorder((String) null));
        ctrPanel.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(1, 10, 3, 5);
        gc.gridwidth = 1;
        gc.weightx = 0;

        JLabel server = ComponentFactory.getH4Label(
                STLConstants.K1053_SERVER_INFO.getValue(), Font.BOLD);
        ctrPanel.add(server, gc);

        gc.weightx = 1;
        JPanel serverInfo = getServerInfoPanel();
        ctrPanel.add(serverInfo, gc);

        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        btnLock = ComponentFactory
                .getIntelDeleteButton(STLConstants.K1051_LOCK.getValue());
        btnLock.setSelected(true);
        btnLock.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = btnLock.isSelected();

                if (isSelected) {
                    consoleListener.onLock(isSelected);
                    toggleLock(!isSelected);
                    consoleListener.hideLoginPanel();
                } else {
                    consoleListener.showLoginPanel();
                }

                btnLock.setSelected(!isSelected);
                if (isSelected) {
                    btnLock.setText(STLConstants.K1052_UNLOCK.getValue());
                } else {
                    btnLock.setText(STLConstants.K1051_LOCK.getValue());
                }
            }
        });
        btnLock.setName(WidgetName.ADMIN_CONSOLE_LOCK.name());
        ctrPanel.add(btnLock, gc);

        gc.insets = new Insets(2, 10, 3, 5);
        gc.gridwidth = 1;
        JLabel cmd = ComponentFactory.getH4Label(
                STLConstants.K1044_COMMAND_TITLE.getValue(), Font.BOLD);
        ctrPanel.add(cmd, gc);

        gc.weightx = 1;
        boxCommand = new JComboBox<String>();
        boxCommand.setUI(new IntelComboBoxUI());
        boxCommand.setEditable(true);
        AutoCompleteDecorator.decorate(boxCommand);
        boxCommand.getEditor().getEditorComponent()
                .addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyReleased(KeyEvent event) {
                        if (event.getKeyChar() == KeyEvent.VK_ENTER) {
                            commandSendAction();
                        }
                    }
                });
        boxCommand.addItemListener(new ItemListener() {

            @Override
            public void itemStateChanged(ItemEvent arg0) {

                // Pass everything that was typed on the command line
                // to the parser
                String entry = ((JTextField) boxCommand.getEditor()
                        .getEditorComponent()).getText();

                if (entry.startsWith("iba_report -o ")) {
                    entry = "iba_report -reporttypes";
                }

                if (!entry.isEmpty()) {
                    consoleListener.getHelpController().parseCommand(entry);

                    // Only pass the command name to the Help comboBox
                    if (entry.split(" ").length > 0) {
                        consoleListener.getHelpController()
                                .updateSelection(entry.split(" ")[0]);
                    }
                    consoleListener.setLastCommand(entry);
                }
            }

        });
        boxCommand.setName(WidgetName.ADMIN_CONSOLE_COMMAND_BOX.name());
        ctrPanel.add(boxCommand, gc);

        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        btnSend = ComponentFactory
                .getIntelActionButton(STLConstants.K1045_SEND.getValue());
        btnSend.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                commandSendAction();
            }
        });
        btnSend.setName(WidgetName.ADMIN_CONSOLE_SEND.name());
        ctrPanel.add(btnSend, gc);

        gc.gridwidth = 2;
        gc.weightx = 1;
        JPanel optionPanel = getOptionPanel();
        ctrPanel.add(optionPanel, gc);

        return ctrPanel;
    }

    protected JPanel getServerInfoPanel() {
        if (serverInfoPanel == null) {
            serverInfoPanel = new JPanel(new GridBagLayout());
            serverInfoPanel.setOpaque(false);

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(1, 2, 2, 2);
            gc.gridwidth = 1;
            gc.weightx = 0;

            JLabel lblHost = ComponentFactory.getH4Label(
                    STLConstants.K0051_HOST.getValue() + ": ", Font.PLAIN);
            serverInfoPanel.add(lblHost, gc);

            gc.weightx = 1;
            lblHostValue = ComponentFactory.getH4Label("N/A", Font.ITALIC);
            lblHostValue.setName(WidgetName.ADMIN_CONSOLE_TERMINAL_HOST.name());
            serverInfoPanel.add(lblHostValue, gc);

            gc.weightx = 0;
            JLabel lblPort = ComponentFactory.getH4Label(
                    STLConstants.K1035_CONFIGURATION_PORT.getValue() + ": ",
                    Font.PLAIN);
            serverInfoPanel.add(lblPort, gc);

            gc.weightx = 1;
            lblPortValue = ComponentFactory.getH4Label("N/A", Font.ITALIC);
            lblPortValue.setName(WidgetName.ADMIN_CONSOLE_TERMINAL_PORT.name());
            serverInfoPanel.add(lblPortValue, gc);

            gc.weightx = 0;
            JLabel lblUser = ComponentFactory.getH4Label(
                    STLConstants.K0602_USER_NAME.getValue() + ": ", Font.PLAIN);
            serverInfoPanel.add(lblUser, gc);

            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.weightx = 1;
            lblUserValue = ComponentFactory.getH4Label("N/A", Font.ITALIC);
            lblUserValue
                    .setName(WidgetName.ADMIN_CONSOLE_TERMINAL_USERNAME.name());
            serverInfoPanel.add(lblUserValue, gc);
        }
        return serverInfoPanel;
    }

    protected JPanel getOptionPanel() {
        if (optionPanel == null) {
            optionPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING, 5, 0));
            optionPanel.setOpaque(false);

            rbtnCurrentTab =
                    new JRadioButton(STLConstants.K1046_CURRENT_TAB.getValue());
            rbtnCurrentTab.setOpaque(false);
            rbtnCurrentTab.setFont(UIConstants.H5_FONT);
            rbtnCurrentTab.setForeground(UIConstants.INTEL_DARK_GRAY);
            rbtnCurrentTab.setName(WidgetName.ADMIN_CONSOLE_CURRENT_TAB.name());
            rbtnCurrentTab.setSelected(true);
            optionPanel.add(rbtnCurrentTab);

            rbtnNewTab =
                    new JRadioButton(STLConstants.K1047_NEW_TAB.getValue());
            rbtnNewTab.setOpaque(false);
            rbtnNewTab.setFont(UIConstants.H5_FONT);
            rbtnNewTab.setForeground(UIConstants.INTEL_DARK_GRAY);
            rbtnNewTab.setName(WidgetName.ADMIN_CONSOLE_NEW_TAB.name());
            rbtnNewTab.setSelected(false);
            optionPanel.add(rbtnNewTab);
        }
        return optionPanel;
    }

    protected void commandSendAction() {
        String command = (String) boxCommand.getSelectedItem();

        // If selection is current tab, then put the
        // command on this console's queue. Otherwise, create a new
        // console and add the command to that queue.
        if ((command != null) && (command.length() != 0)) {
            addCommand(command);
            boxCommand.getEditor().selectAll();

            if (isCurrentTabSelected()) {
                consoleListener.addToQueue(command);
                consoleListener.updatePersonalizedTab(command);

            } else if (isNewTabSelected()) {

                if (consoleListener.isConsoleAllowed()) {
                    consoleListener.addNewConsole(command);
                    boxCommand.setSelectedIndex(-1);
                    rbtnNewTab.setSelected(false);
                    rbtnCurrentTab.setSelected(true);
                } else {
                    Util.showErrorMessage(this,
                            UILabels.STL80009_MAX_CHANNELS_IN_SESSION
                                    .getDescription(
                                            ConsoleDispatchManager.MAX_NUM_CONSOLES_IN_SESSION));
                }
            }
            boxCommand.getEditor().setItem("");
        }

    }

    public void addCommand(String cmd) {
        for (int i = 0; i < boxCommand.getItemCount(); i++) {
            String history = boxCommand.getItemAt(i);
            if (cmd.equals(history)) {
                return;
            }
        }

        boxCommand.addItem(cmd);
    }

    public void setCmdFieldEnable(boolean enable) {
        boxCommand.setEnabled(enable);
    }

    protected void createButtonGroup() {
        ButtonGroup rbtnGroup = new ButtonGroup();
        rbtnGroup.add(rbtnCurrentTab);
        rbtnGroup.add(rbtnNewTab);
    }

    /**
     * @return the hostName
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * @param hostName
     *            the hostName to set
     */
    public void setHostName(String hostName) {
        this.hostName = hostName;
        this.lblHostValue.setText(hostName);
    }

    /**
     * @return the portNum
     */
    public String getPortNum() {
        return portNum;
    }

    /**
     * @param portNum
     *            the portNum to set
     */
    public void setPortNum(String portNum) {
        this.portNum = portNum;
        this.lblPortValue.setText(portNum);
    }

    /**
     * @return the userName
     */
    public String getUserName() {
        return userName;
    }

    /**
     * @param userName
     *            the userName to set
     */
    public void setUserName(String userName) {
        this.userName = userName;
        this.lblUserValue.setText(userName);
    }

    /**
     * @return the terminalView
     */
    public void setTermPanel(IntelTerminalView panel) {
        terminalCardView.setTermPanel(panel);
    }

    public void displayMaxConsoles(boolean enable) {
        terminalCardView.displayMaxConsoles(enable);
    }

    public void setConsoleListener(IConsoleListener listener) {
        consoleListener = listener;
    }

    public boolean isCurrentTabSelected() {
        return rbtnCurrentTab.isSelected();
    }

    public boolean isNewTabSelected() {
        return rbtnNewTab.isSelected();
    }

    public void setFocus() {

        boxCommand.requestFocus();
    }

    public void enableCommanding(boolean enable) {
        boxCommand.setEnabled(enable);
        btnSend.setEnabled(enable);
    }

    public void enableNewTab(boolean enable) {
        rbtnNewTab.setEnabled(enable);
    }

    public IConsoleLogin getConsoleLogin() {
        return loginView;
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

    public Object[] getPassword(String username) {
        final JPasswordField pwdFld = new JPasswordField();

        int result = JOptionPane.showConfirmDialog((Component) owner,
                new Object[] { STLConstants.K1065_ENTER_PASSWORD.getValue()
                        + " " + userName, pwdFld },
                STLConstants.K1064_SESSION_AUTHENTICATION.getValue(),
                JOptionPane.OK_CANCEL_OPTION);

        String password = new String(pwdFld.getPassword());

        return new Object[] { result, password };
    }

    public void toggleLock(boolean isSelected) {
        terminalCardView.setLocked(!isSelected);
        btnSend.setEnabled(isSelected);
        boxCommand.setEnabled(isSelected);
        consoleListener.getTerminal().enableKeyHandler(isSelected);

        if (!isSelected) {
            btnLock.setText(STLConstants.K1052_UNLOCK.getValue());
        } else {
            btnLock.setText(STLConstants.K1051_LOCK.getValue());
        }

        btnLock.setSelected(isSelected);
    }

    @Override
    public void updateUIComponents(boolean state) {

        if (loginView.isVisible()) {
            btnLock.setEnabled(state);
        } else {
            btnLock.setEnabled(true);
        }
    }

    @Override
    public void enableLock(boolean state) {
        btnLock.setSelected(state);
        toggleLock(state);
    }

    @Override
    public int getControlPanelWidth() {
        return pnlControl.getPreferredSize().width;
    }
}
