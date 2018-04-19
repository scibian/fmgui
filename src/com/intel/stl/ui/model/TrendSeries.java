/**
 * Copyright (c) 2016, Intel Corporation
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

import java.util.TreeMap;

import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValues;

public class TrendSeries extends TimePeriodValues {

    /**
     *
     */
    private static final long serialVersionUID = -5075886854486506762L;

    private final TreeMap<Double, Integer> minMaxMap;

    /**
     * Description:
     *
     * @param name
     */
    public TrendSeries(String name) {
        super(name);
        minMaxMap = new TreeMap<Double, Integer>();
    }

    public long getMinY() {
        if (!minMaxMap.isEmpty()) {
            Double val = minMaxMap.firstKey();
            return val.longValue();
        }
        return 0;
    }

    public long getMaxY() {
        if (!minMaxMap.isEmpty()) {
            Double val = minMaxMap.lastKey();
            return val.longValue();
        }
        return 0;
    }

    @Override
    public void add(TimePeriod tp, double value) {
        super.add(tp, value);
        int counter = 1;
        if (minMaxMap.containsKey(value)) {
            counter = minMaxMap.get(value);
            counter += 1;
        }
        minMaxMap.put(value, counter);
    }

    @Override
    public void delete(int start, int end) {
        if (getItemCount() > end && end >= start) {
            for (int i = start; i <= end; i++) {
                double value = getValue(i).doubleValue();
                if (minMaxMap.containsKey(value)) {
                    int counter = minMaxMap.get(value);
                    counter -= 1;
                    if (counter > 0) {
                        minMaxMap.put(value, counter);
                    } else {
                        minMaxMap.remove(value);
                    }
                }
            }
        }
        super.delete(start, end);
    }
}
