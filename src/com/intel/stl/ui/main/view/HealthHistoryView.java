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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTitleAnnotation;
import org.jfree.chart.event.ChartProgressEvent;
import org.jfree.chart.event.ChartProgressListener;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.IntervalXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.common.view.JCardView;

/**
 */
public class HealthHistoryView extends JCardView<ICardListener> {
    private static final long serialVersionUID = -7089992232091159132L;

    private final Font scoreFont;

    private JPanel mainPanel;

    private ChartPanel chartPanel;

    private JLabel startTimeLabel;

    private JLabel endTimeLabel;

    private TextTitle currentValue;

    private String scoreString = STLConstants.K0039_NOT_AVAILABLE.getValue();

    private Color scoreColor = UIConstants.INTEL_DARK_GRAY;

    private String scoreTip;

    /**
     * @param title
     * @param controller
     */
    public HealthHistoryView() {
        this(UIConstants.H1_FONT.deriveFont(Font.BOLD));
    }

    public HealthHistoryView(Font scoreFont) {
        super(STLConstants.K0105_HEALTH_HISTORY.getValue());
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
        this.scoreFont = scoreFont;
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

        mainPanel = new JPanel();
        GridBagLayout gridBag = new GridBagLayout();
        mainPanel.setLayout(gridBag);
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.BOTH;
        gc.weightx = 1;
        gc.weighty = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        chartPanel = new ChartPanel(null);
        // chart.PanelsetBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
        // UIConstants.INTEL_BORDER_GRAY));
        chartPanel.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                chartPanel.setMaximumDrawHeight(e.getComponent().getHeight());
                chartPanel.setMaximumDrawWidth(e.getComponent().getWidth());
                chartPanel.setMinimumDrawWidth(e.getComponent().getWidth());
                chartPanel.setMinimumDrawHeight(e.getComponent().getHeight());
            }
        });
        mainPanel.add(chartPanel, gc);

        gc.gridwidth = 1;
        gc.insets = new Insets(0, 5, 0, 5);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        gc.weighty = 0;
        startTimeLabel = ComponentFactory.getH5Label("start", Font.PLAIN);
        mainPanel.add(startTimeLabel, gc);

        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.anchor = GridBagConstraints.EAST;
        endTimeLabel = ComponentFactory.getH5Label("end", Font.PLAIN);
        mainPanel.add(endTimeLabel, gc);

        setHelpButtonName(WidgetName.HP_HEALTH_HIST_HELP.name());
        setPinButtonName(WidgetName.HP_HEALTH_HIST_PIN.name());

        return mainPanel;
    }

    public void setDataset(final IntervalXYDataset dataset) {
        JFreeChart chart =
                ComponentFactory.createStepAreaChart(dataset,
                        new XYItemLabelGenerator() {
                            @Override
                            public String generateLabel(XYDataset dataset,
                                    int series, int item) {
                                Number val = dataset.getY(series, item);
                                return UIConstants.INTEGER.format(val
                                        .intValue());
                            }
                        });
        chart.addProgressListener(new ChartProgressListener() {
            @Override
            public void chartProgress(ChartProgressEvent event) {
                if (event.getType() == ChartProgressEvent.DRAWING_STARTED
                        && currentValue != null) {
                    currentValue.setText(scoreString);
                    currentValue.setPaint(scoreColor);
                    currentValue.setToolTipText(scoreTip);
                }
            }
        });
        XYPlot plot = chart.getXYPlot();
        plot.getRangeAxis().setRange(0, 105);
        plot.getRenderer().setSeriesPaint(0, UIConstants.INTEL_BLUE);
        currentValue = new TextTitle(scoreString, scoreFont);
        currentValue.setPaint(scoreColor);
        currentValue.setToolTipText(scoreTip);
        // currentValue.setBackgroundPaint(new Color(255, 255, 255, 128));
        currentValue.setPosition(RectangleEdge.BOTTOM);
        XYTitleAnnotation xytitleannotation =
                new XYTitleAnnotation(0.49999999999999998D,
                        0.49999999999999998D, currentValue,
                        RectangleAnchor.CENTER);
        // xytitleannotation.setMaxWidth(0.47999999999999998D);
        plot.addAnnotation(xytitleannotation);

        chartPanel.setChart(chart);
    }

    /**
     * Description:
     * 
     * @param format
     * @param format2
     */
    public void setTimeDuration(String start, String end) {
        startTimeLabel.setText(start);
        endTimeLabel.setText(end);
        validate();
    }

    public void setCurrentScore(String score, Color color, String tip) {
        this.scoreString = score;
        this.scoreColor = color;
        this.scoreTip = tip;
    }
}
