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

import java.util.Collections;
import java.util.List;

import com.intel.stl.api.management.devicegroups.IncludeGroup;
import com.intel.stl.ui.admin.view.devicegroups.ListPanel;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.main.Context;

public class IncludeSelector extends ListSelector<IncludeGroup> {
    private List<IncludeGroup> groups;

    /**
     * Description:
     *
     * @param view
     */
    public IncludeSelector(ListPanel<IncludeGroup> view) {
        super(view);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IPageController#getDescription()
     */
    @Override
    public String getDescription() {
        return UILabels.STL81052_DG_INCLUDE_DESC.getDescription();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.IContextAware#getName()
     */
    @Override
    public String getName() {
        return STLConstants.K2138_INCLUDE.getValue();
    }

    /**
     * @param groups
     *            the groups to set
     */
    public synchronized void setGroups(List<IncludeGroup> groups) {
        model = createModel(groups);
        view.setModel(model);
    }

    public synchronized void groupNameChanged(String oldName, String newName) {
        for (int i = 0; i < model.getSize(); i++) {
            SelectionWrapper<IncludeGroup> element = model.getElementAt(i);
            if (element.getObj().getObject().equals(oldName)) {
                IncludeGroup newGroup = new IncludeGroup(newName);
                SelectionWrapper<IncludeGroup> newElement =
                        new SelectionWrapper<IncludeGroup>(newGroup,
                                getElementDesc(newGroup));
                model.set(i, newElement);
                break;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.ListSelector#getListData(com
     * .intel.stl.ui.main.Context)
     */
    @Override
    protected List<IncludeGroup> getListData(Context context) {
        // we do not init data here. instead we always set list data via method
        // #setGroups
        return Collections.emptyList();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.devicegroups.ListSelector#getElementDesc(
     * com.intel.stl.api.management.IAttribute)
     */
    @Override
    protected String getElementDesc(IncludeGroup element) {
        return element.getValue();
    }

}
