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

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * match port in the format [xx-xx]. Note we intentionally support more
 * powerful format here support future extended format on FM side
 */
public class PortMatcher {
    private final List<Point> ranges = new ArrayList<Point>();

    private final Set<Integer> candidates = new HashSet<Integer>();

    /**
     * Description:
     * 
     * @param pattern
     */
    public PortMatcher(String pattern) {
        pattern = pattern.trim();
        if (pattern.charAt(0) == '['
                && pattern.charAt(pattern.length() - 1) == ']') {
            pattern = pattern.substring(1, pattern.length() - 1);
        }
        String[] segs = pattern.split(",");
        for (String seg : segs) {
            String[] tmp = seg.split("-");
            if (tmp.length == 2) {
                int min = Integer.parseInt(tmp[0].trim());
                int max = Integer.parseInt(tmp[1].trim());
                if (max < min) {
                    throw new IllegalArgumentException("Invalid number range '"
                            + seg + "'");
                }
                ranges.add(new Point(min, max));
            } else if (tmp.length == 1) {
                int candidate = Integer.parseInt(seg.trim());
                candidates.add(candidate);
            } else {
                throw new IllegalArgumentException("Invalid format '" + seg
                        + "'");
            }
        }
    }

    public boolean match(int num) {
        if (candidates.contains(num)) {
            return true;
        }

        for (Point range : ranges) {
            if (num >= range.x && num <= range.y) {
                return true;
            }
        }

        return false;
    }

    /**
     * @return the ranges
     */
    public List<Point> getRanges() {
        return ranges;
    }

    /**
     * @return the candidates
     */
    public Set<Integer> getCandidates() {
        return candidates;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PortMatcher [ranges=" + ranges + ", candidates=" + candidates
                + "]";
    }

}
