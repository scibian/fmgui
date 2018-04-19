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

import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;

import javax.swing.text.NumberFormatter;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;

public class SafeNumberField<N extends Number> extends ExFormattedTextField {
    private static final long serialVersionUID = -9002349798796295869L;

    public SafeNumberField(NumberFormat format, N min, boolean inclusiveMin) {
        this(format, min, inclusiveMin, null, false);
    }

    /**
     * Description:
     *
     * @param formatter
     * @param min
     * @param max
     */
    @SuppressWarnings("rawtypes")
    public SafeNumberField(NumberFormat format, N min, boolean inclusiveMin,
            N max, boolean inclusiveMax) {
        super(new SafeNumberFormatter<N>(format, min, inclusiveMin, max,
                inclusiveMax));
        ((SafeNumberFormatter) getFormatter()).setParent(this);
    }

    @SuppressWarnings("rawtypes")
    public void setValidChars(String validChars) {
        ((SafeNumberFormatter) getFormatter()).setValidCharacters(validChars);
    }

    /**
     * InternationalFormatter has a bug on range validation. When a user type in
     * 1111 for a byte value, it will accept it because when it calls
     * Number#byteValue, 1111 is casted to a valid byte value. So we have this
     * class to validate range directly based Number rather than value type.
     */
    public static class SafeNumberFormatter<N extends Number>
            extends NumberFormatter {
        private static final long serialVersionUID = 6505406066904797986L;

        private String validCharacters = UIConstants.NUMBER_CHARS;

        private ExFormattedTextField parent;

        private final N min;

        private boolean inclusiveMin;

        private final N max;

        private boolean inclusiveMax;

        public SafeNumberFormatter(NumberFormat format, N min,
                boolean inclusiveMin) {
            this(format, min, inclusiveMin, null, false);
        }

        /**
         * Description:
         *
         * @param format
         */
        public SafeNumberFormatter(NumberFormat format, N min,
                boolean inclusiveMin, N max, boolean inclusiveMax) {
            super(format);
            this.min = min;
            this.inclusiveMin = inclusiveMin;
            this.max = max;
            this.inclusiveMax = inclusiveMax;
            setValueClass(min.getClass());
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

        /**
         * @param parent
         *            the parent to set
         */
        public void setParent(ExFormattedTextField parent) {
            this.parent = parent;
        }

        /*
         * (non-Javadoc)
         *
         * @see javax.swing.text.InternationalFormatter#stringToValue(java.lang.
         * String )
         */
        @Override
        public Object stringToValue(String text) throws ParseException {
            checkValidChar(text);

            Format format = getFormat();

            setValidationTooltip(createOutOfRangeTooltip(min, inclusiveMin, max,
                    inclusiveMax, format));

            try {
                Object obj = format.parseObject(text);
                if (obj instanceof Number) {
                    Number num = (Number) obj;
                    if (inclusiveMin) {
                        if (num.doubleValue() < min.doubleValue()) {
                            throw new ParseException(
                                    "Value not within min/max range", 0);
                        }
                    } else {
                        if (num.doubleValue() <= min.doubleValue()) {
                            throw new ParseException(
                                    "Value not within min/max range", 0);
                        }
                    }

                    if (max != null) {
                        if (inclusiveMax) {
                            if (num.doubleValue() > max.doubleValue()) {
                                throw new ParseException(
                                        "Value not within min/max range", 0);
                            }
                        } else {
                            if (num.doubleValue() >= max.doubleValue()) {
                                throw new ParseException(
                                        "Value not within min/max range", 0);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                throw new ParseException(e.getMessage(), 0);
            }

            Object res = super.stringToValue(text);
            return res;
        }

        protected void checkValidChar(String value) throws ParseException {
            for (int i = 0; i < value.length(); i++) {
                if (!isValidCharacter(value.charAt(i))) {
                    setValidationTooltip(
                            createInvalidCharTooltip(value.charAt(i)));
                    throw new ParseException(
                            "Invalid char '" + value.charAt(i) + "'", 0);
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

        protected String createInvalidCharTooltip(char target) {
            return UILabels.STL50096_TEXT_FIELD_INVALID_CHAR
                    .getDescription(target);
        }

        protected String createOutOfRangeTooltip(N min, boolean inclusiveMin,
                N max, boolean inclusiveMax, Format format) {
            String maxStr = max == null ? STLConstants.K0133_INFINITE.getValue()
                    : format.format(max);
            if (max == null && min.intValue() == 0 && !inclusiveMin) {
                return UILabels.STL81020_POSITIVE_VALIDATION.getDescription();
            } else {
                if (inclusiveMin) {
                    if (inclusiveMax) {
                        return UILabels.STL81021_RANGE1_VALIDATION
                                .getDescription(format.format(min), maxStr);
                    } else {
                        return UILabels.STL81022_RANGE2_VALIDATION
                                .getDescription(format.format(min), maxStr);
                    }
                } else {
                    if (inclusiveMax) {
                        return UILabels.STL81023_RANGE3_VALIDATION
                                .getDescription(format.format(min), maxStr);
                    } else {
                        return UILabels.STL81024_RANGE4_VALIDATION
                                .getDescription(format.format(min), maxStr);
                    }
                }
            }
        }

        protected void setValidationTooltip(String tooltip) {
            if (parent != null) {
                parent.setValidationTooltip(tooltip);
            }
        }
    }
}
