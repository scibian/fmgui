/**
 * Copyright (c) 2016, Intel Corporation
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

package com.intel.stl.ui.common.view;

import java.awt.Paint;
import java.util.HashMap;
import java.util.Map;

import org.jfree.chart.axis.CategoryAxis;

import com.intel.stl.ui.model.FocusStatusViz;
import com.intel.stl.ui.model.PortEntry;

public class PortEntryAxis extends CategoryAxis {
    private static final long serialVersionUID = -2331957498724516521L;

    private transient Map<Comparable<?>, Paint> tickLabelPaintMap =
            new HashMap<Comparable<?>, Paint>();

    public PortEntryAxis() {
        super();
    }

    public PortEntryAxis(String label) {
        super(label);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jfree.chart.axis.CategoryAxis#configure()
     */
    @Override
    public void configure() {
        super.configure();
        fireChangeEvent();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jfree.chart.axis.CategoryAxis#getTickLabelPaint(java.lang.Comparable)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public Paint getTickLabelPaint(Comparable category) {
        if (category != null) {
            Paint paint = tickLabelPaintMap.get(category);
            if (paint != null) {
                return paint;
            } else if (category instanceof PortEntry) {
                PortEntry pe = (PortEntry) category;
                FocusStatusViz ffv = (FocusStatusViz) pe.getObject();
                return ffv.getColor();
            }
        }
        return super.getTickLabelPaint(category);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * org.jfree.chart.axis.CategoryAxis#setTickLabelPaint(java.lang.Comparable,
     * java.awt.Paint)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public void setTickLabelPaint(Comparable category, Paint paint) {
        if (category != null) {
            if (paint == null) {
                tickLabelPaintMap.remove(category);
            } else {
                tickLabelPaintMap.put(category, paint);
            }
            fireChangeEvent();
        }
    }

}
