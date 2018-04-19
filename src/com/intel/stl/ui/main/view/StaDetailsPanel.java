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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.NodeTypeViz;

/**
 * per feedback we got, we do not show Router info here. we intentionally change
 * it on UI side rather than backend because we need to support it again in the
 * future
 */
public class StaDetailsPanel extends JPanel {
    private static final long serialVersionUID = -8248761594760146918L;

    private final static double[] STATE_THRESHOLDS =
            new double[] { 0.2, 0.5, 1.0 };

    private final static Color[] STATE_COLORS =
            new Color[] { UIConstants.INTEL_GRAY, UIConstants.INTEL_LIGHT_GRAY,
                    UIConstants.INTEL_TABLE_BORDER_GRAY };

    private final NodeTypeViz[] types;

    private JLabel numberLabel;

    private JLabel nameLabel;

    private ChartPanel failedChartPanel;

    private JLabel failedNumberLabel;

    private JLabel failedNameLabel;

    private ChartPanel skippedChartPanel;

    private JLabel skippedNumberLabel;

    private JLabel skippedNameLabel;

    private ChartPanel typeChartPanel;

    private JLabel[] typeNumberLabels;

    private JLabel[] typeNameLabels;

    public StaDetailsPanel(NodeTypeViz[] types) {
        super();
        this.types = types;
        initComponent();
    }

    /**
     * Description:
     *
     * @param sourceName
     */
    protected void initComponent() {
        setLayout(new BorderLayout(0, 10));
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder((Border) null));

        JPanel titlePanel = new JPanel(new BorderLayout(5, 1));
        titlePanel.setOpaque(false);
        numberLabel = ComponentFactory.getH1Label(
                STLConstants.K0039_NOT_AVAILABLE.getValue(), Font.PLAIN);
        numberLabel.setName(WidgetName.HP_STA_SUM_TOTAL.name());
        numberLabel.setHorizontalAlignment(JLabel.RIGHT);
        titlePanel.add(numberLabel, BorderLayout.CENTER);
        nameLabel = ComponentFactory.getH3Label("", Font.PLAIN);
        nameLabel.setName(WidgetName.HP_STA_SUM_NAME.name());
        nameLabel.setHorizontalAlignment(JLabel.LEFT);
        nameLabel.setVerticalAlignment(JLabel.BOTTOM);
        titlePanel.add(nameLabel, BorderLayout.EAST);
        add(titlePanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        GridBagLayout gridBag = new GridBagLayout();
        mainPanel.setLayout(gridBag);
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(2, 2, 2, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = 1;
        gc.weighty = 0;
        failedChartPanel = new ChartPanel(null);
        failedChartPanel.setName(WidgetName.HP_STA_SUM_FAILED_CHART.name());
        failedChartPanel.setPreferredSize(new Dimension(60, 20));
        mainPanel.add(failedChartPanel, gc);

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 0;
        failedNumberLabel = ComponentFactory.getH2Label(
                STLConstants.K0039_NOT_AVAILABLE.getValue(), Font.BOLD);
        failedNumberLabel.setName(WidgetName.HP_STA_SUM_FAILED_NUM.name());
        failedNumberLabel.setForeground(UIConstants.INTEL_DARK_RED);
        failedNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(failedNumberLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        failedNameLabel = ComponentFactory
                .getH5Label(STLConstants.K0140_NO_RESP.getValue(), Font.PLAIN);
        failedNameLabel.setVerticalAlignment(JLabel.BOTTOM);
        mainPanel.add(failedNameLabel, gc);

        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = 1;
        skippedChartPanel = new ChartPanel(null);
        skippedChartPanel.setName(WidgetName.HP_STA_SUM_SKIPPED_CHART.name());
        skippedChartPanel.setPreferredSize(new Dimension(60, 20));
        mainPanel.add(skippedChartPanel, gc);

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 0;
        skippedNumberLabel = ComponentFactory.getH2Label(
                STLConstants.K0039_NOT_AVAILABLE.getValue(), Font.BOLD);
        skippedNumberLabel.setName(WidgetName.HP_STA_SUM_SKIPPED_NUM.name());
        skippedNumberLabel.setForeground(UIConstants.INTEL_DARK_ORANGE);
        skippedNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(skippedNumberLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        skippedNameLabel = ComponentFactory
                .getH5Label(STLConstants.K0021_SKIPPED.getValue(), Font.PLAIN);
        skippedNameLabel.setVerticalAlignment(JLabel.BOTTOM);
        mainPanel.add(skippedNameLabel, gc);

        gc.weighty = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.insets = new Insets(8, 2, 2, 2);
        gc.weightx = 1;
        gc.gridwidth = 1;
        gc.gridheight = types.length;
        typeChartPanel = new ChartPanel(null);
        typeChartPanel.setName(WidgetName.HP_STA_SUM_TYPE_CHART.name());
        typeChartPanel.setPreferredSize(new Dimension(80, 60));
        mainPanel.add(typeChartPanel, gc);

        typeNumberLabels = new JLabel[types.length];
        typeNameLabels = new JLabel[types.length];
        gc.fill = GridBagConstraints.BOTH;
        gc.gridheight = 1;
        gc.insets = new Insets(12, 2, 2, 2);
        for (int i = 0; i < types.length; i++) {
            if (i == 1) {
                gc.insets = new Insets(2, 2, 2, 2);
            }

            gc.weightx = 0;
            gc.gridwidth = 1;
            typeNumberLabels[i] = createNumberLabel();
            typeNumberLabels[i]
                    .setName(WidgetName.HP_STA_SUM_TYPE_NUM_.name() + i);
            mainPanel.add(typeNumberLabels[i], gc);

            gc.gridwidth = GridBagConstraints.REMAINDER;
            typeNameLabels[i] = createNameLabel(types[i].getName());
            typeNameLabels[i]
                    .setName(WidgetName.HP_STA_SUM_TYPE_NAME_.name() + i);
            mainPanel.add(typeNameLabels[i], gc);
        }

        gc.fill = GridBagConstraints.BOTH;
        mainPanel.add(Box.createGlue(), gc);

        add(mainPanel, BorderLayout.CENTER);
    }

    private JLabel createNumberLabel() {
        JLabel label = ComponentFactory.getH4Label(
                STLConstants.K0039_NOT_AVAILABLE.getValue(), Font.PLAIN);
        label.setHorizontalAlignment(JLabel.RIGHT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        return label;
    }

    private JLabel createNameLabel(String name) {
        JLabel label = ComponentFactory.getH5Label(name, Font.PLAIN);
        label.setVerticalAlignment(JLabel.BOTTOM);
        return label;
    }

    /**
     * @return the types
     */
    public NodeTypeViz[] getTypes() {
        return types;
    }

    public void setNameLabel(String name) {
        nameLabel.setText(name);
    }

    public void setFailedDataset(DefaultCategoryDataset dataset) {
        JFreeChart chart = ComponentFactory.createBulletChart(dataset,
                STATE_THRESHOLDS, STATE_COLORS);
        failedChartPanel.setChart(chart);
    }

    public void setSkipedDataset(DefaultCategoryDataset dataset) {
        JFreeChart chart = ComponentFactory.createBulletChart(dataset,
                STATE_THRESHOLDS, STATE_COLORS);
        skippedChartPanel.setChart(chart);
    }

    public void setTypeDataset(DefaultPieDataset dataset, Color[] colors) {
        JFreeChart chart =
                ComponentFactory.createPlainPieChart(dataset, colors);
        typeChartPanel.setChart(chart);
    }

    public void setTotalNumber(String value) {
        numberLabel.setText(value);
    }

    public void setFailed(String value) {
        failedNumberLabel.setText(value);
    }

    public void setSkipped(String value) {
        skippedNumberLabel.setText(value);
    }

    public void setTypeInfo(NodeTypeViz type, String number, String label) {
        for (int i = 0; i < types.length; i++) {
            if (types[i] == type) {
                typeNumberLabels[i].setText(number);
                typeNameLabels[i].setText(label);
                return;
            }
        }
        throw new IllegalArgumentException("Unsupported Node Type " + type);
    }

    public void clear() {
        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        setTotalNumber(na);
        setFailed(na);
        setSkipped(na);
        for (int i = 0; i < types.length; i++) {
            typeNumberLabels[i].setText(na);
        }
    }
}
