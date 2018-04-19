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
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.Timer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.IBackgroundService;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IntelComboBoxUI;
import com.intel.stl.ui.model.GraphCells;
import com.intel.stl.ui.model.LayoutType;
import com.intel.stl.ui.network.ITopologyListener;
import com.intel.stl.ui.network.TopGraph;
import com.intel.stl.ui.network.TopGraphComponent;
import com.mxgraph.model.mxCell;
import com.mxgraph.util.mxEventObject;
import com.mxgraph.util.mxEventSource.mxIEventListener;
import com.mxgraph.util.mxRectangle;
import com.mxgraph.view.mxGraphView;

public class TopologyGraphView extends JPanel implements MouseWheelListener {
    private static final long serialVersionUID = -6403935305432509869L;

    private static final Logger log = LoggerFactory
            .getLogger(TopologyGraphView.class);

    private JPanel ctrPanel;

    private JToolBar toolbar;

    private JButton zoomInBtn;

    private JButton zoomOutBtn;

    private JButton fitWinBtn;

    private final boolean showExtraButtons = false;

    // extra toolbar buttons
    private JButton expandBtn;

    private JButton collapseBtn;

    private JButton undoBtn;

    private JButton redoBtn;

    private JButton resetBtn;

    private JLabel algorithmLabel;

    private JComboBox algorithmBox;

    // end of extra toolbar buttons

    private TopGraphComponent graphComp;

    private TopGraph graph;

    private ITopologyListener topListener;

    private boolean isFittingWindow = true;

    private Timer resizeTimer;

    private final Queue<Set<String>> selections = new LinkedList<Set<String>>();

    private final IBackgroundService updateService;

    /**
     * Description:
     * 
     */
    public TopologyGraphView(IBackgroundService updateService) {
        super(new BorderLayout());
        this.updateService = updateService;
        setOpaque(false);
        add(getControlPanel(), BorderLayout.NORTH);
    }

    /**
     * @return the updateService
     */
    public IBackgroundService getUpdateService() {
        return updateService;
    }

    protected JPanel getControlPanel() {
        if (ctrPanel == null) {
            ctrPanel = new JPanel(new BorderLayout());
            ctrPanel.setOpaque(false);

            toolbar = new JToolBar();
            toolbar.setFloatable(false);
            installNavButtons(toolbar);
            ctrPanel.add(toolbar, BorderLayout.WEST);

            if (showExtraButtons) {
                JPanel panel = createAlgorithmPanel();
                ctrPanel.add(panel, BorderLayout.EAST);
            }
        }
        return ctrPanel;
    }

    protected JPanel createAlgorithmPanel() {
        JPanel panel = new JPanel();
        algorithmLabel =
                ComponentFactory.getH4Label(
                        STLConstants.K1005_LAYOUT.getValue(), Font.PLAIN);
        panel.add(algorithmLabel);
        algorithmBox = new JComboBox();
        algorithmBox.setRenderer(new DefaultListCellRenderer() {
            private static final long serialVersionUID = 8785729990665992593L;

            @Override
            public Component getListCellRendererComponent(JList list,
                    Object value, int index, boolean isSelected,
                    boolean cellHasFocus) {
                LayoutType type = (LayoutType) value;
                JComponent comp =
                        (JComponent) super
                                .getListCellRendererComponent(list,
                                        type.getName(), index, isSelected,
                                        cellHasFocus);
                list.setToolTipText(type.getDescription());
                algorithmBox.setToolTipText(type.getDescription());
                return comp;
            }
        });
        algorithmBox.setUI(new IntelComboBoxUI());
        algorithmBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onLayoutTypeChange(algorithmBox
                            .getSelectedIndex());
                }
            }
        });
        algorithmBox.setEnabled(false);

        panel.add(algorithmBox);
        return panel;
    }

    protected void installNavButtons(JToolBar toolbar) {
        zoomInBtn = new JButton(// STLConstants.K1002_ZOOM_IN.getValue(),
                UIImages.ZOOM_IN_ICON.getImageIcon());
        zoomInBtn.setToolTipText(STLConstants.K1002_ZOOM_IN.getValue());
        zoomInBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        zoomInBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });
        zoomInBtn.setEnabled(false);
        toolbar.add(zoomInBtn);

        zoomOutBtn = new JButton(// STLConstants.K1003_ZOOM_OUT.getValue(),
                UIImages.ZOOM_OUT_ICON.getImageIcon());
        zoomOutBtn.setToolTipText(STLConstants.K1003_ZOOM_OUT.getValue());
        zoomOutBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        zoomOutBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });
        zoomOutBtn.setEnabled(false);
        toolbar.add(zoomOutBtn);

        fitWinBtn = new JButton(// STLConstants.K1004_FIT_WINDOW.getValue(),
                UIImages.FIT_WINDOW.getImageIcon());
        fitWinBtn.setToolTipText(STLConstants.K1004_FIT_WINDOW.getValue());
        fitWinBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        fitWinBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fitViewport();
            }
        });
        fitWinBtn.setEnabled(false);
        toolbar.add(fitWinBtn);

        if (showExtraButtons) {
            toolbar.addSeparator();
            installExtraNavButtons(toolbar);
        }
    }

    protected void installExtraNavButtons(JToolBar toolbar) {
        expandBtn = new JButton(// STLConstants.K1017_EXPAND_ALL.getValue(),
                UIImages.EXPAND_ALL.getImageIcon());
        expandBtn.setToolTipText(STLConstants.K1019_EXPAND_ALL.getValue());
        expandBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        expandBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onExpandAll();
                }
            }
        });
        expandBtn.setEnabled(false);
        toolbar.add(expandBtn);

        collapseBtn = new JButton(// STLConstants.K1018_COLLAPSE_ALL.getValue(),
                UIImages.COLLAPSE_ALL.getImageIcon());
        collapseBtn.setToolTipText(STLConstants.K1020_COLLAPSE_ALL.getValue());
        collapseBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        collapseBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onCollapseAll();
                }
            }
        });
        collapseBtn.setEnabled(false);
        toolbar.add(collapseBtn);

        toolbar.addSeparator();

        undoBtn = new JButton(// STLConstants.K1007_UNDO.getValue(),
                UIImages.UNDO.getImageIcon());
        undoBtn.setToolTipText(STLConstants.K1007_UNDO.getValue());
        undoBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        undoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onUndo();
                }
            }
        });
        undoBtn.setEnabled(false);
        toolbar.add(undoBtn);

        redoBtn = new JButton(// STLConstants.K1008_REDO.getValue(),
                UIImages.REDO.getImageIcon());
        redoBtn.setToolTipText(STLConstants.K1008_REDO.getValue());
        redoBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        redoBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onRedo();
                }
            }
        });
        redoBtn.setEnabled(false);
        toolbar.add(redoBtn);

        resetBtn = new JButton(// STLConstants.K1006_RESET.getValue(),
                UIImages.RESET.getImageIcon());
        resetBtn.setToolTipText(STLConstants.K1006_RESET.getValue());
        resetBtn.setForeground(UIConstants.INTEL_DARK_GRAY);
        resetBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (topListener != null) {
                    topListener.onReset();
                }
            }
        });
        resetBtn.setEnabled(false);
        toolbar.add(resetBtn);
    }

    /**
     * Description:
     * 
     * @param b
     */
    public void enableUndo(boolean b) {
        undoBtn.setEnabled(b);
    }

    public void enableRedo(boolean b) {
        redoBtn.setEnabled(b);
    }

    public void setTopologyListener(ITopologyListener listener) {
        this.topListener = listener;
    }

    /**
     * @return the graph
     */
    public TopGraph getGraph() {
        return graph;
    }

    public void setGraph(TopGraph graph) {
        // if (this.graph == graph) {
        // return;
        // }

        this.graph = graph;
        if (graphComp == null) {
            graphComp = getGraphComponent();
            graphComp.getViewport().setBackground(UIConstants.INTEL_WHITE);
            graphComp.getViewport().setOpaque(true);
            add(graphComp, BorderLayout.CENTER);
            enableAll(true);
        } else {
            graphComp.setGraph(graph);
        }
    }

    public TopGraphComponent getGraphComponent() {
        if (graphComp != null) {
            return graphComp;
        }

        graphComp = new TopGraphComponent(updateService, graph);
        graphComp.setToolTips(true);
        graphComp.setConnectable(false);
        // graphComp.setCenterZoom(false);

        graphComp.setSelectionListener(new mxIEventListener() {
            @Override
            @SuppressWarnings("unchecked")
            public void invoke(Object sender, mxEventObject evt) {
                Collection<mxCell> added =
                        (Collection<mxCell>) evt.getProperty("added");
                // Since this is the "Change" event, there is a flip on
                // the added and removed
                // (@see mxSelectionChange#execute line 386~389).
                // We flip them back here to get currently added and
                // removed cells.
                selectionChanged(added);
            }
        });

        graphComp.getGraphControl().addMouseWheelListener(this);

        resizeTimer =
                new Timer(UIConstants.UPDATE_TIME / 2, new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        if (graphComp != null && isFittingWindow
                                && !graphComp.isUserTranslated()) {
                            fitViewport();
                        }
                    }
                });
        resizeTimer.setRepeats(false);
        graphComp.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                resizeTimer.restart();
            }
        });

        return graphComp;
    }

    public void updateGraph() {
        graphComp.updateGraph();
    }

    public void setLayoutType(LayoutType type) {
        if (type != null && algorithmBox != null
                && algorithmBox.getSelectedItem() != type) {
            algorithmBox.setSelectedItem(type);
        }
    }

    public void showLayoutUpdating(boolean b) {
        if (algorithmLabel != null) {
            algorithmLabel.setIcon(b ? UIImages.RUNNING.getImageIcon() : null);
        }
    }

    public void setAvailableLayouts(LayoutType[] layouts) {
        if (algorithmBox != null) {
            ComboBoxModel mode = new DefaultComboBoxModel(layouts);
            algorithmBox.setModel(mode);
        }
    }

    protected void fitViewport() {
        if (graphComp == null) {
            return;
        }

        updateService.submit(new Runnable() {
            @Override
            public void run() {
                mxGraphView view = graph.getView();
                mxRectangle graphSize = view.getGraphBounds();
                graphComp.zoomToRectangle(graphSize.getRectangle());
                graphComp.setUserTranslated(false);
                graphComp.updateGraph();
                isFittingWindow = true;
            }
        });
    }

    protected void zoomIn() {
        graphComp.zoomIn(null);
        isFittingWindow = false;
    }

    protected void zoomOut() {
        graphComp.zoomOut(null);
        isFittingWindow = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseWheelListener#mouseWheelMoved(java.awt.event.
     * MouseWheelEvent)
     */
    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        if (e.getWheelRotation() < 0) {
            graphComp.zoomIn(e.getPoint());
        } else {
            graphComp.zoomOut(e.getPoint());
        }
        isFittingWindow = false;
    }

    /**
     * Description:
     * 
     */
    public void initView() {
        if (resizeTimer != null) {
            resizeTimer.restart();
        }
    }

    protected void enableAll(boolean b) {
        zoomInBtn.setEnabled(b);
        zoomOutBtn.setEnabled(b);
        fitWinBtn.setEnabled(b);
        if (showExtraButtons) {
            algorithmBox.setEnabled(b);
            expandBtn.setEnabled(b);
            collapseBtn.setEnabled(b);
            resetBtn.setEnabled(b);
        }
    }

    /**
     * Description:
     * 
     * @param added
     * @param removed
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    protected void selectionChanged(Collection<mxCell> removed) {
        if (topListener == null) {
            return;
        }

        synchronized (selections) {
            if (hasChanges(selections.peek())) {
                GraphCells currentCells =
                        GraphCells
                                .create((List) Arrays.asList(graph
                                        .getSelectionCells()), true);
                topListener.onSelectionChange(currentCells, this, null);
            } else {
                selections.poll();
            }
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
            synchronized (selections) {
                selections.add(lids);
                graph.setSelectionCells(cells);
                graph.refresh();
            }
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
        synchronized (selections) {
            selections.clear();
        }
        graph.clearSelection();
    }

    public void clearEdges() {
        graph.clearMarkedEdges();
        graph.refresh();
    }

}
