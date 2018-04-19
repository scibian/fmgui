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

package com.intel.stl.ui.admin.impl.virtualfabrics;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.ImageIcon;

import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.ui.admin.ChangeState;
import com.intel.stl.ui.admin.InvalidEditException;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.AbstractEditorController;
import com.intel.stl.ui.admin.impl.ConfPageController;
import com.intel.stl.ui.admin.impl.ValidationTask;
import com.intel.stl.ui.admin.view.AbstractConfView;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.admin.view.virtualfabrics.VirtualFabricsEditorPanel;
import com.intel.stl.ui.common.IProgressObserver;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.main.HelpAction;

public class VirtualFabricsPageController extends
        ConfPageController<VirtualFabric, VirtualFabricsEditorPanel> {
    private Set<String> reserved;

    /**
     * Description:
     * 
     * @param name
     * @param description
     * @param icon
     * @param view
     */
    public VirtualFabricsPageController(
            String name,
            String description,
            ImageIcon icon,
            final AbstractConfView<VirtualFabric, VirtualFabricsEditorPanel> view) {
        super(name, description, icon, view);
        view.getEditorPanel().setEnabledActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    view.getEditorPanel().updateItem(currentItem);
                    view.updateItems();
                } catch (InvalidEditException e1) {
                    e1.printStackTrace();
                }
            }
        });
    }

    @Override
    public String getHelpID() {
        return HelpAction.getInstance().getAdminVf();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#creatEditorController(
     * com.intel.stl.ui.admin.view.AbstractEditorPanel)
     */
    @Override
    protected AbstractEditorController<VirtualFabric, VirtualFabricsEditorPanel> creatEditorController(
            VirtualFabricsEditorPanel editorPanel) {
        return new VirtualFabricsEditorController(editorPanel);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#setContext(com.intel.stl
     * .ui.main.Context, com.intel.stl.ui.common.IProgressObserver)
     */
    @Override
    public void setContext(Context context, IProgressObserver observer) {
        super.setContext(context, observer);
        edtCtr.setContext(context);
        reserved = mgtApi.getReservedVirtualFabrics();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.impl.ConfPageController#getItems()
     */
    @Override
    protected ArrayList<Item<VirtualFabric>> initData() throws Exception {
        edtCtr.initData();

        List<VirtualFabric> vfs = mgtApi.getVirtualFabrics();
        ArrayList<Item<VirtualFabric>> res =
                new ArrayList<Item<VirtualFabric>>();
        for (VirtualFabric vf : vfs) {
            boolean isEditable = isEditable(vf);
            Item<VirtualFabric> item =
                    new Item<VirtualFabric>(res.size(), vf.getName(), vf,
                            isEditable);
            item.setState(ChangeState.NONE);
            res.add(item);
        }
        return res;
    }

    /**
     * <i>Description:</i>
     * 
     * @param vf
     * @return
     */
    private boolean isEditable(VirtualFabric vf) {
        String name = vf.getName();
        return !reserved.contains(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getCopy(java.lang.Object)
     */
    @Override
    protected VirtualFabric getCopy(VirtualFabric obj) {
        return obj.copy();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.impl.ConfPageController#createObj()
     */
    @Override
    protected VirtualFabric createObj() {
        return new VirtualFabric();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#removeItemObject(java.
     * lang.String)
     */
    @Override
    protected void removeItemObject(String name) throws Exception {
        mgtApi.removeVirtualFabric(name);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#getValidationTask(com.
     * intel.stl.ui.admin.view.ValidationDialog, com.intel.stl.ui.admin.Item)
     */
    @Override
    protected ValidationTask<VirtualFabric> getValidationTask(
            ValidationDialog dialog, Item<VirtualFabric> item) {
        VFValidationTask task =
                new VFValidationTask(dialog, valModel, orgItems, item);
        return task;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.impl.ConfPageController#saveItemObject(java.lang
     * .String, java.lang.Object)
     */
    @Override
    protected void saveItemObject(String oldName, VirtualFabric obj)
            throws Exception {
        if (oldName != null) {
            mgtApi.updateVirtualFabric(oldName, obj);
        } else {
            mgtApi.addVirtualFabric(obj);
        }
    }

}
