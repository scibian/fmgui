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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.JXTableHeader;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.decorator.PatternPredicate;

import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.FVHeaderRenderer;
import com.intel.stl.ui.common.view.FVTableRenderer;
import com.intel.stl.ui.common.view.FVXTableView;
import com.intel.stl.ui.common.view.JumpPopupUtil;
import com.intel.stl.ui.common.view.JumpPopupUtil.IActionCreator;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.model.ConnectivityTableColumns;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.LinkQualityViz;
import com.intel.stl.ui.monitor.ConnectivityTableData;
import com.intel.stl.ui.monitor.IPortSelectionListener;

/**
 * View for the Connectivity subpage
 */
public class ConnectivitySubpageView extends
        FVXTableView<ConnectivityTableModel> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -2465696391555897980L;

    protected IPortSelectionListener listener;

    private CableInfoPopupView cableInfoPopupView;

    public ConnectivitySubpageView(ConnectivityTableModel model) {
        super(model);
        installPopupMenu();
        installListeners();
    }

    public CableInfoPopupView getCableInfoPopupView() {
        return cableInfoPopupView;
    }

    public void setCableInfoPopupView(CableInfoPopupView cableInfoPopupView) {
        this.cableInfoPopupView = cableInfoPopupView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.view.FVXTableView#createTable(javax.swing.table
     * .TableModel)
     */
    @Override
    protected JXTable createTable(final ConnectivityTableModel model) {
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

        // Highlight the "Neighbor" ports in blue
        HighlightPredicate predicate =
                new PatternPredicate(STLConstants.K0525_NEIGHBOR.getValue());
        ColorHighlighter highlighter =
                new ColorHighlighter(predicate,
                        UIConstants.INTEL_TABLE_ROW_GRAY,
                        UIConstants.INTEL_BLUE, null, null);
        table.setHighlighters(highlighter);

        // Make the background of the "Inactive" ports gray
        predicate =
                new PatternPredicate(STLConstants.K0524_INACTIVE.getValue());
        highlighter =
                new ColorHighlighter(predicate, UIConstants.INTEL_GRAY, null,
                        null, null);
        table.addHighlighter(highlighter);

        // Turn of the ability to sort columns
        table.setSortable(false);

        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JXTable table = (JXTable) e.getSource();

                // Unloaded tables should be ignored
                if (table.getRowCount() <= 0) {
                    return;
                }

                try {
                    int row =
                            table.convertRowIndexToModel(table.rowAtPoint(e
                                    .getPoint()));
                    int col =
                            table.convertColumnIndexToModel(table
                                    .columnAtPoint(e.getPoint()));

                    if (col == ConnectivityTableColumns.CABLE_INFO.getId()) {

                        cableInfoPopupView.onCableInfoSelection(table, row,
                                col, model, e);
                    }
                } catch (IndexOutOfBoundsException ie) {
                    // User click on table beyond number of rows - ignored
                }

            }
        });

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
                            ConnectivityTableData data = model.getEntry(mRow);
                            listener.onJumpToPort(data.getNodeLidValue(),
                                    data.getPortNumValue(),
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
                if (listener == null) {
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
             * @see
             * java.awt.event.MouseAdapter#mouseClicked(java.awt.event.MouseEvent
             * )
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
                        ConnectivityTableData data = model.getEntry(mRow);
                        listener.onJumpToPort(data.getNodeLidValue(),
                                data.getPortNumValue(),
                                JumpDestination.DEFAULT.getName());
                    }
                }
            }

        };
        mTable.addMouseListener(mouseListener);
    }

    public void setPortSelectionListener(IPortSelectionListener listener) {
        this.listener = listener;
    }

    public JComponent getMainComponent() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.view.FVTableView#formatTable()
     */
    @Override
    public void formatTable() {
        FVTableRenderer tableRenderer = new FVTableRenderer() {

            private static final long serialVersionUID = 4771573417795067808L;

            // TODO Need to customize the Neighbor rows in this table
            @Override
            public Component getTableCellRendererComponent(JTable table,
                    Object value, boolean isSelected, boolean hasFocus,
                    int row, int col) {

                Component cell =
                        super.getTableCellRendererComponent(table, value,
                                false, hasFocus, row, col);

                return renderCell(table, value, isSelected, hasFocus, row, col,
                        cell);
            }
        };

        // Format the table columns and headers
        FVHeaderRenderer headerRenderer = new FVHeaderRenderer(mTable);
        mHeader = createTableHeader(mTable.getColumnModel());
        mTable.setTableHeader(mHeader);
        mHeader.setFont(UIConstants.H3_FONT);

        for (int i = 0; i < mTable.getColumnCount(); i++) {
            mTable.getColumnModel().getColumn(i)
                    .setHeaderRenderer(headerRenderer);
            mTblCol = mTable.getColumnModel().getColumn(i);
            mTblCol.setCellRenderer(tableRenderer);
        } // for

        // Choose columns to hide
        filterColumns();

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
                return ConnectivityTableColumns.values()[modelIndex]
                        .getToolTip();
            }
        };

        return header;
    }

    protected void filterColumns() {
        ConnectivityTableColumns[] toShow =
                new ConnectivityTableColumns[] {

                        // Show these columns
                        ConnectivityTableColumns.NODE_NAME,
                        ConnectivityTableColumns.PORT_NUMBER,
                        ConnectivityTableColumns.CABLE_INFO,
                        ConnectivityTableColumns.LINK_STATE,
                        ConnectivityTableColumns.PHYSICAL_LINK_STATE,
                        ConnectivityTableColumns.LINK_QUALITY,
                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH,
                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH_DG_TX,
                        ConnectivityTableColumns.ACTIVE_LINK_WIDTH_DG_RX,
                        ConnectivityTableColumns.ACTIVE_LINK_SPEED,
                        ConnectivityTableColumns.RX_DATA,
                        ConnectivityTableColumns.TX_DATA,
                        ConnectivityTableColumns.LINK_DOWNED, };

        ConnectivityTableColumns[] all = ConnectivityTableColumns.values();
        boolean[] vis = new boolean[all.length];
        for (ConnectivityTableColumns col : toShow) {
            vis[col.getId()] = true;
        }
        for (int i = 0; i < vis.length; i++) {
            mTable.getColumnExt(all[i].getTitle()).setVisible(vis[i]);
        }
    }

    private Component renderLinkState(JTable table, int row, int col,
            Component cell, Object value) {
        JLabel label = (JLabel) cell;
        ConnectivityTableData tableEntry = model.getEntry(row);
        Icon icon = null;
        if (tableEntry.isSlowLinkState()) {
            icon = UIImages.SLOW_LINK.getImageIcon();
        } else {

            String str =
                    (String) table.getModel().getValueAt(row,
                            ConnectivityTableColumns.LINK_STATE.getId());
            if (str.equals(STLConstants.K0524_INACTIVE.getValue())) {
                icon = UIImages.INACTIVE_LINK.getImageIcon();
            } else {
                icon = UIImages.NORMAL_LINK.getImageIcon();
            }
        }

        if (label.getIcon() != null) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setBackground(label.getBackground());
            JLabel stateLabel = new JLabel(icon);
            panel.add(stateLabel, BorderLayout.WEST);
            panel.add(label, BorderLayout.CENTER);
            return panel;
        } else {
            label.setIcon(icon);
            return label;
        }
    }

    private Component renderCell(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int col,
            Component cell) {

        // Turn off the slow link icon in case it's on
        ((JLabel) cell).setIcon(null);

        // un-set the tooltip text and
        // re-Set alignment to default in case it has been set to CENTER
        // by the LinkQuality column. this is due to the fact we are
        // using same renderer for all the columns.
        ((JLabel) cell).setToolTipText(null);
        ((JLabel) cell).setHorizontalAlignment(JLabel.LEADING);

        Color lastForegroundColor = cell.getForeground();
        Color lastBackgroundColor = cell.getBackground();

        // Handle null values
        if (value == null) {
            ((JLabel) cell).setText(STLConstants.K0383_NA.getValue());
        }

        // Rows with "Inactive" types are bold
        NodeType nodeType =
                (NodeType) table.getModel().getValueAt(row,
                        ConnectivityTableColumns.NODE_TYPE.getId());
        String str = nodeType.name();
        if (str.equals(STLConstants.K0524_INACTIVE.getValue())) {
            cell.setFont(UIConstants.H6_FONT.deriveFont(Font.BOLD));
        }

        // Rows with "Neighbor" ports are italic
        str =
                (String) table.getModel().getValueAt(row,
                        ConnectivityTableColumns.PORT_NUMBER.getId());
        if (str.contains(STLConstants.K0525_NEIGHBOR.getValue())) {
            cell.setFont(UIConstants.H6_FONT.deriveFont(Font.ITALIC));
        }

        // Alternate rows white and gray
        if ((row % 2) == 0) {
            cell.setBackground(UIConstants.INTEL_WHITE);
            cell.setFont(UIConstants.H6_FONT.deriveFont(Font.BOLD));
        } else {
            cell.setBackground(UIConstants.INTEL_TABLE_ROW_GRAY);
        }

        // Use a blue highlight cursor for selected rows
        if (isSelected) {
            // cell.setFont(UIConstants.H4_FONT.deriveFont(Font.BOLD));
            lastForegroundColor = cell.getForeground();
            lastBackgroundColor = cell.getBackground();
            cell.setForeground(UIConstants.INTEL_WHITE);
            cell.setBackground(UIConstants.INTEL_MEDIUM_BLUE);
        } else {
            cell.setForeground(lastForegroundColor);
            cell.setBackground(lastBackgroundColor);
        }

        // Set the icon for the cable info column
        int cableInfoColumn = -1;
        try {
            cableInfoColumn =
                    table.getColumnModel().getColumnIndex(
                            ConnectivityTableColumns.CABLE_INFO.getTitle());
        } catch (IllegalArgumentException e) {
        }
        if (cableInfoColumn == col) {
            if (value != null) {
                ((JLabel) cell).setIcon(UIImages.CABLE.getImageIcon());

                // Set the cable info tool tip
                ((JLabel) cell)
                        .setToolTipText(STLConstants.K3050_CABLE_INFO_TOOL_TIP
                                .getValue());
                ((JLabel) cell).setHorizontalAlignment(JLabel.CENTER);
            }
        }

        // Set icon for LINK_QUALITY column cells
        int qualityIndex = -1;
        try {
            qualityIndex =
                    table.getColumnModel().getColumnIndex(
                            ConnectivityTableColumns.LINK_QUALITY.getTitle());
        } catch (IllegalArgumentException e) {
        }

        if (qualityIndex == col) {
            if (value != null) {
                ((JLabel) cell).setIcon(LinkQualityViz
                        .getLinkQualityIcon(((Integer) value).byteValue()));

                // Use link quality description for tool tip
                ((JLabel) cell).setToolTipText(LinkQualityViz
                        .getLinkQualityDescription(((Integer) value)
                                .byteValue()));
            }
            // Don't show link quality number text
            ((JLabel) cell).setText("");
            cell.setForeground(cell.getBackground());
            ((JLabel) cell).setHorizontalAlignment(JLabel.CENTER);
        }

        if (col == 0) {
            // Set the indicator for slow links
            cell = renderLinkState(table, row, col, cell, value);
        }

        return cell;
    }
}
