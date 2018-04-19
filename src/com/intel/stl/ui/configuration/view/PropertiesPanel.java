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

import static com.intel.stl.ui.common.STLConstants.K0210_SHOW_OPTIONS;
import static com.intel.stl.ui.common.STLConstants.K0211_APPLY_OPTIONS;
import static com.intel.stl.ui.common.UILabels.STL40001_ERROR_No_DATA;
import static java.awt.Font.PLAIN;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import com.intel.stl.api.StringUtils;
import com.intel.stl.api.configuration.PropertyGroup;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ButtonPopup;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.model.PropertyGroupViz;
import com.intel.stl.ui.model.PropertySet;

public class PropertiesPanel<M extends PropertySet<?>> extends JPanel {
    private static final long serialVersionUID = 1L;

    private boolean enableOptionControl;

    private boolean enableStyleControl;

    private final int cardGap = 5;

    private int displayWidth;

    private List<Component> panels;

    private List<Integer> panelWidths;

    private int maxPanelWidth;

    private int totalWidth;

    private JToolBar toolBar;

    private JButton optionsBtn;

    private JPanel popupContent;

    private ButtonPopup popupOptions;

    private JButton borderBtn;

    private JButton alternationBtn;

    private JScrollPane scrollPane;

    private JPanel mainPanel;

    private IPropertyListener listener;

    private Map<String, Boolean> changedOptions;

    private Map<String, JCheckBox> options;

    /**
     * Description:
     *
     * @param enableOptionControl
     * @param enableStyleControl
     */
    public PropertiesPanel(boolean enableOptionControl,
            boolean enableStyleControl) {
        super();
        this.enableOptionControl = enableOptionControl;
        this.enableStyleControl = enableStyleControl;
        initComponents();
    }

    public PropertiesPanel() {
        this(true, true);
    }

    protected void initComponents() {
        setLayout(new BorderLayout());
        if (enableOptionControl || enableStyleControl) {
            add(getCtrlPanel(), BorderLayout.NORTH);
        }

        mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));

        scrollPane = new JScrollPane();
        scrollPane.setViewportView(mainPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getViewport().setOpaque(false);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void setMainPanelBackground(Color clr) {
        mainPanel.setBackground(clr);
        toolBar.setBackground(clr);
    }

    public void clearPanel() {
        mainPanel.removeAll();
        // Account for the vertical scroll bar width (15px)
        displayWidth = getWidth() - 15;
        maxPanelWidth = 0;
        totalWidth = 0;
        panels = new ArrayList<Component>();
        panelWidths = new ArrayList<Integer>();
    }

    /**
     * @param listener
     *            the listener to set
     */
    public void setStyleListener(IPropertyListener listener) {
        this.listener = listener;
    }

    public void addPropertyGroupPanel(Component groupPanel) {
        int width = groupPanel.getPreferredSize().width + cardGap;
        panels.add(groupPanel);
        if (width > maxPanelWidth) {
            maxPanelWidth = width;
        }
        panelWidths.add(width);
        totalWidth += width;
    }

    public void showError(Throwable caught) {
        String msg = STL40001_ERROR_No_DATA.getDescription() + " ("
                + StringUtils.getErrorMessage(caught) + ")";
        showMessage(msg);
    }

    public void showMessage(String message) {
        mainPanel.removeAll();
        JLabel label = ComponentFactory.getH2Label(message, PLAIN);
        label.setForeground(UIConstants.INTEL_BLUE);
        mainPanel.add(label);
        repaint();
        revalidate();
    }

    public void setModel(M model) {
        if (panels != null) {
            int numCols = getColumnNumber();
            int col = 0;
            GridBagConstraints gc;
            for (Component panel : panels) {
                if (col >= numCols) {
                    gc = createConstraints();
                    gc.weightx = 1;
                    gc.gridwidth = GridBagConstraints.REMAINDER;
                    mainPanel.add(Box.createGlue(), gc);
                    col = 0;
                }
                gc = createConstraints();
                gc.weightx = 0;
                mainPanel.add(panel, gc);
                col++;
            }
        }
        GridBagConstraints gc = createConstraints();
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weightx = 1;
        mainPanel.add(Box.createGlue(), gc);
        gc = createConstraints();
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.weighty = 1;
        mainPanel.add(Box.createGlue(), gc);
        scrollPane.repaint();
        scrollPane.revalidate();
        panels = null;
    }

    private GridBagConstraints createConstraints() {
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(cardGap, 0, 0, cardGap);
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTHWEST;
        return gc;
    }

    /**
     * Description: calculates the optimum number of columns given the maximum
     * width among all panels and the possible combinations where that width
     * might take effect given the number of columns.
     *
     * @return number of panels per row
     */
    private int getColumnNumber() {
        if (maxPanelWidth >= displayWidth || panels.size() == 1) {
            return 1;
        } else if (totalWidth < displayWidth) {
            return panels.size();
        }

        int numCols = 2;
        for (; numCols <= panels.size(); numCols++) {
            int rowWidth = getRowWidth(panelWidths, numCols);
            if (rowWidth >= displayWidth) {
                numCols -= 1;
                break;
            }
        }
        return numCols;
    }

    private int getRowWidth(List<Integer> widths, int numColumns) {
        int[] colWidth = new int[numColumns];
        int colIndex = 0;
        for (int i = 0; i < widths.size(); i++) {
            int width = widths.get(i);
            if (width > colWidth[colIndex]) {
                colWidth[colIndex] = width;
            }
            colIndex += 1;
            if (colIndex == numColumns) {
                colIndex = 0;
            }
        }
        int rowWidth = 0;
        for (int width : colWidth) {
            rowWidth += width;
        }
        return rowWidth;
    }

    protected Component getCtrlPanel() {
        if (toolBar == null) {
            toolBar = new JToolBar();
            toolBar.setFloatable(false);

            if (enableOptionControl) {
                addOptionButton();
            }

            if (enableStyleControl) {
                addStyleButtons();
            }
        }
        return toolBar;
    }

    protected void addOptionButton() {
        optionsBtn = new JButton(K0210_SHOW_OPTIONS.getValue(),
                UIImages.OPTIONS_NOTSELECTED_ICON.getImageIcon());
        optionsBtn.setFocusable(false);
        optionsBtn.setOpaque(false);
        ActionListener usrOptionsListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent actionEvent) {
                // This code was used together with a toggle button:
                // AbstractButton abstractButton =
                // (AbstractButton) actionEvent.getSource();
                // boolean selected =
                // abstractButton.getModel().isSelected();
                boolean selected = optionsBtn
                        .getIcon() == UIImages.OPTIONS_NOTSELECTED_ICON
                                .getImageIcon();
                if (selected) {
                    optionsBtn.setIcon(UIImages.NORMAL_ICON.getImageIcon());
                    optionsBtn.setText(K0211_APPLY_OPTIONS.getValue());
                    popupOptions.show();
                } else {
                    optionsBtn.setIcon(
                            UIImages.OPTIONS_NOTSELECTED_ICON.getImageIcon());
                    popupOptions.hide();
                    optionsBtn.setText(K0210_SHOW_OPTIONS.getValue());
                }
            }
        };

        optionsBtn.addActionListener(usrOptionsListener);
        toolBar.add(optionsBtn);

        popupContent = new JPanel();
        popupContent.setLayout(new GridBagLayout());
        popupContent.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 0));
        popupOptions = new ButtonPopup(optionsBtn, popupContent) {

            @Override
            public void onShow() {
            }

            @Override
            public void onHide() {
                switch (getHideReason()) {
                    case ButtonPopup.BUTTON_PRESSED:
                        if (listener != null) {
                            listener.onDisplayChanged(changedOptions);
                        }
                        break;
                    case ButtonPopup.FOCUS_LOST:
                        // We can check if changes were made and show a
                        // popup window to apply changes, but for now we'll
                        // ignore changes
                        break;
                    case ButtonPopup.ESC_PRESSED:
                        // Ignore changes
                        break;
                }
            }

        };

    }

    protected void addStyleButtons() {
        borderBtn = new JButton(STLConstants.K0531_HIDE_BORDER.getValue(),
                UIImages.HIDE_BORDER.getImageIcon());
        borderBtn.setOpaque(false);
        borderBtn.setFocusable(false);
        borderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = borderBtn.getIcon() != UIImages.SHOW_BORDER
                        .getImageIcon();
                setShowBorder(isSelected);
                if (listener != null) {
                    listener.onShowBorder(isSelected);
                }
            }
        });
        toolBar.add(borderBtn);

        alternationBtn = new JButton(STLConstants.K0533_UNI_ROWS.getValue(),
                UIImages.UNI_ROWS.getImageIcon());
        alternationBtn.setOpaque(false);
        alternationBtn.setFocusable(false);
        alternationBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                boolean isSelected = alternationBtn
                        .getIcon() != UIImages.ALT_ROWS.getImageIcon();
                setShowAlternation(isSelected);
                if (listener != null) {
                    listener.onShowAlternation(isSelected);
                }
            }
        });
        toolBar.add(alternationBtn);
    }

    protected void setShowBorder(boolean isSelected) {
        if (isSelected) {
            borderBtn.setIcon(UIImages.SHOW_BORDER.getImageIcon());
            borderBtn.setText(STLConstants.K0530_SHOW_BORDER.getValue());
        } else {
            borderBtn.setIcon(UIImages.HIDE_BORDER.getImageIcon());
            borderBtn.setText(STLConstants.K0531_HIDE_BORDER.getValue());
        }
    }

    protected void setShowAlternation(boolean isSelected) {
        if (isSelected) {
            alternationBtn.setIcon(UIImages.ALT_ROWS.getImageIcon());
            alternationBtn.setText(STLConstants.K0532_ALT_ROWS.getValue());
        } else {
            alternationBtn.setIcon(UIImages.UNI_ROWS.getImageIcon());
            alternationBtn.setText(STLConstants.K0533_UNI_ROWS.getValue());
        }
    }

    /**
     * <i>Description:</i>
     *
     * @param showBorder
     * @param showAlternation
     */
    public void setInitialStyle(boolean showBorder, boolean showAlternation) {
        setShowBorder(showBorder);
        setShowAlternation(showAlternation);
    }

    public void initUserSettings(final List<PropertyGroup> groups) {
        if (!enableOptionControl) {
            return;
        }

        options = new HashMap<String, JCheckBox>();
        changedOptions = new HashMap<String, Boolean>();
        popupContent.removeAll();

        // select all
        GridBagConstraints gc = createConstraints(2, 0, 2, 0);
        gc.gridwidth = GridBagConstraints.REMAINDER;
        JPanel panel = new JPanel(new GridLayout(1, 2, 5, 0));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 2, 0,
                        UIConstants.INTEL_ORANGE),
                BorderFactory.createEmptyBorder(2, 2, 4, 2)));
        JButton selectAll = ComponentFactory
                .getIntelActionButton(STLConstants.K4004_SELECT_ALL.getValue());
        selectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Entry<String, JCheckBox> option : options.entrySet()) {
                    option.getValue().setSelected(true);
                    changedOptions.put(option.getKey(), true);
                }
            }
        });
        panel.add(selectAll);
        JButton unselectAll = ComponentFactory.getIntelActionButton(
                STLConstants.K4005_UNSELECT_ALL.getValue());
        unselectAll.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for (Entry<String, JCheckBox> option : options.entrySet()) {
                    option.getValue().setSelected(false);
                    changedOptions.put(option.getKey(), false);
                }
            }
        });
        panel.add(unselectAll);
        popupContent.add(panel, gc);

        int row = 1;
        for (PropertyGroup group : groups) {
            JCheckBox box = addPropertyGroup(popupContent, group, row);
            options.put(group.getName(), box);
            row++;
        }

        gc = createConstraints(0, 5, 0, 0);
        gc.fill = GridBagConstraints.BOTH;
        gc.anchor = GridBagConstraints.NORTHWEST;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        popupContent.add(Box.createGlue(), gc);
    }

    private GridBagConstraints createConstraints(int yPadTop, int xPadLeft,
            int yPadBtm, int xPadRight) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.LINE_START;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(yPadTop, xPadLeft, yPadBtm, xPadRight);
        gc.weightx = 1;
        return gc;
    }

    private JCheckBox addPropertyGroup(JComponent component,
            PropertyGroup group, int row) {
        GridBagConstraints gc = createConstraints(0, 5, 0, 0);
        gc.gridx = 0;
        gc.gridy = row;
        gc.gridwidth = 2;
        boolean isDisplayed = group.isDisplayed();
        final String groupName = group.getName();
        String title = group.getTitle();
        if (title == null || title.length() == 0) {
            title = PropertyGroupViz.getPropertyGroupViz(groupName).getTitle();
        }
        final JCheckBox checkbox = new JCheckBox(title);
        checkbox.setSelected(isDisplayed);
        checkbox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changedOptions.put(groupName, checkbox.isSelected());
            }
        });
        component.add(checkbox, gc);
        return checkbox;
    }

}
