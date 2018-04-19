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

import java.util.Comparator;

public class NameSorter implements Comparator<String> {
    private final static NameSorter instance = new NameSorter();

    private NameSorter() {
    }

    public static NameSorter instance() {
        return instance;
    }

    @Override
    public int compare(String o1, String o2) {
        // null string
        if (o1 == null) {
            if (o2 != null) {
                return -1;
            } else {
                return 0;
            }
        } else if (o2 == null) {
            return 1;
        }
        // empty string
        if (o1.isEmpty()) {
            if (!o2.isEmpty()) {
                return -1;
            } else {
                return 0;
            }
        } else if (o2.isEmpty()) {
            return 1;
        }
        // strings not empty
        return new MixedString(o1).compareTo(new MixedString(o2));
    }

    private static class MixedString implements Comparable<MixedString> {
        private Long prefix;

        private final String mainString;

        private Long suffix;

        public MixedString(String str) {
            int prePos = 0;
            int len = str.length();
            int sufPos = len;
            while (prePos < len && Character.isDigit(str.charAt(prePos++))) {
                ;
            }
            prePos -= 1;
            if (prePos > 0) {
                prefix = Long.valueOf(str.substring(0, prePos));
            }
            if (prePos < len - 1) {
                while (Character.isDigit(str.charAt(--sufPos))) {
                    ;
                }
                sufPos += 1;
                if (sufPos < len) {
                    suffix = Long.valueOf(str.substring(sufPos, len));
                }
            }
            mainString =
                    prePos < sufPos - 1 ? str.substring(prePos, sufPos) : null;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Comparable#compareTo(java.lang.Object)
         */
        @Override
        public int compareTo(MixedString o) {
            // special case pure number is "smaller" than pure string
            if (mainString == null) {
                if (o.mainString != null) {
                    return -1;
                }
            } else if (o.mainString == null) {
                return 1;
            }

            int res = compare(prefix, o.prefix);
            if (res != 0) {
                return res;
            }

            if (mainString != null) {
                res = mainString.compareTo(o.mainString);
                if (res != 0) {
                    return res;
                }
            }

            return compare(suffix, o.suffix);
        }

        /**
         * Description: The method Long#compareTo doesn't consider null
         * point situation, so we need to implement our own.
         * 
         * @param v1
         * @param v2
         * @return
         */
        private int compare(Long v1, Long v2) {
            if (v1 == null) {
                if (v2 != null) {
                    return -1;
                } else {
                    return 0;
                }
            } else {
                if (v2 == null) {
                    return 1;
                } else {
                    return v1.compareTo(v2);
                }
            }
        }
    }
}
