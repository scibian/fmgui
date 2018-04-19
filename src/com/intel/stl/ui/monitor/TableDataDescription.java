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

import java.io.Serializable;
import java.text.DecimalFormat;

public class TableDataDescription implements Serializable {

    private static final long serialVersionUID = -929415085901813310L;

    private double data;

    private String description;

    private final long unit = 1000000;

    public TableDataDescription(double data, String description) {
        this.data = data;
        this.description = description;
    }

    /**
     * @return the data
     */
    public double getData() {
        return data;
    }

    /**
     * @param data
     *            the data to set
     */
    public void setData(double data) {
        this.data = data;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * 
     * <i>Description:</i>This formatting is related to the unit, so, unit
     * conversion (MB) is also done in this class.
     * 
     * @return
     */
    public String getFormattedData() {
        DecimalFormat df = new DecimalFormat("0");
        df.setMaximumFractionDigits(6);
        double dataConverted = data / unit;

        String dataStr = null;
        if (dataConverted < (1d / unit) && dataConverted != 0) {
            dataStr = "<0.000001";
        } else {
            dataStr = df.format(dataConverted);
        }

        return dataStr;
    }

}
