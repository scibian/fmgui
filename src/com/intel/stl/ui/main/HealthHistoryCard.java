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

import java.awt.Font;
import java.text.DateFormat;
import java.util.Date;
import java.util.Properties;

import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriodAnchor;
import org.jfree.data.time.TimePeriodValues;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.PinDescription.PinID;
import com.intel.stl.ui.common.PinnableCardController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ICardListener;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.HealthHistoryView;
import com.intel.stl.ui.model.TimedScore;

import net.engio.mbassy.bus.MBassador;

/**
 */
public class HealthHistoryCard
        extends PinnableCardController<ICardListener, HealthHistoryView> {
    private final static Logger log =
            LoggerFactory.getLogger(HealthHistoryCard.class);

    private final TimePeriodValues dataset;

    private final TimePeriodValuesCollection timeseriescollection;

    private Date lastTime = null;

    // TODO: make this user configurable
    private final int maxHistoryLength = 6 * 3600000; // 6 hours in ms

    private final double INIT_SCORE = 100.0;

    private double lastScore = INIT_SCORE;

    public HealthHistoryCard(HealthHistoryView view,
            MBassador<IAppEvent> eventBus) {
        super(view, eventBus);
        dataset = new TimePeriodValues(
                STLConstants.K0105_HEALTH_HISTORY.getValue());
        timeseriescollection = new TimePeriodValuesCollection(dataset);
        timeseriescollection.setXPosition(TimePeriodAnchor.START);
        view.setDataset(timeseriescollection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.ICardController#getHelpID()
     */
    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getHealthTrend();
    }

    /**
     * @return the dataset
     */
    public TimePeriodValues getDataset() {
        return dataset;
    }

    @Override
    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                lastTime = null;
                if (dataset.getItemCount() > 0) {
                    dataset.delete(0, dataset.getItemCount() - 1);
                }
                view.setCurrentScore(
                        STLConstants.K0039_NOT_AVAILABLE.getValue(),
                        UIConstants.INTEL_DARK_GRAY, "");
                if (pinView != null) {
                    pinView.setCurrentScore(
                            STLConstants.K0039_NOT_AVAILABLE.getValue(),
                            UIConstants.INTEL_DARK_GRAY, "");
                }
            }
        });
    }

    /**
     * @param score
     * @param time
     *
     *            Health score can be refreshed periodically by a timer or
     *            updated asynchronously by an external event.
     */
    public void updateHealthScore(final double score, final Date time,
            final String tip) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                lastScore = score;

                TimedScore tScore = new TimedScore(time.getTime(), score, tip);
                view.setCurrentScore(tScore.getScoreString(), tScore.getColor(),
                        tip);

                if (lastTime == null) {
                    Date fakedLastTime = new Date(time.getTime() - 1000);
                    dataset.add(new SimpleTimePeriod(fakedLastTime, time),
                            Double.valueOf(score));
                    lastTime = time;
                }

                if (lastTime.after(time)) {
                    log.warn("time mismatch happend lastTime=" + lastTime
                            + " current time=" + time);
                    dataset.add(new SimpleTimePeriod(lastTime, lastTime),
                            Double.valueOf(score));
                } else {
                    dataset.add(new SimpleTimePeriod(lastTime, time),
                            Double.valueOf(score));
                    lastTime = time;
                }

                long startTime = dataset.getTimePeriod(0).getStart().getTime();
                long endTime = dataset.getTimePeriod(dataset.getItemCount() - 1)
                        .getEnd().getTime();
                while (endTime - startTime > maxHistoryLength) {
                    dataset.delete(0, 0);
                    startTime = dataset.getTimePeriod(0).getStart().getTime();
                }
                String start =
                        DateFormat.getInstance().format(new Date(startTime));
                String end = DateFormat.getInstance().format(new Date(endTime));
                view.setTimeDuration(start, end);

                if (pinView != null) {
                    pinView.setTimeDuration(start, end);
                    pinView.setCurrentScore(tScore.getScoreString(),
                            tScore.getColor(), tScore.getTip());
                }
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

    @Override
    public String toString() {
        return "HealthHistoryCard ";
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.PinnableCardController#generateArgument(java.
     * util.Properties)
     */
    @Override
    protected void generateArgument(Properties arg) {
        // no argument
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.PinnableCardController#createPinView()
     */
    @Override
    protected HealthHistoryView createPinView() {
        HealthHistoryView pinView = new HealthHistoryView(
                UIConstants.H2_FONT.deriveFont(Font.BOLD));
        pinView.setCardListener(getCardListener());
        return pinView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.PinnableCardController#initPinView()
     */
    @Override
    protected void initPinView() {
        TimedScore tScore =
                new TimedScore(System.currentTimeMillis(), lastScore);
        pinView.setCurrentScore(tScore.getScoreString(), tScore.getColor(), "");
        pinView.setDataset(timeseriescollection);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.PinnableCardController#getPinID()
     */
    @Override
    public PinID getPinID() {
        return PinID.HEALTH;
    }

}
