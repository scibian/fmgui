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

package com.intel.stl.ui.admin.view.devicegroups;

import java.awt.Component;
import java.util.List;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;

import com.intel.stl.ui.admin.impl.devicegroups.SelectionWrapper;
import com.intel.stl.ui.common.UIConstants;

public class ListPanel<E> extends JScrollPane {
    private static final long serialVersionUID = 8708700061726534862L;

    private final JList<SelectionWrapper<E>> list;

    /**
     * Description:
     * 
     */
    public ListPanel() {
        super();
        list = new JList<SelectionWrapper<E>>();
        list.setCellRenderer(new ListRenderer());
        setViewportView(list);
    }

    class ListRenderer extends DefaultListCellRenderer {
        private static final long serialVersionUID = -3056242589407591880L;

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.DefaultListCellRenderer#getListCellRendererComponent(
         * javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @SuppressWarnings("unchecked")
        @Override
        public Component getListCellRendererComponent(JList<?> list,
                Object value, int index, boolean isSelected,
                boolean cellHasFocus) {
            JLabel res =
                    (JLabel) super.getListCellRendererComponent(list, value,
                            index, isSelected, cellHasFocus);
            if (value instanceof SelectionWrapper) {
                SelectionWrapper<E> sw = (SelectionWrapper<E>) value;
                if (sw.isSelected()) {
                    res.setEnabled(false);
                }
            }
            if (!isSelected) {
                res.setOpaque(true);
                res.setBackground(index % 2 == 0 ? UIConstants.INTEL_WHITE
                        : UIConstants.INTEL_TABLE_ROW_GRAY);
            }
            return res;
        }
    }

    public void setModel(ListModel<SelectionWrapper<E>> model) {
        list.setModel(model);
    }

    /**
     * <i>Description:</i>
     * 
     * @param selectionModel
     */
    public void setSelectionModel(ListSelectionModel selectionModel) {
        list.setSelectionModel(selectionModel);
    }

    /**
     * <i>Description:</i>
     * 
     * @return
     */
    public List<SelectionWrapper<E>> getSelectedValuesList() {
        return list.getSelectedValuesList();
    }

    /**
     * <i>Description:</i>
     * 
     */
    public void clearSelection() {
        list.clearSelection();
    }
}
