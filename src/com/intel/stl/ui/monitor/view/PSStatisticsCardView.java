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

package com.intel.stl.ui.monitor.view;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.model.FlowTypeViz;
import com.intel.stl.ui.model.NodeTypeViz;

/**
 * View for the statistics card on the Performance Summary subpage
 */
public class PSStatisticsCardView extends JCardView<ICardListener> {
    private static final long serialVersionUID = -5447526254155197323L;

    private JPanel mainPanel;

    private PSNodesDetailsPanel nodesPanel;

    private PSPortsDetailsPanel portsPanel;

    /**
     * @param title
     * @param controller
     */
    public PSStatisticsCardView(String title) {
        super(title);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.common.JCard#getMainPanel()
     */
    @Override
    protected JPanel getMainComponent() {
        if (mainPanel != null) {
            return mainPanel;
        }

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 5));

        JPanel body = new JPanel(new GridLayout(1, 2, 15, 1));
        body.setOpaque(false);

        nodesPanel = new PSNodesDetailsPanel();
        body.add(nodesPanel);

        NodeTypeViz[] nodeTypes =
                new NodeTypeViz[] { NodeTypeViz.SWITCH, NodeTypeViz.HFI };
        FlowTypeViz[] flowTypes =
                new FlowTypeViz[] { FlowTypeViz.INTERNAL, FlowTypeViz.EXTERNAL };
        portsPanel = new PSPortsDetailsPanel(nodeTypes, flowTypes);
        body.add(portsPanel);

        mainPanel.add(body, BorderLayout.CENTER);

        return mainPanel;
    }

    protected JLabel createNumberLabel(String text) {
        JLabel label = ComponentFactory.getH2Label(text, Font.BOLD);
        label.setHorizontalAlignment(JLabel.LEFT);
        return label;
    }

    protected JLabel createNameLabel(String text) {
        JLabel label = ComponentFactory.getH5Label(text, Font.PLAIN);
        label.setHorizontalAlignment(JLabel.LEFT);
        return label;
    }

    public PSNodesDetailsPanel getNodesPanel() {
        return nodesPanel;
    }

    public PSPortsDetailsPanel getPortsPanel() {
        return portsPanel;
    }

}
