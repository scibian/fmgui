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

import javax.swing.text.DefaultFormatter;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;

/**
 * A safe TextField that only allows digital, alphabetic character, punctuation
 * and space. The max string length is 64 by default
 */
public class SafeTextField extends ExFormattedTextField {
    private static final long serialVersionUID = 1494207078500818852L;

    public SafeTextField(boolean allowEmpty) {
        this(allowEmpty, SafeStringFormatter.DEFAULT_MAX_LENGTH);
    }

    /**
     * Description:
     * 
     * @param formatter
     * @param allowEmpty
     * @param maxLength
     */
    public SafeTextField(boolean allowEmpty, int maxLength) {
        super(new SafeStringFormatter(allowEmpty, maxLength));
        ((SafeStringFormatter) getFormatter()).setParent(this);
    }

    /**
     * Description:
     * 
     * @param formatter
     */
    public SafeTextField(AbstractFormatter formatter) {
        super(formatter);
        if (formatter instanceof SafeStringFormatter) {
            ((SafeStringFormatter) formatter).setParent(this);
        }
    }

    public void setValidChars(String validChars) {
        ((SafeStringFormatter) getFormatter()).setValidCharacters(validChars);
    }

    public static class SafeStringFormatter extends DefaultFormatter {
        private static final long serialVersionUID = -7294298916477068437L;

        protected static final int DEFAULT_MAX_LENGTH = 64;

        public static final String VALID_CHARS = UIConstants.SAFE_STR;

        private ExFormattedTextField parent;

        private String validCharacters;

        private boolean allowEmpty = true;

        private int maxLength = DEFAULT_MAX_LENGTH;

        public SafeStringFormatter(boolean allowEmpty) {
            this(allowEmpty, DEFAULT_MAX_LENGTH);
        }

        /**
         * Description:
         * 
         * @param allowEmpty
         * @param maxLength
         */
        public SafeStringFormatter(boolean allowEmpty, int maxLength) {
            super();
            this.allowEmpty = allowEmpty;
            if (maxLength > 0) {
                this.maxLength = maxLength;
            }
            setValidCharacters(VALID_CHARS);
            setCommitsOnValidEdit(true);
        }

        /**
         * @param maxLength
         *            the maxLength to set
         */
        public void setMaxLength(int maxLength) {
            this.maxLength = maxLength;
        }

        /**
         * @param parent
         *            the parent to set
         */
        public void setParent(ExFormattedTextField parent) {
            this.parent = parent;
        }

        /**
         * @return the validCharacters
         */
        public String getValidCharacters() {
            return validCharacters;
        }

        /**
         * @param validCharacters
         *            the validCharacters to set
         */
        public void setValidCharacters(String validCharacters) {
            this.validCharacters = validCharacters;
        }

        /*
         * (non-Javadoc)
         * 
         * @see javax.swing.text.MaskFormatter#stringToValue(java.lang.String)
         */
        @Override
        public Object stringToValue(String value) throws ParseException {
            verify(value);
            return super.stringToValue(value);
        }

        protected void verify(String value) throws ParseException {
            checkEmpty(value);
            checkLength(value);
            checkValidChar(value);
        }

        protected void checkEmpty(String value) throws ParseException {
            if (parent != null && parent.hasFocus() && !allowEmpty
                    && value.isEmpty()) {
                setValidationTooltip(UILabels.STL50084_CANT_BE_BLANK
                        .getDescription());
                throw new ParseException("Cannot be empty", 0);
            }
        }

        protected void checkLength(String value) throws ParseException {
            if (value.length() > maxLength) {
                setValidationTooltip(UILabels.STL50095_TEXT_FIELD_LIMIT
                        .getDescription(maxLength));
                throw new ParseException("Exceed maximum length " + maxLength,
                        0);
            }
        }

        protected void checkValidChar(String value) throws ParseException {
            for (int i = 0; i < value.length(); i++) {
                if (!isValidCharacter(value.charAt(i))) {
                    setValidationTooltip(UILabels.STL50096_TEXT_FIELD_INVALID_CHAR
                            .getDescription(value.charAt(i)));
                    throw new ParseException("Invalid char '" + value.charAt(i)
                            + "'", 0);
                }
            }
        }

        protected boolean isValidCharacter(char aChar) {
            String filter = getValidCharacters();
            if (filter != null && filter.indexOf(aChar) == -1) {
                return false;
            }
            return true;
        }

        protected void setValidationTooltip(String tooltip) {
            if (parent != null) {
                parent.setValidationTooltip(tooltip);
            }
        }
    }
}
