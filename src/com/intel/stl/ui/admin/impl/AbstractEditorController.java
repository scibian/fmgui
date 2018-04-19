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

package com.intel.stl.ui.admin.impl;

import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.ui.admin.InvalidEditException;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.view.AbstractEditorPanel;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.publisher.TaskScheduler;

public class AbstractEditorController<T, E extends AbstractEditorPanel<T>> {
    protected final E view;

    protected IManagementApi mgtApi;

    protected TaskScheduler taskScheduler;

    /**
     * Description:
     *
     * @param view
     */
    public AbstractEditorController(E view) {
        super();
        this.view = view;
    }

    public void setContext(Context context) {
        mgtApi = context.getManagementApi();
        taskScheduler = context.getTaskScheduler();
    }

    public void initData() throws Exception {
    }

    public void setItem(Item<T> item, Item<T>[] items) {
        if (item == null) {
            view.clear();
            return;
        } else if (items.length < 1) {
            // this shouldn't happen
            throw new IllegalArgumentException("Invalid item array");
        }

        view.setItem(item, items);
    }

    public void selectItemName() {
        view.selectItemName();
    }

    public void updateItem(Item<T> item) throws InvalidEditException {
        view.updateItem(item);
    }

    public void itemNameChanged(String oldName, String newName) {
        view.itemNameChanged(oldName, newName);
    }
}
