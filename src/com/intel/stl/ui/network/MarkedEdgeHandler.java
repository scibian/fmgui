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

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.geom.Line2D;

import com.intel.stl.ui.common.UIConstants;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.swing.handler.mxCellHandler;
import com.mxgraph.view.mxCellState;

public class MarkedEdgeHandler extends mxCellHandler {

    /**
     * Description:
     * 
     * @param graphComponent
     * @param state
     */
    public MarkedEdgeHandler(mxGraphComponent graphComponent, mxCellState state) {
        super(graphComponent, state);
        handlesVisible = false;
    }

    @Override
    public Color getSelectionColor() {
        return UIConstants.EDGE_MARK_COLOR;
    }

    /**
     * 
     */
    @Override
    public Stroke getSelectionStroke() {
        return UIConstants.EDGE_MARK_STROKE;
    }

    @Override
    public void paint(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;

        RenderingHints tmp = g2.getRenderingHints();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            Stroke stroke = g2.getStroke();
            g2.setStroke(getSelectionStroke());
            g.setColor(getSelectionColor());

            Point last = state.getAbsolutePoint(0).getPoint();

            for (int i = 1; i < state.getAbsolutePointCount(); i++) {
                Point current = state.getAbsolutePoint(i).getPoint();
                Line2D line =
                        new Line2D.Float(last.x, last.y, current.x, current.y);

                Rectangle bounds =
                        g2.getStroke().createStrokedShape(line).getBounds();

                if (g.hitClip(bounds.x, bounds.y, bounds.width, bounds.height)) {
                    g2.draw(line);
                }

                last = current;
            }

            g2.setStroke(stroke);
            super.paint(g);

        } finally {
            g2.setRenderingHints(tmp);
        }
    }
}
