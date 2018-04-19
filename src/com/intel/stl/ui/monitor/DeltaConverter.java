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

package com.intel.stl.ui.monitor;

import java.util.Date;

public class DeltaConverter {
    private boolean debug;

    private final String name;

    private Date latestTimestamp = null;

    private long latestCummValue;

    private Date earliestTimestamp = null;

    private long earliestCummValue;

    private final Delta delta = new Delta();

    /**
     * Description:
     * 
     * @param name
     */
    public DeltaConverter(String name) {
        super();
        this.name = name;
        // debug = name.equals("Received Packets Rate");
    }

    public synchronized Delta addValue(long cummValue, Date timestamp) {
        if (debug) {
            System.out.println("[" + name + "] " + timestamp.getTime() + " "
                    + cummValue);
        }
        if (latestTimestamp == null) {
            latestTimestamp = timestamp;
            latestCummValue = cummValue;
        }
        if (earliestTimestamp == null) {
            earliestTimestamp = timestamp;
            earliestCummValue = cummValue;
        }

        if (timestamp.after(latestTimestamp)) {
            delta.setValue(cummValue - latestCummValue);
            delta.setTime(timestamp);
            delta.setPeriodInSec((timestamp.getTime() - latestTimestamp
                    .getTime()) / 1000);
            latestTimestamp = timestamp;
            latestCummValue = cummValue;
            if (debug) {
                System.out.println("[" + name + "]  " + delta);
            }
            return delta;
        }

        if (timestamp.before(earliestTimestamp)) {
            delta.setValue(earliestCummValue - cummValue);
            delta.setTime(earliestTimestamp);
            delta.setPeriodInSec((earliestTimestamp.getTime() - timestamp
                    .getTime()) / 1000);
            earliestTimestamp = timestamp;
            earliestCummValue = cummValue;
            if (debug) {
                System.out.println("[" + name + "]  " + delta);
            }
            return delta;
        }

        return null;
    }

    public synchronized void clear() {
        if (debug) {
            System.out.println("[" + name + "]  CLEAR");
        }
        latestTimestamp = null;
        earliestTimestamp = null;
    }

    public static class Delta {
        private long periodInSec;

        private Date time;

        private long value;

        /**
         * @return the time
         */
        public Date getTime() {
            return time;
        }

        /**
         * @param time
         *            the time to set
         */
        public void setTime(Date time) {
            this.time = time;
        }

        /**
         * @return the value
         */
        public long getValue() {
            return value;
        }

        public long getNormalizedValue(long timePeriodInSec) {
            return value * timePeriodInSec / periodInSec;
        }

        /**
         * @param value
         *            the value to set
         */
        public void setValue(long value) {
            this.value = value;
        }

        /**
         * @return the periodInSec
         */
        public long getPeriodInSec() {
            return periodInSec;
        }

        /**
         * @param periodInSec
         *            the periodInSec to set
         */
        public void setPeriodInSec(long periodInSec) {
            this.periodInSec = periodInSec;
        }

        public double getRate() {
            return value / periodInSec;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Delta [time=" + time + " (" + time.getTime() + "), value="
                    + value + "]";
        }

    }
}
