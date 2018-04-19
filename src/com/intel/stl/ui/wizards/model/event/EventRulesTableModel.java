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

package com.intel.stl.ui.wizards.model.event;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.ui.common.FVTableModel;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.EventClassViz;
import com.intel.stl.ui.model.EventTypeViz;
import com.intel.stl.ui.wizards.impl.event.EventRulesTableColumns;
import com.intel.stl.ui.wizards.view.event.ITableListener;

/**
 * Table model for the Event Wizard
 */
public class EventRulesTableModel extends FVTableModel<EventRule> implements
        ITableListener {

    private static final long serialVersionUID = 5546605189508267737L;

    private int selectedRow;

    public EventRulesTableModel() {

        String[] columnNames =
                new String[EventRulesTableColumns.values().length];
        for (int i = 0; i < EventRulesTableColumns.values().length; i++) {
            columnNames[i] = EventRulesTableColumns.values()[i].getTitle();
        }
        setColumnNames(columnNames);
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        if ((columnIndex == 2) || (columnIndex == 3)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Description: Override getColumnName to set the column headings
     * 
     * @param column
     *            - integer indicating the column number
     * 
     * @return result - name of the column heading
     * 
     */
    @Override
    public String getColumnName(int column) {
        return mColumnNames[column];
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getValueAt(int, int)
     */
    @Override
    public Object getValueAt(int row, int col) {
        Object value = null;

        EventRule eventRule = null;
        synchronized (critical) {
            eventRule = this.mEntryList.get(row);
        }

        switch (EventRulesTableColumns.values()[col]) {
            case EVENT_CLASS:
                value =
                        EventClassViz.getEventTypeClassFor(eventRule
                                .getEventType().getEventClass());
                break;

            case EVENT_TYPE:
                value =
                        EventTypeViz.getEventTypeVizFor(eventRule
                                .getEventType());
                break;

            case EVENT_SEVERITY:
                value = eventRule.getEventSeverity().name();
                break;

            case EVENT_ACTION:
                if (eventRule.getEventActions().size() <= 0) {
                    value = STLConstants.K0799_NO_ACTIONS.getValue();
                } else {
                    value = STLConstants.K0675_ACTION.getValue();
                }

                break;

            default:
                // NOP
                break;
        }

        return value;

    }

    /**
     * Description:
     * 
     */
    public void clear() {
        mEntryList.clear();
    }

    public int getEntrySize() {
        return mEntryList.size();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.event.IComboListener#updateTable(java.util
     * .List)
     */
    @Override
    public void updateTable(final List<EventRule> rules) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                setEntries(rules);
                fireTableDataChanged();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.event.ITableListener#getEventRules()
     */
    @Override
    public List<EventRule> getEventRules() {

        List<EventRule> eventRules = new ArrayList<EventRule>();

        for (int row = 0; row < getEntrySize(); row++) {
            eventRules.add(getEntry(row));
        }

        return eventRules;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.event.ITableListener#getSelectedRow()
     */
    @Override
    public int getSelectedRow() {

        return selectedRow;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.event.ITableListener#setSelectedRow(int)
     */
    @Override
    public void setSelectedRow(int row) {

        selectedRow = row;
    }
}
