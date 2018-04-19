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

package com.intel.stl.ui.model;

import static com.intel.stl.ui.common.UILabels.STL90001_DEVICE_TYPE_NOT_SET;
import static com.intel.stl.ui.common.UILabels.STL90002_DEVICE_CATEGORY_NOT_APPLICABLE;
import static com.intel.stl.ui.common.UILabels.STL90003_DEVICE_CATEGORY_NOT_SELECTED;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.api.configuration.ResourceType;

public class DeviceProperties extends PropertySet<DevicePropertyGroup> {

    private final EnumMap<DeviceProperty, DevicePropertyItem> propertyMap =
            new EnumMap<DeviceProperty, DevicePropertyItem>(
                    DeviceProperty.class);

    private final EnumMap<ResourceCategory, DevicePropertyCategory> categoryMap =
            new EnumMap<ResourceCategory, DevicePropertyCategory>(
                    ResourceCategory.class);

    private ResourceType resourceType;

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType deviceType) {
        this.resourceType = deviceType;
        clear();
    }

    public DevicePropertyItem getProperty(DeviceProperty property) {
        return propertyMap.get(property);
    }

    public List<DevicePropertyCategory> getCategories() {
        List<DevicePropertyCategory> categoryList =
                new ArrayList<DevicePropertyCategory>(categoryMap.size());
        categoryList.addAll(categoryMap.values());
        return categoryList;
    }

    public void addCategory(PropertyCategory category) {
        if (resourceType == null) {
            throw new RuntimeException(
                    STL90001_DEVICE_TYPE_NOT_SET.getDescription());
        }
        ResourceCategory resourceCategory = category.getResourceCategory();
        if (!resourceCategory.isApplicableTo(resourceType)) {
            throw new RuntimeException(
                    STL90002_DEVICE_CATEGORY_NOT_APPLICABLE.getDescription(
                            resourceCategory.name(), resourceType.name()));
        }
        DevicePropertyCategory existing = categoryMap.get(category);
        if (existing != null) {
            return;
        }
        DevicePropertyCategory newCategory =
                new PropertyPageCategoryProxy(category);
        categoryMap.put(resourceCategory, newCategory);
    }

    public DevicePropertyCategory getCategory(ResourceCategory category) {
        return categoryMap.get(category);
    }

    @Override
    public void addPropertyGroup(DevicePropertyGroup propertyGroup) {
        for (ResourceCategory category : propertyGroup.getCategories()) {
            DevicePropertyCategory propertyCategory = categoryMap.get(category);
            if (propertyCategory == null) {
                throw new RuntimeException(
                        STL90003_DEVICE_CATEGORY_NOT_SELECTED.getDescription(
                                category.name(), resourceType.name()));
            }
            propertyGroup.addPropertyCategory(propertyCategory);
        }
        super.addPropertyGroup(propertyGroup);
    }

    private class PropertyPageCategoryProxy extends DevicePropertyCategory {

        public PropertyPageCategoryProxy(PropertyCategory category) {
            super(category);
        }

        @Override
        public void addPropertyItem(DevicePropertyItem item) {
            propertyMap.put(item.getKey(), item);
            super.addPropertyItem(item);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.PropertySet#clear()
     */
    @Override
    public void clear() {
        super.clear();
        propertyMap.clear();
        categoryMap.clear();
    }

}
