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

package com.intel.stl.ui.main;

import static com.intel.stl.ui.main.FabricController.PROGRESS_NOTE_PROPERTY;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.ProgressObserver;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.framework.AbstractTask;
import com.intel.stl.ui.monitor.tree.FVTreeManager;

public class SubnetRefreshTask extends AbstractTask<FabricModel, Void, String> {

    private static Logger log = LoggerFactory
            .getLogger(SubnetRefreshTask.class);

    private final Context context;

    private final List<IPageController> pages;

    private final FVTreeManager builder;

    public SubnetRefreshTask(FabricModel model, FVTreeManager builder,
            List<IPageController> pages, Context context) {
        super(model);
        this.pages = pages;
        this.builder = builder;
        this.context = context;
    }

    @Override
    public Void processInBackground(Context context) throws Exception {
        log.info("Refresh subnet '" + model.getCurrentSubnet() + "'");
        // clear all caches, and re-init DB data
        context.reset();

        builder.setDirty();
        for (int i = 0; i < pages.size(); i++) {
            IPageController page = pages.get(i);
            setProgressNote(UILabels.STL10111_REFRESHING_PAGE
                    .getDescription(page.getName()));
            int estimatedWork = page.getRefreshWeight().getWeight();
            ProgressObserver observer =
                    new ProgressObserver(this, estimatedWork);
            page.onRefresh(observer);
            observer.onFinish();
        }

        return null;
    }

    @Override
    public void onTaskSuccess(Void result) {
        getController().notifyModelChanged();
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        model.setErrorMessage(StringUtils.getErrorMessage(caught));
        getController().notifyModelUpdateFailed(caught);
    }

    @Override
    public void onFinally() {
    }

    @Override
    public void processIntermediateResults(List<String> intermediateResults) {
    }

    protected void setProgressNote(String note) {
        firePropertyChange(PROGRESS_NOTE_PROPERTY, null, note);
    }
}
