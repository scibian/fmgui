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

package com.intel.stl.ui.common;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * a special properties with ordered keys
 */
public class PinArgument extends Properties {
    private static final long serialVersionUID = 5264217209396434776L;

    private final HashSet<Object> orderedKeys = new LinkedHashSet<Object>();

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Properties#propertyNames()
     */
    @Override
    public Enumeration<?> propertyNames() {
        return Collections.enumeration(orderedKeys);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Properties#stringPropertyNames()
     */
    @Override
    public Set<String> stringPropertyNames() {
        HashSet<String> res = new LinkedHashSet<String>();
        for (Object key : orderedKeys) {
            if (key instanceof String) {
                res.add((String) key);
            }
        }
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#keys()
     */
    @Override
    public synchronized Enumeration<Object> keys() {
        return Collections.enumeration(orderedKeys);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#elements()
     */
    @Override
    public synchronized Enumeration<Object> elements() {
        return Collections.enumeration(orderedKeys);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#put(java.lang.Object, java.lang.Object)
     */
    @Override
    public synchronized Object put(Object key, Object value) {
        orderedKeys.add(key);
        return super.put(key, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#remove(java.lang.Object)
     */
    @Override
    public synchronized Object remove(Object key) {
        orderedKeys.remove(key);
        return super.remove(key);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#putAll(java.util.Map)
     */
    @Override
    public synchronized void putAll(Map<? extends Object, ? extends Object> t) {
        orderedKeys.addAll(t.keySet());
        super.putAll(t);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.util.Hashtable#clear()
     */
    @Override
    public synchronized void clear() {
        orderedKeys.clear();
        super.clear();
    }

    public Map<String, String> getSourceDescription() {
        return null;
    }
}
