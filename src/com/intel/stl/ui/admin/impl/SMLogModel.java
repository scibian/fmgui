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

package com.intel.stl.ui.admin.impl;

import java.util.ArrayList;
import java.util.List;

import com.intel.stl.api.logs.LogResponse;
import com.intel.stl.ui.admin.impl.logs.SearchPositionBean;
import com.intel.stl.ui.console.LoginBean;

/**
 * Model for the SM log
 */
public class SMLogModel {

    private String logFilePath;

    private long numLines;

    private long currentLine;

    private LogResponse logMsg;

    private String latestEntry;

    private boolean firstPage;

    private boolean lastPage;

    private long startLine;

    private long endLine;

    private String selectedSearchKey;

    private int selectionStart;

    private int selectionEnd;

    private List<String> filteredDoc = new ArrayList<String>();

    private final List<SearchPositionBean> searchResults =
            new ArrayList<SearchPositionBean>();

    private LoginBean credentials;

    public String getLogFilePath() {
        return logFilePath;
    }

    public void setLogFilePath(String logFilePath) {
        this.logFilePath = logFilePath;
    }

    public long getNumLines() {
        return numLines;
    }

    public void setNumLines(long numLines) {
        this.numLines = numLines;
    }

    public long getCurrentLine() {
        return currentLine;
    }

    public void setCurrentLine(long currentLine) {
        this.currentLine = currentLine;
    }

    public void setLogMsg(LogResponse msg) {
        this.logMsg = msg;
    }

    public LogResponse getLogMsg() {
        return logMsg;
    }

    public void setLatestEntry(String entry) {
        this.latestEntry = entry;
    }

    public String getLatestEntry() {
        return this.latestEntry;
    }

    public boolean isFirstPage() {
        return firstPage;
    }

    public void setFirstPage(boolean firstPage) {
        this.firstPage = firstPage;
    }

    public boolean isLastPage() {
        return lastPage;
    }

    public void setLastPage(boolean lastPage) {
        this.lastPage = lastPage;
    }

    public List<SearchPositionBean> getSearchResults() {
        return searchResults;
    }

    public void addSearchResult(SearchPositionBean position) {
        this.searchResults.add(position);
    }

    public void clearSearchResults(String key) {

        if (key == null) {
            return;
        }

        for (int i = searchResults.size() - 1; i >= 0; i--) {
            SearchPositionBean pos = searchResults.get(i);
            if (pos.getKey().equals(key)) {
                searchResults.remove(i);
            }
        }

    }

    public void resetSearchResults() {
        this.searchResults.clear();
    }

    public List<String> getFilteredDoc() {
        return filteredDoc;
    }

    public void setFilteredDoc(List<String> filteredDoc) {
        this.filteredDoc = filteredDoc;
    }

    public long getStartLine() {
        return startLine;
    }

    public void setStartLine(long startLine) {
        this.startLine = startLine;
    }

    public long getEndLine() {
        return endLine;
    }

    public void setEndLine(long endLine) {
        this.endLine = endLine;
    }

    public String getSelectedKey() {
        return selectedSearchKey;
    }

    public int getSelectionStart() {
        return selectionStart;
    }

    public int getSelectionEnd() {
        return selectionEnd;
    }

    public void setSelection(String key, int start, int end) {
        selectedSearchKey = key;
        selectionStart = start;
        selectionEnd = end;
    }

    public void setCredentials(LoginBean credentials) {
        this.credentials = credentials;
    }

    public LoginBean getCredentials() {
        return credentials;
    }
}
