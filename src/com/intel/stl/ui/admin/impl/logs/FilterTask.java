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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.SwingWorker;

import com.intel.stl.ui.admin.impl.SMLogModel;
import com.intel.stl.ui.admin.view.logs.AbstractLogView;
import com.intel.stl.ui.common.Util;

/**
 * SwingWorker task to filter text
 */
public class FilterTask extends SwingWorker<Void, Void> {
    private final SMLogController parent;

    private final AbstractLogView view;

    private final SMLogModel model;

    private final List<FilterType> keys;

    public FilterTask(SMLogController parent, SMLogModel model,
            List<FilterType> keys) {
        super();

        this.parent = parent;
        this.view = parent.getView();
        this.model = model;
        this.keys = keys;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {
        List<String> doc = model.getLogMsg().getEntries();
        List<String> filteredDoc = filter(doc, keys);
        if (filteredDoc != null) {
            // Update the filteredDoc field in the model
            model.setFilteredDoc(filteredDoc);
        } else {
            // when no filters, FilteredDoc should be the original doc
            model.setFilteredDoc(doc);
        }

        return null;
    }

    protected List<String> filter(List<String> doc, List<FilterType> filters) {
        List<String> filteredDoc = new ArrayList<String>();
        boolean emptyDoc = (doc == null) || (doc.isEmpty());
        boolean emptyFilters = (filters == null) || (filters.isEmpty());

        if (emptyDoc || emptyFilters) {
            return null;
        }

        for (String line : doc) {
            // Check if the line contains the filter name
            boolean found = false;
            Iterator<FilterType> it = filters.iterator();
            while (!found && it.hasNext()) {
                FilterType filter = it.next();
                found = (line.contains(filter.getName()));
            }

            // If the filter was found, add it to the filtered doc list
            if (found) {
                filteredDoc.add(line);
            }
        }

        return filteredDoc;
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
            view.showLogEntry(model.getFilteredDoc());
            parent.onSearch(SearchState.FILTERED_SEARCH);
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
            Util.showError(view, e);
            e.printStackTrace();
        }
    }

}
