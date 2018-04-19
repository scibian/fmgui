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

package com.intel.stl.ui.wizards.view.subnet;

// Java imports
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.jdesktop.swingx.VerticalLayout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.CertsDescription;
import com.intel.stl.api.subnet.HostInfo;
import com.intel.stl.api.subnet.SubnetDescription;
import com.intel.stl.ui.common.DocumentDirtyListener;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.common.view.JScrollablePanel;
import com.intel.stl.ui.wizards.impl.IWizardTask;
import com.intel.stl.ui.wizards.impl.subnet.ISubnetControl;
import com.intel.stl.ui.wizards.model.MultinetWizardModel;
import com.intel.stl.ui.wizards.model.subnet.SubnetModel;
import com.intel.stl.ui.wizards.view.AbstractTaskView;
import com.intel.stl.ui.wizards.view.IMultinetWizardView;
import com.intel.stl.ui.wizards.view.IWizardView;
import com.intel.stl.ui.wizards.view.MultinetWizardView;

/**
 * View for the Subnet Wizard
 */
public class SubnetWizardView extends AbstractTaskView implements ISubnetView,
        IHostInfoListener {

    private static final long serialVersionUID = -4237965084816535532L;

    private static Logger log = LoggerFactory.getLogger(SubnetWizardView.class);

    private JPanel pnlHostContainer;

    private final List<HostInfoPanel> hostPanelList =
            new ArrayList<HostInfoPanel>();

    private JCheckBox chkboxAutoConnect;

    private boolean dirty;

    private ISubnetControl subnetControlListener;

    @SuppressWarnings("unused")
    private IWizardTask subnetWizardControlListener;

    private DocumentListener isDirtyListener;

    private DocumentListener setDirtyListener;

    @SuppressWarnings("unused")
    private IWizardView wizardViewListener = null;

    private IMultinetWizardView multinetWizardViewListener = null;

    private final IHostInfoListener hostInfoListener = this;

    private final JFileChooser chooser;

    /**
     * Main constructor. Creates a subnet dialog and sets this objects tree to
     * the parents tree.
     * 
     * @param frame
     *            parent Frame
     * @param title
     *            title for this dialog
     * @param tree
     *            parent tree to add subnet to
     * @param modal
     *            modality of this dialog.
     */
    public SubnetWizardView(IWizardView wizardViewListener) {
        super("");
        this.wizardViewListener = wizardViewListener;
        this.chooser = new JFileChooser();
        dirty = false;
    }

    public SubnetWizardView(IMultinetWizardView wizardViewListener) {
        super("");
        this.multinetWizardViewListener = wizardViewListener;
        this.chooser = new JFileChooser();
        dirty = false;

        createDocumentListener();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.AbstractTaskView#getOptionComponent()
     */
    @Override
    protected JComponent getOptionComponent() {

        JLabel lblConnectionType =
                ComponentFactory.getH5Label(
                        STLConstants.K3034_CONNECTION_TYPE.getValue(),
                        Font.BOLD);

        chkboxAutoConnect =
                ComponentFactory
                        .getIntelCheckBox(STLConstants.K0604_AUTO_CONNECT
                                .getValue());
        chkboxAutoConnect.setText(STLConstants.K0604_AUTO_CONNECT.getValue());
        chkboxAutoConnect.setFont(UIConstants.H5_FONT.deriveFont(Font.PLAIN));
        chkboxAutoConnect.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                setDirty();
            }
        });

        // Create the optionPane
        JPanel optionPane = new JPanel(new BorderLayout());
        optionPane.setOpaque(true);
        optionPane.setBackground(MultinetWizardView.WIZARD_COLOR);

        // // Add a title panel
        // optionPane.add(getTitlePanel(), BorderLayout.NORTH);

        // Create the container to hold the host info
        pnlHostContainer = new JScrollablePanel();
        pnlHostContainer.setLayout(new VerticalLayout(10));
        pnlHostContainer.setBackground(MultinetWizardView.WIZARD_COLOR);
        pnlHostContainer.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        pnlHostContainer.add(HostAdderPanel.getInstance(this));

        // Put the host info container panel on a scrollpane
        JScrollPane scrpnHostInfo = new JScrollPane(pnlHostContainer);
        scrpnHostInfo.getViewport().setBackground(
                MultinetWizardView.WIZARD_COLOR);
        scrpnHostInfo
                .setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrpnHostInfo.getViewport().setViewSize(new Dimension(100, 100));
        scrpnHostInfo.setBorder(null);
        // scrpnHostInfo.setBorder(BorderFactory.createLineBorder(
        // UIConstants.INTEL_BORDER_GRAY, 1));
        optionPane.add(scrpnHostInfo, BorderLayout.CENTER);

        // Create the panel with the connection type
        JPanel pnlConnectionType = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pnlConnectionType.setBackground(MultinetWizardView.WIZARD_COLOR);
        pnlConnectionType.add(lblConnectionType);
        pnlConnectionType.add(chkboxAutoConnect);

        // Create the connection panel and add the connection test and type
        JPanel pnlConnection = new JPanel();
        pnlConnection.setLayout(new BoxLayout(pnlConnection, BoxLayout.Y_AXIS));
        pnlConnection.add(pnlConnectionType);
        optionPane.add(pnlConnection, BorderLayout.SOUTH);

        return optionPane;
    }

    protected JPanel getTitlePanel() {
        JPanel pnlTitle = new JPanel();
        pnlTitle.setBackground(MultinetWizardView.WIZARD_COLOR);
        pnlTitle.setLayout(new BoxLayout(pnlTitle, BoxLayout.X_AXIS));

        pnlTitle.add(Box.createHorizontalStrut(45));
        pnlTitle.add(ComponentFactory.getH5Label(
                STLConstants.K3037_FE_CONNECTION.getValue(), Font.BOLD));

        pnlTitle.add(Box.createHorizontalStrut(98));
        pnlTitle.add(ComponentFactory.getH5Label(
                STLConstants.K3038_SECURITY_INFO.getValue(), Font.BOLD));

        pnlTitle.add(Box.createHorizontalStrut(74));
        pnlTitle.add(ComponentFactory.getH5Label(
                STLConstants.K3033_CONNECTION_TEST.getValue(), Font.BOLD));

        return pnlTitle;
    }

    public void stopConnectionTest() {
        for (HostInfoPanel pnl : hostPanelList) {
            pnl.stopConnectionTest();
        }
    }

    public void setConnectionStatus(String subnetName, String status) {

        boolean found = false;
        HostInfoPanel hostInfoPanel = null;
        Iterator<HostInfoPanel> it = hostPanelList.iterator();

        while (!found && it.hasNext()) {
            hostInfoPanel = it.next();
            found = hostInfoPanel.getHostName().equals(subnetName);
        }

        if (found) {
            hostInfoPanel.setConnectionStatus(status);
        }
    }

    /**
     * 
     * <i>Description: Document listeners to detect when changes occur to the
     * subnet wizard fields</i>
     * 
     */
    protected void createDocumentListener() {
        isDirtyListener = new DocumentDirtyListener() {

            @Override
            public void setDirty(DocumentEvent e) {
                dirty = true;
            }

        };

        setDirtyListener = new DocumentDirtyListener() {

            @Override
            public void setDirty(DocumentEvent e) {
                setDirtyFlag();
            }

        };
    }

    private void setDirtyFlag() {
        setDirty();
        return;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#getDocumentListeners
     * ()
     */
    @Override
    public DocumentListener[] getDocumentListeners() {
        return new DocumentListener[] { isDirtyListener, setDirtyListener };
    }

    // Overridden in case caller tries to make us visible
    // after we've been killed:
    @Override
    public void setVisible(boolean b) {
        super.setVisible(b);
    }

    public void enableNext(boolean enable) {
    }

    public boolean isAutoConnected() {
        return chkboxAutoConnect.isSelected();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#resetPanel()
     */
    @Override
    public void setSubnet(SubnetDescription subnet) {

    }

    @Override
    public void setDirty() {
        dirty = true;

        int hostNameCount = 0;
        int portNumCount = 0;
        int secureCount = 0;
        int keyStoreCount = 0;
        int trustStoreCount = 0;
        int hostPanelCount = hostPanelList.size();
        for (HostInfoPanel pnl : hostPanelList) {
            if (pnl.isHostNamePopulated()) {
                hostNameCount++;
            }

            if (pnl.isPortNumPopulated()) {
                portNumCount++;
            }

            if (pnl.isSecureConnection()) {
                secureCount++;
                if (pnl.isKeyStorePopulated()) {
                    keyStoreCount++;
                }

                if (pnl.isTrustStorePopulated()) {
                    trustStoreCount++;
                }
            }
        }

        boolean basicRequirement =
                ((hostNameCount == hostPanelCount) && (portNumCount == hostPanelCount));

        boolean secureRequirement =
                ((keyStoreCount == secureCount) && (trustStoreCount == secureCount));

        if ((multinetWizardViewListener.getSubnetName().length() > 0)
                && (hostPanelCount > 0)
                && (basicRequirement && secureRequirement)) {

            multinetWizardViewListener.enableNext(true);
            multinetWizardViewListener.enableApply(true);
            multinetWizardViewListener.enableReset(true);
        } else {
            multinetWizardViewListener.enableNext(false);
            multinetWizardViewListener.enableApply(false);
            multinetWizardViewListener.enableReset(true);
        }
    }

    public void clearPanel() {
        pnlHostContainer.removeAll();

        dirty = false;
    }

    @Override
    public void resetPanel() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#isDirty()
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.ITaskView#setDirty(boolean)
     */
    @Override
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
        multinetWizardViewListener.enableApply(dirty);
        multinetWizardViewListener.enableReset(dirty);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.ITaskView#update(com.intel.stl.ui.wizards
     * .model.MultinetWizardModel)
     */
    @Override
    public void updateView(final MultinetWizardModel model) {

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                SubnetModel subnetModel = model.getSubnetModel();
                SubnetDescription subnet = subnetModel.getSubnet();

                if (subnet != null) {
                    update(subnetModel);
                }
                dirty = false;
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.ISubnetView#update(com.intel.stl
     * .ui.wizards.model.subnet.SubnetModel)
     */
    @Override
    public void update(SubnetModel subnetModel) {
        HostInfo host = null;
        SubnetDescription subnet = subnetModel.getSubnet();

        try {
            host = subnet.getCurrentFE();
        } catch (IllegalArgumentException e) {
            Util.showError(this, e);
        }

        hostPanelList.clear();
        for (HostInfo hostInfo : subnet.getFEList()) {
            HostInfoPanel pnlHostInfo = new HostInfoPanel(this, chooser);
            pnlHostInfo.setCurrentMaster(hostInfo.equals(host));
            pnlHostInfo.setHostName(hostInfo.getHost());
            pnlHostInfo.setPortNum(String.valueOf(hostInfo.getPort()));
            pnlHostInfo.setKeyStoreFile(hostInfo.getCertsDescription()
                    .getKeyStoreFile());
            pnlHostInfo.setTrustFileFile(hostInfo.getCertsDescription()
                    .getTrustStoreFile());
            pnlHostInfo.setSecureConnection(hostInfo.isSecureConnect());
            hostPanelList.add(pnlHostInfo);
        }
        refresh();
        chkboxAutoConnect.setSelected(subnet.isAutoConnect());

        dirty = false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.ISubnetView#showMessage(java.lang
     * .String, int)
     */
    @Override
    public void showMessage(String message, int messageType) {
        showMessage(message, null, messageType, (Object[]) null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.ISubnetView#setControlListener(com
     * .intel.stl.ui.wizards.impl.subnet.ISubnetControl)
     */
    @Override
    public void setControlListener(ISubnetControl listener) {

        subnetControlListener = listener;
    }

    @Override
    public void setWizardListener(IWizardTask listener) {
        super.setWizardListener(listener);
        subnetWizardControlListener = listener;
    }

    protected void refresh() {

        Util.runInEDT(new Runnable() {

            @Override
            public void run() {
                pnlHostContainer.removeAll();
                boolean isFirst = true;
                for (HostInfoPanel pnl : hostPanelList) {
                    pnlHostContainer.add(pnl);
                    if (pnl.getHostName().equals("")) {
                        pnl.setFocus();
                    }

                    if (isFirst) {
                        pnl.enableRemove(false);
                        isFirst = false;
                    }
                }

                pnlHostContainer.add(HostAdderPanel
                        .getInstance(hostInfoListener));
                ((JScrollPane) pnlHostContainer.getParent().getParent())
                        .scrollRectToVisible(pnlHostContainer.getBounds());
                pnlHostContainer.scrollRectToVisible(new Rectangle(0,
                        (int) pnlHostContainer.getPreferredSize().getHeight(),
                        10, 10));
                setDirty();
                repaint();
                revalidate();
            }
        });
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#addHost()
     */
    @Override
    public void addHost() {
        hostPanelList.add(new HostInfoPanel(this, chooser));
        refresh();
        multinetWizardViewListener.enableRun(false);
        dirty = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#removeHost(com
     * .intel.stl.ui.wizards.view.subnet.HostInfoPanel)
     */
    @Override
    public void removeHost(HostInfoPanel pnlHostInfo) {

        boolean found = false;
        Iterator<HostInfoPanel> it = hostPanelList.iterator();

        while (!found && it.hasNext()) {
            HostInfoPanel pnl = it.next();
            if (pnl.equals(pnlHostInfo)) {
                hostPanelList.remove(pnl);
                found = true;
            }
        }
        refresh();
    }

    public List<HostInfoPanel> getHostPanelList() {
        return hostPanelList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#isDuplicate
     * ()
     */
    @Override
    public boolean hasDuplicateHosts() {

        Set<HostInfoPanel> hostPanelSet = new HashSet<HostInfoPanel>();

        // If there are duplicate panels, they can't be added to the set
        for (HostInfoPanel pnl : hostPanelList) {
            if (!hostPanelSet.add(pnl)) {
                return true;
            }
        }

        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#runConnectionTest
     * (com.intel.stl.ui.wizards.view.subnet.HostInfoPanel)
     */
    @Override
    public void runConnectionTest(HostInfoPanel hostInfoPanel) {

        HostInfo hostInfo;
        if (hostInfoPanel.isSecureConnection()) {
            CertsDescription certsDescription =
                    new CertsDescription(hostInfoPanel.getKeyStoreFile(),
                            hostInfoPanel.getTrustStoreFile());
            hostInfo =
                    new HostInfo(hostInfoPanel.getHostName(),
                            Integer.valueOf(hostInfoPanel.getPortNum()),
                            certsDescription);
        } else {
            hostInfo =
                    new HostInfo(hostInfoPanel.getHostName(),
                            Integer.valueOf(hostInfoPanel.getPortNum()));
        }

        subnetControlListener.connectActionPerformed(hostInfo);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#showMessage(String
     * )
     */
    @Override
    public void showErrorMessage(String errorMessage) {
        Util.showErrorMessage(this, errorMessage);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.wizards.view.subnet.IHostInfoListener#showFileChooser()
     */
    @Override
    public int showFileChooser() {

        // Open file chooser centered over the wizard
        return chooser.showOpenDialog(this.getParent().getParent().getParent()
                .getParent());
    }

    public boolean isEditValid() {
        for (HostInfoPanel hip : hostPanelList) {
            if (!hip.isEditValid()) {
                return false;
            }
        }
        return true;
    }
}
