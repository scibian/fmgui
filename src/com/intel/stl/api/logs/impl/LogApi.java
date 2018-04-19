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

package com.intel.stl.api.logs.impl;

import com.intel.stl.api.configuration.impl.SubnetContextImpl;
import com.intel.stl.api.logs.ILogApi;
import com.intel.stl.api.logs.ILogStateListener;
import com.intel.stl.api.logs.LogHelper;
import com.intel.stl.api.logs.LogInitBean;
import com.intel.stl.api.subnet.SubnetDescription;

/**
 * LogApi to support log functionality
 */
public class LogApi implements ILogApi {

    private final LogHelper logHelper;

    public LogApi(SubnetContextImpl subnetContext) {
        this.logHelper = new LogHelper(subnetContext);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#checkForFile(com.intel.stl.api.logs.
     * FileInfoBean)
     */
    @Override
    public void checkForFile() {
        logHelper.checkForFile();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logger.ILogApi#getPreviousLog(long)
     */
    @Override
    public void schedulePreviousPage(long numLinesRequested) {
        logHelper.schedulePreviousPage(numLinesRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logger.ILoggerApi#getNextLog(long)
     */
    @Override
    public void scheduleNextPage(long numLinesRequested) {
        logHelper.scheduleNextPage(numLinesRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getEndOfFile(long)
     */
    @Override
    public void scheduleLastLines(long numLinesRequested) {
        logHelper.scheduleLastLines(numLinesRequested);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getNumLines()
     */
    @Override
    public void scheduleNumLines() {
        logHelper.getNumLines();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.logger.ILoggerApi#setLogStateListener(com.intel.stl
     * .api.logger.ILogStateListener)
     */
    @Override
    public void setLogStateListener(ILogStateListener listener) {
        logHelper.setLogStateListener(listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.logs.ILogApi#startLog(com.intel.stl.api.logs.LogInitBean
     * , char[])
     */
    @Override
    public void startLog(LogInitBean logInitBean, char[] password) {
        logHelper.startLog(logInitBean, password);

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#stopLog()
     */
    @Override
    public void stopLog() {
        logHelper.stopLog();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#isRunning()
     */
    @Override
    public boolean isRunning() {
        return logHelper.isRunning();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#hasSession(com.intel.stl.api.subnet.
     * SubnetDescription)
     */
    @Override
    public boolean hasSession(SubnetDescription subnet) {
        return logHelper.hasSession(subnet);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#cleanup()
     */
    @Override
    public void cleanup() {
        logHelper.stopLog();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getLogFilePath()
     */
    @Override
    public String getLogFilePath() {
        return logHelper.getLogFilePath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getDefaultLogFilePath()
     */
    @Override
    public String getDefaultLogFilePath() {
        return logHelper.getDefaultLogFilePath();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getFileSize()
     */
    @Override
    public long getFileSize() {
        return logHelper.getFileSize();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getCurrentLine()
     */
    @Override
    public long getCurrentLine() {
        return logHelper.getCurrentLine();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getTotalLines()
     */
    @Override
    public long getTotalLines() {
        return logHelper.getTotalLines();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.logs.ILogApi#getSubnetDescription()
     */
    @Override
    public SubnetDescription getSubnetDescription() {
        return logHelper.getSubnetDescription();
    }
}
