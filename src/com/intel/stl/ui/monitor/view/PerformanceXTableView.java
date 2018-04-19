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

package com.intel.stl.ui.monitor.view;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Comparator;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.table.TableColumnExt;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.FVHeaderRenderer;
import com.intel.stl.ui.common.view.FVTableRenderer;
import com.intel.stl.ui.common.view.FVXTableView;
import com.intel.stl.ui.common.view.JumpPopupUtil;
import com.intel.stl.ui.common.view.JumpPopupUtil.IActionCreator;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.model.LinkQualityViz;
import com.intel.stl.ui.model.PerformanceTableColumns;
import com.intel.stl.ui.model.PerformanceTableModel;
import com.intel.stl.ui.monitor.IPortSelectionListener;
import com.intel.stl.ui.monitor.PerformanceTableData;
import com.intel.stl.ui.monitor.TableDataDescription;

/**
 * UI view for the performance table; extends abstract class FVTableView
 */
public class PerformanceXTableView extends FVXTableView<PerformanceTableModel> {
    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 3269064546124145702L;

    protected IPortSelectionListener listener;

    /**
     * Description: Constructor for the EventTableView class
     *
     * @param pController
     *            event table card
     */
    public PerformanceXTableView(PerformanceTableModel model) {
        super(model);
        installPopupMenu();
        installListeners();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.tables.FVTableView#formatTable()
     */
    @Override
    public void formatTable() {
        FVTableRenderer tableRenderer = new FVTableRenderer();

        // Format the table headers
        FVHeaderRenderer headerRenderer = new FVHeaderRenderer(mTable);
        mHeader = createTableHeader(mTable.getColumnModel());
        mTable.setTableHeader(mHeader);
        mHeader.setFont(UIConstants.H3_FONT);

        Comparator<Number> numberComparator = new Comparator<Number>() {
            @Override
            public int compare(Number o1, Number o2) {
                return Double.compare(o1.doubleValue(), o2.doubleValue());
            }
        };
        Comparator<TableDataDescription> tableDataComparator =
                new Comparator<TableDataDescription>() {
                    @Override
                    public int compare(TableDataDescription o1,
                            TableDataDescription o2) {
                        return Double.compare(o1.getData(), o2.getData());
                    }
                };
        for (int i = 0; i < mTable.getColumnCount(); i++) {
            mTable.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
            TableColumnExt col = mTable.getColumnExt(i);

            if (PerformanceTableColumns
                    .values()[i] == PerformanceTableColumns.RX_DATA
                    || PerformanceTableColumns
                            .values()[i] == PerformanceTableColumns.RX_DATA_RATE
                    || PerformanceTableColumns
                            .values()[i] == PerformanceTableColumns.TX_DATA
                    || PerformanceTableColumns
                            .values()[i] == PerformanceTableColumns.TX_DATA_RATE) {
                col.setComparator(tableDataComparator);
                col.setCellRenderer(new FVTableRenderer() {
                    private static final long serialVersionUID =
                            -3747347169291822762L;

                    @Override
                    public Component getTableCellRendererComponent(JTable table,
                            Object value, boolean isSelected, boolean hasFocus,
                            int row, int column) {
                        JLabel renderer =
                                (JLabel) super.getTableCellRendererComponent(
                                        table, value, isSelected, hasFocus, row,
                                        column);

                        TableDataDescription data =
                                (TableDataDescription) value;
                        if (data != null) {
                            renderer.setText(data.getFormattedData());
                            renderer.setToolTipText(data.getDescription());
                        }
                        setHorizontalAlignment(JLabel.LEFT);
                        return this;
                    }
                });

            } else if (PerformanceTableColumns
                    .values()[i] != PerformanceTableColumns.LINK_QUALITY) {
                col.setComparator(numberComparator);
                // For all but LINK_QUALITY column use tableRenderer
                col.setCellRenderer(tableRenderer);
            } else {
                col.setComparator(numberComparator);
                /**
                 * For LINK_QUALITY column use special renderer to display
                 * center-aligned icon corresponding to the link quality,
                 * tooltip for the icon hide text - numeric representation of
                 * link quality.
                 */
                col.setCellRenderer(new FVTableRenderer() {
                    private static final long serialVersionUID =
                            -3747347169291822762L;

                    @Override
                    public Component getTableCellRendererComponent(JTable table,
                            Object value, boolean isSelected, boolean hasFocus,
                            int row, int column) {

                        Component renderer =
                                super.getTableCellRendererComponent(table,
                                        value, isSelected, hasFocus, row,
                                        column);

                        renderer.setForeground(renderer.getBackground());

                        setIcon(LinkQualityViz.getLinkQualityIcon(
                                ((Integer) value).byteValue()));

                        setHorizontalAlignment(JLabel.CENTER);

                        setToolTipText(LinkQualityViz.getLinkQualityDescription(
                                ((Integer) value).byteValue()));

                        return this;
                    }

                });
            }
        } // for

        // mTable.setDefaultRenderer(Object.class, tableRenderer);

        PerformanceTableColumns[] toHide = new PerformanceTableColumns[] {
                PerformanceTableColumns.EXCESSIVE_BUFFER_OVERRUNS,
                PerformanceTableColumns.FM_CONFIG_ERRORS,
                PerformanceTableColumns.RX_PACKETS,
                PerformanceTableColumns.RX_DATA,
                PerformanceTableColumns.RX_REMOTE_PHY_ERRORS,
                PerformanceTableColumns.RX_SWITCH_ERRORS,
                PerformanceTableColumns.TX_DISCARDS,
                PerformanceTableColumns.TX_PACKETS,
                PerformanceTableColumns.TX_DATA,
                PerformanceTableColumns.RX_MC_PACKETS,
                PerformanceTableColumns.RX_ERRORS,
                PerformanceTableColumns.RX_CONSTRAINT,
                PerformanceTableColumns.RX_FECN,
                PerformanceTableColumns.RX_BECN,
                PerformanceTableColumns.RX_BUBBLE,
                PerformanceTableColumns.TX_MC_PACKETS,
                PerformanceTableColumns.TX_CONSTRAINT,
                PerformanceTableColumns.TX_WAIT,
                PerformanceTableColumns.TX_TIME_CONG,
                PerformanceTableColumns.TX_WASTED_BW,
                PerformanceTableColumns.TX_WAIT_DATA,
                PerformanceTableColumns.LOCAL_LINK_INTEGRITY,
                PerformanceTableColumns.MARK_FECN,
                PerformanceTableColumns.LINK_ERROR_RECOVERIES,
                PerformanceTableColumns.LINK_DOWNED,
                PerformanceTableColumns.NUM_LANES_DOWN,
                PerformanceTableColumns.UNCORRECTABLE_ERRORS,
                PerformanceTableColumns.SW_PORT_CONGESTION

                // PerformanceTableColumns.LINK_QUALITY,
                // PerformanceTableColumns.RX_DELTA_PACKETS,
                // PerformanceTableColumns.RX_DELTA_DATA,
                // PerformanceTableColumns.RX_PACKETS,
                // PerformanceTableColumns.RX_DATA,

                // PerformanceTableColumns.TX_DELTA_PACKETS,
                // PerformanceTableColumns.TX_DELTA_DATA,
                // PerformanceTableColumns.TX_PACKETS,
                // PerformanceTableColumns.TX_DATA
        };
        for (PerformanceTableColumns col : toHide) {
            mTable.getColumnExt(col.getTitle()).setVisible(false);
        }

        mTable.packTable(2);
    }

    protected JXTableHeader createTableHeader(TableColumnModel columnModel) {

        JXTableHeader header = new JXTableHeader(columnModel) {

            private static final long serialVersionUID = 1552295147223847158L;

            @Override
            public String getToolTipText(MouseEvent e) {

                Point point = e.getPoint();
                int column = columnModel.getColumnIndexAtX(point.x);
                int modelIndex = table.convertColumnIndexToModel(column);
                return PerformanceTableColumns.values()[modelIndex]
                        .getToolTip();
            }
        };

        return header;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.view.FVXTableView#createTable(javax.swing.table
     * .TableModel)
     */
    @Override
    protected JXTable createTable(final PerformanceTableModel model) {
        // Configure the table
        JXTable table = new JXTable(model);
        table.setColumnControlVisible(true);
        table.setHorizontalScrollEnabled(true);
        table.setAutoscrolls(true);
        table.setFillsViewportHeight(true);
        table.setPreferredScrollableViewportSize(getMaximumSize());
        table.setAutoCreateColumnsFromModel(true);
        table.setAlignmentX(JTable.LEFT_ALIGNMENT);
        table.setBackground(UIConstants.INTEL_WHITE);
        table.setVisibleRowCount(15);
        table.setHighlighters(HighlighterFactory.createAlternateStriping(
                UIConstants.INTEL_WHITE, UIConstants.INTEL_TABLE_ROW_GRAY));
        table.getTableHeader().setReorderingAllowed(false);

        return table;
    }

    protected void installPopupMenu() {
        final JPopupMenu popupMenu = new JPopupMenu();
        JumpPopupUtil.appendPopupMenu(popupMenu, false, new IActionCreator() {

            @Override
            public Action createAction(final JumpDestination destination) {
                return new AbstractAction(destination.getName()) {
                    private static final long serialVersionUID =
                            -2783822215801105313L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (listener == null) {
                            return;
                        }

                        int vRow = mTable.getSelectedRow();
                        if (vRow >= 0) {
                            int mRow = mTable.convertRowIndexToModel(vRow);
                            PerformanceTableData perfData =
                                    model.getEntry(mRow);
                            listener.onJumpToPort(perfData.getNodeLid(),
                                    perfData.getPortNumber(),
                                    destination.getName());
                        }
                    }

                };
            }

        });
        mTable.setComponentPopupMenu(popupMenu);
    }

    protected void installListeners() {
        ListSelectionListener selectionListener = new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (listener == null || e.getValueIsAdjusting()) {
                    return;
                }

                int vRow = mTable.getSelectedRow();
                if (vRow >= 0) {
                    int mRow = mTable.convertRowIndexToModel(vRow);
                    listener.onPortSelection(mRow);
                }
            }
        };
        mTable.getSelectionModel().addListSelectionListener(selectionListener);

        MouseListener mouseListener = new MouseAdapter() {

            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.MouseAdapter#mouseClicked(java.awt.event.
             * MouseEvent )
             */
            @Override
            public void mouseClicked(MouseEvent e) {
                if (listener == null) {
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)
                        && e.getClickCount() > 1) {
                    Point p = e.getPoint();
                    int vRow = mTable.rowAtPoint(p);
                    if (vRow >= 0) {
                        int mRow = mTable.convertRowIndexToModel(vRow);
                        PerformanceTableData perfData = model.getEntry(mRow);
                        // we usually use default destination
                        // JumpDestination.DEFAULT
                        // for double click jumping. Since we are on port
                        // performance summary table, we use
                        // JumpDestination.PERFORMANCE here no matter what
                        // default destination is.
                        listener.onJumpToPort(perfData.getNodeLid(),
                                perfData.getPortNumber(),
                                JumpDestination.PERFORMANCE.getName());
                    }
                }
            }

        };
        mTable.addMouseListener(mouseListener);
    }

    public void setPortSelectionListener(IPortSelectionListener listener) {
        this.listener = listener;
    }

    public void setSelectedPort(final int portIndex) {
        int vId = mTable.convertRowIndexToView(portIndex);
        mTable.setRowSelectionInterval(vId, vId);
        mTable.scrollRowToVisible(vId);
    }

}
