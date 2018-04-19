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

import java.util.LinkedHashMap;
import java.util.Map;

import com.intel.stl.api.management.XMLConstants;
import com.intel.stl.api.management.devicegroups.IncludeGroup;
import com.intel.stl.ui.admin.impl.IRendererModel;
import com.intel.stl.ui.admin.view.IAttrRenderer;
import com.intel.stl.ui.admin.view.devicegroups.DGSelectRenderer;
import com.intel.stl.ui.admin.view.devicegroups.IncludeGroupRenderer;
import com.intel.stl.ui.admin.view.devicegroups.NodeDescRenderer;
import com.intel.stl.ui.admin.view.devicegroups.NodeGUIDRenderer;
import com.intel.stl.ui.admin.view.devicegroups.NodeTypeRenderer;
import com.intel.stl.ui.admin.view.devicegroups.PortGUIDRenderer;
import com.intel.stl.ui.admin.view.devicegroups.SystemImageGUIDRenderer;

public class DeviceGroupRendererModel implements IRendererModel {
    private final static Map<String, Class<? extends IAttrRenderer<?>>> map =
            new LinkedHashMap<String, Class<? extends IAttrRenderer<?>>>() {
                private static final long serialVersionUID =
                        -4676175893529648601L;

                {
                    put(XMLConstants.SI_GUID, SystemImageGUIDRenderer.class);
                    put(XMLConstants.NODE_GUID, NodeGUIDRenderer.class);
                    put(XMLConstants.PORT_GUID, PortGUIDRenderer.class);
                    put(XMLConstants.NODE_DESC, NodeDescRenderer.class);
                    put(XMLConstants.NODE_TYPE, NodeTypeRenderer.class);
                    put(XMLConstants.SELECT, DGSelectRenderer.class);
                    put(XMLConstants.INCLUDE_GROUP, IncludeGroupRenderer.class);
                }
            };

    private String[] dgNames;

    /**
     * @param dgNames
     *            the dgNames to set
     */
    public void setDgNames(String[] dgNames) {
        this.dgNames = dgNames;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.intel.stl.ui.admin.impl.IRendererModel#getRendererNames()
     */
    @Override
    public String[] getRendererNames() {
        return map.keySet().toArray(new String[0]);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.intel.stl.ui.admin.impl.IRendererModel#getRenderer(java.lang.String)
     */
    @Override
    public IAttrRenderer<?> getRenderer(String name) throws Exception {
        Class<? extends IAttrRenderer<?>> klass = map.get(name);
        if (klass != null) {
            IAttrRenderer<?> res = klass.newInstance();
            initRenderer(name, res);
            return res;
        } else {
            throw new IllegalArgumentException(
                    "Unknown renderer '" + name + "'");
        }
    }

    protected void initRenderer(String name, IAttrRenderer<?> renderer) {
        if (name.equals(XMLConstants.INCLUDE_GROUP)) {
            IncludeGroupRenderer lsRenderer = (IncludeGroupRenderer) renderer;
            lsRenderer.setList(IncludeGroup.toArry(dgNames));
        }
    }

    /**
     * <i>Description:</i> update a DG's name
     *
     * @param oldName
     *            the old name
     * @param newName
     *            the new name
     * @return the new DG names array
     */
    public String[] updateDGName(String oldName, String newName) {
        for (int i = 0; i < dgNames.length; i++) {
            if (dgNames[i].equals(oldName)) {
                dgNames[i] = newName;
                break;
            }
        }
        return dgNames;
    }
}
