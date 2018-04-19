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

package com.intel.stl.ui.admin.impl.devicegroups;

import static com.intel.stl.ui.admin.ChangeState.ADD;
import static com.intel.stl.ui.admin.ChangeState.NONE;
import static com.intel.stl.ui.admin.ChangeState.REMOVE;
import static com.intel.stl.ui.admin.ChangeState.UPDATE;
import static com.intel.stl.ui.common.STLConstants.K0408_DEVICE_GROUPS;
import static com.intel.stl.ui.common.STLConstants.K2105_ADM_VFS;
import static com.intel.stl.ui.common.STLConstants.K2124_NAME_CHECK;
import static com.intel.stl.ui.common.STLConstants.K2127_REF_CHECK;
import static com.intel.stl.ui.common.UILabels.STL81001_DUP_NAME;
import static com.intel.stl.ui.common.UILabels.STL81002_DUP_NAME_SUG;
import static com.intel.stl.ui.common.UILabels.STL81003_REF_CONF;
import static com.intel.stl.ui.common.UILabels.STL81004_REMOVE_REF;
import static com.intel.stl.ui.common.UILabels.STL81005_UPDATE_REF;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.intel.stl.api.management.IManagementApi;
import com.intel.stl.api.management.StringNode;
import com.intel.stl.api.management.devicegroups.DeviceGroup;
import com.intel.stl.api.management.virtualfabrics.VirtualFabric;
import com.intel.stl.api.management.virtualfabrics.VirtualFabricException;
import com.intel.stl.ui.admin.Item;
import com.intel.stl.ui.admin.impl.ValidationTask;
import com.intel.stl.ui.admin.view.ValidationDialog;
import com.intel.stl.ui.common.ValidationItem;
import com.intel.stl.ui.common.ValidationModel;

public class DGValidationTask extends ValidationTask<DeviceGroup> {

    private final IManagementApi mgmtApi;

    /**
     * Description:
     *
     * @param dialog
     * @param model
     * @param items
     * @param toCheck
     */
    public DGValidationTask(ValidationDialog dialog,
            ValidationModel<DeviceGroup> model, List<Item<DeviceGroup>> items,
            Item<DeviceGroup> toCheck, IManagementApi mgmtApi) {
        super(dialog, model, items, toCheck);
        this.mgmtApi = mgmtApi;
    }

    /*
     * (non-Javadoc)
     *
     * @see javax.swing.SwingWorker#doInBackground()
     */
    @Override
    protected Integer doInBackground() throws Exception {
        if (toCheck.getState() == NONE) {
            return 0;
        }

        int count = 0;
        if (toCheck.getState() == UPDATE || toCheck.getState() == ADD) {
            dialog.reportProgress(K2124_NAME_CHECK.getValue() + "...");
            ValidationItem<DeviceGroup> vi = uniqueNameCheck(toCheck);
            if (vi != null) {
                publish(vi);
                count += 1;
            }
        }

        if (toCheck.getState() != ADD) {
            dialog.reportProgress(K2127_REF_CHECK.getValue() + "...");
            ValidationItem<DeviceGroup> vi = dgReferenceCheck(toCheck);
            if (vi != null) {
                publish(vi);
                count += 1;
            }
        }
        if (toCheck.getState() != ADD) {
            dialog.reportProgress(K2127_REF_CHECK.getValue() + "...");
            ValidationItem<DeviceGroup> vi = vfReferenceCheck(toCheck);
            if (vi != null) {
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
    protected ValidationItem<DeviceGroup> uniqueNameCheck(
            Item<DeviceGroup> toCheck) {
        long id = toCheck.getId();
        String name = toCheck.getObj().getName();
        for (Item<DeviceGroup> item : items) {
            if (item.getId() != id && item.getObj().getName().equals(name)) {
                return new ValidationItem<DeviceGroup>(
                        K2124_NAME_CHECK.getValue(),
                        STL81001_DUP_NAME.getDescription(),
                        STL81002_DUP_NAME_SUG.getDescription());
            }
        }
        return null;
    }

    /**
     * <i>Description:</i>
     *
     * @param toCheck
     * @return
     */
    protected ValidationItem<DeviceGroup> dgReferenceCheck(
            Item<DeviceGroup> dg) {
        final String oldName = getDeviceGroup(dg.getId()).getName();
        final String newName = dg.getObj().getName();
        if (dg.getState() != REMOVE && oldName.equals(newName)) {
            return null;
        }

        final List<DeviceGroup> refs = getRelatedDGs(oldName);
        if (refs.isEmpty()) {
            return null;
        }

        StringBuffer sb = new StringBuffer();
        sb.append(K0408_DEVICE_GROUPS.getValue());
        sb.append(" ");
        for (DeviceGroup ref : refs) {
            if (sb.length() == 0) {
                sb.append("'" + ref.getName() + "'");
            } else {
                sb.append(", '" + ref.getName() + "'");
            }
        }
        if (dg.getState() == REMOVE) {
            final ValidationItem<DeviceGroup> res =
                    new ValidationItem<DeviceGroup>(K2127_REF_CHECK.getValue(),
                            STL81003_REF_CONF.getDescription(sb.toString()),
                            STL81004_REMOVE_REF.getDescription());
            Callable<DeviceGroup[]> quickFix = new Callable<DeviceGroup[]>() {

                @Override
                public DeviceGroup[] call() throws Exception {
                    DeviceGroup[] newDGs = new DeviceGroup[refs.size()];
                    for (int i = 0; i < refs.size(); i++) {
                        DeviceGroup dg = refs.get(i).copy();
                        dg.removeIncludeGroup(oldName);
                    }
                    fixedIssue(res);
                    return newDGs;
                }

            };
            res.setQuickFix(quickFix);
            return res;
        } else if (dg.getState() == UPDATE) {
            final ValidationItem<DeviceGroup> res =
                    new ValidationItem<DeviceGroup>(K2127_REF_CHECK.getValue(),
                            STL81003_REF_CONF.getDescription(sb.toString()),
                            STL81005_UPDATE_REF.getDescription());
            Callable<DeviceGroup[]> quickFix = new Callable<DeviceGroup[]>() {

                @Override
                public DeviceGroup[] call() throws Exception {
                    DeviceGroup[] newDGs = new DeviceGroup[refs.size()];
                    for (int i = 0; i < refs.size(); i++) {
                        DeviceGroup dg = refs.get(i).copy();
                        int index = dg.indexOfIncludeGroup(oldName);
                        dg.removeIncludeGroup(oldName);
                        dg.insertIncludeGroup(index, newName);
                    }
                    fixedIssue(res);
                    return newDGs;
                }

            };
            res.setQuickFix(quickFix);
            return res;
        } else {
            return null;
        }
    }

    private ValidationItem<DeviceGroup> vfReferenceCheck(Item<DeviceGroup> dg) {
        final String oldName = getDeviceGroup(dg.getId()).getName();
        final String newName = dg.getObj().getName();
        if (dg.getState() != REMOVE && oldName.equals(newName)) {
            return null;
        }
        final List<VirtualFabric> refs = getRelatedVfs(oldName);
        if (refs.isEmpty()) {
            return null;
        }
        StringBuffer sb = new StringBuffer();
        sb.append(K2105_ADM_VFS.getValue());
        sb.append(" ");
        String sep = "'";
        for (VirtualFabric ref : refs) {
            sb.append(sep);
            sb.append(ref.getName());
            sb.append("'");
            sep = ", '";
        }
        if (dg.getState() == REMOVE) {
            final ValidationItem<DeviceGroup> res =
                    new ValidationItem<DeviceGroup>(K2127_REF_CHECK.getValue(),
                            STL81003_REF_CONF.getDescription(sb.toString()),
                            STL81004_REMOVE_REF.getDescription());
            return res;
        } else if (dg.getState() == UPDATE) {
            final ValidationItem<DeviceGroup> res =
                    new ValidationItem<DeviceGroup>(K2127_REF_CHECK.getValue(),
                            STL81003_REF_CONF.getDescription(sb.toString()),
                            STL81005_UPDATE_REF.getDescription());
            return res;
        } else {
            return null;
        }
    }

    private List<VirtualFabric> getRelatedVfs(String dgName) {
        List<VirtualFabric> relVfs = new ArrayList<VirtualFabric>();
        try {
            List<VirtualFabric> vfs = mgmtApi.getVirtualFabrics();
            for (VirtualFabric vf : vfs) {
                List<StringNode> vfMems = vf.getMembers();
                for (StringNode mem : vfMems) {
                    if (mem.getValue().equals(dgName)) {
                        relVfs.add(vf);
                        break;
                    }
                }
            }
        } catch (VirtualFabricException e) {
            e.printStackTrace();
        }
        return relVfs;
    }

    /**
     * <i>Description:</i>
     *
     * @param id
     * @return
     */
    private DeviceGroup getDeviceGroup(long id) {
        for (Item<DeviceGroup> item : items) {
            if (item.getId() == id) {
                return item.getObj();
            }
        }
        throw new IllegalArgumentException("Couldn't find item by id=" + id);
    }

    /**
     * <i>Description:</i>
     *
     * @param oldName
     * @return
     */
    private List<DeviceGroup> getRelatedDGs(String name) {
        List<DeviceGroup> res = new ArrayList<DeviceGroup>();
        for (Item<DeviceGroup> item : items) {
            DeviceGroup dg = item.getObj();
            if (dg.doesIncludeGroup(name)) {
                res.add(dg);
                break;
            }
        }
        return res;
    }

}
