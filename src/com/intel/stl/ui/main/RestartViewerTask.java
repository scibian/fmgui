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

import java.awt.Component;
import java.util.List;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.framework.AbstractTask;

public class RestartViewerTask extends AbstractTask<String, Context, Void> {

    private final SubnetManager subnetMgr;

    private final SubnetDescription subnet;

    private final String existingHostId;

    private final String newHostId;

    public RestartViewerTask(String model, SubnetManager subnetMgr,
            SubnetDescription subnet, String existingHostId, String newHostId) {
        super(model);
        this.subnetMgr = subnetMgr;
        this.subnet = subnet;
        this.existingHostId = existingHostId;
        this.newHostId = newHostId;
    }

    @Override
    public Context processInBackground(Context context) throws Exception {
        IFabricController controller = context.getController();
        Context newContext = subnetMgr.createContext(subnet, controller);
        return newContext;
    }

    @Override
    public void onTaskSuccess(final Context newContext) {
        final IFabricController restartingController =
                (IFabricController) getController();
        String msg = getModel();
        Util.showWarningMessage((Component) restartingController.getView(), msg);
        // subnetMgr.resetHost(existingHostId, newHostId, restartingController,
        // newContext);
        restartingController.resetContext(newContext);
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        IFabricController thisController = (IFabricController) getController();
        Util.showError((Component) thisController.getView(), caught);
        Context context = thisController.getCurrentContext();
        context.close();
    }

    @Override
    public void onFinally() {

    }

    @Override
    protected void processIntermediateResults(List<Void> intermediateResults) {
    }

}