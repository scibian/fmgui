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

package com.intel.stl.ui.network;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;

import com.intel.stl.ui.common.UIConstants;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxVertexHandler;
import com.mxgraph.view.mxCellState;

public class VertexHandler extends mxVertexHandler {

    /**
     * Description:
     * 
     * @param graphComponent
     * @param state
     */
    public VertexHandler(mxGraphComponent graphComponent, mxCellState state) {
        super(graphComponent, state);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mxgraph.swing.handler.mxVertexHandler#paint(java.awt.Graphics)
     */
    @Override
    public void paint(Graphics g) {
        Stroke selStroke = getSelectionStroke();

        Rectangle bounds = getState().getRectangle();
        if (bounds.height == 0) {
            bounds.height = 1;
        }
        if (bounds.width == 0) {
            bounds.width = 1;
        }
        if (bounds.width < 5 || bounds.height < 5) {
            selStroke = UIConstants.VERTEX_SEL_STROKE2;
        }

        if (g.hitClip(bounds.x, bounds.y, bounds.width, bounds.height)) {
            Graphics2D g2 = (Graphics2D) g;

            Stroke stroke = g2.getStroke();
            try {
                g2.setStroke(selStroke);
                g2.setColor(getSelectionColor());
                g2.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);
            } finally {
                g2.setStroke(stroke);
            }
        }

        super.paint(g);
    }

}
