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
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXTaskPane;

import com.intel.stl.ui.common.IBackgroundService;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IntelTaskPaneUI;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.TopGraph;
import com.intel.stl.ui.network.view.TopologyOutlinePanel.Fit;
import com.mxgraph.model.mxCell;

public class TopologyGuideView extends JPanel {
    private static final long serialVersionUID = -2726807929262245620L;

    private JXTaskPane resourcesPane;

    private JPanel resourcesPanel;

    private JXTaskPane overviewPane;

    private TopologyOutlinePanel graphOutline;

    private JXHyperlink enlargeBtn;

    private JFrame popupWindow;

    private Rectangle popupBound;

    private int popupState;

    private TopologyOutlinePanel popupOutline;

    private TopGraph graph;

    private final PropertyVizStyle style;

    private final IBackgroundService updateService;

    private boolean dirtyPopup;

    public TopologyGuideView(IBackgroundService updateService) {
        super(new BorderLayout(2, 5));
        this.updateService = updateService;

        setBackground(UIConstants.INTEL_WHITE);
        JXTaskPane task = getOverviewPane();
        add(task, BorderLayout.NORTH);
        task = getResourcesPane();
        add(task, BorderLayout.CENTER);

        style = new PropertyVizStyle(false, true);
    }

    /**
     * @return the updateService
     */
    public IBackgroundService getUpdateService() {
        return updateService;
    }

    protected JXTaskPane getOverviewPane() {
        if (overviewPane == null) {
            overviewPane =
                    new JXTaskPane(STLConstants.K1060_OUTLINE.getValue());
            overviewPane.setUI(new IntelTaskPaneUI());

            Container content = overviewPane.getContentPane();
            content.setLayout(new BorderLayout());

            graphOutline = new TopologyOutlinePanel(updateService, Fit.WIDTH);
            graphOutline.setBackground(UIConstants.INTEL_WHITE);
            graphOutline.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(UIConstants.INTEL_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            content.add(graphOutline, BorderLayout.CENTER);

            enlargeBtn =
                    new JXHyperlink(new AbstractAction(
                            STLConstants.K1061_ENLARGE.getValue()) {
                        private static final long serialVersionUID =
                                -4493041735258083928L;

                        @Override
                        public void actionPerformed(ActionEvent e) {
                            if (graph != null) {
                                showOutlineWindow();
                            }
                        }

                    });
            enlargeBtn.setHorizontalAlignment(JButton.TRAILING);
            enlargeBtn.setUnclickedColor(UIConstants.INTEL_BLUE);
            content.add(enlargeBtn, BorderLayout.SOUTH);
        }
        return overviewPane;
    }

    protected JXTaskPane getResourcesPane() {
        if (resourcesPane == null) {
            resourcesPane =
                    new JXTaskPane(STLConstants.K1062_SEL_RESOURCES.getValue());
            resourcesPane.setUI(new IntelTaskPaneUI());
            // turned off animation because it has some issues with JScrollPane
            // under our current BorderLayout. Basically we use BorderLayout
            // because we want the <code>resourcePane</code> takes all the
            // remainder space. But the animation intends to specify
            // resourcePane's size (fixed size). The confliction between the the
            // LayoutManager and the animation causes issues. So we turned
            // animation off here.
            resourcesPane.setAnimated(false);

            JPanel content = (JPanel) resourcesPane.getContentPane();
            content.setLayout(new BorderLayout());
            resourcesPanel = new JPanel();
            resourcesPanel.setBackground(UIConstants.INTEL_WHITE);
            resourcesPanel.setBorder(BorderFactory.createMatteBorder(2, 2, 2,
                    2, UIConstants.INTEL_BACKGROUND_GRAY));
            resourcesPanel.setLayout(new GridBagLayout());
            JScrollPane listScroller = new JScrollPane(resourcesPanel);
            listScroller.setBorder(null);
            content.add(listScroller, BorderLayout.CENTER);
        }
        return resourcesPane;
    }

    protected JFrame getPopupWindow() {
        if (popupWindow == null) {
            popupWindow = new JFrame(STLConstants.K1063_TOP_OUTLINE.getValue());
            Image[] images =
                    new Image[] { UIImages.LOGO_24.getImage(),
                            UIImages.LOGO_32.getImage(),
                            UIImages.LOGO_64.getImage(),
                            UIImages.LOGO_128.getImage() };
            popupWindow.setIconImages(Arrays.asList(images));
            popupWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            popupWindow.addComponentListener(new ComponentAdapter() {

                /*
                 * (non-Javadoc)
                 * 
                 * @see
                 * java.awt.event.ComponentAdapter#componentResized(java.awt
                 * .event.ComponentEvent)
                 */
                @Override
                public void componentResized(ComponentEvent e) {
                    popupBound = popupWindow.getBounds();
                    popupState = popupWindow.getExtendedState();
                }

                /*
                 * (non-Javadoc)
                 * 
                 * @see
                 * java.awt.event.ComponentAdapter#componentMoved(java.awt.event
                 * .ComponentEvent)
                 */
                @Override
                public void componentMoved(ComponentEvent e) {
                    popupBound = popupWindow.getBounds();
                    popupState = popupWindow.getExtendedState();
                }

            });
            JPanel content = (JPanel) popupWindow.getContentPane();
            content.setLayout(new BorderLayout());
            content.setBackground(UIConstants.INTEL_WHITE);
            content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            popupOutline = new TopologyOutlinePanel(updateService, Fit.WINDOW);
            popupOutline.setBackground(UIConstants.INTEL_WHITE);
            popupOutline.setPreferredSize(new Dimension(800, 600));
            popupOutline.setMinimumSize(new Dimension(400, 300));
            popupOutline.setToolTipText("");
            popupOutline.setGraph(graph);
            content.add(popupOutline, BorderLayout.CENTER);

            popupWindow.pack();
        }
        return popupWindow;
    }

    public void setGraph(TopGraph graph) {
        graphOutline.setGraph(graph);
        if (popupOutline != null) {
            popupOutline.setGraph(graph);
            dirtyPopup = false;
        }
        this.graph = graph;
    }

    /**
     * <i>Description:</i> clear buffered image, so we will redraw it
     * 
     */
    public void updateGraph() {
        graphOutline.updateImage();
        if (popupOutline != null) {
            if (popupWindow.isVisible()) {
                popupOutline.updateImage();
                dirtyPopup = false;
            } else {
                dirtyPopup = true;
            }
        }
    }

    public void showOutlineWindow() {
        JFrame frame = getPopupWindow();
        if (frame.isVisible()) {
            if (popupBound != null) {
                frame.setBounds(popupBound);
            }
            frame.setExtendedState(popupState);
            frame.toFront();
        } else {
            frame.setLocationRelativeTo(SwingUtilities.getRoot(this));
            frame.setVisible(true);
        }
        if (dirtyPopup) {
            popupOutline.setImage(graphOutline.getImage());
            popupOutline.updateImage();
            dirtyPopup = false;
        }
    }

    public void selectNodes(mxCell[] cells) {
        Set<String> lids = new HashSet<String>();
        if (cells != null) {
            for (mxCell cell : cells) {
                lids.add(cell.getId());
            }
        }
        if (hasChanges(lids)) {
            graph.setSelectionCells(cells);
            graph.refresh();
        }
    }

    protected boolean hasChanges(Set<String> cells) {
        // graphSelections is always not null
        Object[] graphSelections = graph.getSelectionCells();

        if (cells == null) {
            return graphSelections.length != 0;
        }
        if (graphSelections.length != cells.size()) {
            return true;
        }

        for (int i = 0; i < graphSelections.length; i++) {
            mxCell graphCell = (mxCell) graphSelections[i];
            if (!cells.contains(graphCell.getId())) {
                return true;
            }
        }
        return false;
    }

    public void selectConnections(Collection<mxCell> cells, boolean select,
            ICancelIndicator indicator) {
        graph.markConnections(cells, select, indicator);
    }

    public void selectEdges(Collection<mxCell> cells, boolean select) {
        graph.markEdges(cells, select);
    }

    public void clearSelection() {
        graph.clearSelection();
    }

    public void clearEdges() {
        graph.clearMarkedEdges();
    }

    public void setSelectedResources(FVResourceNode[] resources) {
        resourcesPanel.removeAll();
        if (resources == null || resources.length == 0) {
            return;
        }

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        gc.insets = new Insets(0, 2, 0, 2);
        for (int i = 0; i < resources.length; i++) {
            FVResourceNode resource = resources[i];
            JLabel label =
                    ComponentFactory.getH4Label(getResoureName(resource),
                            Font.PLAIN);
            label.setIcon(resource.getType().getIcon());
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                    UIConstants.INTEL_BORDER_GRAY));
            style.decorateKey(label, i);
            resourcesPanel.add(label, gc);
        }
        gc.weighty = 1;
        gc.fill = GridBagConstraints.BOTH;
        resourcesPanel.add(Box.createGlue(), gc);
    }

    protected String getResoureName(FVResourceNode resource) {
        if (resource == null) {
            return "";
        }
        if (resource.isPort()) {
            return resource.getParent().getName() + ":" + resource.getName();
        } else {
            return resource.getName();
        }
    }

    public void initView() {
        graphOutline.initView();
    }
}
