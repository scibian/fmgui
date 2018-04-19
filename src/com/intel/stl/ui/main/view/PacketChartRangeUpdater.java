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

package com.intel.stl.ui.main.view;

import org.jfree.chart.axis.TickUnitSource;
import org.jfree.chart.axis.TickUnits;

import com.intel.stl.ui.common.STLConstants;

public class PacketChartRangeUpdater extends AbstractPacketChartRangeUpdater {

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IChartRange#createTickUnits(long)
     */
    @Override
    public TickUnitSource createTickUnits(long upper) {
        long upperOrig = upper;
        // Decide how many digits we will skip using / (division operator).
        long tenMultiplier = 1L;
        long numOfTicks = 10L;

        while (upper >= 10L) {
            if (tenMultiplier >= Long.MAX_VALUE / 10L) {
                // Overflow will happen, so, break out of loop and set it to
                // giga.
                break;
            } else {
                tenMultiplier = tenMultiplier * 10L;
            }
            upper = upper / 10L;
        }

        if (tenMultiplier >= BILLION) {
            tickUnit = BILLION;
            unitStr = STLConstants.K3314_GP.getValue();
            unitDes = STLConstants.K3317_GP_DESCRIPTION.getValue();
        } else if (tenMultiplier >= MILLION) {
            tickUnit = MILLION;
            unitStr = STLConstants.K3313_MP.getValue();
            unitDes = STLConstants.K3316_MP_DESCRIPTION.getValue();
        } else if (tenMultiplier >= THOUSAND) {
            tickUnit = THOUSAND;
            unitStr = STLConstants.K3312_KP.getValue();
            unitDes = STLConstants.K3315_KP_DESCRIPTION.getValue();
        } else {
            // If upper is less than 1000, reset unitStr to empty string.
            tickUnit = 1L;
            unitStr = STLConstants.K3311_PACKETS.getValue();
            unitDes = STLConstants.K3311_PACKETS.getValue();
            return null;
        }

        TickUnits units = new TickUnits();
        if (DEBUG) {
            System.out.println("upperOrig=" + upperOrig);
            System.out.println("tickUnit=" + tickUnit);
        }

        double unit =
                Math.max(upperOrig / (numOfTicks * tickUnit), 1) * tickUnit;
        units.add(new ShiftedNumberTickUnit(unit, tickUnit));
        return units;
    }
}
