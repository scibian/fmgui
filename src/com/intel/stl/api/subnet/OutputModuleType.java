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

package com.intel.stl.api.subnet;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum OutputModuleType {
    NONE((byte) 0x00),
    SDR((byte) 0x01),
    DDR((byte) 0x02),
    QDR((byte) 0x04),
    FDR((byte) 0x08),
    EDR((byte) 0x10),
    UNKNOWN((byte) 0xe0);

    private final byte id;

    private static Logger log = LoggerFactory.getLogger(OutputModuleType.class);

    private OutputModuleType(byte id) {
        this.id = id;
    }

    /**
     * @return the id
     */
    public byte getId() {
        return id;
    }

    public static OutputModuleType[] getOuputModuleType(byte id) {
        if (id == NONE.id) {
            return new OutputModuleType[] { NONE };
        } else if ((id & UNKNOWN.id) == UNKNOWN.id) {
            return new OutputModuleType[] { UNKNOWN };
        }

        List<OutputModuleType> res = new ArrayList<OutputModuleType>();
        for (OutputModuleType omt : OutputModuleType.values()) {
            if ((id & omt.id) == omt.id) {
                res.add(omt);
            }
        }

        if (res.isEmpty()) {
            log.error("Unknown OutputModuleType : id ='" + id + "'");
            return new OutputModuleType[] { UNKNOWN };
        } else {
            return res.toArray(new OutputModuleType[0]);
        }
    }
}
