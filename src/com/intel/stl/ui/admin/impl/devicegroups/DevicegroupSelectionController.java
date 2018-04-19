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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.api.management.NumberNode;
import com.intel.stl.api.management.devicegroups.DGSelect;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.devicegroups.IncludeGroup;
import com.intel.stl.api.management.devicegroups.NodeDesc;
import com.intel.stl.api.management.devicegroups.NodeGUID;
import com.intel.stl.api.management.devicegroups.NodeTypeAttr;
import com.intel.stl.api.management.devicegroups.PortGUID;
import com.intel.stl.api.management.devicegroups.SystemImageGUID;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.view.devicegroups.DevicegroupSelectionPanel;
import com.intel.stl.ui.admin.view.devicegroups.DevicesPanel;
import com.intel.stl.ui.admin.view.devicegroups.ListPanel;
import com.intel.stl.ui.main.Context;

public class DevicegroupSelectionController {
    private final DevicegroupSelectionPanel view;

    private final Map<String, IResourceSelector> selectors =
            new LinkedHashMap<String, IResourceSelector>();

    private final Map<Class<? extends IAttribute>, Set<IResourceSelector>> typeSelectors =
            new HashMap<Class<? extends IAttribute>, Set<IResourceSelector>>();

    private DevicesSelector devicesSelector;

    private SelectSelector selectSelector;

    private IncludeSelector includeSelector;

    /**
     * Description:
     *
     * @param view
     */
    public DevicegroupSelectionController(DevicegroupSelectionPanel view) {
        super();
        this.view = view;
        installSelectors();
        view.setSelectors(selectors.values());
    }

    @SuppressWarnings("unchecked")
    protected void installSelectors() {
        selectors.clear();

        DevicesPanel dp = new DevicesPanel();
        devicesSelector = new DevicesSelector(dp);
        registerSelector(devicesSelector, SystemImageGUID.class, NodeGUID.class,
                PortGUID.class, NodeDesc.class, NodeTypeAttr.class);

        ListPanel<DGSelect> selectView = new ListPanel<DGSelect>();
        selectSelector = new SelectSelector(selectView);
        registerSelector(selectSelector, DGSelect.class);

        ListPanel<IncludeGroup> includeView = new ListPanel<IncludeGroup>();
        includeSelector = new IncludeSelector(includeView);
        registerSelector(includeSelector, IncludeGroup.class);
    }

    @SuppressWarnings("unchecked")
    protected void registerSelector(IResourceSelector selector,
            Class<? extends IAttribute>... types) {
        selectors.put(selector.getName(), selector);

        for (Class<? extends IAttribute> type : types) {
            Set<IResourceSelector> selectors = typeSelectors.get(type);
            if (selectors == null) {
                selectors = new HashSet<IResourceSelector>();
                typeSelectors.put(type, selectors);
            }
            selectors.add(selector);
        }
    }

    protected void registerSelector(Class<? extends IAttribute> type,
            IResourceSelector selector) {
        Set<IResourceSelector> selectors = typeSelectors.get(type);
        if (selectors == null) {
            selectors = new HashSet<IResourceSelector>();
            typeSelectors.put(type, selectors);
        }
        selectors.add(selector);
    }

    /**
     * <i>Description:</i>
     *
     * @param context
     */
    public void setContext(Context context) {
        for (IResourceSelector selector : selectors.values()) {
            selector.setContext(context, null);
        }
    }

    public void clear() {
        devicesSelector.clear();
        selectSelector.clear();
        includeSelector.clear();
        includeSelector.setGroups(new ArrayList<IncludeGroup>());
    }

    /**
     * <i>Description:</i>
     *
     * @param item
     * @param items
     */
    public void setItem(Item<DeviceGroup> item, Item<DeviceGroup>[] items) {
        DeviceGroup dg = item.getObj();

        devicesSelector.clear();

        List<NumberNode> ids = dg.getIDs();
        if (ids != null) {
            fireAddAttrs(ids, devicesSelector);
        }
        List<NodeDesc> descs = dg.getNodeDesc();
        if (descs != null) {
            fireAddAttrs(descs, devicesSelector);
        }
        List<NodeTypeAttr> types = dg.getNodeTypes();
        if (types != null) {
            fireAddAttrs(types, devicesSelector);
        }

        selectSelector.clear();
        List<DGSelect> sels = dg.getSelects();
        if (sels != null) {
            fireAddAttrs(sels, selectSelector);
        }

        includeSelector.clear();
        List<IncludeGroup> allGroups =
                new ArrayList<IncludeGroup>(items.length);
        for (int i = 0; i < items.length; i++) {
            allGroups.add(new IncludeGroup(items[i].getName()));
        }
        includeSelector.setGroups(allGroups);
        List<IncludeGroup> includeGroups = dg.getIncludeGroups();
        if (includeGroups != null) {
            fireAddAttrs(includeGroups, includeSelector);
        }
    }

    public void includeGroupNameChanged(String oldName, String newName) {
        includeSelector.groupNameChanged(oldName, newName);
    }

    protected void fireAddAttrs(List<? extends IAttribute> attr,
            IResourceSelector... selectors) {
        for (IResourceSelector selector : selectors) {
            selector.setModelSelections(attr);
        }
    }

    public List<IAttribute> getSelections(String selectorName) {
        IResourceSelector selector = selectors.get(selectorName);
        if (selector != null) {
            return selector.getViewSelections();
        } else {
            // shouldn't happen
            throw new IllegalArgumentException(
                    "Couldn't find ResourceSelector '" + selectorName + "'");
        }
    }

    /**
     * <i>Description:</i>
     *
     * @param attrs
     */
    public void addSelections(String selectorName, List<IAttribute> attrs) {
        IResourceSelector selector = selectors.get(selectorName);
        if (selector != null) {
            fireAddAttrs(attrs, selector);
        } else {
            // shouldn't happen
            throw new IllegalArgumentException(
                    "Couldn't find ResourceSelector '" + selectorName + "'");
        }
    }

    /**
     * <i>Description:</i>
     *
     * @param attr
     */
    public void removeSelection(IAttribute attr) {
        Set<IResourceSelector> selectors = typeSelectors.get(attr.getClass());
        if (selectors != null) {
            fireRemoveAttrs(attr, selectors.toArray(new IResourceSelector[0]));
        }
    }

    protected void fireRemoveAttrs(IAttribute attr,
            IResourceSelector... selectors) {
        for (IResourceSelector selector : selectors) {
            selector.removeModelSelection(attr);
        }
    }

    /**
     * <i>Description:</i>
     *
     */
    public void clearViewSelections(String selectorName) {
        IResourceSelector selector = selectors.get(selectorName);
        if (selector != null) {
            selector.clearViewSelections();
        }
    }

}
