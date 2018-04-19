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

package com.intel.stl.ui.wizards.view.subnet;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.wizards.view.MultinetWizardView;

public class HostAdderPanel extends JPanel {

    private static final long serialVersionUID = 2622454818918629829L;

    private static HostAdderPanel instance;

    private final IHostInfoListener hostInfoListener;

    private HostAdderPanel(IHostInfoListener hostInfoListener) {
        this.hostInfoListener = hostInfoListener;
        initComponents();
    }

    public static HostAdderPanel getInstance(
            IHostInfoListener hostInfoListener) {

        if (instance == null) {
            instance = new HostAdderPanel(hostInfoListener);
        }

        return instance;
    }

    protected void initComponents() {

        setLayout(new BorderLayout());
        setBackground(MultinetWizardView.WIZARD_COLOR);

        JLabel lblAddHost = ComponentFactory.getH4Label(
                STLConstants.K3036_ADD_NEW_HOST.getValue(), Font.BOLD);
        lblAddHost.setName(WidgetName.SW_H_ADD_HOST.name());
        lblAddHost.setForeground(UIConstants.INTEL_BLUE);
        lblAddHost.setBorder(BorderFactory.createEmptyBorder(2, 5, 2, 5));

        lblAddHost.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                hostInfoListener.addHost();
            }
        });

        lblAddHost.setOpaque(true);
        lblAddHost.setHorizontalAlignment(JLabel.LEFT);
        lblAddHost.setBackground(UIConstants.INTEL_BORDER_GRAY);
        add(lblAddHost, BorderLayout.WEST);
    }
}
