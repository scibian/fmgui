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

package com.intel.stl.ui.performance.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.swing.SwingUtilities;

import org.jfree.data.general.Dataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.time.TimePeriod;
import org.jfree.data.time.TimePeriodValue;
import org.jfree.data.time.TimePeriodValuesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.TrendSeries;
import com.intel.stl.ui.monitor.ChartScaleGroupManager;
import com.intel.stl.ui.performance.ISource;

/**
 * This controller collects data from a data provider, then applies a data
 * observer to process the data, and then update its internal dataset that is
 * used for a JFreeChart. <br>
 *
 * To use this class to create trend data for a JFreeChart, we should follow the
 * following steps:
 * <ol>
 * <li>Construct the class with <code>name</code> and <code>maxDataPoints</code>
 * <li>Provide data provider and a data observer that define how we collect and
 * process data
 * <li>Set sources to specify the target data sources, such as Device Groups or
 * VFabrics, from which where we will collect data
 * </ol>
 */
public abstract class TrendItem<S extends ISource>
        extends AbstractPerformanceItem<S> {
    private final static Logger log = LoggerFactory.getLogger(TrendItem.class);

    private final static boolean DEBUG = false;

    private ChartScaleGroupManager<TimePeriodValuesCollection> scaleManager;

    protected TimePeriodValuesCollection dataset;

    protected List<TrendSeries> trendSeries;

    private final Object copyCritical = new Object();

    public TrendItem(String shortName, String fullName) {
        this(shortName, fullName, DEFAULT_DATA_POINTS);
    }

    /**
     * Description:
     *
     * @param sourceName
     *            Name of this TrendItem, it will be the name displayed on UI
     * @param maxDataPoints
     *            the history length of this trend. It's defined by number of
     *            data points on a Chart
     */
    public TrendItem(String shortName, String fullName, int maxDataPoints) {
        this(STLConstants.K0078_TREND.getValue(), shortName, fullName,
                maxDataPoints);
    }

    public TrendItem(String name, String shortName, String fullName,
            int maxDataPoints) {
        super(name, shortName, fullName, maxDataPoints);
        initDataProvider();
        initDataset();
    }

    public TrendItem(TrendItem<S> item) {
        super(item);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#copyDataset
     * (com.intel.stl.ui.performance.item.AbstractPerformanceItem)
     */
    @Override
    protected void copyDataset(AbstractPerformanceItem<S> item) {
        TrendItem<S> trendItem = (TrendItem<S>) item;
        dataset = createTrendDataset();
        trendSeries = new ArrayList<TrendSeries>();
        // need to sync with the data update in the item to be copied
        synchronized (trendItem.copyCritical) {
            for (TrendSeries ts : trendItem.trendSeries) {
                TrendSeries copy = new TrendSeries((String) ts.getKey());
                for (int i = 0; i < ts.getItemCount(); i++) {
                    TimePeriodValue val = ts.getDataItem(i);
                    TimePeriodValue valClone = (TimePeriodValue) val.clone();
                    copy.add(valClone);
                }
                trendSeries.add(copy);
            }
        }
        for (int i = trendSeries.size() - 1; i >= 0; i--) {
            dataset.addSeries(trendSeries.get(i));
        }
    }

    /**
     * Description:
     *
     */
    protected void initDataset() {
        dataset = createTrendDataset();
        if (dataset != null) {
            trendSeries = createTrendSeries(sourceNames);
            for (int i = trendSeries.size() - 1; i >= 0; i--) {
                dataset.addSeries(trendSeries.get(i));
            }
        }
    }

    protected TimePeriodValuesCollection createTrendDataset() {
        return new TimePeriodValuesCollection();
    }

    protected List<TrendSeries> createTrendSeries(S[] series) {
        List<TrendSeries> res = new ArrayList<TrendSeries>();
        if (series != null) {
            for (S serie : series) {
                TrendSeries ts = new TrendSeries(serie.sourceName());
                res.add(ts);
            }
        }
        return res;
    }

    protected TrendSeries getTimeSeries(S name) {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            if (name.sourceName().equals(dataset.getSeries(i).getKey())) {
                return (TrendSeries) dataset.getSeries(i);
            }
        }
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.performance.IPerformanceItem#getDataset()
     */
    @Override
    public Dataset getDataset() {
        return dataset;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#isJumpable()
     */
    @Override
    protected boolean isJumpable() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#sourcesRemoved
     * (java.lang.String[])
     */
    @Override
    public void sourcesRemoved(S[] names) {
        if (DEBUG) {
            System.out.println(
                    currentProviderName + ":" + getName() + " " + getFullName()
                            + ": sourcesRemoved " + Arrays.toString(names));
        }
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                int seriesCount = dataset.getSeriesCount();
                for (int i = 0; i < seriesCount; i++) {
                    dataset.removeSeries(0);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.performance.item.AbstractPerformanceItem#sourcesToAdd
     * (java.lang.String[])
     */
    @Override
    public void sourcesToAdd(final S[] names) {
        if (DEBUG) {
            System.out.println(
                    currentProviderName + ":" + getName() + " " + getFullName()
                            + ": sourcesToAdd " + Arrays.toString(names));
        }
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                if (names != null && names.length > 0) {
                    synchronized (copyCritical) {
                        trendSeries = createTrendSeries(names);
                        for (int i = trendSeries.size() - 1; i >= 0; i--) {
                            dataset.addSeries(trendSeries.get(i));
                        }
                    }
                }
            }
        });
    }

    /**
     * @param scaleManager
     *            the scaleManager to set
     */
    public void setScaleManager(
            ChartScaleGroupManager<TimePeriodValuesCollection> scaleManager) {
        this.scaleManager = scaleManager;
    }

    /**
     * @return the scaleManager
     */
    public ChartScaleGroupManager<TimePeriodValuesCollection> getScaleManager() {
        return scaleManager;
    }

    public void updateTrend(final double value, final Date date,
            final int interval, final S name) {
        if (dataset == null) {
            return;
        }

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                // need to sync with the dataset copy
                synchronized (copyCritical) {
                    TrendSeries series = getTimeSeries(name);
                    long time = date.getTime();
                    time += (interval * 1000);
                    if (series != null) {
                        removeTimePeriod(series, date.getTime(), time);
                        series.add(new SimpleTimePeriod(date, new Date(time)),
                                value);

                        if (series.getItemCount() > maxDataPoints) {
                            // Delete the oldest item from the current series
                            series.delete(0, 0);
                            // Also delete the item from other series whose
                            // Time stamp is equal or older the current series
                            // that's been deleted.
                            deleteOldSeries(series.getDataItem(0));
                        }

                        series.fireSeriesChanged();
                    } else {
                        log.warn(currentProviderName + ":" + getName() + " "
                                + getFullName() + ": Couldn't find TimeSeries '"
                                + name + "'");
                        // throw new
                        // RuntimeException("Couldn't find TimeSeries '"
                        // + name + "'");
                    }
                }
            }
        });
        if (scaleManager != null) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    scaleManager.updateChartsRange();
                }
            });
        }
    }

    protected void removeTimePeriod(TrendSeries values, long startTime,
            long endTime) {
        int size = values.getItemCount();
        if (size == 0) {
            return;
        }

        for (int i = size - 1; i >= 0; i--) {
            TimePeriod tp = values.getTimePeriod(i);
            if (tp.getStart().getTime() >= startTime
                    && tp.getEnd().getTime() <= endTime) {
                values.delete(i, i);
            }
        }
    }

    /**
     *
     * <i>Description:</i>
     *
     * @param tpv
     */
    private void deleteOldSeries(TimePeriodValue tpv) {
        for (int i = 0; i < dataset.getSeriesCount(); i++) {
            Date start = tpv.getPeriod().getStart();
            TrendSeries ts = (TrendSeries) dataset.getSeries(i);
            while (ts.getItemCount() > 0) {
                TimePeriod tp = ts.getTimePeriod(0);
                if (null != tp && (tp.getStart().equals(start)
                        || tp.getStart().before(start))) {
                    ts.delete(0, 0);
                } else {
                    break;
                }
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.performance.IPerformanceItem#clear()
     */
    @Override
    public void clear() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                synchronized (copyCritical) {
                    if (dataset != null) {
                        for (int i = 0; i < dataset.getSeriesCount(); i++) {
                            int itemCount = dataset.getSeries(i).getItemCount();
                            dataset.getSeries(i).delete(0, itemCount - 1);
                        }
                    }
                }
            }
        });
    }

}
