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

package com.intel.stl.xml;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.intel.stl.api.configuration.EventRule;
import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.api.configuration.ResourceType;

/**
 * <p>
 * Java class for anonymous complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within
 * this class.
 * 
 * <pre>
 * &lt;complexType>
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element ref="{}Preferences"/>
 *         &lt;element ref="{}EventRules"/>
 *         &lt;element ref="{}PropertiesDisplay"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "preferences", "eventRules",
        "propertiesDisplay" })
@XmlRootElement(name = "UserOptions")
public class UserOptions {

    @XmlElement(name = "Preferences", required = true)
    @XmlJavaTypeAdapter(PreferencesAdapter.class)
    protected Map<String, Properties> preferences;

    @XmlElement(name = "EventRules", required = true)
    @XmlJavaTypeAdapter(EventRulesAdapter.class)
    protected List<EventRule> eventRules;

    @XmlElement(name = "PropertiesDisplay", required = true)
    @XmlJavaTypeAdapter(PropertiesDisplayAdapter.class)
    protected Map<ResourceType, List<PropertyGroup>> propertiesDisplay;

    /**
     * Gets the value of the lastSubnetAccessed property.
     * 
     * @return possible object is {@link String }
     * 
     */
    public Map<String, Properties> getPreferences() {
        return preferences;
    }

    /**
     * Sets the value of the lastSubnetAccessed property.
     * 
     * @param value
     *            allowed object is {@link String }
     * 
     */
    public void setPreferences(Map<String, Properties> value) {
        this.preferences = value;
    }

    /**
     * @return the eventRules
     */
    public List<EventRule> getEventRules() {
        return eventRules;
    }

    /**
     * @param eventRules
     *            the eventRules to set
     */
    public void setEventRules(List<EventRule> eventRules) {
        this.eventRules = eventRules;
    }

    /**
     * Gets the value of the propertiesDisplay property.
     * 
     * @return possible object is {@link PropertiesDisplay }
     * 
     */
    public Map<ResourceType, List<PropertyGroup>> getPropertiesDisplay() {
        return propertiesDisplay;
    }

    /**
     * Sets the value of the propertiesDisplay property.
     * 
     * @param value
     *            allowed object is {@link PropertiesDisplay }
     * 
     */
    public void setPropertiesDisplay(
            Map<ResourceType, List<PropertyGroup>> value) {
        this.propertiesDisplay = value;
    }
}
