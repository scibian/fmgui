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
import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.FlowTypeViz;
import com.intel.stl.ui.model.NodeTypeViz;

/**
 * per feedback we got, we do not show Router info here. we intentionally
 * change it on UI side rather than backend because we need to support it
 * again in the future
 */
public class PSPortsDetailsPanel extends JPanel {
    private static final long serialVersionUID = -8248761594760146918L;

    private final NodeTypeViz[] nodeTypes;

    private final FlowTypeViz[] flowTypes;

    private JLabel numberLabel;

    private JLabel nameLabel;

    private ChartPanel deviceTypeChartPanel;

    private JLabel[] typeNumberLabels;

    private JLabel[] typeNameLabels;

    private ChartPanel flowTypeChartPanel;

    private JLabel[] flowNumberLabels;

    private JLabel[] flowNameLabels;

    public PSPortsDetailsPanel(NodeTypeViz[] nodeTypes, FlowTypeViz[] flowTypes) {
        super();
        this.nodeTypes = nodeTypes;
        this.flowTypes = flowTypes;
        initComponent();
    }

    /**
     * Description:
     * 
     * @param name
     */
    protected void initComponent() {
        setLayout(new GridBagLayout());
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder((Border) null));

        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel titlePanel = createTitlePanel();
        add(titlePanel, gc);

        gc.gridwidth = 1;
        JPanel deviceTypePanel = createDeviceTypePanel();
        add(deviceTypePanel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel flowTypePanel = createFlowTypePanel();
        add(flowTypePanel, gc);

        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(Box.createGlue(), gc);
    }

    protected JPanel createTitlePanel() {
        JPanel panel = new JPanel(new BorderLayout(5, 1));
        panel.setOpaque(false);
        numberLabel =
                ComponentFactory
                        .getH1Label(
                                STLConstants.K0039_NOT_AVAILABLE.getValue(),
                                Font.PLAIN);
        numberLabel.setHorizontalAlignment(JLabel.RIGHT);
        panel.add(numberLabel, BorderLayout.CENTER);
        nameLabel = ComponentFactory.getH3Label("", Font.PLAIN);
        nameLabel.setHorizontalAlignment(JLabel.LEFT);
        nameLabel.setVerticalAlignment(JLabel.BOTTOM);
        panel.add(nameLabel, BorderLayout.EAST);

        return panel;
    }

    protected JPanel createDeviceTypePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(8, 2, 2, 2);
        gc.weighty = 0;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.gridheight = 1;
        deviceTypeChartPanel = new ChartPanel(null);
        deviceTypeChartPanel.setPreferredSize(new Dimension(80, 60));
        panel.add(deviceTypeChartPanel, gc);

        typeNumberLabels = new JLabel[nodeTypes.length];
        typeNameLabels = new JLabel[nodeTypes.length];
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(2, 2, 2, 2);
        for (int i = 0; i < nodeTypes.length; i++) {
            gc.weightx = 1;
            gc.gridwidth = 1;
            typeNumberLabels[i] = createNumberLabel();
            panel.add(typeNumberLabels[i], gc);

            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            typeNameLabels[i] = createNameLabel(nodeTypes[i].getName());
            panel.add(typeNameLabels[i], gc);
        }

        return panel;
    }

    protected JPanel createFlowTypePanel() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        GridBagLayout gridBag = new GridBagLayout();
        panel.setLayout(gridBag);
        GridBagConstraints gc = new GridBagConstraints();

        gc.insets = new Insets(8, 2, 2, 2);
        gc.weighty = 0;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.gridheight = 1;
        flowTypeChartPanel = new ChartPanel(null);
        flowTypeChartPanel.setPreferredSize(new Dimension(80, 60));
        panel.add(flowTypeChartPanel, gc);

        flowNumberLabels = new JLabel[flowTypes.length];
        flowNameLabels = new JLabel[flowTypes.length];
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(2, 2, 2, 2);
        for (int i = 0; i < flowTypes.length; i++) {
            gc.weightx = 1;
            gc.gridwidth = 1;
            flowNumberLabels[i] = createNumberLabel();
            panel.add(flowNumberLabels[i], gc);

            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            flowNameLabels[i] = createNameLabel(flowTypes[i].getName());
            panel.add(flowNameLabels[i], gc);
        }

        return panel;
    }

    private JLabel createNumberLabel() {
        JLabel label =
                ComponentFactory
                        .getH4Label(
                                STLConstants.K0039_NOT_AVAILABLE.getValue(),
                                Font.PLAIN);
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
     * @return the nodeTypes
     */
    public NodeTypeViz[] getNodeTypes() {
        return nodeTypes;
    }

    /**
     * @return the flowTypes
     */
    public FlowTypeViz[] getFlowTypes() {
        return flowTypes;
    }

    @Override
    public void setName(String name) {
        nameLabel.setText(name);
    }

    public void setTotalNumber(String value) {
        numberLabel.setText(value);
    }

    public void setTypeInfo(NodeTypeViz type, String number, String label) {
        for (int i = 0; i < nodeTypes.length; i++) {
            if (nodeTypes[i] == type) {
                typeNumberLabels[i].setText(number);
                typeNameLabels[i].setText(label);
                return;
            }
        }
        throw new IllegalArgumentException("Unsupported Node Type " + type);
    }

    public void setFlowInfo(FlowTypeViz type, String number) {
        for (int i = 0; i < flowTypes.length; i++) {
            if (flowTypes[i] == type) {
                flowNumberLabels[i].setText(number);
                return;
            }
        }
        throw new IllegalArgumentException("Unsupported Flow Type " + type);
    }

    public void setDeviceTypeDataset(DefaultPieDataset dataset, Color[] colors) {
        JFreeChart chart =
                ComponentFactory.createPlainPieChart(dataset, colors);
        deviceTypeChartPanel.setChart(chart);
    }

    public void setFlowTypeDataset(DefaultPieDataset dataset, Color[] colors) {
        JFreeChart chart =
                ComponentFactory.createPlainPieChart(dataset, colors);
        flowTypeChartPanel.setChart(chart);
    }

    public void clear() {
        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        setTotalNumber(na);
        for (JLabel label : typeNumberLabels) {
            label.setText(na);
        }
        for (JLabel label : flowNumberLabels) {
            label.setText(na);
        }
    }
}
