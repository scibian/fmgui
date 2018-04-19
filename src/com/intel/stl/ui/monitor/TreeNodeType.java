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

package com.intel.stl.ui.monitor;

import javax.swing.ImageIcon;

import com.intel.stl.api.configuration.ResourceType;
import com.intel.stl.api.subnet.NodeType;
import com.intel.stl.ui.common.UIImages;

public enum TreeNodeType {
    ALL(UIImages.SUBNET_ICON),
    HCA_GROUP(UIImages.HFI_GROUP_ICON),
    SWITCH_GROUP(UIImages.SW_GROUP_ICON),
    ROUTER_GROUP(null),
    DEVICE_GROUP(UIImages.DEVICE_GROUP_ICON),
    VIRTUAL_FABRIC(UIImages.VIRTUAL_FABRIC_ICON),
    NODE(null),
    HFI(UIImages.HFI_ICON),
    SWITCH(UIImages.SW_ICON),
    ROUTER(null),
    PORT(null),
    ACTIVE_PORT(UIImages.PORT_ICON),
    INACTIVE_PORT(UIImages.INACTIVE_PORT_ICON),
    SYSTEM_IMAGE(UIImages.SYS_IMG);

    private final UIImages icon;

    /**
     * Description:
     *
     * @param icon
     */
    private TreeNodeType(UIImages icon) {
        this.icon = icon;
    }

    /**
     * @param icon
     *            the icon to set
     */
    public ImageIcon getIcon() {
        return icon.getImageIcon();
    }

    public static ResourceType getResourceTypeFor(TreeNodeType nodeType) {
        ResourceType resourceType;
        switch (nodeType) {
            case HFI:
                resourceType = ResourceType.HFI;
                break;
            case SWITCH:
                resourceType = ResourceType.SWITCH;
                break;
            case PORT:
                resourceType = ResourceType.PORT;
                break;
            case ACTIVE_PORT:
                resourceType = ResourceType.PORT;
                break;
            default:
                resourceType = null;
        }
        return resourceType;
    }

    public static NodeType getNodeType(TreeNodeType type) {
        switch (type) {
            case HFI:
                return NodeType.HFI;
            case SWITCH:
                return NodeType.SWITCH;
            case ROUTER:
                return NodeType.ROUTER;
            default:
                return null;
        }
    }

    public static byte getNodeTypeCode(TreeNodeType type) {
        switch (type) {
            case HFI:
                return NodeType.HFI.getId();
            case SWITCH:
                return NodeType.SWITCH.getId();
            case ROUTER:
                return NodeType.ROUTER.getId();
            default:
                return NodeType.UNKNOWN.getId();
        }
    }
}
