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
import java.util.Vector;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.UIManager;

import com.intel.stl.ui.common.ExComboBoxModel;

/**
 * Extended JComboBox that support disabled items
 */
public class ExComboBox<E> extends JComboBox<E> {
    private static final long serialVersionUID = 7774901357006599064L;

    private ExComboBoxModel<E> model;

    private Color disabledColor;

    /**
     * Description:
     * 
     */
    public ExComboBox() {
        super();
        super.setRenderer(new DisabledItemsRenderer());
    }

    /**
     * Description:
     * 
     * @param aModel
     */
    public ExComboBox(ExComboBoxModel<E> aModel) {
        super(aModel);
        super.setRenderer(new DisabledItemsRenderer());
    }

    /**
     * Description:
     * 
     * @param items
     */
    public ExComboBox(E[] items) {
        this(new ExComboBoxModel<E>(items, true));
    }

    /**
     * Description:
     * 
     * @param items
     */
    public ExComboBox(Vector<E> items) {
        this(new ExComboBoxModel<E>(items, true));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComboBox#setModel(javax.swing.ComboBoxModel)
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void setModel(ComboBoxModel<E> aModel) {
        if (!(aModel instanceof ExComboBoxModel)) {
            throw new IllegalArgumentException("Model must be a "
                    + ExComboBoxModel.class.getSimpleName() + " model!");
        }
        model = (ExComboBoxModel) aModel;
        super.setModel(aModel);
    }

    /**
     * @param disabledColor
     *            the disabledColor to set
     */
    public void setDisabledColor(Color disabledColor) {
        this.disabledColor = disabledColor;
    }

    @Override
    public void setSelectedIndex(int index) {
        if (!model.isDisabled(index)) {
            super.setSelectedIndex(index);
        }
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
            decorateDisabledCell(label, model.isDisabled(index), index);
            return label;
        }
    }

    protected void decorateDisabledCell(JLabel label, boolean isDisabled,
            int index) {
        if (isDisabled) {
            if (disabledColor != null) {
                label.setForeground(disabledColor);
            } else {
                label.setForeground(UIManager
                        .getColor("Label.disabledForeground"));
            }
        }
    }
}
