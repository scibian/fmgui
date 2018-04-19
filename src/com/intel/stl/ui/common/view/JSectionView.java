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
package com.intel.stl.ui.common.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

/**
 */
public abstract class JSectionView<E extends ISectionListener> extends JPanel {
    private static final long serialVersionUID = 2453671952562188530L;

    private JPanel titlePanel;

    private JLabel titleLabel;

    private JPanel buttonPanel;

    private JButton helpBtn;

    private E listener;

    protected final JComponent mainComponent;

    public JSectionView(String title) {
        this(title, null);
    }

    public JSectionView(String title, Icon icon) {
        setLayout(new BorderLayout());
        add(getTitlePanel(title, icon), BorderLayout.NORTH);
        mainComponent = getMainComponent();
        if (mainComponent != null) {
            add(mainComponent, BorderLayout.CENTER);
        }
    }

    protected void setHelpButtonName(String name){
        if(null != helpBtn){
            helpBtn.setName(name);
        }
    }


    public void enableHelp(boolean b) {
        if (helpBtn != null) {
            helpBtn.setEnabled(b);
        }
    }

    public void setSectionListener(E listener) {
        this.listener = listener;
    }

    public void setTitle(String title) {
        titleLabel.setText(title);
    }

    public void setTitle(String title, Icon icon) {
        titleLabel.setText(title);
        titleLabel.setIcon(icon);
    }

    public void setIcon(Icon icon) {
        titleLabel.setIcon(icon);
    }

    public JButton getHelpButton() {
        return helpBtn;
    }

    protected JPanel getTitlePanel(String title, Icon icon) {
        if (titlePanel != null) {
            return titlePanel;
        }

        titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(0, 0, 2, 0, UIConstants.INTEL_ORANGE),
                BorderFactory.createEmptyBorder(0, 2, 0, 2)));
        titleLabel = createTitleLabel(title);
        titleLabel.setIcon(icon);
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(getButtonPanel(), BorderLayout.EAST);
        return titlePanel;
    }

    protected JLabel createTitleLabel(String title) {
        return ComponentFactory.getH3Label(title, Font.PLAIN);
    }

    protected JPanel getButtonPanel() {
        if (buttonPanel != null) {
            return buttonPanel;
        }

        buttonPanel = new JPanel();
        addControlButtons(buttonPanel);
        return buttonPanel;
    }

    protected void addControlButtons(JPanel panel) {
        helpBtn =
                ComponentFactory.getImageButton(UIImages.HELP_ICON
                        .getImageIcon());
        helpBtn.setToolTipText(STLConstants.K0037_HELP.getValue());
        helpBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onHelp();
            }
        });
        helpBtn.setEnabled(false);
        panel.add(helpBtn);
    }

    protected abstract JComponent getMainComponent();
}
