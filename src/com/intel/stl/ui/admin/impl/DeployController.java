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

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.swing.SwingWorker;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.ui.admin.view.AbstractConfView;
import com.intel.stl.ui.admin.view.DeployHostPanel;
import com.intel.stl.ui.admin.view.DeployHostPanel.DeployState;
import com.intel.stl.ui.admin.view.DeployPanel;
import com.intel.stl.ui.admin.view.IDeployListener;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.Context;

public class DeployController implements IDeployListener {
    private IManagementApi mgtApi;

    private final AbstractConfView<?, ?> view;

    private final DeployPanel deployPanel;

    private final List<DeployTask> tasks = new ArrayList<DeployTask>();

    private final AtomicInteger finishedTasks = new AtomicInteger();

    /**
     * Description:
     *
     * @param view
     */
    public DeployController(AbstractConfView<?, ?> view) {
        super();
        this.view = view;
        deployPanel = view.getDeployPanel();
        deployPanel.setDeployListener(this);
    }

    public void setContext(Context context, IProgressObserver observer) {
        mgtApi = context.getManagementApi();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.view.IDeployListener#onDeploy(com.intel.stl.ui
     * .admin.view.DeployHostPanel, java.util.List)
     */
    @Override
    public void onDeploy(DeployHostPanel masterSM,
            List<DeployHostPanel> standbySMs) {
        tasks.clear();
        finishedTasks.set(0);
        for (DeployHostPanel sm : standbySMs) {
            if (sm.isSelected()) {
                DeployTask task = new DeployTask(sm);
                tasks.add(task);
                task.execute();
                sm.setState(DeployState.RUNNING);
            }
        }

        if (masterSM.isSelected()) {
            DeployTask task = new DeployTask(masterSM);
            tasks.add(task);
            task.execute();
            masterSM.setState(DeployState.RUNNING);
        }

        if (tasks.isEmpty()) {
            deployPanel.setFinished();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.IDeployListener#onCancel()
     */
    @Override
    public void onCancel() {
        for (DeployTask task : tasks) {
            if (!task.isDone()) {
                task.cancel(true);
                task.getPanel().setErrorMessage(
                        UILabels.STL81115_DEPLOY_CANCELLED.getDescription());
            }
        }
    }

    public boolean isBusy() {
        for (DeployTask task : tasks) {
            if (!task.isDone()) {
                return true;
            }
        }
        return false;
    }

    public int confirmDiscard() {
        return Util.showConfirmDialog(view,
                UILabels.STL81117_ABANDON_DEPLOY.getDescription());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.IDeployListener#onClose()
     */
    @Override
    public void onClose() {
        view.showEditorCard();
    }

    class DeployTask extends SwingWorker<Void, Void> {
        private final DeployHostPanel panel;

        /**
         * Description:
         *
         * @param panel
         */
        public DeployTask(DeployHostPanel panel) {
            super();
            this.panel = panel;
        }

        /**
         * @return the view
         */
        public DeployHostPanel getPanel() {
            return panel;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.SwingWorker#doInBackground()
         */
        @Override
        protected Void doInBackground() throws Exception {
            HostInfo target = panel.getHostInfo();
            char[] password = panel.getPassword();
            Util.runInEDT(new Runnable() {
                @Override
                public void run() {
                    panel.clearPassword();
                }
            });
            mgtApi.deployTo(password, target);
            Arrays.fill(password, '*');
            return null;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.SwingWorker#done()
         */
        @Override
        protected void done() {
            try {
                get();
                panel.setState(DeployState.SUCCESS);
            } catch (InterruptedException e) {
            } catch (CancellationException e) {
                panel.setErrorMessage(
                        UILabels.STL81115_DEPLOY_CANCELLED.getDescription());
            } catch (ExecutionException e) {
                e.printStackTrace();
                Throwable cause = e.getCause();
                while (cause.getCause() != null) {
                    cause = cause.getCause();
                }
                if (cause instanceof UnknownHostException) {
                    panel.setErrorMessage(UILabels.STL81116_UNKNOWN_HOST
                            .getDescription(cause.getMessage()));
                } else {
                    panel.setErrorMessage(StringUtils.getErrorMessage(e));
                }
            } finally {
                if (finishedTasks.incrementAndGet() == tasks.size()) {
                    deployPanel.setFinished();
                }
            }
        }

    }
}
