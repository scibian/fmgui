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

package com.intel.stl.ui.wizards.view;

import java.util.List;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.wizards.impl.IMultinetWizardListener;
import com.intel.stl.ui.wizards.impl.IMultinetWizardTask;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;

public interface IMultinetWizardView extends IWizardView {

    void setSubnets(List<SubnetDescription> subnets);

    void addSubnet(SubnetDescription subnet);

    void setSelectedSubnet(SubnetDescription subnet);

    void resetSubnet(SubnetDescription subnet);

    void setTasks(List<IMultinetWizardTask> tasks);

    void setEnableForAllTasks(boolean enable);

    int getSelectedTask();

    void setSelectedTask(int taskNum);

    void setWizardViewType(WizardViewType type);

    boolean isDirty();

    void setDirty(boolean dirty);

    boolean previousTab();

    boolean nextTab();

    public void update(MultinetWizardModel model);

    public void showErrorMessage(String title, String... msgs);

    public void showErrorMessage(String title, Throwable... errors);

    public void setWizardListener(IMultinetWizardListener listener);

    public void showWizard(SubnetDescription subnet, boolean isFirstRun,
            IFabricView mainFrame);

    String getSubnetName();

    public void stopSubnetConnectionTest();

    /**
     * <i>Description:</i>
     *
     * @param recipients
     */
    public void onEmailTest(String recipients);

    /**
     * <i>Description:</i>
     *
     * @param email
     * @return
     */
    boolean isEmailValid(String email);

}
