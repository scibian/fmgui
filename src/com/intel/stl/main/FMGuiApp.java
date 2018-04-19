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

package com.intel.stl.main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.FMGui;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.AppDataUtils;
import com.intel.stl.common.STLMessages;
import com.intel.stl.configuration.AppComponentRegistry;
import com.intel.stl.configuration.AppConfig;
import com.intel.stl.configuration.AppConfigurationException;
import com.intel.stl.datamanager.DatabaseManager;

/**
 */
public class FMGuiApp {
    private static Logger log;

    private final static String PARM_FIRSTRUN = "-firstrun";

    private static boolean firstrunSet = false;

    private static boolean firstRun;

    private Thread mainThread = null;

    private AppComponentRegistry registry;

    private final List<Throwable> errors = new ArrayList<Throwable>();

    public FMGuiApp() {
        mainThread = Thread.currentThread();
    }

    public boolean isFirstRun() {
        List<SubnetDescription> subnets = null;
        try {
            DatabaseManager dbMgr = registry.getDatabaseManager();
            if (dbMgr != null) {
                subnets = dbMgr.getSubnets();
            } else {
                errors.add(new RuntimeException("No Database Manager"));
            }
        } catch (Exception e) {
            errors.add(e);
        }
        return subnets == null || subnets.isEmpty();
    }

    protected void initApp() {
        AppDataUtils.initializeLogging();
        log = LoggerFactory.getLogger(FMGuiApp.class);
        char[] delim = new char[80];
        Arrays.fill(delim, '=');
        log.info(new String(delim));
        log.info("Starting application " + getClass().getName() + " at "
                + (new Date()) + " on " + System.getProperty("os.name"));
        registry = AppConfig.getAppComponentRegistry();
        try {
            registry.initialize();
        } catch (AppConfigurationException e) {
            log.error("Application configuration exception", e);
            errors.add(e);
        } catch (Throwable e) {
            log.error("Application configuration exception", e);
            errors.add(e);
        }
    }

    /**
     * @return the errors
     */
    public List<Throwable> getErrors() {
        return errors;
    }

    public void addShutdownHook(final Callable<Void> hook) {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                log.debug("Starts shutdown hook " + hook);
                try {
                    hook.call();
                } catch (Exception e1) {
                    e1.printStackTrace();
                } finally {
                    try {
                        mainThread.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void run() {
        addShutdownHook(new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                try {
                    registry.shutdown();
                } finally {
                    System.out.println("Any shutdown cleanup goes here!");
                    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
                    for (Thread thread : threadSet) {
                        if (thread != mainThread
                                && thread != Thread.currentThread()) {
                            // System.out.println("interrupt "+thread);
                            thread.interrupt();
                        }
                    }
                }
                return null;
            }
        });

        initApp();

        FMGui ui = registry.getUIComponent();
        if (errors.isEmpty()) {
            try {
                if (!firstrunSet) {
                    firstRun = isFirstRun();
                }
                ui.invokeMain(firstRun);
            } catch (Exception e) {
                errors.add(e);
            }
        }
        if (!errors.isEmpty()) {
            if (ui != null) {
                ui.setProgress(
                        STLMessages.STL10010_ERROR_DURING_INIT.getDescription(),
                        100);
                ui.showErrors(errors);
                ui.shutdown();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        System.exit(1);
                    }
                });
            } else {
                System.err.println(STLMessages.STL10010_ERROR_DURING_INIT
                        .getDescription());
                for (Throwable error : errors) {
                    System.err.println("  " + error.getMessage());
                }
                System.exit(1);
            }
        }
    }

    public static void main(String[] args) {
        for (String param : args) {
            if (PARM_FIRSTRUN.equalsIgnoreCase(param)) {
                firstRun = true;
                firstrunSet = true;
            }
        }
        FMGuiApp app = new FMGuiApp();
        app.run();
    }

}
