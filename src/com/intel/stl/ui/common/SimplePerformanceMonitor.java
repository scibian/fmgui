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


package com.intel.stl.ui.common;

import java.io.PrintStream;

/**
 * A simple class used for inspect code performance
 */
public class SimplePerformanceMonitor {
    private String id;
    private long time;
    private PrintStream out = System.out;
    
    public SimplePerformanceMonitor() {
        this(SimplePerformanceMonitor.class.getSimpleName());
    }

    /**
     * Description: 
     *
     * @param id 
     */
    public SimplePerformanceMonitor(String id) {
        super();
        this.id = id;
    }

    /**
     * Specify output
     */
    public void setPrintStream(PrintStream out) {
        this.out = out;
    }
    
    /**
     * mark current time as reference point
     */
    public void mark() {
        time = System.currentTimeMillis();
    }
    
    /**
     * print out delta time since last call to this method or #mark
     * 
     * @param text prefix text for description purpose
     */
    public void print(String text) {
        print(text, true);
    }
    
    /**
     * print out delta time either since last call to {@link #print(String)} or
     * {@link #mark()}
     * 
     * @param text
     *            prefix text for description purpose
     * @param relative
     *            delta time will be based on last call to print if
     *            <code>relative</code> is true. Otherwise the delta will be
     *            absolute delta between current time and last mark time.
     */
    public void print(String text, boolean relative) {
        long delta = 0;
        if (relative) {
            delta = -time + (time=System.currentTimeMillis());
        } else {
            delta = -time + System.currentTimeMillis();
        }
        out.println("["+id+"] "+text+" "+delta+" ms");
    }
}
 
