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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionListener;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeSelectionModel;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.admin.impl.devicegroups.DeviceNode;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;

public class DevicesPanel extends JPanel {
    private static final long serialVersionUID = 1350511780844381213L;

    public static final String GUID = "guid";

    public static final String DESC = "desc";

    private JPanel ctrPanel;

    private JCheckBox guidBox;

    private JTree tree;

    private boolean showGUID;

    public DevicesPanel() {
        super();
        initComponent();
    }

    protected void initComponent() {
        setLayout(new BorderLayout());
        JPanel panel = getControlPanel();
        add(panel, BorderLayout.NORTH);

        JTree tree = getTree();
        JScrollPane pane = new JScrollPane(tree);
        add(pane, BorderLayout.CENTER);
    }

    protected JPanel getControlPanel() {
        if (ctrPanel == null) {
            ctrPanel = new JPanel(new FlowLayout(FlowLayout.LEADING, 5, 2));
            ctrPanel.setOpaque(false);

            JLabel label =
                    ComponentFactory.getH4Label(
                            STLConstants.K2135_OPTIONS.getValue(), Font.PLAIN);
            ctrPanel.add(label);

            guidBox = new JCheckBox(STLConstants.K2136_USE_GUID.getValue());
            guidBox.setForeground(UIConstants.INTEL_DARK_GRAY);
            guidBox.setFont(UIConstants.H4_FONT);
            ctrPanel.add(guidBox);
        }
        return ctrPanel;
    }

    protected JTree getTree() {
        if (tree == null) {
            tree = new JTree();
            tree.setCellRenderer(new DescRenderer());
            tree.setRootVisible(false);
            tree.setShowsRootHandles(true);
        }
        return tree;
    }

    public void setTreeModel(TreeModel model) {
        tree.setModel(model);
    }

    public void addOptionsListener(ActionListener listener) {
        guidBox.addActionListener(listener);
    }

    public void removeOptionsListener(ActionListener listener) {
        guidBox.removeActionListener(listener);
    }

    public void setSelectionModel(TreeSelectionModel selectionModel) {
        tree.setSelectionModel(selectionModel);
    }

    // not used for now
    public void showGUID(boolean b) {
        if (showGUID != b) {
            showGUID = b;
            tree.setCellRenderer(b ? new GUIRenderer() : new DescRenderer());
        }
    }

    class GUIRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 3313950353093825301L;

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent
         * (javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int,
         * boolean)
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            JLabel label =
                    (JLabel) super.getTreeCellRendererComponent(tree, value,
                            sel, expanded, leaf, row, hasFocus);
            if (value instanceof DeviceNode) {
                DeviceNode node = (DeviceNode) value;
                label.setIcon(node.getType().getIcon());
                label.setEnabled(!node.isSelected());
                if (node.getGuid() != 0) {
                    label.setText(StringUtils.longHexString(node.getGuid()));
                }
            }
            return label;
        }
    }

    class DescRenderer extends DefaultTreeCellRenderer {
        private static final long serialVersionUID = 3313950353093825301L;

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.tree.DefaultTreeCellRenderer#getTreeCellRendererComponent
         * (javax.swing.JTree, java.lang.Object, boolean, boolean, boolean, int,
         * boolean)
         */
        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value,
                boolean sel, boolean expanded, boolean leaf, int row,
                boolean hasFocus) {
            JLabel label =
                    (JLabel) super.getTreeCellRendererComponent(tree, value,
                            sel, expanded, leaf, row, hasFocus);
            if (value instanceof DeviceNode) {
                DeviceNode node = (DeviceNode) value;
                label.setIcon(node.getType().getIcon());
                label.setEnabled(!node.isSelected());
            }
            return label;
        }
    }

}
