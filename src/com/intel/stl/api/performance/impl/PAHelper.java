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

package com.intel.stl.api.performance.impl;

import java.util.List;

import com.intel.stl.api.performance.FocusPortsRspBean;
import com.intel.stl.api.performance.GroupConfigRspBean;
import com.intel.stl.api.performance.GroupInfoBean;
import com.intel.stl.api.performance.GroupListBean;
import com.intel.stl.api.performance.ImageIdBean;
import com.intel.stl.api.performance.ImageInfoBean;
import com.intel.stl.api.performance.PMConfigBean;
import com.intel.stl.api.performance.PortCountersBean;
import com.intel.stl.api.performance.VFConfigRspBean;
import com.intel.stl.api.performance.VFFocusPortsRspBean;
import com.intel.stl.api.performance.VFInfoBean;
import com.intel.stl.api.performance.VFListBean;
import com.intel.stl.api.performance.VFPortCountersBean;
import com.intel.stl.api.subnet.Selection;
import com.intel.stl.fecdriver.IStatement;
import com.intel.stl.fecdriver.adapter.FEHelper;
import com.intel.stl.fecdriver.messages.command.InputFocus;
import com.intel.stl.fecdriver.messages.command.InputGroupName;
import com.intel.stl.fecdriver.messages.command.InputImageId;
import com.intel.stl.fecdriver.messages.command.InputLidPortNumber;
import com.intel.stl.fecdriver.messages.command.InputVFName;
import com.intel.stl.fecdriver.messages.command.InputVFNameFocus;
import com.intel.stl.fecdriver.messages.command.InputVFNamePort;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetFocusPort;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetGroupConfig;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetGroupInfo;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetGroupList;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetImageInfo;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetPMConfig;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetPortCounters;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetVFConfig;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetVFFocusPort;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetVFInfo;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetVFList;
import com.intel.stl.fecdriver.messages.command.pa.FVCmdGetVFPortCounters;

/**
 */
public class PAHelper extends FEHelper {
    public PAHelper(IStatement statement) {
        super(statement);
    }

    public ImageInfoBean getImageInfo(ImageIdBean imageId) throws Exception {
        return getImageInfo(imageId.getImageNumber(), imageId.getImageOffset());
    }

    public ImageInfoBean getImageInfo(long imageNumber, int imageOffset)
            throws Exception {
        FVCmdGetImageInfo cmd =
                new FVCmdGetImageInfo(
                        new InputImageId(imageNumber, imageOffset));
        return statement.execute(cmd);
    }

    public List<GroupListBean> getGroupList() throws Exception {
        FVCmdGetGroupList cmd = new FVCmdGetGroupList();
        return statement.execute(cmd);
    }

    public GroupInfoBean getGroupInfo(String name) throws Exception {
        FVCmdGetGroupInfo cmd = new FVCmdGetGroupInfo(new InputGroupName(name));
        return statement.execute(cmd);
    }

    public GroupInfoBean getGroupInfoHistory(String name, long imageID,
            int offset) throws Exception {
        FVCmdGetGroupInfo cmd =
                new FVCmdGetGroupInfo(new InputGroupName(name, imageID, offset));
        return statement.execute(cmd);
    }

    public List<GroupConfigRspBean> getGroupConfig(String name)
            throws Exception {
        FVCmdGetGroupConfig cmd =
                new FVCmdGetGroupConfig(new InputGroupName(name));
        return statement.execute(cmd);
    }

    public List<FocusPortsRspBean> getFocusPort(String group,
            Selection seclection, int range) throws Exception {
        InputFocus input = new InputFocus(group, seclection);
        input.setRange(range);
        FVCmdGetFocusPort cmd = new FVCmdGetFocusPort(input);
        return statement.execute(cmd);
    }

    public PortCountersBean getPortCounter(int lid, short portNum)
            throws Exception {
        FVCmdGetPortCounters cmd =
                new FVCmdGetPortCounters(new InputLidPortNumber(lid,
                        (byte) portNum));
        return statement.execute(cmd);
    }

    public PortCountersBean getPortCounterHistory(int lid, short portNum,
            long imageID, int imageOffset) throws Exception {
        FVCmdGetPortCounters cmd =
                new FVCmdGetPortCounters(new InputLidPortNumber(lid,
                        (byte) portNum, imageID, imageOffset));
        return statement.execute(cmd);
    }

    public List<VFListBean> getVFList() throws Exception {
        FVCmdGetVFList cmd = new FVCmdGetVFList();
        return statement.execute(cmd);
    }

    /**
     * Description:
     * 
     * @param name
     * @return
     */
    public VFInfoBean getVFInfo(String name) throws Exception {
        FVCmdGetVFInfo cmd = new FVCmdGetVFInfo(new InputVFName(name));
        return statement.execute(cmd);
    }

    public VFInfoBean getVFInfoHistory(String name, long imageID,
            int imageOffset) throws Exception {
        FVCmdGetVFInfo cmd =
                new FVCmdGetVFInfo(new InputVFName(name, imageID, imageOffset));
        return statement.execute(cmd);
    }

    /**
     * Description:
     * 
     * @param name
     * @return
     */
    public List<VFConfigRspBean> getVFConfig(String name) throws Exception {
        FVCmdGetVFConfig cmd = new FVCmdGetVFConfig(new InputVFName(name));
        return statement.execute(cmd);
    }

    /**
     * Description:
     * 
     * @param vfName
     * @param selection
     * @param n
     * @return
     */
    public List<VFFocusPortsRspBean> getVFFocusPort(String vfName,
            Selection selection, int n) throws Exception {
        InputVFNameFocus input = new InputVFNameFocus(vfName, selection);
        input.setRange(n);
        FVCmdGetVFFocusPort cmd = new FVCmdGetVFFocusPort(input);
        return statement.execute(cmd);
    }

    /**
     * Description:
     * 
     * @param lid
     * @param portNum
     * @return
     */
    public VFPortCountersBean getVFPortCounter(String vfName, int lid,
            short portNum) throws Exception {
        FVCmdGetVFPortCounters cmd =
                new FVCmdGetVFPortCounters(new InputVFNamePort(vfName, lid,
                        (byte) portNum));
        return statement.execute(cmd);
    }

    public VFPortCountersBean getVFPortCounterHistory(String vfName, int lid,
            short portNum, long imageID, int imageOffset) throws Exception {
        FVCmdGetVFPortCounters cmd =
                new FVCmdGetVFPortCounters(new InputVFNamePort(vfName, lid,
                        (byte) portNum, imageID, imageOffset));
        return statement.execute(cmd);
    }

    public PMConfigBean getPMConfig() throws Exception {
        FVCmdGetPMConfig cmd = new FVCmdGetPMConfig();
        return statement.execute(cmd);
    }
}
