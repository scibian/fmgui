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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import com.intel.stl.ui.common.HelpController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.HelpView;
import com.intel.stl.ui.console.ConsoleDispatchManager;

/**
 * Top level view of the Console page with the subpage and help views on a
 * split pane
 */
public class ConsoleView extends JPanel {

    private static final long serialVersionUID = 3726017200596523114L;

    private ConsoleSubpageView consoleSubpageView;

    private HelpView consoleHelpView;

    private static final String HELP_SET_FILE =
            "GUID-F80D11C1-C5DF-4967-A7BB-F46A2828FEC9.hs";

    private static String title = STLConstants.K1056_FAST_FABRIC_ASSISTANT
            .getValue();

    public ConsoleView() {
        initComponents();
    }

    protected void initComponents() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(1, 5, 2, 5);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        gc.weighty = 1;

        JSplitPane spltPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spltPane.setContinuousLayout(true);
        spltPane.setResizeWeight(.2);

        HelpController consoleHelpController =
                new HelpController(title, HELP_SET_FILE);
        consoleHelpView = consoleHelpController.getView();
        spltPane.setRightComponent(consoleHelpView);

        consoleSubpageView = new ConsoleSubpageView(consoleHelpController);
        spltPane.setLeftComponent(consoleSubpageView.getMainComponent());

        add(spltPane, gc);
    }

    public ConsoleSubpageView getConsoleSubpageView() {
        return consoleSubpageView;
    }

    public void enableHelp(boolean b) {
        consoleSubpageView.enableHelp(b);
    }

    public JButton getHelpButton() {
        return consoleSubpageView.getHelpButton();
    }

    /**
     * @return the consoleHelpView
     */
    public HelpView getConsoleHelpView() {
        return consoleHelpView;
    }

    public void setConsoleDispatchManager(ConsoleDispatchManager dispatchManager) {
        this.consoleSubpageView.setConsoleListener(dispatchManager);
    }

}
