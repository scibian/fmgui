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

package com.intel.stl.api.logs;

/**
 * The LogCommander creates the command script to be sent to the remote SSH
 * server
 */
public class LogCommander {

    public static final String RESPONSE_EOM = "intel-inside";

    private ILogPageListener logPageListener;

    private FileInfoBean fileInfo;

    public LogCommander(FileInfoBean fileInfo) {
        this.fileInfo = fileInfo;
    }

    public void setPageMonitorListener(ILogPageListener listener) {
        logPageListener = listener;
    }

    public FileInfoBean getFileInfo() {
        return fileInfo;
    }

    public void setFileInfo(FileInfoBean fileInfo) {
        this.fileInfo = fileInfo;
    }

    public synchronized String getCommand(LogMessageType cmdType,
            long numLinesRequested) {

        String cmdStr = null;

        switch (cmdType) {

            case CHECK_FOR_FILE:
                cmdStr = checkForFile(fileInfo);
                break;

            case CHECK_FILE_ACCESS:
                cmdStr = checkFileAccess(fileInfo);
                break;

            case PREVIOUS_PAGE:
                cmdStr = getPreviousPage(fileInfo, numLinesRequested);
                break;

            case NEXT_PAGE:
                cmdStr = getNextPage(numLinesRequested, fileInfo);
                break;

            case NUM_LINES:
                cmdStr = getNumLines(fileInfo);
                break;

            case LAST_LINES:
                cmdStr = getEndOfFile(fileInfo, numLinesRequested);
                break;

            case FILE_SIZE:
                cmdStr = getFileSize(fileInfo);
                break;

            default:
                break;
        }

        cmdStr += "; echo " + RESPONSE_EOM;
        return cmdStr;
    }

    protected synchronized String checkForFile(FileInfoBean fileInfo) {

        String cmdStr = null;

        cmdStr = "ls " + fileInfo.getFileName();

        return cmdStr;
    }

    protected synchronized String checkFileAccess(FileInfoBean fileInfo) {

        String cmdStr = null;

        cmdStr = "tail " + fileInfo.getFileName()
                + " > /dev/null > 2&>1; echo $?";

        return cmdStr;
    }

    protected synchronized String getPreviousPage(FileInfoBean fileInfo,
            long numLinesRequested) {

        String cmdStr = null;
        String fileName = fileInfo.getFileName();
        long currentLine = fileInfo.getCurrentLine();
        long newCurrentLine = (currentLine >= numLinesRequested)
                ? (currentLine - numLinesRequested) : 1;

        boolean topOfFile = ((currentLine - 1) < numLinesRequested);
        long endLine = topOfFile ? numLinesRequested : (currentLine - 1);
        if (topOfFile) {
            cmdStr = "head -n " + endLine + " " + fileName;
        } else {
            cmdStr = "head -n " + endLine + " " + fileName + " | tail -n "
                    + numLinesRequested;
        }
        fileInfo.setCurrentLine(newCurrentLine);

        if (newCurrentLine > 0) {
            logPageListener.setStartLine(newCurrentLine);
            logPageListener.setEndLine(endLine);
        }

        if (fileInfo.getCurrentLine() == 1) {
            logPageListener.setFirstPage(true);
            logPageListener.setLastPage(false);
        } else {
            logPageListener.setFirstPage(false);
            logPageListener.setLastPage(false);
        }

        return cmdStr;
    }

    protected synchronized String getNextPage(long numLinesRequested,
            FileInfoBean fileInfo) {

        String cmdStr = null;
        String fileName = fileInfo.getFileName();
        long currentLine = fileInfo.getCurrentLine();
        long currentTotalNumLines = fileInfo.getTotalNumLines();

        // Make a correction to the end of range if numLines has been exceeded;
        // i.e. we're on the last page and new lines are added to the file
        long newCurrentLine = currentLine + numLinesRequested;
        long newEndLine = newCurrentLine + numLinesRequested - 1;
        if (newEndLine > currentTotalNumLines) {
            newCurrentLine = currentTotalNumLines - numLinesRequested + 1;
            newEndLine = currentTotalNumLines;

            // Still on the first page
            if (newCurrentLine < 0) {
                newCurrentLine = currentLine;
            }
        }

        cmdStr = "head -n " + newEndLine + " " + fileName + " | tail -n "
                + numLinesRequested;
        fileInfo.setCurrentLine(newCurrentLine);

        if (newCurrentLine > 0) {
            logPageListener.setStartLine(newCurrentLine);
            logPageListener.setEndLine(newEndLine);
        }

        if (newEndLine >= currentTotalNumLines) {
            logPageListener.setLastPage(true);
            if (currentTotalNumLines > numLinesRequested) {
                logPageListener.setFirstPage(false);
            } else {
                logPageListener.setFirstPage(true);
            }
        } else {
            logPageListener.setFirstPage(false);
            logPageListener.setLastPage(false);
        }

        return cmdStr;
    }

    protected synchronized String getNumLines(FileInfoBean fileInfo) {

        String cmdStr = null;
        String fileName = fileInfo.getFileName();
        long oldSize = fileInfo.getFileSize();

        // Create a command to retrieve the current size of the file
        // and the delta number of lines
        String newSize =
                "newSize=`ls -l " + fileName + " | awk '{print $5}'`; ";
        String diffSize =
                "diffSize=$(echo `expr \"$newSize\" - " + oldSize + "`); ";
        String getNewSize = "echo \"newSize=$newSize\";";
        String getNumLines = "echo \"numLines=`tail -c $diffSize " + fileName
                + " | wc -l`\"";

        cmdStr = newSize + diffSize + getNewSize + getNumLines;

        // Remember the last file size so it can be compared with the new size
        // the next time around; no need to change numLines here!
        fileInfo.update(oldSize, fileInfo.getTotalNumLines());
        return cmdStr;
    }

    protected synchronized String getEndOfFile(FileInfoBean fileInfo,
            long numLinesRequested) {

        String cmdStr = null;
        long totalNumLines = fileInfo.getTotalNumLines();
        String fileName = fileInfo.getFileName();

        long newCurrentLine;
        if (totalNumLines < numLinesRequested) {
            newCurrentLine = 1;
        } else {
            newCurrentLine = (fileInfo.getCurrentLine() <= 0) ? totalNumLines
                    : (totalNumLines - numLinesRequested + 1);
        }

        // This command will request the number of lines in the file and the
        // data at the end of the file
        cmdStr = getNumLines(fileInfo) + ";";
        cmdStr += "tail -n " + numLinesRequested + " " + fileName;
        fileInfo.setCurrentLine(newCurrentLine);

        if (numLinesRequested >= totalNumLines) {
            logPageListener.setFirstPage(true);
            logPageListener.setLastPage(true);
        } else {
            logPageListener.setFirstPage(false);
            logPageListener.setLastPage(true);
        }

        return cmdStr;
    }

    protected synchronized String getFileSize(FileInfoBean fileInfo) {

        String fileName = fileInfo.getFileName();

        return "ls -l " + fileName + " | awk '{print $5}'";
    }
}
