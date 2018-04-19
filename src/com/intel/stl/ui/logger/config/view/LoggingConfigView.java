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

package com.intel.stl.ui.logger.config.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intel.stl.api.configuration.AppenderConfig;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.LoggingThreshold;
import com.intel.stl.api.configuration.RollingFileAppender;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.IntelComboBoxUI;
import com.intel.stl.ui.common.view.SafeNumberField;
import com.intel.stl.ui.logger.config.ILoggingControl;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.model.LoggingThresholdViz;
import com.intel.stl.ui.wizards.impl.WizardValidationException;

public class LoggingConfigView extends JDialog {

    private static final long serialVersionUID = -5605031349189530174L;

    public static final int DIALOG_WIDTH = 800;

    public static final int DIALOG_HEIGHT = 500;

    private DocumentListener isDirtyListener;

    private DocumentListener setDirtyListener;

    private boolean dirty;

    private JTextField txtfldConversionPattern;

    private JFileChooser fileChooser;

    private JComboBox<String> cboxThresholdValue;

    private JFormattedTextField txtfldMaxFileSize;

    private JTextField txtfldMaxBackUpIndex;

    private JFormattedTextField txtfldFileLocation;

    private final String byteStr = "B";

    private JComboBox<String> cboxFileSizeUnit;

    private JPanel pnlMainCtrl;

    private JButton btnReset;

    private JButton btnOk;

    private JButton btnCancel;

    private ILoggingControl loggingControlListener;

    private RollingFileAppender rollingFileAppender;

    private final IFabricView owner;

    public LoggingConfigView(IFabricView owner) {
        super((JFrame) owner,
                STLConstants.K3048_LOGGING_CONFIGURATION.getValue(), true);
        this.owner = owner;
        createDocumentListener();
        initComponents();
    }

    protected void initComponents() {

        JPanel contentPane = (JPanel) getContentPane();
        contentPane.setLayout(new BorderLayout());

        final JPanel pnlConfigAppender = new JPanel();
        pnlConfigAppender.setOpaque(true);
        pnlConfigAppender.setBackground(UIConstants.INTEL_WHITE);
        pnlConfigAppender.setLayout(new GridBagLayout());
        pnlConfigAppender
                .setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));
        GridBagConstraints gc = new GridBagConstraints();

        // Information Level Label
        gc.gridx = 0;
        gc.gridy = 0;
        gc.anchor = GridBagConstraints.WEST;
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(5, 5, 5, 5);
        gc.weighty = 1;
        JLabel lblThreshold = ComponentFactory.getH5Label(
                STLConstants.K0636_APPLICATION_LOG_LEVEL.getValue(), Font.BOLD);
        pnlConfigAppender.add(lblThreshold, gc);

        // Information Level combo box
        gc.gridx = 1;
        gc.weightx = 1;
        String[] thresholdValues =
                new String[] { STLConstants.K0698_ALL.getValue(),
                        STLConstants.K0632_TRACE.getValue(),
                        STLConstants.K0630_DEBUG.getValue(),
                        STLConstants.K0631_INFO.getValue(),
                        STLConstants.K3002_WARN.getValue(),
                        STLConstants.K0030_ERROR.getValue(),
                        STLConstants.K0699_OFF.getValue() };
        cboxThresholdValue = ComponentFactory.createComboBox(thresholdValues);
        cboxThresholdValue.setName(WidgetName.LOG_LEVEL.name());
        cboxThresholdValue.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {

                // Disable the logging fields if the threshold level = OFF
                int selectedIndex =
                        ((JComboBox<?>) e.getSource()).getSelectedIndex();
                enableFields(selectedIndex);
                setDirty();
            }
        });
        pnlConfigAppender.add(cboxThresholdValue, gc);
        //
        // // Console Level Label
        // gc.gridx = 0;
        // gc.gridy++;
        // gc.anchor = GridBagConstraints.WEST;
        // gc.fill = GridBagConstraints.HORIZONTAL;
        // gc.insets = new Insets(5, 5, 5, 5);
        // gc.weighty = 1;
        // JLabel lblConsoleThreshold =
        // ComponentFactory.getH5Label(
        // STLConstants.K0700_CONSOLE_LOG_LEVEL.getValue(),
        // Font.BOLD);
        // pnlConfigAppender.add(lblConsoleThreshold, gc);
        //
        // // Console Information Level combo box
        // gc.gridx = 1;
        // gc.weightx = 1;
        // cboxConsoleThresholdValue =
        // ComponentFactory.createComboBox(thresholdValues);
        // cboxConsoleThresholdValue.addItemListener(new ItemListener() {
        // @Override
        // public void itemStateChanged(ItemEvent e) {
        //
        // // Disable the logging fields if the threshold level = OFF
        // int selectedIndex =
        // ((JComboBox<?>) e.getSource()).getSelectedIndex();
        // enableFields(selectedIndex);
        // setDirty();
        // }
        // });
        // cboxThresholdValue.setUI(new IntelComboBoxUI());
        // pnlConfigAppender.add(cboxConsoleThresholdValue, gc);

        // // Information Level Enable CheckBox
        // gc.gridx = 2;
        // gc.weightx = 0;
        // chkboxThresholdEnable =
        // ComponentFactory.getIntelCheckBox(STLConstants.K0445_ENABLE
        // .getValue());
        // chkboxThresholdEnable.setSelected(true);
        // chkboxThresholdEnable.addActionListener(new ActionListener() {
        // @Override
        // public void actionPerformed(ActionEvent e) {
        // cboxThresholdValue.setEnabled(chkboxThresholdEnable
        // .isSelected());
        // }
        // });
        // pnlConfigAppender.add(chkboxThresholdEnable, gc);

        // Output Format Label
        gc.gridx = 0;
        gc.gridy++;
        gc.weightx = 0;
        JLabel lblConversionPattern = ComponentFactory.getH5Label(
                STLConstants.K0637_OUTPUT_FORMAT.getValue(), Font.BOLD);
        pnlConfigAppender.add(lblConversionPattern, gc);

        // Output Format Text Field
        gc.gridx = 1;
        gc.weightx = 1;
        txtfldConversionPattern = ComponentFactory.createTextField(null, false,
                4096, isDirtyListener, setDirtyListener); // can be any char
        txtfldConversionPattern.setName(WidgetName.LOG_OUTPUT_FORMAT.name());
        pnlConfigAppender.add(txtfldConversionPattern, gc);

        // Output Format Help Button
        gc.gridx = 2;
        gc.weightx = 0;
        JButton btnConversionPatternHelp = ComponentFactory
                .getImageButton(UIImages.HELP_ICON.getImageIcon());
        btnConversionPatternHelp
                .setName(WidgetName.LOG_OUTPUT_FORMAT_HELP.name());
        final ConversionPatternHelpDialog dlgPatternHelp =
                new ConversionPatternHelpDialog(pnlConfigAppender);
        btnConversionPatternHelp.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dlgPatternHelp.setVisible(true);
            }
        });
        pnlConfigAppender.add(btnConversionPatternHelp, gc);

        // Max File Size Label
        gc.gridx = 0;
        gc.gridy++;
        JLabel lblMaxFileSize = ComponentFactory.getH5Label(
                STLConstants.K0638_MAX_FILE_SIZE.getValue(), Font.BOLD);
        pnlConfigAppender.add(lblMaxFileSize, gc);

        // Max File Size Text Field
        gc.gridx = 1;
        gc.weightx = 1;
        txtfldMaxFileSize =
                new SafeNumberField<Long>(new DecimalFormat("###"), 0L, false);
        txtfldMaxFileSize.setName(WidgetName.LOG_FILE_SIZE.name());
        ComponentFactory.setupTextField(txtfldMaxFileSize, setDirtyListener,
                isDirtyListener);
        pnlConfigAppender.add(txtfldMaxFileSize, gc);

        // Max File Size Units ComboBox
        gc.gridx = 2;
        gc.weightx = 0;
        String unitValueArray[] =
                new String[] { STLConstants.K0697_BYTE.getValue(),
                        STLConstants.K0695_KB.getValue(),
                        STLConstants.K0722_MB.getValue(),
                        STLConstants.K0696_GB.getValue() };
        cboxFileSizeUnit = new JComboBox<String>(unitValueArray);
        cboxFileSizeUnit.setName(WidgetName.LOG_FILE_SIZE_UNIT.name());
        cboxFileSizeUnit.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                dirty = true;
                setDirty();
            }
        });
        cboxFileSizeUnit.setUI(new IntelComboBoxUI());
        pnlConfigAppender.add(cboxFileSizeUnit, gc);

        // Max # Files Label
        gc.gridx = 0;
        gc.gridy++;
        JLabel lblMaxBackupIndex = ComponentFactory.getH5Label(
                STLConstants.K0640_MAX_NUM_FILE.getValue(), Font.BOLD);
        pnlConfigAppender.add(lblMaxBackupIndex, gc);

        // Max # Files Text Field
        gc.gridx = 1;
        txtfldMaxBackUpIndex = ComponentFactory
                .createNumericTextField(setDirtyListener, isDirtyListener);
        txtfldMaxBackUpIndex.setName(WidgetName.LOG_MAX_NUM_FILES.name());
        pnlConfigAppender.add(txtfldMaxBackUpIndex, gc);

        // File Location Label
        gc.gridx = 0;
        gc.gridy++;
        JLabel lblFileLocation = ComponentFactory
                .getH5Label(STLConstants.K0641_FILE_LOC.getValue(), Font.BOLD);
        pnlConfigAppender.add(lblFileLocation, gc);

        // File Location Text Field
        gc.gridx = 1;
        gc.weightx = 1;
        txtfldFileLocation = ComponentFactory.createTextField(null, false, 4096,
                setDirtyListener, isDirtyListener);
        txtfldFileLocation.setName(WidgetName.LOG_FILE_LOCATION.name());
        pnlConfigAppender.add(txtfldFileLocation, gc);

        // File Location Browser Button
        gc.fill = GridBagConstraints.NONE;
        gc.gridx = 2;
        gc.weightx = 0;
        JButton browseButton = ComponentFactory
                .getImageButton(UIImages.FOLDER_ICON.getImageIcon());
        browseButton.setName(WidgetName.LOG_FILE_BROWSER.name());
        browseButton.setToolTipText(STLConstants.K0642_BROWSE.getValue());
        fileChooser = new JFileChooser();
        browseButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setDialogTitle(
                        STLConstants.K0643_SELECT_FILE.getValue());
                File logLocation =
                        new File(rollingFileAppender.getFileLocation());
                fileChooser.setCurrentDirectory(logLocation);
                fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

                int result = fileChooser.showOpenDialog(pnlConfigAppender);
                if (result == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    txtfldFileLocation.setText(file.getAbsolutePath());
                }
            }
        });
        pnlConfigAppender.add(browseButton, gc);

        // Add a component listener to this panel
        pnlConfigAppender.addAncestorListener(new AncestorListener() {

            @Override
            public void ancestorAdded(AncestorEvent event) {
                // Disable the logging fields if the threshold level = OFF
                int selectedIndex = cboxThresholdValue.getSelectedIndex();
                enableFields(selectedIndex);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {

            }

            @Override
            public void ancestorMoved(AncestorEvent event) {

            }
        });

        contentPane.add(pnlConfigAppender, BorderLayout.NORTH);

        contentPane.add(getControlPanel(), BorderLayout.SOUTH);

        pack();
    }

    protected boolean isNumeric(String str) {

        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    protected JPanel getControlPanel() {
        if (pnlMainCtrl == null) {
            pnlMainCtrl = new JPanel();
            pnlMainCtrl.setLayout(new BoxLayout(pnlMainCtrl, BoxLayout.X_AXIS));

            pnlMainCtrl.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));
            pnlMainCtrl.add(Box.createGlue());

            // Add the Reset button
            btnReset = ComponentFactory
                    .getIntelActionButton(STLConstants.K1006_RESET.getValue());
            btnReset.setName(WidgetName.LOG_RESET.name());
            btnReset.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    loggingControlListener.onReset();
                    btnReset.setEnabled(false);
                    btnOk.setEnabled(false);
                }
            });
            pnlMainCtrl.add(btnReset);
            pnlMainCtrl.add(Box.createHorizontalStrut(5));

            // Add the OK button
            btnOk = ComponentFactory
                    .getIntelActionButton(STLConstants.K0645_OK.getValue());
            btnOk.setName(WidgetName.LOG_OK.name());
            btnOk.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onOk();
                }
            });

            // Add a mouse listener to ensure the Run button gets the focus
            btnOk.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    ((JButton) e.getSource()).requestFocusInWindow();
                }
            });
            pnlMainCtrl.add(btnOk);
            pnlMainCtrl.add(Box.createHorizontalStrut(5));

            // Add the Cancel button
            btnCancel = ComponentFactory
                    .getIntelActionButton(STLConstants.K0621_CANCEL.getValue());
            btnCancel.setName(WidgetName.LOG_CANCEL.name());
            btnCancel.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onCancel();
                }

            });
            pnlMainCtrl.add(btnCancel);
        }
        return pnlMainCtrl;
    }

    /**
     *
     * <i>Description: Center the wizard dialog relative to the main frame</i>
     *
     */
    private void centerDialog() {
        pack();
        Point dialogLocation = new Point(0, 0);

        dialogLocation.x = owner.getScreenPosition().x
                + owner.getScreenSize().width / 2 - getWidth() / 2;

        dialogLocation.y = owner.getScreenPosition().y
                + owner.getScreenSize().height / 2 - getHeight() / 2;

        setLocation(dialogLocation);
    } // centerDialog

    protected JTextField createTextField(String txt) {
        if (isDirtyListener == null || setDirtyListener == null) {
            createDocumentListener();
        }

        JTextField txtField = new JTextField(txt);
        txtField.getDocument().addDocumentListener(setDirtyListener);
        txtField.getDocument().addDocumentListener(isDirtyListener);
        return txtField;
    }

    /**
     *
     * <i>Description: Document listeners to detect when changes occur to the
     * subnet wizard fields</i>
     *
     */
    protected void createDocumentListener() {
        isDirtyListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                dirty = true;
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                dirty = true;
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                dirty = true;
            }

        };

        setDirtyListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                setDirty();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                setDirty();
            }

        };
    }

    protected void enableFields(int selectedIndex) {

        // Disable the logging fields if the threshold level = OFF
        int offIndex = LoggingThresholdViz.OFF.getId();

        boolean enabled = (selectedIndex == offIndex) ? false : true;
        txtfldConversionPattern.setEnabled(enabled);
        txtfldMaxFileSize.setEnabled(enabled);
        txtfldMaxBackUpIndex.setEnabled(enabled);
        txtfldFileLocation.setEnabled(enabled);
    }

    protected void setDirty() {

        dirty = true;

        if ((txtfldConversionPattern.getText().length() > 0)
                && (txtfldMaxFileSize.getText().length() > 0)
                && (txtfldMaxBackUpIndex.getText().length() > 0)
                && (txtfldFileLocation.getText().length() > 0)) {

            btnOk.setEnabled(true);
            btnReset.setEnabled(true);
        } else {
            btnOk.setEnabled(false);
            btnReset.setEnabled(false);
        }
    }

    protected void onOk() {
        try {
            String selection = (String) cboxThresholdValue.getSelectedItem();
            LoggingThreshold threshold =
                    LoggingThresholdViz.getLoggingThreshold(selection);
            loggingControlListener.setRootLoggingLevel(threshold);
            boolean success = loggingControlListener.onOk();

            if (success) {
                // Close the Logging Config dialog
                closeLoggingConfig();
            }
        } catch (WizardValidationException e) {
            Util.showErrorMessage(this, e.getMessage());
        }
    }

    protected void onCancel() {
        closeLoggingConfig();
    }

    private void setMaxFileSize(String maxFileSizeStr) {
        // There could be no unit string at the end. That is for unit Byte.
        // See if it's Byte by checking the last character.

        if (!maxFileSizeStr.endsWith(byteStr)) {
            cboxFileSizeUnit
                    .setSelectedItem(STLConstants.K0697_BYTE.getValue());
            txtfldMaxFileSize.setText(maxFileSizeStr);
        } else {
            String unit = maxFileSizeStr.substring(maxFileSizeStr.length() - 2,
                    maxFileSizeStr.length());
            if (unit.equals(STLConstants.K0695_KB.getValue())) {
                cboxFileSizeUnit
                        .setSelectedItem(STLConstants.K0695_KB.getValue());
                txtfldMaxFileSize.setText(maxFileSizeStr.substring(0,
                        maxFileSizeStr.length() - 2));
            } else if (unit.equals(STLConstants.K0722_MB.getValue())) {
                cboxFileSizeUnit
                        .setSelectedItem(STLConstants.K0722_MB.getValue());
                txtfldMaxFileSize.setText(maxFileSizeStr.substring(0,
                        maxFileSizeStr.length() - 2));
            } else if (unit.equals(STLConstants.K0696_GB.getValue())) {
                cboxFileSizeUnit
                        .setSelectedItem(STLConstants.K0696_GB.getValue());
                txtfldMaxFileSize.setText(maxFileSizeStr.substring(0,
                        maxFileSizeStr.length() - 2));
            } else {
                throw new IllegalArgumentException(
                        "Unknow unit '" + unit + "'");
            }
        }
    }

    public void setLoggingControlListener(ILoggingControl listener) {
        this.loggingControlListener = listener;
    }

    public void setConversionPattern(String pattern) {
        txtfldConversionPattern.setText(pattern);
    }

    public void initView(
            final HashMap<String, AppenderConfig> appenderConfigMap) {

        Runnable init = new Runnable() {

            @Override
            public void run() {

                // Get the rolling file appender
                rollingFileAppender = (RollingFileAppender) appenderConfigMap
                        .get(STLConstants.K3003_ROLLING_FILE_APPENDER
                                .getValue());

                // Populate the logging wizard form
                // If the threshold is null, initialize the combo box to default
                // ERROR
                if (rollingFileAppender != null) {
                    LoggingThreshold threshold =
                            loggingControlListener.getRootLoggingLevel();
                    if (threshold == null) {
                        cboxThresholdValue.setSelectedIndex(
                                LoggingThresholdViz.ERROR.getId());
                    } else {
                        cboxThresholdValue.setSelectedIndex(threshold.getId());
                    }

                    // Update output format
                    txtfldConversionPattern.setText(
                            rollingFileAppender.getConversionPattern());

                    // Set the max file size
                    setMaxFileSize(rollingFileAppender.getMaxFileSize());
                    txtfldMaxBackUpIndex.setText(String
                            .valueOf(rollingFileAppender.getMaxNumOfBackUp()));

                    // Set the logger's file location
                    txtfldFileLocation
                            .setText(rollingFileAppender.getFileLocation());
                }
            }
        };
        Util.runInEDT(init);
    }

    public void updateAppender(LoggingConfiguration loggingConfig) {

        try {

            List<AppenderConfig> appenders = loggingConfig.getAppenders();
            Iterator<AppenderConfig> it = appenders.iterator();
            while (it.hasNext()) {

                AppenderConfig appender = it.next();
                // if (appender instanceof ConsoleAppender) {
                // int index = cboxConsoleThresholdValue.getSelectedIndex();
                // LoggingThreshold threshold =
                // LoggingThreshold.values()[index];
                // appender.setThreshold(threshold);
                // } else

                if (appender instanceof RollingFileAppender) {

                    rollingFileAppender.setName(appender.getName());

                    rollingFileAppender.setConversionPattern(
                            txtfldConversionPattern.getText());

                    try {
                        rollingFileAppender.setFileLocation(
                                txtfldFileLocation.getText().trim());

                    } catch (ConfigurationException e) {
                        Util.showErrorMessage(this, e.getMessage());
                    }

                    String fileSizeUnitSelected =
                            (String) cboxFileSizeUnit.getSelectedItem();
                    String maxFileSizestr = txtfldMaxFileSize.getText().trim();
                    if (fileSizeUnitSelected
                            .equals(STLConstants.K0697_BYTE.getValue())) {
                        rollingFileAppender.setMaxFileSize(maxFileSizestr);
                    } else {
                        String maxFileSizeInclUnitStr =
                                maxFileSizestr + fileSizeUnitSelected;
                        rollingFileAppender
                                .setMaxFileSize(maxFileSizeInclUnitStr);
                    }

                    // Set the number of backups
                    String maxNumBackupsStr =
                            txtfldMaxBackUpIndex.getText().trim();
                    rollingFileAppender.setMaxNumOfBackUp(maxNumBackupsStr);
                }
            }
        } catch (Exception e) {
            Util.showErrorMessage(this,
                    UILabels.STL50057_LOGGING_CONFIG_SAVE_FAILURE
                            .getDescription());
        }
    }

    public void setDirty(boolean dirty) {

        this.dirty = dirty;
    }

    public boolean isDirty() {
        return dirty;
    }

    public void clearPanel() {
        cboxThresholdValue.setSelectedItem(STLConstants.K0699_OFF.getValue());
        cboxFileSizeUnit.setSelectedIndex(0);
        txtfldConversionPattern.setText("");
        txtfldFileLocation.setText("");
        txtfldMaxFileSize.setText("");
        txtfldMaxBackUpIndex.setText("");
    }

    public void showErrorMessage(String errorMessage) {
        Util.showErrorMessage(this, errorMessage);
    }

    public void showLoggingConfig() {
        btnOk.setEnabled(false);
        btnReset.setEnabled(false);
        centerDialog();
        this.setVisible(true);
    }

    public void closeLoggingConfig() {
        this.setVisible(false);
    }

}
