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

import com.intel.stl.ui.common.STLConstants;

public enum HistoryType {
    CURRENT(0, STLConstants.K1114_CURRENT.getValue()),
    ONE(1, Integer.toString(1) + STLConstants.K1112_HOURS.getValue()),
    TWO(2, Integer.toString(2) + STLConstants.K1112_HOURS.getValue()),
    SIX(6, Integer.toString(6) + STLConstants.K1112_HOURS.getValue()),
    DAY(24, Integer.toString(24) + STLConstants.K1112_HOURS.getValue());

    private final String name;

    // length of history in hours
    private final int value;

    /**
     * 
     * Description:
     * 
     * @param value
     * @param name
     */
    private HistoryType(int value, String name) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    public int getValue() {
        return value;
    }

    public int getLengthInSeconds() {
        return value * 3600;
    }

    public int getMaxDataPoints(int refreshRate) {
        // Calculate maxDataPoints based on history type, refresh rate
        int maxDataPoints = getLengthInSeconds() / refreshRate;
        return maxDataPoints;
    }

    @Override
    public String toString() {
        return name;
    }
}
