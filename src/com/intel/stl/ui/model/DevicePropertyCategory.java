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

import java.util.Collection;
import java.util.EnumMap;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.ui.configuration.ICategoryProcessorContext;
import com.intel.stl.ui.configuration.ResourceCategoryMap;

public class DevicePropertyCategory extends ModelCollection<DevicePropertyItem>
        implements IPropertyCategory<DevicePropertyItem> {

    private final EnumMap<DeviceProperty, DevicePropertyItem> propertyMap =
            new EnumMap<DeviceProperty, DevicePropertyItem>(
                    DeviceProperty.class);

    private final PropertyCategory category;

    private final ResourceCategoryMap categoryMap;

    public DevicePropertyCategory(PropertyCategory category) {
        this.category = category;
        ResourceCategory resourceCat = category.getResourceCategory();
        this.categoryMap =
                ResourceCategoryMap.getResourceCategoryMapFor(resourceCat);
    }

    public ResourceCategory getCategory() {
        return category.getResourceCategory();
    }

    @Override
    public String getKeyHeader() {
        String keyHeader = null;
        if (category.isShowHeader()) {
            String usrKeyHeader = category.getKeyHeader();
            if (usrKeyHeader == null || usrKeyHeader.length() == 0) {
                keyHeader = categoryMap.getDefaultKeyHeader();
            } else {
                keyHeader = usrKeyHeader;
            }
        }
        return keyHeader;
    }

    @Override
    public String getValueHeader() {
        String valueHeader = null;
        if (category.isShowHeader()) {
            String usrValueHeader = category.getKeyHeader();
            if (usrValueHeader == null || usrValueHeader.length() == 0) {
                valueHeader = categoryMap.getDefaultValueHeader();
            } else {
                valueHeader = usrValueHeader;
            }
        }
        return valueHeader;
    }

    public EnumMap<DeviceProperty, DevicePropertyItem> getProperties() {
        return propertyMap;
    }

    public int getNumProperties() {
        return propertyMap.size();
    }

    public DevicePropertyItem getProperty(DeviceProperty property) {
        DevicePropertyItem res = propertyMap.get(property);
        if (res != null) {
            return res;
        } else {
            throw new IllegalArgumentException("Couldn't find DeviceProperty "
                    + property);
        }
    }

    public void addPropertyItem(DevicePropertyItem item) {
        propertyMap.put(item.getKey(), item);
        addItem(item);
    }

    public void populate(ICategoryProcessorContext context) {
        categoryMap.getProcessor().process(context, this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.IPropertyCategory#getItems()
     */
    @Override
    public Collection<DevicePropertyItem> getItems() {
        return getList();
    }

}
