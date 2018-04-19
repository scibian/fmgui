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
import java.awt.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListModel;
import javax.swing.UIManager;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.jdesktop.swingx.JXList;

/**
 * Extended JComboBox that support disabled items
 */
public class ExJXList<E> extends JXList {
    private static final long serialVersionUID = 7774901357006599064L;

    private final List<E> disabledItems = new ArrayList<E>();

    private final Set<Integer> disabledItemIds = new HashSet<Integer>();

    private Color disabledColor;

    private ListModel<?> dataModel;

    /**
     * Description:
     * 
     * @param items
     */
    public ExJXList(E[] items) {
        super(items);
        super.setCellRenderer(new DisabledItemsRenderer());
        dataModel = super.getModel();
        dataModel.addListDataListener(new ExListDataListener());
    }

    /**
     * @param disabledColor
     *            the disabledColor to set
     */
    public void setDisabledColor(Color disabledColor) {
        this.disabledColor = disabledColor;
    }

    public void remove(int anIndex) {
        @SuppressWarnings("unchecked")
        E toRemove = (E) dataModel.getElementAt(anIndex);
        disabledItems.remove(toRemove);
        chacheDisabledIndices();
        adjustSelection();
    }

    @SuppressWarnings("unchecked")
    public void setDisabledItem(E... items) {
        disabledItems.clear();
        disabledItemIds.clear();
        if (items == null || items.length == 0) {
            return;
        }

        disabledItems.addAll(Arrays.asList(items));
        chacheDisabledIndices();
        adjustSelection();
    }

    protected void chacheDisabledIndices() {
        disabledItemIds.clear();
        for (int i = 0; i < dataModel.getSize(); i++) {
            @SuppressWarnings("unchecked")
            E element = (E) dataModel.getElementAt(i);
            for (E item : disabledItems) {
                if (element != null && element.equals(item)) {
                    disabledItemIds.add(i);
                }
            }
        }
    }

    protected void adjustSelection() {
        Object selected = super.getSelectedValue();
        if (selected == null) {
            return;
        }

        for (E item : disabledItems) {
            if (selected.equals(item)) {
                boolean foundDisabledItem = false;
                for (int i = 0; i < dataModel.getSize(); i++) {
                    @SuppressWarnings("unchecked")
                    E element = (E) dataModel.getElementAt(i);
                    if (!foundDisabledItem) {
                        foundDisabledItem =
                                element != null && element.equals(item);
                    } else if (!disabledItemIds.contains(i)) {
                        super.setSelectedValue(element, true);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void setSelectedIndex(int index) {
        if (!disabledItemIds.contains(index)) {
            super.setSelectedIndex(index);
        }
    }

    /**
     * Description:
     * 
     * @param items
     */
    public ExJXList(Vector<E> items) {
        super(items);
        super.setCellRenderer(new DisabledItemsRenderer());
    }

    private class DisabledItemsRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -5395462996710992524L;

        @SuppressWarnings("rawtypes")
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel label =
                    (JLabel) super.getListCellRendererComponent(list, value,
                            index, isSelected, cellHasFocus);
            if (disabledItemIds.contains(index)) {
                if (disabledColor != null) {
                    label.setForeground(disabledColor);
                } else {
                    label.setForeground(UIManager
                            .getColor("Label.disabledForeground"));
                }

            }
            return label;
        }
    }

    private class ExListDataListener implements ListDataListener {

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.ListDataListener#intervalAdded(javax.swing.event
         * .ListDataEvent)
         */
        @Override
        public void intervalAdded(ListDataEvent e) {
            // TODO Auto-generated method stub

        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.ListDataListener#intervalRemoved(javax.swing.event
         * .ListDataEvent)
         */
        @Override
        public void intervalRemoved(ListDataEvent e) {
            // TODO Auto-generated method stub
            int low = e.getIndex0();
            int high = e.getIndex1();
            while (low <= high) {
                remove(low);
                low++;
            }
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.ListDataListener#contentsChanged(javax.swing.event
         * .ListDataEvent)
         */
        @Override
        public void contentsChanged(ListDataEvent e) {
            // TODO Auto-generated method stub

        }

    }
}
