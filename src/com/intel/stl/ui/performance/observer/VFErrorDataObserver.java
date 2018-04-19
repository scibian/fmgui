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

package com.intel.stl.ui.performance.observer;

import java.util.Date;

import com.intel.stl.api.performance.ErrStatBean;
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.item.TrendItem;

public abstract class VFErrorDataObserver
        extends AbstractDataObserver<VFInfoBean[], TrendItem<GroupSource>> {

    public VFErrorDataObserver(TrendItem<GroupSource> controller) {
        this(controller, DataType.ALL);
    }

    /**
     * Description:
     *
     * @param controller
     * @param type
     */
    public VFErrorDataObserver(TrendItem<GroupSource> controller,
            DataType type) {
        super(controller, type);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.performance.IDataObserver#processData(java.lang
     * .Object)
     */
    @Override
    public void processData(VFInfoBean[] data) {
        if (data == null || data.length == 0) {
            return;
        }

        for (VFInfoBean bean : data) {
            if (bean == null) {
                continue;
            }

            Date time = bean.getTimestampDate();
            int interval = bean.getImageInterval();
            ErrStatBean[] errors = getErrStatBeans(bean, type);
            long value = 0;
            for (ErrStatBean error : errors) {
                value += getValue(error);
            }
            GroupSource sourceName = new GroupSource(bean.getVfName());
            controller.updateTrend(value, time, interval, sourceName);
        }
    }

    protected abstract long getValue(ErrStatBean error);

}
