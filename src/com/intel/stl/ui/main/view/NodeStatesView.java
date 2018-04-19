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

package com.intel.stl.ui.main.view;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jfree.data.general.PieDataset;

import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.model.ChartStyle;

/**
 */
public class NodeStatesView extends JCardView<IChartStyleListener> {
    private static final long serialVersionUID = -8330957415988551326L;

    private JPanel mainPanel;

    private CardLayout layout;

    private NodeStatesBar barPanel;

    private NodeStatesPie piePanel;

    private JButton styleBtn;

    private ChartStyle style = ChartStyle.PIE;

    private ImageIcon barIcon, pieIcon;

    /**
     * @param title
     * @param controller
     */
    public NodeStatesView(String title) {
        super(title);
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.hpc.stl.ui.common.JCard#getMainPanel()
     */
    @Override
    protected JPanel getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            layout = new CardLayout();
            mainPanel.setLayout(layout);

            barPanel = new NodeStatesBar(false);
            barPanel.setOpaque(false);
            mainPanel.add(barPanel, ChartStyle.BAR.name());

            piePanel = new NodeStatesPie(false);
            piePanel.setOpaque(false);
            mainPanel.add(piePanel, ChartStyle.PIE.name());

            layout.show(mainPanel, ChartStyle.PIE.name());
        }
        return mainPanel;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.hpc.stl.ui.common.JCard#addControlButtons(javax.swing.JToolBar)
     */
    @Override
    protected void addControlButtons(JToolBar toolBar) {
        styleBtn =
                ComponentFactory.getImageButton(UIImages.BAR_ICON
                        .getImageIcon());
        styleBtn.setToolTipText(UILabels.STL40002_TO_BAR.getDescription());
        toolBar.add(styleBtn);

        super.addControlButtons(toolBar);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.common.view.JCardView#setCardListener(com.intel.stl.
     * ui.common.view.ICardListener)
     */
    @Override
    public void setCardListener(final IChartStyleListener listener) {
        super.setCardListener(listener);
        styleBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onStyleChange(style);
            }
        });
    }

    /**
     * Description:
     * 
     * @param dataset
     * @param colors
     */
    public void setDataset(PieDataset dataset, Color[] colors) {
        piePanel.setDataset(dataset, colors);
    }

    public void setStates(double[] values, String[] labels, String[] tooltips) {
        if (style == ChartStyle.PIE) {
            piePanel.setStates(values, labels, tooltips);
        } else if (style == ChartStyle.BAR) {
            barPanel.setStates(values, labels, tooltips);
        }
    }

    public void setStyle(ChartStyle style) {
        this.style = style;
        layout.show(mainPanel, style.name());
        if (style == ChartStyle.BAR) {
            if (pieIcon == null) {
                pieIcon = UIImages.PIE_ICON.getImageIcon();
            }
            styleBtn.setIcon(pieIcon);
            styleBtn.setToolTipText(UILabels.STL40003_TO_PIE.getDescription());
        } else if (style == ChartStyle.PIE) {
            if (barIcon == null) {
                barIcon = UIImages.BAR_ICON.getImageIcon();
            }
            styleBtn.setIcon(barIcon);
            styleBtn.setToolTipText(UILabels.STL40002_TO_BAR.getDescription());
        }
    }

    public void clear() {
        barPanel.clear();
        piePanel.clear();
    }
}
