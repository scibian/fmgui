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

package com.intel.stl.ui.admin.impl;

import static com.intel.stl.ui.common.PageWeight.MEDIUM;

import java.awt.Component;
import java.util.ArrayList;
import java.util.List;

import javax.swing.ImageIcon;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.engio.mbassy.bus.MBassador;

import com.intel.stl.ui.admin.FunctionType;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.UndoableTabSelection;
import com.intel.stl.ui.admin.impl.applications.AppsPageController;
import com.intel.stl.ui.admin.impl.devicegroups.DevicegroupsPageController;
import com.intel.stl.ui.admin.impl.logs.LogPage;
import com.intel.stl.ui.admin.impl.logs.SMLogController;
import com.intel.stl.ui.admin.impl.virtualfabrics.VirtualFabricsPageController;
import com.intel.stl.ui.admin.view.AdminView;
import com.intel.stl.ui.admin.view.BlankView;
import com.intel.stl.ui.admin.view.BlankView.BlankEditorPanel;
import com.intel.stl.ui.admin.view.NavigationPanel.IconPanel;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.admin.view.applications.AppsSubpageView;
import com.intel.stl.ui.admin.view.devicegroups.DevicegroupsSubpageView;
import com.intel.stl.ui.admin.view.logs.SMLogView;
import com.intel.stl.ui.admin.view.virtualfabrics.VirtualFabricsSubpageView;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.console.ConsoleDispatchManager;
import com.intel.stl.ui.console.ConsolePage;
import com.intel.stl.ui.console.view.ConsoleView;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.UndoHandler;

public class AdminPage implements IPageController, ChangeListener {
    public static final String NAME = STLConstants.K1057_ADMIN.getValue();

    private final AdminView view;

    private final List<IPageController> subpages;

    private IPageController currentPage;

    private final MBassador<IAppEvent> eventBus;

    private boolean isShowing;

    protected UndoHandler undoHandler;

    public AdminPage(AdminView view, MBassador<IAppEvent> eventBus) {
        this.view = view;
        view.addChangeListener(this);
        subpages = getAdmSubpages();
        installSubpages(subpages);

        this.eventBus = eventBus;
        eventBus.subscribe(this);
    }

    protected void installSubpages(List<IPageController> subpages) {
        for (IPageController subpage : subpages) {
            if (subpage instanceof ConsolePage) {
                view.addSeperator(20);
            }
            view.addViewCard(subpage.getIcon(), subpage.getView(),
                    subpage.getName());
        }
        view.setView(subpages.get(0).getName());
        currentPage = subpages.get(0);
    }

    protected List<IPageController> getAdmSubpages() {
        List<IPageController> res = new ArrayList<IPageController>();

        res.add(createAppsPage());
        res.add(createDGsPage());
        res.add(createVFsPage());
        res.add(createConsolePage());
        res.add(createLogsPage());

        return res;
    }

    protected IPageController createAppsPage() {
        FunctionType type = FunctionType.APPLICATIONS;
        AppsSubpageView view = new AppsSubpageView(type.getName());
        AppsPageController appsPage =
                new AppsPageController(type.getName(), type.getDescription(),
                        type.getIcon(), view);
        return appsPage;
    }

    protected IPageController createDGsPage() {
        FunctionType type = FunctionType.DEVICE_GROUPS;
        DevicegroupsSubpageView view =
                new DevicegroupsSubpageView(type.getName());
        DevicegroupsPageController appsPage =
                new DevicegroupsPageController(type.getName(),
                        type.getDescription(), type.getIcon(), view);
        return appsPage;
    }

    protected IPageController createVFsPage() {
        FunctionType type = FunctionType.VIRTUAL_FABRICS;
        VirtualFabricsSubpageView view =
                new VirtualFabricsSubpageView(type.getName());
        VirtualFabricsPageController appsPage =
                new VirtualFabricsPageController(type.getName(),
                        type.getDescription(), type.getIcon(), view);
        return appsPage;
    }

    protected IPageController createBlankPage(FunctionType type) {
        BlankView view = new BlankView(type.getName());
        ConfPageController<Object, BlankEditorPanel> page =
                new ConfPageController<Object, BlankEditorPanel>(
                        type.getName(), type.getDescription(), type.getIcon(),
                        view) {

                    @Override
                    protected AbstractEditorController<Object, BlankEditorPanel> creatEditorController(
                            BlankEditorPanel editorPanel) {
                        return null;
                    }

                    @Override
                    protected ArrayList<Item<Object>> initData()
                            throws Exception {
                        return null;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.intel.stl.ui.admin.impl.ConfPageController#getCopy
                     * (java.lang.Object)
                     */
                    @Override
                    protected Object getCopy(Object obj) {
                        return null;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.intel.stl.ui.admin.impl.ConfPageController#createObj
                     * ()
                     */
                    @Override
                    protected Object createObj() {
                        return null;
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see com.intel.stl.ui.admin.impl.ConfPageController#
                     * removeItemObject(java.lang.String)
                     */
                    @Override
                    protected void removeItemObject(String name)
                            throws Exception {
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see
                     * com.intel.stl.ui.admin.impl.ConfPageController#saveItemObject
                     * (java.lang.Object)
                     */
                    @Override
                    protected void saveItemObject(String oldName, Object obj)
                            throws Exception {
                    }

                    /*
                     * (non-Javadoc)
                     * 
                     * @see com.intel.stl.ui.admin.impl.ConfPageController#
                     * getValidationTask
                     * (com.intel.stl.ui.admin.view.ValidationDialog,
                     * com.intel.stl.ui.admin.Item)
                     */
                    @Override
                    protected ValidationTask<Object> getValidationTask(
                            ValidationDialog dialog, Item<Object> item) {
                        return null;
                    }

                    @Override
                    protected String getHelpID() {
                        return null;
                    }

                };
        return page;
    }

    protected IPageController createConsolePage() {
        ConsoleView consoleView = new ConsoleView();
        ConsoleDispatchManager dispatchManager =
                new ConsoleDispatchManager(consoleView, view.getOwner());
        consoleView.setConsoleDispatchManager(dispatchManager);
        ConsolePage consolePage =
                new ConsolePage(consoleView, view.getOwner(), dispatchManager,
                        eventBus);
        return consolePage;
    }

    protected IPageController createLogsPage() {
        SMLogView smLogView = new SMLogView();
        SMLogModel smLogModel = new SMLogModel();
        SMLogController smLogController =
                new SMLogController(smLogModel, smLogView);
        LogPage logPage = new LogPage(smLogController);

        return logPage;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.IPageController#setContext(com.intel.stl.ui.main
     * .Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        IProgressObserver[] subObservers = null;
        if (observer != null) {
            subObservers = observer.createSubObservers(subpages.size());
        }
        for (int i = 0; i < subpages.size(); i++) {
            IPageController subpage = subpages.get(i);
            subpage.setContext(context, subObservers == null ? null
                    : subObservers[i]);
        }

        if (context != null && context.getController() != null) {
            undoHandler = context.getController().getUndoHandler();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.event.ChangeListener#stateChanged(javax.swing.event.ChangeEvent
     * )
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        IconPanel panel = (IconPanel) e.getSource();
        String name = panel.getName();
        IPageController newPage = null;
        for (IPageController page : subpages) {
            if (page.getName().equals(name)) {
                newPage = page;
                break;
            }
        }
        if (newPage != null) {
            IPageController oldPage = currentPage;
            boolean success = selectPage(newPage);
            if (success && undoHandler != null && !undoHandler.isInProgress()) {
                UndoableTabSelection undoSel =
                        new UndoableTabSelection(this, oldPage, currentPage);
                undoHandler.addUndoAction(undoSel);
            }
        }
    }

    public boolean selectPage(IPageController page) {
        if (currentPage != null) {
            if (!currentPage.canExit()) {
                return false;
            }
            currentPage.onExit();
        }

        currentPage = page;
        view.setView(page.getName());
        page.onEnter();
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getName()
     */
    @Override
    public String getName() {
        return NAME;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return STLConstants.K1058_ADMIN_DESCRIPTION.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getView()
     */
    @Override
    public Component getView() {
        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#getIcon()
     */
    @Override
    public ImageIcon getIcon() {
        return null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
        for (IPageController subpage : subpages) {
            subpage.cleanup();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        isShowing = true;
        if (currentPage != null) {
            currentPage.onEnter();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        if (currentPage != null) {
            currentPage.onExit();
        }
        isShowing = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        if (currentPage != null && currentPage instanceof ConfPageController) {
            return currentPage.canExit();
        }
        return true;
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
        if (isShowing && currentPage != null) {
            currentPage.onRefresh(observer);
        }
        if (observer != null) {
            observer.onFinish();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.IPageController#clear()
     */
    @Override
    public void clear() {
    }

    @Override
    public PageWeight getContextSwitchWeight() {
        return MEDIUM;
    }

    @Override
    public PageWeight getRefreshWeight() {
        return MEDIUM;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getName();
    }

}
