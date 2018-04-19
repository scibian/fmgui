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


package com.intel.stl.ui.common.view;

import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.JLabel;

public class JHorizontalBar extends JLabel {
    private static final long serialVersionUID = 3695428455610236209L;

    private double normalizedValue;
    private double upperMargin = 0.25;
    private double lowerMargin = 0.25;
    
    public JHorizontalBar() {
        super();
        setOpaque(true);
    }

    /**
     * @return the normalizedValue
     */
    public double getNormalizedValue() {
        return normalizedValue;
    }

    /**
     * @param normalizedValue the normalizedValue to set
     */
    public void setNormalizedValue(double normalizedValue) {
        check(normalizedValue);
        this.normalizedValue = normalizedValue;
        repaint();
    }

    /**
     * @return the upperMargin
     */
    public double getUpperMargin() {
        return upperMargin;
    }

    /**
     * @param upperMargin the upperMargin to set
     */
    public void setUpperMargin(double upperMargin) {
        check(upperMargin);
        this.upperMargin = upperMargin;
        repaint();
    }

    /**
     * @return the lowerMargin
     */
    public double getLowerMargin() {
        return lowerMargin;
    }

    /**
     * @param lowerMargin the lowerMargin to set
     */
    public void setLowerMargin(double lowerMargin) {
        check(lowerMargin);
        this.lowerMargin = lowerMargin;
        repaint();
    }
    
    protected void check(double value) {
        if (value<0 || value>1) {
            throw new IllegalArgumentException("Value "+value+" is not in range [0, 1]");
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        Insets insets = getInsets();
        
        g.setColor(getForeground());
        int w = (int) ((getWidth() - insets.left - insets.right) * normalizedValue);
        int height = getHeight() - insets.top - insets.bottom;
        int y = insets.top + (int)(height * upperMargin);
        int h = (int) (height * (1 - upperMargin - lowerMargin));
        if (h>0 && w>0) {
            g.fillRect(insets.left, y, w, h);
        }
    }
    
}
