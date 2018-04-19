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

package com.intel.stl.ui.admin.view;

import java.awt.Component;
import java.text.NumberFormat;

import javax.swing.JFormattedTextField.AbstractFormatter;

import com.intel.stl.api.management.WrapperNode;
import com.intel.stl.ui.common.view.ExFormattedTextField;

public abstract class FieldRenderer<T, E extends WrapperNode<T>> extends
        AbstractAttrRenderer<E> {
    protected ExFormattedTextField field;

    public FieldRenderer(AbstractFormatter formatter, T defaultValue) {
        super();
        try {
            field = createFiled(formatter);
            init(defaultValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public FieldRenderer(NumberFormat format, T defaultValue) {
        super();
        field = createFiled(format);
        init(defaultValue);
    }

    protected ExFormattedTextField createFiled(AbstractFormatter formatter) {
        return new ExFormattedTextField(formatter);
    }

    protected ExFormattedTextField createFiled(NumberFormat format) {
        return new ExFormattedTextField(format);
    }

    protected void init(T defaultValue) {
        field.setValue(defaultValue);
    }

    protected void setValidationTooltip(String tooltip) {
        field.setValidationTooltip(tooltip);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.IAttrRenderer#setAttr(com.intel.stl.api.
     * management.IAttribute)
     */
    @Override
    public void setAttr(E attr) {
        field.setValue(attr.getObject());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.IAttrRenderer#setEditable(boolean)
     */
    @Override
    public void setEditable(boolean isEditable) {
        field.setEditable(isEditable);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.IAttrRenderer#getAttr()
     */
    @SuppressWarnings("unchecked")
    @Override
    public E getAttr() {
        return createAttr((T) field.getValue());
    }

    protected abstract E createAttr(T value);

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractAttrRenderer#getFields()
     */
    @Override
    protected Component[] getFields() {
        return new Component[] { field };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.IAttrRenderer#isEditValid()
     */
    @Override
    public boolean isEditValid() {
        return field.isEditValid();
    }

}
