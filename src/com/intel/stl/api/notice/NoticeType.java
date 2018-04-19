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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * <pre>
 * ref:/ALL_EMB/Esm/ib/include/ib_mad.h
 * #define  TRAP_ALL    0xffff
 *  
 * #define  NOTICE_TYPE_FATAL       0
 * #define  NOTICE_TYPE_URGENT      1
 * #define  NOTICE_TYPE_SECURITY    2
 * #define  NOTICE_TYPE_SM          3
 * #define  NOTICE_TYPE_INFO        4
 * #define  NOTICE_TYPE_EMPTY       0x7f
 * 
 * ref:/ALL_EMB/Esm/ib/src/smi/sa/sa_InformInfo.c
 * static char * getType(uint16_t type)
 * {
 *  switch (type) {
 *  case NOTICE_TYPE_FATAL:
 *      return "Fatal";
 *  case NOTICE_TYPE_URGENT:
 *      return "Urgent";
 *  case NOTICE_TYPE_SECURITY:
 *      return "Security";
 *  case NOTICE_TYPE_SM:
 *      return "Subnet Management";
 *  case NOTICE_TYPE_INFO:
 *      return "Informational";
 *  case TRAP_ALL:
 *      return "All Types";
 *  default:
 *      return NULL;
 *  }
 * }
 * </pre>
 * We will ignore TRAP_ALL because notice type is only 7 bits, it doesn't
 * make sense here.
 */
public enum NoticeType {
    FATAL((byte)0),
    URGENT((byte)1),
    SECURITY((byte)2),
    SM((byte)3),
    INFO((byte)4),
    EMPTY((byte)0x7f);

    private static Logger log = LoggerFactory.getLogger(NoticeType.class);
    private final byte id;

    private NoticeType(byte id) {
        this.id = id;
    }
    
    /**
     * @return the id
     */
    public byte getId() {
        return id;
    }


    public static NoticeType getNoticeType(byte id) {
        for (NoticeType type : NoticeType.values()) {
            if (type.getId()==id) {
                return type;
            }
        }
        log.warn("Unknown NoticeType id "+id);
        return null;
    }
}
