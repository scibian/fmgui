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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.util.Collections;
import java.util.List;

import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.SortOrder;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.EventTableModel;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.EventTypeViz;
import com.intel.stl.ui.model.NoticeSeverityViz;

/**
 * UI view for the event table; extends abstract class FVTableView
 */
public class EventTableView extends FVTableView {

    /**
     * Description: Constructor for the EventTableView class
     * 
     * @param pController
     *            - event table card
     */
    public EventTableView(EventTableModel pModel) {
        super();
        initComponents();
        setModel(pModel);
        formatTable();
    }

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3269064546124145702L;

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.tables.FVTableView#formatTable()
     */
    @Override
    public void formatTable() {

        FVTableRenderer tableRenderer = new FVTableRenderer() {
            private static final long serialVersionUID = -5458310374933530865L;

            /*
             * (non-Javadoc)
             * 
             * @see com.intel.stl.ui.common.view.FVTableRenderer#
             * getTableCellRendererComponent(javax.swing.JTable,
             * java.lang.Object, boolean, boolean, int, int)
             */
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int column) {
                JLabel cell =
                        (JLabel) super.getTableCellRendererComponent(table,
                                value, isSelected, hasFocus, row, column);

                if (column == EventTableModel.SEVERITY_IDX && value != null) {
                    NoticeSeverityViz nsv =
                            NoticeSeverityViz
                                    .getNoticeSeverityVizFor((NoticeSeverity) value);
                    if (nsv != null) {
                        cell.setText(nsv.getName());
                        cell.setForeground(nsv.getColor());
                    }
                    cell.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
                    // cell.setIcon(nsv.getIcon().getImageIcon());
                } else {
                    if (column == EventTableModel.DESCRIPTION_IDX
                            && value != null) {
                        EventTypeViz ntv =
                                EventTypeViz
                                        .getEventTypeVizFor((EventType) value);
                        if (ntv != null) {
                            cell.setText(ntv.getName());
                        }
                    } else if (column == EventTableModel.TIME_IDX
                            && value != null) {
                        String timeStr = Util.getYYYYMMDDHHMMSS().format(value);
                        cell.setText(timeStr);
                    }
                    cell.setForeground(UIConstants.INTEL_DARK_GRAY);
                    cell.setIcon(null);
                }
                return cell;
            }

        };

        // Format the table headers
        FVHeaderRenderer headerRenderer = new FVHeaderRenderer(mTable);
        mHeaderCol = mTable.getTableHeader();
        mHeaderCol.setFont(UIConstants.H3_FONT.deriveFont(Font.BOLD));

        for (int i = 0; i < mTable.getColumnCount(); i++) {
            mTable.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
            mTblCol = mTable.getColumnModel().getColumn(i);
            mTblCol.setCellRenderer(tableRenderer);
        } // for

        TableRowSorter<TableModel> sorter =
                new TableRowSorter<TableModel>(mTable.getModel());
        mTable.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys =
                Collections.singletonList(new RowSorter.SortKey(
                        EventTableModel.TIME_IDX, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
    }

    /**
     * 
     * Description: Initializes the components for this view
     * 
     */
    public void initComponents() {

        // Configure the table
        mTable = new JTable();
        mTable.setFillsViewportHeight(true);
        mTable.setPreferredScrollableViewportSize(getMaximumSize());
        mTable.setAutoCreateColumnsFromModel(true);
        mTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        mTable.setAlignmentX(JTable.LEFT_ALIGNMENT);
        mTable.setBackground(UIConstants.INTEL_WHITE);
        mTable.setIntercellSpacing(new Dimension(2, 3));
        mTable.getTableHeader().setReorderingAllowed(false);

        // Add the table to the scroll pane and configure
        ScrollPaneLayout spLayout = new ScrollPaneLayout();
        spLayout.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
        spLayout.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        mScrollPane = new JScrollPane(mTable);
        mScrollPane.setPreferredSize(new Dimension(MAIN_SCROLL_PANE_WIDTH,
                MAIN_SCROLL_PANE_HEIGHT));
        mScrollPane.createHorizontalScrollBar();
        mScrollPane.createVerticalScrollBar();
        mScrollPane.setLayout(spLayout);

        // Configure the scroll pane layout and constraints
        GridBagLayout gbLayout = new GridBagLayout();
        gbLayout.columnWeights = new double[] { 1.0, Double.MIN_VALUE };
        setLayout(new GridBagLayout());

        GridBagConstraints gbcMainPanel = new GridBagConstraints();
        gbcMainPanel.fill = GridBagConstraints.BOTH;
        gbcMainPanel.weightx = 1;
        gbcMainPanel.weighty = 1;
        gbcMainPanel.gridwidth = GridBagConstraints.REMAINDER;

        // Add the scroll pane to this panel
        add(mScrollPane, gbcMainPanel);
    } // initComponents

}
