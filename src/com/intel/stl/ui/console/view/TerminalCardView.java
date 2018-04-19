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

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.console.ConsoleDispatchManager;

/**
 * JCard view on the ConsoleTerminalView containing the SSH terminal
 */
public class TerminalCardView extends JCardView<ICardListener> {
    private static final long serialVersionUID = 4439143011519610809L;

    private JPanel mainPanel;

    private JLabel statusLabel;

    public TerminalCardView(String title) {
        super(title);
    }

    public void setTermPanel(IntelTerminalView panel) {

        panel.updateTermPanelDimensions(panel.getPreferredSize());
        mainPanel.removeAll();
        mainPanel.add(panel, BorderLayout.CENTER);
        mainPanel.revalidate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.view.JCardView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout());
            mainPanel.setOpaque(false);
        }

        return mainPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.view.JCardView#getExtraComponent()
     */
    @Override
    protected JComponent getExtraComponent() {
        if (statusLabel == null) {
            statusLabel = ComponentFactory.getH4Label(null, Font.BOLD);
            statusLabel.setHorizontalAlignment(JLabel.LEADING);
            statusLabel.setForeground(UIConstants.INTEL_DARK_RED);
        }
        return statusLabel;
    }

    /**
     * <i>Description:</i>
     * 
     * @param isSelected
     */
    public void setLocked(boolean isSelected) {
        if (isSelected) {
            statusLabel.setText("[" + STLConstants.K1054_LOCKED.getValue()
                    + "]");
        } else {
            statusLabel.setText("");
        }
    }

    public void displayMaxConsoles(boolean isMaxConsoles) {

        if (isMaxConsoles) {
            statusLabel.setText(UILabels.STL80004_MAX_CONSOLES
                    .getDescription(ConsoleDispatchManager.MAX_NUM_CONSOLES));
        } else {
            statusLabel.setText("");
        }
    }

}
