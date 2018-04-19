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

package com.intel.stl.ui.configuration;

import com.intel.stl.api.configuration.IConfigurationApi;
import com.intel.stl.api.performance.IPerformanceApi;
import com.intel.stl.api.subnet.ISubnetApi;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.NodeInfoBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.PortInfoBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SwitchInfoBean;
import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.ui.main.Context;
import com.intel.stl.ui.monitor.tree.FVResourceNode;

public interface ICategoryProcessorContext {

    /**
     *
     * <i>Description:</i> return the resource node for this context
     *
     * @return
     */
    FVResourceNode getResourceNode();

    /**
     *
     * <i>Description:</i> returns the page context for this context
     *
     * @return
     */
    Context getContext();

    ISubnetApi getSubnetApi();

    IConfigurationApi getConfigurationApi();

    IPerformanceApi getPerformanceApi();

    /**
     *
     * <i>Description:</i> returns the node associated with the resource node
     * (the parent node if the resource node is a port)
     *
     * @return
     */
    NodeRecordBean getNode();

    /**
     *
     * <i>Description:</i> returns the node information for the resource node
     * (the parent node information if the resource node is a port)
     *
     * @return
     */
    NodeInfoBean getNodeInfo();

    /**
     *
     * <i>Description:</i> returns the switch record when the resource node is a
     * switch (null if not)
     *
     * @return
     */
    SwitchRecordBean getSwitch();

    /**
     *
     * <i>Description:</i> returns the switch information when the resource node
     * is a switch (null if not)
     *
     * @return
     */
    SwitchInfoBean getSwitchInfo();

    /**
     *
     * <i>Description:</i> returns the port record when the resource node is a
     * port (null if not)
     *
     * @return
     */
    PortRecordBean getPort();

    /**
     *
     * <i>Description:</i> returns the port information when the resource node
     * is a port (null if not)
     *
     * @return
     */
    PortInfoBean getPortInfo();

    /**
     *
     * <i>Description:</i> returns the link record associated with the port when
     * the resource node is a port (null if not)
     *
     * @return
     */
    LinkRecordBean getLink();

    /**
     *
     * <i>Description:</i> returns the node connected to the port when the
     * resource node is a port (null if not)
     *
     * @return
     */
    NodeRecordBean getNeighbor();

    /**
     *
     * <i>Description:</i> returns whether this port is an end port (a computing
     * node)
     *
     * @return
     */
    boolean isEndPort();

    boolean isHFI();

    boolean isExternalSWPort();

    boolean isBaseSWPort0();

    boolean isEnhSWPort0();

}
