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
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.border.Border;

/**
 */
public class JLabelBar extends JLabel {
	private static final long serialVersionUID = 5644031069268625217L;
	
	/**
	 * The value must be normalized between 0 and 1.0
	 */
	private double normalizedBarValue;
	private Color barColor;
	private int barSize = 5;
	private int barPosition;
	private Insets barInsets = new Insets(0, 5, 0, 0);
	private Border outerBorder;
	
	public JLabelBar(Icon image, int horizontalAlignment, 
			double normalizedBarValue, Color barColor, int barPosition) {
		super(image, horizontalAlignment);
		initBar(normalizedBarValue, barColor, barPosition);
	}
	
	public JLabelBar(String text, int horizontalAlignment, 
			double normalizedBarValue, Color barColor, int barPosition) {
		super(text, horizontalAlignment);
		initBar(normalizedBarValue, barColor, barPosition);
	}
	
	protected void initBar(double normalizedBarValue, Color barColor, int barPosition) {
		if (normalizedBarValue<0 || normalizedBarValue>1.0)
			throw new IllegalArgumentException("normalizedBarValue ("+normalizedBarValue+") in not in range [0, 1]");
		if (barPosition!=TOP && barPosition!=BOTTOM && barPosition!=LEFT && barPosition!=RIGHT)
			throw new IllegalArgumentException("Invalid barPosition ("+barPosition+"). It must be TOP, BOTTOM, LEFT or RIGHT.");
		
		this.normalizedBarValue = normalizedBarValue;
		this.barColor = barColor;
		this.barPosition = barPosition;
	}
	
	/**
	 * @param barSize the barSize to set
	 */
	public void setBarSize(int barSize) {
		this.barSize = barSize;
		setBorder(outerBorder);
		repaint();
	}
	
	/**
	 * @return the barInsets
	 */
	public Insets getBarInsets() {
		return barInsets;
	}

	/**
	 * @param barInsets the barInsets to set
	 */
	public void setBarInsets(Insets barInsets) {
		this.barInsets = barInsets;
		setBorder(outerBorder);
		repaint();
	}

	/* (non-Javadoc)
	 * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
	 */
	@Override
	public void setBorder(Border border) {
		this.outerBorder = border;
		Border innerBorder = null;
		int horizontalBarSpace = barInsets.top + barInsets.bottom + barSize;
		int verticalBarSpace = barInsets.left + barInsets.right + barSize;
		
		switch(barPosition) {
		case TOP:
			innerBorder = BorderFactory.createEmptyBorder(horizontalBarSpace, 0, 0, 0);
			break;
		case BOTTOM:
			innerBorder = BorderFactory.createEmptyBorder(0, 0, horizontalBarSpace, 0);
			break;
		case LEFT:
			innerBorder = BorderFactory.createEmptyBorder(0, verticalBarSpace, 0, 0);
			break;
		case RIGHT:
			innerBorder = BorderFactory.createEmptyBorder(0, 0, 0, verticalBarSpace);
			break;
		}
		Border newBorder = BorderFactory.createCompoundBorder(
				border, innerBorder);
		super.setBorder(newBorder);
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
	 */
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		switch(barPosition) {
		case TOP:
			drawTopBar(g);
			break;
		case BOTTOM:
			drawBottomBar(g);
			break;
		case LEFT:
			drawLeftBar(g);
			break;
		case RIGHT:
			drawRightBar(g);
			break;
		}
	}
	
	protected void drawTopBar(Graphics g) {
		Insets insets = getInsets();
		int w = (int) ((getWidth() - insets.left - insets.right - barInsets.left - barInsets.right) * normalizedBarValue);
		int h = barSize;
		int x = insets.left + barInsets.left;
		int y = insets.top - barInsets.bottom - barSize;
		if (y<0) {
			y = 0;
		}
		
		g.setColor(barColor);
		g.fillRect(x, y, w, h);
	}
	
	protected void drawBottomBar(Graphics g) {
		Insets insets = getInsets();
		int w = (int) ((getWidth() - insets.left - insets.right - barInsets.left - barInsets.right) * normalizedBarValue);
		int h = barSize;
		int x = insets.left + barInsets.left;
		int y = getHeight() - insets.bottom + barInsets.top;
		if (y<0) {
			y = 0;
		}
		
		g.setColor(barColor);
		g.fillRect(x, y, w, h);
	}

	protected void drawLeftBar(Graphics g) {
		Insets insets = getInsets();
		int w = barSize;
		int h = (int) ((getHeight() - insets.top - insets.bottom - barInsets.top - barInsets.bottom) * normalizedBarValue);
		int x = insets.left - barInsets.right - barSize;
		if (x<0) {
			x = 0;
		}
		int y = getHeight() - insets.bottom -barInsets.bottom - h;
		if (y<0) {
			y = 0;
		}
		
		g.setColor(barColor);
		g.fillRect(x, y, w, h);
	}

	protected void drawRightBar(Graphics g) {
		Insets insets = getInsets();
		int w = barSize;
		int h = (int) ((getHeight() - insets.top - insets.bottom - barInsets.top - barInsets.bottom) * normalizedBarValue);
		int x = getWidth() - insets.right + barInsets.left;
		if (x<0) {
			x = 0;
		}
		int y = getHeight() - insets.bottom -barInsets.bottom - h;
		if (y<0) {
			y = 0;
		}
		
		g.setColor(barColor);
		g.fillRect(x, y, w, h);
	}
}
