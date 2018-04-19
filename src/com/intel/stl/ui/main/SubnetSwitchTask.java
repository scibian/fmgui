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

import static com.intel.stl.api.configuration.UserSettings.PROPERTY_LASTSUBNETACCESSED;
import static com.intel.stl.api.configuration.UserSettings.SECTION_USERSTATE;
import static com.intel.stl.ui.common.UILabels.STL10108_INIT_PAGE;
import static com.intel.stl.ui.main.FabricController.PROGRESS_NOTE_PROPERTY;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.IContextAware;
import com.intel.stl.ui.common.ProgressObserver;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.framework.AbstractTask;
import com.intel.stl.ui.framework.IController;

public class SubnetSwitchTask extends
        AbstractTask<FabricModel, Context, String> {

    private static Logger log = LoggerFactory.getLogger(SubnetSwitchTask.class);

    private boolean previousContextCleared;

    private Context oldContext;

    private final List<ContextSwitchTask> subtasks;

    private final List<IContextAware> backgroundContextPages;

    /**
     * IContextAware that run in current thread in order
     */
    private final List<IContextAware> foregroundContextPages;

    private Exception foregroundFailure;

    private final Context newContext;

    private final Object mutex = new Object();

    public SubnetSwitchTask(FabricModel model, Context newContext,
            List<IContextAware> foregroundContextPages,
            List<IContextAware> backgroundContextPages) {
        super(model);
        this.newContext = newContext;
        this.foregroundContextPages = foregroundContextPages;
        this.backgroundContextPages = backgroundContextPages;
        this.subtasks = new ArrayList<ContextSwitchTask>();
    }

    /**
     * @return the newContext
     */
    public Context getNewContext() {
        return newContext;
    }

    /**
     * @return the oldContext
     */
    public Context getOldContext() {
        return oldContext;
    }

    /**
     * @param oldContext
     *            the oldContext to set
     */
    public void setOldContext(Context oldContext) {
        this.oldContext = oldContext;
    }

    @Override
    public Context processInBackground(Context context) throws Exception {
        foregroundFailure = null;
        oldContext = context;
        previousContextCleared = false;
        newContext.initialize();
        // Prepare context
        // - apply random values for demo purpose
        boolean addRandomValues = model.isAddRandomValues();
        newContext.setRandom(addRandomValues);
        newContext.getPerformanceApi().setRandom(addRandomValues);

        final FabricController controller = (FabricController) getController();
        SubnetDescription newSubnet = newContext.getSubnetDescription();
        SubnetDescription currentSubnet = null;
        if (oldContext != null) {
            currentSubnet = oldContext.getSubnetDescription();
            log.info("Switching to subnet '" + newSubnet + "' from '"
                    + currentSubnet + "'...");
        } else {
            log.info("Switching to subnet '" + newSubnet + "'...");
        }

        publishProgressNote(UILabels.STL10105_CREATE_CONTEXT.getDescription());

        // any connection issues should have occurred by now
        // start switching subnets

        // only switch context when we are sure the new subnet is working
        if (oldContext != null) {
            log.info("Clearing context for '" + currentSubnet + "'");
            publishProgressNote(UILabels.STL10107_CLEAR_CONTEXT
                    .getDescription());
            oldContext.close();
            previousContextCleared = true;
        }

        // run IContextAware in current thread in order
        for (IContextAware page : foregroundContextPages) {
            int work = page.getContextSwitchWeight().getWeight();
            ProgressObserver observer = new ProgressObserver(this, work);
            try {
                publishProgressNote(STL10108_INIT_PAGE.getDescription(page
                        .getName()));
                page.setContext(newContext, observer);
                publishProgressNote(UILabels.STL10112_INIT_PAGE_COMPLETED
                        .getDescription(page.getName()));
            } catch (Exception e) {
                if (foregroundFailure == null) {
                    // only remember the first failure
                    foregroundFailure = e;
                }
            } finally {
                observer.onFinish();
            }
        }

        // run IContextAware in parallel in background
        for (IContextAware page : backgroundContextPages) {
            ContextSwitchTask subtask =
                    new ContextSwitchTask(model, newContext, this, page);
            subtasks.add(subtask);
            controller.submitTask(subtask);
        }
        boolean allDone = false;
        while (!allDone) {
            waitForSubtasks();
            boolean completed = true;
            for (ContextSwitchTask subtask : subtasks) {
                if (subtask.isDone() || subtask.isCancelled()) {
                } else {
                    completed = false;
                }
            }
            if (completed) {
                allDone = true;
            }
        }

        return newContext;
    }

    private void waitForSubtasks() {
        try {
            synchronized (mutex) {
                mutex.wait(500L);
            }
        } catch (InterruptedException e) {
        }
    }

    /**
     * This method is invoked by a ContextSwitchTask to mark its end
     */
    public void checkSubtasks() {
        synchronized (mutex) {
            mutex.notify();
        }
    }

    @Override
    public void onTaskSuccess(Context result) {
        if (foregroundFailure != null) {
            onTaskFailure(foregroundFailure);
            return;
        }

        for (ContextSwitchTask subtask : subtasks) {
            if (subtask.hasException()) {
                onTaskFailure(subtask.getExecutionException());
                return;
            }
        }
        IController controller = getController();
        controller.setContext(result);
        // Update the model
        SubnetDescription oldSubnet = model.getCurrentSubnet();
        SubnetDescription resSubnet = result.getSubnetDescription();
        model.setCurrentSubnet(resSubnet);
        model.setPreviousSubnet(oldSubnet);
        UserSettings userSettings = result.getUserSettings();
        Properties usrState = null;
        if (userSettings != null) {
            usrState = userSettings.getPreferences().get(SECTION_USERSTATE);
            if (usrState == null) {
                usrState = new Properties();
                userSettings.getPreferences().put(SECTION_USERSTATE, usrState);
            }
        }

        if (usrState != null) {
            usrState.setProperty(PROPERTY_LASTSUBNETACCESSED,
                    resSubnet.getName());
        }
        controller.notifyModelChanged();
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        SubnetDescription newSubnet = newContext.getSubnetDescription();
        StringBuffer sb = new StringBuffer();
        sb.append(UILabels.STL50050_CONNECTION_FAIL.getDescription(
                newSubnet.getName(), newSubnet.getCurrentFE().getHost()));
        sb.append("\n" + StringUtils.getErrorMessage(caught));
        if (!previousContextCleared && oldContext != null) {
            SubnetDescription currentSubnet = oldContext.getSubnetDescription();
            sb.append("\n");
            sb.append(UILabels.STL50051_USE_OLD_SUBNET.getDescription(
                    currentSubnet.getName(), currentSubnet.getCurrentFE()
                            .getHost()));
        }
        model.setErrorMessage(sb.toString());
        getController().notifyModelUpdateFailed(caught);
    }

    @Override
    public void onFinally() {
    }

    @Override
    public void processIntermediateResults(List<String> intermediateResults) {
    }

    public void publishProgressNote(String note) {
        firePropertyChange(PROGRESS_NOTE_PROPERTY, null, note);
    }
}
