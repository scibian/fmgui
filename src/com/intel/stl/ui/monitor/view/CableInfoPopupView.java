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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

import org.jdesktop.swingx.JXTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.configuration.ResourceCategory;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.Util;
import com.intel.stl.ui.model.ConnectivityTableColumns;
import com.intel.stl.ui.model.ConnectivityTableModel;
import com.intel.stl.ui.model.DevicePropertyGroup;
import com.intel.stl.ui.model.PropertySet;
import com.intel.stl.ui.monitor.ICableInfoListener;

/**
 * View to hold the Cable Info popup for the connectivity table
 */
public class CableInfoPopupView {
    private final static Logger log =
            LoggerFactory.getLogger(CableInfoPopupView.class);

    protected ICableInfoListener cableInfoListener;

    private final CableInfoPopup popupComp;

    private final PopupFactory popupFactory;

    private Popup popup;

    private JLabel lastFocusedCell;

    private MouseEvent mouseEvent;

    private final ConnectivitySubpageView view;

    public CableInfoPopupView(ConnectivitySubpageView view) {
        popupFactory = PopupFactory.getSharedInstance();
        popupComp = new CableInfoPopup();
        this.view = view;
    }

    public void onCableInfoSelection(JXTable table, int row, int col,
            final ConnectivityTableModel model, MouseEvent e) {

        FocusListener focusListener = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                popupComp.hidePopup();
            }
        };

        // Remove the listener from the last focused cell
        if (lastFocusedCell != null) {
            lastFocusedCell.removeFocusListener(focusListener);
        }

        JLabel cell = (JLabel) table.getCellRenderer(row, col);
        cell.addFocusListener(focusListener);

        // Get the GUID from the table model
        long portGuid = Long.decode((String) model.getValueAt(row,
                ConnectivityTableColumns.NODE_GUID.getId()));

        // Get the port # from the table model
        String value = (String) model.getValueAt(row,
                ConnectivityTableColumns.PORT_NUMBER.getId());
        if (value != null) {
            short portNum = Short.valueOf((value).split(" ")[0]);

            // Get the node type from the table model
            NodeType nodeType = (NodeType) model.getValueAt(row,
                    ConnectivityTableColumns.NODE_TYPE.getId());

            // Get the node lid from the table model
            int lid = (int) model.getValueAt(row,
                    ConnectivityTableColumns.NODE_LID.getId());

            cableInfoListener.onCableInfoSelection(lid, portGuid, portNum,
                    nodeType);
        } else {
            log.error(UILabels.STL50202_CONNECTIVITY_PORT_IS_NULL
                    .getDescription());
        }

        mouseEvent = e;

        // Save the last cell focused
        lastFocusedCell = cell;
    }

    public void setPopup() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                Point p = getPopupLocation(mouseEvent, popupComp);
                popup = popupFactory.getPopup(mouseEvent.getComponent(),
                        popupComp, p.x, p.y);

                popupComp.setPopup(popup);
            }
        });
    }

    public void showPopup() {
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {

                popup.show();

            }
        });
    }

    public void updatePopup(final PropertySet<DevicePropertyGroup> model,
            final boolean isFinal) {
        model.getGroups().get(0).getCategory(ResourceCategory.CABLE_INFO);
        Util.runInEDT(new Runnable() {
            @Override
            public void run() {
                popupComp.setModel(model);
                popupComp.setPopupIcon(
                        isFinal ? UIImages.CABLE.getImageIcon()
                                : UIImages.RUNNING.getImageIcon(),
                        STLConstants.K3049_CABLE_INFO.getValue());
                setPopup();
                showPopup();
            }
        });
    }

    protected Point getPopupLocation(MouseEvent e, Component comp) {
        Point p = e.getPoint();
        SwingUtilities.convertPointToScreen(p, e.getComponent());
        p.x = Math.max(0, p.x - 5);
        p.y = Math.max(0, p.y - 5);
        Dimension d = comp.getPreferredSize();
        Rectangle rec = new Rectangle(p.x, p.y, d.width, d.height);
        p = Util.adjustPoint(rec, SwingUtilities.getWindowAncestor(view));
        return p;
    }

    public void setCableInfoListener(ICableInfoListener cableInfoListener) {
        this.cableInfoListener = cableInfoListener;
    }
}
