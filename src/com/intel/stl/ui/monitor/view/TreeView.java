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
import java.util.EnumMap;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import com.intel.stl.ui.common.IBackgroundService;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.IntelSplitPaneUI;
import com.intel.stl.ui.monitor.TreeTypeEnum;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.tree.FVTreeModel;

/**
 */
public abstract class TreeView extends JPanel
        implements TreeViewInterface, IStack {

    private static final long serialVersionUID = 849119323304459300L;

    /**
     * Tree panel for left component of the split pane
     */
    private JPanel mPnlTree;

    private EnumMap<TreeTypeEnum, StackPanel> stackPanels;

    /**
     * Temporary status label for data panel
     */
    private JLabel mlblStatus;

    protected final IBackgroundService graphService;

    protected final IBackgroundService outlineService;

    private SearchView searchView;

    /**
     *
     * Description: Constructor for the TreeView class
     *
     */
    public TreeView(IBackgroundService graphService,
            IBackgroundService outlineService) {
        super();
        this.graphService = graphService;
        this.outlineService = outlineService;
        setOpaque(true);
        initComponents();
    } // TreeView

    /**
     *
     * Description: Initializes the UI components for the tree view
     *
     */
    private void initComponents() {

        // Set the layout for this panel
        setLayout(new BorderLayout());

        // Create the main split pane
        JSplitPane spltpnMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        spltpnMain.setResizeWeight(.02);
        spltpnMain.setDividerSize(5);

        // Create the tree split pane
        JSplitPane splpnTree = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splpnTree.setMinimumSize(new Dimension(250, 300));
        splpnTree.setResizeWeight(.8);
        splpnTree.setDividerSize(15);
        splpnTree.setUI(new IntelSplitPaneUI());
        splpnTree.setOneTouchExpandable(true);

        // Create the tree panel
        mPnlTree = new JPanel(new GridBagLayout());
        mPnlTree.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        stackPanels = getStackPanels();
        for (StackPanel panel : stackPanels.values()) {
            mPnlTree.add(panel, gc);
        }
        gc.weighty = 1;
        mPnlTree.add(Box.createGlue(), gc);

        // Create a scroll pane for the tree
        JScrollPane scrpnTree = new JScrollPane();
        ScrollPaneLayout spTreeLayout = new ScrollPaneLayout();
        scrpnTree.createHorizontalScrollBar();
        scrpnTree.createVerticalScrollBar();
        spTreeLayout.setHorizontalScrollBarPolicy(
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spTreeLayout.setVerticalScrollBarPolicy(
                ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrpnTree.getVerticalScrollBar().setUnitIncrement(10);
        scrpnTree.setLayout(spTreeLayout);

        searchView = new SearchView();
        splpnTree.setBottomComponent(searchView);

        // Add the tree panel to the tree scroll pane, and add the tree
        // scroll pane to the top component of the tree split pane.
        // Then add the tree split pane to the left component of the main
        // split pane
        scrpnTree.add(mPnlTree);
        scrpnTree.setViewportView(mPnlTree);
        splpnTree.setTopComponent(scrpnTree);
        spltpnMain.setLeftComponent(splpnTree);

        // Create the data panel and add a label to it
        JComponent rightComp = getDataComponent();
        rightComp.setOpaque(false);

        // Add the main component of the derived class on the right
        // side of the main split pane
        spltpnMain.setRightComponent(getMainComponent());

        add(spltpnMain, BorderLayout.CENTER);

    } // initialize

    public SearchView getSearchView() {
        return searchView;
    }

    protected JComponent getDataComponent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        mlblStatus = new JLabel("Data Goes Here!!!");
        mlblStatus.setFont(UIConstants.H3_FONT.deriveFont(Font.BOLD));
        panel.add(mlblStatus);
        return panel;
    }

    protected EnumMap<TreeTypeEnum, StackPanel> getStackPanels() {
        EnumMap<TreeTypeEnum, StackPanel> panels =
                new EnumMap<TreeTypeEnum, StackPanel>(TreeTypeEnum.class);
        panels.put(TreeTypeEnum.DEVICE_TYPES_TREE, new StackPanel(
                TreeTypeEnum.DEVICE_TYPES_TREE, createTree(), this));
        panels.put(TreeTypeEnum.DEVICE_GROUPS_TREE, new StackPanel(
                TreeTypeEnum.DEVICE_GROUPS_TREE, createTree(), this));
        panels.put(TreeTypeEnum.VIRTUAL_FABRICS_TREE, new StackPanel(
                TreeTypeEnum.VIRTUAL_FABRICS_TREE, createTree(), this));
        // panels.put(TreeTypeEnum.TOP_10_CONGESTED_TREE, new StackPanel(
        // TreeTypeEnum.TOP_10_CONGESTED_TREE, null));
        return panels;
    }

    protected JTree createTree() {
        JTree res = new JTree();
        res.setModel(null);
        return res;
    }

    @Override
    public void stackChange(TreeTypeEnum stack) {
        for (TreeTypeEnum id : stackPanels.keySet()) {
            if (id != stack) {
                stackPanels.get(id).close();
            }
        }
    }

    protected void showNode(FVResourceNode node) {
        mlblStatus.setText("You have selected node: " + node);
    }

    @Override
    public void addTreeSelectionListener(TreeSelectionListener treeListener) {
        for (TreeTypeEnum id : stackPanels.keySet()) {
            stackPanels.get(id).addTreeListener(treeListener);
        }
    }

    /**
     *
     * Description: The derived class returns its main component which is put on
     * the right side of the main split pane
     *
     */
    protected abstract JComponent getMainComponent();

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.trees.TreeViewInterface#setViewSize(java.awt.
     * Dimension )
     */
    @Override
    public void setViewSize(Dimension pSize) {
        this.setPreferredSize(pSize);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.trees.TreeViewInterface#getMainPanel()
     */
    @Override
    public JPanel getMainPanel() {
        return this;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.trees.TreeViewInterface#setTreeModel(com.intel.hpc
     * .stl.ui.trees.TreeTypeEnum, com.intel.hpc.stl.ui.trees.FVResourceNode)
     */
    @Override
    public void setTreeModel(final TreeTypeEnum pTreeType,
            final FVTreeModel pModel) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                getStackPanel(pTreeType).setTreeModel(pModel);
            }
        });
    } // setTreeModel

    @Override
    public void setTreeSelection(final TreeTypeEnum pTreeType) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                StackPanel sp = getStackPanel(pTreeType);
                sp.open();
            }
        });
    }

    @Override
    public void setTreeSelection(final TreeTypeEnum pTreeType,
            final int index) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                StackPanel sp = getStackPanel(pTreeType);
                sp.open();
                sp.select(index);
            }
        });
    }

    protected StackPanel getStackPanel(TreeTypeEnum type) {
        StackPanel sp = stackPanels.get(type);
        if (sp != null) {
            return sp;
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find StackPanel for " + type);
        }
    }

    public void setTreeSelection(final FVTreeModel model,
            final TreePath[] paths) {
        final boolean[] isExpanded = new boolean[paths.length];
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                for (StackPanel sp : stackPanels.values()) {
                    if (sp.getTreeModel() == model) {
                        sp.select(paths, isExpanded);
                    }
                }
            }
        });
    }

    public void clearTreeSelection(final FVTreeModel model) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                for (StackPanel sp : stackPanels.values()) {
                    if (sp.getTreeModel() == model) {
                        sp.clearSelection();
                    }
                }
            }
        });
    }

    public void collapseTreePath(FVTreeModel model, TreePath path) {
        for (StackPanel sp : stackPanels.values()) {
            if (sp.getTreeModel() == model) {
                sp.collapse(path);
            }
        }
    }

    public void expandTreePath(FVTreeModel model, TreePath path) {
        for (StackPanel sp : stackPanels.values()) {
            if (sp.getTreeModel() == model) {
                sp.expand(path);
            }
        }
    }

    @Override
    public void expandAndSelectTreePath(FVTreeModel model, TreePath[] paths,
            boolean[] isExpanded) {
        for (StackPanel sp : stackPanels.values()) {
            if (sp.getTreeModel() == model) {
                if (!sp.isOpened()) {
                    sp.open();
                }
                sp.select(paths, isExpanded);
            } else {
                sp.close();
            }
        }
    }

    public void ensureSelectionVisible(final FVTreeModel model) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                for (StackPanel sp : stackPanels.values()) {
                    if (sp.getTreeModel() == model) {
                        sp.ensureSelectionVisible();
                    }
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.monitor.view.TreeViewInterface#setSelectionMode(int)
     */
    @Override
    public void setSelectionMode(int selectionMode) {
        for (StackPanel sp : stackPanels.values()) {
            sp.setSelectionMode(selectionMode);
        }
    }

    @Override
    public void clear() {
        setTreeSelection(TreeTypeEnum.DEVICE_TYPES_TREE, 0);
        stackChange(TreeTypeEnum.DEVICE_TYPES_TREE);
    }

} // TreeView
