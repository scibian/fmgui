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

package com.intel.stl.api.management.virtualfabrics;

import com.intel.stl.api.management.WrapperNode;
import com.intel.stl.api.management.XMLConstants;
import com.intel.stl.api.management.virtualfabrics.HoqLife.TimeOut;
import com.intel.stl.api.management.virtualfabrics.HoqLife.TimeOut.Unit;

/**
 * This timeout is specified per SM instance and may be overridden per Virtual
 * Fabric. If not specified per Virtual Fabric, default to the SM instance
 * values.
 */
public class HoqLife extends WrapperNode<TimeOut> {
    private static final long serialVersionUID = -3442071697440514266L;

    public HoqLife() {
        this(null);
    }

    /**
     * Description:
     * 
     * @param type
     * @param value
     */
    public HoqLife(TimeOut value) {
        super(XMLConstants.HOQ_LIFE, value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.WrapperNode#valueOf(java.lang.String)
     */
    @Override
    protected TimeOut valueOf(String str) {
        str = str.trim();
        int pos = 0;
        while (pos < str.length() && Character.isDigit(str.charAt(pos))) {
            pos += 1;
        }
        if (pos < str.length() - 1) {
            String numStr = str.substring(0, pos);
            String unitStr = str.substring(pos).trim();
            Unit unit = Unit.valueOf(unitStr.toUpperCase());
            if (unit != null) {
                int num = Integer.parseInt(numStr);
                return new TimeOut(num, unit);
            }
        }
        throw new IllegalArgumentException("Invalid format '" + str + "'");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.WrapperNode#valueString(java.lang.Object)
     */
    @Override
    protected String valueString(TimeOut value) {
        return value.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.api.management.IAttribute#copy()
     */
    @Override
    public HoqLife copy() {
        return new HoqLife(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "HoqLife [type=" + type + ", value=" + value + "]";
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.management.WrapperNode#installVirtualFabric(com.intel
     * .stl.api.management.virtualfabrics.VirtualFabric)
     */
    @Override
    public void installVirtualFabric(VirtualFabric vf) {
        vf.setHoqLife(this);
    }

    public static class TimeOut {
        public enum Unit {
            NS,
            US,
            MS,
            S,
            M,
            H
        };

        private final int value;

        private final Unit unit;

        /**
         * Description:
         * 
         * @param value
         * @param unit
         */
        public TimeOut(int value, Unit unit) {
            super();
            this.value = value;
            this.unit = unit;
        }

        /**
         * @return the value
         */
        public int getValue() {
            return value;
        }

        /**
         * @return the unit
         */
        public Unit getUnit() {
            return unit;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#hashCode()
         */
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + ((unit == null) ? 0 : unit.hashCode());
            result = prime * result + value;
            return result;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#equals(java.lang.Object)
         */
        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            TimeOut other = (TimeOut) obj;
            if (unit == null) {
                if (other.unit != null) {
                    return false;
                }
            } else if (!unit.equals(other.unit)) {
                return false;
            }
            if (value != other.value) {
                return false;
            }
            return true;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return value + unit.name().toLowerCase();
        }

    }

}
