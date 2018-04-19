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

package com.intel.stl.ui.configuration.view;

import java.awt.Component;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.IPropertyCategory;
import com.intel.stl.ui.model.PropertyItem;

public class DefaultPropertyRenderer implements IPropertyRenderer {
    private final static String NA = STLConstants.K0039_NOT_AVAILABLE
            .getValue();

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyRenderer#getKeyHeaderComponent
     * (com.intel.stl.ui.model.IPropertyCategory, int,
     * com.intel.stl.ui.configuration.view.PropertyVizStyle)
     */
    @Override
    public Component getKeyHeaderComponent(IPropertyCategory<?> category,
            int row, PropertyVizStyle style) {
        String key = category.getKeyHeader();
        JLabel res =
                ComponentFactory.getH4Label(key == null ? "" : key, Font.BOLD);
        style.decorateHeaderKey(res, row);
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyRenderer#getValueHeaderComponent
     * (com.intel.stl.ui.model.IPropertyCategory, int,
     * com.intel.stl.ui.configuration.view.PropertyVizStyle)
     */
    @Override
    public Component getValueHeaderComponent(IPropertyCategory<?> category,
            int row, PropertyVizStyle style) {
        String value = category.getValueHeader();
        JLabel res =
                ComponentFactory.getH4Label(value == null ? "" : value,
                        Font.BOLD);
        style.decorateHeaderKey(res, row);
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyRenderer#getKeyComponent
     * (com.intel.stl.ui.model.PropertyItem, int, int,
     * com.intel.stl.ui.configuration.view.PropertyVizStyle)
     */
    @Override
    public Component getKeyComponent(PropertyItem<?> item, int itemIndex,
            int row, PropertyVizStyle style) {
        String key = item.getLabel();
        JLabel res =
                ComponentFactory.getH4Label(key == null ? "" : key, Font.PLAIN);
        res.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 3));
        style.decorateKey(res, itemIndex);
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyRenderer#getValueComponent
     * (com.intel.stl.ui.model.PropertyItem, int, int,
     * com.intel.stl.ui.configuration.view.PropertyVizStyle)
     */
    @Override
    public Component getValueComponent(PropertyItem<?> item, int itemIndex,
            int row, PropertyVizStyle style) {
        String value = item.getValue();
        JLabel res =
                ComponentFactory.getH4Label(value == null ? NA : value,
                        Font.PLAIN);
        res =
                ComponentFactory.deriveLabel(res, false,
                        UIConstants.MAX_LABEL_Width);
        res.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 2));
        style.decorateValue(res, itemIndex);
        return res;
    }
}
