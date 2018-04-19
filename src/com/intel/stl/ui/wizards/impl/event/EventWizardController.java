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

package com.intel.stl.ui.wizards.impl.event;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.configuration.EventType;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.model.EventTypeViz;
import com.intel.stl.ui.wizards.impl.IMultinetWizardListener;
import com.intel.stl.ui.wizards.impl.IMultinetWizardTask;
import com.intel.stl.ui.wizards.impl.IWizardListener;
import com.intel.stl.ui.wizards.impl.InteractionType;
import com.intel.stl.ui.wizards.impl.WizardValidationException;
import com.intel.stl.ui.wizards.model.IModelChangeListener;
import com.intel.stl.ui.wizards.model.IWizardModel;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.event.EventRulesTableModel;
import com.intel.stl.ui.wizards.model.event.EventsModel;
import com.intel.stl.ui.wizards.view.event.EventWizardView;

/**
 * Controller for the Event Wizard
 */
public class EventWizardController implements IMultinetWizardTask,
        IEventControl, IModelChangeListener<IWizardModel> {

    private final String eventFieldDelimiter = ",";

    private final EventWizardView view;

    private final EventRulesTableModel eventRulesModel;

    private EventsModel eventsModel;

    @SuppressWarnings("unused")
    private boolean connectable;

    @SuppressWarnings("unused")
    private IWizardListener wizardController;

    private IMultinetWizardListener multinetWizardController;

    private boolean done;

    private UserSettings userSettings;

    private boolean firstPass = true;

    public EventWizardController(EventWizardView view) {

        this.view = view;
        this.view.setDirty(false);
        this.view.setWizardListener(this);
        this.view.setEventControlListener(this);
        eventRulesModel = view.getModel();
    }

    public EventWizardController(EventWizardView view, EventsModel eventsModel) {

        this(view);
        this.eventsModel = eventsModel;
        view.setWizardListener(this);
    }

    protected void createEventTable() {

        for (EventType type : EventType.values()) {
            EventRule rule = new EventRule();
            rule.setCommandId(type.getId());
            rule.setEventType(type);
            rule.setEventActions(new ArrayList<EventRuleAction>());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#getName()
     */
    @Override
    public String getName() {

        return STLConstants.K0406_EVENTS.getValue();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#getView()
     */
    @Override
    public EventWizardView getView() {

        return view;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#init()
     */
    @Override
    public void init() {

        this.userSettings = multinetWizardController.getUserSettings();

        List<EventRule> userEventRules = userSettings.getEventRules();
        List<EventRule> defaultSettings = createDefaultTable();

        // Create a default table for the model
        if (eventRulesModel.getEventRules().size() <= 0) {
            eventRulesModel.updateTable(defaultSettings);
            eventsModel.setEventsRulesModel(eventRulesModel);
        }

        if ((multinetWizardController.isNewWizard())
                || (multinetWizardController.isFirstRun() && firstPass)) {

            // Update the database with the default settings
            updateUserSettings(defaultSettings);

        } else if (userEventRules != null) {

            // Update the model with the user settings
            if (!multinetWizardController.isFirstRun()) {
                updateModel(userEventRules);
            }
        }

        firstPass = false;
        view.setDirty(false);
        done = false;
    }

    public List<EventRule> createDefaultTable() {

        List<EventRule> eventRules = new ArrayList<EventRule>();

        // Build a list of all supported events
        for (EventType eventType : EventType.values()) {

            EventRule rule = new EventRule();
            rule.setEventName(eventType.name());
            rule.setEventType(eventType);
            rule.setEventSeverity(eventType.getDefaultSeverity());

            // Initially the action list is empty
            List<EventRuleAction> eventActionList =
                    new ArrayList<EventRuleAction>();
            rule.setEventActions(eventActionList);

            eventRules.add(rule);
        }

        return eventRules;
    }

    protected void updateModel(List<EventRule> eventRules) {

        // Retrieve the event rules from the model
        List<EventRule> modelRulesList = eventRulesModel.getEventRules();

        if (eventRules != null) {
            // Deep copy of event rules to table model
            for (int i = 0; i < eventRules.size(); i++) {
                if (modelRulesList.size() > 0) {
                    EventRule modelRule = modelRulesList.get(i);
                    EventRule eventRule = eventRules.get(i);
                    modelRule.setEventType(eventRule.getEventType());
                    modelRule.setEventSeverity(eventRule.getEventSeverity());

                    // Update the actions
                    List<EventRuleAction> actions = eventRule.getEventActions();
                    List<EventRuleAction> modelActions =
                            modelRule.getEventActions();
                    modelActions.clear();
                    for (int j = 0; j < actions.size(); j++) {
                        modelActions.add(EventRuleAction.valueOf(actions.get(j)
                                .name()));
                    } // for
                }
            } // for
        }

        eventRulesModel.updateTable(eventRulesModel.getEventRules());
        eventsModel.setEventsRulesModel(eventRulesModel);
    } // updateModel

    protected void updateUserSettings(List<EventRule> eventRules) {

        List<EventRule> userEventRules = new ArrayList<EventRule>();

        // Deep copy of event rules to database
        for (EventRule rule : eventRules) {

            // Create a new user action list & populate with the provided values
            List<EventRuleAction> userActionList =
                    new ArrayList<EventRuleAction>();
            for (int i = 0; i < rule.getEventActions().size(); i++) {
                userActionList.add(rule.getEventActions().get(i));
            }

            // Create a new event rule
            EventRule newRule =
                    new EventRule(rule.getEventType(), rule.getEventSeverity(),
                            userActionList);
            newRule.setEventName(rule.getEventType().name());
            userEventRules.add(newRule);
        } // for

        // Update the user settings in the data base
        userSettings.setEventRules(userEventRules);
    }

    public EventRulesTableModel updateEventRulesTableModel(
            List<EventRule> eventRules) {

        if (eventRules != null) {
            List<EventRule> modelRulesList = eventRules;
            eventRulesModel.updateTable(modelRulesList);
        }

        return eventRulesModel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#setDone(boolean)
     */
    @Override
    public void setDone(boolean done) {

        this.done = done;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#isDone()
     */
    @Override
    public boolean isDone() {

        return done;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IConfigTask#onApply()
     */
    @Override
    public boolean validateUserEntry() throws WizardValidationException {

        // Update the eventsModel with the latest event rules
        updateModel();

        // Nothing to validate here
        return true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onPrevious()
     */
    @Override
    public void onPrevious() {

    }

    protected String eventRuleToString(EventRule eventRule) {

        String str = null;
        EventTypeViz eventTypeViz =
                EventTypeViz.getEventTypeVizFor(eventRule.getEventType());
        if (eventTypeViz != null) {
            str =
                    eventTypeViz.getName() + eventFieldDelimiter
                            + eventFieldDelimiter
                            + eventRule.getEventSeverity().name()
                            + eventFieldDelimiter;
        }
        List<EventRuleAction> actions = eventRule.getEventActions();
        int numActions = actions.size();
        for (int i = 0; i < numActions; i++) {
            str += actions.get(i).name();
            if (i < (numActions - 1)) {
                str += eventFieldDelimiter;
            }
        }// for

        return str;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#onReset()
     */
    @Override
    public void onReset() {
        userSettings = multinetWizardController.retrieveUserSettings();
        updateModel(userSettings.getEventRules());
        view.setDirty(false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#cleanup()
     */
    @Override
    public void cleanup() {

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#selectStep(java.lang.String)
     */
    @Override
    public void selectStep(String taskName) {

        multinetWizardController.selectStep(taskName);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#isDirty()
     */
    @Override
    public boolean isDirty() {

        return view.isDirty();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean dirty) {

        view.setDirty(dirty);
        multinetWizardController.setDirty(dirty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#doInteractiveAction(com.intel
     * .stl.ui.wizards.impl.InteractionType, java.lang.Object)
     */
    @Override
    public void doInteractiveAction(InteractionType action, Object... data) {

        switch (action) {

            case CHANGE_WIZARDS:

                if (data == null) {
                    return;
                }

                String taskName = (String) data[0];
                if (taskName != null) {
                    onReset();
                    view.closeStatusPanel();
                    selectStep(taskName);
                }
                break;

            case SAVE_LOGGING:
                // NOP
                break;

            default:
                break;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#updateModel()
     */

    @Override
    public void updateModel() {

        // Update the local events model
        eventsModel.setEventsRulesModel(eventRulesModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#updateView(com.intel.stl.ui
     * .wizards.model.MultinetWizardModel)
     */
    @Override
    public void promoteModel(MultinetWizardModel topModel) {

        // Promote the events model to the top model
        topModel.setEventsModel(eventsModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.model.IModelChangeListener#onModelChange(com
     * .intel.stl.ui.wizards.model.IWizardModel)
     */
    @Override
    public void onModelChange(IWizardModel m) {

        MultinetWizardModel model = (MultinetWizardModel) m;
        eventsModel = model.getEventsModel();
        view.updateView(model);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IMultinetWizardTask#setWizardController
     * (com.intel.stl.ui.wizards.impl.IMultinetWizardListener)
     */
    @Override
    public void setWizardController(IMultinetWizardListener controller) {
        multinetWizardController = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.IWizardTask#setWizardController(com.intel
     * .stl.ui.wizards.impl.IWizardListener)
     */
    @Override
    public void setWizardController(IWizardListener controller) {
        wizardController = controller;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#clear()
     */
    @Override
    public void clear() {

        // Just clear the table and the init() method will
        // initialize the table with the default settings
        eventRulesModel.clear();
        eventsModel.setEventsRulesModel(eventRulesModel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.impl.event.IEventControl#updateTable(com.intel
     * .stl.ui.wizards.model.event.EventRulesTableModel)
     */
    @Override
    public void updateTable(EventRulesTableModel model) {

        eventRulesModel.updateTable(model.getEventRules());
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#setConnectable(boolean)
     */
    @Override
    public void setConnectable(boolean connectable) {
        this.connectable = connectable;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.impl.IWizardTask#isEditValid()
     */
    @Override
    public boolean isEditValid() {
        return view.isEditValid();
    }
}
