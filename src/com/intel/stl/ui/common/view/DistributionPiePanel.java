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
import java.awt.Color;
import java.awt.FlowLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

/**
 */
public class DistributionPiePanel extends JPanel {
    private static final long serialVersionUID = -8922967649586607241L;

    private ChartPanel chartPanel;

    private JPanel labelPanel;

    private JLabel[] labels;

    public DistributionPiePanel() {
        super(new BorderLayout(0, 0));
        
        chartPanel = new ChartPanel(null);
        add(chartPanel, BorderLayout.CENTER);
        
        labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        add(labelPanel, BorderLayout.SOUTH);
    }
    
    public void setDataset(DefaultPieDataset dataset, Color[] colors) {
        JFreeChart chart = ComponentFactory.createPlainPieChart(dataset, colors);
        chartPanel.setChart(chart);
    }
    
    public void setLabels(String[] itemNames, ImageIcon[] icons,
            int labelColumns) {
        if (icons.length != itemNames.length) {
            throw new IllegalArgumentException("Inconsistent number of items. "
                    + " itemNames=" + itemNames.length + " icons=" + icons.length);
        }
        
        labels = new JLabel[icons.length];
        for (int i = 0; i < icons.length; i++) {
            labels[i] = new JLabel(itemNames[i], icons[i], JLabel.LEFT);
        }

        int rows = 1;
        if (labelColumns <= 0) {
            labelPanel.setLayout(new FlowLayout());
            for (JLabel label : labels) {
                labelPanel.add(label);
            }
        } else {
            BoxLayout layout = new BoxLayout(labelPanel, BoxLayout.X_AXIS);
            labelPanel.setLayout(layout);
            JPanel[] columns = new JPanel[labelColumns];
            for (int i = 0; i < columns.length; i++) {
                labelPanel.add(Box.createHorizontalGlue());
                columns[i] = new JPanel();
                columns[i].setOpaque(false);
                columns[i].setBorder(BorderFactory
                        .createEmptyBorder(2, 3, 2, 3));
                BoxLayout cLayout = new BoxLayout(columns[i], BoxLayout.Y_AXIS);
                columns[i].setLayout(cLayout);
                labelPanel.add(columns[i]);
            }
            labelPanel.add(Box.createHorizontalGlue());
            rows = (int) Math.ceil((double) labels.length / labelColumns);
            int index = 0;
            for (int i = 0; i < rows; i++) {
                for (int j = 0; j < labelColumns; j++) {
                    columns[i].add(index < labels.length ? labels[index] : Box
                            .createGlue());
                    index += 1;
                }
            }
        }
    }

    public void update(String[] itemLabels) {
        if (itemLabels.length != labels.length) {
            throw new IllegalArgumentException(
                    "Incorrect array size. Expected " + labels.length
                            + " items, got " + itemLabels.length + " items.");
        }

        chartPanel.getChart().fireChartChanged();

        for (int i = 0; i < itemLabels.length; i++) {
            labels[i].setText(itemLabels[i]);
        }
        validate();
    }

}
