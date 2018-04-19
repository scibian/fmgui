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


package com.intel.stl.ui.model;

import org.jfree.chart.axis.NumberTickUnit;
import org.jfree.chart.axis.TickUnits;

public class ErrorChartTickUnit extends NumberTickUnit {
    private static final long serialVersionUID = 2654598599166333554L;
    
    // Buckets:
    public static final int LOWER_BOUND = 0;
    public static final int B0_25       = LOWER_BOUND;
    public static final int B26_50      = 1;
    public static final int B51_75      = 2;
    public static final int B76_100     = 3;
    public static final int B100Plus    = 4;
    public static final int UPPER_BOUND = 5;

    public ErrorChartTickUnit()
    {
        super((double)0);
    }

    public ErrorChartTickUnit(double d)
    {
        super((double)d);
    }

    public TickUnits genTickUnits()
    {
        TickUnits tickUnits = new TickUnits();

        tickUnits.add(new ErrorChartTickUnit(B0_25));
        tickUnits.add(new ErrorChartTickUnit(B26_50));
        tickUnits.add(new ErrorChartTickUnit(B51_75));
        tickUnits.add(new ErrorChartTickUnit(B76_100));
        tickUnits.add(new ErrorChartTickUnit(B100Plus));
        tickUnits.add(new ErrorChartTickUnit(UPPER_BOUND));

        return tickUnits;
    }

    public String valueToString(double value)
    {
        if     (value == (double)B0_25)    return "0+%";
        else if(value == (double)B26_50)   return "25+%";
        else if(value == (double)B51_75)   return "50+%";
        else if(value == (double)B76_100)  return "75+%";
        else if(value == (double)B100Plus) return "100+%";
        else                               return "";
    }

}
