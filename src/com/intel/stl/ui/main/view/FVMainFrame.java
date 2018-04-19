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

import static com.intel.stl.ui.common.STLConstants.K0001_FABRIC_VIEWER_TITLE;
import static com.intel.stl.ui.common.STLConstants.K0007_SUBNET;
import static com.intel.stl.ui.common.STLConstants.K0054_CONFIGURE;
import static com.intel.stl.ui.common.STLConstants.K0069_CONNECT_TO;
import static com.intel.stl.ui.common.STLConstants.K0112_ONLINE_HELP;
import static com.intel.stl.ui.common.STLConstants.K0669_LOGGING;
import static com.intel.stl.ui.common.STLConstants.K0689_WIZARD;
import static com.intel.stl.ui.common.STLConstants.K0740_CLOSE;
import static com.intel.stl.ui.common.STLConstants.K5001_EMAIL_MENU_ITEM_TEXT;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CancellationException;

import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jdesktop.swingx.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.FMGuiPlugin;
import com.intel.stl.api.StatementClosedException;
import com.intel.stl.api.StringUtils;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.EventTableController;
import com.intel.stl.ui.common.EventTableModel;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.EventSummaryBarPanel;
import com.intel.stl.ui.common.view.EventTableView;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;
import com.intel.stl.ui.common.view.ProgressPanel;
import com.intel.stl.ui.main.FabricController;
import com.intel.stl.ui.main.FabricModel;

/**
 * New main frame with split panes to separate the Pages from the pinboard and
 * the event table at the bottom of every screen
 */
public class FVMainFrame extends JFrame
        implements IFabricView, ChangeListener, IProgressListener {
    private final Logger log = LoggerFactory.getLogger(FVMainFrame.class);

    /**
     * Serial Version UID
     */
    private static final long serialVersionUID = 6591005475727108668L;

    private static final String INIT_PANEL = "init panel";

    private static final String CONTENT_PANEL = "content panel";

    private static final String SUBNET_PROPERTY = "subnet";

    /**
     * Horizontally split pane for the top level of the application. Separates
     * the pages from the pin-board.
     */
    private JSplitPane mSplPnTopLevel;

    private JPanel mInitTopLevel;

    private CardLayout mCardLayout;

    private JSplitPane leftPane;

    /**
     * Tabbed pane that holds the pages
     */
    private JTabbedPane mTabbedTopLevel;

    private JToolBar toolBar;

    private JButton refreshBtn;

    private JButton undoBtn;

    private JButton redoBtn;

    /**
     * Pin Board Panel
     */
    private JPanel mPnlPinBoard;

    private PinBoardView pinBoardView;

    private EventSummaryBarPanel eventSummaryBarPanel;

    /**
     * Event Table Model
     */
    private EventTableModel mEventTableModel;

    /**
     * Event Table View
     */
    private EventTableView mEventTableView;

    /**
     * Event Table Controller
     */
    private EventTableController mEventTableController;

    /**
     * The controller for this view
     */
    private FabricController controller;

    private JMenu connecttoMenu;

    private JMenuItem closeMenu;

    private JMenuItem wizardMenu;

    private JMenuItem loggingMenu;

    private JMenuItem randomMenu;

    private JMenuItem hideNodesMenu;

    private JMenuItem onlineHelpMenu;

    private JMenuItem aboutMenu;

    private JMenuItem emailMenu;

    private JPanel glassPanel;

    private String subnetName;

    private ProgressPanel progressPanel;

    private String currentTab;

    private Dimension screenSize;

    private Rectangle screenBounds;

    private IPageListener listener;

    private final List<String> progressLabels = new ArrayList<String>();

    /**
     *
     * Description: Constructor for the MainAppFrame class
     *
     */
    public FVMainFrame(String subnetName) {
        super(K0001_FABRIC_VIEWER_TITLE.getValue());
        this.subnetName = subnetName;
        setName(K0001_FABRIC_VIEWER_TITLE.getValue() + "_" + subnetName);
    } // MainAppFrame

    /**
     * @param subnetName
     *            the subnetName to set
     */
    @Override
    public void setSubnetName(String subnetName) {
        this.subnetName = subnetName;
    }

    /**
     * @return the subnetName
     */
    @Override
    public String getSubnetName() {
        return subnetName;
    }

    @Override
    public void showInitScreen(Rectangle bounds, boolean maximized) {
        // Get property value for the hideNodesMenu
        // and trigger the action to make it appear checked
        if (controller.getHideInactiveNodes()) {
            hideNodesMenu.doClick();
        }

        createConnectMenu();
        mCardLayout.show(getContentPane(), INIT_PANEL);
        setBounds(bounds);
        setVisible(true);
        // Maximizing should be done after the frame is set visible
        if (maximized) {
            setExtendedState(getExtendedState() | Frame.MAXIMIZED_BOTH);
        }
    } // showInitScreen

    public void setController(FabricController controller) {
        this.controller = controller;
    }

    public void initComponents() {
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        // Set the main frame screen dimensions
        screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        screenSize.width = (int) (screenSize.width * 0.8);
        screenSize.height = (int) (screenSize.height * 0.8);
        setPreferredSize(screenSize);

        // set Icon images
        Image[] images = new Image[] { UIImages.LOGO_24.getImage(),
                UIImages.LOGO_32.getImage(), UIImages.LOGO_64.getImage(),
                UIImages.LOGO_128.getImage() };
        setIconImages(Arrays.asList(images));
        // set menus
        installMenus();

        // set cards, so we an switch between content panel and init panel
        JPanel panel = (JPanel) getContentPane();
        mCardLayout = new CardLayout();
        panel.setLayout(mCardLayout);

        // mSplPnTopLevel = createContentPane();
        // panel.add(CONTENT_PANEL, mSplPnTopLevel);

        mInitTopLevel = createInitPanel();
        panel.add(INIT_PANEL, mInitTopLevel);

        progressPanel = new ProgressPanel(false, this);
        glassPanel = new JPanel();
        glassPanel.setOpaque(false);

        glassPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        glassPanel.add(progressPanel, gc);

        //
        // The following listeners are being added to consume and block
        // any events to the FV main frame behind the glass panel with
        // certificates details.
        //
        glassPanel.addMouseListener(new MouseAdapter() {
        });
        glassPanel.addMouseMotionListener(new MouseMotionAdapter() {
        });
        glassPanel.addKeyListener(new KeyAdapter() {
        });

        setGlassPane(glassPanel);

        installActions();

        pack();
    }

    private JSplitPane createContentPane() {

        // Create a split pane and add it to the top level panel
        JSplitPane pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        pane.setContinuousLayout(true);
        pane.setResizeWeight(.7);
        pane.setDividerSize(5);

        // Create a panel for the pin board
        mPnlPinBoard = new JPanel();
        mPnlPinBoard.setLayout(new BorderLayout());
        mPnlPinBoard.setOpaque(false);
        mPnlPinBoard.setMinimumSize(new Dimension(100, 300));

        eventSummaryBarPanel = new EventSummaryBarPanel();
        eventSummaryBarPanel.setVisible(true);
        mPnlPinBoard.add(eventSummaryBarPanel, BorderLayout.NORTH);
        mPnlPinBoard.setVisible(true);
        mPnlPinBoard.setBackground(UIConstants.INTEL_WHITE);

        pinBoardView = new PinBoardView();
        pinBoardView.setOpaque(false);
        JScrollPane scrollPane = new JScrollPane(pinBoardView);
        scrollPane.getViewport().setBackground(UIConstants.INTEL_WHITE);
        mPnlPinBoard.add(scrollPane, BorderLayout.CENTER);
        // Put the pin board on the right component of the main split pane
        pane.setRightComponent(mPnlPinBoard);

        // Create split pane on left side
        leftPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        leftPane.setContinuousLayout(true);
        leftPane.setResizeWeight(0.8);
        leftPane.setDividerSize(4);
        leftPane.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (mEventTableView.getSize().height < mEventTableView
                        .getMinimumSize().height) {
                    int newLoc = leftPane.getSize().height
                            - mEventTableView.getMinimumSize().height - 4;
                    leftPane.setDividerLocation(newLoc);
                }
            }
        });

        // Create the tabbed pane which will be populated when showContent() is
        // called
        mTabbedTopLevel = new JTabbedPane() {
            private static final long serialVersionUID = -638815127814812316L;

            @Override
            public void setSelectedIndex(int index) {
                if (listener == null || listener.canPageChange(currentTab,
                        mTabbedTopLevel.getTitleAt(index))) {
                    super.setSelectedIndex(index);
                }
            }
        };
        mTabbedTopLevel.addChangeListener(this);

        IntelTabbedPaneUI tabUi = new IntelTabbedPaneUI();
        JPanel ctrPanel = tabUi.getControlPanel();
        initTabCtrlPanel(ctrPanel);
        mTabbedTopLevel.setUI(tabUi);
        leftPane.setTopComponent(mTabbedTopLevel);

        // Add the event table
        mEventTableModel = new EventTableModel();
        mEventTableView = new EventTableView(mEventTableModel);
        mEventTableView.setMinimumSize(new Dimension(200, 64));
        mEventTableView.setVisible(false);
        mEventTableController =
                new EventTableController(mEventTableModel, mEventTableView);
        leftPane.setBottomComponent(mEventTableView);

        // Put left pane to the left component of the main split pane
        pane.setLeftComponent(leftPane);

        return pane;
    }

    private void initTabCtrlPanel(JPanel ctrPanel) {
        ctrPanel.setLayout(new BorderLayout());
        ctrPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
        JLabel intelLogo = new JLabel(UIImages.LOGO_32.getImageIcon());
        ctrPanel.add(intelLogo, BorderLayout.EAST);
        Component toolbar = createTabToolbar();
        ctrPanel.add(toolbar, BorderLayout.CENTER);
    }

    protected Component createTabToolbar() {
        JPanel panel = new JPanel();
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        undoBtn = new JButton(UIImages.UNDO.getImageIcon());
        toolBar.add(undoBtn);

        redoBtn = new JButton(UIImages.REDO.getImageIcon());
        toolBar.add(redoBtn);

        refreshBtn = new JButton(STLConstants.K0107_REFRESH.getValue(),
                UIImages.REFRESH.getImageIcon()) {
            private static final long serialVersionUID = 1L;

            @Override
            public void setEnabled(boolean b) {
                super.setEnabled(b);
                setForeground(b ? UIConstants.INTEL_MEDIUM_DARK_BLUE
                        : UIConstants.INTEL_GRAY);
            }
        };
        refreshBtn.setFont(UIConstants.H4_FONT);
        refreshBtn.setForeground(UIConstants.INTEL_MEDIUM_DARK_BLUE);
        refreshBtn.setEnabled(false);
        toolBar.add(refreshBtn);

        panel.add(toolBar);
        return panel;
    }

    private JPanel createInitPanel() {
        JPanel pnlInit = new JPanel(new BorderLayout());
        pnlInit.setBackground(UIConstants.INTEL_BLUE);

        // Welcome Label
        JLabel lblWelcome = ComponentFactory.getH2Label(
                UILabels.STL50093_WELCOME_FM_GUI.getDescription(), Font.BOLD);
        lblWelcome.setForeground(UIConstants.INTEL_WHITE);
        lblWelcome.setHorizontalAlignment(SwingConstants.CENTER);
        pnlInit.add(lblWelcome, BorderLayout.NORTH);

        // Add space between panels
        pnlInit.add(Box.createVerticalStrut(25));

        // Instruction panel and labels
        JPanel pnlInstructions = new JPanel(new VerticalLayout(20));
        pnlInstructions.setBackground(UIConstants.INTEL_WHITE);
        pnlInstructions.add(Box.createVerticalStrut(50));

        JLabel lblSelectSubnet = ComponentFactory.getH3Label(
                UILabels.STL50091_CONNECT_TO_SUBNET.getDescription(),
                Font.ITALIC);
        lblSelectSubnet.setHorizontalAlignment(SwingConstants.CENTER);
        pnlInstructions.add(lblSelectSubnet);

        JLabel lblOr = ComponentFactory
                .getH3Label(STLConstants.K3040_OR.getValue(), Font.ITALIC);
        lblOr.setHorizontalAlignment(SwingConstants.CENTER);
        pnlInstructions.add(lblOr);

        JLabel lblConfigureSubnet = ComponentFactory.getH3Label(
                UILabels.STL50092_CONFIGURE_SUBNET.getDescription(),
                Font.ITALIC);
        lblConfigureSubnet.setHorizontalAlignment(SwingConstants.CENTER);
        pnlInstructions.add(lblConfigureSubnet);

        // Add instruction panel to init panel
        pnlInit.add(pnlInstructions, BorderLayout.CENTER);
        return pnlInit;
    }

    private void installMenus() {
        JMenuBar menubar = new JMenuBar();
        setJMenuBar(menubar);
        JMenu subnet = new JMenu(K0007_SUBNET.getValue());
        menubar.add(subnet);
        connecttoMenu = new JMenu(K0069_CONNECT_TO.getValue());
        subnet.add(connecttoMenu);
        closeMenu = new JMenuItem(K0740_CLOSE.getValue());
        subnet.add(closeMenu);

        JMenu conf = new JMenu(K0054_CONFIGURE.getValue());
        menubar.add(conf);
        wizardMenu = new JMenuItem(K0689_WIZARD.getValue(),
                UIImages.SETTING_ICON.getImageIcon());
        conf.add(wizardMenu);
        loggingMenu = new JMenuItem(K0669_LOGGING.getValue(),
                UIImages.LOG_MENU_ICON.getImageIcon());
        conf.add(loggingMenu);

        emailMenu = new JMenuItem(K5001_EMAIL_MENU_ITEM_TEXT.getValue(),
                UIImages.EMAIL_ICON.getImageIcon());
        conf.add(emailMenu);

        if (FMGuiPlugin.IS_DEV) {
            randomMenu = ComponentFactory.getIntelCheckBoxMenuItem(
                    STLConstants.K0057_RANDOM.getValue());
            conf.add(randomMenu);
        }

        hideNodesMenu = ComponentFactory.getIntelCheckBoxMenuItem(
                STLConstants.K5013_HIDE_INACTIVE_NODES_MENU_STR.getValue());
        conf.add(hideNodesMenu);

        JMenu help = new JMenu(STLConstants.K0037_HELP.getValue());
        onlineHelpMenu = new JMenuItem(K0112_ONLINE_HELP.getValue(),
                UIImages.HELP_ICON.getImageIcon());
        help.add(onlineHelpMenu);

        String aboutMenuStr = STLConstants.K3100_ABOUT_DIALOG.getValue();
        aboutMenu = new JMenuItem(aboutMenuStr);
        aboutMenu.setMnemonic(aboutMenuStr.charAt(0));

        help.add(aboutMenu);
        menubar.add(help);
    }

    private void installActions() {
        setAboutDialogAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.showAboutDialog();
            }
        });
        setWindowAction(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                if (listener == null
                        || listener.canPageChange(currentTab, null)) {
                    dispose();
                    controller.onWindowClose();
                }
            }
        });
        setCloseAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (listener == null
                        || listener.canPageChange(currentTab, null)) {
                    controller.onMenuClose();
                }
            }
        });
        setWizardAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.showSetupWizard(subnetName);
            }
        });
        setLoggingAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.showLoggingConfig();
            }
        });
        setEmailSettingsAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.showEmailSettingsDialog();
            }
        });

        if (FMGuiPlugin.IS_DEV) {
            setRandomAction(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    boolean addRandomValues =
                            ((JCheckBoxMenuItem) e.getSource()).isSelected();
                    if ((e.getModifiers()
                            & ActionEvent.CTRL_MASK) == ActionEvent.CTRL_MASK) {
                        controller.startSimulatedFailover();
                    } else {
                        controller.applyRandomValue(addRandomValues);
                    }
                }
            });
        }
        setHideNodesAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (controller.getCurrentSubnet() == null) {
                    // do nothing if not any subnet connected
                    return;
                }

                boolean hideInactiveNodes =
                        ((JCheckBoxMenuItem) e.getSource()).isSelected();
                controller.onHideInactiveNodes(hideInactiveNodes);
            }
        });
        addComponentListener(new ComponentAdapter() {

            @Override
            public void componentMoved(ComponentEvent event) {
                if (!isFrameMaximized()) {
                    screenBounds = getBounds();
                }

            }

            @Override
            public void componentResized(ComponentEvent event) {
                if (!isFrameMaximized()) {
                    screenBounds = getBounds();
                }
            }
        });
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        String oldTab = currentTab;
        int index = mTabbedTopLevel.getSelectedIndex();
        currentTab = mTabbedTopLevel.getTitleAt(index);
        if (listener != null) {
            listener.onPageChanged(oldTab, currentTab);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.IFabricView#showMessageAndExit(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void showMessageAndExit(String message, String title) {
        Util.showErrorMessage(this, message);
        controller.onWindowClose();
    }

    @Override
    public void showMessage(String message, String title) {
        Util.showErrorMessage(this, message);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.IFabricView#showErrors(java.util.List)
     */
    @Override
    public void showErrors(List<Throwable> errors) {
        mInitTopLevel.removeAll();
        BoxLayout layout = new BoxLayout(mInitTopLevel, BoxLayout.Y_AXIS);
        mInitTopLevel.setLayout(layout);

        int numErrors = errors.size();
        JLabel label = numErrors == 1
                ? ComponentFactory.getIntelH1Label(
                        UILabels.STL10101_ONE_ERROR_INIT_APP.getDescription(),
                        Font.PLAIN)
                : ComponentFactory
                        .getIntelH1Label(
                                UILabels.STL10100_ERRORS_INIT_APP
                                        .getDescription(errors.size()),
                                Font.PLAIN);
        mInitTopLevel.add(label);
        for (Throwable e : errors) {
            label = ComponentFactory.getIntelH3Label(getErrorMsg(e),
                    Font.PLAIN);
            mInitTopLevel.add(label);
        }
        mCardLayout.show(getContentPane(), INIT_PANEL);
        validate();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    protected String getErrorMsg(Throwable e) {
        String res = StringUtils.getErrorMessage(e);
        return res;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.IFabricView#showContent(java.util.List,
     * java.util.List)
     */
    @Override
    public void showContent(List<IPageController> pages) {
        mTabbedTopLevel.removeChangeListener(this);
        for (IPageController page : pages) {
            mTabbedTopLevel.addTab(page.getName(), page.getIcon(),
                    page.getView(), page.getDescription());
        }
        int index = mTabbedTopLevel.getSelectedIndex();
        currentTab = mTabbedTopLevel.getTitleAt(index);
        mTabbedTopLevel.addChangeListener(this);

        mCardLayout.show(getContentPane(), CONTENT_PANEL);

        Dimension d1 = mTabbedTopLevel.getMinimumSize();
        Dimension d2 = mEventTableView.getMinimumSize();
        int other = getJMenuBar().getHeight() + 4; // divider size
        int newMinHeight = d1.height + d2.height + other;
        setMinimumSize(new Dimension(getMinimumSize().width, newMinHeight));

        validate();
    }

    @Override
    public void close() {
        if (listener == null || listener.canPageChange(currentTab, null)) {
            dispatchEvent(new WindowEvent(this, WindowEvent.WINDOW_CLOSING));
        }
    }

    @Override
    public void resetConnectMenu() {
        createConnectMenu();

        validate();
    }

    private void createConnectMenu() {
        connecttoMenu.removeAll();
        List<SubnetDescription> subnets = controller.getSubnets();
        if(subnets == null || subnets.size() == 0){
            connecttoMenu.setEnabled(false);
        }else{
            connecttoMenu.setEnabled(true);
            for (SubnetDescription subnet : subnets) {
                JMenuItem subnetConnectTo = createMenuItem(subnet);
                connecttoMenu.add(subnetConnectTo);
            }
        }
    }

    private JMenuItem createMenuItem(SubnetDescription subnet) {
        String subnetName = subnet.getName();
        JMenuItem newMenuItem = new JMenuItem(subnetName);
        newMenuItem.putClientProperty(SUBNET_PROPERTY, subnetName);
        newMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem menuItem = (JMenuItem) e.getSource();
                String subnetName =
                        (String) menuItem.getClientProperty(SUBNET_PROPERTY);
                controller.selectSubnet(subnetName);
            }
        });
        return newMenuItem;
    }

    @Override
    public void setCurrentTab(IPageController page) {
        int ix = mTabbedTopLevel.indexOfComponent(page.getView());
        if (ix >= 0) {
            mTabbedTopLevel.setSelectedIndex(ix);
            currentTab = mTabbedTopLevel.getTitleAt(ix);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setWizardAtion(java.awt.event.
     * ActionListener)
     */
    @Override
    public void setWizardAction(ActionListener listener) {
        wizardMenu.addActionListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setLoggingAction(java.awt.event
     * .ActionListener)
     */
    @Override
    public void setLoggingAction(ActionListener listener) {
        loggingMenu.addActionListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setAboutDialogAction(java.awt.
     * event .ActionListener)
     */
    @Override
    public void setAboutDialogAction(ActionListener listener) {
        aboutMenu.addActionListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#setEmailSettingsAction(
     * java.awt.event.ActionListener)
     */
    @Override
    public void setEmailSettingsAction(ActionListener listener) {
        emailMenu.addActionListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setRandomAtion(java.awt.event.
     * ActionListener)
     */
    @Override
    public void setRandomAction(ActionListener listener) {
        randomMenu.addActionListener(listener);
    }

    public void setHelpAction(ActionListener listener) {
        onlineHelpMenu.addActionListener(listener);
    };

    public JMenuItem getOnlineHelpMenu() {
        return onlineHelpMenu;
    }

    public void setHideNodesAction(ActionListener listener) {
        hideNodesMenu.addActionListener(listener);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setWindowAction(java.awt.event
     * .WindowListener)
     */
    @Override
    public void setWindowAction(WindowListener listener) {
        addWindowListener(listener);
    }

    public void setCloseAction(ActionListener listener) {
        closeMenu.addActionListener(listener);
    }

    /**
     *
     * Description:
     *
     * @param listener
     */
    @Override
    public void setPageListener(final IPageListener listener) {
        this.listener = listener;
    }

    @Override
    public void setRefreshAction(ActionListener listener) {
        refreshBtn.addActionListener(listener);
    }

    @Override
    public void setRefreshRunning(boolean isRunning) {
        if (refreshBtn != null) {
            refreshBtn.setIcon(isRunning ? UIImages.RUNNING.getImageIcon()
                    : UIImages.REFRESH.getImageIcon());
        }
    }

    @Override
    public void setUndoAction(Action action) {
        undoBtn.setAction(action);
    }

    @Override
    public void setRedoAction(Action action) {
        redoBtn.setAction(action);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#cleanup()
     */
    @Override
    public void cleanup() {
        dispose();
    }

    @Override
    public void clear() {
        mCardLayout.show(getContentPane(), INIT_PANEL);
        subnetName = null;
        setTitle(K0001_FABRIC_VIEWER_TITLE.getValue());
        mTabbedTopLevel.removeChangeListener(this);
        mTabbedTopLevel.removeAll();
        currentTab = null;
        mTabbedTopLevel.addChangeListener(this);
    }

    @Override
    public void bringToFront() {
        super.setVisible(true);
    }

    @Override
    public Rectangle getFrameBounds() {
        if (isFrameMaximized()) {
            return screenBounds;
        }
        return getBounds();
    }

    @Override
    public boolean isFrameMaximized() {
        return ((getExtendedState()
                & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setEventSummaryBarPanelController
     * (com.intel.stl.ui.common.EventSummaryBarPanelController)
     */
    @Override
    public EventSummaryBarPanel getEventSummaryBarPanel() {
        return this.eventSummaryBarPanel;
    }

    @Override
    public void setReady(boolean ready) {
        mTabbedTopLevel.setEnabled(ready);
        refreshBtn.setEnabled(ready);
        if (ready) {
            controller.processPendingTasks();
        }
    }

    @Override
    public boolean isReady() {
        return refreshBtn.isEnabled();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#showEventSummaryTable()
     */
    @Override
    public void toggleEventSummaryTable() {
        if (mEventTableView.isVisible()) {
            mEventTableView.setVisible(false);
            leftPane.remove(mEventTableView);
        } else {
            mEventTableView.setVisible(true);
            leftPane.setRightComponent(mEventTableView);
        }
    }

    @Override
    public void showEventSummaryTable() {
        mEventTableView.setVisible(true);
        leftPane.setRightComponent(mEventTableView);
    }

    @Override
    public void hideEventSummaryTable() {
        mEventTableView.setVisible(false);
        leftPane.remove(mEventTableView);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#getEventTableController()
     */
    @Override
    public EventTableController getEventTableController() {
        return mEventTableController;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.view.IEventRules#displayErrorMessage(java.lang
     * .String, java.lang.Exception)
     */
    @Override
    public void displayErrorMessage(String windowTitle, Exception exception) {
        Util.showError(this, exception);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#showProgress(java.lang.String,
     * boolean)
     */
    @Override
    public void showProgress(String label, boolean visible) {
        if (visible) {
            progressLabels.add(label);
            progressPanel.setLabel(label);
        } else {
            int last = progressLabels.size() - 1;
            if (last >= 0) {
                progressLabels.remove(last);
                last = progressLabels.size() - 1;
                if (last >= 0) {
                    progressPanel.setLabel(progressLabels.get(last));
                } else {
                    progressPanel.setLabel("");
                }
            }
        }
        glassPanel.setVisible(visible);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#showFailoverProgress(java.lang.
     * String, boolean)
     */
    @Override
    public void showFailoverProgress(String label, boolean visible) {
        if (visible) {
            progressPanel.setCancellable(true);
        } else {
            progressPanel.setCancellable(false);
        }
        showProgress(label, visible);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#setProgress(int)
     */
    @Override
    public void setProgress(final int progress) {
        if (!glassPanel.isVisible() && progress < 100) {
            glassPanel.setVisible(true);
        }
        progressPanel.setProgress(progress);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.view.IFabricView#setProgressNote(java.lang.String)
     */
    @Override
    public void setProgressNote(final String note) {
        if (!glassPanel.isVisible()
                && progressPanel.getPercentComplete() < 1.0) {
            glassPanel.setVisible(true);
        }
        progressPanel.setProgressNote(note);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#getView()
     */
    @Override
    public Component getView() {
        return this;
    }

    /**
     * @return the pinBoardView
     */
    public PinBoardView getPinBoardView() {
        return pinBoardView;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#getScreenSize()
     */
    @Override
    public Dimension getScreenSize() {

        return new Dimension(getWidth(), getHeight());
    }

    @Override
    public void setScreenSize(Dimension dimension) {
        setPreferredSize(dimension);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IFabricView#getScreenPosition()
     */
    @Override
    public Point getScreenPosition() {
        return new Point(getX(), getY());
    }

    public void modelUpdateFailed(FabricModel model, Throwable caught) {
        showProgress(null, false);
        setReady(true);
        if (caught instanceof InterruptedException
                || caught instanceof CancellationException) {
            return;
        }
        if (caught instanceof StatementClosedException) {
            // silent on StatementClosedException
            log.error(caught.getMessage(), caught);
            return;
        }
        caught.printStackTrace();
        Util.showErrorMessage(this, model.getErrorMessage());
    }

    public void modelChanged(FabricModel model) {
        showProgress(null, false);
        SubnetDescription subnet = model.getCurrentSubnet();
        if (subnet == null) {
            subnetName = null;
            setupContentPane();
            mCardLayout.show(getContentPane(), INIT_PANEL);
            setTitle(K0001_FABRIC_VIEWER_TITLE.getValue());
        } else {
            setReady(true);
            subnetName = subnet.getName();
            mCardLayout.show(getContentPane(), CONTENT_PANEL);
            setTitle(K0001_FABRIC_VIEWER_TITLE.getValue() + " - " + subnetName);
        }
    }

    private void setupContentPane() {
        JPanel panel = (JPanel) getContentPane();
        if (mSplPnTopLevel != null) {
            panel.remove(mSplPnTopLevel);
        }
        mSplPnTopLevel = createContentPane();
        panel.add(CONTENT_PANEL, mSplPnTopLevel);
        setRefreshAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                controller.onRefresh();
            }
        });
        setPageListener(controller);
    }

    public Component installGlassPanel(Component comp) {
        Component oldComp = null;
        if (glassPanel.getComponentCount() == 1) {
            oldComp = glassPanel.getComponent(0);
        } else if (glassPanel.getComponentCount() > 1) {
            // shouldn't happen
            throw new RuntimeException("Invalid glass panel!");
        }

        glassPanel.removeAll();
        glassPanel.setLayout(new GridBagLayout());
        GridBagConstraints gc = new GridBagConstraints();
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.CENTER;
        glassPanel.add(comp, gc);
        glassPanel.updateUI();

        return oldComp;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.view.IProgressListener#onCancel()
     */
    @Override
    public void onCancel() {
        controller.cancelFailover();
    }

} // class MainAppFrame
