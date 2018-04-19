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
 * #define NODE_TYPE_ALL 0xffffff
 * 
 * #define NOTICE_PRODUCERTYPE_CA           1
 * #define NOTICE_PRODUCERTYPE_SWITCH       2
 * #define NOTICE_PRODUCERTYPE_ROUTER       3
 * #define NOTICE_PRODUCERTYPE_CLASSMANAGER 4
 * 
 * ref:/ALL_EMB/Esm/ib/src/smi/sa/sa_InformInfo.c
 * static char * getNodeType(uint32_t nodeType)
 * {
 *     switch (nodeType) {
 *     case NOTICE_PRODUCERTYPE_CA:
 *         return "Channel Adapter";
 *     case NOTICE_PRODUCERTYPE_SWITCH:
 *         return "Switch";
 *     case NOTICE_PRODUCERTYPE_ROUTER:
 *         return "Router";
 *     case NOTICE_PRODUCERTYPE_CLASSMANAGER:
 *         return "Subnet Management";
 *     case NODE_TYPE_ALL:
 *         return "All producer types";
 *     default:
 *         return NULL;
 *     }
 * }
 * </pre>
 */
public enum ProducerType {
    CA(1),
    SWITCH(2),
    ROUTER(3),
    SM(4),
    ALL(0xffffff);

    private static Logger log = LoggerFactory.getLogger(ProducerType.class);
    private final int id;

    private ProducerType(int id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }
    
    public static ProducerType getProducerType(int id) {
        for (ProducerType type : ProducerType.values()) {
            if (type.getId()==id) {
                return type;
            }
        }
        log.warn("Unknown ProducerType id "+id);
        return null;
    }
}
