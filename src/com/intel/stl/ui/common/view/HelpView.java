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

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.List;

import javax.help.BadIDException;
import javax.help.JHelpContentViewer;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.jdesktop.swingx.autocomplete.AutoCompleteDecorator;
import org.jdesktop.swingx.combobox.ListComboBoxModel;

import com.intel.stl.ui.common.IHelp;

/**
 *
 * View for the console help utility
 */
public class HelpView extends JPanel {
    private static final long serialVersionUID = 6892075486378808599L;

    private static final Logger log = LoggerFactory.getLogger(HelpView.class);

    private final String title;

    private List<String> topicIdList;

    private final JHelpContentViewer topicPanel;

    private JComboBox cboxTopic;

    private final IHelp helpController;

    public HelpView(String title, List<String> topicIdList,
            JHelpContentViewer topicPanel, IHelp helpController) {

        this.title = title;
        this.topicIdList = topicIdList;
        this.topicPanel = topicPanel;
        this.helpController = helpController;
        initComponents();
    }

    protected void initComponents() {
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(1, 5, 2, 5);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;

        JLabel lblTitle = new JLabel(title);
        lblTitle.setFont(new Font("Segoe UI", Font.PLAIN, 16));

        lblTitle.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                Color.ORANGE));
        lblTitle.setHorizontalAlignment(JLabel.CENTER);
        add(lblTitle, gc);

        cboxTopic = new JComboBox();
        cboxTopic.setUI(new IntelComboBoxUI());
        cboxTopic.setEditable(true);
        AutoCompleteDecorator.decorate(cboxTopic);
        add(cboxTopic, gc);
        cboxTopic.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                if (topicIdList.contains(cboxTopic.getSelectedItem())) {
                    helpController.showTopic((String) cboxTopic
                            .getSelectedItem());
                }
            }
        });
        cboxTopic.setModel(new ListComboBoxModel<String>(topicIdList));

        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        add(topicPanel, gc);
    }

    /**
     * @return the topicIdList
     */
    public List<String> getTopicIdList() {
        return topicIdList;
    }

    /**
     * @param topicIdList
     *            the topicIdList to set
     */
    public void setTopicIdList(List<String> topicIdList) {
        this.topicIdList = topicIdList;
    }

    public void displayTopic(String topic) {
        try {
            topicPanel.setCurrentID(topic);
        } catch (BadIDException e) {
            // silent on this since a user may type in any string
            // e.printStackTrace();
        }
    }

    public void selectTopic(String topicId) {
        cboxTopic.setSelectedItem(topicId);
        helpController.showTopic((String) cboxTopic.getSelectedItem());
    }

    public void updateSelection(String value) {
        cboxTopic.setSelectedItem(value);
    }
    
    public void resetView(){
    	updateSelection("");
    	topicPanel.clear();
    }
}
