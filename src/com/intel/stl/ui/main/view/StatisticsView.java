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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.concurrent.TimeUnit;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.common.view.JDuration;
import com.intel.stl.ui.model.NodeTypeViz;

/**
 */
public class StatisticsView extends JCardView<ICardListener> {
    private static final long serialVersionUID = -5447526254155197323L;

    private static Font NUM_FONT = UIConstants.H3_FONT.deriveFont(Font.BOLD);

    private static Color NUM_COLOR = UIConstants.INTEL_DARK_GRAY;

    private static Font LABEL_FONT = UIConstants.H4_FONT;

    private static Font SMALL_LABEL_FONT = UIConstants.H5_FONT;

    private static Color LABEL_COLOR = UIConstants.INTEL_DARK_GRAY;

    private JPanel mainPanel;

    private BaseInfoPanel baseInfoPanel;

    private StaDetailsPanel nodesPanel;

    private StaDetailsPanel portsPanel;

    /**
     * @param title
     * @param controller
     */
    public StatisticsView() {
        super(STLConstants.K0007_SUBNET.getValue());
        setName(WidgetName.HP_STA_TITLE.name());
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

        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 5));

        baseInfoPanel = new BaseInfoPanel();
        mainPanel.add(baseInfoPanel, BorderLayout.WEST);

        JPanel body = new JPanel(new GridLayout(1, 2, 15, 1));
        body.setOpaque(false);

        NodeTypeViz[] nodeTypes =
                new NodeTypeViz[] { NodeTypeViz.SWITCH, NodeTypeViz.HFI };
        nodesPanel = new StaDetailsPanel(nodeTypes);
        nodesPanel.setName(WidgetName.HP_STA_NODES_SUM.name());
        body.add(nodesPanel);

        nodeTypes = new NodeTypeViz[] { NodeTypeViz.SWITCH, NodeTypeViz.HFI };
        portsPanel = new StaDetailsPanel(nodeTypes);
        portsPanel.setName(WidgetName.HP_STA_PORTS_SUM.name());
        body.add(portsPanel);

        mainPanel.add(body, BorderLayout.CENTER);

        setHelpButtonName(WidgetName.HP_STA_HELP.name());
        setPinButtonName(WidgetName.HP_STA_PIN.name());

        return mainPanel;
    }

    /**
     * @return the nodesPanel
     */
    public StaDetailsPanel getNodesPanel() {
        return nodesPanel;
    }

    /**
     * @return the portsPanel
     */
    public StaDetailsPanel getPortsPanel() {
        return portsPanel;
    }

    /**
     *
     * Description:
     *
     * @param duration
     * @param unit
     */
    public void setDuration(long duration, TimeUnit unit) {
        baseInfoPanel.setDuration(duration, unit);
    }

    public void setMsmName(String name, String description) {
        baseInfoPanel.setMsmName(name, description);
    }

    public void setStandbySMNames(String[] names, String[] descriptions) {
        baseInfoPanel.setStandbySMNames(names, descriptions);
    }

    public void setNumSwitchLinks(String number) {
        baseInfoPanel.setNumSwitchLinks(number);
    }

    public void setNumHostLinks(String number) {
        baseInfoPanel.setNumHostLinks(number);
    }

    public void setOtherPorts(String number) {
        baseInfoPanel.setOtherPorts(number);
    }

    public void clear() {
        setTitle(STLConstants.K0007_SUBNET.getValue());
        baseInfoPanel.clear();
        repaint();
    }

    class BaseInfoPanel extends JPanel {
        private static final long serialVersionUID = 4249046807614016076L;

        private JLabel msmNameLabel;

        private JDuration durationLabel;

        private JPanel standbySMsPanel;

        private JLabel numberSwitchLinksLabel;

        private JLabel numberHostLinksLabel;

        private JLabel otherPortsLabel;

        public BaseInfoPanel() {
            super();
            initComponents();
        }

        protected void initComponents() {
            setLayout(new GridBagLayout());
            setOpaque(false);

            GridBagConstraints gc = new GridBagConstraints();
            Insets labelInset = new Insets(2, 5, 1, 15);
            Insets contentInset = new Insets(0, 20, 3, 1);

            gc.insets = new Insets(0, 5, 1, 15);
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.weightx = 1;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.weighty = 0;
            JLabel label =
                    createNameLabel(STLConstants.K0025_MASTER_SM.getValue());
            add(label, gc);

            gc.insets = contentInset;
            msmNameLabel = createNumberLabel(
                    STLConstants.K0039_NOT_AVAILABLE.getValue());
            msmNameLabel.setName(WidgetName.HP_STA_MASTER_SM.name());
            add(msmNameLabel, gc);

            gc.insets = labelInset;
            label = createNameLabel(STLConstants.K0008_UPTIME.getValue());
            add(label, gc);

            gc.insets = contentInset;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.WEST;
            durationLabel = new JDuration();
            durationLabel.setName(WidgetName.HP_STA_MSM_UPTIME.name());
            durationLabel.setOpaque(false);
            durationLabel.setLAF(NUM_FONT, NUM_COLOR, SMALL_LABEL_FONT,
                    LABEL_COLOR);
            add(durationLabel, gc);

            gc.insets = labelInset;
            gc.fill = GridBagConstraints.HORIZONTAL;
            label = createNameLabel(STLConstants.K0059_STANDBY_SMS.getValue());
            add(label, gc);

            gc.insets = contentInset;
            standbySMsPanel = new JPanel();
            standbySMsPanel.setName(WidgetName.HP_STA_STANDBY_SMS.name());
            standbySMsPanel.setOpaque(false);
            standbySMsPanel.setLayout(
                    new BoxLayout(standbySMsPanel, BoxLayout.Y_AXIS));
            add(standbySMsPanel, gc);

            gc.insets = labelInset;
            label = createNameLabel(STLConstants.K0013_LINKS.getValue());
            add(label, gc);

            gc.insets = contentInset;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.SOUTHEAST;
            gc.gridwidth = 1;
            numberSwitchLinksLabel = createNumberLabel(
                    STLConstants.K0039_NOT_AVAILABLE.getValue());
            numberSwitchLinksLabel.setName(WidgetName.HP_STA_ISL.name());
            add(numberSwitchLinksLabel, gc);

            gc.insets = labelInset;
            gc.anchor = GridBagConstraints.SOUTHWEST;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            label = createNameLabel(STLConstants.K0060_ISL.getValue(),
                    SMALL_LABEL_FONT);
            add(label, gc);

            gc.insets = contentInset;
            gc.anchor = GridBagConstraints.SOUTHEAST;
            gc.gridwidth = 1;
            numberHostLinksLabel = createNumberLabel(
                    STLConstants.K0039_NOT_AVAILABLE.getValue());
            numberHostLinksLabel.setName(WidgetName.HP_STA_HOST_LINKS.name());
            add(numberHostLinksLabel, gc);

            gc.insets = labelInset;
            gc.anchor = GridBagConstraints.SOUTHWEST;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            label = createNameLabel(STLConstants.K0061_HOST_LINKS.getValue(),
                    SMALL_LABEL_FONT);
            add(label, gc);

            gc.insets = labelInset;
            label = createNameLabel(STLConstants.K2071_OTHER_PORTS.getValue());
            add(label, gc);

            gc.insets = contentInset;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.SOUTHEAST;
            gc.gridwidth = 1;
            otherPortsLabel = createNumberLabel(
                    STLConstants.K0039_NOT_AVAILABLE.getValue());
            otherPortsLabel.setName(WidgetName.HP_STA_OTHER_PORTS.name());
            otherPortsLabel.setForeground(UIConstants.INTEL_GRAY);
            add(otherPortsLabel, gc);

            gc.insets = labelInset;
            gc.anchor = GridBagConstraints.SOUTHWEST;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            label = createNameLabel(STLConstants.K2077_NOT_IN_FABRIC.getValue(),
                    SMALL_LABEL_FONT);
            add(label, gc);

            gc.weighty = 1;
            add(Box.createGlue(), gc);
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

        public void setMsmName(String name, String description) {
            if (name == null) {
                msmNameLabel
                        .setText(STLConstants.K0039_NOT_AVAILABLE.getValue());
            } else {
                msmNameLabel.setText(name);
                msmNameLabel.setToolTipText(description);
            }
        }

        public void setStandbySMNames(String[] names, String[] descriptions) {
            standbySMsPanel.removeAll();
            if (names == null || names.length == 0) {
                standbySMsPanel.add(createNumberLabel(
                        STLConstants.K0039_NOT_AVAILABLE.getValue()));
            } else {
                for (int i = 0; i < names.length; i++) {
                    JLabel label = createNumberLabel(names[i]);
                    label.setToolTipText(descriptions[i]);
                    standbySMsPanel.add(label);
                }
            }
            revalidate();
        }

        public void setNumSwitchLinks(String number) {
            if (number == null) {
                numberSwitchLinksLabel
                        .setText(STLConstants.K0039_NOT_AVAILABLE.getValue());
            } else {
                numberSwitchLinksLabel.setText(number);
            }
        }

        public void setNumHostLinks(String number) {
            if (number == null) {
                numberHostLinksLabel
                        .setText(STLConstants.K0039_NOT_AVAILABLE.getValue());
            } else {
                numberHostLinksLabel.setText(number);
            }
        }

        public void setOtherPorts(String number) {
            if (number == null) {
                otherPortsLabel
                        .setText(STLConstants.K0039_NOT_AVAILABLE.getValue());
            } else {
                otherPortsLabel.setText(number);
            }
        }

        protected JLabel createNumberLabel(String text) {
            JLabel label = new JLabel(text);
            label.setFont(NUM_FONT);
            label.setForeground(NUM_COLOR);
            label.setHorizontalAlignment(JLabel.LEFT);
            label = ComponentFactory.deriveLabel(label, false,
                    UIConstants.MAX_LABEL_Width);
            return label;
        }

        protected JLabel createNameLabel(String text) {
            return createNameLabel(text, LABEL_FONT);
        }

        protected JLabel createNameLabel(String text, Font font) {
            JLabel label = new JLabel(text);
            label.setFont(font);
            label.setForeground(LABEL_COLOR);
            label.setHorizontalAlignment(JLabel.LEFT);
            return label;
        }

        protected void clear() {
            setMsmName(null, null);
            setStandbySMNames(null, null);
            setNumSwitchLinks(null);
            setNumHostLinks(null);
            setOtherPorts(null);
            durationLabel.clear();
        }
    }
}
