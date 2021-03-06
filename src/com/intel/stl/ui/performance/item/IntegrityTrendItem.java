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

import com.intel.stl.api.performance.CategoryStatBean;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.performance.GroupSource;
import com.intel.stl.ui.performance.observer.ErrorDataObserver;
import com.intel.stl.ui.performance.observer.VFErrorDataObserver;
import com.intel.stl.ui.performance.provider.CombinedGroupInfoProvider;
import com.intel.stl.ui.performance.provider.CombinedVFInfoProvider;
import com.intel.stl.ui.performance.provider.DataProviderName;

/**
 * Integrity Errors Trend
 */
public class IntegrityTrendItem extends TrendItem<GroupSource> {

    /**
     * Description:
     * 
     * @param name
     */
    public IntegrityTrendItem() {
        this(DEFAULT_DATA_POINTS);
    }

    /**
     * Description:
     * 
     * @param name
     * @param maxDataPoints
     */
    public IntegrityTrendItem(int maxDataPoints) {
        super(STLConstants.K0855_SHORT_INTEG_TREND.getValue(),
                STLConstants.K0873_INTEGRITY_TREND.getValue(), maxDataPoints);
    }

    public IntegrityTrendItem(IntegrityTrendItem item) {
        super(item);
    }

    @Override
    protected void initDataProvider() {
        CombinedGroupInfoProvider provider = new CombinedGroupInfoProvider();
        ErrorDataObserver observer = new ErrorDataObserver(this) {
            @Override
            protected long getValue(CategoryStatBean error) {
                return error.getCategoryMaximums().getIntegrityErrors();
            }
        };
        registerDataProvider(DataProviderName.PORT_GROUP, provider, observer);

        CombinedVFInfoProvider vfProvider = new CombinedVFInfoProvider();
        VFErrorDataObserver vfObserver = new VFErrorDataObserver(this) {
            @Override
            protected long getValue(CategoryStatBean error) {
                return error.getCategoryMaximums().getIntegrityErrors();
            }
        };
        registerDataProvider(DataProviderName.VIRTUAL_FABRIC, vfProvider,
                vfObserver);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.performance.item.IPerformanceItem#copy()
     */
    @Override
    public IPerformanceItem<GroupSource> copy() {
        return new IntegrityTrendItem(this);
    }
}
