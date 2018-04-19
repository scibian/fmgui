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

package com.intel.stl.ui.configuration;

import java.util.List;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.framework.AbstractTask;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.model.DeviceProperties;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.model.PropertyGroupViz;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public class GetDevicePropertiesTask extends
        AbstractTask<DeviceProperties, Void, Void> {

    private final FVResourceNode node;

    private final IProgressObserver observer;

    /**
     * Description: background task in charge of retrieving property information
     * for a node
     * 
     * @param model
     */
    public GetDevicePropertiesTask(DeviceProperties model, FVResourceNode node,
            IProgressObserver observer) {
        super(model);
        this.node = node;
        this.observer = observer;
    }

    @Override
    public Void processInBackground(Context context) throws Exception {
        UserSettings userSettings = context.getUserSettings();
        List<PropertyGroup> groups = null;
        if (userSettings != null) {
            groups =
                    userSettings.getPropertiesDisplayOptions().get(
                            model.getResourceType());
        }
        CategoryProcessorContext categoryCtx =
                new CategoryProcessorContext(node, context);

        // Categories must be selected in the model first and then optionally
        // PropertyGroups can be defined and you can specify which categories
        // you want included in each group
        if (groups != null) {
            for (PropertyGroup group : groups) {
                if (group.isDisplayed()) {
                    DevicePropertyGroup pageGroup = createGroup(group);
                    for (PropertyCategory category : group.getCategories()) {
                        model.addCategory(category);
                        pageGroup.addCategory(category.getResourceCategory());
                    }
                    model.addPropertyGroup(pageGroup);
                }
            }
        }

        for (DevicePropertyCategory pageCategory : model.getCategories()) {
            pageCategory.populate(categoryCtx);
        }
        return null;
    }

    @Override
    public void onTaskSuccess(Void result) {
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        caught.printStackTrace();
    }

    @Override
    public void onFinally() {
        if (observer != null) {
            observer.onFinish();
        }
    }

    @Override
    public void processIntermediateResults(List<Void> intermediateResults) {
    }

    protected DevicePropertyGroup createGroup(PropertyGroup propertyGroup) {
        DevicePropertyGroup group = new DevicePropertyGroup();
        String title = propertyGroup.getTitle();
        if (title == null || title.length() == 0) {
            String groupName = propertyGroup.getName();
            title = PropertyGroupViz.getPropertyGroupViz(groupName).getTitle();
        }
        group.setGroupName(title);
        return group;
    }

}
