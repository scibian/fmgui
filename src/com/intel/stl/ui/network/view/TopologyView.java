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

import javax.swing.JComponent;
import javax.swing.JSplitPane;
import javax.swing.JTree;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.IBackgroundService;
import com.intel.stl.ui.monitor.view.TreeView;
import com.intel.stl.ui.network.TopologyTreeSelectionModel;

public class TopologyView extends TreeView {
    private static final long serialVersionUID = -1174727662197941419L;

    private static final Logger log = LoggerFactory
            .getLogger(TopologyView.class);

    private JSplitPane spltPane;

    private JSplitPane graphSpltPane;

    private TopologyGuideView guideView;

    private TopologyGraphView graphView;

    private ResourceView resourceView;

    /**
     * Description:
     * 
     */
    public TopologyView(IBackgroundService graphService,
            IBackgroundService outlineService) {
        super(graphService, outlineService);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.view.TreeView#createTree()
     */
    @Override
    protected JTree createTree() {
        JTree tree = super.createTree();
        tree.setSelectionModel(new TopologyTreeSelectionModel());
        return tree;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.monitor.view.TreeView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (spltPane == null) {
            spltPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
            spltPane.setResizeWeight(.8);
            spltPane.setDividerSize(5);

            JComponent comp = getGraphComponent();
            spltPane.setTopComponent(comp);

            comp = getResourceView();
            spltPane.setBottomComponent(comp);
        }

        return spltPane;
    }

    protected JComponent getGraphComponent() {
        if (graphSpltPane == null) {
            graphSpltPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
            graphSpltPane.setResizeWeight(0.1);
            graphSpltPane.setDividerSize(5);
            graphSpltPane.setOneTouchExpandable(true);

            final TopologyGuideView guideView = getGuideView();
            graphSpltPane.setLeftComponent(guideView);
            // graphSpltPane.addPropertyChangeListener(
            // JSplitPane.DIVIDER_LOCATION_PROPERTY,
            // new PropertyChangeListener() {
            // @Override
            // public void propertyChange(PropertyChangeEvent evt) {
            // guideView.update();
            // }
            // });
            // graphSpltPane.addComponentListener(new ComponentAdapter() {
            // /*
            // * (non-Javadoc)
            // *
            // * @see
            // * java.awt.event.ComponentAdapter#componentResized(java.awt
            // * .event.ComponentEvent)
            // */
            // @Override
            // public void componentResized(ComponentEvent e) {
            // guideView.update();
            // }
            // });

            TopologyGraphView graphView = getGraphView();
            graphSpltPane.setRightComponent(graphView);
        }
        return graphSpltPane;
    }

    public TopologyGuideView getGuideView() {
        if (guideView == null) {
            guideView = new TopologyGuideView(outlineService);
        }
        return guideView;
    }

    public TopologyGraphView getGraphView() {
        if (graphView == null) {
            graphView = new TopologyGraphView(graphService);
        }
        return graphView;
    }

    public ResourceView getResourceView() {
        if (resourceView == null) {
            resourceView = new ResourceView();
        }
        return resourceView;
    }

    /**
     * <i>Description:</i>
     * 
     */
    public void initView() {
        getGraphView().initView();
        getGuideView().initView();
    }

}
