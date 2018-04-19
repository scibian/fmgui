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

package com.intel.stl.ui.wizards.view.event;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;

import org.jdesktop.swingx.JXList;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ButtonPopup;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.EventRuleActionViz;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.model.event.EventRulesTableModel;
import com.intel.stl.ui.wizards.view.IWizardView;
import com.intel.stl.ui.wizards.view.MultinetWizardView;

/**
 * Popup panel for the action column of the Event Wizard table
 */
public class ActionPanel extends JPanel {

    private static final long serialVersionUID = -2996609094010046773L;

    private JButton btnAction;

    private PopupPanel popupPanel;

    private ButtonPopup popup;

    private final List<JCheckBox> cbActionList = new ArrayList<JCheckBox>();

    private List<EventRule> eventRules;

    private final EventRulesTableModel tableModel;

    private int activeRow = -1;

    private int popupCount;

    private final IWizardView wizardViewListener;

    private IWizardTask eventWizardControlListener;

    public ActionPanel(EventRulesTableModel model,
            IWizardView wizardViewListener) {

        this.tableModel = model;
        this.wizardViewListener = wizardViewListener;
        initComponents();
    }

    protected void initComponents() {

        // Create the panel to hold the popup's toolbar
        JPanel mainPanel = new JPanel();

        // Create the toolbar and put the button on it
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        btnAction = ComponentFactory
                .getIntelActionButton(STLConstants.K3004_SELECT.getValue());

        btnAction.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (popup.isVisible()) {
                    popup.hide();
                } else {
                    wizardViewListener.enableApply(true);
                    popup.show();
                }
            }
        });
        toolBar.add(btnAction);
        mainPanel.add(toolBar);

        popupPanel = new PopupPanel();
        popup = new ButtonPopup(btnAction, popupPanel, false) {

            @Override
            public void onShow() {
                if (activeRow == -1) {
                    // shouldn't happen
                    return;
                }

                popupCount += 1;
                List<EventRuleAction> actionList =
                        tableModel.getEntry(activeRow).getEventActions();

                // Initialize the check boxes to unselected
                for (int i = 0; i < cbActionList.size(); i++) {
                    cbActionList.get(i).setSelected(false);
                }

                // Loop through the actions from the model and select the
                // corresponding check boxes before opening the panel
                Iterator<JCheckBox> it = cbActionList.iterator();
                Iterator<EventRuleAction> actionIterator =
                        actionList.iterator();

                while (actionIterator.hasNext()) {

                    EventRuleAction action = actionIterator.next();
                    EventRuleActionViz actViz =
                            EventRuleActionViz.getEventRuleActionVizFor(action);
                    boolean found = false;
                    while (actViz != null && it.hasNext() && !found) {
                        JCheckBox chkbox = it.next();
                        found = (actViz.getName().equals(chkbox.getText()))
                                ? true : false;
                        chkbox.setSelected(found);
                    }
                }
            }

            @Override
            public void onHide() {
                popupCount -= 1;
                updateModel(activeRow, ITableListener.ACTION_EDITOR_COLUMN);
            }
        };
    }

    public void updateModel(int row, int column) {
        popupPanel.updateModel(row, column);
    }

    /**
     * @return the action button
     */
    public JButton getActionButton() {
        return btnAction;
    }

    /**
     * @param activeRow
     *            the activeRow to set
     */
    public void setActiveRow(int activeRow) {
        this.activeRow = activeRow;
    }

    public boolean isReady() {
        return popupCount == 0;
    }

    /**
     * @param eventWizardControlListener
     *            the eventWizardControlListener to set
     */
    public void setEventWizardControlListener(
            IWizardTask eventWizardControlListener) {
        this.eventWizardControlListener = eventWizardControlListener;
    }

    class PopupPanel extends JPanel implements ListCellRenderer<JCheckBox> {

        private static final long serialVersionUID = 5009991107568106318L;

        private DefaultListModel<JCheckBox> listModel;

        private JXList list;

        public PopupPanel() {
            super();
            initPopupPanel();
        }

        protected void initPopupPanel() {

            setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));
            listModel = new DefaultListModel<JCheckBox>();
            list = new JXList(listModel);
            list.setVisibleRowCount(10);
            list.setCellRenderer(this);

            // Create the check boxes on a panel
            JPanel pnlActionList = new JPanel();
            pnlActionList.setLayout(
                    new BoxLayout(pnlActionList, BoxLayout.PAGE_AXIS));
            for (EventRuleActionViz action : EventRuleActionViz.values()) {
                final JCheckBox chkBox =
                        ComponentFactory.getIntelCheckBox(action.getName());
                chkBox.setName(action.getWidgetName().name());
                cbActionList.add(chkBox);
                chkBox.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        eventWizardControlListener.setDirty(true);
                    }

                });
                listModel.addElement(chkBox);
                pnlActionList.add(chkBox);
            }

            JScrollPane scroll = new JScrollPane(pnlActionList);
            scroll.getViewport().getView()
                    .setBackground(MultinetWizardView.WIZARD_COLOR);
            add(scroll, BorderLayout.CENTER);
        } // initPopupPanel

        protected void updateModel(final int row, int column) {

            Runnable updater = new Runnable() {
                @Override
                public void run() {

                    // Get the list of event rules from the model
                    eventRules = tableModel.getEventRules();

                    // Create a list of actions based on selected check boxes
                    List<EventRuleAction> actions =
                            new ArrayList<EventRuleAction>();

                    for (int i = 0; i < cbActionList.size(); i++) {

                        if (cbActionList.get(i).isSelected()) {
                            actions.add(EventRuleAction.values()[i]);
                        }
                    }

                    // Update the rules
                    eventRules.get(row).getEventActions().clear();
                    eventRules.get(row).setEventActions(actions);

                    // Update the model
                    tableModel.updateTable(eventRules);
                }
            };
            Util.runInEDT(updater);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
         * .JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(
                JList<? extends JCheckBox> list, JCheckBox value, int index,
                boolean isSelected, boolean cellHasFocus) {

            return cbActionList.get(index);
        }
    } // class PopupPanel

} // class ActionPanel
