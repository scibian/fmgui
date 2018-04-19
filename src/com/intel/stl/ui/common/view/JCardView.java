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

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;

/**
 */
public abstract class JCardView<E extends ICardListener> extends JPanel {
    private static final long serialVersionUID = 381185145876115191L;

    protected E listener;

    private JComponent contentComponent;

    private String title;

    private JPanel titlePanel;

    private JLabel titleLabel;

    private JToolBar toolBar;

    private JButton helpBtn;

    private JButton pinBtn;

    private ActionListener pinListener;

    private boolean showTitle;

    private Color boderColor = UIConstants.INTEL_BORDER_GRAY;

    private int borderRound = 15;

    private int borderThick = 2;

    public JCardView(String title) {
        this(title, true);
    }

    public JCardView(String title, boolean showTitle) {
        this.title = title;
        this.showTitle = showTitle;

        setLayout(new BorderLayout(0, 0));
        setBackground(UIConstants.INTEL_WHITE);
        setBorder(BorderFactory.createEmptyBorder(borderThick, borderThick,
                borderThick, borderThick));
        setOpaque(false);

        if (showTitle) {
            JPanel panel = getTitlePanel(title);
            if (panel != null) {
                add(panel, BorderLayout.NORTH);
            }
        }

        contentComponent = getMainComponent();
        if (contentComponent != null) {
            contentComponent.setOpaque(false);
            add(contentComponent, BorderLayout.CENTER);
        }
    }

    protected JPanel getTitlePanel(String title) {
        if (titlePanel == null) {
            titlePanel = new JPanel(new BorderLayout(0, 0));
            titlePanel.setOpaque(false);
            titlePanel.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                    UIConstants.INTEL_PALE_BLUE));

            if (title != null) {
                titleLabel = ComponentFactory.getH4Label(title, Font.PLAIN);
                titleLabel.setForeground(UIConstants.INTEL_DARK_GRAY);
                titleLabel.setBorder(BorderFactory
                        .createEmptyBorder(0, 5, 0, 5));
                titlePanel.add(titleLabel, BorderLayout.WEST);
            }

            toolBar = new JToolBar();
            toolBar.setOpaque(false);
            toolBar.setFloatable(false);
            titlePanel.add(toolBar, BorderLayout.EAST);
            addControlButtons(toolBar);

            JComponent comp = getExtraComponent();
            if (comp != null) {
                comp.setOpaque(false);
                titlePanel.add(comp, BorderLayout.CENTER);
            }
        }
        return titlePanel;
    }

    protected JComponent getExtraComponent() {
        return null;
    }

    protected void addControlButtons(JToolBar toolBar) {
        pinBtn =
                ComponentFactory.getImageButton(UIImages.PIN_ICON
                        .getImageIcon());
        pinBtn.setToolTipText(STLConstants.K0038_PIN_TOOLTIP.getValue());
        pinBtn.setEnabled(false);
        toolBar.add(pinBtn);

        helpBtn =
                ComponentFactory.getImageButton(UIImages.HELP_ICON
                        .getImageIcon());
        helpBtn.setToolTipText(STLConstants.K0037_HELP.getValue());
        helpBtn.setEnabled(false);
        toolBar.add(helpBtn);
    }

    public void setHelpButtonName(String name){
        if(null != helpBtn){
            helpBtn.setName(name);
        }
    }

    public void setPinButtonName(String name){
        if(null != pinBtn){
            pinBtn.setName(name);
        }
    }

    public void enableHelp(boolean b) {
        if (helpBtn != null) {
            helpBtn.setEnabled(b);
            repaint();
        }
    }

    public void enablePin(boolean b) {
        if (pinBtn != null) {
            pinBtn.setEnabled(b);
            repaint();
        }
    }

    protected void setPinListener() {
        if (pinListener != null) {
            pinBtn.removeActionListener(pinListener);
        }
        pinListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onPin();
            }
        };
        pinBtn.addActionListener(pinListener);
    }

    public JButton getHelpButton() {
        return helpBtn;
    }

    public void setCardListener(final E listener) {
        this.listener = listener;
        if (listener != null) {
            setPinListener();

            // help action is set from controller via HelpAction
        }
    }

    public void setTitle(String title) {
        setTitle(title, null);
    }

    public void setTitle(String title, String tooltip) {
        this.title = title;
        if (showTitle && titleLabel != null) {
            titleLabel.setText(title);
            titleLabel.setToolTipText(tooltip);
            validate();
        }
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param boderColor
     *            the boderColor to set
     */
    public void setBoderColor(Color boderColor) {
        this.boderColor = boderColor;
    }

    /**
     * @param round
     *            the round to set
     */
    public void setBorderRound(int round) {
        this.borderRound = round;
    }

    /**
     * @param borderThick
     *            the borderThick to set
     */
    public void setBorderThick(int borderThick) {
        this.borderThick = borderThick;
    }

    protected abstract JComponent getMainComponent();

    /**
     * @return the contentComponent
     */
    public JComponent getContentComponent() {
        return contentComponent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.JComponent#paintComponent(java.awt.Graphics)
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int width = getWidth();
        int height = getHeight();
        Graphics2D graphics = (Graphics2D) g;
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Stroke oldStroke = graphics.getStroke();
        Color oldColor = graphics.getColor();

        graphics.setColor(getBackground());
        graphics.fillRoundRect(0, 0, width - borderThick, height - borderThick,
                borderRound, borderRound);

        if (borderThick > 0) {
            graphics.setColor(boderColor);
            graphics.setStroke(new BasicStroke(borderThick));
            graphics.drawRoundRect(0, 0, width - borderThick, height
                    - borderThick, borderRound, borderRound);
        }

        graphics.setStroke(oldStroke);
        graphics.setColor(oldColor);
    }

}
