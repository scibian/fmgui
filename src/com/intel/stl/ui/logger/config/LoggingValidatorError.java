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

import java.util.HashMap;

import com.intel.stl.ui.common.UILabels;

public enum LoggingValidatorError {

    MAX_FILE_SIZE_MISSING(0, UILabels.STL50034_MAX_FILE_SIZE_MISSING),
    MAX_FILE_SIZE_INVALID_TYPE(1, UILabels.STL50035_MAX_FILE_SIZE_INVALID_TYPE),
    MAX_FILE_SIZE_FORMAT_EXCEPTION(2,
            UILabels.STL50036_MAX_FILE_SIZE_FORMAT_EXCEPTION),
    MAX_FILE_SIZE_OUT_OF_RANGE(3, UILabels.STL50062_MAX_FILE_SIZE_OUT_OF_RANGE),
    MAX_NUM_FILES_OUT_OF_RANGE(4, UILabels.STL50037_MAX_NUM_FILES_OUT_OF_RANGE),
    MAX_NUM_FILES_MISSING(5, UILabels.STL50038_MAX_NUM_FILES_MISSING),
    MAX_NUM_FILES_INVALID_TYPE(6, UILabels.STL50039_MAX_NUM_FILES_INVALID_TYPE),
    MAX_NUM_FILES_TOO_LARGE(7, UILabels.STL50040_MAX_NUM_FILES_TOO_LARGE),
    MAX_NUM_FILES_FORMAT_EXCEPTION(8,
            UILabels.STL50041_MAX_NUM_FILES_FORMAT_EXCEPTION),
    FILE_LOCATION_MISSING(9, UILabels.STL50042_FILE_LOCATION_MISSING),
    FILE_LOCATION_CREATION_ERROR(10,
            UILabels.STL50043_FILE_LOCATION_CREATION_ERROR),
    FILE_LOCATION_HEADLESS_ERROR(11,
            UILabels.STL50044_FILE_LOCATION_HEADLESS_ERROR),
    FILE_LOCATION_IO_ERROR(12, UILabels.STL50045_FILE_LOCATION_IO_ERROR),
    FILE_LOCATION_DIRECTORY_ERROR(13,
            UILabels.STL50046_FILE_LOCATION_DIRECTORY_ERROR),
    FORMAT_STRING_EMPTY(14, UILabels.STL50047_FORMAT_STRING_EMPTY),
    FORMAT_STRING_INVALID(15, UILabels.STL50048_FORMAT_STRING_INVALID),
    UNSUPPORTED_APPENDER_TYPE(16, UILabels.STL50060_UNSUPPORTED_APPENDER_TYPE),
    INVALID_THRESHOLD_TYPE(17, UILabels.STL50061_INVALID_THRESHOLD_TYPE),
    OK(18, UILabels.STL50063_OK);

    private final static HashMap<Integer, LoggingValidatorError> validateErrorMap =
            new HashMap<Integer, LoggingValidatorError>();

    static {
        for (LoggingValidatorError type : LoggingValidatorError.values()) {
            validateErrorMap.put(type.getId(), type);
        }
    }

    private final int id;

    private final String value;

    public final UILabels label;

    public static Object data;

    private LoggingValidatorError(int id, UILabels label) {

        this.id = id;
        this.label = label;
        this.value = label.getDescription();
    }

    public UILabels getLabel() {
        return label;
    }

    public int getId() {
        return id;
    }

    public String getValue() {
        return value;
    }

    public static String getValue(int id) {

        LoggingValidatorError err = validateErrorMap.get(id);
        String description = null;
        if (err != null) {
            description = err.getLabel().getDescription(data);
        }
        return description;
    }

    public static Object getData() {
        return data;
    }
}
