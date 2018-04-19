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
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableCellRenderer;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.FVHeaderRenderer;
import com.intel.stl.ui.common.view.FVTableRenderer;
import com.intel.stl.ui.common.view.FVXTableView;
import com.intel.stl.ui.model.EventRuleActionViz;
import com.intel.stl.ui.model.EventTypeViz;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.impl.event.EventRulesTableColumns;
import com.intel.stl.ui.wizards.model.event.EventRulesTableModel;
import com.intel.stl.ui.wizards.view.IMultinetWizardView;
import com.intel.stl.ui.wizards.view.IWizardView;
import com.intel.stl.ui.wizards.view.MultinetWizardView;

/**
 * This class replaces the EventRulesPanel, in the Event Wizard, with a event
 * rules table on a single panel.
 */
public class EventTableView extends FVXTableView<EventRulesTableModel> {

    private static final long serialVersionUID = 2521825791205609777L;

    private static final boolean ACTION_COLUMN_ENABLE = true;

    private IWizardTask eventWizardControlListener;

    private JComboBox<String> cboxEventSeverity;

    private ActionPanel pnlEventAction;

    @SuppressWarnings("unused")
    private IWizardView wizardViewListener = null;

    private IMultinetWizardView multinetWizardViewListener = null;

    private boolean dirty;

    public EventTableView(EventRulesTableModel model,
            IWizardView wizardViewListener) {
        super(model);
        this.wizardViewListener = wizardViewListener;
        installEditors();
    }

    public EventTableView(EventRulesTableModel model,
            IMultinetWizardView wizardViewListener) {
        super(model);
        this.multinetWizardViewListener = wizardViewListener;
        installEditors();
    }

    @Override
    protected JXTable createTable(final EventRulesTableModel model) {
        final JXTable table = new JXTable(model);

        table.getSelectionModel()
                .addListSelectionListener(new ListSelectionListener() {
                    @Override
                    public void valueChanged(ListSelectionEvent e) {
                        model.setSelectedRow(table.getSelectedRow());
                    }
                });
        table.setRowHeight(22); // adapt to image icon's size
        table.setHorizontalScrollEnabled(true);
        table.setAutoscrolls(true);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(getMaximumSize());
        table.setAutoCreateColumnsFromModel(true);
        table.setAlignmentX(JTable.LEFT_ALIGNMENT);
        table.setBackground(MultinetWizardView.WIZARD_COLOR);
        table.setVisibleRowCount(15);
        table.setHighlighters(HighlighterFactory.createAlternateStriping(
                UIConstants.INTEL_WHITE, UIConstants.INTEL_TABLE_ROW_GRAY));
        table.getTableHeader().setReorderingAllowed(false);
        table.setSortable(false);

        // Turn off column control icon
        table.setColumnControlVisible(false);

        // The action column should only be enabled if e-mail is available
        if (!ACTION_COLUMN_ENABLE) {
            table.removeColumn(table.getColumnModel()
                    .getColumn(EventRulesTableColumns.EVENT_ACTION.getId()));
        }

        return table;
    }

    protected void installEditors() {
        // Create the event severity combo box
        cboxEventSeverity = new JComboBox<String>(new String[] {
                NoticeSeverity.INFO.name(), NoticeSeverity.WARNING.name(),
                NoticeSeverity.ERROR.name(), NoticeSeverity.CRITICAL.name() });
        cboxEventSeverity.setName(WidgetName.SW_E_SEVERITY_OPTION.name());
        cboxEventSeverity.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    dirty = true;
                    eventWizardControlListener.setDone(false);
                    multinetWizardViewListener.enableApply(true);
                    multinetWizardViewListener.enableReset(true);
                }
            }
        });
        EventSeverityEditor serverityEditor =
                new EventSeverityEditor(cboxEventSeverity, model);
        mTable.getColumn(ITableListener.SEVERITY_EDITOR_COLUMN)
                .setCellEditor(serverityEditor);

        // Create the popup window
        pnlEventAction = new ActionPanel(model, multinetWizardViewListener);

        // The action column should only be enabled if e-mail is available
        if (ACTION_COLUMN_ENABLE) {
            EventActionEditor actionEditor =
                    new EventActionEditor(pnlEventAction);
            mTable.getColumn(ITableListener.ACTION_EDITOR_COLUMN)
                    .setCellEditor(actionEditor);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.view.FVXTableView#formatTable()
     */
    @Override
    public void formatTable() {

        // Format the table headers
        FVHeaderRenderer headerRenderer = new FVHeaderRenderer(mTable);
        mHeader = (JXTableHeader) mTable.getTableHeader();
        mHeader.setFont(UIConstants.H3_FONT);

        Comparator<Number> numberComparator = new Comparator<Number>() {
            @Override
            public int compare(Number o1, Number o2) {
                return Double.compare(o1.doubleValue(), o2.doubleValue());
            }
        };

        FVTableRenderer tableRenderer = new FVTableRenderer() {

            private static final long serialVersionUID = 2677707405000074070L;

            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus, int row,
                    int column) {

                Component cell = super.getTableCellRendererComponent(table,
                        value, false, hasFocus, row, column);

                if (value == null) {
                    ((JLabel) cell).setText(STLConstants.K0383_NA.getValue());
                }

                if (isSelected) {
                    cell.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
                    cell.setForeground(UIConstants.INTEL_WHITE);
                    cell.setBackground(UIConstants.INTEL_MEDIUM_BLUE);
                } else {
                    cell.setFont(UIConstants.H5_FONT);
                    cell.setForeground(UIConstants.INTEL_DARK_GRAY);
                    if ((row % 2) == 0) {
                        cell.setBackground(UIConstants.INTEL_WHITE);
                    } else {
                        cell.setBackground(UIConstants.INTEL_TABLE_ROW_GRAY);
                    }
                }

                String cellText = ((JLabel) cell).getText();
                if (column == EventRulesTableColumns.EVENT_CLASS.getId()) {
                    ((JLabel) cell).setToolTipText(cellText);
                } else if (column == EventRulesTableColumns.EVENT_TYPE
                        .getId()) {
                    ((JLabel) cell).setToolTipText(
                            EventTypeViz.getEventTypeDescription(cellText));
                } else {
                    ((JLabel) cell).setToolTipText(null);
                }

                return cell;
            }

        };
        ActionListRenderer actionRenderer = new ActionListRenderer();
        mTable.putClientProperty("terminateEditOnFocusLost", Boolean.FALSE);
        for (int i = 0; i < mTable.getColumnCount(); i++) {
            mTable.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
            TableColumnExt col = mTable.getColumnExt(i);
            col.setComparator(numberComparator);
            if (i == EventRulesTableColumns.EVENT_ACTION.getId()) {
                mTable.getColumnModel().getColumn(i)
                        .setCellRenderer(actionRenderer);
            } else {
                mTable.setDefaultRenderer(Object.class, tableRenderer);
            }
        } // for

        mTable.packTable(2);
    }

    public void setWizardListener(IWizardTask listener) {
        eventWizardControlListener = listener;
        pnlEventAction.setEventWizardControlListener(listener);
    }

    public boolean isDirty() {
        return dirty;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    class ActionListRenderer extends JPanel implements TableCellRenderer {
        private static final long serialVersionUID = 1294872510853521883L;

        private final JPanel actionPanel;

        private Rectangle desiredBound;

        public ActionListRenderer() {
            super(new BorderLayout());
            setOpaque(true);
            setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

            actionPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 3, 0));
            actionPanel.setOpaque(false);
            add(actionPanel, BorderLayout.WEST);

            JLabel label = new JLabel(STLConstants.K3004_SELECT.getValue());
            label.setFont(UIConstants.H6_FONT);
            label.setForeground(UIConstants.INTEL_BLUE);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                    UIConstants.INTEL_BLUE));
            label.setToolTipText(STLConstants.K3000_SELECT_ACTIONS.getValue());
            add(label, BorderLayout.EAST);
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.table.TableCellRenderer#getTableCellRendererComponent
         * (javax.swing.JTable, java.lang.Object, boolean, boolean, int, int)
         */
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column) {
            EventRule tableEntry = model.getEntry(row);
            actionPanel.removeAll();
            for (EventRuleAction action : tableEntry.getEventActions()) {
                EventRuleActionViz actionViz =
                        EventRuleActionViz.getEventRuleActionVizFor(action);
                if (actionViz != null) {
                    JLabel label = new JLabel(actionViz.getImageIcon());
                    label.setToolTipText(actionViz.getName());
                    actionPanel.add(label);
                }
            }

            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }

            desiredBound = table.getCellRect(row, column, false);
            return this;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.JComponent#getToolTipText(java.awt.event.MouseEvent)
         */
        @Override
        public String getToolTipText(MouseEvent event) {
            setBounds(desiredBound);
            validate();
            doLayout();
            JComponent comp = (JComponent) getComponentAt(event.getPoint());
            if (comp != null) {
                if (comp instanceof JLabel) {
                    return comp.getToolTipText();
                } else {
                    Point p = SwingUtilities.convertPoint(this, event.getX(),
                            event.getY(), actionPanel);
                    actionPanel.validate();
                    actionPanel.doLayout();
                    comp = (JComponent) actionPanel.getComponentAt(p);
                    if (comp != null) {
                        return comp.getToolTipText();
                    }
                }
            }
            return super.getToolTipText(event);
        }

    }
}
