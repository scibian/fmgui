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

import static java.awt.Font.PLAIN;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.ParallelGroup;
import javax.swing.GroupLayout.SequentialGroup;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.configuration.MultiColumnCategoryController;
import com.intel.stl.ui.framework.AbstractView;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyItem;

public class MultiColumnCategoryPanel extends
        AbstractView<DevicePropertyCategory, MultiColumnCategoryController> {

    private static final long serialVersionUID = 1L;

    protected JPanel mainPanel;

    protected JPanel pagePanel;

    protected GroupLayout layout;

    protected DevicePropertyCategory model;

    private final int numCols;

    private final PropertyVizStyle style;

    /**
     *
     * Description: displays PropertyItems in a multi-column view
     *
     * @param numCols
     *            the number of columns in the page
     */
    public MultiColumnCategoryPanel(int numCols, PropertyVizStyle style) {
        if (numCols <= 0) {
            throw new IllegalArgumentException(
                    "Invalid number of columns: " + numCols);
        }
        this.numCols = numCols;
        this.style = style;
    }

    @Override
    public void modelUpdateFailed(DevicePropertyCategory model,
            Throwable caught) {
    }

    @Override
    public void modelChanged(DevicePropertyCategory model) {
        this.model = model;
        displayPage(0, model.size());
    }

    @Override
    public JComponent getMainComponent() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        mainPanel.setBackground(UIConstants.INTEL_WHITE);
        return mainPanel;
    }

    @Override
    public void initComponents() {
        createPagePanel();
        mainPanel.add(pagePanel);
    }

    protected void createPagePanel() {
        pagePanel = new JPanel();
        layout = new GroupLayout(pagePanel);
        pagePanel.setLayout(layout);
        pagePanel.setBackground(UIConstants.INTEL_WHITE);
        return;
    }

    protected void displayPage(int start, int numLines) {
        String keyHeader = model.getKeyHeader();
        String valHeader = model.getValueHeader();
        if (keyHeader == null || keyHeader.length() == 0) {
            displayPage(start, numLines, null, null);
        } else {
            if (valHeader == null) {
                valHeader = "";
            }
            displayPage(start, numLines, keyHeader, valHeader);
        }
    }

    protected void displayPage(int start, int numLines, String keyHeader,
            String valueHeader) {
        boolean showHeaders = false;
        if (keyHeader != null) {
            showHeaders = true;
        }
        int itemsPerColumn = (numLines + (numCols - 1)) / numCols;

        pagePanel.removeAll();
        SequentialGroup hGroup = layout.createSequentialGroup();
        SequentialGroup vGroup = layout.createSequentialGroup();
        ParallelGroup headerRow = null;
        ParallelGroup[] row = new ParallelGroup[itemsPerColumn];
        if (showHeaders) {
            headerRow =
                    layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            vGroup.addGroup(headerRow);
        }
        for (int i = 0; i < itemsPerColumn; i++) {
            row[i] = layout.createParallelGroup(GroupLayout.Alignment.BASELINE);
            vGroup.addGroup(row[i]);
        }
        List<DevicePropertyItem> items = model.getList();
        int size = model.size();
        int offset = start;
        int itemCt = 0;
        List<Component> keys = new ArrayList<Component>();
        List<Component> values = new ArrayList<Component>();
        for (int i = 0; i < numCols; i++) {
            ParallelGroup keyCol =
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            ParallelGroup valCol =
                    layout.createParallelGroup(GroupLayout.Alignment.LEADING);
            hGroup.addGap(5);
            hGroup.addGroup(keyCol);
            hGroup.addGroup(valCol);
            hGroup.addGap(5);
            keys.clear();
            values.clear();
            if (showHeaders) {
                JLabel keyTitle = createHeaderKey(keyHeader, 0);
                keyTitle.setName(WidgetName.PM_PROP_HEADER_NAME_ + keyHeader);
                keys.add(keyTitle);
                JLabel valTitle = createHeaderValue(valueHeader);
                valTitle.setName(WidgetName.PM_PROP_HEADER_VALUE_ + keyHeader);
                values.add(valTitle);
                headerRow.addComponent(keyTitle);
                headerRow.addComponent(valTitle);
                keyCol.addComponent(keyTitle);
                valCol.addComponent(valTitle);
            }
            for (int j = 0; j < itemsPerColumn; j++) {
                if (itemCt < numLines && offset < size) {
                    DevicePropertyItem item = items.get(offset);
                    JLabel key = createKey(item.getLabel(), j);
                    key.setName(
                            WidgetName.PM_PROP_ITEM_NAME_ + item.getLabel());
                    keys.add(key);
                    JLabel value = createValue(item.getValue(), j);
                    value.setName(
                            WidgetName.PM_PROP_ITEM_VALUE_ + item.getLabel());
                    values.add(value);
                    row[j].addComponent(key);
                    row[j].addComponent(value);
                    keyCol.addComponent(key);
                    valCol.addComponent(value);
                }
                itemCt++;
                offset++;
            }
            layout.linkSize(SwingConstants.HORIZONTAL,
                    keys.toArray(new Component[0]));
            layout.linkSize(SwingConstants.HORIZONTAL,
                    values.toArray(new Component[0]));
        }
        layout.setHorizontalGroup(hGroup);
        layout.setVerticalGroup(vGroup);
        pagePanel.repaint();
    }

    protected JLabel createKey(String text, int row) {
        JLabel label = ComponentFactory.getH4Label(text, PLAIN);
        label.setBorder(BorderFactory.createEmptyBorder(1, 2, 1, 3));
        style.decorateKey(label, row);
        return label;
    }

    protected JLabel createValue(String text, int row) {
        JLabel label = ComponentFactory.getH4Label(text, PLAIN);
        label.setBorder(BorderFactory.createEmptyBorder(1, 3, 1, 2));
        style.decorateValue(label, row);
        return label;
    }

    protected JLabel createHeaderKey(String text, int row) {
        JLabel label = ComponentFactory.getH4Label(text, PLAIN);
        style.decorateHeaderKey(label, row);
        return label;
    }

    protected JLabel createHeaderValue(String text) {
        JLabel label = ComponentFactory.getH4Label(text, PLAIN);
        style.decorateHeaderValue(label);
        return label;
    }
}
