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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.management.FMConfHelper;
import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SshLoginBean;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.fecdriver.network.ssh.SshKeyType;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSession;
import com.intel.stl.fecdriver.network.ssh.impl.JSchSessionFactory;
import com.jcraft.jsch.JSchException;

/**
 * Over-arching helper class to the LogApi, to submit commands to the
 * LogCommandProcessor, and process responses for the UI
 */
public class LogHelper
        implements IResponseListener, ILogErrorListener, ILogPageListener {

    private final static Logger log = LoggerFactory.getLogger(LogHelper.class);

    public final String DEFAULT_LOG_FILE = "/var/log/messages";

    private final boolean DEBUG_LOG = false;

    private final boolean DEBUG_RESPONSE = false;

    private final static int RESPONSE_TIMEOUT = 10000; // 10 seconds

    private boolean usingDefaultLogFile = false;

    private final LogHelper helper = this;

    private boolean logRunning = false;

    private ILogStateListener logStateListener;

    private final SubnetDescription subnet;

    private final FMConfHelper fmConfigHelper;

    private final FMConfigParser fmConfigParser;

    private final IManagementApi managementApi;

    private LogStatusTask logStatusTask;

    private LogErrorType errorCode;

    private LogCommandProcessor userCommandProcessor;

    private final LogCommander logCommander;

    private FileInfoBean fileInfo;

    private String logFilePath;

    private JSchSession jschSession;

    private boolean initInProgress;

    private String logHost;

    private LogConfigType configType;

    public LogHelper(SubnetContext subnetContext) {
        super();
        this.subnet = subnetContext.getSubnetDescription();
        managementApi = subnetContext.getManagementApi();
        fmConfigHelper = FMConfHelper.getInstance(subnet);
        fmConfigParser = new FMConfigParser(fmConfigHelper);
        fileInfo = new FileInfoBean(DEFAULT_LOG_FILE, 0, 0, 0);
        this.logCommander = new LogCommander(fileInfo);
        logCommander.setPageMonitorListener(this);

    }

    protected synchronized void initializationTask(
            final LogInitBean logInitBean, final char[] password) {

        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    initInProgress = true;
                    errorCode = LogErrorType.LOG_OK;

                    // Start the SSH session
                    helper.logHost = logInitBean.getLogHost();
                    errorCode = initializeSsh(logInitBean, password);

                    if (errorCode == LogErrorType.LOG_OK) {
                        // Initialize the User Command Processor
                        userCommandProcessor = new LogCommandProcessor(
                                jschSession, RESPONSE_TIMEOUT,
                                helper.getClass().getSimpleName());
                        userCommandProcessor.setResponseListener(helper);

                        // Initialize the log file path
                        if (configType.equals(LogConfigType.CUSTOM_CONFIG)) {
                            helper.logFilePath = logInitBean.getLogFilePath();
                        } else {
                            helper.logFilePath =
                                    initLogFilePath(logInitBean, password);
                        }
                        fileInfo.setFileName(logFilePath);

                        // Check if the log file exists
                        checkForFile();
                    } else {
                        logStateListener.onError(errorCode, logHost);
                    }
                } catch (Exception e) {
                    logStateListener.onError(
                            LogErrorType.UNEXPECTED_LOGIN_FAILURE,
                            e.getMessage());
                    stopLog();
                }
            }
        }).start();
    }

    protected String initLogFilePath(LogInitBean logInitBean,
            final char[] password) {
        try {
            // Retrieve the log file name from FMConfig
            logFilePath = fmConfigParser.getLogFilePath(password);

            // If there is no logfile path in the config
            // file, check if the default file exists
            if (logFilePath == null) {
                logFilePath = DEFAULT_LOG_FILE;
            }

            if (DEBUG_LOG) {
                // Override the SM Log file
                logFilePath = "/nfs/site/home/rjtierne/bin/messages";
            }

            // Initialize the file info bean
            fileInfo.setFileName(logFilePath);

            // Check if the log file exists
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return logFilePath;
    }

    protected void startLogStatusTask(long delay, long timeBetweenExecutions) {
        // Initialize the timer task to retrieve total # lines
        // Share fileInfo with the numLine timer task
        logStatusTask = new LogStatusTask(logFilePath, LogMessageType.NUM_LINES,
                jschSession, fileInfo);
        logStatusTask.setResponseListener(helper);
        logStatusTask.setErrorListener(helper);
        logStatusTask.start(delay, timeBetweenExecutions);
    }

    protected void debug(String... msgs) {
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        System.out.print(formatter.format(new Date()) + ": ");
        for (int i = 0; i < msgs.length; i++) {
            System.out.print(msgs[i]);
            if ((i > 0) && (i % msgs.length) != 0) {
                System.out.print(", ");
            } else {
                System.out.print(" ");
            }
        }
        System.out.println();
    }

    public String getLogFilePath() {
        return fileInfo.getFileName();
    }

    public String getDefaultLogFilePath() {
        return DEFAULT_LOG_FILE;
    }

    public long getFileSize() {
        return fileInfo.getFileSize();
    }

    protected synchronized String getScriptCmd(LogMessageType msgType,
            long numLinesRequested) {

        logCommander.setFileInfo(fileInfo);
        String cmd = logCommander.getCommand(msgType, numLinesRequested);

        return cmd;
    }

    public void getNumLines() {
        String cmd = getScriptCmd(LogMessageType.NUM_LINES, 0);
        userCommandProcessor.executeCommand(LogMessageType.NUM_LINES, cmd);
    }

    public long getCurrentLine() {
        return fileInfo.getCurrentLine();
    }

    public long getTotalLines() {
        return fileInfo.getTotalNumLines();
    }

    public void checkForFile() {
        String cmd = getScriptCmd(LogMessageType.CHECK_FOR_FILE, 0);
        userCommandProcessor.executeCommand(LogMessageType.CHECK_FOR_FILE, cmd);
    }

    public void checkFileAccess() {
        String cmd = getScriptCmd(LogMessageType.CHECK_FILE_ACCESS, 0);
        userCommandProcessor.executeCommand(LogMessageType.CHECK_FILE_ACCESS,
                cmd);
    }

    public synchronized void schedulePreviousPage(long numLinesRequested) {
        String cmd =
                getScriptCmd(LogMessageType.PREVIOUS_PAGE, numLinesRequested);
        userCommandProcessor.executeCommand(LogMessageType.PREVIOUS_PAGE, cmd);
    }

    public synchronized void scheduleNextPage(long numLinesRequested) {
        String cmd = getScriptCmd(LogMessageType.NEXT_PAGE, numLinesRequested);
        userCommandProcessor.executeCommand(LogMessageType.NEXT_PAGE, cmd);
    }

    public synchronized void scheduleLastLines(long numLinesRequested) {
        String cmd = getScriptCmd(LogMessageType.LAST_LINES, numLinesRequested);
        userCommandProcessor.executeCommand(LogMessageType.LAST_LINES, cmd);
    }

    public void setLogStateListener(ILogStateListener listener) {
        logStateListener = listener;
    }

    public SubnetDescription getSubnetDescription() {
        return managementApi.getSubnetDescription();
    }

    protected LogErrorType initializeSsh(LogInitBean logInitBean,
            char[] password) {

        LogErrorType error = LogErrorType.LOG_OK;

        try {
            // Create an SshLoginBean with the new information and make a
            // copy of the subnet
            HostInfo hostInfo = subnet.getCurrentFE();
            SshLoginBean sshLoginBean =
                    new SshLoginBean(subnet.getSubnetId(), subnet.getName(),
                            /* hostInfo.getSshUserName(), */logInitBean
                                    .getUserName(),
                            logInitBean.getLogHost(),
                            Integer.valueOf(hostInfo.getPort()),
                            subnet.getCurrentFE().getCertsDescription());
            SubnetDescription sNet = new SubnetDescription(sshLoginBean);

            // Initialize the session with the copied subnet
            jschSession = JSchSessionFactory.getSession(sNet,
                    logInitBean.isStrictHostKey(), password,
                    SshKeyType.LOG_KEY.getKey(subnet.getSubnetId()));
        } catch (JSchException e) {
            error = LogErrorType.SSH_HOST_CONNECT_ERROR;
            log.error(e.getMessage(), e);
        }
        return error;
    }

    public boolean isRunning() {
        return logRunning;
    }

    public boolean hasSession(SubnetDescription subnet) {
        boolean connectionStatus = false;

        // Check if the factory has a session for this subnet
        // and if it does, verify that it is connected
        JSchSession session = JSchSessionFactory.getSessionFromMap(
                SshKeyType.LOG_KEY.getKey(subnet.getSubnetId()));

        if (session != null) {
            connectionStatus = session.isConnected();
        }

        return connectionStatus;
    }

    protected void onFinish(LogErrorType errorCode, Object... data) {
        if (errorCode.getId() == LogErrorType.LOG_OK.getId()) {
            logRunning = true;
            logStateListener.onReady();

            // Start the file status timer task
            startLogStatusTask(0, 10000);
        } else {
            logRunning = false;
            logStateListener.onError(errorCode, data);
            stopLog();
        }
    }

    public void startLog(LogInitBean logInitBean, char[] password) {
        if (!logRunning) {
            debug("Start Log...");
            this.configType = logInitBean.getConfigType();
            initializationTask(logInitBean, password);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.logs.IErrorListener#stopLog()
     */
    @Override
    public void stopLog() {
        try {
            if (logRunning) {
                if (userCommandProcessor != null) {
                    try {
                        userCommandProcessor.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (logStatusTask != null) {
                    try {
                        logStatusTask.stop();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                debug("Log Stopped...");
            }
        } finally {
            try {
                // Shut down the session
                JSchSessionFactory.closeSession(
                        SshKeyType.LOG_KEY.getKey(subnet.getSubnetId()));
            } finally {
                logRunning = false;
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.logs.ICommandListener#onResponseReceived(LogResponse)
     */
    @Override
    public synchronized void onResponseReceived(LogResponse response) {
        switch (response.getMsgType()) {
            case CHECK_FOR_FILE:
                String fileName = response.getEntries().get(0);
                if (fileName.contains(logFilePath)) {
                    logStateListener.onResponse(response);

                    // Check if the file is accessible
                    checkFileAccess();
                }
                break;

            case CHECK_FILE_ACCESS:
                int returnCode = Integer.valueOf(response.getEntries().get(0));
                boolean fileAccessible = (returnCode == 0);
                if (fileAccessible) {
                    logStateListener.onResponse(response);
                    onFinish(errorCode, logFilePath);
                } else {
                    onResponseError(LogErrorType.FILE_ACCESS_DENIED,
                            LogMessageType.CHECK_FILE_ACCESS);
                }
                break;

            case PREVIOUS_PAGE:
            case NEXT_PAGE:
            case FILE_SIZE:
                logStateListener.onResponse(response);
                break;

            case NUM_LINES:
                // This case is only called by LogStatusTask on a periodic
                // interval when the number of lines in the file is retrieved.
                // This response contains both the fileSize and "total" number
                // of lines in the file AFTER the delta-numLines have been added
                if (response.getEntries().size() > 0) {
                    List<String> entries = response.getEntries();

                    // Get fileSize and # lines values from LogStatusTask and
                    // store in FileInfoBean
                    long fileSize = Long.parseLong(
                            entries.get(LogStatusTask.FILE_SIZE_POSITION));
                    long totalNumLines = Long.parseLong(
                            entries.get(LogStatusTask.NUM_LINE_POSITION));

                    // Update the file size and numLines
                    fileInfo.update(fileSize, totalNumLines);

                    if (initInProgress && (totalNumLines > 0)) {
                        fileInfo.setCurrentLine(totalNumLines);
                        initInProgress = false;
                    }

                    // Prepare the response for the UI; it's only expecting
                    // the number of lines so remove the file size
                    entries.remove(LogStatusTask.FILE_SIZE_POSITION);

                    logStateListener.onResponse(response);
                }
                break;

            case LAST_LINES:
                // This case is only called when the Refresh button is clicked
                // On the UI, SMLogController#onLastLines() calls
                // LogHelper#scheduleLastLines() through LogApi to execute the
                // command to retrieve the following:
                // fileSize: new file size (in the form "name=value")
                // numNewLines: # lines added since last checked ("name=value")
                // data - lines of data from the file

                if (response.getEntries().size() > 0) {
                    List<String> entries = response.getEntries();

                    // Parse the values for fileSize and numLines
                    long fileSize = Long.parseLong(
                            entries.get(LogStatusTask.FILE_SIZE_POSITION)
                                    .split("=")[1]);
                    long numNewLines = Long.parseLong(
                            entries.get(LogStatusTask.NUM_LINE_POSITION)
                                    .split("=")[1]);

                    // Add in the number of new lines to the current total
                    long currentTotal = fileInfo.getTotalNumLines();
                    long totalNumLines = currentTotal + numNewLines;

                    // Update the file size and numLines
                    fileInfo.update(fileSize, totalNumLines);
                    debugResponse(currentTotal, numNewLines, totalNumLines);

                    // Prepare the response for the UI; it's only expecting
                    // the number of lines so remove the file size
                    entries.remove(LogStatusTask.FILE_SIZE_POSITION);

                    // Change this entry to the value instead of "name=value"
                    entries.set(0, String.valueOf(totalNumLines));
                    logStateListener.onResponse(response);
                }
                break;

            case EXIT:
                break;

            case UNKNOWN:
                break;

            default:
                break;
        }
    }

    public void debugResponse(long currentTotal, long numLines,
            long totalNumLines) {
        if (DEBUG_RESPONSE) {
            System.out.println(this.getClass().getSimpleName()
                    + " - currentTotal:" + currentTotal + " numLines:"
                    + numLines + " totalNumLines:" + totalNumLines);
            System.out.println();
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.api.logs.IResponseListener#onResponseError(LogErrorType,
     * LogMessageType)
     */
    @Override
    public synchronized void onResponseError(LogErrorType errorCode,
            LogMessageType msgType, Object... data) {

        switch (errorCode) {
            case LOG_OK:
                break;

            case SSH_HOST_CONNECT_ERROR:
            case INVALID_LOG_USER:
            case UNEXPECTED_LOGIN_FAILURE:
                break;

            case LOG_FILE_NOT_FOUND:
                // For auto-config try the default log file as a backup plan
                if (configType == LogConfigType.AUTO_CONFIG) {
                    if (usingDefaultLogFile) {
                        logStateListener.onError(errorCode, logFilePath,
                                DEFAULT_LOG_FILE);
                    } else {
                        // Re-send the command requesting the default log
                        // file
                        logFilePath = DEFAULT_LOG_FILE;
                        fileInfo = new FileInfoBean(logFilePath, 0, 0, 0);
                        fileInfo.setFileName(logFilePath);
                        usingDefaultLogFile = true;
                        checkForFile();
                        return;
                    }
                } else {
                    // For custom-config, just issue an error
                    logStateListener.onError(errorCode, logFilePath, "N/A");
                }
                break;

            case FILE_ACCESS_DENIED:
            case EMPTY_LOG_FILE:
                logStateListener.onError(errorCode, logFilePath);
                break;

            case SYSLOG_ACCESS_ERROR:
                logStateListener.onError(errorCode, logHost);
                break;

            case RESPONSE_TIMEOUT:
                logStateListener.onError(errorCode, logHost);
                break;

            case INVALID_RESPONSE_FORMAT:
                logStateListener.onError(errorCode, data);
                break;

            default:
                break;
        }
        stopLog();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.logs.ILogPageListener#setFirstPage(boolean)
     */
    @Override
    public void setFirstPage(boolean b) {
        logStateListener.setFirstPage(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.logs.ILogPageListener#setLastPage(boolean)
     */
    @Override
    public void setLastPage(boolean b) {
        logStateListener.setLastPage(b);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.logs.ILogPageListener#setStartLine(long)
     */
    @Override
    public void setStartLine(long lineNum) {
        logStateListener.setStartLine(lineNum);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.api.logs.ILogPageListener#setEndLine(long)
     */
    @Override
    public void setEndLine(long lineNum) {
        logStateListener.setEndLine(lineNum);
    }
}
