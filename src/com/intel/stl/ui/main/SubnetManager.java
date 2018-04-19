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

import static com.intel.stl.api.configuration.AppInfo.PROPERTIES_SUBNET_FRAMES;

import java.awt.Component;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import javax.swing.SwingWorker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.intel.stl.api.AppContext;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.ISubnetEventListener;
import com.intel.stl.api.SubnetContext;
import com.intel.stl.api.SubnetEvent;
import com.intel.stl.api.Utils;
import com.intel.stl.api.configuration.AppInfo;
import com.intel.stl.api.configuration.ConfigurationException;
import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.configuration.LoggingConfiguration;
import com.intel.stl.api.configuration.UserNotFoundException;
import com.intel.stl.api.configuration.UserSettings;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetConnectionException;
import com.intel.stl.api.subnet.SubnetDataNotFoundException;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.api.subnet.SubnetDescription.Status;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.framework.IAppEvent;
import com.intel.stl.ui.main.view.FVMainFrame;
import com.intel.stl.ui.main.view.FabricView;
import com.intel.stl.ui.main.view.IFabricView;
import com.intel.stl.ui.main.view.SplashScreen;
import com.intel.stl.ui.publisher.TaskScheduler;
import com.intel.stl.ui.wizards.impl.IWizardListener;
import com.intel.stl.ui.wizards.impl.MultinetWizardController;

import net.engio.mbassy.bus.MBassador;
import net.engio.mbassy.bus.error.IPublicationErrorHandler;
import net.engio.mbassy.bus.error.PublicationError;

public class SubnetManager implements ISubnetManager, ISubnetEventListener {
    private static Logger log = LoggerFactory.getLogger(SubnetManager.class);

    private final static String PROPERTIES_SUBNET_STATE_SUFFIX = "-State";

    private final static double DEFAULT_SCREEN_SIZE_PERCENTAGE = 0.8;

    private static final int FRAME_OFFSET = 20;

    private static final String SM_THREAD_PREFIX = "smthread-";

    private final AtomicInteger threadCount = new AtomicInteger(1);

    private final String userName = "defaultuser";

    private final AppContext appContext;

    private final ICertsAssistant certsAssistant;

    protected final GraphicsDevice[] device;

    private final GraphicsDevice defaultDevice;

    private Rectangle lastBounds;

    protected boolean isFirstRun;

    protected LinkedHashMap<Long, SubnetDescription> subnets;

    // This lock is used whenever the controllers and contexts maps are read or
    // written
    private final Object tablesLock = new Object();

    protected final Map<SubnetDescription, Context> contexts =
            new HashMap<SubnetDescription, Context>();

    protected final Map<SubnetDescription, Rectangle> bounds =
            new HashMap<SubnetDescription, Rectangle>();

    protected final Map<SubnetDescription, Boolean> windowStates =
            new HashMap<SubnetDescription, Boolean>();

    protected final Map<String, Rectangle> savedBounds =
            new HashMap<String, Rectangle>();

    protected final Map<String, Boolean> savedStates =
            new HashMap<String, Boolean>();

    protected final AtomicReference<IFabricController> lastViewer =
            new AtomicReference<IFabricController>(null);

    public SubnetManager(AppContext appContext,
            ICertsAssistant certsAssistant) {
        this.appContext = appContext;
        this.certsAssistant = certsAssistant;
        GraphicsEnvironment ge = getLocalGraphicsEnvironment();
        device = ge.getScreenDevices();
        defaultDevice = ge.getDefaultScreenDevice();
    }

    @Override
    public void init(boolean isFirstRun) {
        this.isFirstRun = isFirstRun;
        loadSubnets();
        loadFrameStates();
    }

    /**
     * Adds a subnet to the set of managed subnets and saves it to the database.
     * This method should be invoked from the EDT
     *
     * @throws SubnetDataNotFoundException
     */
    @Override
    public SubnetDescription saveSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException {
        if (subnet == null) {
            log.error("Attempting to save a null subnet");
            return null;
        }
        log.debug("saveSubnet {}", subnet);
        IConfigurationApi confApi = appContext.getConfigurationApi();
        if (subnet.getSubnetId() == 0) {
            // New subnet definition
            subnet.setLastStatus(Status.INVALID);
            SubnetDescription savedSubnet = confApi.defineSubnet(subnet);
            subnets.put(savedSubnet.getSubnetId(), savedSubnet);
            log.debug("New subnet: ", savedSubnet);
            resetConnectMenus();
            return savedSubnet;
        } else {
            Context context = contexts.get(subnet);
            if (context != null) {
                SubnetDescription ctxSubnet = context.getSubnetDescription();
                if (ctxSubnet.getSubnetId() == subnet.getSubnetId()
                        && !ctxSubnet.getName()
                                .equalsIgnoreCase(subnet.getName())) {
                    IFabricController controller = context.getController();
                    if (controller != null) {
                        controller.resetSubnet(subnet);
                    }
                }
            }

            resetConnectMenus();
            confApi.updateSubnet(subnet);
            return subnet;
        }
    }

    /**
     * Removes a subnet from the set of managed subnets and from the database.
     * This method should be invoked from the EDT
     *
     * @throws SubnetDataNotFoundException
     */
    @Override
    public void removeSubnet(SubnetDescription subnet)
            throws SubnetDataNotFoundException {
        log.debug("removeSubnet {}; subnets = {}", subnet, subnets.size());

        IConfigurationApi confApi = appContext.getConfigurationApi();
        long subnetId = subnet.getSubnetId();
        Context context = contexts.get(subnet);
        if (context != null) {
            context.setDeleted(true);
        }
        removeHost(subnet, false);
        confApi.removeSubnet(subnetId);
        subnets.remove(subnetId);
        resetConnectMenus();
    }

    private void resetConnectMenus() {
        for (Context context : contexts.values()) {
            IFabricController controller = context.getController();
            controller.resetConnectMenu();
        }
        if (lastViewer.get() != null) {
            lastViewer.get().resetConnectMenu();
        }
    }

    /**
     * Saves the userSettings for the specified subnet. This method should be
     * invoked from the EDT.
     */
    @Override
    public void saveUserSettings(String subnetName, UserSettings userSettings) {
        SubnetDescription subnet = getSubnet(subnetName);
        appContext.getConfigurationApi().saveUserSettings(subnetName,
                userSettings);
        Context context = contexts.get(subnet);
        if (context != null) {
            context.refreshUserSettings();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.ISubnetManager#getHostIp(java.lang.String)
     */
    @Override
    public String getHostIp(String hostName) throws SubnetConnectionException {
        return appContext.getConfigurationApi().getHostIp(hostName);
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.ISubnetManager#isReachable(java.lang.String)
     */
    @Override
    public boolean isHostReachable(String hostName) {
        return appContext.getConfigurationApi().isHostReachable(hostName);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.ISubnetManager#isConnectable(com.intel.stl.api.
     * subnet.SubnetDescription)
     */
    @Override
    public boolean isHostConnectable(SubnetDescription subnet)
            throws ConfigurationException {

        return appContext.getConfigurationApi().isHostConnectable(subnet);
    }

    @Override
    public boolean tryToConnect(SubnetDescription subnet)
            throws SubnetConnectionException {
        return appContext.getConfigurationApi().tryToConnect(subnet);
    }

    @Override
    public PMConfigBean getPMConfig(SubnetDescription subnet) {
        return appContext.getConfigurationApi().getPMConfig(subnet);
    }

    @Override
    public SubnetDescription getNewSubnet() {
        SubnetDescription newSubnet = new SubnetDescription();
        newSubnet.setSubnetId(0L);
        return newSubnet;
    }

    /**
     * Starts a FabricViewer for the specified subnet, if not already started.
     * This method is intended to be called from the EDT and therefore it's not
     * synchronized
     */
    @Override
    public void startSubnet(String subnetName)
            throws SubnetConnectionException {
        SubnetDescription subnet = getSubnet(subnetName);
        startSubnet(subnet);
    }

    @Override
    public void stopSubnet(String subnetName, boolean forceWindowClose) {
        log.debug("Stopping subnet '{}'. Existing contexts = {}", subnetName,
                contexts.size());
        try {
            if (subnetName != null) {
                SubnetDescription subnet = getSubnet(subnetName);
                removeHost(subnet, forceWindowClose);
            }
        } finally {
            if (subnetName == null) {
                if (contexts.size() == 0 || !hasRunningSubnetFrames()) {
                    try {
                        saveFrameStates();
                    } finally {
                        shutdownApplication();
                    }
                }
            }
        }
    }

    protected boolean hasRunningSubnetFrames() {
        Frame[] frames = Frame.getFrames();
        for (Frame frame : frames) {
            if (frame.isVisible() && frame.getName().startsWith(
                    STLConstants.K0001_FABRIC_VIEWER_TITLE.getValue())) {
                return true;
            }
        }
        log.debug("No visible subnet frames!");
        return false;
    }

    protected void shutdownApplication() {
        startNewThread(new Runnable() {

            @Override
            public void run() {
                appContext.shutdown();
            }

        });
    }

    /**
     * Selects the specified subnet to display. If a fabric viewer is already
     * showing it, focus is given to the window; if no fabric viewer is
     * available, it's started. This method is supposed to be invoked from the
     * EDT
     */
    @Override
    public void selectSubnet(final String subnetName)
            throws SubnetConnectionException {
        startSubnet(subnetName);
    }

    @Override
    public List<SubnetDescription> getSubnets() {
        return Collections.unmodifiableList(
                new ArrayList<SubnetDescription>(subnets.values()));
    }

    @Override
    public boolean isFirstRun() {
        return isFirstRun;
    }

    @Override
    public UserSettings getUserSettings(String subnetName, String userName)
            throws UserNotFoundException {
        return appContext.getConfigurationApi().getUserSettings(subnetName,
                userName);
    }

    /**
     * Gets the TaskScheduler associated with the specified subnet. If not
     * context has been created, it returns null
     */
    @Override
    public TaskScheduler getTaskScheduler(SubnetDescription subnet) {
        Context context = contexts.get(subnet);
        if (context == null) {
            return null;
        }
        return context.getTaskScheduler();
    }

    @Override
    public void saveLoggingConfiguration(LoggingConfiguration loggingConfig) {
        appContext.getConfigurationApi()
                .saveLoggingConfiguration(loggingConfig);
    }

    @Override
    public LoggingConfiguration getLoggingConfig() {
        return appContext.getConfigurationApi().getLoggingConfig();
    }

    @Override
    public synchronized void startSubnets(SplashScreen splashScreen) {
        final AtomicBoolean splashClosed = new AtomicBoolean(false);
        List<SubnetDescription> toStart = getSubnetsToStart();

        if (toStart.size() == 0 && !isFirstRun) {
            final IFabricController controller = createFabricController(null);
            final Rectangle bounds = getBounds();
            lastViewer.set(controller);
            runInEDT(new Runnable() {
                @Override
                public void run() {
                    controller.doShowInitScreen(bounds, false);
                }
            });
        } else {
            StartSubnetsTask startSubnetsTask =
                    createStartSubnetsTask(toStart, splashScreen);
            startSubnetsTask.execute();
            try {
                startSubnetsTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        if (isFirstRun) {
            final IFabricController controller = createFabricController(null);
            final Rectangle bounds = getBounds();
            lastViewer.set(controller);
            runInEDT(new Runnable() {
                @Override
                public void run() {
                    controller.doShowInitScreen(bounds, false);
                    controller.showSetupWizard(null);
                    isFirstRun = false;
                }
            });
        }
        if (!splashClosed.get()) {
            splashScreen.close();
        }
    }

    @Override
    public void showSetupWizard(String subnetName,
            IFabricController controller) {

        IFabricView mainFrame = controller.getView();
        IWizardListener wizardController;

        wizardController =
                MultinetWizardController.getInstance(mainFrame, this);
        SubnetDescription subnet = null;
        if (subnetName != null && subnetName.length() > 0) {
            subnet = getSubnet(subnetName);
        }
        if (subnet == null) {
            subnet = new SubnetDescription();
        }
        wizardController.showView(subnet, userName, controller);
    }

    @Override
    public void cleanup() {
        for (Context context : contexts.values()) {
            IFabricController controller = null;
            try {
                context.cleanup();
                controller = context.getController();
                if (controller != null) {
                    controller.cleanup();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (controller != null) {
                    closeViewer(controller);
                }
            }
        }
        contexts.clear();
        subnets.clear();
    }

    @Override
    public void clearSubnetFactories(SubnetDescription subnet) {
        certsAssistant.clearSubnetFactories(subnet);
    }

    @Override
    public IConfigurationApi getConfigurationApi() {
        return appContext.getConfigurationApi();
    }

    @Override
    public void onFailoverCompleted(SubnetEvent event) {
        // Nothing to do
    }

    @Override
    public void onFailoverFailed(SubnetEvent event) {
        // Nothing to do
    }

    @Override
    public void onSubnetManagerConnected(SubnetEvent event) {
        SubnetDescription subnet = (SubnetDescription) event.getSource();
        VerifySubnetsTask verifyTask = new VerifySubnetsTask(subnet);
        verifyTask.execute();
    }

    @Override
    public void onSubnetManagerConnectionLost(SubnetEvent event) {
        // Nothing to do
    }

    protected Context createContext(SubnetDescription subnet,
            IFabricController controller) throws Exception {
        try {
            SubnetContext subnetCtx =
                    appContext.getSubnetContextFor(subnet, true);
            subnetCtx.addSubnetEventListener(this);
            return new Context(subnetCtx, controller, userName);
        } catch (Exception e) {
            log.error("Error creating Context for subnet '{}'",
                    subnet.getName(), e);
            throw e;
        }
    }

    private void removeHost(SubnetDescription subnet,
            boolean forceWindowClose) {
        IFabricController controller = null;
        Context context;
        synchronized (tablesLock) {
            context = contexts.remove(subnet);
            if (context != null) {
                controller = context.getController();
            }

            if (controller != null) {
                Rectangle frameBounds = controller.getBounds();
                boolean maximized = controller.isMaximized();
                if (frameBounds != null) {
                    bounds.put(subnet, frameBounds);
                    savedBounds.put(subnet.getName(), frameBounds);
                    windowStates.put(subnet, maximized);
                }
            }
        }
        if (context != null) {
            stopSubnet(controller, context, forceWindowClose);
        }
    }

    private void startSubnet(final SubnetDescription subnet)
            throws SubnetConnectionException {

        List<SubnetDescription> toStart = new ArrayList<SubnetDescription>(1);
        toStart.add(subnet);
        StartSubnetsTask startSubnetsTask =
                createStartSubnetsTask(toStart, null);
        startSubnetsTask.execute();
    }

    private List<SubnetDescription> getSubnetsToStart() {
        List<SubnetDescription> startSubnets =
                new ArrayList<SubnetDescription>(subnets.values());

        List<SubnetDescription> autoConnectSubnets =
                new ArrayList<SubnetDescription>();

        // sort in ascending order by status timestamp
        Collections.sort(startSubnets, new Comparator<SubnetDescription>() {
            @Override
            public int compare(SubnetDescription o1, SubnetDescription o2) {
                return Long.compare(o1.getStatusTimestamp(),
                        o2.getStatusTimestamp());
            }
        });
        // only keep autoconnect subnets
        for (SubnetDescription subnet : subnets.values()) {
            if (subnet.isAutoConnect()) {
                autoConnectSubnets.add(subnet);
            }
        }
        return autoConnectSubnets;
    }

    private void showInitialFrame(String subnetName,
            IFabricController controller) {
        Rectangle bounds = getBounds(subnetName);
        boolean maximized = getMaximized(subnetName);
        showInitScreen(controller, bounds, maximized);
    }

    private void showInitScreen(final IFabricController controller,
            final Rectangle bounds, final boolean maximized) {
        if (controller == null) {
            return;
        }
        runInEDT(new Runnable() {
            @Override
            public void run() {
                controller.doShowInitScreen(bounds, maximized);
            }
        });
    }

    private Rectangle getBounds(String subnetName) {
        if (subnetName == null) {
            return getBounds();
        }
        Rectangle bounds = savedBounds.get(subnetName);
        if (bounds != null) {
            if (!boundsDisplayable(bounds)) {
                bounds = getBounds();
            }
        } else {
            bounds = getBounds();
        }
        return bounds;
    }

    protected GraphicsEnvironment getLocalGraphicsEnvironment() {
        return GraphicsEnvironment.getLocalGraphicsEnvironment();
    }

    protected boolean boundsDisplayable(Rectangle bounds) {
        boolean displayable = false;
        for (int i = 0; i < device.length; i++) {
            if (device[i].getDefaultConfiguration().getBounds()
                    .contains(bounds.getLocation())) {
                displayable = true;
                break;
            }
        }
        return displayable;
    }

    private boolean getMaximized(String subnetName) {
        if (subnetName == null) {
            return false;
        }
        Boolean maximized = savedStates.get(subnetName);
        if (maximized == null) {
            return false;
        }
        return maximized;
    }

    /**
     * Saves the states of all frames that have been opened and that
     * successfully connected
     *
     * Frame state is saved by subnet name, not by host id; the reason is that,
     * when we display a new frame, host connection is not yet established but
     * we need to show the frame in its previous location.
     */
    private void saveFrameStates() {
        Properties subnetFrames = new Properties();

        AppInfo appInfo = appContext.getConfigurationApi().getAppInfo();
        appInfo.setProperty(PROPERTIES_SUBNET_FRAMES, subnetFrames);
        for (Long subnetId : subnets.keySet()) {
            SubnetDescription subnet = subnets.get(subnetId);
            Rectangle frameBounds = bounds.get(subnet);
            Boolean frameMaximized = windowStates.get(subnet);
            if (frameBounds != null && frameMaximized != null) {
                String subnetName = subnet.getName();
                subnetFrames.put(subnetName, subnetName);
                Properties frameLocation = new Properties();
                frameLocation.put("x", frameBounds.x);
                frameLocation.put("y", frameBounds.y);
                frameLocation.put("width", frameBounds.width);
                frameLocation.put("height", frameBounds.height);
                frameLocation.put("state", frameMaximized);
                appInfo.setProperty(subnetName + PROPERTIES_SUBNET_STATE_SUFFIX,
                        frameLocation);
            }
        }
        appContext.getConfigurationApi().saveAppInfo(appInfo);
    }

    private void loadFrameStates() {
        AppInfo appInfo = appContext.getConfigurationApi().getAppInfo();
        Map<String, Properties> appProps = appInfo.getPropertiesMap();
        Properties subnetFrames = appProps.get(PROPERTIES_SUBNET_FRAMES);
        if (subnetFrames != null) {
            for (Object key : subnetFrames.keySet()) {
                String subnetName = (String) key;
                Properties frameProps = appProps
                        .get(subnetName + PROPERTIES_SUBNET_STATE_SUFFIX);
                if (frameProps != null) {
                    try {
                        int x = Integer.parseInt(frameProps.getProperty("x"));
                        int y = Integer.parseInt(frameProps.getProperty("y"));
                        int width = Integer
                                .parseInt(frameProps.getProperty("width"));
                        int height = Integer
                                .parseInt(frameProps.getProperty("height"));
                        boolean maximized = Boolean
                                .parseBoolean(frameProps.getProperty("state"));
                        Rectangle frameBounds =
                                new Rectangle(x, y, width, height);
                        savedBounds.put(subnetName, frameBounds);
                        savedStates.put(subnetName, maximized);
                    } catch (NumberFormatException e) {

                    }
                }
            }
        }

    }

    private Rectangle getBounds() {
        Rectangle frameBounds = null;
        GraphicsConfiguration screenConfig =
                defaultDevice.getDefaultConfiguration();
        Rectangle screenBounds = screenConfig.getBounds();
        if (lastBounds == null) {
            int width =
                    (int) (screenBounds.width * DEFAULT_SCREEN_SIZE_PERCENTAGE);
            int height = (int) (screenBounds.height
                    * DEFAULT_SCREEN_SIZE_PERCENTAGE);
            int x = screenBounds.x + ((screenBounds.width - width) / 2);
            int y = screenBounds.y + ((screenBounds.height - height) / 2);
            frameBounds = new Rectangle(x, y, width, height);
            lastBounds = frameBounds;
        } else {
            int x = lastBounds.x + FRAME_OFFSET;
            int y = lastBounds.y + FRAME_OFFSET;
            frameBounds =
                    new Rectangle(x, y, lastBounds.width, lastBounds.height);
            if (!screenBounds.contains(frameBounds)) {
                if ((x + lastBounds.width) > screenBounds.x
                        + screenBounds.width) {
                    x = screenBounds.x;
                }
                if ((y + lastBounds.height) > screenBounds.y
                        + screenBounds.height) {
                    y = screenBounds.y;
                }
                frameBounds = new Rectangle(x, y, lastBounds.width,
                        lastBounds.height);
            }
            lastBounds = frameBounds;
        }
        return frameBounds;
    }

    private void stopSubnet(final IFabricController controller,
            final Context context, final boolean forceWindowClose) {
        final boolean keepLastViewer = (contexts.size() == 0);
        if (keepLastViewer && controller != null) {
            lastViewer.set(controller);
        }
        final String subnetName = context.getSubnetDescription().getName();
        MDC.put("subnet", subnetName);
        final Map<String, String> configMap = MDC.getCopyOfContextMap();
        MDC.remove("subnet");
        startNewThread(new Runnable() {
            @Override
            public void run() {
                MDC.setContextMap(configMap);
                try {
                    if (context != null) {
                        context.close();
                    }
                } finally {
                    try {
                        if (controller != null) {
                            if (keepLastViewer || !forceWindowClose) {
                                controller.reset();
                            } else {
                                controller.cleanup();
                            }
                        }
                    } finally {
                        if (!keepLastViewer || forceWindowClose) {
                            log.info("Closing viewer for subnet '{}'",
                                    subnetName);
                            closeViewer(controller);
                        }
                    }
                }
            }
        });
    }

    private void startNewThread(Runnable runnable) {
        Thread newThread = new Thread(runnable);
        String threadName = SM_THREAD_PREFIX + threadCount.getAndIncrement();
        newThread.setName(threadName);
        newThread.start();
    }

    private void closeViewer(final IFabricController controller) {
        if (controller == null) {
            return;
        }
        runInEDT(new Runnable() {
            @Override
            public void run() {
                controller.doClose();
            }
        });
    }

    private void loadSubnets() {
        List<SubnetDescription> dbSubnets = appContext.getSubnets();
        if (dbSubnets != null) {
            subnets = new LinkedHashMap<Long, SubnetDescription>();
            for (SubnetDescription dbSubnet : dbSubnets) {
                subnets.put(dbSubnet.getSubnetId(), dbSubnet);
            }
        }
    }

    @SuppressWarnings("unused")
    private void restartViewer(final SubnetDescription subnet,
            final IFabricController controller, String existingHostId,
            String newHostId) {
        RestartViewerTask restarter = new RestartViewerTask(
                "<html>This fabric viewer will restart</html>", this, subnet,
                existingHostId, newHostId);
        controller.addPendingTask(restarter);
    }

    protected IFabricController createFabricController(String subnetName) {
        FabricView view = new FabricView(new FVMainFrame(subnetName));

        MBassador<IAppEvent> eventBus =
                new MBassador<IAppEvent>(new IPublicationErrorHandler() {
                    @Override
                    public void handleError(PublicationError error) {
                        log.error(null, error);
                        error.getCause().printStackTrace();
                    }
                });
        IFabricController controller =
                new FabricController(subnetName, view, this, eventBus);
        return controller;
    }

    @Override
    public SubnetDescription getSubnet(String subnetName) {
        for (SubnetDescription subnet : subnets.values()) {
            if (subnet.getName().equalsIgnoreCase(subnetName)) {
                return subnet;
            }
        }
        IllegalArgumentException iae = new IllegalArgumentException(
                "Cannot find subnet with the name '" + subnetName + "'");
        throw iae;
    }

    /**
     * This class is used to start a list of subnets. Fabric viewers are being
     * created and displayed and any connectivity issues are shown in the
     * progress bar (including failover progress if needed).
     */
    /*-
     * The general sequence to start a controller is as follows:
     * 1) Create the FabricController; get it to display on screen (showInit)
     * 2) Create the Context for the subnet; any errors while creating the context should be shown on the displayed FabricController
     * 3) Start the FabricController; at this point, we store both the FabricController and the Context for general use
     *
     */
    protected class StartSubnetsTask
            extends SwingWorker<Void, SubnetDescription> {

        private final List<SubnetDescription> subnets;

        private final SplashScreen splashScreen;

        private final List<Throwable> errors = new ArrayList<Throwable>();

        private IFabricController lastController;

        private boolean splashClosed = false;

        private final Map<String, String> loggingContextMap;

        public StartSubnetsTask(List<SubnetDescription> subnets,
                SplashScreen splashScreen) {
            this.subnets = subnets;
            this.splashScreen = splashScreen;
            // Just making sure the Context Map is created
            MDC.put("subnet", "all");
            MDC.remove("subnet");
            loggingContextMap = MDC.getCopyOfContextMap();
        }

        @Override
        protected Void doInBackground() throws Exception {
            MDC.setContextMap(loggingContextMap);
            for (SubnetDescription subnet : subnets) {
                try {
                    Context context;
                    IFabricController controller;
                    IFabricController newController = null;
                    // Update tables
                    synchronized (tablesLock) {
                        context = contexts.get(subnet);
                        if (context == null) {
                            // First, create a new Context and a new
                            // FabricController (just in case doing so fails)
                            IFabricController tempController;
                            IFabricController lastController = lastViewer.get();
                            if (lastController != null && lastController
                                    .getCurrentContext() == null) {
                                if (lastViewer.compareAndSet(lastController,
                                        null)) {
                                    tempController = lastController;
                                } else {
                                    newController = createFabricController(
                                            subnet.getName());
                                    tempController = newController;
                                }
                            } else {
                                newController = createFabricController(
                                        subnet.getName());
                                tempController = newController;
                            }
                            // Now put them together so that there is no chance
                            // that one is there without the other
                            Context newContext =
                                    createContext(subnet, tempController);
                            contexts.put(subnet, newContext);
                            newContext.addSubnetEventListener(tempController);
                            this.lastController = tempController;
                        }
                    }
                    if (context == null) {
                        if (newController != null) {
                            showInitialFrame(subnet.getName(), newController);
                        }
                        publish(subnet);
                    } else {
                        if (!context.isValid()) {
                            controller = context.getController();
                            context = createContext(subnet, controller);
                            synchronized (tablesLock) {
                                contexts.put(subnet, context);
                            }
                            context.addSubnetEventListener(controller);
                            publish(subnet);
                        } else {
                            checkSubnetAlreadyDisplayed(subnet.getName(),
                                    context);
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    errors.add(e);
                }
            }
            return null;
        }

        /*-
         * Note that done runs on the EDT
         * @see http://docs.oracle.com/javase/7/docs/api/javax/swing/SwingWorker.html#done()
         */
        @Override
        protected void done() {
            if (errors.size() > 0) {
                if (lastController == null) {
                    lastController = createFabricController(null);
                    showInitialFrame(null, lastController);
                }
                Util.showErrors((Component) lastController.getView(), errors);
            }
        }

        /*-
         * Note that process runs on the EDT
         * @see http://docs.oracle.com/javase/7/docs/api/javax/swing/SwingWorker.html#process(java.util.List)
         */
        @Override
        protected void process(List<SubnetDescription> subnets) {
            for (SubnetDescription subnet : subnets) {
                log.info("Starting subnet {}", subnet.getName());
                Context context;
                IFabricController controller = null;
                context = contexts.get(subnet);
                if (context != null) {
                    controller = context.getController();
                }
                if (controller != null) {
                    controller.doShowContent();
                    controller.initializeContext(context);
                }
                if (!splashClosed) {
                    if (splashScreen != null) {
                        splashScreen.close();
                    }
                    splashClosed = true;
                }
            }
        }

        private void checkSubnetAlreadyDisplayed(String subnetName,
                Context context) {
            IFabricController controller = context.getController();
            controller.bringToFront();
            SubnetDescription targetSubnet = context.getSubnetDescription();
            if (!subnetName.equalsIgnoreCase(targetSubnet.getName())) {
                log.debug("Subnet " + subnetName
                        + " definition resolves to this subnet "
                        + targetSubnet.getName());
                StringBuffer sb = new StringBuffer();
                sb.append("Specified subnet '");
                sb.append(subnetName);
                sb.append("' resolves to this subnet '");
                sb.append(targetSubnet.getName());
                sb.append("'");
                ShowWarningTask warn = new ShowWarningTask(sb.toString());
                controller.addPendingTask(warn);
            }
        }
    }

    private class VerifySubnetsTask
            extends SwingWorker<Void, SubnetDescription> {

        private final SubnetDescription connectedSubnet;

        public VerifySubnetsTask(SubnetDescription connectedSubnet) {
            this.connectedSubnet = connectedSubnet;
        }

        @Override
        protected Void doInBackground() throws Exception {
            HostInfo hostInfo = connectedSubnet.getCurrentFE();
            String connIp = hostInfo.getInetAddress().getHostAddress();
            log.info("Verifying subnet '{}' connected to FE at {}",
                    connectedSubnet.getName(), connIp);
            for (SubnetDescription subnet : contexts.keySet()) {
                if (!connectedSubnet.equals(subnet)) {
                    HostInfo currHost = subnet.getCurrentFE();
                    String currIp = currHost.getInetAddress().getHostAddress();
                    if (connIp.equals(currIp)
                            && hostInfo.getPort() == currHost.getPort()) {
                        System.out.println("Connected subnet " + connectedSubnet
                                + "(" + connIp
                                + ") connects to the same FE than subnet "
                                + subnet + ")");
                    }
                }
            }
            return null;
        }

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.ISubnetManager#onEmailTest(com.intel.stl.api.subnet
     * .SubnetDescription, java.lang.String)
     */
    @Override
    public void onEmailTest(String recipients) {

        if ((recipients != null) && (recipients.isEmpty() == false)) {
            String subject =
                    UILabels.STL92001_TEST_EMAIL_SUBJECT.getDescription();
            String body = "";
            List<String> recipientsList = Utils.concatenatedStringToList(
                    recipients, UIConstants.MAIL_LIST_DELIMITER);
            getConfigurationApi().submitMessage(subject, body, recipientsList);
        }
    }

    @Override
    public Context getContext(SubnetDescription subnetDescription) {
        return contexts.get(subnetDescription);
    }

    // Used in testing
    protected StartSubnetsTask createStartSubnetsTask(
            List<SubnetDescription> subnets, SplashScreen splashScreen) {
        return new StartSubnetsTask(subnets, splashScreen);
    }

    // Used in testing
    protected void runInEDT(Runnable runnable) {
        Util.runInEDT(runnable);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.main.ISubnetManager#cancelFailoverFor(java.lang.String)
     */
    @Override
    public void cancelFailoverFor(String subnetName) {
        SubnetDescription subnet = getSubnet(subnetName);
        Context context = contexts.get(subnet);
        if (context != null) {
            context.cancelFailover();
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.main.ISubnetManager#validateEmail(java.lang.String)
     */
    @Override
    public boolean isEmailValid(String email) {
        return getConfigurationApi().isEmailValid(email);
    }

}
