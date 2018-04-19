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

package com.intel.stl.ui.admin.view.logs;

import java.awt.Component;
import java.awt.Font;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultEditorKit;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter;
import javax.swing.text.JTextComponent;

import com.intel.stl.ui.admin.impl.logs.ITextMenuListener;
import com.intel.stl.ui.admin.impl.logs.SearchKey;
import com.intel.stl.ui.admin.impl.logs.SearchPositionBean;
import com.intel.stl.ui.admin.impl.logs.SearchState;
import com.intel.stl.ui.admin.impl.logs.TextEventType;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;

/**
 * The main component of the log page that displays the log text
 */
public class SMLogView extends AbstractLogView {

    private static final long serialVersionUID = -2959984172557178645L;

    private JTextArea txtAreaMain;

    private TextMenuPanel pnlMainMenu;

    /**
     * Description:
     * 
     * @param name
     */
    public SMLogView() {
        super();
    }

    public void setTextMenuListener(ITextMenuListener listener) {
        if (pnlMainMenu != null) {
            pnlMainMenu.setTextMenuListener(listener);
        }
        pnlSearchMenu.setTextMenuListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.logs.AbstractLogView#getMainComponent()
     */
    @Override
    protected Component getMainComponent() {

        txtAreaMain = new JTextArea();
        txtAreaMain.setBackground(UIConstants.INTEL_WHITE);
        txtAreaMain.setFont(UIConstants.H4_FONT);
        txtAreaMain.setLineWrap(true);
        txtAreaMain.setWrapStyleWord(true);
        txtAreaMain.setEditable(false);
        txtAreaMain.getDocument().putProperty(
                DefaultEditorKit.EndOfLineStringProperty, "\n");
        Highlighter h = txtAreaMain.getHighlighter();
        try {
            h.addHighlight(0, 0, new DefaultHighlightPainter(
                    UIConstants.INTEL_BLUE));

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        // Add a context menu to the text area
        List<TextEventType> eventTypes =
                new ArrayList<TextEventType>(Arrays.asList(TextEventType.COPY,
                        TextEventType.HIGHLIGHT));
        pnlMainMenu = new TextMenuPanel(eventTypes);
        txtAreaMain.addMouseListener(pnlMainMenu);

        JScrollPane scrpnMain = new JScrollPane(txtAreaMain);
        scrpnMain
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrpnMain
                .setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        return scrpnMain;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.logs.AbstractLogView#getTextContent()
     */
    @Override
    JTextComponent getTextContent() {
        return txtAreaMain;
    }

    @Override
    public void showLogEntry(final List<String> entries) {

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                txtAreaMain.setText(null);
                if (entries == null) {
                    return;
                }

                int numEntries = entries.size();
                for (int i = 0; i < numEntries; i++) {
                    txtAreaMain.append(entries.get(i));

                    // Add a carriage return for all but the last line
                    if (i < (numEntries - 1)) {
                        txtAreaMain.append("\n");
                    }
                }

                // Move cursor to the end
                int endPosition = txtAreaMain.getDocument().getLength();
                txtAreaMain.setCaretPosition(endPosition);
            }
        });
    }

    public void setEsmView() {
        txtAreaMain.setForeground(UIConstants.INTEL_DARK_GRAY);
        txtAreaMain.setBackground(UIConstants.INTEL_LIGHT_GRAY);
        txtAreaMain.setFont(UIConstants.H2_FONT.deriveFont(Font.BOLD));
    }

    public void unHighlightText() {
        txtAreaMain.getHighlighter().removeAllHighlights();
    }

    public void unHighlightSelection(String key, int start, int end) {
        // Preserved for future development
        // Highlight the initially selected text with a slightly different shade
        SearchPositionBean selectedBean =
                new SearchPositionBean(key, start, end,
                        new DefaultHighlightPainter(UIConstants.INTEL_ORANGE));

        try {
            txtAreaMain.getHighlighter().addHighlight(
                    selectedBean.getStartOffset(), selectedBean.getEndOffset(),
                    selectedBean.getPainter());
        } catch (BadLocationException e) {
            e.printStackTrace();
        } finally {
            logViewListener.setCurrentSelection("", 0, 0);
        }
    }

    @Override
    public void highlightText(List<SearchKey> searchKeys,
            List<SearchPositionBean> searchResults, SearchState searchState) {

        boolean showErrors =
                (searchState.equals(SearchState.MARKED_SEARCH) || searchState
                        .equals(SearchState.STANDARD_SEARCH));

        if (showErrors) {
            // Find the search key produced in STANDARD or MARKED search
            boolean found = false;
            String searchToken = null;
            Iterator<SearchKey> it = searchKeys.iterator();
            while (!found && it.hasNext()) {
                SearchKey key = it.next();
                if (key.getState().equals(SearchState.MARKED_SEARCH)
                        || key.getState().equals(SearchState.STANDARD_SEARCH)) {
                    searchToken = key.getText();
                    found = true;
                }
            }

            // Check if the search token is in the search results
            if (found) {
                found = false;
                Iterator<SearchPositionBean> itr = searchResults.iterator();
                while (!found && itr.hasNext()) {
                    SearchPositionBean result = itr.next();
                    found = searchToken.equals(result.getKey());
                }

                // Display an error if no STANDARD or MARKED key was found
                if (!found) {
                    Util.showErrorMessage(this,
                            UILabels.STL50206_SEARCH_TEXT_NOT_FOUND
                                    .getDescription(searchToken));
                    return;
                }
            }
        }

        unHighlightText();
        Highlighter highlighter = txtAreaMain.getHighlighter();

        for (SearchPositionBean position : searchResults) {
            try {
                highlighter.addHighlight(position.getStartOffset(),
                        position.getEndOffset(), position.getPainter());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
        }

        // Preserved for future development
        // Highlight the initially selected text with a slightly different shade
        /*-
         *          int selectionStart = textContent.getSelectionStart();
         int selectionEnd = textContent.getSelectionEnd();
         logViewListener.setCurrentSelection(searchKey, selectionStart,
         selectionEnd);
         
            SearchPositionBean selectedBean =
                    new SearchPositionBean(searchKey, selectionStart, selectionEnd,
                            new DefaultHighlightPainter(
                                    UIConstants.INTEL_DARK_ORANGE));
            try {
                highlighter.addHighlight(selectedBean.getStartOffset(),
                        selectedBean.getEndOffset(), selectedBean.getPainter());
            } catch (BadLocationException e) {
                e.printStackTrace();
            }
         * 
         */
    }

    @Override
    public void moveToText(int start, int end) {
        txtAreaMain.setCaretPosition(start);
        txtAreaMain.moveCaretPosition(start);
    }
}
