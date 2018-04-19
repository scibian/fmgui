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

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.common.view.JHorizontalBar;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.model.NodeTypeViz;

public class TopSummaryGroupPanel extends JCardView<ICardListener> {
    private static final long serialVersionUID = -3101042683294419137L;

    private final PropertyVizStyle style;

    private JPanel mainPanel;

    private int tiers;

    private TierPanelWrapper[] tierPanels;

    /**
     * Description:
     * 
     * @param style
     * @param tiers
     */
    public TopSummaryGroupPanel(PropertyVizStyle style) {
        super(STLConstants.K2064_TOP_SUMMARY.getValue());
        this.style = style;
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.view.JCardView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setOpaque(false);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        }
        return mainPanel;
    }

    public void init(int numTiers) {
        this.tiers = numTiers;
        mainPanel.removeAll();

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;

        tierPanels = new TierPanelWrapper[tiers];
        for (int i = 0; i < tiers; i++) {
            TierPanelWrapper tierPanel = new TierPanelWrapper();
            tierPanels[i] = tierPanel;
            gc.weightx = 0;
            gc.gridwidth = 1;

            gc.insets = new Insets(1, 1, 1, 0);
            mainPanel.add(tierPanel.nameLabel, gc);

            gc.insets = new Insets(1, 0, 1, 0);
            mainPanel.add(tierPanel.totalPanel, gc);

            gc.weightx = 1;
            mainPanel.add(tierPanel.portsPanel, gc);
            mainPanel.add(tierPanel.slowPortsPanel, gc);

            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.insets = new Insets(1, 0, 1, 5);
            mainPanel.add(tierPanel.degPortsPanel, gc);

            // apply style
            if (style != null) {
                style.decorateKey(tierPanel.nameLabel, i);
                style.decorateValue(tierPanel.totalPanel, i);
                style.decorateValue(tierPanel.portsPanel, i);
                style.decorateValue(tierPanel.slowPortsPanel, i);
                style.decorateValue(tierPanel.degPortsPanel, i);
            }
        }
    }

    public void setTierName(int index, String name) {
        tierPanels[index].nameLabel.setText(name);
    }

    public void setSummary(int index, String numSwitches, String numHFIs,
            String numPorts) {
        tierPanels[index].totalPanel.setContent(numSwitches, numHFIs, numPorts);
    }

    public void setPortsDist(int index, double[] normalizedVals,
            String[] values, Color[] colors, String[] labels, String[] tooltips) {
        tierPanels[index].portsPanel.setContent(normalizedVals, values, colors,
                labels, tooltips, null);
    }

    public void setSlowPortsDist(int index, double[] normalizedVals,
            String[] values, Color[] colors, String[] labels,
            String[] tooltips, ActionListener[] actions) {
        tierPanels[index].slowPortsPanel.setContent(normalizedVals, values,
                colors, labels, tooltips, actions);
    }

    public void setDegPortsDist(int index, double[] normalizedVals,
            String[] values, Color[] colors, String[] labels,
            String[] tooltips, ActionListener[] actions) {
        tierPanels[index].degPortsPanel.setContent(normalizedVals, values,
                colors, labels, tooltips, actions);
    }

    class TierPanelWrapper {
        private final JLabel nameLabel;

        private final TotalPanel totalPanel;

        private final PortsPanel portsPanel;

        private final PortsPanel slowPortsPanel;

        private final PortsPanel degPortsPanel;

        public TierPanelWrapper() {
            nameLabel = ComponentFactory.getH4Label("", Font.BOLD);
            nameLabel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 5));
            totalPanel = new TotalPanel();
            portsPanel = new PortsPanel(3);
            slowPortsPanel = new PortsPanel(2);
            degPortsPanel = new PortsPanel(2);
        }
    }

    class TotalPanel extends JPanel {
        private static final long serialVersionUID = -499235218493665238L;

        private JLabel switchesLabel;

        private JLabel hfisLabel;

        private JLabel portsLabel;

        public TotalPanel() {
            super();
            initComponents();
        }

        protected void initComponents() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(2, 2, 2, 5);

            gc.weightx = 1;
            switchesLabel = ComponentFactory.getH4Label("", Font.PLAIN);
            switchesLabel.setHorizontalAlignment(JLabel.RIGHT);
            add(switchesLabel, gc);

            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            JLabel label =
                    ComponentFactory.getH4Label(
                            NodeTypeViz.SWITCH.getPluralName(), Font.PLAIN);
            add(label, gc);

            gc.weightx = 1;
            gc.gridwidth = 1;
            hfisLabel = ComponentFactory.getH4Label("", Font.PLAIN);
            hfisLabel.setHorizontalAlignment(JLabel.RIGHT);
            add(hfisLabel, gc);

            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            label =
                    ComponentFactory.getH4Label(
                            STLConstants.K2076_CONNECTED_HFIS.getValue(),
                            Font.PLAIN);
            add(label, gc);

            gc.weightx = 1;
            gc.gridwidth = 1;
            portsLabel = ComponentFactory.getH4Label("", Font.PLAIN);
            portsLabel.setHorizontalAlignment(JLabel.RIGHT);
            add(portsLabel, gc);

            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            label =
                    ComponentFactory.getH4Label(
                            STLConstants.K1026_PORT_RESOURCE.getValue(),
                            Font.PLAIN);
            add(label, gc);
        }

        public void setContent(String numSwitches, String numHFIs,
                String numPorts) {
            switchesLabel.setText(numSwitches);
            hfisLabel.setText(numHFIs);
            portsLabel.setText(numPorts);
            repaint();
        }
    }

    class PortsPanel extends JPanel {
        private static final long serialVersionUID = -4597232952448379838L;

        private final int size;

        private JHorizontalBar[] bars;

        private ActionListener[] actions;

        private JLabel[] nameLabels;

        public PortsPanel(int size) {
            super();
            this.size = size;
            initComponents();
        }

        protected void initComponents() {
            setOpaque(false);
            setLayout(new GridBagLayout());

            bars = new JHorizontalBar[size];
            nameLabels = new JLabel[size];
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            for (int i = 0; i < size; i++) {
                gc.gridwidth = 1;
                gc.weightx = 1;
                gc.insets = new Insets(2, 5, 2, 2);
                bars[i] = new JHorizontalBar();
                bars[i].setUpperMargin(0.8);
                bars[i].setLowerMargin(0);
                bars[i].setPreferredSize(new Dimension(60, 20));
                bars[i].setVerticalAlignment(JLabel.TOP);
                bars[i].setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
                final int index = i;
                bars[i].addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (actions != null && actions[index] != null) {
                            actions[index].actionPerformed(new ActionEvent(
                                    bars[index], e.getID(), e.paramString()));
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        if (actions != null && actions[index] != null) {
                            bars[index].setCursor(Cursor
                                    .getPredefinedCursor(Cursor.HAND_CURSOR));
                        }
                    }

                    @Override
                    public void mouseExited(MouseEvent e) {
                        bars[index].setCursor(Cursor.getDefaultCursor());
                    }
                });
                add(bars[i], gc);

                gc.gridwidth = GridBagConstraints.REMAINDER;
                gc.weightx = 0;
                gc.insets = new Insets(2, 2, 2, 5);
                nameLabels[i] = ComponentFactory.getH4Label("", Font.PLAIN);
                add(nameLabels[i], gc);
            }
        }

        public void setContent(double[] normalizedVals, String[] values,
                Color[] colors, String[] labels, String[] tooltips,
                ActionListener[] actions) {
            if (normalizedVals.length != size) {
                throw new IllegalArgumentException(
                        "Incorrect normalized values size. Expecte " + size
                                + ", got " + normalizedVals.length);
            }
            checkSize("values", values, size);
            checkSize("colors", colors, size);
            checkSize("labels", labels, size);
            checkSize("tooltips", tooltips, size);
            checkSize("actions", actions, size);

            for (int i = 0; i < size; i++) {
                bars[i].setText(values[i]);
                bars[i].setForeground(colors[i]);
                bars[i].setNormalizedValue(normalizedVals[i]);

                nameLabels[i].setText(labels[i]);

                if (tooltips != null) {
                    nameLabels[i].setToolTipText(tooltips[i]);
                } else {
                    nameLabels[i].setToolTipText(null);
                }
            }
            this.actions = actions;
            repaint();
        }

        protected void checkSize(String name, Object[] values, int size) {
            if (values != null && values.length != size) {
                throw new IllegalArgumentException("Incorrect " + name
                        + " size. Expecte " + size + ", got " + values.length);
            }
        }
    }

}
