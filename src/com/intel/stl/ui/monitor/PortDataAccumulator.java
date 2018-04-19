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

/**
 * Storage class for performance table accumulators
 */
public class PortDataAccumulator {
    /**
     * Captured packet and data accumulators when querying totals data
     */
    private long rxCumulativePacket = 0;

    private long rxCumulativeData = 0;

    private long txCumulativePacket = 0;

    private long txCumulativeData = 0;

    /**
     * Calculated packet and data accumulators when querying delta data
     */
    private long rxPacketAcc = 0;

    private long rxDataAcc = 0;

    private long txPacketAcc = 0;

    private long txDataAcc = 0;

    /**
     * @return the rxCumulativePacket
     */
    public long getRxCumulativePacket() {
        return rxCumulativePacket;
    }

    /**
     * @param rxCumulativePacket
     *            the rxCumulativePacket to set
     */
    public void setRxCumulativePacket(long rxCumulativePacket) {
        this.rxCumulativePacket = rxCumulativePacket;
    }

    /**
     * @return the rxCumulativeData
     */
    public long getRxCumulativeData() {
        return rxCumulativeData;
    }

    /**
     * @param rxCumulativeData
     *            the rxCumulativeData to set
     */
    public void setRxCumulativeData(long rxCumulativeData) {
        this.rxCumulativeData = rxCumulativeData;
    }

    /**
     * @return the txCumulativePacket
     */
    public long getTxCumulativePacket() {
        return txCumulativePacket;
    }

    /**
     * @param txCumulativePacket
     *            the txCumulativePacket to set
     */
    public void setTxCumulativePacket(long txCumulativePacket) {
        this.txCumulativePacket = txCumulativePacket;
    }

    /**
     * @return the txCumulativeData
     */
    public long getTxCumulativeData() {
        return txCumulativeData;
    }

    /**
     * @param txCumulativeData
     *            the txCumulativeData to set
     */
    public void setTxCumulativeData(long txCumulativeData) {
        this.txCumulativeData = txCumulativeData;
    }

    /**
     * @return the rxPacketAcc
     */
    public long getRxPacketAcc() {
        return rxPacketAcc;
    }

    /**
     * @param rxPacketAcc
     *            - value by which to increment accumulator
     */
    public void incRxPacketAcc(long rxPacketAcc) {
        this.rxPacketAcc += rxPacketAcc;
        this.rxPacketAcc %= Long.MAX_VALUE;
    }

    /**
     * @return the rxDataAcc
     */
    public long getRxDataAcc() {
        return rxDataAcc;
    }

    /**
     * @param rxDataAcc
     *            - value by which to increment accumulator
     */
    public void incRxDataAcc(long rxDataAcc) {
        this.rxDataAcc += rxDataAcc;
        this.rxDataAcc %= Long.MAX_VALUE;
    }

    /**
     * @return the txPacketAcc
     */
    public long getTxPacketAcc() {
        return txPacketAcc;
    }

    /**
     * @param txPacketAcc
     *            - value by which to increment accumulator
     */
    public void incTxPacketAcc(long txPacketAcc) {
        this.txPacketAcc += txPacketAcc;
        this.txPacketAcc %= Long.MAX_VALUE;
    }

    /**
     * @return the txDataAcc
     */
    public long getTxDataAcc() {
        return txDataAcc;
    }

    /**
     * @param txDataAcc
     *            - value by which to increment accumulator
     */
    public void incTxDataAcc(long txDataAcc) {
        this.txDataAcc += txDataAcc;
        this.txDataAcc %= Long.MAX_VALUE;
    }

    @Override
    public String toString() {
        return "[rxCumulativePacket=" + rxCumulativePacket
                + ", rxCumulativeData=" + rxCumulativeData
                + ", txCumulativePacket=" + txCumulativePacket
                + ", txCumulativeData=" + txCumulativeData + ", rxPacketAcc="
                + rxPacketAcc + ", rxDataAcc=" + rxDataAcc + ", txPacketAcc="
                + txPacketAcc + ", txDataAcc=" + txDataAcc + "]";
    }

}
