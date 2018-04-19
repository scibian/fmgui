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

import static com.intel.stl.ui.common.STLConstants.K2124_NAME_CHECK;
import static com.intel.stl.ui.common.STLConstants.K2162_PKEY_CHECK;
import static com.intel.stl.ui.common.UILabels.STL81010_APPLICATIONS_ALL;
import static com.intel.stl.ui.common.UILabels.STL81011_APPLICATIONS_ALL_SUG;

import java.util.List;

import com.intel.stl.api.management.virtualfabrics.ApplicationName;
import com.intel.stl.api.management.virtualfabrics.PKey;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.ui.admin.ChangeState;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.ValidationTask;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UILabels;
import com.intel.stl.ui.common.ValidationItem;
import com.intel.stl.ui.common.ValidationModel;

public class VFValidationTask extends ValidationTask<VirtualFabric> {

    private static final String VF_APPLICATION_ALL = "All";

    /**
     * Description:
     * 
     * @param dialog
     * @param model
     * @param items
     * @param toCheck
     */
    public VFValidationTask(ValidationDialog dialog,
            ValidationModel<VirtualFabric> model,
            List<Item<VirtualFabric>> items, Item<VirtualFabric> toCheck) {
        super(dialog, model, items, toCheck);
    }

    /*
     * (non-Javadoc)
     * 
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Integer doInBackground() throws Exception {
        if (toCheck.getState() == ChangeState.NONE) {
            return 0;
        }

        int count = 0;
        if (toCheck.getState() == ChangeState.UPDATE
                || toCheck.getState() == ChangeState.ADD) {
            dialog.reportProgress(K2124_NAME_CHECK.getValue() + "...");
            ValidationItem<VirtualFabric> vi = uniqueNameCheck(toCheck);
            if (vi != null) {
                publish(vi);
                count += 1;
            }
        }
        VirtualFabric vf = toCheck.getObj();
        PKey pkey = vf.getPKey();
        if (pkey != null) {
            List<ApplicationName> apps = vf.getApplications();
            boolean appAllSpecified = false;
            for (ApplicationName app : apps) {
                if (VF_APPLICATION_ALL.equals(app.getValue())) {
                    appAllSpecified = true;
                    break;
                }
            }
            if (appAllSpecified) {
                // The Admin VF has the all powerfull PKey 0x7fff; another
                // fabric specifying All would need that PKey, so in this case
                // PKey should be defaulted
                ValidationItem<VirtualFabric> vi =
                        new ValidationItem<VirtualFabric>(
                                K2162_PKEY_CHECK.getValue(),
                                STL81010_APPLICATIONS_ALL.getDescription(),
                                STL81011_APPLICATIONS_ALL_SUG.getDescription());
                publish(vi);
                count += 1;
            }
        }
        return count;
    }

    /**
     * <i>Description:</i>
     * 
     * @param toCheck
     * @return
     */
    protected ValidationItem<VirtualFabric> uniqueNameCheck(
            Item<VirtualFabric> toCheck) {
        long id = toCheck.getId();
        String name = toCheck.getObj().getName();
        for (Item<VirtualFabric> item : items) {
            if (item.getId() != id && item.getObj().getName().equals(name)) {
                return new ValidationItem<VirtualFabric>(
                        STLConstants.K2124_NAME_CHECK.getValue(),
                        UILabels.STL81001_DUP_NAME.getDescription(),
                        UILabels.STL81002_DUP_NAME_SUG.getDescription());
            }
        }
        return null;
    }

}
