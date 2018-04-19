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

package com.intel.stl.ui.main;

import static com.intel.stl.api.configuration.AppInfo.PROPERTIES_FM_GUI_APP;
import static com.intel.stl.ui.common.UILabels.STL10110_REFRESHING_PAGES;
import static com.intel.stl.ui.common.UILabels.STL10113_CONNECTION_LOST;
import static com.intel.stl.ui.common.UILabels.STL60008_CONN_LOST;
import static com.intel.stl.ui.common.UILabels.STL60009_PRESS_REFRESH;

import java.awt.Rectangle;
import java.awt.Window;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.SubnetEvent;
import com.intel.stl.api.configuration.AppInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.admin.impl.AdminPage;
import com.intel.stl.ui.admin.view.AdminView;
import com.intel.stl.ui.common.EventSummaryBarPanelController;
import com.intel.stl.ui.common.EventTableController;
import com.intel.stl.ui.common.IContextAware;
import com.intel.stl.ui.common.IEventSummaryBarListener;
import com.intel.stl.ui.common.IPageController;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.EventSummaryBarPanel;
import com.intel.stl.ui.email.EmailSettingsController;
import com.intel.stl.ui.email.IEmailController;
import com.intel.stl.ui.event.JumpToEvent;
import com.intel.stl.ui.event.TaskStatusEvent;
import com.intel.stl.ui.framework.AbstractController;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.framework.IModelListener;
import com.intel.stl.ui.framework.ITask;
import com.intel.stl.ui.logger.config.ILoggingControl;
import com.intel.stl.ui.logger.config.LoggingConfigController;
import com.intel.stl.ui.main.view.AboutDialog;
import com.intel.stl.ui.main.view.CredentialsGlassPanel;
import com.intel.stl.ui.main.view.FVMainFrame;
import com.intel.stl.ui.main.view.FabricView;
import com.intel.stl.ui.main.view.HomeView;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.main.view.IPageListener;
import com.intel.stl.ui.monitor.PerformancePage;
import com.intel.stl.ui.monitor.tree.FVTreeManager;
import com.intel.stl.ui.monitor.view.PerformanceTreeView;
import com.intel.stl.ui.network.GraphService;
import com.intel.stl.ui.network.OutlineService;
import com.intel.stl.ui.network.TopologyPage;
import com.intel.stl.ui.network.view.TopologyView;
import com.intel.stl.ui.publisher.TaskScheduler;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;
import net.engio.mbassy.listener.Handler;

/**
 * FabricController class creates initial view and installs pages on a tabbed
 * pane for testing
 */
public class FabricController
        extends AbstractController<FabricModel, FabricView, FabricController>
        implements IFabricController, IPageListener, IEventSummaryBarListener,
        PropertyChangeListener {
    public static final String PROGRESS_AMOUNT_PROPERTY = "ProgressAmount";

    public static final String PROGRESS_NOTE_PROPERTY = "ProgressNote";

    private static Logger log = LoggerFactory.getLogger(FabricController.class);

    private IFabricView mainFrame;

    private final JFrame viewFrame;

    private final List<IPageController> pages =
            new CopyOnWriteArrayList<IPageController>();

    private int refreshCount;

    private final ISubnetManager subnetMgr;

    private final ILoggingControl loggingConfigController;

    private final IEmailController emailSettingsController;

    private EventSummaryBarPanel eventSummaryBarPanel;

    private EventSummaryBarPanelController eventSummaryBarPanelController;

    private EventTableController eventTableController;

    private boolean hasEventTableToggled;

    private ITask backgroundTask;

    private final List<ITask> pendingTasks =
            Collections.synchronizedList(new ArrayList<ITask>());

    private final FVTreeManager builder;

    private int pageLoadWork;

    private int backgroundTotalWork;

    private double backgroundWork;

    private Rectangle lastBounds;

    private boolean maximized;

    private GraphService graphService;

    private OutlineService outlineService;

    // This name is set by the SubnetManager to manage this controller. It
    // should match the SubnetDescription in the model and in the Context.
    private String subnetName;

    private final HelpAction helpAction;

    private final CertsLoginController certsLoginCtr;

    private final PinBoardController pinBoardCtr;

    private Boolean hideInactiveNodes;

    /**
     * System update, such as refresh, jumpToEvent etc., will trigger page
     * selection changes. This attribute tracks when system is updating, so we
     * know when we should ignore selection on undo track
     */
    protected boolean isSystemUpdate;

    private UndoHandler undoHandler;

    public FabricController(String subnetName, FabricView view,
            ISubnetManager subnetMgr, MBassador<IAppEvent> eventBus) {
        super(new FabricModel(), view, eventBus);
        this.subnetMgr = subnetMgr;
        this.subnetName = subnetName;
        setupEventBus();
        this.builder = new FVTreeManager();
        this.mainFrame = view.getView();
        this.viewFrame = view.getMainFrame();

        loggingConfigController = createLoggingConfigController();

        helpAction = HelpAction.getInstance();
        helpAction.enableHelpMenu(view.getMainFrame().getOnlineHelpMenu());
        certsLoginCtr = createCertsLoginController();

        emailSettingsController =
                createEmailSettingsController(view.getMainFrame());

        pinBoardCtr = createPinBoardController();
        addModelListener(new IModelListener<FabricModel>() {

            @Override
            public void modelChanged(FabricModel model) {
                if (model.getPreviousSubnet() == null
                        && model.getCurrentSubnet() != null) {
                    // first time we get valid model (context). init pin board
                    // besed on DB
                    pinBoardCtr.init();
                }
            }

            @Override
            public void modelUpdateFailed(FabricModel model, Throwable caught) {
            }
        });

        init();
    }

    protected CertsLoginController createCertsLoginController() {
        CredentialsGlassPanel cgp = new CredentialsGlassPanel();
        CertsLoginController ctr =
                new CertsLoginController(this, (FVMainFrame) mainFrame, cgp);
        return ctr;
    }

    protected PinBoardController createPinBoardController() {
        return new PinBoardController(view.getPinBoardView(), this);
    }

    protected void setupEventBus() {
        eventBus.subscribe(this);
    }

    @Override
    public JFrame getViewFrame() {
        return viewFrame;
    }

    @Override
    public MBassador<IAppEvent> getEventBus() {
        return eventBus;
    }

    private void init() {
        eventSummaryBarPanel = mainFrame.getEventSummaryBarPanel();
        eventSummaryBarPanelController =
                new EventSummaryBarPanelController(eventSummaryBarPanel);
        eventSummaryBarPanelController.setEventSummaryBarListener(this);
        mainFrame.showEventSummaryTable();
        hasEventTableToggled = false;

        eventTableController = mainFrame.getEventTableController();

        installPages();
        installUndoHandler();
    }

    /**
     * Add pages to <code>pages</code>
     */
    protected void installPages() {
        HomePage homePage = createHomePage();
        pages.add(homePage);

        PerformancePage perPage = createPerformancePage();
        pages.add(perPage);

        TopologyPage topologyPage = createTopologyPage();
        pages.add(topologyPage);

        AdminPage adminPage = createAdminPage();
        pages.add(adminPage);

        pageLoadWork = 0;
        for (IPageController page : pages) {
            pageLoadWork += page.getContextSwitchWeight().getWeight();
        }

    }

    protected void installUndoHandler() {
        undoHandler = new UndoHandler();
        view.getView().setUndoAction(undoHandler.getUndoAction());
        view.getView().setRedoAction(undoHandler.getRedoAction());
    }

    // The following createXXX methods are overridden in unit tests
    protected HomePage createHomePage() {
        return new HomePage(new HomeView(), eventBus);
    }

    protected PerformancePage createPerformancePage() {
        return new PerformancePage(new PerformanceTreeView(), eventBus,
                builder);
    }

    protected TopologyPage createTopologyPage() {
        // These two services are being created here so that they can be shut
        // down when the connection is lost.
        graphService = new GraphService();
        outlineService = new OutlineService();
        TopologyView topologyView =
                new TopologyView(graphService, outlineService);
        return new TopologyPage(topologyView, eventBus, builder);
    }

    protected AdminPage createAdminPage() {
        Window owner = (mainFrame != null && (mainFrame instanceof Window))
                ? (Window) mainFrame : null;
        return new AdminPage(new AdminView((IFabricView) owner), eventBus);
    }

    protected LoggingConfigController createLoggingConfigController() {

        return LoggingConfigController.getInstance(view.getMainFrame(),
                subnetMgr);

    }

    protected EmailSettingsController createEmailSettingsController(
            FVMainFrame owner) {
        return EmailSettingsController.getInstance(owner, subnetMgr);
    }

    protected boolean isAddRandomValues() {
        return model.isAddRandomValues();
    }

    @Override
    public IFabricView getView() {
        return mainFrame;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.IFabricController#getCurrentSubnet()
     */
    @Override
    public SubnetDescription getCurrentSubnet() {
        return model.getCurrentSubnet();
    }

    @Override
    public Context getCurrentContext() {
        return super.getContext();
    }

    @Override
    public void initializeContext(Context context) {
        this.subnetName = context.getSubnetDescription().getName();
        checkBackgroundTask();
        // Show progress on UI
        mainFrame.setSubnetName(subnetName);
        mainFrame.setReady(false);
        mainFrame.showProgress(
                UILabels.STL10104_INIT_SUBNET.getDescription(subnetName), true);
        mainFrame.setProgress(0);

        // need to set context for builder first before Performance and Topology
        // page do the conext setting.
        List<IContextAware> foregroundContextPages =
                new ArrayList<IContextAware>();
        foregroundContextPages.add(builder);

        List<IContextAware> backgroundContextPages =
                new ArrayList<IContextAware>();
        backgroundContextPages.addAll(pages);
        backgroundContextPages.add(eventSummaryBarPanelController);
        backgroundContextPages.add(eventTableController);
        backgroundTotalWork = builder.getContextSwitchWeight().getWeight()
                + eventSummaryBarPanelController.getContextSwitchWeight()
                        .getWeight()
                + eventTableController.getContextSwitchWeight().getWeight();
        backgroundTotalWork += pageLoadWork;
        backgroundWork = 0.0;
        backgroundTask = new SubnetSwitchTask(model, context,
                foregroundContextPages, backgroundContextPages);
        backgroundTask.addPropertyChangeListener(this);
        mainFrame.setTitle(STLConstants.K0001_FABRIC_VIEWER_TITLE.getValue());
        submitTask(backgroundTask);
    }

    @Override
    public void resetContext(Context newContext) {
        checkBackgroundTask();
        SubnetDescription subnet = newContext.getSubnetDescription();
        this.subnetName = subnet.getName();
        mainFrame.setSubnetName(subnetName);
        mainFrame.setReady(false);
        mainFrame.showProgress(
                UILabels.STL10104_INIT_SUBNET.getDescription(subnetName), true);
        mainFrame.setProgress(0);

        // need to set context for builder first before Performance and Topology
        // page do the conext setting.
        List<IContextAware> foregroundContextPages =
                new ArrayList<IContextAware>();
        foregroundContextPages.add(builder);

        List<IContextAware> backgroundContextPages =
                new ArrayList<IContextAware>();
        backgroundContextPages.addAll(pages);
        backgroundContextPages.add(eventSummaryBarPanelController);
        backgroundContextPages.add(eventTableController);
        backgroundTotalWork = builder.getContextSwitchWeight().getWeight()
                + eventSummaryBarPanelController.getContextSwitchWeight()
                        .getWeight()
                + eventTableController.getContextSwitchWeight().getWeight();
        backgroundTotalWork += pageLoadWork;
        backgroundWork = 0.0;
        backgroundTask = new SubnetSwitchTask(model, newContext,
                foregroundContextPages, backgroundContextPages);
        backgroundTask.addPropertyChangeListener(this);
        mainFrame.setTitle(STLConstants.K0001_FABRIC_VIEWER_TITLE.getValue());
        submitTask(backgroundTask);
    }

    /**
     *
     * <i>Description:</i>Once all page updates are done, reset the refresh
     * button.
     *
     * @param evt
     */
    @Handler
    protected synchronized void onNoticeTaskStatus(TaskStatusEvent<?> evt) {
        if (evt.isStarted()) {
            refreshCount += 1;
        } else {
            refreshCount -= 1;
        }

        if (refreshCount == 1 && evt.isStarted()) {
            Util.runInEDT(new Runnable() {
                @Override
                public void run() {
                    mainFrame.setRefreshRunning(true);
                }
            });
        } else if (refreshCount == 0) {
            Util.runInEDT(new Runnable() {
                @Override
                public void run() {
                    if (mainFrame != null) {
                        mainFrame.setRefreshRunning(false);
                    }
                }
            });
        }
    }

    /*
     * Initially, an instance of FabricController was able to support multiple
     * subnets; that is, the subnet could change to another. With multiple
     * subnet support, an instance of FabricController is associated with one
     * subnet only, and onRefresh should always use the isReady flag to avoid
     * extra refreshes (if they come from failover)
     */
    public synchronized void onRefresh() {
        if (!mainFrame.isReady()) {
            return;
        }
        isSystemUpdate = true;

        Context context = getContext();
        if (context != null && context.isValid()) {
            checkBackgroundTask();

            mainFrame.setReady(false);
            mainFrame.showProgress(UILabels.STL10110_REFRESHING_PAGES
                    .getDescription(getCurrentSubnet().getName()), true);
            mainFrame.setProgress(0);

            backgroundTotalWork = pageLoadWork;
            backgroundWork = 0.0;
            backgroundTask =
                    new SubnetRefreshTask(model, builder, pages, context);
            backgroundTask.addPropertyChangeListener(this);
            submitTask(backgroundTask);
        } else if (context == null) {
            // This is the case where the Controller was never initialized or
            // there was an error during initialization
            if (backgroundTask != null && !backgroundTask.isDone()) {
                // Initialization is running
                mainFrame.setReady(false);
                mainFrame.showProgress(UILabels.STL10104_INIT_SUBNET
                        .getDescription(subnetName), true);
                backgroundTask.addPropertyChangeListener(this);
            } else {
                // There was an error during initialization
                selectSubnet(subnetName);
            }
        } else {
            checkBackgroundTask();

            selectSubnet(subnetName);
        }

        certsLoginCtr.sslReconnectCleanup();
    }

    private void checkBackgroundTask() {
        if (backgroundTask != null && !backgroundTask.isDone()) {
            backgroundTask.removePropertyChangeListener(this);
            try {
                System.out
                        .println("FabricController cancelling backgroundTask!");
                backgroundTask.cancel(true);
            } catch (CancellationException ce) {
                // If the background task is actually running, here is where we
                // get the CancellationException; ignore it, since that's what
                // we want.
            }
        }
    }

    @Override
    public void onTaskSuccess() {
        // Since the backgroundTask can spawn multiple threads under this same
        // controller, we need to trigger a model changed event only when
        // backgroundTask has finished (and not when the sub threads finish).
        // So we override the triggering of the model change event here and do
        // it in the onTaskSuccess method of the backgroundTask
    }

    @Override
    public void onTaskFailure(Throwable caught) {
        // Same as in onTaskSucess()
    }

    /**
     * This method is invoked on the EDT by the backgroundTask
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (PROGRESS_AMOUNT_PROPERTY == evt.getPropertyName()) {
            double progress = (Double) evt.getNewValue();
            backgroundWork = backgroundWork + progress;
            double percentProgress =
                    (backgroundWork / backgroundTotalWork) * 100;
            // System.out.println("=========== " + percentProgress);
            if (percentProgress > 100) {
                percentProgress = 100.00;
            }

            if (mainFrame != null) {
                mainFrame.setProgress((int) percentProgress);
            }
        } else if (PROGRESS_NOTE_PROPERTY == evt.getPropertyName()) {
            String note = (String) evt.getNewValue();
            if (mainFrame == null) {
                System.out.println("=========== " + note);
                return;
            }
            if (mainFrame != null) {
                mainFrame.setProgressNote(note);
            }
        }
    }

    @Override
    public void selectSubnet(final String subnetName) {
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#doInBackground()
             */
            @Override
            protected Void doInBackground() throws Exception {
                subnetMgr.selectSubnet(subnetName);
                return null;
            }

            /*
             * (non-Javadoc)
             *
             * @see javax.swing.SwingWorker#done()
             */
            @Override
            protected void done() {
                try {
                    get();
                } catch (InterruptedException e) {
                } catch (ExecutionException e) {
                    Util.showError(mainFrame.getView(), e);
                }
            }

        };
        worker.execute();
    }

    @Override
    public void resetConnectMenu() {
        if (mainFrame != null) {
            mainFrame.resetConnectMenu();
        }
    }

    @Override
    public void resetSubnet(SubnetDescription subnet) {
        SubnetDescription currSubnet = model.getCurrentSubnet();
        this.subnetName = subnet.getName();
        if (currSubnet != null) {
            currSubnet.setName(subnetName);
        }
        Context context = getContext();
        if (context != null) {
            context.getSubnetDescription().setName(subnetName);
        }
        this.notifyModelChanged();
    }

    /**
     *
     * Description: clear context for context switch TODO: need to make a
     * decision whether we should stop data collection on old context. Right
     * now, we stop it since we only consider one subnet
     *
     * @param context
     */
    private void clearContext(Context context) {
        context.getTaskScheduler().clear();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.IFabricController#doShowInitScreen()
     */
    @Override
    public void doShowInitScreen(Rectangle bounds, boolean maximized) {
        if (mainFrame != null) {
            mainFrame.showInitScreen(bounds, maximized);
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.IFabricController#doShowMessageAndExit(java.lang
     * .String)
     */
    @Override
    public void doShowMessageAndExit(final String message, final String title) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                if (mainFrame != null) {
                    mainFrame.showMessageAndExit(message, title);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.hpc.stl.ui.IFabricController#doShowErrorsAndExit(java.util.
     * List)
     */
    @Override
    public void doShowErrors(final List<Throwable> errors) {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                if (mainFrame != null) {
                    mainFrame.showErrors(errors);
                }
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.hpc.stl.ui.IFabricController#doShowContent()
     */
    @Override
    public void doShowContent() {
        if (mainFrame != null) {
            mainFrame.showContent(pages);
        }
    }

    public List<SubnetDescription> getSubnets() {
        return subnetMgr.getSubnets();
    }

    @Override
    public void reset() {
        this.subnetName = null;
        try {
            resetView();
            pinBoardCtr.cleanup();
        } finally {
            init();
            eventBus.shutdown();
            eventBus = new MBassador<IAppEvent>(new IPublicationErrorHandler() {
                @Override
                public void handleError(PublicationError error) {
                    log.error(null, error);
                    error.getCause().printStackTrace();
                }
            });
            Context context = getContext();
            if (context != null) {
                clearContext(context);
                setContext(null);
            }
        }
    }

    @Override
    public void doClose() {
        if (mainFrame != null) {
            mainFrame.close();
            mainFrame = null;
        }
    }

    @Override
    public void showSetupWizard(String subnetName) {
        subnetMgr.showSetupWizard(subnetName, this);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.IFabricController#showLoggingConfig()
     */
    @Override
    public void showLoggingConfig() {
        loggingConfigController.showLoggingConfig();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.IFabricController#showEmailSettingsDialog()
     */
    @Override
    public void showEmailSettingsDialog() {
        emailSettingsController.showEmailSettingsDlg((FVMainFrame) mainFrame);
    }

    @Override
    public void addPendingTask(ITask task) {
        pendingTasks.add(task);
        if (mainFrame.isReady()) {
            processPendingTasks();
        }
    }

    public void processPendingTasks() {
        synchronized (pendingTasks) {
            Iterator<ITask> it = pendingTasks.iterator();
            while (it.hasNext()) {
                submitTask(it.next());
                it.remove();
            }
        }
    }

    /**
     *
     * <i>Description:</i> invoked by the view when the UI is closed (running on
     * the EDT).
     *
     */
    public void onWindowClose() {
        resetView(true);
    }

    public void onMenuClose() {
        resetView(false);
    }

    private void resetView(boolean forceWindowClose) {
        try {
            resetView();
        } finally {
            // SubnetManager starts a thread on its own
            subnetMgr.stopSubnet(subnetName, forceWindowClose);
        }
    }

    private void resetView() {
        try {
            if (graphService != null) {
                graphService.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (outlineService != null) {
                outlineService.shutdown();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (IPageController page : pages) {
            page.cleanup();
        }
        pages.clear();

        try {
            model.setCurrentSubnet(null);
            notifyModelChanged();
        } finally {
            if (mainFrame != null) {
                lastBounds = mainFrame.getFrameBounds();
                maximized = mainFrame.isFrameMaximized();
            }
        }
    }

    @Override
    public void bringToFront() {
        if (mainFrame != null) {
            Util.runInEDT(new Runnable() {
                @Override
                public void run() {
                    mainFrame.bringToFront();
                }
            });
        }
    }

    @Override
    public Rectangle getBounds() {
        if (mainFrame == null) {
            return lastBounds;
        }
        return mainFrame.getFrameBounds();
    }

    @Override
    public boolean isMaximized() {
        if (mainFrame == null) {
            return maximized;
        }
        return mainFrame.isFrameMaximized();
    }

    /**
     * <i>Description:</i> invoked by the Subnet Manager when stopping a subnet
     * (running on a non-EDT thread).
     */
    @Override
    public void cleanup() {
        this.subnetName = null;
        eventBus.shutdown();

        if (pinBoardCtr != null) {
            pinBoardCtr.cleanup();
        }
    }

    protected IPageController getPage(String name) {
        for (IPageController page : pages) {
            if (page.getName().equals(name)) {
                return page;
            }
        }
        throw new IllegalArgumentException(
                "Couldn't find page with name '" + name + "'");
    }

    @Override
    public boolean canPageChange(String oldPageId, String newPageId) {
        if (oldPageId != null && !pages.isEmpty()) {
            IPageController oldPage = getPage(oldPageId);
            return oldPage.canExit();
        }
        return true;
    }

    public void selectPage(IPageController page) {
        isSystemUpdate = true;
        mainFrame.setCurrentTab(page);
    }

    @Override
    public void onPageChanged(String oldPageId, String newPageId) {
        IPageController oldPage = null;
        if (oldPageId != null) {
            oldPage = getPage(oldPageId);
            if (oldPage == pages.get(0) && !hasEventTableToggled) {
                mainFrame.hideEventSummaryTable();
            }
            oldPage.onExit();
        }
        IPageController newPage = null;
        if (newPageId != null) {
            newPage = getPage(newPageId);
            if (newPage == pages.get(0) && !hasEventTableToggled) {
                mainFrame.showEventSummaryTable();
            }
            newPage.onEnter();
        }

        if (!isSystemUpdate && !undoHandler.isInProgress()) {
            UndoablePageSelection undoSel =
                    new UndoablePageSelection(this, oldPage, newPage);
            undoHandler.addUndoAction(undoSel);
        }
        if (isSystemUpdate) {
            isSystemUpdate = false;
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.common.IEventSummaryBarListener#showEventSummaryTable()
     */
    @Override
    public void toggleEventSummaryTable() {
        mainFrame.toggleEventSummaryTable();
        if (!hasEventTableToggled) {
            hasEventTableToggled = true;
        }
    }

    /**
     * Description:
     *
     * @param selected
     */
    public void applyRandomValue(boolean selected) {
        model.setAddRandomValues(selected);
        Context context = getContext();
        if (context != null) {
            // apply random values for demo purpose
            context.setRandom(selected);
            context.getPerformanceApi().setRandom(selected);
        }
    }

    public void startSimulatedFailover() {
        Context context = getContext();
        if (context != null && model.getCurrentSubnet() != null) {
            // Start a simulated failover
            SubnetDescription subnet = model.getCurrentSubnet();
            context.getConfigurationApi()
                    .startSimulatedFailover(subnet.getName());
        }
    }

    @Handler
    protected void onJumpToEvent(JumpToEvent event) {
        for (IPageController page : pages) {
            if (page.getName().equals(event.getDestination())) {
                selectPage(page);
                return;
            }
        }
        log.warn("Unsupported destination " + event.getDestination());
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.framework.AbstractController#initModel()
     */
    @Override
    public void initModel() {
    }

    // For testing

    protected List<IPageController> getPages() {
        return pages;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.IFabricController#getTaskScheduler()
     */
    @Override
    public TaskScheduler getTaskScheduler() {

        Context context = getContext();
        TaskScheduler taskScheduler = null;

        if (context != null) {
            taskScheduler = context.getTaskScheduler();
        }

        return taskScheduler;
    }

    @Override
    public void onSubnetManagerConnectionLost(SubnetEvent event) {
        SubnetContext subnetCtx = (SubnetContext) event.getSource();
        SubnetDescription subnet = subnetCtx.getSubnetDescription();
        backgroundWork = 0.0;
        if (subnet != null) {
            backgroundTotalWork = subnet.getFEList().size();
        }
        subnetCtx.addFailoverProgressListener(this);
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                mainFrame.setReady(false);
                mainFrame.setProgressNote("");
                mainFrame.showFailoverProgress(
                        STL10113_CONNECTION_LOST.getDescription(), true);
                mainFrame.setProgress(0);
            }
        });
    }

    @Override
    public void onFailoverCompleted(SubnetEvent event) {
        Context context = getContext();
        Context newContext = context == null ? null
                : subnetMgr.getContext(context.getSubnetDescription());
        if (newContext != null && context != newContext) {
            // special case - failover from a new context
            resetContext(newContext);
            return;
        }

        if (context != null) {
            context.removeFailoverProgressListener(this);
            TaskScheduler ts = context.getTaskScheduler();
            ts.updateRefreshRate(ts.getRefreshRate());
            context.getManagementApi().reset();
        }
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                mainFrame.showFailoverProgress(STL10110_REFRESHING_PAGES
                        .getDescription(getCurrentSubnet().getName()), true);
                mainFrame.setReady(true);
                onRefresh();
            }
        });
    }

    @Override
    public void onFailoverFailed(SubnetEvent event) {
        Context context = getContext();
        if (context != null) {
            context.removeFailoverProgressListener(this);
        }
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                if (mainFrame != null) {
                    mainFrame.showFailoverProgress(null, false);
                    mainFrame.setReady(true);
                    mainFrame.showMessage(
                            STL60009_PRESS_REFRESH.getDescription(),
                            STL60008_CONN_LOST.getDescription());
                    // special case: refresh AdminPage
                    for (IPageController page : pages) {
                        if (page instanceof AdminPage) {
                            page.onRefresh(null);
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onSubnetManagerConnected(SubnetEvent event) {
        // Nothing to do here
    }

    public void cancelFailover() {
        subnetMgr.cancelFailoverFor(subnetName);
    }

    public void showAboutDialog() {
        if (subnetMgr != null) {
            AppInfo appInfo = subnetMgr.getConfigurationApi().getAppInfo();
            String appVersion = appInfo.getOpaFmVersion();
            String buildId = appInfo.getAppBuildId();
            AboutDialog.showAboutDialog((javax.swing.JFrame) mainFrame,
                    appInfo.getAppName(), appVersion, buildId,
                    appInfo.getAppBuildDate());
        }
    }

    /**
     * @return the certsLoginCtr
     */
    @Override
    public CertsLoginController getCertsLoginController() {
        return certsLoginCtr;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.IFabricController#getPinBoardController()
     */
    @Override
    public PinBoardController getPinBoardController() {
        return pinBoardCtr;
    }

    /**
     * @return the undoHandler
     */
    @Override
    public UndoHandler getUndoHandler() {
        return undoHandler;
    }

    public void onHideInactiveNodes(boolean hideInactiveNodes) {
        // Save property state in the AppSettings:
        AppInfo appInfo = subnetMgr.getConfigurationApi().getAppInfo();

        Properties applicationProperties = new Properties();
        applicationProperties.put("hide.inactive.nodes",
                String.valueOf(hideInactiveNodes));
        appInfo.setProperty(PROPERTIES_FM_GUI_APP, applicationProperties);

        subnetMgr.getConfigurationApi().saveAppInfo(appInfo);

        // update UI:
        this.hideInactiveNodes = hideInactiveNodes;
        this.onRefresh();
    }

    @Override
    public boolean getHideInactiveNodes() {
        if (hideInactiveNodes == null) {
            AppInfo appInfo = subnetMgr.getConfigurationApi().getAppInfo();

            Properties appProps = appInfo.getProperty(PROPERTIES_FM_GUI_APP);
            String hideNodes = "false";
            if (appProps != null) {
                hideNodes = (String) appProps.get("hide.inactive.nodes");
            }
            hideInactiveNodes = Boolean.valueOf(hideNodes);
        }
        return hideInactiveNodes;
    }

}
