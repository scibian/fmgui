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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.ByteArrayOutputStream;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.TitledBorder;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;

import org.jdesktop.swingx.VerticalLayout;
import org.slf4j.LoggerFactory;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.OutputStreamAppender;

class ConversionPatternHelpDialog extends JDialog {

    private static final long serialVersionUID = 3640306308504496390L;

    private JPanel buttonPannel = null;

    private JScrollPane conversionPatternPanel = null;

    private JPanel previewPanel = null;

    private JButton okButton = null;

    private JButton cancelButton = null;

    private JButton previewButton = null;

    @SuppressWarnings("unused")
    private JPanel parentDialog = null;

    private JTextField previewText = null;

    private JLabel formatString = null;

    private JLabel formatStringLabel = null;

    private JLabel sampleString = null;

    private JLabel sampleStringLabel = null;

    private JTable conversionCharacterTable = null;

    private final String heading[] =
            new String[] { STLConstants.K0646_CONVERSION_CHARACTER.getValue(),
                    STLConstants.K0647_EFFECT.getValue() };

    private final String data[][] = new String[14][2];

    final Logger logger =
            (Logger) LoggerFactory.getLogger(ConversionPatternHelpDialog.class);

    final LoggerContext loggerContext = logger.getLoggerContext();

    public ConversionPatternHelpDialog(JPanel mainPanel) {
        parentDialog = mainPanel;
        setTitle(STLConstants.K0648_OUTPUT_FORMAT_HELP.getValue());
        setResizable(false);
        Container container = getContentPane();
        container.setLayout(new VerticalLayout());
        container.add(getPreviewPanel());
        container.add(getConversionPatternPanel());
        container.add(getButtonPanel());
        setModal(true);
        pack();
        setLocationRelativeTo(mainPanel);
        setAlwaysOnTop(true);
    }

    protected JPanel getPreviewPanel() {
        if (previewPanel == null) {
            previewPanel = new JPanel(new GridBagLayout());
            previewPanel.setBackground(UIConstants.INTEL_WHITE);
            previewPanel.setBorder(
                    new TitledBorder(BorderFactory.createEtchedBorder(),
                            STLConstants.K0664_PREVIEW.getValue(),
                            TitledBorder.LEADING, TitledBorder.TOP,
                            UIConstants.H4_FONT.deriveFont(Font.BOLD), null));

            GridBagConstraints gc = new GridBagConstraints();
            // preview Panel element generation
            formatStringLabel =
                    ComponentFactory
                            .getH5Label(
                                    UILabels.STL50049_TO_PREVIEW.getDescription(
                                            UILabels.STL50058_SAMPLE_LOG_MESSAGE
                                                    .getDescription()),
                            Font.BOLD);
            gc.gridx = 0;
            gc.gridy = 0;
            gc.gridwidth = 3;
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.anchor = GridBagConstraints.WEST;
            gc.insets = new Insets(3, 5, 3, 5);
            previewPanel.add(formatStringLabel, gc);

            formatString = ComponentFactory.getH5Label(
                    STLConstants.K0665_ENTER_FORMAT.getValue(), Font.BOLD);
            gc.gridx = 0;
            gc.gridy = 1;
            gc.gridwidth = 1;
            gc.weightx = 0;
            previewPanel.add(formatString, gc);

            previewText = new JTextField(40);
            previewText.setName(WidgetName.LOG_PREVIW_TEXT.name());
            gc.gridx = 1;
            gc.gridy = 1;
            gc.weightx = 1;
            previewPanel.add(previewText, gc);

            previewButton =
                    ComponentFactory.getIntelActionButton(previewAction);
            previewButton.setName(WidgetName.LOG_PREVIW.name());
            gc.gridx = 2;
            gc.gridy = 1;
            gc.weightx = 0;
            previewPanel.add(previewButton, gc);

            sampleStringLabel = ComponentFactory.getH5Label(
                    STLConstants.K0666_SAMPLE_FORMATTED.getValue(), Font.BOLD);
            gc.gridx = 0;
            gc.gridy = 2;
            gc.gridwidth = 1;
            gc.weightx = 0;
            previewPanel.add(sampleStringLabel, gc);

            sampleString = new JLabel("");
            sampleString.setName(WidgetName.LOG_PREVIW_OUTPUT.name());
            sampleString.setForeground(Color.RED);
            gc.gridx = 1;
            gc.gridy = 2;
            gc.gridwidth = 2;
            gc.weightx = 1;
            previewPanel.add(sampleString, gc);
        }
        return previewPanel;
    }

    protected JScrollPane getConversionPatternPanel() {
        if (conversionPatternPanel == null) {
            JTable table = getConversionCharacterTable();
            conversionPatternPanel = new JScrollPane(table);
            conversionPatternPanel.setBackground(UIConstants.INTEL_WHITE);
            conversionPatternPanel.setBorder(
                    new TitledBorder(BorderFactory.createEtchedBorder(),
                            STLConstants.K0663_CONVERSION_PATTERN.getValue(),
                            TitledBorder.LEADING, TitledBorder.TOP,
                            UIConstants.H4_FONT.deriveFont(Font.BOLD), null));
        }
        return conversionPatternPanel;
    }

    protected JTable getConversionCharacterTable() {
        if (conversionCharacterTable == null) {
            data[0][0] = STLConstants.K0649_SC.getValue();
            data[0][1] = UILabels.STL50002_DATA1.getDescription();

            data[1][0] = STLConstants.K0650_C.getValue();
            data[1][1] = UILabels.STL50003_DATA2.getDescription();

            data[2][0] = STLConstants.K0651_D.getValue();
            data[2][1] = UILabels.STL50004_DATA3.getDescription();

            data[3][0] = STLConstants.K0652_F.getValue();
            data[3][1] = UILabels.STL50005_DATA4.getDescription();

            data[4][0] = STLConstants.K0653_SL.getValue();
            data[4][1] = UILabels.STL50006_DATA5.getDescription();

            data[5][0] = STLConstants.K0654_L.getValue();
            data[5][1] = UILabels.STL50007_DATA6.getDescription();

            data[6][0] = STLConstants.K0655_SM.getValue();
            data[6][1] = UILabels.STL50008_DATA7.getDescription();

            data[7][0] = STLConstants.K0656_M.getValue();
            data[7][1] = UILabels.STL50009_DATA8.getDescription();

            data[8][0] = STLConstants.K0657_SN.getValue();
            data[8][1] = UILabels.STL50010_DATA9.getDescription();

            data[9][0] = STLConstants.K0658_SP.getValue();
            data[9][1] = UILabels.STL50011_DATA10.getDescription();

            data[10][0] = STLConstants.K0659_SR.getValue();
            data[10][1] = UILabels.STL50012_DATA11.getDescription();

            data[11][0] = STLConstants.K0660_ST.getValue();
            data[11][1] = UILabels.STL50013_DATA12.getDescription();

            data[12][0] = STLConstants.K0661_SX.getValue();
            data[12][1] = UILabels.STL50014_DATA13.getDescription();

            data[13][0] = STLConstants.K0662_DOUBLE_PERCENT.getValue();
            data[13][1] = UILabels.STL50015_DATA14.getDescription();

            conversionCharacterTable = ComponentFactory
                    .createIntelNonSortableSimpleTable(data, heading);
            conversionCharacterTable
                    .setBorder(BorderFactory.createEtchedBorder());
            conversionCharacterTable.setEnabled(false);

            packRows(conversionCharacterTable, 2);
            packCols(conversionCharacterTable, 2);
            conversionCharacterTable.setPreferredScrollableViewportSize(
                    conversionCharacterTable.getPreferredSize());
        }
        return conversionCharacterTable;
    }

    protected JPanel getButtonPanel() {
        if (buttonPannel == null) {
            buttonPannel = new JPanel();
            okButton = ComponentFactory.getIntelActionButton(okAction);
            okButton.setName(WidgetName.LOG_PREVIW_OK.name());
            buttonPannel.add(okButton);
            cancelButton = ComponentFactory.getIntelCancelButton(cancelAction);
            cancelButton.setName(WidgetName.LOG_PREVIW_CANCEL.name());
            buttonPannel.add(cancelButton);
        }
        return buttonPannel;
    }

    protected AbstractAction okAction =
            new AbstractAction(STLConstants.K0645_OK.getValue()) {

                private static final long serialVersionUID =
                        -6961800759728659264L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }

            };

    protected AbstractAction cancelAction =
            new AbstractAction(STLConstants.K0621_CANCEL.getValue()) {

                private static final long serialVersionUID =
                        9180757082471160424L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }

            };

    protected AbstractAction previewAction =
            new AbstractAction(STLConstants.K0664_PREVIEW.getValue()) {

                private static final long serialVersionUID =
                        8019247636030333301L;

                @Override
                public void actionPerformed(ActionEvent e) {
                    String sampleStr = preview();
                    sampleString.setText(sampleStr);
                }

            };

    protected String preview() {
        String s = previewText.getText().trim();
        StringBuilder ss = new StringBuilder(s);

        if (s.contains(STLConstants.K0652_F.getValue().trim())) {
            int index = ss.indexOf(STLConstants.K0652_F.getValue().trim());
            ss.replace(index, index + 2, "ConversionPatternHelpDialog.java");
        }
        if (s.contains(STLConstants.K0654_L.getValue().trim())) {
            int index = ss.indexOf(STLConstants.K0654_L.getValue().trim());
            ss.replace(index, index + 2, "38");
        }
        s = ss.toString();

        loggerContext.reset();
        logger.detachAndStopAllAppenders();

        PatternLayoutEncoder encoder = new PatternLayoutEncoder();
        encoder.setContext(loggerContext);
        encoder.setPattern(s);
        encoder.start();

        OutputStreamAppender<ILoggingEvent> appender =
                new OutputStreamAppender<ILoggingEvent>();
        appender.setContext(loggerContext);
        appender.setEncoder(encoder);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        appender.setOutputStream(out);
        appender.start();
        try {
            logger.addAppender(appender);
            logger.info(STLConstants.K0666_SAMPLE_FORMATTED.getValue());
        } finally {
            appender.stop();
        }
        return out.toString();
    }

    public int getPreferredRowHeight(JTable table, int rowIndex, int margin) {
        // Get the current default height for all rows
        int height = table.getRowHeight();

        // Determine highest cell in the row
        for (int c = 0; c < table.getColumnCount(); c++) {
            TableCellRenderer renderer = table.getCellRenderer(rowIndex, c);
            Component comp = table.prepareRenderer(renderer, rowIndex, c);
            int h = comp.getPreferredSize().height + 2 * margin;
            height = Math.max(height, h);
        }
        return height;
    }

    public int getPreferredColumnWidth(JTable table, int colIndex, int margin) {
        int width = 90;

        for (int r = 0; r < table.getRowCount(); r++) {
            TableCellRenderer renderer = table.getCellRenderer(r, colIndex);
            Component comp = table.prepareRenderer(renderer, r, colIndex);
            int w = comp.getPreferredSize().width + 2 * margin;
            width = Math.max(width, w);
        }
        return width;
    }

    public void packRows(JTable table, int margin) {
        packRows(table, 0, table.getRowCount(), margin);
    }

    public void packRows(JTable table, int start, int end, int margin) {
        for (int r = 0; r < table.getRowCount(); r++) {
            // Get the preferred height
            int h = getPreferredRowHeight(table, r, margin);

            // Now set the row height using the preferred height
            if (table.getRowHeight(r) != h) {
                table.setRowHeight(r, h);
            }
        }
    }

    public void packCols(JTable table, int margin) {
        TableColumn column = null;
        for (int i = 0; i < table.getColumnCount(); i++) {
            column = table.getColumnModel().getColumn(i);
            int w = getPreferredColumnWidth(table, i, margin);
            column.setPreferredWidth(w);
        }
    }
}
