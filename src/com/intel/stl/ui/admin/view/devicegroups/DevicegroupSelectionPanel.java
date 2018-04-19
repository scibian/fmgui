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

package com.intel.stl.ui.admin.view.devicegroups;

import java.awt.Font;
import java.util.Collection;

import javax.swing.BorderFactory;
import javax.swing.JTabbedPane;

import com.intel.stl.ui.admin.impl.devicegroups.IResourceSelector;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIConstants;
import com.intel.stl.ui.common.view.IntelTabbedPaneUI;

public class DevicegroupSelectionPanel extends JTabbedPane {
    private static final long serialVersionUID = 3325634575575732829L;

    public DevicegroupSelectionPanel() {
        super();
        initComponent();
    }

    protected void initComponent() {
        setBackground(UIConstants.INTEL_WHITE);
        setBorder(BorderFactory
                .createTitledBorder(STLConstants.K2133_RESOURCES_SELECTION
                        .getValue()));
        IntelTabbedPaneUI tabUi = new IntelTabbedPaneUI();
        setUI(tabUi);
        tabUi.setFont(UIConstants.H5_FONT.deriveFont(Font.BOLD));
    }

    public void setSelectors(Collection<IResourceSelector> selectors) {
        for (IResourceSelector selector : selectors) {
            addTab(selector.getName(), selector.getView());
        }
    }

    public String getSelectorName() {
        int index = getSelectedIndex();
        String name = getTitleAt(index);
        return name;
    }

}
