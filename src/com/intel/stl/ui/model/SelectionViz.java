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

import java.util.EnumMap;

import com.intel.stl.api.subnet.Selection;
import com.intel.stl.ui.common.STLConstants;

public enum SelectionViz {
    UTILIZATION_HIGH(Selection.UTILIZATION_HIGH,
            STLConstants.K1630_SEL_UTILIZATION_HIGH.getValue()),
    PACKET_RATE_HIGH(Selection.PACKET_RATE_HIGH,
            STLConstants.K1631_SEL_PACKET_RATE_HIGH.getValue()),
    UTILIZATION_LOW(Selection.UTILIZATION_LOW,
            STLConstants.K1632_SEL_UTILIZATION_LOW.getValue()),
    INTEGRITY_ERRORS_HIGH(Selection.INTEGRITY_ERRORS_HIGH,
            STLConstants.K1633_SEL_INTEGRITY_ERRORS_HIGH.getValue()),
    CONGESTION_ERRORS_HIGH(Selection.CONGESTION_ERRORS_HIGH,
            STLConstants.K1634_SEL_CONGESTION_ERRORS_HIGH.getValue()),
    SMA_CONGESTION_ERRORS_HIGH(Selection.SMA_CONGESTION_ERRORS_HIGH,
            STLConstants.K1635_SEL_SMA_CONGESTION_ERRORS_HIGH.getValue()),
    BUBBLE_ERRORS_HIGH(Selection.BUBBLE_ERRORS_HIGH,
            STLConstants.K1636_SEL_BUBBLE_ERRORS_HIGH.getValue()),
    SECURITY_ERRORS_HIGH(Selection.SECURITY_ERRORS_HIGH,
            STLConstants.K1637_SEL_SECURITY_ERRORS_HIGH.getValue()),
    ROUTING_ERRORS_HIGH(Selection.ROUTING_ERRORS_HIGH,
            STLConstants.K1638_SEL_ROUTING_ERRORS_HIGH.getValue());

    private final static EnumMap<Selection, SelectionViz> SelectionMap =
            new EnumMap<Selection, SelectionViz>(Selection.class);
    static {
        for (SelectionViz lqz : SelectionViz.values()) {
            SelectionMap.put(lqz.selection, lqz);
        }
    };

    private final Selection selection;

    private final String description;

    private SelectionViz(Selection selection, String description) {
        this.selection = selection;
        this.description = description;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the selection
     */
    public Selection getSelection() {
        return selection;
    }

    public static String getSelectionStr(Selection selection) {
        SelectionViz viz = SelectionMap.get(selection);
        if (viz != null) {
            return viz.getDescription();
        } else {
            throw new IllegalArgumentException(
                    "Couldn't find SelectionViz for " + selection);
        }
    }

    public static String getSelectionStr(int value) {
        for (SelectionViz lqz : SelectionViz.values()) {
            if (lqz.getSelection().getSelect() == value) {
                return lqz.getDescription();
            }
        }
        return null;
    }

}
