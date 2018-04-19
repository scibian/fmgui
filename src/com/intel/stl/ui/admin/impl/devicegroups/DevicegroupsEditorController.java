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

import java.util.List;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.AbstractEditorController;
import com.intel.stl.ui.admin.view.devicegroups.DevicegroupsEditorPanel;
import com.intel.stl.ui.main.Context;

public class DevicegroupsEditorController
        extends AbstractEditorController<DeviceGroup, DevicegroupsEditorPanel>
        implements IAttributeListener {
    private final DevicegroupSelectionController selectionCtr;

    public DevicegroupsEditorController(DevicegroupsEditorPanel view) {
        super(view);
        view.setAttributeListener(this);
        selectionCtr =
                new DevicegroupSelectionController(view.getSelctionPanel());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.AbstractEditorController#setContext(com.intel
     * .stl.ui.main.Context)
     */
    @Override
    public void setContext(Context context) {
        super.setContext(context);
        selectionCtr.setContext(context);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.AbstractEditorController#setItem(com.intel
     * .stl.ui.admin.Item, com.intel.stl.ui.admin.Item[])
     */
    @Override
    public void setItem(Item<DeviceGroup> item, Item<DeviceGroup>[] items) {
        if (item == null) {
            selectionCtr.clear();
        } else {
            selectionCtr.setItem(item, items);
        }
        super.setItem(item, items);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.AbstractEditorController#itemNameChanged(java
     * .lang.String, java.lang.String)
     */
    @Override
    public void itemNameChanged(String oldName, String newName) {
        super.itemNameChanged(oldName, newName);
        selectionCtr.includeGroupNameChanged(oldName, newName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.IAttributeListener#onAdd(java
     * .long.String)
     */
    @Override
    public void onAdd(String selectorName) {
        List<IAttribute> attrs = selectionCtr.getSelections(selectorName);
        for (IAttribute attr : attrs) {
            view.addAttr(attr);
        }
        selectionCtr.clearViewSelections(selectorName);
        selectionCtr.addSelections(selectorName, attrs);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.IAttributeListener#onRemove(
     * com.intel.stl.api.management.IAttribute)
     */
    @Override
    public void onRemove(IAttribute attr) {
        selectionCtr.removeSelection(attr);
    }
}
