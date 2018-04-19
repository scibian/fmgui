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

package com.intel.stl.ui.network.task;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.model.LayoutType;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.GraphBuilder;
import com.intel.stl.ui.network.IModelChange;
import com.intel.stl.ui.network.LayoutChange;
import com.intel.stl.ui.network.TopGraph;
import com.intel.stl.ui.network.TopologyGraphController;
import com.intel.stl.ui.network.TopologyTreeModel;

public class RefreshGraphTask extends TopologyUpdateTask {
    private static Logger log = LoggerFactory.getLogger(RefreshGraphTask.class);

    private final LayoutType type;

    private final IProgressObserver observer;

    private TopologyTreeModel tmpTreeMode;

    /**
     * Description:
     *
     * @param controller
     * @param source
     * @param selectedResources
     */
    public RefreshGraphTask(TopologyGraphController controller, Object source,
            FVResourceNode[] selectedResources, LayoutType type,
            IProgressObserver observer) {
        super(controller, source, selectedResources);
        this.type = type;
        this.observer = observer;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.network.task.TopologyUpdateTask#createNewGraph(com.intel
     * .stl.ui.common.ICancelIndicator, com.intel.stl.ui.network.TopGraph)
     */
    @Override
    public TopGraph createNewGraph(ICancelIndicator indicator,
            TopGraph oldGraph) {
        if (indicator.isCancelled()) {
            return null;
        }
        try {
            ISubnetApi subnetApi = controller.getSubnetApi();
            List<NodeRecordBean> nodes = subnetApi.getNodes(false);
            List<LinkRecordBean> links = subnetApi.getLinks(false);
            TopGraph fullGraph = TopGraph.createGraph();
            GraphBuilder builder = new GraphBuilder();
            tmpTreeMode = builder.build(fullGraph, nodes, links);
            fullGraph.expandAll();
            return fullGraph;
        } catch (Exception e) {
            if (!isInterruptedException(e)) {
                log.error(e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return oldGraph;
    }

    protected boolean isInterruptedException(Exception e) {
        Throwable tmp = e;
        while (tmp != null) {
            if (tmp instanceof InterruptedException) {
                return true;
            }
            tmp = tmp.getCause();
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.network.task.TopologyUpdateTask#applyChanges(com.intel
     * .stl.ui.common.ICancelIndicator, com.intel.stl.ui.network.TopGraph)
     */
    @Override
    public boolean applyChanges(ICancelIndicator indicator, TopGraph newGraph) {
        IModelChange change = new LayoutChange(type, tmpTreeMode);
        change.execute(newGraph, indicator);
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.network.task.TopologyUpdateTask#onSuccess(com.intel.
     * stl.ui.common.ICancelIndicator, com.intel.stl.ui.network.TopGraph)
     */
    @Override
    public void onSuccess(ICancelIndicator indicator, TopGraph graph) {
        // create a copy before we do any UI related things on it
        final TopGraph outlineGraph = TopGraph.createGraph();
        outlineGraph.setModel(graph.getModel());

        super.onSuccess(indicator, graph);

        controller.setFullTopTreeModel(tmpTreeMode);
        controller.setTopTreeModel(tmpTreeMode);

        submitGraphTask(new Runnable() {
            @Override
            public void run() {
                graphView.updateGraph();
            }
        });

        submitOutlineTask(new Runnable() {
            @Override
            public void run() {
                guideView.setGraph(outlineGraph);
                guideView.updateGraph();
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.network.task.TopologyUpdateTask#onFinally(com.intel.
     * stl.ui.common.ICancelIndicator)
     */
    @Override
    public void onFinally(ICancelIndicator indicator) {
        if (observer != null) {
            observer.onFinish();
        }
    }

}
