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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JPanel;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.ComponentFactory;

public class CredentialsGlassPanel extends JPanel {
    
    private static final long serialVersionUID = -5353883824268956748L;

    private CertsPanel certsPanel;

    private JButton cancelBtn, okBtn;

    public CredentialsGlassPanel() {
        super();
        initComponents();
    }

    protected void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UIConstants.INTEL_BLUE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));
        setBackground(UIConstants.INTEL_WHITE);

        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.TRAILING));
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder());
        buttonsPanel.setOpaque(false);
        buttonsPanel.setBackground(UIConstants.INTEL_WHITE);

        // this is cancel button
        cancelBtn =
                ComponentFactory.getIntelCancelButton(STLConstants.K0621_CANCEL
                        .getValue());
        buttonsPanel.add(cancelBtn);

        // this is ok button
        okBtn =
                ComponentFactory.getIntelActionButton(STLConstants.K0645_OK
                        .getValue());
        buttonsPanel.add(okBtn);

        // Make buttons same width.
        JButton btnGroup[] = { okBtn, cancelBtn };
        ComponentFactory.makeSameWidthButtons(btnGroup);

        certsPanel = new CertsPanel();
        add(certsPanel, BorderLayout.CENTER);
        add(buttonsPanel, BorderLayout.SOUTH);
    }

    public void setCancelAction(ActionListener listener) {
        cancelBtn.addActionListener(listener);
    }

    public void setOkAction(ActionListener listener) {
        okBtn.addActionListener(listener);
    }

    public void reset() {
        certsPanel.reset();
    }

    public String getKeyStoreLocation() {
        return certsPanel.getKeyStoreLocation();
    }

    public String getTrustStoreLocation() {
        return certsPanel.getTrustStoreLocation();
    }

    public char[] getKeyStorePwd() {
        return certsPanel.getKeyStorePwd();
    }

    public char[] getTrustStorePwd() {
        return certsPanel.getTrustStorePwd();
    }

    public void setKeyStoreLocation(String keyStoreFile) {
        certsPanel.setKeyStoreLocation(keyStoreFile);
    }

    public void setTrustStoreLocation(String trustStoreFile) {
        certsPanel.setTrustStoreLocation(trustStoreFile);
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
        certsPanel.setTrustStoreLocError(errorMessage);
    }
}

