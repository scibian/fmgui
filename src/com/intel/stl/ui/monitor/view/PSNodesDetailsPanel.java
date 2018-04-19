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
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;

/**
 * per feedback we got, we do not show Router info here. we intentionally
 * change it on UI side rather than backend because we need to support it
 * again in the future
 */
public class PSNodesDetailsPanel extends JPanel {
    private static final long serialVersionUID = -8248761594760146918L;

    private JLabel numberLabel;

    private JLabel nameLabel;

    private ChartPanel typeChartPanel;

    private JLabel swNumberLabel;

    private JLabel swNameLabel;

    private JLabel fiNumberLabel;

    private JLabel fiNameLabel;

    private JLabel otherPortsLabel;

    // private JLabel rtNumberLabel;
    //
    // private JLabel rtNameLabel;

    public PSNodesDetailsPanel() {
        super();
        initComponent();
    }

    /**
     * Description:
     * 
     * @param name
     */
    protected void initComponent() {
        setLayout(new BorderLayout(0, 0));
        setOpaque(false);
        setBorder(BorderFactory.createTitledBorder((Border) null));

        JPanel titlePanel = new JPanel(new BorderLayout(5, 1));
        titlePanel.setOpaque(false);
        numberLabel =
                ComponentFactory
                        .getH1Label(
                                STLConstants.K0039_NOT_AVAILABLE.getValue(),
                                Font.PLAIN);
        numberLabel.setHorizontalAlignment(JLabel.RIGHT);
        titlePanel.add(numberLabel, BorderLayout.CENTER);
        nameLabel = ComponentFactory.getH3Label("", Font.PLAIN);
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

        gc.weighty = 0;
        gc.insets = new Insets(8, 2, 2, 2);
        gc.weightx = 1;
        gc.gridwidth = 1;
        gc.gridheight = 2;
        // gc.gridheight = 3; // Should change to 3 if we add Route
        typeChartPanel = new ChartPanel(null);
        typeChartPanel.setPreferredSize(new Dimension(80, 60));
        mainPanel.add(typeChartPanel, gc);

        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(12, 2, 2, 2);
        gc.weightx = 0;
        gc.gridheight = 1;
        swNumberLabel = createNumberLabel();
        mainPanel.add(swNumberLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        swNameLabel = createNameLabel(STLConstants.K0048_SWITCHES.getValue());
        mainPanel.add(swNameLabel, gc);

        gc.insets = new Insets(2, 2, 6, 2);
        gc.gridwidth = 1;
        fiNumberLabel = createNumberLabel();
        mainPanel.add(fiNumberLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        fiNameLabel = createNameLabel(STLConstants.K0052_HOSTS.getValue());
        mainPanel.add(fiNameLabel, gc);

        // gc.gridwidth = 1;
        // rtNumberLabel = ComponentFactory.getH4Label(
        // STLConstants.K0039_NOT_AVAILABLE.getValue(), Font.PLAIN);
        // rtNumberLabel.setHorizontalAlignment(JLabel.CENTER);
        // mainPanel.add(rtNumberLabel, gc);
        //
        // gc.gridwidth = GridBagConstraints.REMAINDER;
        // rtNameLabel = ComponentFactory.getH5Label(
        // STLConstants.K0050_ROUTERS.getValue(), Font.PLAIN);
        // rtNameLabel.setVerticalAlignment(JLabel.BOTTOM);
        // mainPanel.add(rtNameLabel, gc);

        add(mainPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new GridBagLayout());
        bottomPanel.setOpaque(false);
        bottomPanel.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0,
                UIConstants.INTEL_BORDER_GRAY));
        gc = new GridBagConstraints();
        gc.insets = new Insets(0, 5, 1, 5);
        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.gridheight = 2;
        otherPortsLabel =
                ComponentFactory
                        .getH1Label(
                                STLConstants.K0039_NOT_AVAILABLE.getValue(),
                                Font.PLAIN);
        otherPortsLabel.setForeground(UIConstants.INTEL_GRAY);
        otherPortsLabel.setHorizontalAlignment(JLabel.RIGHT);
        bottomPanel.add(otherPortsLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.gridheight = 1;
        gc.weightx = 0;
        gc.weighty = 1;
        JLabel label =
                ComponentFactory
                        .getH4Label(
                                STLConstants.K1026_PORT_RESOURCE.getValue(),
                                Font.PLAIN);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        bottomPanel.add(label, gc);

        gc.weighty = 0;
        label =
                ComponentFactory
                        .getH4Label(
                                STLConstants.K2077_NOT_IN_FABRIC.getValue(),
                                Font.PLAIN);
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        bottomPanel.add(label, gc);
        add(bottomPanel, BorderLayout.SOUTH);
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

    @Override
    public void setName(String name) {
        nameLabel.setText(name);
    }

    public void setTotalNumber(String value) {
        numberLabel.setText(value);
    }

    public void setSwitches(String number, String label) {
        swNumberLabel.setText(number);
        swNameLabel.setText(label);
    }

    public void setFabricInterfaes(String number, String label) {
        fiNumberLabel.setText(number);
        fiNameLabel.setText(label);
    }

    // public void setRouters(String number, String label) {
    // rtNumberLabel.setText(number);
    // rtNameLabel.setText(label);
    // }

    public void setTypeDataset(DefaultPieDataset dataset, Color[] colors) {
        JFreeChart chart =
                ComponentFactory.createPlainPieChart(dataset, colors);
        typeChartPanel.setChart(chart);
    }

    public void setOtherPorts(String value) {
        otherPortsLabel.setText(value);
    }

    public void clear() {
        String na = STLConstants.K0039_NOT_AVAILABLE.getValue();
        setTotalNumber(na);
        swNumberLabel.setText(na);
        fiNumberLabel.setText(na);
        // rtNumberLabel.setText(na);
        setOtherPorts(na);
    }
}
