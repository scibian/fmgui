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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.IEventSummaryBarListener;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.model.NoticeSeverityViz;

/**
 * Severity count summary panel (EventSummaryBarPanel) that goes to pin board.
 */
public class EventSummaryBarPanel extends JPanel {

    /**
     * 
     */
    private static final long serialVersionUID = 1457639937923618304L;

    /**
     * Logging instance
     */
    private static Logger log = LoggerFactory
            .getLogger(EventSummaryBarPanel.class);

    private final EnumMap<NoticeSeverity, LabelPanel> severityPanelList =
            new EnumMap<NoticeSeverity, LabelPanel>(NoticeSeverity.class);

    private final GridBagLayout severityPanelGridBagLayout =
            new GridBagLayout();

    private final GridBagConstraints severityPanelConstraints =
            new GridBagConstraints();

    private MouseListener panelMouseListener = null;

    private IEventSummaryBarListener iEventSummaryBarListener = null;
    
    private JPanel titlePanel;
    private String timingWindow;

    public EventSummaryBarPanel() {
        super();
        initialize();
    }

    public void setEventSummaryBarListener(
            IEventSummaryBarListener iEventSummary) {
        iEventSummaryBarListener = iEventSummary;
        installActionListener();
        initializeEventSeverity();
    }

    private void installActionListener() {
        panelMouseListener = new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                iEventSummaryBarListener.toggleEventSummaryTable();
            }

        };
    }

    public void initialize() {
        setLayout(severityPanelGridBagLayout);
        setBackground(UIConstants.INTEL_WHITE);
    }

    /**
     * Add event severity count label panels (LabelPanel) to the
     * EventSummaryBarPanel with the counts set with 'N/A'. Also, add mouse
     * click listener to each panel so that click on each panel can open/close
     * the event summary table at the bottom of left pane.
     * 
     */
    public void initializeEventSeverity() {
    	
        titlePanel = new JPanel();
    	JLabel titleLabel = ComponentFactory.getH4Label(
                STLConstants.K0128_NODES_DISTR_SEVERITY.getValue(),
                Font.BOLD);
    	
    	GridBagConstraints constraints = new GridBagConstraints();
    	constraints.insets = new Insets(2, 5, 2, 0);
        
        constraints.weightx = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        titlePanel.add(titleLabel, constraints);
        
        titlePanel.addMouseListener(panelMouseListener);
        severityPanelConstraints.weighty = 0.0;

        addToEventSummaryBarPane(titlePanel);
    	
        NoticeSeverity[] array = NoticeSeverity.values();
        for (int i = array.length - 1; 0 <= i; i--) {
            LabelPanel panel = new LabelPanel(array[i]);
            panel.addMouseListener(panelMouseListener);

            severityPanelList.put(array[i], panel);
            severityPanelConstraints.weighty = 0.0;

            addToEventSummaryBarPane(panel);

        }
        severityPanelConstraints.weighty = 1.0;
        severityPanelConstraints.fill = GridBagConstraints.BOTH;

        add(Box.createGlue(), severityPanelConstraints);
        // repaint, validate didn't work.
        revalidate();

    }

    public void updateEventSeverity(EnumMap<NoticeSeverity, Integer> countMap) {
    	
    	titlePanel.setToolTipText(UILabels.STL10008_NODES_DISTR_SEVERITY.getDescription(timingWindow));
    	
        // The severityPanelList is fully populated with four possible
        // severities.
        // Find a matching entry and update the count label.
        for (NoticeSeverity severity : countMap.keySet()) {
            LabelPanel panel = severityPanelList.get(severity);
            JLabel countLabel = panel.getCountLabel();
            countLabel.setText(UIConstants.INTEGER.format(countMap
                    .get(severity)));
        }
    }

    public void addToEventSummaryBarPane(JPanel panel) {

        severityPanelConstraints.weightx = 1.0;
        severityPanelConstraints.fill = GridBagConstraints.HORIZONTAL;
        severityPanelConstraints.gridwidth = GridBagConstraints.REMAINDER;

        severityPanelGridBagLayout.setConstraints(panel,
                severityPanelConstraints);
        add(panel, severityPanelConstraints);
    }

    public void setTimingWindow(String timingWindow){
    	this.timingWindow = timingWindow;
    }
    
    private class LabelPanel extends JPanel {
        private static final long serialVersionUID = -2905931691586163645L;

        private JLabel nameLabel;

        private final JLabel countLabel;

        private final JPanel countNamePanel;

        private JLabel iconLabel;

        private final GridBagLayout gridBagLayout = new GridBagLayout();

        private final GridBagConstraints constraints = new GridBagConstraints();

        public LabelPanel(NoticeSeverity severity) {
            super();

            setLayout(new BorderLayout(5, 2));
            setOpaque(false);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory
                    .createLineBorder(UIConstants.INTEL_BORDER_GRAY),
                    BorderFactory.createEmptyBorder(0, 5, 0, 5)));
            // setPreferredSize(new Dimension(200, 30));
            setBackground(UIConstants.INTEL_WHITE);

            countNamePanel = new JPanel(gridBagLayout);
            countNamePanel.setBackground(UIConstants.INTEL_WHITE);
            // countNamePanel.setPreferredSize(new Dimension(120, 30));
            countLabel =
                    ComponentFactory.getH2Label(
                            STLConstants.K0039_NOT_AVAILABLE.getValue(),
                            Font.BOLD);

            NoticeSeverityViz sevViz =
                    NoticeSeverityViz.getNoticeSeverityVizFor(severity);
            if (sevViz != null) {
                iconLabel =
                        new JLabel(sevViz.getIcon().getImageIcon(),
                                JLabel.RIGHT);
                countLabel.setForeground(sevViz.getColor());
                nameLabel =
                        ComponentFactory.getH4Label(sevViz.getName(),
                                Font.PLAIN);
            }
            if (nameLabel != null) {
                nameLabel.setHorizontalAlignment(JLabel.LEFT);
            }

            gridBagLayout.setConstraints(countNamePanel, constraints);

            constraints.insets = new Insets(2, 5, 2, 0);
            constraints.anchor = GridBagConstraints.EAST;
            constraints.fill = GridBagConstraints.NONE;
            countNamePanel.add(countLabel, constraints);

            constraints.weightx = 1;
            constraints.gridwidth = GridBagConstraints.REMAINDER;
            constraints.anchor = GridBagConstraints.SOUTHWEST;
            countNamePanel.add(nameLabel, constraints);

            add(iconLabel, BorderLayout.WEST);
            add(countNamePanel, BorderLayout.CENTER);
        }

        /**
         * @return the countLabel
         */
        public JLabel getCountLabel() {
            return countLabel;
        }

        /**
         * @return the nameLabel
         */
        public JLabel getNameLabel() {
            return nameLabel;
        }

    } // class LabelPanel

}
