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

package com.intel.stl.ui.monitor.view;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ButtonPopup;
import com.intel.stl.ui.common.view.ChartsView;
import com.intel.stl.ui.common.view.ExJXList;
import com.intel.stl.ui.common.view.ISectionListener;
import com.intel.stl.ui.common.view.JSectionView;
import com.intel.stl.ui.main.view.IDataTypeListener;
import com.intel.stl.ui.model.HistoryType;
import com.intel.stl.ui.model.LinkQualityViz;

/**
 * This is the charts section view on the Performance "Node" view. It holds the
 * cards that show the Tx/Rx packet graphs.
 */
public class PerformanceChartsSectionView extends
        JSectionView<ISectionListener> {

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6166893610476283350L;

    private JPanel mainPanel;

    private ChartsView rxCardView;

    private ChartsView txCardView;

    // Followings are history type realted members.
    private ExJXList<HistoryType> historyTypeList;

    private IDataTypeListener<HistoryType> historyTypeListener;

    private MouseListener historyTypeListSelectionListener;

    private JButton historyTypeBtn;

    private ButtonPopup historyPopupOptions;

    private HistoryType historyType;

    private HistoryType prevHistoryType;

    private String title = null;

    // Place holder until LinkQuality data is received.
    private ImageIcon linkQualityIcon = UIImages.LINK_QUALITY_NONE
            .getImageIcon();

    /**
     * Description:
     * 
     * @param title
     */
    public PerformanceChartsSectionView(String title) {
        super(title);
        this.title = title;
        setHistoryTypes(HistoryType.values());
        super.setIcon(linkQualityIcon);
    }

    private void setHistoryTypes(HistoryType... types) {
        if (types != null && types.length > 0) {
            JPanel ctrPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            getTitlePanel(title, linkQualityIcon).add(ctrPanel,
                    BorderLayout.CENTER);

            historyTypeList = new ExJXList<HistoryType>(types);
            historyTypeList.setDisabledColor(UIConstants.INTEL_LIGHT_GRAY);
            historyType = types[0];
            prevHistoryType = types[0];
            setHistoryTypeListener(historyTypeListener);
            addHistoryTypeButton(ctrPanel);
        }
    }

    public void setHistoryTypeListener(
            final IDataTypeListener<HistoryType> listener) {
        historyTypeListener = listener;
        if (listener != null) {
            if (historyTypeListSelectionListener != null) {
                historyTypeList
                        .removeMouseListener(historyTypeListSelectionListener);
            }
            historyTypeList.addMouseListener(

            historyTypeListSelectionListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)
                            && e.getClickCount() == 1) {
                        HistoryType type =
                                (HistoryType) historyTypeList
                                        .getSelectedValue();
                        if (prevHistoryType != type) {
                            HistoryType oldType = prevHistoryType;
                            prevHistoryType = type;
                            if (historyTypeBtn != null) {
                                historyTypeBtn.setText(historyTypeList
                                        .getSelectedValue().toString());
                            }

                            listener.onDataTypeChange(oldType, type);
                        }
                        if (historyPopupOptions != null
                                && historyPopupOptions.isVisible()) {
                            historyPopupOptions.hide();
                        }
                    }

                }

                @Override
                public void mousePressed(MouseEvent e) {
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                }

                @Override
                public void mouseEntered(MouseEvent e) {
                }

                @Override
                public void mouseExited(MouseEvent e) {
                }
            });

            setHistoryType(historyType);
        }
    }

    public void setHistoryType(HistoryType type) {
        historyTypeList.setSelectedValue(type, true);
        if (historyTypeBtn != null) {
            historyTypeBtn.setText(historyTypeList.getSelectedValue()
                    .toString());
        }
    }

    protected void addHistoryTypeButton(JPanel controlPanel) {
        JToolBar toolBar = new JToolBar();
        toolBar.setLayout(new GridBagLayout());
        toolBar.setFloatable(false);
        historyTypeBtn =
                new JButton(historyType.getName(),
                        UIImages.HISTORY_ICON.getImageIcon());
        historyTypeBtn.setFocusable(false);
        historyTypeBtn.setOpaque(false);
        historyTypeBtn.setToolTipText(STLConstants.K1113_HISTORY_SCOPE
                .getValue());
        historyTypeBtn.setBackground(UIConstants.INTEL_BACKGROUND_GRAY);
        ActionListener usrOptionsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!historyPopupOptions.isVisible()) {
                    historyPopupOptions.show();
                } else {
                    historyPopupOptions.hide();
                }
            }
        };

        historyTypeBtn.addActionListener(usrOptionsListener);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 1;
        gc.weightx = 1;

        toolBar.add(historyTypeBtn, gc);
        controlPanel.add(toolBar);

        historyTypeList.setLayout(new GridBagLayout());
        historyTypeList.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        historyPopupOptions = new ButtonPopup(historyTypeBtn, historyTypeList) {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {

            }
        };
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.common.view.JSectionView#getMainComponent()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPanel == null) {
            mainPanel = new JPanel();
            mainPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 2, 2));
        }
        return mainPanel;
    }

    public void installCardViews(ChartsView rxCardView, ChartsView txCardView) {
        this.rxCardView = rxCardView;
        this.txCardView = txCardView;

        if (mainPanel != null) {
            mainPanel.removeAll();
            mainPanel.setLayout(new GridLayout(1, 2, 5, 5));
            mainPanel.add(rxCardView);
            mainPanel.add(txCardView);
            revalidate();
        }
    }

    /**
     * @return the rxPacketsCardView
     */
    public ChartsView getRxCardView() {
        return rxCardView;
    }

    /**
     * @return the txPacketsCardView
     */
    public ChartsView getTxCardView() {
        return txCardView;
    }

    public void setLinkQualityValue(byte linkQuality) {
        linkQualityIcon =
                (ImageIcon) LinkQualityViz.getLinkQualityIcon(linkQuality);
        linkQualityIcon.setDescription(LinkQualityViz
                .getLinkQualityDescription(linkQuality));
        super.setIcon(linkQualityIcon);
    }

    public void clearQualityValue() {
        setIcon(null);
    }
}
