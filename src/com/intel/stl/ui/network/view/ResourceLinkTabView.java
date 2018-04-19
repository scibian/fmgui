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

package com.intel.stl.ui.network.view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;

/**
 * View for the Link/Path tab on a Link or Path tabbed pane to house 2 stacked
 * labels for the to/from nodes on a link or path
 */
public class ResourceLinkTabView extends JPanel {

    private static final long serialVersionUID = -7811464011121142244L;

    private static final byte FROM_NODE_NAME_IDX = 0;

    private static final byte TO_NODE_NAME_IDX = 1;

    private JLabel lblFromNodeName;

    private JLabel lblToNodeName;

    private final String[] nodeNames;

    public ResourceLinkTabView(String[] nodeNames) {
        super();
        this.nodeNames = nodeNames;
        initComponent();
    }

    protected void initComponent() {

        setLayout(new GridBagLayout());
        setOpaque(false);
        // lblFromNodeName = new JLabel(nodeNames[FROM_NODE_NAME_IDX]);
        // lblToNodeName = new JLabel(nodeNames[TO_NODE_NAME_IDX]);
        lblFromNodeName =
                ComponentFactory.getH5Label(nodeNames[FROM_NODE_NAME_IDX],
                        Font.PLAIN);
        lblToNodeName =
                ComponentFactory.getH5Label(nodeNames[TO_NODE_NAME_IDX],
                        Font.PLAIN);
        lblToNodeName.setIcon(UIImages.CONNECT_GRAY.getImageIcon());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.weightx = 1;
        gbc.ipadx = 10;
        add(lblFromNodeName, gbc);

        gbc.gridy++;
        add(lblToNodeName, gbc);
    }

    public JPanel getMainComponent() {
        return this;
    }

    /**
     * @return the lblFromNodeName
     */
    public JLabel getLblFromNodeName() {
        return lblFromNodeName;
    }

    /**
     * @param lblFromNodeName
     *            the lblFromNodeName to set
     */
    public void setLblFromNodeName(JLabel lblFromNodeName) {
        this.lblFromNodeName = lblFromNodeName;
    }

    /**
     * @return the lblToNodeName
     */
    public JLabel getLblToNodeName() {
        return lblToNodeName;
    }

    /**
     * @param lblToNodeName
     *            the lblToNodeName to set
     */
    public void setLblToNodeName(JLabel lblToNodeName) {
        this.lblToNodeName = lblToNodeName;
    }

    public void setLabelProperties(boolean highlight) {

        if (highlight) {
            lblFromNodeName.setBackground(UIConstants.INTEL_WHITE);
            lblFromNodeName.setForeground(UIConstants.INTEL_DARK_GRAY);

            lblToNodeName.setBackground(UIConstants.INTEL_WHITE);
            lblToNodeName.setForeground(UIConstants.INTEL_DARK_GRAY);

            lblToNodeName.setIcon(UIImages.CONNECT_GRAY.getImageIcon());
        } else {
            lblFromNodeName.setBackground(UIConstants.INTEL_BLUE);
            lblFromNodeName.setForeground(UIConstants.INTEL_WHITE);

            lblToNodeName.setBackground(UIConstants.INTEL_BLUE);
            lblToNodeName.setForeground(UIConstants.INTEL_WHITE);

            lblToNodeName.setIcon(UIImages.CONNECT_WHITE.getImageIcon());
        }
    }

}
