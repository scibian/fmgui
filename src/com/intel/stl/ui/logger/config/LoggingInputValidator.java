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

package com.intel.stl.ui.logger.config;

import java.awt.HeadlessException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import com.intel.stl.api.configuration.AppenderConfig;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.LoggingThreshold;
import com.intel.stl.api.configuration.RollingFileAppender;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Validator;
import com.intel.stl.ui.model.LoggingThresholdViz;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.PatternLayout;
import ch.qos.logback.core.status.Status;

/**
 * Custom input validator to check the validity of the inputs on the Logging
 * Wizard view
 */
public class LoggingInputValidator {

    private final int OK = LoggingValidatorError.OK.getId();

    private final int MAX_BACKUP_FILES = 20;

    private final int kiloBytes = 1024;

    private final String byteStr = "B";

    private static LoggingInputValidator instance;

    ArrayList<String> validCodes =
            new ArrayList<String>(
                    Arrays.asList(STLConstants.K0649_SC.getValue().trim(),
                            STLConstants.K0650_C.getValue().trim(),
                            STLConstants.K0651_D.getValue().trim(),
                            STLConstants.K0652_F.getValue().trim(),
                            STLConstants.K0653_SL.getValue().trim(),
                            STLConstants.K0654_L.getValue().trim(),
                            STLConstants.K0655_SM.getValue().trim(),
                            STLConstants.K0656_M.getValue().trim(),
                            STLConstants.K0657_SN.getValue().trim(),
                            STLConstants.K0658_SP.getValue().trim(),
                            STLConstants.K0659_SR.getValue()
                                    .trim(),
            STLConstants.K0660_ST.getValue().trim(),
            STLConstants.K0661_SX.getValue().trim(),
            STLConstants.K0662_DOUBLE_PERCENT.getValue().trim()));

    public static LoggingInputValidator getInstance() {

        if (instance == null) {
            instance = new LoggingInputValidator();
        }

        return instance;
    }

    public int validate(LoggingConfiguration loggingConfig) {

        int errorCode = OK;
        LoggingThreshold rootLoggingLevel =
                loggingConfig.getRootLogger().getLevel();
        List<AppenderConfig> appenders = loggingConfig.getAppenders();

        // Find Rolling File Appender
        boolean found = false;
        RollingFileAppender fileAppender = null;
        Iterator<AppenderConfig> it = appenders.iterator();
        while (it.hasNext() && !found) {
            AppenderConfig appender = it.next();
            if (appender instanceof RollingFileAppender) {
                fileAppender = (RollingFileAppender) appender;
                found = true;
            }
        }

        // Currently supporting File Appender type only
        if (fileAppender != null) {
            // Validate information level (threshold)
            errorCode =
                    validateInformationLevel(fileAppender, rootLoggingLevel);
            if (errorCode == OK) {
                // Validate output format
                errorCode = validateOutputFormat(fileAppender);
                if (errorCode == OK) {
                    // Validate maximum file size
                    errorCode = validateMaxFileSize(fileAppender);
                    if (errorCode == OK) {
                        // Validate maximum number of files
                        errorCode = validateMaxNumFiles(fileAppender);
                        if (errorCode == OK) {
                            // Validate file location
                            errorCode = validateFileLocation(fileAppender);
                        }
                    }
                }
            }
        } else {

            errorCode = LoggingValidatorError.UNSUPPORTED_APPENDER_TYPE.getId();
        }

        return errorCode;
    }

    protected int validateInformationLevel(RollingFileAppender fileAppender,
            LoggingThreshold threshold) {

        int errorCode = OK;

        String thresholdName = threshold.name().toLowerCase();
        String thresholdNameVizUpper =
                LoggingThresholdViz.getLoggingThresholdName(threshold);
        String thresholdNameViz = null;
        if (thresholdNameVizUpper != null) {
            thresholdNameViz = thresholdNameVizUpper.toLowerCase();
        }
        if (!thresholdName.equals(thresholdNameViz)) {
            errorCode = LoggingValidatorError.INVALID_THRESHOLD_TYPE.getId();
        }

        // int i = 0;
        // boolean found = false;
        // while ((!found) && (i < LoggingThresholdViz.values().length)) {
        // found = threshold.equals(LoggingThresholdViz.values()[i]);
        // i++;
        // }
        //
        // if (!found) {
        // errorCode = LoggingValidatorError.INVALID_THRESHOLD_TYPE.getId();
        // }

        return errorCode;
    }

    protected int validateOutputFormat(RollingFileAppender fileAppender) {

        int errorCode = OK;

        // Check if conversion pattern is blank or null
        String conversionPattern = fileAppender.getConversionPattern();
        if ((conversionPattern == null) || (conversionPattern.equals(""))) {
            errorCode = LoggingValidatorError.FORMAT_STRING_EMPTY.getId();
        } else {
            // I think this validation implementation should go in the back end
            // to hide the logging tool in use
            LoggerContext loggerContext = new LoggerContext();
            loggerContext.reset();
            PatternLayout encoder = new PatternLayout();
            encoder.setContext(loggerContext);
            encoder.setPattern(conversionPattern);
            encoder.start();
            List<Status> errors =
                    loggerContext.getStatusManager().getCopyOfStatusList();
            if (errors.size() == 0) {
                errorCode = OK;
            } else {
                String emsg = errors.get(errors.size() - 1).getMessage();
                // This message should be shown to the user
                System.out.println(emsg);
                errorCode = LoggingValidatorError.FORMAT_STRING_INVALID.getId();
            }

            // Evaluate the validity of the pattern
            /*-
            String[] conversionArray = conversionPattern.split(" ");
            for (int i = 0; i < conversionArray.length; i++) {
                boolean validStartToken =
                        ((conversionArray[i].startsWith("%")) || (conversionArray[i]
                                .startsWith("[%")));

                if (validStartToken) {
                    String token = conversionArray[i].replace("[", "");
                    token = token.replace("]", "");
                    if (!validCodes.contains(token)) {
                        errorCode =
                                LoggingValidatorError.FORMAT_STRING_INVALID
                                        .getId();
                    }
                } else {
                    System.out.println("Token in error: " + conversionArray[i]);
                    errorCode =
                            LoggingValidatorError.FORMAT_STRING_INVALID.getId();
                }
            }
             */

        }

        return errorCode;
    }

    protected int validateMaxFileSize(RollingFileAppender fileAppender) {

        int errorCode = OK;

        String maxFileSizeStr = fileAppender.getMaxFileSize();

        // Check if conversion pattern is blank or null
        if (Validator.isBlankOrNull(maxFileSizeStr)) {

            errorCode = LoggingValidatorError.MAX_FILE_SIZE_MISSING.getId();
        } else {

            String maxFileSizeNumOnly = null;
            String unit = null;

            // Separate the number from the units
            if (!maxFileSizeStr.endsWith(byteStr)) {
                maxFileSizeNumOnly = maxFileSizeStr;
            } else {
                unit = maxFileSizeStr.substring(maxFileSizeStr.length() - 2,
                        maxFileSizeStr.length());
                maxFileSizeNumOnly = maxFileSizeStr.substring(0,
                        maxFileSizeStr.length() - 2);
            }

            // Check the validity of the number
            try {
                Long maxFileSize = new Long(maxFileSizeNumOnly);
                Long max = null;

                if (unit == null) {
                    max = Long.MAX_VALUE;
                } else if (unit.equals(STLConstants.K0695_KB.getValue())) {
                    max = Long.MAX_VALUE / kiloBytes;
                } else if (unit.equals(STLConstants.K0722_MB.getValue())) {
                    max = Long.MAX_VALUE / (kiloBytes * kiloBytes);
                } else if (unit.equals(STLConstants.K0696_GB.getValue())) {
                    max = Long.MAX_VALUE / (kiloBytes * kiloBytes * kiloBytes);
                } else {
                    // shouldn't happen
                    throw new IllegalArgumentException(
                            "Unknow unit '" + unit + "'");
                }

                if ((maxFileSize <= 0) || (max < maxFileSize)) {

                    LoggingValidatorError.data = max;
                    errorCode = LoggingValidatorError.MAX_FILE_SIZE_OUT_OF_RANGE
                            .getId();
                }

            } catch (NumberFormatException e) {

                errorCode = LoggingValidatorError.MAX_FILE_SIZE_FORMAT_EXCEPTION
                        .getId();
            }

        }

        return errorCode;
    }

    protected int validateMaxNumFiles(RollingFileAppender fileAppender) {

        int errorCode = OK;
        String maxNumFilesStr = fileAppender.getMaxNumOfBackUp();

        try {

            if (Validator.isBlankOrNull(maxNumFilesStr)) {
                errorCode = LoggingValidatorError.MAX_NUM_FILES_MISSING.getId();
            } else {
                int maxNumFiles = Integer.parseInt(maxNumFilesStr);

                // Check that the number is an integer
                if ((Integer
                        .valueOf(maxNumFiles) instanceof Integer) == false) {
                    errorCode = LoggingValidatorError.MAX_NUM_FILES_INVALID_TYPE
                            .getId();
                }

                // Range check the max number of files
                if ((maxNumFiles <= 0) || (MAX_BACKUP_FILES < maxNumFiles)) {

                    LoggingValidatorError.data = MAX_BACKUP_FILES;
                    errorCode = LoggingValidatorError.MAX_NUM_FILES_OUT_OF_RANGE
                            .getId();
                }
            }
        } catch (NumberFormatException e) {

            errorCode = LoggingValidatorError.MAX_NUM_FILES_FORMAT_EXCEPTION
                    .getId();
        }

        return errorCode;
    }

    protected int validateFileLocation(RollingFileAppender fileAppender) {

        int errorCode = OK;
        File file = new File(fileAppender.getFileLocation());

        // Make sure the file location isn't null or blank
        String fileLocationStr = fileAppender.getFileLocation();
        if (fileLocationStr == null || fileLocationStr.equals("")) {
            errorCode = LoggingValidatorError.FILE_LOCATION_MISSING.getId();

        } else {

            // Make sure this file location isn't a directory
            if (file.isDirectory()) {
                errorCode = LoggingValidatorError.FILE_LOCATION_DIRECTORY_ERROR
                        .getId();
            } else {
                // See if the file exists
                if (!file.exists()) {

                    try {
                        if (!file.createNewFile()) {
                            errorCode =
                                    LoggingValidatorError.FILE_LOCATION_CREATION_ERROR
                                            .getId();
                        }
                    } catch (HeadlessException e) {
                        errorCode =
                                LoggingValidatorError.FILE_LOCATION_HEADLESS_ERROR
                                        .getId();

                    } catch (IOException e) {
                        errorCode = LoggingValidatorError.FILE_LOCATION_IO_ERROR
                                .getId();
                    }
                }
            }
        }

        return errorCode;
    }

}
