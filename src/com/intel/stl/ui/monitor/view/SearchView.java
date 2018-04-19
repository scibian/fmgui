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
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.EnumMap;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.ScrollPaneConstants;
import javax.swing.ScrollPaneLayout;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.basic.BasicProgressBarUI;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IntelComboBoxUI;
import com.intel.stl.ui.common.view.SafeNumberField.SafeNumberFormatter;
import com.intel.stl.ui.common.view.SafeTextField;
import com.intel.stl.ui.common.view.SafeTextField.SafeStringFormatter;
import com.intel.stl.ui.monitor.ISearchListener;
import com.intel.stl.ui.monitor.TreeSearchType;
import com.intel.stl.ui.monitor.TreeTypeEnum;
import com.intel.stl.ui.monitor.tree.FVTreeModel;

public class SearchView extends JPanel implements ISearchView, IStack {

    /**
     * 
     */
    private static final long serialVersionUID = -7812859211672953591L;

    private JComboBox<TreeSearchType> searchTypeList;

    private SafeTextField searchTextField;

    private AbstractFormatter nameInputFormatter;

    private AbstractFormatter lidInputFormatter;

    private AbstractFormatter guidInputFormatter;

    private ActionListener searchTypeListener;

    private JButton searchBtn;

    private ISearchListener searchListener;

    private JScrollPane resultsTree;

    private EnumMap<TreeTypeEnum, StackPanel> stackPanels;

    private JLabel messageLabel;

    private JPanel progressPanel;

    private JLabel running;

    private JProgressBar progressBar;

    public SearchView() {

        initComponents();
    }

    private void initComponents() {
        setBackground(UIConstants.INTEL_WHITE);
        setLayout(new BorderLayout());

        // Create the tree search panel
        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new GridBagLayout());
        searchPanel.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc1 = new GridBagConstraints();
        gc1.weightx = 0;

        searchTypeList = new JComboBox<TreeSearchType>(TreeSearchType.values());
        IntelComboBoxUI ui = new IntelComboBoxUI() {

            /*
             * (non-Javadoc)
             * 
             * @see com.intel.stl.ui.common.view.IntelComboBoxUI#getValueString
             * (java.lang.Object)
             */
            @Override
            protected String getValueString(Object value) {
                return ((TreeSearchType) value).getName();
            }

            /*
             * (non-Javadoc)
             * 
             * @see com.intel.stl.ui.common.view.IntelComboBoxUI#getValueTooltip
             * (java.lang.Object)
             */
            @Override
            protected String getValueTooltip(Object value) {
                return ((TreeSearchType) value).getName();
            }

        };
        ui.setEditorBorder(BorderFactory.createEmptyBorder());
        ui.setArrowButtonTooltip(UILabels.STL10103_MORE_SELECTIONS
                .getDescription());
        ui.setArrowButtonBorder(null);
        searchTypeList.setUI(ui);

        searchPanel.add(searchTypeList, gc1);

        gc1.fill = GridBagConstraints.HORIZONTAL;
        gc1.insets = new Insets(3, 2, 5, 2);
        gc1.weighty = 0;
        gc1.weightx = 1;
        gc1.gridwidth = 1;
        searchTextField = new SafeTextField(true);
        searchTextField.setValidChars(UIConstants.NODE_DESC_CHARS + "_");
        searchTextField.setText(STLConstants.K0115_TREE_SEARCH.getValue());
        searchTextField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                searchTextField.selectAll();
            }
        });

        nameInputFormatter = searchTextField.getFormatter();
        ((SafeStringFormatter) nameInputFormatter).setParent(searchTextField);
        searchPanel.add(searchTextField, gc1);

        gc1.weightx = 0;
        gc1.gridwidth = GridBagConstraints.REMAINDER;
        searchBtn =
                ComponentFactory.getImageButton(UIImages.SEARCH.getImageIcon());
        searchBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Cancelled
                if (searchBtn.isSelected()) {
                    searchBtn.setEnabled(false);
                    searchListener.cancel();
                } else {
                    // Search
                    searchTypeList.setEnabled(false);
                    searchBtn.setIcon(UIImages.STOP_RED.getImageIcon());
                    searchBtn.setSelected(true);
                    messageLabel.setVisible(false);
                    progressPanel.setVisible(true);
                    resultsTree.setVisible(false);
                    setProgress(0);
                    setProgressNote("");
                    searchListener.setSearchValue(searchTextField.getText());
                    searchListener.searchTree();
                }
            }
        });
        // Add a mouse listener to ensure the Apply button gets the focus
        searchBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).requestFocusInWindow();
            }
        });
        searchBtn.requestFocusInWindow();

        searchBtn.setToolTipText(STLConstants.K0115_TREE_SEARCH.getValue());
        searchPanel.add(searchBtn, gc1);

        // Progress Bar and cancel button
        progressPanel = new JPanel(new BorderLayout());
        progressPanel.setOpaque(true);
        progressPanel.setBackground(UIConstants.INTEL_WHITE);
        progressPanel.setBorder(BorderFactory.createLoweredBevelBorder());
        running = new JLabel();
        running.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        progressPanel.add(running, BorderLayout.WEST);

        progressBar = new JProgressBar(0, 100);
        progressBar.setUI(new BasicProgressBarUI());
        progressBar.setBorderPainted(false);
        progressBar.setStringPainted(true);
        progressBar.setIndeterminate(false);
        progressPanel.add(progressBar, BorderLayout.CENTER);

        gc1.weightx = 0;
        gc1.gridwidth = GridBagConstraints.REMAINDER;

        progressPanel.setVisible(false);
        searchPanel.add(progressPanel, gc1);

        messageLabel = ComponentFactory.getH4Label("", Font.BOLD);
        messageLabel.setHorizontalAlignment(JLabel.CENTER);
        messageLabel.setVisible(false);
        searchPanel.add(messageLabel, gc1);
        add(searchPanel, BorderLayout.NORTH);

        // Create the tree panel
        JPanel mPnlTree = new JPanel(new GridBagLayout());
        mPnlTree.setBackground(UIConstants.INTEL_WHITE);
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        stackPanels = getStackPanels();
        for (StackPanel stackPanel : stackPanels.values()) {
            stackPanel.setVisible(false);
            mPnlTree.add(stackPanel, gc);
        }
        gc.weighty = 1;
        mPnlTree.add(Box.createGlue(), gc);

        // Create a scroll pane for the tree
        resultsTree = new JScrollPane();
        ScrollPaneLayout spTreeLayout = new ScrollPaneLayout();
        resultsTree.createHorizontalScrollBar();
        resultsTree.createVerticalScrollBar();
        spTreeLayout
                .setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        spTreeLayout
                .setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        resultsTree.getVerticalScrollBar().setUnitIncrement(10);
        resultsTree.setLayout(spTreeLayout);

        resultsTree.add(mPnlTree);
        resultsTree.setViewportView(mPnlTree);
        resultsTree.setVisible(false);

        add(resultsTree, BorderLayout.CENTER);
    }

    public void enableSearch() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                searchBtn.setEnabled(true);
                searchTypeList.setEnabled(true);
                setRunning(false);

                progressPanel.setVisible(false);
            }
        });
    }

    public void setRunning(boolean b) {
        if (!b) {
            searchBtn.setSelected(b);
            searchBtn.setIcon(UIImages.SEARCH.getImageIcon());
        }
        running.setIcon(b == true ? UIImages.RUNNING.getImageIcon() : null);
    }

    public void setProgress(int progress) {
        progressBar.setValue(progress);
    }

    public void setProgressNote(String note) {
        if (note != null) {
            progressBar.setString(note);
        }
    }

    public void showMessage(String msg) {
        messageLabel.setVisible(true);
        messageLabel.setText(msg);
        resultsTree.setVisible(false);
    }

    public void addSearchListener(ISearchListener listener) {
        this.searchListener = listener;
        if (searchTypeList == null || listener == null) {
            return;
        }

        if (searchTypeListener != null) {
            searchTypeList.removeActionListener(searchTypeListener);
        }
        searchTypeListener = new ActionListener() {

            @SuppressWarnings("rawtypes")
            @Override
            public void actionPerformed(ActionEvent e) {

                searchTextField.setBackground(UIConstants.INTEL_WHITE);
                searchTextField.setToolTipText(null);
                showTree(false);
                progressPanel.setVisible(false);
                messageLabel.setVisible(false);

                String text = searchTextField.getText();
                Object value = null;
                searchTextField.setValue(null);

                TreeSearchType type =
                        (TreeSearchType) searchTypeList.getSelectedItem();
                switch (type) {
                    case NAME:
                        searchTextField.setFixedFormatter(nameInputFormatter);
                        searchTextField.setValue(text);
                        break;
                    case LID:
                        if (lidInputFormatter == null) {
                            lidInputFormatter =
                                    new SafeNumberFormatter<Integer>(
                                            new DecimalFormat("###"), 0, true,
                                            Integer.MAX_VALUE, true);
                            ((SafeNumberFormatter) lidInputFormatter)
                                    .setParent(searchTextField);
                        }

                        try {
                            value = lidInputFormatter.stringToValue(text);
                        } catch (Exception ee) {
                            // ee.printStackTrace();
                        }
                        searchTextField.setFixedFormatter(lidInputFormatter);
                        searchTextField.setValue(value);
                        break;
                    case GUID:
                        if (guidInputFormatter == null) {
                            SafeStringFormatter formatter =
                                    new SafeStringFormatter(true, 18);
                            formatter.setValidCharacters(UIConstants.HEX_CHARS);
                            formatter.setParent(searchTextField);
                            guidInputFormatter = formatter;
                        }

                        try {
                            value = guidInputFormatter.stringToValue(text);
                        } catch (Exception ee) {
                            // ee.printStackTrace();
                        }
                        searchTextField.setFixedFormatter(guidInputFormatter);
                        searchTextField.setValue(value);
                        break;
                    default:
                        break;

                }
                searchListener.setSearchType(type);
            }

        };

        searchTypeList.addActionListener(searchTypeListener);
        searchTypeList.setSelectedItem(TreeSearchType.NAME);
        searchTextField.setText(STLConstants.K0115_TREE_SEARCH.getValue());
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
        return panels;
    }

    protected StackPanel getStackPanel(TreeTypeEnum type) {
        StackPanel sp = stackPanels.get(type);
        if (sp != null) {
            return sp;
        } else {
            throw new IllegalArgumentException("Couldn't find StackPanel for "
                    + type);
        }
    }

    protected JTree createTree() {
        JTree res = new JTree();
        res.setModel(null);
        res.setRootVisible(false);
        return res;
    }

    @Override
    public void stackChange(final TreeTypeEnum stack) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                for (TreeTypeEnum treeType : stackPanels.keySet()) {
                    if (treeType != stack) {
                        StackPanel panel = getStackPanel(treeType);
                        panel.close();
                        panel.clearSelection();
                    }
                }
            }
        });

    }

    @Override
    public void setTreeModel(final TreeTypeEnum treeType,
            final FVTreeModel pModel, final int nodeCount) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                StackPanel panel = getStackPanel(treeType);
                panel.setVisible(true);
                panel.setTreeModel(pModel);
                panel.setSearchCount(nodeCount);
            }
        });
    }

    @Override
    public void addTreeSelectionListener(TreeSelectionListener listener) {
        for (TreeTypeEnum id : stackPanels.keySet()) {
            stackPanels.get(id).addTreeListener(listener);
        }
    }

    @Override
    public void setSelectionMode(int selectionMode) {
        for (StackPanel sp : stackPanels.values()) {
            sp.setSelectionMode(selectionMode);
        }
    }

    @Override
    public void showTree(final boolean b) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                resultsTree.setVisible(b);
                validate();
                repaint();
            }
        });
    }
}
