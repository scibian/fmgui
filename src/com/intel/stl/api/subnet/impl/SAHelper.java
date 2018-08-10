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

package com.intel.stl.api.subnet.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.intel.stl.api.subnet.CableRecordBean;
import com.intel.stl.api.subnet.FabricInfoBean;
import com.intel.stl.api.subnet.LFTRecordBean;
import com.intel.stl.api.subnet.LinkRecordBean;
import com.intel.stl.api.subnet.MFTRecordBean;
import com.intel.stl.api.subnet.NodeRecordBean;
import com.intel.stl.api.subnet.P_KeyTableRecordBean;
import com.intel.stl.api.subnet.PathRecordBean;
import com.intel.stl.api.subnet.PortRecordBean;
import com.intel.stl.api.subnet.SC2SLMTRecordBean;
import com.intel.stl.api.subnet.SC2VLMTRecordBean;
import com.intel.stl.api.subnet.SMRecordBean;
import com.intel.stl.api.subnet.SwitchRecordBean;
import com.intel.stl.api.subnet.TraceRecordBean;
import com.intel.stl.api.subnet.VLArbTableRecordBean;
import com.intel.stl.fecdriver.IStatement;
import com.intel.stl.fecdriver.adapter.FEHelper;
import com.intel.stl.fecdriver.messages.adapter.sa.GID;
import com.intel.stl.fecdriver.messages.command.InputGidPair;
import com.intel.stl.fecdriver.messages.command.InputLid;
import com.intel.stl.fecdriver.messages.command.InputPortGid;
import com.intel.stl.fecdriver.messages.command.InputPortGuid;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetCable;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetFabricInfo;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetLFT;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetLink;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetMFT;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetNode;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetNodes;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetPKeyTable;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetPath;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetPortInfo;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSC2SLMT;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSC2SLMTs;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSC2VLNTMT;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSC2VLTMT;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSMInfo;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSwitchInfo;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetSwitches;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetTrace;
import com.intel.stl.fecdriver.messages.command.sa.FVCmdGetVLArb;

/**
 */
public class SAHelper extends FEHelper {
    private final static Logger log = LoggerFactory.getLogger(SAHelper.class);

    public SAHelper(IStatement statement) {
        super(statement);
    }

    public List<NodeRecordBean> getNodes() throws Exception {
        FVCmdGetNodes cmd = new FVCmdGetNodes();
        List<NodeRecordBean> res = statement.execute(cmd);
        log.info("Get " + (res == null ? 0 : res.size()) + " nodes from FE");
        if (res != null) {
            for (NodeRecordBean bean : res) {
                if (bean.getNodeInfo().getNumPorts() == 0) {
                    log.error("Node has no ports! " + bean);
                }
            }
        }
        return res;
    }

    public NodeRecordBean getNode(int lid) throws Exception {
        FVCmdGetNode cmd = new FVCmdGetNode(new InputLid(lid));
        return statement.execute(cmd);
    }

    public NodeRecordBean getNode(long portGuid) throws Exception {
        FVCmdGetNode cmd = new FVCmdGetNode(new InputPortGuid(portGuid));
        return statement.execute(cmd);
    }

    public List<LinkRecordBean> getLinks() throws Exception {
        FVCmdGetLink cmd = new FVCmdGetLink();
        List<LinkRecordBean> res = statement.execute(cmd);
        log.info("Get " + (res == null ? 0 : res.size()) + " links from FE");
        return res;
    }

    public List<LinkRecordBean> getLinks(int lid) throws Exception {
        FVCmdGetLink cmd = new FVCmdGetLink(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<PortRecordBean> getPorts() throws Exception {
        FVCmdGetPortInfo cmd = new FVCmdGetPortInfo();
        List<PortRecordBean> res = statement.execute(cmd);
        log.info("Get " + (res == null ? 0 : res.size()) + " ports from FE");
        return res;
    }

    public List<PortRecordBean> getPorts(int lid) throws Exception {
        FVCmdGetPortInfo cmd = new FVCmdGetPortInfo(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<SwitchRecordBean> getSwitches() throws Exception {
        FVCmdGetSwitches cmd = new FVCmdGetSwitches();
        return statement.execute(cmd);
    }

    public SwitchRecordBean getSwitch(int lid) throws Exception {
        FVCmdGetSwitchInfo cmd = new FVCmdGetSwitchInfo(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<LFTRecordBean> getLFTs() throws Exception {
        FVCmdGetLFT cmd = new FVCmdGetLFT();
        return statement.execute(cmd);
    }

    public List<LFTRecordBean> getLFTs(int lid) throws Exception {
        FVCmdGetLFT cmd = new FVCmdGetLFT(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<MFTRecordBean> getMFTs() throws Exception {
        FVCmdGetMFT cmd = new FVCmdGetMFT();
        return statement.execute(cmd);
    }

    public List<MFTRecordBean> getMFTs(int lid) throws Exception {
        FVCmdGetMFT cmd = new FVCmdGetMFT(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<CableRecordBean> getCables() throws Exception {
        FVCmdGetCable cmd = new FVCmdGetCable();
        List<CableRecordBean> cables = statement.execute(cmd);
        if (cables != null && !cables.isEmpty()) {
            cables = combineCables(cables);
        }
        return cables;
    }

    public List<CableRecordBean> getCables(int lid) throws Exception {
        FVCmdGetCable cmd = new FVCmdGetCable(new InputLid(lid));
        List<CableRecordBean> cables = statement.execute(cmd);
        if (cables != null && !cables.isEmpty()) {
            cables = combineCables(cables);
        }
        return cables;
    }

    private List<CableRecordBean> combineCables(List<CableRecordBean> raw) {
        // keep the order
        Map<Point, CableRecordBean> map =
                new LinkedHashMap<Point, CableRecordBean>();
        for (CableRecordBean bean : raw) {
            Point key = new Point(bean.getLid(), bean.getPort());
            CableRecordBean finalBean = map.get(key);
            if (finalBean == null) {
                map.put(key, bean);
            } else {
                finalBean.combine(bean);
            }
        }
        return Collections
                .unmodifiableList(new ArrayList<CableRecordBean>(map.values()));
    }

    public List<SC2SLMTRecordBean> getSC2SLMTs() throws Exception {
        FVCmdGetSC2SLMTs cmd = new FVCmdGetSC2SLMTs();
        return statement.execute(cmd);
    }

    public SC2SLMTRecordBean getSC2SLMT(int lid) throws Exception {
        FVCmdGetSC2SLMT cmd = new FVCmdGetSC2SLMT(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<SC2VLMTRecordBean> getSC2VLTMTs() throws Exception {
        FVCmdGetSC2VLTMT cmd = new FVCmdGetSC2VLTMT();
        return statement.execute(cmd);
    }

    public List<SC2VLMTRecordBean> getSC2VLTMT(int lid) throws Exception {
        FVCmdGetSC2VLTMT cmd = new FVCmdGetSC2VLTMT(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<SC2VLMTRecordBean> getSC2VLNTMTs() throws Exception {
        FVCmdGetSC2VLNTMT cmd = new FVCmdGetSC2VLNTMT();
        return statement.execute(cmd);
    }

    public List<SC2VLMTRecordBean> getSC2VLNTMT(int lid) throws Exception {
        FVCmdGetSC2VLNTMT cmd = new FVCmdGetSC2VLNTMT(new InputLid(lid));
        return statement.execute(cmd);
    }

    public List<P_KeyTableRecordBean> getPKeyTables() throws Exception {
        FVCmdGetPKeyTable cmd = new FVCmdGetPKeyTable();
        return statement.execute(cmd);
    }

    public List<VLArbTableRecordBean> getVLArbTables() throws Exception {
        FVCmdGetVLArb cmd = new FVCmdGetVLArb();
        return statement.execute(cmd);
    }

    public List<SMRecordBean> getSMs() throws Exception {
        FVCmdGetSMInfo cmd = new FVCmdGetSMInfo();
        return statement.execute(cmd);
    }

    public List<PathRecordBean> getPath(GID.Global gid) throws Exception {
        FVCmdGetPath cmd = new FVCmdGetPath(new InputPortGid(gid));
        return statement.execute(cmd);
    }

    public List<TraceRecordBean> getTrace(GID.Global source, GID.Global target)
            throws Exception {
        FVCmdGetTrace cmd = new FVCmdGetTrace(new InputGidPair(source, target));
        return statement.execute(cmd);
    }

    public FabricInfoBean getFabricInfo() throws Exception {
        FVCmdGetFabricInfo cmd = new FVCmdGetFabricInfo();
        return statement.execute(cmd);
    }
}
