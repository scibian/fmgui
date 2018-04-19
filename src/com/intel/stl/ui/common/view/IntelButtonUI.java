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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.AbstractButton;
import javax.swing.ButtonModel;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.plaf.basic.BasicGraphicsUtils;

import com.intel.stl.ui.common.UIConstants;

public class IntelButtonUI extends BasicButtonUI {
    protected int dashedRectGap = 1;

    private Color pressedColor = UIConstants.INTEL_MEDIUM_DARK_BLUE;

    private Color hoverColor = UIConstants.INTEL_MEDIUM_BLUE;

    private Color focusColor = UIConstants.INTEL_WHITE;

    private Color disabledBackgroundColor = UIConstants.INTEL_BORDER_GRAY;

    private Color disabledForegroundColor = UIConstants.INTEL_GRAY;

    /**
     * Description:
     * 
     * @param pressedColor
     */
    public IntelButtonUI(Color hoverColor, Color pressedColor) {
        super();
        this.hoverColor = hoverColor;
        this.pressedColor = pressedColor;
    }

    /**
     * Description:
     * 
     */
    public IntelButtonUI() {
        super();
    }

    /**
     * @return the pressedColor
     */
    public Color getPressedColor() {
        return pressedColor;
    }

    /**
     * @param pressedColor
     *            the pressedColor to set
     */
    public void setPressedColor(Color pressedColor) {
        this.pressedColor = pressedColor;
    }

    /**
     * @return the hoverColor
     */
    public Color getHoverColor() {
        return hoverColor;
    }

    /**
     * @param hoverColor
     *            the hoverColor to set
     */
    public void setHoverColor(Color hoverColor) {
        this.hoverColor = hoverColor;
    }

    /**
     * @return the focusColor
     */
    public Color getFocusColor() {
        return focusColor;
    }

    /**
     * @param focusColor
     *            the focusColor to set
     */
    public void setFocusColor(Color focusColor) {
        this.focusColor = focusColor;
    }

    /**
     * @return the disabledBackgroundColor
     */
    public Color getDisabledBackgroundColor() {
        return disabledBackgroundColor;
    }

    /**
     * @param disabledBackgroundColor
     *            the disabledBackgroundColor to set
     */
    public void setDisabledBackgroundColor(Color disabledBackgroundColor) {
        this.disabledBackgroundColor = disabledBackgroundColor;
    }

    /**
     * @return the disabledForegroundColor
     */
    public Color getDisabledForegroundColor() {
        return disabledForegroundColor;
    }

    /**
     * @param disabledForegroundColor
     *            the disabledForegroundColor to set
     */
    public void setDisabledForegroundColor(Color disabledForegroundColor) {
        this.disabledForegroundColor = disabledForegroundColor;
    }

    /**
     * @return the dashedRectGap
     */
    public int getDashedRectGap() {
        return dashedRectGap;
    }

    /**
     * @param dashedRectGap
     *            the dashedRectGap to set
     */
    public void setDashedRectGap(int dashedRectGap) {
        this.dashedRectGap = dashedRectGap;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paint(java.awt.Graphics,
     * javax.swing.JComponent)
     */
    @Override
    public void paint(Graphics g, JComponent c) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        if (b.isRolloverEnabled() && model.isRollover()) {
            paintBackground(g, b, getHoverColor());
        } else if (!b.isEnabled()) {
            paintBackground(g, b, getDisabledBackgroundColor());
        }
        super.paint(g, c);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paintText(java.awt.Graphics,
     * javax.swing.JComponent, java.awt.Rectangle, java.lang.String)
     */
    @Override
    protected void paintText(Graphics g, JComponent c, Rectangle textRect,
            String text) {
        AbstractButton b = (AbstractButton) c;
        ButtonModel model = b.getModel();
        if (model.isEnabled()) {
            super.paintText(g, c, textRect, text);
        } else {
            g.setColor(getDisabledForegroundColor());
            FontMetrics fm = g.getFontMetrics();
            int mnemonicIndex = b.getDisplayedMnemonicIndex();
            BasicGraphicsUtils.drawStringUnderlineCharAt(g, text, mnemonicIndex,
                    textRect.x, textRect.y + fm.getAscent());
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * javax.swing.plaf.basic.BasicButtonUI#paintButtonPressed(java.awt.Graphics
     * , javax.swing.AbstractButton)
     */
    @Override
    protected void paintButtonPressed(Graphics g, AbstractButton b) {
        paintBackground(g, b, getPressedColor());
    }

    protected void paintBackground(Graphics g, AbstractButton b, Color clr) {
        if (b.isContentAreaFilled()) {
            Dimension size = b.getSize();
            g.setColor(clr);
            g.fillRect(0, 0, size.width, size.height);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.plaf.basic.BasicButtonUI#paintFocus(java.awt.Graphics,
     * javax.swing.AbstractButton, java.awt.Rectangle, java.awt.Rectangle,
     * java.awt.Rectangle)
     */
    @Override
    protected void paintFocus(Graphics g, AbstractButton b, Rectangle viewRect,
            Rectangle textRect, Rectangle iconRect) {
        int width = b.getWidth();
        int height = b.getHeight();
        g.setColor(getFocusColor());
        BasicGraphicsUtils.drawDashedRect(g, dashedRectGap, dashedRectGap,
                width - dashedRectGap * 2, height - dashedRectGap * 2);
    }
}
