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

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.main.view.IDataTypeListener;
import com.intel.stl.ui.model.DataType;
import com.intel.stl.ui.model.HistoryType;

public class OptionChartsView extends ChartsView {
    private static final long serialVersionUID = 3144008795121961486L;

    private ExJXList<DataType> dataTypeList;

    private ExJXList<HistoryType> historyTypeList;

    private IDataTypeListener<DataType> groupInfoTypeListener;

    private IDataTypeListener<HistoryType> historyTypeListener;

    private MouseListener groupInfoTypeListSelectionListener;

    private MouseListener historyTypeListSelectionListener;

    private JButton dataTypeBtn;

    private JButton historyTypeBtn;

    private ButtonPopup dataPopupOptions;

    private ButtonPopup historyPopupOptions;

    private DataType dataType;

    private DataType prevDataType;

    private HistoryType historyType;

    private HistoryType prevHistoryType;

    private JComponent ctrPanel;

    private JToolBar toolBar;

    /**
     * Description:
     * 
     * @param title
     * @param chartCreator
     * @param dataType
     */
    public OptionChartsView(String title, IChartCreator chartCreator) {
        super(title, chartCreator);
        initView();
    }

    private void initView() {
        ctrPanel = getExtraComponent();

        toolBar = new JToolBar();
        toolBar.setLayout(new GridBagLayout());
        toolBar.setFloatable(false);
        toolBar.setBackground(UIConstants.INTEL_WHITE);

        ctrPanel.add(toolBar);
    }

    public void setTypes(DataType... types) {
        if (types != null && types.length > 0) {
            dataTypeList = new ExJXList<DataType>(types);
            dataTypeList.setDisabledColor(UIConstants.INTEL_LIGHT_GRAY);
            dataType = types[0];
            prevDataType = types[0];
            setDataTypeListener(groupInfoTypeListener);
            addDataTypeButton();
        }
    }

    public void setDisbaledDataTypes(DataType... types) {
        if (dataTypeList != null) {
            dataTypeList.setDisabledItem(types);
        }
    }

    /**
     * @param groupInfoTypeActionListener
     *            the dataTypeListener to set
     */
    public void setDataTypeListener(final IDataTypeListener<DataType> listener) {

        groupInfoTypeListener = listener;
        if (dataTypeList != null && listener != null) {

            if (groupInfoTypeListSelectionListener != null) {
                dataTypeList
                        .removeMouseListener(groupInfoTypeListSelectionListener);
            }
            dataTypeList.addMouseListener(

            groupInfoTypeListSelectionListener = new MouseListener() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    if (SwingUtilities.isLeftMouseButton(e)
                            && e.getClickCount() == 1) {
                        DataType type =
                                (DataType) dataTypeList.getSelectedValue();
                        if (prevDataType != type) {
                            DataType oldType = prevDataType;
                            prevDataType = type;
                            if (dataTypeBtn != null) {
                                dataTypeBtn.setText(dataTypeList
                                        .getSelectedValue().toString());
                            }

                            listener.onDataTypeChange(oldType, type);
                        }
                        if (dataPopupOptions != null
                                && dataPopupOptions.isVisible()) {
                            dataPopupOptions.hide();
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

            setType(dataType);
        }
    }

    public void setType(DataType type) {
        dataTypeList.setSelectedValue(type, true);
        prevDataType = type;
        if (dataTypeBtn != null) {
            dataTypeBtn.setText(dataTypeList.getSelectedValue().toString());
        }
    }

    protected void addDataTypeButton() {
        dataTypeBtn =
                new JButton(dataType.getName(),
                        UIImages.DATA_TYPE.getImageIcon());
        dataTypeBtn.setFocusable(false);
        dataTypeBtn.setToolTipText(STLConstants.K0747_DATA_TYPE.getValue());
        dataTypeBtn.setBackground(UIConstants.INTEL_WHITE);
        ActionListener usrOptionsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                if (!dataPopupOptions.isVisible()) {
                    dataPopupOptions.show();
                } else {
                    dataPopupOptions.hide();
                }
            }
        };

        dataTypeBtn.addActionListener(usrOptionsListener);

        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.BOTH;
        gc.gridx = 0;
        gc.weightx = 1;

        toolBar.add(dataTypeBtn, gc);

        dataTypeList.setLayout(new GridBagLayout());
        dataTypeList.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        dataPopupOptions = new ButtonPopup(dataTypeBtn, dataTypeList) {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {

            }

        };

    }

    public void setHistoryTypes(HistoryType... types) {
        if (types != null && types.length > 0) {
            historyTypeList = new ExJXList<HistoryType>(types);
            historyTypeList.setDisabledColor(UIConstants.INTEL_LIGHT_GRAY);
            historyType = types[0];
            prevHistoryType = types[0];
            setHistoryTypeListener(historyTypeListener);
            addHistoryTypeButton();
        }
    }

    public void setDisabledHistoryTypes(HistoryType... types) {
        if (historyTypeList != null) {
            historyTypeList.setDisabledItem(types);
        }
    }

    /**
     * @param groupInfoTypeActionListener
     *            the dataTypeListener to set
     */
    public void setHistoryTypeListener(
            final IDataTypeListener<HistoryType> listener) {
        historyTypeListener = listener;
        if (historyTypeList != null && listener != null) {
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

    protected void addHistoryTypeButton() {
        historyTypeBtn =
                new JButton(historyType.getName(),
                        UIImages.HISTORY_ICON.getImageIcon());
        historyTypeBtn.setFocusable(false);
        historyTypeBtn.setToolTipText(STLConstants.K1113_HISTORY_SCOPE
                .getValue());
        historyTypeBtn.setBackground(UIConstants.INTEL_WHITE);
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
}
