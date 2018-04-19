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

import com.intel.stl.api.performance.PAConstants;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.ui.common.UILabels;

public class FocusPortsTask<E> extends Task<E> {
    final private Selection selection;

    final private int range;

    final private boolean isVF;

    public FocusPortsTask(String name, Selection selection, int range) {
        this(name, selection, range, false);
    }

    public FocusPortsTask(String name, Selection selection, int range,
            boolean isVF) {
        super(isVF ? PAConstants.STL_PA_ATTRID_GET_VF_FOCUS_PORTS
                : PAConstants.STL_PA_ATTRID_GET_FOCUS_PORTS, name,
                UILabels.STL40006_FOCUSPORTS_TASK.getDescription(name,
                        selection));
        this.selection = selection;
        this.range = range;
        this.isVF = isVF;
    }

    /**
     * @return the selection
     */
    public Selection getSelection() {
        return selection;
    }

    /**
     * @return the range
     */
    public int getRange() {
        return range;
    }

    /**
     * @return the isVF
     */
    public boolean isVF() {
        return isVF;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + (isVF ? 1231 : 1237);
        result = prime * result + range;
        result =
                prime * result
                        + ((selection == null) ? 0 : selection.hashCode());
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        FocusPortsTask other = (FocusPortsTask) obj;
        if (isVF != other.isVF) {
            return false;
        }
        if (range != other.range) {
            return false;
        }
        if (selection != other.selection) {
            return false;
        }
        return true;
    }

}
