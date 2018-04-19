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

package com.intel.stl.ui.admin.view.logs;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import com.intel.stl.api.logs.LogConfigType;
import com.intel.stl.ui.admin.impl.SMLogModel;
import com.intel.stl.ui.admin.impl.logs.FilterType;
import com.intel.stl.ui.admin.impl.logs.ILogController;
import com.intel.stl.ui.admin.impl.logs.ILogViewListener;
import com.intel.stl.ui.admin.impl.logs.SearchKey;
import com.intel.stl.ui.admin.impl.logs.SearchPositionBean;
import com.intel.stl.ui.admin.impl.logs.SearchState;
import com.intel.stl.ui.admin.impl.logs.TextEventType;
import com.intel.stl.ui.admin.view.ILoginListener;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.SafeNumberField;
import com.intel.stl.ui.console.LoginBean;
import com.intel.stl.ui.main.HelpAction;

public abstract class AbstractLogView extends JPanel {

    private static final long serialVersionUID = 5538100358303437466L;

    private final long MIN_NUM_LINES = 1;

    private final long MAX_NUM_LINES = 1000;

    private SMLoginView smLoginView;

    protected JPanel pnlRefresh;

    private JButton btnHelp;

    private JButton btnRefresh;

    private JLabel lblRefreshRunning;

    private SafeNumberField<Long> txtFldLinesPerPage;

    private JComboBox<Long> cboxLinesPerPageValue;

    private JLabel lblTotalLinesValue;

    private JLabel lblStartLineValue;

    private JLabel lblEndLineValue;

    private JLabel lblRangeDelimiter;

    private JLabel lblFileNameValue;

    private JButton btnPrevious;

    private JButton btnNext;

    private JTextField txtfldSearch;

    protected String lastSearchKey;

    private JButton btnSearch;

    private JButton btnCancelSearch;

    private final CardLayout cardLayout = new CardLayout();

    protected ILogViewListener logViewListener;

    private ILogController logController;

    private long numLinesRequested = 100;

    private JCheckBox chkboxSM;

    private JCheckBox chkboxPM;

    private JCheckBox chkboxFE;

    private JCheckBox chkboxWarnings;

    private JCheckBox chkboxErrors;

    private final List<JCheckBox> chkboxFilterList = new ArrayList<JCheckBox>();

    private JLabel lblPageRunning;

    protected JTextComponent textContent;

    private int numLineIndex;

    private DocumentListener numLineListener;

    private JButton[] buttons;

    private boolean[] lastButtonState;

    private JLabel lblNumMatchesValue;

    protected TextMenuPanel pnlSearchMenu;

    private boolean enableUserActions;

    protected ILoginListener loginListener;

    private String currentCardName;

    public AbstractLogView() {
        super();
        createNumLineListener();
        initComponent();
    }

    protected void createNumLineListener() {
        numLineListener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                numLineIndex = cboxLinesPerPageValue.getSelectedIndex();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                numLineIndex = cboxLinesPerPageValue.getSelectedIndex();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                numLineIndex = cboxLinesPerPageValue.getSelectedIndex();
            }
        };
    }

    protected void initComponent() {
        setLayout(cardLayout);

        JPanel pnlLogCard = new JPanel();
        pnlLogCard.setLayout(new BorderLayout(5, 5));
        pnlLogCard.setOpaque(false);
        pnlLogCard
                .setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                UIConstants.INTEL_BORDER_GRAY, 1, true),
                        BorderFactory.createEmptyBorder(2, 5, 2, 2)));

        // Main panel
        JPanel pnlMain = createMainPanel();

        // Add the panels for the log view
        pnlLogCard.add(pnlMain, BorderLayout.NORTH);
        pnlLogCard.add(getMainComponent(), BorderLayout.CENTER);
        add(pnlLogCard, LogViewType.SM_LOG.getValue());

        // Login Panel
        JPanel pnlLoginCard = new JPanel(new FlowLayout());
        smLoginView = new SMLoginView();
        pnlLoginCard.setBackground(UIConstants.INTEL_WHITE);
        if (smLoginView != null) {
            smLoginView.enableForm(false);
        }
        pnlLoginCard.add(smLoginView);

        // Add the login and log view panels to the card layout
        add(pnlLoginCard, LogViewType.LOGIN.getValue());

        // Initialize last button states
        buttons = new JButton[] { btnRefresh, btnNext, btnPrevious };
        lastButtonState = new boolean[] { btnRefresh.isEnabled(),
                btnNext.isEnabled(), btnPrevious.isEnabled() };

        textContent = getTextContent();
        textContent.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() > 1) {
                    String selected = textContent.getSelectedText();
                    if (selected != null && !selected.isEmpty()) {
                        txtfldSearch.setText(selected);
                        logViewListener.onSearch(SearchState.MARKED_SEARCH);
                    }
                }
            }
        });

        // Initialize the Help button
        HelpAction helpAction = HelpAction.getInstance();
        helpAction.getHelpBroker().enableHelpOnButton(btnHelp,
                helpAction.getAdminLogViewer(), helpAction.getHelpSet());
    }

    protected JPanel createMainPanel() {
        // Main Panel
        JPanel pnlMain = new JPanel();
        pnlMain.setLayout(new BorderLayout());

        JPanel pnlControl = createControlPanel();

        JPanel pnlStatus = createStatusPanel();

        // Add the control and status panels to the main panel
        pnlMain.add(pnlControl, BorderLayout.NORTH);
        pnlMain.add(pnlStatus, BorderLayout.SOUTH);

        return pnlMain;
    }

    protected JPanel createControlPanel() {

        JPanel pnlControl = new JPanel();
        pnlControl.setBackground(UIConstants.INTEL_WHITE);
        pnlControl.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.insets = new Insets(2, 5, 2, 5);
        gc.weightx = 1;
        gc.gridwidth = 1;

        // Filters Panel
        JPanel pnlFilters = createFiltersPanel();
        pnlControl.add(pnlFilters, gc);

        // File Control Panel
        pnlRefresh = createRefreshPanel();
        pnlControl.add(pnlRefresh, gc);

        // Search Panel
        JPanel pnlSearch = createSearchPanel();
        pnlControl.add(pnlSearch, gc);

        gc.weightx = 0;
        // Navigation Panel
        JPanel pnlNav = createNavPanel();
        pnlControl.add(pnlNav, gc);

        // Configure Button
        JButton btnConfig = ComponentFactory
                .getImageButton(UIImages.SETTING_ICON.getImageIcon());
        btnConfig.setToolTipText(
                STLConstants.K2166_CONFIGURE_LOG_HOST.getValue());
        btnConfig.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                logViewListener.onConfigure();
            }
        });
        btnConfig.setName(WidgetName.ADMIN_LOGS_CONFIGURE_BUTTON.name());
        pnlControl.add(btnConfig, gc);

        // Help Button
        btnHelp = ComponentFactory
                .getImageButton(UIImages.HELP_ICON.getImageIcon());
        btnHelp.setToolTipText(STLConstants.K0037_HELP.getValue());
        btnHelp.setName(WidgetName.ADMIN_LOGS_HELP_BUTTON.name());
        pnlControl.add(btnHelp);

        return pnlControl;
    }

    protected JPanel createStatusPanel() {
        // Status Panel
        JPanel pnlStatus = new JPanel();
        pnlStatus.setBackground(UIConstants.INTEL_WHITE);
        pnlStatus.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 5, 5, 5);
        gc.weightx = 1;
        gc.gridwidth = 1;

        // File Name
        gc.anchor = GridBagConstraints.WEST;
        JPanel pnlFileName = createFileNamePanel();
        pnlStatus.add(pnlFileName, gc);

        // Total Lines
        gc.anchor = GridBagConstraints.CENTER;
        JPanel pnlTotalLines = createTotalLinesPanel();
        pnlStatus.add(pnlTotalLines, gc);

        // # Matches
        JPanel pnlNumMatches = createNumMatchesPanel();
        pnlStatus.add(pnlNumMatches, gc);

        // Line Range
        gc.anchor = GridBagConstraints.EAST;
        JPanel pnlLineRange = createLineRangePanel();
        pnlStatus.add(pnlLineRange, gc);

        return pnlStatus;
    }

    protected JPanel createFileNamePanel() {
        JPanel pnlStatus = new JPanel();
        pnlStatus.setBackground(UIConstants.INTEL_WHITE);
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));

        // File Name Label
        JLabel lblFileName = ComponentFactory
                .getFieldLabel(STLConstants.K2154_FILE_NAME.getValue() + ":");
        lblFileName.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        lblFileName.setName(WidgetName.ADMIN_LOGS_FILE_NAME_LABEL.name());
        pnlStatus.add(lblFileName);

        // File Name Value
        lblFileNameValue = ComponentFactory.deriveLabel(
                ComponentFactory.getH6Label("", Font.PLAIN), false, 200);
        lblFileNameValue.setName(WidgetName.ADMIN_LOGS_FILE_NAME_VALUE.name());
        pnlStatus.add(lblFileNameValue);

        return pnlStatus;
    }

    protected JPanel createTotalLinesPanel() {

        JPanel pnlTotalLines = new JPanel();
        pnlTotalLines.setBackground(UIConstants.INTEL_WHITE);
        pnlTotalLines.setLayout(new BoxLayout(pnlTotalLines, BoxLayout.X_AXIS));

        // Total Lines Label
        JLabel lblTotalLines = ComponentFactory
                .getFieldLabel(STLConstants.K2151_TOTAL_LINES.getValue() + ":");
        lblTotalLines.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        lblTotalLines.setName(WidgetName.ADMIN_LOGS_TOTAL_LINES_LABEL.name());
        pnlTotalLines.add(lblTotalLines);

        // Total Lines Value
        lblTotalLinesValue = ComponentFactory.getH6Label("", Font.PLAIN);
        lblTotalLinesValue
                .setName(WidgetName.ADMIN_LOGS_TOTAL_LINES_VALUE.name());
        pnlTotalLines.add(lblTotalLinesValue);

        return pnlTotalLines;
    }

    protected JPanel createNumMatchesPanel() {

        JPanel pnlNumMatches = new JPanel();
        pnlNumMatches.setBackground(UIConstants.INTEL_WHITE);
        pnlNumMatches.setLayout(new BoxLayout(pnlNumMatches, BoxLayout.X_AXIS));

        // # Matches Label
        JLabel lblNumMatches = ComponentFactory
                .getFieldLabel(STLConstants.K2158_NUM_MATCHES.getValue() + ":");
        lblNumMatches.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        lblNumMatches.setName(WidgetName.ADMIN_LOGS_NUM_MATCHES_LABEL.name());
        pnlNumMatches.add(lblNumMatches);

        // # Matches Value
        lblNumMatchesValue = ComponentFactory.getH6Label("", Font.PLAIN);
        lblNumMatchesValue
                .setName(WidgetName.ADMIN_LOGS_NUM_MATCHES_VALUE.name());
        pnlNumMatches.add(lblNumMatchesValue);

        return pnlNumMatches;
    }

    protected JPanel createLineRangePanel() {

        JPanel pnlLineRange = new JPanel();
        pnlLineRange.setBackground(UIConstants.INTEL_WHITE);
        pnlLineRange.setLayout(new BoxLayout(pnlLineRange, BoxLayout.X_AXIS));

        // Line Range Label
        JLabel lblLineRange = ComponentFactory
                .getFieldLabel(STLConstants.K2155_LINE_RANGE.getValue() + ":");
        lblLineRange.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        lblLineRange.setName(WidgetName.ADMIN_LOGS_LINE_RANGE_LABEL.name());
        pnlLineRange.add(lblLineRange);

        // Line Range Value
        lblStartLineValue = ComponentFactory.getH6Label("", Font.PLAIN);
        lblStartLineValue
                .setName(WidgetName.ADMIN_LOGS_LINE_RANGE_START.name());
        lblRangeDelimiter = ComponentFactory.getH6Label(
                STLConstants.K2156_RANGE_DELIMITER.getValue(), Font.PLAIN);
        lblEndLineValue = ComponentFactory.getH6Label("", Font.PLAIN);
        lblEndLineValue.setName(WidgetName.ADMIN_LOGS_LINE_RANGE_END.name());
        pnlLineRange.add(lblStartLineValue);
        pnlLineRange.add(lblRangeDelimiter);
        pnlLineRange.add(lblEndLineValue);

        return pnlLineRange;
    }

    protected JPanel createFiltersPanel() {
        // Filters Panel
        JPanel pnlFilters = new JPanel();
        pnlFilters
                .setToolTipText(STLConstants.K2170_SHOW_SELECTIONS.getValue());
        pnlFilters.setBackground(UIConstants.INTEL_WHITE);
        pnlFilters.setBorder(BorderFactory
                .createTitledBorder(STLConstants.K2147_FILTERS.getValue()));
        pnlFilters.setLayout(new BoxLayout(pnlFilters, BoxLayout.X_AXIS));
        chkboxSM = ComponentFactory.getIntelCheckBox(FilterType.SM.getName());
        chkboxSM.setName(WidgetName.ADMIN_LOGS_FILTERS_SM.name());
        chkboxFilterList.add(chkboxSM);
        chkboxPM = ComponentFactory.getIntelCheckBox(FilterType.PM.getName());
        chkboxPM.setName(WidgetName.ADMIN_LOGS_FILTERS_PM.name());
        chkboxFilterList.add(chkboxPM);
        chkboxFE = ComponentFactory.getIntelCheckBox(FilterType.FE.getName());
        chkboxFE.setName(WidgetName.ADMIN_LOGS_FILTERS_FE.name());
        chkboxFilterList.add(chkboxFE);
        chkboxWarnings = ComponentFactory
                .getIntelCheckBox(FilterType.WARNINGS.getName());
        chkboxWarnings.setName(WidgetName.ADMIN_LOGS_FILTERS_WARN.name());
        chkboxFilterList.add(chkboxWarnings);
        chkboxErrors =
                ComponentFactory.getIntelCheckBox(FilterType.ERRORS.getName());
        chkboxErrors.setName(WidgetName.ADMIN_LOGS_FILTERS_ERROR.name());
        chkboxFilterList.add(chkboxErrors);
        for (JCheckBox chkbox : chkboxFilterList) {
            chkbox.setSelected(false);
            chkbox.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    logViewListener.onFilter();
                }
            });
            pnlFilters.add(chkbox);
        }

        return pnlFilters;
    }

    protected JPanel createRefreshPanel() {
        // Refresh Panel
        JPanel pnlRefresh = new JPanel();
        pnlRefresh.setBackground(UIConstants.INTEL_WHITE);
        pnlRefresh.setBorder(BorderFactory
                .createTitledBorder(STLConstants.K0107_REFRESH.getValue()));
        pnlRefresh.setLayout(new BoxLayout(pnlRefresh, BoxLayout.X_AXIS));
        JLabel lblLinesPerPage = ComponentFactory.getFieldLabel(
                STLConstants.K2150_LINES_PER_PAGE.getValue() + ":");
        lblLinesPerPage.setAlignmentX(SwingConstants.RIGHT);
        lblLinesPerPage.setName(WidgetName.ADMIN_LOGS_LPP_LABEL.name());
        pnlRefresh.setMaximumSize(new Dimension(225, 44));
        pnlRefresh.add(lblLinesPerPage);

        txtFldLinesPerPage = new SafeNumberField<Long>(new DecimalFormat("###"),
                MIN_NUM_LINES, true, MAX_NUM_LINES, true);
        txtFldLinesPerPage.setValidChars(UIConstants.DIGITS);
        txtFldLinesPerPage.setColumns(10);
        txtFldLinesPerPage.setName(WidgetName.ADMIN_LOGS_LPP_TEXT_FIELD.name());
        cboxLinesPerPageValue = ComponentFactory.createComboBox(
                new Long[] { 100L, 300L, 500L, 1000L }, txtFldLinesPerPage,
                numLineListener);
        cboxLinesPerPageValue.setEditable(true);
        cboxLinesPerPageValue.getEditor().getEditorComponent()
                .addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                            onEndOfPage();
                        }
                    }
                });
        cboxLinesPerPageValue
                .setName(WidgetName.ADMIN_LOGS_LPP_COMBO_BOX.name());

        // Add the refresh button to the refresh panel
        btnRefresh = ComponentFactory
                .getImageButton(UIImages.REFRESH.getImageIcon());
        btnRefresh.setToolTipText(STLConstants.K0107_REFRESH.getValue());
        btnRefresh.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onEndOfPage();
            }
        });
        btnRefresh.setName(WidgetName.ADMIN_LOGS_LPP_REFRESH_BUTTON.name());

        // Create running icon next to the refresh panel. The initial icon is
        // set to INVISIBLE and then changed to RUNNING when necessary. This
        // makes it possible to display an icon without shifting widgets to
        // the right
        lblRefreshRunning = new JLabel();
        lblRefreshRunning.setIcon(UIImages.INVISIBLE.getImageIcon());
        lblRefreshRunning
                .setName(WidgetName.ADMIN_LOGS_LPP_REFRESH_RUNNING.name());

        pnlRefresh.add(Box.createHorizontalStrut(5));
        pnlRefresh.add(cboxLinesPerPageValue);
        pnlRefresh.add(Box.createHorizontalStrut(5));
        pnlRefresh.add(btnRefresh);
        pnlRefresh.add(lblRefreshRunning);

        return pnlRefresh;
    }

    protected JPanel createSearchPanel() {
        // SearchPanel
        JPanel pnlSearch = new JPanel();
        pnlSearch.setBackground(UIConstants.INTEL_WHITE);
        pnlSearch.setBorder(BorderFactory
                .createTitledBorder(STLConstants.K2153_SEARCH.getValue()));
        pnlSearch.setLayout(new BoxLayout(pnlSearch, BoxLayout.X_AXIS));
        pnlSearch.setMaximumSize(new Dimension(250, 44));

        // Add a text box to the Search panel
        txtfldSearch = new JTextField();
        txtfldSearch.setPreferredSize(new Dimension(200, 10));
        txtfldSearch.setMinimumSize(txtfldSearch.getPreferredSize());
        txtfldSearch.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == KeyEvent.VK_ENTER) {
                    logViewListener.onSearch(SearchState.STANDARD_SEARCH);
                }
            }
        });

        List<TextEventType> eventTypes = new ArrayList<TextEventType>(
                Arrays.asList(TextEventType.PASTE));
        pnlSearchMenu = new TextMenuPanel(eventTypes);
        txtfldSearch.addMouseListener(pnlSearchMenu);
        txtfldSearch.setName(WidgetName.ADMIN_LOGS_SEARCH_TEXT_FIELD.name());
        pnlSearch.add(txtfldSearch);

        // Add a button to the Search panel
        btnSearch =
                ComponentFactory.getImageButton(UIImages.SEARCH.getImageIcon());
        btnSearch.setToolTipText(STLConstants.K2153_SEARCH.getValue());
        btnSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                logViewListener.onSearch(SearchState.STANDARD_SEARCH);
            }
        });
        btnSearch.setName(WidgetName.ADMIN_LOGS_SEARCH_BUTTON.name());
        pnlSearch.add(btnSearch);

        // Add a cancel button to the Search panel
        btnCancelSearch = ComponentFactory
                .getImageButton(UIImages.CLOSE_GRAY.getImageIcon());
        btnCancelSearch
                .setToolTipText(STLConstants.K2169_CLEAR_SEARCH.getValue());
        btnCancelSearch.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // Clear the search text field and let the document listener
                // handle the rest
                txtfldSearch.setText("");
            }

        });
        btnCancelSearch
                .setName(WidgetName.ADMIN_LOGS_SEARCH_CANCEL_BUTTON.name());
        pnlSearch.add(btnCancelSearch);

        return pnlSearch;
    }

    public void enableSearch(boolean b) {
        btnSearch.setSelected(true);
        btnSearch.setEnabled(b);
    }

    public void enableControlPanel(boolean b) {
        cboxLinesPerPageValue.setEnabled(b);

        for (JCheckBox chkbox : chkboxFilterList) {
            chkbox.setEnabled(b);
        }
    }

    public String getSearchKey() {
        return txtfldSearch.getText();
    }

    public void setSearchField(String value) {
        txtfldSearch.setText(value);
    }

    public String getLastSearchKey() {
        return lastSearchKey;
    }

    public void saveLastSearchKey() {
        lastSearchKey = getSearchKey();
    }

    public void resetSearchField() {
        txtfldSearch.setText("");
    }

    public void resetLogin() {
        smLoginView.resetLogin();
    }

    public String getDocument() {
        // The search field is a JTextField which cannot contain carriage
        // returns. So in order to match multiple lines in the search field,
        // replace all carriage returns in the textContent with blanks, and
        // trim off the blank space at the beginning/end of the string.
        return textContent.getText().replaceAll("\\n", " ").trim();
    }

    protected JPanel createNavPanel() {
        // Navigation Panel
        JPanel pnlNav = new JPanel();
        pnlNav.setBackground(UIConstants.INTEL_WHITE);
        pnlNav.setPreferredSize(new Dimension(100, 20));
        pnlNav.setMinimumSize(pnlNav.getPreferredSize());
        pnlNav.setLayout(new BoxLayout(pnlNav, BoxLayout.X_AXIS));

        // Add the previous button to the Nav panel
        btnPrevious =
                ComponentFactory.getIntelActionButton(new AbstractAction() {
                    private static final long serialVersionUID =
                            -4779867622071856085L;

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        setPageRunningVisible(true);
                        disableUserActions();
                        logViewListener.onPrevious(numLinesRequested);
                    }
                });
        btnPrevious.setToolTipText(STLConstants.K2168_PREVIOUS.getValue());
        btnPrevious.setIcon(UIImages.BACK_WHITE_ICON.getImageIcon());
        btnPrevious.setName(WidgetName.ADMIN_LOGS_PREVIOUS_BUTTON.name());
        pnlNav.add(btnPrevious);

        // Put a horizontal gap between the buttons
        pnlNav.add(Box.createHorizontalStrut(5));

        // Add the next button to the Nav panel
        btnNext = ComponentFactory.getIntelActionButton(new AbstractAction() {
            private static final long serialVersionUID = 905214147006236116L;

            @Override
            public void actionPerformed(ActionEvent e) {
                setPageRunningVisible(true);
                disableUserActions();
                logViewListener.onNext(numLinesRequested);
            }
        });
        btnNext.setToolTipText(STLConstants.K0622_NEXT.getValue());

        // Last page is up first, so disable this button
        btnNext.setEnabled(false);
        btnNext.setIcon(UIImages.FORWARD_WHITE_ICON.getImageIcon());
        btnNext.setName(WidgetName.ADMIN_LOGS_NEXT_BUTTON.name());
        pnlNav.add(btnNext);

        // Add a Running icon for paging. The initial icon is set to INVISIBLE
        // and then changed to RUNNING when necessary. This makes it possible to
        // display an icon without shifting widgets to the right
        lblPageRunning = new JLabel();
        lblPageRunning.setIcon(UIImages.INVISIBLE.getImageIcon());
        lblPageRunning.setName(WidgetName.ADMIN_LOGS_PAGE_RUNNING.name());
        pnlNav.add(lblPageRunning);

        return pnlNav;
    }

    public void onEndOfPage() {
        if (!txtFldLinesPerPage.isEditValid()) {
            txtFldLinesPerPage.requestFocusInWindow();
            return;
        }

        numLinesRequested = (long) cboxLinesPerPageValue.getEditor().getItem();

        boolean ok = checkNumLines(numLinesRequested);
        if (ok) {
            // Refresh the log
            disableUserActions();
            setRefreshRunningVisible(true);
            logViewListener.onLastLines(numLinesRequested);
        } else {
            Util.showErrorMessage(pnlRefresh,
                    UILabels.STL50213_LINES_PER_PAGE_ERROR.getDescription(
                            numLinesRequested, MIN_NUM_LINES, MAX_NUM_LINES));
            cboxLinesPerPageValue.setSelectedIndex(numLineIndex);
        }

        // Prevent duplicate entries
        if (ok && ((DefaultComboBoxModel<Long>) cboxLinesPerPageValue
                .getModel()).getIndexOf(numLinesRequested) == -1) {
            cboxLinesPerPageValue.addItem(numLinesRequested);
            cboxLinesPerPageValue.setSelectedItem(numLinesRequested);

        }
    }

    protected boolean checkNumLines(long numLines) {
        return ((MIN_NUM_LINES <= numLines) && (numLines <= MAX_NUM_LINES));
    }

    public void setPageRunningVisible(boolean b) {
        if (b) {
            lblPageRunning.setIcon(UIImages.RUNNING.getImageIcon());
        } else {
            lblPageRunning.setIcon(UIImages.INVISIBLE.getImageIcon());
        }
    }

    public void setRefreshRunningVisible(boolean b) {
        if (b) {
            lblRefreshRunning.setIcon(UIImages.RUNNING.getImageIcon());
        } else {
            lblRefreshRunning.setIcon(UIImages.INVISIBLE.getImageIcon());
        }
    }

    public List<FilterType> getSelectedFilters() {

        List<FilterType> filters = new ArrayList<FilterType>();
        for (JCheckBox chkbox : chkboxFilterList) {
            if (chkbox.isSelected()) {
                filters.add(FilterType.getFilter(chkbox.getText()));
            }
        }
        return filters;
    }

    public void updateLogView(final SMLogModel model) {

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                showFileName(model.getLogFilePath());
                showLogEntry(model.getFilteredDoc());
                showTotalLines(model);
                showLineRange(model.getStartLine(), model.getEndLine());

                if (model.getLogMsg() != null) {
                    logViewListener.onFilter();
                }

                setPageRunningVisible(false);
                setRefreshRunningVisible(false);
            }
        });
    }

    public void updateLoginView(final SMLogModel model) {
        smLoginView.updateView(model);
    }

    public synchronized void setNextEnabled(boolean b) {
        if (enableUserActions) {
            btnNext.setEnabled(b);
        }
    }

    public synchronized void setPreviousEnabled(boolean b) {
        if (enableUserActions) {
            btnPrevious.setEnabled(b);
        }
    }

    public void showTotalLines(final SMLogModel model) {
        if (hasRunningIcon()) {
            setNumLineIcon(false);
        }
        lblTotalLinesValue.setText(String.valueOf(model.getNumLines()));
    }

    public void showFileName(final String fileName) {
        lblFileNameValue.setText(fileName);
        lblFileNameValue.setToolTipText(fileName);
    }

    public void showLineRange(long startLine, long endLine) {
        if ((startLine > 0) && (endLine > 0)) {
            lblStartLineValue.setText(String.valueOf(startLine));
            lblEndLineValue.setText(String.valueOf(endLine));
        }
    }

    public void showNumMatches(long matches) {
        lblNumMatchesValue.setText(String.valueOf(matches));
    }

    public long getNumLinesRequested() {
        return numLinesRequested;
    }

    public void setLoginListener(ILoginListener listener) {
        this.loginListener = listener;
        smLoginView.setListener(listener);
    }

    public void setLogController(ILogController controller) {
        logController = controller;
    }

    public void setLogViewListener(ILogViewListener listener) {
        logViewListener = listener;
        smLoginView.setLoginViewListener(listener);

        // Add the document listener to the search text field
        txtfldSearch.getDocument()
                .addDocumentListener(logController.getDocumentListener());
    }

    public void setView(String name) {
        cardLayout.show(this, name);
        currentCardName = name;
    }

    public boolean isLoginView() {
        return (currentCardName == LogViewType.LOGIN.getValue());
    }

    public boolean isSmLogView() {
        return (currentCardName == LogViewType.SM_LOG.getValue());
    }

    public void showLogView() {
        smLoginView.showProgress(false);
        setView(LogViewType.SM_LOG.getValue());
    }

    public void showLoginView() {
        smLoginView.showProgress(false);
        setView(LogViewType.LOGIN.getValue());
    }

    /**
     * <i>Description:</i>
     *
     * @param b
     */
    public void setLoginEnabled(boolean b) {
        smLoginView.setEnabled(b);
    }

    public void showProgress(boolean show) {
        smLoginView.showProgress(show);
    }

    public void showError(String message) {
        smLoginView.setMessage(message);
    }

    public void showErrorDialog(String message) {
        Util.showErrorMessage(smLoginView, message);
    }

    public void clearLoginData() {
        smLoginView.clearLoginData();
        smLoginView.setMessage("");
        smLoginView.repaint();
    }

    public LoginBean getCredentials() {
        return smLoginView.getCredentials();
    }

    public String getLogFilePath() {
        return smLoginView.getLogFilePath();
    }

    public void setCredentials(LoginBean credentials) {
        smLoginView.setHostNameField(credentials.getHostName());
        smLoginView.setPortNumber(credentials.getPortNum());
        smLoginView.setUserNameField(credentials.getUserName());
    }

    public void setNumLineIcon(boolean b) {
        if (b) {
            lblTotalLinesValue.setIcon(UIImages.RUNNING.getImageIcon());
        } else {
            lblTotalLinesValue.setIcon(null);
        }
    }

    public boolean hasRunningIcon() {
        return (lblTotalLinesValue.getIcon() != null);
    }

    public synchronized void disableUserActions() {

        // Disabling user actions prevents any updates to the paging buttons
        // enables from the back end
        enableUserActions = false;

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                // Save the current state of each button and then disable
                for (int i = 0; i < buttons.length; i++) {
                    lastButtonState[i] = buttons[i].isEnabled();
                    buttons[i].setEnabled(false);
                }
            }
        });
    }

    public synchronized void restoreUserActions(final boolean isFirstPage,
            final boolean isLastPage) {

        // Allow updates to the paging button enables from the back end
        enableUserActions = true;

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                // Restore all buttons to their original state
                for (int i = 0; i < buttons.length; i++) {
                    buttons[i].setEnabled(lastButtonState[i]);
                }

                // Now check whether this is the first or last page and set
                // the button enables accordingly
                setPreviousEnabled(!isFirstPage);
                setNextEnabled(!isLastPage);
            }
        });
    }

    public LogConfigType getConfigType() {
        return smLoginView.getConfigType().getType();
    }

    abstract Component getMainComponent();

    abstract JTextComponent getTextContent();

    abstract public void showLogEntry(List<String> entries);

    abstract public void highlightText(List<SearchKey> searchKeys,
            List<SearchPositionBean> searchResults, SearchState searchState);

    abstract public void moveToText(int start, int end);
}
