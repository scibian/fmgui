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
package com.intel.stl.ui.configuration;

import static com.intel.stl.ui.common.STLConstants.K0130_MS;
import static com.intel.stl.ui.common.STLConstants.K0131_US;
import static com.intel.stl.ui.common.STLConstants.K0132_S;
import static com.intel.stl.ui.common.STLConstants.K0133_INFINITE;
import static com.intel.stl.ui.model.DeviceProperty.HOQLIFE;
import static com.intel.stl.ui.model.DeviceProperty.NUM_VL;

import com.intel.stl.api.Utils;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.SAConstants;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyItem;

public class HoQLifeProcessor extends BaseCategoryProcessor {
    // to be able to display both infinite and other values in a chart, we need
    // to set INFINITE to a reasonable large number rather than real infinite
    // that will turn out show nothing on chart
    private static double INFINITE = 10000;

    @Override
    public void process(ICategoryProcessorContext context,
            DevicePropertyCategory category) {
        PortInfoBean portInfo = context.getPortInfo();
        if (portInfo != null) {
            getHoQLife(category, portInfo.getHoqLife());
        }

    }

    private void getHoQLife(DevicePropertyCategory category, byte[] hoqLife) {
        double[] series = new double[hoqLife.length];
        for (int i = 0; i < series.length; i++) {
            series[i] = Utils.getHoQLife(hoqLife[i]); // in ms
            if (series[i] == Double.POSITIVE_INFINITY) {
                series[i] = INFINITE;
            }
        }

        DevicePropertyItem property = new DevicePropertyItem(HOQLIFE, series);
        category.addPropertyItem(property);

        DevicePropertyItem numVLs = new DevicePropertyItem(NUM_VL,
                new Integer(SAConstants.STL_MAX_VLS));
        category.addPropertyItem(numVLs);
    }

    /**
     *
     * <i>Description:</i>
     *
     * @param hoqLife
     *            life in ms
     * @return a string represent joq life in unit us or ms or s
     */
    public static String getHoqLifeString(double hoqLife) {
        String hoqString = null;
        if (hoqLife >= INFINITE) {
            hoqString = K0133_INFINITE.getValue();
        } else if (hoqLife < 1) {
            hoqString = (int) (hoqLife * 1024) + " " + K0131_US.getValue();
        } else if (hoqLife >= 1024) {
            hoqString = (int) (hoqLife / 1024) + " " + K0132_S.getValue();
        } else {
            hoqString = (int) hoqLife + " " + K0130_MS.getValue();
        }
        return hoqString;
    }
}
