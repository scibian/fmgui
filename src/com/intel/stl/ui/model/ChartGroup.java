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
import java.util.List;

import com.intel.stl.ui.common.view.ChartsView;

/**
 * Organize charts by a group
 */
public class ChartGroup {
    /**
     * Name of this ChartsGroup
     */
    private String name;
    /**
     * The chart view that represents this group. it can be a special view
     * that represents this group, or just one of the members.  
     */
    private ChartsView chartView;
    /**
     * The member of this group. When this list is null or empty, this group
     * is actually a leaf node represents a <code>chartView</code> with the 
     * name <code>name</code>
     */
    private List<ChartGroup> members;
    
    /**
     * Description: 
     *
     * @param name
     * @param chartView 
     */
    public ChartGroup(String name, ChartsView chartView) {
        super();
        this.name = name;
        this.chartView = chartView;
    }
    
    public ChartGroup(ChartsView chartView) {
        this(chartView==null ? null : chartView.getTitle(), chartView);
    }
    
    public void addMember(ChartGroup member) {
        if (members==null) {
            members = new ArrayList<ChartGroup>();
        }
        members.add(member);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the chartView
     */
    public ChartsView getChartView() {
        return chartView;
    }

    /**
     * @return the members
     */
    public List<ChartGroup> getMembers() {
        return members;
    }
    
    public boolean hasMembers() {
        return members!=null && !members.isEmpty();
    }
    
    public int numMembers() {
        return members==null ? 0 : members.size();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "ChartsGroup [name=" + name + ", chartView=" + chartView
                + ", members=" + members + "]";
    }
    
}
