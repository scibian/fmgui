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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.general.Dataset;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;

import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.DatasetDescription;

public class ChartsView extends JCardView<IChartsCardListener> {
    private static final long serialVersionUID = -2134542205733463526L;

    private JPanel ctrPanel;

    private JComboBox<DatasetDescription> chartList;

    private ActionListener chartListListener;

    private JPanel mainPanel;

    private final IChartCreator chartCreator;

    private JumpChartPanel chartPanel;

    private Map<String, ChartWrap> charts;

    /**
     * Description:
     *
     * @param title
     * @param controller
     */
    public ChartsView(String title, IChartCreator chartCreator) {
        super(title);
        setPreferredSize(new Dimension(270, 250));
        this.chartCreator = chartCreator;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.common.JCard#getExtraComponent()
     */
    @Override
    protected JComponent getExtraComponent() {
        if (ctrPanel == null) {
            ctrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 1, 1));
        }
        return ctrPanel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.common.JCard#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel(new BorderLayout(0, 0));
            mainPanel.setOpaque(false);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));

            chartPanel = new JumpChartPanel(null);
            chartPanel.setOpaque(false);
            chartPanel.addComponentListener(new ComponentAdapter() {
                @Override
                public void componentResized(ComponentEvent e) {
                    chartPanel
                            .setMaximumDrawHeight(e.getComponent().getHeight());
                    chartPanel.setMaximumDrawWidth(e.getComponent().getWidth());
                    chartPanel.setMinimumDrawWidth(e.getComponent().getWidth());
                    chartPanel
                            .setMinimumDrawHeight(e.getComponent().getHeight());
                }
            });
            mainPanel.add(chartPanel, BorderLayout.CENTER);
        }
        return mainPanel;
    }

    public void setMainPanelName(String name){
        if(null != mainPanel){
            mainPanel.setName(name);
        }
    }

    public void setDatasets(List<DatasetDescription> datasets) {
        setChartNames(datasets);
        charts = new HashMap<String, ChartWrap>();
        if (datasets != null) {
            for (DatasetDescription dd : datasets) {
                String name = dd.getName();
                Dataset dataset = dd.getDataset();
                charts.put(name, new ChartWrap(
                        chartCreator.createChart(dd.getFullName(), dataset),
                        dd.isJumpable()));
            }
        }
    }

    protected void setChartNames(List<DatasetDescription> datasets) {
        if (ctrPanel == null) {
            // shouldn't happen
            throw new RuntimeException(
                    "Something weird happend! chartPanel is null");
        }

        if (datasets == null || datasets.isEmpty()) {
            return;
        } else if (datasets.size() == 1) {
            DatasetDescription dd = datasets.get(0);
            setTitle(dd.getName(), dd.getFullName());
        } else {
            setTitle(null);
            chartList = new JComboBox<DatasetDescription>(
                    datasets.toArray(new DatasetDescription[0]));
            IntelComboBoxUI ui = new IntelComboBoxUI() {

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.intel.stl.ui.common.view.IntelComboBoxUI#getValueString
                 * (java.lang.Object)
                 */
                @Override
                protected String getValueString(Object value) {
                    return ((DatasetDescription) value).getName();
                }

                /*
                 * (non-Javadoc)
                 *
                 * @see
                 * com.intel.stl.ui.common.view.IntelComboBoxUI#getValueTooltip
                 * (java.lang.Object)
                 */
                @Override
                protected String getValueTooltip(Object value) {
                    return ((DatasetDescription) value).getFullName();
                }

            };
            ui.setEditorBorder(BorderFactory.createEmptyBorder());
            ui.setArrowButtonTooltip(
                    UILabels.STL10103_MORE_SELECTIONS.getDescription());
            ui.setArrowButtonBorder(null);
            chartList.setUI(ui);
            setListListener();
            ctrPanel.add(chartList);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.view.JCardView#setCardListener(com.intel.stl.
     * ui.common.view.ICardListener)
     */
    @Override
    public void setCardListener(IChartsCardListener listener) {
        if (chartPanel == null) {
            // shouldn't happen
            throw new RuntimeException(
                    "Something weird happend! chartPanel is null");
        }

        IChartsCardListener oldListener = this.listener;
        super.setCardListener(listener);
        setListListener();

        if (oldListener != null) {
            chartPanel.removeListener(oldListener);
        }
        if (listener != null) {
            chartPanel.addListener(listener);
        }
    }

    protected void setListListener() {
        if (chartList == null || listener == null) {
            return;
        }

        if (chartListListener != null) {
            chartList.removeActionListener(chartListListener);
        }
        chartListListener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DatasetDescription dd =
                        (DatasetDescription) chartList.getSelectedItem();
                listener.onSelectChart(dd.getName());
            }

        };
        chartList.addActionListener(chartListListener);
    }

    public void setChart(String name) {
        if (chartPanel == null) {
            // shouldn't happen
            throw new RuntimeException(
                    "Something weird happend! chartPanel is null");
        }

        ChartWrap cw = getChartWrap(name);
        chartPanel.setChart(cw.chart, cw.isJumpable);
        super.enablePin(cw.isPinnable());
        validate();
    }

    public void selectChart(String name) {
        if (chartList != null) {
            DatasetDescription dd =
                    (DatasetDescription) chartList.getSelectedItem();
            if (!dd.getName().equals(name)) {
                int index = indexOf(name);
                if (index >= 0) {
                    chartList.setSelectedIndex(index);
                }
                // setChart will get called again via #setSelectedIndex
                return;
            }
        }
    }

    protected int indexOf(String name) {
        for (int i = 0; i < chartList.getItemCount(); i++) {
            DatasetDescription dd = chartList.getItemAt(i);
            if (dd.getName().equals(name)) {
                return i;
            }
        }
        return -1;
    }

    public JFreeChart getChart(String name) {
        return getChartWrap(name).chart;
    }

    /**
     * <i>Description:</i> enable/disable pin for all charts
     *
     * @see com.intel.stl.ui.common.view.JCardView#enablePin(boolean)
     */
    @Override
    public void enablePin(boolean b) {
        for (ChartWrap cw : charts.values()) {
            cw.setPinnable(b);
        }
        super.enablePin(b);
    }

    /**
     *
     * <i>Description:</i> enable/disable pin for a chart
     *
     * @param name
     *            chart name
     * @param b
     *            indicate whether to enable pin the specified chart
     */
    public void enablePin(String name, boolean b) {
        ChartWrap cw = getChartWrap(name);
        if (chartPanel.getChart() == cw.getChart()) {
            super.enablePin(b);
        }
        cw.setPinnable(b);
    }

    public JFreeChart getSparkline(String name) {
        if (charts == null) {
            return null;
        }

        JFreeChart chart = getChartWrap(name).chart;
        if (chart == null) {
            return null;
        }

        XYPlot plot = chart.getXYPlot();
        if (plot == null) {
            return null;
        }

        int count = plot.getDatasetCount();
        if (count == 3) {
            return ComponentFactory.createUtilXYTrendSparkline(
                    (IntervalXYDataset) plot.getDataset(0), plot.getDataset(1),
                    plot.getDataset(2));
        } else {
            XYDataset dataset = plot.getDataset();
            return ComponentFactory
                    .createXYTrendSparkline((IntervalXYDataset) dataset);
        }
    }

    protected ChartWrap getChartWrap(String name) {
        ChartWrap res = charts.get(name);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find chart '" + name + "'");
        }
    }

    private class ChartWrap {
        JFreeChart chart;

        boolean isJumpable;

        boolean isPinnable;

        /**
         * Description:
         *
         * @param chart
         * @param isJumpable
         */
        public ChartWrap(JFreeChart chart, boolean isJumpable) {
            super();
            this.chart = chart;
            this.isJumpable = isJumpable;
        }

        /**
         * @return the chart
         */
        public JFreeChart getChart() {
            return chart;
        }

        /**
         * @return the isPinnable
         */
        public boolean isPinnable() {
            return isPinnable;
        }

        /**
         * @param isPinnable
         *            the isPinnable to set
         */
        public void setPinnable(boolean isPinnable) {
            this.isPinnable = isPinnable;
        }

    }
}
