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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JPanel;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.model.IPropertyCategory;
import com.intel.stl.ui.model.PropertyItem;

public class PropertyCategoryPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    protected PropertyVizStyle style;

    protected IPropertyRenderer propertyRenderer;

    public PropertyCategoryPanel() {
        this(new PropertyVizStyle());
    }

    /**
     * Description:
     *
     * @param style
     */
    public PropertyCategoryPanel(PropertyVizStyle style) {
        super();
        this.style = style;
        initComponent();
    }

    protected void initComponent() {
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        setBackground(UIConstants.INTEL_WHITE);
    }

    /**
     * @return the propertyRenderer
     */
    public IPropertyRenderer getPropertyRenderer() {
        if (propertyRenderer == null) {
            propertyRenderer = new DefaultPropertyRenderer();
        }
        return propertyRenderer;
    }

    /**
     * @param propertyRenderer
     *            the propertyRenderer to set
     */
    public void setPropertyRenderer(IPropertyRenderer propertyRenderer) {
        this.propertyRenderer = propertyRenderer;
    }

    /**
     * @return the style
     */
    public PropertyVizStyle getStyle() {
        return style;
    }

    /**
     * @param style
     *            the style to set
     */
    public void setStyle(PropertyVizStyle style) {
        this.style = style;
    }

    public <C extends IPropertyCategory<? extends PropertyItem<?>>> void setModel(
            C model) {
        int row = 0;
        removeAll();
        if (model.getKeyHeader() != null || model.getValueHeader() != null) {
            addHeaders(model, row);
            row++;
        }

        int itemIndex = 0;
        for (PropertyItem<?> item : model.getItems()) {
            addPropertyItem(item, row, itemIndex++);
            row++;
        }
        repaint();
    }

    private void addHeaders(IPropertyCategory<?> category, int row) {
        GridBagConstraints gc = createConstraints(2, 2, 0, 3);
        gc.gridx = 0;
        gc.gridy = 0;
        gc.weightx = 1;
        Component keyComp = getPropertyRenderer()
                .getKeyHeaderComponent(category, row, style);
        keyComp.setName(
                WidgetName.PM_PROP_HEADER_NAME_ + category.getKeyHeader());
        String valueHeader = category.getValueHeader();
        if (valueHeader != null && valueHeader.length() > 0) {
            add(keyComp, gc);
            gc = createConstraints(2, 2, 0, 3);
            gc.gridx = 1;
            gc.gridy = 0;
            Component valComp = getPropertyRenderer()
                    .getValueHeaderComponent(category, row, style);
            valComp.setName(
                    WidgetName.PM_PROP_HEADER_VALUE_ + category.getKeyHeader());
            add(valComp, gc);
        } else {
            gc.gridwidth = 2;
            add(keyComp, gc);
        }
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(Box.createGlue(), gc);
    }

    private void addPropertyItem(PropertyItem<?> item, int row, int itemIndex) {
        GridBagConstraints gc = createConstraints(0, 12, 0, 0);
        gc.gridx = 0;
        gc.gridy = row;
        Component keyComp = getPropertyRenderer().getKeyComponent(item,
                itemIndex, row, style);
        keyComp.setName(WidgetName.PM_PROP_ITEM_NAME_ + item.getLabel());
        add(keyComp, gc);

        gc = createConstraints(0, 0, 0, 3);
        gc.gridx = 1;
        gc.gridy = row;
        Component valComp = getPropertyRenderer().getValueComponent(item,
                itemIndex, row, style);
        valComp.setName(WidgetName.PM_PROP_ITEM_VALUE_ + item.getLabel());
        add(valComp, gc);
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(Box.createGlue(), gc);

    }

    private GridBagConstraints createConstraints(int yPadTop, int xPadLeft,
            int yPadBtm, int xPadRight) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(yPadTop, xPadLeft, yPadBtm, xPadRight);
        gc.weightx = 1;
        return gc;
    }

}
