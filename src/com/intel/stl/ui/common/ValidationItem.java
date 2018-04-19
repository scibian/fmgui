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

import java.util.concurrent.Callable;

public class ValidationItem<E> {
    public final static String[] NAMES = new String[] {
            STLConstants.K2120_VALIDATION_TYPE.getValue(),
            STLConstants.K2121_ISSUES.getValue(),
            STLConstants.K2122_SUGGESTION.getValue(),
    // STLConstants.K2123_QUICK_FIX.getValue()
            };

    private final String type;

    private final String issues;

    private final String suggestions;

    private Callable<E[]> quickFix;

    /**
     * Description:
     * 
     * @param type
     * @param issues
     * @param suggestions
     * @param quickFix
     */
    public ValidationItem(String type, String issues, String suggestions) {
        super();
        this.type = type;
        this.issues = issues;
        this.suggestions = suggestions;
    }

    /**
     * @param quickFix
     *            the quickFix to set
     */
    public void setQuickFix(Callable<E[]> quickFix) {
        this.quickFix = quickFix;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @return the issues
     */
    public String getIssues() {
        return issues;
    }

    /**
     * @return the suggestions
     */
    public String getSuggestions() {
        return suggestions;
    }

    /**
     * @return the quickFix
     */
    public Callable<E[]> getQuickFix() {
        return quickFix;
    }

}
