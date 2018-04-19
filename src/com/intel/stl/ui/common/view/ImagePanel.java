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

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Insets;

import javax.swing.JPanel;

/**
 * A panel use a image as background
 */
public class ImagePanel extends JPanel {
    private static final long serialVersionUID = 1931660137919651275L;

    public enum Style {
        // scale image to fit this panel
        FIT_PANEL,
        // scale image to fit this panel's width
        FIT_PANEL_WIDTH,
        // scale image to fit this panel's height
        FIT_PANEL_HEIGHT,
        // just blindly draw image from left-top corner without any scaling
        FIXED,
        // resize panel to fit the image
        FIT_IMAGE,
        // resize panel to fit the image's width
        FIT_IMAGE_WIDTH,
        // resize panel to fit the image's height
        FIT_IMAGE_HEIGHT;
    };

    private final Image image;

    private final Style style;

    /**
     * Description:
     * 
     * @param image
     * @param style
     */
    public ImagePanel(Image image, Style style) {
        super();
        this.image = image;
        this.style = style;
    }

    // /*
    // * (non-Javadoc)
    // *
    // * @see javax.swing.JComponent#getMinimumSize()
    // */
    // @Override
    // public Dimension getMinimumSize() {
    // if (style == Style.FIT_IMAGE) {
    // Insets insets = getInsets();
    // int w = insets.left + image.getWidth(null) + insets.right;
    // int h = insets.top + image.getHeight(null) + insets.bottom;
    // return new Dimension(w, h);
    // } else if (style == Style.FIT_IMAGE_WIDTH) {
    // Insets insets = getInsets();
    // int w = insets.left + image.getWidth(null) + insets.right;
    // return new Dimension(w, super.getMinimumSize().height);
    // } else if (style == Style.FIT_IMAGE_HEIGHT) {
    // Insets insets = getInsets();
    // int h = insets.top + image.getHeight(null) + insets.bottom;
    // return new Dimension(super.getMinimumSize().width, h);
    // } else {
    // return super.getMinimumSize();
    // }
    // }
    //
    // /*
    // * (non-Javadoc)
    // *
    // * @see java.awt.Component#getSize()
    // */
    // @Override
    // public Dimension getSize() {
    // if (style == Style.FIT_IMAGE) {
    // Insets insets = getInsets();
    // int w = insets.left + image.getWidth(null) + insets.right;
    // int h = insets.top + image.getHeight(null) + insets.bottom;
    // return new Dimension(w, h);
    // } else if (style == Style.FIT_IMAGE_WIDTH) {
    // Insets insets = getInsets();
    // int w = insets.left + image.getWidth(null) + insets.right;
    // return new Dimension(w, super.getSize().height);
    // } else if (style == Style.FIT_IMAGE_HEIGHT) {
    // Insets insets = getInsets();
    // int h = insets.top + image.getHeight(null) + insets.bottom;
    // return new Dimension(super.getSize().width, h);
    // } else {
    // return super.getSize();
    // }
    // }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        if (style == Style.FIT_IMAGE) {
            return new Dimension(getImageDesiredWidth(),
                    getImageDesiredHeight());
        } else if (style == Style.FIT_IMAGE_WIDTH) {
            return new Dimension(getImageDesiredWidth(),
                    super.getPreferredSize().height);
        } else if (style == Style.FIT_IMAGE_HEIGHT) {
            return new Dimension(super.getPreferredSize().width,
                    getImageDesiredHeight());
        } else {
            return super.getPreferredSize();
        }
    }

    protected int getImageDesiredWidth() {
        Insets insets = getInsets();
        int w = insets.left + image.getWidth(null) + insets.right;
        return w;
    }

    protected int getImageDesiredHeight() {
        Insets insets = getInsets();
        int h = insets.top + image.getHeight(null) + insets.bottom;
        return h;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Insets insets = getInsets();
        switch (style) {
            case FIT_PANEL: {
                Dimension size = getSize();
                int width = (int) size.getWidth() - insets.left - insets.right;
                int height =
                        (int) size.getHeight() - insets.top - insets.bottom;
                g2d.drawImage(image, insets.left, insets.top, width, height,
                        this);
            }
            case FIT_PANEL_WIDTH: {
                Dimension size = getSize();
                int width = (int) size.getWidth() - insets.left - insets.right;
                double scale = (double) width / image.getWidth(this);
                int height = (int) (scale * image.getHeight(this));
                g2d.drawImage(image, insets.left, insets.top, width, height,
                        this);
            }
            case FIT_PANEL_HEIGHT: {
                Dimension size = getSize();
                int height =
                        (int) size.getHeight() - insets.top - insets.bottom;
                double scale = (double) height / image.getHeight(this);
                int width = (int) (scale * image.getWidth(this));
                g2d.drawImage(image, insets.left, insets.top, width, height,
                        this);
            }
            case FIXED:
            case FIT_IMAGE:
            case FIT_IMAGE_WIDTH:
            case FIT_IMAGE_HEIGHT: {
                g2d.drawImage(image, insets.left, insets.top, this);
            }
        }
    }

}
