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

package com.intel.stl.ui.network;

import java.util.Map;

import javax.swing.Icon;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.ICardController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.configuration.view.IPropertyListener;
import com.intel.stl.ui.configuration.view.PropertyGroupPanel;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.main.UndoHandler;
import com.intel.stl.ui.model.PropertySet;
import com.intel.stl.ui.model.SimplePropertyCategory;
import com.intel.stl.ui.model.SimplePropertyGroup;
import com.intel.stl.ui.monitor.tree.FVResourceNode;
import com.intel.stl.ui.network.view.ResourceAllView;
import com.intel.stl.ui.network.view.TopSummaryGroupPanel;
import com.intel.stl.ui.publisher.CallbackAdapter;
import com.intel.stl.ui.publisher.CancellableCall;
import com.intel.stl.ui.publisher.ICallback;
import com.intel.stl.ui.publisher.SingleTaskManager;

/**
 * Card controller for the main overview topology JCard displayed when no
 * components have been selected on the topology graph
 */
public class ResourceAllSection extends ResourceSection<ResourceAllView>
        implements IPropertyListener {
    private final String SUBNET_SUMMARY = STLConstants.K2063_OVERALL_SUMMARY
            .getValue();

    private final String TOP_SUMMARY = STLConstants.K2064_TOP_SUMMARY
            .getValue();

    private final PropertyVizStyle style = new PropertyVizStyle(true, false);

    private final ResourceAllView view;

    private PropertySet<SimplePropertyGroup> model;

    private ISubnetApi subnetApi;

    private final SingleTaskManager taskMgr;

    private FVResourceNode[] selectedResources;

    protected UndoHandler undoHandler;

    public ResourceAllSection(ResourceAllView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        this.view = view;
        view.setInitialStyle(style.isShowBorder(), style.isAlternatRows());
        view.setStyleListener(this);
        taskMgr = new SingleTaskManager();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseSectionController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getNameOfSubnet();
    }

    @Override
    public void setContext(Context context, IProgressObserver observer) {
        if (context != null) {
            subnetApi = context.getSubnetApi();
            if (context.getController() != null) {
                undoHandler = context.getController().getUndoHandler();
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.network.ResourceSection#setCurrentSubpage(java.lang.
     * String)
     */
    @Override
    public void setCurrentSubpage(String subpageName) {
        // do nothing
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.network.ResourceSection#getPreviousSubpage()
     */
    @Override
    public String getPreviousSubpage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.network.ResourceSection#getCurrentSubpage()
     */
    @Override
    public String getCurrentSubpage() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public ResourceAllView getView() {
        return view;
    }

    public void showAll(final FVResourceNode[] selectedResources,
            final String name, final Icon icon,
            final TopologyTreeModel topArch, final TopGraph graph,
            final TopGraph fullGraph) {
        this.selectedResources = selectedResources;
        CancellableCall<PropertySet<SimplePropertyGroup>> caller =
                new CancellableCall<PropertySet<SimplePropertyGroup>>() {
                    @Override
                    public PropertySet<SimplePropertyGroup> call(
                            ICancelIndicator cancelIndicator) throws Exception {
                        TopologySummaryProcessor topProcessor =
                                new TopologySummaryProcessor(SUBNET_SUMMARY,
                                        TOP_SUMMARY, topArch, graph, fullGraph,
                                        subnetApi, cancelIndicator);
                        PropertySet<SimplePropertyGroup> model =
                                topProcessor.populate();
                        return model;
                    }
                };

        ICallback<PropertySet<SimplePropertyGroup>> callback =
                new CallbackAdapter<PropertySet<SimplePropertyGroup>>() {
                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.intel.stl.ui.publisher.CallbackAdapter#onDone(java
                     * .lang .Object )
                     */
                    @Override
                    public void onDone(PropertySet<SimplePropertyGroup> result) {
                        view.setTitle(name, icon);
                        updateMode(selectedResources, result);
                    }
                };
        taskMgr.submit(caller, callback);
    }

    protected void updateMode(FVResourceNode[] selResources,
            PropertySet<SimplePropertyGroup> model) {
        this.model = model;
        view.clearPanel();
        for (SimplePropertyGroup group : model.getGroups()) {
            if (group.getGroupName() == SUBNET_SUMMARY) {
                updateSubnetSummaryModel(group);
            } else if (group.getGroupName() == TOP_SUMMARY) {
                updateTopSummaryModel(selResources, group);
            }
        }
        view.setModel(model);
    }

    protected void updateSubnetSummaryModel(SimplePropertyGroup model) {
        PropertyGroupPanel<SimplePropertyCategory, SimplePropertyGroup> groupPanel =
                new PropertyGroupPanel<SimplePropertyCategory, SimplePropertyGroup>(
                        style);
        groupPanel.setModel(model);

        groupPanel.enableHelp(true);
        HelpAction helpAction = HelpAction.getInstance();
        helpAction.getHelpBroker().enableHelpOnButton(
                groupPanel.getHelpButton(), helpAction.getOverallSummary(),
                helpAction.getHelpSet());

        view.addPropertyGroupPanel(groupPanel);
    }

    protected SimplePropertyGroup populateTopSummaryModel() {
        SimplePropertyGroup model =
                new SimplePropertyGroup(
                        STLConstants.K2064_TOP_SUMMARY.getValue());
        return model;
    }

    protected void updateTopSummaryModel(FVResourceNode[] selResources,
            SimplePropertyGroup model) {
        TopSummaryGroupPanel groupPanel = new TopSummaryGroupPanel(style);

        // help already set in the TopSummaryGroupController
        TopSummaryGroupController groupController =
                new TopSummaryGroupController(groupPanel, eventBus, undoHandler);
        groupController.setModel(selResources, model);

        view.addPropertyGroupPanel(groupPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ISectionController#getCards()
     */
    @Override
    public ICardController<?>[] getCards() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseSectionController#getSectionListener()
     */
    @Override
    protected ISectionListener getSectionListener() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#onShowBorder
     * (boolean)
     */
    @Override
    public void onShowBorder(boolean b) {
        style.setShowBorder(b);
        updateMode(selectedResources, model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyStyleListener#showAlternation
     * (boolean)
     */
    @Override
    public void onShowAlternation(boolean b) {
        style.setAlternateRows(b);
        updateMode(selectedResources, model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.configuration.view.IPropertyListener#onDisplayChanged
     * (java.util.Map)
     */
    @Override
    public void onDisplayChanged(Map<String, Boolean> newSelections) {
        // TODO Auto-generated method stub

    }
}
