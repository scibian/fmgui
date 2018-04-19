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

package com.intel.stl.ui.admin.view.applications;

import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import org.jdesktop.swingx.VerticalLayout;

import com.intel.stl.api.management.IAttribute;
import com.intel.stl.api.management.applications.AppSelect;
import com.intel.stl.api.management.applications.Application;
import com.intel.stl.api.management.applications.IncludeApplication;
import com.intel.stl.api.management.applications.MGID;
import com.intel.stl.api.management.applications.ServiceID;
import com.intel.stl.ui.admin.impl.applications.AppRendererModel;
import com.intel.stl.ui.admin.view.AbstractEditorPanel;
import com.intel.stl.ui.admin.view.IAttrRenderer;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;

public class AppsEditorPanel extends AbstractEditorPanel<Application> {
    private static final long serialVersionUID = -7830529617793812677L;

    private JScrollPane mainPane;

    private JPanel attrsPanel;

    private final List<AppAttrPanel> appAttrPanels =
            new ArrayList<AppAttrPanel>();

    private final AppRendererModel rendererModel;

    /**
     * Description:
     *
     */
    public AppsEditorPanel(AppRendererModel rendererModel) {
        super();
        this.rendererModel = rendererModel;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#getMainPanel()
     */
    @Override
    protected JComponent getMainComponent() {
        if (mainPane == null) {
            attrsPanel = new JPanel(new VerticalLayout(10));
            attrsPanel.setBackground(UIConstants.INTEL_WHITE);

            mainPane = new JScrollPane(attrsPanel);
            mainPane.setBackground(UIConstants.INTEL_WHITE);
            mainPane.setBorder(BorderFactory.createTitledBorder(
                    STLConstants.K2112_ATTRIBUTES.getValue()));
        }
        return mainPane;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#clear()
     */
    @Override
    public void clear() {
        super.clear();
        attrsPanel.removeAll();
        appAttrPanels.clear();
        rendererModel.setAppNames(new String[0]);
        revalidate();
        repaint();
    }

    @Override
    protected void showItemObject(Application app, String[] appNames,
            boolean isEditable) {
        rendererModel.setAppNames(appNames);

        attrsPanel.removeAll();
        appAttrPanels.clear();
        List<ServiceID> serviceIDs = app.getServiceIDs();
        if (serviceIDs != null) {
            for (ServiceID sid : serviceIDs) {
                addAttr(sid.getType(), sid, isEditable);
            }
        }
        List<MGID> mgids = app.getMgids();
        if (mgids != null) {
            for (MGID mgid : mgids) {
                addAttr(mgid.getType(), mgid, isEditable);
            }
        }
        List<AppSelect> selects = app.getSelects();
        if (selects != null) {
            for (AppSelect sel : selects) {
                addAttr(sel.getType(), sel, isEditable);
            }
        }
        List<IncludeApplication> incApps = app.getIncludeApplications();
        if (incApps != null) {
            for (IncludeApplication incApp : incApps) {
                addAttr(incApp.getType(), incApp, isEditable);
            }
        }
        if (isEditable) {
            AppAttrPanel attrPanel = new AppAttrPanel(this, rendererModel);
            attrsPanel.add(attrPanel);
        }
        revalidate();
        repaint();
    }

    private <E extends IAttribute> void addAttr(String type, E attr,
            boolean isEditable) {
        AppAttrPanel attrPanel = new AppAttrPanel(this, rendererModel);
        attrPanel.setAttr(type, attr, isEditable);
        attrsPanel.add(attrPanel);
        appAttrPanels.add(attrPanel);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.view.AbstractEditorPanel#itemNameChanged(java.lang
     * .String)
     */
    @Override
    public void itemNameChanged(String oldName, String newName) {
        String[] appNames = rendererModel.updateAppName(oldName, newName);
        for (AppAttrPanel aap : appAttrPanels) {
            IAttrRenderer<?> renderer = aap.getAttrRenderer();
            if (renderer instanceof IncludeApplicationRenderer) {
                IncludeApplicationRenderer iar =
                        (IncludeApplicationRenderer) renderer;
                IncludeApplication sel = iar.getAttr();
                iar.setList(IncludeApplication.toArry(appNames));
                if (sel.getObject().equals(oldName)) {
                    sel.setValue(newName);
                }
                iar.setAttr(sel);
            }
        }
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.view.AbstractEditorPanel#updateItemObject(java
     * .lang.Object)
     */
    @Override
    protected void updateItemObject(Application obj) {
        obj.setName(getCurrentName());
        obj.clear();
        for (AppAttrPanel attrPanel : appAttrPanels) {
            IAttrRenderer<? extends IAttribute> renderer =
                    attrPanel.getAttrRenderer();
            if (renderer != null) {
                IAttribute attr = renderer.getAttr();
                if (attr != null) {
                    attr.installApplication(obj);
                }
            }
        }
    }

    /**
     * <i>Description:</i>
     *
     */
    public void beginEdit(AppAttrPanel attrPanel) {
        appAttrPanels.add(attrPanel);
        AppAttrPanel aap = new AppAttrPanel(this, rendererModel);
        attrsPanel.add(aap);
    }

    /**
     * <i>Description:</i>
     *
     * @param appAttrPanel
     */
    public void removeEditor(AppAttrPanel appAttrPanel) {
        attrsPanel.remove(appAttrPanel);
        appAttrPanels.remove(appAttrPanel);
        revalidate();
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.view.AbstractEditorPanel#isEditValid()
     */
    @Override
    public boolean isEditValid() {
        if (!super.isEditValid()) {
            return false;
        }

        for (AppAttrPanel attrPanel : appAttrPanels) {
            IAttrRenderer<?> renderer = attrPanel.getAttrRenderer();
            if (renderer != null && !renderer.isEditValid()) {
                return false;
            }
        }
        return true;
    }
}
