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

package com.intel.stl.ui.admin.view.virtualfabrics;

import com.intel.stl.ui.admin.impl.virtualfabrics.VirtualFabricRendererModel;
import com.intel.stl.ui.admin.view.AbstractAttrPanel;
import com.intel.stl.ui.common.ExComboBoxModel;

public class VirtualFabricAttrPanel extends AbstractAttrPanel {
    private static final long serialVersionUID = 7079382835741909390L;

    private final VirtualFabricsEditorPanel parent;

    /**
     * Description:
     * 
     * @param parent
     */
    public VirtualFabricAttrPanel(VirtualFabricsEditorPanel parent,
            VirtualFabricRendererModel rendererModel) {
        super(rendererModel);
        this.parent = parent;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractAttrPanel#onAddAttr()
     */
    @Override
    protected void onAddAttr() {
        parent.beginEdit(this);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.intel.stl.ui.admin.view.AbstractAttrPanel#onRemoveAttr()
     */
    @Override
    protected void onRemoveAttr() {
        parent.removeEditor(this);
    }

    public void addDisabledAttr(String name) {
        ExComboBoxModel<String> model =
                (ExComboBoxModel<String>) typeList.getModel();
        model.addDisabledItem(name);
        repaint();
    }

    public void removeDisabledAttr(String name) {
        ExComboBoxModel<String> model =
                (ExComboBoxModel<String>) typeList.getModel();
        model.removeDisabledItem(name);
        repaint();
    }

    public void setDisabledAttrs(String[] names) {
        ExComboBoxModel<String> model =
                (ExComboBoxModel<String>) typeList.getModel();
        model.setDisabledItem(names);
        repaint();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.intel.stl.ui.admin.view.AbstractAttrPanel#onChangeRenderer(java.lang
     * .String, java.lang.String)
     */
    @Override
    protected void onChangeRenderer(String oldRenderer, String newRenderer) {
        parent.changeEditorRenderer(oldRenderer, newRenderer);
    }

}
