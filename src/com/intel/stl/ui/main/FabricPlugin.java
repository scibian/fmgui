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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.AppContext;
import com.intel.stl.api.FMGuiPlugin;
import com.intel.stl.api.ICertsAssistant;
import com.intel.stl.api.StringUtils;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.DialogFactory;
import com.intel.stl.ui.main.view.CertsPanel;
import com.intel.stl.ui.main.view.SplashScreen;

public class FabricPlugin extends FMGuiPlugin {
    private final static Logger log =
            LoggerFactory.getLogger(FMGuiPlugin.class);

    private SplashScreen splashScreen;

    private ICertsAssistant certsAssistant;

    private ISubnetManager subnetMgr;

    private final List<Throwable> errors = new ArrayList<Throwable>();

    @Override
    public void init(AppContext appContext) {
        CertsPanel certsPanel = new CertsPanel();
        certsAssistant = new CertsAssistant(certsPanel,
                appContext.getConfigurationApi());
        appContext.registerCertsAssistant(certsAssistant);
        super.init(appContext);

        try {
            // enable anti-aliased text
            System.setProperty("awt.useSystemAAFontSettings", "on");
            System.setProperty("swing.aatext", "true");

            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            if (System.getProperty("os.name").equals("Linux")) {
                UIManager.setLookAndFeel(
                        UIManager.getCrossPlatformLookAndFeelClassName());
            }
            UIManager.put("SplitPaneDivider.draggingColor",
                    UIConstants.INTEL_LIGHT_GRAY);

            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
            System.setProperty("sun.awt.exception.handler",
                    ExceptionHandler.class.getName());

            HelpAction.DYNAMIC_SIZE = true;

        } catch (Exception e) {
            errors.add(e);
            e.printStackTrace();
        }
        splashScreen = new SplashScreen();
        splashScreen.showScreen();
    }

    @Override
    public void invokeMain(final boolean firstRun) {
        splashScreen.setProgress("Initializing UI", 99);
        try {
            subnetMgr = createSubnetManager();
            subnetMgr.init(firstRun);
        } catch (Throwable e) {
            errors.add(e);
            e.printStackTrace();
        }
        // Up to this point, the plugin handles errors. Now the SubnetManager
        // should handle errors and route them to the proper frame
        if (errors.isEmpty()) {
            subnetMgr.startSubnets(splashScreen);
        } else {
            StringBuffer msg = new StringBuffer();
            for (Throwable e : errors) {
                msg.append(StringUtils.getErrorMessage(e));
            }
            throw new RuntimeException(msg.toString());
        }
    }

    protected ISubnetManager createSubnetManager() {
        AppContext appContext = getAppContext();

        return new SubnetManager(appContext, certsAssistant);
    }

    @Override
    public void setProgress(int progress) {
        splashScreen.setProgress(progress);
    }

    @Override
    public void setProgress(String message) {
        splashScreen.setProgress(message);
    }

    @Override
    public void setProgress(String message, int progress) {
        splashScreen.setProgress(message, progress);
    }

    @Override
    public void showErrors(List<Throwable> errors) {
        for (Throwable e : errors) {
            e.printStackTrace();
        }
        if (!splashScreen.isClosed()) {
            splashScreen.toBack();
        }
        DialogFactory.showModalErrorDialog(null, errors);
    }

    @Override
    public void shutdown() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                if (!splashScreen.isClosed()) {
                    splashScreen.close();
                }
                splashScreen = new SplashScreen();
                splashScreen.setShutdownImage();
                splashScreen.showScreen();
                if (subnetMgr != null) {
                    try {
                        subnetMgr.cleanup();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    class ExceptionHandler implements Thread.UncaughtExceptionHandler {
        @Override
        public void uncaughtException(Thread t, Throwable e) {
            log.error(e.getMessage() + " @ " + t, e);
            if (IS_DEV) {
                showErrors(Arrays.asList(e));
            }
        }
    }
}
