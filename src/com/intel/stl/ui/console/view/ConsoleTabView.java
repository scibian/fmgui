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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.IConsoleListener;
import com.intel.stl.ui.console.ITabListener;

/**
 * View for the tab on a console subpage tabbed pane to house the labels for
 * user name, command name, and close button
 */
public class ConsoleTabView extends JPanel {

    private static final long serialVersionUID = -4025941288575285019L;

    private static final byte USER_NAME_IDX = 0;

    private static final byte COMMAND_NAME_IDX = 1;

    private JLabel lblCloseTab;

    private JLabel lblUserName;

    private JLabel lblCommandName;

    private final String[] tabNames;

    private ITabListener tabListener;

    private int tabIndex;

    private final int consoleId;

    @SuppressWarnings("unused")
    private IConsoleListener consoleListener;

    private final ConsoleTabView tabView = this;

    public ConsoleTabView(int tabIndex, String[] tabNames, int consoleId) {
        super();
        this.tabIndex = tabIndex;
        this.tabNames = tabNames;
        this.consoleId = consoleId;
        initComponent();
    }

    protected void initComponent() {

        setLayout(new GridBagLayout());
        setOpaque(false);
        lblUserName = ComponentFactory.getH4Label("", Font.PLAIN);

        lblCloseTab = new JLabel(UIImages.CLOSE_GRAY.getImageIcon());
        lblCloseTab.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(0, 2, 0, 0, UIConstants.INTEL_BORDER_GRAY),
                BorderFactory.createEmptyBorder(0, 5, 0, 0)));

        lblCommandName = ComponentFactory.getH4Label("", Font.PLAIN);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(1, 2, 1, 2);
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.gridy = 0;
        add(lblUserName, gc);

        gc.gridx = 1;
        gc.gridheight = 2;
        gc.weighty = 1;
        gc.insets = new Insets(1, 2, 1, 2);
        add(lblCloseTab, gc);

        gc.gridx = 0;
        gc.gridy = 1;
        gc.weighty = 0;
        gc.insets = new Insets(1, 2, 1, 2);
        add(lblCommandName, gc);

        setUserName(tabNames[USER_NAME_IDX]);
        setCommandName(tabNames[COMMAND_NAME_IDX]);
    }

    public void addConsoleListener(IConsoleListener listener) {
        this.consoleListener = listener;
    }

    public void addTabListener(ITabListener listener) {

        tabListener = listener;

        lblCloseTab.addMouseListener((new MouseAdapter() {
            private Icon oldIcon;

            @Override
            public void mouseClicked(MouseEvent e) {

                Runnable runit = new Runnable() {

                    @Override
                    public void run() {
                        tabListener.closeConsole(tabView);
                    }
                };
                runit.run();
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                oldIcon = lblCloseTab.getIcon();
                lblCloseTab.setIcon(UIImages.CLOSE_RED.getImageIcon());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblCloseTab.setIcon(oldIcon);
            }

        }));
    }

    public JPanel getMainComponent() {
        return this;
    }

    /**
     * @return the lblUserName
     */
    public String getUserName() {
        return lblUserName.getText();
    }

    /**
     * @param lblUserName
     *            the lblUserName to set
     */
    public void setUserName(String userName) {
        this.lblUserName.setText(userName);
    }

    /**
     * @return the lblCommandName
     */
    public String getCommandName() {
        return lblCommandName.getText();
    }

    /**
     * @param lblCommandName
     *            the lblCommandName to set
     */
    public void setCommandName(String command) {
        this.lblCommandName.setText(getCommandName(command));
    }

    protected String getCommandName(String cmd) {
        if (cmd == null) {
            return "";
        }

        int pos = cmd.indexOf(' ');
        if (pos >= 0) {
            return cmd.substring(0, pos);
        } else {
            return cmd;
        }
    }

    /**
     * @return the tabIndex
     */
    public int getTabIndex() {
        return tabIndex;
    }

    public void setTabIndex(int index) {
        tabIndex = index;
    }

    /**
     * @return the consoleId
     */
    public int getConsoleId() {
        return consoleId;
    }

    public void setLabelProperties(boolean highlight) {

        if (highlight) {
            lblUserName.setBackground(UIConstants.INTEL_WHITE);
            lblUserName.setForeground(UIConstants.INTEL_DARK_GRAY);

            lblCloseTab.setIcon(UIImages.CLOSE_GRAY.getImageIcon());

            lblCommandName.setBackground(UIConstants.INTEL_WHITE);
            lblCommandName.setForeground(UIConstants.INTEL_DARK_GRAY);
        } else {
            lblUserName.setBackground(UIConstants.INTEL_BLUE);
            lblUserName.setForeground(UIConstants.INTEL_WHITE);

            lblCloseTab.setIcon(UIImages.CLOSE_WHITE.getImageIcon());

            lblCommandName.setBackground(UIConstants.INTEL_BLUE);
            lblCommandName.setForeground(UIConstants.INTEL_WHITE);
        }
    }

}
