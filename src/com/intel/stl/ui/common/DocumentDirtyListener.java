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

import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public abstract class DocumentDirtyListener implements DocumentListener {

    public static final String ORIGINAL_TEXT = "orig";

    public static final String CURRENT_TEXT = "text";

    @Override
    public void insertUpdate(DocumentEvent e) {
        Document doc = e.getDocument();
        String originalText = (String) doc.getProperty(ORIGINAL_TEXT);
        if (originalText == null) {
            originalText = getTextFromDocument(doc);
            doc.putProperty(ORIGINAL_TEXT, originalText);
            if (originalText != null && originalText.length() > 0) {
                setDirty(e);
            }
        }
        String newText = getTextFromDocument(doc);
        doc.putProperty(CURRENT_TEXT, newText);
        if (!newText.equals(originalText)) {
            setDirty(e);
        }
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        Document doc = e.getDocument();
        String originalText = (String) doc.getProperty(ORIGINAL_TEXT);
        String newText = (String) doc.getProperty(CURRENT_TEXT);
        if (newText != null && e.getOffset() == 0
                && e.getLength() == newText.length()) {
            doc.putProperty(ORIGINAL_TEXT, newText);
        } else {
            newText = getTextFromDocument(doc);
            doc.putProperty(CURRENT_TEXT, newText);
            if (!newText.equals(originalText)) {
                setDirty(e);
            }
        }
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        setDirty(e);
    }

    public abstract void setDirty(DocumentEvent e);

    private String getTextFromDocument(Document document) {
        try {
            return document.getText(0, document.getLength());
        } catch (BadLocationException e) {
            e.printStackTrace();
            return "";
        }
    }
}
