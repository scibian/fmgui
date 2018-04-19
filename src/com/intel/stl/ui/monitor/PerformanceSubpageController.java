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

package com.intel.stl.ui.monitor;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.awt.Component;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.common.IPerfSubpageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.monitor.view.PerformanceSubpageView;
import com.intel.stl.ui.monitor.view.PerformanceView;

public class PerformanceSubpageController implements IPerfSubpageController {

    PerformanceSubpageView subpageView;

    Map<TreeNodeType, IPerfSubpageController> subpages =
            new HashMap<TreeNodeType, IPerfSubpageController>();

    private PerformancePortController performancePortController;

    private PerformanceNodeController performanceNodeController;

    private final MBassador<IAppEvent> eventBus;

    @SuppressWarnings("unused")
    private PerformanceTreeController parentController;

    public PerformanceSubpageController(PerformanceSubpageView subpageView,
            MBassador<IAppEvent> eventBus) {
        this.eventBus = eventBus;
        subpages = getSubPages();
        this.subpageView = subpageView;

        this.subpageView.initializeViews(subpages);
    }

    protected Map<TreeNodeType, IPerfSubpageController> getSubPages() {
        performancePortController =
                new PerformancePortController(new PerformanceView(),
                        eventBus);
        subpages.put(TreeNodeType.ACTIVE_PORT, performancePortController);

        performanceNodeController =
                new PerformanceNodeController(new PerformanceView(), eventBus);
        subpages.put(TreeNodeType.NODE, performanceNodeController);

        return subpages;
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(2);
        performancePortController.setContext(context, subObservers[0]);
        subObservers[0].onFinish();
        performanceNodeController.setContext(context, subObservers[1]);
        subObservers[1].onFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#onRefresh(com.intel.stl.ui.common
     * .IProgressObserver)
     */
    @Override
    public void onRefresh(IProgressObserver observer) {
        IProgressObserver[] subObservers = observer.createSubObservers(2);
        performancePortController.onRefresh(subObservers[0]);
        subObservers[0].onFinish();
        performanceNodeController.onRefresh(subObservers[1]);
        subObservers[0].onFinish();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return STLConstants.K0200_PERFORMANCE.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return this.subpageView;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return true;
    }

    @Override
    public void setParentController(PerformanceTreeController parentController) {
        this.parentController = parentController;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPerfSubpageController#showNode(com.intel.stl
     * .ui.monitor.FVResourceNode)
     */
    @Override
    public void showNode(FVResourceNode node, IProgressObserver observer) {

        TreeNodeType nodeType = node.getType();
        subpageView.showView(nodeType);

        if ((nodeType == TreeNodeType.HFI) || (nodeType == TreeNodeType.SWITCH)) {
            nodeType = TreeNodeType.NODE;
        }
        IPerfSubpageController page = subpages.get(nodeType);
        if (page != null) {
            page.showNode(node, observer);
        } else {
            throw new IllegalArgumentException("Couldn't find subpage for "
                    + nodeType);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
        performancePortController.clear();
        performanceNodeController.clear();
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

}
