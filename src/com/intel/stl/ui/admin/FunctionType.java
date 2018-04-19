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

package com.intel.stl.ui.admin;

import javax.swing.ImageIcon;

import com.intel.stl.ui.common.STLConstants;
import com.intel.stl.ui.common.UIImages;

public enum FunctionType {
    APPLICATIONS(STLConstants.K2101_ADM_APPS.getValue(),
            STLConstants.K2102_ADM_APPS_DESC.getValue(),
            UIImages.APPS_LARGE_ICON),
    DEVICE_GROUPS(STLConstants.K0408_DEVICE_GROUPS.getValue(),
            STLConstants.K2104_ADM_DGS_DESC.getValue(),
            UIImages.DEVICE_GROUP_LARGE_ICON),
    VIRTUAL_FABRICS(STLConstants.K2105_ADM_VFS.getValue(),
            STLConstants.K2106_ADM_VFS_DESC.getValue(),
            UIImages.VIRTUAL_FABRIC_LARGE_ICON),
    CONSOLE(STLConstants.K2107_ADM_CONSOLE.getValue(),
            STLConstants.K2108_ADM_CONSOLE_DESC.getValue(),
            UIImages.CONSOLE_ICON),
    LOGS(STLConstants.K2109_ADM_LOG.getValue(), STLConstants.K2110_ADM_LOG_DESC
            .getValue(), UIImages.LOG_ICON);

    private final String name;

    private final String description;

    private final UIImages image;

    /**
     * Description:
     * 
     * @param name
     * @param description
     * @param icon
     */
    private FunctionType(String name, String description, UIImages image) {
        this.name = name;
        this.description = description;
        this.image = image;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @return the icon
     */
    public ImageIcon getIcon() {
        return image.getImageIcon();
    }

}
