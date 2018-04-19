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
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.StateLongTypeViz;

public class PSEventsPieChart extends JPanel {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 7866706629944517223L;
    
    private DefaultPieDataset pieDataset;
    
    private JLabel[] stateLabels;

    private ChartPanel pieChartPanel;
    
    
    
    
    public PSEventsPieChart() {
        super();
        initComponents();
    }
    
    
    protected void initComponents() {        
        setLayout(new BorderLayout());

        pieDataset = new DefaultPieDataset();
        StateLongTypeViz[] states = StateLongTypeViz.values();
        for (int i = 0; i < states.length; i++) {
            pieDataset.setValue(states[i], 0);
        }

        //Create the pie chart panel and put it on this panel
        pieChartPanel = new ChartPanel(ComponentFactory.createPlainPieChart(
                pieDataset, StateLongTypeViz.colors));
        pieChartPanel.setPreferredSize(new Dimension(80, 8));
        add(pieChartPanel);

        //Create the legend panel and put it on this panel
        JPanel legendPanel = getLengendPanel();
        add(legendPanel, BorderLayout.EAST);
    }

    protected JPanel getLengendPanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.BOTH;
        StateLongTypeViz[] states = StateLongTypeViz.values();
        stateLabels = new JLabel[states.length];
        for (int i = 0; i < states.length; i++) {
            StateLongTypeViz state = states[i];
            gc.insets = new Insets(2, 5, 2, 2);
            gc.weightx = 0;
            gc.gridwidth = 1;

            JLabel label = new JLabel(state.getName(), Util.generateImageIcon(
                    state.getColor(), 8, new Insets(1, 1, 1, 1)), JLabel.LEFT);
            label.setFont(UIConstants.H5_FONT);
            label.setForeground(UIConstants.INTEL_DARK_GRAY);
            panel.add(label, gc);

            gc.gridwidth = GridBagConstraints.REMAINDER;
            gc.weightx = 0;
            stateLabels[i] = new JLabel();
            stateLabels[i].setForeground(UIConstants.INTEL_DARK_GRAY);
            stateLabels[i].setFont(UIConstants.H5_FONT);
            panel.add(stateLabels[i], gc);
        }
        return panel;
    }
    
    
    public void setTypeDataset(DefaultPieDataset dataset) {
        JFreeChart chart = ComponentFactory.createPlainPieChart(
                dataset, StateLongTypeViz.colors);
        pieChartPanel.setChart(chart);
    }


    public void setTypes(double[] values, String[] labels, String[] tooltips) {
        if (values.length != stateLabels.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + stateLabels.length
                            + " values, got " + values.length + " values.");
        }
        if (labels.length != stateLabels.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + stateLabels.length
                            + " labels, got " + labels.length + " labels.");
        }
        if (tooltips.length != stateLabels.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + stateLabels.length
                            + " tooltips, got " + tooltips.length
                            + " tooltips.");
        }

        StateLongTypeViz[] states = StateLongTypeViz.values();
        for (int i = 0; i < values.length; i++) {
            pieDataset.setValue(states[i], values[i]);
            stateLabels[i].setText(labels[i]);
            stateLabels[i].setToolTipText(tooltips[i]);
        }
    }
  
}
