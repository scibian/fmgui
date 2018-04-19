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

package com.intel.stl.ui.admin.impl.applications;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import com.intel.stl.api.management.applications.Application;
import com.intel.stl.ui.admin.ChangeState;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.ConfPageController;
import com.intel.stl.ui.admin.impl.ValidationTask;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.admin.view.applications.AppsEditorPanel;
import com.intel.stl.ui.admin.view.applications.AppsSubpageView;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;

public class AppsPageController
        extends ConfPageController<Application, AppsEditorPanel> {
    private Set<String> reserved;

    /**
     * Description:
     *
     * @param name
     * @param description
     * @param icon
     * @param view
     */
    public AppsPageController(String name, String description, ImageIcon icon,
            AppsSubpageView view) {
        super(name, description, icon, view);
    }

    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getAdminApp();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#setContext(com.intel.stl
     * .ui.main.Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        super.setContext(context, observer);
        reserved = mgtApi.getReservedApplications();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.ConfPageController#getItems()
     */
    @Override
    protected ArrayList<Item<Application>> initData() throws Exception {
        List<Application> apps = mgtApi.getApplications();
        ArrayList<Item<Application>> res = new ArrayList<Item<Application>>();
        for (Application app : apps) {
            boolean isEditable = isEditable(app);
            Item<Application> item = new Item<Application>(res.size(),
                    app.getName(), app, isEditable);
            item.setState(ChangeState.NONE);
            res.add(item);
        }
        return res;
    }

    /**
     * <i>Description:</i>
     *
     * @param app
     * @return
     */
    protected boolean isEditable(Application app) {
        String name = app.getName();
        return !reserved.contains(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getCopy(java.lang.Object)
     */
    @Override
    protected Application getCopy(Application obj) {
        return obj.copy();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.ConfPageController#createObj()
     */
    @Override
    protected Application createObj() {
        return new Application();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#removeItemObject(java.
     * lang.String)
     */
    @Override
    protected void removeItemObject(String name) throws Exception {
        mgtApi.removeApplication(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#saveItemObject(java.lang
     * .Object)
     */
    @Override
    protected void saveItemObject(String oldName, Application obj)
            throws Exception {
        if (oldName != null) {
            mgtApi.updateApplication(oldName, obj);
        } else {
            mgtApi.addApplication(obj);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getValidationTask(com.
     * intel.stl.ui.admin.view.ValidationDialog, com.intel.stl.ui.admin.Item)
     */
    @Override
    protected ValidationTask<Application> getValidationTask(
            ValidationDialog dialog, Item<Application> item) {
        AppValidationTask task =
                new AppValidationTask(dialog, valModel, orgItems, item, mgtApi);
        return task;
    }

}
