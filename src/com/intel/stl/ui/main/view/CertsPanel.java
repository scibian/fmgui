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

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;

public class CertsPanel extends JPanel {
    private static final long serialVersionUID = 1430723933878902327L;

    /**
     * We share file chooser between keyStore and trustStore, so we can share
     * file directory to make our users lives easier.
     */
    private JFileChooser chooser;

    private StorePanel keyStorePanel;

    private StorePanel trustStorePanel;

    public CertsPanel() {
        super();
        initComponents();
    }

    protected void initComponents() {
        setBackground(UIConstants.INTEL_WHITE);
        
        setLayout(new GridLayout(2, 1, 0, 5));
        chooser = new JFileChooser();

        keyStorePanel =
                new StorePanel(STLConstants.K2001_KEY_STORE.getValue(), chooser);
        add(keyStorePanel);

        trustStorePanel =
                new StorePanel(STLConstants.K2002_TRUST_STORE.getValue(),
                        chooser);
        add(trustStorePanel);
    }

    public void reset() {
        keyStorePanel.reset();
        trustStorePanel.reset();
    }

    public void setKeyStoreLocation(String location) {
        keyStorePanel.setStoreLocation(location);
    }

    public String getKeyStoreLocation() {
        return keyStorePanel.getStoreLocation();
    }

    public char[] getKeyStorePwd() {
        return keyStorePanel.getStorePassword();
    }

    public void setKeyStoreLocError(String msg) {
        keyStorePanel.setLocError(msg);
    }

    public void setKeyStorePwdError(String msg) {
        keyStorePanel.setPwdError(msg);
    }

    public void setTrustStoreLocation(String location) {
        trustStorePanel.setStoreLocation(location);
    }

    public String getTrustStoreLocation() {
        return trustStorePanel.getStoreLocation();
    }

    public char[] getTrustStorePwd() {
        return trustStorePanel.getStorePassword();
    }

    public void setTrustStoreLocError(String msg) {
        trustStorePanel.setLocError(msg);
    }

    public void setTrustStorePwdError(String msg) {
        trustStorePanel.setPwdError(msg);
    }
    

    class StorePanel extends JPanel {
        private static final long serialVersionUID = -4039456864583682621L;

        private final String title;

        private JTextField location;

        private JLabel locErrLabel;

        private ErrorCorrectListener locDocListener;

        private JButton browseBtn;

        private JPasswordField password;

        private JLabel pwdErrLabel;

        private ErrorCorrectListener pwdDocListener;

        private JFileChooser chooser;

        public StorePanel(String title, JFileChooser chooser) {
            super();
            this.title = title;
            this.chooser = chooser;
            initComponents();
            installListeners();
        }

        protected void initComponents() {
            setBackground(UIConstants.INTEL_WHITE);
            
            GridBagLayout gridBag = new GridBagLayout();
            setLayout(gridBag);

            GridBagConstraints gc = new GridBagConstraints();
            gc.fill = GridBagConstraints.BOTH;
            gc.insets = new Insets(2, 2, 8, 2);

            gc.gridx = gc.gridy = 0;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            JLabel label = ComponentFactory.getH4Label(title, Font.BOLD);
            label.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0,
                    UIConstants.INTEL_ORANGE));
            add(label, gc);

            gc.gridy += 1;
            gc.gridwidth = 1;
            gc.insets = new Insets(2, 10, 1, 2);
            label =
                    ComponentFactory.getFieldLabel(STLConstants.K0641_FILE_LOC
                            .getValue());
            add(label, gc);

            gc.weightx = 1;
            gc.gridx += 1;
            gc.insets = new Insets(2, 2, 1, 5);
            location = new JTextField(24);
            location.setBackground(UIConstants.INTEL_WHITE);
            add(location, gc);

            gc.weightx = 0;
            gc.gridx += 1;
            gc.insets = new Insets(2, 2, 1, 2);
            browseBtn =
                    ComponentFactory.getImageButton(UIImages.FOLDER_ICON
                            .getImageIcon());
            browseBtn.setToolTipText(STLConstants.K0642_BROWSE.getValue());
            browseBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    if (chooser == null) {
                        chooser = new JFileChooser();
                    }
                    String fileLoc = location.getText();
                    if (!fileLoc.isEmpty()) {
                        File file = new File(fileLoc);
                        chooser.setCurrentDirectory(file.getParentFile());
                    }
                    int returnVal = chooser.showOpenDialog(StorePanel.this);
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        location.setText(chooser.getSelectedFile()
                                .getAbsolutePath());
                    }
                }
            });
            add(browseBtn, gc);

            gc.gridx = 1;
            gc.gridy += 1;
            gc.insets = new Insets(1, 2, 2, 2);
            gc.gridwidth = GridBagConstraints.REMAINDER;
            locErrLabel = cerateErrLabel();
            add(locErrLabel, gc);

            gc.gridx = 0;
            gc.gridy += 1;
            gc.gridwidth = 1;
            gc.insets = new Insets(4, 10, 1, 2);
            label =
                    ComponentFactory.getFieldLabel(STLConstants.K1049_PASSWORD
                            .getValue());
            add(label, gc);

            gc.gridx += 1;
            gc.fill = GridBagConstraints.NONE;
            gc.anchor = GridBagConstraints.WEST;
            gc.insets = new Insets(4, 2, 1, 5);
            gc.weightx = 1;
            gc.gridwidth = GridBagConstraints.REMAINDER;
            password = new JPasswordField(12);
            password.setBackground(UIConstants.INTEL_WHITE);
            add(password, gc);

            gc.gridx = 1;
            gc.gridy += 1;
            gc.insets = new Insets(1, 2, 2, 2);
            pwdErrLabel = cerateErrLabel();
            password.getDocument().addDocumentListener(
                    new ErrorCorrectListener(this, password, pwdErrLabel));
            add(pwdErrLabel, gc);
        }

        protected JLabel cerateErrLabel() {
            JLabel label = ComponentFactory.getFieldContent(" ");
            label.setForeground(UIConstants.INTEL_DARK_RED);
            return label;
        }

        protected void installListeners() {
            locDocListener =
                    new ErrorCorrectListener(this, location, locErrLabel);
            location.getDocument().addDocumentListener(locDocListener);

            pwdDocListener =
                    new ErrorCorrectListener(this, password, pwdErrLabel);
            password.getDocument().addDocumentListener(pwdDocListener);
        }

        public void reset() {
            location.setText(null);
            locDocListener.clearError();
            password.setText(null);
            pwdDocListener.clearError();
        }
        
        public void setLocError(String msg) {
            locErrLabel.setText(msg);
            location.setBackground(UIConstants.INTEL_LIGHT_RED);
            revalidate();
        }

        public void setPwdError(String msg) {
            pwdErrLabel.setText(msg);
            password.setBackground(UIConstants.INTEL_LIGHT_RED);
            revalidate();
        }

        public void setStoreLocation(String loc) {
            location.setText(loc);
        }

        public String getStoreLocation() {
            return location.getText();
        }

        public char[] getStorePassword() {
            char[] res = password.getPassword();
            return res;
        }
    }

    class ErrorCorrectListener implements DocumentListener {
        private final JPanel parent;

        private final JTextField field;

        private final JLabel errorLabel;

        /**
         * Description:
         * 
         * @param filed
         * @param errorLabel
         */
        public ErrorCorrectListener(JPanel parent, JTextField field,
                JLabel errorLabel) {
            super();
            this.parent = parent;
            this.field = field;
            this.errorLabel = errorLabel;
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.DocumentListener#insertUpdate(javax.swing.event
         * .DocumentEvent)
         */
        @Override
        public void insertUpdate(DocumentEvent e) {
            clearError();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.DocumentListener#removeUpdate(javax.swing.event
         * .DocumentEvent)
         */
        @Override
        public void removeUpdate(DocumentEvent e) {
            clearError();
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * javax.swing.event.DocumentListener#changedUpdate(javax.swing.event
         * .DocumentEvent)
         */
        @Override
        public void changedUpdate(DocumentEvent e) {
            clearError();
        }

        protected void clearError() {
            if (!errorLabel.getText().isEmpty()) {
                errorLabel.setText(" ");
                field.setBackground(UIConstants.INTEL_WHITE);
                parent.revalidate();
            }
        }
    }
}
