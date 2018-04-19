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

package com.intel.stl.ui.monitor.view;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Popup;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.view.ComponentFactory;
import com.intel.stl.ui.configuration.MultiColumnCategoryController;
import com.intel.stl.ui.configuration.view.MultiColumnCategoryPanel;
import com.intel.stl.ui.configuration.view.PropertyGroupPanel;
import com.intel.stl.ui.configuration.view.PropertyVizStyle;
import com.intel.stl.ui.model.DevicePropertyCategory;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.model.PropertySet;

/**
 * Popup window view for the cable info
 */
public class CableInfoPopup extends JPanel implements MouseListener {

    private static final long serialVersionUID = 3258624627237634655L;

    private GroupPanel groupPanel;

    private final PropertyVizStyle style = new PropertyVizStyle(true, false);

    private JLabel lblHeader;

    private Popup popup;

    public CableInfoPopup() {
        super();
        initComponents();
        addMouseListener(this);
    }

    protected void initComponents() {
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createLineBorder(UIConstants.INTEL_BLUE));

        lblHeader = createHeader();
        add(lblHeader, BorderLayout.NORTH);
        groupPanel = new GroupPanel(style);
        add(groupPanel.getContentComponent(), BorderLayout.CENTER);
    }

    protected JLabel createHeader() {
        lblHeader = ComponentFactory.getH4Label("", Font.BOLD);
        lblHeader.setIcon(UIImages.CABLE.getImageIcon());
        lblHeader.setText(STLConstants.K3049_CABLE_INFO.getValue());
        lblHeader.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0,
                UIConstants.INTEL_ORANGE));
        return lblHeader;
    }

    public void setModel(PropertySet<?> model) {

        @SuppressWarnings("unchecked")
        PropertySet<DevicePropertyGroup> cableInfoModel =
                (PropertySet<DevicePropertyGroup>) model;
        DevicePropertyGroup group = cableInfoModel.getGroups().get(0);
        groupPanel.setModel(group);
    }

    public void setPopupIcon(ImageIcon icon, String name) {
        lblHeader.setIcon(icon);
        lblHeader.setText(name);
    }

    public synchronized Popup getPopup() {
        return this.popup;
    }

    public synchronized void setPopup(Popup popup) {
        hidePopup();
        this.popup = popup;
    }

    protected synchronized void hidePopup() {
        if (popup != null) {
            popup.hide();
            popup = null;
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseClicked(MouseEvent e) {
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

    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.MouseListener#mouseExited(java.awt.event.MouseEvent)
     */
    @Override
    public void mouseExited(MouseEvent e) {

        if (popup != null && !getVisibleRect().contains(e.getPoint())) {
            hidePopup();
        }
    }

    class GroupPanel extends
            PropertyGroupPanel<DevicePropertyCategory, DevicePropertyGroup> {

        private static final long serialVersionUID = -9170550212900442484L;

        /**
         * Description:
         * 
         * @param style
         */
        public GroupPanel(PropertyVizStyle style) {
            super(style);
        }

        @Override
        protected Component createCategoryPanel(
                DevicePropertyCategory category, PropertyVizStyle style) {

            Component categoryPanel = new MultiColumnCategoryPanel(2, style);
            new MultiColumnCategoryController(category,
                    (MultiColumnCategoryPanel) categoryPanel, null);

            return categoryPanel;
        }

    }
}
