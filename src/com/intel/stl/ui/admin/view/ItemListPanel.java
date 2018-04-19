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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.decorator.ColorHighlighter;
import org.jdesktop.swingx.decorator.HighlightPredicate;

import com.intel.stl.ui.admin.IItemListListener;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;

public class ItemListPanel<T> extends JPanel {
    private static final long serialVersionUID = -8018647522341749109L;

    private final String name;

    private JLabel titleLabel;

    private JXList itemList;

    private JPanel ctrPanel;

    private JButton addBtn;

    private JButton removeBtn;

    private final List<IItemListListener> listeners =
            new CopyOnWriteArrayList<IItemListListener>();

    /**
     * Description:
     * 
     * @param name
     */
    public ItemListPanel(String name) {
        super();
        this.name = name;
        initComponent();
    }

    @SuppressWarnings("rawtypes")
    protected void initComponent() {
        setLayout(new BorderLayout(5, 5));

        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        UIConstants.INTEL_TABLE_BORDER_GRAY, 1, true),
                BorderFactory.createEmptyBorder(0, 5, 5, 5)));
        // panel.setBorder(BorderFactory.createLineBorder(
        // UIConstants.INTEL_TABLE_BORDER_GRAY, 1, true));
        titleLabel = ComponentFactory.getH3Label(name, Font.BOLD);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        titleLabel.setBorder(BorderFactory.createCompoundBorder(BorderFactory
                .createMatteBorder(0, 0, 2, 0, UIConstants.INTEL_ORANGE),
                BorderFactory.createEmptyBorder(5, 0, 5, 0)));
        panel.add(titleLabel, BorderLayout.NORTH);

        itemList = new JXList();
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemList.setCellRenderer(new ListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                Item<?> item = (Item<?>) value;
                JLabel label =
                        ComponentFactory.getH5Label(item.getName(), Font.PLAIN);
                label.setOpaque(true);
                label.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
                if (isSelected) {
                    label.setBackground(UIConstants.INTEL_BLUE);
                    label.setForeground(UIConstants.INTEL_WHITE);
                } else {
                    label.setBackground(UIConstants.INTEL_WHITE);
                }
                if (!item.isEditable()) {
                    label.setIcon(UIImages.UNEDITABLE.getImageIcon());
                }
                return label;
            }
        });
        itemList.setRolloverEnabled(true);
        ColorHighlighter rooloverHighlighter =
                new ColorHighlighter(HighlightPredicate.ROLLOVER_ROW,
                        UIConstants.INTEL_LIGHT_BLUE, UIConstants.INTEL_WHITE);
        ColorHighlighter evenHighlighter =
                new ColorHighlighter(HighlightPredicate.EVEN,
                        UIConstants.INTEL_TABLE_ROW_GRAY, null);
        itemList.setHighlighters(evenHighlighter, rooloverHighlighter);
        itemList.addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                Item<?> item = (Item<?>) itemList.getSelectedValue();
                removeBtn.setEnabled(item != null && item.isEditable());
                fireItemSelected(item);
            }
        });
        JScrollPane pane = new JScrollPane(itemList);
        panel.add(pane, BorderLayout.CENTER);

        add(panel, BorderLayout.CENTER);

        ctrPanel = new JPanel();
        ctrPanel.setOpaque(false);
        installButtons(ctrPanel);
        add(ctrPanel, BorderLayout.SOUTH);
    }

    public void setItemRenderer(ListCellRenderer<Item<T>> renderer) {
        itemList.setCellRenderer(renderer);
    }

    protected void installButtons(JPanel panel) {
        addBtn = ComponentFactory.getIntelActionButton("+");
        addBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireAddItem();
            }
        });
        panel.add(addBtn);

        removeBtn = ComponentFactory.getIntelActionButton("-");
        removeBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fireRemoveItem(itemList.getSelectedValue());
            }
        });
        panel.add(removeBtn);
    }

    /**
     * <i>Description:</i>
     * 
     * @param model
     */
    public void setListModel(ListModel<Item<T>> model) {
        itemList.setModel(model);
        boolean hasEditable = false;
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            Item<T> item = model.getElementAt(i);
            if (item.isEditable()) {
                hasEditable = true;
                break;
            }
        }
        removeBtn.setEnabled(hasEditable);
    }

    protected void fireItemSelected(Object selectedValue) {
        long id =
                selectedValue == null ? -1 : ((Item<?>) selectedValue).getId();
        for (IItemListListener listener : listeners) {
            listener.onSelect(id);
        }
    }

    protected void fireAddItem() {
        for (IItemListListener listener : listeners) {
            listener.onAdd();
        }
    }

    protected void fireRemoveItem(Object selectedValue) {
        if (selectedValue == null) {
            return;
        }

        long id = ((Item<?>) selectedValue).getId();
        for (IItemListListener listener : listeners) {
            listener.onRemove(id);
        }
    }

    /**
     * <i>Description:</i>
     * 
     * @param selectedValue
     */
    public void addListSelectionListener(ListSelectionListener listener) {
        itemList.addListSelectionListener(listener);
    }

    public void removeListSelectionListener(ListSelectionListener listener) {
        itemList.removeListSelectionListener(listener);
    }

    /**
     * <i>Description:</i>
     * 
     * @param index
     */
    public void selectItem(final int index) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                itemList.setSelectedIndex(index);
                Item<?> item = (Item<?>) itemList.getSelectedValue();
                removeBtn.setEnabled(item != null && item.isEditable());
                revalidate();
                repaint();
            }
        });
    }

    /**
     * <i>Description:</i>
     * 
     * @param listener
     */
    public void addItemListListener(IItemListListener listener) {
        listeners.add(listener);
    }

    /**
     * <i>Description:</i>
     * 
     * @param listener
     */
    public void removeItemListListener(IItemListListener listener) {
        listeners.remove(listener);
    }

}
