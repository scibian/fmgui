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

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.SSLStoreCredentialsDeniedException;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.DialogFactory;
import com.intel.stl.ui.main.view.CredentialsGlassPanel;
import com.intel.stl.ui.main.view.FVMainFrame;

public class CertsLoginController {
    private final FabricController controller;

    private final FVMainFrame mainFrame;

    private final CredentialsGlassPanel certsPanel;

    private Component oldGlassComp;

    private final Object waitingForUserResponse = new Object();

    private CertsDescription connectionCerts = null;

    private int buttonPressed = -1;

    public static final int MAX_TRIES = 5;

    private int sslAttempts = MAX_TRIES;

    public CertsLoginController(FabricController controller,
            FVMainFrame mainFrame, CredentialsGlassPanel panel) {
        super();
        this.controller = controller;
        this.mainFrame = mainFrame;
        certsPanel = panel;
        certsPanel.setCancelAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cancelAction();
            }
        });
        certsPanel.setOkAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                okAction();
            }
        });
    }

    public void sslReconnectCleanup() {
        buttonPressed = -1;
        sslAttempts = MAX_TRIES;
        certsPanel.reset();
    }

    public CertsDescription getSSLCredentials(final HostInfo hostInfo)
            throws SSLStoreCredentialsDeniedException {

        if (sslAttempts < 1) {
            sslAttempts = MAX_TRIES;
            certsPanel.reset();
            controller.onMenuClose();
            throw new SSLStoreCredentialsDeniedException(
                    UILabels.STL50050_CONNECTION_FAIL);
        }

        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                showCredentialsPanel(hostInfo);
            }
        });

        synchronized (waitingForUserResponse) {
            while (buttonPressed < 0) {
                try {
                    waitingForUserResponse.wait();

                } catch (InterruptedException e) {
                } catch (Exception e1) {
                    e1.printStackTrace();
                }
            }
        }

        if (buttonPressed == DialogFactory.CANCEL_OPTION) {
            sslAttempts = MAX_TRIES;
            certsPanel.reset();
            controller.onMenuClose();
            throw new SSLStoreCredentialsDeniedException(
                    UILabels.STL10114_USER_CANCELLED);
        }

        hostInfo.setCertsDescription(connectionCerts);

        // prep for next iteration
        buttonPressed = -1;

        return connectionCerts;
    }

    public void showCredentialsPanel(final HostInfo hostInfo) {
        final CertsDescription currentCerts = hostInfo.getCertsDescription();
        certsPanel.setKeyStoreLocation(currentCerts.getKeyStoreFile());
        certsPanel.setTrustStoreLocation(currentCerts.getTrustStoreFile());
        oldGlassComp = mainFrame.installGlassPanel(certsPanel);
    }

    private void cancelAction() {
        buttonPressed = DialogFactory.CANCEL_OPTION;
        sslAttempts = 0;
        // this.clear();
        synchronized (waitingForUserResponse) {
            waitingForUserResponse.notify();
        }

    }

    private void okAction() {
        buttonPressed = DialogFactory.OK_OPTION;
        connectionCerts =
                new CertsDescription(certsPanel.getKeyStoreLocation(),
                        certsPanel.getTrustStoreLocation());
        connectionCerts.setKeyStorePwd(certsPanel.getKeyStorePwd());
        connectionCerts.setTrustStorePwd(certsPanel.getTrustStorePwd());

        // Swap back to the progress glass panel so that life can go on
        if (oldGlassComp != null) {
            mainFrame.installGlassPanel(oldGlassComp);
        }

        sslAttempts = sslAttempts - 1;
        synchronized (waitingForUserResponse) {
            waitingForUserResponse.notify();
        }

    }

    public void setKeyStorePwdError(String errorMessage) {
        certsPanel.setKeyStorePwdError(errorMessage);
    }

    public void setKeyStoreLocError(String errorMessage) {
        certsPanel.setKeyStoreLocError(errorMessage);
    }

    public void setTrustStorePwdError(String errorMessage) {
        certsPanel.setTrustStorePwdError(errorMessage);
    }

    public void setTrustStoreLocError(String errorMessage) {
        certsPanel.setTrustStorePwdError(errorMessage);
    }
}
