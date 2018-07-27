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

package com.intel.stl.ui.admin.view.virtualfabrics;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import com.intel.stl.api.management.virtualfabrics.PktLifeTimeMult;
import com.intel.stl.ui.admin.view.FieldRenderer;
import com.intel.stl.ui.common.view.ExFormattedTextField;
import com.intel.stl.ui.common.view.SafeNumberField;

public class PktLifeTimeMultRenderer extends
        FieldRenderer<Short, PktLifeTimeMult> {
    /**
     * Description:
     * 
     * @param format
     * @param defaultValue
     */
    public PktLifeTimeMultRenderer() {
        super(new DecimalFormat("###"), (short) 1);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.FieldRenderer#createFiled(java.text.NumberFormat
     * )
     */
    @Override
    protected ExFormattedTextField createFiled(NumberFormat format) {
        SafeNumberField<Short> res =
                new SafeNumberField<Short>(format, (short) 1, true,
                        Short.MAX_VALUE, true);
        // only positive short
        res.setValidChars("0123456789");
        return res;
    }

    @Override
    protected PktLifeTimeMult createAttr(Short value) {
        return new PktLifeTimeMult(value);
    }
}