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

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;

import org.jfree.data.general.PieDataset;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.JCardView;
import com.intel.stl.ui.model.ChartStyle;

public class StatusView extends JCardView<IChartStyleListener> {
    private static final long serialVersionUID = 8374679365582635122L;

    private JButton styleBtn;

    private ChartStyle style = ChartStyle.PIE;

    private ImageIcon barIcon, pieIcon;

    private JPanel mainPanel;

    private NodeStatusPanel swPanel;

    private NodeStatusPanel fiPanel;

    /**
     * Description:
     *
     * @param title
     */
    public StatusView() {
        super(STLConstants.K0062_STATUS.getValue());
        // this is unnecessary, but can stop klocwork from complaining
        getMainComponent();
    }

    protected boolean isConcise() {
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.common.view.JCardView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            BoxLayout layout = new BoxLayout(mainPanel, BoxLayout.Y_AXIS);
            mainPanel.setLayout(layout);

            JLabel label = ComponentFactory.getH4Label(
                    STLConstants.K0063_SW_STATUS.getValue(), Font.BOLD);
            label.setBorder(BorderFactory.createEmptyBorder(2, 15, 0, 0));
            label.setAlignmentX(0.0f);
            mainPanel.add(label);

            swPanel = new NodeStatusPanel(isConcise());
            swPanel.setName(WidgetName.HP_STU_SW_DIST.name());
            swPanel.setOpaque(false);
            swPanel.setAlignmentX(0.0f);
            mainPanel.add(swPanel);

            label = ComponentFactory.getH4Label(
                    STLConstants.K0064_FI_STATUS.getValue(), Font.BOLD);
            label.setBorder(BorderFactory.createEmptyBorder(5, 15, 0, 0));
            label.setAlignmentX(0.0f);
            mainPanel.add(label);

            fiPanel = new NodeStatusPanel(isConcise());
            fiPanel.setName(WidgetName.HP_STU_HFI_DIST.name());
            fiPanel.setOpaque(false);
            fiPanel.setAlignmentX(0.0f);
            mainPanel.add(fiPanel);

            styleBtn.setName(WidgetName.HP_STU_CHART_STYLE.name());
            setHelpButtonName(WidgetName.HP_STU_HELP.name());
            setPinButtonName(WidgetName.HP_STU_PIN.name());
        }
        return mainPanel;
    };

    /**
     * @return the swPanel
     */
    public NodeStatusPanel getSwPanel() {
        return swPanel;
    }

    /**
     * @return the fiPanel
     */
    public NodeStatusPanel getFiPanel() {
        return fiPanel;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.common.JCard#addControlButtons(javax.swing.JToolBar)
     */
    @Override
    protected void addControlButtons(JToolBar toolBar) {
        styleBtn = ComponentFactory
                .getImageButton(UIImages.BAR_ICON.getImageIcon());
        styleBtn.setName(WidgetName.HP_STU_STYLE.name());
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
        if (styleBtn != null) {
            styleBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    listener.onStyleChange(style);
                }
            });
        }
    }

    public void setStyle(ChartStyle style) {
        if (styleBtn == null) {
            return;
        }

        this.style = style;
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

    public void setSwDataset(PieDataset dataset, Color[] colors) {
        swPanel.setDataset(dataset, colors);
    }

    public void setFiDataset(PieDataset dataset, Color[] colors) {
        fiPanel.setDataset(dataset, colors);
    }

    public void setSwStates(double[] values, String[] labels,
            String[] tooltips) {
        swPanel.setStates(values, labels, tooltips);
    }

    public void setFiStates(double[] values, String[] labels,
            String[] tooltips) {
        fiPanel.setStates(values, labels, tooltips);
    }

    public void clear() {
        swPanel.clear();
        fiPanel.clear();
    }

}
