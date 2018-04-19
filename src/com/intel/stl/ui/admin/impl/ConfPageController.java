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

import java.awt.Component;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.ui.admin.ChangeState;
import com.intel.stl.ui.admin.IConfListener;
import com.intel.stl.ui.admin.IItemEditorListener;
import com.intel.stl.ui.admin.IItemListListener;
import com.intel.stl.ui.admin.InvalidEditException;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.view.AbstractConfView;
import com.intel.stl.ui.admin.view.AbstractEditorPanel;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.PageWeight;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.ValidationModel;
import com.intel.stl.ui.console.LoginBean;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.jcraft.jsch.JSchException;

public abstract class ConfPageController<T, E extends AbstractEditorPanel<T>>
        implements IPageController, IConfListener, IItemListListener,
        IItemEditorListener {
    private static final Logger log =
            LoggerFactory.getLogger(ConfPageController.class);

    private final String name;

    private final String description;

    private final ImageIcon icon;

    private final AbstractConfView<T, E> view;

    private String helpID;

    protected IManagementApi mgtApi;

    protected TaskScheduler taskScheduler;

    protected final AbstractEditorController<T, E> edtCtr;

    protected ArrayList<Item<T>> orgItems;

    protected DefaultListModel<Item<T>> workingItems;

    protected Item<T> currentItem;

    protected ValidationModel<T> valModel;

    protected DeployController deployController;

    private boolean restart;

    private int busyCount;

    private Future<?> future;

    /**
     * Description:
     *
     * @param name
     * @param description
     * @param view
     */
    public ConfPageController(String name, String description, ImageIcon icon,
            AbstractConfView<T, E> view) {
        super();
        this.name = name;
        this.description = description;
        this.icon = icon;
        this.view = view;
        installHelp();
        workingItems = new DefaultListModel<Item<T>>();
        view.setListModel(workingItems);
        view.addItemListListener(this);
        view.setConfListener(this);

        E edtPanel = view.getEditorPanel();
        edtPanel.setEditorListener(this);
        edtCtr = creatEditorController(edtPanel);

        valModel = new ValidationModel<T>();
        deployController = new DeployController(view);
    }

    protected void installHelp() {
        String helpId = getHelpID();
        if (helpId != null) {
            view.enableHelp(true);
            HelpAction helpAction = HelpAction.getInstance();
            helpAction.getHelpBroker().enableHelpOnButton(view.getHelpButton(),
                    helpId, helpAction.getHelpSet());
        } else {
            view.enableHelp(false);
        }
    }

    /**
     * @param helpID
     *            the helpID to set
     */
    public void setHelpID(String helpID) {
        this.helpID = helpID;
        installHelp();
    }

    protected abstract String getHelpID();

    protected AbstractEditorController<T, E> creatEditorController(
            E editorPanel) {
        return new AbstractEditorController<T, E>(editorPanel);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IContextAware#setContext(com.intel.stl.ui.main
     * .Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        try {
            mgtApi = context.getManagementApi();
            taskScheduler = context.getTaskScheduler();
            deployController.setContext(context, null);
        } finally {
            if (observer != null) {
                observer.publishProgress(1);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getContextSwitchWeight()
     */
    @Override
    public PageWeight getContextSwitchWeight() {
        return PageWeight.LOW;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getRefreshWeight()
     */
    @Override
    public PageWeight getRefreshWeight() {
        return PageWeight.LOW;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return description;
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
        return icon;
    }

    protected SwingWorker<ArrayList<Item<T>>, Void> getInitWorker() {
        SwingWorker<ArrayList<Item<T>>, Void> worker =
                new SwingWorker<ArrayList<Item<T>>, Void>() {

                    @Override
                    protected ArrayList<Item<T>> doInBackground()
                            throws Exception {

                        ArrayList<Item<T>> res = initData();

                        return res;
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see javax.swing.SwingWorker#done()
                     */
                    @Override
                    protected void done() {
                        try {
                            // Show editor card in the view
                            view.showEditorCard();

                            orgItems = get();
                            if (orgItems == null) {
                                return;
                            }

                            // test empty items
                            // orgItems = new ArrayList<Item<T>>();

                            view.removeItemListListener(
                                    ConfPageController.this);
                            workingItems.clear();
                            int first = -1;
                            for (int i = 0; i < orgItems.size(); i++) {
                                Item<T> item = orgItems.get(i);
                                if (first == -1 && item.isEditable()) {
                                    first = i;
                                }
                                workingItems.addElement(
                                        getCopy(item, ChangeState.NONE));
                            }
                            view.addItemListListener(ConfPageController.this);

                            // view.setItems(workingItems);
                            if (currentItem != null) {
                                int index = workingItems.indexOf(currentItem);
                                if (index >= 0) {
                                    first = index;
                                }
                            }

                            if (first >= 0) {
                                currentItem = workingItems.get(first);
                                currentItem.setState(ChangeState.UPDATE);
                                view.setListModel(workingItems);
                                view.selectItem(first);
                                edtCtr.setItem(currentItem, getWorkingItems());
                            }
                        } catch (InterruptedException e) {
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                            Util.showError(view, e);
                        } finally {
                            busyCount -= 1;
                        }
                    }

                };
        return worker;
    }

    protected abstract ArrayList<Item<T>> initData() throws Exception;

    protected Item<T> getCopy(Item<T> item, ChangeState newState) {
        if (item != null) {
            Item<T> newItem = new Item<T>(item.getId(), item.getName(),
                    getCopy(item.getObj()), item.isEditable());
            newItem.setState(newState);
            return newItem;
        } else {
            Item<T> newItem = new Item<T>(System.currentTimeMillis(),
                    STLConstants.K0016_UNKNOWN.getValue(), createObj(), true);
            newItem.setState(newState);
            return newItem;
        }
    }

    protected abstract T getCopy(T obj);

    protected abstract T createObj();

    protected Item<T> getOrgItem(long id) {
        synchronized (orgItems) {
            int index = indexOfOrgItem(id);
            return orgItems.get(index);
        }
    }

    protected int indexOfOrgItem(long id) {
        if (orgItems == null) {
            throw new RuntimeException("No item list!");
        } else {
            synchronized (orgItems) {
                for (int i = 0; i < orgItems.size(); i++) {
                    Item<T> item = orgItems.get(i);
                    if (item.getId() == id) {
                        return i;
                    }
                }
                // this shouldn't happen
                throw new IllegalArgumentException(
                        "Couldn't find item with id=" + id);
            }
        }
    }

    protected Item<T> getWorkingItem(long id) {
        synchronized (workingItems) {
            int index = indexOfWorkingItem(id);
            return workingItems.get(index);
        }
    }

    protected int indexOfWorkingItem(long id) {
        synchronized (workingItems) {
            for (int i = 0; i < workingItems.size(); i++) {
                Item<T> item = workingItems.get(i);
                if (item.getId() == id) {
                    return i;
                }
            }
            // this shouldn't happen
            throw new IllegalArgumentException(
                    "Couldn't find item with id=" + id);
        }
    }

    @SuppressWarnings("unchecked")
    protected Item<T>[] getWorkingItems() {
        Item<T>[] res = new Item[workingItems.size()];
        for (int i = 0; i < res.length; i++) {
            res[i] = workingItems.get(i);
        }
        return res;
    }

    protected boolean changeCheck() {
        if (currentItem == null) {
            return true;
        }

        boolean hasChange = false;
        try {
            edtCtr.updateItem(currentItem);
        } catch (InvalidEditException e) {
            hasChange = true;
        }
        if (hasChange || hasChange(currentItem)) {
            int index = workingItems.indexOf(currentItem);
            int option = view.confirmDiscard();
            if (option != JOptionPane.YES_OPTION) {
                view.selectItem(index);
                return false;
            } else {
                if (currentItem.getState() == ChangeState.ADD) {
                    // discard new added item
                    currentItem = null;
                    workingItems.remove(index);
                } else if (currentItem.getState() == ChangeState.UPDATE) {
                    // change back to org item
                    Item<T> orgItem = getOrgItem(currentItem.getId());
                    Item<T> newItem = getCopy(orgItem, ChangeState.NONE);
                    currentItem = newItem;
                    workingItems.set(index, newItem);
                    edtCtr.setItem(currentItem, getWorkingItems());
                }
                view.updateItems();
            }
        }
        return true;
    }

    protected boolean hasChange(Item<T> workingItem) {
        Item<T> orgItem = null;
        try {
            orgItem = getOrgItem(workingItem.getId());
        } catch (IllegalArgumentException e) {
        } catch (Exception e) {
            e.printStackTrace();
        }
        // System.out
        // .println((orgItem == null ? "null" : orgItem
        // .getFullDescription())
        // + " "
        // + workingItem.getFullDescription());
        return orgItem == null || !orgItem.equals(workingItem);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IItemListListener#onSelect(java.lang.String)
     */
    @Override
    public void onSelect(long id) {
        // System.out.println("Select " + id);
        if (id == -1) {
            edtCtr.setItem(null, getWorkingItems());
            currentItem = null;
            return;
        }

        if (currentItem != null) {
            if (currentItem.getId() == id) {
                return;
            }

            if (!changeCheck()) {
                return;
            }
        }

        Item<T> item = getWorkingItem(id);
        // change state to UPDATE for items already existed.
        if (item.getState() == ChangeState.NONE) {
            item.setState(ChangeState.UPDATE);
        }
        edtCtr.setItem(item, getWorkingItems());
        currentItem = item;
        log.info("Select " + item.getFullDescription());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IItemListListener#onAdd()
     */
    @Override
    public void onAdd() {
        if (!changeCheck()) {
            return;
        }

        Item<T> newItem = getCopy(null, ChangeState.ADD);
        currentItem = newItem;
        workingItems.addElement(newItem);
        view.selectItem(workingItems.size() - 1);
        edtCtr.setItem(newItem, getWorkingItems());
        edtCtr.selectItemName();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IItemListListener#onRemove(java.lang.String)
     */
    @Override
    public void onRemove(final long id) {
        if (!changeCheck()) {
            return;
        }

        // if it's new add, we just need to remove on UI side since it's not in
        // backend yet
        if (currentItem == null || currentItem.getState() == ChangeState.ADD) {
            view.selectItem(workingItems.size() - 1);
            return;
        }

        currentItem.setState(ChangeState.REMOVE);
        ValidationDialog vd =
                new ValidationDialog(view, UILabels.STL81101_REMOVE_ITEM
                        .getDescription(currentItem.getName())) {
                    private static final long serialVersionUID =
                            -8807399194554240022L;

                    /*
                     * (non-Javadoc)
                     *
                     * @see com.intel.stl.ui.common.view.OptionDialog#onCancel()
                     */
                    @Override
                    public void onCancel() {
                        super.onCancel();
                        close();
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see com.intel.stl.ui.common.view.OptionDialog#onOk()
                     */
                    @Override
                    public void onOk() {
                        super.onOk();
                        showMessage(STLConstants.K2130_REMOVING.getValue());
                        SwingWorker<Integer, Void> worker =
                                getRemoveWorker(this, id);
                        worker.execute();
                    }

                };
        valModel.clear();

        vd.setValidationTableModel(valModel);
        vd.enableOk(false);
        vd.showDialog();

        vd.showMessage(STLConstants.K2129_VALIDATING.getValue());
        vd.startProgress();
        ValidationTask<T> vTask = getValidationTask(vd, currentItem);
        vTask.execute();
    }

    protected SwingWorker<Integer, Void> getRemoveWorker(
            final ValidationDialog vd, final long id) {
        SwingWorker<Integer, Void> worker = new SwingWorker<Integer, Void>() {

            @Override
            protected Integer doInBackground() throws Exception {
                int index = indexOfWorkingItem(id);
                Item<T> item = workingItems.get(index);
                removeItemObject(item.getName());
                int orgIndex = indexOfOrgItem(id);
                orgItems.remove(orgIndex);

                workingItems.remove(index);
                if (index >= workingItems.size()) {
                    index -= 1;
                }
                currentItem = null;

                return index;
            }

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    Integer index = get();
                    view.updateItems();

                    view.selectItem(index);
                    vd.reportProgress("Done!");
                    vd.close();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    vd.showMessage(StringUtils.getErrorMessage(e));
                } finally {
                    vd.stopProgress();
                }
            }

        };
        return worker;
    }

    protected abstract void removeItemObject(String name) throws Exception;

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.IItemEditorListener#nameChange(java.lang.String)
     */
    @Override
    public void nameChanged(String name) {
        String oldName = currentItem.getName();
        currentItem.setName(name);
        view.updateItems();
        edtCtr.itemNameChanged(oldName, name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IItemEditorListener#onSave()
     */
    @Override
    public void onSave() {
        try {
            edtCtr.updateItem(currentItem);
        } catch (InvalidEditException e) {
            Util.showWarningMessage(view, e.getMessage());
            return;
        }

        if (!hasChange(currentItem)) {
            Util.showWarningMessage(view,
                    UILabels.STL81112_NO_CHANGES.getDescription());
            return;
        }

        ValidationDialog vd =
                new ValidationDialog(view, UILabels.STL81100_SAVE_ITEM
                        .getDescription(currentItem.getName())) {
                    private static final long serialVersionUID =
                            -8807399194554240022L;

                    /*
                     * (non-Javadoc)
                     *
                     * @see com.intel.stl.ui.common.view.OptionDialog#onCancel()
                     */
                    @Override
                    public void onCancel() {
                        super.onCancel();
                        close();
                    }

                    /*
                     * (non-Javadoc)
                     *
                     * @see com.intel.stl.ui.common.view.OptionDialog#onOk()
                     */
                    @Override
                    public void onOk() {
                        super.onOk();
                        showMessage(STLConstants.K2128_SAVING.getValue());
                        SwingWorker<Void, Void> saveWorker =
                                getSaveWorker(this);
                        saveWorker.execute();
                    }

                };
        valModel.clear();

        vd.setValidationTableModel(valModel);
        vd.enableOk(false);
        vd.showDialog();

        vd.showMessage(STLConstants.K2129_VALIDATING.getValue());
        vd.startProgress();
        ValidationTask<T> vTask = getValidationTask(vd, currentItem);
        vTask.execute();
    }

    protected SwingWorker<Void, Void> getSaveWorker(final ValidationDialog vd) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {
                String oldName = null;
                int index = -1;
                if (currentItem.getState() == ChangeState.UPDATE) {
                    index = indexOfOrgItem(currentItem.getId());
                    Item<T> oldOrgItem = orgItems.get(index);
                    oldName = oldOrgItem.getName();
                }
                saveItemObject(oldName, currentItem.getObj());
                Item<T> newOrgItem = getCopy(currentItem, ChangeState.NONE);
                if (index >= 0) {
                    orgItems.set(index, newOrgItem);
                } else {
                    orgItems.add(newOrgItem);
                }
                currentItem.setState(ChangeState.UPDATE);
                return null;
            }

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    get();
                    vd.reportProgress("Done!");
                    vd.close();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    e.printStackTrace();
                    vd.showMessage(StringUtils.getErrorMessage(e));
                }
            }
        };
        return worker;
    }

    /**
     *
     * <i>Description:</i> validate an item and put result in the
     * ValidationModel
     *
     * @param model
     *            the validation model to update
     * @param obj
     *            the item to validate
     */
    protected abstract ValidationTask<T> getValidationTask(
            ValidationDialog dialog, Item<T> item);

    /**
     * <i>Description:</i>
     *
     * @param obj
     */
    protected abstract void saveItemObject(String oldName, T obj)
            throws Exception;

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IItemEditorListener#onReset()
     */
    @Override
    public void onReset() {
        int index = workingItems.indexOf(currentItem);
        Item<T> orgItem = getOrgItem(currentItem.getId());
        Item<T> newItem = getCopy(orgItem, ChangeState.NONE);
        workingItems.set(index, newItem);
        view.updateItems();
        edtCtr.setItem(newItem, getWorkingItems());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.IConfListener#onApply(boolean)
     */
    @Override
    public void onApply(boolean restart) {
        this.restart = restart;

        try {
            edtCtr.updateItem(currentItem);
        } catch (InvalidEditException e) {
            Util.showWarningMessage(view, e.getMessage());
            return;
        }

        if (hasChange(currentItem)) {
            int index = workingItems.indexOf(currentItem);
            int option = view.confirmDiscard();
            if (option != JOptionPane.YES_OPTION) {
                return;
            }

            if (currentItem.getState() == ChangeState.ADD) {
                // discard new added item
                currentItem = null;
                workingItems.remove(index);
                view.selectItem(workingItems.size() - 1);
            } else if (currentItem.getState() == ChangeState.UPDATE) {
                // change back to org item
                Item<T> orgItem = getOrgItem(currentItem.getId());
                Item<T> newItem = getCopy(orgItem, ChangeState.UPDATE);
                currentItem = newItem;
                workingItems.set(index, newItem);
                edtCtr.setItem(newItem, getWorkingItems());
            }
            view.updateItems();
        }

        if (!mgtApi.hasChanges()) {
            Util.showWarningMessage(view,
                    UILabels.STL81112_NO_CHANGES.getDescription());
            return;
        }

        view.showDeployCard(mgtApi.getSubnetDescription());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#cleanup()
     */
    @Override
    public void cleanup() {
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onEnter()
     */
    @Override
    public void onEnter() {
        if (mgtApi == null) {
            view.showLoginCard();
            view.setMessage(UILabels.STL10116_NOT_INIT.getDescription());
            view.setLoginEnabled(false);
            return;
        }

        // onEnter cancel any non-null future. Note: shouldn't be any.
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }

        if (mgtApi.isConfigReady()) {
            if (!view.isShowingDeployCard()) {
                view.showEditorCard();
                SwingWorker<ArrayList<Item<T>>, Void> worker = getInitWorker();
                worker.execute();
            }
        } else if (mgtApi.hasSession()) {
            loadConfigFile(null);
        } else {
            // display and ask for log in info
            // Set host name and port number
            view.setLoginEnabled(true);
            view.setHostNameField(
                    mgtApi.getSubnetDescription().getCurrentFE().getHost());
            view.setUserNameField(mgtApi.getSubnetDescription().getCurrentFE()
                    .getSshUserName());
            view.showLoginCard(); // turn on login card
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onExit()
     */
    @Override
    public void onExit() {
        if (mgtApi == null) {
            return;
        }

        // Save username for ssl login to mgtApi for persistence between tabs
        // If we don't, the user can change user name on one of the tabs, switch
        // to another tab on Admin page and see a different user name being
        // displayed. Same goes for port number
        mgtApi.getSubnetDescription().getCurrentFE()
                .setSshUserName(view.getUserNameFieldStr());
        mgtApi.getSubnetDescription().getCurrentFE()
                .setSshPortNum(Integer.parseInt(view.getPortFieldStr()));
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#canExit()
     */
    @Override
    public boolean canExit() {
        return busyCount <= 0 && deployCheck() && changeCheck();
    }

    protected boolean deployCheck() {
        if (view.isShowingDeployCard() && deployController.isBusy()) {
            int ret = deployController.confirmDiscard();
            if (ret == JOptionPane.YES_OPTION) {
                deployController.onCancel();
            } else {
                return false;
            }
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
        // Make the login card visible to get password from user to fetch a new
        // copy of config file
        if (changeCheck()) {
            if (mgtApi != null) {
                mgtApi.reset();
            }
            onEnter();
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

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#onCancel()
     */
    @Override
    public synchronized void onCancelLogin() {
        if (future != null) {
            future.cancel(true);
        }

        // Call ManagementApi to cancel fetching of the config file.
        busyCount -= 1;
        mgtApi.onCancelFetchConfig(mgtApi.getSubnetDescription());
        view.showLoginCard();
        view.clearLoginCard();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.IConfListener#prepare(com.intel.stl.ui.console
     * .LoginBean)
     */
    @Override
    public synchronized void prepare(LoginBean credentials) {
        busyCount += 1;
        int portNum = Integer.parseInt(credentials.getPortNum());
        mgtApi.getSubnetDescription().getCurrentFE().setSshPortNum(portNum);
        mgtApi.getSubnetDescription().getCurrentFE()
                .setSshUserName(credentials.getUserName());
        loadConfigFile(credentials.getPassword());
    }

    protected void loadConfigFile(final char[] password) {
        if (future != null && !future.isDone()) {
            future.cancel(true);
        }

        final FutureCancelIndicator cancelIndicator =
                new FutureCancelIndicator();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                try {
                    mgtApi.fetchConfigFile(password);
                    if (!cancelIndicator.isCancelled()
                            && mgtApi.isConfigReady()) {
                        SwingWorker<ArrayList<Item<T>>, Void> worker =
                                getInitWorker();
                        worker.execute();
                    }
                } catch (JSchException e) {
                    log.error("Failed to feaching conf file", e);
                    // if canceled silent on error messages
                    if (!cancelIndicator.isCancelled()) {
                        view.showLoginCard();
                        view.setMessage(
                                UILabels.STL81111_LOGIN_ERROR.getDescription(
                                        StringUtils.getErrorMessage(e)));
                        busyCount -= 1;
                    }
                } catch (Exception e) {
                    log.error("Failed to feaching conf file", e);
                    // if canceled silent on error messages
                    if (!cancelIndicator.isCancelled()) {
                        view.showLoginCard();
                        view.setMessage(StringUtils.getErrorMessage(e));
                        busyCount -= 1;
                    }
                } finally {
                    if (!cancelIndicator.isCancelled()) {
                        view.clearLoginCard();
                    }
                }
            }
        };

        future = taskScheduler.submitToBackground(task);
        cancelIndicator.setFuture(future);
    }

    class FutureCancelIndicator implements ICancelIndicator {
        private Future<?> future;

        /**
         * @param future
         *            the future to set
         */
        public void setFuture(Future<?> future) {
            this.future = future;
        }

        /*
         * (non-Javadoc)
         *
         * @see com.intel.stl.ui.common.ICancelIndicator#isCancelled()
         */
        @Override
        public boolean isCancelled() {
            return future != null && future.isCancelled();
        }

    }
}
