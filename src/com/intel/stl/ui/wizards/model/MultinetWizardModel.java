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

package com.intel.stl.ui.wizards.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.intel.stl.ui.wizards.impl.WizardType;
import com.intel.stl.ui.wizards.model.event.EventsModel;
import com.intel.stl.ui.wizards.model.preferences.PreferencesModel;
import com.intel.stl.ui.wizards.model.subnet.SubnetModel;

/**
 * Top model use to store the sub-wizard models and update their views
 */
public class MultinetWizardModel implements IWizardModel {

    private SubnetModel subnetModel;

    private EventsModel eventsModel;

    private PreferencesModel preferencesModel;

    private final List<IModelChangeListener<IWizardModel>> listeners =
            new ArrayList<IModelChangeListener<IWizardModel>>();

    private final Map<WizardType, IModelChangeListener<IWizardModel>> listenerMap =
            new LinkedHashMap<WizardType, IModelChangeListener<IWizardModel>>();

    public SubnetModel getSubnetModel() {
        return subnetModel;
    }

    public void setSubnetModel(SubnetModel subnetModel) {
        this.subnetModel = subnetModel;
    }

    public EventsModel getEventsModel() {
        return eventsModel;
    }

    public void setEventsModel(EventsModel eventsModel) {
        this.eventsModel = eventsModel;
    }

    public PreferencesModel getPreferencesModel() {
        return preferencesModel;
    }

    public void setPreferencesModel(PreferencesModel preferencesModel) {
        this.preferencesModel = preferencesModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.model.IWizardModel#addModelListener(com.intel
     * .stl.ui.wizards.model.IModelChangeListener)
     */
    @Override
    public void addModelListener(IModelChangeListener<IWizardModel> listener,
            WizardType wizardType) {

        listeners.add(listener);

        if (wizardType != null) {
            listenerMap.put(wizardType, listener);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.model.IWizardModel#notifyModelChange()
     */
    @Override
    public void notifyModelChange() {
        // Notify all wizards of changes to the top model
        for (IModelChangeListener<IWizardModel> listener : listeners) {
            listener.onModelChange(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.model.IWizardModel#notifyModelChange(com.intel
     * .stl.ui.wizards.impl.WizardType)
     */
    @Override
    public void notifyModelChange(WizardType wizardType) {
        // Notify only interested wizards of changes to the top model
        IModelChangeListener<IWizardModel> listener =
                listenerMap.get(wizardType);
        if (listener != null) {
            listener.onModelChange(this);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.model.IWizardModel#clearModel()
     */
    @Override
    public void clearModel() {
        // TODO Auto-generated method stub

    }
}
