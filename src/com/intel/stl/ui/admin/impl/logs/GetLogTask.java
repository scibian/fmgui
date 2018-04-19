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

import javax.swing.SwingWorker;

import com.intel.stl.api.logs.ILogApi;
import com.intel.stl.api.logs.LogMessageType;

public class GetLogTask extends SwingWorker<Void, Void> {

    private final ILogApi logApi;

    private final long numLinesRequested;

    private final LogMessageType msgType;

    public GetLogTask(ILogApi logApi, LogMessageType msgType,
            long numLinesRequested) {
        this.logApi = logApi;
        this.msgType = msgType;
        this.numLinesRequested = numLinesRequested;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Void doInBackground() throws Exception {

        switch (msgType) {

            case LAST_LINES:
                logApi.scheduleLastLines(numLinesRequested);
                break;

            case PREVIOUS_PAGE:
                logApi.schedulePreviousPage(numLinesRequested);
                break;

            case NEXT_PAGE:
                logApi.scheduleNextPage(numLinesRequested);
                break;

            case NUM_LINES:
                logApi.scheduleNumLines();
                break;

            default:
                break;
        }

        return null;
    }

}
