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

package com.intel.stl.ui.admin.impl.devicegroups;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.ui.admin.ChangeState;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.AbstractEditorController;
import com.intel.stl.ui.admin.impl.ConfPageController;
import com.intel.stl.ui.admin.impl.ValidationTask;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.admin.view.devicegroups.DevicegroupsEditorPanel;
import com.intel.stl.ui.admin.view.devicegroups.DevicegroupsSubpageView;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;

public class DevicegroupsPageController
        extends ConfPageController<DeviceGroup, DevicegroupsEditorPanel> {
    private Set<String> reserved;

    /**
     * Description:
     *
     * @param name
     * @param description
     * @param icon
     * @param view
     */
    public DevicegroupsPageController(String name, String description,
            ImageIcon icon, DevicegroupsSubpageView view) {
        super(name, description, icon, view);
    }

    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getAdminDg();
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
        edtCtr.setContext(context);
        reserved = mgtApi.getReservedDeviceGroups();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#creatEditorController(
     * com.intel.stl.ui.admin.view.AbstractEditorPanel)
     */
    @Override
    protected AbstractEditorController<DeviceGroup, DevicegroupsEditorPanel> creatEditorController(
            DevicegroupsEditorPanel editorPanel) {
        return new DevicegroupsEditorController(editorPanel);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.ConfPageController#getItems()
     */
    @Override
    protected ArrayList<Item<DeviceGroup>> initData() throws Exception {
        List<DeviceGroup> groups = mgtApi.getDeviceGroups();
        ArrayList<Item<DeviceGroup>> res = new ArrayList<Item<DeviceGroup>>();
        for (DeviceGroup group : groups) {
            // System.out.println(group);
            boolean isEditable = isEditable(group);
            Item<DeviceGroup> item = new Item<DeviceGroup>(res.size(),
                    group.getName(), group, isEditable);
            item.setState(ChangeState.NONE);
            res.add(item);
        }
        return res;
    }

    /**
     * <i>Description:</i>
     *
     * @param group
     * @return
     */
    private boolean isEditable(DeviceGroup group) {
        String name = group.getName();
        return !reserved.contains(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getCopy(java.lang.Object)
     */
    @Override
    protected DeviceGroup getCopy(DeviceGroup obj) {
        return obj.copy();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.ConfPageController#createObj()
     */
    @Override
    protected DeviceGroup createObj() {
        return new DeviceGroup();
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
        mgtApi.removeDeviceGroup(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getValidationTask(com.
     * intel.stl.ui.admin.view.ValidationDialog, com.intel.stl.ui.admin.Item)
     */
    @Override
    protected ValidationTask<DeviceGroup> getValidationTask(
            ValidationDialog dialog, Item<DeviceGroup> item) {
        DGValidationTask task =
                new DGValidationTask(dialog, valModel, orgItems, item, mgtApi);
        return task;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#saveItemObject(java.lang
     * .String, java.lang.Object)
     */
    @Override
    protected void saveItemObject(String oldName, DeviceGroup obj)
            throws Exception {
        if (oldName != null) {
            mgtApi.updateDeviceGroup(oldName, obj);
        } else {
            mgtApi.addDeviceGroup(obj);
        }
    }

}
