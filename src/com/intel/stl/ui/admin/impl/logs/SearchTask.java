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

import java.awt.Color;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.SwingWorker;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;

import com.intel.stl.ui.admin.impl.SMLogModel;
import com.intel.stl.ui.admin.view.logs.AbstractLogView;
import com.intel.stl.ui.common.Util;

/**
 * SwingWorker task to search for and highlight text
 */
public class SearchTask extends SwingWorker<Void, Void> {
    private final AbstractLogView view;

    private final SMLogModel model;

    private final String text;

    private final List<SearchKey> searchKeys;

    private final SearchState searchState;

    public SearchTask(AbstractLogView view, SMLogModel model, String text,
            SearchState searchState, List<SearchKey> searchKeys) {
        super();
        this.view = view;
        this.model = model;
        this.text = text;
        this.searchState = searchState;
        this.searchKeys = searchKeys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {

        // Clear out previous search results
        model.resetSearchResults();

        // Perform the search and update the model
        if (searchKeys != null && !searchKeys.isEmpty()) {
            for (SearchKey searchKey : searchKeys) {
                if (searchKey != null) {
                    String key = searchKey.getText();
                    Color color = searchKey.getColor();
                    search(searchState, key, color);
                }
            }
        }

        return null;
    }

    protected void search(SearchState searchState, String searchKey, Color color) {

        if (searchKey == null) {
            return;
        }

        // Treat as a literal pattern string
        String newSearchKey = Pattern.quote(searchKey);

        // Define the pattern for the searchKey
        Pattern pattern = Pattern.compile(newSearchKey);

        // Get a matcher to search with
        Matcher matcher = pattern.matcher(text);

        // Perform the search
        while (matcher.find()) {
            // Update the model with the starting and ending positions of
            // the searchKey
            model.addSearchResult(new SearchPositionBean(searchKey, matcher
                    .start(), matcher.end(), new DefaultHighlightPainter(color)));
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#done()
     */
    @Override
    protected void done() {
        try {
            get();

            // Save the last search
            view.saveLastSearchKey();

            view.highlightText(searchKeys, model.getSearchResults(),
                    searchState);

            if (!model.getSearchResults().isEmpty()) {
                view.moveToText(model.getSearchResults().get(0)
                        .getStartOffset(), model.getSearchResults().get(0)
                        .getEndOffset());
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            Util.showError(view, e);
            e.printStackTrace();
        } finally {
            view.enableSearch(true);
            // Update the number of matched search results
            List<SearchPositionBean> results = model.getSearchResults();
            if (results != null) {
                view.showNumMatches(model.getSearchResults().size());
            }
        }
    }
}
