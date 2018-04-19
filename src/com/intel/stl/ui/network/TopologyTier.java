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

package com.intel.stl.ui.network;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.intel.stl.ui.common.UILabels;

public class TopologyTier {
    private final String name;

    private final int index;

    private int numSwitches;

    private int numHFIs;

    private int numOtherPorts;

    private Quality upQuality;

    private Quality downQuality;

    /**
     * Description:
     * 
     * @param index
     */
    public TopologyTier(int index) {
        super();
        this.index = index;
        name = UILabels.STL10212_TIRE_N.getDescription(index);
    }

    /**
     * @return the nodes
     */
    public int getNumSwitches() {
        return numSwitches;
    }

    /**
     * @param nodes
     *            the nodes to set
     */
    public void setNumSwitches(int nodes) {
        this.numSwitches = nodes;
    }

    /**
     * @return the numHFIs
     */
    public int getNumHFIs() {
        return numHFIs;
    }

    /**
     * @param numHFIs
     *            the numHFIs to set
     */
    public void setNumHFIs(int numHFIs) {
        this.numHFIs = numHFIs;
    }

    /**
     * @return the otherPorts
     */
    public int getNumOtherPorts() {
        return numOtherPorts;
    }

    /**
     * @param otherPorts
     *            the otherPorts to set
     */
    public void setNumOtherPorts(int otherPorts) {
        this.numOtherPorts = otherPorts;
    }

    public int getTotalPorts() {
        return upQuality.getTotalPorts() + downQuality.getTotalPorts()
                + numOtherPorts;
    }

    public int getTotalActivePorts() {
        return upQuality.getTotalPorts() + downQuality.getTotalPorts();
    }

    /**
     * @return the upQuality
     */
    public Quality getUpQuality() {
        return upQuality;
    }

    /**
     * @param upQuality
     *            the upQuality to set
     */
    public void setUpQuality(Quality upQuality) {
        this.upQuality = upQuality;
    }

    /**
     * @return the downQuality
     */
    public Quality getDownQuality() {
        return downQuality;
    }

    /**
     * @param downQuality
     *            the downQuality to set
     */
    public void setDownQuality(Quality downQuality) {
        this.downQuality = downQuality;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the index
     */
    public int getIndex() {
        return index;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "TopologyTier [name=" + name + ", index=" + index + ", nodes="
                + numSwitches + ", upQuality=" + upQuality + ", downQuality="
                + downQuality + ", otherPorts=" + numOtherPorts + "]";
    }

    public static class Quality {
        private int totalPorts;

        private final List<Point> slowPorts = new ArrayList<Point>();

        private final List<Point> degPorts = new ArrayList<Point>();

        private int[] qualities;

        /**
         * @return the totalPorts
         */
        public int getTotalPorts() {
            return totalPorts;
        }

        /**
         * @param totalPorts
         *            the totalPorts to set
         */
        public void setTotalPorts(int totalPorts) {
            this.totalPorts = totalPorts;
        }

        public void increaseTotalPorts(int num) {
            totalPorts += num;
        }

        /**
         * @return the slowPorts
         */
        public int getNumSlowPorts() {
            return slowPorts.size();
        }

        /**
         * @return the slowPorts
         */
        public List<Point> getSlowPorts() {
            return slowPorts;
        }

        public void addSlowPort(int lid, int portNum) {
            slowPorts.add(new Point(lid, portNum));
        }

        /**
         * @return the degPorts
         */
        public int getNumDegPorts() {
            return degPorts.size();
        }

        /**
         * @return the degPorts
         */
        public List<Point> getDegPorts() {
            return degPorts;
        }

        public void addDegPort(int lid, int portNum) {
            degPorts.add(new Point(lid, portNum));
        }

        /**
         * @return the qualities
         */
        public int[] getQualities() {
            return qualities;
        }

        /**
         * @param qualities
         *            the qualities to set
         */
        public void setQualities(int[] qualities) {
            this.qualities = qualities;
        }

        /*
         * (non-Javadoc)
         * 
         * @see java.lang.Object#toString()
         */
        @Override
        public String toString() {
            return "Quality [totalPorts=" + totalPorts + ", slowPorts="
                    + slowPorts + ", degPorts=" + degPorts + ", qualities="
                    + Arrays.toString(qualities) + "]";
        }

    }
}
