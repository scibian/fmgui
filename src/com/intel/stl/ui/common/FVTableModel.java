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

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

public abstract class FVTableModel<E> extends AbstractTableModel {
    protected static final long serialVersionUID = 7593641813548886227L;

    public static final int DEFAULT_CAPABILITY = 1024;

    /**
     * Table header column names
     */
    protected String[] mColumnNames;

    /**
     * List of entries
     */
    protected List<E> mEntryList = new ArrayList<E>();

    protected int mCapability = DEFAULT_CAPABILITY;

    /**
     * Table model should be updated in EDT. The following synchronization code
     * is just for in case we accidentally do update from other threads. Maybe
     * we should remove these syn code to force us update a table model in EDT
     * assuming that we can easily figure out we are updating from wrong thread.
     */
    protected Object critical = new Object();

    /**
     * @return the mCapability
     */
    public int getCapability() {
        return mCapability;
    }

    /**
     * @param mCapability
     *            the mCapability to set
     */
    public void setCapability(int capability) {
        this.mCapability = capability;
    }

    /**
     * Description: Override getColumnName to set the column headings.
     * 
     * @param column
     *            - integer indicating the column number
     * 
     * @return result - name of the column heading
     * 
     */
    @Override
    public String getColumnName(int column) {
        if (mColumnNames == null) {
            return "";
        } else {
            return mColumnNames[column];
        }
    }

    /**
     * 
     * Description: Initializes the local column names
     * 
     * @param pColumnNames
     *            - string array of column names
     */
    protected void setColumnNames(String[] pColumnNames) {
        mColumnNames = pColumnNames;
    } // setColumnNames

    /**
     * 
     * Description: Add an entry to the list, appending a new row to the table
     * 
     * @param pEntry
     *            - incoming entry to set in the list
     * 
     * @return N/a
     */
    public boolean addEntry(E pEntry) {
        boolean result = false;

        synchronized (critical) {
            while (mEntryList.size() > mCapability) {
                mEntryList.remove(0);
            }
            result = mEntryList.add(pEntry);
        }

        return result;
    } // addEntry

    public void setEntries(List<E> entryList) {
        synchronized (critical) {
            this.mEntryList = entryList;
        }
    }

    /**
     * Description: Remove an event message from the list, deleting the
     * specified row in the table
     * 
     * @param pRow
     *            - row number
     * 
     * @return N/a
     * 
     */
    public void removeEntry(E pEntry) {
        synchronized (critical) {
            mEntryList.remove(pEntry);
        }
    } // removeEntry

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getRowCount()
     */
    @Override
    public int getRowCount() {
        synchronized (critical) {
            return mEntryList.size();
        }
    } // getRowCount

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.table.TableModel#getColumnCount()
     */
    @Override
    public int getColumnCount() {
        return (mColumnNames == null) ? 0 : mColumnNames.length;

    }

    public E getEntry(int row) {
        return mEntryList.get(row);
    }

    @Override
    public abstract boolean isCellEditable(int rowIndex, int columnIndex);
} // abstract class FVTableModel
