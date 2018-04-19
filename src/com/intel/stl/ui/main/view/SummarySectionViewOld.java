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
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;

/**
 */
public class SummarySectionViewOld extends JSectionView<ISectionListener> {
    private static final long serialVersionUID = 7004235726509918990L;

    private JPanel statesPanel;
    private StatisticsViewOld statisticsView;
    private NodeStatesViewOld swStatesPie;
    private NodeStatesViewOld caStatesPie;
    private HealthHistoryView healthHistoryView;
    private WorstNodesView worstNodesView;

    public SummarySectionViewOld() {
        super(STLConstants.K0102_HOME_SUMMARY.getValue());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.common.JSection#getMainPanel()
     */
    protected JPanel getMainComponent() {
        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 5, 2));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        statisticsView = new StatisticsViewOld();
        mainPanel.add(statisticsView);

        statesPanel = getStatesPanel();
        mainPanel.add(statesPanel);

        return mainPanel;
    }

    protected JPanel getStatesPanel() {
        if (statesPanel == null) {
            statesPanel = new JPanel(new BorderLayout(0, 8));

            JPanel topPanel = new JPanel(new GridLayout(1, 2, 8, 2));
            swStatesPie = new NodeStatesViewOld(STLConstants.K0033_SWITCH_STAETES.getValue());
            swStatesPie.setPreferredSize(new Dimension(150, 200));
            swStatesPie.setBackground(UIConstants.INTEL_WHITE);
            topPanel.add(swStatesPie);
            caStatesPie = new NodeStatesViewOld(STLConstants.K0034_HFI_STATES.getValue());
            caStatesPie.setPreferredSize(new Dimension(150, 200));
            caStatesPie.setBackground(UIConstants.INTEL_WHITE);
            topPanel.add(caStatesPie);
            statesPanel.add(topPanel, BorderLayout.CENTER);

            JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 8, 2));
            healthHistoryView = new HealthHistoryView();
            healthHistoryView.setBackground(UIConstants.INTEL_WHITE);
            healthHistoryView.setPreferredSize(new Dimension(150, 100));
            bottomPanel.add(healthHistoryView);
            worstNodesView = new WorstNodesView();
            worstNodesView.setBackground(UIConstants.INTEL_WHITE);
            bottomPanel.add(worstNodesView);
            statesPanel.add(bottomPanel, BorderLayout.SOUTH);
        }
        return statesPanel;
    }

    /**
     * @return the statisticsView
     */
    public StatisticsViewOld getStatisticsView() {
        return statisticsView;
    }

    /**
     * @return the swStatesPie
     */
    public NodeStatesViewOld getSwStatesPie() {
        return swStatesPie;
    }

    /**
     * @return the caStatesPie
     */
    public NodeStatesViewOld getCaStatesPie() {
        return caStatesPie;
    }

    /**
     * @return the healthHistoryView
     */
    public HealthHistoryView getHealthHistoryView() {
        return healthHistoryView;
    }

    /**
     * @return the worstNodesView
     */
    public WorstNodesView getWorstNodesView() {
        return worstNodesView;
    }

}
