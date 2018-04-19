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

package com.intel.stl.ui.admin.view.virtualfabrics;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;

import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.virtualfabrics.VirtualFabricRendererModel;
import com.intel.stl.ui.admin.view.AbstractConfView;
import com.intel.stl.ui.admin.view.ItemListPanel;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;

public class VirtualFabricsSubpageView extends
        AbstractConfView<VirtualFabric, VirtualFabricsEditorPanel> {
    private static final long serialVersionUID = -1922337021727497050L;

    /**
     * Description:
     * 
     * @param name
     */
    public VirtualFabricsSubpageView(String name) {
        super(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractConfView#createrEditorPanel()
     */
    @Override
    protected VirtualFabricsEditorPanel createrEditorPanel() {
        return new VirtualFabricsEditorPanel(new VirtualFabricRendererModel());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractConfView#createItemSelectionPanel()
     */
    @Override
    protected ItemListPanel<VirtualFabric> createItemSelectionPanel() {
        ItemListPanel<VirtualFabric> listPanel =
                new ItemListPanel<VirtualFabric>(getName());
        listPanel.setItemRenderer(new ListCellRenderer<Item<VirtualFabric>>() {

            @Override
            public Component getListCellRendererComponent(
                    JList<? extends Item<VirtualFabric>> list,
                    Item<VirtualFabric> value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Item<?> item = value;
                VirtualFabric vf = (VirtualFabric) item.getObj();
                ItemPanel ip = new ItemPanel(item.getName());
                ip.setState(isSelected, item.isEditable(), vf.getEnable()
                        .isSelected());
                return ip;
            }

        });
        return listPanel;
    }

    class ItemPanel extends JPanel {
        private static final long serialVersionUID = 2072583310868638992L;

        private final JLabel enableLabel;

        private final JLabel itemLabel;

        public ItemPanel(String name) {
            super(new FlowLayout(FlowLayout.LEADING, 2, 2));
            setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

            enableLabel = new JLabel();
            add(enableLabel);

            itemLabel = ComponentFactory.getH5Label(name, Font.PLAIN);
            add(itemLabel);
        }

        public void setState(boolean selected, boolean editable, boolean enabled) {
            if (selected) {
                setBackground(UIConstants.INTEL_BLUE);
                itemLabel.setForeground(UIConstants.INTEL_WHITE);
            } else {
                setBackground(UIConstants.INTEL_WHITE);
            }

            if (!editable) {
                itemLabel.setIcon(UIImages.UNEDITABLE.getImageIcon());
            } else {
                itemLabel.setIcon(null);
            }

            if (enabled) {
                if (selected) {
                    enableLabel.setIcon(UIImages.CHECK_WHITE_ICON
                            .getImageIcon());
                } else {
                    enableLabel
                            .setIcon(UIImages.CHECK_BLUE_ICON.getImageIcon());
                }
            } else {
                enableLabel.setIcon(null);
            }
        }

    }
}
