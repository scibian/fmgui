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

package com.intel.stl.dbengine.impl;

import static com.intel.stl.common.STLMessages.STL30012_ENTITY_NOT_FOUND;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.xml.bind.JAXBException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.DatabaseException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.EventRuleAction;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.AppDataUtils;
import com.intel.stl.common.STLMessages;
import com.intel.stl.datamanager.EventActionRecord;
import com.intel.stl.datamanager.EventRuleRecord;
import com.intel.stl.datamanager.SubnetRecord;
import com.intel.stl.datamanager.UserId;
import com.intel.stl.datamanager.UserRecord;
import com.intel.stl.dbengine.ConfigurationDAO;
import com.intel.stl.dbengine.DatabaseContext;
import com.intel.stl.xml.UserOptions;
import com.intel.stl.xml.UserOptionsMarshaller;

public class ConfigurationDAOImpl extends BaseDAO implements ConfigurationDAO {

    private static Logger log = LoggerFactory
            .getLogger(ConfigurationDAOImpl.class);

    public ConfigurationDAOImpl(EntityManager entityManager) {
        super(entityManager);
    }

    public ConfigurationDAOImpl(EntityManager entityManager,
            DatabaseContext databaseCtx) {
        super(entityManager, databaseCtx);
    }

    @Override
    public void saveEventRule(EventRule rule) {
        EventRuleRecord dbRule =
                em.find(EventRuleRecord.class, rule.getEventName());
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (dbRule == null) {
            persistEventRule(rule);
        } else {
            mergeEventRule(dbRule, rule);
        }
        try {
            tx.commit();
        } catch (Exception e) {
            DatabaseException dbe =
                    new DatabaseException(
                            STLMessages.STL30013_ERROR_SAVING_ENTITY,
                            "EventRule", rule.getEventName(),
                            StringUtils.getErrorMessage(e));
            log.error(StringUtils.getErrorMessage(dbe), e);
            log.error(rule.toString());
            throw dbe;
        }
    }

    @Override
    public void saveEventRules(List<EventRule> rules) {
        List<EventRuleRecord> currRules = getEventRuleRecords();
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        for (EventRuleRecord rule : currRules) {
            if (!rules.contains(rule.getEventRule())) {
                em.remove(rule);
            }
        }
        for (EventRule rule : rules) {
            EventRuleRecord ruleRec = new EventRuleRecord(rule);
            int x = currRules.indexOf(ruleRec);
            if (x >= 0) {
                EventRuleRecord dbRule = currRules.get(x);
                mergeEventRule(dbRule, rule);
            } else {
                persistEventRule(rule);
            }
        }
        try {
            tx.commit();
        } catch (Exception e) {
            DatabaseException dbe =
                    new DatabaseException(
                            STLMessages.STL30013_ERROR_SAVING_ENTITY,
                            "EventRule", "@log", StringUtils.getErrorMessage(e));
            log.error(StringUtils.getErrorMessage(dbe), e);
            log.error(Arrays.toString(rules.toArray()));
            throw dbe;
        }
    }

    @Override
    public List<EventRule> getEventRules() {
        List<EventRule> rules = new ArrayList<EventRule>();
        List<EventRuleRecord> ruleRecs = getEventRuleRecords();
        for (EventRuleRecord ruleRec : ruleRecs) {
            rules.add(ruleRec.getEventRule());
        }
        return rules;
    }

    private List<EventRuleRecord> getEventRuleRecords() {
        TypedQuery<EventRuleRecord> query =
                em.createNamedQuery("EventRule.All", EventRuleRecord.class);
        return query.getResultList();
    }

    @Override
    public EventRule getEventRule(String ruleName) {
        EventRuleRecord record = em.find(EventRuleRecord.class, ruleName);
        if (record == null) {
            return null;
        }
        return record.getEventRule();
    }

    private void persistEventRule(EventRule rule) throws DatabaseException {
        EventRuleRecord ruleRec = new EventRuleRecord(rule);
        Set<SubnetRecord> managedSubnets = new HashSet<SubnetRecord>();
        Set<SubnetDescription> eventSubnets = rule.getEventSubnets();
        if (eventSubnets != null) {
            for (SubnetDescription subnet : eventSubnets) {
                addSubnetToSet(managedSubnets, subnet);
            }
        }
        ruleRec.setEventSubnets(managedSubnets);
        Set<EventActionRecord> managedActions =
                new HashSet<EventActionRecord>();
        List<EventRuleAction> eventActions = rule.getEventActions();
        if (eventActions != null) {
            for (EventRuleAction action : eventActions) {
                EventActionRecord actionRec = new EventActionRecord();
                actionRec.setId(action.name());
                managedActions.add(actionRec);
            }
        }
        ruleRec.setEventActions(managedActions);
        em.persist(ruleRec);
    }

    private void mergeEventRule(EventRuleRecord ruleRec, EventRule newRule)
            throws DatabaseException {
        ruleRec.setEventRule(newRule);

        Set<SubnetRecord> newSubnets = getEventSubnets(newRule);
        Set<SubnetRecord> dbSubnets = ruleRec.getEventSubnets();

        dbSubnets.retainAll(newSubnets);
        newSubnets.removeAll(dbSubnets);
        for (SubnetRecord subnet : newSubnets) {
            addSubnetToSet(dbSubnets, subnet.getSubnetDescription());
        }
        em.merge(ruleRec);
    }

    private void addSubnetToSet(Set<SubnetRecord> set, SubnetDescription subnet)
            throws DatabaseException {
        try {
            set.add(em.getReference(SubnetRecord.class, subnet.getSubnetId()));
        } catch (EntityNotFoundException enf) {
            DatabaseException dbe =
                    new DatabaseException(STL30012_ENTITY_NOT_FOUND, enf,
                            "SubnetDescription", subnet.getName());
            throw dbe;
        }
    }

    private Set<SubnetRecord> getEventSubnets(EventRule rule) {
        Set<SubnetRecord> subnets = new HashSet<SubnetRecord>();
        Set<SubnetDescription> newSubnets = rule.getEventSubnets();
        for (SubnetDescription subnet : newSubnets) {
            SubnetRecord subnetRec = new SubnetRecord(subnet);
            subnets.add(subnetRec);
        }
        return subnets;
    }

    @Override
    public UserSettings getUserSettings(SubnetRecord subnet, String userName) {
        if (subnet == null) {
            subnet = new SubnetRecord();
        }
        UserId userId = new UserId();
        userId.setFabricId(subnet.getId());
        userId.setUserName(userName);
        UserRecord user = em.find(UserRecord.class, userId);
        if (user == null) {
            user = new UserRecord();
            user.setId(userId);
            user.setUserDescription("Default User");
            user.setUserOptionsXml(AppDataUtils.getDefaultUserOptions());
        }
        UserSettings settings = new UserSettings();
        settings.setUserName(user.getId().getUserName());
        settings.setUserDescription(user.getUserDescription());
        String userOptions = user.getUserOptionsXml();
        if (userOptions == null) {
            userOptions = AppDataUtils.getDefaultUserOptions();
        }
        try {
            UserOptions options = UserOptionsMarshaller.unmarshal(userOptions);
            settings.setPreferences(options.getPreferences());
            settings.setEventRules(options.getEventRules());
            settings.setPropertiesDisplayOptions(options.getPropertiesDisplay());
        } catch (JAXBException e) {
            DatabaseException dbe = createUserOptionsXmlException(userName, e);
            throw dbe;
        }
        return settings;
    }

    @Override
    public void saveUserSettings(SubnetRecord subnet, UserSettings userSettings) {
        UserId userId = new UserId();
        userId.setFabricId(subnet.getId());
        userId.setUserName(userSettings.getUserName());
        UserRecord dbUser = em.find(UserRecord.class, userId);
        EntityTransaction tx = em.getTransaction();
        tx.begin();
        if (dbUser == null) {
            UserRecord user = new UserRecord();
            user.setId(userId);
            mergeUserSettings(user, userSettings);
            em.persist(user);
        } else {
            mergeUserSettings(dbUser, userSettings);
            em.merge(dbUser);
        }
        try {
            tx.commit();
        } catch (Exception e) {
            DatabaseException dbe =
                    new DatabaseException(
                            STLMessages.STL30013_ERROR_SAVING_ENTITY,
                            "UserRecord", userSettings.getUserName(),
                            StringUtils.getErrorMessage(e));
            log.error(StringUtils.getErrorMessage(dbe), e);
            log.error(userSettings.toString());
            throw dbe;
        }
    }

    private void mergeUserSettings(UserRecord record, UserSettings settings) {
        record.setUserDescription(settings.getUserDescription());
        UserOptions options = new UserOptions();
        options.setPreferences(settings.getPreferences());
        options.setEventRules(settings.getEventRules());
        options.setPropertiesDisplay(settings.getPropertiesDisplayOptions());
        String strOptions = null;
        try {
            strOptions = UserOptionsMarshaller.marshal(options);
        } catch (JAXBException e) {
            DatabaseException dbe =
                    createUserOptionsXmlException(settings.getUserName(), e);
            throw dbe;
        }
        record.setUserOptionsXml(strOptions);
    }

    private DatabaseException createUserOptionsXmlException(String userName,
            Exception e) {
        DatabaseException dbe =
                new DatabaseException(
                        STLMessages.STL30071_INVALID_USEROPTIONS_XML, e,
                        userName, StringUtils.getErrorMessage(e));
        log.error(StringUtils.getErrorMessage(dbe), e);
        return dbe;
    }
}
