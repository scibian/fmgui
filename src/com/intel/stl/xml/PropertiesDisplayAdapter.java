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

package com.intel.stl.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.bind.MarshalException;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.intel.stl.api.configuration.PropertyCategory;
import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.api.configuration.ResourceType;

/**
 * Converts the XML elements underneath PropertiesDisplay during
 * marshaling/unmarshaling to/from the XML types that represent the options for
 * the Properties Display pages (Performance tab in the UI) into a map that can
 * be easily processed in the UI. Also, this needs to be done because we cannot
 * expose the annotated JAXB types to the API (it would create a dependency for
 * the UI).
 */
public class PropertiesDisplayAdapter extends
        XmlAdapter<PropertiesDisplay, Map<ResourceType, List<PropertyGroup>>> {

    @Override
    public Map<ResourceType, List<PropertyGroup>> unmarshal(
            PropertiesDisplay displayOptions) throws Exception {
        Map<ResourceType, List<PropertyGroup>> options =
                new HashMap<ResourceType, List<PropertyGroup>>();
        List<ResourceClassType> resources =
                displayOptions.getResourceClassTypes();
        for (ResourceClassType resource : resources) {
            ResourceType resourceType = resource.getResourceType();
            List<GroupType> groups = resource.getGroups();
            List<PropertyGroup> propertyGroups =
                    new ArrayList<PropertyGroup>(groups.size());
            for (GroupType group : groups) {
                PropertyGroup propertyGroup = new PropertyGroup();
                propertyGroup.setName(group.getName());
                propertyGroup.setTitle(group.getTitle());
                propertyGroup.setDisplayed(group.getDisplayed());
                List<ResourceCategoryType> categories = group.getCategory();
                List<PropertyCategory> resourceCategories =
                        new ArrayList<PropertyCategory>(categories.size());
                for (ResourceCategoryType category : categories) {
                    PropertyCategory resourceCategory = new PropertyCategory();
                    resourceCategory.setResourceCategory(category
                            .getResourceCategory());
                    resourceCategory.setKeyHeader(category.getValue());
                    resourceCategory.setShowHeader(category.isShowHeader());
                    resourceCategory.setValueHeader(category.getValueHeader());

                    resourceCategories.add(resourceCategory);
                }
                propertyGroup.setCategories(resourceCategories);

                propertyGroups.add(propertyGroup);
            }
            options.put(resourceType, propertyGroups);
        }
        return options;
    }

    @Override
    public PropertiesDisplay marshal(
            Map<ResourceType, List<PropertyGroup>> options) throws Exception {
        PropertiesDisplay displayOptions = new PropertiesDisplay();
        List<ResourceClassType> classTypes =
                displayOptions.getResourceClassTypes();
        Iterator<ResourceType> it = options.keySet().iterator();
        while (it.hasNext()) {
            ResourceType resourceType = it.next();
            List<PropertyGroup> groups = options.get(resourceType);
            ResourceClassType resource;
            switch (resourceType) {
                case HFI:
                    resource = new HfiType();
                    resource.setGroups(createGroups(groups,
                            new HfiTypeFactory()));
                    break;
                case PORT:
                    resource = new PortType();
                    resource.setGroups(createGroups(groups,
                            new PortTypeFactory()));
                    break;
                case SWITCH:
                    resource = new SwitchType();
                    resource.setGroups(createGroups(groups,
                            new SwitchTypeFactory()));
                    break;
                default:
                    resource = null;
            }
            classTypes.add(resource);

        }
        return displayOptions;
    }

    private List<GroupType> createGroups(List<PropertyGroup> propertyGroups,
            XmlTypeFactory factory) throws Exception {
        List<GroupType> groups =
                new ArrayList<GroupType>(propertyGroups.size());
        for (PropertyGroup propertyGroup : propertyGroups) {
            GroupType group = factory.createGroupTypeFrom(propertyGroup);
            List<PropertyCategory> propertyCategories =
                    propertyGroup.getCategories();
            if (propertyCategories != null) {
                List<ResourceCategoryType> categories = group.getCategory();

                for (PropertyCategory propertyCategory : propertyCategories) {
                    ResourceCategoryType category =
                            factory.createResourceCategoryTypeFrom(propertyCategory);
                    categories.add(category);
                }
            }
            groups.add(group);
        }
        return groups;
    }

    private abstract class XmlTypeFactory {
        protected void populateGroupType(GroupType groupType,
                PropertyGroup group) {
            groupType.setName(group.getName());
            groupType.setTitle(group.getTitle());
            groupType.setDisplayed(group.isDisplayed());
        }

        protected void populateResourceCategoryType(
                ResourceCategoryType categoryType, PropertyCategory category) {
            categoryType.setShowHeader(category.isShowHeader());
            categoryType.setValue(category.getKeyHeader());
            categoryType.setValueHeader(category.getValueHeader());
        }

        protected abstract GroupType createGroupTypeFrom(PropertyGroup group);

        protected abstract ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception;
    }

    private class HfiTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            HfiGroupType hfiGroup = new HfiGroupType();
            populateGroupType(hfiGroup, group);
            return hfiGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception {
            HfiCategoryType hfiCategory = new HfiCategoryType();
            populateResourceCategoryType(hfiCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                HfiCategory value = HfiCategory.fromValue(rc.name());
                hfiCategory.setName(value);
            }
            return hfiCategory;
        }
    }

    private class PortTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            PortGroupType portGroup = new PortGroupType();
            populateGroupType(portGroup, group);
            return portGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws Exception {
            PortCategoryType portCategory = new PortCategoryType();
            populateResourceCategoryType(portCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                PortCategory value = PortCategory.fromValue(rc.name());
                portCategory.setName(value);
            }
            return portCategory;
        }
    }

    private class SwitchTypeFactory extends XmlTypeFactory {

        @Override
        public GroupType createGroupTypeFrom(PropertyGroup group) {
            SwitchGroupType switchGroup = new SwitchGroupType();
            populateGroupType(switchGroup, group);
            return switchGroup;
        }

        @Override
        public ResourceCategoryType createResourceCategoryTypeFrom(
                PropertyCategory category) throws MarshalException {
            SwitchCategoryType switchCategory = new SwitchCategoryType();
            populateResourceCategoryType(switchCategory, category);
            ResourceCategory rc = category.getResourceCategory();
            if (rc != null) {
                SwitchCategory value = SwitchCategory.fromValue(rc.name());
                switchCategory.setName(value);
            }
            return switchCategory;
        }
    }
}
