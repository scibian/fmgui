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

package com.intel.stl.api.notice;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <b>Note</b>: We will use NoticeSeverity ordinal to compare severity level. We
 * must ensure that severity levels are listed at increase order!
 */
public enum NoticeSeverity {
    INFO(0),
    WARNING(1),
    ERROR(2),
    CRITICAL(3);

    private final static Map<String, NoticeSeverity> severityMap =
            new HashMap<String, NoticeSeverity>();
    static {
        for (NoticeSeverity severity : NoticeSeverity.values()) {
            severityMap.put(severity.name(), severity);
        }
    };

    private static Logger log = LoggerFactory.getLogger(NoticeSeverity.class);

    private final int id;

    private NoticeSeverity(int id) {
        this.id = id;
    }

    public static NoticeSeverity getNoticeSeverity(NoticeType type) {
        if (type == null) {
            return null;
        }

        switch (type) {
            case FATAL:
                return NoticeSeverity.CRITICAL;
            case URGENT:
                return NoticeSeverity.ERROR;
            case SECURITY:
                return NoticeSeverity.WARNING;
            case SM:
                return NoticeSeverity.WARNING;
            case INFO:
                return NoticeSeverity.INFO;
            default:
                log.warn("Unknown NotieType " + type);
                return null;
        }
    }

    public static NoticeSeverity getNoticeSeverity(byte type) {
        return getNoticeSeverity(NoticeType.getNoticeType(type));
    }

    public static NoticeSeverity getNoticeSeverity(int id) {

        NoticeSeverity severity = null;

        switch (id) {
            case 0:
                severity = INFO;
                break;

            case 1:
                severity = WARNING;
                break;

            case 2:
                severity = ERROR;
                break;

            case 3:
                severity = CRITICAL;
                break;
        }

        return severity;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    public static NoticeSeverity getNoticeSeverity(String severityName) {
        return severityMap.get(severityName);
    }
}
