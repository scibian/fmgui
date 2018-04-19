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
import java.awt.Insets;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.tree.TreeSelectionModel;

import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.monitor.TreeNodeType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class PerformanceTreeView extends TreeView implements IPerformanceView,
        ChangeListener {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -4312903533516795863L;

    /**
     * Custom tabbed pane
     */
    private JTabbedPane tabbedPane;

    private IntelTabbedPaneUI tabUI;

    private JPanel ctrPanel;

    private final JLabel lblNodeName = new JLabel("");

    private IPageListener listener;

    private String currentTab = null;

    /**
     * 
     * Description: Constructor for the PerformanceView class
     * 
     */
    public PerformanceTreeView() {
        super(null, null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.view.TreeView#createTree()
     */
    @Override
    protected JTree createTree() {
        JTree tree = new JTree();
        tree.getSelectionModel().setSelectionMode(
                TreeSelectionModel.SINGLE_TREE_SELECTION);
        return super.createTree();
    }

    @Override
    protected JComponent getMainComponent() {
        if (tabbedPane != null) {
            return tabbedPane;
        }

        // Create the tabbed pane which will be populated when getMainComponent
        // is called from subpages
        tabbedPane = new JTabbedPane();
        tabUI = new IntelTabbedPaneUI();
        ctrPanel = tabUI.getControlPanel();
        ctrPanel.setLayout(new BorderLayout());
        ctrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 5));
        tabbedPane.setUI(tabUI);
        tabUI.setFont(UIConstants.H4_FONT);
        tabUI.setTabAreaInsets(new Insets(2, 5, 4, 5));
        return tabbedPane;
    }

    @Override
    public JComponent getView() {
        return this;
    }

    @Override
    public void setNodeName(FVResourceNode node) {
        String name =
                new String(node.isPort() ? node.getParent().getName() + ":"
                        + node.getName() : node.getName());
        lblNodeName.setText(name);
        lblNodeName.setForeground(UIConstants.INTEL_DARK_GRAY);
        lblNodeName.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
        ctrPanel.add(lblNodeName, BorderLayout.CENTER);
    }

    @Override
    public void setTabs(List<IPerfSubpageController> subpages, int selection) {
        tabbedPane.removeChangeListener(this);

        // remove all old tabs
        // add the view of each subpage to our tabbed pane
        tabbedPane.removeAll();

        for (IPerfSubpageController subpage : subpages) {
            tabbedPane.addTab(subpage.getName(), subpage.getIcon(),
                    subpage.getView(), subpage.getDescription());
        }

        tabbedPane.setSelectedIndex(selection > 0 ? selection : 0);
        int index = tabbedPane.getSelectedIndex();
        currentTab = tabbedPane.getTitleAt(index);
        tabbedPane.addChangeListener(this);
        return;
    }

    public String getCurrentSubpage() {
        int currentTab = tabbedPane.getSelectedIndex();
        if (currentTab < 0) {
            return null;
        } else {
            return tabbedPane.getTitleAt(currentTab);
        }
    }

    public void setCurrentSubpage(String name) {
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            if (tabbedPane.getTitleAt(i).equals(name)) {
                tabbedPane.setSelectedIndex(i);
                break;
            }
        }
    }

    public void clearPage(TreeNodeType nodeType) {
        String msg = new String("");
        tabbedPane.removeChangeListener(this);
        tabbedPane.removeAll();
        tabbedPane.addChangeListener(this);

        switch (nodeType) {

            case INACTIVE_PORT:
                msg = UILabels.STL40004_ERROR_INACTIVE_PORT.getDescription();
                break;

            case ALL:
            case HCA_GROUP:
            case SWITCH_GROUP:
            case ROUTER_GROUP:
            case DEVICE_GROUP:
            case VIRTUAL_FABRIC:
                msg = UILabels.STL40005_TREE_INFO_MSG.getDescription();
                break;

            default:
                break;

        }
        lblNodeName.setText(msg);
        lblNodeName.setForeground(UIConstants.INTEL_BLUE);
        lblNodeName.setFont(UIConstants.H2_FONT.deriveFont(Font.PLAIN));
        getMainPanel().revalidate();
    }

    /**
     * 
     * Description:
     * 
     * @param listener
     */
    public void setPageListener(final IPageListener listener) {
        this.listener = listener;
        tabbedPane.addChangeListener(this);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if (listener == null) {
            return;
        }

        // only fire onPageChanged when we have valid oldPageId and
        // newPageId
        String oldTab = currentTab;
        int index = tabbedPane.getSelectedIndex();
        currentTab = tabbedPane.getTitleAt(index);
        if (oldTab != null && currentTab != null) {
            listener.onPageChanged(oldTab, currentTab);
        }
    }

    public void setRunning(boolean isRunning) {
        if (lblNodeName != null) {
            lblNodeName.setIcon(isRunning ? UIImages.RUNNING.getImageIcon()
                    : null);
        }
    }

}
