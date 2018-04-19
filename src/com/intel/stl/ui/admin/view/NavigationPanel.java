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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jfree.ui.tabbedui.VerticalLayout;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;

public class NavigationPanel extends JPanel {
    private static final long serialVersionUID = -7750864974612798171L;

    private final Map<String, IconPanel> itemMap =
            new LinkedHashMap<String, IconPanel>();

    private final List<ChangeListener> listeners =
            new CopyOnWriteArrayList<ChangeListener>();

    public NavigationPanel() {
        super(new VerticalLayout());
        setBorder(BorderFactory.createMatteBorder(0, 0, 0, 10,
                UIConstants.INTEL_WHITE));
    }

    public void addItem(String name, Icon icon) {
        if (itemMap.containsKey(name)) {
            throw new IllegalArgumentException("Item '" + name
                    + "' alreday existed.");
        }

        IconPanel panel = new IconPanel(name, icon);
        itemMap.put(name, panel);
        add(panel);
    }

    public void addSeparator(int size) {
        add(Box.createRigidArea(new Dimension(10, size)));
    }

    public void selectItem(String name) {
        for (Entry<String, IconPanel> entry : itemMap.entrySet()) {
            IconPanel panel = entry.getValue();
            panel.setSelected(entry.getKey().equals(name));
        }
    }

    protected void fireSelection(String name) {
        IconPanel panel = itemMap.get(name);
        if (panel != null) {
            ChangeEvent event = new ChangeEvent(panel);
            for (ChangeListener listener : listeners) {
                listener.stateChanged(event);
            }
        }
    }

    public void addListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    public class IconPanel extends JPanel {

        private static final long serialVersionUID = -1275114280998145382L;

        private JLabel[] nameLabels;

        private final String name;

        public IconPanel(String name, Icon image) {
            super();
            this.name = name;
            initializePanel(name, image);
        }

        /**
         * @return the name
         */
        @Override
        public String getName() {
            return name;
        }

        protected void initializePanel(String title, Icon image) {
            setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

            setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
            setBorder(BorderFactory.createCompoundBorder(BorderFactory
                    .createEmptyBorder(2, 2, 2, 2), BorderFactory
                    .createCompoundBorder(BorderFactory
                            .createLineBorder(UIConstants.INTEL_GRAY),
                            BorderFactory.createEmptyBorder(4, 2, 2, 2))));

            JLabel lblIcon = new JLabel(image);
            lblIcon.setAlignmentX(0.5f);
            add(lblIcon);

            String[] lines = title.split(" ");
            nameLabels = new JLabel[lines.length];
            for (int i = 0; i < lines.length; i++) {
                nameLabels[i] =
                        ComponentFactory.getH4Label(lines[i], Font.BOLD);
                nameLabels[i].setHorizontalAlignment(JLabel.CENTER);
                nameLabels[i].setAlignmentX(0.5f);
                add(nameLabels[i]);
            }

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseReleased(MouseEvent e) {
                    fireSelection(name);
                }
            });
        }

        public void setSelected(boolean isSelected) {
            if (isSelected) {
                setBackground(UIConstants.INTEL_WHITE);
                for (JLabel label : nameLabels) {
                    label.setForeground(UIConstants.INTEL_BLUE);
                }
                setBorder(BorderFactory.createCompoundBorder(BorderFactory
                        .createEmptyBorder(2, 2, 2, 2), BorderFactory
                        .createCompoundBorder(BorderFactory.createMatteBorder(
                                1, 5, 1, 0, UIConstants.INTEL_BLUE),
                                BorderFactory.createEmptyBorder(4, 2, 2, 2))));
            } else {
                setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
                for (JLabel label : nameLabels) {
                    label.setForeground(UIConstants.INTEL_GRAY);
                }
                setBorder(BorderFactory.createCompoundBorder(BorderFactory
                        .createEmptyBorder(2, 5, 2, 2), BorderFactory
                        .createCompoundBorder(BorderFactory
                                .createLineBorder(UIConstants.INTEL_GRAY),
                                BorderFactory.createEmptyBorder(4, 2, 2, 2))));
            }
        }
    }
}
