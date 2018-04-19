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

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.VerticalLayout;

import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.admin.view.DeployHostPanel.DeployState;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.WidgetName;
import com.intel.stl.ui.common.view.ComponentFactory;

public class DeployPanel extends JPanel {
    private static final long serialVersionUID = -7256068752960371918L;

    private DeployHostPanel masterSM;

    private JCheckBox sameCredential;

    private JScrollPane smsScrollPane;

    private JPanel standbySMsPanel;

    private JLabel addBtn;

    private JButton deployBtn;

    private JButton backBtn;

    private final List<DeployHostPanel> otherSMs =
            new ArrayList<DeployHostPanel>();

    private IDeployListener listener;

    private DocumentListener passwordListener;

    private DocumentListener userListener;

    public DeployPanel() {
        super();
        setBackground(UIConstants.INTEL_WHITE);
        initComponent();
    }

    protected void initComponent() {
        setLayout(new GridBagLayout());

        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(2, 2, 2, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.weightx = 1;
        gc.gridwidth = GridBagConstraints.REMAINDER;

        gc.insets = new Insets(20, 2, 20, 2);
        JLabel label = ComponentFactory.getH2Label(
                UILabels.STL81110_DEPLOY_MSG.getDescription(), Font.ITALIC);
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setForeground(UIConstants.INTEL_BLUE);
        add(label, gc);

        label = ComponentFactory
                .getH3Label(STLConstants.K0025_MASTER_SM.getValue(), Font.BOLD);
        add(label, gc);

        int indent = 25;
        gc.insets = new Insets(2, indent, 2, 2);
        masterSM = new DeployHostPanel();
        masterSM.addPasswordDocListener(getPasswordListener());
        masterSM.addUserDocListener(getUserListener());
        add(masterSM, gc);

        // gc.insets = new Insets(2, indent + 2, 2, 2);
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.EAST;
        sameCredential = ComponentFactory.getIntelCheckBox(
                UILabels.STL81113_APPLY_CREDENTIAL.getDescription());
        sameCredential.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                masterSM.removePasswordDocListener(getPasswordListener());
                masterSM.removeUserDocListener(getUserListener());
                if (sameCredential.isSelected()) {
                    applyCredential();
                    masterSM.addPasswordDocListener(getPasswordListener());
                    masterSM.addUserDocListener(getUserListener());
                }
            }
        });
        sameCredential.setSelected(true);
        sameCredential
                .setName(WidgetName.ADMIN_VF_DEPLOY_SAME_CREDENTIAL.name());
        add(sameCredential, gc);

        gc.insets = new Insets(2, 2, 2, 2);
        gc.fill = GridBagConstraints.HORIZONTAL;
        label = ComponentFactory.getH3Label(
                STLConstants.K0059_STANDBY_SMS.getValue(), Font.BOLD);
        add(label, gc);

        gc.insets = new Insets(2, indent, 2, 2);
        standbySMsPanel = new JPanel(new VerticalLayout());
        standbySMsPanel.setBackground(UIConstants.INTEL_WHITE);
        standbySMsPanel.setBorder(BorderFactory.createEmptyBorder(5, 2, 5, 7));
        standbySMsPanel.setName(WidgetName.ADMIN_VF_DEPLOY_STANDBY_SMS.name());
        smsScrollPane = new JScrollPane(standbySMsPanel);
        smsScrollPane.setPreferredSize(new Dimension(400, 400));
        add(smsScrollPane, gc);

        gc.weightx = 0;
        gc.fill = GridBagConstraints.NONE;
        gc.anchor = GridBagConstraints.WEST;
        addBtn = ComponentFactory.getH3Label(
                UILabels.STL81114_ADD_SM.getDescription(), Font.PLAIN);
        addBtn.setForeground(UIConstants.INTEL_BLUE);
        final Border border1 = BorderFactory.createEmptyBorder(2, 4, 3, 4);
        final Border border2 = BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0,
                        UIConstants.INTEL_GRAY),
                BorderFactory.createEmptyBorder(2, 4, 2, 4));
        addBtn.setBorder(border1);
        addBtn.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                addHostEntry(null);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                addBtn.setBorder(border2);
            }

            @Override
            public void mouseExited(MouseEvent e) {
                addBtn.setBorder(border1);
            }

        });
        addBtn.setName(WidgetName.ADMIN_VF_DEPLOY_ADD_NEW_STANDBY_SM.name());
        add(addBtn, gc);

        gc.anchor = GridBagConstraints.EAST;
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        deployBtn = ComponentFactory
                .getIntelActionButton(STLConstants.K2131_DEPLOY.getValue());
        deployBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (deployBtn.getText()
                        .equals(STLConstants.K2131_DEPLOY.getValue())) {
                    if (listener != null) {
                        listener.onDeploy(masterSM, otherSMs);
                        deployBtn.setText(STLConstants.K0621_CANCEL.getValue());
                        backBtn.setEnabled(false);
                    }
                } else {
                    if (listener != null) {
                        listener.onCancel();
                    }
                }
            }

        });
        deployBtn.setName(WidgetName.ADMIN_VF_DEPLOY_DEPLOY.name());
        panel.add(deployBtn);

        backBtn = ComponentFactory
                .getIntelDeleteButton(STLConstants.K0624_BACK.getValue());
        backBtn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                listener.onClose();
            }

        });
        backBtn.setName(WidgetName.ADMIN_VF_DEPLOY_BACK.name());
        panel.add(backBtn);
        add(panel, gc);
    }

    protected DocumentListener getPasswordListener() {
        if (passwordListener == null) {
            passwordListener = new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    applyPassword();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    applyPassword();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    applyPassword();
                }

                protected void applyPassword() {
                    char[] password = masterSM.getPassword();
                    for (DeployHostPanel hp : otherSMs) {
                        hp.setPassword(password);
                        hp.repaint();
                    }
                }

            };
        }
        return passwordListener;
    }

    protected DocumentListener getUserListener() {
        if (userListener == null) {
            userListener = new DocumentListener() {

                @Override
                public void insertUpdate(DocumentEvent e) {
                    applyUserName();
                }

                @Override
                public void removeUpdate(DocumentEvent e) {
                    applyUserName();
                }

                @Override
                public void changedUpdate(DocumentEvent e) {
                    applyUserName();
                }

                protected void applyUserName() {
                    String name = masterSM.getUser();
                    for (DeployHostPanel hp : otherSMs) {
                        hp.setUser(name);
                        hp.repaint();
                    }
                }

            };
        }
        return userListener;
    }

    public void setSubnet(SubnetDescription subnet) {
        HostInfo hostInfo = subnet.getCurrentFE();
        masterSM.setHostInfo(hostInfo);
        masterSM.setState(DeployState.NONE);

        standbySMsPanel.removeAll();
        List<HostInfo> fes = subnet.getFEList();
        for (HostInfo fe : fes) {
            if (fe != hostInfo) {
                addHostEntry(fe);
            }
        }

        deployBtn.setText(STLConstants.K2131_DEPLOY.getValue());
    }

    public void setDeployListener(IDeployListener listener) {
        this.listener = listener;
    }

    public void setFinished() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                deployBtn.setText(STLConstants.K2131_DEPLOY.getValue());
                backBtn.setEnabled(true);
            }
        });
    }

    /**
     * <i>Description:</i>
     *
     */
    protected void applyCredential() {
        String name = masterSM.getUser();
        char[] password = masterSM.getPassword();
        for (DeployHostPanel hp : otherSMs) {
            hp.setUser(name);
            hp.setPassword(password);
            hp.repaint();
        }
    }

    protected void addHostEntry(HostInfo hostInfo) {
        final DeployHostPanel hp = new DeployHostPanel(hostInfo);
        hp.setRemoveAction(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                DeployState state = hp.getState();
                if (state != DeployState.SUCCESS
                        && state != DeployState.RUNNING) {
                    standbySMsPanel.remove(hp);
                    otherSMs.remove(hp);
                    revalidate();
                }
            }
        });
        hp.setState(DeployState.EDIT);
        if (sameCredential.isSelected()) {
            String name = masterSM.getUser();
            char[] password = masterSM.getPassword();
            hp.setUser(name);
            hp.setPassword(password);
        }
        standbySMsPanel.add(hp);
        otherSMs.add(hp);
        revalidate();
        standbySMsPanel.scrollRectToVisible(hp.getBounds());
    }

}
