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

package com.intel.stl.ui.monitor.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Enumeration;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.monitor.TreeTypeEnum;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeModel;

public class StackPanel extends JPanel {
    private static final long serialVersionUID = -2905931691586163645L;

    private final TreeTypeEnum id;

    private final JPanel headerPanel;

    private final JLabel nameLabel;

    private final JLabel arrowLabel;

    private JTree tree;

    private JLabel txtLabel;

    private boolean opened = false;

    public StackPanel(TreeTypeEnum type, JTree tree,
            final IStack stackInterface) {
        super(new GridBagLayout());
        this.id = type;

        setOpaque(false);
        headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INTEL_BORDER_GRAY),
                BorderFactory.createEmptyBorder(0, 5, 0, 5)));
        headerPanel.setPreferredSize(new Dimension(200, 30));
        headerPanel.setBackground(UIConstants.INTEL_WHITE);
        nameLabel = ComponentFactory.getH4Label(id.getName(), Font.BOLD);
        headerPanel.add(nameLabel, BorderLayout.WEST);
        arrowLabel = new JLabel(UIImages.DOWN_ICON.getImageIcon());
        headerPanel.add(arrowLabel, BorderLayout.EAST);
        headerPanel.addMouseListener(new MouseAdapter() {
            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.MouseAdapter#mouseReleased(java.awt.event.
             * MouseEvent)
             */
            @Override
            public void mouseReleased(MouseEvent e) {
                if (opened) {
                    close();
                } else {
                    open();
                }
                stackInterface.stackChange(id);
            }
        });

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        add(headerPanel, gc);

        gc.fill = GridBagConstraints.BOTH;
        gc.insets = new Insets(2, 5, 5, 5);
        if (tree != null) {
            tree.setName(type.getName());
            tree.setCellRenderer(new NodeRenderer());
            tree.setVisible(false);
            this.tree = tree;
            add(tree, gc);
        } else {
            txtLabel = new JLabel("Tree is unavailable at this time!");
            txtLabel.setVisible(false);
            add(txtLabel, gc);
        }
    }

    public void setSearchCount(int n) {
        nameLabel.setText(id.getName() + " (" + String.valueOf(n) + ")");
    }

    public void setTreeModel(FVTreeModel pModel) {
        if (tree != null) {
            TreePath[] selections = null;
            Enumeration<TreePath> expanedPaths = null;
            if (tree.getModel() != null
                    && tree.getModel().getRoot().equals(pModel.getRoot())) {
                selections = tree.getSelectionPaths();
                expanedPaths =
                        tree.getExpandedDescendants(tree.getPathForRow(0));
            }
            tree.setModel(pModel);
            pModel.fireTreeStructureChanged(this,
                    ((FVResourceNode) pModel.getRoot()).getPath());
            if (expanedPaths != null) {
                while (expanedPaths.hasMoreElements()) {
                    TreePath path = expanedPaths.nextElement();
                    tree.expandPath(path);
                }
            }
            if (selections != null) {
                TreeSelectionListener[] listeners =
                        tree.getTreeSelectionListeners();
                for (TreeSelectionListener listener : listeners) {
                    tree.removeTreeSelectionListener(listener);
                }
                TreePath[] newSels = pModel.getTreePaths(selections);
                tree.setSelectionPaths(newSels);
                for (TreeSelectionListener listener : listeners) {
                    tree.addTreeSelectionListener(listener);
                }
            }
        }
    }

    public TreeModel getTreeModel() {
        if (tree != null) {
            return tree.getModel();
        } else {
            return null;
        }
    }

    public void open() {
        if (tree != null) {
            tree.setVisible(true);
            TreePath[] currentPaths = tree.getSelectionPaths();
            if (currentPaths != null && currentPaths.length > 0) {
                // force reselect a node, so we can update
                // charts/tables from Tree Controller
                tree.removeSelectionPaths(currentPaths);
                tree.setSelectionPaths(currentPaths);
            } else {
                tree.setSelectionRow(0);
                tree.scrollRowToVisible(0);
            }
        } else {
            txtLabel.setVisible(true);
        }
        arrowLabel.setIcon(UIImages.UP_ICON.getImageIcon());
        opened = true;
    }

    public void close() {
        if (!opened) {
            return;
        }

        if (tree != null) {
            tree.setVisible(false);
        } else {
            txtLabel.setVisible(false);
        }
        arrowLabel.setIcon(UIImages.DOWN_ICON.getImageIcon());
        opened = false;
    }

    public void setSelectionMode(int selectionMode) {
        if (tree != null) {
            tree.getSelectionModel().setSelectionMode(selectionMode);
        }
    }

    public void select(int index) {
        if (tree != null) {
            tree.setSelectionRow(index);
        }
    }

    public void select(TreePath[] paths, boolean[] isExpanded) {
        if (tree != null) {
            TreePath[] newSels =
                    ((FVTreeModel) tree.getModel()).getTreePaths(paths);
            tree.setSelectionPaths(newSels);
            if (paths.length > 0) {
                for (int i = 0; i < paths.length; i++) {
                    if (!isExpanded[i]) {
                        tree.collapsePath(paths[i]);
                    } else {
                        tree.expandPath(paths[i]);
                    }
                    tree.makeVisible(paths[i]);
                }
                int row = tree.getRowForPath(paths[0]);
                row += 5;
                if (row > tree.getRowCount()) {
                    row = tree.getRowCount() - 1;
                }
                tree.scrollRowToVisible(row);
            }
        }
    }

    public void ensureSelectionVisible() {
        if (tree != null) {
            TreePath path = tree.getSelectionPath();
            int row = Math.max(0, tree.getRowForPath(path) - 5);
            tree.scrollRowToVisible(row);
        }
    }

    public void clearSelection() {
        if (tree != null) {
            tree.clearSelection();
        }
    }

    /**
     * Description:
     *
     * @param paths
     */
    public void collapse(TreePath path) {
        if (tree != null) {
            tree.collapsePath(path);
        }
    }

    public void expand(TreePath path) {
        if (tree != null) {
            tree.expandPath(path);
        }
    }

    public void addTreeListener(TreeSelectionListener treeListener) {
        if (tree != null) {
            tree.addTreeSelectionListener(treeListener);
        }
    }

    /**
     * @return the opened
     */
    public boolean isOpened() {
        return opened;
    }

} // class StackPanel
