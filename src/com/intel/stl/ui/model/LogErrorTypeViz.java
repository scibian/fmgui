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

package com.intel.stl.ui.model;

import com.intel.stl.api.logs.LogErrorType;
import com.intel.stl.ui.common.UILabels;

public enum LogErrorTypeViz {

    LOG_OK(LogErrorType.LOG_OK, UILabels.STL50208_LOG_OK),
    LOG_FILE_NOT_FOUND(LogErrorType.LOG_FILE_NOT_FOUND,
            UILabels.STL50209_LOG_FILE_NOT_FOUND),
    RESPONSE_TIMEOUT(LogErrorType.RESPONSE_TIMEOUT,
            UILabels.STL50212_RESPONSE_TIMEOUT),
    SSH_HOST_CONNECT_ERROR(LogErrorType.SSH_HOST_CONNECT_ERROR,
            UILabels.STL50210_SSH_UNABLE_TO_CONNECT),
    INVALID_LOG_USER(LogErrorType.INVALID_LOG_USER,
            UILabels.STL50214_INVALID_LOG_USER),
    SYSLOG_ACCESS_ERROR(LogErrorType.SYSLOG_ACCESS_ERROR,
            UILabels.STL50215_SYSLOG_ACCESS_ERROR),
    UNEXPECTED_LOGIN_FAILURE(LogErrorType.UNEXPECTED_LOGIN_FAILURE,
            UILabels.STL50216_UNEXPECTED_LOGIN_FAILURE),
    FILE_ACCESS_DENIED(LogErrorType.FILE_ACCESS_DENIED,
            UILabels.STL50217_FILE_ACCESS_DENIED),
    EMPTY_LOG_FILE(LogErrorType.EMPTY_LOG_FILE,
            UILabels.STL50218_EMPTY_LOG_FILE),
    INVALID_RESPONSE_FORMAT(LogErrorType.INVALID_RESPONSE_FORMAT,
            UILabels.STL50221_INVALID_RESPONSE_FORMAT);

    private final LogErrorType type;

    private UILabels label;

    private LogErrorTypeViz(LogErrorType type, UILabels label) {
        this.type = type;
        this.label = label;
    }

    public LogErrorType getType() {
        return type;
    }

    public UILabels getLabel() {
        return label;
    }

}
