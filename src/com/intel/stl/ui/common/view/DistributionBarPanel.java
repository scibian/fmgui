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
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

/**
 */
public class DistributionBarPanel extends JPanel {
    private static final long serialVersionUID = 5519998835959335916L;

    private long[] cumulativeSum;

    private Color[] colors;

    private final int barHeight = 8;

    private JLabel[] labels;

    public DistributionBarPanel() {
        super();
        setLayout(new FlowLayout(FlowLayout.RIGHT, 4, 0));
        // setBorder(BorderFactory.createLineBorder(Color.RED));
    }

    public void init(String[] itemLabels, ImageIcon[] icons) {
        if (itemLabels.length != icons.length) {
            throw new IllegalArgumentException(
                    "Inconsistent array size. itemLabels=" + itemLabels.length
                            + " icons=" + icons.length);
        }

        labels = new JLabel[itemLabels.length];
        for (int i = 0; i < itemLabels.length; i++) {
            labels[i] = new JLabel(itemLabels[i], icons[i], JLabel.LEFT);
            labels[i].setVerticalAlignment(JLabel.BOTTOM);
            // labels[i].setBorder(BorderFactory.createLineBorder(Color.GREEN));
            add(labels[i]);
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#setBorder(javax.swing.border.Border)
     */
    @Override
    public void setBorder(Border border) {
        Border newBorder =
                BorderFactory.createCompoundBorder(border,
                        BorderFactory.createEmptyBorder(16, 0, 0, 0));
        super.setBorder(newBorder);
    }

    public void update(String[] newLabels, long[] cumulativeSum, Color[] colors) {
        if (newLabels.length != labels.length) {
            throw new IllegalArgumentException(
                    "Incorrect number of newLabels. Expected " + labels.length
                            + " items, got " + newLabels.length + " items");
        }
        if (cumulativeSum.length != labels.length) {
            throw new IllegalArgumentException(
                    "Incorrect number of cumulativeSum. Expected "
                            + labels.length + " items, got "
                            + cumulativeSum.length + " items");
        }
        if (colors.length != labels.length) {
            throw new IllegalArgumentException(
                    "Incorrect number of newLabels. Expected " + labels.length
                            + " items, got " + colors.length + " items");
        }

        this.cumulativeSum = cumulativeSum;
        this.colors = colors;
        for (int i = 0; i < colors.length; i++) {
            labels[i].setText(newLabels[i]);
        }
        repaint();
        validate();
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#getPreferredSize()
     */
    @Override
    public Dimension getPreferredSize() {
        Dimension res = super.getPreferredSize();
        res.height += barHeight;
        return res;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (cumulativeSum == null) {
            return;
        }

        Insets insets = getInsets();
        int width = getWidth() - insets.left - insets.right;
        int x = insets.left;
        int y = Math.max(insets.top - barHeight - 2, 0);

        int nextX = 0;
        double scale = (double) width / cumulativeSum[cumulativeSum.length - 1];
        for (int i = 0; i < cumulativeSum.length; i++) {
            x = nextX;
            nextX = (int) (cumulativeSum[i] * scale);
            g.setColor(colors[i]);
            g.fillRect(x, y, nextX - x, barHeight);
        }
    }

}
