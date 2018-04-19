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

package com.intel.stl.ui.admin.impl.virtualfabrics;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.management.applications.Application;
import com.intel.stl.api.management.applications.ApplicationException;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.devicegroups.DeviceGroupException;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.AbstractEditorController;
import com.intel.stl.ui.admin.view.virtualfabrics.VirtualFabricsEditorPanel;

public class VirtualFabricsEditorController extends
        AbstractEditorController<VirtualFabric, VirtualFabricsEditorPanel> {
    /**
     * Description:
     * 
     * @param view
     */
    public VirtualFabricsEditorController(VirtualFabricsEditorPanel view) {
        super(view);
    }

    @Override
    public void initData() throws Exception {
        List<String> applications = getApplicationNames();
        view.setApplicationNames(applications);

        List<String> devicegroups = getDeviceGroupNames();
        view.setDeviceGroupNames(devicegroups);
    }

    protected List<String> getApplicationNames() throws ApplicationException {
        List<Application> apps = mgtApi.getApplications();
        List<String> names = new ArrayList<String>(apps.size());
        for (Application app : apps) {
            names.add(app.getName());
        }
        return names;
    }

    protected List<String> getDeviceGroupNames() throws DeviceGroupException {
        List<DeviceGroup> groups = mgtApi.getDeviceGroups();
        List<String> names = new ArrayList<String>(groups.size());
        for (DeviceGroup group : groups) {
            names.add(group.getName());
        }
        return names;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.AbstractEditorController#setItem(com.intel
     * .stl.ui.admin.Item, com.intel.stl.ui.admin.Item[])
     */
    @Override
    public void setItem(Item<VirtualFabric> item, Item<VirtualFabric>[] items) {
        super.setItem(item, items);
    }

}
