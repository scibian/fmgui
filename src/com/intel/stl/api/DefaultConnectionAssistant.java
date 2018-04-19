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

import java.util.concurrent.Exchanger;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.common.STLMessages;

public class DefaultConnectionAssistant implements IConnectionAssistant {

    public static final int MAX_TRIES = 5;

    public static final long TIME_OUT = 30000; // 30 sec

    private JPanel certPanel;

    private JTextField keyFileField;

    private JPasswordField keyPwdField;

    private JTextField trustFileField;

    private JPasswordField trustPwdField;

    @Override
    public CertsDescription getSSLStoreCredentials(HostInfo hostInfo)
            throws SSLStoreCredentialsDeniedException {
        final CertsDescription certs = hostInfo.getCertsDescription();
        if (certs.hasPwds()) {
            return certs;
        }
        final Exchanger<CertsDescription> xchgr =
                new Exchanger<CertsDescription>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                CertsDescription newCerts = getNewCerts(certs);
                try {
                    xchgr.exchange(newCerts, TIME_OUT, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        CertsDescription result = null;
        try {
            result = xchgr.exchange(null);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (result == null) {
            throw new SSLStoreCredentialsDeniedException(
                    STLMessages.STL20001_CONNECTION_ERROR);
        }
        return result;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.api.IConnectionAssistant#onSSLStoreError(com.intel.stl.
     * api.FMException)
     */
    @Override
    public void onSSLStoreError(final FMException fmException) {
        final Exchanger<String> xchgr = new Exchanger<String>();
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                JOptionPane.showMessageDialog(null,
                        StringUtils.getErrorMessage(fmException), "Error",
                        JOptionPane.ERROR_MESSAGE);
                try {
                    xchgr.exchange("Ok pressed in Dialog", TIME_OUT,
                            TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (TimeoutException e) {
                    e.printStackTrace();
                }
            }
        });
        try {
            xchgr.exchange("");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private CertsDescription getNewCerts(CertsDescription certs) {
        if (certPanel == null) {
            createCertPanel(certs);
        }
        String[] options = new String[] { "OK", "Cancel" };
        int option =
                JOptionPane.showOptionDialog(null, certPanel,
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

    private void createCertPanel(CertsDescription certs) {
        certPanel = new JPanel();
        BoxLayout layout = new BoxLayout(certPanel, BoxLayout.Y_AXIS);
        certPanel.setLayout(layout);

        JLabel label =
                new JLabel(STLMessages.STL61002_KEY_STORE_LOC.getDescription());
        certPanel.add(label);
        keyFileField = new JTextField();
        certPanel.add(keyFileField);

        label = new JLabel(STLMessages.STL61003_KEY_STORE_PWD.getDescription());
        certPanel.add(label);
        keyPwdField = new JPasswordField();
        certPanel.add(keyPwdField);

        label =
                new JLabel(
                        STLMessages.STL61004_TRUST_STORE_LOC.getDescription());
        certPanel.add(label);
        trustFileField = new JTextField();
        certPanel.add(trustFileField);

        label =
                new JLabel(
                        STLMessages.STL61005_TRUST_STORE_PWD.getDescription());
        certPanel.add(label);
        trustPwdField = new JPasswordField();
        certPanel.add(trustPwdField);

        if (certs != null) {
            keyFileField.setText(certs.getKeyStoreFile());
            trustFileField.setText(certs.getTrustStoreFile());
        }
    }

}
