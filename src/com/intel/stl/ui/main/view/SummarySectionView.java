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
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;

/**
 */
public class SummarySectionView extends JSectionView<ISectionListener> {
    private static final long serialVersionUID = 7004235726509918990L;

    private JPanel mainPanel;
    private StatisticsView statisticsView;
    
    private JPanel statesPanel;
    private StatusView statusView;
    private HealthHistoryView healthView;
    private WorstNodesView worstNodesView;

    public SummarySectionView() {
        super(STLConstants.K0102_HOME_SUMMARY.getValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.common.JSection#getMainPanel()
     */
    protected JPanel getMainComponent() {
        if (mainPanel==null) {
            mainPanel = new JPanel(new GridBagLayout());
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 3, 5));

            GridBagConstraints gc = new GridBagConstraints();
            gc.insets = new Insets(0, 0, 0, 5);
            gc.weightx = 1;
            gc.weighty = 1;
            gc.gridwidth = 1;
            gc.fill = GridBagConstraints.BOTH;

            statisticsView =new StatisticsView();
            mainPanel.add(statisticsView, gc);

            gc.insets = new Insets(0, 0, 0, 0);
            gc.gridwidth = GridBagConstraints.REMAINDER;
            statesPanel = getStatesPanel();
            mainPanel.add(statesPanel, gc);
            setHelpButtonName(WidgetName.HP_STAT_SUM_SECTION_HELP.name());
        }
        return mainPanel;
    }

    protected JPanel getStatesPanel() {
        if (statesPanel == null) {
            statesPanel = new JPanel(new GridLayout(1, 2, 8, 2));

            statusView = new StatusView();
            statusView.setBackground(UIConstants.INTEL_WHITE);
            statesPanel.add(statusView);
            
            JPanel rightPanel = new JPanel(new GridLayout(2, 1, 8, 2));
            healthView = new HealthHistoryView();
            healthView.setBackground(UIConstants.INTEL_WHITE);
            healthView.setPreferredSize(new Dimension(150, 100));
            rightPanel.add(healthView);
            worstNodesView = new WorstNodesView();
            worstNodesView.setBackground(UIConstants.INTEL_WHITE);
            rightPanel.add(worstNodesView);
            statesPanel.add(rightPanel);
        }
        return statesPanel;
    }

    /**
     * @return the statisticsView
     */
    public StatisticsView getStatisticsView() {
        return statisticsView;
    }

    /**
     * @return the swStatesView
     */
    public StatusView getStatusView() {
        return statusView;
    }

    /**
     * @return the healthView
     */
    public HealthHistoryView getHealthView() {
        return healthView;
    }

    /**
     * @return the worstNodesView
     */
    public WorstNodesView getWorstNodesView() {
        return worstNodesView;
    }

}
