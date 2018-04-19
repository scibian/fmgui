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

package com.intel.stl.ui.admin.impl.devicegroups;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * match node desc with the following format<br>
 * <code>*</code> Wildcard is 0 or more of any combination of: (A-Z)(a-z)(0-9)
 * "-" "," "=" "." "_"<br>
 * <code>?</code> Wildcard is 0 or 1 of any combination of: (A-Z)(a-z)(0-9) "-"
 * "," "=" "." "_" --><br>
 * <code>[##-##]</code> can be used to specify a range of numbers.
 * ie: node[1-4] matches nodes named node1, node2, node3, node4
 * 
 */
public class DescMatcher {
    private final static String validChars = "[A-Za-z0-9\\-=._]";

    private final Pattern pattern;

    public DescMatcher(String pattern) {
        if (pattern.indexOf(':') >= 0) {
            throw new IllegalArgumentException("Invalid format '" + pattern
                    + "'. Please exclude the port pattern string.");
        }
        pattern = rangePattern(pattern);
        pattern = pattern.replaceAll("\\*", validChars + "*");
        pattern = pattern.replaceAll("\\?", validChars + "?");
        this.pattern = Pattern.compile("^" + pattern + "$");
    }

    // assume only one [xx-xx] in the pattern
    protected String rangePattern(String pattern) {
        int pos1 = pattern.indexOf('[');
        if (pos1 < 0) {
            return pattern;
        }
        int pos2 = pattern.indexOf(']');
        if (pos2 < 0) {
            return pattern;
        }

        String rangeStr = pattern.substring(pos1 + 1, pos2);
        String[] segs = rangeStr.split("-");
        if (segs.length != 2) {
            throw new IllegalArgumentException("Invalid range format '"
                    + rangeStr + "'");
        }
        int min = Integer.parseInt(segs[0].trim());
        int max = Integer.parseInt(segs[1].trim());
        StringBuffer sb = new StringBuffer();
        for (int i = min; i <= max; i++) {
            if (sb.length() == 0) {
                sb.append("(" + i);
            } else {
                sb.append("|" + i);
            }
        }
        sb.append(")");
        return pattern.substring(0, pos1) + sb.toString()
                + pattern.substring(pos2 + 1, pattern.length());
    }

    public boolean match(String str) {
        Matcher m = pattern.matcher(str);
        return m.matches();
    }
}
