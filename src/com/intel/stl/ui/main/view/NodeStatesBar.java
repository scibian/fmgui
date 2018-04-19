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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.JHorizontalBar;
import com.intel.stl.ui.model.StateLongTypeViz;

public class NodeStatesBar extends AbstractNodeStatesView {
    private static final long serialVersionUID = 1036776904191810974L;

    private final boolean concise;

    private JHorizontalBar[] stateBars;

    private JLabel[] stateLabels;

    public NodeStatesBar(boolean concise) {
        super();
        this.concise = concise;
        initComponent();
    }

    /**
     * Description:
     *
     */
    protected void initComponent() {
        setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.BOTH;
        StateLongTypeViz[] states = StateLongTypeViz.values();
        stateBars = new JHorizontalBar[states.length];
        stateLabels = new JLabel[states.length];
        for (int i = 0; i < states.length; i++) {
            StateLongTypeViz state = states[i];
            gc.insets = new Insets(2, 5, 2, 2);
            gc.weightx = 0;
            gc.gridwidth = 1;
            // ImageIcon icon = Util.getImageIcon(state.getIcon());
            // JLabel iconLabel = new JLabel(icon);
            // mainPanel.add(iconLabel, gc);

            JLabel label = new JLabel(state.getName(), JLabel.RIGHT);
            label.setFont(UIConstants.H5_FONT);
            label.setForeground(UIConstants.INTEL_DARK_GRAY);
            add(label, gc);

            gc.insets = new Insets(2, 2, 2, 5);
            gc.weightx = 1;
            stateBars[i] = new JHorizontalBar();
            stateBars[i].setName(WidgetName.HP_STU_DIST_BAR_.name() + i);
            stateBars[i].setOpaque(false);
            stateBars[i].setBorder(BorderFactory
                    .createLineBorder(UIConstants.INTEL_BORDER_GRAY));
            stateBars[i].setForeground(state.getColor());
            stateBars[i].setPreferredSize(new Dimension(60, 20));
            add(stateBars[i], gc);

            if (concise) {
                gc.gridwidth = GridBagConstraints.REMAINDER;
            }
            stateLabels[i] = new JLabel();
            stateLabels[i].setName(WidgetName.HP_STU_DIST_LABEL_.name() + i);
            stateLabels[i].setForeground(UIConstants.INTEL_DARK_GRAY);
            stateLabels[i].setFont(UIConstants.H3_FONT);
            add(stateLabels[i], gc);

            if (!concise) {
                gc.weightx = 0;
                gc.gridwidth = GridBagConstraints.REMAINDER;
                label = new JLabel(STLConstants.K0014_ACTIVE_NODES.getValue(),
                        JLabel.RIGHT);
                label.setFont(UIConstants.H5_FONT);
                label.setForeground(UIConstants.INTEL_DARK_GRAY);
                add(label, gc);
            }
        }
    }

    @Override
    public void setStates(double[] values, String[] labels, String[] tooltips) {
        if (values.length != stateBars.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + stateBars.length
                            + " values, got " + values.length + " values.");
        }
        if (labels.length != stateLabels.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + stateLabels.length
                            + " labels, got " + labels.length + " labels.");
        }
        if (tooltips.length != stateLabels.length) {
            throw new IllegalArgumentException("Incorrect array size. Expected "
                    + stateLabels.length + " tooltips, got " + tooltips.length
                    + " tooltips.");
        }

        for (int i = 0; i < stateBars.length; i++) {
            stateBars[i].setNormalizedValue(values[i]);
            stateBars[i].setToolTipText(tooltips[i]);
            stateLabels[i].setText(labels[i]);
            stateLabels[i].setToolTipText(tooltips[i]);
        }
    }

    public void clear() {
        for (int i = 0; i < stateLabels.length; i++) {
            stateLabels[i].setText(STLConstants.K0039_NOT_AVAILABLE.getValue());
            stateLabels[i].setToolTipText(null);
        }
    }
}
