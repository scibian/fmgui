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
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.console.IConsoleEventListener;
import com.intel.stl.ui.console.ITabListener;
import com.intel.stl.ui.console.LoginBean;

/**
 * View for the tab on a console subpage tabbed pane to house the labels for
 * adding ("+") a new console.
 */
public class NewTabView extends JPanel implements ITabListener {

    private static final long serialVersionUID = 6934999496353209087L;

    private final IConsoleEventListener consoleEventListener;

    private JLabel lblPlus;

    private final LoginBean defaultLoginBean;

    public NewTabView(LoginBean defaultLoginBean,
            IConsoleEventListener consoleEventListener) {

        this.defaultLoginBean = defaultLoginBean;
        this.consoleEventListener = consoleEventListener;
        initComponents();
    }

    protected void initComponents() {
        setLayout(new BorderLayout());
        setBackground(UIConstants.INTEL_BLUE);

        lblPlus = ComponentFactory.getH2Label("+", Font.PLAIN);
        lblPlus.setBackground(UIConstants.INTEL_BLUE);
        lblPlus.setForeground(UIConstants.INTEL_WHITE);
        add(lblPlus, BorderLayout.CENTER);
        addPlusLabelMouseListener();
    }

    protected void addPlusLabelMouseListener() {
        lblPlus.addMouseListener(new MouseListener() {

            @Override
            public void mouseClicked(MouseEvent e) {
                consoleEventListener.addNewConsole(defaultLoginBean, true, "");
            }

            @Override
            public void mousePressed(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // TODO Auto-generated method stub

            }

            @Override
            public void mouseEntered(MouseEvent e) {
                lblPlus.setForeground(UIConstants.INTEL_RED);

            }

            @Override
            public void mouseExited(MouseEvent e) {
                lblPlus.setForeground(UIConstants.INTEL_WHITE);

            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.console.ITabListener#closeConsole(com.intel.stl.ui.console
     * .view.ConsoleTabView)
     */
    @Override
    public void closeConsole(ConsoleTabView tabView) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITabListener#enableNewTab(boolean)
     */
    @Override
    public void enableNewTab(boolean enabled) {

        lblPlus.setEnabled(enabled);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITabListener#getNewTabView()
     */
    @Override
    public NewTabView getNewTabView() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.console.ITabListener#getCurrentTabView()
     */
    @Override
    public ConsoleTabView getCurrentTabView() {
        // TODO Auto-generated method stub
        return null;
    }

}
