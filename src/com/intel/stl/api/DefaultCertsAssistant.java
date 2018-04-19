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

package com.intel.stl.api;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicReference;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.TrustManagerFactory;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.common.STLMessages;

public class DefaultCertsAssistant implements ICertsAssistant {

    public static final int MAX_TRIES = 5;

    public static final long TIME_OUT = 30000; // 30 sec

    private CertsDescription currentCerts;

    private KeyManagerFactory kmf;

    private TrustManagerFactory tmf;

    private JPanel certPanel;

    private JTextField keyFileField;

    private JPasswordField keyPwdField;

    private JTextField trustFileField;

    private JPasswordField trustPwdField;

    public DefaultCertsAssistant() {
    }

    @Override
    public SSLEngine getSSLEngine(SubnetDescription subnet) throws Exception {
        // HostInfo in the SubnetDescription may change during fail over, so it
        // must be retrieved from the description.
        HostInfo host = subnet.getCurrentFE();
        return getSSLEngine(host);
    }

    @Override
    public SSLEngine getSSLEngine(HostInfo host) throws Exception {
        String hostname = host.getHost();
        int port = host.getPort();
        KeyManagerFactory kmf = getKeyManagerFactory(host);
        TrustManagerFactory tmf = getTrustManagerFactory(host);
        SSLEngine engine = Utils.createSSLEngine(hostname, port, kmf, tmf);
        return engine;
    }

    @Override
    public KeyManagerFactory getKeyManagerFactory(SubnetDescription subnet) {
        if (subnet == null) {
            return null;
        }
        return getKeyManagerFactory(subnet.getCurrentFE());
    }

    public KeyManagerFactory getKeyManagerFactory(HostInfo host) {
        CertsDescription certs = host.getCertsDescription();
        if (certs == null) {
            return null;
        }
        checkCerts(certs);
        return kmf;
    }

    @Override
    public TrustManagerFactory getTrustManagerFactory(SubnetDescription subnet) {
        if (subnet == null) {
            return null;
        }
        return getTrustManagerFactory(subnet.getCurrentFE());
    }

    public TrustManagerFactory getTrustManagerFactory(HostInfo host) {
        CertsDescription certs = host.getCertsDescription();
        if (certs == null) {
            return null;
        }
        checkCerts(certs);
        return tmf;
    }

    @Override
    public void clearSubnetFactories(SubnetDescription subnet) {
        if (subnet != null) {
            CertsDescription certs =
                    subnet.getCurrentFE().getCertsDescription();
            if (certs != null && certs.equals(currentCerts)) {
                kmf = null;
                tmf = null;
            }
        }
    }

    private synchronized void checkCerts(CertsDescription certs) {
        // We need to serialize updates to this class fields (this.kmf, this.tmf
        // and this.currentCerts) so that multiple threads do not step on each
        // other.
        if (!certs.equals(currentCerts)) {
            showPanel(certs);
        }
    }

    private void showPanel(final CertsDescription certs) {
        int tries = MAX_TRIES;
        kmf = null;
        tmf = null;

        final Exchanger<CertsDescription> xchgr =
                new Exchanger<CertsDescription>();
        final List<Throwable> errors = new ArrayList<Throwable>();
        final AtomicReference<CertsDescription> currCerts =
                new AtomicReference<CertsDescription>(certs);
        while (tries > 0) {
            if (!errors.isEmpty() || !currCerts.get().hasPwds()) {
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        CertsDescription newCerts =
                                getNewCerts(currCerts.get());
                        try {
                            xchgr.exchange(newCerts, TIME_OUT,
                                    TimeUnit.MILLISECONDS);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (TimeoutException e) {
                            e.printStackTrace();
                        }
                    }
                });
                // Here, we wait indefinitely for the CertsAssistant to respond
                try {
                    currCerts.set(xchgr.exchange(null));
                } catch (InterruptedException e) {
                    return;
                }
            }
            if (currCerts.get() == null) {
                System.out.println("Certs is null");
                return;
            }

            errors.clear();
            KeyManagerFactory kmf = null;
            try {
                kmf =
                        Utils.createKeyManagerFactory(currCerts.get()
                                .getKeyStoreFile(), currCerts.get()
                                .getKeyStorePwd());
                this.kmf = kmf;
            } catch (Exception e) {
                errors.add(e);
                showErrorMessage(e);
            } finally {
                currCerts.get().clearKeyPwd();
            }

            TrustManagerFactory tmf = null;
            try {
                tmf =
                        Utils.createTrustManagerFactory(currCerts.get()
                                .getTrustStoreFile(), currCerts.get()
                                .getTrustStorePwd());
                this.tmf = tmf;
            } catch (Exception e) {
                errors.add(e);
                showErrorMessage(e);
            } finally {
                currCerts.get().clearTrustPwd();
            }

            if (errors.isEmpty() && kmf != null && tmf != null) {
                currentCerts = currCerts.get();
                break;
            }

            tries -= 1;
        }

    }

    private void showErrorMessage(final Exception e) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        StringUtils.getErrorMessage(e), "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    private CertsDescription getNewCerts(CertsDescription certs) {
        JPanel panel = getCertPanel(certs);
        String[] options = new String[] { "OK", "Cancel" };
        int option =
                JOptionPane.showOptionDialog(null, panel,
                        STLMessages.STL61001_CERT_CONF.getDescription(),
                        JOptionPane.NO_OPTION, JOptionPane.PLAIN_MESSAGE, null,
                        options, options[0]);
        if (option == 0) {
            CertsDescription newCerts =
                    new CertsDescription(keyFileField.getText(),
                            trustFileField.getText());
            newCerts.setKeyStorePwd(keyPwdField.getPassword());
            newCerts.setTrustStorePwd(trustPwdField.getPassword());
            return newCerts;
        } else {
            return null;
        }
    }

    private JPanel getCertPanel(CertsDescription certs) {
        if (certPanel == null) {
            certPanel = new JPanel();
            BoxLayout layout = new BoxLayout(certPanel, BoxLayout.Y_AXIS);
            certPanel.setLayout(layout);

            JLabel label =
                    new JLabel(
                            STLMessages.STL61002_KEY_STORE_LOC.getDescription());
            certPanel.add(label);
            keyFileField = new JTextField();
            certPanel.add(keyFileField);

            label =
                    new JLabel(
                            STLMessages.STL61003_KEY_STORE_PWD.getDescription());
            certPanel.add(label);
            keyPwdField = new JPasswordField();
            certPanel.add(keyPwdField);

            label =
                    new JLabel(
                            STLMessages.STL61004_TRUST_STORE_LOC
                                    .getDescription());
            certPanel.add(label);
            trustFileField = new JTextField();
            certPanel.add(trustFileField);

            label =
                    new JLabel(
                            STLMessages.STL61005_TRUST_STORE_PWD
                                    .getDescription());
            certPanel.add(label);
            trustPwdField = new JPasswordField();
            certPanel.add(trustPwdField);
        }

        if (certs != null) {
            keyFileField.setText(certs.getKeyStoreFile());
            trustFileField.setText(certs.getTrustStoreFile());
        }
        return certPanel;
    }

}
