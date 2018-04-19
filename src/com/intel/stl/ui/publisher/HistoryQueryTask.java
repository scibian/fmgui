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

package com.intel.stl.ui.publisher;

import java.util.concurrent.Future;

import com.intel.stl.api.ITimestamped;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.ui.common.ICancelIndicator;
import com.intel.stl.ui.model.HistoryType;

public abstract class HistoryQueryTask<E extends ITimestamped>
        extends CancellableCall<Void> {
    private final IPerformanceApi perfApi;

    private int lastImageInterval;

    private final int refreshRate;

    private final int maxDataPoints;

    private final int lengthInSeconds;

    private final ICallback<E[]> callback;

    /**
     * Description:
     *
     * @param step
     */
    public HistoryQueryTask(IPerformanceApi perfApi, int imageInterval,
            int refreshRate, HistoryType type, ICallback<E[]> callback) {
        super();
        this.perfApi = perfApi;
        this.lastImageInterval = imageInterval;
        this.refreshRate = refreshRate;
        this.maxDataPoints = type.getMaxDataPoints(refreshRate);
        this.lengthInSeconds = type.getLengthInSeconds();
        this.callback = callback;
    }

    public void setFuture(final Future<Void> future) {
        setCancelIndicator(new ICancelIndicator() {
            @Override
            public boolean isCancelled() {
                return future.isCancelled();
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.publisher.CancellableCall#call(com.intel.stl.ui.common
     * .ICancelIndicator)
     */
    @Override
    public Void call(ICancelIndicator cancelIndicator) throws Exception {
        long firstTime = -1;

        ImageIdBean[] imageIdBeans = queryImageId();
        int imageIdBeanLength = imageIdBeans.length;
        long[] imageIds = new long[imageIdBeanLength];
        for (int j = 0; j < imageIdBeanLength; j++) {
            imageIds[j] = imageIdBeans[j].getImageNumber();
        }

        double step = getHistoryStep(lastImageInterval, refreshRate);
        long lastTimePoint = queryImageTime(imageIds[0], 0);
        int lastOffset = 0;
        double tmpOffset = 0;
        for (int i = 0; i < getMaxDataPoints()
                && !cancelIndicator.isCancelled(); i++) {
            tmpOffset -= step;
            int offset = (int) tmpOffset;
            E[] datapoints = queryHistory(imageIds, offset);
            if (datapoints != null && datapoints.length > 0) {
                E datapoint = datapoints[0];
                long time = datapoint.getTimestamp();
                if (firstTime == -1) {
                    firstTime = time;
                } else if (firstTime - time > getLengthInSeconds()) {
                    break;
                }
                int curInterval = datapoint.getImageInterval();
                if (curInterval != lastImageInterval) {
                    offset = adjustOffset(imageIds[0], lastTimePoint,
                            lastOffset, cancelIndicator);
                    datapoints = queryHistory(imageIds, offset);
                    if (datapoints == null || datapoints.length == 0) {
                        break;
                    }
                    tmpOffset = offset;
                    time = datapoints[0].getTimestamp();
                    lastImageInterval = datapoints[0].getImageInterval();
                    step = getHistoryStep(lastImageInterval, refreshRate);
                }
                callback.onDone(datapoints);
                lastTimePoint = time;
                lastOffset = offset;
            } else {
                break;
            }
            Thread.yield();
        }
        return null;
    }

    /**
     * for unit test purpose
     *
     * @return the maxDataPoints
     */
    protected int getMaxDataPoints() {
        return maxDataPoints;
    }

    /**
     * for unit test purpose
     *
     * @return the lengthInSeconds
     */
    protected int getLengthInSeconds() {
        return lengthInSeconds;
    }

    /**
     *
     * <i>Description:</i> given a reference <code>refTimePoint</code> and
     * <code>refOffset</code>, find the next history data point that is most
     * close to the desired refresh rate
     *
     * @param refTimePoint
     * @param refOffset
     * @param startOffset
     * @return
     */
    protected int adjustOffset(long imageNumber, long refTimePoint,
            int refOffset, ICancelIndicator cancelIndicator) {
        long desiredTime = refTimePoint - refreshRate;
        int offset = refOffset - 1;
        long time = 0;
        // find the first time point pasts the desired time
        while ((time = queryImageTime(imageNumber, offset)) > 0
                && time > desiredTime) {
            if (cancelIndicator.isCancelled()) {
                return offset;
            }
            offset -= 1;
        }
        if (time < 0) {
            return offset;
        }

        double delta1 = desiredTime - time;
        double delta2 = queryImageTime(imageNumber, offset + 1) - desiredTime;
        if (delta1 <= delta2 || offset == refOffset - 1) {
            return offset;
        } else {
            return offset + 1;
        }
    }

    protected long queryImageTime(long imageNumber, int offset) {
        ImageInfoBean bean = perfApi.getImageInfo(imageNumber, offset);
        if (bean != null) {
            return bean.getTimestamp();
        } else {
            return -1;
        }
    }

    protected double getHistoryStep(int imageInterval, int refreshRate) {
        double step = Math.max(1, (double) refreshRate / imageInterval);
        return step;
    }

    protected abstract ImageIdBean[] queryImageId();

    protected abstract E[] queryHistory(long[] imageIDs, int offset);
}
