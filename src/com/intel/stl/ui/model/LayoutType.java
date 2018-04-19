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

import com.intel.stl.ui.common.STLConstants;

public enum LayoutType {
    FORCE_DIRECTED(
            STLConstants.K1009_FORCE_DIRECTED.getValue(), 
            STLConstants.K1010_FORCE_DIRECTED_DESCRIPTION.getValue()),
    HIERARCHICAL(
            STLConstants.K1011_HIERARCHICAL.getValue(), 
            STLConstants.K1012_HIERARCHICAL_DESCRIPTION.getValue()),
    TREE_CIRCLE(
            STLConstants.K1013_TREE_CIRCLE.getValue(), 
            STLConstants.K1014_TREE_CIRCLE_DESCRIPTION.getValue()),
    TREE_SLASH(
            STLConstants.K1015_TREE_SLASH.getValue(), 
            STLConstants.K1016_TREE_SLASH_DESCRIPTION.getValue()),
    TREE_LINE(
            STLConstants.K1017_TREE_LINE.getValue(), 
            STLConstants.K1018_TREE_LINE_DESCRIPTION.getValue());
    
    private final String name;
    private final String description;
    
    /**
     * Description: 
     *
     * @param name
     * @param description 
     */
    private LayoutType(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
    
}
