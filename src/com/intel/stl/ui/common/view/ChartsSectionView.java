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
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.model.ChartGroup;

/**
 */
public class ChartsSectionView extends JSectionView<ISectionListener> {
    private static final long serialVersionUID = -2957418754040069251L;

    private final List<TabTitle> titles = new ArrayList<TabTitle>();

    private JPanel mainPanel;

    private TabbedPanel[] groupPanels;

    private ChangeListener listener;

    public ChartsSectionView(String title) {
        super(title);
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.common.JSection#getMainPanel()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
        }
        return mainPanel;
    }

    public void setChartGroups(ChartGroup[] groups) {
        installChartsViews(mainPanel, groups);
    }

    protected void installChartsViews(JPanel panel, ChartGroup[] chartsGroups) {
        setLayout(panel);
        groupPanels = new TabbedPanel[chartsGroups.length];
        for (int i = 0; i < chartsGroups.length; i++) {
            groupPanels[i] = createTabbedPane(chartsGroups[i]);
            panel.add(groupPanels[i]);
        }
    }

    protected void setLayout(JPanel mainPanel) {
        mainPanel.setLayout(new GridLayout(1, 4, 5, 5));
    }

    protected TabbedPanel createTabbedPane(ChartGroup chartsGroup) {
        if (chartsGroup == null || !chartsGroup.hasMembers()) {
            throw new IllegalArgumentException(
                    "ChartGroup cannot be null and it must have members");
        }

        TabbedPanel panel = new TabbedPanel();
        panel.setName(chartsGroup.getName());
        if (listener != null) {
            panel.setListener(listener);
        }
        List<ChartGroup> members = chartsGroup.getMembers();
        for (int i = 0; i < members.size(); i++) {
            ChartGroup member = members.get(i);
            Component component = createGroupPanel(member);
            TabTitle title =
                    new TabTitle(panel, member.getName(), member.getChartView());
            panel.addTab(member.getName(), title, component);
            titles.add(title);
        }
        return panel;
    }

    protected Component createGroupPanel(ChartGroup group) {
        if (group == null || !group.hasMembers()) {
            throw new IllegalArgumentException(
                    "ChartGroup cannot be null and it must have members");
        }

        JPanel panel = new JPanel(new GridLayout(1, group.numMembers(), 5, 5));
        List<ChartGroup> members = group.getMembers();
        for (ChartGroup member : members) {
            ChartsView view = member.getChartView();
            view.setPreferredSize(new Dimension(270, 250));
            panel.add(view);
        }
        return panel;
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void setListener(ChangeListener listener) {
        this.listener = listener;
        if (groupPanels != null) {
            for (TabbedPanel panel : groupPanels) {
                panel.setListener(listener);
            }
        }
    }

    public class TabbedPanel extends JPanel {
        private static final long serialVersionUID = -1113934752788733902L;

        private JPanel tabPanel;

        private CardLayout layout;

        private JPanel titlePanel;

        private final LinkedHashMap<String, TabTitle> titles =
                new LinkedHashMap<String, TabTitle>();

        private Component padPanel;

        private GridBagConstraints gc;

        private String previousSelection;

        private String selection;

        private ChangeListener listener;

        public TabbedPanel() {
            super();
            initComponent();
        }

        private void initComponent() {
            setLayout(new BorderLayout());
            setOpaque(false);

            layout = new CardLayout();
            tabPanel = new JPanel(layout);
            tabPanel.setBorder(BorderFactory.createMatteBorder(3, 3, 5, 3,
                    UIConstants.INTEL_WHITE));
            add(tabPanel, BorderLayout.CENTER);

            titlePanel = new JPanel(new GridBagLayout());
            titlePanel.setOpaque(false);
            gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.BOTH;
            gc.weightx = 0;
            gc.weighty = 0;
            gc.gridwidth = 1;

            padPanel = Box.createHorizontalGlue();
            titlePanel.add(padPanel);
            add(titlePanel, BorderLayout.SOUTH);
        }

        public void addTab(String name, TabTitle title, Component content) {
            tabPanel.add(name, content);

            titlePanel.remove(padPanel);
            gc.weightx = 0;
            titlePanel.add(title, gc);
            gc.weightx = 1;
            titlePanel.add(padPanel, gc);
            titles.put(name, title);

            if (selection == null) {
                select(name);
            }
        }

        public void select(String name) {
            if (selection != null) {
                TabTitle tab = titles.get(selection);
                if (tab != null) {
                    tab.setSelected(false);
                }
            }
            TabTitle tab = titles.get(name);
            if (tab != null) {
                tab.setSelected(true);
            }
            layout.show(tabPanel, name);
            previousSelection = selection;
            selection = name;
            if (listener != null) {
                listener.stateChanged(new ChangeEvent(this));
            }
        }

        /**
         * @return the selection
         */
        public String getSelection() {
            return selection;
        }

        /**
         * @return the previousSelection
         */
        public String getPreviousSelection() {
            return previousSelection;
        }

        /**
         * @param listener
         *            the listener to set
         */
        public void setListener(ChangeListener listener) {
            this.listener = listener;
        }
    }

    class TabTitle extends JPanel {
        private static final long serialVersionUID = 268393669119889498L;

        private final TabbedPanel parent;

        private final String name;

        private final String sparkline;

        private final ChartsView chartsView;

        private ChartPanel chartPanel;

        private JLabel title;

        private JFreeChart chart;

        private boolean isSelected;

        /**
         * Description:
         * 
         * @param name
         * @param chartsView
         */
        public TabTitle(TabbedPanel parent, String name, ChartsView chartsView) {
            super();
            this.parent = parent;
            this.name = name;
            this.sparkline = chartsView.getTitle();
            this.chartsView = chartsView;
            initComponent();
            setSelected(isSelected);
            update();
        }

        protected void initComponent() {
            setLayout(new BorderLayout());
            setOpaque(true);
            setBorder(BorderFactory.createMatteBorder(0, 2, 1, 2,
                    UIConstants.INTEL_WHITE));

            title = ComponentFactory.getH4Label(name, Font.BOLD);
            title.setHorizontalAlignment(JLabel.CENTER);
            title.setBackground(UIConstants.INTEL_WHITE);
            title.setBorder(BorderFactory.createEmptyBorder(0, 2, 2, 2));
            add(title, BorderLayout.SOUTH);

            chartPanel = new ChartPanel(null);
            chartPanel.setPreferredSize(new Dimension(60, 20));
            chartPanel.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    parent.select(name);
                }
            });
            add(chartPanel, BorderLayout.CENTER);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    parent.select(name);
                }
            });

        }

        protected void update() {
            chart = chartsView.getSparkline(sparkline);
            if (chart == null) {
                return;
            }

            chartPanel.setChart(chart);
            if (isSelected) {
                chart.setBackgroundPaint(UIConstants.INTEL_WHITE);
            } else {
                chart.setBackgroundPaint(new Color(240, 240, 240, 0));
            }
        }

        public void setSelected(boolean b) {
            isSelected = b;
            if (b) {
                title.setOpaque(true);
                setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
                if (chart != null) {
                    chart.setBackgroundPaint(UIConstants.INTEL_WHITE);
                }
            } else {
                title.setOpaque(false);
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(5, 5, 0, 5),
                        // BorderFactory.createRaisedBevelBorder()
                        BorderFactory.createLineBorder(UIConstants.INTEL_GRAY)));
                if (chart != null) {
                    chart.setBackgroundPaint(new Color(240, 240, 240, 0));
                }
            }
        }
    }
}
