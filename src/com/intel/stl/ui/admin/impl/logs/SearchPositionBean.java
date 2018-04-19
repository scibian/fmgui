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

package com.intel.stl.ui.admin.impl.logs;

import javax.swing.text.Highlighter.Highlight;
import javax.swing.text.Highlighter.HighlightPainter;

/**
 * The SearchPositionBean class contains the starting and ending positions of
 * search items in a given text
 */
public class SearchPositionBean implements Highlight {
    private final String key;

    /**
     * Starting position of the searchKey relative to the beginning of the full
     * search text
     * 
     */
    private final int start;

    /**
     * Ending position of the searchKey relative to the beginning of the full
     * search text
     */
    private final int end;

    private final HighlightPainter painter;

    public SearchPositionBean(String key, int start, int end,
            HighlightPainter painter) {
        this.key = key;
        this.start = start;
        this.end = end;
        this.painter = painter;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.Highlighter.Highlight#getStartOffset()
     */
    @Override
    public int getStartOffset() {
        return start;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.Highlighter.Highlight#getEndOffset()
     */
    @Override
    public int getEndOffset() {
        return end;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.text.Highlighter.Highlight#getPainter()
     */
    @Override
    public HighlightPainter getPainter() {
        return painter;
    }

    @Override
    public String toString() {
        return "SearchPositionBean [key=" + key + ", start=" + start + ", end="
                + end + "]";
    }
}
