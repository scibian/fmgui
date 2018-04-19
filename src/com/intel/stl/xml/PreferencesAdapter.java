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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.xml.bind.annotation.adapters.XmlAdapter;

public class PreferencesAdapter extends
        XmlAdapter<Preferences, Map<String, Properties>> {

    @Override
    public Map<String, Properties> unmarshal(Preferences preferences)
            throws Exception {
        List<SectionType> sections = preferences.getSectionTypes();
        Map<String, Properties> prefsMap =
                new HashMap<String, Properties>(sections.size());
        for (SectionType section : sections) {
            String sectionName = section.getName();
            Properties prefProps = new Properties();
            for (EntryType entry : section.getEntry()) {
                prefProps.put(entry.getName(), entry.getValue());
            }
            prefsMap.put(sectionName, prefProps);
        }
        return prefsMap;
    }

    @Override
    public Preferences marshal(Map<String, Properties> prefsMap)
            throws Exception {
        Preferences preferences = new Preferences();
        if (prefsMap != null) {
            Set<String> keys = prefsMap.keySet();
            List<SectionType> sections = preferences.getSectionTypes();
            for (String sectionName : keys) {
                SectionType sectionType = new SectionType();
                sectionType.setName(sectionName);
                List<EntryType> entryList = sectionType.getEntry();
                Properties prefProps = prefsMap.get(sectionName);
                Set<Object> propNames = prefProps.keySet();
                for (Object propName : propNames) {
                    Object propValue = prefProps.get(propName);
                    EntryType entry = new EntryType();
                    entry.setName(propName.toString());
                    entry.setValue(propValue.toString());
                    entryList.add(entry);
                }
                sections.add(sectionType);
            }
        }
        return preferences;
    }

}
