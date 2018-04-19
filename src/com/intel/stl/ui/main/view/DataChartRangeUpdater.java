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
import com.intel.stl.ui.common.UIConstants;

public class DataChartRangeUpdater extends AbstractDataChartRangeUpdater {

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IChartRange#createTickUnits(long)
     */
    @Override
    public TickUnitSource createTickUnits(long upper) {
        long upperOrig = upper * UIConstants.BYTE_PER_FLIT;
        long numberOfTicks = 10L;

        if (upperOrig < KB) {
            // If upper is less than 1000, don't change anything.
            unitStr = STLConstants.K0697_BYTE.getValue();
            unitDes = STLConstants.K0697_BYTE.getValue();
            tickUnitSize = 1 / UIConstants.BYTE_PER_FLIT;
            return null;
        } else if (upperOrig < MB) {
            unitStr = STLConstants.K0695_KB.getValue();
            unitDes = STLConstants.K3308_KB_DESCRIPTION.getValue();
            tickUnitSize = KB / UIConstants.BYTE_PER_FLIT;
        } else if (upperOrig < GB) {
            unitStr = STLConstants.K0722_MB.getValue();
            unitDes = STLConstants.K3309_MB_DESCRIPTION.getValue();
            tickUnitSize = MB / UIConstants.BYTE_PER_FLIT;
        } else {
            unitStr = STLConstants.K0696_GB.getValue();
            unitDes = STLConstants.K3310_GB_DESCRIPTION.getValue();
            tickUnitSize = GB / UIConstants.BYTE_PER_FLIT;
        }

        TickUnits units = new TickUnits();
        double unit = Math.max(upper / (numberOfTicks * tickUnitSize), 1)
                * tickUnitSize;
        units.add(new ShiftedNumberTickUnit(unit, tickUnitSize));
        return units;
    }
}
