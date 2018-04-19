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

package com.intel.stl.ui.admin.view;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.text.DecimalFormat;

import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.FieldPair;
import com.intel.stl.ui.common.view.SafeNumberField;
import com.intel.stl.ui.common.view.SafeTextField;

public class DeployHostPanel extends JPanel implements MouseListener {
    private static final long serialVersionUID = 2530929163645777773L;

    private JCheckBox selectBox;

    private SafeTextField hostField;

    private SafeNumberField<Integer> portField;

    private SafeTextField userField;

    private JPasswordField passwordField;

    private JLabel stateLabel;

    private JLabel msgLabel;

    private ActionListener removeAction;

    private DeployState state;

    public DeployHostPanel() {
        this(null);
    }

    public DeployHostPanel(HostInfo hostInfo) {
        super();
        initComponent();
        if (hostInfo != null) {
            setHost(hostInfo.getHost());
            setPort(hostInfo.getSshPortNum());
            setUser(hostInfo.getSshUserName());
        }
    }

    protected void initComponent() {
        setLayout(new GridBagLayout());
        setOpaque(false);

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridwidth = 1;

        selectBox = ComponentFactory.getIntelCheckBox("");
        selectBox.setSelected(true);
        selectBox.setName(WidgetName.ADMIN_VF_DEPLOY_SELECT_BOX.name());
        add(selectBox, gc);

        gc.weightx = 1;
        JPanel panel = new JPanel(new GridLayout(1, 4));
        panel.setOpaque(false);
        panel.add(getHostField());
        panel.add(getPortField());
        panel.add(getUserField());
        panel.add(getPasswordField());
        add(panel, gc);

        gc.weightx = 0;
        gc.gridwidth = GridBagConstraints.REMAINDER;
        stateLabel = new JLabel();
        stateLabel.addMouseListener(this);
        stateLabel.setName(WidgetName.ADMIN_VF_DEPLOY_STATE_ICON.name());
        add(stateLabel, gc);

        gc.weightx = 1;
        msgLabel = ComponentFactory.getH5Label("", Font.PLAIN);
        msgLabel.setForeground(UIConstants.INTEL_DARK_ORANGE);
        msgLabel.setName(WidgetName.ADMIN_VF_DEPLOY_STATE_MESSAGE.name());
        add(msgLabel, gc);
    }

    protected FieldPair<?> getHostField() {
        String hostNameChars = UIConstants.DIGITS + UIConstants.LETTERS + "-.";
        hostField = new SafeTextField(false, 253);
        hostField.setValidChars(hostNameChars);
        hostField.setName(WidgetName.ADMIN_VF_DEPLOY_HOST.name());
        return new FieldPair<SafeTextField>(STLConstants.K0051_HOST.getValue(),
                hostField);
    }

    protected FieldPair<?> getPortField() {
        portField = new SafeNumberField<Integer>(new DecimalFormat("###"), 0,
                false, 65535, false);
        // only positive integer
        portField.setValidChars(UIConstants.DIGITS);
        portField.setText("22");
        portField.setName(WidgetName.ADMIN_VF_DEPLOY_PORT_NUMBER.name());
        return new FieldPair<SafeNumberField<Integer>>(
                STLConstants.K0427_PORT_NUMBER.getValue(), portField);
    }

    protected FieldPair<?> getUserField() {
        userField = new SafeTextField(false, 32);
        userField.setName(WidgetName.ADMIN_VF_DEPLOY_USER_NAME.name());
        return new FieldPair<SafeTextField>(
                STLConstants.K0602_USER_NAME.getValue(), userField);
    }

    protected FieldPair<?> getPasswordField() {
        passwordField =
                ComponentFactory.createPasswordField((DocumentListener[]) null);
        passwordField.setName(WidgetName.ADMIN_VF_DEPLOY_PASSWORD.name());
        return new FieldPair<JPasswordField>(
                STLConstants.K1049_PASSWORD.getValue(), passwordField);
    }

    public void setHostInfo(HostInfo hostInfo) {
        if (hostInfo != null) {
            msgLabel.setText(null);
            setHost(hostInfo.getHost());
            setPort(hostInfo.getSshPortNum());
            setUser(hostInfo.getSshUserName());
        }
    }

    public HostInfo getHostInfo() {
        HostInfo res = new HostInfo();
        res.setHost(getHost());
        res.setSshPortNum(Integer.parseInt(getPort()));
        res.setSshUserName(getUser());
        return res;
    }

    public void setHost(String name) {
        hostField.setText(name);
    }

    public String getHost() {
        return hostField.getText();
    }

    public void setPort(int port) {
        portField.setText(Integer.toString(port));
    }

    public String getPort() {
        return portField.getText();
    }

    public void setUser(String name) {
        userField.setText(name);
    }

    public String getUser() {
        return userField.getText();
    }

    public void setPassword(char[] password) {
        passwordField.setText(new String(password));
    }

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public void addPasswordDocListener(DocumentListener listener) {
        passwordField.getDocument().addDocumentListener(listener);
    }

    public void removePasswordDocListener(DocumentListener listener) {
        passwordField.getDocument().removeDocumentListener(listener);
    }

    public void addUserDocListener(DocumentListener listener) {
        userField.getDocument().addDocumentListener(listener);
    }

    public void removeUserDocListener(DocumentListener listener) {
        userField.getDocument().removeDocumentListener(listener);
    }

    public void clearPassword() {
        DocumentListener[] listeners =
                ((AbstractDocument) passwordField.getDocument())
                        .getDocumentListeners();
        for (DocumentListener listener : listeners) {
            passwordField.getDocument().removeDocumentListener(listener);
        }
        passwordField.setText(null);
        for (DocumentListener listener : listeners) {
            passwordField.getDocument().addDocumentListener(listener);
        }
    }

    public void setState(DeployState state) {
        this.state = state;
        stateLabel.setIcon(state.getIcon());
        if (state == DeployState.RUNNING) {
            msgLabel.setText(null);
        }
        selectBox.setSelected(state != DeployState.SUCCESS);
    }

    /**
     * @return the state
     */
    public DeployState getState() {
        return state;
    }

    public void setErrorMessage(String msg) {
        setState(DeployState.ERROR);
        msgLabel.setText(msg);
    }

    public void setRemoveAction(ActionListener listener) {
        removeAction = listener;
    }

    /**
     * <i>Description:</i>
     *
     * @return
     */
    public boolean isSelected() {
        return selectBox.isSelected();
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        if (removeAction != null) {
            removeAction.actionPerformed(
                    new ActionEvent(e.getSource(), e.getID(), e.paramString()));
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mousePressed(java.awt.event.MouseEvent)
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * java.awt.event.MouseListener#mouseReleased(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseReleased(MouseEvent e) {
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseEntered(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        if (state != DeployState.SUCCESS && state != DeployState.RUNNING) {
            stateLabel.setIcon(UIImages.CLOSE_RED.getImageIcon());
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {
        if (state != DeployState.SUCCESS && state != DeployState.RUNNING) {
            stateLabel.setIcon(state.getIcon());
        }
    }

    public static enum DeployState {
        NONE(null),
        EDIT(UIImages.CLOSE_GRAY.getImageIcon()),
        RUNNING(UIImages.RUNNING.getImageIcon()),
        ERROR(UIImages.WARNING_ICON.getImageIcon()),
        SUCCESS(UIImages.CHECK_MARK.getImageIcon());

        private Icon icon;

        /**
         * Description:
         *
         * @param icon
         */
        private DeployState(Icon icon) {
            this.icon = icon;
        }

        /**
         * @return the icon
         */
        public Icon getIcon() {
            return icon;
        }

    }

}
