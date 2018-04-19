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
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;

import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI;

import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

public class IntelTaskPaneUI extends BasicTaskPaneUI {
    private Font titleFont = UIConstants.H4_FONT.deriveFont(Font.BOLD);

    private Color titleColor = UIConstants.INTEL_DARK_GRAY;

    private Color titleOverColor = UIConstants.INTEL_BLUE;

    private Color titleBackground = UIConstants.INTEL_WHITE;

    private Color titleBorder = UIConstants.INTEL_BORDER_GRAY;

    private Color contentBackground = UIConstants.INTEL_WHITE;

    private UIImages expandImg = UIImages.UP_ICON;

    private UIImages collapseImg = UIImages.DOWN_ICON;

    private Insets contentInsets = new Insets(2, 2, 2, 2);

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#installDefaults()
     */
    @Override
    protected void installDefaults() {
        super.installDefaults();
        group.setFont(titleFont);
        group.getContentPane().setBackground(contentBackground);
    }

    /**
     * @param titleFont
     *            the titleFont to set
     */
    public void setTitleFont(Font titleFont) {
        this.titleFont = titleFont;
    }

    /**
     * @param titleColor
     *            the titleColor to set
     */
    public void setTitleColor(Color titleColor) {
        this.titleColor = titleColor;
    }

    /**
     * @param titleOverColor
     *            the titleOverColor to set
     */
    public void setTitleOverColor(Color titleOverColor) {
        this.titleOverColor = titleOverColor;
    }

    /**
     * @param titleBackground
     *            the titleBackground to set
     */
    public void setTitleBackground(Color titleBackground) {
        this.titleBackground = titleBackground;
    }

    /**
     * @param titleBorder
     *            the titleBorder to set
     */
    public void setTitleBorder(Color titleBorder) {
        this.titleBorder = titleBorder;
    }

    /**
     * @param contentBackground
     *            the contentBackground to set
     */
    public void setContentBackground(Color contentBackground) {
        this.contentBackground = contentBackground;
    }

    /**
     * @param expandImg
     *            the expandImg to set
     */
    public void setExpandImg(UIImages expandImg) {
        this.expandImg = expandImg;
    }

    /**
     * @param collapseImg
     *            the collapseImg to set
     */
    public void setCollapseImg(UIImages collapseImg) {
        this.collapseImg = collapseImg;
    }

    /**
     * @param contentInsets
     *            the contentInsets to set
     */
    public void setContentInsets(Insets contentInsets) {
        this.contentInsets = contentInsets;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#createPaneBorder()
     */
    @Override
    protected Border createPaneBorder() {
        return new IntelPaneBorder();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI#createContentPaneBorder()
     */
    @Override
    protected Border createContentPaneBorder() {
        Color borderColor = UIManager.getColor("TaskPane.borderColor");
        return new CompoundBorder(new ContentPaneBorder(borderColor),
                BorderFactory.createEmptyBorder(contentInsets.top,
                        contentInsets.left, contentInsets.bottom,
                        contentInsets.right));
    }

    class IntelPaneBorder extends PaneBorder {

        /**
         * Description:
         * 
         */
        public IntelPaneBorder() {
            super();
            titleForeground = titleColor;
            titleBackgroundGradientStart = titleBackground;
            titleOver = titleOverColor;
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#
         * paintTitleBackground(org.jdesktop.swingx.JXTaskPane,
         * java.awt.Graphics)
         */
        @Override
        protected void paintTitleBackground(JXTaskPane group, Graphics g) {
            super.paintTitleBackground(group, g);
            g.setColor(titleBorder);
            g.drawRect(0, 0, group.getWidth() - 1, getTitleHeight(group) - 1);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#
         * paintExpandedControls(org.jdesktop.swingx.JXTaskPane,
         * java.awt.Graphics, int, int, int, int)
         */
        @Override
        protected void paintExpandedControls(JXTaskPane group, Graphics g,
                int x, int y, int width, int height) {
            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);

            if (group.isCollapsed()) {
                g.drawImage(collapseImg.getImage(), x, y, group);
            } else {
                g.drawImage(expandImg.getImage(), x, y, group);
            }

            ((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_OFF);
        }

        /*
         * (non-Javadoc)
         * 
         * @see org.jdesktop.swingx.plaf.basic.BasicTaskPaneUI.PaneBorder#
         * isMouseOverBorder()
         */
        @Override
        protected boolean isMouseOverBorder() {
            return true;
        }

    }
}
