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

package com.intel.stl.ui.wizards.view;

import static com.intel.stl.ui.common.STLConstants.K0622_NEXT;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.VerticalLayout;

import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.DocumentDirtyListener;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.ExFormattedTextField;
import com.intel.stl.ui.common.view.IntelButtonUI;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;
import com.intel.stl.ui.common.view.ProgressPanel;
import com.intel.stl.ui.common.view.RoundedCornersBorder;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.wizards.impl.ConfigTaskStatus;
import com.intel.stl.ui.wizards.impl.ConfigTaskType;
import com.intel.stl.ui.wizards.impl.IMultinetWizardListener;
import com.intel.stl.ui.wizards.impl.IMultinetWizardTask;
import com.intel.stl.ui.wizards.impl.IWizardListener;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;

public class MultinetWizardView extends JDialog implements IMultinetWizardView {

    private static final long serialVersionUID = 6898168781808453167L;

    public static final Color CONTENT_COLOR = UIConstants.INTEL_WHITE;

    public static final Color WIZARD_COLOR = UIConstants.INTEL_WHITE;

    public static final int DIALOG_WIDTH = 800;

    public static final int DIALOG_HEIGHT = 500;

    private static final String CLIENT_KEY = "SUBNET";

    private final int MAX_SUBNET_NAME_LEN = 56;

    private IMultinetWizardListener wizardListener;

    private JPanel pnlNavigation;

    private JPanel pnlNavigationButtons;

    private JPanel pnlSubwizard;

    private JPanel pnlMainCtrl;

    private JButton btnAddSubnet;

    private JButton btnDeleteSubnet;

    private JButton btnApply;

    private JButton btnReset;

    private JButton btnPrevious;

    private JButton btnNext;

    private JButton btnRun;

    private JButton btnClose;

    private JButton helpBtn;

    private JTabbedPane tabbedPane;

    private JPanel pnlMain;

    private JPanel pnlHeading;

    private JPanel pnlSubwizardCtrl;

    private JPanel pnlExistingSubnet;

    private ExFormattedTextField txtFldSubnetName;

    private Border originalSubnetNameBorder;

    private boolean nextSelected = false;

    private boolean subnetBtnSelected = false;

    private JButton btnSelected;

    private boolean manualSelect = false;

    private boolean newWizardInProgress = false;

    private final CardLayout wizardLayout = new CardLayout();

    private final CardLayout controlLayout = new CardLayout();

    private final CardLayout welcomeLayout = new CardLayout();

    private JPanel pnlWelcomeContent;

    private ProgressPanel progressPanel;

    private JLabel lblEntryValidation;

    private JLabel lblEntryValidationIcon;

    private JLabel lblEntryValidationStatus;

    private JLabel lblEntryValidationNotes;

    private JLabel lblHostReachability;

    private JLabel lblHostReachabilityIcon;

    private JLabel lblHostReachabilityStatus;

    private JLabel lblHostReachabilityNotes;

    private JLabel lblDatabaseUpdate;

    private JLabel lblDatabaseUpdateIcon;

    private JLabel lblDatabaseUpdateStatus;

    private JLabel lblDatabaseUpdateNotes;

    private JButton btnWelcomeOk;

    private JLabel lblWelcomeError;

    // when we are deleting we need to check whether edit is valid or not
    private boolean ignoreEditCheck;

    private WizardViewType wizardViewType;

    private boolean dirty;

    /**
     *
     * Description: Constructor for the MultinetWizardView
     *
     * @param owner
     *            top level window of the application to center this dialog
     *            around
     */
    public MultinetWizardView(IFabricView owner) {

        super((JFrame) owner, STLConstants.K0667_CONFIG_SETUP_WIZARD.getValue(),
                true);

        initComponents();

        // Ignore the default close operation and add a custom window listener
        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {

            /*
             * (non-Javadoc)
             *
             * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.
             * WindowEvent )
             */
            @Override
            public void windowClosing(WindowEvent e) {
                onClose(false);
            }
        });
    }

    /**
     *
     * <i>Description: Initializes the GUI components for this view</i>
     *
     */
    protected void initComponents() {
        // Dialog is not resizable
        this.setResizable(false);

        // Configure the layout of the content pane
        JPanel pnlContent = (JPanel) getContentPane();
        pnlContent.setLayout(new BorderLayout(5, 0));
        pnlContent.setBorder(BorderFactory
                .createLineBorder(UIConstants.INTEL_BORDER_GRAY, 2, true));
        pnlContent.setPreferredSize(new Dimension(DIALOG_WIDTH, DIALOG_HEIGHT));

        // Create the subnet navigation panel
        JPanel pnlNavigation = getNavigationPanel();
        pnlContent.add(pnlNavigation, BorderLayout.WEST);

        // Create a main panel on the right side to hold the wizard views and
        // the main control panel
        JPanel pnlMain = getConfPanel();
        pnlContent.add(pnlMain, BorderLayout.CENTER);

        // Add the main control panel
        JPanel pnlMainCtrl = getControlPanel();
        pnlContent.add(pnlMainCtrl, BorderLayout.SOUTH);
    }

    /**
     *
     * <i>Description:</i> the subnet navigation panel
     *
     * @return
     */
    protected JPanel getNavigationPanel() {
        if (pnlNavigation == null) {
            pnlNavigation = new JPanel(new BorderLayout(5, 5));
            pnlNavigation.setBackground(UIConstants.INTEL_WHITE);
            pnlNavigation.setBorder(new RoundedCornersBorder(
                    UIConstants.INTEL_BORDER_GRAY, 2, 5));

            // Create the head panel to hold the "Subnets" title
            JLabel pnlNavigationHeader = ComponentFactory.getH4Label(
                    STLConstants.K3013_SUBNETS.getValue(), Font.BOLD);
            pnlNavigationHeader.setHorizontalAlignment(JLabel.CENTER);
            pnlNavigationHeader.setBorder(BorderFactory.createMatteBorder(0, 0,
                    2, 0, UIConstants.INTEL_ORANGE));
            pnlNavigation.add(pnlNavigationHeader, BorderLayout.NORTH);

            // Create the Navigation Button Panel
            pnlNavigationButtons = new JPanel(new VerticalLayout(2));
            pnlNavigationButtons.setName(WidgetName.SW_SUBNETS.name());

            // Create the navigation panel on a scroll pane with a minor tweak
            // to
            // scrollbar widths w/out creating a new ScrollBarUI
            JScrollPane scrpnNavigation = new JScrollPane(pnlNavigationButtons,
                    JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                    JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            scrpnNavigation.setBorder(null);
            scrpnNavigation.getVerticalScrollBar()
                    .setPreferredSize(new Dimension(10, 0));
            scrpnNavigation.getHorizontalScrollBar()
                    .setPreferredSize(new Dimension(0, 10));
            scrpnNavigation.setPreferredSize(
                    new Dimension((int) (DIALOG_WIDTH * .2f), DIALOG_HEIGHT));
            pnlNavigation.add(scrpnNavigation, BorderLayout.CENTER);
        }
        return pnlNavigation;
    }

    protected JPanel getConfPanel() {
        if (pnlMain == null) {
            pnlMain = new JPanel(wizardLayout);
            pnlMain.setBorder(BorderFactory
                    .createLineBorder(UIConstants.INTEL_BORDER_GRAY, 2, true));
            JPanel wizardPanel = getWizardPanel();
            pnlMain.add(wizardPanel, WizardViewType.WIZARD.getName());

            // Create the Welcome panel and add it to the main panel
            JPanel pnlWelcome = createWelcomePanel();
            pnlMain.add(pnlWelcome, WizardViewType.WELCOME.getName());
        }
        return pnlMain;
    }

    protected JPanel getWizardPanel() {

        if (pnlSubwizard == null) {
            pnlSubwizard = new JPanel(new BorderLayout());
            pnlSubwizard.setBorder(
                    BorderFactory.createLineBorder(UIConstants.INTEL_WHITE, 5));

            // Add the subnet name label/text field to the top of the wizard
            // panel
            JLabel lblSubnetName = ComponentFactory.getH5Label(
                    STLConstants.K2111_NAME.getValue() + ":", Font.BOLD);
            txtFldSubnetName = ComponentFactory.createTextField(
                    UIConstants.NAME_CHARS, false, MAX_SUBNET_NAME_LEN,
                    (DocumentListener[]) null);
            txtFldSubnetName.setName(WidgetName.SW_SUBNET_NAME.name());
            originalSubnetNameBorder = txtFldSubnetName.getBorder();
            txtFldSubnetName.getDocument()
                    .addDocumentListener(createDocumentListener());
            txtFldSubnetName.addFocusListener(new FocusListener() {

                @Override
                public void focusGained(FocusEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            txtFldSubnetName.selectAll();
                        }
                    });
                }

                @Override
                public void focusLost(FocusEvent e) {
                    if (btnSelected != null) {
                        String name = txtFldSubnetName.getText();
                        btnSelected.setText(name);
                    }
                }

            });

            this.addWindowListener(new WindowAdapter() {

                @Override
                public void windowActivated(WindowEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            txtFldSubnetName.requestFocusInWindow();
                        }
                    });
                }

            });

            pnlHeading = new JPanel();
            pnlHeading.setBackground(CONTENT_COLOR);
            pnlHeading.setLayout(new GridBagLayout());
            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.HORIZONTAL;
            gc.insets = new Insets(3, 2, 5, 2);
            gc.weighty = 1;
            gc.weightx = 0;
            gc.gridwidth = 1;
            pnlHeading.add(lblSubnetName, gc);
            gc.weightx = 1;
            pnlHeading.add(txtFldSubnetName, gc);
            gc.weighty = 1;
            gc.weightx = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            // Help button
            helpBtn = ComponentFactory
                    .getImageButton(UIImages.HELP_ICON.getImageIcon());
            helpBtn.setName(WidgetName.SW_HELP.name());
            helpBtn.setToolTipText(STLConstants.K0037_HELP.getValue());
            pnlHeading.add(helpBtn, gc);
            pnlSubwizard.add(pnlHeading, BorderLayout.NORTH);

            // Create a tabbed pane
            tabbedPane = new JTabbedPane() {
                private static final long serialVersionUID =
                        -4351718652903653728L;

                /*
                 * (non-Javadoc)
                 *
                 * @see javax.swing.JTabbedPane#setSelectedIndex(int)
                 */
                @Override
                public void setSelectedIndex(int index) {
                    if (isEditValid()) {
                        super.setSelectedIndex(index);
                    }
                }

            };
            tabbedPane.addChangeListener(new ChangeListener() {

                @Override
                public void stateChanged(ChangeEvent e) {

                    // Update the "Next" button depending on what tab is
                    // selected
                    String str;
                    int maxIndex = tabbedPane.getTabCount() - 1;
                    if (tabbedPane.getSelectedIndex() == maxIndex) {
                        btnPrevious.setEnabled(true);
                        str = STLConstants.K0627_FINISH.getValue();
                    } else {
                        str = STLConstants.K0622_NEXT.getValue();
                        if (tabbedPane.getSelectedIndex() == 0) {
                            btnPrevious.setEnabled(false);
                        }
                    }
                    updateNextButton(str);

                    // Only do this if the tab was selected
                    Component[] comps = pnlNavigationButtons.getComponents();
                    if ((!nextSelected && !subnetBtnSelected)
                            && (comps.length > 0)) {
                        int tabIndex = tabbedPane.getSelectedIndex();

                        if (tabIndex >= 0) {
                            wizardListener.setCurrentTask(tabIndex);
                            Component component =
                                    tabbedPane.getTabComponentAt(tabIndex);
                            String tabName = (component != null) ? tabbedPane
                                    .getTabComponentAt(tabIndex).getName()
                                    : null;
                            wizardListener.onTab(tabName);
                        }
                    }
                }
            });
            tabbedPane.setUI(new IntelTabbedPaneUI());
            tabbedPane.setBorder(BorderFactory
                    .createLineBorder(UIConstants.INTEL_BORDER_GRAY, 2));

            pnlSubwizard.add(tabbedPane);

            // Create a card layout panel to hold two control panels; one for
            // subnet creation and the other for existing subnets
            pnlSubwizardCtrl = new JPanel(controlLayout);
            pnlSubwizardCtrl.setBackground(WIZARD_COLOR);
            pnlSubwizardCtrl.add(creationControlPanel(),
                    WizardControlType.CREATION.getName());
            pnlSubwizardCtrl.add(existingControlPanel(),
                    WizardControlType.EXISTING.getName());

            // Add the subwizard panel to the main panel
            pnlSubwizard.add(pnlSubwizardCtrl, BorderLayout.SOUTH);
        }
        return pnlSubwizard;
    }

    protected JPanel createWelcomePanel() {

        // Create the main welcome panel
        JPanel pnlWelcome = new JPanel(new BorderLayout());
        pnlWelcome.setBackground(WIZARD_COLOR);
        JLabel lblWelcome = ComponentFactory.getH2Label(
                STLConstants.K3019_WELCOME_MESSAGE.getValue(), Font.ITALIC);
        lblWelcome.setHorizontalAlignment(JLabel.CENTER);
        lblWelcome.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        pnlWelcome.add(lblWelcome, BorderLayout.NORTH);

        // The Welcome Content panel has a CardLayout, and contains either an
        // Instruction panel if there are no subnets, or a Status panel if
        // saving a configuration
        pnlWelcomeContent = new JPanel(welcomeLayout);
        pnlWelcome.add(pnlWelcomeContent, BorderLayout.CENTER);

        // Create the Instructions panel
        JPanel pnlInstructions = createWelcomeInstructionsPanel();
        pnlWelcomeContent.add(pnlInstructions,
                WizardWelcomeType.INSTRUCTIONS.getName());

        // Create the Status panel
        JPanel pnlStatus = createWelcomeStatusPanel();
        pnlWelcomeContent.add(pnlStatus, WizardWelcomeType.STATUS.getName());

        // Create the Error panel
        JPanel pnlError = createWelcomeErrorPanel();
        pnlWelcomeContent.add(pnlError, WizardWelcomeType.ERROR.getName());

        return pnlWelcome;
    }

    protected JPanel createWelcomeInstructionsPanel() {

        // Create the instructions panel
        JPanel pnlInstructions = new JPanel(new GridBagLayout());
        pnlInstructions.setBackground(WIZARD_COLOR);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.weightx = 1;
        JLabel lblInstructions = ComponentFactory.getH3Label(
                UILabels.STL50083_WELCOME_MESSAGE.getDescription(),
                Font.ITALIC);
        lblInstructions.setHorizontalAlignment(JLabel.CENTER);
        pnlInstructions.add(lblInstructions, gc);

        return pnlInstructions;
    }

    protected JPanel createWelcomeStatusPanel() {

        JPanel pnlStatus = new JPanel(new BorderLayout());
        pnlStatus.setBackground(WIZARD_COLOR);

        // Create the status panel
        JPanel pnlContent = new JPanel();
        pnlContent.setLayout(new BoxLayout(pnlContent, BoxLayout.Y_AXIS));
        pnlContent.setBackground(WIZARD_COLOR);

        // Add a progress panel to the status panel
        progressPanel = new ProgressPanel(false, null);
        progressPanel.setName(WidgetName.SW_VAL_PROGRESS.name());
        progressPanel
                .setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        pnlContent.add(progressPanel);

        pnlContent.add(Box.createVerticalStrut((int) (DIALOG_HEIGHT * .05)));

        // Host Check Status
        lblHostReachabilityIcon = new JLabel(UIImages.DASH.getImageIcon());
        lblHostReachabilityIcon
                .setName(WidgetName.SW_HOST_REACHABILITY_STATUS_ICON.name());
        lblHostReachability = ComponentFactory.getH3Label(
                STLConstants.K3022_HOST_CONNECTIVITY.getValue(), Font.ITALIC);
        lblHostReachability.setHorizontalAlignment(JLabel.LEFT);
        lblHostReachabilityStatus =
                ComponentFactory.getH3Label("**********", Font.ITALIC);
        lblHostReachabilityStatus
                .setName(WidgetName.SW_HOST_REACHABILITY_STATUS.name());
        lblHostReachabilityNotes = ComponentFactory.getH5Label("", Font.BOLD);
        lblHostReachabilityNotes
                .setName(WidgetName.SW_HOST_REACHABILITY_NOTES.name());
        pnlContent.add(
                getStatusPanel(lblHostReachabilityIcon, lblHostReachability,
                        lblHostReachabilityStatus, lblHostReachabilityNotes));
        pnlContent.add(Box.createVerticalStrut((int) (DIALOG_HEIGHT * .1)));

        // Validation Status
        lblEntryValidationIcon = new JLabel(UIImages.DASH.getImageIcon());
        lblEntryValidationIcon
                .setName(WidgetName.SW_ENTRY_VALIDATION_STATUS_ICON.name());
        lblEntryValidation = ComponentFactory.getH3Label(
                STLConstants.K3023_ENTRY_VALIDATION.getValue(), Font.ITALIC);
        lblEntryValidation.setHorizontalAlignment(JLabel.LEFT);
        lblEntryValidationStatus =
                ComponentFactory.getH3Label("**********", Font.ITALIC);
        lblEntryValidationStatus
                .setName(WidgetName.SW_ENTRY_VALIDATION_STATUS.name());
        lblEntryValidationNotes = ComponentFactory.getH5Label("", Font.BOLD);
        lblEntryValidationNotes
                .setName(WidgetName.SW_ENTRY_VALIDATION_NOTES.name());
        pnlContent
                .add(getStatusPanel(lblEntryValidationIcon, lblEntryValidation,
                        lblEntryValidationStatus, lblEntryValidationNotes));
        pnlContent.add(Box.createVerticalStrut((int) (DIALOG_HEIGHT * .1)));

        // Database Update Status
        lblDatabaseUpdateIcon = new JLabel(UIImages.DASH.getImageIcon());
        lblDatabaseUpdateIcon
                .setName(WidgetName.SW_DB_UPDATE_STATUS_ICON.name());
        lblDatabaseUpdate = ComponentFactory.getH3Label(
                STLConstants.K3024_DATABASE_UPDATE.getValue(), Font.ITALIC);
        lblDatabaseUpdate.setHorizontalAlignment(JLabel.LEFT);
        lblDatabaseUpdateStatus =
                ComponentFactory.getH3Label("**********", Font.ITALIC);
        lblDatabaseUpdateStatus.setName(WidgetName.SW_DB_UPDATE_STATUS.name());
        lblDatabaseUpdateNotes = ComponentFactory.getH5Label("", Font.BOLD);
        lblDatabaseUpdateNotes.setName(WidgetName.SW_DB_UPDATE_NOTES.name());
        pnlContent.add(getStatusPanel(lblDatabaseUpdateIcon, lblDatabaseUpdate,
                lblDatabaseUpdateStatus, lblDatabaseUpdateNotes));
        pnlContent.add(Box.createVerticalStrut((int) (DIALOG_HEIGHT * .1)));
        pnlStatus.add(pnlContent, BorderLayout.NORTH);

        // Control Panel
        JPanel pnlControl = new JPanel(new BorderLayout());
        pnlControl.setBackground(WIZARD_COLOR);
        pnlControl.setBorder(BorderFactory.createEmptyBorder(0, 0, 2, 2));
        btnWelcomeOk = ComponentFactory
                .getIntelActionButton(STLConstants.K0645_OK.getValue());
        btnWelcomeOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                manualSelect = true;
                setSelectedTask(0);
                onSubnetButtonClick(btnSelected);
            }

        });
        pnlControl.add(btnWelcomeOk, BorderLayout.EAST);
        pnlStatus.add(pnlControl, BorderLayout.SOUTH);

        return pnlStatus;
    }

    protected JPanel createWelcomeErrorPanel() {

        // Create the error panel
        JPanel pnlError = new JPanel(new GridBagLayout());
        pnlError.setBackground(WIZARD_COLOR);
        GridBagConstraints gc = new GridBagConstraints();
        gc.gridy = 0;
        gc.weightx = 1;
        lblWelcomeError = ComponentFactory.getH3Label(
                UILabels.STL50094_WELCOME_ERROR.getDescription(), Font.ITALIC);
        lblWelcomeError.setName(WidgetName.SW_SUBNET_ERROR.name());
        lblWelcomeError.setHorizontalAlignment(JLabel.CENTER);
        lblWelcomeError.setVerticalAlignment(JLabel.TOP);
        pnlError.add(lblWelcomeError, gc);

        return pnlError;
    }

    protected JPanel getControlPanel() {
        if (pnlMainCtrl == null) {
            pnlMainCtrl = new JPanel();
            pnlMainCtrl.setLayout(new BoxLayout(pnlMainCtrl, BoxLayout.X_AXIS));

            pnlMainCtrl.setBorder(BorderFactory.createEmptyBorder(4, 2, 4, 2));

            // Add +, -, Ok, Cancel, and Apply buttons to the main control panel
            // Add a single space character on the left and on the right of the
            // '+'
            btnAddSubnet = ComponentFactory.getIntelActionButton(
                    " " + STLConstants.K3011_PLUS.getValue() + " ");
            btnAddSubnet.setName(WidgetName.SW_ADD_SUBNET.name());
            btnAddSubnet.setBorder(
                    new RoundedCornersBorder(UIConstants.INTEL_BLUE, 1, 4));
            btnAddSubnet.setAlignmentX(Component.LEFT_ALIGNMENT);
            btnAddSubnet.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    SwingUtilities.invokeLater(new Runnable() {

                        @Override
                        public void run() {
                            onAddSubnet();
                        }

                    });
                }
            });
            pnlMainCtrl.add(btnAddSubnet);
            pnlMainCtrl.add(Box.createHorizontalStrut(2));

            // Add a single space character on the left and on the right of the
            // '-'
            btnDeleteSubnet = ComponentFactory.getIntelActionButton(
                    " " + STLConstants.K3012_MINUS.getValue() + " ");
            btnDeleteSubnet.setName(WidgetName.SW_DELETE_SUBNET.name());
            btnDeleteSubnet.setBorder(
                    new RoundedCornersBorder(UIConstants.INTEL_BLUE, 1, 4));
            btnDeleteSubnet.setAlignmentX(Component.LEFT_ALIGNMENT);

            btnDeleteSubnet.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        ignoreEditCheck = true;
                        onDeleteSubnet();
                    } finally {
                        ignoreEditCheck = false;
                    }
                }
            });
            pnlMainCtrl.add(btnDeleteSubnet);

            ComponentFactory.makeSameWidthButtons(
                    new JButton[] { btnAddSubnet, btnDeleteSubnet });

            pnlMainCtrl.add(Box.createGlue());

            // Add the Run button
            btnRun = ComponentFactory
                    .getIntelActionButton(STLConstants.K3014_RUN.getValue());
            btnRun.setName(WidgetName.SW_RUN.name());
            btnRun.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onRun();
                }

            });

            // Add a mouse listener to ensure the Run button gets the focus
            btnRun.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    // ((JButton) e.getSource()).requestFocusInWindow();
                }
            });
            pnlMainCtrl.add(btnRun);
            pnlMainCtrl.add(Box.createHorizontalStrut(5));

            // Add the Close button
            btnClose = ComponentFactory
                    .getIntelActionButton(STLConstants.K0740_CLOSE.getValue());
            btnClose.setName(WidgetName.SW_CLOSE.name());
            btnClose.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    onClose(false);
                }

            });
            pnlMainCtrl.add(btnClose);
            ComponentFactory
                    .makeSameWidthButtons(new JButton[] { btnRun, btnClose });
        }
        return pnlMainCtrl;
    }

    private DocumentListener createDocumentListener() {
        DocumentListener listener = new DocumentDirtyListener() {

            @Override
            public void setDirty(DocumentEvent e) {
                dirty = true;
                wizardListener.setDirty(true);
            }

        };
        return listener;
    }

    public void setWelcomeOkEnabled(boolean enable) {
        btnWelcomeOk.setEnabled(enable);
    }

    protected JPanel getStatusPanel(JLabel... labels) {

        int numLabels = labels.length;

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(WIZARD_COLOR);

        // Put all but the last label horizontally on the status panel
        JPanel pnlStatus = new JPanel();
        pnlStatus.setBackground(WIZARD_COLOR);
        pnlStatus.setAlignmentX(Component.LEFT_ALIGNMENT);
        pnlStatus.setLayout(new BoxLayout(pnlStatus, BoxLayout.X_AXIS));
        for (int i = 0; i < numLabels - 1; i++) {
            labels[i].setAlignmentX(Component.LEFT_ALIGNMENT);
            labels[i].setBorder(BorderFactory.createEmptyBorder(5, 60, 5, 65));
            pnlStatus.add(labels[i]);
        }
        mainPanel.add(pnlStatus, BorderLayout.NORTH);

        // Add the last label vertically under the status panel
        JPanel pnlNotes = new JPanel();
        pnlNotes.setBackground(WIZARD_COLOR);
        pnlNotes.setLayout(new BoxLayout(pnlNotes, BoxLayout.X_AXIS));
        pnlNotes.add(Box.createHorizontalStrut(60));
        pnlNotes.add(labels[numLabels - 1]);
        mainPanel.add(pnlNotes, BorderLayout.CENTER);

        return mainPanel;
    }

    protected void addGlue(JPanel panel, int direction) {
        GridBagConstraints gc = new GridBagConstraints();
        gc.anchor = GridBagConstraints.NORTH;
        gc.fill = direction;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        gc.insets = new Insets(2, 2, 2, 2);
        gc.weightx = 1;
        gc.weighty = 1;

        switch (direction) {
            case GridBagConstraints.VERTICAL:
                panel.add(Box.createVerticalGlue(), gc);
                break;

            case GridBagConstraints.HORIZONTAL:
                panel.add(Box.createVerticalGlue(), gc);
                break;
        }

    }

    protected JPanel existingControlPanel() {
        // Add the sub-wizard control panel containing buttons pertaining
        // to all sub-wizards
        pnlExistingSubnet = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        pnlExistingSubnet.setOpaque(false);

        // Add the Apply button
        btnApply = ComponentFactory
                .getIntelActionButton(STLConstants.K0672_APPLY.getValue());
        btnApply.setName(WidgetName.SW_APPLY.name());
        btnApply.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (!isEditValid()) {
                    return;
                }

                if (tabbedPane.getSelectedIndex() >= 0) {
                    wizardListener
                            .setCurrentTask(tabbedPane.getSelectedIndex());
                }
                onFinish();
            }
        });
        // Add a mouse listener to ensure the Apply button gets the focus
        btnApply.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).requestFocusInWindow();
            }
        });
        pnlExistingSubnet.add(btnApply, BorderLayout.EAST);

        // Add the Reset button
        btnReset = ComponentFactory
                .getIntelActionButton(STLConstants.K1006_RESET.getValue());
        btnReset.setName(WidgetName.SW_RESET.name());
        btnReset.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onReset();
            }
        });
        // Add a mouse listener to ensure the Reset button gets the focus
        btnReset.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                ((JButton) e.getSource()).requestFocusInWindow();
            }
        });
        pnlExistingSubnet.add(btnReset, BorderLayout.EAST);

        return pnlExistingSubnet;
    }

    protected JPanel creationControlPanel() {
        // Add the sub-wizard control panel containing buttons pertaining
        // to all sub-wizards
        JPanel pnlCreateSubnet =
                new JPanel(new FlowLayout(FlowLayout.TRAILING));
        pnlCreateSubnet.setOpaque(false);

        // Add the Back button
        btnPrevious = ComponentFactory
                .getIntelActionButton(STLConstants.K0624_BACK.getValue());
        btnPrevious.setName(WidgetName.SW_PREVIOUS.name());
        btnPrevious.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onPrevious();
            }
        });
        pnlCreateSubnet.add(btnPrevious, BorderLayout.EAST);

        // Add the Next button
        btnNext = ComponentFactory
                .getIntelActionButton(STLConstants.K0622_NEXT.getValue());
        btnNext.setName(WidgetName.SW_NEXT.name());
        btnNext.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                onNext();
            }
        });
        pnlCreateSubnet.add(btnNext, BorderLayout.EAST);

        return pnlCreateSubnet;
    }

    public JButton getHelpButton() {
        return helpBtn;
    }

    @Override
    public void onEmailTest(String recipients) {
        wizardListener.onEmailTest(recipients);
    }

    @Override
    public void setSubnets(final List<SubnetDescription> subnets) {
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                pnlNavigationButtons.removeAll();
                if (subnets != null) {
                    for (SubnetDescription subnet : subnets) {
                        JButton btn = createSubnetButton();
                        btn.setText(subnet.getName());
                        btn.putClientProperty(CLIENT_KEY, subnet);
                        pnlNavigationButtons.add(btn);
                    }
                }
                revalidate();
                // Repaint the panel with the new components
                pnlNavigationButtons.repaint();
            }

        });
    }

    @Override
    public void addSubnet(final SubnetDescription subnet) {
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                JButton btn = createSubnetButton();
                btn.setText(subnet.getName());
                btn.putClientProperty(CLIENT_KEY, subnet);
                pnlNavigationButtons.add(btn);
                revalidate();
                // Repaint the panel with the new components
                pnlNavigationButtons.repaint();
                tabbedPane.setSelectedIndex(0);
                if (subnet.getSubnetId() == 0) {
                    updateNextButton(K0622_NEXT.getValue());
                }
                txtFldSubnetName.setText(subnet.getName());
            }

        });
    }

    @Override
    public void resetSubnet(final SubnetDescription subnet) {
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                btnSelected.putClientProperty(CLIENT_KEY, subnet);
            }

        });
    }

    public void setNewWizardInProgress(boolean newWizardInProgress) {
        this.newWizardInProgress = newWizardInProgress;
    }

    public boolean getNewWizardStatus() {
        return this.newWizardInProgress;
    }

    /**
     *
     * <i>Description: Create button for new subnet </i>
     *
     * @param control
     * @return
     */
    private JButton createSubnetButton() {
        JButton btnUnknown =
                new JButton(STLConstants.K3018_UNKNOWN_SUBNET.getValue());
        btnUnknown.setUI(new IntelButtonUI(UIConstants.INTEL_MEDIUM_BLUE,
                UIConstants.INTEL_PALE_BLUE));
        btnUnknown.setHorizontalAlignment(JButton.LEADING);
        btnUnknown.setFont(UIConstants.H4_FONT);
        btnUnknown.setRolloverEnabled(false);
        btnUnknown.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INTEL_GRAY),
                BorderFactory.createEmptyBorder(5, 10, 5, 5)));

        btnUnknown.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {

                JButton buttonSource = (JButton) e.getSource();
                if (buttonSource.isEnabled()) {
                    subnetBtnSelected = true;
                    onSubnetButtonClick(buttonSource);
                }
                subnetBtnSelected = false;
            }
        });

        return btnUnknown;
    }

    private void onSubnetButtonClick(final JButton buttonSource) {

        if (buttonSource == null || !isEditValid()) {
            return;
        }

        // If the current subnet button is selected, and this wasn't caused by
        // clicking on the Welcome screen's OK button - then do nothing
        if (!manualSelect && buttonSource.equals(btnSelected)) {
            return;
        }
        manualSelect = false;

        // If a new subnet is being configured or an existing one is changed
        // and a different subnet button is clicked, display a message asking
        // the user if the changes are to be abandoned.
        if (newWizardInProgress || haveUnsavedChanges()) {
            int result = showWarningDialog(
                    UILabels.STL50081_ABANDON_CHANGES_MESSAGE.getDescription());

            if (result == JOptionPane.NO_OPTION) {
                return;
            } else {
                // Reset all the wizards back to their original state
                onReset();
                btnAddSubnet.setEnabled(true);
                if ((btnSelected != null) && ((SubnetDescription) btnSelected
                        .getClientProperty(CLIENT_KEY)).getSubnetId() == 0) {
                    newWizardCleanup();
                }
            }
        }

        SubnetDescription subnet =
                (SubnetDescription) buttonSource.getClientProperty(CLIENT_KEY);
        ignoreEditCheck = true;
        try {
            wizardListener.onSelectSubnet(subnet);
        } catch (Exception e) {
            onSubnetError(buttonSource);
        } finally {
            ignoreEditCheck = false;
        }

    }

    private void onSubnetError(final JButton subnetButton) {

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                btnRun.setEnabled(false);
                String subnetName = subnetButton.getText();
                // updateSelectedButton(subnetButton);
                lblWelcomeError.setText(UILabels.STL50094_WELCOME_ERROR
                        .getDescription(subnetName));
                welcomeLayout.show(pnlWelcomeContent,
                        WizardWelcomeType.ERROR.getName());
                wizardLayout.show(pnlMain, WizardViewType.WELCOME.getName());
            }
        });
    }

    public void enableNavPanel(boolean enable) {
        for (Component c : pnlNavigationButtons.getComponents()) {
            c.setEnabled(enable);
        }
    }

    public void enableSubnetModifiers(boolean enable) {
        btnAddSubnet.setEnabled(enable);
        btnDeleteSubnet.setEnabled(enable);
        btnRun.setEnabled(enable);
    }

    // Button control begins
    private void onAddSubnet() {

        try {
            // Handle unsaved changes when adding a new subnet
            boolean abandoningEdits = onAbandonEdits();
            if (!abandoningEdits) {
                return;
            }

            // Don't check for edit errors if there aren't any subnets
            if (pnlNavigationButtons.getComponents().length == 0) {
                ignoreEditCheck = true;
                txtFldSubnetName.setBackground(UIConstants.INTEL_WHITE);
                txtFldSubnetName.setBorder(originalSubnetNameBorder);
            }

            if (!isEditValid()) {
                return;
            }

            // Disable the '+' button so only one subnet can be a added at a
            // time
            btnAddSubnet.setEnabled(false);

            // Disable all the control buttons
            btnPrevious.setEnabled(false);
            btnNext.setEnabled(false);
            btnRun.setEnabled(false);

            wizardListener.onNewSubnet();

            newWizardInProgress = true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            ignoreEditCheck = false;
        }
    }

    private void onDeleteSubnet() {
        if (btnSelected == null) {
            return;
        }
        // Delete selected subnet and button and change selected button
        // to next in list
        int position = -1;
        boolean found = false;
        Component[] comps = pnlNavigationButtons.getComponents();
        for (int i = 0; i < comps.length; i++) {
            JButton btn = (JButton) comps[i];
            if (btn.equals(btnSelected)) {
                position = i;
                found = true;
                break;
            }

        }

        JButton nextBtn = null;
        if (found) {
            // Warn the user first
            int result = showWarningDialog(
                    UILabels.STL50082_DELETE_SUBNET_MESSAGE.getDescription());
            if (result == JOptionPane.YES_OPTION) {
                newWizardInProgress = false;
                SubnetDescription subnet = (SubnetDescription) btnSelected
                        .getClientProperty(CLIENT_KEY);
                wizardListener.deleteSubnet(subnet);

                if (position < (comps.length - 1)) {
                    nextBtn = (JButton) comps[position + 1];
                } else if (comps.length > 0) {
                    nextBtn = (JButton) comps[0];
                }
                pnlNavigationButtons.remove(btnSelected);
                revalidate();
                pnlNavigationButtons.repaint();
                comps = pnlNavigationButtons.getComponents();
                pnlHeading.setVisible(false);

                // Enable the '+' button so more subnets can be added
                btnAddSubnet.setEnabled(true);

                // Update the view as though the selected button was clicked
                if (nextBtn != null) {
                    manualSelect = true;
                    onSubnetButtonClick(nextBtn);
                }
            }
        }

        // When all subnets have been removed, clear the tabbed pane, disable
        // buttons
        if (comps.length == 0) {
            newWizardInProgress = false;
            btnApply.setEnabled(false);
            btnReset.setEnabled(false);
            btnNext.setEnabled(false);
            btnRun.setEnabled(false);
            welcomeLayout.show(pnlWelcomeContent,
                    WizardWelcomeType.INSTRUCTIONS.getName());
            wizardLayout.show(pnlMain, WizardViewType.WELCOME.getName());
        }
    }

    private void onApply(boolean finished) {
        if (isEditValid()) {
            wizardListener.onFinish();
        }
    }

    protected void onReset() {
        SubnetDescription subnet =
                (SubnetDescription) btnSelected.getClientProperty(CLIENT_KEY);
        String name = subnet.getName();
        btnSelected.setText(name);
        txtFldSubnetName.setText(name);
        wizardListener.onReset();
    }

    protected void onPrevious() {
        if (isEditValid()) {
            wizardListener.onPrevious();
        }
    }

    private void onNext() {
        if (!isEditValid()) {
            return;
        }

        if (isNextButton()) {

            // Check for duplicates before doing anything else
            if (wizardListener.getSubnetView().hasDuplicateHosts()) {
                Util.showErrorMessage(this,
                        UILabels.STL50086_DUPLICATE_HOSTS.getDescription());
                return;
            }

            nextSelected = true;
            boolean success = wizardListener.onNext();

            if (success) {
                // Re-enable the '+' button so more subnets can be added
                btnAddSubnet.setEnabled(true);
            }
        } else {
            onFinish();
        }
        nextSelected = false;
    }

    private void onFinish() {

        // Check for duplicates backup hosts
        if (wizardListener.getSubnetView().hasDuplicateHosts()) {
            Util.showErrorMessage(this,
                    UILabels.STL50086_DUPLICATE_HOSTS.getDescription());
            return;
        }

        String newName = txtFldSubnetName.getText();

        if (!hasDuplicateSubnetNames(newName)) {
            btnSelected.setText(newName);
            pnlNavigationButtons.repaint();
        } else {
            Util.showErrorMessage(this,
                    UILabels.STL50087_DUPLICATE_SUBNETS.getDescription());
            return;
        }

        // Disable the buttons on the Navigation panel and the +/- buttons
        enableNavPanel(false);
        enableSubnetModifiers(false);

        // Prepare the Welcome panel
        initConfigLabels();
        welcomeLayout.show(pnlWelcomeContent,
                WizardWelcomeType.STATUS.getName());
        wizardLayout.show(pnlMain, WizardViewType.WELCOME.getName());
        wizardListener.onFinish();
    }

    protected boolean isEditValid() {
        // if we are deleting whether the edit is valid or not doesn't matter.
        // so we always to return true.
        if (ignoreEditCheck) {
            return true;
        }

        IWizardTask task = wizardListener.getCurrentTask();
        if (task != null
                && (!txtFldSubnetName.isEditValid() || !task.isEditValid())) {
            showErrorMessage(STLConstants.K0030_ERROR.getValue(),
                    UILabels.STL81053_INVALID_EDIT.getDescription());
            return false;
        }
        return true;
    }

    private boolean hasDuplicateSubnets() {

        Set<String> subnetNameSet = new HashSet<String>();

        // Duplicate subnet names can't be added to the set
        // so we will return true
        for (Component comp : pnlNavigationButtons.getComponents()) {
            JButton btn = (JButton) comp;
            SubnetDescription subnet =
                    (SubnetDescription) btn.getClientProperty(CLIENT_KEY);
            if (!subnetNameSet.add(subnet.getName().toLowerCase())) {
                return true;
            }
        }

        // If we make it this far, there are no duplicates
        return false;
    }

    protected boolean hasDuplicateSubnetNames(String newName) {

        Set<String> subnetNameSet = new HashSet<String>();

        // Duplicate subnet names can't be added to the set
        // so we will return true
        for (Component comp : pnlNavigationButtons.getComponents()) {
            JButton btn = (JButton) comp;
            if (btn.equals(btnSelected)) {
                continue;
            }
            SubnetDescription subnet =
                    (SubnetDescription) btn.getClientProperty(CLIENT_KEY);
            if (!subnetNameSet.add(subnet.getName().toLowerCase())) {
                return true;
            }
        }

        // Now try to add the newName
        if (!subnetNameSet.add(newName.toLowerCase())) {
            return true;
        }

        // If we make it this far, the new name is not a duplicate
        return false;
    }

    protected void initConfigLabels() {

        // Reset the progress panel
        progressPanel.setProgress(0);
        progressPanel.setProgressNote("");

        // Reset the config status labels
        lblHostReachabilityIcon.setIcon(UIImages.DASH.getImageIcon());
        lblHostReachabilityStatus.setText("**********");
        lblHostReachabilityNotes.setText("");

        lblEntryValidationIcon.setIcon(UIImages.DASH.getImageIcon());
        lblEntryValidationStatus.setText("**********");
        lblEntryValidationNotes.setText("");

        lblDatabaseUpdateIcon.setIcon(UIImages.DASH.getImageIcon());
        lblDatabaseUpdateStatus.setText("**********");
        lblDatabaseUpdateNotes.setText("");
    }

    public void updateConfigStatus(ConfigTaskStatus status) {

        ImageIcon icon =
                (status.isSuccess()) ? UIImages.CHECK_MARK.getImageIcon()
                        : UIImages.X_MARK.getImageIcon();

        String resultStr =
                status.isSuccess() ? STLConstants.K3026_SUCCESSFUL.getValue()
                        : STLConstants.K0020_FAILED.getValue();

        switch (status.getType()) {

            case CHECK_HOST:
                icon = (status.isSuccess()) ? UIImages.CHECK_MARK.getImageIcon()
                        : UIImages.WARNING2_ICON.getImageIcon();
                lblHostReachabilityIcon.setIcon(icon);
                lblHostReachabilityStatus.setText(resultStr);

                if (!status.isSuccess()) {
                    lblHostReachabilityNotes.setText(
                            UILabels.STL50088_HOST_NOT_FOUND.getDescription());
                }
                break;

            case VALIDATE_ENTRY:
                icon = (status.isSuccess()) ? UIImages.CHECK_MARK.getImageIcon()
                        : UIImages.WARNING2_ICON.getImageIcon();
                lblEntryValidationIcon.setIcon(icon);
                lblEntryValidationStatus.setText(resultStr);

                if (!status.isSuccess()) {
                    List<? extends Exception> errors = status.getErrors();
                    StringBuffer sb = new StringBuffer("<html>");
                    for (Exception error : errors) {
                        sb.append(error.getMessage() + "<br>");
                    }
                    sb.append("<html>");
                    lblEntryValidationNotes.setText(sb.toString());
                }
                break;

            case UPDATE_DATABASE:
                lblDatabaseUpdateIcon.setIcon(icon);
                lblDatabaseUpdateStatus.setText(resultStr);
                if (!status.isSuccess()) {
                    lblDatabaseUpdateNotes.setText(
                            UILabels.STL50090_DB_SAVE_FAILURE.getDescription());

                }
                setProgress(ConfigTaskType.CONFIGURATION_COMPLETE);
                break;

            default:
                break;
        }
    }

    public void setProgress(ConfigTaskType taskType) {

        switch (taskType) {

            case CHECK_HOST:
                progressPanel.setProgress(33);
                break;

            case VALIDATE_ENTRY:
                progressPanel.setProgress(66);
                break;

            case UPDATE_DATABASE:
                progressPanel.setProgress(99);
                break;

            case CONFIGURATION_COMPLETE:
                progressPanel.setProgress(100);
                break;
        }

        progressPanel.setProgressNote(taskType.getName());
    }

    private void onRun() {
        if (!isEditValid()) {
            return;
        }

        // If there are unsaved changes when Run is clicked, save them to the DB
        if (haveUnsavedChanges()) {
            onApply(true);
        }

        if (newWizardInProgress) {
            // Update the control panel
            controlLayout.show(pnlSubwizardCtrl,
                    WizardControlType.EXISTING.getName());
        }

        // Close the wizard if run was successful
        if (wizardListener.onRun()) {
            onClose(true);
        }
    }

    private void onClose(boolean running) {

        boolean okayToClose = true;

        if (newWizardInProgress) {
            int result = showWarningDialog(
                    UILabels.STL50081_ABANDON_CHANGES_MESSAGE.getDescription());

            // Remove the new button/subnet and close the wizard
            // otherwise leave the wizard open
            if (result == JOptionPane.YES_OPTION) {
                onReset();
                newWizardCleanup();
                okayToClose = true;
            } else {
                okayToClose = false;
            }
        } else if (!running && haveUnsavedChanges()) {
            // Reinitialize each task and close the wizard
            // otherwise leave the wizard open
            int result = showWarningDialog(
                    UILabels.STL50081_ABANDON_CHANGES_MESSAGE.getDescription());

            if (result == JOptionPane.YES_OPTION) {
                onReset();
                newWizardCleanup();
                okayToClose = true;
            } else {
                okayToClose = false;
            }
        }

        // Close the status panels and wizard
        if (okayToClose) {

            // Enable any buttons that were grayed out when the welcome panel
            // was displayed
            enableNavPanel(true);
            enableSubnetModifiers(true);

            wizardListener.closeStatusPanels();
            btnSelected = null;
            closeWizard();

            // Shutdown the configuration thread
            if (!running) {
                wizardListener.cancelConfiguration();
            }
        }
    }

    private boolean haveUnsavedChanges() {
        return wizardListener.haveUnsavedChanges();
    }

    protected boolean onAbandonEdits() {

        // Assume edits will be abandoned
        boolean abandoningEdits = true;

        // If there are unsaved changes display a message
        if (haveUnsavedChanges()) {
            int result = showWarningDialog(
                    UILabels.STL50081_ABANDON_CHANGES_MESSAGE.getDescription());

            // If changes are to be abandoned, restore the model to the views
            if (result == JOptionPane.YES_OPTION) {
                abandoningEdits = true;
                onReset();
            } else {
                abandoningEdits = false;
            }
        }

        return abandoningEdits;
    }

    protected int showWarningDialog(String message) {
        int result = Util.showConfirmDialog(this, message);
        return result;
    }

    private void newWizardCleanup() {
        newWizardInProgress = false;

        pnlNavigationButtons.remove(btnSelected);
        revalidate();
        pnlNavigationButtons.repaint();

        // Clear all the models
        wizardListener.clearTasks();
    }

    @Override
    public boolean nextTab() {

        boolean result = false;
        int currentIndex = tabbedPane.getSelectedIndex();
        int maxIndex = tabbedPane.getTabCount() - 1;

        if (currentIndex == maxIndex) {
            result = false;
        } else if ((0 <= currentIndex) && (currentIndex < maxIndex)) {
            // Enable and move to the next tab
            int newIndex = currentIndex + 1;
            tabbedPane.setEnabledAt(newIndex, true);
            tabbedPane.setSelectedIndex(newIndex);
            result = true;
        }

        return result;
    }

    @Override
    public boolean previousTab() {

        boolean result = false;
        int currentIndex = tabbedPane.getSelectedIndex();

        if (currentIndex == 0) {
            result = true;
        } else if (currentIndex > 0) {
            // Move to the previous tab
            int newIndex = currentIndex - 1;
            tabbedPane.setSelectedIndex(newIndex);

            // If the first tab has been reached, disable the "Back" button
            if (newIndex == 0) {
                btnPrevious.setEnabled(false);
            }

            result = true;
        }

        return result;
    }

    /**
     *
     * <i>Description: Center the wizard dialog relative to the main frame</i>
     *
     */
    private void centerDialog(IFabricView mainFrame) {
        pack();
        Point dialogLocation = new Point(0, 0);

        dialogLocation.x = mainFrame.getScreenPosition().x
                + mainFrame.getScreenSize().width / 2 - getWidth() / 2;

        dialogLocation.y = mainFrame.getScreenPosition().y
                + mainFrame.getScreenSize().height / 2 - getHeight() / 2;

        setLocation(dialogLocation);
    } // centerDialog

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IWizardView#showWizard(SubnetDescription,
     * boolean)
     */
    @Override
    public void showWizard(SubnetDescription subnet, boolean isFirstRun,
            final IFabricView mainFrame) {
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                try {
                    String type = wizardViewType.getName();
                    wizardLayout.show(pnlMain, type);
                    btnNext.setEnabled(false);
                    btnRun.setEnabled(false);
                    if (wizardViewType == WizardViewType.WIZARD) {
                        pnlHeading.setVisible(true);
                    } else {
                        pnlHeading.setVisible(false);
                    }

                    manualSelect = true;
                    onSubnetButtonClick(btnSelected);
                } finally {
                    centerDialog(mainFrame);
                    showWindow(true);
                }

            }
        });
    }

    private void showWindow(boolean show) {
        this.setVisible(true);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IWizardView#showTaskView(java.lang.String)
     */
    @Override
    public void showTaskView(String name) {

    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#enablePrevious(boolean)
     */
    @Override
    public void enablePrevious(boolean enable) {
        btnPrevious.setEnabled(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#enableNext(boolean)
     */
    @Override
    public void enableNext(boolean enable) {
        btnNext.setEnabled(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#enableApply(boolean)
     */
    @Override
    public void enableApply(boolean enable) {
        btnApply.setEnabled(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#enableReset(boolean)
     */
    @Override
    public void enableReset(boolean enable) {
        btnReset.setEnabled(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#enableRun(boolean)
     */
    @Override
    public void enableRun(boolean enable) {
        btnRun.setEnabled(enable);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#closeWizard()
     */
    @Override
    public void closeWizard() {
        this.setVisible(false);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IWizardView#updateNextButton(java.lang.
     * String)
     */
    @Override
    public void updateNextButton(String name) {
        btnNext.setText(name);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IWizardView#isNextButton()
     */
    @Override
    public boolean isNextButton() {

        return btnNext.getText().equals(STLConstants.K0622_NEXT.getValue());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IWizardView#update(com.intel.stl.ui.wizards
     * .model.MultinetWizardModel)
     */
    @Override
    public void update(MultinetWizardModel model) {

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IWizardView#setWizardListener(com.intel
     * .stl.ui.wizards.impl.IWizardListener)
     */
    @Override
    public void setWizardListener(IWizardListener listener) {
        // TODO Auto-generated method stub
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IMultinetWizardView#setWizardListener(com
     * .intel.stl.ui.wizards.impl.IMultinetWizardListener)
     */
    @Override
    public void setWizardListener(IMultinetWizardListener listener) {
        this.wizardListener = listener;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IMultinetWizardView#showErrorMessage(java
     * .lang.String, java.lang.String[])
     */
    @Override
    public void showErrorMessage(String title, String... msgs) {

        StringBuffer message = new StringBuffer();
        for (String msg : msgs) {
            if (message.length() == 0) {
                message.append(msg);
            } else {
                message.append("\n" + msg);
            }
        }

        Util.showErrorMessage(this, message.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IMultinetWizardView#showErrorMessage(java
     * .lang.String, java.lang.Throwable[])
     */
    @Override
    public void showErrorMessage(String title, Throwable... errors) {
        Util.showErrors(this, Arrays.asList(errors));
    }

    @Override
    public void setSelectedSubnet(final SubnetDescription subnet) {
        final Component[] comps = pnlNavigationButtons.getComponents();
        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                btnSelected = null;
                dirty = false;
                txtFldSubnetName.setText(subnet.getName());
                for (Component comp : comps) {
                    JButton btn = (JButton) comp;
                    SubnetDescription btnSubnet = (SubnetDescription) btn
                            .getClientProperty(CLIENT_KEY);
                    if (btnSubnet.equals(subnet)) {
                        btnSelected = btn;
                        btn.setBackground(UIConstants.INTEL_LIGHT_BLUE);
                        btn.setForeground(UIConstants.INTEL_WHITE);
                    } else {
                        btn.setBackground(UIConstants.INTEL_WHITE);
                        btn.setForeground(UIConstants.INTEL_DARK_GRAY);
                    }
                }
                resetButtons((subnet.getSubnetId() == 0));
                txtFldSubnetName.requestFocusInWindow();
            }
        });
    }

    private void resetButtons(boolean isNewSubnet) {
        ignoreEditCheck = true;
        try {
            // Stop the connection test
            stopSubnetConnectionTest();

            // Make the heading panel visible
            pnlHeading.setVisible(true);

            // Show wizard view
            wizardLayout.show(pnlMain, WizardViewType.WIZARD.getName());

            // Set control layout depending on whether button is new
            // or not
            WizardControlType controlType = isNewSubnet
                    ? WizardControlType.CREATION : WizardControlType.EXISTING;
            controlLayout.show(pnlSubwizardCtrl, controlType.getName());

            btnApply.setEnabled(false);
            btnReset.setEnabled(false);

            // Set the tabs
            if (isNewSubnet) {
                setEnableForAllTasks(false);
                tabbedPane.setEnabledAt(0, true);

                btnRun.setEnabled(false);
            } else {
                setEnableForAllTasks(true);
                btnRun.setEnabled(true);
            }
        } finally {
            ignoreEditCheck = false;
        }
    }

    @Override
    public void setTasks(List<IMultinetWizardTask> tasks) {
        tabbedPane.removeAll();
        for (IMultinetWizardTask task : tasks) {
            tabbedPane.add(task.getName(), task.getView());
        }
    }

    @Override
    public void setEnableForAllTasks(boolean enable) {
        int tabs = tabbedPane.getTabCount();
        for (int i = 0; i < tabs; i++) {
            tabbedPane.setEnabledAt(i, enable);
        }
    }

    @Override
    public int getSelectedTask() {
        return tabbedPane.getSelectedIndex();
    }

    @Override
    public void setSelectedTask(int taskNum) {
        if ((0 <= taskNum) && (taskNum < tabbedPane.getTabCount())) {
            tabbedPane.setEnabledAt(taskNum, true);
            tabbedPane.setSelectedIndex(taskNum);
        }
    }

    @Override
    public void setWizardViewType(WizardViewType type) {
        this.wizardViewType = type;
    }

    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IMultinetWizardView#getSubnetName()
     */
    @Override
    public String getSubnetName() {
        return txtFldSubnetName.getText();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.wizards.view.IMultinetWizardView#
     * stopSubnetConnectionTest ()
     */
    @Override
    public void stopSubnetConnectionTest() {
        wizardListener.getSubnetView().stopConnectionTest();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.wizards.view.IMultinetWizardView#validateEmail(java.lang
     * .String)
     */
    @Override
    public boolean isEmailValid(String email) {
        return wizardListener.isEmailValid(email);
    }
}
