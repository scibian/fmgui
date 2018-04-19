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

package com.intel.stl.ui.common;

import java.awt.Color;
import java.awt.Insets;

import javax.swing.ImageIcon;

import com.intel.stl.ui.common.view.DistributionBarPanel;

/**
 * The controller for JDistributionBar
 */
public class DistributionBarController {
    private final String[] items;

    private long[] values;

    private long[] cumulativeSum;

    private final Color[] colors;

    private final DistributionBarPanel distributionBar;

    public DistributionBarController(DistributionBarPanel view, String[] items,
            long[] values, Color[] colors) {
        super();
        this.items = items;
        this.colors = colors;

        distributionBar = view;
        updateValues(values);
        check(items, colors);
        initBar();
    }

    /**
     * @return the distributionBar
     */
    public DistributionBarPanel getDistributionBar() {
        return distributionBar;
    }

    protected void check(String[] names, Color[] colors) {
        if (names == null) {
            throw new IllegalArgumentException("No names for segments");
        }

        if (colors != null && colors.length != names.length) {
            throw new IllegalArgumentException("Invalid argument. Expected "
                    + colors.length + " colors, got " + values.length
                    + " colors.");
        }
    }

    protected void check(long[] values, String[] names) {
        if (names == null) {
            throw new IllegalArgumentException("No names for segments");
        }

        if (values != null && values.length != names.length) {
            throw new IllegalArgumentException("Invalid argument. Expected "
                    + names.length + " values, got " + values.length
                    + " values.");
        }
    }

    protected void initBar() {
        ImageIcon[] icons = new ImageIcon[colors.length];
        String[] itemLabels = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            icons[i] =
                    Util.generateImageIcon(colors[i], 10,
                            new Insets(1, 1, 1, 1));
            itemLabels[i] = getItemLegend(i);
        }
        distributionBar.init(itemLabels, icons);
    }

    public void setDistribution(long[] values) {
        updateValues(values);
        final String[] itemLabels = new String[colors.length];
        for (int i = 0; i < colors.length; i++) {
            itemLabels[i] = getItemLegend(i);
        }
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                distributionBar.update(itemLabels, cumulativeSum, colors);
            }
        });
    }

    protected String getItemLegend(int i) {
        if (cumulativeSum == null) {
            return STLConstants.K0039_NOT_AVAILABLE.getValue();
        }

        double perentage =
                (double) values[i] / cumulativeSum[cumulativeSum.length - 1];
        return items[i] + " " + values[i] + " ("
                + UIConstants.PERCENTAGE.format(perentage) + ")";
    }

    protected void updateValues(long[] values) {
        check(values, items);
        this.values = values;

        if (values == null) {
            cumulativeSum = null;
        } else {
            int sum = 0;
            cumulativeSum = new long[values.length];
            for (int i = 0; i < values.length; i++) {
                sum += values[i];
                cumulativeSum[i] = sum;
            }
        }
    }
}
