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

package com.intel.stl.ui.network.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.ListCellRenderer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jdesktop.swingx.JXList;
import org.jdesktop.swingx.rollover.RolloverProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ButtonPopup;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.network.ResourceLinkPage;

/**
 * JCardView to display tabbed pages when links are selected on the topology
 * graph
 */
public class ResourceLinkSubpageView extends JSectionView<ISectionListener>
        implements ChangeListener {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = -8162278424319448619L;

    private static Logger log =
            LoggerFactory.getLogger(ResourceLinkSubpageView.class);

    private static final int MAX_TABS = 5;

    private JTabbedPane tabbedPane;

    private IntelTabbedPaneUI tabUI;

    private JButton moreBtn;

    private PopupPanel popupPanel;

    private ButtonPopup popup;

    private IPageListener listener;

    private String currentTab;

    public ResourceLinkSubpageView(String title, Icon icon) {
        super(title, icon);
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.view.JCardView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {

        if (tabbedPane != null) {
            return tabbedPane;
        }

        // Create the tabbed pane which will be populated when getMainComponent
        // is called from subpages
        tabbedPane = new JTabbedPane();
        tabUI = new IntelTabbedPaneUI();
        tabbedPane.setUI(tabUI);
        tabUI.setFont(UIConstants.H4_FONT);
        tabUI.setTabAreaInsets(new Insets(2, 5, 4, 5));

        JPanel ctrPanel = tabUI.getControlPanel();
        installMoreButton(ctrPanel);

        // Add change listener to highlight the tabs
        tabbedPane.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                // Highlight the tabs depending on selection
                Runnable highlightTabs = new Runnable() {
                    @Override
                    public void run() {
                        if (tabbedPane.getSelectedIndex() >= 0) {
                            highlightTabs();
                        }
                    }
                };
                Util.runInEDT(highlightTabs);
            }
        });

        return tabbedPane;
    }

    protected void installMoreButton(JPanel ctrPanel) {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        moreBtn = new JButton(STLConstants.K0036_MORE.getValue(),
                UIImages.DOWN_ICON.getImageIcon()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                setForeground(
                        b ? UIConstants.INTEL_BLUE : UIConstants.INTEL_GRAY);
            }
        };
        moreBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (popup.isVisible()) {
                    popup.hide();
                } else {
                    popup.show();
                }
            }
        });
        toolBar.add(moreBtn);
        ctrPanel.add(toolBar);

        popupPanel = new PopupPanel();
        popup = new ButtonPopup(moreBtn, popupPanel) {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {
            }

        };
    }

    public String getCurrentSubpage() {
        int currentTab = tabbedPane.getSelectedIndex();
        if (currentTab < 0) {
            return null;
        } else {
            return tabbedPane.getTitleAt(currentTab);
        }
    }

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

    public synchronized void setTabs(ResourceLinkPage[] subpages,
            String desiredSubpage) {
        tabbedPane.removeChangeListener(this);

        popupPanel.setItems(subpages);

        // remove all old tabs
        tabbedPane.removeAll();
        moreBtn.setEnabled(subpages.length > MAX_TABS);

        for (int i = 0; i < subpages.length; i++) {
            ResourceLinkPage page = subpages[i];
            if (i < MAX_TABS) {
                addTab(page);
            }
        }

        // Set the selected tab
        if (popupPanel.getPage(desiredSubpage) != null) {
            setCurrentSubpage(desiredSubpage);
        } else if (tabbedPane.getTabCount() > 0) {
            tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        }
        int index = tabbedPane.getSelectedIndex();
        currentTab = tabbedPane.getTitleAt(index);

        // Highlight the tabs
        highlightTabs();
        tabbedPane.addChangeListener(this);
    }

    protected void addTab(ResourceLinkPage page) {
        String title = page.getName();
        String[] nodeNames = title.split(",");
        tabbedPane.addTab(title, page.getIcon(), page.getView(),
                page.getDescription());
        popupPanel.addSelection(title);
        if (tabbedPane.getTabCount() > MAX_TABS) {
            popupPanel.removeSelection(tabbedPane.getTitleAt(0));
            tabbedPane.remove(0);
        }
        tabbedPane.setSelectedIndex(tabbedPane.getTabCount() - 1);
        updateTab(title, nodeNames);
    }

    protected boolean selectTab(ResourceLinkPage page) {
        String name = page.getName();
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            String title = tabbedPane.getTitleAt(i);
            if (title.equals(name)) {
                tabbedPane.setSelectedIndex(i);
                return true;
            }
        }
        return false;
    }

    public void updateTab(String title, String[] nodeNames) {

        ResourceLinkTabView tab = new ResourceLinkTabView(nodeNames);
        String subpageTitle = getCurrentSubpage();

        if (subpageTitle != null) {
            boolean highlight = (subpageTitle.equals(title));
            tab.setLabelProperties(highlight);
            tabbedPane.setTabComponentAt(tabbedPane.indexOfTab(title), tab);
        } else {
            log.error(STLConstants.K3046_LINK_SUBPAGE_NULL.getValue());
        }
    }

    public void highlightTabs() {
        // Loop through all the tabs and highlight or unhighlight depending
        // on whether it is the selected tab
        for (int i = 0; i < tabbedPane.getTabCount(); i++) {
            boolean highlight = (i == tabbedPane.getSelectedIndex());
            ResourceLinkTabView tabView =
                    (ResourceLinkTabView) tabbedPane.getTabComponentAt(i);
            if (tabView != null) {
                tabView.setLabelProperties(highlight);
            }
        }
    }

    public void setCurrentSubpage(String name) {
        ResourceLinkPage page = popupPanel.getPage(name);
        if (page != null) {
            if (!selectTab(page)) {
                addTab(page);
            }
        }
    }

    @SuppressWarnings("rawtypes")
    class PopupPanel extends JPanel implements ListCellRenderer {
        private static final long serialVersionUID = 2314564428422180815L;

        private DefaultListModel model;

        private JXList list;

        private int highlightedRow = -1;

        private final Set<String> selections = new HashSet<String>();

        public PopupPanel() {
            super();
            initComponents();
        }

        protected void initComponents() {
            setLayout(new BorderLayout());

            model = new DefaultListModel();
            list = new JXList(model);
            list.setVisibleRowCount(10);
            list.setCellRenderer(this);
            list.setRolloverEnabled(true);
            // list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
            list.addPropertyChangeListener(RolloverProducer.ROLLOVER_KEY,
                    new PropertyChangeListener() {

                        @Override
                        public void propertyChange(PropertyChangeEvent evt) {
                            Point location = (Point) evt.getNewValue();
                            if (location != null) {
                                highlightedRow = location.y;
                            } else {
                                highlightedRow = -1;
                            }
                        }

                    });
            list.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent e) {
                    popup.hide();
                    ResourceLinkPage page =
                            (ResourceLinkPage) list.getSelectedValue();
                    if (page != null) {
                        if (!selectTab(page)) {
                            addTab(page);
                        }
                    }
                }
            });

            JScrollPane scroll = new JScrollPane(list);
            scroll.getViewport().getView()
                    .setBackground(UIConstants.INTEL_WHITE);
            add(scroll, BorderLayout.CENTER);
        }

        @SuppressWarnings("unchecked")
        public void setItems(ResourceLinkPage[] pages) {
            clear();
            for (ResourceLinkPage page : pages) {
                model.addElement(page);
            }
            list.setVisibleRowCount(Math.min(10, pages.length));
        }

        public ResourceLinkPage getPage(String name) {
            for (int i = 0; i < model.getSize(); i++) {
                ResourceLinkPage page =
                        (ResourceLinkPage) model.getElementAt(i);
                if (page.getName().equals(name)) {
                    return page;
                }
            }
            return null;
        }

        public void addSelection(String name) {
            selections.add(name);
        }

        public void removeSelection(String name) {
            selections.remove(name);
        }

        public void clear() {
            model.clear();
            selections.clear();
        }

        /*
         * (non-Javadoc)
         *
         * @see
         * javax.swing.ListCellRenderer#getListCellRendererComponent(javax.swing
         * .JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            ResourceLinkPage page = (ResourceLinkPage) value;
            String[] nodeNames = page.getName().split(",");
            final ResourceLinkTabView view = new ResourceLinkTabView(nodeNames);
            view.setOpaque(true);
            view.setBorder(BorderFactory.createEmptyBorder(4, 4, 2, 2));
            if (index == highlightedRow) {
                view.setBackground(UIConstants.INTEL_DARK_GREEN);
                view.setLabelProperties(false);
            } else if (selections.contains(page.getName())) {
                view.setBackground(UIConstants.INTEL_BLUE);
                view.setLabelProperties(false);
            } else if (index % 2 == 0) {
                // view.setBackground(UIConstants.INTEL_PALE_BLUE);
                // view.setLabelProperties(true);
                // } else {
                view.setBackground(UIConstants.INTEL_WHITE);
                view.setLabelProperties(true);
            }
            view.setToolTipText(page.getDescription());
            return view;
        }
    }

    public void clear() {
        tabbedPane.removeChangeListener(this);
        tabbedPane.removeAll();
        tabbedPane.addChangeListener(this);
        moreBtn.setEnabled(false);
    }
}
