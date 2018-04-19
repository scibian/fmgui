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

import java.awt.Color;
import java.util.Date;
import java.util.EnumMap;

import net.engio.mbassy.bus.MBassador;

import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Second;
import org.jfree.data.time.TimeTableXYDataset;

import com.intel.stl.api.notice.NoticeSeverity;
import com.intel.stl.ui.common.BaseCardController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.HelpAction;
import com.intel.stl.ui.model.NoticeSeverityViz;
import com.intel.stl.ui.monitor.view.PSEventsCardView;

/**
 * Events card for the Performance Summary subpage
 */
public class PSEventsCard extends
        BaseCardController<ICardListener, PSEventsCardView> {
    private final TimeTableXYDataset trendDataset;

    private final DefaultPieDataset stateDataset;

    // TODO: make this user configurable
    private final int maxHistoryLength = 6 * 3600000; // 6 hours in ms

    public PSEventsCard(PSEventsCardView view, MBassador<IAppEvent> eventBus) {
        super(view, eventBus);

        stateDataset = new DefaultPieDataset();
        NoticeSeverityViz[] states = NoticeSeverityViz.values();
        Color[] colors = new Color[states.length + 1];
        for (int i = 0; i < states.length; i++) {
            stateDataset.setValue(states[i].getName(), 0);
            colors[i] = states[i].getColor();
        }
        stateDataset.setValue(STLConstants.K0056_NONE.getValue(), 0);
        colors[states.length] = UIConstants.INTEL_GRAY;
        view.setStateDataset(stateDataset, colors);

        trendDataset = new TimeTableXYDataset();
        view.setTrendDataset(trendDataset, NoticeSeverityViz.colors);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.ICardController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getEvents();
    }

    /**
     * @param swStates
     * @param totalSWs
     */
    public void updateStates(final EnumMap<NoticeSeverity, Integer> states,
            final int total, final Date date) {

        final NoticeSeverityViz[] items = NoticeSeverityViz.values();
        final int[] counts = new int[items.length + 1];
        final double[] values = new double[counts.length];
        final String[] labels = new String[counts.length];
        final String[] tooltips = new String[counts.length];

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                int sum = 0;
                trendDataset.setNotify(false);

                if (trendDataset.getItemCount() > 0) {
                    long startTime =
                            trendDataset.getTimePeriod(0).getStart().getTime();
                    long endTime =
                            trendDataset
                                    .getTimePeriod(
                                            trendDataset.getItemCount() - 1)
                                    .getEnd().getTime();
                    while (endTime - startTime > maxHistoryLength) {
                        for (int i = 0; i < counts.length - 1; i++) {
                            trendDataset.remove(trendDataset.getTimePeriod(0),
                                    items[i], false);
                        }
                        startTime =
                                trendDataset.getTimePeriod(0).getStart()
                                        .getTime();
                    }
                }

                for (int i = 0; i < counts.length; i++) {
                    if (i < counts.length - 1) {
                        Integer count = states.get(items[i].getSeverity());
                        counts[i] = count == null ? 0 : count;
                        sum += counts[i];
                        trendDataset.add(new Second(date), counts[i], items[i],
                                false);
                        stateDataset.setValue(items[i].getName(), counts[i]);
                    } else {
                        counts[i] = total - sum;
                        stateDataset.setValue(
                                STLConstants.K0056_NONE.getValue(), counts[i]);
                    }
                    values[i] = (double) counts[i] / total;
                    labels[i] =
                            UIConstants.INTEGER.format(counts[i]) + " ("
                                    + UIConstants.PERCENTAGE.format(values[i])
                                    + ") ";
                    if (i < counts.length - 1) {
                        tooltips[i] =
                                UILabels.STL10203_NODE_EVENTS.getDescription(
                                        UIConstants.INTEGER.format(counts[i]),
                                        UIConstants.PERCENTAGE
                                                .format(values[i]),
                                        NoticeSeverityViz.names[i]);
                    } else {
                        tooltips[i] =
                                UILabels.STL10204_NODE_NO_EVENTS
                                        .getDescription(UIConstants.INTEGER
                                                .format(counts[i]),
                                                UIConstants.PERCENTAGE
                                                        .format(values[i]));
                    }
                }
                trendDataset.setNotify(true);
                view.setStates(values, labels, tooltips);
            }
        });

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#getCardListener()
     */
    @Override
    public ICardListener getCardListener() {
        return this;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.BaseCardController#clear()
     */
    @Override
    public void clear() {
        super.clear();
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                stateDataset.clear();
                trendDataset.clear();
            }
        });
        view.clear();
    }

}
