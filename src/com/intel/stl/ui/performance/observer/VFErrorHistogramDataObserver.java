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

import com.intel.stl.api.performance.CategoryBucketBean;
import com.intel.stl.api.performance.CategoryStatBean;
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.performance.item.ErrHistogramItem;

public abstract class VFErrorHistogramDataObserver extends
        AbstractDataObserver<VFInfoBean[], ErrHistogramItem> {

    public VFErrorHistogramDataObserver(ErrHistogramItem controller) {
        this(controller, DataType.ALL);
    }

    /**
     * Description:
     * 
     * @param controller
     * @param type
     */
    public VFErrorHistogramDataObserver(ErrHistogramItem controller,
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
        if (data != null && data.length > 0) {
            processData(data[0]);
        }
    }

    public void processData(VFInfoBean data) {
        if (data == null) {
            return;
        }

        CategoryStatBean[] errors = getCategoryStatBeans(data, type);
        int[] counts = null;
        for (CategoryStatBean error : errors) {
            CategoryBucketBean[] buckets = error.getPorts();
            if (counts == null) {
                counts = new int[buckets.length];
            }
            for (int i = 0; i < buckets.length; i++) {
                counts[i] += getValue(buckets[i]);
            }
        }
        controller.updateHistogram(counts);
    }

    protected abstract long getValue(CategoryBucketBean err);
}
