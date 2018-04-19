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

import java.awt.Cursor;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.entity.CategoryLabelEntity;
import org.jfree.chart.entity.ChartEntity;
import org.jfree.chart.plot.CategoryPlot;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.JumpPopupUtil.IActionCreator;
import com.intel.stl.ui.event.JumpDestination;
import com.intel.stl.ui.model.FocusFlagViz;
import com.intel.stl.ui.model.PortEntry;

public class JumpChartPanel extends ChartPanel implements ChartMouseListener {
    private static final long serialVersionUID = -5651038769029599628L;

    private JMenu jumpToMenu;

    private CategoryLabelEntity jumpToEntity;

    private CategoryAxis categoryAxis;

    private CategoryLabelEntity highlightedEntity;

    private final List<IJumpListener> listeners =
            new CopyOnWriteArrayList<IJumpListener>();

    private boolean jumpable;

    /**
     * Description:
     *
     * @param chart
     */
    public JumpChartPanel(JFreeChart chart) {
        super(chart);
    }

    public void setChart(JFreeChart chart, boolean jumpable) {
        this.jumpable = jumpable;
        if (jumpable) {
            addChartMouseListener(this);
        } else {
            removeChartMouseListener(this);
        }
        super.setChart(chart);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jfree.chart.ChartPanel#createPopupMenu(boolean, boolean,
     * boolean, boolean, boolean)
     */
    @Override
    protected JPopupMenu createPopupMenu(boolean properties, boolean copy,
            boolean save, boolean print, boolean zoom) {
        JPopupMenu popup =
                super.createPopupMenu(properties, copy, save, print, zoom);
        jumpToMenu = addJumpMenu(popup);
        return popup;
    }

    protected JMenu addJumpMenu(JPopupMenu popup) {
        return JumpPopupUtil.appendPopupMenu(popup, true, new IActionCreator() {

            @Override
            public Action createAction(final JumpDestination destination) {
                return new AbstractAction(destination.getName()) {
                    private static final long serialVersionUID =
                            -2231031530367349855L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (jumpToEntity != null) {
                            fireJumpEvent(jumpToEntity.getKey(), destination);
                        }
                    }

                };
            }

        });
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jfree.chart.ChartPanel#displayPopupMenu(int, int)
     */
    @Override
    protected void displayPopupMenu(int x, int y) {
        if (getPopupMenu() != null) {
            ChartEntity entity = getEntityForPoint(x, y);
            if (entity != null && entity instanceof CategoryLabelEntity) {
                jumpToEntity = (CategoryLabelEntity) entity;
            } else {
                jumpToEntity = null;
            }
            if (jumpToMenu != null) {
                jumpToMenu.setEnabled(jumpable && jumpToEntity != null);
            }
        }
        super.displayPopupMenu(x, y);
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jfree.chart.ChartPanel#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        super.mouseExited(e);
        if (highlightedEntity != null) {
            highlightEntity(highlightedEntity, false);
            highlightedEntity = null;
        }
    }

    @Override
    public void chartMouseClicked(ChartMouseEvent cme) {
        if (!SwingUtilities.isLeftMouseButton(cme.getTrigger())) {
            return;
        }

        ChartEntity xyItem = cme.getEntity();
        if (xyItem instanceof CategoryLabelEntity) {
            jumpToEntity = (CategoryLabelEntity) xyItem;
            fireJumpEvent(jumpToEntity.getKey(), JumpDestination.DEFAULT);
        }
    }

    @Override
    public void chartMouseMoved(ChartMouseEvent cme) {
        JPopupMenu popup = getPopupMenu();
        if (popup != null && popup.isShowing()) {
            return;
        }

        ChartEntity xyItem = cme.getEntity();
        if (xyItem instanceof CategoryLabelEntity) {
            CategoryLabelEntity newCatEntity = (CategoryLabelEntity) xyItem;

            if (highlightedEntity != null && newCatEntity.getKey()
                    .equals(highlightedEntity.getKey())) {
                return;
            }

            if (highlightedEntity != null) {
                highlightEntity(highlightedEntity, false);
            }

            highlightedEntity = (CategoryLabelEntity) xyItem;
            if (categoryAxis == null) {
                CategoryPlot plot = getChart().getCategoryPlot();
                categoryAxis = plot.getDomainAxis();
            }
            highlightEntity(highlightedEntity, true);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        } else {
            if (highlightedEntity != null) {
                highlightEntity(highlightedEntity, false);
                highlightedEntity = null;
            }
            setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
        }
    }

    protected void highlightEntity(CategoryLabelEntity entity,
            boolean highlight) {
        if (categoryAxis == null) {
            return;
        }

        if (highlight) {
            categoryAxis.setTickLabelFont(entity.getKey(),
                    UIConstants.H5_FONT.deriveFont(Font.BOLD));
        } else {
            categoryAxis.setTickLabelFont(entity.getKey(),
                    categoryAxis.getTickLabelFont());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see org.jfree.chart.ChartPanel#getToolTipText(java.awt.event.MouseEvent)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public String getToolTipText(MouseEvent e) {
        ChartEntity entity = getEntityForPoint(e.getX(), e.getY());
        if (entity != null) {
            if (entity instanceof CategoryLabelEntity) {
                CategoryLabelEntity labelEntity = (CategoryLabelEntity) entity;
                Comparable key = labelEntity.getKey();
                if (key instanceof PortEntry) {
                    FocusFlagViz flag =
                            (FocusFlagViz) ((PortEntry) key).getObject();
                    if (flag != null && flag != FocusFlagViz.OK) {
                        return "<html>" + key.toString() + "<br>"
                                + flag.getValue() + "</html>";
                    }
                }
                return key.toString();
            }
            return entity.getToolTipText();
        }
        return null;
    }

    public void addListener(IJumpListener listener) {
        listeners.add(listener);
    }

    public void removeListener(IJumpListener listener) {
        listeners.remove(listener);
    }

    protected void fireJumpEvent(Object content, JumpDestination destination) {
        for (IJumpListener listener : listeners) {
            listener.jumpTo(content, destination);
        }
    }

}
