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

package com.intel.stl.ui.common.view;

import java.text.ParseException;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;

public class SafeNameField extends SafeTextField {
    private static final long serialVersionUID = 5250530579368568787L;

    /**
     * Description:
     * 
     * @param allowEmpty
     */
    public SafeNameField(boolean allowEmpty) {
        super(new SafeNameFormatter(allowEmpty));
    }

    public SafeNameField(boolean allowEmpty, int maxLength) {
        super(new SafeNameFormatter(allowEmpty, maxLength));
    }

    public static class SafeNameFormatter extends SafeStringFormatter {
        private static final long serialVersionUID = 7301310953156564571L;

        /**
         * Description:
         * 
         * @param allowEmpty
         */
        public SafeNameFormatter(boolean allowEmpty) {
            this(allowEmpty, DEFAULT_MAX_LENGTH);
        }

        /**
         * Description:
         * 
         * @param allowEmpty
         * @param maxLength
         */
        public SafeNameFormatter(boolean allowEmpty, int maxLength) {
            super(allowEmpty, maxLength);
            setValidCharacters(UIConstants.NAME_CHARS);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * com.intel.stl.ui.common.view.SafeTextField.SafeStringFormatter#verify
         * (java.lang.String)
         */
        @Override
        protected void verify(String value) throws ParseException {
            super.verify(value);
            checkSpace(value);
            chectDigits(value);
        }

        /**
         * 
         * <i>Description:</i> no leading or trailing space
         * 
         * @param value
         */
        protected void checkSpace(String value) throws ParseException {
            if (value.trim().length() != value.length()) {
                setValidationTooltip(UILabels.STL50098_TEXT_FIELD_INVALID_SPACES
                        .getDescription());
                throw new ParseException("Cannot start or end with space", 0);
            }
        }

        protected void chectDigits(String value) throws ParseException {
            if (!value.isEmpty() && Character.isDigit(value.charAt(0))) {
                setValidationTooltip(UILabels.STL50099_TEXT_FIELD_INVALID_DIGITS
                        .getDescription());
                throw new ParseException("Cannot start with digits", 0);
            }
        }
    }
}
