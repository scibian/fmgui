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
import com.intel.stl.ui.common.IPageController;

/**
 * There are two types of selections.
 * <ul>
 * <li>View Selections are the selections on a view. e.g. on a tree or list, a
 * user may select one or more items
 * <li>Model Selections are the selections already take effect, i.e. the
 * selections used in a DeviceGroup's config.
 * </ul>
 * A view selection will become a model selection if we click the "Add" button
 * to add it to a DeviceGroup's config.
 */
public interface IResourceSelector extends IPageController {
    /**
     * 
     * <i>Description:</i>
     * 
     * @return current selected selections on the view
     */
    List<IAttribute> getViewSelections();

    /**
     * 
     * <i>Description:</i> Set the model selections. Typically used when we init
     * the ResourceSelector with a DeviceGroup
     * 
     * @param attrs
     */
    void setModelSelections(List<? extends IAttribute> attrs);

    /**
     * 
     * <i>Description:</i> remove a model selection. Useful when we remove one
     * attribute from a DeviceGroup, and then update it on the ResourceSelector
     * 
     * @param attr
     */
    void removeModelSelection(IAttribute attr);

    /**
     * 
     * <i>Description:</i> clear the selections on the view
     * 
     */
    void clearViewSelections();
}
