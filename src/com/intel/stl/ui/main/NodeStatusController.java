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

package com.intel.stl.ui.main;

import java.util.EnumMap;

import org.jfree.data.general.DefaultPieDataset;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.main.view.NodeStatusPanel;
import com.intel.stl.ui.model.ChartStyle;
import com.intel.stl.ui.model.StateLongTypeViz;

public class NodeStatusController {
    private final NodeStatusPanel view;

    private int lastTotal;

    private final DefaultPieDataset dataset;

    private EnumMap<NoticeSeverity, Integer> lastStates;

    /**
     * Description:
     * 
     * @param view
     */
    public NodeStatusController(NodeStatusPanel view) {
        super();
        this.view = view;
        dataset = new DefaultPieDataset();
        StateLongTypeViz[] states = StateLongTypeViz.values();
        for (int i = 0; i < states.length; i++) {
            dataset.setValue(states[i].getName(), 0);
        }
        view.setDataset(dataset, StateLongTypeViz.colors);
    }

    /**
     * @return the lastTotal
     */
    public int getLastTotal() {
        return lastTotal;
    }

    /**
     * @return the lastStates
     */
    public EnumMap<NoticeSeverity, Integer> getLastStates() {
        return lastStates;
    }

    public void updateStates(EnumMap<NoticeSeverity, Integer> states,
            final int total) {
        lastStates = states;
        lastTotal = total;

        if (lastStates == null) {
            view.clear();
            return;
        }

        final int[] counts =
                StateLongTypeViz.getDistributionValues(states, total);
        int countsLen = 0;
        if (counts != null) {
            countsLen = counts.length;
        }
        final double[] values = new double[countsLen];
        final String[] labels = new String[countsLen];
        final String[] tooltips = new String[countsLen];
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < counts.length; i++) {
                    dataset.setValue(StateLongTypeViz.values()[i].getName(),
                            counts[i]);
                    if (total > 0) {
                        values[i] = (double) counts[i] / total;
                        labels[i] =
                                UIConstants.INTEGER.format(counts[i])
                                        + " ("
                                        + UIConstants.PERCENTAGE
                                                .format(values[i]) + ") ";
                        tooltips[i] =
                                UILabels.STL10202_NODE_STATES.getDescription(
                                        UIConstants.INTEGER.format(counts[i]),
                                        UIConstants.PERCENTAGE
                                                .format(values[i]),
                                        StateLongTypeViz.names[i]);
                    } else {
                        labels[i] = STLConstants.K0039_NOT_AVAILABLE.getValue();
                        tooltips[i] = null;
                    }
                }

                view.setStates(values, labels, tooltips);
            }
        });
    }

    public void setStyle(ChartStyle style) {
        view.setStyle(style);
        updateStates(lastStates, lastTotal);
    }

    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                dataset.clear();
                view.clear();
            }
        });
    }
}
