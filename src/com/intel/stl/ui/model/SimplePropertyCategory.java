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

package com.intel.stl.ui.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class SimplePropertyCategory implements
        IPropertyCategory<PropertyItem<SimplePropertyKey>> {
    private final String keyHeader;

    private final String valueHeader;

    private boolean showHeader;

    private final List<PropertyItem<SimplePropertyKey>> items;

    public SimplePropertyCategory() {
        this(null, null);
        showHeader = false;
    }

    /**
     * Description:
     * 
     * @param keyHeader
     * @param valueHeader
     */
    public SimplePropertyCategory(String keyHeader, String valueHeader) {
        super();
        this.keyHeader = keyHeader;
        this.valueHeader = valueHeader;
        items = new ArrayList<PropertyItem<SimplePropertyKey>>();
    }

    /**
     * @return the showHeader
     */
    public boolean isShowHeader() {
        return showHeader;
    }

    /**
     * @param showHeader
     *            the showHeader to set
     */
    public void setShowHeader(boolean showHeader) {
        this.showHeader = showHeader;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.IPropertyCategory#getKeyHeader()
     */
    @Override
    public String getKeyHeader() {
        return showHeader ? keyHeader : null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.IPropertyCategory#getValueHeader()
     */
    @Override
    public String getValueHeader() {
        return showHeader ? valueHeader : null;
    }

    public void addItem(PropertyItem<SimplePropertyKey> item) {
        items.add(item);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.IPropertyCategory#getItems()
     */
    @Override
    public Collection<PropertyItem<SimplePropertyKey>> getItems() {
        return items;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.model.IPropertyCategory#size()
     */
    @Override
    public int size() {
        return items.size();
    }

}
