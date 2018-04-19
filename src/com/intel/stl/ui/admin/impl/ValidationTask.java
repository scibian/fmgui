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

import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.ValidationItem;
import com.intel.stl.ui.common.ValidationModel;

public abstract class ValidationTask<T> extends
        SwingWorker<Integer, ValidationItem<T>> {
    protected final ValidationDialog dialog;

    protected ValidationModel<T> model;

    protected final List<Item<T>> items;

    protected final Item<T> toCheck;

    public ValidationTask(ValidationDialog dialog, ValidationModel<T> model,
            List<Item<T>> items, Item<T> toCheck) {
        this.dialog = dialog;
        this.model = model;
        this.items = items;
        this.toCheck = toCheck;
    }

    @Override
    protected void done() {
        Integer result;
        try {
            result = get();
            onTaskSuccess(result);
        } catch (InterruptedException e) {
            onTaskFailure(e);
        } catch (ExecutionException e) {
            onTaskFailure(e.getCause());
        } finally {
            onFinally();
        }
    }

    public void onTaskSuccess(Integer result) {
        if (result == 0) {
            dialog.showMessage(UILabels.STL81103_NO_ISSUES_FOUND
                    .getDescription());
            dialog.enableOk(true);
        } else {
            String msg =
                    result == 1 ? UILabels.STL81104_ONE_ISSUE_FOUND
                            .getDescription() : UILabels.STL81102_ISSUES_FOUND
                            .getDescription(result);
            dialog.showMessage(msg);
            dialog.enableOk(false);
        }
    }

    public void onTaskFailure(Throwable caught) {
        caught.printStackTrace();
        dialog.showMessage(StringUtils.getErrorMessage(caught));
        dialog.enableOk(false);
    }

    public void onFinally() {
        dialog.reportProgress("100%");
        dialog.stopProgress();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.framework.AbstractTask#processIntermediateResults(java
     * .util.List)
     */
    @Override
    protected void process(List<ValidationItem<T>> intermediateResults) {
        for (ValidationItem<T> issue : intermediateResults) {
            model.addEntry(issue);
        }
        model.fireTableDataChanged();
        dialog.updateIssues();
    }

    protected void fixedIssue(ValidationItem<T> issue) {
        model.removeEntry(issue);
        model.fireTableDataChanged();
        onTaskSuccess(model.getRowCount());
        dialog.updateIssues();
    }
}
