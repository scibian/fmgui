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
package com.intel.stl.ui.main.view;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.DistributionBarPanel;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.common.view.JDuration;

/**
 */
public class StatisticsViewOld extends JCardView<ICardListener> {
    private static final long serialVersionUID = -5447526254155197323L;

    private JPanel mainPanel;

    private JDuration durationLabel;

    private JLabel linksLabel;

    private JLabel nodesLabel;

    private DistributionBarPanel nodeTypesBar;

    private DistributionBarPanel nodeStatesBar;

    private JLabel portsLabel;

    private DistributionBarPanel portTypesBar;

    private DistributionBarPanel portStatesBar;

    private JLabel msmNameLabel;

    /**
     * @param title
     * @param controller
     */
    public StatisticsViewOld() {
        super(STLConstants.K0007_SUBNET.getValue());
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
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

        mainPanel = new JPanel();

        GridBagLayout gridBag = new GridBagLayout();
        mainPanel.setLayout(gridBag);
        mainPanel.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(1, 5, 2, 5);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weighty = 0;
        gc.weightx = 0;
        JLabel label =
                ComponentFactory.getFieldLabel(STLConstants.K0025_MASTER_SM
                        .getValue());
        mainPanel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        // gc.weightx = 1;
        msmNameLabel = ComponentFactory.getFieldContent("");
        mainPanel.add(msmNameLabel, gc);

        gc.anchor = GridBagConstraints.EAST;
        gc.gridx += 1;
        gc.weightx = 0;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0008_UPTIME
                        .getValue());
        mainPanel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        gc.weightx = 1;
        durationLabel = new JDuration();
        durationLabel.setOpaque(false);
        mainPanel.add(durationLabel, gc);

        gc.anchor = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0013_LINKS
                        .getValue());
        mainPanel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        // gc.weightx = 1;
        linksLabel = ComponentFactory.getFieldContent("");
        mainPanel.add(linksLabel, gc);

        gc.anchor = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0014_ACTIVE_NODES
                        .getValue());
        mainPanel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        // gc.weightx = 1;
        nodesLabel = ComponentFactory.getFieldContent("");
        mainPanel.add(nodesLabel, gc);

        gc.gridx = 1;
        gc.gridy += 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel panel = createNodesDeatilsPanel();
        mainPanel.add(panel, gc);

        gc.anchor = GridBagConstraints.EAST;
        gc.gridx = 0;
        gc.gridy += 1;
        gc.gridwidth = 1;
        gc.fill = GridBagConstraints.NONE;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0024_ACTIVE_PORTS
                        .getValue());
        mainPanel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        // gc.weightx = 1;
        portsLabel = ComponentFactory.getFieldContent("");
        mainPanel.add(portsLabel, gc);

        gc.gridx = 1;
        gc.gridy += 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        panel = createPortsDeatilsPanel();
        mainPanel.add(panel, gc);

        gc.gridx = 0;
        gc.gridy += 1;
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        mainPanel.add(Box.createGlue(), gc);
        return mainPanel;
    }

    protected JPanel createNodesDeatilsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory
                .createLineBorder(UIConstants.INTEL_BORDER_GRAY));
        // panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(1, 5, 2, 5);
        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        JLabel label =
                ComponentFactory.getFieldLabel(STLConstants.K0015_TYPES
                        .getValue());
        panel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        nodeTypesBar = new DistributionBarPanel();
        nodeTypesBar.setOpaque(false);
        panel.add(nodeTypesBar, gc);

        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0023_STATES
                        .getValue());
        panel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        nodeStatesBar = new DistributionBarPanel();
        nodeStatesBar.setOpaque(false);
        panel.add(nodeStatesBar, gc);

        return panel;
    }

    protected JPanel createPortsDeatilsPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory
                .createLineBorder(UIConstants.INTEL_BORDER_GRAY));
        // panel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 0));
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(1, 5, 2, 5);
        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        JLabel label =
                ComponentFactory.getFieldLabel(STLConstants.K0015_TYPES
                        .getValue());
        panel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        portTypesBar = new DistributionBarPanel();
        portTypesBar.setOpaque(false);
        panel.add(portTypesBar, gc);

        gc.gridx = 0;
        gc.gridy += 1;
        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        label =
                ComponentFactory.getFieldLabel(STLConstants.K0023_STATES
                        .getValue());
        panel.add(label, gc);

        gc.anchor = GridBagConstraints.WEST;
        gc.gridx += 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        portStatesBar = new DistributionBarPanel();
        portStatesBar.setOpaque(false);
        panel.add(portStatesBar, gc);

        return panel;
    }

    /**
     * @return the nodeTypesBar
     */
    public DistributionBarPanel getNodeTypesBar() {
        return nodeTypesBar;
    }

    /**
     * @return the nodeStatesBar
     */
    public DistributionBarPanel getNodeStatesBar() {
        return nodeStatesBar;
    }

    /**
     * @return the portTypesBar
     */
    public DistributionBarPanel getPortTypesBar() {
        return portTypesBar;
    }

    /**
     * @return the portStatesBar
     */
    public DistributionBarPanel getPortStatesBar() {
        return portStatesBar;
    }

    /**
     * 
     * Description:
     * 
     * @param duration
     * @param unit
     */
    public void setDuration(long duration, TimeUnit unit) {
        durationLabel.setDuration(duration, unit);
    }

    public void setLinks(String numLinks) {
        linksLabel.setText(numLinks);
    }

    public void setNodes(String numNodes) {
        nodesLabel.setText(numNodes);
    }

    public void setPorts(String numPorts) {
        portsLabel.setText(numPorts);
    }

    public void setMsmName(String name, String description) {
        msmNameLabel.setText(name);
        msmNameLabel.setToolTipText(description);
    }

}
