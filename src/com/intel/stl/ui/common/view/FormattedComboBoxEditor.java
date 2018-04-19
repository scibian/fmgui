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
import java.awt.event.ActionListener;
import java.text.ParseException;

import javax.swing.BorderFactory;
import javax.swing.ComboBoxEditor;
import javax.swing.JFormattedTextField;

import com.intel.stl.ui.common.UIConstants;

public class FormattedComboBoxEditor implements ComboBoxEditor {
    private final JFormattedTextField textField;

    public FormattedComboBoxEditor(JFormattedTextField textField) {
        super();
        this.textField = textField;
        textField.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0,
                UIConstants.INTEL_GRAY));
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxEditor#getEditorComponent()
     */
    @Override
    public Component getEditorComponent() {
        return textField;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxEditor#setItem(java.lang.Object)
     */
    @Override
    public void setItem(Object anObject) {
        try {
            String newText = textField.getFormatter().valueToString(anObject);
            if (!newText.equals(textField.getText())) {
                textField.setText(newText);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxEditor#getItem()
     */
    @Override
    public Object getItem() {
        String text = textField.getText();
        try {
            return textField.getFormatter().stringToValue(text);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.ComboBoxEditor#selectAll()
     */
    @Override
    public void selectAll() {
        textField.selectAll();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.ComboBoxEditor#addActionListener(java.awt.event.ActionListener
     * )
     */
    @Override
    public void addActionListener(ActionListener l) {
        textField.addActionListener(l);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.ComboBoxEditor#removeActionListener(java.awt.event.ActionListener
     * )
     */
    @Override
    public void removeActionListener(ActionListener l) {
        textField.removeActionListener(l);
    }

}
