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

package com.intel.stl.ui.common;

import com.intel.stl.api.notice.EventDescription;
import com.intel.stl.api.notice.IEventSource;

/**
 * Supporting model for the event table
 */
public class EventTableModel extends FVTableModel<EventDescription> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -5075022331424320175L;

    /**
     * Column numbers for setValueAt() and getValueAt() methods
     */
    public static final int TIME_IDX = 0;

    public static final int SEVERITY_IDX = 1;

    public static final int SOURCE_IDX = 2;

    public static final int DESCRIPTION_IDX = 3;

    /**
     * 
     * Description: Constructor for the EventTableModel class
     * 
     * @param N
     *            /a
     * 
     * @return EventTableModel
     * 
     */
    public EventTableModel() {

        String[] columnNames =
                new String[] { STLConstants.K0401_TIME.getValue(),
                        STLConstants.K0402_SEVERITY.getValue(),
                        STLConstants.K0403_SOURCE.getValue(),
                        STLConstants.K0405_DESCRIPTION.getValue() };

        setColumnNames(columnNames);
    } // EventTableModel

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
    public Object getValueAt(int pRow, int pCol) {

        Object value = null;

        EventDescription eventMsg = null;
        synchronized (critical) {
            eventMsg = this.mEntryList.get(pRow);
        }
        if (eventMsg == null) {
            return null;
        }

        switch (pCol) {

            case TIME_IDX:
                value = eventMsg.getDate();
                break;

            case SEVERITY_IDX:
                value = eventMsg.getSeverity();
                break;

            case SOURCE_IDX:
                IEventSource source = eventMsg.getSource();
                value =
                        source == null ? STLConstants.K0383_NA.getValue()
                                : source.getDescription();
                break;

            case DESCRIPTION_IDX:
                value = eventMsg.getType();
                break;

            default:
                // NOP
                break;
        } // switch

        return value;
    } // getValueAt

    /**
     * Description:
     * 
     */
    public void clear() {
        synchronized (critical) {
            mEntryList.clear();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.FVTableModel#isCellEditable(int, int)
     */
    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }

} // class EventTableModel
