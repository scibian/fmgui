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

package com.intel.stl.ui.configuration.view;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JComponent;

import com.intel.stl.ui.common.UIConstants;

public class PropertyVizStyle {
    private boolean showBorder;

    private Color borderColor = UIConstants.INTEL_LIGHT_GRAY;

    private boolean alternateRows;

    private Color alternationColor = UIConstants.INTEL_BACKGROUND_GRAY;

    /**
     * Description:
     *
     */
    public PropertyVizStyle() {
        super();
    }

    /**
     * Description:
     *
     * @param showBorder
     * @param useAlternation
     */
    public PropertyVizStyle(boolean showBorder, boolean alternateRows) {
        super();
        this.showBorder = showBorder;
        this.alternateRows = alternateRows;
    }

    /**
     * @return the showBorder
     */
    public boolean isShowBorder() {
        return showBorder;
    }

    /**
     * @param showBorder
     *            the showBorder to set
     */
    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    /**
     * @return the borderColor
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * @param borderColor
     *            the borderColor to set
     */
    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    /**
     * @return the useAlternation
     */
    public boolean isAlternatRows() {
        return alternateRows;
    }

    /**
     * @param useAlternation
     *            the useAlternation to set
     */
    public void setAlternateRows(boolean alternateRows) {
        this.alternateRows = alternateRows;
    }

    /**
     * @return the alternationColor
     */
    public Color getAlternationColor() {
        return alternationColor;
    }

    /**
     * @param alternationColor
     *            the alternationColor to set
     */
    public void setAlternationColor(Color alternationColor) {
        this.alternationColor = alternationColor;
    }

    public void decorateKey(JComponent comp, int row) {
        if (alternateRows && (row & 0x01) == 0x01) {
            comp.setOpaque(true);
            comp.setBackground(alternationColor);
        }
        if (showBorder) {
            // String text = label.getText();
            // if (!text.endsWith(" ")) {
            // label.setText(text + " ");
            // }
            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 1, 1, 1, borderColor),
                    comp.getBorder()));
        } else {
            // String text = label.getText();
            // if (!text.endsWith(":")) {
            // label.setText(text + ":");
            // }
            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(1, 1, 1, 1),
                    comp.getBorder()));
        }
    }

    public void decorateValue(JComponent comp, int row) {
        if (alternateRows && (row & 0x01) == 0x01) {
            comp.setOpaque(true);
            comp.setBackground(alternationColor);
        }
        if (showBorder) {
            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(1, 0, 1, 1, borderColor),
                    comp.getBorder()));
        } else {
            comp.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(1, 0, 1, 1),
                    comp.getBorder()));
        }
    }

    public void decorateHeaderKey(JComponent comp, int row) {
        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
    }

    public void decorateHeaderValue(JComponent comp) {
        comp.setFont(comp.getFont().deriveFont(Font.BOLD));
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "PropertyVizStyle [showBorder=" + showBorder + ", borderColor="
                + borderColor + ", alternateRows=" + alternateRows
                + ", alternationColor=" + alternationColor + "]";
    }

}
